/**
 * Copyright 2010-2012 Ralph Schaer <ralphschaer@gmail.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package ch.ralscha.extdirectspring.controller;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter;
import org.springframework.web.util.WebUtils;

import ch.ralscha.extdirectspring.annotation.ExtDirectMethodType;
import ch.ralscha.extdirectspring.bean.BaseResponse;
import ch.ralscha.extdirectspring.bean.ExtDirectFormLoadResult;
import ch.ralscha.extdirectspring.bean.ExtDirectFormPostResult;
import ch.ralscha.extdirectspring.bean.ExtDirectPollResponse;
import ch.ralscha.extdirectspring.bean.ExtDirectRequest;
import ch.ralscha.extdirectspring.bean.ExtDirectResponse;
import ch.ralscha.extdirectspring.bean.ExtDirectStoreReadResult;
import ch.ralscha.extdirectspring.util.ExtDirectSpringUtil;
import ch.ralscha.extdirectspring.util.JsonHandler;
import ch.ralscha.extdirectspring.util.MethodInfo;
import ch.ralscha.extdirectspring.util.MethodInfoCache;
import ch.ralscha.extdirectspring.util.ParameterInfo;
import ch.ralscha.extdirectspring.util.ParametersResolver;
import ch.ralscha.extdirectspring.util.SupportedParameters;

import com.fasterxml.jackson.core.JsonEncoding;
import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Main router controller that handles polling, form handler and normal Ext
 * Direct calls.
 * 
 * @author mansari
 * @author Ralph Schaer
 * @author Goddanao
 */
@Controller
public class RouterController implements InitializingBean, DisposableBean {

	public static final Charset UTF8_CHARSET = Charset.forName("UTF-8");

	public static final MediaType APPLICATION_JSON = new MediaType("application", "json", UTF8_CHARSET);

	public static final MediaType TEXT_HTML = new MediaType("text", "html", UTF8_CHARSET);

	private static final Log log = LogFactory.getLog(RouterController.class);

	@Autowired
	private ApplicationContext context;

	@Autowired(required = false)
	private JsonHandler jsonHandler;

	@Autowired(required = false)
	private Configuration configuration;

	@Autowired
	private ParametersResolver parametersResolver;

	@Autowired
	private RequestMappingHandlerAdapter handlerAdapter;

	public Configuration getConfiguration() {
		return configuration;
	}

	public JsonHandler getJsonHandler() {
		return jsonHandler;
	}

	@Override
	public void afterPropertiesSet() {

		if (configuration == null) {
			configuration = new Configuration();
		}

		if (jsonHandler == null) {
			jsonHandler = new JsonHandler();
		}

		if (configuration.getBatchedMethodsExecutionPolicy() == BatchedMethodsExecutionPolicy.CONCURRENT
				&& configuration.getBatchedMethodsExecutorService() == null) {
			configuration.setBatchedMethodsExecutorService(Executors.newFixedThreadPool(5));
		}

	}

	@Override
	public void destroy() throws Exception {
		if (configuration.getBatchedMethodsExecutorService() != null) {
			configuration.getBatchedMethodsExecutorService().shutdown();
		}
	}

