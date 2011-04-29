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
package ch.ralscha.extdirectspring.controller;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.inject.Inject;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import ch.ralscha.extdirectspring.bean.ExtDirectResponse;

/**
 * Tests for {@link RouterController}.
 * 
 * @author Ralph Schaer
 */
@SuppressWarnings("all")
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:/testExceptionHandlingConfig.xml")
public class ExceptionHandlingConfigInXmlTest {

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
	public void testExceptionInMapping() throws Exception {

		Map<String,Object> edRequest = ControllerUtil.createRequestJson("remoteProviderSimple", "method4", 2, 3, 2.5, "string.param");
		List<ExtDirectResponse> responses = controller.router(request, response, Locale.ENGLISH, edRequest);

		assertEquals(1, responses.size());
		ExtDirectResponse resp = responses.get(0);
		assertEquals("remoteProviderSimple", resp.getAction());
		assertEquals("method4", resp.getMethod());
		assertEquals("exception", resp.getType());
		assertEquals(2, resp.getTid());
		assertEquals("illegal argument", resp.getMessage());
		assertNull(resp.getResult());
		assertTrue(resp
				.getWhere()
				.startsWith(
						"java.lang.IllegalArgumentException: Invalid remoting method 'remoteProviderSimple.method4'. Missing ExtDirectMethod annotation"));

	}

	@Test
	public void testExceptionInMappingWithNullValue() throws Exception {

		Map<String,Object> edRequest = ControllerUtil.createRequestJson("remoteProviderSimple2", "method4", 2, 3, 2.5, "string.param");
		List<ExtDirectResponse> responses = controller.router(request, response, Locale.ENGLISH, edRequest);

		assertEquals(1, responses.size());
		ExtDirectResponse resp = responses.get(0);
		assertEquals("remoteProviderSimple2", resp.getAction());
		assertEquals("method4", resp.getMethod());
		assertEquals("exception", resp.getType());
		assertEquals(2, resp.getTid());
		assertEquals("No bean named 'remoteProviderSimple2' is defined", resp.getMessage());
		assertNull(resp.getResult());
		assertTrue(resp
				.getWhere()
				.startsWith(
						"org.springframework.beans.factory.NoSuchBeanDefinitionException: No bean named 'remoteProviderSimple2' is defined"));

	}

	@Test
	public void testExceptionNotInMapping() {
		Map<String,Object> edRequest = ControllerUtil.createRequestJson("remoteProviderSimple", "method11", 3);
		List<ExtDirectResponse> responses = controller.router(request, response, Locale.ENGLISH, edRequest);

		assertEquals(1, responses.size());
		ExtDirectResponse resp = responses.get(0);
		assertEquals("remoteProviderSimple", resp.getAction());
		assertEquals("method11", resp.getMethod());
		assertEquals("exception", resp.getType());
		assertEquals(3, resp.getTid());
		assertEquals("Panic!!!", resp.getMessage());
		assertNull(resp.getResult());
		assertTrue(resp.getWhere().startsWith("java.lang.NullPointerException"));

	}
}
