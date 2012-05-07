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

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Collection;

import org.springframework.core.GenericCollectionTypeResolver;
import org.springframework.core.MethodParameter;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ValueConstants;

/**
 * Object holds information about a parameter as the name, type and the
 * attributes of a RequestParam annotation
 * 
 * @author Ralph Schaer
 */
public class ParameterInfo {
	private Class<?> type;
	private Class<?> collectionType;
	private String name;
	private boolean hasRequestParamAnnotation;
	private boolean required;
	private String defaultValue;
	private TypeDescriptor typeDescriptor;
	private boolean supportedParameter;

	public ParameterInfo(Class<?> clazz, Method method, Method methodWithAnnotation, int paramIndex, Class<?> type,
			String paramName, Annotation[] paramAnnotations) {
		this.type = type;
		this.supportedParameter = SupportedParameterTypes.isSupported(type);
		this.name = paramName;

		Method typeDescriptorMethod = method;
		if (methodWithAnnotation != null) {
			typeDescriptorMethod = methodWithAnnotation;
		}
		MethodParameter methodParameter = new MethodParameter(typeDescriptorMethod, paramIndex);
		this.typeDescriptor = new TypeDescriptor(methodParameter);

		if (Collection.class.isAssignableFrom(type)) {
			this.collectionType = getCollectionParameterType(clazz, method, paramIndex, methodParameter);
		}

		if (paramAnnotations != null) {

			for (Annotation paramAnn : paramAnnotations) {
				if (RequestParam.class.isInstance(paramAnn)) {
					RequestParam requestParam = (RequestParam) paramAnn;
					if (StringUtils.hasText(requestParam.value())) {
						this.name = requestParam.value();
					}
					this.required = requestParam.required();
					this.defaultValue = ValueConstants.DEFAULT_NONE.equals(requestParam.defaultValue()) ? null
							: requestParam.defaultValue();
					this.hasRequestParamAnnotation = true;
					break;
				}
			}
		}
	}

	public Class<?> getType() {
		return type;
	}

	public Class<?> getCollectionType() {
		return collectionType;
	}

	public String getName() {
		return name;
	}

	public boolean isHasRequestParamAnnotation() {
		return hasRequestParamAnnotation;
	}

	public boolean isRequired() {
		return required;
	}

	public String getDefaultValue() {
		return defaultValue;
	}

	public boolean isSupportedParameter() {
		return supportedParameter;
	}

	public TypeDescriptor getTypeDescriptor() {
		return typeDescriptor;
	}

	private Class<?> getCollectionParameterType(Class<?> clazz, final Method method, final int paramIndex,
			final MethodParameter methodParameter) {

		Class<?> paramType = GenericCollectionTypeResolver.getCollectionParameterType(methodParameter);

		if (paramType == null) {

			Class<?> superClass = clazz.getSuperclass();

			while (superClass != null && paramType == null) {
				try {
					Method equivalentMethod = superClass
							.getDeclaredMethod(method.getName(), method.getParameterTypes());
					paramType = GenericCollectionTypeResolver.getCollectionParameterType(new MethodParameter(
							equivalentMethod, paramIndex));

				} catch (NoSuchMethodException e) {
					// do nothing here
				}
				superClass = superClass.getSuperclass();
			}
		}
		return paramType;
	}
}
