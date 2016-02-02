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

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonRawValue;

/**
 * Represents the response of a Ext Direct call with a raw (json) result. Internal class
 */
@JsonInclude(Include.NON_NULL)
public class ExtDirectResponseRaw extends BaseResponse {

	private final int tid;

	private final String action;

	private final String method;

	@JsonRawValue
	private final String result;

	public ExtDirectResponseRaw(ExtDirectResponse response, String result) {
		this.action = response.getAction();
		this.method = response.getMethod();
		this.tid = response.getTid();
		setType(response.getType());
		this.result = result;
	}

	public int getTid() {
		return this.tid;
	}

	public String getAction() {
		return this.action;
	}

	public String getMethod() {
		return this.method;
	}

	public String getResult() {
		return this.result;
	}

}
