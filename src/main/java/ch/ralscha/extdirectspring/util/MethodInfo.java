/**
 * Copyright 2010-2013 Ralph Schaer <ralphschaer@gmail.com>
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

import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.method.HandlerMethod;

import ch.ralscha.extdirectspring.annotation.ExtDirectDocParameters;
import ch.ralscha.extdirectspring.annotation.ExtDirectDocReturn;
import ch.ralscha.extdirectspring.annotation.ExtDirectMethod;
import ch.ralscha.extdirectspring.annotation.ExtDirectMethodDocumentation;
import ch.ralscha.extdirectspring.annotation.ExtDirectMethodType;
import ch.ralscha.extdirectspring.bean.api.Action;
import ch.ralscha.extdirectspring.bean.api.ActionDoc;
import ch.ralscha.extdirectspring.bean.api.PollingProvider;

/**
 * Object holds information about a method like the method itself and a list of
 * parameters.
 */
public final class MethodInfo {

	private String group;

	private final ExtDirectMethodType type;

	private final Class<?> jsonView;

	private final boolean synchronizeOnSession;

	private final boolean streamResponse;

	private List<ParameterInfo> parameters;

	private Method method;

	private String forwardPath;

	private HandlerMethod handlerMethod;

	private Class<?> collectionType;

	private Action action;

	private PollingProvider pollingProvider;

	private String sseMethod;

	public MethodInfo(Class<?> clazz, ApplicationContext context, String beanName, Method method) {

		ExtDirectMethod extDirectMethodAnnotation = AnnotationUtils.findAnnotation(method, ExtDirectMethod.class);

		this.type = extDirectMethodAnnotation.value();

		if (extDirectMethodAnnotation.jsonView() != ExtDirectMethod.NoJsonView.class) {
			this.jsonView = extDirectMethodAnnotation.jsonView();
		} else {
			this.jsonView = null;
		}

		if (StringUtils.hasText(extDirectMethodAnnotation.group())) {
			this.group = extDirectMethodAnnotation.group().trim();
		} else {
			this.group = null;
		}

		this.synchronizeOnSession = extDirectMethodAnnotation.synchronizeOnSession();
		this.streamResponse = extDirectMethodAnnotation.streamResponse();

		if (type != ExtDirectMethodType.FORM_POST) {
			this.method = method;
			this.parameters = buildParameterList(clazz, method);

			this.collectionType = (extDirectMethodAnnotation.entryClass() == Object.class) ? null
					: extDirectMethodAnnotation.entryClass();

			if (this.collectionType == null) {
				for (ParameterInfo parameter : parameters) {
					Class<?> collType = parameter.getCollectionType();
					if (collType != null) {
						this.collectionType = collType;
						break;
					}
				}
			}
		} else {
			if (method.getReturnType().equals(Void.TYPE)) {

				RequestMapping methodAnnotation = AnnotationUtils.findAnnotation(method, RequestMapping.class);
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
			} else {
				this.handlerMethod = new HandlerMethod(beanName, context, method).createWithResolvedBean();
			}
		}

		switch (type) {
		case SIMPLE:
			int paramLength = 0;
			for (ParameterInfo parameter : this.parameters) {
				if (!parameter.isSupportedParameter() && !parameter.isHasRequestHeaderAnnotation()) {
					paramLength++;
				}
			}
			this.action = new Action(method.getName(), paramLength, null);
			break;
		case SIMPLE_NAMED:
			List<String> parameterNames = new ArrayList<String>();
			for (ParameterInfo parameter : this.parameters) {
				if (!parameter.isSupportedParameter() && !parameter.isHasRequestHeaderAnnotation()) {
					parameterNames.add(parameter.getName());
				}
			}
			this.action = new Action(method.getName(), parameterNames);
			break;
		case FORM_LOAD:
		case STORE_READ:
		case STORE_MODIFY:
		case TREE_LOAD:
			this.action = new Action(method.getName(), 1, null);
			break;
		case FORM_POST:
			this.action = new Action(method.getName(), 0, true);
			break;
		case POLL:
			this.pollingProvider = new PollingProvider(beanName, method.getName(), extDirectMethodAnnotation.event());
			break;
		case SSE:
			this.sseMethod = method.getName();
			break;
		default:
			throw new IllegalStateException("ExtDirectMethodType: " + type + " does not exists");
		}

		this.action = extractDocumentationAnnotations(extDirectMethodAnnotation.documentation());

	}

