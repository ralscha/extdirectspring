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
import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.joda.time.LocalDate;
import org.joda.time.format.ISODateTimeFormat;
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
import ch.ralscha.extdirectspring.util.JsonHandler;

/**
 * Tests for {@link RouterController}.
 * 
 * @author Ralph Schaer
 */
@SuppressWarnings("all")
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:/testApplicationContext.xml")
public class RouterControllerStoreModifyTest {

	@Autowired
	private RouterController controller;
	
	@Autowired
	private JsonHandler jsonHandler;

	private MockHttpServletResponse response;
	private MockHttpServletRequest request;

	@Before
	public void beforeTest() {
		response = new MockHttpServletResponse();
		request = new MockHttpServletRequest();
	}

	@Test
	public void testCreateNoData() {
		Map<String, Object> storeRequest = new LinkedHashMap<String, Object>();
		storeRequest.put("records", new ArrayList<Row>());
		Map<String, Object> edRequest = ControllerUtil.createRequestJson("remoteProviderStoreModify", "create1", 1,
				storeRequest);
		List<ExtDirectResponse> responses = controller.router(request, response, Locale.ENGLISH, edRequest);

		assertEquals(1, responses.size());
		ExtDirectResponse resp = responses.get(0);
		assertEquals("remoteProviderStoreModify", resp.getAction());
		assertEquals("create1", resp.getMethod());
		assertEquals("rpc", resp.getType());
		assertEquals(1, resp.getTid());
		assertNull(resp.getMessage());
		assertNull(resp.getWhere());
		assertNotNull(resp.getResult());

		List<Row> rows = (List<Row>) resp.getResult();
		assertTrue(rows.isEmpty());
	}
	
	@Test
	public void testCreateWithData() {
		Map<String, Object> storeRequest = new LinkedHashMap<String, Object>();
		List<Row> rowsToUpdate = new ArrayList<Row>();
		rowsToUpdate.add(new Row(10, "Ralph", true, "109.55"));
		rowsToUpdate.add(new Row(23, "John", false, "23.12"));

		storeRequest.put("records", rowsToUpdate);
		Map<String, Object> edRequest = ControllerUtil.createRequestJson("remoteProviderStoreModify", "create1", 1,
				storeRequest);
		List<ExtDirectResponse> responses = controller.router(request, response, Locale.ENGLISH, edRequest);

		assertEquals(1, responses.size());
		ExtDirectResponse resp = responses.get(0);
		assertEquals("remoteProviderStoreModify", resp.getAction());
		assertEquals("create1", resp.getMethod());
		assertEquals("rpc", resp.getType());
		assertEquals(1, resp.getTid());
		assertNull(resp.getMessage());
		assertNull(resp.getWhere());
		assertNotNull(resp.getResult());

		List<Row> storeResponse = (List<Row>) resp.getResult();
		assertEquals(2, storeResponse.size());

		Collections.sort(storeResponse);
		assertEquals(10, storeResponse.get(0).getId());
		assertEquals(23, storeResponse.get(1).getId());
	}

	@Test
	public void testCreateWithDataSingle() {
		Map<String, Object> edRequest = ControllerUtil.createRequestJson("remoteProviderStoreModifySingle", "create1", 1,
				new Row(10, "Ralph", true, "109.55"));
		List<ExtDirectResponse> responses = controller.router(request, response, Locale.ENGLISH, edRequest);

		assertEquals(1, responses.size());
		ExtDirectResponse resp = responses.get(0);
		assertEquals("remoteProviderStoreModifySingle", resp.getAction());
		assertEquals("create1", resp.getMethod());
		assertEquals("rpc", resp.getType());
		assertEquals(1, resp.getTid());
		assertNull(resp.getMessage());
		assertNull(resp.getWhere());
		assertNotNull(resp.getResult());

		Row storeResponse = (Row) resp.getResult();
		
		assertEquals(10, storeResponse.getId());
	}
	
	@Test
	public void testCreateWithDataAndSupportedArguments() {
		Map<String, Object> storeRequest = new LinkedHashMap<String, Object>();
		List<Row> rowsToUpdate = new ArrayList<Row>();
		rowsToUpdate.add(new Row(10, "Ralph", false, "109.55"));

		storeRequest.put("records", rowsToUpdate);
		Map<String, Object> edRequest = ControllerUtil.createRequestJson("remoteProviderStoreModify", "create2", 1,
				storeRequest);
		List<ExtDirectResponse> responses = controller.router(request, response, Locale.ENGLISH, edRequest);

		assertEquals(1, responses.size());
		ExtDirectResponse resp = responses.get(0);
		assertEquals("remoteProviderStoreModify", resp.getAction());
		assertEquals("create2", resp.getMethod());
		assertEquals("rpc", resp.getType());
		assertEquals(1, resp.getTid());
		assertNull(resp.getMessage());
		assertNull(resp.getWhere());
		assertNotNull(resp.getResult());

		List<Row> storeResponse = (List<Row>) resp.getResult();
		assertEquals(1, storeResponse.size());
		assertEquals(10, storeResponse.get(0).getId());
	}

