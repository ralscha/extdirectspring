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
package ch.ralscha.extdirectspring.annotation;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import ch.ralscha.extdirectspring.bean.EdFormPostResult;
import ch.ralscha.extdirectspring.bean.ExtDirectFormPostResult;

/**
 * Enumeration of all possible remote method types.
 */
public enum ExtDirectMethodType {

	/**
	 * Specifies a simple remote method. This type of method can have any parameter and
	 * any return type but must not contain a parameter with @RequestParam annotated.
	 */
	SIMPLE {
		@Override
		public boolean isValid(String beanAndMethodName, Class<?> clazz, Method method) {

			ExtDirectMethod extDirectMethodAnnotation = AnnotationUtils
					.findAnnotation(method, ExtDirectMethod.class);
			if (StringUtils.hasText(extDirectMethodAnnotation.event())) {
				log.warn("SIMPLE method '" + beanAndMethodName
						+ "' does not support event attribute of @ExtDirectMethod");
			}

			if (extDirectMethodAnnotation.entryClass() != Object.class) {
				log.warn("SIMPLE method '" + beanAndMethodName
						+ "' does not support entryClass attribute of @ExtDirectMethod");
			}

			Annotation[][] allParameterAnnotations = method.getParameterAnnotations();

			for (Annotation[] paramAnnotations : allParameterAnnotations) {
				for (Annotation paramAnnotation : paramAnnotations) {
					if (RequestParam.class.isInstance(paramAnnotation)) {
						log.error("SIMPLE method '" + beanAndMethodName
								+ "' contains a non supported parameter annotation @RequestParam");
						return false;
					}
				}
			}

			return true;
		}
	},

	/**
	 * Specifies a simple remote method with named parameters. This type of method can
	 * have any parameter and any return type.
	 */
	SIMPLE_NAMED {
		@Override
		public boolean isValid(String beanAndMethodName, Class<?> clazz, Method method) {

			ExtDirectMethod extDirectMethodAnnotation = AnnotationUtils
					.findAnnotation(method, ExtDirectMethod.class);
			if (StringUtils.hasText(extDirectMethodAnnotation.event())) {
				log.warn("SIMPLE_NAMED method '" + beanAndMethodName
						+ "' does not support event attribute of @ExtDirectMethod");
			}

			if (extDirectMethodAnnotation.entryClass() != Object.class) {
				log.warn("SIMPLE_NAMED method '" + beanAndMethodName
						+ "' does not support entryClass attribute of @ExtDirectMethod");
			}

			return true;
		}
	},

	/**
	 * Specifies a method that handles a form load.
	 */
	FORM_LOAD {
		@Override
		public boolean isValid(String beanAndMethodName, Class<?> clazz, Method method) {

			ExtDirectMethod extDirectMethodAnnotation = AnnotationUtils
					.findAnnotation(method, ExtDirectMethod.class);
			if (StringUtils.hasText(extDirectMethodAnnotation.event())) {
				log.warn("FORM_LOAD method '" + beanAndMethodName
						+ "' does not support event attribute of @ExtDirectMethod");
			}

			if (extDirectMethodAnnotation.entryClass() != Object.class) {
				log.warn("FORM_LOAD method '" + beanAndMethodName
						+ "' does not support entryClass attribute of @ExtDirectMethod");
			}

			return true;
		}
	},

	/**
	 * Specifies a method that handles read calls from DirectStore.
	 */
	STORE_READ {
		@Override
		public boolean isValid(String beanAndMethodName, Class<?> clazz, Method method) {

			ExtDirectMethod extDirectMethodAnnotation = AnnotationUtils
					.findAnnotation(method, ExtDirectMethod.class);
			if (StringUtils.hasText(extDirectMethodAnnotation.event())) {
				log.warn("STORE_READ method '" + beanAndMethodName
						+ "' does not support event attribute of @ExtDirectMethod");
			}

			if (extDirectMethodAnnotation.entryClass() != Object.class) {
				log.warn("STORE_READ method '" + beanAndMethodName
						+ "' does not support entryClass attribute of @ExtDirectMethod");
			}

			return true;
		}
	},

