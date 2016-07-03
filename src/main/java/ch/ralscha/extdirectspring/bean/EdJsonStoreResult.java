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
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

import org.immutables.value.Value;
import org.immutables.value.Value.Style.ImplementationVisibility;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@JsonInclude(Include.NON_NULL)
@JsonSerialize(as = ImmutableEdJsonStoreResult.class)
@JsonPropertyOrder(value = { "metaData", "success", "total", "records", "message" })
@Value.Style(visibility = ImplementationVisibility.PACKAGE)
@Value.Immutable
public abstract class EdJsonStoreResult extends JsonViewHint {

	@Value.Parameter
	@JsonSerialize(using = CollectionStringSerializer.class)
	public abstract Collection<String> records();

	@Nullable
	@Value.Parameter
	public abstract Long total();

	@Nullable
	@Value.Parameter
	public abstract Boolean success();

	@Nullable
	@Value.Parameter
	public abstract MetaData metaData();

	@Nullable
	@Value.Parameter
	public abstract String message();

	public static EdJsonStoreResult success(String record) {
		return ImmutableEdJsonStoreResult.of(Collections.singletonList(record), null,
				Boolean.TRUE, null, null);
	}

	public static EdJsonStoreResult success(String[] records) {
		return ImmutableEdJsonStoreResult.of(Arrays.asList(records), null, Boolean.TRUE,
				null, null);
	}

	public static EdJsonStoreResult success(Collection<String> records) {
		return ImmutableEdJsonStoreResult.of(records, null, Boolean.TRUE, null, null);
	}

	public static EdJsonStoreResult success(Collection<String> records, Long total) {
		return ImmutableEdJsonStoreResult.of(records, total, Boolean.TRUE, null, null);
	}

	public static EdJsonStoreResult success(Collection<String> records,
			Class<?> jsonView) {
		ImmutableEdJsonStoreResult result = ImmutableEdJsonStoreResult.of(records, null,
				Boolean.TRUE, null, null);
		result.setJsonView(jsonView);
		return result;
	}

	public static EdJsonStoreResult success(Collection<String> records, Long total,
			Class<?> jsonView) {
		ImmutableEdJsonStoreResult result = ImmutableEdJsonStoreResult.of(records, total,
				Boolean.TRUE, null, null);
		result.setJsonView(jsonView);
		return result;
	}

	public static Builder builder() {
		return new Builder();
	}

	public static final class Builder extends ImmutableEdJsonStoreResult.Builder {
		// nothing here
	}

	public final static class CollectionStringSerializer
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
