/**
 * Copyright 2010-2013 Ralph Schaer <ralphschaer@gmail.com>
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
package ch.ralscha.extdirectspring.bean;

import static org.fest.assertions.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import ch.ralscha.extdirectspring.filter.Filter;
import ch.ralscha.extdirectspring.filter.StringFilter;
public class ExtDirectStoreReadRequestTest {

	@Test
	public void testSetFilter() {
		ExtDirectStoreReadRequest request = new ExtDirectStoreReadRequest();
		assertThat(request.getFilters()).isEmpty();
		
		request.setFilters(null);
		assertThat(request.getFilters()).isEmpty();
		
		StringFilter sf = new StringFilter("field", "10");
		List<Filter> filters = new ArrayList<Filter>();
		filters.add(sf);
		request.setFilters(filters);
		assertThat(request.getFilters()).hasSize(1).contains(sf);
	}

}
