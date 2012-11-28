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
 * Superclass for response object that are sent to an Ext Direct client.
 * 
 * @see ExtDirectPollResponse
 * @see ExtDirectResponse
 * 
 */
public class BaseResponse {

	private String type;

	private String message;

	private String where;

	public String getType() {
		return type;
	}

	/**
	 * Sets the type of the response. Valid values are: <br>
	 * "exception": when an error occurred on the server side <br>
	 * "event": response from a polling method <br>
	 * "rpc": response from a remote method call
	 * 
	 * @param type the new type of the response
	 */
	public void setType(String type) {
		this.type = type;
	}

	public String getMessage() {
		return message;
	}

	/**
	 * Sets an error message if type is "exception". In all other cases this
	 * should not be called and message should be null.
	 * 
	 * @param message an error message
	 */
	public void setMessage(String message) {
		this.message = message;
	}

	public String getWhere() {
		return where;
	}

	/**
	 * Contains a detailed description (stacktrace) of the error if type is
	 * "exception" and sendStacktrace is set to true in {@link Configuration}.
	 * 
	 * @param where contains an detailed error description
	 * 
	 * @see Configuration#setSendStacktrace(boolean)
	 */
	public void setWhere(String where) {
		this.where = where;
	}

}