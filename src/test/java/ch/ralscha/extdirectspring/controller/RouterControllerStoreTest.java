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
import static org.assertj.core.api.Assertions.entry;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.Cookie;

import org.joda.time.DateTime;
import org.joda.time.format.ISODateTimeFormat;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.fasterxml.jackson.core.type.TypeReference;

import ch.ralscha.extdirectspring.bean.ExtDirectResponse;
import ch.ralscha.extdirectspring.bean.ExtDirectStoreReadRequest;
import ch.ralscha.extdirectspring.bean.ExtDirectStoreResult;
import ch.ralscha.extdirectspring.provider.Row;

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration("classpath:/testApplicationContext.xml")
@SuppressWarnings("unchecked")
public class RouterControllerStoreTest {

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

	public static void assert100Rows(List<Row> rows, String appendix) {
		assertThat(rows.size()).isEqualTo(100);

		for (int i = 0; i < rows.size(); i += 2) {
			assertThat(rows.get(i)).isEqualTo(
					new Row(i, "name: " + i + appendix, true, "" + (1000 + i)));
			assertThat(rows.get(i + 1)).isEqualTo(new Row(i + 1,
					"firstname: " + (i + 1) + appendix, false, "" + (10 + i + 1)));
		}
	}

	@Test
	public void testNoArgumentsNoRequestParameters() {
		List<Row> rows = (List<Row>) ControllerUtil.sendAndReceive(this.mockMvc,
				"remoteProviderStoreRead", "method1",
				new TypeReference<List<Row>>() {/*
												 * nothing here
												 */
				});
		assert100Rows(rows, "");
	}

	@Test
	public void testNoArgumentsWithRequestParameters() {
		ExtDirectStoreReadRequest storeRead = new ExtDirectStoreReadRequest();
		storeRead.setQuery("ralph");

		List<Row> rows = (List<Row>) ControllerUtil.sendAndReceive(this.mockMvc,
				"remoteProviderStoreRead", "method1",
				new TypeReference<List<Row>>() {/*
												 * nothing here
												 */
				}, storeRead);
		assert100Rows(rows, "");
	}

	@Test
	public void testReturnsNull() {
		ControllerUtil.sendAndReceive(this.mockMvc, "remoteProviderStoreRead", "method2",
				Collections.emptyList());
	}

	@Test
	public void testSupportedArguments() {
		List<Row> rows = (List<Row>) ControllerUtil.sendAndReceive(this.mockMvc,
				"remoteProviderStoreRead", "method3",
				new TypeReference<List<Row>>() {/*
												 * nothing here
												 */
				});
		assert100Rows(rows, ":true;true:true;en");
	}

