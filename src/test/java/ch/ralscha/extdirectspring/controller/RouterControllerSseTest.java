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

import java.util.Locale;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import ch.ralscha.extdirectspring.bean.SSEvent;

/**
 * Tests for {@link RouterController}.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:/testApplicationContext.xml")
public class RouterControllerSseTest {

	@Autowired
	private RouterController controller;

	private MockHttpServletResponse response;

	private MockHttpServletRequest request;

	@Before
	public void beforeTest() {
		response = new MockHttpServletResponse();
		request = new MockHttpServletRequest();
	}

	@Test
	public void sseBeanDoesNotExists() throws Exception {
		controller.sse("sseProviderXY", "message1", request, response, Locale.ENGLISH);
		SSEvent resp = ControllerUtil.readDirectSseResponse(response.getContentAsByteArray());

		assertThat(resp.getEvent()).isEqualTo("error");
		assertThat(resp.getComment()).isNull();
		assertThat(resp.getData()).isEqualTo("Server Error");
		assertThat(resp.getId()).isNull();
		assertThat(resp.getRetry()).isNull();
	}

	@Test
	public void sseNoArguments() throws Exception {

		controller.sse("sseProvider", "message1", request, response, Locale.ENGLISH);
		SSEvent resp = ControllerUtil.readDirectSseResponse(response.getContentAsByteArray());

		assertThat(resp.getEvent()).isNull();
		assertThat(resp.getComment()).isNull();
		assertThat(resp.getData()).startsWith("Successfully polled at: ");
		assertThat(resp.getId()).isNull();
		assertThat(resp.getRetry()).isNull();
	}

	@Test
	public void sseSupportedArguments() throws Exception {
		controller.sse("sseProvider", "message2", request, response, Locale.ENGLISH);
		SSEvent resp = ControllerUtil.readDirectSseResponse(response.getContentAsByteArray());

		assertThat(resp.getEvent()).isNull();
		assertThat(resp.getComment()).isNull();
		assertThat(resp.getData()).startsWith("Successfully polled at: ");
		assertThat(resp.getId()).isNull();
		assertThat(resp.getRetry()).isEqualTo(200000);
	}

	@Test
	public void sseRequiredArgument() throws Exception {
		request.setParameter("id", "2");

		controller.sse("sseProvider", "message3", request, response, Locale.ENGLISH);
		SSEvent resp = ControllerUtil.readDirectSseResponse(response.getContentAsByteArray());

		assertThat(resp.getEvent()).isNull();
		assertThat(resp.getComment()).isNull();
		assertThat(resp.getData()).startsWith("Result: 2");
		assertThat(resp.getId()).isNull();
		assertThat(resp.getRetry()).isNull();
	}

	@Test
	public void sseRequiredArgumentNoRequestParameter() throws Exception {

		controller.sse("sseProvider", "message3", request, response, Locale.ENGLISH);
		SSEvent resp = ControllerUtil.readDirectSseResponse(response.getContentAsByteArray());

		assertThat(resp.getEvent()).isEqualTo("error");
		assertThat(resp.getComment()).isNull();
		assertThat(resp.getData()).isEqualTo("Server Error");
		assertThat(resp.getId()).isNull();
		assertThat(resp.getRetry()).isNull();
	}

	@Test
	public void sseDefaultValueArgumentWithRequestParameter() throws Exception {
		request.setParameter("id", "7");
		request.setSession(new MockHttpSession());

		controller.sse("sseProvider", "message4", request, response, Locale.ENGLISH);
		SSEvent resp = ControllerUtil.readDirectSseResponse(response.getContentAsByteArray());

		assertThat(resp.getEvent()).isNull();
		assertThat(resp.getComment()).isNull();
		assertThat(resp.getData()).isEqualTo("14");
		assertThat(resp.getId()).isNull();
		assertThat(resp.getRetry()).isNull();

	}

	@Test
	public void sseDefaultValueArgumentWithoutRequestParameter() throws Exception {
		controller.sse("sseProvider", "message4", request, response, Locale.ENGLISH);
		SSEvent resp = ControllerUtil.readDirectSseResponse(response.getContentAsByteArray());

		assertThat(resp.getEvent()).isNull();
		assertThat(resp.getComment()).isNull();
		assertThat(resp.getData()).isEqualTo("2");
		assertThat(resp.getId()).isNull();
		assertThat(resp.getRetry()).isNull();
	}

	@Test
	public void sseNotRequiredArgumentWithRequestParameter() throws Exception {
		request.setParameter("id", "3");

		controller.sse("sseProvider", "message5", request, response, Locale.ENGLISH);
		SSEvent resp = ControllerUtil.readDirectSseResponse(response.getContentAsByteArray());

		assertThat(resp.getEvent()).isNull();
		assertThat(resp.getComment()).isNull();
		assertThat(resp.getData()).isEqualTo("6");
		assertThat(resp.getId()).isNull();
		assertThat(resp.getRetry()).isNull();
	}

	@Test
	public void sseNotRequiredArgumentWithoutRequestParameter() throws Exception {
		controller.sse("sseProvider", "message5", request, response, Locale.ENGLISH);
		SSEvent resp = ControllerUtil.readDirectSseResponse(response.getContentAsByteArray());

		assertThat(resp.getEvent()).isNull();
		assertThat(resp.getComment()).isNull();
		assertThat(resp.getData()).isNull();
		assertThat(resp.getId()).isNull();
		assertThat(resp.getRetry()).isNull();
	}

	@Test
	public void sseRequiredHeaderWithoutValue() throws Exception {
		request.addHeader("header", "headerValue");

		controller.sse("sseProvider", "message7", request, response, Locale.ENGLISH);
		SSEvent resp = ControllerUtil.readDirectSseResponse(response.getContentAsByteArray());

		assertThat(resp.getEvent()).isNull();
		assertThat(resp.getComment()).isNull();
		assertThat(resp.getData()).startsWith("null;null;headerValue");
		assertThat(resp.getId()).isEqualTo("1");
		assertThat(resp.getRetry()).isNull();
	}

	@Test
	public void sseRequiredHeaderWithValue() throws Exception {
		request.setParameter("id", "1");
		request.addHeader("header", "headerValue");
		request.addHeader("anotherName", "headerValue1");
		request.addHeader("anotherName", "headerValue2");

		controller.sse("sseProvider", "message8", request, response, Locale.ENGLISH);
		SSEvent resp = ControllerUtil.readDirectSseResponse(response.getContentAsByteArray());

		assertThat(resp.getEvent()).isNull();
		assertThat(resp.getComment()).isNull();
		assertThat(resp.getData()).startsWith("1;headerValue1");
		assertThat(resp.getId()).isNull();
		assertThat(resp.getRetry()).isNull();

		request = new MockHttpServletRequest();
		response = new MockHttpServletResponse();
		request.setParameter("id", "2");

		controller.sse("sseProvider", "message8", request, response, Locale.ENGLISH);
		resp = ControllerUtil.readDirectSseResponse(response.getContentAsByteArray());

		assertThat(resp.getEvent()).isEqualTo("error");
		assertThat(resp.getComment()).isNull();
		assertThat(resp.getData()).startsWith("Server Error");
		assertThat(resp.getId()).isNull();
		assertThat(resp.getRetry()).isNull();
	}

	@Test
	public void sseRequiredHeaderWithValueAndDefault1() throws Exception {
		request.addHeader("header", "headerValue");
		request.addHeader("anotherName", "headerValue1");

		controller.sse("sseProvider", "message9", request, response, Locale.ENGLISH);
		SSEvent resp = ControllerUtil.readDirectSseResponse(response.getContentAsByteArray());

		assertThat(resp.getEvent()).isEqualTo("message9");
		assertThat(resp.getComment()).isNull();
		assertThat(resp.getData()).isEqualTo("headerValue1");
		assertThat(resp.getId()).isNull();
		assertThat(resp.getRetry()).isNull();
	}

	@Test
	public void sseRequiredHeaderWithValueAndDefault2() throws Exception {

		controller.sse("sseProvider", "message9", request, response, Locale.ENGLISH);
		SSEvent resp = ControllerUtil.readDirectSseResponse(response.getContentAsByteArray());

		assertThat(resp.getEvent()).isEqualTo("message9");
		assertThat(resp.getComment()).isNull();
		assertThat(resp.getData()).isEqualTo("default");
		assertThat(resp.getId()).isNull();
		assertThat(resp.getRetry()).isNull();
	}

	@Test
	public void sseOptionalHeaderWithoutValueAndDefault1() throws Exception {
		request.addHeader("header", "headerValue");
		request.addHeader("anotherName", "headerValue1");

		controller.sse("sseProvider", "message10", request, response, Locale.ENGLISH);
		SSEvent resp = ControllerUtil.readDirectSseResponse(response.getContentAsByteArray());

		assertThat(resp.getEvent()).isEqualTo("message10");
		assertThat(resp.getComment()).isEqualTo("comment of message headerValue");
		assertThat(resp.getData()).isEqualTo("headerValue");
		assertThat(resp.getId()).isNull();
		assertThat(resp.getRetry()).isNull();
	}

	@Test
	public void sseOptionalHeaderWithoutValueAndDefault2() throws Exception {
		controller.sse("sseProvider", "message10", request, response, Locale.ENGLISH);
		SSEvent resp = ControllerUtil.readDirectSseResponse(response.getContentAsByteArray());

		assertThat(resp.getEvent()).isEqualTo("message10");
		assertThat(resp.getComment()).isEqualTo("comment of message default");
		assertThat(resp.getData()).isEqualTo("default");
		assertThat(resp.getId()).isNull();
		assertThat(resp.getRetry()).isNull();
	}

	@Test
	public void sseMultipleHeaders1() throws Exception {
		request.addHeader("last", "lastHeader");

		controller.sse("sseProvider", "message11", request, response, Locale.ENGLISH);
		SSEvent resp = ControllerUtil.readDirectSseResponse(response.getContentAsByteArray());

		assertThat(resp.getEvent()).isEqualTo("message11");
		assertThat(resp.getComment()).isEqualTo("comment of message null");
		assertThat(resp.getData()).isEqualTo("null;default1;default2;lastHeader");
		assertThat(resp.getId()).isEqualTo("122");
		assertThat(resp.getRetry()).isNull();
	}

	@Test
	public void sseMultipleHeaders2() throws Exception {
		request.setParameter("id", "33");
		request.addHeader("last", "lastHeader");
		request.addHeader("header2", "2ndHeader");

		controller.sse("sseProvider", "message11", request, response, Locale.ENGLISH);
		SSEvent resp = ControllerUtil.readDirectSseResponse(response.getContentAsByteArray());

		assertThat(resp.getEvent()).isEqualTo("message11");
		assertThat(resp.getComment()).isEqualTo("comment of message 33");
		assertThat(resp.getData()).isEqualTo("33;default1;2ndHeader;lastHeader");
		assertThat(resp.getId()).isEqualTo("122");
		assertThat(resp.getRetry()).isNull();
	}

	@Test
	public void sseMultipleHeaders3() throws Exception {
		request.setParameter("id", "44");
		request.addHeader("last", "last");
		request.addHeader("header1", "1st");
		request.addHeader("header2", "2nd");

		controller.sse("sseProvider", "message11", request, response, Locale.ENGLISH);
		SSEvent resp = ControllerUtil.readDirectSseResponse(response.getContentAsByteArray());

		assertThat(resp.getEvent()).isEqualTo("message11");
		assertThat(resp.getComment()).isEqualTo("comment of message 44");
		assertThat(resp.getData()).isEqualTo("44;1st;2nd;last");
		assertThat(resp.getId()).isEqualTo("122");
		assertThat(resp.getRetry()).isNull();
	}

	@Test
	public void sseHeaderWithConversion() throws Exception {

		request.addHeader("intHeader", "2");
		request.addHeader("booleanHeader", "true");

		controller.sse("sseProvider", "message12", request, response, Locale.ENGLISH);
		SSEvent resp = ControllerUtil.readDirectSseResponse(response.getContentAsByteArray());

		assertThat(resp.getEvent()).isEqualTo("message12");
		assertThat(resp.getComment()).isEqualTo("comment");
		assertThat(resp.getData()).isEqualTo("2;true");
		assertThat(resp.getId()).isEqualTo("123");
		assertThat(resp.getRetry()).isEqualTo(10000);
	}
}
