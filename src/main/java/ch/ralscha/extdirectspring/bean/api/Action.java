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
package ch.ralscha.extdirectspring.bean.api;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
public class Action {

	private final String name;

	private final Integer len;

	private final Boolean batched;

	private final List<String> params;

	private final Boolean formHandler;

	private final Boolean strict;

	private final Metadata metadata;

	public static Action create(String name, Integer len) {
		return new Action(name, len, null, null, null, null, null);
	}

	public static Action create(String name, Integer len, Boolean batched) {
		return new Action(name, len, batched, null, null, null, null);
	}

	public static Action createFormHandler(String name, Integer len) {
		return new Action(name, len, null, Boolean.TRUE, null, null, null);
	}

	public static Action createTreeLoad(String name, Integer len,
			List<String> metadataParams, Boolean batched) {
		return new Action(name, len, batched, null, null, null, metadataParams);
	}

	public static Action createNamed(String name, List<String> params, Boolean strict,
			Boolean batched) {
		return new Action(name, null, batched, null, params, strict, null);
	}

	Action(String name, Integer len, Boolean batched, Boolean formHandler,
			List<String> params, Boolean strict, List<String> metadataParams) {
		this.name = name;
		this.len = len;

		if (batched != null && !batched.booleanValue()) {
			this.batched = batched;
		}
		else {
			this.batched = null;
		}

		if (formHandler != null && formHandler.booleanValue()) {
			this.formHandler = formHandler;
		}
		else {
			this.formHandler = null;
		}

		if (params != null) {
			this.params = params;
		}
		else {
			this.params = null;
		}

		if (strict != null && !strict.booleanValue()) {
			this.strict = strict;
		}
		else {
			this.strict = null;
		}

		if (metadataParams != null && !metadataParams.isEmpty()) {
			this.metadata = new Metadata(metadataParams);
		}
		else {
			this.metadata = null;
		}
	}

	public Action(Action toCopy) {
		this.name = toCopy.name;
		this.len = toCopy.len;
		this.formHandler = toCopy.formHandler;
		this.params = toCopy.params;
		this.strict = toCopy.strict;
		this.metadata = toCopy.metadata;
		this.batched = toCopy.batched;
	}

	public Boolean getFormHandler() {
		return this.formHandler;
	}

	public Integer getLen() {
		return this.len;
	}

	public String getName() {
		return this.name;
	}

	public List<String> getParams() {
		return this.params;
	}

	public Boolean getStrict() {
		return this.strict;
	}

	public Metadata getMetadata() {
		return this.metadata;
	}

	public Boolean getBatched() {
		return this.batched;
	}

	class Metadata {
		@SuppressWarnings("hiding")
		private final List<String> params;

		Metadata(List<String> params) {
			this.params = params;
		}

		public List<String> getParams() {
			return this.params;
		}
	}
}
