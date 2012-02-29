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
package ch.ralscha.extdirectspring.filter;

import static org.fest.assertions.Assertions.assertThat;
import static org.junit.Assert.assertSame;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.springframework.core.convert.support.ConversionServiceFactory;
import org.springframework.core.convert.support.GenericConversionService;

public class FilterTest {

	private static final GenericConversionService genericConversionService = ConversionServiceFactory
			.createDefaultConversionService();

	@Test
	public void testNumericFilter() {
		Map<String, Object> json = new HashMap<String, Object>();
		json.put("field", "aField");
		json.put("type", "numeric");
		json.put("comparison", "lt");
		json.put("value", 12);

		Filter filter = Filter.createFilter(json, genericConversionService);
		assertThat(filter instanceof NumericFilter).isTrue();
		NumericFilter numericFilter = (NumericFilter) filter;
		assertThat(numericFilter.getField()).isEqualTo("aField");
		assertThat(numericFilter.getValue()).isEqualTo(12);
		assertSame(Comparison.LESS_THAN, numericFilter.getComparison());

		json = new HashMap<String, Object>();
		json.put("field", "aField");
		json.put("type", "numeric");
		json.put("comparison", "eq");
		json.put("value", "0");

		filter = Filter.createFilter(json, genericConversionService);
		assertThat(filter instanceof NumericFilter).isTrue();
		numericFilter = (NumericFilter) filter;
		assertThat(numericFilter.getField()).isEqualTo("aField");
		assertThat(numericFilter.getValue().intValue()).isEqualTo(0);
		assertSame(Comparison.EQUAL, numericFilter.getComparison());
	}

	@Test
	public void testNumericFilterWithoutType() {
		Map<String, Object> json = new HashMap<String, Object>();
		json.put("property", "aField");
		json.put("value", 10);

		Filter filter = Filter.createFilter(json, genericConversionService);
		assertThat(filter instanceof NumericFilter).isTrue();
		NumericFilter numericFilter = (NumericFilter) filter;
		assertThat(numericFilter.getField()).isEqualTo("aField");
		assertThat(numericFilter.getValue()).isEqualTo(10);
	}

	@Test
	public void testStringFilter() {
		Map<String, Object> json = new HashMap<String, Object>();
		json.put("field", "aField");
		json.put("type", "string");
		json.put("value", "aString");

		Filter filter = Filter.createFilter(json, genericConversionService);
		assertThat(filter instanceof StringFilter).isTrue();
		StringFilter stringFilter = (StringFilter) filter;
		assertThat(stringFilter.getField()).isEqualTo("aField");
		assertThat(stringFilter.getValue()).isEqualTo("aString");
	}

	@Test
	public void testStringFilterWithoutType() {
		Map<String, Object> json = new HashMap<String, Object>();
		json.put("property", "aField");
		json.put("value", "aString");

		Filter filter = Filter.createFilter(json, genericConversionService);
		assertThat(filter instanceof StringFilter).isTrue();
		StringFilter stringFilter = (StringFilter) filter;
		assertThat(stringFilter.getField()).isEqualTo("aField");
		assertThat(stringFilter.getValue()).isEqualTo("aString");
	}

	@Test
	public void testDateFilter() {
		Map<String, Object> json = new HashMap<String, Object>();
		json.put("field", "aField");
		json.put("type", "date");
		json.put("value", "12.12.2010");
		json.put("comparison", "gt");

		Filter filter = Filter.createFilter(json, genericConversionService);
		assertThat(filter instanceof DateFilter).isTrue();
		DateFilter dateFilter = (DateFilter) filter;
		assertThat(dateFilter.getField()).isEqualTo("aField");
		assertThat(dateFilter.getValue()).isEqualTo("12.12.2010");
		assertSame(Comparison.GREATER_THAN, dateFilter.getComparison());
	}

	@Test
	public void testListFilter() {
		Map<String, Object> json = new HashMap<String, Object>();
		json.put("field", "aField");
		json.put("type", "list");
		json.put("value", "one,two,three");

		Filter filter = Filter.createFilter(json, genericConversionService);
		assertThat(filter instanceof ListFilter).isTrue();
		ListFilter listFilter = (ListFilter) filter;
		assertThat(listFilter.getField()).isEqualTo("aField");

		List<String> list = listFilter.getValue();
		assertThat(list).hasSize(3);
		assertThat(list).contains("one", "two", "three");
	}

	@Test
	public void testBooleanFilter() {
		Map<String, Object> json = new HashMap<String, Object>();
		json.put("field", "aField");
		json.put("type", "boolean");
		json.put("value", false);

		Filter filter = Filter.createFilter(json, genericConversionService);
		assertThat(filter instanceof BooleanFilter).isTrue();
		BooleanFilter booleanFilter = (BooleanFilter) filter;
		assertThat(booleanFilter.getField()).isEqualTo("aField");
		assertThat(booleanFilter.getValue()).isEqualTo(false);
	}

	@Test
	public void testBooleanFilterWithoutType() {
		Map<String, Object> json = new HashMap<String, Object>();
		json.put("property", "aField");
		json.put("value", false);

		Filter filter = Filter.createFilter(json, genericConversionService);
		assertThat(filter instanceof BooleanFilter).isTrue();
		BooleanFilter booleanFilter = (BooleanFilter) filter;
		assertThat(booleanFilter.getField()).isEqualTo("aField");
		assertThat(booleanFilter.getValue()).isEqualTo(false);
	}

	@Test
	public void testNotExistsFilter() {
		Map<String, Object> json = new HashMap<String, Object>();
		json.put("field", "aField");
		json.put("type", "xy");
		json.put("value", "aValue");

		Filter filter = Filter.createFilter(json, genericConversionService);
		assertThat(filter).isNull();
	}

	@Test
	public void testNoValue() {
		Map<String, Object> json = new HashMap<String, Object>();
		json.put("property", "aField");

		Filter filter = Filter.createFilter(json, genericConversionService);
		assertThat(filter).isNull();
	}
}
