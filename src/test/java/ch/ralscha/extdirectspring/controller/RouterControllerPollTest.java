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
package ch.ralscha.extdirectspring.controller;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.Cookie;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import ch.ralscha.extdirectspring.bean.ExtDirectPollResponse;

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration("classpath:/testApplicationContext.xml")
public class RouterControllerPollTest {

	@Autowired
	private WebApplicationContext wac;

	private MockMvc mockMvc;

	@Before
	public void setupMockMvc() throws Exception {
		this.mockMvc = MockMvcBuilders.webAppContextSetup(this.wac).build();
	}

	@Test
	public void pollBeanDoesNotExists() throws Exception {

		ExtDirectPollResponse resp = ControllerUtil.performPollRequest(this.mockMvc,
				"pollProviderXY", "handleMessage1", "message1", null, null);

		assertThat(resp).isNotNull();
		assertThat(resp.getType()).isEqualTo("exception");
		assertThat(resp.getName()).isEqualTo("message1");
		assertThat(resp.getData()).isNull();
		assertThat(resp.getWhere()).isNull();
		assertThat(resp.getMessage()).isEqualTo("Server Error");
	}

	@Test
	public void pollNoArguments() throws Exception {
		ExtDirectPollResponse resp = ControllerUtil.performPollRequest(this.mockMvc,
				"pollProvider", "handleMessage1", "message1", null, null);

		assertThat(resp).isNotNull();
		assertThat(resp.getType()).isEqualTo("event");
		assertThat(resp.getName()).isEqualTo("message1");
		assertThat((String) resp.getData()).startsWith("Successfully polled at: ");
		assertThat(resp.getWhere()).isNull();
		assertThat(resp.getMessage()).isNull();
	}

	@Test
	public void pollSupportedArguments() throws Exception {
		ExtDirectPollResponse resp = ControllerUtil.performPollRequest(this.mockMvc,
				"pollProvider", "handleMessage2", "message2", null, null);

		assertThat(resp).isNotNull();
		assertThat(resp.getType()).isEqualTo("event");
		assertThat(resp.getName()).isEqualTo("message2");
		assertThat((String) resp.getData()).startsWith("Successfully polled at: ");
		assertThat(resp.getWhere()).isNull();
		assertThat(resp.getMessage()).isNull();
	}

	@Test
	public void pollRequiredArgument() throws Exception {
		Map<String, String> params = new LinkedHashMap<String, String>();
		params.put("id", "2");

		ExtDirectPollResponse resp = ControllerUtil.performPollRequest(this.mockMvc,
				"pollProvider", "handleMessage3", "message3", params, null);

		assertThat(resp).isNotNull();
		assertThat(resp.getType()).isEqualTo("event");
		assertThat(resp.getName()).isEqualTo("message3");
		assertThat(resp.getData()).isEqualTo("Result: 2");
		assertThat(resp.getWhere()).isNull();
		assertThat(resp.getMessage()).isNull();
	}

	@Test
	public void pollRequiredArgumentNoRequestParameter() throws Exception {

		ExtDirectPollResponse resp = ControllerUtil.performPollRequest(this.mockMvc,
				"pollProvider", "handleMessage3", "message3", null, null);

		assertThat(resp).isNotNull();
		assertThat(resp.getType()).isEqualTo("exception");
		assertThat(resp.getName()).isEqualTo("message3");
		assertThat(resp.getData()).isNull();
		assertThat(resp.getMessage()).isEqualTo("Server Error");
		assertThat(resp.getWhere()).isNull();
	}

	@Test
	public void pollDefaultValueArgumentWithRequestParameter() throws Exception {
		Map<String, String> params = new LinkedHashMap<String, String>();
		params.put("id", "7");

		ExtDirectPollResponse resp = ControllerUtil.performPollRequest(this.mockMvc,
				"pollProvider", "handleMessage4", "message4", params, null);
		assertThat(resp).isNotNull();
		assertThat(resp.getType()).isEqualTo("event");
		assertThat(resp.getName()).isEqualTo("message4");
		assertThat(resp.getData()).isEqualTo(Integer.valueOf(14));
		assertThat(resp.getWhere()).isNull();
		assertThat(resp.getMessage()).isNull();
	}

