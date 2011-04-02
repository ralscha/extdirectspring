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
package ch.ralscha.extdirectspring.bean;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.codehaus.jackson.annotate.JsonIgnore;

import ch.ralscha.extdirectspring.filter.Filter;

/**
 * 
 * Class representing the request of a DirectStore read call
 * 
 * @author Ralph Schaer
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
	private List<Filter> filters;

	public ExtDirectStoreReadRequest() {
		this.filters = Collections.emptyList();
		this.sorters = Collections.emptyList();
	}

	public String getQuery() {
		return query;
	}

	public void setQuery(final String query) {
		this.query = query;
	}

	public Integer getLimit() {
		return limit;
	}

	public void setLimit(final Integer limit) {
		this.limit = limit;
	}

	public Integer getStart() {
		return start;
	}

	public void setStart(final Integer start) {
		this.start = start;
	}

	public String getDir() {
		return dir;
	}

	public void setDir(final String dir) {
		this.dir = dir;
	}

	@JsonIgnore
	public boolean isAscendingSort() {
		return (SortDirection.fromString(getDir()) == SortDirection.ASCENDING);
	}

	@JsonIgnore
	public boolean isDescendingSort() {
		return (SortDirection.fromString(getDir()) == SortDirection.DESCENDING);
	}

	public String getSort() {
		return sort;
	}

	public void setSort(final String sort) {
		this.sort = sort;
	}

	public String getGroupBy() {
		return groupBy;
	}

	public void setGroupBy(final String groupBy) {
		this.groupBy = groupBy;
	}

	public String getGroupDir() {
		return groupDir;
	}

	public void setGroupDir(final String groupDir) {
		this.groupDir = groupDir;
	}

	@JsonIgnore
	public boolean isAscendingGroupSort() {
		return (SortDirection.fromString(getGroupDir()) == SortDirection.ASCENDING);
	}

	@JsonIgnore
	public boolean isDescendingGroupSort() {
		return (SortDirection.fromString(getGroupDir()) == SortDirection.DESCENDING);
	}

	public Collection<Filter> getFilters() {
		return Collections.unmodifiableCollection(filters);
	}

	public void setFilters(final List<Filter> filters) {
		this.filters = filters;
	}

	public Integer getPage() {
		return page;
	}

	public void setPage(Integer page) {
		this.page = page;
	}
	
	public Collection<SortInfo> getSorters() {
		return Collections.unmodifiableCollection(sorters);
	}

	public void setSorters(final List<SortInfo> sorters) {
		this.sorters = sorters;
	}

	@Override
	public String toString() {
		return "ExtDirectStoreReadRequest [query=" + query + ", limit=" + limit + ", start=" + start + ", page=" + page
				+ ", dir=" + dir + ", sort=" + sort + ", groupBy=" + groupBy + ", groupDir=" + groupDir + ", sorters="
				+ sorters + ", filters=" + filters + "]";
	}	

}