	@RequestMapping(value = "/poll/{beanName}/{method}/{event}")
	public void poll(@PathVariable("beanName") String beanName, @PathVariable("method") String method,
			@PathVariable("event") String event, HttpServletRequest request, HttpServletResponse response, Locale locale)
			throws Exception {

		ExtDirectPollResponse directPollResponse = new ExtDirectPollResponse();
		directPollResponse.setName(event);

		MethodInfo methodInfo = MethodInfoCache.INSTANCE.get(beanName, method);
		boolean streamResponse;

		if (methodInfo != null) {

			streamResponse = configuration.isStreamResponse() || methodInfo.isStreamResponse();

			try {

				List<ParameterInfo> methodParameters = methodInfo.getParameters();
				Object[] parameters = null;
				if (!methodParameters.isEmpty()) {
					parameters = new Object[methodParameters.size()];

					for (int paramIndex = 0; paramIndex < methodParameters.size(); paramIndex++) {
						ParameterInfo methodParameter = methodParameters.get(paramIndex);

						if (methodParameter.isSupportedParameter()) {
							parameters[paramIndex] = SupportedParameters.resolveParameter(methodParameter.getType(),
									request, response, locale);
						} else if (methodParameter.isHasRequestHeaderAnnotation()) {
							parameters[paramIndex] = parametersResolver.resolveRequestHeader(request, methodParameter);
						} else {
							parameters[paramIndex] = parametersResolver.resolveRequestParam(request, null,
									methodParameter);
						}

					}
				}

				if (configuration.isSynchronizeOnSession() || methodInfo.isSynchronizeOnSession()) {
					HttpSession session = request.getSession(false);
					if (session != null) {
						Object mutex = WebUtils.getSessionMutex(session);
						synchronized (mutex) {
							directPollResponse.setData(ExtDirectSpringUtil.invoke(context, beanName, methodInfo,
									parameters));
						}
					} else {
						directPollResponse.setData(ExtDirectSpringUtil
								.invoke(context, beanName, methodInfo, parameters));
					}
				} else {
					directPollResponse.setData(ExtDirectSpringUtil.invoke(context, beanName, methodInfo, parameters));
				}

			} catch (Exception e) {
				log.error("Error polling method '" + beanName + "." + method + "'", e.getCause() != null ? e.getCause()
						: e);
				handleException(directPollResponse, e);
			}
		} else {
			log.error("Error invoking method '" + beanName + "." + method + "'. Method or Bean not found");
			handleMethodNotFoundError(directPollResponse, beanName, method);
			streamResponse = configuration.isStreamResponse();
		}

		writeJsonResponse(response, directPollResponse, streamResponse);
	}

	@RequestMapping(value = "/router", method = RequestMethod.POST, params = "extAction")
	public String router(HttpServletRequest request, HttpServletResponse response,
			@RequestParam("extAction") String extAction, @RequestParam("extMethod") String extMethod)
			throws IOException {

		ExtDirectResponse directResponse = new ExtDirectResponse(request);
		MethodInfo methodInfo = MethodInfoCache.INSTANCE.get(extAction, extMethod);
		boolean streamResponse;

		if (methodInfo != null && methodInfo.getForwardPath() != null) {
			return methodInfo.getForwardPath();
		} else if (methodInfo != null && methodInfo.getHandlerMethod() != null) {
			streamResponse = configuration.isStreamResponse() || methodInfo.isStreamResponse();

			HandlerMethod handlerMethod = methodInfo.getHandlerMethod();
			try {

				ModelAndView modelAndView = null;

				if (configuration.isSynchronizeOnSession() || methodInfo.isSynchronizeOnSession()) {
					HttpSession session = request.getSession(false);
					if (session != null) {
						Object mutex = WebUtils.getSessionMutex(session);
						synchronized (mutex) {
							modelAndView = handlerAdapter.handle(request, response, handlerMethod);
						}
					} else {
						modelAndView = handlerAdapter.handle(request, response, handlerMethod);
					}
				} else {
					modelAndView = handlerAdapter.handle(request, response, handlerMethod);
				}

				ExtDirectFormPostResult formPostResult = (ExtDirectFormPostResult) modelAndView.getModel().get(
						"extDirectFormPostResult");
				directResponse.setResult(formPostResult.getResult());
			} catch (Exception e) {
				log.error("Error calling method: " + extMethod, e.getCause() != null ? e.getCause() : e);
				handleException(directResponse, e);
				Map<String, Object> result = new HashMap<String, Object>();
				result.put("success", false);
				directResponse.setResult(result);
			}
		} else {
			streamResponse = configuration.isStreamResponse();
			log.error("Error invoking method '" + extAction + "." + extMethod + "'. Method  or Bean not found");
			handleMethodNotFoundError(directResponse, extAction, extMethod);
		}
		writeJsonResponse(response, directResponse, streamResponse, ExtDirectSpringUtil.isMultipart(request));

		return null;
	}

	@RequestMapping(value = "/router", method = RequestMethod.POST, params = "!extAction")
	public void router(HttpServletRequest request, HttpServletResponse response, Locale locale) throws IOException {

		Object requestData = jsonHandler.readValue(request.getInputStream(), Object.class);

		List<ExtDirectRequest> directRequests = new ArrayList<ExtDirectRequest>();
		if (requestData instanceof Map) {
			directRequests.add(jsonHandler.convertValue(requestData, ExtDirectRequest.class));
		} else if (requestData instanceof List) {
			for (Object oneRequest : (List<?>) requestData) {
				directRequests.add(jsonHandler.convertValue(oneRequest, ExtDirectRequest.class));
			}
		}

		if (directRequests.size() == 1
				|| configuration.getBatchedMethodsExecutionPolicy() == BatchedMethodsExecutionPolicy.SEQUENTIAL) {
			handleMethodCallsSequential(directRequests, request, response, locale);
		} else if (configuration.getBatchedMethodsExecutionPolicy() == BatchedMethodsExecutionPolicy.CONCURRENT) {
			handleMethodCallsConcurrent(directRequests, request, response, locale);
		}

	}

