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
package ch.ralscha.extdirectspring.filter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertSame;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.core.convert.support.GenericConversionService;
import org.springframework.format.support.DefaultFormattingConversionService;

public class NumericFilterTest {

	private static final GenericConversionService genericConversionService = new DefaultFormattingConversionService();

	@ParameterizedTest
	@ValueSource(strings = { "numeric", "int", "float", "number" })
	public void testNumericFilterLT(String type) {
		Map<String, Object> json = new HashMap<>();
		json.put("field", "aField");
		json.put("type", type);
		json.put("comparison", "lt");
		json.put("value", 12);

		Filter filter = Filter.createFilter(json, genericConversionService);
		assertThat(filter).isInstanceOf(NumericFilter.class);
		NumericFilter numericFilter = (NumericFilter) filter;
		assertThat(numericFilter.getField()).isEqualTo("aField");
		assertThat(numericFilter.getValue()).isEqualTo(12);
		assertSame(Comparison.LESS_THAN, numericFilter.getComparison());
	}

	@ParameterizedTest
	@ValueSource(strings = { "numeric", "int", "float", "number" })
	public void testNumericFilterGT(String type) {
		Map<String, Object> json = new HashMap<>();
		json.put("field", "aField2");
		json.put("type", type);
		json.put("comparison", "gt");
		json.put("value", 13);

		Filter filter = Filter.createFilter(json, genericConversionService);
		assertThat(filter).isInstanceOf(NumericFilter.class);
		NumericFilter numericFilter = (NumericFilter) filter;
		assertThat(numericFilter.getField()).isEqualTo("aField2");
		assertThat(numericFilter.getValue()).isEqualTo(13);
		assertSame(Comparison.GREATER_THAN, numericFilter.getComparison());
	}

	@ParameterizedTest
	@ValueSource(strings = { "numeric", "int", "float", "number" })
	public void testNumericFilterEQ(String type) {
		Map<String, Object> json = new HashMap<>();
		json.put("field", "aField3");
		json.put("type", type);
		json.put("comparison", "eq");
		json.put("value", "1");

		Filter filter = Filter.createFilter(json, genericConversionService);
		assertThat(filter).isInstanceOf(NumericFilter.class);
		NumericFilter numericFilter = (NumericFilter) filter;
		assertThat(numericFilter.getField()).isEqualTo("aField3");
		assertThat(numericFilter.getValue().intValue()).isEqualTo(1);
		assertSame(Comparison.EQUAL, numericFilter.getComparison());
		assertThat(numericFilter.getRawComparison()).isEqualTo("eq");
	}

	@ParameterizedTest
	@ValueSource(strings = { "numeric", "int", "float", "number" })
	public void testNumericFilterNE(String type) {
		Map<String, Object> json = new HashMap<>();
		json.put("field", "aField4");
		json.put("type", type);
		json.put("comparison", "ne");
		json.put("value", "3");

		Filter filter = Filter.createFilter(json, genericConversionService);
		assertThat(filter).isInstanceOf(NumericFilter.class);
		NumericFilter numericFilter = (NumericFilter) filter;
		assertThat(numericFilter.getField()).isEqualTo("aField4");
		assertThat(numericFilter.getValue().intValue()).isEqualTo(3);
		assertSame(Comparison.NOT_EQUAL, numericFilter.getComparison());
		assertThat(numericFilter.getRawComparison()).isEqualTo("ne");
	}

	@ParameterizedTest
	@ValueSource(strings = { "numeric", "int", "float", "number" })
	public void testNumericFilterGTE(String type) {
		Map<String, Object> json = new HashMap<>();
		json.put("field", "aField5");
		json.put("type", type);
		json.put("comparison", "gte");
		json.put("value", "4");

		Filter filter = Filter.createFilter(json, genericConversionService);
		assertThat(filter).isInstanceOf(NumericFilter.class);
		NumericFilter numericFilter = (NumericFilter) filter;
		assertThat(numericFilter.getField()).isEqualTo("aField5");
		assertThat(numericFilter.getValue().intValue()).isEqualTo(4);
		assertSame(Comparison.GREATER_THAN_OR_EQUAL, numericFilter.getComparison());
		assertThat(numericFilter.getRawComparison()).isEqualTo("gte");
	}

