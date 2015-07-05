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

import static org.fest.assertions.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.HashMap;
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
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import ch.ralscha.extdirectspring.bean.SSEvent;

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration("classpath:/testApplicationContext.xml")
public class RouterControllerSseTest {

	@Autowired
	private WebApplicationContext wac;

	private MockMvc mockMvc;

	@Autowired
	private ConfigurationService configurationService;

	@Before
	public void setupMockMvc() throws Exception {
		Configuration config = new Configuration();
		ReflectionTestUtils.setField(configurationService, "configuration", config);
		configurationService.afterPropertiesSet();

		mockMvc = MockMvcBuilders.webAppContextSetup(wac).build();
	}

	@Test
	public void sseBeanDoesNotExists() throws Exception {
		List<SSEvent> events = ControllerUtil.performSseRequest(mockMvc, "sseProviderXY",
				"message1", null, null, null);
		assertThat(events).hasSize(1);
		SSEvent event = events.get(0);

		assertThat(event.getEvent()).isEqualTo("error");
		assertThat(event.getComment()).isNull();
		assertThat(event.getData()).isEqualTo("Server Error");
		assertThat(event.getId()).isNull();
		assertThat(event.getRetry()).isNull();
	}

	@Test
	public void sseBeanDoesNotExistsWithStacktrace() throws Exception {
		Configuration config = new Configuration();
		config.setSendStacktrace(true);
		ReflectionTestUtils.setField(configurationService, "configuration", config);
		configurationService.afterPropertiesSet();

		List<SSEvent> events = ControllerUtil.performSseRequest(mockMvc, "sseProviderXY",
				"message1", null, null, null);
		assertThat(events).hasSize(1);
		SSEvent event = events.get(0);

		assertThat(event.getEvent()).isEqualTo("error");
		assertThat(event.getComment())
				.isEqualTo("Bean or Method 'sseProviderXY.message1' not found");
		assertThat(event.getData()).isEqualTo("Server Error");
		assertThat(event.getId()).isNull();
		assertThat(event.getRetry()).isNull();
	}

	@Test
	public void sseNoArguments() throws Exception {

		List<SSEvent> events = ControllerUtil.performSseRequest(mockMvc, "sseProvider",
				"message1", null, null, null);
		assertThat(events).hasSize(1);
		SSEvent event = events.get(0);

		assertThat(event.getEvent()).isNull();
		assertThat(event.getComment()).isNull();
		assertThat(event.getData()).startsWith("Successfully polled at: ");
		assertThat(event.getId()).isNull();
		assertThat(event.getRetry()).isNull();
	}

	@Test
	public void sseSupportedArguments() throws Exception {
		List<SSEvent> events = ControllerUtil.performSseRequest(mockMvc, "sseProvider",
				"message2", null, null, null);
		assertThat(events).hasSize(1);
		SSEvent event = events.get(0);

		assertThat(event.getEvent()).isNull();
		assertThat(event.getComment()).isNull();
		assertThat(event.getData()).startsWith("Successfully polled at: ");
		assertThat(event.getId()).isNull();
		assertThat(event.getRetry()).isEqualTo(200000);
	}

	@Test
	public void sseRequiredArgument() throws Exception {
		Map<String, String> params = new HashMap<String, String>();
		params.put("id", "2");

		List<SSEvent> events = ControllerUtil.performSseRequest(mockMvc, "sseProvider",
				"message3", params, null, null);
		assertThat(events).hasSize(1);
		SSEvent event = events.get(0);

		assertThat(event.getEvent()).isNull();
		assertThat(event.getComment()).isNull();
		assertThat(event.getData()).startsWith("Result: 2");
		assertThat(event.getId()).isNull();
		assertThat(event.getRetry()).isNull();
	}

	@Test
	public void sseRequiredArgumentNoRequestParameter() throws Exception {

		List<SSEvent> events = ControllerUtil.performSseRequest(mockMvc, "sseProvider",
				"message3", null, null, null);
		assertThat(events).hasSize(1);
		SSEvent event = events.get(0);

		assertThat(event.getEvent()).isEqualTo("error");
		assertThat(event.getComment()).isNull();
		assertThat(event.getData()).isEqualTo("Server Error");
		assertThat(event.getId()).isNull();
		assertThat(event.getRetry()).isNull();
	}

