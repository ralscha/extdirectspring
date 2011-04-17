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
package ch.ralscha.extdirectspring.util;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Proxy;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.aop.support.AopUtils;
import org.springframework.core.GenericCollectionTypeResolver;
import org.springframework.core.GenericTypeResolver;
import org.springframework.core.LocalVariableTableParameterNameDiscoverer;
import org.springframework.core.MethodParameter;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ValueConstants;

import ch.ralscha.extdirectspring.annotation.ExtDirectMethod;
import ch.ralscha.extdirectspring.annotation.ExtDirectMethodType;

/**
 * Object holds information about a method like the method itself and a list of
 * parameters
 * 
 * @author Ralph Schaer
 */
public class MethodInfo {
	private static final LocalVariableTableParameterNameDiscoverer discoverer = new LocalVariableTableParameterNameDiscoverer();

	private List<ParameterInfo> parameters;
	private Method method;
	private String forwardPath;

	private ExtDirectMethodType type;
	private Class<?> collectionType;

	public MethodInfo(final Class<?> clazz, final Method method) {

		this.method = method;

		RequestMapping methodAnnotation = AnnotationUtils.findAnnotation(method, RequestMapping.class);
		if (methodAnnotation != null) {

			RequestMapping classAnnotation = AnnotationUtils.findAnnotation(clazz, RequestMapping.class);

			String path = null;
			if (hasValue(classAnnotation)) {
				path = classAnnotation.value()[0];
			}

			if (hasValue(methodAnnotation)) {
				String methodPath = methodAnnotation.value()[0];
				if (path != null) {
					path = path + methodPath;
				} else {
					path = methodPath;
				}
			}

			if (path != null) {
				if (path.charAt(0) == '/' && path.length() > 1) {
					path = path.substring(1, path.length());
				}
				this.forwardPath = "forward:" + path;
			}
		}

		ExtDirectMethod extDirectMethodAnnotation = AnnotationUtils.findAnnotation(method, ExtDirectMethod.class);
		if (extDirectMethodAnnotation != null) {
			this.type = extDirectMethodAnnotation.value();
		}

		this.parameters = buildParameterList(clazz, method);

		for (ParameterInfo parameter : parameters) {
			if (parameter.getCollectionType() != null) {
				this.collectionType = parameter.getCollectionType();
				break;
			}
		}

	}

	private boolean hasValue(RequestMapping requestMapping) {
		return (requestMapping != null && requestMapping.value() != null && requestMapping.value().length > 0 && StringUtils
				.hasText(requestMapping.value()[0]));
	}

	private static List<ParameterInfo> buildParameterList(Class<?> clazz, Method method) {
		List<ParameterInfo> params = new ArrayList<ParameterInfo>();

		Class<?>[] parameterTypes = method.getParameterTypes();
		Annotation[][] parameterAnnotations = null;
		String[] parameterNames = null;

		Method methodWithAnnotation = ExtDirectSpringUtil.findMethodWithAnnotation(method, ExtDirectMethod.class);
		if (methodWithAnnotation != null) {
			parameterAnnotations = methodWithAnnotation.getParameterAnnotations();
			parameterNames = discoverer.getParameterNames(methodWithAnnotation);
		}

		for (int paramIndex = 0; paramIndex < parameterTypes.length; paramIndex++) {

			ParameterInfo parameterInfo = new ParameterInfo();
			parameterInfo.setType(parameterTypes[paramIndex]);

			parameterInfo.setSupportedParameter(SupportedParameterTypes.isSupported(parameterTypes[paramIndex]));

			if (parameterNames != null) {
				parameterInfo.setName(parameterNames[paramIndex]);
			}

			if (parameterAnnotations != null) {

				for (Annotation paramAnn : parameterAnnotations[paramIndex]) {
					if (RequestParam.class.isInstance(paramAnn)) {
						RequestParam requestParam = (RequestParam) paramAnn;
						if (StringUtils.hasText(requestParam.value())) {
							parameterInfo.setName(requestParam.value());
						}
						parameterInfo.setRequired(requestParam.required());
						parameterInfo
								.setDefaultValue(ValueConstants.DEFAULT_NONE.equals(requestParam.defaultValue()) ? null
										: requestParam.defaultValue());
						parameterInfo.setHasRequestParamAnnotation(true);
						break;
					}
				}
			}

			if (Collection.class.isAssignableFrom(parameterTypes[paramIndex])) {
				parameterInfo.setCollectionType(getCollectionParameterType(clazz, method, paramIndex));
			}

			params.add(parameterInfo);
		}

		return params;
	}