	@Test
	public void testWithExtDirectStoreReadRequest() throws Exception {
		Map<String, Object> storeRead = new LinkedHashMap<String, Object>();
		storeRead.put("query", "name");
		ExtDirectStoreResult<Row> storeResponse = executeWithExtDirectStoreReadRequest(
				storeRead);
		assertThat(storeResponse.getTotal()).isEqualTo(50L);
		assertThat(storeResponse.getRecords()).hasSize(50);
		for (Row row : storeResponse.getRecords()) {
			assertThat(row.getName()).startsWith("name");
		}

		storeRead = new LinkedHashMap<String, Object>();
		storeRead.put("query", "firstname");
		storeResponse = executeWithExtDirectStoreReadRequest(storeRead);
		assertThat(storeResponse.getTotal()).isEqualTo(50L);
		assertThat(storeResponse.getRecords()).hasSize(50);
		for (Row row : storeResponse.getRecords()) {
			assertThat(row.getName()).startsWith("firstname");
		}

		storeRead = new LinkedHashMap<String, Object>();
		storeRead.put("query", "");
		storeRead.put("sort", "id");
		storeRead.put("dir", "ASC");
		storeRead.put("limit", "10");
		storeRead.put("start", "10");

		storeResponse = executeWithExtDirectStoreReadRequest(storeRead);

		assertThat(storeResponse.getTotal()).isEqualTo(100L);
		assertThat(storeResponse.getRecords()).hasSize(10);
		int id = 10;
		for (Row row : storeResponse.getRecords()) {
			assertThat(row.getId()).isEqualTo(id);
			id++;
		}

		storeRead = new LinkedHashMap<String, Object>();
		storeRead.put("query", "");
		storeRead.put("sort", "id");
		storeRead.put("dir", "DESC");
		storeRead.put("limit", "10");
		storeRead.put("start", "20");

		storeResponse = executeWithExtDirectStoreReadRequest(storeRead);

		assertThat(storeResponse.getTotal()).isEqualTo(100L);
		assertThat(storeResponse.getRecords()).hasSize(10);
		id = 79;
		for (Row row : storeResponse.getRecords()) {
			assertThat(row.getId()).isEqualTo(id);
			id--;
		}

		storeRead = new LinkedHashMap<String, Object>();
		storeRead.put("query", "");
		storeRead.put("groupBy", "id");
		storeRead.put("groupDir", "ASC");
		storeRead.put("limit", "10");
		storeRead.put("start", "10");

		storeResponse = executeWithExtDirectStoreReadRequest(storeRead);

		assertThat(storeResponse.getTotal()).isEqualTo(100L);
		assertThat(storeResponse.getRecords()).hasSize(10);
		id = 10;
		for (Row row : storeResponse.getRecords()) {
			assertThat(row.getId()).isEqualTo(id);
			id++;
		}

		storeRead = new LinkedHashMap<String, Object>();
		storeRead.put("query", "");
		storeRead.put("groupBy", "id");
		storeRead.put("groupDir", "DESC");
		storeRead.put("limit", "10");
		storeRead.put("start", "20");

		storeResponse = executeWithExtDirectStoreReadRequest(storeRead);

		assertThat(storeResponse.getTotal()).isEqualTo(100L);
		assertThat(storeResponse.getRecords()).hasSize(10);
		id = 79;
		for (Row row : storeResponse.getRecords()) {
			assertThat(row.getId()).isEqualTo(id);
			id--;
		}

		storeRead = new LinkedHashMap<String, Object>();
		storeRead.put("query", "");
		storeRead.put("sort", "id");
		storeRead.put("dir", "ASC");
		storeRead.put("limit", "10");
		storeRead.put("start", "0");
		storeRead.put("page", "1");

		storeResponse = executeWithExtDirectStoreReadRequest(storeRead);

		assertThat(storeResponse.getTotal()).isEqualTo(100L);
		assertThat(storeResponse.getRecords()).hasSize(10);
		id = 0;
		for (Row row : storeResponse.getRecords()) {
			assertThat(row.getId()).isEqualTo(id);
			id++;
		}

		storeRead = new LinkedHashMap<String, Object>();
		storeRead.put("query", "");
		storeRead.put("sort", "id");
		storeRead.put("dir", "ASC");
		storeRead.put("limit", "10");
		storeRead.put("start", "10");
		storeRead.put("page", "2");

		storeResponse = executeWithExtDirectStoreReadRequest(storeRead);

		assertThat(storeResponse.getTotal()).isEqualTo(100L);
		assertThat(storeResponse.getRecords()).hasSize(10);
		id = 10;
		for (Row row : storeResponse.getRecords()) {
			assertThat(row.getId()).isEqualTo(id);
			id++;
		}
	}

	@Test
	public void testWithExtDirectStoreReadRequestMultipeGroups() throws Exception {
		Map<String, Object> storeRead = new LinkedHashMap<String, Object>();
		storeRead.put("query", "");

		List<Map<String, Object>> groups = new ArrayList<Map<String, Object>>();
		Map<String, Object> groupInfo = new LinkedHashMap<String, Object>();
		groupInfo.put("property", "id");
		groupInfo.put("direction", "ASC");
		groups.add(groupInfo);
		storeRead.put("group", groups);
		storeRead.put("limit", "10");
		storeRead.put("start", "10");
		ExtDirectStoreResult<Row> storeResponse = executeWithExtDirectStoreReadRequest(
				storeRead);

		assertThat(storeResponse.getTotal()).isEqualTo(100L);
		assertThat(storeResponse.getRecords()).hasSize(10);
		int id = 10;
		for (Row row : storeResponse.getRecords()) {
			assertThat(row.getId()).isEqualTo(id);
			id++;
		}

		storeRead = new LinkedHashMap<String, Object>();
		storeRead.put("query", "");
		groups = new ArrayList<Map<String, Object>>();
		groupInfo = new LinkedHashMap<String, Object>();
		groupInfo.put("property", "id");
		groupInfo.put("direction", "DESC");
		groups.add(groupInfo);
		storeRead.put("group", groups);
		storeRead.put("limit", "10");
		storeRead.put("start", "20");
		storeResponse = executeWithExtDirectStoreReadRequest(storeRead);

		assertThat(storeResponse.getTotal()).isEqualTo(100L);
		assertThat(storeResponse.getRecords()).hasSize(10);
		id = 79;
		for (Row row : storeResponse.getRecords()) {
			assertThat(row.getId()).isEqualTo(id);
			id--;
		}
	}