	@Test
	public void sseRequiredArgumentNoRequestParameterWithStacktrace() throws Exception {
		Configuration config = new Configuration();
		config.setSendStacktrace(true);
		ReflectionTestUtils.setField(configurationService, "configuration", config);
		configurationService.afterPropertiesSet();

		List<SSEvent> events = ControllerUtil.performSseRequest(mockMvc, "sseProvider",
				"message3", null, null, null);
		assertThat(events).hasSize(1);
		SSEvent event = events.get(0);

		assertThat(event.getEvent()).isEqualTo("error");
		assertThat(event.getComment()).startsWith(
				"java.lang.IllegalStateException: Missing parameter 'id' of type [int]");
		assertThat(event.getData()).isEqualTo("Server Error");
		assertThat(event.getId()).isNull();
		assertThat(event.getRetry()).isNull();
	}

	@Test
	public void sseDefaultValueArgumentWithRequestParameter() throws Exception {
		Map<String, String> params = new HashMap<String, String>();
		params.put("id", "7");

		List<SSEvent> events = ControllerUtil.performSseRequest(mockMvc, "sseProvider",
				"message4", params, null, null);
		assertThat(events).hasSize(1);
		SSEvent event = events.get(0);

		assertThat(event.getEvent()).isNull();
		assertThat(event.getComment()).isNull();
		assertThat(event.getData()).isEqualTo("14");
		assertThat(event.getId()).isNull();
		assertThat(event.getRetry()).isNull();

	}

	@Test
	public void sseDefaultValueArgumentWithoutRequestParameter() throws Exception {
		List<SSEvent> events = ControllerUtil.performSseRequest(mockMvc, "sseProvider",
				"message4", null, null, null, true);
		assertThat(events).hasSize(1);
		SSEvent event = events.get(0);

		assertThat(event.getEvent()).isNull();
		assertThat(event.getComment()).isNull();
		assertThat(event.getData()).isEqualTo("2");
		assertThat(event.getId()).isNull();
		assertThat(event.getRetry()).isNull();
	}

	@Test
	public void sseNotRequiredArgumentWithRequestParameter() throws Exception {
		Map<String, String> params = new HashMap<String, String>();
		params.put("id", "3");

		List<SSEvent> events = ControllerUtil.performSseRequest(mockMvc, "sseProvider",
				"message5", params, null, null);
		assertThat(events).hasSize(1);
		SSEvent event = events.get(0);

		assertThat(event.getEvent()).isNull();
		assertThat(event.getComment()).isNull();
		assertThat(event.getData()).isEqualTo("6");
		assertThat(event.getId()).isNull();
		assertThat(event.getRetry()).isNull();
	}

	@Test
	public void sseNotRequiredArgumentWithoutRequestParameter() throws Exception {
		List<SSEvent> events = ControllerUtil.performSseRequest(mockMvc, "sseProvider",
				"message5", null, null, null);
		assertThat(events).hasSize(1);
		SSEvent event = events.get(0);

		assertThat(event.getEvent()).isNull();
		assertThat(event.getComment()).isNull();
		assertThat(event.getData()).isNull();
		assertThat(event.getId()).isNull();
		assertThat(event.getRetry()).isNull();
	}

	@Test
	public void sseRequiredHeaderWithoutValue() throws Exception {
		HttpHeaders headers = new HttpHeaders();
		headers.add("header", "headerValue");

		List<SSEvent> events = ControllerUtil.performSseRequest(mockMvc, "sseProvider",
				"messageRequestHeader1", null, headers, null);
		assertThat(events).hasSize(1);
		SSEvent event = events.get(0);

		assertThat(event.getEvent()).isNull();
		assertThat(event.getComment()).isNull();
		assertThat(event.getData()).startsWith("null;null;headerValue");
		assertThat(event.getId()).isEqualTo("1");
		assertThat(event.getRetry()).isNull();
	}