	@Test
	public void pollDefaultValueArgumentWithoutRequestParameter() throws Exception {
		ExtDirectPollResponse resp = ControllerUtil.performPollRequest(this.mockMvc,
				"pollProvider", "handleMessage4", "message4", null, null, null, true);

		assertThat(resp).isNotNull();
		assertThat(resp.getType()).isEqualTo("event");
		assertThat(resp.getName()).isEqualTo("message4");
		assertThat(resp.getData()).isEqualTo(Integer.valueOf(2));
		assertThat(resp.getWhere()).isNull();
		assertThat(resp.getMessage()).isNull();
	}

	@Test
	public void pollNotRequiredArgumentWithRequestParameter() throws Exception {
		Map<String, String> params = new LinkedHashMap<String, String>();
		params.put("id", "3");

		ExtDirectPollResponse resp = ControllerUtil.performPollRequest(this.mockMvc,
				"pollProvider", "handleMessage5", "message5", params, null);

		assertThat(resp).isNotNull();
		assertThat(resp.getType()).isEqualTo("event");
		assertThat(resp.getName()).isEqualTo("message5");
		assertThat(resp.getData()).isEqualTo(Integer.valueOf(6));
		assertThat(resp.getWhere()).isNull();
		assertThat(resp.getMessage()).isNull();
	}

	@Test
	public void pollNotRequiredArgumentWithoutRequestParameter() throws Exception {
		ExtDirectPollResponse resp = ControllerUtil.performPollRequest(this.mockMvc,
				"pollProvider", "handleMessage5", "message5", null, null);

		assertThat(resp).isNotNull();
		assertThat(resp.getType()).isEqualTo("event");
		assertThat(resp.getName()).isEqualTo("message5");
		assertThat(resp.getData()).isNull();
		assertThat(resp.getWhere()).isNull();
		assertThat(resp.getMessage()).isNull();
	}

	@Test
	public void pollRequiredHeaderWithoutValue() throws Exception {
		HttpHeaders headers = new HttpHeaders();
		headers.add("header", "headerValue");

		ExtDirectPollResponse resp = ControllerUtil.performPollRequest(this.mockMvc,
				"pollProvider", "messageRequestHeader1", "messageRequestHeader1", null,
				headers);

		assertThat(resp).isNotNull();
		assertThat(resp.getType()).isEqualTo("event");
		assertThat(resp.getName()).isEqualTo("messageRequestHeader1");
		assertThat(resp.getData()).isEqualTo("null;null;headerValue");
		assertThat(resp.getWhere()).isNull();
		assertThat(resp.getMessage()).isNull();
	}

	@Test
	public void pollRequiredHeaderWithValue() throws Exception {
		Map<String, String> params = new LinkedHashMap<String, String>();
		params.put("id", "1");

		HttpHeaders headers = new HttpHeaders();
		headers.add("header", "headerValue");
		headers.add("anotherName", "headerValue1");
		headers.add("anotherName", "headerValue2");

		ExtDirectPollResponse resp = ControllerUtil.performPollRequest(this.mockMvc,
				"pollProvider", "messageRequestHeader2", "messageRequestHeader2", params,
				headers);

		assertThat(resp).isNotNull();
		assertThat(resp.getType()).isEqualTo("event");
		assertThat(resp.getName()).isEqualTo("messageRequestHeader2");
		assertThat(resp.getData()).isEqualTo("1;headerValue1");
		assertThat(resp.getWhere()).isNull();
		assertThat(resp.getMessage()).isNull();

		params.clear();
		params.put("id", "2");

		resp = ControllerUtil.performPollRequest(this.mockMvc, "pollProvider",
				"messageRequestHeader2", "messageRequestHeader2", params, null, null,
				true);

		assertThat(resp).isNotNull();
		assertThat(resp.getType()).isEqualTo("exception");
		assertThat(resp.getName()).isEqualTo("messageRequestHeader2");
		assertThat(resp.getData()).isNull();
		assertThat(resp.getWhere()).isNull();
		assertThat(resp.getMessage()).isEqualTo("Server Error");

		params.clear();
		params.put("id", "3");
		headers = new HttpHeaders();
		headers.add("header", "headerValue");
		resp = ControllerUtil.performPollRequest(this.mockMvc, "pollProvider",
				"messageRequestHeader2", "messageRequestHeader2", params, headers, null,
				true);

		assertThat(resp).isNotNull();
		assertThat(resp.getType()).isEqualTo("exception");
		assertThat(resp.getName()).isEqualTo("messageRequestHeader2");
		assertThat(resp.getData()).isNull();
		assertThat(resp.getWhere()).isNull();
		assertThat(resp.getMessage()).isEqualTo("Server Error");
	}

