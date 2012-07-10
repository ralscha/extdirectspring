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

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.springframework.core.convert.ConversionService;

/**
 * Base class for all filter implementation.
 * 
 * @see BooleanFilter
 * @see DateFilter
 * @see ListFilter
 * @see NumericFilter
 * @see StringFilter
 * 
 * @author Ralph Schaer
 */
public class Filter {
	private final String field;

	public Filter(String field) {
		this.field = field;
	}

	public String getField() {
		return field;
	}

	@SuppressWarnings("unchecked")
	public static Filter createFilter(Map<String, Object> jsonData, ConversionService conversionService) {
		String type = (String) jsonData.get("type");
		Object source = jsonData.get("value");

		if (type == null) {
			if (jsonData.containsKey("property") && jsonData.containsKey("value")) {
				// a filter from store.filter, create a Filter depending on the
				// type of the value
				String property = (String) jsonData.get("property");
				Object filterValue = source;

				if (filterValue instanceof Number) {
					return new NumericFilter(property, (Number) filterValue, null);
				} else if (filterValue instanceof Boolean) {
					return new BooleanFilter(property, (Boolean) filterValue);
				}
				return new StringFilter(property, filterValue.toString());
			}

			return null;
		}

		String field = (String) jsonData.get("field");
		if (type.equals("numeric")) {
			String comparison = (String) jsonData.get("comparison");
			Number value = conversionService.convert(source, Number.class);
			return new NumericFilter(field, value, Comparison.fromString(comparison));
		} else if (type.equals("string")) {
			String value = (String) source;
			return new StringFilter(field, value);
		} else if (type.equals("date")) {
			String comparison = (String) jsonData.get("comparison");
			String value = (String) source;
			return new DateFilter(field, value, Comparison.fromString(comparison));
		} else if (type.equals("list")) {
			Object value = source;
			if (value instanceof String) {
				String[] values = ((String) value).split(",");
				return new ListFilter(field, Arrays.asList(values));
			}
			return new ListFilter(field, (List<String>) value);
		} else if (type.equals("boolean")) {
			boolean value = (Boolean) source;
			return new BooleanFilter(field, value);
		}

		return null;
	}

}
