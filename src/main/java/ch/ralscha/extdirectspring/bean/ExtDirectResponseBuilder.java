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
package ch.ralscha.extdirectspring.bean;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.MessageSource;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.support.RequestContextUtils;

import ch.ralscha.extdirectspring.controller.Configuration;
import ch.ralscha.extdirectspring.controller.RouterController;
import ch.ralscha.extdirectspring.util.ExtDirectSpringUtil;

/**
 * An utility class that helps building the response for a FORM_POST method. The
 * response is written directly into the
 * {@link HttpServletResponse#getOutputStream()} with {@link #buildAndWrite()}.
 */
public class ExtDirectResponseBuilder {

	private final ExtDirectResponse extDirectResponse;

	private final HttpServletRequest request;

	private final HttpServletResponse response;

	private final Map<String, Object> result;

	private Class<?> jsonView;

	/**
	 * Creates a builder that builds and writes the response of a FORM_POST
	 * method. Sets the successful flag to true, can be changed with the
	 * {@link #successful()} and {@link #unsuccessful()} methods.
	 * 
	 * @param request the current http servlet request object
	 * @param response the current http servlet response object
	 */
	public ExtDirectResponseBuilder(HttpServletRequest request, HttpServletResponse response) {
		this.request = request;
		this.response = response;

		extDirectResponse = new ExtDirectResponse(request);
		result = new HashMap<String, Object>();
		successful();
		extDirectResponse.setResult(result);
	}

	/**
	 * Creates a builder instance.
	 * 
	 * @see #ExtDirectResponseBuilder(HttpServletRequest, HttpServletResponse)
	 * 
	 * @param request the current http servlet request object
	 * @param response the current http servlet response object
	 * 
	 * @return the created builder instance
	 */
	public static ExtDirectResponseBuilder create(HttpServletRequest request, HttpServletResponse response) {
		return new ExtDirectResponseBuilder(request, response);
	}

	/**
	 * Creates an "exception" response. Calls
	 * {@link ExtDirectResponse#setType(String)} with a value of "exception".
	 * Calls {@link ExtDirectResponse#setMessage(String)} and
	 * {@link ExtDirectResponse#setWhere(String)} according to the
	 * {@link Configuration}.
	 * 
	 * This is a method primarly used for implementations of
	 * {@link HandlerExceptionResolver}.
	 * 
	 * @param exception the exception that was thrown.
	 * @return this instance
	 */
	public ExtDirectResponseBuilder setException(Exception exception) {
		unsuccessful();

		WebApplicationContext ctx = RequestContextUtils.getWebApplicationContext(request);
		Configuration configuration = null;
		try {
			configuration = ctx.getBean(Configuration.class);
		} catch (NoSuchBeanDefinitionException e) {
			configuration = new Configuration();
		}

		extDirectResponse.setType("exception");
		extDirectResponse.setMessage(configuration.getMessage(exception));

		if (configuration.isSendStacktrace()) {
			extDirectResponse.setWhere(ExtDirectSpringUtil.getStackTrace(exception));
		} else {
			extDirectResponse.setWhere(null);
		}

		return this;
	}

	/**
	 * Adds an "errors" property in the response if there are any errors in the
	 * bindingResult. Sets the success flag to false if there are errors.
	 * 
	 * @param bindingResult
	 * @return this instance
	 */
	public ExtDirectResponseBuilder addErrors(BindingResult bindingResult) {
		addErrors(null, null, bindingResult);
		return this;
	}

	/**
	 * Adds an "errors" property in the response if there are any errors in the
	 * bindingResult. Sets the success flag to false if there are errors.
	 * 
	 * @param locale
	 * @param messageSource
	 * @param bindingResult
	 * @return this instance
	 */
	public ExtDirectResponseBuilder addErrors(Locale locale, MessageSource messageSource,
			final BindingResult bindingResult) {
		if (bindingResult != null && bindingResult.hasFieldErrors()) {
			Map<String, List<String>> errorMap = new HashMap<String, List<String>>();
			for (FieldError fieldError : bindingResult.getFieldErrors()) {
				String message = fieldError.getDefaultMessage();
				if (messageSource != null) {
					Locale loc = (locale != null ? locale : Locale.getDefault());
					message = messageSource.getMessage(fieldError.getCode(), fieldError.getArguments(), loc);
				}
				List<String> fieldErrors = errorMap.get(fieldError.getField());

				if (fieldErrors == null) {
					fieldErrors = new ArrayList<String>();
					errorMap.put(fieldError.getField(), fieldErrors);
				}

				fieldErrors.add(message);
			}
			if (errorMap.isEmpty()) {
				addResultProperty("success", true);
			} else {
				addResultProperty("errors", errorMap);
				addResultProperty("success", false);
			}
		}
		return this;
	}

	/**
	 * Add additional property to the response.
	 * 
	 * @param key the key of the property
	 * @param value the value of this property
	 * @return this instance
	 */
	public ExtDirectResponseBuilder addResultProperty(String key, Object value) {
		result.put(key, value);
		return this;
	}

	/**
	 * Sets success flag to true.
	 * 
	 * @return this instance
	 */
	public ExtDirectResponseBuilder successful() {
		result.put("success", true);
		return this;
	}

	/**
	 * Sets success flag to false.
	 * 
	 * @return this instance
	 */
	public ExtDirectResponseBuilder unsuccessful() {
		result.put("success", false);
		return this;
	}

	/**
	 * Sets success flag to the provided parameter.
	 * 
	 * @param flag the new success value
	 * @return this instance
	 */
	public ExtDirectResponseBuilder setSuccess(boolean flag) {
		result.put("success", flag);
		return this;
	}

	/**
	 * Sets a specific JSON View (filter) that Jackson uses to serialize the
	 * response.
	 * 
	 * @param jsonView
	 */
	public void setJsonView(Class<?> jsonView) {
		this.jsonView = jsonView;
	}

	/**
	 * Builds and writes the response into the OutputStream of
	 * {@link HttpServletResponse}. This methods has to be called at the end of
	 * a FORM_POST method.
	 */
	public void buildAndWrite() {

		try {
			RouterController routerController = RequestContextUtils.getWebApplicationContext(request).getBean(
					RouterController.class);

			routerController.writeJsonResponse(request, response, extDirectResponse, jsonView);

		} catch (IOException e) {
			LogFactory.getLog(getClass()).error("buildAndWrite", e);
			throw new RuntimeException(e);
		}

	}

}
