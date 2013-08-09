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

import java.util.Arrays;
import java.util.Collection;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

/**
 * Class representing the result of STORE_READ and STORE_MODIFY methods.
 * 
 * @param <T> Type of the entry inside the collection
 */
@JsonInclude(Include.NON_NULL)
@JsonPropertyOrder(value = { "metaData", "success", "total", "records" })
public class ExtDirectStoreResult<T> extends JsonViewHint {

	private Long total;

	private Collection<T> records;

	private Boolean success;

	private MetaData metaData;

	public ExtDirectStoreResult() {
		// default constructor
	}

	@SuppressWarnings("unchecked")
	public ExtDirectStoreResult(T record) {
		this((Long) null, Arrays.asList(record), true, null);
	}

	public ExtDirectStoreResult(T[] record) {
		this((Long) null, Arrays.asList(record), true, null);
	}

	public ExtDirectStoreResult(Collection<T> records) {
		this((Long) null, records, true, null);
	}

	public ExtDirectStoreResult(Integer total, Collection<T> records) {
		this(total, records, true);
	}

	public ExtDirectStoreResult(Integer total, Collection<T> records, Boolean success) {
		this(total != null ? Long.valueOf(total) : null, records, success, null);
	}

	public ExtDirectStoreResult(Long total, Collection<T> records) {
		this(total, records, true, null);
	}

	public ExtDirectStoreResult(Long total, Collection<T> records, Class<?> jsonView) {
		this(total, records, true, jsonView);
	}

	public ExtDirectStoreResult(Long total, Collection<T> records, Boolean success, Class<?> jsonView) {
		this.total = total;
		this.records = records;
		this.success = success;
		setJsonView(jsonView);
	}

	public Long getTotal() {
		return total;
	}

	public Collection<T> getRecords() {
		return records;
	}

	public Boolean isSuccess() {
		return success;
	}

	public void setTotal(Long total) {
		this.total = total;
	}

	public void setRecords(Collection<T> records) {
		this.records = records;
	}

	public void setSuccess(Boolean success) {
		this.success = success;
	}

	public Map<String, Object> getMetaData() {
		if (metaData != null) {
			return metaData.getMetaData();
		}
		return null;
	}

	public void setMetaData(MetaData metaData) {
		this.metaData = metaData;
	}

	@Override
	public String toString() {
		return "ExtDirectStoreResult [total=" + total + ", records=" + records + ", success=" + success + ", metaData="
				+ metaData + "]";
	}

}