	/**
	 * Specifies a method that handles create, update or destroy calls from DirectStore.
	 */
	STORE_MODIFY {
		@Override
		public boolean isValid(String beanAndMethodName, Class<?> clazz, Method method) {

			ExtDirectMethod extDirectMethodAnnotation = AnnotationUtils
					.findAnnotation(method, ExtDirectMethod.class);
			if (StringUtils.hasText(extDirectMethodAnnotation.event())) {
				log.warn("STORE_MODIFY method '" + beanAndMethodName
						+ "' does not support event attribute of @ExtDirectMethod");
			}

			return true;
		}
	},

	/**
	 * Specifies a method that handles a form post (with or without upload). A FORM_POST
	 * method must not return anything. This type of method must be annotated
	 * with @RequestMapping. @RequestMapping must contain a value and a method of type
	 * RequestMethod.POST. This kind of method must be member of a bean annotated
	 * with @Controller.
	 */
	FORM_POST {
		@Override
		public boolean isValid(String beanAndMethodName, Class<?> clazz, Method method) {

			boolean isValid = true;

			if (method.getReturnType().equals(ExtDirectFormPostResult.class)
					|| method.getReturnType().equals(EdFormPostResult.class)) {
				ExtDirectMethod extDirectMethodAnnotation = AnnotationUtils
						.findAnnotation(method, ExtDirectMethod.class);
				if (StringUtils.hasText(extDirectMethodAnnotation.event())) {
					log.warn("FORM_POST method '" + beanAndMethodName
							+ "' does not support event attribute of @ExtDirectMethod");
				}

				if (extDirectMethodAnnotation.entryClass() != Object.class) {
					log.warn("FORM_POST method '" + beanAndMethodName
							+ "' does not support entryClass attribute of @ExtDirectMethod");
				}

				if (extDirectMethodAnnotation.batched() == false) {
					log.warn("FORM_POST method '" + beanAndMethodName
							+ "' does not support batched attribute of @ExtDirectMethod");
				}

				isValid = true;
			}
			else if (method.getReturnType().equals(Void.TYPE)) {

				if (AnnotationUtils.findAnnotation(method, ResponseBody.class) != null) {
					log.warn("FORM_POST method '" + beanAndMethodName
							+ "' should not have a @ResponseBody annotation");
				}

				if (AnnotationUtils.findAnnotation(clazz, Controller.class) == null) {
					log.error("FORM_POST method '" + beanAndMethodName
							+ "' must be a member of a @Controller bean");
					isValid = false;
				}

				final RequestMapping methodAnnotation = AnnotationUtils
						.findAnnotation(method, RequestMapping.class);

				if (methodAnnotation == null) {
					log.error("FORM_POST method '" + beanAndMethodName
							+ "' must be annotated with @RequestMapping");
					isValid = false;
				}

				RequestMapping classAnnotation = AnnotationUtils.findAnnotation(clazz,
						RequestMapping.class);

				boolean hasValue = false;

				if (classAnnotation != null) {
					hasValue = classAnnotation.value() != null
							&& classAnnotation.value().length > 0;
				}

				if (methodAnnotation != null && !hasValue) {
					hasValue = methodAnnotation.value() != null
							&& methodAnnotation.value().length > 0;
				}

				if (!hasValue) {
					log.error("FORM_POST method '" + beanAndMethodName
							+ "' must have a @RequestMapping annotation with a value");
					isValid = false;
				}

				if (methodAnnotation != null) {
					boolean hasPostRequestMethod = false;
					for (RequestMethod requestMethod : methodAnnotation.method()) {
						if (requestMethod.equals(RequestMethod.POST)) {
							hasPostRequestMethod = true;
							break;
						}
					}

					if (!hasPostRequestMethod) {
						log.error("FORM_POST method '" + beanAndMethodName
								+ "' must have a @RequestMapping annotation with method = RequestMethod.POST");
						isValid = false;
					}
				}

				ExtDirectMethod extDirectMethodAnnotation = AnnotationUtils
						.findAnnotation(method, ExtDirectMethod.class);

				if (extDirectMethodAnnotation.batched() == false) {
					log.warn("FORM_POST method '" + beanAndMethodName
							+ "' does not support batched attribute of @ExtDirectMethod");
				}

				if (StringUtils.hasText(extDirectMethodAnnotation.event())) {
					log.warn("FORM_POST method '" + beanAndMethodName
							+ "' does not support event attribute of @ExtDirectMethod");
				}

				if (extDirectMethodAnnotation.entryClass() != Object.class) {
					log.warn("FORM_POST method '" + beanAndMethodName
							+ "' does not support entryClass attribute of @ExtDirectMethod");
				}

				if (extDirectMethodAnnotation.synchronizeOnSession()) {
					log.warn("FORM_POST method '" + beanAndMethodName
							+ "' does not support synchronizeOnSession attribute of @ExtDirectMethod");
				}

				if (extDirectMethodAnnotation.streamResponse()) {
					log.warn("FORM_POST method '" + beanAndMethodName
							+ "' does not support streamResponse attribute of @ExtDirectMethod");
				}

			}
			else {
				log.error("FORM_POST method '" + beanAndMethodName
						+ "' must return void or an instance of ExtDirectFormPostResult or EdFormPostResult");
				isValid = false;
			}

			return isValid;
		}
	},