	@Test
	public void pollRequiredHeaderWithValueAndDefault1() throws Exception {
		HttpHeaders headers = new HttpHeaders();
		headers.add("header", "headerValue");
		headers.add("anotherName", "headerValue1");

		ExtDirectPollResponse resp = ControllerUtil.performPollRequest(this.mockMvc,
				"pollProvider", "messageRequestHeader3", "messageRequestHeader3", null,
				headers);

		assertThat(resp).isNotNull();
		assertThat(resp.getType()).isEqualTo("event");
		assertThat(resp.getName()).isEqualTo("messageRequestHeader3");
		assertThat(resp.getData()).isEqualTo("headerValue1");
		assertThat(resp.getWhere()).isNull();
		assertThat(resp.getMessage()).isNull();
	}

	@Test
	public void pollRequiredHeaderWithValueAndDefault2() throws Exception {

		ExtDirectPollResponse resp = ControllerUtil.performPollRequest(this.mockMvc,
				"pollProvider", "messageRequestHeader3", "messageRequestHeader3", null,
				null);

		assertThat(resp).isNotNull();
		assertThat(resp.getType()).isEqualTo("event");
		assertThat(resp.getName()).isEqualTo("messageRequestHeader3");
		assertThat(resp.getData()).isEqualTo("default");
		assertThat(resp.getWhere()).isNull();
		assertThat(resp.getMessage()).isNull();
	}

	@Test
	public void pollOptionalHeaderWithoutValueAndDefault1() throws Exception {
		HttpHeaders headers = new HttpHeaders();
		headers.add("header", "headerValue");
		headers.add("anotherName", "headerValue1");

		ExtDirectPollResponse resp = ControllerUtil.performPollRequest(this.mockMvc,
				"pollProvider", "messageRequestHeader4", "messageRequestHeader4", null,
				headers);

		assertThat(resp).isNotNull();
		assertThat(resp.getType()).isEqualTo("event");
		assertThat(resp.getName()).isEqualTo("messageRequestHeader4");
		assertThat(resp.getData()).isEqualTo("headerValue");
		assertThat(resp.getWhere()).isNull();
		assertThat(resp.getMessage()).isNull();
	}

	@Test
	public void pollOptionalHeaderWithoutValueAndDefault2() throws Exception {
		ExtDirectPollResponse resp = ControllerUtil.performPollRequest(this.mockMvc,
				"pollProvider", "messageRequestHeader4", "messageRequestHeader4", null,
				null);

		assertThat(resp).isNotNull();
		assertThat(resp.getType()).isEqualTo("event");
		assertThat(resp.getName()).isEqualTo("messageRequestHeader4");
		assertThat(resp.getData()).isEqualTo("default");
		assertThat(resp.getWhere()).isNull();
		assertThat(resp.getMessage()).isNull();
	}

	@Test
	public void pollMultipleHeaders1() throws Exception {
		HttpHeaders headers = new HttpHeaders();
		headers.add("last", "lastHeader");

		ExtDirectPollResponse resp = ControllerUtil.performPollRequest(this.mockMvc,
				"pollProvider", "messageRequestHeader5", "messageRequestHeader5", null,
				headers);
		assertThat(resp).isNotNull();
		assertThat(resp.getType()).isEqualTo("event");
		assertThat(resp.getName()).isEqualTo("messageRequestHeader5");
		assertThat(resp.getData()).isEqualTo("null;default1;default2;lastHeader");
		assertThat(resp.getWhere()).isNull();
		assertThat(resp.getMessage()).isNull();
	}

