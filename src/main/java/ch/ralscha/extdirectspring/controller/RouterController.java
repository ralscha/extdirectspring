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

import java.beans.PropertyDescriptor;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

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
import org.codehaus.jackson.type.TypeReference;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.BeanFactoryUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.util.ClassUtils;
import org.springframework.util.ReflectionUtils.MethodFilter;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.util.WebUtils;

import ch.ralscha.extdirectspring.annotation.ExtDirectMethod;
import ch.ralscha.extdirectspring.annotation.ExtDirectMethodType;
import ch.ralscha.extdirectspring.bean.BaseResponse;
import ch.ralscha.extdirectspring.bean.ExtDirectFormLoadResult;
import ch.ralscha.extdirectspring.bean.ExtDirectPollResponse;
import ch.ralscha.extdirectspring.bean.ExtDirectRequest;
import ch.ralscha.extdirectspring.bean.ExtDirectResponse;
import ch.ralscha.extdirectspring.bean.ExtDirectStoreReadRequest;
import ch.ralscha.extdirectspring.bean.ExtDirectStoreResponse;
import ch.ralscha.extdirectspring.bean.GroupInfo;
import ch.ralscha.extdirectspring.bean.SortDirection;
import ch.ralscha.extdirectspring.bean.SortInfo;
import ch.ralscha.extdirectspring.filter.Filter;
import ch.ralscha.extdirectspring.util.ExtDirectSpringUtil;
import ch.ralscha.extdirectspring.util.JsonHandler;
import ch.ralscha.extdirectspring.util.MethodInfo;
import ch.ralscha.extdirectspring.util.MethodInfoCache;
import ch.ralscha.extdirectspring.util.ParameterInfo;
import ch.ralscha.extdirectspring.util.SupportedParameters;

/**
 * Main router controller that handles polling, form handler and normal Ext
 * Direct calls.
 * 
 * @author mansari
 * @author Ralph Schaer
 */
@Controller
public class RouterController implements InitializingBean {

	public static final MediaType APPLICATION_JSON = new MediaType("application", "json", Charset.forName("UTF-8"));

	public static final MediaType TEXT_HTML = new MediaType("text", "html", Charset.forName("UTF-8"));

	private static final Log log = LogFactory.getLog(RouterController.class);

	@Autowired
	private ConversionService conversionService;

	@Autowired
	private ApplicationContext context;

	@Autowired(required = false)
	private JsonHandler jsonHandler;

	@Autowired(required = false)
	private Configuration configuration;

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

		// register ExtDirectMethod methods
		MethodInfoCache.INSTANCE.clear();

		String[] beanNames = BeanFactoryUtils.beanNamesForTypeIncludingAncestors(context, Object.class);

