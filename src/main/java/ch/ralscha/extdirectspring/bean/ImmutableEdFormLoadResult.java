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

import java.util.Objects;

import org.jspecify.annotations.Nullable;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public final class ImmutableEdFormLoadResult extends EdFormLoadResult {

	private final @Nullable Object data;

	private final boolean success;

	@JsonCreator
	private ImmutableEdFormLoadResult(@JsonProperty("data") @Nullable Object data,
			@JsonProperty(value = "success", required = true) boolean success) {
		this.data = data;
		this.success = success;
	}

	public static ImmutableEdFormLoadResult of(@Nullable Object data, boolean success) {
		return new ImmutableEdFormLoadResult(data, success);
	}

	public static ImmutableEdFormLoadResult copyOf(EdFormLoadResult instance) {
		if (instance instanceof ImmutableEdFormLoadResult immutable) {
			return immutable;
		}
		ImmutableEdFormLoadResult result = new ImmutableEdFormLoadResult(instance.data(), instance.success());
		result.setJsonView(instance.getJsonView());
		return result;
	}

	@JsonProperty("data")
	@Override
	public @Nullable Object data() {
		return this.data;
	}

	@JsonProperty(value = "success", required = true)
	@Override
	public boolean success() {
		return this.success;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!(obj instanceof ImmutableEdFormLoadResult other)) {
			return false;
		}
		return Objects.equals(this.data, other.data) && this.success == other.success;
	}

	@Override
	public int hashCode() {
		return Objects.hash(this.data, this.success);
	}

	@Override
	public String toString() {
		return "EdFormLoadResult{" + "data=" + this.data + ", success=" + this.success + '}';
	}

}