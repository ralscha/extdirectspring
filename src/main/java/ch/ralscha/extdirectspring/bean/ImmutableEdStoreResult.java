/*
 * Copyright the original author or authors.
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
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

import org.jspecify.annotations.Nullable;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public final class ImmutableEdStoreResult<T> extends EdStoreResult<T> {

	private final Collection<T> records;

	private final @Nullable Long total;

	private final @Nullable Boolean success;

	private final @Nullable Map<String, Object> metaData;

	private final @Nullable String message;

	@JsonCreator
	private ImmutableEdStoreResult(@JsonProperty(value = "records", required = true) Collection<T> records,
			@JsonProperty("total") @Nullable Long total, @JsonProperty("success") @Nullable Boolean success,
			@JsonProperty("metaData") @Nullable Map<String, Object> metaData,
			@JsonProperty("message") @Nullable String message) {
		this.records = Objects.requireNonNull(records, "records");
		this.total = total;
		this.success = success;
		this.metaData = metaData == null ? null : createUnmodifiableMap(metaData);
		this.message = message;
	}

	public static <T> ImmutableEdStoreResult<T> of(Collection<T> records, @Nullable Long total,
			@Nullable Boolean success, @Nullable Map<String, ? extends Object> metaData, @Nullable String message) {
		return new ImmutableEdStoreResult<>(records, total, success, copyNullableMap(metaData), message);
	}

	public static <T> ImmutableEdStoreResult<T> copyOf(EdStoreResult<T> instance) {
		if (instance instanceof ImmutableEdStoreResult<?> immutable) {
			return (ImmutableEdStoreResult<T>) immutable;
		}
		return new EdStoreResult.Builder<T>().from(instance).build();
	}

	@JsonProperty(value = "records", required = true)
	@Override
	public Collection<T> records() {
		return this.records;
	}

	@JsonProperty("total")
	@Override
	public @Nullable Long total() {
		return this.total;
	}

	@JsonProperty("success")
	@Override
	public @Nullable Boolean success() {
		return this.success;
	}

	@JsonProperty("metaData")
	@Override
	public @Nullable Map<String, Object> metaData() {
		return this.metaData;
	}

	@JsonProperty("message")
	@Override
	public @Nullable String message() {
		return this.message;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!(obj instanceof ImmutableEdStoreResult<?> other)) {
			return false;
		}
		return this.records.equals(other.records) && Objects.equals(this.total, other.total)
				&& Objects.equals(this.success, other.success) && Objects.equals(this.metaData, other.metaData)
				&& Objects.equals(this.message, other.message);
	}

	@Override
	public int hashCode() {
		return Objects.hash(this.records, this.total, this.success, this.metaData, this.message);
	}

	@Override
	public String toString() {
		return "EdStoreResult{" + "records=" + this.records + ", total=" + this.total + ", success=" + this.success
				+ ", metaData=" + this.metaData + ", message=" + this.message + '}';
	}

	private static Map<String, Object> copyNullableMap(@Nullable Map<String, ? extends Object> entries) {
		if (entries == null) {
			return null;
		}
		Map<String, Object> copy = new LinkedHashMap<>(entries.size());
		for (Map.Entry<String, ? extends Object> entry : entries.entrySet()) {
			copy.put(entry.getKey(), entry.getValue());
		}
		return copy;
	}

	private static Map<String, Object> createUnmodifiableMap(Map<String, ? extends Object> entries) {
		if (entries.isEmpty()) {
			return Collections.emptyMap();
		}
		Map<String, Object> copy = new LinkedHashMap<>(entries.size() * 4 / 3 + 1);
		for (Map.Entry<String, ? extends Object> entry : entries.entrySet()) {
			String key = Objects.requireNonNull(entry.getKey(), "metaData key");
			Object value = Objects.requireNonNull(entry.getValue(),
					entry.getValue() == null ? "metaData value for key: " + key : null);
			copy.put(key, value);
		}
		return Collections.unmodifiableMap(copy);
	}

	public static class Builder<T> {

		private Collection<T> records;

		private @Nullable Long total;

		private @Nullable Boolean success;

		private @Nullable Map<String, Object> metaData;

		private @Nullable String message;

		private @Nullable Class<?> jsonView;

		public Builder() {
			// default constructor
		}

		public EdStoreResult.Builder<T> from(EdStoreResult<T> instance) {
			Objects.requireNonNull(instance, "instance");
			this.records(instance.records());
			if (instance.total() != null) {
				this.total(instance.total());
			}
			if (instance.success() != null) {
				this.success(instance.success());
			}
			if (instance.metaData() != null) {
				this.metaData(instance.metaData());
			}
			if (instance.message() != null) {
				this.message(instance.message());
			}
			this.jsonView = instance.getJsonView();
			return (EdStoreResult.Builder<T>) this;
		}

		public EdStoreResult.Builder<T> records(Collection<T> records) {
			this.records = Objects.requireNonNull(records, "records");
			return (EdStoreResult.Builder<T>) this;
		}

		public EdStoreResult.Builder<T> total(@Nullable Long total) {
			this.total = total;
			return (EdStoreResult.Builder<T>) this;
		}

		public EdStoreResult.Builder<T> success(@Nullable Boolean success) {
			this.success = success;
			return (EdStoreResult.Builder<T>) this;
		}

		public EdStoreResult.Builder<T> putMetaData(String key, Object value) {
			if (this.metaData == null) {
				this.metaData = new LinkedHashMap<>();
			}
			this.metaData.put(Objects.requireNonNull(key, "metaData key"),
					Objects.requireNonNull(value, value == null ? "metaData value for key: " + key : null));
			return (EdStoreResult.Builder<T>) this;
		}

		public EdStoreResult.Builder<T> metaData(@Nullable Map<String, ? extends Object> entries) {
			if (entries == null) {
				this.metaData = null;
				return (EdStoreResult.Builder<T>) this;
			}
			this.metaData = new LinkedHashMap<>();
			return this.putAllMetaData(entries);
		}

		public EdStoreResult.Builder<T> putAllMetaData(Map<String, ? extends Object> entries) {
			if (this.metaData == null) {
				this.metaData = new LinkedHashMap<>();
			}
			for (Map.Entry<String, ? extends Object> entry : entries.entrySet()) {
				this.putMetaData(entry.getKey(), entry.getValue());
			}
			return (EdStoreResult.Builder<T>) this;
		}

		public EdStoreResult.Builder<T> message(@Nullable String message) {
			this.message = message;
			return (EdStoreResult.Builder<T>) this;
		}

		public ImmutableEdStoreResult<T> build() {
			if (this.records == null) {
				throw new IllegalStateException(
						"Cannot build EdStoreResult, some of required attributes are not set [records]");
			}
			ImmutableEdStoreResult<T> result = ImmutableEdStoreResult.of(this.records, this.total, this.success,
					this.metaData, this.message);
			result.setJsonView(this.jsonView);
			return result;
		}

	}

}