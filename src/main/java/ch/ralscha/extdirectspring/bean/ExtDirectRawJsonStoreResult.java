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

import java.io.IOException;
import java.util.Collection;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

public class ExtDirectRawJsonStoreResult extends ExtDirectStoreResult<String> {

	public ExtDirectRawJsonStoreResult(Collection<String> records) {
		super((Long) null, records, Boolean.TRUE, null);
	}

	public ExtDirectRawJsonStoreResult(Integer total, Collection<String> records) {
		super(total, records, Boolean.TRUE);
	}

	public ExtDirectRawJsonStoreResult(Integer total, Collection<String> records,
			Boolean success) {
		super(total, records, success);
	}

	public ExtDirectRawJsonStoreResult(Long total, Collection<String> records) {
		super(total, records, Boolean.TRUE, null);
	}

	public ExtDirectRawJsonStoreResult(Long total, Collection<String> records,
			Boolean success) {
		super(total, records, success, null);
	}

	@Override
	@JsonSerialize(using = CollectionStringSerializer.class)
	public Collection<String> getRecords() {
		return super.getRecords();
	}

	@Override
	public String toString() {
		return "ExtDirectRawJsonStoreResult [getRecords()=" + getRecords()
				+ ", getTotal()=" + getTotal() + ", isSuccess()=" + isSuccess()
				+ ", getMetaData()=" + getMetaData() + "]";
	}

	private final static class CollectionStringSerializer
			extends JsonSerializer<Collection<String>> {

		@Override
		public void serialize(Collection<String> values, JsonGenerator jgen,
				SerializerProvider provider) throws IOException {

			StringBuilder sb = new StringBuilder();
			sb.append("[");
			if (values != null) {
				for (String value : values) {
					if (sb.length() > 1) {
						sb.append(",");
					}
					sb.append(value);
				}
			}
			sb.append("]");
			jgen.writeRawValue(sb.toString());
		}

	}

}
