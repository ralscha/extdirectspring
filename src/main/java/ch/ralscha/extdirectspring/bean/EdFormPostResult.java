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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.immutables.value.Value;
import org.immutables.value.Value.Style.ImplementationVisibility;
import org.springframework.context.MessageSource;
import org.springframework.util.Assert;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

/**
 * Represents the result of a FORM_POST method call.
 */
@JsonSerialize(as = ImmutableEdFormPostResult.class)
@Value.Style(visibility = ImplementationVisibility.PACKAGE)
@Value.Immutable
public abstract class EdFormPostResult {

	private static final String ERRORS_PROPERTY = "errors";

	private static final String SUCCESS_PROPERTY = "success";

	@Value.Parameter
	public abstract Map<String, Object> result();

	public static EdFormPostResult success() {
		return ImmutableEdFormPostResult.builder()
				.putResult(SUCCESS_PROPERTY, Boolean.TRUE).build();
	}

	public static EdFormPostResult success(Map<String, Object> result) {
		return ImmutableEdFormPostResult.builder().result(result)
				.putResult(SUCCESS_PROPERTY, Boolean.TRUE).build();
	}

	public static EdFormPostResult create(BindingResult bindingResult) {
		return ImmutableEdFormPostResult.builder().addErrors(null, null, bindingResult)
				.build();
	}

	public static EdFormPostResult create(BindingResult bindingResult, boolean success) {
		return ImmutableEdFormPostResult.builder().addErrors(null, null, bindingResult)
				.putResult(SUCCESS_PROPERTY, success).build();
	}

	public static EdFormPostResult create(Locale locale, MessageSource messageSource,
			BindingResult bindingResult) {
		return ImmutableEdFormPostResult.builder()
				.addErrors(locale, messageSource, bindingResult).build();
	}

	public static EdFormPostResult create(Locale locale, MessageSource messageSource,
			BindingResult bindingResult, boolean success) {
		ImmutableEdFormPostResult.Builder builder = ImmutableEdFormPostResult.builder();
		builder.addErrors(locale, messageSource, bindingResult);
		return builder.putResult(SUCCESS_PROPERTY, success).build();
	}

	public static Builder builder() {
		return ImmutableEdFormPostResult.builder();
	}

	public static abstract class Builder {
		public abstract Builder result(Map<String, ? extends java.lang.Object> entries);

		public abstract Builder putResult(String key, Object value);

		public abstract EdFormPostResult build();

		/**
		 * Extracts errors from the bindingResult and inserts them into the error
		 * properties. Sets the property success to false if there are errors. Sets the
		 * property success to true if there are no errors.
		 *
		 * @param builder
		 * @param locale
		 * @param messageSource
		 * @param bindingResult
		 */
		public Builder addErrors(Locale locale, MessageSource messageSource,
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
					putResult(SUCCESS_PROPERTY, Boolean.TRUE);
				}
				else {
					putResult(ERRORS_PROPERTY, errorMap);
					putResult(SUCCESS_PROPERTY, Boolean.FALSE);
				}
			}
			else {
				putResult(SUCCESS_PROPERTY, Boolean.TRUE);
			}

			return this;
		}

		/**
		 * resolve the messages codes along the implementation described in
		 * {@link org.springframework.validation.DefaultMessageCodesResolver}<br>
		 * stop at first message found<br>
		 * method is useless if no specific validation message have been set (example:
		 * javax.validation.constraints.NotNull.message.fax=Fax number is mandatory)<br>
		 * it will behave {@link #addErrors(Locale, MessageSource, BindingResult)} with a
		 * big overhead
		 *
		 * @param locale locale for internationalization
		 * @param messageSource source of validation code and message
		 * @param bindingResult Errors list to resolve
		 * @return this {@link #ExtDirectFormPostResult} for easy chaining
		 */
		public Builder addErrorsResolveCode(Locale locale, MessageSource messageSource,
				BindingResult bindingResult) {
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
								 * expected if code/message doesn't exist, default
								 * behavior to counter that, set to your message bundle,
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
					putResult(SUCCESS_PROPERTY, Boolean.TRUE);
				}
				else {
					putResult(ERRORS_PROPERTY, errorMap);
					putResult(SUCCESS_PROPERTY, Boolean.FALSE);
				}
			}
			else {
				putResult(SUCCESS_PROPERTY, Boolean.TRUE);
			}
			return this;
		}

		public Builder addError(BindingResult bindingResult) {
			addErrors(null, null, bindingResult);
			return this;
		}

		public Builder fail() {
			putResult(SUCCESS_PROPERTY, Boolean.FALSE);
			return this;
		}

		public Builder success() {
			putResult(SUCCESS_PROPERTY, Boolean.TRUE);
			return this;
		}

		/**
		 * Adds one error message to a specific field. Does not overwrite already existing
		 * errors.
		 *
		 * @param field the name of the field
		 * @param error the error message
		 */
		public Builder addError(String field, String error) {
			Assert.notNull(field, "field must not be null");
			Assert.notNull(error, "field must not be null");
			return addErrors(field, Collections.singletonList(error));
		}

		/**
		 * Adds multiple error messages to a specific field. Does not overwrite already
		 * existing errors.
		 *
		 * @param field the name of the field
		 * @param errors a collection of error messages
		 */
		private Map<String, List<String>> helper = new LinkedHashMap<String, List<String>>();

		public Builder addErrors(String field, List<String> errors) {
			Assert.notNull(field, "field must not be null");
			Assert.notNull(errors, "field must not be null");

			List<String> fieldErrors = helper.get(field);
			if (fieldErrors == null) {
				fieldErrors = new ArrayList<String>();
				helper.put(field, fieldErrors);
			}
			fieldErrors.addAll(errors);

			putResult(ERRORS_PROPERTY, helper);
			putResult(SUCCESS_PROPERTY, Boolean.FALSE);

			return this;
		}

	}

}
