/**
 * Copyright 2010-2011 Ralph Schaer <ralphschaer@gmail.com>
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

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.junit.Assert;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;

import ch.ralscha.extdirectspring.annotation.ExtDirectMethod;
import ch.ralscha.extdirectspring.annotation.ExtDirectMethodType;

@Service
public class PollProvider {

	@ExtDirectMethod(value = ExtDirectMethodType.POLL, event = "message1", group = "group2")
	public String handleMessage1() {
		Date now = new Date();
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd 'at' hh:mm:ss");
		return "Successfully polled at: " + formatter.format(now);
	}

	@ExtDirectMethod(value = ExtDirectMethodType.POLL, event = "message2", group = "group2")
	public String handleMessage2(HttpServletResponse response, HttpServletRequest request, HttpSession session,
			Locale locale) {
		Assert.assertNotNull(response);
		Assert.assertNotNull(request);
		Assert.assertNotNull(session);
		Assert.assertEquals(Locale.ENGLISH, locale);

		Date now = new Date();
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd 'at' hh:mm:ss");
		return "Successfully polled at: " + formatter.format(now);
	}

	@ExtDirectMethod(value = ExtDirectMethodType.POLL, event = "message3", group = "group4")
	public String handleMessage3(Locale locale, @RequestParam(value = "id") int id) {
		Assert.assertEquals(Locale.ENGLISH, locale);
		return "Result: " + id;
	}

	@ExtDirectMethod(value = ExtDirectMethodType.POLL, event = "message4")
	public int handleMessage4(@RequestParam(value = "id", defaultValue = "1") int id, HttpServletRequest request) {
		Assert.assertNotNull(request);
		return id * 2;
	}

	@ExtDirectMethod(value = ExtDirectMethodType.POLL, event = "message5", group = "group3")
	public Integer handleMessage5(@RequestParam(value = "id", required = false) Integer id, String dummy) {
		Assert.assertNull(dummy);
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
}