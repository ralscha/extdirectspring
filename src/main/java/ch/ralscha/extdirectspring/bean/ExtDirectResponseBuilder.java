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
package ch.ralscha.extdirectspring.bean;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.context.MessageSource;
import org.springframework.util.FileCopyUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.servlet.support.RequestContextUtils;

import ch.ralscha.extdirectspring.controller.RouterController;

/**
 * An utility class that helps building a {@link ExtDirectResponse}. A form
 * handler must return an instance of such type.
 * 
 * @author Ralph Schaer
 */
public class ExtDirectResponseBuilder {

	private final ExtDirectResponse extDirectResponse;
	private final HttpServletRequest request;
	private final HttpServletResponse response;
	private final Map<String, Object> result;

	/**
	 * Creates a builder that builds the response object needed for form handler
	 * and form upload handler methods. Sets the successful flag to true, can be
	 * changed with the successful() and unsuccessful() methods
	 * 
	 * @param request
	 *          the current request
	 */
	public ExtDirectResponseBuilder(final HttpServletRequest request, final HttpServletResponse response) {
		this.request = request;
		this.response = response;

		extDirectResponse = new ExtDirectResponse(request);
		result = new HashMap<String, Object>();
		successful();
		extDirectResponse.setResult(result);
	}

	public static ExtDirectResponseBuilder create(final HttpServletRequest request, final HttpServletResponse response) {
		return new ExtDirectResponseBuilder(request, response);
	}

	/**
	 * Creates a errors property in the response if there are any errors in the
	 * bindingResult Sets the success flag to false if there are errors
	 * 
	 * @param bindingResult
	 */
	public ExtDirectResponseBuilder addErrors(final BindingResult bindingResult) {
		addErrors(null, null, bindingResult);
		return this;
	}

	/**
	 * Creates a errors property in the response if there are any errors in the
	 * bindingResult Sets the success flag to false if there are errors
	 * 
	 * @param locale
	 * @param messageSource
	 * @param bindingResult
	 */
	public ExtDirectResponseBuilder addErrors(final Locale locale, final MessageSource messageSource,
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
	 * Add additional property to the response
	 * 
	 * @param key
	 *          the key of the property
	 * @param value
	 *          the value of this property
	 */
	public ExtDirectResponseBuilder addResultProperty(final String key, final Object value) {
		result.put(key, value);
		return this;
	}

	/**
	 * Sets success flag to true
	 */
	public ExtDirectResponseBuilder successful() {
		result.put("success", true);
		return this;
	}

	/**
	 * Sets success flag to false
	 */
	public ExtDirectResponseBuilder unsuccessful() {
		result.put("success", false);
		return this;
	}

	/**
	 * Builds and writes the response to the OutputStream of the response. This
	 * methods has to be called at the end of a form upload handler method.	 * 
	 */
	public void buildAndWrite() {

		try {

			RouterController routerController = RequestContextUtils.getWebApplicationContext(request).getBean(
					RouterController.class);

			if (isMultipart()) {
				response.setContentType(RouterController.TEXT_HTML.toString());
				response.setCharacterEncoding(RouterController.TEXT_HTML.getCharSet().name());

				ByteArrayOutputStream bos = new ByteArrayOutputStream(1024);
				bos.write("<html><body><textarea>".getBytes());

				String responseJson = routerController.getJsonHandler().getMapper()
						.writeValueAsString(extDirectResponse);

				responseJson = responseJson.replace("&quot;", "\\&quot;");
				bos.write(responseJson.getBytes());
				bos.write("</textarea></body></html>".getBytes());

				response.setContentLength(bos.size());
				FileCopyUtils.copy(bos.toByteArray(), response.getOutputStream());
			} else {
				routerController.writeJsonResponse(response, extDirectResponse, routerController.getConfiguration()
						.isStreamResponse());
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new RuntimeException(e);
		}

	}

	private boolean isMultipart() {
		if (!"post".equals(request.getMethod().toLowerCase())) {
			return false;
		}
		String contentType = request.getContentType();
		return (contentType != null && contentType.toLowerCase().startsWith("multipart/"));
	}
}
