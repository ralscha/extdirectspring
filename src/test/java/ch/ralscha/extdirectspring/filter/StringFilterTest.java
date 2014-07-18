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

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;
import org.springframework.core.convert.support.GenericConversionService;
import org.springframework.format.support.DefaultFormattingConversionService;

public class StringFilterTest {

	private static final GenericConversionService genericConversionService = new DefaultFormattingConversionService();

	@Test
	public void testString() {
		StringFilter filter = new StringFilter("field", "value", null);
		assertThat(filter.getValue()).isEqualTo("value");
		assertThat(filter.getField()).isEqualTo("field");
		assertThat(filter.getComparison()).isNull();
		assertThat(filter.toString()).isEqualTo(
				"StringFilter [value=value, comparison=null, getField()=field]");
	}

	@Test
	public void testStringFilter() {
		Map<String, Object> json = new HashMap<String, Object>();
		json.put("field", "aField");
		json.put("type", "string");
		json.put("value", "aString");

		Filter filter = Filter.createFilter(json, genericConversionService);
		assertThat(filter).isInstanceOf(StringFilter.class);
		StringFilter stringFilter = (StringFilter) filter;
		assertThat(stringFilter.getField()).isEqualTo("aField");
		assertThat(stringFilter.getValue()).isEqualTo("aString");
	}

	@Test
	public void testStringFilterLIKE() {
		Map<String, Object> json = new HashMap<String, Object>();
		json.put("field", "aField");
		json.put("type", "string");
		json.put("value", "aString");
		json.put("comparison", "like");

		Filter filter = Filter.createFilter(json, genericConversionService);
		assertThat(filter).isInstanceOf(StringFilter.class);
		StringFilter stringFilter = (StringFilter) filter;
		assertThat(stringFilter.getField()).isEqualTo("aField");
		assertThat(stringFilter.getValue()).isEqualTo("aString");
		assertThat(stringFilter.getComparison()).isEqualTo(Comparison.LIKE);
	}

	@Test
	public void testStringFilterEQ() {
		Map<String, Object> json = new HashMap<String, Object>();
		json.put("field", "aField");
		json.put("type", "string");
		json.put("value", "aString");
		json.put("comparison", "eq");

		Filter filter = Filter.createFilter(json, genericConversionService);
		assertThat(filter).isInstanceOf(StringFilter.class);
		StringFilter stringFilter = (StringFilter) filter;
		assertThat(stringFilter.getField()).isEqualTo("aField");
		assertThat(stringFilter.getValue()).isEqualTo("aString");
		assertThat(stringFilter.getComparison()).isEqualTo(Comparison.EQUAL);
	}

	@Test
	public void testStringPropertyFilter() {
		Map<String, Object> json = new HashMap<String, Object>();
		json.put("property", "aField");
		json.put("type", "string");
		json.put("value", "aString");

		Filter filter = Filter.createFilter(json, genericConversionService);
		assertThat(filter).isInstanceOf(StringFilter.class);
		StringFilter stringFilter = (StringFilter) filter;
		assertThat(stringFilter.getField()).isEqualTo("aField");
		assertThat(stringFilter.getValue()).isEqualTo("aString");
	}

	@Test
	public void testStringPropertyFilterLIKE() {
		Map<String, Object> json = new HashMap<String, Object>();
		json.put("property", "aField");
		json.put("type", "string");
		json.put("value", "aString");
		json.put("comparison", "LIKE");

		Filter filter = Filter.createFilter(json, genericConversionService);
		assertThat(filter).isInstanceOf(StringFilter.class);
		StringFilter stringFilter = (StringFilter) filter;
		assertThat(stringFilter.getField()).isEqualTo("aField");
		assertThat(stringFilter.getValue()).isEqualTo("aString");
		assertThat(stringFilter.getComparison()).isEqualTo(Comparison.LIKE);
	}

	@Test
	public void testStringPropertyFilterEQ() {
		Map<String, Object> json = new HashMap<String, Object>();
		json.put("property", "aField");
		json.put("type", "string");
		json.put("value", "aString");
		json.put("comparison", "=");

		Filter filter = Filter.createFilter(json, genericConversionService);
		assertThat(filter).isInstanceOf(StringFilter.class);
		StringFilter stringFilter = (StringFilter) filter;
		assertThat(stringFilter.getField()).isEqualTo("aField");
		assertThat(stringFilter.getValue()).isEqualTo("aString");
		assertThat(stringFilter.getComparison()).isEqualTo(Comparison.EQUAL);
	}

	@Test
	public void testStringFilterWithoutType() {
		Map<String, Object> json = new HashMap<String, Object>();
		json.put("property", "aField");
		json.put("value", "aString");

		Filter filter = Filter.createFilter(json, genericConversionService);
		assertThat(filter).isInstanceOf(StringFilter.class);
		StringFilter stringFilter = (StringFilter) filter;
		assertThat(stringFilter.getField()).isEqualTo("aField");
		assertThat(stringFilter.getValue()).isEqualTo("aString");
	}

}
