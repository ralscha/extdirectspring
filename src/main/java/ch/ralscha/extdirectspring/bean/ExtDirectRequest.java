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
package ch.ralscha.extdirectspring.bean;

/**
 * Class representing the request of a Ext Direct call.
 * 
 * @author mansari
 * @author Ralph Schaer
 */
public class ExtDirectRequest {

	private String action;

	private String method;

	private String type;

	private int tid;

	private Object data;

	/**
	 * @return name of the spring managed bean
	 */
	public String getAction() {
		return action;
	}

	public void setAction(final String action) {
		this.action = action;
	}

	/**
	 * @return payload of the request
	 */
	public Object getData() {
		return data;
	}

	public void setData(final Object data) {
		this.data = data;
	}

	/**
	 * @return method name that is member of the spring managed bean
	 */
	public String getMethod() {
		return method;
	}

	public void setMethod(final String method) {
		this.method = method;
	}

	/**
	 * @return the transaction ID that is associated with this request. The
	 * response has to return the same tid
	 */
	public int getTid() {
		return tid;
	}

	public void setTid(final int tid) {
		this.tid = tid;
	}

	/**
	 * @return the type of the message. "event" for polling or "rpc" for a
	 * method call
	 */
	public String getType() {
		return type;
	}

	public void setType(final String type) {
		this.type = type;
	}

	@Override
	public String toString() {
		return "ExtDirectRequest [action=" + action + ", method=" + method + ", type=" + type + ", tid=" + tid
				+ ", data=" + data + "]";
	}

}
