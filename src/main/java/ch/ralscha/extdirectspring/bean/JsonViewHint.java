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

import com.fasterxml.jackson.annotation.JsonIgnore;

import ch.ralscha.extdirectspring.annotation.ExtDirectMethod;

/**
 * Specifies a Json View (filter) that Jackson uses to serialize the response. A model
 * class can subclass this class and set jsonView.
 * <p>
 * If the property jsonView is set it overrides a jsonView specified on
 * {@link ExtDirectMethod#jsonView()}.
 * <p>
 * To disable a JsonView specified on {@link ExtDirectMethod#jsonView()} set the property
 * jsonView to {@link ch.ralscha.extdirectspring.annotation.ExtDirectMethod.NoJsonView}.
 */
public class JsonViewHint {

	@JsonIgnore
	private Class<?> jsonView;

	public JsonViewHint() {
		// default constructor
	}

	public JsonViewHint(Class<?> jsonView) {
		this.jsonView = jsonView;
	}

	public Class<?> getJsonView() {
		return this.jsonView;
	}

	public void setJsonView(Class<?> jsonView) {
		this.jsonView = jsonView;
	}

}