	/**
	 * Specifies a method that handles read calls from TreeLoader or TreeStore
	 */
	TREE_LOAD {
		@Override
		public boolean isValid(String beanAndMethodName, Class<?> clazz, Method method) {

			ExtDirectMethod extDirectMethodAnnotation = AnnotationUtils
					.findAnnotation(method, ExtDirectMethod.class);
			if (StringUtils.hasText(extDirectMethodAnnotation.event())) {
				log.warn("TREE_LOAD method '" + beanAndMethodName
						+ "' does not support event attribute of @ExtDirectMethod");
			}

			if (extDirectMethodAnnotation.entryClass() != Object.class) {
				log.warn("TREE_LOAD method '" + beanAndMethodName
						+ "' does not support entryClass attribute of @ExtDirectMethod");
			}

			return true;
		}
	},

	/**
	 * Specifies a method that handles polling.
	 */
	POLL {
		@Override
		public boolean isValid(String beanAndMethodName, Class<?> clazz, Method method) {

			ExtDirectMethod extDirectMethodAnnotation = AnnotationUtils
					.findAnnotation(method, ExtDirectMethod.class);

			if (extDirectMethodAnnotation.entryClass() != Object.class) {
				log.warn("POLL method '" + beanAndMethodName
						+ "' does not support entryClass attribute of @ExtDirectMethod");
			}

			if (extDirectMethodAnnotation.batched() == false) {
				log.warn("POLL method '" + beanAndMethodName
						+ "' does not support batched attribute of @ExtDirectMethod");
			}

			return true;
		}
	},
	/**
	 * Specifies a method that handles a form post with a Json payload
	 */
	FORM_POST_JSON {

		@Override
		public boolean isValid(String beanAndMethodName, Class<?> clazz, Method method) {

			ExtDirectMethod extDirectMethodAnnotation = AnnotationUtils
					.findAnnotation(method, ExtDirectMethod.class);
			if (StringUtils.hasText(extDirectMethodAnnotation.event())) {
				log.warn("FORM_POST_JSON method '" + beanAndMethodName
						+ "' does not support event attribute of @ExtDirectMethod");
			}

			if (extDirectMethodAnnotation.batched() == false) {
				log.warn("FORM_POST_JSON method '" + beanAndMethodName
						+ "' does not support batched attribute of @ExtDirectMethod");
			}

			for (Class<?> clazzz : method.getParameterTypes()) {
				if (clazzz.isAssignableFrom(BindingResult.class)) {
					log.error("FORM_POST_JSON method '" + beanAndMethodName
							+ "' must not have a BindingResult parameter");
					return false;
				}
				else if (clazzz.isAssignableFrom(MultipartFile.class)) {
					log.error("FORM_POST_JSON method '" + beanAndMethodName
							+ "' must not have a MultipartFile parameter");
					return false;
				}
			}
			return true;
		}

	};

	static final Log log = LogFactory.getLog(ExtDirectMethodType.class);

	/**
	 * Checks if the annotated method contains non supported annotation properties and
	 * contains non supported parameters and/or parameter annotations. Method logs
	 * warnings and errors. Check is running during startup of the application. If return
	 * value is false the method is not registered and cannot be called from the client.
	 *
	 * @param beanAndMethodName Name of the bean and method for logging purpose. e.g.
	 * 'bean.methodname'
	 * @param clazz The class where the method is member of
	 * @param method The annotated method
	 *
	 * @return true if the method does not contains any errors.
	 */
	public abstract boolean isValid(String beanAndMethodName, Class<?> clazz,
			Method method);

}
