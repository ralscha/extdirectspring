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
		ExtDirectPollResponse resp = controller.poll("pollProvider", "handleMessage1", "message1", request, response,
				Locale.ENGLISH);
		assertThat(resp).isNotNull();
		assertThat(resp.getType()).isEqualTo("event");
		assertThat(resp.getName()).isEqualTo("message1");
		assertThat(((String) resp.getData()).startsWith("Successfully polled at: ")).isTrue();
		assertThat(resp.getWhere()).isNull();
		assertThat(resp.getMessage()).isNull();
	}

	@Test
	public void pollSupportedArguments() throws Exception {
		ExtDirectPollResponse resp = controller.poll("pollProvider", "handleMessage2", "message2", request, response,
				Locale.ENGLISH);
		assertThat(resp).isNotNull();
		assertThat(resp.getType()).isEqualTo("event");
		assertThat(resp.getName()).isEqualTo("message2");
		assertThat(((String) resp.getData()).startsWith("Successfully polled at: ")).isTrue();
		assertThat(resp.getWhere()).isNull();
		assertThat(resp.getMessage()).isNull();
	}

	@Test
	public void pollRequiredArgument() throws Exception {
		request.setParameter("id", "2");
		ExtDirectPollResponse resp = controller.poll("pollProvider", "handleMessage3", "message3", request, response,
				Locale.ENGLISH);
		assertThat(resp).isNotNull();
		assertThat(resp.getType()).isEqualTo("event");
		assertThat(resp.getName()).isEqualTo("message3");
		assertThat(resp.getData()).isEqualTo("Result: 2");
		assertThat(resp.getWhere()).isNull();
		assertThat(resp.getMessage()).isNull();
	}

	@Test
	public void pollRequiredArgumentNoRequestParameter() throws Exception {
		ExtDirectPollResponse resp = controller.poll("pollProvider", "handleMessage3", "message3", request, response,
				Locale.ENGLISH);
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
		ExtDirectPollResponse resp = controller.poll("pollProvider", "handleMessage4", "message4", request, response,
				Locale.ENGLISH);
		assertThat(resp).isNotNull();
		assertThat(resp.getType()).isEqualTo("event");
		assertThat(resp.getName()).isEqualTo("message4");
		assertThat(resp.getData()).isEqualTo(Integer.valueOf(14));
		assertThat(resp.getWhere()).isNull();
		assertThat(resp.getMessage()).isNull();
	}

	@Test
	public void pollDefaultValueArgumentWithoutRequestParameter() throws Exception {
		ExtDirectPollResponse resp = controller.poll("pollProvider", "handleMessage4", "message4", request, response,
				Locale.ENGLISH);
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
		ExtDirectPollResponse resp = controller.poll("pollProvider", "handleMessage5", "message5", request, response,
				Locale.ENGLISH);
		assertThat(resp).isNotNull();
		assertThat(resp.getType()).isEqualTo("event");
		assertThat(resp.getName()).isEqualTo("message5");
		assertThat(resp.getData()).isEqualTo(Integer.valueOf(6));
		assertThat(resp.getWhere()).isNull();
		assertThat(resp.getMessage()).isNull();
	}

	@Test
	public void pollNotRequiredArgumentWithoutRequestParameter() throws Exception {
		ExtDirectPollResponse resp = controller.poll("pollProvider", "handleMessage5", "message5", request, response,
				Locale.ENGLISH);
		assertThat(resp).isNotNull();
		assertThat(resp.getType()).isEqualTo("event");
		assertThat(resp.getName()).isEqualTo("message5");
		assertThat(resp.getData()).isNull();
		assertThat(resp.getWhere()).isNull();
		assertThat(resp.getMessage()).isNull();
	}

}