	private void handleMethodCallsConcurrent(List<ExtDirectRequest> directRequests, HttpServletRequest request,
			HttpServletResponse response, Locale locale) throws JsonGenerationException, JsonMappingException,
			IOException {
		List<Future<ExtDirectResponse>> futures = new ArrayList<Future<ExtDirectResponse>>(directRequests.size());
		for (ExtDirectRequest directRequest : directRequests) {
			Callable<ExtDirectResponse> callable = createMethodCallCallable(directRequest, request, response, locale);
			futures.add(configuration.getBatchedMethodsExecutorService().submit(callable));
		}

		List<ExtDirectResponse> directResponses = new ArrayList<ExtDirectResponse>(directRequests.size());
		boolean streamResponse = configuration.isStreamResponse();
		for (Future<ExtDirectResponse> future : futures) {
			try {
				ExtDirectResponse directResponse = future.get();
				streamResponse = streamResponse || directResponse.isStreamResponse();
				directResponses.add(directResponse);
			} catch (InterruptedException e) {
				log.error("Error invoking method", e);
			} catch (ExecutionException e) {
				log.error("Error invoking method", e);
			}
		}
		writeJsonResponse(response, directResponses, streamResponse);
	}

	private Callable<ExtDirectResponse> createMethodCallCallable(final ExtDirectRequest directRequest,
			final HttpServletRequest request, final HttpServletResponse response, final Locale locale) {
		return new Callable<ExtDirectResponse>() {
			@Override
			public ExtDirectResponse call() throws Exception {
				return handleMethodCall(directRequest, request, response, locale);
			}
		};
	}