	@Test
	public void testCreateWithDataAndSupportedArgumentsSingle() {
		Map<String, Object> edRequest = ControllerUtil.createRequestJson("remoteProviderStoreModifySingle", "create2", 1,
				new Row(10, "Ralph", false, "109.55"));
		List<ExtDirectResponse> responses = controller.router(request, response, Locale.ENGLISH, edRequest);

		assertEquals(1, responses.size());
		ExtDirectResponse resp = responses.get(0);
		assertEquals("remoteProviderStoreModifySingle", resp.getAction());
		assertEquals("create2", resp.getMethod());
		assertEquals("rpc", resp.getType());
		assertEquals(1, resp.getTid());
		assertNull(resp.getMessage());
		assertNull(resp.getWhere());
		assertNotNull(resp.getResult());

		Row storeResponse = (Row) resp.getResult();
		assertEquals(10, storeResponse.getId());
	}
	
	@Test
	public void testUpdate() {
		Map<String, Object> storeRequest = new LinkedHashMap<String, Object>();
		List<Row> rowsToUpdate = new ArrayList<Row>();
		rowsToUpdate.add(new Row(10, "Ralph", true, "109.55"));
		storeRequest.put("records", rowsToUpdate);
		executeUpdate("remoteProviderStoreModify", storeRequest, "update1");
	}

	@Test
	public void testUpdateWithRequestParam() {
		Map<String, Object> storeRequest = new LinkedHashMap<String, Object>();
		List<Row> rowsToUpdate = new ArrayList<Row>();
		rowsToUpdate.add(new Row(10, "Ralph", true, "109.55"));
		storeRequest.put("id", 10);
		storeRequest.put("records", rowsToUpdate);
		executeUpdate("remoteProviderStoreModify", storeRequest, "update2");
	}

	@Test
	public void testUpdateWithRequestParamDefaultValue() {
		Map<String, Object> storeRequest = new LinkedHashMap<String, Object>();
		List<Row> rowsToUpdate = new ArrayList<Row>();
		rowsToUpdate.add(new Row(10, "Ralph", true, "109.55"));
		storeRequest.put("records", rowsToUpdate);
		executeUpdate("remoteProviderStoreModify", storeRequest, "update3");
	}

	@Test
	public void testUpdateWithRequestParamOptional() {
		Map<String, Object> storeRequest = new LinkedHashMap<String, Object>();
		List<Row> rowsToUpdate = new ArrayList<Row>();
		rowsToUpdate.add(new Row(10, "Ralph", true, "109.55"));
		storeRequest.put("records", rowsToUpdate);
		executeUpdate("remoteProviderStoreModify", storeRequest, "update4");

		storeRequest = new LinkedHashMap<String, Object>();
		rowsToUpdate = new ArrayList<Row>();
		rowsToUpdate.add(new Row(10, "Ralph", true, "109.55"));
		storeRequest.put("records", rowsToUpdate);
		storeRequest.put("id", 11);
		storeRequest.put("yesterday", ISODateTimeFormat.date().print(new LocalDate().minusDays(1)));
		executeUpdate("remoteProviderStoreModify", storeRequest, "update4");
	}

	@Test
	public void testUpdateSingle() {
		Map<String, Object> storeRequest = new LinkedHashMap<String, Object>();
		Row row = new Row(10, "Ralph", true, "109.55");
		storeRequest.putAll(jsonHandler.convertValue(row, Map.class));
		executeUpdate("remoteProviderStoreModifySingle", storeRequest, "update1");
	}

	@Test
	public void testUpdateWithRequestParamSingle() {
		Map<String, Object> storeRequest = new LinkedHashMap<String, Object>();
		Row row = new Row(10, "Ralph", true, "109.55");
		storeRequest.put("aParam", 42);
		storeRequest.putAll(jsonHandler.convertValue(row, Map.class));
		executeUpdate("remoteProviderStoreModifySingle", storeRequest, "update2");
	}

