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
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.codehaus.jackson.JsonEncoding;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.util.WebUtils;

import ch.ralscha.extdirectspring.annotation.ExtDirectMethodType;
import ch.ralscha.extdirectspring.bean.BaseResponse;
import ch.ralscha.extdirectspring.bean.ExtDirectFormLoadResult;
import ch.ralscha.extdirectspring.bean.ExtDirectPollResponse;
import ch.ralscha.extdirectspring.bean.ExtDirectRequest;
import ch.ralscha.extdirectspring.bean.ExtDirectResponse;
import ch.ralscha.extdirectspring.bean.ExtDirectStoreResponse;
import ch.ralscha.extdirectspring.util.ExtDirectSpringUtil;
import ch.ralscha.extdirectspring.util.JsonHandler;
import ch.ralscha.extdirectspring.util.MethodInfo;
import ch.ralscha.extdirectspring.util.MethodInfoCache;
import ch.ralscha.extdirectspring.util.ParameterInfo;
import ch.ralscha.extdirectspring.util.ParametersResolver;
import ch.ralscha.extdirectspring.util.SupportedParameters;

/**
 * Main router controller that handles polling, form handler and normal Ext
 * Direct calls.
 * 
 * @author mansari
 * @author Ralph Schaer
 * @author Goddanao
 */
@Controller
public class RouterController implements InitializingBean {

	public static final MediaType APPLICATION_JSON = new MediaType("application", "json", Charset.forName("UTF-8"));

	public static final MediaType TEXT_HTML = new MediaType("text", "html", Charset.forName("UTF-8"));

	private static final Log log = LogFactory.getLog(RouterController.class);

	@Autowired
	private ApplicationContext context;

	@Autowired(required = false)
	private JsonHandler jsonHandler;

	@Autowired(required = false)
	private Configuration configuration;

	@Autowired
	private ParametersResolver parametersResolver;

	public Configuration getConfiguration() {
		return configuration;
	}

	public JsonHandler getJsonHandler() {
		return jsonHandler;
	}

	public void afterPropertiesSet() {

		if (configuration == null) {
			configuration = new Configuration();
		}

		if (jsonHandler == null) {
			jsonHandler = new JsonHandler();
		}

	}

