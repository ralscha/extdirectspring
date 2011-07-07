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
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import ch.ralscha.extdirectspring.bean.ExtDirectResponse;
import ch.ralscha.extdirectspring.provider.Row;

/**
 * Tests for {@link RouterController}.
 * 
 * @author Ralph Schaer
 */
@SuppressWarnings("all")
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:/testApplicationContext.xml")
public class RouterControllerInterfaceTest {

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
	public void testNoParameters() {
		Map<String, Object> edRequest = ControllerUtil.createRequestJson("remoteProviderImplementation", "method2", 1,
				null);
		List<ExtDirectResponse> responses = controller.router(request, response, Locale.ENGLISH, edRequest);

		ExtDirectResponse resp = responses.get(0);
		assertEquals(1, responses.size());
		assertEquals("remoteProviderImplementation", resp.getAction());
		assertEquals("method2", resp.getMethod());
		assertEquals(1, resp.getTid());
		assertEquals("rpc", resp.getType());
		assertNull(resp.getWhere());
		assertNull(resp.getMessage());
		assertEquals("method2() called", resp.getResult());
	}
	
	@Test
	public void testNoParameterAnnotation() {
		Map<String, Object> edRequest = ControllerUtil.createRequestJson("remoteProviderImplementation", "method3", 1,
				20, 2.1, "aString");
		List<ExtDirectResponse> responses = controller.router(request, response, Locale.ENGLISH, edRequest);

		ExtDirectResponse resp = responses.get(0);
		assertEquals(1, responses.size());
		assertEquals("remoteProviderImplementation", resp.getAction());
		assertEquals("method3", resp.getMethod());
		assertEquals(1, resp.getTid());
		assertEquals("rpc", resp.getType());
		assertNull(resp.getWhere());
		assertNull(resp.getMessage());
		assertEquals("method3() called-20-2.1-aString", resp.getResult());
	}
	
	@Test
	public void testWithRequestParamAnnotation() {
		
		Map<String, Object> readRequest = new HashMap<String, Object>();
		readRequest.put("lastName", "Smith");
		readRequest.put("active", true);
		Map<String,Object> edRequest = ControllerUtil.createRequestJson("remoteProviderImplementation", "storeRead", 1, readRequest);
		
		List<ExtDirectResponse> responses = controller.router(request, response, Locale.ENGLISH, edRequest);

		assertEquals(1, responses.size());
		ExtDirectResponse resp = responses.get(0);

		assertEquals("remoteProviderImplementation", resp.getAction());
		assertEquals("storeRead", resp.getMethod());
		assertEquals("rpc", resp.getType());
		assertEquals(1, resp.getTid());
		assertNull(resp.getMessage());
		assertNull(resp.getWhere());
		assertNotNull(resp.getResult());
		
		List<Row> rows = (List<Row>)resp.getResult();
		assertEquals(1, rows.size());
		Row theRow = rows.get(0);
		assertEquals(1, theRow.getId());
		assertEquals("Smith", theRow.getName());
		assertEquals(new BigDecimal("40"), theRow.getSalary());
	}
}
