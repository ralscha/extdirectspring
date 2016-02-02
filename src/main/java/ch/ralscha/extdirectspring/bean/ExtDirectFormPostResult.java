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
package ch.ralscha.extdirectspring.bean;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.springframework.context.MessageSource;
import org.springframework.util.Assert;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

/**
 * Represents the result of a FORM_POST method call.
 */
public class ExtDirectFormPostResult {

	private static final String ERRORS_PROPERTY = "errors";

	private static final String SUCCESS_PROPERTY = "success";

	private final Map<String, Object> result = new HashMap<String, Object>();

	public ExtDirectFormPostResult() {
		setSuccess(true);
	}

	public ExtDirectFormPostResult(boolean success) {
		setSuccess(success);
	}

	public ExtDirectFormPostResult(BindingResult bindingResult) {
		addErrors(null, null, bindingResult);
	}

	public ExtDirectFormPostResult(BindingResult bindingResult, boolean success) {
		addErrors(null, null, bindingResult);
		setSuccess(success);
	}

	public ExtDirectFormPostResult(Locale locale, MessageSource messageSource,
			BindingResult bindingResult) {
		addErrors(locale, messageSource, bindingResult);
	}

	public ExtDirectFormPostResult(Locale locale, MessageSource messageSource,
			BindingResult bindingResult, boolean success) {
		addErrors(locale, messageSource, bindingResult);
		setSuccess(success);
	}

	/**
	 * Extracts errors from the bindingResult and inserts them into the error properties.
	 * Sets the property success to false if there are errors. Sets the property success
	 * to true if there are no errors.
	 *
	 * @param locale
	 * @param messageSource
	 * @param bindingResult
	 */
	private void addErrors(Locale locale, MessageSource messageSource,
			BindingResult bindingResult) {
		if (bindingResult != null && bindingResult.hasFieldErrors()) {
			Map<String, List<String>> errorMap = new HashMap<String, List<String>>();
			for (FieldError fieldError : bindingResult.getFieldErrors()) {
				String message = fieldError.getDefaultMessage();
				if (messageSource != null) {
					Locale loc = locale != null ? locale : Locale.getDefault();
					message = messageSource.getMessage(fieldError.getCode(),
							fieldError.getArguments(), loc);
				}
				List<String> fieldErrors = errorMap.get(fieldError.getField());

				if (fieldErrors == null) {
					fieldErrors = new ArrayList<String>();
					errorMap.put(fieldError.getField(), fieldErrors);
				}

				fieldErrors.add(message);
			}
			if (errorMap.isEmpty()) {
				addResultProperty(SUCCESS_PROPERTY, Boolean.TRUE);
			}
			else {
				addResultProperty(ERRORS_PROPERTY, errorMap);
				addResultProperty(SUCCESS_PROPERTY, Boolean.FALSE);
			}
		}
		else {
			setSuccess(true);
		}
	}

	/**
	 * resolve the messages codes along the implementation described in
	 * {@link org.springframework.validation.DefaultMessageCodesResolver}<br>
	 * stop at first message found<br>
	 * method is useless if no specific validation message have been set (example:
	 * javax.validation.constraints.NotNull.message.fax=Fax number is mandatory)<br>
	 * it will behave {@link #addErrors(Locale, MessageSource, BindingResult)} with a big
	 * overhead
	 *
	 * @param locale locale for internationalization
	 * @param messageSource source of validation code and message
	 * @param bindingResult Errors list to resolve
	 * @return this {@link #ExtDirectFormPostResult} for easy chaining
	 */
	public ExtDirectFormPostResult addErrorsResolveCode(Locale locale,
			MessageSource messageSource, BindingResult bindingResult) {
		if (bindingResult != null && bindingResult.hasFieldErrors()) {
			Map<String, List<String>> errorMap = new HashMap<String, List<String>>();
			for (FieldError fieldError : bindingResult.getFieldErrors()) {
				String message = fieldError.getDefaultMessage();
				if (messageSource != null) {
					Locale loc = locale != null ? locale : Locale.getDefault();
					for (String code : fieldError.getCodes()) {
						try {
							message = messageSource.getMessage(code,
									fieldError.getArguments(), loc);
						}
						catch (Exception e) {
							/**
							 * expected if code/message doesn't exist, default behavior to
							 * counter that, set to your message bundle,
							 * {@link org.springframework.context.support.AbstractMessageSource#setUseCodeAsDefaultMessage(true)}
							 * beware of side effects
							 */
						}
						if (message != null && !message.equals(code)) {
							break;
						}
					}
				}
				List<String> fieldErrors = errorMap.get(fieldError.getField());

				if (fieldErrors == null) {
					fieldErrors = new ArrayList<String>();
					errorMap.put(fieldError.getField(), fieldErrors);
				}

				fieldErrors.add(message);
			}
			if (errorMap.isEmpty()) {
				addResultProperty(SUCCESS_PROPERTY, Boolean.TRUE);
			}
			else {
				addResultProperty(ERRORS_PROPERTY, errorMap);
				addResultProperty(SUCCESS_PROPERTY, Boolean.FALSE);
			}
		}
		else {
			setSuccess(true);
		}
		return this;
	}

	/**
	 * Adds one error message to a specific field. Does not overwrite already existing
	 * errors.
	 *
	 * @param field the name of the field
	 * @param error the error message
	 */
	public void addError(String field, String error) {
		Assert.notNull(field, "field must not be null");
		Assert.notNull(error, "field must not be null");

		addErrors(field, Collections.singletonList(error));

		addResultProperty(SUCCESS_PROPERTY, Boolean.FALSE);
	}

	/**
	 * Adds multiple error messages to a specific field. Does not overwrite already
	 * existing errors.
	 *
	 * @param field the name of the field
	 * @param errors a collection of error messages
	 */
	@SuppressWarnings("unchecked")
	public void addErrors(String field, List<String> errors) {
		Assert.notNull(field, "field must not be null");
		Assert.notNull(errors, "field must not be null");

		// do not overwrite existing errors
		Map<String, List<String>> errorMap = (Map<String, List<String>>) this.result
				.get(ERRORS_PROPERTY);
		if (errorMap == null) {
			errorMap = new HashMap<String, List<String>>();
			addResultProperty(ERRORS_PROPERTY, errorMap);
		}

		List<String> fieldErrors = errorMap.get(field);
		if (fieldErrors == null) {
			fieldErrors = new ArrayList<String>();
			errorMap.put(field, fieldErrors);
		}
		fieldErrors.addAll(errors);

		addResultProperty(SUCCESS_PROPERTY, Boolean.FALSE);
	}

	public void addResultProperty(String key, Object value) {
		this.result.put(key, value);
	}

	public Map<String, Object> getResult() {
		return this.result;
	}

	public void setSuccess(boolean flag) {
		this.result.put(SUCCESS_PROPERTY, flag);
	}

}
