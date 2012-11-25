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

import static org.fest.assertions.api.Assertions.assertThat;

import org.junit.Test;

public class BooleanFilterTest {

	@Test
	public void testFalseFilter() {
		BooleanFilter filter = new BooleanFilter("field", false);
		assertThat(filter.getValue()).isEqualTo(false);
		assertThat(filter.getField()).isEqualTo("field");
		assertThat(filter.toString()).isEqualTo("BooleanFilter [value=false, getField()=field]");
	}

	@Test
	public void testTrueFilter() {
		BooleanFilter filter = new BooleanFilter("xy", true);
		assertThat(filter.getValue()).isEqualTo(true);
		assertThat(filter.getField()).isEqualTo("xy");
		assertThat(filter.toString()).isEqualTo("BooleanFilter [value=true, getField()=xy]");
	}
}