	@Test
	public void pollMultipleHeaders2() throws Exception {
		Map<String, String> params = new LinkedHashMap<String, String>();
		params.put("id", "33");

		HttpHeaders headers = new HttpHeaders();
		headers.add("last", "lastHeader");
		headers.add("header2", "2ndHeader");

		ExtDirectPollResponse resp = ControllerUtil.performPollRequest(this.mockMvc,
				"pollProvider", "messageRequestHeader5", "messageRequestHeader5", params,
				headers);

		assertThat(resp).isNotNull();
		assertThat(resp.getType()).isEqualTo("event");
		assertThat(resp.getName()).isEqualTo("messageRequestHeader5");
		assertThat(resp.getData()).isEqualTo("33;default1;2ndHeader;lastHeader");
		assertThat(resp.getWhere()).isNull();
		assertThat(resp.getMessage()).isNull();
	}

	@Test
	public void pollMultipleHeaders3() throws Exception {
		Map<String, String> params = new LinkedHashMap<String, String>();
		params.put("id", "44");

		HttpHeaders headers = new HttpHeaders();
		headers.add("last", "last");
		headers.add("header1", "1st");
		headers.add("header2", "2nd");

		ExtDirectPollResponse resp = ControllerUtil.performPollRequest(this.mockMvc,
				"pollProvider", "messageRequestHeader5", "messageRequestHeader5", params,
				headers);

		assertThat(resp).isNotNull();
		assertThat(resp.getType()).isEqualTo("event");
		assertThat(resp.getName()).isEqualTo("messageRequestHeader5");
		assertThat(resp.getData()).isEqualTo("44;1st;2nd;last");
		assertThat(resp.getWhere()).isNull();
		assertThat(resp.getMessage()).isNull();
	}

	@Test
	public void pollHeaderWithConversion() throws Exception {

		HttpHeaders headers = new HttpHeaders();
		headers.add("intHeader", "2");
		headers.add("booleanHeader", "true");

		ExtDirectPollResponse resp = ControllerUtil.performPollRequest(this.mockMvc,
				"pollProvider", "messageRequestHeader6", "messageRequestHeader6", null,
				headers);

		assertThat(resp).isNotNull();
		assertThat(resp.getType()).isEqualTo("event");
		assertThat(resp.getName()).isEqualTo("messageRequestHeader6");
		assertThat(resp.getData()).isEqualTo("2;true");
		assertThat(resp.getWhere()).isNull();
		assertThat(resp.getMessage()).isNull();
	}

	// CookieValue

	@Test
	public void pollRequiredCookieWithoutValue() throws Exception {
		List<Cookie> cookies = new ArrayList<Cookie>();
		cookies.add(new Cookie("cookie", "cookieValue"));

		ExtDirectPollResponse resp = ControllerUtil.performPollRequest(this.mockMvc,
				"pollProvider", "messageCookieValue1", "messageCookieValue1", null, null,
				cookies);

		assertThat(resp).isNotNull();
		assertThat(resp.getType()).isEqualTo("event");
		assertThat(resp.getName()).isEqualTo("messageCookieValue1");
		assertThat(resp.getData()).isEqualTo("null;null;cookieValue");
		assertThat(resp.getWhere()).isNull();
		assertThat(resp.getMessage()).isNull();
	}

