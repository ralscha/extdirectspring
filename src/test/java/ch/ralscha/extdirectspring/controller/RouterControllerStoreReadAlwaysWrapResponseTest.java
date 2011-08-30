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
import ch.ralscha.extdirectspring.bean.ExtDirectStoreReadRequest;
import ch.ralscha.extdirectspring.bean.ExtDirectStoreResponse;
import ch.ralscha.extdirectspring.provider.Row;

/**
 * Tests for {@link RouterController}.
 * 
 * @author Ralph Schaer
 */
@SuppressWarnings("all")
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:/testApplicationContextWrapResponse.xml")
public class RouterControllerStoreReadAlwaysWrapResponseTest {

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
	public void testNoArgumentsNoRequestParameters() {
		Map<String, Object> edRequest = ControllerUtil.createRequestJson("remoteProviderStoreRead", "method1", 1, null);
		List<ExtDirectResponse> responses = controller.router(request, response, Locale.ENGLISH, edRequest);

		assertEquals(1, responses.size());
		ExtDirectResponse resp = responses.get(0);
		assertEquals("remoteProviderStoreRead", resp.getAction());
		assertEquals("method1", resp.getMethod());
		assertEquals("rpc", resp.getType());
		assertEquals(1, resp.getTid());
		assertNull(resp.getMessage());
		assertNull(resp.getWhere());
		assertNotNull(resp.getResult());

		ExtDirectStoreResponse storeResponse = (ExtDirectStoreResponse) resp.getResult();
		List<Row> rows = (List<Row>) storeResponse.getRecords();
		assertEquals(100, rows.size());

	}

	@Test
	public void testNoArgumentsWithRequestParameters() {

		ExtDirectStoreReadRequest storeRead = new ExtDirectStoreReadRequest();
		storeRead.setQuery("ralph");

		Map<String, Object> edRequest = ControllerUtil.createRequestJson("remoteProviderStoreRead", "method1", 1,
				storeRead);
		List<ExtDirectResponse> responses = controller.router(request, response, Locale.ENGLISH, edRequest);

		assertEquals(1, responses.size());
		ExtDirectResponse resp = responses.get(0);
		assertEquals("remoteProviderStoreRead", resp.getAction());
		assertEquals("method1", resp.getMethod());
		assertEquals("rpc", resp.getType());
		assertEquals(1, resp.getTid());
		assertNull(resp.getMessage());
		assertNull(resp.getWhere());
		assertNotNull(resp.getResult());

		ExtDirectStoreResponse storeResponse = (ExtDirectStoreResponse) resp.getResult();
		List<Row> rows = (List<Row>) storeResponse.getRecords();
		assertEquals(100, rows.size());

	}

	@Test
	public void testSupportedArguments() {

		Map<String, Object> edRequest = ControllerUtil.createRequestJson("remoteProviderStoreRead", "method3", 1, null);
		List<ExtDirectResponse> responses = controller.router(request, response, Locale.ENGLISH, edRequest);

		assertEquals(1, responses.size());
		ExtDirectResponse resp = responses.get(0);
		assertEquals("remoteProviderStoreRead", resp.getAction());
		assertEquals("method3", resp.getMethod());
		assertEquals("rpc", resp.getType());
		assertEquals(1, resp.getTid());
		assertNull(resp.getMessage());
		assertNull(resp.getWhere());
		assertNotNull(resp.getResult());

		ExtDirectStoreResponse storeResponse = (ExtDirectStoreResponse) resp.getResult();
		List<Row> rows = (List<Row>) storeResponse.getRecords();
		assertEquals(100, rows.size());

	}

	@Test
	public void testWithAdditionalParametersOptional() {

		Map<String, Object> edRequest = ControllerUtil.createRequestJson("remoteProviderStoreRead", "method7", 1, null);
		List<ExtDirectResponse> responses = controller.router(request, response, Locale.ENGLISH, edRequest);

		assertEquals(1, responses.size());
		ExtDirectResponse resp = responses.get(0);

		assertEquals("remoteProviderStoreRead", resp.getAction());
		assertEquals("method7", resp.getMethod());
		assertEquals("rpc", resp.getType());
		assertEquals(1, resp.getTid());
		assertNull(resp.getMessage());
		assertNull(resp.getWhere());
		assertNotNull(resp.getResult());

		ExtDirectStoreResponse storeResponse = (ExtDirectStoreResponse) resp.getResult();
		List<Row> rows = (List<Row>) storeResponse.getRecords();
		assertEquals(100, rows.size());

		Map<String, Object> readRequest = new HashMap<String, Object>();
		readRequest.put("id", 11);
		readRequest.put("query", "");

		edRequest = ControllerUtil.createRequestJson("remoteProviderStoreRead", "method7", 1, readRequest);
		responses = controller.router(request, response, Locale.ENGLISH, edRequest);

		assertEquals(1, responses.size());
		resp = responses.get(0);

		assertEquals("remoteProviderStoreRead", resp.getAction());
		assertEquals("method7", resp.getMethod());
		assertEquals("rpc", resp.getType());
		assertEquals(1, resp.getTid());
		assertNull(resp.getMessage());
		assertNull(resp.getWhere());
		assertNotNull(resp.getResult());

		storeResponse = (ExtDirectStoreResponse) resp.getResult();
		rows = (List<Row>) storeResponse.getRecords();
		assertEquals(100, rows.size());

	}

}
