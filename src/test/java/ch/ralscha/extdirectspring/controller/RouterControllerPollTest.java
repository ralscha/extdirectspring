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

import java.util.LinkedHashMap;
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
		mockMvc = MockMvcBuilders.webAppContextSetup(wac).build();
	}

	@Test
	public void pollBeanDoesNotExists() throws Exception {

		ExtDirectPollResponse resp = ControllerUtil.performPollRequest(mockMvc,
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
		ExtDirectPollResponse resp = ControllerUtil.performPollRequest(mockMvc,
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
		ExtDirectPollResponse resp = ControllerUtil.performPollRequest(mockMvc,
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

		ExtDirectPollResponse resp = ControllerUtil.performPollRequest(mockMvc,
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

		ExtDirectPollResponse resp = ControllerUtil.performPollRequest(mockMvc,
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

		ExtDirectPollResponse resp = ControllerUtil.performPollRequest(mockMvc,
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
		ExtDirectPollResponse resp = ControllerUtil.performPollRequest(mockMvc,
				"pollProvider", "handleMessage4", "message4", null, null, true);

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

		ExtDirectPollResponse resp = ControllerUtil.performPollRequest(mockMvc,
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
		ExtDirectPollResponse resp = ControllerUtil.performPollRequest(mockMvc,
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

		ExtDirectPollResponse resp = ControllerUtil.performPollRequest(mockMvc,
				"pollProvider", "message7", "message7", null, headers);

		assertThat(resp).isNotNull();
		assertThat(resp.getType()).isEqualTo("event");
		assertThat(resp.getName()).isEqualTo("message7");
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

		ExtDirectPollResponse resp = ControllerUtil.performPollRequest(mockMvc,
				"pollProvider", "message8", "message8", params, headers);

		assertThat(resp).isNotNull();
		assertThat(resp.getType()).isEqualTo("event");
		assertThat(resp.getName()).isEqualTo("message8");
		assertThat(resp.getData()).isEqualTo("1;headerValue1");
		assertThat(resp.getWhere()).isNull();
		assertThat(resp.getMessage()).isNull();

		params.clear();
		params.put("id", "2");

		resp = ControllerUtil.performPollRequest(mockMvc, "pollProvider", "message8",
				"message8", params, null, true);

		assertThat(resp).isNotNull();
		assertThat(resp.getType()).isEqualTo("exception");
		assertThat(resp.getName()).isEqualTo("message8");
		assertThat(resp.getData()).isNull();
		assertThat(resp.getWhere()).isNull();
		assertThat(resp.getMessage()).isEqualTo("Server Error");
	}

	@Test
	public void pollRequiredHeaderWithValueAndDefault1() throws Exception {
		HttpHeaders headers = new HttpHeaders();
		headers.add("header", "headerValue");
		headers.add("anotherName", "headerValue1");

		ExtDirectPollResponse resp = ControllerUtil.performPollRequest(mockMvc,
				"pollProvider", "message9", "message9", null, headers);

		assertThat(resp).isNotNull();
		assertThat(resp.getType()).isEqualTo("event");
		assertThat(resp.getName()).isEqualTo("message9");
		assertThat(resp.getData()).isEqualTo("headerValue1");
		assertThat(resp.getWhere()).isNull();
		assertThat(resp.getMessage()).isNull();
	}

	@Test
	public void pollRequiredHeaderWithValueAndDefault2() throws Exception {

		ExtDirectPollResponse resp = ControllerUtil.performPollRequest(mockMvc,
				"pollProvider", "message9", "message9", null, null);

		assertThat(resp).isNotNull();
		assertThat(resp.getType()).isEqualTo("event");
		assertThat(resp.getName()).isEqualTo("message9");
		assertThat(resp.getData()).isEqualTo("default");
		assertThat(resp.getWhere()).isNull();
		assertThat(resp.getMessage()).isNull();
	}

	@Test
	public void pollOptionalHeaderWithoutValueAndDefault1() throws Exception {
		HttpHeaders headers = new HttpHeaders();
		headers.add("header", "headerValue");
		headers.add("anotherName", "headerValue1");

		ExtDirectPollResponse resp = ControllerUtil.performPollRequest(mockMvc,
				"pollProvider", "message10", "message10", null, headers);

		assertThat(resp).isNotNull();
		assertThat(resp.getType()).isEqualTo("event");
		assertThat(resp.getName()).isEqualTo("message10");
		assertThat(resp.getData()).isEqualTo("headerValue");
		assertThat(resp.getWhere()).isNull();
		assertThat(resp.getMessage()).isNull();
	}

	@Test
	public void pollOptionalHeaderWithoutValueAndDefault2() throws Exception {
		ExtDirectPollResponse resp = ControllerUtil.performPollRequest(mockMvc,
				"pollProvider", "message10", "message10", null, null);

		assertThat(resp).isNotNull();
		assertThat(resp.getType()).isEqualTo("event");
		assertThat(resp.getName()).isEqualTo("message10");
		assertThat(resp.getData()).isEqualTo("default");
		assertThat(resp.getWhere()).isNull();
		assertThat(resp.getMessage()).isNull();
	}

	@Test
	public void pollMultipleHeaders1() throws Exception {
		HttpHeaders headers = new HttpHeaders();
		headers.add("last", "lastHeader");

		ExtDirectPollResponse resp = ControllerUtil.performPollRequest(mockMvc,
				"pollProvider", "message11", "message11", null, headers);
		assertThat(resp).isNotNull();
		assertThat(resp.getType()).isEqualTo("event");
		assertThat(resp.getName()).isEqualTo("message11");
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

		ExtDirectPollResponse resp = ControllerUtil.performPollRequest(mockMvc,
				"pollProvider", "message11", "message11", params, headers);

		assertThat(resp).isNotNull();
		assertThat(resp.getType()).isEqualTo("event");
		assertThat(resp.getName()).isEqualTo("message11");
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

		ExtDirectPollResponse resp = ControllerUtil.performPollRequest(mockMvc,
				"pollProvider", "message11", "message11", params, headers);

		assertThat(resp).isNotNull();
		assertThat(resp.getType()).isEqualTo("event");
		assertThat(resp.getName()).isEqualTo("message11");
		assertThat(resp.getData()).isEqualTo("44;1st;2nd;last");
		assertThat(resp.getWhere()).isNull();
		assertThat(resp.getMessage()).isNull();
	}

	@Test
	public void pollHeaderWithConversion() throws Exception {

		HttpHeaders headers = new HttpHeaders();
		headers.add("intHeader", "2");
		headers.add("booleanHeader", "true");

		ExtDirectPollResponse resp = ControllerUtil.performPollRequest(mockMvc,
				"pollProvider", "message12", "message12", null, headers);

		assertThat(resp).isNotNull();
		assertThat(resp.getType()).isEqualTo("event");
		assertThat(resp.getName()).isEqualTo("message12");
		assertThat(resp.getData()).isEqualTo("2;true");
		assertThat(resp.getWhere()).isNull();
		assertThat(resp.getMessage()).isNull();

	}
}
