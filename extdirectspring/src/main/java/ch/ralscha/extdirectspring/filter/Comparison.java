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

import java.util.NoSuchElementException;

/**
 * @author Ralph Schaer
 */
public enum Comparison {
	LESS_THAN("lt"), GREATER_THAN("gt"), EQUAL("eq");

	private final String name;

	private Comparison(final String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public static Comparison fromString(final String name) {
		for (Comparison comparison : Comparison.values()) {
			if (comparison.getName().equalsIgnoreCase(name)) {
				return comparison;
			}
		}
		throw new NoSuchElementException(name + " not found");
	}

}