	private void handleMethodCallsSequential(List<ExtDirectRequest> directRequests, HttpServletRequest request,
			HttpServletResponse response, Locale locale) throws JsonGenerationException, JsonMappingException,
			IOException {
		List<ExtDirectResponse> directResponses = new ArrayList<ExtDirectResponse>(directRequests.size());
		boolean streamResponse = configuration.isStreamResponse();

		for (ExtDirectRequest directRequest : directRequests) {
			ExtDirectResponse directResponse = handleMethodCall(directRequest, request, response, locale);
			streamResponse = streamResponse || directResponse.isStreamResponse();
			directResponses.add(directResponse);
		}

		writeJsonResponse(response, directResponses, streamResponse);
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	ExtDirectResponse handleMethodCall(ExtDirectRequest directRequest, HttpServletRequest request,
			HttpServletResponse response, Locale locale) {
		ExtDirectResponse directResponse = new ExtDirectResponse(directRequest);

		MethodInfo methodInfo = MethodInfoCache.INSTANCE.get(directRequest.getAction(), directRequest.getMethod());

		if (methodInfo != null) {

			try {
				directResponse.setStreamResponse(methodInfo.isStreamResponse());

				Object result = processRemotingRequest(request, response, locale, directRequest, methodInfo);

				if (result != null) {

					if (methodInfo.isType(ExtDirectMethodType.FORM_LOAD)
							&& !ExtDirectFormLoadResult.class.isAssignableFrom(result.getClass())) {
						result = new ExtDirectFormLoadResult(result);
					} else if ((methodInfo.isType(ExtDirectMethodType.STORE_MODIFY) || methodInfo
							.isType(ExtDirectMethodType.STORE_READ))
							&& !ExtDirectStoreReadResult.class.isAssignableFrom(result.getClass())
							&& configuration.isAlwaysWrapStoreResponse()) {
						if (result instanceof Collection) {
							result = new ExtDirectStoreReadResult((Collection) result);
						} else {
							result = new ExtDirectStoreReadResult(result);
						}
					}

					directResponse.setResult(result);
				} else {
					if (methodInfo.isType(ExtDirectMethodType.STORE_MODIFY)
							|| methodInfo.isType(ExtDirectMethodType.STORE_READ)) {
						directResponse.setResult(Collections.emptyList());
					}
				}

			} catch (Exception e) {
				log.error("Error calling method: " + directRequest.getMethod(), e.getCause() != null ? e.getCause() : e);
				handleException(directResponse, e);
			}
		} else {
			log.error("Error invoking method '" + directRequest.getAction() + "." + directRequest.getMethod()
					+ "'. Method or Bean not found");
			handleMethodNotFoundError(directResponse, directRequest.getAction(), directRequest.getMethod());
		}

		return directResponse;
	}

	private void writeJsonResponse(HttpServletResponse response, Object responseObject, boolean streamResponse)
			throws IOException, JsonGenerationException, JsonMappingException {
		writeJsonResponse(response, responseObject, streamResponse, false);
	}

	public void writeJsonResponse(HttpServletResponse response, Object responseObject, boolean streamResponse,
			boolean isMultipart) throws IOException, JsonGenerationException, JsonMappingException {

		if (isMultipart) {
			response.setContentType(RouterController.TEXT_HTML.toString());
			response.setCharacterEncoding(RouterController.TEXT_HTML.getCharSet().name());

			ByteArrayOutputStream bos = new ByteArrayOutputStream(1024);
			bos.write("<html><body><textarea>".getBytes(UTF8_CHARSET));

			String responseJson = jsonHandler.getMapper().writeValueAsString(responseObject);

			responseJson = responseJson.replace("&quot;", "\\&quot;");
			bos.write(responseJson.getBytes(UTF8_CHARSET));
			bos.write("</textarea></body></html>".getBytes(UTF8_CHARSET));

			response.setContentLength(bos.size());
			FileCopyUtils.copy(bos.toByteArray(), response.getOutputStream());
		} else {

			response.setContentType(APPLICATION_JSON.toString());
			response.setCharacterEncoding(APPLICATION_JSON.getCharSet().name());

			ObjectMapper objectMapper = jsonHandler.getMapper();

			@SuppressWarnings("resource")
			ServletOutputStream outputStream = response.getOutputStream();

			if (!streamResponse) {
				ByteArrayOutputStream bos = new ByteArrayOutputStream(1024);
				JsonGenerator jsonGenerator = objectMapper.getJsonFactory().createJsonGenerator(bos, JsonEncoding.UTF8);
				objectMapper.writeValue(jsonGenerator, responseObject);
				response.setContentLength(bos.size());
				outputStream.write(bos.toByteArray());
				jsonGenerator.close();
			} else {
				JsonGenerator jsonGenerator = objectMapper.getJsonFactory().createJsonGenerator(outputStream,
						JsonEncoding.UTF8);
				objectMapper.writeValue(jsonGenerator, responseObject);
				jsonGenerator.close();
			}

			outputStream.flush();
		}
	}

	private Object processRemotingRequest(HttpServletRequest request, HttpServletResponse response, Locale locale,
			ExtDirectRequest directRequest, MethodInfo methodInfo) throws Exception {

		Object[] parameters = parametersResolver
				.resolveParameters(request, response, locale, directRequest, methodInfo);

		if (configuration.isSynchronizeOnSession() || methodInfo.isSynchronizeOnSession()) {
			HttpSession session = request.getSession(false);
			if (session != null) {
				Object mutex = WebUtils.getSessionMutex(session);
				synchronized (mutex) {
					return ExtDirectSpringUtil.invoke(context, directRequest.getAction(), methodInfo, parameters);
				}
			}
		}

		return ExtDirectSpringUtil.invoke(context, directRequest.getAction(), methodInfo, parameters);
	}

	private void handleException(BaseResponse response, Exception e) {
		Throwable cause;
		if (e.getCause() != null) {
			cause = e.getCause();
		} else {
			cause = e;
		}

		response.setType("exception");
		response.setMessage(configuration.getMessage(cause));

		if (configuration.isSendStacktrace()) {
			response.setWhere(ExtDirectSpringUtil.getStackTrace(cause));
		} else {
			response.setWhere(null);
		}
	}

	private void handleMethodNotFoundError(BaseResponse response, String beanName, String methodName) {
		response.setType("exception");
		response.setMessage(configuration.getDefaultExceptionMessage());

		if (configuration.isSendStacktrace()) {
			response.setWhere("Bean or Method '" + beanName + "." + methodName + "' not found");
		} else {
			response.setWhere(null);
		}
	}

}
