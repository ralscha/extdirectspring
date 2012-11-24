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
package ch.ralscha.extdirectspring.bean.api;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

/**
 * @author Ralph Schaer
 */
@JsonInclude(Include.NON_NULL)
public class Action {

	private final String name;

	private final Integer len;

	private final List<String> params;

	private final Boolean formHandler;

	public Action(String name, Integer len, Boolean formHandler) {
		this.name = name;
		this.len = len;
		this.formHandler = formHandler;
		this.params = null;
	}

	public Action(String name, List<String> params) {
		this.name = name;
		this.len = null;
		this.formHandler = null;
		this.params = new ArrayList<String>(params);
	}

	public Action(Action toCopy) {
		this.name = toCopy.name;
		this.len = toCopy.len;
		this.formHandler = toCopy.formHandler;
		this.params = toCopy.params;
	}

	public Boolean isFormHandler() {
		return formHandler;
	}

	public Integer getLen() {
		return len;
	}

	public String getName() {
		return name;
	}

	public List<String> getParams() {
		if (params != null) {
			return Collections.unmodifiableList(params);
		}
		return null;
	}

}