	@ParameterizedTest
	@ValueSource(strings = { "numeric", "int", "float", "number" })
	public void testNumericFilterLTE(String type) {
		Map<String, Object> json = new HashMap<>();
		json.put("field", "aField6");
		json.put("type", type);
		json.put("comparison", "lte");
		json.put("value", "5");

		Filter filter = Filter.createFilter(json, genericConversionService);
		assertThat(filter).isInstanceOf(NumericFilter.class);
		NumericFilter numericFilter = (NumericFilter) filter;
		assertThat(numericFilter.getField()).isEqualTo("aField6");
		assertThat(numericFilter.getValue().intValue()).isEqualTo(5);
		assertSame(Comparison.LESS_THAN_OR_EQUAL, numericFilter.getComparison());
		assertThat(numericFilter.getRawComparison()).isEqualTo("lte");
	}

	@ParameterizedTest
	@ValueSource(strings = { "numeric", "int", "float", "number" })
	public void testNumericPropertyFilterLT(String type) {
		Map<String, Object> json = new HashMap<>();
		json.put("property", "aField");
		json.put("type", type);
		json.put("comparison", "lt");
		json.put("value", 12);

		Filter filter = Filter.createFilter(json, genericConversionService);
		assertThat(filter).isInstanceOf(NumericFilter.class);
		NumericFilter numericFilter = (NumericFilter) filter;
		assertThat(numericFilter.getField()).isEqualTo("aField");
		assertThat(numericFilter.getValue()).isEqualTo(12);
		assertSame(Comparison.LESS_THAN, numericFilter.getComparison());
		assertThat(numericFilter.getRawComparison()).isEqualTo("lt");
	}

	@ParameterizedTest
	@ValueSource(strings = { "numeric", "int", "float", "number" })
	public void testNumericPropertyFilterGT(String type) {
		Map<String, Object> json = new HashMap<>();
		json.put("property", "aField2");
		json.put("type", type);
		json.put("comparison", "gt");
		json.put("value", 13);

		Filter filter = Filter.createFilter(json, genericConversionService);
		assertThat(filter).isInstanceOf(NumericFilter.class);
		NumericFilter numericFilter = (NumericFilter) filter;
		assertThat(numericFilter.getField()).isEqualTo("aField2");
		assertThat(numericFilter.getValue()).isEqualTo(13);
		assertSame(Comparison.GREATER_THAN, numericFilter.getComparison());
		assertThat(numericFilter.getRawComparison()).isEqualTo("gt");
	}

	@ParameterizedTest
	@ValueSource(strings = { "numeric", "int", "float", "number" })
	public void testNumericPropertyFilterEQ(String type) {
		Map<String, Object> json = new HashMap<>();
		json.put("property", "aField3");
		json.put("type", type);
		json.put("comparison", "eq");
		json.put("value", "1");

		Filter filter = Filter.createFilter(json, genericConversionService);
		assertThat(filter).isInstanceOf(NumericFilter.class);
		NumericFilter numericFilter = (NumericFilter) filter;
		assertThat(numericFilter.getField()).isEqualTo("aField3");
		assertThat(numericFilter.getValue().intValue()).isEqualTo(1);
		assertSame(Comparison.EQUAL, numericFilter.getComparison());
		assertThat(numericFilter.getRawComparison()).isEqualTo("eq");
	}

	@ParameterizedTest
	@ValueSource(strings = { "numeric", "int", "float", "number" })
	public void testNumericPropertyFilterNE(String type) {
		Map<String, Object> json = new HashMap<>();
		json.put("property", "aField4");
		json.put("type", type);
		json.put("comparison", "ne");
		json.put("value", "3");

		Filter filter = Filter.createFilter(json, genericConversionService);
		assertThat(filter).isInstanceOf(NumericFilter.class);
		NumericFilter numericFilter = (NumericFilter) filter;
		assertThat(numericFilter.getField()).isEqualTo("aField4");
		assertThat(numericFilter.getValue().intValue()).isEqualTo(3);
		assertSame(Comparison.NOT_EQUAL, numericFilter.getComparison());
		assertThat(numericFilter.getRawComparison()).isEqualTo("ne");
	}

