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
package ch.ralscha.extdirectspring.bean;

import java.io.IOException;
import java.util.Collection;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

/**
 * Special result of a DirectStore request. This class is useful if your data is
 * already in JSON format. Add the JSON to the records collection and the
 * response will contain the unfiltered JSON. There is no validation that the
 * added JSON string is valid.
 */
public class ExtDirectRawJsonStoreReadResult extends ExtDirectStoreReadResult<String> {

	public ExtDirectRawJsonStoreReadResult(Collection<String> records) {
		super((Long) null, records, true);
	}

	public ExtDirectRawJsonStoreReadResult(Integer total, Collection<String> records) {
		super(total, records, true);
	}

	public ExtDirectRawJsonStoreReadResult(Integer total, Collection<String> records, Boolean success) {
		super(total, records, success);
	}

	public ExtDirectRawJsonStoreReadResult(Long total, Collection<String> records) {
		super(total, records, true);
	}

	public ExtDirectRawJsonStoreReadResult(Long total, Collection<String> records, Boolean success) {
		super(total, records, success);
	}

	@Override
	@JsonSerialize(using = CollectionStringSerializer.class)
	public Collection<String> getRecords() {
		return super.getRecords();
	}

	@Override
	public String toString() {
		return "ExtDirectRawJsonStoreResult [getRecords()=" + getRecords() + ", getTotal()=" + getTotal()
				+ ", isSuccess()=" + isSuccess() + ", getMetaData()=" + getMetaData() + "]";
	}

	private final static class CollectionStringSerializer extends JsonSerializer<Collection<String>> {

		@Override
		public void serialize(Collection<String> values, JsonGenerator jgen, SerializerProvider provider)
				throws IOException, JsonProcessingException {

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
