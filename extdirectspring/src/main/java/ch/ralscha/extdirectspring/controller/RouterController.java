/**
 * Copyright 2010-2011 Ralph Schaer <ralphschaer@gmail.com>
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
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.InvocationTargetException;
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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.codehaus.jackson.type.TypeReference;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.util.WebUtils;

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
import ch.ralscha.extdirectspring.util.ParameterInfo;
import ch.ralscha.extdirectspring.util.SupportedParameterTypes;

/**
 * Main router controller who handles polling, form handler and normal
 * Ext.Direct calls
 * 
 * @author mansari
 * @author Ralph Schaer
 */
@Controller
public class RouterController implements InitializingBean {

	private static final Log log = LogFactory.getLog(RouterController.class);

	private ConversionService conversionService;
	private ApplicationContext context;
	private JsonHandler jsonHandler;

	@Deprecated
	@Autowired(required = false)
	@Qualifier("extDirectSpringExceptionToMessage")
	private Map<String, Map<Class<?>, String>> exceptionToMessage;

	@Autowired(required = false)
	private Configuration configuration;

	@Autowired
	public RouterController(ApplicationContext context, ConversionService conversionService, JsonHandler jsonHandler) {
		this.context = context;
		this.conversionService = conversionService;
		this.jsonHandler = jsonHandler;
	}

	public void setConfiguration(Configuration configuration) {
		this.configuration = configuration;
	}

	public void afterPropertiesSet() throws Exception {

		if (configuration == null) {
			configuration = new Configuration();
		}

		if (exceptionToMessage != null && configuration.getExceptionToMessage() == null) {
			configuration.setExceptionToMessage(exceptionToMessage.get("extDirectSpringExceptionToMessage"));
		}
	}

	@RequestMapping(value = "/poll/{beanName}/{method}/{event}")
	@ResponseBody
	public ExtDirectPollResponse poll(@PathVariable("beanName") String beanName, @PathVariable("method") String method,
			@PathVariable("event") String event, HttpServletRequest request, HttpServletResponse response, Locale locale)
			throws Exception {

		ExtDirectPollResponse directPollResponse = new ExtDirectPollResponse();
		directPollResponse.setName(event);

		try {
			MethodInfo methodInfo = ExtDirectSpringUtil.findMethodInfo(context, beanName, method);

			List<ParameterInfo> methodParameters = methodInfo.getParameters();
			Object[] parameters = null;
			if (!methodParameters.isEmpty()) {
				parameters = new Object[methodParameters.size()];

				for (int paramIndex = 0; paramIndex < methodParameters.size(); paramIndex++) {
					ParameterInfo methodParameter = methodParameters.get(paramIndex);

					if (methodParameter.isSupportedParameter()) {
						parameters[paramIndex] = SupportedParameterTypes.resolveParameter(methodParameter.getType(),
								request, response, locale);
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
						directPollResponse.setData(ExtDirectSpringUtil.invoke(context, beanName, methodInfo, parameters));
					}
				} else {
					directPollResponse.setData(ExtDirectSpringUtil.invoke(context, beanName, methodInfo, parameters));
				}
			} else {			
				directPollResponse.setData(ExtDirectSpringUtil.invoke(context, beanName, methodInfo, parameters));
			}
			
		} catch (Exception e) {
			log.error("Error polling method '" + beanName + "." + method + "'", e.getCause() != null ? e.getCause() : e);
			handleException(directPollResponse, e);
		}
		return directPollResponse;

	}

