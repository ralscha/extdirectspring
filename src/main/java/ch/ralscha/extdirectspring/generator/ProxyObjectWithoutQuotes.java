/**
 * Copyright 2010-2013 Ralph Schaer <ralphschaer@gmail.com>
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

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.springframework.util.StringUtils;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonRawValue;

/**
 * Internal class used by the {@link ModelGenerator} to serialize the model code
 */
@JsonInclude(Include.NON_NULL)
@JsonAutoDetect(fieldVisibility = Visibility.ANY)
@SuppressWarnings("unused")
class ProxyObjectWithoutQuotes {
	private final String type = "direct";

	private String idParam;

	@JsonRawValue
	private Object pageParam = null;

	@JsonRawValue
	private Object startParam = null;

	@JsonRawValue
	private Object limitParam = null;

	@JsonRawValue
	private String directFn;

	private ApiObject api;

	private Map<String, String> reader;

	public ProxyObjectWithoutQuotes(ModelBean model, OutputConfig config) {
		if (StringUtils.hasText(model.getIdProperty()) && !model.getIdProperty().equals("id")) {
			this.idParam = model.getIdProperty();
		}

		if (model.isDisablePagingParameters()) {
			Object value = config.getOutputFormat() == OutputFormat.EXTJS4 ? "undefined" : false;
			pageParam = value;
			startParam = value;
			limitParam = value;
		}

		boolean hasApiMethods = false;
		ApiObject apiObject = new ApiObject();

		if (StringUtils.hasText(model.getCreateMethod())) {
			hasApiMethods = true;
			apiObject.create = model.getCreateMethod();
		}
		if (StringUtils.hasText(model.getUpdateMethod())) {
			hasApiMethods = true;
			apiObject.update = model.getUpdateMethod();
		}
		if (StringUtils.hasText(model.getDestroyMethod())) {
			hasApiMethods = true;
			apiObject.destroy = model.getDestroyMethod();
		}

		if (StringUtils.hasText(model.getReadMethod())) {
			if (hasApiMethods) {
				apiObject.read = model.getReadMethod();
			} else {
				this.directFn = model.getReadMethod();
			}
		}

		if (hasApiMethods) {
			this.api = apiObject;
		}

		if (model.isPaging()) {
			String rootPropertyName = config.getOutputFormat() == OutputFormat.EXTJS4 ? "root" : "rootProperty";
			if (StringUtils.hasText(model.getMessageProperty())) {
				this.reader = new HashMap<String, String>();
				this.reader.put(rootPropertyName, "records");
				this.reader.put("messageProperty", model.getMessageProperty());
			} else {
				this.reader = Collections.singletonMap(rootPropertyName, "records");
			}
		} else if (StringUtils.hasText(model.getMessageProperty())) {
			this.reader = Collections.singletonMap("messageProperty", model.getMessageProperty());
		}
	}

	@JsonAutoDetect(fieldVisibility = Visibility.ANY)
	@JsonInclude(Include.NON_NULL)
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

	public boolean hasMethods() {
		return api != null || directFn != null;
	}
}
