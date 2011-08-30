/**
 * Copyright 2010-2011 Ralph Schaer <ralphschaer@gmail.com>
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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

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
		assertTrue(filter instanceof NumericFilter);
		NumericFilter numericFilter = (NumericFilter) filter;
		assertEquals("aField", numericFilter.getField());
		assertEquals(12, numericFilter.getValue());
		assertSame(Comparison.LESS_THAN, numericFilter.getComparison());

		json = new HashMap<String, Object>();
		json.put("field", "aField");
		json.put("type", "numeric");
		json.put("comparison", "eq");
		json.put("value", "0");

		filter = Filter.createFilter(json, genericConversionService);
		assertTrue(filter instanceof NumericFilter);
		numericFilter = (NumericFilter) filter;
		assertEquals("aField", numericFilter.getField());
		assertEquals(0, numericFilter.getValue().intValue());
		assertSame(Comparison.EQUAL, numericFilter.getComparison());
	}

	@Test
	public void testNumericFilterWithoutType() {
		Map<String, Object> json = new HashMap<String, Object>();
		json.put("property", "aField");
		json.put("value", 10);

		Filter filter = Filter.createFilter(json, genericConversionService);
		assertTrue(filter instanceof NumericFilter);
		NumericFilter numericFilter = (NumericFilter) filter;
		assertEquals("aField", numericFilter.getField());
		assertEquals(10, numericFilter.getValue());
	}

	@Test
	public void testStringFilter() {
		Map<String, Object> json = new HashMap<String, Object>();
		json.put("field", "aField");
		json.put("type", "string");
		json.put("value", "aString");

		Filter filter = Filter.createFilter(json, genericConversionService);
		assertTrue(filter instanceof StringFilter);
		StringFilter stringFilter = (StringFilter) filter;
		assertEquals("aField", stringFilter.getField());
		assertEquals("aString", stringFilter.getValue());
	}

	@Test
	public void testStringFilterWithoutType() {
		Map<String, Object> json = new HashMap<String, Object>();
		json.put("property", "aField");
		json.put("value", "aString");

		Filter filter = Filter.createFilter(json, genericConversionService);
		assertTrue(filter instanceof StringFilter);
		StringFilter stringFilter = (StringFilter) filter;
		assertEquals("aField", stringFilter.getField());
		assertEquals("aString", stringFilter.getValue());
	}

	@Test
	public void testDateFilter() {
		Map<String, Object> json = new HashMap<String, Object>();
		json.put("field", "aField");
		json.put("type", "date");
		json.put("value", "12.12.2010");
		json.put("comparison", "gt");

		Filter filter = Filter.createFilter(json, genericConversionService);
		assertTrue(filter instanceof DateFilter);
		DateFilter dateFilter = (DateFilter) filter;
		assertEquals("aField", dateFilter.getField());
		assertEquals("12.12.2010", dateFilter.getValue());
		assertSame(Comparison.GREATER_THAN, dateFilter.getComparison());
	}

	@Test
	public void testListFilter() {
		Map<String, Object> json = new HashMap<String, Object>();
		json.put("field", "aField");
		json.put("type", "list");
		json.put("value", "one,two,three");

		Filter filter = Filter.createFilter(json, genericConversionService);
		assertTrue(filter instanceof ListFilter);
		ListFilter listFilter = (ListFilter) filter;
		assertEquals("aField", listFilter.getField());

		List<String> list = listFilter.getValue();
		assertEquals(3, list.size());
		assertTrue(list.contains("one"));
		assertTrue(list.contains("two"));
		assertTrue(list.contains("three"));
		assertFalse(list.contains("four"));
	}

	@Test
	public void testBooleanFilter() {
		Map<String, Object> json = new HashMap<String, Object>();
		json.put("field", "aField");
		json.put("type", "boolean");
		json.put("value", false);

		Filter filter = Filter.createFilter(json, genericConversionService);
		assertTrue(filter instanceof BooleanFilter);
		BooleanFilter booleanFilter = (BooleanFilter) filter;
		assertEquals("aField", booleanFilter.getField());
		assertEquals(false, booleanFilter.getValue());
	}

	@Test
	public void testBooleanFilterWithoutType() {
		Map<String, Object> json = new HashMap<String, Object>();
		json.put("property", "aField");
		json.put("value", false);

		Filter filter = Filter.createFilter(json, genericConversionService);
		assertTrue(filter instanceof BooleanFilter);
		BooleanFilter booleanFilter = (BooleanFilter) filter;
		assertEquals("aField", booleanFilter.getField());
		assertEquals(false, booleanFilter.getValue());
	}

	@Test
	public void testNotExistsFilter() {
		Map<String, Object> json = new HashMap<String, Object>();
		json.put("field", "aField");
		json.put("type", "xy");
		json.put("value", "aValue");

		Filter filter = Filter.createFilter(json, genericConversionService);
		assertNull(filter);
	}

	@Test
	public void testNoValue() {
		Map<String, Object> json = new HashMap<String, Object>();
		json.put("property", "aField");

		Filter filter = Filter.createFilter(json, genericConversionService);
		assertNull(filter);
	}
}
