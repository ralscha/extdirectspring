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
package ch.ralscha.extdirectspring.filter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertSame;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.springframework.core.convert.support.GenericConversionService;
import org.springframework.format.support.DefaultFormattingConversionService;

public class FilterTest {

	private static final GenericConversionService genericConversionService = new DefaultFormattingConversionService();

	@Test
	public void testDateFilterGT() {
		Map<String, Object> json = new HashMap<String, Object>();
		json.put("field", "aField");
		json.put("type", "date");
		json.put("value", "12.12.2010");
		json.put("comparison", "gt");

		Filter filter = Filter.createFilter(json, genericConversionService);
		assertThat(filter).isInstanceOf(DateFilter.class);
		DateFilter dateFilter = (DateFilter) filter;
		assertThat(dateFilter.getField()).isEqualTo("aField");
		assertThat(dateFilter.getValue()).isEqualTo("12.12.2010");
		assertSame(Comparison.GREATER_THAN, dateFilter.getComparison());
		assertThat(dateFilter.getRawComparison()).isEqualTo("gt");
	}

	@Test
	public void testDateFilterGTE() {
		Map<String, Object> json = new HashMap<String, Object>();
		json.put("field", "aField2");
		json.put("type", "date");
		json.put("value", "13.12.2010");
		json.put("comparison", "gte");

		Filter filter = Filter.createFilter(json, genericConversionService);
		assertThat(filter).isInstanceOf(DateFilter.class);
		DateFilter dateFilter = (DateFilter) filter;
		assertThat(dateFilter.getField()).isEqualTo("aField2");
		assertThat(dateFilter.getValue()).isEqualTo("13.12.2010");
		assertSame(Comparison.GREATER_THAN_OR_EQUAL, dateFilter.getComparison());
		assertThat(dateFilter.getRawComparison()).isEqualTo("gte");
	}

	@Test
	public void testDateFilterLTE() {
		Map<String, Object> json = new HashMap<String, Object>();
		json.put("field", "aField3");
		json.put("type", "date");
		json.put("value", "11.12.2010");
		json.put("comparison", "lte");

		Filter filter = Filter.createFilter(json, genericConversionService);
		assertThat(filter).isInstanceOf(DateFilter.class);
		DateFilter dateFilter = (DateFilter) filter;
		assertThat(dateFilter.getField()).isEqualTo("aField3");
		assertThat(dateFilter.getValue()).isEqualTo("11.12.2010");
		assertSame(Comparison.LESS_THAN_OR_EQUAL, dateFilter.getComparison());
		assertThat(dateFilter.getRawComparison()).isEqualTo("lte");
	}

	@Test
	public void testDateFilterNE() {
		Map<String, Object> json = new HashMap<String, Object>();
		json.put("field", "aField4");
		json.put("type", "date");
		json.put("value", "11.11.2010");
		json.put("comparison", "ne");

		Filter filter = Filter.createFilter(json, genericConversionService);
		assertThat(filter).isInstanceOf(DateFilter.class);
		DateFilter dateFilter = (DateFilter) filter;
		assertThat(dateFilter.getField()).isEqualTo("aField4");
		assertThat(dateFilter.getValue()).isEqualTo("11.11.2010");
		assertSame(Comparison.NOT_EQUAL, dateFilter.getComparison());
		assertThat(dateFilter.getRawComparison()).isEqualTo("ne");
	}

	@Test
	public void testDateFilterEQ() {
		Map<String, Object> json = new HashMap<String, Object>();
		json.put("field", "aField5");
		json.put("type", "date");
		json.put("value", "11.11.2011");
		json.put("comparison", "eq");

		Filter filter = Filter.createFilter(json, genericConversionService);
		assertThat(filter).isInstanceOf(DateFilter.class);
		DateFilter dateFilter = (DateFilter) filter;
		assertThat(dateFilter.getField()).isEqualTo("aField5");
		assertThat(dateFilter.getValue()).isEqualTo("11.11.2011");
		assertSame(Comparison.EQUAL, dateFilter.getComparison());
		assertThat(dateFilter.getRawComparison()).isEqualTo("eq");
	}

	@Test
	public void testDatePropertyFilterGT() {
		Map<String, Object> json = new HashMap<String, Object>();
		json.put("property", "aField");
		json.put("type", "date");
		json.put("value", "12.12.2010");
		json.put("comparison", "gt");

		Filter filter = Filter.createFilter(json, genericConversionService);
		assertThat(filter).isInstanceOf(DateFilter.class);
		DateFilter dateFilter = (DateFilter) filter;
		assertThat(dateFilter.getField()).isEqualTo("aField");
		assertThat(dateFilter.getValue()).isEqualTo("12.12.2010");
		assertSame(Comparison.GREATER_THAN, dateFilter.getComparison());
		assertThat(dateFilter.getRawComparison()).isEqualTo("gt");
	}

