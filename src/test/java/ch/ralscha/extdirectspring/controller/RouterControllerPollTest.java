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

import static org.fest.assertions.Assertions.assertThat;

import java.util.Locale;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import ch.ralscha.extdirectspring.bean.ExtDirectPollResponse;

/**
 * Tests for {@link RouterController}.
 * 
 * @author Ralph Schaer
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:/testApplicationContext.xml")
public class RouterControllerPollTest {

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
	public void pollNoArguments() throws Exception {

		controller.poll("pollProvider", "handleMessage1", "message1", request, response, Locale.ENGLISH);
		ExtDirectPollResponse resp = ControllerUtil.readDirectPollResponse(response.getContentAsByteArray());

		assertThat(resp).isNotNull();
		assertThat(resp.getType()).isEqualTo("event");
		assertThat(resp.getName()).isEqualTo("message1");
		assertThat((String) resp.getData()).startsWith("Successfully polled at: ");
		assertThat(resp.getWhere()).isNull();
		assertThat(resp.getMessage()).isNull();
	}

	@Test
	public void pollSupportedArguments() throws Exception {
		controller.poll("pollProvider", "handleMessage2", "message2", request, response, Locale.ENGLISH);
		ExtDirectPollResponse resp = ControllerUtil.readDirectPollResponse(response.getContentAsByteArray());

		assertThat(resp).isNotNull();
		assertThat(resp.getType()).isEqualTo("event");
		assertThat(resp.getName()).isEqualTo("message2");
		assertThat((String) resp.getData()).startsWith("Successfully polled at: ");
		assertThat(resp.getWhere()).isNull();
		assertThat(resp.getMessage()).isNull();
	}

	@Test
	public void pollRequiredArgument() throws Exception {
		request.setParameter("id", "2");

		controller.poll("pollProvider", "handleMessage3", "message3", request, response, Locale.ENGLISH);
		ExtDirectPollResponse resp = ControllerUtil.readDirectPollResponse(response.getContentAsByteArray());

		assertThat(resp).isNotNull();
		assertThat(resp.getType()).isEqualTo("event");
		assertThat(resp.getName()).isEqualTo("message3");
		assertThat(resp.getData()).isEqualTo("Result: 2");
		assertThat(resp.getWhere()).isNull();
		assertThat(resp.getMessage()).isNull();
	}

	@Test
	public void pollRequiredArgumentNoRequestParameter() throws Exception {

		controller.poll("pollProvider", "handleMessage3", "message3", request, response, Locale.ENGLISH);
		ExtDirectPollResponse resp = ControllerUtil.readDirectPollResponse(response.getContentAsByteArray());

		assertThat(resp).isNotNull();
		assertThat(resp.getType()).isEqualTo("exception");
		assertThat(resp.getName()).isEqualTo("message3");
		assertThat(resp.getData()).isNull();
		assertThat(resp.getMessage()).isEqualTo("Server Error");
		assertThat(resp.getWhere()).isNull();
	}

	@Test
	public void pollDefaultValueArgumentWithRequestParameter() throws Exception {
		request.setParameter("id", "7");

		controller.poll("pollProvider", "handleMessage4", "message4", request, response, Locale.ENGLISH);
		ExtDirectPollResponse resp = ControllerUtil.readDirectPollResponse(response.getContentAsByteArray());

		assertThat(resp).isNotNull();
		assertThat(resp.getType()).isEqualTo("event");
		assertThat(resp.getName()).isEqualTo("message4");
		assertThat(resp.getData()).isEqualTo(Integer.valueOf(14));
		assertThat(resp.getWhere()).isNull();
		assertThat(resp.getMessage()).isNull();
	}

	@Test
	public void pollDefaultValueArgumentWithoutRequestParameter() throws Exception {
		controller.poll("pollProvider", "handleMessage4", "message4", request, response, Locale.ENGLISH);
		ExtDirectPollResponse resp = ControllerUtil.readDirectPollResponse(response.getContentAsByteArray());

		assertThat(resp).isNotNull();
		assertThat(resp.getType()).isEqualTo("event");
		assertThat(resp.getName()).isEqualTo("message4");
		assertThat(resp.getData()).isEqualTo(Integer.valueOf(2));
		assertThat(resp.getWhere()).isNull();
		assertThat(resp.getMessage()).isNull();
	}

	@Test
	public void pollNotRequiredArgumentWithRequestParameter() throws Exception {
		request.setParameter("id", "3");

		controller.poll("pollProvider", "handleMessage5", "message5", request, response, Locale.ENGLISH);
		ExtDirectPollResponse resp = ControllerUtil.readDirectPollResponse(response.getContentAsByteArray());

		assertThat(resp).isNotNull();
		assertThat(resp.getType()).isEqualTo("event");
		assertThat(resp.getName()).isEqualTo("message5");
		assertThat(resp.getData()).isEqualTo(Integer.valueOf(6));
		assertThat(resp.getWhere()).isNull();
		assertThat(resp.getMessage()).isNull();
	}

	@Test
	public void pollNotRequiredArgumentWithoutRequestParameter() throws Exception {
		controller.poll("pollProvider", "handleMessage5", "message5", request, response, Locale.ENGLISH);
		ExtDirectPollResponse resp = ControllerUtil.readDirectPollResponse(response.getContentAsByteArray());

		assertThat(resp).isNotNull();
		assertThat(resp.getType()).isEqualTo("event");
		assertThat(resp.getName()).isEqualTo("message5");
		assertThat(resp.getData()).isNull();
		assertThat(resp.getWhere()).isNull();
		assertThat(resp.getMessage()).isNull();
	}

	@Test
	public void pollRequiredHeaderWithoutValue() throws Exception {
		request.addHeader("header", "headerValue");

		controller.poll("pollProvider", "message7", "message7", request, response, Locale.ENGLISH);
		ExtDirectPollResponse resp = ControllerUtil.readDirectPollResponse(response.getContentAsByteArray());

		assertThat(resp).isNotNull();
		assertThat(resp.getType()).isEqualTo("event");
		assertThat(resp.getName()).isEqualTo("message7");
		assertThat(resp.getData()).isEqualTo("null;null;headerValue");
		assertThat(resp.getWhere()).isNull();
		assertThat(resp.getMessage()).isNull();
	}

	@Test
	public void pollRequiredHeaderWithValue() throws Exception {
		request.setParameter("id", "1");
		request.addHeader("header", "headerValue");
		request.addHeader("anotherName", "headerValue1");
		request.addHeader("anotherName", "headerValue2");

		controller.poll("pollProvider", "message8", "message8", request, response, Locale.ENGLISH);
		ExtDirectPollResponse resp = ControllerUtil.readDirectPollResponse(response.getContentAsByteArray());

		assertThat(resp).isNotNull();
		assertThat(resp.getType()).isEqualTo("event");
		assertThat(resp.getName()).isEqualTo("message8");
		assertThat(resp.getData()).isEqualTo("1;headerValue1");
		assertThat(resp.getWhere()).isNull();
		assertThat(resp.getMessage()).isNull();
	}

	@Test
	public void pollRequiredHeaderWithValueAndDefault1() throws Exception {
		request.addHeader("header", "headerValue");
		request.addHeader("anotherName", "headerValue1");

		controller.poll("pollProvider", "message9", "message9", request, response, Locale.ENGLISH);
		ExtDirectPollResponse resp = ControllerUtil.readDirectPollResponse(response.getContentAsByteArray());

		assertThat(resp).isNotNull();
		assertThat(resp.getType()).isEqualTo("event");
		assertThat(resp.getName()).isEqualTo("message9");
		assertThat(resp.getData()).isEqualTo("headerValue1");
		assertThat(resp.getWhere()).isNull();
		assertThat(resp.getMessage()).isNull();
	}

	@Test
	public void pollRequiredHeaderWithValueAndDefault2() throws Exception {

		controller.poll("pollProvider", "message9", "message9", request, response, Locale.ENGLISH);
		ExtDirectPollResponse resp = ControllerUtil.readDirectPollResponse(response.getContentAsByteArray());

		assertThat(resp).isNotNull();
		assertThat(resp.getType()).isEqualTo("event");
		assertThat(resp.getName()).isEqualTo("message9");
		assertThat(resp.getData()).isEqualTo("default");
		assertThat(resp.getWhere()).isNull();
		assertThat(resp.getMessage()).isNull();
	}

	@Test
	public void pollOptionalHeaderWithoutValueAndDefault1() throws Exception {
		request.addHeader("header", "headerValue");
		request.addHeader("anotherName", "headerValue1");

		controller.poll("pollProvider", "message10", "message10", request, response, Locale.ENGLISH);
		ExtDirectPollResponse resp = ControllerUtil.readDirectPollResponse(response.getContentAsByteArray());

		assertThat(resp).isNotNull();
		assertThat(resp.getType()).isEqualTo("event");
		assertThat(resp.getName()).isEqualTo("message10");
		assertThat(resp.getData()).isEqualTo("headerValue");
		assertThat(resp.getWhere()).isNull();
		assertThat(resp.getMessage()).isNull();
	}

	@Test
	public void pollOptionalHeaderWithoutValueAndDefault2() throws Exception {
		controller.poll("pollProvider", "message10", "message10", request, response, Locale.ENGLISH);
		ExtDirectPollResponse resp = ControllerUtil.readDirectPollResponse(response.getContentAsByteArray());

		assertThat(resp).isNotNull();
		assertThat(resp.getType()).isEqualTo("event");
		assertThat(resp.getName()).isEqualTo("message10");
		assertThat(resp.getData()).isEqualTo("default");
		assertThat(resp.getWhere()).isNull();
		assertThat(resp.getMessage()).isNull();
	}

	@Test
	public void pollMultipleHeaders1() throws Exception {
		request.addHeader("last", "lastHeader");

		controller.poll("pollProvider", "message11", "message11", request, response, Locale.ENGLISH);
		ExtDirectPollResponse resp = ControllerUtil.readDirectPollResponse(response.getContentAsByteArray());

		assertThat(resp).isNotNull();
		assertThat(resp.getType()).isEqualTo("event");
		assertThat(resp.getName()).isEqualTo("message11");
		assertThat(resp.getData()).isEqualTo("null;default1;default2;lastHeader");
		assertThat(resp.getWhere()).isNull();
		assertThat(resp.getMessage()).isNull();
	}

	@Test
	public void pollMultipleHeaders2() throws Exception {
		request.setParameter("id", "33");
		request.addHeader("last", "lastHeader");
		request.addHeader("header2", "2ndHeader");

		controller.poll("pollProvider", "message11", "message11", request, response, Locale.ENGLISH);
		ExtDirectPollResponse resp = ControllerUtil.readDirectPollResponse(response.getContentAsByteArray());

		assertThat(resp).isNotNull();
		assertThat(resp.getType()).isEqualTo("event");
		assertThat(resp.getName()).isEqualTo("message11");
		assertThat(resp.getData()).isEqualTo("33;default1;2ndHeader;lastHeader");
		assertThat(resp.getWhere()).isNull();
		assertThat(resp.getMessage()).isNull();
	}

	@Test
	public void pollMultipleHeaders3() throws Exception {
		request.setParameter("id", "44");
		request.addHeader("last", "last");
		request.addHeader("header1", "1st");
		request.addHeader("header2", "2nd");

		controller.poll("pollProvider", "message11", "message11", request, response, Locale.ENGLISH);
		ExtDirectPollResponse resp = ControllerUtil.readDirectPollResponse(response.getContentAsByteArray());

		assertThat(resp).isNotNull();
		assertThat(resp.getType()).isEqualTo("event");
		assertThat(resp.getName()).isEqualTo("message11");
		assertThat(resp.getData()).isEqualTo("44;1st;2nd;last");
		assertThat(resp.getWhere()).isNull();
		assertThat(resp.getMessage()).isNull();
	}

	@Test
	public void pollHeaderWithConversion() throws Exception {

		request.addHeader("intHeader", "2");
		request.addHeader("booleanHeader", "true");

		controller.poll("pollProvider", "message12", "message12", request, response, Locale.ENGLISH);
		ExtDirectPollResponse resp = ControllerUtil.readDirectPollResponse(response.getContentAsByteArray());

		assertThat(resp).isNotNull();
		assertThat(resp.getType()).isEqualTo("event");
		assertThat(resp.getName()).isEqualTo("message12");
		assertThat(resp.getData()).isEqualTo("2;true");
		assertThat(resp.getWhere()).isNull();
		assertThat(resp.getMessage()).isNull();

	}
}
