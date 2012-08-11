package ch.ralscha.extdirectspring.bean;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.springframework.context.MessageSource;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

public class ExtDirectFormPostResponse {

	private final Map<String, Object> result = new HashMap<String, Object>();

	public ExtDirectFormPostResponse() {
		setSuccess(true);
	}

	public ExtDirectFormPostResponse(boolean success) {
		setSuccess(success);
	}

	public ExtDirectFormPostResponse(BindingResult bindingResult) {
		addErrors(null, null, bindingResult);
	}

	public ExtDirectFormPostResponse(BindingResult bindingResult, boolean success) {
		addErrors(null, null, bindingResult);
		setSuccess(success);
	}

	public ExtDirectFormPostResponse(Locale locale, MessageSource messageSource, BindingResult bindingResult) {
		addErrors(locale, messageSource, bindingResult);
	}

	public ExtDirectFormPostResponse(Locale locale, MessageSource messageSource, BindingResult bindingResult,
			boolean success) {
		addErrors(locale, messageSource, bindingResult);
		setSuccess(success);
	}

	private void addErrors(Locale locale, MessageSource messageSource, BindingResult bindingResult) {
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
		} else {
			setSuccess(true);
		}
	}

	public void addResultProperty(String key, Object value) {
		result.put(key, value);
	}

	public Map<String, Object> getResult() {
		return result;
	}

	public void setSuccess(boolean flag) {
		result.put("success", flag);
	}

}
