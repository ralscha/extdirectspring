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
package ch.ralscha.extdirectspring.provider;

import static org.assertj.core.api.Assertions.assertThat;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

import ch.ralscha.extdirectspring.annotation.ExtDirectMethod;
import ch.ralscha.extdirectspring.annotation.ExtDirectMethodType;

@Service
public class PollProvider {

	@ExtDirectMethod(value = ExtDirectMethodType.POLL, event = "message1",
			group = "group2")
	public String handleMessage1() {
		Date now = new Date();
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd 'at' hh:mm:ss");
		return "Successfully polled at: " + formatter.format(now);
	}

	@ExtDirectMethod(value = ExtDirectMethodType.POLL, event = "message2",
			group = "group2", entryClass = String.class)
	public String handleMessage2(HttpServletResponse response, HttpServletRequest request,
			HttpSession session, Locale locale) {
		assertThat(response).isNotNull();
		assertThat(request).isNotNull();
		assertThat(session).isNotNull();
		assertThat(locale).isEqualTo(Locale.ENGLISH);

		Date now = new Date();
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd 'at' hh:mm:ss");
		return "Successfully polled at: " + formatter.format(now);
	}

	@ExtDirectMethod(value = ExtDirectMethodType.POLL, event = "message3",
			group = "group4")
	public String handleMessage3(Locale locale, @RequestParam(value = "id") int id) {
		assertThat(locale).isEqualTo(Locale.ENGLISH);
		return "Result: " + id;
	}

	@ExtDirectMethod(value = ExtDirectMethodType.POLL, event = "message4",
			synchronizeOnSession = true)
	public int handleMessage4(@RequestParam(value = "id", defaultValue = "1") int id,
			HttpServletRequest request) {
		assertThat(request).isNotNull();
		return id * 2;
	}

	@ExtDirectMethod(value = ExtDirectMethodType.POLL, event = "message5",
			group = "group3")
	public Integer handleMessage5(
			@RequestParam(value = "id", required = false) Integer id, String dummy) {
		assertThat(dummy).isNull();
		if (id != null) {
			return id * 2;
		}
		return null;
	}

	@ExtDirectMethod(value = ExtDirectMethodType.POLL, group = "group2")
	public String message6() {
		Date now = new Date();
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd 'at' hh:mm:ss");
		return "Successfully polled at: " + formatter.format(now);
	}

	/* Request Header */

	@ExtDirectMethod(value = ExtDirectMethodType.POLL, group = "group5")
	public String messageRequestHeader1(
			@RequestParam(value = "id", required = false) Integer id, String dummy,
			@RequestHeader String header) {
		return id + ";" + dummy + ";" + header;
	}

	@ExtDirectMethod(value = ExtDirectMethodType.POLL, group = "group5",
			synchronizeOnSession = true)
	public String messageRequestHeader2(@RequestParam Integer id,
			@RequestHeader(value = "anotherName", required = true) String header) {
		return id + ";" + header;
	}

	@ExtDirectMethod(value = ExtDirectMethodType.POLL, group = "group5")
	public String messageRequestHeader3(@RequestHeader(value = "anotherName",
			defaultValue = "default") String header) {
		return header;
	}

	@ExtDirectMethod(value = ExtDirectMethodType.POLL, group = "group5")
	public String messageRequestHeader4(
			@RequestHeader(defaultValue = "default", required = false) String header) {
		return header;
	}

	@ExtDirectMethod(value = ExtDirectMethodType.POLL, group = "group5")
	public String messageRequestHeader5(
			@RequestHeader(defaultValue = "default1", required = false) String header1,
			@RequestParam(value = "id", required = false) Integer id,
			@RequestHeader(defaultValue = "default2", required = false) String header2,
			@RequestHeader(value = "last") String header3) {
		return id + ";" + header1 + ";" + header2 + ";" + header3;
	}

	@ExtDirectMethod(value = ExtDirectMethodType.POLL, group = "group5")
	public String messageRequestHeader6(@RequestHeader Integer intHeader,
			@RequestHeader Boolean booleanHeader) {
		return intHeader + ";" + booleanHeader;
	}

	/* Cookie Value */

	@ExtDirectMethod(value = ExtDirectMethodType.POLL, group = "group5")
	public String messageCookieValue1(
			@RequestParam(value = "id", required = false) Integer id, String dummy,
			@CookieValue String cookie) {
		return id + ";" + dummy + ";" + cookie;
	}

	@ExtDirectMethod(value = ExtDirectMethodType.POLL, group = "group5",
			synchronizeOnSession = true)
	public String messageCookieValue2(@RequestParam Integer id,
			@CookieValue(value = "anotherName", required = true) String cookie) {
		return id + ";" + cookie;
	}

	@ExtDirectMethod(value = ExtDirectMethodType.POLL, group = "group5")
	public String messageCookieValue3(
			@CookieValue(value = "anotherName", defaultValue = "default") String cookie) {
		return cookie;
	}

	@ExtDirectMethod(value = ExtDirectMethodType.POLL, group = "group5")
	public String messageCookieValue4(
			@CookieValue(defaultValue = "default", required = false) String cookie) {
		return cookie;
	}

	@ExtDirectMethod(value = ExtDirectMethodType.POLL, group = "group5")
	public String messageCookieValue5(@RequestHeader String requestHeader,
			@CookieValue(defaultValue = "default1", required = false) String cookie1,
			@RequestParam(value = "id", required = false) Integer id,
			@CookieValue(defaultValue = "default2", required = false) String cookie2,
			@CookieValue(value = "last") String cookie3) {
		return requestHeader + ";" + id + ";" + cookie1 + ";" + cookie2 + ";" + cookie3;
	}

	@ExtDirectMethod(value = ExtDirectMethodType.POLL, group = "group5")
	public String messageCookieValue6(
			@RequestHeader(required = false,
					defaultValue = "theHeader") String requestHeader,
			@CookieValue Integer intCookie, @CookieValue Boolean booleanCookie) {
		return requestHeader + ";" + intCookie + ";" + booleanCookie;
	}
}