	@Test
	public void testDatePropertyFilterGTE() {
		Map<String, Object> json = new HashMap<String, Object>();
		json.put("property", "aField2");
		json.put("type", "date");
		json.put("value", "13.12.2010");
		json.put("comparison", "gte");

		Filter filter = Filter.createFilter(json, genericConversionService);
		assertThat(filter).isInstanceOf(DateFilter.class);
		DateFilter dateFilter = (DateFilter) filter;
		assertThat(dateFilter.getField()).isEqualTo("aField2");
		assertThat(dateFilter.getValue()).isEqualTo("13.12.2010");
		assertSame(Comparison.GREATER_THAN_OR_EQUAL, dateFilter.getComparison());
		assertThat(dateFilter.getRawComparison()).isEqualTo("gte");
	}

	@Test
	public void testDatePropertyFilterLTE() {
		Map<String, Object> json = new HashMap<String, Object>();
		json.put("property", "aField3");
		json.put("type", "date");
		json.put("value", "11.12.2010");
		json.put("operator", "lte");

		Filter filter = Filter.createFilter(json, genericConversionService);
		assertThat(filter).isInstanceOf(DateFilter.class);
		DateFilter dateFilter = (DateFilter) filter;
		assertThat(dateFilter.getField()).isEqualTo("aField3");
		assertThat(dateFilter.getValue()).isEqualTo("11.12.2010");
		assertSame(Comparison.LESS_THAN_OR_EQUAL, dateFilter.getComparison());
		assertThat(dateFilter.getRawComparison()).isEqualTo("lte");
	}

	@Test
	public void testDatePropertyFilterNE() {
		Map<String, Object> json = new HashMap<String, Object>();
		json.put("property", "aField4");
		json.put("type", "date");
		json.put("value", "11.11.2010");
		json.put("comparison", "ne");

		Filter filter = Filter.createFilter(json, genericConversionService);
		assertThat(filter).isInstanceOf(DateFilter.class);
		DateFilter dateFilter = (DateFilter) filter;
		assertThat(dateFilter.getField()).isEqualTo("aField4");
		assertThat(dateFilter.getValue()).isEqualTo("11.11.2010");
		assertSame(Comparison.NOT_EQUAL, dateFilter.getComparison());
		assertThat(dateFilter.getRawComparison()).isEqualTo("ne");
	}

	@Test
	public void testDatePropertyFilterEQ() {
		Map<String, Object> json = new HashMap<String, Object>();
		json.put("property", "aField5");
		json.put("type", "date");
		json.put("value", "11.11.2011");
		json.put("comparison", "eq");

		Filter filter = Filter.createFilter(json, genericConversionService);
		assertThat(filter).isInstanceOf(DateFilter.class);
		DateFilter dateFilter = (DateFilter) filter;
		assertThat(dateFilter.getField()).isEqualTo("aField5");
		assertThat(dateFilter.getValue()).isEqualTo("11.11.2011");
		assertSame(Comparison.EQUAL, dateFilter.getComparison());
		assertThat(dateFilter.getRawComparison()).isEqualTo("eq");
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Test
	public void testListFilter() {
		Map<String, Object> json = new HashMap<String, Object>();
		json.put("field", "aField");
		json.put("type", "list");
		json.put("value", "one,two,three");

		Filter filter = Filter.createFilter(json, genericConversionService);
		assertThat(filter).isInstanceOf(ListFilter.class);
		assertThat(filter.getRawComparison()).isNull();
		assertThat(filter.getComparison()).isNull();
		ListFilter<String> listFilter = (ListFilter) filter;
		assertThat(listFilter.getField()).isEqualTo("aField");

		List<String> list = listFilter.getValue();
		assertThat(list).hasSize(3);
		assertThat(list).contains("one", "two", "three");

	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Test
	public void testListPropertyFilter() {
		Map<String, Object> json = new HashMap<String, Object>();
		json.put("property", "aField");
		json.put("type", "list");
		json.put("value", "one,two,three");

		Filter filter = Filter.createFilter(json, genericConversionService);
		assertThat(filter).isInstanceOf(ListFilter.class);
		assertThat(filter.getRawComparison()).isNull();
		assertThat(filter.getComparison()).isNull();
		ListFilter<String> listFilter = (ListFilter) filter;
		assertThat(listFilter.getField()).isEqualTo("aField");

		List<String> list = listFilter.getValue();
		assertThat(list).hasSize(3);
		assertThat(list).contains("one", "two", "three");
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
	public void testNotExistsPropertyFilter() {
		Map<String, Object> json = new HashMap<String, Object>();
		json.put("property", "aField");
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
		StringFilter stringFilter = (StringFilter) filter;
		assertThat(stringFilter.getField()).isEqualTo("aField");
		assertThat(stringFilter.getValue()).isNull();
		assertThat(filter.getRawComparison()).isNull();
		assertThat(filter.getComparison()).isNull();
	}
}
