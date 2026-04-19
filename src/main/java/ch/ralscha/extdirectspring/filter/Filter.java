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

	private final String rawComparison;

	private final Comparison comparison;

	public Filter(String field, String rawComparison, Comparison comparison) {
		this.field = field;
		this.rawComparison = rawComparison;
		this.comparison = comparison;
	}

	public String getField() {
		return this.field;
	}

	public String getRawComparison() {
		return this.rawComparison;
	}

	public Comparison getComparison() {
		return this.comparison;
	}

	public String getOperator() {
		return this.rawComparison;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static Filter createFilter(Map<String, Object> jsonData, ConversionService conversionService) {
		String type = (String) jsonData.get("type");
		Object source = jsonData.get("value");
		String rawComparison = extractRawComparison(jsonData);
		Comparison comparisonFromJson = Comparison.fromString(rawComparison);

		String property = (String) jsonData.get("property");
		if (property == null) {
			property = (String) jsonData.get("field");
		}

		if (type == null) {
			if (property != null) {
				// a filter from store.filter, create a Filter depending on the
				// type of the value
				if (source instanceof Number number) {
					return new NumericFilter(property, number, rawComparison, comparisonFromJson);
				}
				if (source instanceof Boolean b) {
					return new BooleanFilter(property, b, rawComparison, comparisonFromJson);
				}
				else if (source instanceof List) {
					return new ListFilter(property, (List<?>) source, rawComparison, comparisonFromJson);
				}
				return new StringFilter(property, source != null ? source.toString() : null, rawComparison,
						comparisonFromJson);
			}

			return null;
		}

		if ("numeric".equals(type) || "int".equals(type) || "float".equals(type) || "number".equals(type)) {
			Number value = conversionService.convert(source, Number.class);
			return new NumericFilter(property, value, rawComparison, comparisonFromJson);
		}
		if ("string".equals(type)) {
			return new StringFilter(property, (String) source, rawComparison, comparisonFromJson);
		}
		else if ("date".equals(type)) {
			return new DateFilter(property, (String) source, rawComparison, comparisonFromJson);
		}
		else if ("list".equals(type) || "combo".equals(type)) {
			if (source instanceof String string) {
				String[] values = string.split(",");
				return new ListFilter(property, Arrays.asList(values), rawComparison, comparisonFromJson);
			}
			return new ListFilter(property, (List<String>) source, rawComparison, comparisonFromJson);
		}
		else if ("boolean".equals(type)) {
			return new BooleanFilter(property, (Boolean) source, rawComparison, comparisonFromJson);
		}

		return null;
	}

	private static String extractRawComparison(Map<String, Object> jsonData) {
		String comparison = (String) jsonData.get("comparison");
		if (comparison != null) {
			return comparison;
		}
		return (String) jsonData.get("operator");
	}

}
