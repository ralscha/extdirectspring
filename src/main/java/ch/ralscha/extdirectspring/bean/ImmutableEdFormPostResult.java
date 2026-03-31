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

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public final class ImmutableEdFormPostResult extends EdFormPostResult {

	private final Map<String, Object> result;

	@JsonCreator
	private ImmutableEdFormPostResult(@JsonProperty("result") Map<String, Object> result) {
		this.result = createUnmodifiableMap(result == null ? Collections.emptyMap() : result);
	}

	public static ImmutableEdFormPostResult of(Map<String, ? extends Object> result) {
		return new ImmutableEdFormPostResult(copyNullableMap(result));
	}

	public static Builder builder() {
		return new Builder();
	}

	public static ImmutableEdFormPostResult copyOf(EdFormPostResult instance) {
		if (instance instanceof ImmutableEdFormPostResult immutable) {
			return immutable;
		}
		return builder().from(instance).build();
	}

	@JsonProperty("result")
	@Override
	public Map<String, Object> result() {
		return this.result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!(obj instanceof ImmutableEdFormPostResult other)) {
			return false;
		}
		return this.result.equals(other.result);
	}

	@Override
	public int hashCode() {
		return this.result.hashCode();
	}

	@Override
	public String toString() {
		return "EdFormPostResult{" + "result=" + this.result + '}';
	}

	private static Map<String, Object> createUnmodifiableMap(Map<String, ? extends Object> entries) {
		if (entries.isEmpty()) {
			return Collections.emptyMap();
		}
		Map<String, Object> copy = new LinkedHashMap<>(entries.size() * 4 / 3 + 1);
		for (Map.Entry<String, ? extends Object> entry : entries.entrySet()) {
			String key = Objects.requireNonNull(entry.getKey(), "result key");
			Object value = Objects.requireNonNull(entry.getValue(),
					entry.getValue() == null ? "result value for key: " + key : null);
			copy.put(key, value);
		}
		return Collections.unmodifiableMap(copy);
	}

	private static Map<String, Object> copyNullableMap(Map<String, ? extends Object> entries) {
		Map<String, Object> copy = new LinkedHashMap<>(entries.size());
		for (Map.Entry<String, ? extends Object> entry : entries.entrySet()) {
			copy.put(entry.getKey(), entry.getValue());
		}
		return copy;
	}

	public static final class Builder extends EdFormPostResult.Builder {

		private final Map<String, Object> result = new LinkedHashMap<>();

		private Builder() {
			// use factory method
		}

		public Builder from(EdFormPostResult instance) {
			Objects.requireNonNull(instance, "instance");
			this.putAllResult(instance.result());
			return this;
		}

		@Override
		public Builder result(Map<String, ? extends Object> entries) {
			this.result.clear();
			return this.putAllResult(entries);
		}

		@Override
		public Builder putResult(String key, Object value) {
			this.result.put(Objects.requireNonNull(key, "result key"),
					Objects.requireNonNull(value, value == null ? "result value for key: " + key : null));
			return this;
		}

		public Builder putAllResult(Map<String, ? extends Object> entries) {
			for (Map.Entry<String, ? extends Object> entry : entries.entrySet()) {
				this.putResult(entry.getKey(), entry.getValue());
			}
			return this;
		}

		@Override
		public ImmutableEdFormPostResult build() {
			return ImmutableEdFormPostResult.of(this.result);
		}

	}

}