	@Test
	public void testWithExtDirectStoreReadRequestMultipleSorters() throws Exception {
		Map<String, Object> storeRead = new LinkedHashMap<String, Object>();
		storeRead.put("query", "");

		List<Map<String, Object>> sorters = new ArrayList<Map<String, Object>>();
		Map<String, Object> sortInfo = new LinkedHashMap<String, Object>();
		sortInfo.put("property", "id");
		sortInfo.put("direction", "ASC");
		sorters.add(sortInfo);
		storeRead.put("sort", sorters);

		storeRead.put("limit", "10");
		storeRead.put("start", "10");
		storeRead.put("page", "2");
		ExtDirectStoreResult<Row> storeResponse = executeWithExtDirectStoreReadRequest(
				storeRead);

		assertThat(storeResponse.getTotal()).isEqualTo(100L);
		assertThat(storeResponse.getRecords()).hasSize(10);
		int id = 10;
		for (Row row : storeResponse.getRecords()) {
			assertThat(row.getId()).isEqualTo(id);
			id++;
		}

		storeRead = new LinkedHashMap<String, Object>();
		storeRead.put("query", "");
		sorters = new ArrayList<Map<String, Object>>();
		sortInfo = new LinkedHashMap<String, Object>();
		sortInfo.put("property", "id");
		sortInfo.put("direction", "DESC");
		sorters.add(sortInfo);
		storeRead.put("sort", sorters);
		storeRead.put("limit", "10");
		storeRead.put("start", "20");
		storeRead.put("page", "3");
		storeResponse = executeWithExtDirectStoreReadRequest(storeRead);

		assertThat(storeResponse.getTotal()).isEqualTo(100L);
		assertThat(storeResponse.getRecords()).hasSize(10);
		id = 79;
		for (Row row : storeResponse.getRecords()) {
			assertThat(row.getId()).isEqualTo(id);
			id--;
		}

		storeRead = new LinkedHashMap<String, Object>();
		storeRead.put("query", "");
		sorters = new ArrayList<Map<String, Object>>();
		sortInfo = new LinkedHashMap<String, Object>();
		sortInfo.put("property", "id");
		sortInfo.put("direction", "ASC");
		sorters.add(sortInfo);
		storeRead.put("sort", sorters);
		storeRead.put("limit", "10");
		storeRead.put("start", "10");
		storeRead.put("page", "2");
		storeResponse = executeWithExtDirectStoreReadRequest(storeRead);

		assertThat(storeResponse.getTotal()).isEqualTo(100L);
		assertThat(storeResponse.getRecords()).hasSize(10);
		id = 10;
		for (Row row : storeResponse.getRecords()) {
			assertThat(row.getId()).isEqualTo(id);
			id++;
		}

		storeRead = new LinkedHashMap<String, Object>();
		storeRead.put("query", "");
		sorters = new ArrayList<Map<String, Object>>();
		sortInfo = new LinkedHashMap<String, Object>();
		sortInfo.put("property", "id");
		sortInfo.put("direction", "DESC");
		sorters.add(sortInfo);
		storeRead.put("sort", sorters);
		storeRead.put("limit", "10");
		storeRead.put("start", "20");
		storeRead.put("page", "3");
		storeResponse = executeWithExtDirectStoreReadRequest(storeRead);

		assertThat(storeResponse.getTotal()).isEqualTo(100L);
		assertThat(storeResponse.getRecords()).hasSize(10);
		id = 79;
		for (Row row : storeResponse.getRecords()) {
			assertThat(row.getId()).isEqualTo(id);
			id--;
		}

		storeRead = new LinkedHashMap<String, Object>();
		storeRead.put("query", "");
		sorters = new ArrayList<Map<String, Object>>();
		sortInfo = new LinkedHashMap<String, Object>();
		sortInfo.put("property", "id");
		sortInfo.put("direction", "ASC");
		sorters.add(sortInfo);
		storeRead.put("sort", sorters);
		storeRead.put("limit", "10");
		storeRead.put("start", "0");
		storeRead.put("page", "1");
		storeResponse = executeWithExtDirectStoreReadRequest(storeRead);

		assertThat(storeResponse.getTotal()).isEqualTo(100L);
		assertThat(storeResponse.getRecords()).hasSize(10);
		id = 0;
		for (Row row : storeResponse.getRecords()) {
			assertThat(row.getId()).isEqualTo(id);
			id++;
		}

		storeRead = new LinkedHashMap<String, Object>();
		storeRead.put("query", "");
		sorters = new ArrayList<Map<String, Object>>();
		sortInfo = new LinkedHashMap<String, Object>();
		sortInfo.put("property", "id");
		sortInfo.put("direction", "ASC");
		sorters.add(sortInfo);
		storeRead.put("sort", sorters);
		storeRead.put("limit", "10");
		storeRead.put("start", "10");
		storeRead.put("page", "2");
		storeResponse = executeWithExtDirectStoreReadRequest(storeRead);

		assertThat(storeResponse.getTotal()).isEqualTo(100L);
		assertThat(storeResponse.getRecords()).hasSize(10);
		id = 10;
		for (Row row : storeResponse.getRecords()) {
			assertThat(row.getId()).isEqualTo(id);
			id++;
		}
	}

