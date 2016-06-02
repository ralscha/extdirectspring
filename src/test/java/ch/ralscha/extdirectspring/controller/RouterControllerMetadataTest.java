/**
 * Copyright 2010-2016 Ralph Schaer <ralphschaer@gmail.com>
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

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import ch.ralscha.extdirectspring.bean.EdStoreResult;
import ch.ralscha.extdirectspring.bean.ExtDirectResponse;
import ch.ralscha.extdirectspring.bean.ExtDirectStoreResult;
import ch.ralscha.extdirectspring.provider.RemoteProviderTreeLoad.Node;
import ch.ralscha.extdirectspring.provider.Row;

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration("classpath:/testApplicationContext.xml")
@SuppressWarnings("unchecked")
public class RouterControllerMetadataTest {

	@Autowired
	private WebApplicationContext wac;

	private MockMvc mockMvc;

	@BeforeClass
	public static void beforeTest() {
		Locale.setDefault(Locale.US);
	}

	@Before
	public void setupMockMvc() throws Exception {
		this.mockMvc = MockMvcBuilders.webAppContextSetup(this.wac).build();
	}

	@Test
	public void testWithMetadataParameter() {
		Map<String, Object> readRequest = new HashMap<String, Object>();
		readRequest.put("id", 10);
		readRequest.put("query", "name");

		Map<String, Object> metadata = new HashMap<String, Object>();
		metadata.put("mp", "aMetadataValue");

		ExtDirectStoreResult<Row> storeResponse = (ExtDirectStoreResult<Row>) ControllerUtil
				.sendAndReceive(this.mockMvc, "remoteProviderMetadata", "method1",
						metadata, new TypeReference<ExtDirectStoreResult<Row>>() {
							// nothing here
						}, readRequest);

		assertThat(storeResponse.getTotal()).isEqualTo(50L);
		assertThat(storeResponse.getRecords()).hasSize(50);
		int ix = 0;
		for (Row row : storeResponse.getRecords()) {
			assertThat(row.getName()).startsWith("name: " + ix + ":10;aMetadataValue;en");
			ix += 2;
		}

		readRequest = new HashMap<String, Object>();
		readRequest.put("id", 10);
		readRequest.put("query", "name");

		storeResponse = (ExtDirectStoreResult<Row>) ControllerUtil.sendAndReceive(
				this.mockMvc, "remoteProviderMetadata", "method1", null, null,
				readRequest);
	}

	@Test
	public void testWithMetadataParameterDefaultValue() {
		Map<String, Object> readRequest = new HashMap<String, Object>();
		readRequest.put("query", "firstname");

		ExtDirectStoreResult<Row> storeResponse = (ExtDirectStoreResult<Row>) ControllerUtil
				.sendAndReceive(this.mockMvc, "remoteProviderMetadata", "method2", null,
						new TypeReference<ExtDirectStoreResult<Row>>() {
							// nothing here
						}, readRequest);

		assertThat(storeResponse.getTotal()).isEqualTo(50L);
		assertThat(storeResponse.getRecords()).hasSize(50);
		int i = 1;
		for (Row row : storeResponse.getRecords()) {
			assertThat(row.getName()).isEqualTo("firstname: " + i + ":1;true");
			i += 2;
		}
	}

	@Test
	public void testWithMetadataParameterEd() {
		Map<String, Object> readRequest = new HashMap<String, Object>();
		readRequest.put("id", 10);
		readRequest.put("query", "name");

		Map<String, Object> metadata = new HashMap<String, Object>();
		metadata.put("mp", "aMetadataValue");

		EdStoreResult storeResponse = (EdStoreResult) ControllerUtil.sendAndReceive(
				this.mockMvc, "remoteProviderMetadata", "method1Ed", metadata,
				EdStoreResult.class, readRequest);

		assertThat(storeResponse.total()).isEqualTo(50L);
		assertThat(storeResponse.records()).hasSize(50);
		int ix = 0;
		ObjectMapper om = new ObjectMapper();
		for (Map<String, Object> m : (Collection<Map<String, Object>>) storeResponse
				.records()) {
			Row row = om.convertValue(m, Row.class);
			assertThat(row.getName()).startsWith("name: " + ix + ":10;aMetadataValue;en");
			ix += 2;
		}

		readRequest = new HashMap<String, Object>();
		readRequest.put("id", 10);
		readRequest.put("query", "name");

		storeResponse = (EdStoreResult) ControllerUtil.sendAndReceive(this.mockMvc,
				"remoteProviderMetadata", "method1Ed", null, null, readRequest);
	}

	@Test
	public void testWithMetadataParameterDefaultValueEd() {
		Map<String, Object> readRequest = new HashMap<String, Object>();
		readRequest.put("query", "firstname");

		EdStoreResult storeResponse = (EdStoreResult) ControllerUtil.sendAndReceive(
				this.mockMvc, "remoteProviderMetadata", "method2Ed", null,
				EdStoreResult.class, readRequest);

		assertThat(storeResponse.total()).isEqualTo(50L);
		assertThat(storeResponse.records()).hasSize(50);
		int i = 1;
		ObjectMapper om = new ObjectMapper();
		for (Map<String, Object> m : (Collection<Map<String, Object>>) storeResponse
				.records()) {
			Row row = om.convertValue(m, Row.class);
			assertThat(row.getName()).isEqualTo("firstname: " + i + ":1;true");
			i += 2;
		}
	}

	@Test
	public void testWithMetadataParameterOptional() {

		List<Row> rows = (List<Row>) ControllerUtil.sendAndReceive(this.mockMvc,
				"remoteProviderMetadata", "method3", null,
				new TypeReference<List<Row>>() {
					// nothing here
				});
		RouterControllerStoreTest.assert100Rows(rows, ":null");

		Map<String, Object> readRequest = new HashMap<String, Object>();
		readRequest.put("query", "");

		Map<String, Object> metadata = new HashMap<String, Object>();
		metadata.put("id", "12");

		rows = (List<Row>) ControllerUtil.sendAndReceive(this.mockMvc,
				"remoteProviderMetadata", "method3", metadata,
				new TypeReference<List<Row>>() {
					// nothing here
				}, readRequest);
		RouterControllerStoreTest.assert100Rows(rows, ":12");
	}

	@Test
	public void testWithMetadataParameterJava8Optional() {

		List<Row> rows = (List<Row>) ControllerUtil.sendAndReceive(this.mockMvc,
				"remoteProviderMetadata", "method4", null,
				new TypeReference<List<Row>>() {
					// nothing here
				});
		RouterControllerStoreTest.assert100Rows(rows, ":null");

		Map<String, Object> readRequest = new HashMap<String, Object>();
		readRequest.put("query", "");

		Map<String, Object> metadata = new HashMap<String, Object>();
		metadata.put("id", "13");

		rows = (List<Row>) ControllerUtil.sendAndReceive(this.mockMvc,
				"remoteProviderMetadata", "method4", metadata,
				new TypeReference<List<Row>>() {
					// nothing here
				}, readRequest);
		RouterControllerStoreTest.assert100Rows(rows, ":13");
	}

	@Test
	public void testWithMetadataParameterOptionalDefaultValue() {
		Map<String, Object> readRequest = new HashMap<String, Object>();
		readRequest.put("query", "name");

		Map<String, Object> metadata = new HashMap<String, Object>();
		metadata.put("id", "10");

		ExtDirectStoreResult<Row> storeResponse = (ExtDirectStoreResult<Row>) ControllerUtil
				.sendAndReceive(this.mockMvc, "remoteProviderMetadata", "method5",
						metadata, new TypeReference<ExtDirectStoreResult<Row>>() {
							// nothing here
						}, readRequest);

		assertThat(storeResponse.getTotal()).isEqualTo(50L);
		assertThat(storeResponse.getRecords()).hasSize(50);
		int ix = 0;
		for (Row row : storeResponse.getRecords()) {
			assertThat(row.getName()).startsWith("name: " + ix + ":10;en");
			ix += 2;
		}

		readRequest = new HashMap<String, Object>();
		readRequest.put("query", "name");

		storeResponse = (ExtDirectStoreResult<Row>) ControllerUtil.sendAndReceive(
				this.mockMvc, "remoteProviderMetadata", "method5", null,
				new TypeReference<ExtDirectStoreResult<Row>>() {
					// nothing here
				}, readRequest);

		assertThat(storeResponse.getTotal()).isEqualTo(50L);
		assertThat(storeResponse.getRecords()).hasSize(50);
		ix = 0;
		for (Row row : storeResponse.getRecords()) {
			assertThat(row.getName()).startsWith("name: " + ix + ":20;en");
			ix += 2;
		}
	}

	@Test
	public void testWithMetadataParameterJava8OptionalDefaultValue() {
		Map<String, Object> readRequest = new HashMap<String, Object>();
		readRequest.put("query", "name");

		Map<String, Object> metadata = new HashMap<String, Object>();
		metadata.put("id", "10");

		ExtDirectStoreResult<Row> storeResponse = (ExtDirectStoreResult<Row>) ControllerUtil
				.sendAndReceive(this.mockMvc, "remoteProviderMetadata", "method6",
						metadata, new TypeReference<ExtDirectStoreResult<Row>>() {
							// nothing here
						}, readRequest);

		assertThat(storeResponse.getTotal()).isEqualTo(50L);
		assertThat(storeResponse.getRecords()).hasSize(50);
		int ix = 0;
		for (Row row : storeResponse.getRecords()) {
			assertThat(row.getName()).startsWith("name: " + ix + ":10;en");
			ix += 2;
		}

		readRequest = new HashMap<String, Object>();
		readRequest.put("query", "name");

		storeResponse = (ExtDirectStoreResult<Row>) ControllerUtil.sendAndReceive(
				this.mockMvc, "remoteProviderMetadata", "method6", null,
				new TypeReference<ExtDirectStoreResult<Row>>() {
					// nothing here
				}, readRequest);

		assertThat(storeResponse.getTotal()).isEqualTo(50L);
		assertThat(storeResponse.getRecords()).hasSize(50);
		ix = 0;
		for (Row row : storeResponse.getRecords()) {
			assertThat(row.getName()).startsWith("name: " + ix + ":20;en");
			ix += 2;
		}
	}

	@Test
	public void testWithMetadataParameterOptionalDefaultValueEd() {
		Map<String, Object> readRequest = new HashMap<String, Object>();
		readRequest.put("query", "name");

		Map<String, Object> metadata = new HashMap<String, Object>();
		metadata.put("id", "10");

		EdStoreResult storeResponse = (EdStoreResult) ControllerUtil.sendAndReceive(
				this.mockMvc, "remoteProviderMetadata", "method5Ed", metadata,
				EdStoreResult.class, readRequest);

		assertThat(storeResponse.total()).isEqualTo(50L);
		assertThat(storeResponse.records()).hasSize(50);
		int ix = 0;
		ObjectMapper om = new ObjectMapper();
		for (Map<String, Object> m : (Collection<Map<String, Object>>) storeResponse
				.records()) {
			Row row = om.convertValue(m, Row.class);
			assertThat(row.getName()).startsWith("name: " + ix + ":10;en");
			ix += 2;
		}

		readRequest = new HashMap<String, Object>();
		readRequest.put("query", "name");

		storeResponse = (EdStoreResult) ControllerUtil.sendAndReceive(this.mockMvc,
				"remoteProviderMetadata", "method5Ed", null, EdStoreResult.class,
				readRequest);

		assertThat(storeResponse.total()).isEqualTo(50L);
		assertThat(storeResponse.records()).hasSize(50);
		ix = 0;
		for (Map<String, Object> m : (Collection<Map<String, Object>>) storeResponse
				.records()) {
			Row row = om.convertValue(m, Row.class);
			assertThat(row.getName()).startsWith("name: " + ix + ":20;en");
			ix += 2;
		}
	}

	@Test
	public void testWithMetadataParameterJava8OptionalDefaultValueEd() {
		Map<String, Object> readRequest = new HashMap<String, Object>();
		readRequest.put("query", "name");

		Map<String, Object> metadata = new HashMap<String, Object>();
		metadata.put("id", "10");

		EdStoreResult storeResponse = (EdStoreResult) ControllerUtil.sendAndReceive(
				this.mockMvc, "remoteProviderMetadata", "method6Ed", metadata,
				EdStoreResult.class, readRequest);

		assertThat(storeResponse.total()).isEqualTo(50L);
		assertThat(storeResponse.records()).hasSize(50);
		int ix = 0;
		ObjectMapper om = new ObjectMapper();
		for (Map<String, Object> m : (Collection<Map<String, Object>>) storeResponse
				.records()) {
			Row row = om.convertValue(m, Row.class);
			assertThat(row.getName()).startsWith("name: " + ix + ":10;en");
			ix += 2;
		}

		readRequest = new HashMap<String, Object>();
		readRequest.put("query", "name");

		storeResponse = (EdStoreResult) ControllerUtil.sendAndReceive(this.mockMvc,
				"remoteProviderMetadata", "method6Ed", null, EdStoreResult.class,
				readRequest);

		assertThat(storeResponse.total()).isEqualTo(50L);
		assertThat(storeResponse.records()).hasSize(50);
		ix = 0;
		for (Map<String, Object> m : (Collection<Map<String, Object>>) storeResponse
				.records()) {
			Row row = om.convertValue(m, Row.class);
			assertThat(row.getName()).startsWith("name: " + ix + ":20;en");
			ix += 2;
		}
	}

	@Test
	public void testUpdateWithMetadataParam() throws Exception {
		Map<String, Object> storeRequest = new LinkedHashMap<String, Object>();
		List<Row> rowsToUpdate = new ArrayList<Row>();
		rowsToUpdate.add(new Row(10, "Ralph", true, "109.55"));
		storeRequest.put("records", rowsToUpdate);

		Map<String, Object> metadata = new HashMap<String, Object>();
		metadata.put("id", 10);

		executeUpdate(this.mockMvc, "remoteProviderMetadata", "update1", storeRequest,
				metadata);
	}

	@Test
	public void testUpdateWithMetadataParamDefaultValue() throws Exception {
		Map<String, Object> storeRequest = new LinkedHashMap<String, Object>();
		List<Row> rowsToUpdate = new ArrayList<Row>();
		rowsToUpdate.add(new Row(10, "Ralph", true, "109.55"));
		storeRequest.put("records", rowsToUpdate);

		Map<String, Object> metadata = new HashMap<String, Object>();
		metadata.put("id", 1);

		executeUpdate(this.mockMvc, "remoteProviderMetadata", "update2", storeRequest,
				metadata);

		executeUpdate(this.mockMvc, "remoteProviderMetadata", "update2", storeRequest,
				null);
	}

	@Test
	public void testUpdateWithMetadataParamJava8Optional() throws Exception {
		Map<String, Object> storeRequest = new LinkedHashMap<String, Object>();
		List<Row> rowsToUpdate = new ArrayList<Row>();
		rowsToUpdate.add(new Row(10, "Ralph", true, "109.55"));
		storeRequest.put("records", rowsToUpdate);

		Map<String, Object> metadata = new HashMap<String, Object>();
		metadata.put("id", 2);

		executeUpdate(this.mockMvc, "remoteProviderMetadata", "update3", storeRequest,
				metadata);

		executeUpdate(this.mockMvc, "remoteProviderMetadata", "update3", storeRequest,
				null);
	}

	private static void executeUpdate(MockMvc mockMvc, String action, String method,
			Map<String, Object> storeRequest, Map<String, Object> metadata)
			throws Exception {
		String edRequest = ControllerUtil.createEdsRequest(action, method, false, 1,
				storeRequest, metadata);

		MvcResult mvcResult = ControllerUtil.performRouterRequest(mockMvc, edRequest);
		List<ExtDirectResponse> responses = ControllerUtil
				.readDirectResponses(mvcResult.getResponse().getContentAsByteArray());

		assertThat(responses).hasSize(1);
		ExtDirectResponse resp = responses.get(0);
		assertThat(resp.getAction()).isEqualTo(action);
		assertThat(resp.getMethod()).isEqualTo(method);
		assertThat(resp.getType()).isEqualTo("rpc");
		assertThat(resp.getTid()).isEqualTo(1);
		assertThat(resp.getMessage()).isNull();
		assertThat(resp.getWhere()).isNull();
		assertThat(resp.getResult()).isNotNull();

		Object result = resp.getResult();
		if (result instanceof List) {
			List<Row> storeResponse = ControllerUtil.convertValue(result,
					new TypeReference<List<Row>>() {/* nothing_here */
					});
			assertThat(storeResponse).hasSize(1);
			assertThat(storeResponse.get(0).getId()).isEqualTo(10);
			assertThat(storeResponse.get(0).getName()).isEqualTo("Ralph");
			assertThat(storeResponse.get(0).isAdmin()).isTrue();
			assertThat(storeResponse.get(0).getSalary())
					.isEqualTo(new BigDecimal("109.55"));
		}
		else {
			Row storeResponse = ControllerUtil.convertValue(result, Row.class);
			assertThat(storeResponse.getId()).isEqualTo(10);
			assertThat(storeResponse.getName()).isEqualTo("Ralph");
			assertThat(storeResponse.isAdmin()).isTrue();
			assertThat(storeResponse.getSalary()).isEqualTo(new BigDecimal("109.55"));
		}
	}

	@Test
	public void testTreeLoadMetadataParam() {

		Map<String, Object> requestParameters = new LinkedHashMap<String, Object>();
		requestParameters.put("node", "root");
		requestParameters.put("foo", "foo");

		Map<String, Object> metadata = new HashMap<String, Object>();
		metadata.put("id", 2);

		List<Node> nodes = (List<Node>) ControllerUtil.sendAndReceive(this.mockMvc,
				"remoteProviderMetadata", "treeLoad1", metadata,
				new TypeReference<List<Node>>() {/* nothinghere */
				}, requestParameters);

		String appendix = ":foo;2";
		assertThat(nodes).hasSize(5).containsSequence(
				new Node("n1", "Node 1" + appendix, false),
				new Node("n2", "Node 2" + appendix, false),
				new Node("n3", "Node 3" + appendix, false),
				new Node("n4", "Node 4" + appendix, false),
				new Node("n5", "Node 5" + appendix, false));

		ControllerUtil.sendAndReceive(this.mockMvc, "remoteProviderMetadata", "treeLoad1",
				null, null, requestParameters);
	}

	@Test
	public void testTreeLoadMetadataParamOptional() {

		Map<String, Object> requestParameters = new LinkedHashMap<String, Object>();
		requestParameters.put("node", "root");
		requestParameters.put("foo", "foo");

		Map<String, Object> metadata = new HashMap<String, Object>();
		metadata.put("id", 22);

		List<Node> nodes = (List<Node>) ControllerUtil.sendAndReceive(this.mockMvc,
				"remoteProviderMetadata", "treeLoad2", metadata,
				new TypeReference<List<Node>>() {/* nothinghere */
				}, requestParameters);

		String appendix = ":foo;22";
		assertThat(nodes).hasSize(5).containsSequence(
				new Node("n1", "Node 1" + appendix, false),
				new Node("n2", "Node 2" + appendix, false),
				new Node("n3", "Node 3" + appendix, false),
				new Node("n4", "Node 4" + appendix, false),
				new Node("n5", "Node 5" + appendix, false));

		nodes = (List<Node>) ControllerUtil.sendAndReceive(this.mockMvc,
				"remoteProviderMetadata", "treeLoad2", null,
				new TypeReference<List<Node>>() {/* nothinghere */
				}, requestParameters);

		appendix = ":foo;22";
		assertThat(nodes).hasSize(5).containsSequence(
				new Node("n1", "Node 1" + appendix, false),
				new Node("n2", "Node 2" + appendix, false),
				new Node("n3", "Node 3" + appendix, false),
				new Node("n4", "Node 4" + appendix, false),
				new Node("n5", "Node 5" + appendix, false));
	}

	@Test
	public void testTreeLoadMetadataParamJava8Optional() {

		Map<String, Object> requestParameters = new LinkedHashMap<String, Object>();
		requestParameters.put("node", "root");
		requestParameters.put("foo", "foo");

		Map<String, Object> metadata = new HashMap<String, Object>();
		metadata.put("id", 23);

		List<Node> nodes = (List<Node>) ControllerUtil.sendAndReceive(this.mockMvc,
				"remoteProviderMetadata", "treeLoad3", metadata,
				new TypeReference<List<Node>>() {/* nothinghere */
				}, requestParameters);

		String appendix = ":foo;23";
		assertThat(nodes).hasSize(5).containsSequence(
				new Node("n1", "Node 1" + appendix, false),
				new Node("n2", "Node 2" + appendix, false),
				new Node("n3", "Node 3" + appendix, false),
				new Node("n4", "Node 4" + appendix, false),
				new Node("n5", "Node 5" + appendix, false));

		nodes = (List<Node>) ControllerUtil.sendAndReceive(this.mockMvc,
				"remoteProviderMetadata", "treeLoad3", null,
				new TypeReference<List<Node>>() {/* nothinghere */
				}, requestParameters);

		appendix = ":foo;23";
		assertThat(nodes).hasSize(5).containsSequence(
				new Node("n1", "Node 1" + appendix, false),
				new Node("n2", "Node 2" + appendix, false),
				new Node("n3", "Node 3" + appendix, false),
				new Node("n4", "Node 4" + appendix, false),
				new Node("n5", "Node 5" + appendix, false));
	}
}