	@Test
	public void pollRequiredCookieWithValue() throws Exception {
		Map<String, String> params = new LinkedHashMap<String, String>();
		params.put("id", "1");

		List<Cookie> cookies = new ArrayList<Cookie>();
		cookies.add(new Cookie("cookie", "cookieValue"));
		cookies.add(new Cookie("anotherName", "cookieValue1"));
		cookies.add(new Cookie("anotherName", "cookieValue2"));

		ExtDirectPollResponse resp = ControllerUtil.performPollRequest(this.mockMvc,
				"pollProvider", "messageCookieValue2", "messageCookieValue2", params,
				null, cookies);

		assertThat(resp).isNotNull();
		assertThat(resp.getType()).isEqualTo("event");
		assertThat(resp.getName()).isEqualTo("messageCookieValue2");
		assertThat(resp.getData()).isEqualTo("1;cookieValue1");
		assertThat(resp.getWhere()).isNull();
		assertThat(resp.getMessage()).isNull();

		params.clear();
		params.put("id", "2");

		resp = ControllerUtil.performPollRequest(this.mockMvc, "pollProvider",
				"messageCookieValue2", "messageCookieValue2", params, null, null, true);

		assertThat(resp).isNotNull();
		assertThat(resp.getType()).isEqualTo("exception");
		assertThat(resp.getName()).isEqualTo("messageCookieValue2");
		assertThat(resp.getData()).isNull();
		assertThat(resp.getWhere()).isNull();
		assertThat(resp.getMessage()).isEqualTo("Server Error");

		params.clear();
		params.put("id", "3");
		cookies = new ArrayList<Cookie>();
		cookies.add(new Cookie("cookie", "cookieValue"));
		resp = ControllerUtil.performPollRequest(this.mockMvc, "pollProvider",
				"messageCookieValue2", "messageCookieValue2", params, null, cookies,
				true);

		assertThat(resp).isNotNull();
		assertThat(resp.getType()).isEqualTo("exception");
		assertThat(resp.getName()).isEqualTo("messageCookieValue2");
		assertThat(resp.getData()).isNull();
		assertThat(resp.getWhere()).isNull();
		assertThat(resp.getMessage()).isEqualTo("Server Error");
	}

	@Test
	public void pollRequiredCookieWithValueAndDefault1() throws Exception {
		List<Cookie> cookies = new ArrayList<Cookie>();
		cookies.add(new Cookie("cookie", "cookieValue"));
		cookies.add(new Cookie("anotherName", "cookieValue1"));

		ExtDirectPollResponse resp = ControllerUtil.performPollRequest(this.mockMvc,
				"pollProvider", "messageCookieValue3", "messageCookieValue3", null, null,
				cookies);

		assertThat(resp).isNotNull();
		assertThat(resp.getType()).isEqualTo("event");
		assertThat(resp.getName()).isEqualTo("messageCookieValue3");
		assertThat(resp.getData()).isEqualTo("cookieValue1");
		assertThat(resp.getWhere()).isNull();
		assertThat(resp.getMessage()).isNull();
	}

	@Test
	public void pollRequiredCookieWithValueAndDefault2() throws Exception {

		ExtDirectPollResponse resp = ControllerUtil.performPollRequest(this.mockMvc,
				"pollProvider", "messageCookieValue3", "messageCookieValue3", null, null);

		assertThat(resp).isNotNull();
		assertThat(resp.getType()).isEqualTo("event");
		assertThat(resp.getName()).isEqualTo("messageCookieValue3");
		assertThat(resp.getData()).isEqualTo("default");
		assertThat(resp.getWhere()).isNull();
		assertThat(resp.getMessage()).isNull();
	}

	@Test
	public void pollOptionalCookieWithoutValueAndDefault1() throws Exception {
		List<Cookie> cookies = new ArrayList<Cookie>();
		cookies.add(new Cookie("cookie", "cookieValue"));
		cookies.add(new Cookie("anotherName", "cookieValue1"));

		ExtDirectPollResponse resp = ControllerUtil.performPollRequest(this.mockMvc,
				"pollProvider", "messageCookieValue4", "messageCookieValue4", null, null,
				cookies);

		assertThat(resp).isNotNull();
		assertThat(resp.getType()).isEqualTo("event");
		assertThat(resp.getName()).isEqualTo("messageCookieValue4");
		assertThat(resp.getData()).isEqualTo("cookieValue");
		assertThat(resp.getWhere()).isNull();
		assertThat(resp.getMessage()).isNull();
	}

	@Test
	public void pollOptionalCookieWithoutValueAndDefault2() throws Exception {
		ExtDirectPollResponse resp = ControllerUtil.performPollRequest(this.mockMvc,
				"pollProvider", "messageCookieValue4", "messageCookieValue4", null, null);

		assertThat(resp).isNotNull();
		assertThat(resp.getType()).isEqualTo("event");
		assertThat(resp.getName()).isEqualTo("messageCookieValue4");
		assertThat(resp.getData()).isEqualTo("default");
		assertThat(resp.getWhere()).isNull();
		assertThat(resp.getMessage()).isNull();
	}