	@RequestMapping(value = "/router", method = RequestMethod.POST, params = "extAction")
	public String router(@RequestParam("extAction") String extAction, @RequestParam("extMethod") String extMethod) {

		MethodInfo methodInfo = ExtDirectSpringUtil.findMethodInfo(context, extAction, extMethod);
		if (methodInfo.getForwardPath() != null) {
			return methodInfo.getForwardPath();
		}
		throw new IllegalArgumentException("Invalid remoting form method: " + extAction + "." + extMethod);

	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@RequestMapping(value = "/router", method = RequestMethod.POST, params = "!extAction")
	@ResponseBody
	public List<ExtDirectResponse> router(HttpServletRequest request, HttpServletResponse response, Locale locale,
			@RequestBody Object requestData) {

		List<ExtDirectRequest> directRequests = new ArrayList<ExtDirectRequest>();
		if (requestData instanceof Map) {
			directRequests.add(jsonHandler.convertValue(requestData, ExtDirectRequest.class));
		} else if (requestData instanceof List) {
			for (Object oneRequest : (List) requestData) {
				directRequests.add(jsonHandler.convertValue(oneRequest, ExtDirectRequest.class));
			}
		}

		List<ExtDirectResponse> directResponses = new ArrayList<ExtDirectResponse>();

		for (ExtDirectRequest directRequest : directRequests) {

			ExtDirectResponse directResponse = new ExtDirectResponse(directRequest);

			try {
				MethodInfo methodInfo = ExtDirectSpringUtil.findMethodInfo(context, directRequest.getAction(),
						directRequest.getMethod());

				Object result = processRemotingRequest(request, response, locale, directRequest, methodInfo);

				if (result != null) {

					if (methodInfo.isType(ExtDirectMethodType.FORM_LOAD)
							&& !ExtDirectFormLoadResult.class.isAssignableFrom(result.getClass())) {
						result = new ExtDirectFormLoadResult(result);
					} else if ((methodInfo.isType(ExtDirectMethodType.STORE_MODIFY) || methodInfo
							.isType(ExtDirectMethodType.STORE_READ))
							&& !ExtDirectStoreResponse.class.isAssignableFrom(result.getClass())
							&& configuration.isAlwaysWrapStoreResponse()) {
						result = new ExtDirectStoreResponse((Collection) result);
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

			directResponses.add(directResponse);
		}

		return directResponses;

	}

	private String getStackTrace(final Throwable t) {
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw, true);
		t.printStackTrace(pw);
		pw.flush();
		sw.flush();
		return sw.toString();
	}

	@SuppressWarnings({ "unchecked" })
	private Object processRemotingRequest(final HttpServletRequest request, final HttpServletResponse response,
			final Locale locale, final ExtDirectRequest directRequest, final MethodInfo methodInfo) throws Exception {

		int jsonParamIndex = 0;
		Map<String, Object> remainingParameters = null;
		ExtDirectStoreReadRequest directStoreReadRequest = null;

		List<Object> directStoreModifyRecords = null;
		Class<?> directStoreEntryClass;

		if (methodInfo.isType(ExtDirectMethodType.STORE_READ) || methodInfo.isType(ExtDirectMethodType.FORM_LOAD)
				|| methodInfo.isType(ExtDirectMethodType.TREE_LOADER)
				|| methodInfo.isType(ExtDirectMethodType.TREE_LOAD)) {

			List<Object> data = (List<Object>) directRequest.getData();

			if (data != null && data.size() > 0) {
				if (methodInfo.isType(ExtDirectMethodType.STORE_READ)) {
					directStoreReadRequest = new ExtDirectStoreReadRequest();
					remainingParameters = fillReadRequestFromMap(directStoreReadRequest,
							(Map<String, Object>) data.get(0));
				} else {
					remainingParameters = (Map<String, Object>) data.get(0);
				}
				jsonParamIndex = 1;
			}
		} else if (methodInfo.isType(ExtDirectMethodType.STORE_MODIFY)) {
			directStoreEntryClass = methodInfo.getCollectionType();
			List<Object> data = (List<Object>) directRequest.getData();

			if (data != null && data.size() > 0) {

				if (data.get(0) instanceof List) {
					directStoreModifyRecords = convertObjectEntriesToType((List<Object>) data.get(0),
							directStoreEntryClass);
				} else {
					Map<String, Object> jsonData = (Map<String, Object>) data.get(0);
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
					parameters[paramIndex] = SupportedParameterTypes.resolveParameter(methodParameter.getType(),
							request, response, locale);
				} else if (ExtDirectStoreReadRequest.class.isAssignableFrom(methodParameter.getType())) {
					parameters[paramIndex] = directStoreReadRequest;
				} else if (directStoreModifyRecords != null && methodParameter.getCollectionType() != null) {
					parameters[paramIndex] = directStoreModifyRecords;
				} else if (methodParameter.isHasRequestParamAnnotation()) {
					parameters[paramIndex] = handleRequestParam(null, remainingParameters, methodParameter);
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

	private Object convertValue(Object jsonValue, ParameterInfo methodParameter) {
		if (jsonValue != null) {
			if (methodParameter.getType().equals(jsonValue.getClass())) {
				return jsonValue;
			} else if (conversionService.canConvert(TypeDescriptor.forObject(jsonValue), methodParameter.getTypeDescriptor())) {
				return conversionService.convert(jsonValue, TypeDescriptor.forObject(jsonValue),  methodParameter.getTypeDescriptor());
			} else {
				return jsonHandler.convertValue(jsonValue, methodParameter.getType());
			}
		}
		return jsonValue;
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
				throw new IllegalArgumentException("Missing request parameter: " + parameterInfo.getName());
			}
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
							new TypeReference<List<Map<String, Object>>>() {/* empty */});
	
					for (Map<String, Object> rawFilter : rawFilters) {
						filters.add(Filter.createFilter(rawFilter, conversionService));
					}
				} else if (value instanceof List) {
					@SuppressWarnings("unchecked")
					List<Map<String, Object>> filterList = (List<Map<String, Object>>)value;
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
			if (to.getPage() != null && to.getStart() == null) {
				to.setStart(to.getLimit() * (to.getPage() - 1));
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
			response.setWhere(getStackTrace(cause));
		} else {
			response.setWhere(null);
		}
	}

}
