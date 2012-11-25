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

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

/**
 * @author Ralph Schaer
 */
@JsonInclude(Include.NON_NULL)
public class RangeValidation extends AbstractValidation {

	private final BigDecimal min;

	private final BigDecimal max;

	public RangeValidation(String field, Long min, Long max) {
		this(field, min != null ? new BigDecimal(min) : null, max != null ? new BigDecimal(max) : null);
	}

	public RangeValidation(String field, BigDecimal min, BigDecimal max) {
		super("range", field);

		this.min = min;
		this.max = max;
	}

	public BigDecimal getMin() {
		return min;
	}

	public BigDecimal getMax() {
		return max;
	}

}
