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
package ch.ralscha.extdirectspring.generator.validation;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
public class LengthValidation extends AbstractValidation {

	private final Long min;

	private final Long max;

	public LengthValidation(String field, Integer min, Integer max) {
		this(field, min != null ? Long.valueOf(min) : null, max != null ? Long.valueOf(max) : null);
	}

	public LengthValidation(String field, Long min, Long max) {
		super("length", field);

		if (min == null && max == null) {
			throw new IllegalArgumentException("At least min or max must be set");
		}

		this.min = min;
		this.max = max;
	}

	public Long getMin() {
		return min;
	}

	public Long getMax() {
		return max;
	}

}
