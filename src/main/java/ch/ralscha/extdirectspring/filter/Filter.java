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
 */
public class Filter {
	private final String field;

	private final Comparison comparison;

	public Filter(String field, Comparison comparison) {
		this.field = field;
		this.comparison = comparison;
	}

	public String getField() {
		return field;
	}

	public Comparison getComparison() {
		return comparison;
	}

	@SuppressWarnings("unchecked")
	public static Filter createFilter(Map<String, Object> jsonData,
			ConversionService conversionService) {
		String type = (String) jsonData.get("type");
		Object source = jsonData.get("value");

		if (type == null) {
			String property = (String) jsonData.get("property");
			if (property != null) {
				// a filter from store.filter, create a Filter depending on the
				// type of the value
				if (source instanceof Number) {
					return new NumericFilter(property, (Number) source, null);
				}
				else if (source instanceof Boolean) {
					return new BooleanFilter(property, (Boolean) source, null);
				}
				return new StringFilter(property, source != null ? source.toString()
						: null, null);
			}

			return null;
		}

		String field = (String) jsonData.get("field");
		if (field == null) {
			field = (String) jsonData.get("property");
		}
		if (type.equals("numeric") || type.equals("int") || type.equals("float")
				|| type.equals("number")) {
			Number value = conversionService.convert(source, Number.class);
			return new NumericFilter(field, value, getComparison(jsonData));
		}
		else if (type.equals("string")) {
			return new StringFilter(field, (String) source, getComparison(jsonData));
		}
		else if (type.equals("date")) {
			return new DateFilter(field, (String) source, getComparison(jsonData));
		}
		else if (type.equals("list") || type.equals("combo")) {
			if (source instanceof String) {
				String[] values = ((String) source).split(",");
				return new ListFilter(field, Arrays.asList(values),
						getComparison(jsonData));
			}
			return new ListFilter(field, (List<String>) source, getComparison(jsonData));
		}
		else if (type.equals("boolean")) {
			return new BooleanFilter(field, (Boolean) source, getComparison(jsonData));
		}

		return null;
	}

	private static Comparison getComparison(Map<String, Object> jsonData) {
		String comparison = (String) jsonData.get("comparison");
		if (comparison == null) {
			comparison = (String) jsonData.get("operator");
		}
		if (comparison != null) {
			return Comparison.fromString(comparison);
		}

		return null;
	}

}
