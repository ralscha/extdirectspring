/**
 * Copyright 2010 Ralph Schaer <ralphschaer@gmail.com>
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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Locale;

import javax.inject.Inject;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
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

	@Inject
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
		assertNotNull(resp);
		assertEquals("event", resp.getType());
		assertEquals("message1", resp.getName());
		assertTrue(((String) resp.getData()).startsWith("Successfully polled at: "));
		assertNull(resp.getWhere());
		assertNull(resp.getMessage());
	}

	@Test
	public void pollSupportedArguments() throws Exception {
		ExtDirectPollResponse resp = controller.poll("pollProvider", "handleMessage2", "message2", request, response,
				Locale.ENGLISH);
		assertNotNull(resp);
		assertEquals("event", resp.getType());
		assertEquals("message2", resp.getName());
		assertTrue(((String) resp.getData()).startsWith("Successfully polled at: "));
		assertNull(resp.getWhere());
		assertNull(resp.getMessage());
	}

	@Test
	public void pollRequiredArgument() throws Exception {
		request.setParameter("id", "2");
		ExtDirectPollResponse resp = controller.poll("pollProvider", "handleMessage3", "message3", request, response,
				Locale.ENGLISH);
		assertNotNull(resp);
		assertEquals("event", resp.getType());
		assertEquals("message3", resp.getName());
		assertEquals("Result: 2", resp.getData());
		assertNull(resp.getWhere());
		assertNull(resp.getMessage());
	}

	@Test
	public void pollRequiredArgumentNoRequestParameter() throws Exception {
		ExtDirectPollResponse resp = controller.poll("pollProvider", "handleMessage3", "message3", request, response,
				Locale.ENGLISH);
		assertNotNull(resp);
		assertEquals("exception", resp.getType());
		assertEquals("message3", resp.getName());
		assertNull(resp.getData());
		assertEquals("Server Error", resp.getMessage());
		assertNull(resp.getWhere());
	}

	@Test
	public void pollDefaultValueArgumentWithRequestParameter() throws Exception {
		request.setParameter("id", "7");
		ExtDirectPollResponse resp = controller.poll("pollProvider", "handleMessage4", "message4", request, response,
				Locale.ENGLISH);
		assertNotNull(resp);
		assertEquals("event", resp.getType());
		assertEquals("message4", resp.getName());
		assertEquals(Integer.valueOf(14), resp.getData());
		assertNull(resp.getWhere());
		assertNull(resp.getMessage());
	}

	@Test
	public void pollDefaultValueArgumentWithoutRequestParameter() throws Exception {
		ExtDirectPollResponse resp = controller.poll("pollProvider", "handleMessage4", "message4", request, response,
				Locale.ENGLISH);
		assertNotNull(resp);
		assertEquals("event", resp.getType());
		assertEquals("message4", resp.getName());
		assertEquals(Integer.valueOf(2), resp.getData());
		assertNull(resp.getWhere());
		assertNull(resp.getMessage());
	}

	@Test
	public void pollNotRequiredArgumentWithRequestParameter() throws Exception {
		request.setParameter("id", "3");
		ExtDirectPollResponse resp = controller.poll("pollProvider", "handleMessage5", "message5", request, response,
				Locale.ENGLISH);
		assertNotNull(resp);
		assertEquals("event", resp.getType());
		assertEquals("message5", resp.getName());
		assertEquals(Integer.valueOf(6), resp.getData());
		assertNull(resp.getWhere());
		assertNull(resp.getMessage());
	}

	@Test
	public void pollNotRequiredArgumentWithoutRequestParameter() throws Exception {
		ExtDirectPollResponse resp = controller.poll("pollProvider", "handleMessage5", "message5", request, response,
				Locale.ENGLISH);
		assertNotNull(resp);
		assertEquals("event", resp.getType());
		assertEquals("message5", resp.getName());
		assertNull(resp.getData());
		assertNull(resp.getWhere());
		assertNull(resp.getMessage());
	}

}
