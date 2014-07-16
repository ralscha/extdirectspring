/**
 * Copyright 2010-2014 Ralph Schaer <ralphschaer@gmail.com>
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
 * Class represents a response in a Server-Sent Event roundtrip. A SSE method can either
 * return a String or an instance of this class.
 * <p>
 * The library maps every property to the corresponding keyword in a Server-Sent Event
 * response. If a property in this class is null it will be ignored.
 * <p>
 * See <a href="http://www.w3.org/TR/eventsource/">Server-Sent Specification</a>
 */
public class SSEvent {

	private String id;

	private String data;

	private Integer retry;

	private String event;

	private String comment;

	public String getId() {
		return id;
	}

	/**
	 * Sets the event source's last event ID. Next time the client reconnects to the
	 * server it will send a HTTP Header Last-Event-ID with the current last event ID.
	 * This value does not have to be a number it can be any string.
	 *
	 * @param id new event source's last event id
	 */
	public void setId(String id) {
		this.id = id;
	}

	public String getData() {
		return data;
	}

	/**
	 * Sets the data part of the Server-Sent Event response.
	 *
	 * @param data the actual payload of the response
	 */
	public void setData(String data) {
		this.data = data;
	}

	/**
	 * Sets the data part of the Server-Sent Event response.
	 *
	 * @param data the actual payload of the response. Converted to a String by calling
	 * the object's toString() method. If null data is set to null.
	 */
	public void setData(Object data) {
		if (data != null) {
			this.data = data.toString();
		}
		else {
			this.data = null;
		}
	}

	public Integer getRetry() {
		return retry;
	}

	/**
	 * Sets the retry value. This specifies how long the client is waiting after a
	 * disconnect before he tries to open a new connection to the server.
	 * <p>
	 * Default value is 3000 (3 seconds).
	 *
	 * @param retry the new retry value in milliseconds
	 */
	public void setRetry(Integer retry) {
		this.retry = retry;
	}

	public String getEvent() {
		return event;
	}

	/**
	 * Name of the event. On the client side the corresponding event is fired after
	 * receiving this response. If empty the default event 'message' will be fired
	 *
	 * @param event the new name of the event
	 */
	public void setEvent(String event) {
		this.event = event;
	}

	public String getComment() {
		return comment;
	}

	/**
	 * Adds a comment to this event. In the response this is a line starting with :
	 * (colon) following by the text from this property. Comments will be ignored by the
	 * client but may be useful for debugging.
	 *
	 * @param comment the new comment for this event
	 */
	public void setComment(String comment) {
		this.comment = comment;
	}

}
