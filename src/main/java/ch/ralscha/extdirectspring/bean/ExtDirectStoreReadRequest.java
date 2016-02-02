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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnore;

import ch.ralscha.extdirectspring.filter.BooleanFilter;
import ch.ralscha.extdirectspring.filter.DateFilter;
import ch.ralscha.extdirectspring.filter.Filter;
import ch.ralscha.extdirectspring.filter.ListFilter;
import ch.ralscha.extdirectspring.filter.NumericFilter;
import ch.ralscha.extdirectspring.filter.StringFilter;

/**
 * Represents the request of a DirectStore read call.
 */
public class ExtDirectStoreReadRequest {

	private String query;

	private Integer limit;

	private Integer start;

	private Integer page;

	private String dir;

	private String sort;

	private String groupBy;

	private String groupDir;

	private List<SortInfo> sorters;

	private List<GroupInfo> groups;

	private List<Filter> filters;

	private Map<String, Object> params;

	public ExtDirectStoreReadRequest() {
		this.filters = Collections.emptyList();
		this.sorters = Collections.emptyList();
		this.groups = Collections.emptyList();
		this.params = Collections.emptyMap();
	}

	/**
	 * @return the text a user entered into a combobox with queryMode 'remote'
	 */
	public String getQuery() {
		return this.query;
	}

	public void setQuery(String query) {
		this.query = query;
	}

	/**
	 * @return the number of rows the DirectStore requests for paging
	 */
	public Integer getLimit() {
		return this.limit;
	}

	public void setLimit(Integer limit) {
		this.limit = limit;
	}

	/**
	 * @return the start row from where to send records back for a paging request. start =
	 * {@link #getLimit()} * ( {@link #getPage()}-1)
	 */
	public Integer getStart() {
		return this.start;
	}

	public void setStart(Integer start) {
		this.start = start;
	}

	/**
	 * @return sorting order. "ASC" or "DESC".<br>
	 * Ext JS 4.x and Touch 2 can send more than one sorters. Use {@link #getSorters()}
	 * instead.
	 * @see #isAscendingSort()
	 * @see #isDescendingSort()
	 */
	public String getDir() {
		return this.dir;
	}

	public void setDir(String dir) {
		this.dir = dir;
	}

	/**
	 * @return true if sorting order is ascending.<br>
	 * Ext JS 4.x and Touch 2 can send more than one sorters. Use {@link #getSorters()}
	 * instead.
	 */
	@JsonIgnore
	public boolean isAscendingSort() {
		return SortDirection.fromString(getDir()) == SortDirection.ASCENDING;
	}

	/**
	 * @return true if sorting order is descending.<br>
	 * Ext JS 4.x and Touch 2 can send more than one sorters. Use {@link #getSorters()}
	 * instead.
	 */
	@JsonIgnore
	public boolean isDescendingSort() {
		return SortDirection.fromString(getDir()) == SortDirection.DESCENDING;
	}

	/**
	 * @return the field/property on which the sort should be applied.<br>
	 * Ext JS 4.x and Touch 2 can send more than one sorters. Use {@link #getSorters()}
	 * instead.
	 */
	public String getSort() {
		return this.sort;
	}

	public void setSort(String sort) {
		this.sort = sort;
	}

	/**
	 * @return the field/property name on which the grouping should occur.<br>
	 * Ext JS 4.x and Touch 2 can send more than one group info. Use {@link #getGroups()}
	 * instead.
	 */
	public String getGroupBy() {
		return this.groupBy;
	}

	public void setGroupBy(String groupBy) {
		this.groupBy = groupBy;
	}

	/**
	 * @return sorting order for a grouping request. "ASC" or "DESC".<br>
	 * Ext JS 4.x and Touch 2 can send more than one group info. Use {@link #getGroups()}
	 * instead.
	 */
	public String getGroupDir() {
		return this.groupDir;
	}

	public void setGroupDir(String groupDir) {
		this.groupDir = groupDir;
	}

	/**
	 * @return true if grouping sorting order is ascending.<br>
	 * Ext JS 4.x and Touch 2 can send more than one group info. Use {@link #getGroups()}
	 * instead.
	 */
	@JsonIgnore
	public boolean isAscendingGroupSort() {
		return SortDirection.fromString(getGroupDir()) == SortDirection.ASCENDING;
	}

	/**
	 * @return true if grouping sorting order is descending.<br>
	 * Ext JS 4.x and Touch 2 can send more than one group info. Use {@link #getGroups()}
	 * instead.
	 */
	@JsonIgnore
	public boolean isDescendingGroupSort() {
		return SortDirection.fromString(getGroupDir()) == SortDirection.DESCENDING;
	}

	/**
	 * @return collection of filter implementations
	 * @see BooleanFilter
	 * @see DateFilter
	 * @see ListFilter
	 * @see NumericFilter
	 * @see StringFilter
	 */
	public List<Filter> getFilters() {
		return Collections.unmodifiableList(this.filters);
	}

	/**
	 * Returns the first filter for the field.
	 *
	 * @param field name of the field
	 * @return the first filter for the field. Null if not filter exists.
	 */
	@SuppressWarnings("unchecked")
	public <T extends Filter> T getFirstFilterForField(String field) {
		for (Filter filter : this.filters) {
			if (filter.getField().equals(field)) {
				return (T) filter;
			}
		}
		return null;
	}

	/**
	 * Returns all filters for a field
	 *
	 * @param field name of the field
	 * @return a collection of filters for the field. Empty collection if no filter exists
	 */
	public List<Filter> getAllFiltersForField(String field) {
		List<Filter> foundFilters = new ArrayList<Filter>();

		for (Filter filter : this.filters) {
			if (filter.getField().equals(field)) {
				foundFilters.add(filter);
			}
		}

		return Collections.unmodifiableList(foundFilters);
	}

	public void setFilters(List<Filter> filters) {
		if (filters != null) {
			this.filters = filters;
		}
		else {
			this.filters = Collections.emptyList();
		}
	}

	/**
	 * @return page number of a paging request. page = ({@link #getStart()} /
	 * {@link #getLimit()}) + 1
	 */
	public Integer getPage() {
		return this.page;
	}

	public void setPage(Integer page) {
		this.page = page;
	}

	public List<SortInfo> getSorters() {
		return Collections.unmodifiableList(this.sorters);
	}

	public void setSorters(List<SortInfo> sorters) {
		if (sorters != null) {
			this.sorters = sorters;
		}
		else {
			this.sorters = Collections.emptyList();
		}
	}

	public List<GroupInfo> getGroups() {
		return Collections.unmodifiableList(this.groups);
	}

	public void setGroups(List<GroupInfo> groups) {
		if (groups != null) {
			this.groups = groups;
		}
		else {
			this.groups = Collections.emptyList();
		}
	}

	/**
	 * @return a map with all the keys and values from <code>extraParams</code>
	 */
	public Map<String, Object> getParams() {
		return Collections.unmodifiableMap(this.params);
	}

	public void setParams(Map<String, Object> params) {
		if (params != null) {
			this.params = params;
		}
		else {
			this.params = Collections.emptyMap();
		}
	}

	@Override
	public String toString() {
		return "ExtDirectStoreReadRequest [query=" + this.query + ", limit=" + this.limit
				+ ", start=" + this.start + ", page=" + this.page + ", dir=" + this.dir
				+ ", sort=" + this.sort + ", groupBy=" + this.groupBy + ", groupDir="
				+ this.groupDir + ", sorters=" + this.sorters + ", groups=" + this.groups
				+ ", filters=" + this.filters + ", params=" + this.params + "]";
	}

}
