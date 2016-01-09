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

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;

public class ListFilterTest {

	@Test
	public void testList() {
		String[] values = { "one", "two", "three" };
		ListFilter<String> filter = new ListFilter<String>("field", Arrays.asList(values),
				null, null);

		List<String> list = filter.getValue();
		assertThat(list).hasSize(3);
		assertThat(list).contains("one", "two", "three");

		assertThat(filter.getRawComparison()).isNull();
		assertThat(filter.getComparison()).isNull();
		assertThat(filter.getField()).isEqualTo("field");
	}

}
