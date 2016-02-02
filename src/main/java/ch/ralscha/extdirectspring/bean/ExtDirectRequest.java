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

import java.util.Map;

/**
 * Represents the request of an Ext Direct call. Internal class.
 */
public class ExtDirectRequest {

	private String action;

	private String method;

	private String type;

	private int tid;

	private Object data;

	private Map<String, Object> metadata;

	/**
	 * @return name of the spring managed bean
	 */
	public String getAction() {
		return this.action;
	}

	public void setAction(String action) {
		this.action = action;
	}

	/**
	 * @return payload of the request
	 */
	public Object getData() {
		return this.data;
	}

	public void setData(Object data) {
		this.data = data;
	}

	/**
	 * @return method name that is member of the spring managed bean
	 */
	public String getMethod() {
		return this.method;
	}

	public void setMethod(String method) {
		this.method = method;
	}

	/**
	 * @return the transaction ID that is associated with this request. The response has
	 * to return the same tid
	 */
	public int getTid() {
		return this.tid;
	}

	public void setTid(int tid) {
		this.tid = tid;
	}

	/**
	 * @return the type of the message. "event" for polling or "rpc" for a method call
	 */
	public String getType() {
		return this.type;
	}

	public void setType(String type) {
		this.type = type;
	}

	/**
	 * @return metadata parameters
	 */
	public Map<String, Object> getMetadata() {
		return this.metadata;
	}

	public void setMetadata(Map<String, Object> metadata) {
		this.metadata = metadata;
	}

	@Override
	public String toString() {
		return "ExtDirectRequest [action=" + this.action + ", method=" + this.method
				+ ", type=" + this.type + ", tid=" + this.tid + ", data=" + this.data
				+ ", metadata=" + this.metadata + "]";
	}

}
