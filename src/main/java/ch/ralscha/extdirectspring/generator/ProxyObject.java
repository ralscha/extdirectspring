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
package ch.ralscha.extdirectspring.generator;

import org.springframework.util.StringUtils;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonRawValue;

/**
 * Internal class used by the {@link ModelGenerator} to serialize the model code
 * 
 * @author Ralph Schaer
 */
@JsonInclude(Include.NON_NULL)
@JsonAutoDetect(fieldVisibility = Visibility.ANY)
@SuppressWarnings("unused")
class ProxyObject {
	private final String type = "direct";

	private String idParam;

	@JsonRawValue
	private String directFn;

	private ApiObject api;

	private ReaderObject reader;

	public ProxyObject(String idParam, String read, String create, String update, String destroy, boolean paging) {

		if (StringUtils.hasText(idParam) && !idParam.equals("id")) {
			this.idParam = idParam;
		}

		boolean hasApiMethods = false;
		ApiObject apiObject = new ApiObject();

		if (StringUtils.hasText(create)) {
			hasApiMethods = true;
			apiObject.create = create;
		}
		if (StringUtils.hasText(update)) {
			hasApiMethods = true;
			apiObject.update = update;
		}
		if (StringUtils.hasText(destroy)) {
			hasApiMethods = true;
			apiObject.destroy = destroy;
		}

		if (StringUtils.hasText(read)) {
			if (hasApiMethods) {
				apiObject.read = read;
			} else {
				this.directFn = read;
			}
		}

		if (hasApiMethods) {
			this.api = apiObject;
		}

		if (paging) {
			this.reader = new ReaderObject();
		}
	}

	@JsonAutoDetect(fieldVisibility = Visibility.ANY)
	private final class ApiObject {
		@JsonRawValue
		private String read;

		@JsonRawValue
		private String create;

		@JsonRawValue
		private String update;

		@JsonRawValue
		private String destroy;
	}

	@JsonAutoDetect(fieldVisibility = Visibility.ANY)
	private final class ReaderObject {
		private final String root = "records";
	}

	public boolean hasMethods() {
		return api != null || directFn != null;
	}
}
