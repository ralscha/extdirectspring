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
package ch.ralscha.extdirectspring.controller;

import static org.fest.assertions.api.Assertions.assertThat;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

import ch.ralscha.extdirectspring.bean.SSEvent;

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration("classpath:/testApplicationContext.xml")
public class RouterControllerSseTest {

	@Autowired
	private WebApplicationContext wac;

	private MockMvc mockMvc;

	@Before
	public void setupMockMvc() throws Exception {
		mockMvc = MockMvcBuilders.webAppContextSetup(wac).build();
	}

	@Test
	public void sseBeanDoesNotExists() throws Exception {
		List<SSEvent> events = ControllerUtil.performSseRequest(mockMvc, "sseProviderXY", "message1", null, null);
		assertThat(events).hasSize(1);
		SSEvent event = events.get(0);

		assertThat(event.getEvent()).isEqualTo("error");
		assertThat(event.getComment()).isNull();
		assertThat(event.getData()).isEqualTo("Server Error");
		assertThat(event.getId()).isNull();
		assertThat(event.getRetry()).isNull();
	}

	@Test
	public void sseNoArguments() throws Exception {

		List<SSEvent> events = ControllerUtil.performSseRequest(mockMvc, "sseProvider", "message1", null, null);
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
		List<SSEvent> events = ControllerUtil.performSseRequest(mockMvc, "sseProvider", "message2", null, null);
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

		List<SSEvent> events = ControllerUtil.performSseRequest(mockMvc, "sseProvider", "message3", params, null);
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

		List<SSEvent> events = ControllerUtil.performSseRequest(mockMvc, "sseProvider", "message3", null, null);
		assertThat(events).hasSize(1);
		SSEvent event = events.get(0);

		assertThat(event.getEvent()).isEqualTo("error");
		assertThat(event.getComment()).isNull();
		assertThat(event.getData()).isEqualTo("Server Error");
		assertThat(event.getId()).isNull();
		assertThat(event.getRetry()).isNull();
	}

	@Test
	public void sseDefaultValueArgumentWithRequestParameter() throws Exception {
		Map<String, String> params = new HashMap<String, String>();
		params.put("id", "7");

		List<SSEvent> events = ControllerUtil.performSseRequest(mockMvc, "sseProvider", "message4", params, null);
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
		List<SSEvent> events = ControllerUtil.performSseRequest(mockMvc, "sseProvider", "message4", null, null);
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

		List<SSEvent> events = ControllerUtil.performSseRequest(mockMvc, "sseProvider", "message5", params, null);
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
		List<SSEvent> events = ControllerUtil.performSseRequest(mockMvc, "sseProvider", "message5", null, null);
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

		List<SSEvent> events = ControllerUtil.performSseRequest(mockMvc, "sseProvider", "message7", null, headers);
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

		List<SSEvent> events = ControllerUtil.performSseRequest(mockMvc, "sseProvider", "message8", params, headers);
		assertThat(events).hasSize(1);
		SSEvent event = events.get(0);

		assertThat(event.getEvent()).isNull();
		assertThat(event.getComment()).isNull();
		assertThat(event.getData()).startsWith("1;headerValue1");
		assertThat(event.getId()).isNull();
		assertThat(event.getRetry()).isNull();

		params.clear();
		params.put("id", "2");

		events = ControllerUtil.performSseRequest(mockMvc, "sseProvider", "message8", params, null);
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

		List<SSEvent> events = ControllerUtil.performSseRequest(mockMvc, "sseProvider", "message9", null, headers);
		assertThat(events).hasSize(1);
		SSEvent event = events.get(0);

		assertThat(event.getEvent()).isEqualTo("message9");
		assertThat(event.getComment()).isNull();
		assertThat(event.getData()).isEqualTo("headerValue1");
		assertThat(event.getId()).isNull();
		assertThat(event.getRetry()).isNull();
	}

	@Test
	public void sseRequiredHeaderWithValueAndDefault2() throws Exception {

		List<SSEvent> events = ControllerUtil.performSseRequest(mockMvc, "sseProvider", "message9", null, null);
		assertThat(events).hasSize(1);
		SSEvent event = events.get(0);

		assertThat(event.getEvent()).isEqualTo("message9");
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

		List<SSEvent> events = ControllerUtil.performSseRequest(mockMvc, "sseProvider", "message10", null, headers);
		assertThat(events).hasSize(1);
		SSEvent event = events.get(0);

		assertThat(event.getEvent()).isEqualTo("message10");
		assertThat(event.getComment()).isEqualTo("comment of message headerValue");
		assertThat(event.getData()).isEqualTo("headerValue");
		assertThat(event.getId()).isNull();
		assertThat(event.getRetry()).isNull();
	}

	@Test
	public void sseOptionalHeaderWithoutValueAndDefault2() throws Exception {
		List<SSEvent> events = ControllerUtil.performSseRequest(mockMvc, "sseProvider", "message10", null, null);
		assertThat(events).hasSize(1);
		SSEvent event = events.get(0);

		assertThat(event.getEvent()).isEqualTo("message10");
		assertThat(event.getComment()).isEqualTo("comment of message default");
		assertThat(event.getData()).isEqualTo("default");
		assertThat(event.getId()).isNull();
		assertThat(event.getRetry()).isNull();
	}

	@Test
	public void sseMultipleHeaders1() throws Exception {
		HttpHeaders headers = new HttpHeaders();
		headers.add("last", "lastHeader");

		List<SSEvent> events = ControllerUtil.performSseRequest(mockMvc, "sseProvider", "message11", null, headers);
		assertThat(events).hasSize(1);
		SSEvent event = events.get(0);

		assertThat(event.getEvent()).isEqualTo("message11");
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

		List<SSEvent> events = ControllerUtil.performSseRequest(mockMvc, "sseProvider", "message11", params, headers);
		assertThat(events).hasSize(1);
		SSEvent event = events.get(0);

		assertThat(event.getEvent()).isEqualTo("message11");
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

		List<SSEvent> events = ControllerUtil.performSseRequest(mockMvc, "sseProvider", "message11", params, headers);
		assertThat(events).hasSize(1);
		SSEvent event = events.get(0);

		assertThat(event.getEvent()).isEqualTo("message11");
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

		List<SSEvent> events = ControllerUtil.performSseRequest(mockMvc, "sseProvider", "message12", null, headers);
		assertThat(events).hasSize(1);
		SSEvent event = events.get(0);

		assertThat(event.getEvent()).isEqualTo("message12");
		assertThat(event.getComment()).isEqualTo("comment");
		assertThat(event.getData()).isEqualTo("2;true");
		assertThat(event.getId()).isEqualTo("123");
		assertThat(event.getRetry()).isEqualTo(10000);
	}

	@Test
	public void sseWithWriterAndStringReturn() throws Exception {
		List<SSEvent> events = ControllerUtil.performSseRequest(mockMvc, "sseProvider", "message13", null, null);
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
		List<SSEvent> events = ControllerUtil.performSseRequest(mockMvc, "sseProvider", "message14", null, null);
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
		List<SSEvent> events = ControllerUtil.performSseRequest(mockMvc, "sseProvider", "message15", null, null);
		assertThat(events).hasSize(3);

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

	}
}
