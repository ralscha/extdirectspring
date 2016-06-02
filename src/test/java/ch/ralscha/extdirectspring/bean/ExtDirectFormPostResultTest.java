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

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.assertj.core.data.MapEntry;
import org.junit.Test;
import org.springframework.validation.AbstractBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

@SuppressWarnings("unchecked")
public class ExtDirectFormPostResultTest {

	@Test
	public void testExtDirectFormPostResult() {
		ExtDirectFormPostResult result = new ExtDirectFormPostResult();
		assertThat(result.getResult()).hasSize(1)
				.contains(MapEntry.entry("success", Boolean.TRUE));
	}

	@Test
	public void testExtDirectFormPostResultBoolean() {
		ExtDirectFormPostResult result = new ExtDirectFormPostResult(true);
		assertThat(result.getResult()).hasSize(1)
				.contains(MapEntry.entry("success", Boolean.TRUE));

		result = new ExtDirectFormPostResult(false);
		assertThat(result.getResult()).hasSize(1)
				.contains(MapEntry.entry("success", Boolean.FALSE));
	}

	@Test
	public void testExtDirectFormPostResultBindingResult() {
		BindingResult br = new TestBindingResult(Collections.<FieldError>emptyList());
		ExtDirectFormPostResult result = new ExtDirectFormPostResult(br);
		assertThat(result.getResult()).hasSize(1)
				.contains(MapEntry.entry("success", Boolean.TRUE));

		FieldError error = new FieldError("testobject", "field1", "message");
		br = new TestBindingResult(Collections.singletonList(error));
		result = new ExtDirectFormPostResult(br);
		assertThat(result.getResult()).hasSize(2)
				.contains(MapEntry.entry("success", Boolean.FALSE));
		Map<String, List<String>> errors = (Map<String, List<String>>) result.getResult()
				.get("errors");
		assertThat(errors).isNotNull().hasSize(1);
		assertThat(errors.get("field1")).containsExactly("message");
	}

	@Test
	public void testExtDirectFormPostResultBindingResultBoolean() {
		BindingResult br = new TestBindingResult(Collections.<FieldError>emptyList());
		ExtDirectFormPostResult result = new ExtDirectFormPostResult(br, false);
		assertThat(result.getResult()).hasSize(1)
				.contains(MapEntry.entry("success", Boolean.FALSE));

		br = new TestBindingResult(
				Arrays.asList(new FieldError("testobject", "field1", "message"),
						new FieldError("testobject", "field2", "second message")));
		result = new ExtDirectFormPostResult(br, true);
		assertThat(result.getResult()).hasSize(2)
				.contains(MapEntry.entry("success", Boolean.TRUE));
		Map<String, List<String>> errors = (Map<String, List<String>>) result.getResult()
				.get("errors");
		assertThat(errors).isNotNull().hasSize(2);
		assertThat(errors.get("field1")).containsExactly("message");
		assertThat(errors.get("field2")).containsExactly("second message");

		result.addError("field2", "another message");
		errors = (Map<String, List<String>>) result.getResult().get("errors");
		assertThat(errors).isNotNull().hasSize(2);
		assertThat(errors.get("field2")).containsExactly("second message",
				"another message");
	}

	@Test
	public void testAddError() {
		ExtDirectFormPostResult result = new ExtDirectFormPostResult();
		result.addError("field1", "error1");
		result.addError("field1", "error2");
		result.addError("field2", "error_for_field2");
		result.addError("field3", "error_for_field3");
		result.addError("field1", "error3");

		assertThat(result.getResult()).hasSize(2)
				.contains(MapEntry.entry("success", Boolean.FALSE));

		Map<String, List<String>> errors = (Map<String, List<String>>) result.getResult()
				.get("errors");
		assertThat(errors).isNotNull().hasSize(3);
		assertThat(errors.get("field1")).containsExactly("error1", "error2", "error3");
		assertThat(errors.get("field2")).containsExactly("error_for_field2");
		assertThat(errors.get("field3")).containsExactly("error_for_field3");
	}

	@Test
	public void testAddErrors() {
		ExtDirectFormPostResult result = new ExtDirectFormPostResult();
		result.addErrors("field1", Arrays.asList("errors1", "errors2", "errors3"));
		result.addErrors("field2", Arrays.asList("errors_for_field2"));
		result.addErrors("field3", Arrays.asList("errors_for_field3"));
		result.addErrors("field1", Arrays.asList("errors4", "errors5"));

		assertThat(result.getResult()).hasSize(2)
				.contains(MapEntry.entry("success", Boolean.FALSE));

		Map<String, List<String>> errors = (Map<String, List<String>>) result.getResult()
				.get("errors");
		assertThat(errors).isNotNull().hasSize(3);
		assertThat(errors.get("field1")).containsExactly("errors1", "errors2", "errors3",
				"errors4", "errors5");
		assertThat(errors.get("field2")).containsExactly("errors_for_field2");
		assertThat(errors.get("field3")).containsExactly("errors_for_field3");
	}

	@Test
	public void testAddResultProperty() {
		ExtDirectFormPostResult result = new ExtDirectFormPostResult();
		result.addResultProperty("one", 1);
		result.addResultProperty("two", "2");
		result.addResultProperty("three", Boolean.TRUE);
		assertThat(result.getResult()).hasSize(4).contains(
				MapEntry.entry("success", Boolean.TRUE), MapEntry.entry("one", 1),
				MapEntry.entry("two", "2"), MapEntry.entry("three", Boolean.TRUE));

	}

	@Test
	public void testSetSuccess() {
		ExtDirectFormPostResult result = new ExtDirectFormPostResult();
		assertThat(result.getResult()).hasSize(1)
				.contains(MapEntry.entry("success", Boolean.TRUE));
		result.setSuccess(false);
		assertThat(result.getResult()).hasSize(1)
				.contains(MapEntry.entry("success", Boolean.FALSE));
		result.setSuccess(true);
		assertThat(result.getResult()).hasSize(1)
				.contains(MapEntry.entry("success", Boolean.TRUE));
	}

	private static class TestBindingResult extends AbstractBindingResult {

		private static final long serialVersionUID = 1L;

		private List<FieldError> errors = null;

		protected TestBindingResult(List<FieldError> errors) {
			super(null);
			this.errors = errors;
		}

		@Override
		public Object getTarget() {
			return null;
		}

		@Override
		protected Object getActualFieldValue(String field) {
			return null;
		}

		@Override
		public List<FieldError> getFieldErrors() {
			return this.errors;
		}

	}
}
