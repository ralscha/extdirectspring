/**
 * Copyright 2010-2014 Ralph Schaer <ralphschaer@gmail.com>
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

import static org.fest.assertions.api.Assertions.assertThat;
import static org.junit.Assert.assertSame;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;
import org.springframework.core.convert.support.GenericConversionService;
import org.springframework.format.support.DefaultFormattingConversionService;

@RunWith(Parameterized.class)
public class NumericFilterTest {

	private static final GenericConversionService genericConversionService = new DefaultFormattingConversionService();

	@Parameters
	public static Collection<Object[]> types() {
		return Arrays.asList(new Object[][] { { "numeric" }, { "int" }, { "float" },
				{ "number" } });
	}

	@Parameter
	public String type;

	@Test
	public void testNumericFilterLT() {
		Map<String, Object> json = new HashMap<String, Object>();
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

	@Test
	public void testNumericFilterGT() {
		Map<String, Object> json = new HashMap<String, Object>();
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

	@Test
	public void testNumericFilterEQ() {
		Map<String, Object> json = new HashMap<String, Object>();
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
	}

	@Test
	public void testNumericFilterNE() {
		Map<String, Object> json = new HashMap<String, Object>();
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
	}

	@Test
	public void testNumericFilterGTE() {
		Map<String, Object> json = new HashMap<String, Object>();
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
	}

	@Test
	public void testNumericFilterLTE() {
		Map<String, Object> json = new HashMap<String, Object>();
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
	}

	@Test
	public void testNumericPropertyFilterLT() {
		Map<String, Object> json = new HashMap<String, Object>();
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
	}

	@Test
	public void testNumericPropertyFilterGT() {
		Map<String, Object> json = new HashMap<String, Object>();
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
	}

	@Test
	public void testNumericPropertyFilterEQ() {
		Map<String, Object> json = new HashMap<String, Object>();
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
	}

	@Test
	public void testNumericPropertyFilterNE() {
		Map<String, Object> json = new HashMap<String, Object>();
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
	}

	@Test
	public void testNumericPropertyFilterGTE() {
		Map<String, Object> json = new HashMap<String, Object>();
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
	}

	@Test
	public void testNumericPropertyFilterLTE() {
		Map<String, Object> json = new HashMap<String, Object>();
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
	}

	@Test
	public void testNumericFilterWithoutType() {
		Map<String, Object> json = new HashMap<String, Object>();
		json.put("property", "aField");
		json.put("value", 10);

		Filter filter = Filter.createFilter(json, genericConversionService);
		assertThat(filter).isInstanceOf(NumericFilter.class);
		NumericFilter numericFilter = (NumericFilter) filter;
		assertThat(numericFilter.getField()).isEqualTo("aField");
		assertThat(numericFilter.getValue()).isEqualTo(10);
	}

	@Test
	public void testNumeric() {
		NumericFilter filter = new NumericFilter("field", 42, Comparison.GREATER_THAN);
		assertThat(filter.getValue()).isEqualTo(42);
		assertThat(filter.getField()).isEqualTo("field");
		assertThat(filter.toString()).isEqualTo(
				"NumericFilter [value=42, comparison=GREATER_THAN, getField()=field]");

		filter = new NumericFilter("xy", 23, Comparison.EQUAL);
		assertThat(filter.getValue()).isEqualTo(23);
		assertThat(filter.getField()).isEqualTo("xy");
		assertThat(filter.toString()).isEqualTo(
				"NumericFilter [value=23, comparison=EQUAL, getField()=xy]");

		filter = new NumericFilter("field", 44, Comparison.LESS_THAN_OR_EQUAL);
		assertThat(filter.getValue()).isEqualTo(44);
		assertThat(filter.getField()).isEqualTo("field");
		assertThat(filter.toString())
				.isEqualTo(
						"NumericFilter [value=44, comparison=LESS_THAN_OR_EQUAL, getField()=field]");
	}
}
