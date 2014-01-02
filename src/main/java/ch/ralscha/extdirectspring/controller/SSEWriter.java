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
package ch.ralscha.extdirectspring.controller;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

import org.springframework.http.MediaType;
import org.springframework.util.StringUtils;

import ch.ralscha.extdirectspring.bean.SSEvent;
import ch.ralscha.extdirectspring.util.ExtDirectSpringUtil;

/**
 * This class allows you to send server sent events in a streaming fashion. Add
 * this class as a parameter to the method and send {@link SSEvent} with
 * {@link #write(SSEvent)} to the client.
 * <p>
 * Example:
 * 
 * <pre>
 *  {@literal @}ExtDirectMethod(ExtDirectMethodType.SSE)
 *  public void sse(SSEWriter sseWriter) {
 *    SSEvent event = new SSEvent();
 *    event.set....	    
 *    sseWriter.write(event);
 *    //do something else or wait for something or ...
 *    sseWriter.write(event);
 *    //...
 *  }
 * </pre>
 */
public class SSEWriter {
	private final HttpServletResponse response;

	private static final MediaType EVENT_STREAM = new MediaType("text", "event-stream",
			ExtDirectSpringUtil.UTF8_CHARSET);

	public SSEWriter(HttpServletResponse response) {
		this.response = response;

		response.setContentType(EVENT_STREAM.toString());
		response.setCharacterEncoding(EVENT_STREAM.getCharSet().name());
	}

	/**
	 * Creates a {@link SSEvent} object and sets the data property to the
	 * provided parameter. Then it writes the event into the servlet output
	 * stream and flushes the response.
	 * 
	 * @param data the value that becomes the data part of the {@link SSEvent}.
	 *            If null nothing is written.
	 * @throws IOException
	 */
	public void write(Object data) throws IOException {
		if (data != null) {
			SSEvent sseEvent = new SSEvent();
			sseEvent.setData(data.toString());
			write(sseEvent);
		}
	}

	/**
	 * Writes the event into the servlet output stream and flushes the response.
	 * The method does not close the output stream.
	 * 
	 * @param sseEvent the event object
	 * @throws IOException
	 */
	public void write(SSEvent sseEvent) throws IOException {
		StringBuilder sb = new StringBuilder(32);

		if (StringUtils.hasText(sseEvent.getComment())) {
			for (String line : sseEvent.getComment().split("\\r?\\n|\\r")) {
				sb.append(":").append(line).append("\n");
			}
		}

		if (StringUtils.hasText(sseEvent.getId())) {
			sb.append("id:").append(sseEvent.getId()).append("\n");
		}

		if (StringUtils.hasText(sseEvent.getEvent())) {
			sb.append("event:").append(sseEvent.getEvent()).append("\n");
		}

		if (StringUtils.hasText(sseEvent.getData())) {
			for (String line : sseEvent.getData().split("\\r?\\n|\\r")) {
				sb.append("data:").append(line).append("\n");
			}
		}

		if (sseEvent.getRetry() != null) {
			sb.append("retry:").append(sseEvent.getRetry()).append("\n");
		}

		sb.append("\n");
		response.getOutputStream().write(sb.toString().getBytes(ExtDirectSpringUtil.UTF8_CHARSET));
		response.getOutputStream().flush();

	}

}
