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
import java.util.ArrayList;
import java.util.List;

import org.springframework.core.LocalVariableTableParameterNameDiscoverer;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;

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
	private boolean synchronizeOnSession;

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
			this.synchronizeOnSession = extDirectMethodAnnotation.synchronizeOnSession();
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

		Method methodWithAnnotation = findMethodWithAnnotation(method, ExtDirectMethod.class);
		if (methodWithAnnotation != null) {
			parameterAnnotations = methodWithAnnotation.getParameterAnnotations();
			parameterNames = discoverer.getParameterNames(methodWithAnnotation);
		} else {
			parameterAnnotations = method.getParameterAnnotations();
			parameterNames = discoverer.getParameterNames(method);		
		}

		for (int paramIndex = 0; paramIndex < parameterTypes.length; paramIndex++) {
			String paramName = null;
			if (parameterNames != null) {
				paramName = parameterNames[paramIndex];
			}
			Annotation[] paramAnnotations = null;
			if (parameterAnnotations != null) {
				paramAnnotations = parameterAnnotations[paramIndex];
			}
			
			params.add(new ParameterInfo(clazz, method, paramIndex, parameterTypes[paramIndex], paramName, paramAnnotations));
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
	
	public boolean isSynchronizeOnSession() {
		return synchronizeOnSession;
	}


	/**
	 * Find a method that is annotated with a specific annotation. Starts with the
	 * method and goes up to the superclasses of the class.
	 * 
	 * @param method
	 *          the starting method
	 * @param annotation
	 *          the annotation to look for
	 * @return the method if there is a annotated method, else null
	 */
	public static Method findMethodWithAnnotation(final Method method, final Class<? extends Annotation> annotation) {
		if (method.isAnnotationPresent(annotation)) {
			return method;
		}

		Class<?> cl = method.getDeclaringClass();
		while (cl != null && cl != Object.class) {
			try {
				Method equivalentMethod = cl.getDeclaredMethod(method.getName(), method.getParameterTypes());
				if (equivalentMethod.isAnnotationPresent(annotation)) {
					return equivalentMethod;
				}
			} catch (NoSuchMethodException e) {
				// do nothing here
			}
			cl = cl.getSuperclass();
		}

		return null;
	}


}