	private ExtDirectStoreResult<Row> executeWithExtDirectStoreReadRequest(
			Map<String, Object> storeRead) throws Exception {

		String edRequest = ControllerUtil.createEdsRequest("remoteProviderStoreRead",
				"method4", 1, storeRead);

		MvcResult result = ControllerUtil.performRouterRequest(this.mockMvc, edRequest);
		List<ExtDirectResponse> responses = ControllerUtil
				.readDirectResponses(result.getResponse().getContentAsByteArray());

		assertThat(responses).hasSize(1);
		ExtDirectResponse resp = responses.get(0);

		assertThat(resp.getAction()).isEqualTo("remoteProviderStoreRead");
		assertThat(resp.getMethod()).isEqualTo("method4");
		assertThat(resp.getType()).isEqualTo("rpc");
		assertThat(resp.getTid()).isEqualTo(1);
		assertThat(resp.getMessage()).isNull();
		assertThat(resp.getWhere()).isNull();
		assertThat(resp.getResult()).isNotNull();

		return ControllerUtil.convertValue(resp.getResult(),
				new TypeReference<ExtDirectStoreResult<Row>>() {/* nothing_here */
				});
	}

	@Test
	public void testWithAdditionalParameters() {
		Map<String, Object> readRequest = new HashMap<String, Object>();
		readRequest.put("id", 10);
		readRequest.put("query", "name");

		ExtDirectStoreResult<Row> storeResponse = (ExtDirectStoreResult<Row>) ControllerUtil
				.sendAndReceive(this.mockMvc, "remoteProviderStoreRead", "method5",
						new TypeReference<ExtDirectStoreResult<Row>>() {
							// nothing here
						}, readRequest);

		assertThat(storeResponse.getTotal()).isEqualTo(50L);
		assertThat(storeResponse.getRecords()).hasSize(50);
		for (Row row : storeResponse.getRecords()) {
			assertThat(row.getName()).startsWith("name");
		}

		readRequest = new HashMap<String, Object>();
		readRequest.put("query", "name");

		storeResponse = (ExtDirectStoreResult<Row>) ControllerUtil.sendAndReceive(
				this.mockMvc, "remoteProviderStoreRead", "method5", null, null,
				readRequest);
	}

