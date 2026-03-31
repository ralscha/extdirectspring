/*
 * Copyright the original author or authors.
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

		this.name = methodParam.getParameterName();
		this.typeDescriptor = new TypeDescriptor(methodParam);

		Class<?> paramType = methodParam.withContainingClass(clazz).getParameterType();
		this.javaUtilOptional = "java.util.Optional".equals(paramType.getName());

		this.supportedParameter = SupportedParameters.isSupported(this.typeDescriptor.getObjectType());

		Annotation[] paramAnnotations = methodParam.getParameterAnnotations();

		for (Annotation annotation : paramAnnotations) {
			Annotation paramAnn = AnnotationUtils.synthesizeAnnotation(annotation, clazz);

			this.hasRequestParamAnnotation = false;
			this.hasMetadataParamAnnotation = false;
			this.hasRequestHeaderAnnotation = false;
			this.hasCookieValueAnnotation = false;
			this.hasAuthenticationPrincipalAnnotation = null;

			if ((paramAnn instanceof RequestParam requestParam)) {
				if (StringUtils.hasText(requestParam.value())) {
					this.name = requestParam.value();
				}
				this.required = requestParam.required();
				this.defaultValue = ValueConstants.DEFAULT_NONE.equals(requestParam.defaultValue()) ? null
						: requestParam.defaultValue();
				this.hasRequestParamAnnotation = true;
				break;
			}
			if ((paramAnn instanceof MetadataParam metadataParam)) {
				if (StringUtils.hasText(metadataParam.value())) {
					this.name = metadataParam.value();
				}
				this.required = metadataParam.required();
				this.defaultValue = ValueConstants.DEFAULT_NONE.equals(metadataParam.defaultValue()) ? null
						: metadataParam.defaultValue();
				this.hasMetadataParamAnnotation = true;
				break;
			}
			else if ((paramAnn instanceof RequestHeader requestHeader)) {
				if (StringUtils.hasText(requestHeader.value())) {
					this.name = requestHeader.value();
				}
				this.required = requestHeader.required();
				this.defaultValue = ValueConstants.DEFAULT_NONE.equals(requestHeader.defaultValue()) ? null
						: requestHeader.defaultValue();
				this.hasRequestHeaderAnnotation = true;
				break;
			}
			else if ((paramAnn instanceof CookieValue cookieValue)) {
				if (StringUtils.hasText(cookieValue.value())) {
					this.name = cookieValue.value();
				}
				this.required = cookieValue.required();
				this.defaultValue = ValueConstants.DEFAULT_NONE.equals(cookieValue.defaultValue()) ? null
						: cookieValue.defaultValue();
				this.hasCookieValueAnnotation = true;
				break;
			}
			else if ("org.springframework.security.web.bind.annotation.AuthenticationPrincipal"
				.equals(paramAnn.annotationType().getName())
					|| "org.springframework.security.core.annotation.AuthenticationPrincipal"
						.equals(paramAnn.annotationType().getName())) {
				Object errorOnInvalidType = AnnotationUtils.getValue(paramAnn, "errorOnInvalidType");
				this.hasAuthenticationPrincipalAnnotation = errorOnInvalidType instanceof Boolean booleanValue
						? booleanValue : Boolean.FALSE;
				break;
			}
		}
	}

	public Class<?> getType() {
		return this.typeDescriptor.getType();
	}

	public Class<?> getCollectionType() {
		if (this.typeDescriptor.isCollection() && this.typeDescriptor.getElementTypeDescriptor() != null) {
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
		return Boolean.TRUE.equals(this.hasAuthenticationPrincipalAnnotation);
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
		return !isSupportedParameter() && !hasRequestHeaderAnnotation() && !hasCookieValueAnnotation()
				&& !hasAuthenticationPrincipalAnnotation();
	}

	public boolean isJavaUtilOptional() {
		return this.javaUtilOptional;
	}

}