	/**
	 * The rule is: whatever has been given is taken as is the API documentation
	 * is non critical, so any discrepancies will be silently ignored
	 * 
	 * @param documentation
	 * @return ActionDoc
	 */
	private Action extractDocumentationAnnotations(ExtDirectMethodDocumentation documentation) {
		if (!documentation.value().isEmpty()) {
			ActionDoc actionDoc = new ActionDoc(getAction(), documentation.value(), documentation.author(),
					documentation.version(), documentation.deprecated());
			ExtDirectDocParameters docParameters = documentation.parameters();
			if (null != docParameters) {
				String[] params = docParameters.params();
				String[] descriptions = docParameters.descriptions() == null ? new String[params.length]
						: docParameters.descriptions();
				if (params.length == descriptions.length) {
					for (int i = 0; i < params.length; i++) {
						actionDoc.getParameters().put(params[i],
								descriptions[i] == null ? "No description" : descriptions[i]);
					}
				} else {
					LogFactory
							.getLog(MethodInfo.class)
							.info("Documentation: skip generation of parameters, params size is different from descriptions size");
				}
			}
			ExtDirectDocReturn docReturn = documentation.returnMethod();
			if (null != docReturn) {
				String[] properties = docReturn.properties();
				String[] descriptions = docReturn.descriptions() == null ? new String[properties.length] : docReturn
						.descriptions();
				if (properties.length == descriptions.length) {
					for (int i = 0; i < properties.length; i++) {
						actionDoc.getReturnMethod().put(properties[i],
								descriptions[i] == null ? "No description" : descriptions[i]);
					}
				} else {
					LogFactory
							.getLog(MethodInfo.class)
							.info("Documentation: skip generation of return method properties, properties size is different from descriptions size");
				}
			}
			return actionDoc;
		}
		return this.action;
	}

	private static boolean hasValue(RequestMapping requestMapping) {
		return (requestMapping != null && requestMapping.value() != null && requestMapping.value().length > 0 && StringUtils
				.hasText(requestMapping.value()[0]));
	}

	private static List<ParameterInfo> buildParameterList(Class<?> clazz, Method method) {
		List<ParameterInfo> params = new ArrayList<ParameterInfo>();

		Class<?>[] parameterTypes = method.getParameterTypes();

		Method methodWithAnnotation = findMethodWithAnnotation(method, ExtDirectMethod.class);
		if (methodWithAnnotation == null) {
			methodWithAnnotation = method;
		}

		for (int paramIndex = 0; paramIndex < parameterTypes.length; paramIndex++) {
			params.add(new ParameterInfo(clazz, methodWithAnnotation, paramIndex));
		}

		return params;
	}

	public Method getMethod() {
		return method;
	}

	public String getForwardPath() {
		return forwardPath;
	}

	public HandlerMethod getHandlerMethod() {
		return handlerMethod;
	}

	public List<ParameterInfo> getParameters() {
		return parameters;
	}

	public Class<?> getCollectionType() {
		return collectionType;
	}

	public boolean isType(ExtDirectMethodType methodType) {
		return this.type == methodType;
	}

	public Class<?> getJsonView() {
		return jsonView;
	}

	public boolean isSynchronizeOnSession() {
		return synchronizeOnSession;
	}

	public boolean isStreamResponse() {
		return streamResponse;
	}

	public PollingProvider getPollingProvider() {
		return pollingProvider;
	}

	public String getSseMethod() {
		return sseMethod;
	}

	public Action getAction() {
		return action;
	}

	public String getGroup() {
		return group;
	}

	/**
	 * Find a method that is annotated with a specific annotation. Starts with
	 * the method and goes up to the superclasses of the class.
	 * 
	 * @param method the starting method
	 * @param annotation the annotation to look for
	 * @return the method if there is a annotated method, else null
	 */
	public static Method findMethodWithAnnotation(Method method, Class<? extends Annotation> annotation) {
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