	@RequestMapping(value = "/poll/{beanName}/{method}/{event}")
	public void poll(@PathVariable("beanName") final String beanName, @PathVariable("method") final String method,
			@PathVariable("event") final String event, final HttpServletRequest request,
			final HttpServletResponse response, final Locale locale) throws Exception {

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
	public String router(final HttpServletRequest request, final HttpServletResponse response,
			@RequestParam("extAction") final String extAction, @RequestParam("extMethod") final String extMethod)
			throws IOException {

		MethodInfo methodInfo = MethodInfoCache.INSTANCE.get(extAction, extMethod);

		if (methodInfo != null && methodInfo.getForwardPath() != null) {
			return methodInfo.getForwardPath();
		}

		log.error("Error invoking method '" + extAction + "." + extMethod + "'. Method  or Bean not found");
		ExtDirectResponse directResponse = new ExtDirectResponse(request);
		handleMethodNotFoundError(directResponse, extAction, extMethod);
		writeJsonResponse(response, directResponse, configuration.isStreamResponse());

		return null;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@RequestMapping(value = "/router", method = RequestMethod.POST, params = "!extAction")
	public void router(final HttpServletRequest request, final HttpServletResponse response, final Locale locale)
			throws IOException {

		Object requestData = jsonHandler.readValue(request.getInputStream(), Object.class);

		List<ExtDirectRequest> directRequests = new ArrayList<ExtDirectRequest>();
		if (requestData instanceof Map) {
			directRequests.add(jsonHandler.convertValue(requestData, ExtDirectRequest.class));
		} else if (requestData instanceof List) {
			for (Object oneRequest : (List) requestData) {
				directRequests.add(jsonHandler.convertValue(oneRequest, ExtDirectRequest.class));
			}
		}

		List<ExtDirectResponse> directResponses = new ArrayList<ExtDirectResponse>();
		boolean streamResponse = configuration.isStreamResponse();

		for (ExtDirectRequest directRequest : directRequests) {

			ExtDirectResponse directResponse = new ExtDirectResponse(directRequest);

			MethodInfo methodInfo = MethodInfoCache.INSTANCE.get(directRequest.getAction(), directRequest.getMethod());

			if (methodInfo != null) {

				try {
					streamResponse = streamResponse || methodInfo.isStreamResponse();

					Object result = processRemotingRequest(request, response, locale, directRequest, methodInfo);

					if (result != null) {

						if (methodInfo.isType(ExtDirectMethodType.FORM_LOAD)
								&& !ExtDirectFormLoadResult.class.isAssignableFrom(result.getClass())) {
							result = new ExtDirectFormLoadResult(result);
						} else if (methodInfo.isType(ExtDirectMethodType.TREE_LOAD)) {
							if (!(result instanceof Collection) && !result.getClass().isArray()) {
								result = Arrays.asList(result);
							}
						} else if ((methodInfo.isType(ExtDirectMethodType.STORE_MODIFY) || methodInfo
								.isType(ExtDirectMethodType.STORE_READ))
								&& !ExtDirectStoreResponse.class.isAssignableFrom(result.getClass())
								&& configuration.isAlwaysWrapStoreResponse()) {
							if (result instanceof Collection) {
								result = new ExtDirectStoreResponse((Collection) result);
							} else {
								result = new ExtDirectStoreResponse(result);
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
					log.error("Error calling method: " + directRequest.getMethod(), e.getCause() != null ? e.getCause()
							: e);
					handleException(directResponse, e);
				}
			} else {
				log.error("Error invoking method '" + directRequest.getAction() + "." + directRequest.getMethod()
						+ "'. Method or Bean not found");
				handleMethodNotFoundError(directResponse, directRequest.getAction(), directRequest.getMethod());
			}

			directResponses.add(directResponse);
		}

		writeJsonResponse(response, directResponses, streamResponse);
	}

	public void writeJsonResponse(final HttpServletResponse response, final Object responseObject,
			final boolean streamResponse) throws IOException, JsonGenerationException, JsonMappingException {
		response.setContentType(APPLICATION_JSON.toString());
		response.setCharacterEncoding(APPLICATION_JSON.getCharSet().name());

		final ObjectMapper objectMapper = jsonHandler.getMapper();
		final ServletOutputStream outputStream = response.getOutputStream();

		if (!streamResponse) {
			ByteArrayOutputStream bos = new ByteArrayOutputStream(1024);
			JsonGenerator jsonGenerator = objectMapper.getJsonFactory().createJsonGenerator(bos, JsonEncoding.UTF8);
			objectMapper.writeValue(jsonGenerator, responseObject);
			response.setContentLength(bos.size());
			outputStream.write(bos.toByteArray());
		} else {
			JsonGenerator jsonGenerator = objectMapper.getJsonFactory().createJsonGenerator(outputStream,
					JsonEncoding.UTF8);
			objectMapper.writeValue(jsonGenerator, responseObject);
		}

		outputStream.flush();
	}

	private Object processRemotingRequest(final HttpServletRequest request, final HttpServletResponse response,
			final Locale locale, final ExtDirectRequest directRequest, final MethodInfo methodInfo) throws Exception {

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

	private void handleException(final BaseResponse response, final Exception e) {
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

	private void handleMethodNotFoundError(final BaseResponse response, final String beanName, final String methodName) {
		response.setType("exception");
		response.setMessage(configuration.getDefaultExceptionMessage());

		if (configuration.isSendStacktrace()) {
			response.setWhere("Bean or Method '" + beanName + "." + methodName + "' not found");
		} else {
			response.setWhere(null);
		}
	}

}
