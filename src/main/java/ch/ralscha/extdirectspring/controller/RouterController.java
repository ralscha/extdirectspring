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
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.codehaus.jackson.type.TypeReference;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import ch.ralscha.extdirectspring.annotation.ExtDirectMethodType;
import ch.ralscha.extdirectspring.bean.BaseResponse;
import ch.ralscha.extdirectspring.bean.ExtDirectFormLoadResult;
import ch.ralscha.extdirectspring.bean.ExtDirectPollResponse;
import ch.ralscha.extdirectspring.bean.ExtDirectRequest;
import ch.ralscha.extdirectspring.bean.ExtDirectResponse;
import ch.ralscha.extdirectspring.bean.ExtDirectStoreReadRequest;
import ch.ralscha.extdirectspring.bean.ExtDirectStoreResponse;
import ch.ralscha.extdirectspring.bean.SortDirection;
import ch.ralscha.extdirectspring.bean.SortInfo;
import ch.ralscha.extdirectspring.filter.Filter;
import ch.ralscha.extdirectspring.util.ExtDirectSpringUtil;
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

	@Deprecated
	@Autowired(required = false)
	@Qualifier("extDirectSpringExceptionToMessage")
	private Map<String, Map<Class<?>, String>> exceptionToMessage;
	
	@Autowired(required = false) 
	private Configuration configuration;
	
	@Autowired
	public RouterController(ApplicationContext context, ConversionService conversionService) {
		this.context = context;
		this.conversionService = conversionService;
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

			directPollResponse.setData(ExtDirectSpringUtil.invoke(context, beanName, methodInfo, parameters));
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
			@RequestBody String rawRequestString) {

		List<ExtDirectRequest> directRequests = getExtDirectRequests(rawRequestString);
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
							&& !ExtDirectStoreResponse.class.isAssignableFrom(result.getClass())) {
						result = new ExtDirectStoreResponse((Collection) result);
					}

					directResponse.setResult(result);
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

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private Object processRemotingRequest(final HttpServletRequest request, final HttpServletResponse response,
			final Locale locale, final ExtDirectRequest directRequest, final MethodInfo methodInfo) throws Exception {

		int jsonParamIndex = 0;
		Map<String, Object> remainingParameters = null;
		ExtDirectStoreReadRequest directStoreReadRequest = null;

		List<Object> directStoreModifyRecords = null;
		Class<?> directStoreEntryClass;

		if (methodInfo.isType(ExtDirectMethodType.STORE_READ) || methodInfo.isType(ExtDirectMethodType.FORM_LOAD)
				|| methodInfo.isType(ExtDirectMethodType.TREE_LOADER)) {

			List<Object> data = (List<Object>)directRequest.getData();
			
			if (data != null && data.size() > 0) {
				if (methodInfo.isType(ExtDirectMethodType.STORE_READ)) {
					directStoreReadRequest = new ExtDirectStoreReadRequest();
					remainingParameters = fillReadRequestFromMap(directStoreReadRequest, (Map) data.get(0));
				} else {
					remainingParameters = (Map) data.get(0);
				}
				jsonParamIndex = 1;
			}
		} else if (methodInfo.isType(ExtDirectMethodType.STORE_MODIFY)) {
			directStoreEntryClass = methodInfo.getCollectionType();
			List<Object> data = (List<Object>)directRequest.getData();
			
			if (data != null && data.size() > 0) {
				Map<String, Object> jsonData = (Map<String, Object>) data.get(0);

				ArrayList<Object> records = (ArrayList<Object>) jsonData.get("records");
				directStoreModifyRecords = convertObjectEntriesToType(records, directStoreEntryClass);
				jsonParamIndex = 1;

				remainingParameters = new HashMap<String, Object>(jsonData);
				remainingParameters.remove("records");

			}
		} else if (methodInfo.isType(ExtDirectMethodType.SIMPLE_NAMED)) {
			Map<String, Object> data = (Map<String, Object>)directRequest.getData();
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
					if (methodParameter.getType().equals(jsonValue.getClass())) {
						parameters[paramIndex] = jsonValue;
					} else if (conversionService.canConvert(jsonValue.getClass(), methodParameter.getType())) {
						parameters[paramIndex] = conversionService.convert(jsonValue, methodParameter.getType());		
					} else {
						parameters[paramIndex] = ExtDirectSpringUtil.convertObject(jsonValue, methodParameter.getType());
					}

				} else if (directRequest.getData() != null && directRequest.getData() instanceof List && ((List<Object>)directRequest.getData()).size() > jsonParamIndex) {
					
					Object jsonValue = ((List<Object>)directRequest.getData()).get(jsonParamIndex);
				
					if (methodParameter.getType().equals(jsonValue.getClass())) {
						parameters[paramIndex] = jsonValue;
					} else if (conversionService.canConvert(jsonValue.getClass(), methodParameter.getType())) {
						parameters[paramIndex] = conversionService.convert(jsonValue, methodParameter.getType());		
					} else {
						parameters[paramIndex] = ExtDirectSpringUtil.convertObject(jsonValue, methodParameter.getType());
					}

					jsonParamIndex++;
				} else {
					throw new IllegalArgumentException(
							"Error, parameter mismatch. Please check your remoting method signature to ensure all supported parameters types are used.");
				}

			}
		}

		return ExtDirectSpringUtil.invoke(context, directRequest.getAction(), methodInfo, parameters);
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
				return conversionService.convert(value, parameterInfo.getType());
			}

			if (parameterInfo.isRequired()) {
				throw new IllegalArgumentException("Missing request parameter: " + parameterInfo.getName());
			}
		}

		return null;
	}

	private Map<String, Object> fillReadRequestFromMap(final ExtDirectStoreReadRequest to, final Map<String, Object> from) {
		Set<String> foundParameters = new HashSet<String>();

		for (Entry<String, Object> entry : from.entrySet()) {

			if (entry.getKey().equals("filter")) {
				List<Filter> filters = new ArrayList<Filter>();

				List<Map<String, Object>> rawFilters = ExtDirectSpringUtil.deserializeJsonToObject(
						(String) entry.getValue(), new TypeReference<List<Map<String, Object>>>() {/* empty */
						});

				for (Map<String, Object> rawFilter : rawFilters) {
					filters.add(Filter.createFilter(rawFilter));
				}

				to.setFilters(filters);
				foundParameters.add(entry.getKey());
			} else if (entry.getKey().equals("sort") && entry.getValue() != null && entry.getValue() instanceof List) {
				
				List<SortInfo> sorters = new ArrayList<SortInfo>();
				@SuppressWarnings("unchecked")
				List<Map<String, Object>> rawSorters = (List<Map<String, Object>>)entry.getValue();
				
				for (Map<String, Object> aRawSorter : rawSorters) {
					sorters.add(SortInfo.createSortInfo(aRawSorter));
				}
				
				to.setSorters(sorters);
				foundParameters.add(entry.getKey());
			} else {

				PropertyDescriptor descriptor = BeanUtils.getPropertyDescriptor(to.getClass(), entry.getKey());
				if (descriptor != null && descriptor.getWriteMethod() != null) {
					try {

						descriptor.getWriteMethod().invoke(to,
								conversionService.convert(entry.getValue(), descriptor.getPropertyType()));

						foundParameters.add(entry.getKey());
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
			if (to.getPage() != null) {
				to.setStart(to.getLimit() * (to.getPage()-1));
			} else if (to.getStart() != null) {
			    to.setPage(to.getStart() / to.getLimit() + 1);
			}
		}
		
		if (to.getSort() != null && to.getDir() != null) {
			List<SortInfo> sorters = new ArrayList<SortInfo>();
			sorters.add(new SortInfo(to.getSort(), SortDirection.fromString(to.getDir())));
			to.setSorters(sorters);
		}
		
		Map<String, Object> remainingParameters = new HashMap<String, Object>();
		for (Entry<String, Object> entry : from.entrySet()) {
			if (!foundParameters.contains(entry.getKey())) {
				remainingParameters.put(entry.getKey(), entry.getValue());
			}
		}
		return remainingParameters;
	}

	private List<Object> convertObjectEntriesToType(final ArrayList<Object> records, final Class<?> directStoreType) {
		if (records != null) {
			List<Object> convertedList = new ArrayList<Object>();
			for (Object record : records) {
				Object convertedObject = ExtDirectSpringUtil.deserializeJsonToObject(
						ExtDirectSpringUtil.serializeObjectToJson(record), directStoreType);
				convertedList.add(convertedObject);
			}
			return convertedList;
		}
		return null;
	}

	private List<ExtDirectRequest> getExtDirectRequests(final String rawRequestString) {
		List<ExtDirectRequest> directRequests = new ArrayList<ExtDirectRequest>();

		if (rawRequestString.length() > 0 && rawRequestString.charAt(0) == '[') {
			directRequests.addAll(ExtDirectSpringUtil.deserializeJsonToObject(rawRequestString,
					new TypeReference<List<ExtDirectRequest>>() {/* empty */
					}));
		} else {
			ExtDirectRequest directRequest = ExtDirectSpringUtil.deserializeJsonToObject(rawRequestString,
					ExtDirectRequest.class);
			directRequests.add(directRequest);
		}

		return directRequests;
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