	public Method getMethod() {
		return method;
	}

	public String getForwardPath() {
		return forwardPath;
	}

	public List<ParameterInfo> getParameters() {
		return parameters;
	}

	public Class<?> getCollectionType() {
		return collectionType;
	}

	public boolean isType(final ExtDirectMethodType methodType) {
		return this.type == methodType;
	}

	private static Class<?> getCollectionParameterType(Class<?> clazz, final Method method, final int paramIndex) {
		MethodParameter methodParameter = new MethodParameter(method, paramIndex);
		Class<?> paramType = GenericCollectionTypeResolver.getCollectionParameterType(methodParameter);

		if (paramType == null) {
			
			Map<TypeVariable<?>, Class<?>> typeVarMap = getTypeVariableMap(clazz);
			
			paramType = getGenericCollectionParameterType(typeVarMap, method, paramIndex);
			
			Class<?> superClass = clazz.getSuperclass();

			while (superClass != null && paramType == null) {
				try {
					Method equivalentMethod = superClass
							.getDeclaredMethod(method.getName(), method.getParameterTypes());
					paramType = GenericCollectionTypeResolver.getCollectionParameterType(new MethodParameter(
							equivalentMethod, paramIndex));

					if (paramType == null) {
						paramType = getGenericCollectionParameterType(typeVarMap, equivalentMethod, paramIndex);
					}

				} catch (NoSuchMethodException e) {
					// do nothing here
				}
				superClass = superClass.getSuperclass();
			}
		}

		return paramType;
	}

	private static Class<?> getGenericCollectionParameterType(final Map<TypeVariable<?>, Class<?>> typeVarMap, final Method method, final int paramIndex) {
		
		if (!typeVarMap.isEmpty()) {
			Type genericType = method.getGenericParameterTypes()[paramIndex];

			if (genericType instanceof ParameterizedType) {
				ParameterizedType parameterizedType = (ParameterizedType) genericType;
				Type actualType = parameterizedType.getActualTypeArguments()[0];
				if (actualType instanceof TypeVariable) {
					return typeVarMap.get(actualType);
				}
			}
		}
		return null;

	}

	/**
	 * Copy of Spring's {@link org.springframework.core.GenericTypeResolver}. Needed
	 * until {@link #getTypeVariableMap(Class)} gets public.
	 * 
	 * TODO: remove that method, as soon as Spring 3.0.6 gets released.
	 */
	private static Map<TypeVariable<?>, Class<?>> getTypeVariableMap(final Class<?> c) {
		Map<TypeVariable<?>, Class<?>> varMap = new HashMap<TypeVariable<?>, Class<?>>();
		
		Class<?> clazz;		
		if (Proxy.isProxyClass(c) || AopUtils.isCglibProxyClass(c)) {
			clazz = c.getSuperclass();
		} else {
			clazz = c;
		}
		
		Type genericSuperclassType = clazz.getGenericSuperclass();
		if (genericSuperclassType instanceof ParameterizedType) {
			ParameterizedType parameterizedType = (ParameterizedType) genericSuperclassType;
			Class<?>[] typeArguments = GenericTypeResolver.resolveTypeArguments(clazz, clazz.getSuperclass());
			varMap = new HashMap<TypeVariable<?>, Class<?>>();

			TypeVariable<?>[] typeVariables = ((Class<?>) parameterizedType.getRawType()).getTypeParameters();

			for (int i = 0; i < typeVariables.length; i++) {
				varMap.put(typeVariables[i], typeArguments[i]);
			}
		}		

		return varMap;
	}
}