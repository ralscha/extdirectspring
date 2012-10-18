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

import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.util.Assert;

import com.fasterxml.jackson.annotation.JsonAnyGetter;

/**
 * Represents one field in a {@link ModelBean}
 * 
 * @author Ralph Schaer
 */
public class ModelFieldValidationBean {
	private final String type;

	private final String field;

	private Map<String, Object> options = new LinkedHashMap<String, Object>();

	public ModelFieldValidationBean(String type, String field, Map<String, Object> options) {

		Assert.notNull(type, "type must not be null");
		Assert.notNull(field, "field must not be null");

		this.type = type;
		this.field = field;

		if (options != null) {
			this.options.putAll(options);
		}
	}

	public ModelFieldValidationBean(String type, String field) {
		this(type, field, null);
	}

	public String getType() {
		return type;
	}

	public String getField() {
		return field;
	}

	@JsonAnyGetter
	public Map<String, Object> getOptions() {
		return options;
	}

	public void setOptions(Map<String, Object> options) {
		this.options = options;
	}

	public void addOption(String name, Object value) {
		Assert.notNull(name, "name must not be null");
		Assert.notNull(value, "value must not be null");

		options.put(name, value);
	}

}
