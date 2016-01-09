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
package ch.ralscha.extdirectspring.bean;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.assertj.core.data.MapEntry;
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

		StringFilter sf = new StringFilter("field", "10", null, null);
		List<Filter> filters = new ArrayList<Filter>();
		filters.add(sf);
		request.setFilters(filters);
		assertThat(request.getFilters()).hasSize(1).contains(sf);
	}

	@Test
	public void testSetSorters() {
		ExtDirectStoreReadRequest request = new ExtDirectStoreReadRequest();
		assertThat(request.getSorters()).isEmpty();

		request.setSorters(null);
		assertThat(request.getSorters()).isEmpty();

		SortInfo si = new SortInfo("property", SortDirection.ASCENDING);
		List<SortInfo> sortInfos = new ArrayList<SortInfo>();
		sortInfos.add(si);
		request.setSorters(sortInfos);
		assertThat(request.getSorters()).hasSize(1).contains(si);
	}

	@Test
	public void testSetGroups() {
		ExtDirectStoreReadRequest request = new ExtDirectStoreReadRequest();
		assertThat(request.getGroups()).isEmpty();

		request.setGroups(null);
		assertThat(request.getGroups()).isEmpty();

		GroupInfo gi = new GroupInfo("property", SortDirection.ASCENDING);
		List<GroupInfo> groupInfos = new ArrayList<GroupInfo>();
		groupInfos.add(gi);
		request.setGroups(groupInfos);
		assertThat(request.getGroups()).hasSize(1).contains(gi);
	}

	@Test
	public void testSetParams() {
		ExtDirectStoreReadRequest request = new ExtDirectStoreReadRequest();
		assertThat(request.getParams()).isEmpty();

		request.setParams(null);
		assertThat(request.getParams()).isEmpty();

		Map<String, Object> params = new HashMap<String, Object>();
		params.put("id", 10);
		request.setParams(params);
		assertThat(request.getParams()).hasSize(1).contains(MapEntry.entry("id", 10));
	}
}
