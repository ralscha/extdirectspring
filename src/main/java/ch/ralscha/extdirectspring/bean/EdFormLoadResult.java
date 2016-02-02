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

import org.immutables.value.Value;
import org.immutables.value.Value.Style.ImplementationVisibility;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

/**
 * Represents the result of a FORM_LOAD method call.
 */
@JsonSerialize(as = ImmutableEdFormLoadResult.class)
@JsonDeserialize(as = ImmutableEdFormLoadResult.class)
@Value.Style(visibility = ImplementationVisibility.PACKAGE)
@Value.Immutable
public abstract class EdFormLoadResult extends JsonViewHint {

	@Value.Parameter
	@Nullable
	public abstract Object data();

	@Value.Parameter
	public abstract boolean success();

	public static EdFormLoadResult success(Object data) {
		return ImmutableEdFormLoadResult.of(data, true);
	}

	public static EdFormLoadResult fail(Object data) {
		return ImmutableEdFormLoadResult.of(data, false);
	}

}
