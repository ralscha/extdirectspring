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

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;

import org.immutables.value.Value;
import org.immutables.value.Value.Style.ImplementationVisibility;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

/**
 * Class representing the result of STORE_READ and STORE_MODIFY methods.
 *
 * @param <T> Type of the entry inside the collection
 */
@JsonInclude(Include.NON_NULL)
@JsonSerialize(as = ImmutableEdStoreResult.class)
@JsonDeserialize(as = ImmutableEdStoreResult.class)
@JsonPropertyOrder(value = { "metaData", "success", "total", "records", "message" })
@Value.Style(visibility = ImplementationVisibility.PACKAGE)
@Value.Immutable
public abstract class EdStoreResult<T> extends JsonViewHint {

	@Value.Parameter
	public abstract Collection<T> records();

	@Nullable
	@Value.Parameter
	public abstract Long total();

	@Nullable
	@Value.Parameter
	public abstract Boolean success();

	@Nullable
	@Value.Parameter
	public abstract Map<String, Object> metaData();

	@Nullable
	@Value.Parameter
	public abstract String message();

	public static <T> EdStoreResult<T> success(T record) {
		return ImmutableEdStoreResult.of(Collections.singletonList(record), null,
				Boolean.TRUE, null, null);
	}

	public static <T> EdStoreResult<T> success(T[] records) {
		return ImmutableEdStoreResult.of(Arrays.asList(records), null, Boolean.TRUE, null,
				null);
	}

	public static <T> EdStoreResult<T> success(Collection<T> records) {
		return ImmutableEdStoreResult.of(records, null, Boolean.TRUE, null, null);
	}

	public static <T> EdStoreResult<T> success(Collection<T> records, Long total) {
		return ImmutableEdStoreResult.of(records, total, Boolean.TRUE, null, null);
	}

	public static <T> EdStoreResult<T> success(Collection<T> records, Class<?> jsonView) {
		ImmutableEdStoreResult<T> result = ImmutableEdStoreResult.of(records, null,
				Boolean.TRUE, null, null);
		result.setJsonView(jsonView);
		return result;
	}

	public static <T> EdStoreResult<T> success(Collection<T> records, Long total,
			Class<?> jsonView) {
		ImmutableEdStoreResult<T> result = ImmutableEdStoreResult.of(records, total,
				Boolean.TRUE, null, null);
		result.setJsonView(jsonView);
		return result;
	}

	public static <T> Builder<T> builder() {
		return new Builder<T>();
	}

	public static final class Builder<T> extends ImmutableEdStoreResult.Builder<T> {
		// nothing here
	}

}
