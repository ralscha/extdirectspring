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
package ch.ralscha.extdirectspring.util;

import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.stereotype.Component;

import ch.ralscha.extdirectspring.annotation.ExtDirectMethodType;
import ch.ralscha.extdirectspring.bean.ExtDirectRequest;
import ch.ralscha.extdirectspring.bean.ExtDirectStoreReadRequest;
import ch.ralscha.extdirectspring.bean.GroupInfo;
import ch.ralscha.extdirectspring.bean.SortDirection;
import ch.ralscha.extdirectspring.bean.SortInfo;
import ch.ralscha.extdirectspring.filter.Filter;

import com.fasterxml.jackson.core.type.TypeReference;

/**
 * Resolver of ExtDirectRequest parameters.
 * 
 * @author Goddanao
 */
@Component
public final class ParametersResolver implements InitializingBean {

	private static final Log log = LogFactory.getLog(ParametersResolver.class);

	@Autowired
	private ConversionService conversionService;

	@Autowired(required = false)
	private JsonHandler jsonHandler;

	@Override
	public void afterPropertiesSet() throws Exception {
		if (jsonHandler == null) {
			jsonHandler = new JsonHandler();
		}
	}

	@SuppressWarnings("unchecked")
	public Object[] resolveParameters(HttpServletRequest request, HttpServletResponse response, Locale locale,
			ExtDirectRequest directRequest, MethodInfo methodInfo) throws Exception {

		int jsonParamIndex = 0;
		Map<String, Object> remainingParameters = null;
		ExtDirectStoreReadRequest extDirectStoreReadRequest = null;

		List<Object> directStoreModifyRecords = null;
		Class<?> directStoreEntryClass;

		if (methodInfo.isType(ExtDirectMethodType.STORE_READ) || methodInfo.isType(ExtDirectMethodType.FORM_LOAD)
				|| methodInfo.isType(ExtDirectMethodType.TREE_LOAD)) {

			List<Object> data = (List<Object>) directRequest.getData();

			if (data != null && data.size() > 0) {
				if (methodInfo.isType(ExtDirectMethodType.STORE_READ)) {
					extDirectStoreReadRequest = new ExtDirectStoreReadRequest();
					remainingParameters = fillReadRequestFromMap(extDirectStoreReadRequest,
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
					parameters[paramIndex] = extDirectStoreReadRequest;
				} else if (directStoreModifyRecords != null && methodParameter.getCollectionType() != null) {
					parameters[paramIndex] = directStoreModifyRecords;
				} else if (methodParameter.isHasRequestParamAnnotation()) {
					parameters[paramIndex] = resolveRequestParam(null, remainingParameters, methodParameter);
				} else if (methodParameter.isHasRequestHeaderAnnotation()) {
					parameters[paramIndex] = resolveRequestHeader(request, methodParameter);
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

		return parameters;
	}

	public Object resolveRequestParam(HttpServletRequest request, Map<String, Object> valueContainer,
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

	public Object resolveRequestHeader(HttpServletRequest request, ParameterInfo parameterInfo) {
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

	private Object convertValue(Object value, ParameterInfo methodParameter) {
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

	private Map<String, Object> fillReadRequestFromMap(ExtDirectStoreReadRequest to, Map<String, Object> from) {
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

	private List<Object> convertObjectEntriesToType(List<Object> records, Class<?> directStoreType) {
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

}
