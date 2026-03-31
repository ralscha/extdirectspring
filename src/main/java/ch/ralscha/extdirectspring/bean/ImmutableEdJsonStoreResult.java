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
import java.util.Objects;

import org.jspecify.annotations.Nullable;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import tools.jackson.databind.annotation.JsonSerialize;

public final class ImmutableEdJsonStoreResult extends EdJsonStoreResult {

	private final Collection<String> records;

	private final @Nullable Long total;

	private final @Nullable Boolean success;

	private final @Nullable MetaData metaData;

	private final @Nullable String message;

	@JsonCreator
	private ImmutableEdJsonStoreResult(@JsonProperty(value = "records", required = true) Collection<String> records,
			@JsonProperty("total") @Nullable Long total, @JsonProperty("success") @Nullable Boolean success,
			@JsonProperty("metaData") @Nullable MetaData metaData, @JsonProperty("message") @Nullable String message) {
		this.records = Objects.requireNonNull(records, "records");
		this.total = total;
		this.success = success;
		this.metaData = metaData;
		this.message = message;
	}

	public static ImmutableEdJsonStoreResult of(Collection<String> records, @Nullable Long total,
			@Nullable Boolean success, @Nullable MetaData metaData, @Nullable String message) {
		return new ImmutableEdJsonStoreResult(records, total, success, metaData, message);
	}

	public static ImmutableEdJsonStoreResult copyOf(EdJsonStoreResult instance) {
		if (instance instanceof ImmutableEdJsonStoreResult immutable) {
			return immutable;
		}
		return new EdJsonStoreResult.Builder().from(instance).build();
	}

	@JsonProperty(value = "records", required = true)
	@JsonSerialize(using = EdJsonStoreResult.CollectionStringSerializer.class)
	@Override
	public Collection<String> records() {
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
	public @Nullable MetaData metaData() {
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
		if (!(obj instanceof ImmutableEdJsonStoreResult other)) {
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
		return "EdJsonStoreResult{" + "records=" + this.records + ", total=" + this.total + ", success=" + this.success
				+ ", metaData=" + this.metaData + ", message=" + this.message + '}';
	}

	public static class Builder {

		private Collection<String> records;

		private @Nullable Long total;

		private @Nullable Boolean success;

		private @Nullable MetaData metaData;

		private @Nullable String message;

		private @Nullable Class<?> jsonView;

		public Builder() {
			// default constructor
		}

		public EdJsonStoreResult.Builder from(EdJsonStoreResult instance) {
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
			return (EdJsonStoreResult.Builder) this;
		}

		public EdJsonStoreResult.Builder records(Collection<String> records) {
			this.records = Objects.requireNonNull(records, "records");
			return (EdJsonStoreResult.Builder) this;
		}

		public EdJsonStoreResult.Builder total(@Nullable Long total) {
			this.total = total;
			return (EdJsonStoreResult.Builder) this;
		}

		public EdJsonStoreResult.Builder success(@Nullable Boolean success) {
			this.success = success;
			return (EdJsonStoreResult.Builder) this;
		}

		public EdJsonStoreResult.Builder metaData(@Nullable MetaData metaData) {
			this.metaData = metaData;
			return (EdJsonStoreResult.Builder) this;
		}

		public EdJsonStoreResult.Builder message(@Nullable String message) {
			this.message = message;
			return (EdJsonStoreResult.Builder) this;
		}

		public ImmutableEdJsonStoreResult build() {
			if (this.records == null) {
				throw new IllegalStateException(
						"Cannot build EdJsonStoreResult, some of required attributes are not set [records]");
			}
			ImmutableEdJsonStoreResult result = ImmutableEdJsonStoreResult.of(this.records, this.total, this.success,
					this.metaData, this.message);
			result.setJsonView(this.jsonView);
			return result;
		}

	}

}