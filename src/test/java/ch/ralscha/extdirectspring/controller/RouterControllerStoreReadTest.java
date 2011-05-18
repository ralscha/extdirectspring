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

import static org.hamcrest.Matchers.hasEntry;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

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
@ContextConfiguration(locations = "classpath:/testApplicationContext.xml")
public class RouterControllerStoreReadTest {

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
		Configuration config = new Configuration();
		config.setAlwaysWrapStoreReadResponse(true);
		controller.setConfiguration(config);
		
		Map<String,Object> edRequest = ControllerUtil.createRequestJson("remoteProviderStoreRead", "method1", 1, null);
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
		
		controller.setConfiguration(new Configuration());
				
		edRequest = ControllerUtil.createRequestJson("remoteProviderStoreRead", "method1", 1, null);
		responses = controller.router(request, response, Locale.ENGLISH, edRequest);
		
		assertEquals(1, responses.size());
		resp = responses.get(0);
		assertEquals("remoteProviderStoreRead", resp.getAction());
		assertEquals("method1", resp.getMethod());
		assertEquals("rpc", resp.getType());
		assertEquals(1, resp.getTid());
		assertNull(resp.getMessage());
		assertNull(resp.getWhere());
		assertNotNull(resp.getResult());

		rows = (List<Row>) resp.getResult();
		assertEquals(100, rows.size());		
		
	}

	@Test
	public void testNoArgumentsWithRequestParameters() {
		
		Configuration config = new Configuration();
		config.setAlwaysWrapStoreReadResponse(true);
		controller.setConfiguration(config);
		
		ExtDirectStoreReadRequest storeRead = new ExtDirectStoreReadRequest();
		storeRead.setQuery("ralph");

		Map<String,Object> edRequest = ControllerUtil.createRequestJson("remoteProviderStoreRead", "method1", 1, storeRead);
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
		
		controller.setConfiguration(new Configuration());
		
		edRequest = ControllerUtil.createRequestJson("remoteProviderStoreRead", "method1", 1, storeRead);
		responses = controller.router(request, response, Locale.ENGLISH, edRequest);

		assertEquals(1, responses.size());
		resp = responses.get(0);
		assertEquals("remoteProviderStoreRead", resp.getAction());
		assertEquals("method1", resp.getMethod());
		assertEquals("rpc", resp.getType());
		assertEquals(1, resp.getTid());
		assertNull(resp.getMessage());
		assertNull(resp.getWhere());
		assertNotNull(resp.getResult());

		rows = (List<Row>)  resp.getResult();
		assertEquals(100, rows.size());
	}

	@Test
	public void testReturnsNull() {
		Map<String,Object> edRequest = ControllerUtil.createRequestJson("remoteProviderStoreRead", "method2", 1, null);
		List<ExtDirectResponse> responses = controller.router(request, response, Locale.ENGLISH, edRequest);

		assertEquals(1, responses.size());
		ExtDirectResponse resp = responses.get(0);
		assertEquals("remoteProviderStoreRead", resp.getAction());
		assertEquals("method2", resp.getMethod());
		assertEquals("rpc", resp.getType());
		assertEquals(1, resp.getTid());
		assertNull(resp.getMessage());
		assertNull(resp.getWhere());
		assertNotNull(resp.getResult());
	}

	@Test
	public void testSupportedArguments() {
		
		Configuration config = new Configuration();
		config.setAlwaysWrapStoreReadResponse(true);
		controller.setConfiguration(config);
		
		Map<String,Object> edRequest = ControllerUtil.createRequestJson("remoteProviderStoreRead", "method3", 1, null);
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
		
		controller.setConfiguration(new Configuration());
	}

	@Test
	public void testWithExtDirectStoreReadRequest() {
		ExtDirectStoreReadRequest storeRead = new ExtDirectStoreReadRequest();
		storeRead.setQuery("name");
		ExtDirectResponse resp = executeWithExtDirectStoreReadRequest(storeRead);
		ExtDirectStoreResponse<Row> storeResponse = (ExtDirectStoreResponse<Row>) resp.getResult();
		assertEquals(Integer.valueOf(50), storeResponse.getTotal());
		assertEquals(50, storeResponse.getRecords().size());
		for (Row row : storeResponse.getRecords()) {
			assertTrue(row.getName().startsWith("name"));
		}

		storeRead = new ExtDirectStoreReadRequest();
		storeRead.setQuery("firstname");
		resp = executeWithExtDirectStoreReadRequest(storeRead);
		storeResponse = (ExtDirectStoreResponse<Row>) resp.getResult();
		assertEquals(Integer.valueOf(50), storeResponse.getTotal());
		assertEquals(50, storeResponse.getRecords().size());
		for (Row row : storeResponse.getRecords()) {
			assertTrue(row.getName().startsWith("firstname"));
		}

		storeRead = new ExtDirectStoreReadRequest();
		storeRead.setQuery("");
		storeRead.setSort("id");
		storeRead.setDir("ASC");
		storeRead.setLimit(10);
		storeRead.setStart(10);
		resp = executeWithExtDirectStoreReadRequest(storeRead);
		storeResponse = (ExtDirectStoreResponse<Row>) resp.getResult();
		assertEquals(Integer.valueOf(100), storeResponse.getTotal());
		assertEquals(10, storeResponse.getRecords().size());
		int id = 10;
		for (Row row : storeResponse.getRecords()) {
			assertEquals(id, row.getId());
			id++;
		}

		storeRead = new ExtDirectStoreReadRequest();
		storeRead.setQuery("");
		storeRead.setSort("id");
		storeRead.setDir("DESC");
		storeRead.setLimit(10);
		storeRead.setStart(20);
		resp = executeWithExtDirectStoreReadRequest(storeRead);
		storeResponse = (ExtDirectStoreResponse<Row>) resp.getResult();
		assertEquals(Integer.valueOf(100), storeResponse.getTotal());
		assertEquals(10, storeResponse.getRecords().size());
		id = 79;
		for (Row row : storeResponse.getRecords()) {
			assertEquals(id, row.getId());
			id--;
		}

		storeRead = new ExtDirectStoreReadRequest();
		storeRead.setQuery("");
		storeRead.setGroupBy("id");
		storeRead.setGroupDir("ASC");
		storeRead.setLimit(10);
		storeRead.setStart(10);
		resp = executeWithExtDirectStoreReadRequest(storeRead);
		storeResponse = (ExtDirectStoreResponse<Row>) resp.getResult();
		assertEquals(Integer.valueOf(100), storeResponse.getTotal());
		assertEquals(10, storeResponse.getRecords().size());
		id = 10;
		for (Row row : storeResponse.getRecords()) {
			assertEquals(id, row.getId());
			id++;
		}

		storeRead = new ExtDirectStoreReadRequest();
		storeRead.setQuery("");
		storeRead.setGroupBy("id");
		storeRead.setGroupDir("DESC");
		storeRead.setLimit(10);
		storeRead.setStart(20);
		resp = executeWithExtDirectStoreReadRequest(storeRead);
		storeResponse = (ExtDirectStoreResponse<Row>) resp.getResult();
		assertEquals(Integer.valueOf(100), storeResponse.getTotal());
		assertEquals(10, storeResponse.getRecords().size());
		id = 79;
		for (Row row : storeResponse.getRecords()) {
			assertEquals(id, row.getId());
			id--;
		}
	}

	private ExtDirectResponse executeWithExtDirectStoreReadRequest(ExtDirectStoreReadRequest storeRead) {
		Map<String,Object> edRequest = ControllerUtil.createRequestJson("remoteProviderStoreRead", "method4", 1, storeRead);
		List<ExtDirectResponse> responses = controller.router(request, response, Locale.ENGLISH, edRequest);

		assertEquals(1, responses.size());
		ExtDirectResponse resp = responses.get(0);

		assertEquals("remoteProviderStoreRead", resp.getAction());
		assertEquals("method4", resp.getMethod());
		assertEquals("rpc", resp.getType());
		assertEquals(1, resp.getTid());
		assertNull(resp.getMessage());
		assertNull(resp.getWhere());
		assertNotNull(resp.getResult());
		return resp;
	}

	@Test
	public void testWithAdditionalParameters() {
		Map<String, Object> readRequest = new HashMap<String, Object>();
		readRequest.put("id", 10);
		readRequest.put("query", "name");

		Map<String,Object> edRequest = ControllerUtil.createRequestJson("remoteProviderStoreRead", "method5", 1, readRequest);
		List<ExtDirectResponse> responses = controller.router(request, response, Locale.ENGLISH, edRequest);

		assertEquals(1, responses.size());
		ExtDirectResponse resp = responses.get(0);

		assertEquals("remoteProviderStoreRead", resp.getAction());
		assertEquals("method5", resp.getMethod());
		assertEquals("rpc", resp.getType());
		assertEquals(1, resp.getTid());
		assertNull(resp.getMessage());
		assertNull(resp.getWhere());
		assertNotNull(resp.getResult());

		ExtDirectStoreResponse<Row> storeResponse = (ExtDirectStoreResponse<Row>) resp.getResult();
		assertEquals(Integer.valueOf(50), storeResponse.getTotal());
		assertEquals(50, storeResponse.getRecords().size());
		for (Row row : storeResponse.getRecords()) {
			assertTrue(row.getName().startsWith("name"));
		}

		readRequest = new HashMap<String, Object>();
		readRequest.put("query", "name");

		edRequest = ControllerUtil.createRequestJson("remoteProviderStoreRead", "method5", 1, readRequest);
		responses = controller.router(request, response, Locale.ENGLISH, edRequest);

		assertEquals(1, responses.size());
		resp = responses.get(0);

		assertEquals("remoteProviderStoreRead", resp.getAction());
		assertEquals("method5", resp.getMethod());
		assertEquals("exception", resp.getType());
		assertEquals(1, resp.getTid());
		assertEquals("Server Error", resp.getMessage());
	}

	@Test
	public void testWithAdditionalParametersDefaultValue() {
		Map<String, Object> readRequest = new HashMap<String, Object>();
		readRequest.put("query", "firstname");

		Map<String,Object> edRequest = ControllerUtil.createRequestJson("remoteProviderStoreRead", "method6", 1, readRequest);
		List<ExtDirectResponse> responses = controller.router(request, response, Locale.ENGLISH, edRequest);

		assertEquals(1, responses.size());
		ExtDirectResponse resp = responses.get(0);

		assertEquals("remoteProviderStoreRead", resp.getAction());
		assertEquals("method6", resp.getMethod());
		assertEquals("rpc", resp.getType());
		assertEquals(1, resp.getTid());
		assertNull(resp.getMessage());
		assertNull(resp.getWhere());
		assertNotNull(resp.getResult());

		ExtDirectStoreResponse<Row> storeResponse = (ExtDirectStoreResponse<Row>) resp.getResult();
		assertEquals(Integer.valueOf(50), storeResponse.getTotal());
		assertEquals(50, storeResponse.getRecords().size());
		for (Row row : storeResponse.getRecords()) {
			assertTrue(row.getName().startsWith("firstname"));
		}
	}

	@Test
	public void testWithAdditionalParametersOptional() {

		Configuration config = new Configuration();
		config.setAlwaysWrapStoreReadResponse(true);
		controller.setConfiguration(config);
		
		Map<String,Object> edRequest = ControllerUtil.createRequestJson("remoteProviderStoreRead", "method7", 1, null);
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
		
		controller.setConfiguration(new Configuration());
	}

	@Test
	public void testMetadata() {

		Map<String,Object> edRequest = ControllerUtil.createRequestJson("remoteProviderStoreRead", "methodMetadata", 1, new HashMap<String, Object>());
		List<ExtDirectResponse> responses = controller.router(request, response, Locale.ENGLISH, edRequest);

		assertEquals(1, responses.size());
		ExtDirectResponse resp = responses.get(0);

		assertEquals("remoteProviderStoreRead", resp.getAction());
		assertEquals("methodMetadata", resp.getMethod());
		assertEquals("rpc", resp.getType());
		assertEquals(1, resp.getTid());
		assertNull(resp.getMessage());
		assertNull(resp.getWhere());
		assertNotNull(resp.getResult());

		ExtDirectStoreResponse<Row> response = (ExtDirectStoreResponse<Row>) resp.getResult();
		assertEquals(50, response.getRecords().size());
		assertEquals(100, response.getTotal().intValue());
		Map<String, Object> metadata = response.getMetaData();
		assertNotNull(metadata);

		assertThat(metadata, hasEntry("root", (Object) "records"));
		assertThat(metadata, hasEntry("totalProperty", (Object) "total"));
		assertThat(metadata, hasEntry("successProperty", (Object) "success"));
		assertThat(metadata, hasEntry("start", (Object) 0));
		assertThat(metadata, hasEntry("limit", (Object) 50));

		Map<String, String> sortInfo = (Map<String, String>) metadata.get("sortInfo");
		assertEquals(2, sortInfo.size());
		assertThat(sortInfo, hasEntry("field", "name"));
		assertThat(sortInfo, hasEntry("direction", "ASC"));

		List<Map<String, Object>> fields = (List<Map<String, Object>>) metadata.get("fields");
		assertEquals(4, fields.size());

		Map<String, Object> field1 = fields.get(0);
		assertThat(field1, hasEntry("name", (Object) "id"));
		assertThat(field1, hasEntry("type", (Object) "int"));
		assertThat(field1, hasEntry("header", (Object) "ID"));
		assertThat(field1, hasEntry("width", (Object) 20));
		assertThat(field1, hasEntry("sortable", (Object) true));
		assertThat(field1, hasEntry("resizable", (Object) true));
		assertThat(field1, hasEntry("hideable", (Object) false));

		Map<String, Object> field2 = fields.get(1);
		assertThat(field2, hasEntry("name", (Object) "name"));
		assertThat(field2, hasEntry("type", (Object) "string"));
		assertThat(field2, hasEntry("header", (Object) "Name"));
		assertThat(field2, hasEntry("width", (Object) 70));
		assertThat(field2, hasEntry("sortable", (Object) true));
		assertThat(field2, hasEntry("resizable", (Object) true));
		assertThat(field2, hasEntry("hideable", (Object) false));

		Map<String, Object> field3 = fields.get(2);
		assertThat(field3, hasEntry("name", (Object) "admin"));
		assertThat(field3, hasEntry("type", (Object) "boolean"));
		assertThat(field3, hasEntry("header", (Object) "Administrator"));
		assertThat(field3, hasEntry("width", (Object) 30));
		assertThat(field3, hasEntry("sortable", (Object) true));
		assertThat(field3, hasEntry("resizable", (Object) true));
		assertThat(field3, hasEntry("hideable", (Object) true));

		Map<String, Object> field4 = fields.get(3);
		assertThat(field4, hasEntry("name", (Object) "salary"));
		assertThat(field4, hasEntry("type", (Object) "float"));
		assertThat(field4, hasEntry("header", (Object) "Salary"));
		assertThat(field4, hasEntry("width", (Object) 50));
		assertThat(field4, hasEntry("sortable", (Object) false));
		assertThat(field4, hasEntry("resizable", (Object) true));
		assertThat(field4, hasEntry("hideable", (Object) true));

	}


}
