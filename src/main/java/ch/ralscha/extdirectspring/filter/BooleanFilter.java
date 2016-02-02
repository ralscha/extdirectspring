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

public class BooleanFilter extends Filter {

	private final Boolean value;

	public BooleanFilter(String field, Boolean value, String rawComparison,
			Comparison comparison) {
		super(field, rawComparison, comparison);
		this.value = value;
	}

	public Boolean getValue() {
		return this.value;
	}

	@Override
	public String toString() {
		return "BooleanFilter [value=" + this.value + ", getField()=" + getField()
				+ ", getRawComparison()=" + getRawComparison() + ", getComparison()="
				+ getComparison() + "]";
	}

}