	@Test
	public void testWithAdditionalParametersDefaultValue() {
		Map<String, Object> readRequest = new HashMap<String, Object>();
		readRequest.put("query", "firstname");

		ExtDirectStoreResult<Row> storeResponse = (ExtDirectStoreResult<Row>) ControllerUtil
				.sendAndReceive(this.mockMvc, "remoteProviderStoreRead", "method6",
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
	public void testWithAdditionalParametersOptional() {

		List<Row> rows = (List<Row>) ControllerUtil.sendAndReceive(this.mockMvc,
				"remoteProviderStoreRead", "method7", new TypeReference<List<Row>>() {
					// nothing here
				});
		assert100Rows(rows, ":null");

		Map<String, Object> readRequest = new HashMap<String, Object>();
		readRequest.put("id", 11);
		readRequest.put("query", "");

		rows = (List<Row>) ControllerUtil.sendAndReceive(this.mockMvc,
				"remoteProviderStoreRead", "method7", new TypeReference<List<Row>>() {
					// nothing here
				}, readRequest);
		assert100Rows(rows, ":11");

	}

	@Test
	public void testWithAdditionalParametersAndConversion() {
		DateTime today = new DateTime();
		Map<String, Object> readRequest = new HashMap<String, Object>();
		readRequest.put("endDate", ISODateTimeFormat.dateTime().print(today));

		ExtDirectStoreResult<Row> storeResponse = (ExtDirectStoreResult<Row>) ControllerUtil
				.sendAndReceive(this.mockMvc, "remoteProviderStoreRead", "method8",
						new TypeReference<ExtDirectStoreResult<Row>>() {
							// nothing here
						}, readRequest);

		assertThat(storeResponse.getRecords()).hasSize(50);

	}

	@Test
	public void testMessageProperty() {
		Map<String, Object> readRequest = new HashMap<String, Object>();
		ExtDirectStoreResult<Row> storeResponse = (ExtDirectStoreResult<Row>) ControllerUtil
				.sendAndReceive(this.mockMvc, "remoteProviderStoreRead", "method9",
						new TypeReference<ExtDirectStoreResult<Row>>() {
							// nothing here
						}, readRequest);

		assertThat(storeResponse.getJsonView()).isNull();
		assertThat(storeResponse.getMessage()).isEqualTo("everything is okay");
		assertThat(storeResponse.getTotal()).isEqualTo(100L);
		assertThat(storeResponse.getRecords()).hasSize(50);

	}

	@Test
	public void testRequestParam() {
		Map<String, Object> readRequest = new HashMap<String, Object>();
		readRequest.put("id", 10);
		readRequest.put("query", "name");

		ExtDirectStoreResult<Row> storeResponse = (ExtDirectStoreResult<Row>) ControllerUtil
				.sendAndReceive(this.mockMvc, "remoteProviderStoreRead", "method10",
						new TypeReference<ExtDirectStoreResult<Row>>() {
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
				this.mockMvc, "remoteProviderStoreRead", "method10",
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
	public void testCookieAndRequestHeader() {

		HttpHeaders headers = new HttpHeaders();
		headers.add("requestHeader", "rValue");

		List<Cookie> cookies = new ArrayList<Cookie>();
		cookies.add(new Cookie("cookie", "cValue"));

		Map<String, Object> readRequest = new HashMap<String, Object>();
		readRequest.put("query", "name");

		ExtDirectStoreResult<Row> storeResponse = (ExtDirectStoreResult<Row>) ControllerUtil
				.sendAndReceive(this.mockMvc, headers, cookies, "remoteProviderStoreRead",
						"method11", new TypeReference<ExtDirectStoreResult<Row>>() {
							// nothing here
						}, readRequest);

		assertThat(storeResponse.getTotal()).isEqualTo(50L);
		assertThat(storeResponse.getRecords()).hasSize(50);
		int ix = 0;
		for (Row row : storeResponse.getRecords()) {
			assertThat(row.getName()).startsWith("name: " + ix + ":cValue:rValue");
			ix += 2;
		}

		readRequest = new HashMap<String, Object>();
		readRequest.put("query", "name");

		storeResponse = (ExtDirectStoreResult<Row>) ControllerUtil.sendAndReceive(
				this.mockMvc, "remoteProviderStoreRead", "method11",
				new TypeReference<ExtDirectStoreResult<Row>>() {
					// nothing here
				}, readRequest);

		assertThat(storeResponse.getTotal()).isEqualTo(50L);
		assertThat(storeResponse.getRecords()).hasSize(50);
		ix = 0;
		for (Row row : storeResponse.getRecords()) {
			assertThat(row.getName())
					.startsWith("name: " + ix + ":defaultCookie:defaultHeader");
			ix += 2;
		}
	}

	@Test
	public void testMetadata() throws Exception {

		String edRequest = ControllerUtil.createEdsRequest("remoteProviderStoreRead",
				"methodMetadata", 1, new HashMap<String, Object>());

		MvcResult result = ControllerUtil.performRouterRequest(this.mockMvc, edRequest);
		List<ExtDirectResponse> responses = ControllerUtil
				.readDirectResponses(result.getResponse().getContentAsByteArray());

		assertThat(responses).hasSize(1);
		ExtDirectResponse resp = responses.get(0);

		assertThat(resp.getAction()).isEqualTo("remoteProviderStoreRead");
		assertThat(resp.getMethod()).isEqualTo("methodMetadata");
		assertThat(resp.getType()).isEqualTo("rpc");
		assertThat(resp.getTid()).isEqualTo(1);
		assertThat(resp.getMessage()).isNull();
		assertThat(resp.getWhere()).isNull();
		assertThat(resp.getResult()).isNotNull();

		Map<String, Object> response = (Map<String, Object>) resp.getResult();
		assertThat(((List<Object>) response.get("records")).size()).isEqualTo(50);
		assertThat((Integer) response.get("total")).isEqualTo(100);
		Map<String, Object> metadata = (Map<String, Object>) response.get("metaData");
		assertThat(metadata).isNotNull();

		assertThat(metadata).contains(entry("root", "records"));
		assertThat(metadata).contains(entry("totalProperty", "total"));
		assertThat(metadata).contains(entry("successProperty", "success"));
		assertThat(metadata).contains(entry("start", 0));
		assertThat(metadata).contains(entry("limit", 50));

		Map<String, String> sortInfo = (Map<String, String>) metadata.get("sortInfo");
		assertThat(sortInfo).hasSize(2);
		assertThat(sortInfo).contains(entry("field", "name"));
		assertThat(sortInfo).contains(entry("direction", "ASC"));

		List<Map<String, Object>> fields = (List<Map<String, Object>>) metadata
				.get("fields");
		assertThat(fields).hasSize(4);

		Map<String, Object> field1 = fields.get(0);
		assertThat(field1).contains(entry("name", "id"));
		assertThat(field1).contains(entry("type", "int"));
		assertThat(field1).contains(entry("header", "ID"));
		assertThat(field1).contains(entry("width", 20));
		assertThat(field1).contains(entry("sortable", Boolean.TRUE));
		assertThat(field1).contains(entry("resizable", Boolean.TRUE));
		assertThat(field1).contains(entry("hideable", Boolean.FALSE));

		Map<String, Object> field2 = fields.get(1);
		assertThat(field2).contains(entry("name", "name"));
		assertThat(field2).contains(entry("type", "string"));
		assertThat(field2).contains(entry("header", "Name"));
		assertThat(field2).contains(entry("width", 70));
		assertThat(field2).contains(entry("sortable", Boolean.TRUE));
		assertThat(field2).contains(entry("resizable", Boolean.TRUE));
		assertThat(field2).contains(entry("hideable", Boolean.FALSE));

		Map<String, Object> field3 = fields.get(2);
		assertThat(field3).contains(entry("name", "admin"));
		assertThat(field3).contains(entry("type", "boolean"));
		assertThat(field3).contains(entry("header", "Administrator"));
		assertThat(field3).contains(entry("width", 30));
		assertThat(field3).contains(entry("sortable", Boolean.TRUE));
		assertThat(field3).contains(entry("resizable", Boolean.TRUE));
		assertThat(field3).contains(entry("hideable", Boolean.TRUE));

		Map<String, Object> field4 = fields.get(3);
		assertThat(field4).contains(entry("name", "salary"));
		assertThat(field4).contains(entry("type", "float"));
		assertThat(field4).contains(entry("header", "Salary"));
		assertThat(field4).contains(entry("width", 50));
		assertThat(field4).contains(entry("sortable", Boolean.FALSE));
		assertThat(field4).contains(entry("resizable", Boolean.TRUE));
		assertThat(field4).contains(entry("hideable", Boolean.TRUE));

	}

}