	@Test
	public void testUpdateWithRequestParamDefaultValueSingle() {
		Map<String, Object> storeRequest = new LinkedHashMap<String, Object>();
		Row row = new Row(10, "Ralph", true, "109.55");
		storeRequest.putAll(jsonHandler.convertValue(row, Map.class));
		executeUpdate("remoteProviderStoreModifySingle", storeRequest, "update3");
	}

	@Test
	public void testUpdateWithRequestParamOptionalSingle() {
		Map<String, Object> storeRequest = new LinkedHashMap<String, Object>();
		Row row = new Row(10, "Ralph", true, "109.55");
		storeRequest.putAll(jsonHandler.convertValue(row, Map.class));
		executeUpdate("remoteProviderStoreModifySingle", storeRequest, "update4");

		storeRequest = new LinkedHashMap<String, Object>();
		storeRequest.putAll(jsonHandler.convertValue(row, Map.class));
		storeRequest.put("aParam", 11);
		storeRequest.put("yesterday", ISODateTimeFormat.date().print(new LocalDate().minusDays(1)));
		executeUpdate("remoteProviderStoreModifySingle", storeRequest, "update4");
	}
	
	private void executeUpdate(String action, Map<String, Object> storeRequest, String method) {
		Map<String, Object> edRequest = ControllerUtil.createRequestJson(action, method, 1,
				storeRequest);
		List<ExtDirectResponse> responses = controller.router(request, response, Locale.ENGLISH, edRequest);

		assertEquals(1, responses.size());
		ExtDirectResponse resp = responses.get(0);
		assertEquals(action, resp.getAction());
		assertEquals(method, resp.getMethod());
		assertEquals("rpc", resp.getType());
		assertEquals(1, resp.getTid());
		assertNull(resp.getMessage());
		assertNull(resp.getWhere());
		assertNotNull(resp.getResult());

		Object result = resp.getResult();
		if (result instanceof List) {
			List<Row> storeResponse = (List<Row>) result;
			assertEquals(1, storeResponse.size());
			assertEquals(10, storeResponse.get(0).getId());
			assertEquals("Ralph", storeResponse.get(0).getName());
			assertTrue(storeResponse.get(0).isAdmin());
			assertEquals(new BigDecimal("109.55"), storeResponse.get(0).getSalary());
		} else {
			Row storeResponse = (Row)result;
			assertEquals(10, storeResponse.getId());
			assertEquals("Ralph", storeResponse.getName());
			assertTrue(storeResponse.isAdmin());
			assertEquals(new BigDecimal("109.55"), storeResponse.getSalary());
		}
	}

	@Test
	public void testDestroy() {
		Map<String, Object> storeRequest = new LinkedHashMap<String, Object>();
		List<Integer> rowsToUpdate = new ArrayList<Integer>();
		rowsToUpdate.add(10);

		storeRequest.put("records", rowsToUpdate);
		Map<String, Object> edRequest = ControllerUtil.createRequestJson("remoteProviderStoreModify", "destroy", 1,
				storeRequest);
		List<ExtDirectResponse> responses = controller.router(request, response, Locale.ENGLISH, edRequest);

		assertEquals(1, responses.size());
		ExtDirectResponse resp = responses.get(0);
		assertEquals("remoteProviderStoreModify", resp.getAction());
		assertEquals("destroy", resp.getMethod());
		assertEquals("rpc", resp.getType());
		assertEquals(1, resp.getTid());
		assertNull(resp.getMessage());
		assertNull(resp.getWhere());
		assertNotNull(resp.getResult());

		List<Integer> storeResponse = (List<Integer>) resp.getResult();
		assertEquals(1, storeResponse.size());
		assertEquals(Integer.valueOf(10), storeResponse.get(0));
	}

	@Test
	public void testDestroySingle() {
		Map<String, Object> edRequest = ControllerUtil.createRequestJson("remoteProviderStoreModifySingle", "destroy", 1,
				10);
		List<ExtDirectResponse> responses = controller.router(request, response, Locale.ENGLISH, edRequest);

		assertEquals(1, responses.size());
		ExtDirectResponse resp = responses.get(0);
		assertEquals("remoteProviderStoreModifySingle", resp.getAction());
		assertEquals("destroy", resp.getMethod());
		assertEquals("rpc", resp.getType());
		assertEquals(1, resp.getTid());
		assertNull(resp.getMessage());
		assertNull(resp.getWhere());
		assertNotNull(resp.getResult());

		Integer storeResponse = (Integer) resp.getResult();
		assertEquals(Integer.valueOf(10), storeResponse);
	}
	
}