	@Test
	public void sseRequiredHeaderWithValue() throws Exception {
		Map<String, String> params = new HashMap<String, String>();
		params.put("id", "1");

		HttpHeaders headers = new HttpHeaders();
		headers.add("header", "headerValue");
		headers.add("anotherName", "headerValue1");
		headers.add("anotherName", "headerValue2");

		List<SSEvent> events = ControllerUtil.performSseRequest(mockMvc, "sseProvider",
				"messageRequestHeader2", params, headers, null);
		assertThat(events).hasSize(1);
		SSEvent event = events.get(0);

		assertThat(event.getEvent()).isNull();
		assertThat(event.getComment()).isNull();
		assertThat(event.getData()).startsWith("1;headerValue1");
		assertThat(event.getId()).isNull();
		assertThat(event.getRetry()).isNull();

		params.clear();
		params.put("id", "2");

		events = ControllerUtil.performSseRequest(mockMvc, "sseProvider",
				"messageRequestHeader2", params, null, null, true);
		assertThat(events).hasSize(1);
		event = events.get(0);

		assertThat(event.getEvent()).isEqualTo("error");
		assertThat(event.getComment()).isNull();
		assertThat(event.getData()).startsWith("Server Error");
		assertThat(event.getId()).isNull();
		assertThat(event.getRetry()).isNull();
	}

	@Test
	public void sseRequiredHeaderWithValueAndDefault1() throws Exception {
		HttpHeaders headers = new HttpHeaders();
		headers.add("header", "headerValue");
		headers.add("anotherName", "headerValue1");

		List<SSEvent> events = ControllerUtil.performSseRequest(mockMvc, "sseProvider",
				"messageRequestHeader3", null, headers, null);
		assertThat(events).hasSize(1);
		SSEvent event = events.get(0);

		assertThat(event.getEvent()).isEqualTo("messageRequestHeader3");
		assertThat(event.getComment()).isNull();
		assertThat(event.getData()).isEqualTo("headerValue1");
		assertThat(event.getId()).isNull();
		assertThat(event.getRetry()).isNull();
	}

	@Test
	public void sseRequiredHeaderWithValueAndDefault2() throws Exception {

		List<SSEvent> events = ControllerUtil.performSseRequest(mockMvc, "sseProvider",
				"messageRequestHeader3", null, null, null);
		assertThat(events).hasSize(1);
		SSEvent event = events.get(0);

		assertThat(event.getEvent()).isEqualTo("messageRequestHeader3");
		assertThat(event.getComment()).isNull();
		assertThat(event.getData()).isEqualTo("default");
		assertThat(event.getId()).isNull();
		assertThat(event.getRetry()).isNull();
	}

	@Test
	public void sseOptionalHeaderWithoutValueAndDefault1() throws Exception {

		HttpHeaders headers = new HttpHeaders();
		headers.add("header", "headerValue");
		headers.add("anotherName", "headerValue1");

		List<SSEvent> events = ControllerUtil.performSseRequest(mockMvc, "sseProvider",
				"messageRequestHeader4", null, headers, null);
		assertThat(events).hasSize(1);
		SSEvent event = events.get(0);

		assertThat(event.getEvent()).isEqualTo("messageRequestHeader4");
		assertThat(event.getComment()).isEqualTo("comment of message headerValue");
		assertThat(event.getData()).isEqualTo("headerValue");
		assertThat(event.getId()).isNull();
		assertThat(event.getRetry()).isNull();
	}

	@Test
	public void sseOptionalHeaderWithoutValueAndDefault2() throws Exception {
		List<SSEvent> events = ControllerUtil.performSseRequest(mockMvc, "sseProvider",
				"messageRequestHeader4", null, null, null);
		assertThat(events).hasSize(1);
		SSEvent event = events.get(0);

		assertThat(event.getEvent()).isEqualTo("messageRequestHeader4");
		assertThat(event.getComment()).isEqualTo("comment of message default");
		assertThat(event.getData()).isEqualTo("default");
		assertThat(event.getId()).isNull();
		assertThat(event.getRetry()).isNull();
	}

