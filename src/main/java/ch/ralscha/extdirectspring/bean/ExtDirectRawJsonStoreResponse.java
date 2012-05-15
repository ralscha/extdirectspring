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

import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.map.JsonSerializer;
import org.codehaus.jackson.map.SerializerProvider;
import org.codehaus.jackson.map.annotate.JsonSerialize;

/**
 * Special response of a DirectStore request. This class is useful if your data
 * is already in JSON format. Add the json to the records collection and the
 * response will contain the unfiltered json. There is no validation that the
 * added json string is valid.
 * 
 * @author Ralph Schaer
 */
public class ExtDirectRawJsonStoreResponse extends ExtDirectStoreResponse<String> {

	public ExtDirectRawJsonStoreResponse() {
		// default constructor
	}

	public ExtDirectRawJsonStoreResponse(final Collection<String> records) {
		super(null, records, true);
	}

	public ExtDirectRawJsonStoreResponse(final Integer total, final Collection<String> records) {
		super(total, records, true);
	}

	public ExtDirectRawJsonStoreResponse(final Integer total, final Collection<String> records, final Boolean success) {
		super(total, records, success);
	}

	@Override
	@JsonSerialize(using = CollectionStringSerializer.class)
	public Collection<String> getRecords() {
		return super.getRecords();
	}

	@Override
	public String toString() {
		return "ExtDirectRawJsonStoreResponse [getRecords()=" + getRecords() + ", getTotal()=" + getTotal() + ", isSuccess()="
				+ isSuccess() + ", getMetaData()=" + getMetaData() + "]";
	}

	private final static class CollectionStringSerializer extends JsonSerializer<Collection<String>> {

		@Override
		public void serialize(final Collection<String> values, final JsonGenerator jgen, final SerializerProvider provider)
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
