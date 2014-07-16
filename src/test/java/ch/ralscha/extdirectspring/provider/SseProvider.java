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
package ch.ralscha.extdirectspring.provider;

import static org.fest.assertions.api.Assertions.assertThat;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

import ch.ralscha.extdirectspring.annotation.ExtDirectMethod;
import ch.ralscha.extdirectspring.annotation.ExtDirectMethodType;
import ch.ralscha.extdirectspring.bean.SSEvent;
import ch.ralscha.extdirectspring.controller.SSEWriter;

@Service
public class SseProvider {

	@ExtDirectMethod(value = ExtDirectMethodType.SSE, event = "message1",
			group = "group2")
	public String message1() {
		Date now = new Date();
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd 'at' hh:mm:ss");
		return "Successfully polled at: " + formatter.format(now);
	}

	@ExtDirectMethod(value = ExtDirectMethodType.SSE, group = "group2",
			entryClass = String.class)
	public SSEvent message2(HttpServletResponse response, HttpServletRequest request,
			HttpSession session, Locale locale) {
		assertThat(response).isNotNull();
		assertThat(request).isNotNull();
		assertThat(session).isNotNull();
		assertThat(locale).isEqualTo(Locale.ENGLISH);

		Date now = new Date();
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd 'at' hh:mm:ss");

		SSEvent event = new SSEvent();
		event.setRetry(200000);
		event.setData("Successfully polled at: " + formatter.format(now));
		return event;
	}

	@ExtDirectMethod(value = ExtDirectMethodType.SSE, group = "group4")
	public String message3(Locale locale, @RequestParam(value = "id") int id) {
		assertThat(locale).isEqualTo(Locale.ENGLISH);
		return "Result: " + id;
	}

	@ExtDirectMethod(value = ExtDirectMethodType.SSE, synchronizeOnSession = true)
	public int message4(@RequestParam(value = "id", defaultValue = "1") int id,
			HttpServletRequest request) {
		assertThat(request).isNotNull();
		return id * 2;
	}

	@ExtDirectMethod(value = ExtDirectMethodType.SSE, group = "group3")
	public Integer message5(@RequestParam(value = "id", required = false) Integer id,
			String dummy) {
		assertThat(dummy).isNull();
		if (id != null) {
			return id * 2;
		}
		return null;
	}

	@ExtDirectMethod(value = ExtDirectMethodType.SSE, group = "group2")
	public String message6() {
		Date now = new Date();
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd 'at' hh:mm:ss");
		return "Successfully polled at: " + formatter.format(now);
	}

	/* Request Header */

	@ExtDirectMethod(value = ExtDirectMethodType.SSE, group = "group5")
	public SSEvent message7(@RequestParam(value = "id", required = false) Integer id,
			String dummy, @RequestHeader String header) {
		SSEvent event = new SSEvent();
		event.setId("1");
		event.setData(id + ";" + dummy + ";" + header);
		return event;
	}

	@ExtDirectMethod(value = ExtDirectMethodType.SSE, group = "group5",
			synchronizeOnSession = true)
	public SSEvent message8(@RequestParam Integer id, @RequestHeader(
			value = "anotherName", required = true) String header) {
		SSEvent event = new SSEvent();
		event.setData(id + ";" + header);
		return event;
	}

	@ExtDirectMethod(value = ExtDirectMethodType.SSE, group = "group5")
	public SSEvent message9(@RequestHeader(value = "anotherName",
			defaultValue = "default") String header) {
		SSEvent event = new SSEvent();
		event.setEvent("message9");
		event.setData(header);
		return event;
	}

	@ExtDirectMethod(value = ExtDirectMethodType.SSE, group = "group5")
	public SSEvent message10(
			@RequestHeader(defaultValue = "default", required = false) String header) {
		SSEvent event = new SSEvent();
		event.setEvent("message10");
		event.setComment("comment of message " + header);
		event.setData(header);
		return event;
	}

	@ExtDirectMethod(value = ExtDirectMethodType.SSE, group = "group5")
	public SSEvent message11(
			@RequestHeader(defaultValue = "default1", required = false) String header1,
			@RequestParam(value = "id", required = false) Integer id, @RequestHeader(
					defaultValue = "default2", required = false) String header2,
			@RequestHeader(value = "last") String header3) {

		SSEvent event = new SSEvent();
		event.setEvent("message11");
		event.setComment("comment of message " + id);
		event.setId("122");
		event.setData(id + ";" + header1 + ";" + header2 + ";" + header3);
		return event;
	}

	@ExtDirectMethod(value = ExtDirectMethodType.SSE, group = "group5")
	public SSEvent message12(@RequestHeader Integer intHeader,
			@RequestHeader Boolean booleanHeader) {
		SSEvent event = new SSEvent();
		event.setComment("comment");
		event.setEvent("message12");
		event.setId("123");
		event.setRetry(10000);
		event.setData(intHeader + ";" + booleanHeader);
		return event;
	}

	@ExtDirectMethod(value = ExtDirectMethodType.SSE, group = "group5")
	public String message13(SSEWriter writer) throws IOException {
		SSEvent event = new SSEvent();
		event.setComment("first comment");
		event.setEvent("event");
		event.setId("1");
		event.setRetry(1000);
		event.setData("one");

		writer.write(event);

		event = new SSEvent();
		event.setComment("second comment");
		event.setEvent("event");
		event.setId("2");
		event.setRetry(1000);
		event.setData("two");

		writer.write(event);
		writer.write((String) null);
		writer.write("third");
		writer.write((String) null);
		writer.write("fourth");

		return "fifth";
	}

	@ExtDirectMethod(value = ExtDirectMethodType.SSE, group = "group5")
	public SSEvent message14(SSEWriter writer) throws IOException {
		writer.write(1);
		writer.write(2);

		SSEvent event = new SSEvent();
		event.setData(3);
		event.setRetry(0);
		event.setComment("the last message");
		event.setId("123");
		return event;
	}

	@ExtDirectMethod(value = ExtDirectMethodType.SSE, group = "group5")
	public void message15(SSEWriter writer) throws IOException {
		writer.write("A");
		writer.write("B");

		SSEvent event = new SSEvent();
		event.setData("C");
		event.setRetry(0);
		writer.write(event);

		event = new SSEvent();
		event.setRetry(10);
		writer.write(event);
	}
}