	@Test
	public void sseMultipleHeaders1() throws Exception {
		HttpHeaders headers = new HttpHeaders();
		headers.add("last", "lastHeader");

		List<SSEvent> events = ControllerUtil.performSseRequest(mockMvc, "sseProvider",
				"messageRequestHeader5", null, headers, null);
		assertThat(events).hasSize(1);
		SSEvent event = events.get(0);

		assertThat(event.getEvent()).isEqualTo("messageRequestHeader5");
		assertThat(event.getComment()).isEqualTo("comment of message null");
		assertThat(event.getData()).isEqualTo("null;default1;default2;lastHeader");
		assertThat(event.getId()).isEqualTo("122");
		assertThat(event.getRetry()).isNull();
	}

	@Test
	public void sseMultipleHeaders2() throws Exception {
		Map<String, String> params = new HashMap<String, String>();
		params.put("id", "33");

		HttpHeaders headers = new HttpHeaders();
		headers.add("last", "lastHeader");
		headers.add("header2", "2ndHeader");

		List<SSEvent> events = ControllerUtil.performSseRequest(mockMvc, "sseProvider",
				"messageRequestHeader5", params, headers, null);
		assertThat(events).hasSize(1);
		SSEvent event = events.get(0);

		assertThat(event.getEvent()).isEqualTo("messageRequestHeader5");
		assertThat(event.getComment()).isEqualTo("comment of message 33");
		assertThat(event.getData()).isEqualTo("33;default1;2ndHeader;lastHeader");
		assertThat(event.getId()).isEqualTo("122");
		assertThat(event.getRetry()).isNull();
	}

	@Test
	public void sseMultipleHeaders3() throws Exception {
		Map<String, String> params = new HashMap<String, String>();
		params.put("id", "44");

		HttpHeaders headers = new HttpHeaders();
		headers.add("last", "last");
		headers.add("header1", "1st");
		headers.add("header2", "2nd");

		List<SSEvent> events = ControllerUtil.performSseRequest(mockMvc, "sseProvider",
				"messageRequestHeader5", params, headers, null);
		assertThat(events).hasSize(1);
		SSEvent event = events.get(0);

		assertThat(event.getEvent()).isEqualTo("messageRequestHeader5");
		assertThat(event.getComment()).isEqualTo("comment of message 44");
		assertThat(event.getData()).isEqualTo("44;1st;2nd;last");
		assertThat(event.getId()).isEqualTo("122");
		assertThat(event.getRetry()).isNull();
	}

	@Test
	public void sseHeaderWithConversion() throws Exception {
		HttpHeaders headers = new HttpHeaders();
		headers.add("intHeader", "2");
		headers.add("booleanHeader", "true");

		List<SSEvent> events = ControllerUtil.performSseRequest(mockMvc, "sseProvider",
				"messageRequestHeader6", null, headers, null);
		assertThat(events).hasSize(1);
		SSEvent event = events.get(0);

		assertThat(event.getEvent()).isEqualTo("messageRequestHeader6");
		assertThat(event.getComment()).isEqualTo("comment");
		assertThat(event.getData()).isEqualTo("2;true");
		assertThat(event.getId()).isEqualTo("123");
		assertThat(event.getRetry()).isEqualTo(10000);
	}

