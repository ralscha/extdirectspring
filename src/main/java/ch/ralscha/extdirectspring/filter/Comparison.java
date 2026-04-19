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

import java.util.Locale;

public enum Comparison {

	LESS_THAN("lt", "<"), LESS_THAN_OR_EQUAL("lte", "le", "<="), GREATER_THAN("gt", ">"),
	GREATER_THAN_OR_EQUAL("gte", "ge", ">="), EQUAL("eq", "="), NOT_EQUAL("ne", "!="), LIKE("like"), IN("in");

	private final String value1;

	private final String value2;

	private final String value3;

	Comparison(String... values) {
		this.value1 = values.length > 0 ? values[0] : null;
		this.value2 = values.length > 1 ? values[1] : null;
		this.value3 = values.length > 2 ? values[2] : null;
	}

	public boolean is(String externalValue) {
		return externalValue.equals(this.value1) || externalValue.equals(this.value2)
				|| externalValue.equals(this.value3);
	}

	public static Comparison fromString(String externalValue) {
		if (externalValue != null) {
			final String externalValueLowerCase = externalValue.toLowerCase(Locale.ROOT);
			for (Comparison comparison : Comparison.values()) {
				if (comparison.is(externalValueLowerCase)) {
					return comparison;
				}
			}
		}
		return null;
	}

}
