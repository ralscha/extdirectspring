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

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class DateFilterTest {

	@Test
	public void testDate() {
		DateFilter filter = new DateFilter("field", "12.12.2010", Comparison.LESS_THAN);
		assertEquals("12.12.2010", filter.getValue());
		assertEquals("field", filter.getField());
		assertEquals("DateFilter [value=12.12.2010, comparison=LESS_THAN, getField()=field]", filter.toString());

		filter = new DateFilter("xy", "01.01.2000", Comparison.EQUAL);
		assertEquals("01.01.2000", filter.getValue());
		assertEquals("xy", filter.getField());
		assertEquals("DateFilter [value=01.01.2000, comparison=EQUAL, getField()=xy]", filter.toString());
	}
}