	@Test
	public void sseWithWriterAndStringReturn() throws Exception {
		List<SSEvent> events = ControllerUtil.performSseRequest(mockMvc, "sseProvider",
				"message13", null, null, null);
		assertThat(events).hasSize(5);

		SSEvent event = events.get(0);
		assertThat(event.getEvent()).isEqualTo("event");
		assertThat(event.getComment()).isEqualTo("first comment");
		assertThat(event.getData()).isEqualTo("one");
		assertThat(event.getId()).isEqualTo("1");
		assertThat(event.getRetry()).isEqualTo(1000);

		event = events.get(1);
		assertThat(event.getEvent()).isEqualTo("event");
		assertThat(event.getComment()).isEqualTo("second comment");
		assertThat(event.getData()).isEqualTo("two");
		assertThat(event.getId()).isEqualTo("2");
		assertThat(event.getRetry()).isEqualTo(1000);

		event = events.get(2);
		assertThat(event.getEvent()).isNull();
		assertThat(event.getComment()).isNull();
		assertThat(event.getData()).isEqualTo("third");
		assertThat(event.getId()).isNull();
		assertThat(event.getRetry()).isNull();

		event = events.get(3);
		assertThat(event.getEvent()).isNull();
		assertThat(event.getComment()).isNull();
		assertThat(event.getData()).isEqualTo("fourth");
		assertThat(event.getId()).isNull();
		assertThat(event.getRetry()).isNull();

		event = events.get(4);
		assertThat(event.getEvent()).isNull();
		assertThat(event.getComment()).isNull();
		assertThat(event.getData()).isEqualTo("fifth");
		assertThat(event.getId()).isNull();
		assertThat(event.getRetry()).isNull();
	}

	@Test
	public void sseWithWriterAndSSEventReturn() throws Exception {
		List<SSEvent> events = ControllerUtil.performSseRequest(mockMvc, "sseProvider",
				"message14", null, null, null);
		assertThat(events).hasSize(3);

		SSEvent event = events.get(0);
		assertThat(event.getEvent()).isNull();
		assertThat(event.getComment()).isNull();
		assertThat(event.getData()).isEqualTo("1");
		assertThat(event.getId()).isNull();
		assertThat(event.getRetry()).isNull();

		event = events.get(1);
		assertThat(event.getEvent()).isNull();
		assertThat(event.getComment()).isNull();
		assertThat(event.getData()).isEqualTo("2");
		assertThat(event.getId()).isNull();
		assertThat(event.getRetry()).isNull();

		event = events.get(2);
		assertThat(event.getEvent()).isNull();
		assertThat(event.getComment()).isEqualTo("the last message");
		assertThat(event.getData()).isEqualTo("3");
		assertThat(event.getId()).isEqualTo("123");
		assertThat(event.getRetry()).isEqualTo(0);
	}

	@Test
	public void sseWithWriterAndVoidReturn() throws Exception {
		List<SSEvent> events = ControllerUtil.performSseRequest(mockMvc, "sseProvider",
				"message15", null, null, null);
		assertThat(events).hasSize(4);

		SSEvent event = events.get(0);
		assertThat(event.getEvent()).isNull();
		assertThat(event.getComment()).isNull();
		assertThat(event.getData()).isEqualTo("A");
		assertThat(event.getId()).isNull();
		assertThat(event.getRetry()).isNull();

		event = events.get(1);
		assertThat(event.getEvent()).isNull();
		assertThat(event.getComment()).isNull();
		assertThat(event.getData()).isEqualTo("B");
		assertThat(event.getId()).isNull();
		assertThat(event.getRetry()).isNull();

		event = events.get(2);
		assertThat(event.getEvent()).isNull();
		assertThat(event.getComment()).isNull();
		assertThat(event.getData()).isEqualTo("C");
		assertThat(event.getId()).isNull();
		assertThat(event.getRetry()).isEqualTo(0);

		event = events.get(3);
		assertThat(event.getEvent()).isNull();
		assertThat(event.getComment()).isNull();
		assertThat(event.getData()).isNull();
		assertThat(event.getId()).isNull();
		assertThat(event.getRetry()).isEqualTo(10);
	}

	@Test
	public void sseRequiredCookieWithoutValue() throws Exception {
		List<Cookie> cookies = new ArrayList<Cookie>();
		cookies.add(new Cookie("cookie", "cookieValue"));

		List<SSEvent> events = ControllerUtil.performSseRequest(mockMvc, "sseProvider",
				"messageCookieValue1", null, null, cookies);
		assertThat(events).hasSize(1);
		SSEvent event = events.get(0);

		assertThat(event.getEvent()).isNull();
		assertThat(event.getComment()).isNull();
		assertThat(event.getData()).startsWith("null;null;cookieValue");
		assertThat(event.getId()).isEqualTo("1");
		assertThat(event.getRetry()).isNull();
	}