		for (String beanName : beanNames) {

			Class<?> handlerType = context.getType(beanName);
			final Class<?> userType = ClassUtils.getUserClass(handlerType);

			Set<Method> methods = ExtDirectSpringUtil.selectMethods(userType, new MethodFilter() {
				public boolean matches(final Method method) {
					return AnnotationUtils.findAnnotation(method, ExtDirectMethod.class) != null;
				}
			});

			for (Method method : methods) {
				ExtDirectMethod directMethodAnnotation = AnnotationUtils.findAnnotation(method, ExtDirectMethod.class);
				final String beanMethodName = beanName + "." + method.getName();
				if (directMethodAnnotation.value().isValid(beanMethodName, userType, method)) {
					MethodInfoCache.INSTANCE.put(beanName, handlerType, method);
					String info = "Register " + beanMethodName + "(" + directMethodAnnotation.value();
					if (StringUtils.hasText(directMethodAnnotation.group())) {
						info += ", " + directMethodAnnotation.group();
					}
					info += ")";
					log.debug(info);
				}
			}

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
							parameters[paramIndex] = handleRequestHeader(request, methodParameter);
						} else {
							parameters[paramIndex] = handleRequestParam(request, null, methodParameter);
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
						} else if ((methodInfo.isType(ExtDirectMethodType.STORE_MODIFY) || methodInfo
								.isType(ExtDirectMethodType.STORE_READ))
								&& !ExtDirectStoreResponse.class.isAssignableFrom(result.getClass())
								&& configuration.isAlwaysWrapStoreResponse()) {
							if (result instanceof Collection) {
								result = new ExtDirectStoreResponse((Collection) result);
							} else {
								List responses = new ArrayList();
								responses.add(result);
								result = new ExtDirectStoreResponse(responses);
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

	@SuppressWarnings({ "unchecked" })
	private Object processRemotingRequest(final HttpServletRequest request, final HttpServletResponse response,
			final Locale locale, final ExtDirectRequest directRequest, final MethodInfo methodInfo) throws Exception {

		int jsonParamIndex = 0;
		Map<String, Object> remainingParameters = null;
		ExtDirectStoreReadRequest ExtDirectStoreReadRequest = null;

		List<Object> directStoreModifyRecords = null;
		Class<?> directStoreEntryClass;

		if (methodInfo.isType(ExtDirectMethodType.STORE_READ) || methodInfo.isType(ExtDirectMethodType.FORM_LOAD)
				|| methodInfo.isType(ExtDirectMethodType.TREE_LOAD)) {

			List<Object> data = (List<Object>) directRequest.getData();

			if (data != null && data.size() > 0) {
				if (methodInfo.isType(ExtDirectMethodType.STORE_READ)) {
					ExtDirectStoreReadRequest = new ExtDirectStoreReadRequest();
					remainingParameters = fillReadRequestFromMap(ExtDirectStoreReadRequest,
							(Map<String, Object>) data.get(0));
				} else {
					remainingParameters = (Map<String, Object>) data.get(0);
				}
				jsonParamIndex = 1;
			}
		} else if (methodInfo.isType(ExtDirectMethodType.STORE_MODIFY)) {
			directStoreEntryClass = methodInfo.getCollectionType();
			List<Object> data = (List<Object>) directRequest.getData();

			if (directStoreEntryClass != null && data != null && data.size() > 0) {
				Object obj = data.get(0);
				if (obj instanceof List) {
					directStoreModifyRecords = convertObjectEntriesToType((List<Object>) obj, directStoreEntryClass);
				} else {
					Map<String, Object> jsonData = (Map<String, Object>) obj;
					Object records = jsonData.get("records");
					if (records != null) {
						if (records instanceof List) {
							directStoreModifyRecords = convertObjectEntriesToType((List<Object>) records,
									directStoreEntryClass);
						} else {
							directStoreModifyRecords = new ArrayList<Object>();
							directStoreModifyRecords.add(jsonHandler.convertValue(records, directStoreEntryClass));
						}
						remainingParameters = new HashMap<String, Object>(jsonData);
						remainingParameters.remove("records");
					} else {
						directStoreModifyRecords = new ArrayList<Object>();
						directStoreModifyRecords.add(jsonHandler.convertValue(jsonData, directStoreEntryClass));
					}
				}
				jsonParamIndex = 1;

			} else if (data != null && data.size() > 0) {
				Object obj = data.get(0);
				if (obj instanceof Map) {
					remainingParameters = new HashMap<String, Object>((Map<String, Object>) obj);
					remainingParameters.remove("records");
				}
			}
		} else if (methodInfo.isType(ExtDirectMethodType.SIMPLE_NAMED)) {
			Map<String, Object> data = (Map<String, Object>) directRequest.getData();
			if (data != null && data.size() > 0) {
				remainingParameters = new HashMap<String, Object>(data);
			}

		} else if (methodInfo.isType(ExtDirectMethodType.POLL)) {
			throw new IllegalStateException("this controller does not handle poll calls");
		} else if (methodInfo.isType(ExtDirectMethodType.FORM_POST)) {
			throw new IllegalStateException("this controller does not handle form posts");
		}

		List<ParameterInfo> methodParameters = methodInfo.getParameters();
		Object[] parameters = null;

		if (!methodParameters.isEmpty()) {
			parameters = new Object[methodParameters.size()];

			for (int paramIndex = 0; paramIndex < methodParameters.size(); paramIndex++) {
				ParameterInfo methodParameter = methodParameters.get(paramIndex);

				if (methodParameter.isSupportedParameter()) {
					parameters[paramIndex] = SupportedParameters.resolveParameter(methodParameter.getType(), request,
							response, locale);
				} else if (ExtDirectStoreReadRequest.class.isAssignableFrom(methodParameter.getType())) {
					parameters[paramIndex] = ExtDirectStoreReadRequest;
				} else if (directStoreModifyRecords != null && methodParameter.getCollectionType() != null) {
					parameters[paramIndex] = directStoreModifyRecords;
				} else if (methodParameter.isHasRequestParamAnnotation()) {
					parameters[paramIndex] = handleRequestParam(null, remainingParameters, methodParameter);
				} else if (methodParameter.isHasRequestHeaderAnnotation()) {
					parameters[paramIndex] = handleRequestHeader(request, methodParameter);
				} else if (remainingParameters != null && remainingParameters.containsKey(methodParameter.getName())) {
					Object jsonValue = remainingParameters.get(methodParameter.getName());
					parameters[paramIndex] = convertValue(jsonValue, methodParameter);
				} else if (directRequest.getData() != null && directRequest.getData() instanceof List
						&& ((List<Object>) directRequest.getData()).size() > jsonParamIndex) {
					Object jsonValue = ((List<Object>) directRequest.getData()).get(jsonParamIndex);
					parameters[paramIndex] = convertValue(jsonValue, methodParameter);
					jsonParamIndex++;
				} else {
					throw new IllegalArgumentException(
							"Error, parameter mismatch. Please check your remoting method signature to ensure all supported parameters types are used.");
				}

			}
		}

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

	private Object convertValue(final Object value, final ParameterInfo methodParameter) {
		if (value != null) {
			if (methodParameter.getType().equals(value.getClass())) {
				return value;
			} else if (conversionService.canConvert(TypeDescriptor.forObject(value),
					methodParameter.getTypeDescriptor())) {
				return conversionService.convert(value, TypeDescriptor.forObject(value),
						methodParameter.getTypeDescriptor());
			} else {
				return jsonHandler.convertValue(value, methodParameter.getType());
			}
		}
		return value;
	}

	private Object handleRequestParam(final HttpServletRequest request, final Map<String, Object> valueContainer,
			final ParameterInfo parameterInfo) {

		if (parameterInfo.getName() != null) {
			Object value;
			if (request != null) {
				value = request.getParameter(parameterInfo.getName());
			} else if (valueContainer != null) {
				value = valueContainer.get(parameterInfo.getName());
			} else {
				value = null;
			}

			if (value == null) {
				value = parameterInfo.getDefaultValue();
			}

			if (value != null) {
				return convertValue(value, parameterInfo);
			}

			if (parameterInfo.isRequired()) {
				throw new IllegalStateException("Missing parameter '" + parameterInfo.getName() + "' of type ["
						+ parameterInfo.getTypeDescriptor().getType() + "]");
			}
		}

		return null;
	}

	private Object handleRequestHeader(final HttpServletRequest request, final ParameterInfo parameterInfo) {
		String value = request.getHeader(parameterInfo.getName());

		if (value == null) {
			value = parameterInfo.getDefaultValue();
		}

		if (value != null) {
			return convertValue(value, parameterInfo);
		}

		if (parameterInfo.isRequired()) {
			throw new IllegalStateException("Missing header '" + parameterInfo.getName() + "' of type ["
					+ parameterInfo.getTypeDescriptor().getType() + "]");
		}

		return null;
	}

	private Map<String, Object> fillReadRequestFromMap(final ExtDirectStoreReadRequest to,
			final Map<String, Object> from) {
		Set<String> foundParameters = new HashSet<String>();

		for (Entry<String, Object> entry : from.entrySet()) {
			String key = entry.getKey();
			Object value = entry.getValue();

			if (key.equals("filter")) {
				List<Filter> filters = new ArrayList<Filter>();

				if (value instanceof String) {
					List<Map<String, Object>> rawFilters = jsonHandler.readValue((String) value,
							new TypeReference<List<Map<String, Object>>>() {/* empty */
							});

					for (Map<String, Object> rawFilter : rawFilters) {
						filters.add(Filter.createFilter(rawFilter, conversionService));
					}
				} else if (value instanceof List) {
					@SuppressWarnings("unchecked")
					List<Map<String, Object>> filterList = (List<Map<String, Object>>) value;
					for (Map<String, Object> filter : filterList) {
						filters.add(Filter.createFilter(filter, conversionService));
					}
				}
				to.setFilters(filters);
				foundParameters.add(key);
			} else if (key.equals("sort") && value != null && value instanceof List) {

				List<SortInfo> sorters = new ArrayList<SortInfo>();
				@SuppressWarnings("unchecked")
				List<Map<String, Object>> rawSorters = (List<Map<String, Object>>) value;

				for (Map<String, Object> aRawSorter : rawSorters) {
					sorters.add(SortInfo.create(aRawSorter));
				}

				to.setSorters(sorters);
				foundParameters.add(key);
			} else if (key.equals("group") && value != null && value instanceof List) {
				List<GroupInfo> groups = new ArrayList<GroupInfo>();
				@SuppressWarnings("unchecked")
				List<Map<String, Object>> rawGroups = (List<Map<String, Object>>) value;

				for (Map<String, Object> aRawGroupInfo : rawGroups) {
					groups.add(GroupInfo.create(aRawGroupInfo));
				}

				to.setGroups(groups);
				foundParameters.add(key);
			} else {

				PropertyDescriptor descriptor = BeanUtils.getPropertyDescriptor(to.getClass(), key);
				if (descriptor != null && descriptor.getWriteMethod() != null) {
					try {

						descriptor.getWriteMethod().invoke(to,
								conversionService.convert(value, descriptor.getPropertyType()));

						foundParameters.add(key);
					} catch (IllegalArgumentException e) {
						log.error("fillObjectFromMap", e);
					} catch (IllegalAccessException e) {
						log.error("fillObjectFromMap", e);
					} catch (InvocationTargetException e) {
						log.error("fillObjectFromMap", e);
					}
				}
			}
		}

		if (to.getLimit() != null) {
			// this test is no longer needed with extjs 4.0.7 and extjs 4.1.0
			// these two libraries always send page, start and limit
			if (to.getPage() != null && to.getStart() == null) {
				to.setStart(to.getLimit() * (to.getPage() - 1));
				// the else if is still valid for extjs 3 code
			} else if (to.getPage() == null && to.getStart() != null) {
				to.setPage(to.getStart() / to.getLimit() + 1);
			}
		}

		if (to.getSort() != null && to.getDir() != null) {
			List<SortInfo> sorters = new ArrayList<SortInfo>();
			sorters.add(new SortInfo(to.getSort(), SortDirection.fromString(to.getDir())));
			to.setSorters(sorters);
		}

		if (to.getGroupBy() != null && to.getGroupDir() != null) {
			List<GroupInfo> groups = new ArrayList<GroupInfo>();
			groups.add(new GroupInfo(to.getGroupBy(), SortDirection.fromString(to.getGroupDir())));
			to.setGroups(groups);
		}

		Map<String, Object> remainingParameters = new HashMap<String, Object>();
		for (Entry<String, Object> entry : from.entrySet()) {
			if (!foundParameters.contains(entry.getKey())) {
				remainingParameters.put(entry.getKey(), entry.getValue());
			}
		}
		to.setParams(remainingParameters);

		return remainingParameters;
	}

	private List<Object> convertObjectEntriesToType(final List<Object> records, final Class<?> directStoreType) {
		if (records != null) {
			List<Object> convertedList = new ArrayList<Object>();
			for (Object record : records) {
				Object convertedObject = jsonHandler.convertValue(record, directStoreType);
				convertedList.add(convertedObject);
			}
			return convertedList;
		}
		return null;
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
