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

import java.util.Arrays;
import java.util.Map;

import org.springframework.core.convert.ConversionService;

/**
 * @author Ralph Schaer
 */
public class Filter {
	private final String field;

	public Filter(final String field) {
		this.field = field;
	}

	public String getField() {
		return field;
	}

	public static Filter createFilter(final Map<String, Object> jsonData, ConversionService conversionService) {
		String field = (String) jsonData.get("field");
		String type = (String) jsonData.get("type");

		if (type.equals("numeric")) {
			String comparison = (String) jsonData.get("comparison");
			Number value = conversionService.convert(jsonData.get("value"), Number.class);
			return new NumericFilter(field, value, Comparison.fromString(comparison));
		} else if (type.equals("string")) {
			String value = (String) jsonData.get("value");
			return new StringFilter(field, value);
		} else if (type.equals("date")) {
			String comparison = (String) jsonData.get("comparison");
			String value = (String) jsonData.get("value");
			return new DateFilter(field, value, Comparison.fromString(comparison));
		} else if (type.equals("list")) {
			String value = (String) jsonData.get("value");
			String[] values = value.split(",");
			return new ListFilter(field, Arrays.asList(values));
		} else if (type.equals("boolean")) {
			boolean value = (Boolean) jsonData.get("value");
			return new BooleanFilter(field, value);
		} else {
			return null;
		}
	}

}