	@Test
	public void sseRequiredCookieWithValue() throws Exception {
		Map<String, String> params = new HashMap<String, String>();
		params.put("id", "1");

		List<Cookie> cookies = new ArrayList<Cookie>();
		cookies.add(new Cookie("cookie", "cookieValue"));
		cookies.add(new Cookie("anotherName", "cookieValue1"));
		cookies.add(new Cookie("anotherName", "cookieValue2"));

		List<SSEvent> events = ControllerUtil.performSseRequest(mockMvc, "sseProvider",
				"messageCookieValue2", params, null, cookies);
		assertThat(events).hasSize(1);
		SSEvent event = events.get(0);

		assertThat(event.getEvent()).isNull();
		assertThat(event.getComment()).isNull();
		assertThat(event.getData()).startsWith("1;cookieValue1");
		assertThat(event.getId()).isNull();
		assertThat(event.getRetry()).isNull();

		params.clear();
		params.put("id", "2");

		events = ControllerUtil.performSseRequest(mockMvc, "sseProvider",
				"messageCookieValue2", params, null, null, true);
		assertThat(events).hasSize(1);
		event = events.get(0);

		assertThat(event.getEvent()).isEqualTo("error");
		assertThat(event.getComment()).isNull();
		assertThat(event.getData()).startsWith("Server Error");
		assertThat(event.getId()).isNull();
		assertThat(event.getRetry()).isNull();
	}

	@Test
	public void sseRequiredCookieWithValueAndDefault1() throws Exception {
		List<Cookie> cookies = new ArrayList<Cookie>();
		cookies.add(new Cookie("cookie", "cookieValue"));
		cookies.add(new Cookie("anotherName", "cookieValue1"));

		List<SSEvent> events = ControllerUtil.performSseRequest(mockMvc, "sseProvider",
				"messageCookieValue3", null, null, cookies);
		assertThat(events).hasSize(1);
		SSEvent event = events.get(0);

		assertThat(event.getEvent()).isEqualTo("messageCookieValue3");
		assertThat(event.getComment()).isNull();
		assertThat(event.getData()).isEqualTo("cookieValue1");
		assertThat(event.getId()).isNull();
		assertThat(event.getRetry()).isNull();
	}

	@Test
	public void sseRequiredCookieWithValueAndDefault2() throws Exception {

		List<SSEvent> events = ControllerUtil.performSseRequest(mockMvc, "sseProvider",
				"messageCookieValue3", null, null, null);
		assertThat(events).hasSize(1);
		SSEvent event = events.get(0);

		assertThat(event.getEvent()).isEqualTo("messageCookieValue3");
		assertThat(event.getComment()).isNull();
		assertThat(event.getData()).isEqualTo("default");
		assertThat(event.getId()).isNull();
		assertThat(event.getRetry()).isNull();
	}

	@Test
	public void sseOptionalCookieWithoutValueAndDefault1() throws Exception {

		List<Cookie> cookies = new ArrayList<Cookie>();
		cookies.add(new Cookie("cookie", "cookieValue"));
		cookies.add(new Cookie("anotherName", "cookieValue1"));

		List<SSEvent> events = ControllerUtil.performSseRequest(mockMvc, "sseProvider",
				"messageCookieValue4", null, null, cookies);
		assertThat(events).hasSize(1);
		SSEvent event = events.get(0);

		assertThat(event.getEvent()).isEqualTo("messageCookieValue4");
		assertThat(event.getComment()).isEqualTo("comment of message cookieValue");
		assertThat(event.getData()).isEqualTo("cookieValue");
		assertThat(event.getId()).isNull();
		assertThat(event.getRetry()).isNull();
	}

	@Test
	public void sseOptionalCookieWithoutValueAndDefault2() throws Exception {
		List<SSEvent> events = ControllerUtil.performSseRequest(mockMvc, "sseProvider",
				"messageCookieValue4", null, null, null);
		assertThat(events).hasSize(1);
		SSEvent event = events.get(0);

		assertThat(event.getEvent()).isEqualTo("messageCookieValue4");
		assertThat(event.getComment()).isEqualTo("comment of message default");
		assertThat(event.getData()).isEqualTo("default");
		assertThat(event.getId()).isNull();
		assertThat(event.getRetry()).isNull();
	}

