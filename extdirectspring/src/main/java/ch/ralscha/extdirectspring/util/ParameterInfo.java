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

import org.springframework.core.GenericTypeResolver;
import org.springframework.core.LocalVariableTableParameterNameDiscoverer;
import org.springframework.core.MethodParameter;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ValueConstants;

/**
 * Object holds information about a parameter. i.e. the name, type and the
 * attributes of a RequestParam annotation.
 * 
 * @author Ralph Schaer
 */
public final class ParameterInfo {

	private static final LocalVariableTableParameterNameDiscoverer discoverer = new LocalVariableTableParameterNameDiscoverer();

	private String name;

	private final TypeDescriptor typeDescriptor;

	private final boolean supportedParameter;

	private boolean hasRequestParamAnnotation;

	private boolean hasRequestHeaderAnnotation;

	private boolean required;

	private String defaultValue;

	public ParameterInfo(Method method, int paramIndex) {

		MethodParameter methodParam = new MethodParameter(method, paramIndex);
		methodParam.initParameterNameDiscovery(discoverer);
		GenericTypeResolver.resolveParameterType(methodParam, method.getClass());

		this.name = methodParam.getParameterName();
		this.typeDescriptor = new TypeDescriptor(methodParam);

		this.supportedParameter = SupportedParameters.isSupported(typeDescriptor.getObjectType());

		Annotation[] paramAnnotations = methodParam.getParameterAnnotations();

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
				this.hasRequestHeaderAnnotation = false;
				break;
			} else if (RequestHeader.class.isInstance(paramAnn)) {
				RequestHeader requestHeader = (RequestHeader) paramAnn;
				if (StringUtils.hasText(requestHeader.value())) {
					this.name = requestHeader.value();
				}
				this.required = requestHeader.required();
				this.defaultValue = ValueConstants.DEFAULT_NONE.equals(requestHeader.defaultValue()) ? null
						: requestHeader.defaultValue();
				this.hasRequestParamAnnotation = false;
				this.hasRequestHeaderAnnotation = true;
				break;
			}
		}
	}

	public Class<?> getType() {
		return typeDescriptor.getType();
	}

	public Class<?> getCollectionType() {
		if (typeDescriptor.isCollection()) {
			return typeDescriptor.getElementType();
		}
		return null;
	}

	public String getName() {
		return name;
	}

	public boolean isHasRequestParamAnnotation() {
		return hasRequestParamAnnotation;
	}

	public boolean isHasRequestHeaderAnnotation() {
		return hasRequestHeaderAnnotation;
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

}