	@Test
	public void pollMultipleCookies1() throws Exception {
		HttpHeaders headers = new HttpHeaders();
		headers.add("requestHeader", "aRequestHeader");

		List<Cookie> cookies = new ArrayList<Cookie>();
		cookies.add(new Cookie("last", "lastCookie"));

		ExtDirectPollResponse resp = ControllerUtil.performPollRequest(this.mockMvc,
				"pollProvider", "messageCookieValue5", "messageCookieValue5", null,
				headers, cookies);
		assertThat(resp).isNotNull();
		assertThat(resp.getType()).isEqualTo("event");
		assertThat(resp.getName()).isEqualTo("messageCookieValue5");
		assertThat(resp.getData())
				.isEqualTo("aRequestHeader;null;default1;default2;lastCookie");
		assertThat(resp.getWhere()).isNull();
		assertThat(resp.getMessage()).isNull();
	}

	@Test
	public void pollMultipleCookies2() throws Exception {
		HttpHeaders headers = new HttpHeaders();
		headers.add("requestHeader", "aRequestHeader");

		Map<String, String> params = new LinkedHashMap<String, String>();
		params.put("id", "33");

		List<Cookie> cookies = new ArrayList<Cookie>();
		cookies.add(new Cookie("last", "lastCookie"));
		cookies.add(new Cookie("cookie2", "2ndCookie"));

		ExtDirectPollResponse resp = ControllerUtil.performPollRequest(this.mockMvc,
				"pollProvider", "messageCookieValue5", "messageCookieValue5", params,
				headers, cookies);

		assertThat(resp).isNotNull();
		assertThat(resp.getType()).isEqualTo("event");
		assertThat(resp.getName()).isEqualTo("messageCookieValue5");
		assertThat(resp.getData())
				.isEqualTo("aRequestHeader;33;default1;2ndCookie;lastCookie");
		assertThat(resp.getWhere()).isNull();
		assertThat(resp.getMessage()).isNull();
	}

	@Test
	public void pollMultipleCookies3() throws Exception {
		HttpHeaders headers = new HttpHeaders();
		headers.add("requestHeader", "aRequestHeader");

		Map<String, String> params = new LinkedHashMap<String, String>();
		params.put("id", "44");

		List<Cookie> cookies = new ArrayList<Cookie>();
		cookies.add(new Cookie("last", "last"));
		cookies.add(new Cookie("cookie1", "1st"));
		cookies.add(new Cookie("cookie2", "2nd"));

		ExtDirectPollResponse resp = ControllerUtil.performPollRequest(this.mockMvc,
				"pollProvider", "messageCookieValue5", "messageCookieValue5", params,
				headers, cookies);

		assertThat(resp).isNotNull();
		assertThat(resp.getType()).isEqualTo("event");
		assertThat(resp.getName()).isEqualTo("messageCookieValue5");
		assertThat(resp.getData()).isEqualTo("aRequestHeader;44;1st;2nd;last");
		assertThat(resp.getWhere()).isNull();
		assertThat(resp.getMessage()).isNull();
	}

	@Test
	public void pollCookieWithConversion() throws Exception {
		List<Cookie> cookies = new ArrayList<Cookie>();
		cookies.add(new Cookie("intCookie", "2"));
		cookies.add(new Cookie("booleanCookie", "true"));

		ExtDirectPollResponse resp = ControllerUtil.performPollRequest(this.mockMvc,
				"pollProvider", "messageCookieValue6", "messageCookieValue6", null, null,
				cookies);

		assertThat(resp).isNotNull();
		assertThat(resp.getType()).isEqualTo("event");
		assertThat(resp.getName()).isEqualTo("messageCookieValue6");
		assertThat(resp.getData()).isEqualTo("theHeader;2;true");
		assertThat(resp.getWhere()).isNull();
		assertThat(resp.getMessage()).isNull();

	}

}
