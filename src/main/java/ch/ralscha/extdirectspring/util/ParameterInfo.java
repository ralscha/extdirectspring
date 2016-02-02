/**
 * Copyright 2010-2016 Ralph Schaer <ralphschaer@gmail.com>
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

import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.core.GenericTypeResolver;
import org.springframework.core.MethodParameter;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ValueConstants;

import ch.ralscha.extdirectspring.annotation.MetadataParam;

/**
 * Object holds information about a parameter. i.e. the name, type and the attributes of a
 * RequestParam annotation.
 */
public final class ParameterInfo {

	private static final DefaultParameterNameDiscoverer discoverer = new DefaultParameterNameDiscoverer();

	private String name;

	private final TypeDescriptor typeDescriptor;

	private final boolean supportedParameter;

	private boolean hasRequestParamAnnotation;

	private boolean hasRequestHeaderAnnotation;

	private boolean hasCookieValueAnnotation;

	private boolean hasMetadataParamAnnotation;

	private Boolean hasAuthenticationPrincipalAnnotation;

	private boolean required;

	private final boolean javaUtilOptional;

	private String defaultValue;

	public ParameterInfo(Class<?> clazz, Method method, int paramIndex) {

		MethodParameter methodParam = new MethodParameter(method, paramIndex);
		methodParam.initParameterNameDiscovery(discoverer);
		GenericTypeResolver.resolveParameterType(methodParam, clazz);

		this.name = methodParam.getParameterName();
		this.typeDescriptor = new TypeDescriptor(methodParam);

		Class<?> paramType = methodParam.getParameterType();
		this.javaUtilOptional = paramType.getName().equals("java.util.Optional");

		this.supportedParameter = SupportedParameters
				.isSupported(this.typeDescriptor.getObjectType());

		Annotation[] paramAnnotations = methodParam.getParameterAnnotations();

		for (Annotation paramAnn : paramAnnotations) {

			this.hasRequestParamAnnotation = false;
			this.hasMetadataParamAnnotation = false;
			this.hasRequestHeaderAnnotation = false;
			this.hasCookieValueAnnotation = false;
			this.hasAuthenticationPrincipalAnnotation = null;

			if (RequestParam.class.isInstance(paramAnn)) {
				RequestParam requestParam = (RequestParam) paramAnn;
				if (StringUtils.hasText(requestParam.value())) {
					this.name = requestParam.value();
				}
				this.required = requestParam.required();
				this.defaultValue = ValueConstants.DEFAULT_NONE.equals(
						requestParam.defaultValue()) ? null : requestParam.defaultValue();
				this.hasRequestParamAnnotation = true;
				break;
			}
			else if (MetadataParam.class.isInstance(paramAnn)) {
				MetadataParam metadataParam = (MetadataParam) paramAnn;
				if (StringUtils.hasText(metadataParam.value())) {
					this.name = metadataParam.value();
				}
				this.required = metadataParam.required();
				this.defaultValue = ValueConstants.DEFAULT_NONE
						.equals(metadataParam.defaultValue()) ? null
								: metadataParam.defaultValue();
				this.hasMetadataParamAnnotation = true;
				break;
			}
			else if (RequestHeader.class.isInstance(paramAnn)) {
				RequestHeader requestHeader = (RequestHeader) paramAnn;
				if (StringUtils.hasText(requestHeader.value())) {
					this.name = requestHeader.value();
				}
				this.required = requestHeader.required();
				this.defaultValue = ValueConstants.DEFAULT_NONE
						.equals(requestHeader.defaultValue()) ? null
								: requestHeader.defaultValue();
				this.hasRequestHeaderAnnotation = true;
				break;
			}
			else if (CookieValue.class.isInstance(paramAnn)) {
				CookieValue cookieValue = (CookieValue) paramAnn;
				if (StringUtils.hasText(cookieValue.value())) {
					this.name = cookieValue.value();
				}
				this.required = cookieValue.required();
				this.defaultValue = ValueConstants.DEFAULT_NONE.equals(
						cookieValue.defaultValue()) ? null : cookieValue.defaultValue();
				this.hasCookieValueAnnotation = true;
				break;
			}
			else if (paramAnn.annotationType().getName()
					.equals("org.springframework.security.web.bind.annotation.AuthenticationPrincipal")
					|| paramAnn.annotationType().getName().equals(
							"org.springframework.security.core.annotation.AuthenticationPrincipal")) {
				this.hasAuthenticationPrincipalAnnotation = (Boolean) AnnotationUtils
						.getValue(paramAnn, "errorOnInvalidType");
			}
		}
	}

	public Class<?> getType() {
		return this.typeDescriptor.getType();
	}

	public Class<?> getCollectionType() {
		if (this.typeDescriptor.isCollection()
				&& this.typeDescriptor.getElementTypeDescriptor() != null) {
			return this.typeDescriptor.getElementTypeDescriptor().getType();
		}
		return null;
	}

	public String getName() {
		return this.name;
	}

	public boolean hasRequestParamAnnotation() {
		return this.hasRequestParamAnnotation;
	}

	public boolean hasMetadataParamAnnotation() {
		return this.hasMetadataParamAnnotation;
	}

	public boolean hasRequestHeaderAnnotation() {
		return this.hasRequestHeaderAnnotation;
	}

	public boolean hasCookieValueAnnotation() {
		return this.hasCookieValueAnnotation;
	}

	public boolean hasAuthenticationPrincipalAnnotation() {
		return this.hasAuthenticationPrincipalAnnotation != null;
	}

	public boolean authenticationPrincipalAnnotationErrorOnInvalidType() {
		return this.hasAuthenticationPrincipalAnnotation != null
				? this.hasAuthenticationPrincipalAnnotation : false;
	}

	public boolean isRequired() {
		return this.required;
	}

	public String getDefaultValue() {
		return this.defaultValue;
	}

	public boolean isSupportedParameter() {
		return this.supportedParameter;
	}

	public TypeDescriptor getTypeDescriptor() {
		return this.typeDescriptor;
	}

	public boolean isClientParameter() {
		return !isSupportedParameter() && !hasRequestHeaderAnnotation()
				&& !hasCookieValueAnnotation() && !hasAuthenticationPrincipalAnnotation();
	}

	public boolean isJavaUtilOptional() {
		return this.javaUtilOptional;
	}
}