	@Test
	public void sseMultipleCookies1() throws Exception {
		HttpHeaders headers = new HttpHeaders();
		headers.add("requestHeader", "aRequestHeader");

		List<Cookie> cookies = new ArrayList<Cookie>();
		cookies.add(new Cookie("last", "lastCookie"));

		List<SSEvent> events = ControllerUtil.performSseRequest(mockMvc, "sseProvider",
				"messageCookieValue5", null, headers, cookies);
		assertThat(events).hasSize(1);
		SSEvent event = events.get(0);

		assertThat(event.getEvent()).isEqualTo("messageCookieValue5");
		assertThat(event.getComment()).isEqualTo("comment of message null");
		assertThat(event.getData())
				.isEqualTo("aRequestHeader;null;default1;default2;lastCookie");
		assertThat(event.getId()).isEqualTo("122");
		assertThat(event.getRetry()).isNull();
	}

	@Test
	public void sseMultipleCookies2() throws Exception {
		HttpHeaders headers = new HttpHeaders();
		headers.add("requestHeader", "aRequestHeader");

		Map<String, String> params = new HashMap<String, String>();
		params.put("id", "33");

		List<Cookie> cookies = new ArrayList<Cookie>();
		cookies.add(new Cookie("last", "lastCookie"));
		cookies.add(new Cookie("cookie2", "2ndCookie"));

		List<SSEvent> events = ControllerUtil.performSseRequest(mockMvc, "sseProvider",
				"messageCookieValue5", params, headers, cookies);
		assertThat(events).hasSize(1);
		SSEvent event = events.get(0);

		assertThat(event.getEvent()).isEqualTo("messageCookieValue5");
		assertThat(event.getComment()).isEqualTo("comment of message 33");
		assertThat(event.getData())
				.isEqualTo("aRequestHeader;33;default1;2ndCookie;lastCookie");
		assertThat(event.getId()).isEqualTo("122");
		assertThat(event.getRetry()).isNull();
	}

	@Test
	public void sseMultipleCookies3() throws Exception {
		HttpHeaders headers = new HttpHeaders();
		headers.add("requestHeader", "aRequestHeader");

		Map<String, String> params = new HashMap<String, String>();
		params.put("id", "44");

		List<Cookie> cookies = new ArrayList<Cookie>();
		cookies.add(new Cookie("last", "last"));
		cookies.add(new Cookie("cookie1", "1st"));
		cookies.add(new Cookie("cookie2", "2nd"));

		List<SSEvent> events = ControllerUtil.performSseRequest(mockMvc, "sseProvider",
				"messageCookieValue5", params, headers, cookies);
		assertThat(events).hasSize(1);
		SSEvent event = events.get(0);

		assertThat(event.getEvent()).isEqualTo("messageCookieValue5");
		assertThat(event.getComment()).isEqualTo("comment of message 44");
		assertThat(event.getData()).isEqualTo("aRequestHeader;44;1st;2nd;last");
		assertThat(event.getId()).isEqualTo("122");
		assertThat(event.getRetry()).isNull();
	}

	@Test
	public void sseCookieWithConversion() throws Exception {
		List<Cookie> cookies = new ArrayList<Cookie>();
		cookies.add(new Cookie("intCookie", "2"));
		cookies.add(new Cookie("booleanCookie", "true"));

		List<SSEvent> events = ControllerUtil.performSseRequest(mockMvc, "sseProvider",
				"messageCookieValue6", null, null, cookies);
		assertThat(events).hasSize(1);
		SSEvent event = events.get(0);

		assertThat(event.getEvent()).isEqualTo("messageCookieValue6");
		assertThat(event.getComment()).isEqualTo("comment");
		assertThat(event.getData()).isEqualTo("theHeader;2;true");
		assertThat(event.getId()).isEqualTo("123");
		assertThat(event.getRetry()).isEqualTo(10000);
	}

}