	@ParameterizedTest
	@ValueSource(strings = { "numeric", "int", "float", "number" })
	public void testNumericPropertyFilterGTE(String type) {
		Map<String, Object> json = new HashMap<>();
		json.put("property", "aField5");
		json.put("type", type);
		json.put("comparison", "gte");
		json.put("value", "4");

		Filter filter = Filter.createFilter(json, genericConversionService);
		assertThat(filter).isInstanceOf(NumericFilter.class);
		NumericFilter numericFilter = (NumericFilter) filter;
		assertThat(numericFilter.getField()).isEqualTo("aField5");
		assertThat(numericFilter.getValue().intValue()).isEqualTo(4);
		assertSame(Comparison.GREATER_THAN_OR_EQUAL, numericFilter.getComparison());
		assertThat(numericFilter.getRawComparison()).isEqualTo("gte");
	}

	@ParameterizedTest
	@ValueSource(strings = { "numeric", "int", "float", "number" })
	public void testNumericPropertyFilterLTE(String type) {
		Map<String, Object> json = new HashMap<>();
		json.put("property", "aField6");
		json.put("type", type);
		json.put("operator", "lte");
		json.put("value", "5");

		Filter filter = Filter.createFilter(json, genericConversionService);
		assertThat(filter).isInstanceOf(NumericFilter.class);
		NumericFilter numericFilter = (NumericFilter) filter;
		assertThat(numericFilter.getField()).isEqualTo("aField6");
		assertThat(numericFilter.getValue().intValue()).isEqualTo(5);
		assertSame(Comparison.LESS_THAN_OR_EQUAL, numericFilter.getComparison());
		assertThat(numericFilter.getRawComparison()).isEqualTo("lte");
	}

	@Test
	public void testNumericFilterWithoutType() {
		Map<String, Object> json = new HashMap<>();
		json.put("property", "aField");
		json.put("value", 10);

		Filter filter = Filter.createFilter(json, genericConversionService);
		assertThat(filter).isInstanceOf(NumericFilter.class);
		NumericFilter numericFilter = (NumericFilter) filter;
		assertThat(numericFilter.getField()).isEqualTo("aField");
		assertThat(numericFilter.getValue()).isEqualTo(10);
		assertThat(numericFilter.getRawComparison()).isNull();
		assertThat(numericFilter.getComparison()).isNull();
	}

	@Test
	public void testNumeric() {
		NumericFilter filter = new NumericFilter("field", 42, "gt",
				Comparison.GREATER_THAN);
		assertThat(filter.getValue()).isEqualTo(42);
		assertThat(filter.getField()).isEqualTo("field");
		assertThat(filter.getRawComparison()).isEqualTo("gt");
		assertThat(filter.getComparison()).isEqualTo(Comparison.GREATER_THAN);
		assertThat(filter.getRawComparison()).isEqualTo("gt");

		filter = new NumericFilter("xy", 23, "eq", Comparison.EQUAL);
		assertThat(filter.getValue()).isEqualTo(23);
		assertThat(filter.getField()).isEqualTo("xy");
		assertThat(filter.getRawComparison()).isEqualTo("eq");
		assertThat(filter.getComparison()).isEqualTo(Comparison.EQUAL);
		assertThat(filter.getRawComparison()).isEqualTo("eq");

		filter = new NumericFilter("field", 44, "lte", Comparison.LESS_THAN_OR_EQUAL);
		assertThat(filter.getValue()).isEqualTo(44);
		assertThat(filter.getField()).isEqualTo("field");
		assertThat(filter.getRawComparison()).isEqualTo("lte");
		assertThat(filter.getComparison()).isEqualTo(Comparison.LESS_THAN_OR_EQUAL);
		assertThat(filter.getRawComparison()).isEqualTo("lte");

	}
}
