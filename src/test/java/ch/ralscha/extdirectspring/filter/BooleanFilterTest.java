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

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;
import org.springframework.core.convert.support.GenericConversionService;
import org.springframework.format.support.DefaultFormattingConversionService;

public class BooleanFilterTest {

	private static final GenericConversionService genericConversionService = new DefaultFormattingConversionService();

	@Test
	public void testFalseFilter() {
		BooleanFilter filter = new BooleanFilter("field", false, null, null);
		assertThat(filter.getValue()).isEqualTo(false);
		assertThat(filter.getField()).isEqualTo("field");
		assertThat(filter.getRawComparison()).isNull();
		assertThat(filter.getComparison()).isNull();
	}

	@Test
	public void testTrueFilter() {
		BooleanFilter filter = new BooleanFilter("xy", true, null, null);
		assertThat(filter.getValue()).isEqualTo(true);
		assertThat(filter.getField()).isEqualTo("xy");
		assertThat(filter.getRawComparison()).isNull();
		assertThat(filter.getComparison()).isNull();
	}

	@Test
	public void testBooleanFilter() {
		Map<String, Object> json = new HashMap<String, Object>();
		json.put("field", "aField");
		json.put("type", "boolean");
		json.put("value", Boolean.FALSE);

		Filter filter = Filter.createFilter(json, genericConversionService);
		assertThat(filter).isInstanceOf(BooleanFilter.class);
		BooleanFilter booleanFilter = (BooleanFilter) filter;
		assertThat(booleanFilter.getField()).isEqualTo("aField");
		assertThat(booleanFilter.getValue()).isEqualTo(false);
	}

	@Test
	public void testBooleanNullFilter() {
		Map<String, Object> json = new HashMap<String, Object>();
		json.put("field", "aField");
		json.put("type", "boolean");
		json.put("value", null);

		Filter filter = Filter.createFilter(json, genericConversionService);
		assertThat(filter).isInstanceOf(BooleanFilter.class);
		BooleanFilter booleanFilter = (BooleanFilter) filter;
		assertThat(booleanFilter.getField()).isEqualTo("aField");
		assertThat(booleanFilter.getValue()).isNull();
	}

	@Test
	public void testBooleanPropertyFilter() {
		Map<String, Object> json = new HashMap<String, Object>();
		json.put("property", "aField");
		json.put("type", "boolean");
		json.put("value", Boolean.FALSE);

		Filter filter = Filter.createFilter(json, genericConversionService);
		assertThat(filter).isInstanceOf(BooleanFilter.class);
		BooleanFilter booleanFilter = (BooleanFilter) filter;
		assertThat(booleanFilter.getField()).isEqualTo("aField");
		assertThat(booleanFilter.getValue()).isEqualTo(false);
	}

	@Test
	public void testBooleanPropertyNullFilter() {
		Map<String, Object> json = new HashMap<String, Object>();
		json.put("property", "aField");
		json.put("type", "boolean");
		json.put("value", null);

		Filter filter = Filter.createFilter(json, genericConversionService);
		assertThat(filter).isInstanceOf(BooleanFilter.class);
		BooleanFilter booleanFilter = (BooleanFilter) filter;
		assertThat(booleanFilter.getField()).isEqualTo("aField");
		assertThat(booleanFilter.getValue()).isNull();
	}

	@Test
	public void testBooleanFilterWithoutType() {
		Map<String, Object> json = new HashMap<String, Object>();
		json.put("property", "aField");
		json.put("value", Boolean.FALSE);

		Filter filter = Filter.createFilter(json, genericConversionService);
		assertThat(filter).isInstanceOf(BooleanFilter.class);
		BooleanFilter booleanFilter = (BooleanFilter) filter;
		assertThat(booleanFilter.getField()).isEqualTo("aField");
		assertThat(booleanFilter.getValue()).isEqualTo(false);
	}

}
