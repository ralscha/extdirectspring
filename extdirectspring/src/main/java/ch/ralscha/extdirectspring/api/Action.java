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
package ch.ralscha.extdirectspring.api;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.codehaus.jackson.map.annotate.JsonSerialize.Inclusion;

/**
 * @author Ralph Schaer
 */
@JsonSerialize(include = Inclusion.NON_NULL)
class Action {

	private final String name;
	private final Integer len;
	private final List<String> params;
	private final Boolean formHandler;

	public Action(final String name, final Integer len, final Boolean formHandler) {
		this.name = name;
		this.len = len;
		this.formHandler = formHandler;
		this.params = null;
	}

	public Action(final String name, List<String> params) {
		this.name = name;
		this.len = null;
		this.formHandler = null;
		this.params = new ArrayList<String>(params);
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

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((formHandler == null) ? 0 : formHandler.hashCode());
		result = prime * result + ((len == null) ? 0 : len.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((params == null) ? 0 : params.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		Action other = (Action) obj;
		if (formHandler == null) {
			if (other.formHandler != null) {
				return false;
			}
		} else if (!formHandler.equals(other.formHandler)) {
			return false;
		}
		if (len == null) {
			if (other.len != null) {
				return false;
			}
		} else if (!len.equals(other.len)) {
			return false;
		}
		if (name == null) {
			if (other.name != null) {
				return false;
			}
		} else if (!name.equals(other.name)) {
			return false;
		}
		if (params == null) {
			if (other.params != null) {
				return false;
			}
		} else if (!params.equals(other.params)) {
			return false;
		}
		return true;
	}

}
