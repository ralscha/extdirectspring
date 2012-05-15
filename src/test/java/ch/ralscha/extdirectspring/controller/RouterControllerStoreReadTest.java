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
import static org.fest.assertions.MapAssert.entry;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.codehaus.jackson.type.TypeReference;
import org.joda.time.DateTime;
import org.joda.time.format.ISODateTimeFormat;
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
import ch.ralscha.extdirectspring.bean.GroupInfo;
import ch.ralscha.extdirectspring.bean.SortDirection;
import ch.ralscha.extdirectspring.bean.SortInfo;
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

	public static void assert100Rows(List<Row> rows, String appendix) {
		assertThat(rows.size()).isEqualTo(100);

		for (int i = 0; i < rows.size(); i += 2) {
			assertThat(rows.get(i)).isEqualTo(new Row(i, "name: " + i + appendix, true, "" + (1000 + i)));
			assertThat(rows.get(i + 1)).isEqualTo(new Row(i + 1, "firstname: " + (i + 1) + appendix, false, "" + (10 + i + 1)));
		}
	}

	@Test
	public void testNoArgumentsNoRequestParameters() throws IOException {
		List<Row> rows = (List<Row>) ControllerUtil.sendAndReceive(controller, "remoteProviderStoreRead", "method1", null,
				new TypeReference<List<Row>>() {/* nothing here */
				});
		assert100Rows(rows, "");
	}

	@Test
	public void testNoArgumentsWithRequestParameters() throws IOException {
		ExtDirectStoreReadRequest storeRead = new ExtDirectStoreReadRequest();
		storeRead.setQuery("ralph");

		List<Row> rows = (List<Row>) ControllerUtil.sendAndReceive(controller, "remoteProviderStoreRead", "method1", storeRead,
				new TypeReference<List<Row>>() {/* nothing here */
				});
		assert100Rows(rows, "");
	}

	@Test
	public void testReturnsNull() throws IOException {
		ControllerUtil.sendAndReceive(controller, "remoteProviderStoreRead", "method2", null, Collections.emptyList());
	}

	@Test
	public void testSupportedArguments() throws IOException {
		List<Row> rows = (List<Row>) ControllerUtil.sendAndReceive(controller, "remoteProviderStoreRead", "method3", null,
				new TypeReference<List<Row>>() {/* nothing here */
				});
		assert100Rows(rows, ":true;true:true;en");
	}

	@Test
	public void testWithExtDirectStoreReadRequest() throws IOException {
		ExtDirectStoreReadRequest storeRead = new ExtDirectStoreReadRequest();
		storeRead.setQuery("name");
		ExtDirectStoreResponse<Row> storeResponse = executeWithExtDirectStoreReadRequest(storeRead);
		assertThat(storeResponse.getTotal()).isEqualTo(Integer.valueOf(50));
		assertThat(storeResponse.getRecords()).hasSize(50);
		for (Row row : storeResponse.getRecords()) {
			assertThat(row.getName().startsWith("name")).isTrue();
		}

		storeRead = new ExtDirectStoreReadRequest();
		storeRead.setQuery("firstname");
		storeResponse = executeWithExtDirectStoreReadRequest(storeRead);
		assertThat(storeResponse.getTotal()).isEqualTo(Integer.valueOf(50));
		assertThat(storeResponse.getRecords()).hasSize(50);
		for (Row row : storeResponse.getRecords()) {
			assertThat(row.getName()).startsWith("firstname");
		}

		storeRead = new ExtDirectStoreReadRequest();
		storeRead.setQuery("");
		storeRead.setSort("id");
		storeRead.setDir("ASC");
		storeRead.setLimit(10);
		storeRead.setStart(10);
		assertThat(storeRead.isAscendingSort()).isTrue();
		assertThat(storeRead.isDescendingSort()).isFalse();

		storeResponse = executeWithExtDirectStoreReadRequest(storeRead);

		assertThat(storeResponse.getTotal()).isEqualTo(Integer.valueOf(100));
		assertThat(storeResponse.getRecords()).hasSize(10);
		int id = 10;
		for (Row row : storeResponse.getRecords()) {
			assertThat(row.getId()).isEqualTo(id);
			id++;
		}

		storeRead = new ExtDirectStoreReadRequest();
		storeRead.setQuery("");
		storeRead.setSort("id");
		storeRead.setDir("DESC");
		storeRead.setLimit(10);
		storeRead.setStart(20);
		assertThat(storeRead.isAscendingSort()).isFalse();
		assertThat(storeRead.isDescendingSort()).isTrue();

		storeResponse = executeWithExtDirectStoreReadRequest(storeRead);

		assertThat(storeResponse.getTotal()).isEqualTo(Integer.valueOf(100));
		assertThat(storeResponse.getRecords()).hasSize(10);
		id = 79;
		for (Row row : storeResponse.getRecords()) {
			assertThat(row.getId()).isEqualTo(id);
			id--;
		}

		storeRead = new ExtDirectStoreReadRequest();
		storeRead.setQuery("");
		storeRead.setGroupBy("id");
		storeRead.setGroupDir("ASC");
		storeRead.setLimit(10);
		storeRead.setStart(10);
		assertThat(storeRead.isAscendingGroupSort()).isTrue();
		assertThat(storeRead.isDescendingGroupSort()).isFalse();

		storeResponse = executeWithExtDirectStoreReadRequest(storeRead);

		assertThat(storeResponse.getTotal()).isEqualTo(Integer.valueOf(100));
		assertThat(storeResponse.getRecords()).hasSize(10);
		id = 10;
		for (Row row : storeResponse.getRecords()) {
			assertThat(row.getId()).isEqualTo(id);
			id++;
		}

		storeRead = new ExtDirectStoreReadRequest();
		storeRead.setQuery("");
		storeRead.setGroupBy("id");
		storeRead.setGroupDir("DESC");
		storeRead.setLimit(10);
		storeRead.setStart(20);
		assertThat(storeRead.isAscendingGroupSort()).isFalse();
		assertThat(storeRead.isDescendingGroupSort()).isTrue();

		storeResponse = executeWithExtDirectStoreReadRequest(storeRead);

		assertThat(storeResponse.getTotal()).isEqualTo(Integer.valueOf(100));
		assertThat(storeResponse.getRecords()).hasSize(10);
		id = 79;
		for (Row row : storeResponse.getRecords()) {
			assertThat(row.getId()).isEqualTo(id);
			id--;
		}

		storeRead = new ExtDirectStoreReadRequest();
		storeRead.setQuery("");
		storeRead.setSort("id");
		storeRead.setDir("ASC");
		storeRead.setPage(1);
		storeRead.setStart(0);
		storeRead.setLimit(10);
		storeResponse = executeWithExtDirectStoreReadRequest(storeRead);

		assertThat(storeResponse.getTotal()).isEqualTo(Integer.valueOf(100));
		assertThat(storeResponse.getRecords()).hasSize(10);
		id = 0;
		for (Row row : storeResponse.getRecords()) {
			assertThat(row.getId()).isEqualTo(id);
			id++;
		}

		storeRead = new ExtDirectStoreReadRequest();
		storeRead.setQuery("");
		storeRead.setSort("id");
		storeRead.setDir("ASC");
		storeRead.setPage(2);
		storeRead.setStart(10);
		storeRead.setLimit(10);
		storeResponse = executeWithExtDirectStoreReadRequest(storeRead);

		assertThat(storeResponse.getTotal()).isEqualTo(Integer.valueOf(100));
		assertThat(storeResponse.getRecords()).hasSize(10);
		id = 10;
		for (Row row : storeResponse.getRecords()) {
			assertThat(row.getId()).isEqualTo(id);
			id++;
		}
	}

	@Test
	public void testWithExtDirectStoreReadRequestMultipeGroups() throws IOException {
		ExtDirectStoreReadRequest storeRead = new ExtDirectStoreReadRequest();
		storeRead.setQuery("");
		List<GroupInfo> groups = new ArrayList<GroupInfo>();
		groups.add(new GroupInfo("id", SortDirection.ASCENDING));
		storeRead.setGroups(groups);
		storeRead.setLimit(10);
		storeRead.setStart(10);
		ExtDirectStoreResponse<Row> storeResponse = executeWithExtDirectStoreReadRequest(storeRead);

		assertThat(storeResponse.getTotal()).isEqualTo(Integer.valueOf(100));
		assertThat(storeResponse.getRecords()).hasSize(10);
		int id = 10;
		for (Row row : storeResponse.getRecords()) {
			assertThat(row.getId()).isEqualTo(id);
			id++;
		}

		storeRead = new ExtDirectStoreReadRequest();
		storeRead.setQuery("");
		groups = new ArrayList<GroupInfo>();
		groups.add(new GroupInfo("id", SortDirection.DESCENDING));
		storeRead.setGroups(groups);
		storeRead.setLimit(10);
		storeRead.setStart(20);
		storeResponse = executeWithExtDirectStoreReadRequest(storeRead);

		assertThat(storeResponse.getTotal()).isEqualTo(Integer.valueOf(100));
		assertThat(storeResponse.getRecords()).hasSize(10);
		id = 79;
		for (Row row : storeResponse.getRecords()) {
			assertThat(row.getId()).isEqualTo(id);
			id--;
		}
	}

	@Test
	public void testWithExtDirectStoreReadRequestMultipleSorters() throws IOException {
		ExtDirectStoreReadRequest storeRead = new ExtDirectStoreReadRequest();
		storeRead.setQuery("");

		List<SortInfo> sorters = new ArrayList<SortInfo>();
		sorters.add(new SortInfo("id", SortDirection.ASCENDING));
		storeRead.setSorters(sorters);

		storeRead.setLimit(10);
		storeRead.setStart(10);
		storeRead.setPage(2);
		ExtDirectStoreResponse<Row> storeResponse = executeWithExtDirectStoreReadRequest(storeRead);

		assertThat(storeResponse.getTotal()).isEqualTo(Integer.valueOf(100));
		assertThat(storeResponse.getRecords()).hasSize(10);
		int id = 10;
		for (Row row : storeResponse.getRecords()) {
			assertThat(row.getId()).isEqualTo(id);
			id++;
		}

		storeRead = new ExtDirectStoreReadRequest();
		storeRead.setQuery("");
		sorters = new ArrayList<SortInfo>();
		sorters.add(new SortInfo("id", SortDirection.DESCENDING));
		storeRead.setSorters(sorters);
		storeRead.setLimit(10);
		storeRead.setStart(20);
		storeRead.setPage(3);
		storeResponse = executeWithExtDirectStoreReadRequest(storeRead);

		assertThat(storeResponse.getTotal()).isEqualTo(Integer.valueOf(100));
		assertThat(storeResponse.getRecords()).hasSize(10);
		id = 79;
		for (Row row : storeResponse.getRecords()) {
			assertThat(row.getId()).isEqualTo(id);
			id--;
		}

		storeRead = new ExtDirectStoreReadRequest();
		storeRead.setQuery("");
		sorters = new ArrayList<SortInfo>();
		sorters.add(new SortInfo("id", SortDirection.ASCENDING));
		storeRead.setSorters(sorters);
		storeRead.setLimit(10);
		storeRead.setStart(10);
		storeRead.setPage(2);
		storeResponse = executeWithExtDirectStoreReadRequest(storeRead);

		assertThat(storeResponse.getTotal()).isEqualTo(Integer.valueOf(100));
		assertThat(storeResponse.getRecords()).hasSize(10);
		id = 10;
		for (Row row : storeResponse.getRecords()) {
			assertThat(row.getId()).isEqualTo(id);
			id++;
		}

		storeRead = new ExtDirectStoreReadRequest();
		storeRead.setQuery("");
		sorters = new ArrayList<SortInfo>();
		sorters.add(new SortInfo("id", SortDirection.DESCENDING));
		storeRead.setSorters(sorters);
		storeRead.setLimit(10);
		storeRead.setStart(20);
		storeRead.setPage(3);
		storeResponse = executeWithExtDirectStoreReadRequest(storeRead);

		assertThat(storeResponse.getTotal()).isEqualTo(Integer.valueOf(100));
		assertThat(storeResponse.getRecords()).hasSize(10);
		id = 79;
		for (Row row : storeResponse.getRecords()) {
			assertThat(row.getId()).isEqualTo(id);
			id--;
		}

		storeRead = new ExtDirectStoreReadRequest();
		storeRead.setQuery("");
		sorters = new ArrayList<SortInfo>();
		sorters.add(new SortInfo("id", SortDirection.ASCENDING));
		storeRead.setSorters(sorters);
		storeRead.setPage(1);
		storeRead.setStart(0);
		storeRead.setLimit(10);
		storeResponse = executeWithExtDirectStoreReadRequest(storeRead);

		assertThat(storeResponse.getTotal()).isEqualTo(Integer.valueOf(100));
		assertThat(storeResponse.getRecords()).hasSize(10);
		id = 0;
		for (Row row : storeResponse.getRecords()) {
			assertThat(row.getId()).isEqualTo(id);
			id++;
		}

		storeRead = new ExtDirectStoreReadRequest();
		storeRead.setQuery("");
		sorters = new ArrayList<SortInfo>();
		sorters.add(new SortInfo("id", SortDirection.ASCENDING));
		storeRead.setSorters(sorters);
		storeRead.setPage(2);
		storeRead.setStart(10);
		storeRead.setLimit(10);
		storeResponse = executeWithExtDirectStoreReadRequest(storeRead);

		assertThat(storeResponse.getTotal()).isEqualTo(Integer.valueOf(100));
		assertThat(storeResponse.getRecords()).hasSize(10);
		id = 10;
		for (Row row : storeResponse.getRecords()) {
			assertThat(row.getId()).isEqualTo(id);
			id++;
		}
	}

	private ExtDirectStoreResponse<Row> executeWithExtDirectStoreReadRequest(ExtDirectStoreReadRequest storeRead) throws IOException {
		Map<String, Object> edRequest = ControllerUtil.createRequestJson("remoteProviderStoreRead", "method4", 1, storeRead);
		Map<String, Object> data = (Map<String, Object>) ((List) edRequest.get("data")).get(0);
		List<Map<String, Object>> sorters = (List<Map<String, Object>>) data.get("sorters");

		if (sorters != null && !sorters.isEmpty()) {
			for (Map<String, Object> map : sorters) {
				if ("DESCENDING".equals(map.get("direction"))) {
					map.put("direction", "DESC");
				} else {
					map.put("direction", "ASC");
				}
			}
			data.remove("sorters");
			data.put("sort", sorters);
		}

		List<Map<String, Object>> groups = (List<Map<String, Object>>) data.get("groups");
		if (groups != null && !groups.isEmpty()) {
			for (Map<String, Object> map : groups) {
				if ("DESCENDING".equals(map.get("direction"))) {
					map.put("direction", "DESC");
				} else {
					map.put("direction", "ASC");
				}
			}
			data.remove("groups");
			data.put("group", groups);
		}

		MockHttpServletResponse response = new MockHttpServletResponse();
		MockHttpServletRequest request = new MockHttpServletRequest();
		request.setContent(ControllerUtil.writeAsByte(edRequest));
		controller.router(request, response, Locale.ENGLISH);
		List<ExtDirectResponse> responses = ControllerUtil.readDirectResponses(response.getContentAsByteArray());

		assertThat(responses).hasSize(1);
		ExtDirectResponse resp = responses.get(0);

		assertThat(resp.getAction()).isEqualTo("remoteProviderStoreRead");
		assertThat(resp.getMethod()).isEqualTo("method4");
		assertThat(resp.getType()).isEqualTo("rpc");
		assertThat(resp.getTid()).isEqualTo(1);
		assertThat(resp.getMessage()).isNull();
		assertThat(resp.getWhere()).isNull();
		assertThat(resp.getResult()).isNotNull();

		return ControllerUtil.convertValue(resp.getResult(), new TypeReference<ExtDirectStoreResponse<Row>>() {/*
																												 * nothing
																												 * here
																												 */
		});
	}

	@Test
	public void testWithAdditionalParameters() throws IOException {
		Map<String, Object> readRequest = new HashMap<String, Object>();
		readRequest.put("id", 10);
		readRequest.put("query", "name");

		ExtDirectStoreResponse<Row> storeResponse = (ExtDirectStoreResponse<Row>) ControllerUtil.sendAndReceive(controller,
				"remoteProviderStoreRead", "method5", readRequest, new TypeReference<ExtDirectStoreResponse<Row>>() {
				});

		assertThat(storeResponse.getTotal()).isEqualTo(Integer.valueOf(50));
		assertThat(storeResponse.getRecords()).hasSize(50);
		for (Row row : storeResponse.getRecords()) {
			assertThat(row.getName().startsWith("name")).isTrue();
		}

		readRequest = new HashMap<String, Object>();
		readRequest.put("query", "name");

		storeResponse = (ExtDirectStoreResponse<Row>) ControllerUtil.sendAndReceive(controller, "remoteProviderStoreRead", "method5",
				readRequest, null);
	}

	@Test
	public void testWithAdditionalParametersDefaultValue() throws IOException {
		Map<String, Object> readRequest = new HashMap<String, Object>();
		readRequest.put("query", "firstname");

		ExtDirectStoreResponse<Row> storeResponse = (ExtDirectStoreResponse<Row>) ControllerUtil.sendAndReceive(controller,
				"remoteProviderStoreRead", "method6", readRequest, new TypeReference<ExtDirectStoreResponse<Row>>() {
				});

		assertThat(storeResponse.getTotal()).isEqualTo(Integer.valueOf(50));
		assertThat(storeResponse.getRecords()).hasSize(50);
		int i = 1;
		for (Row row : storeResponse.getRecords()) {
			assertThat(row.getName()).isEqualTo("firstname: " + i + ":1;true");
			i += 2;
		}
	}

	@Test
	public void testWithAdditionalParametersOptional() throws IOException {

		List<Row> rows = (List<Row>) ControllerUtil.sendAndReceive(controller, "remoteProviderStoreRead", "method7", null,
				new TypeReference<List<Row>>() {
				});
		assert100Rows(rows, ":null");

		Map<String, Object> readRequest = new HashMap<String, Object>();
		readRequest.put("id", 11);
		readRequest.put("query", "");

		rows = (List<Row>) ControllerUtil.sendAndReceive(controller, "remoteProviderStoreRead", "method7", readRequest,
				new TypeReference<List<Row>>() {
				});
		assert100Rows(rows, ":11");

	}

	@Test
	public void testWithAdditionalParametersAndConversion() throws IOException {
		DateTime today = new DateTime();
		Map<String, Object> readRequest = new HashMap<String, Object>();
		readRequest.put("endDate", ISODateTimeFormat.dateTime().print(today));

		ExtDirectStoreResponse<Row> storeResponse = (ExtDirectStoreResponse<Row>) ControllerUtil.sendAndReceive(controller,
				"remoteProviderStoreRead", "method8", readRequest, new TypeReference<ExtDirectStoreResponse<Row>>() {
				});

		assertThat(storeResponse.getRecords()).hasSize(50);

	}

	@Test
	public void testMetadata() throws IOException {

		Map<String, Object> edRequest = ControllerUtil.createRequestJson("remoteProviderStoreRead", "methodMetadata", 1,
				new HashMap<String, Object>());

		MockHttpServletResponse servletResponse = new MockHttpServletResponse();
		MockHttpServletRequest request = new MockHttpServletRequest();

		request.setContent(ControllerUtil.writeAsByte(edRequest));
		controller.router(request, servletResponse, Locale.ENGLISH);
		List<ExtDirectResponse> responses = ControllerUtil.readDirectResponses(servletResponse.getContentAsByteArray());

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

		assertThat(metadata).includes(entry("root", "records"));
		assertThat(metadata).includes(entry("totalProperty", "total"));
		assertThat(metadata).includes(entry("successProperty", "success"));
		assertThat(metadata).includes(entry("start", 0));
		assertThat(metadata).includes(entry("limit", 50));

		Map<String, String> sortInfo = (Map<String, String>) metadata.get("sortInfo");
		assertThat(sortInfo).hasSize(2);
		assertThat(sortInfo).includes(entry("field", "name"));
		assertThat(sortInfo).includes(entry("direction", "ASC"));

		List<Map<String, Object>> fields = (List<Map<String, Object>>) metadata.get("fields");
		assertThat(fields).hasSize(4);

		Map<String, Object> field1 = fields.get(0);
		assertThat(field1).includes(entry("name", "id"));
		assertThat(field1).includes(entry("type", "int"));
		assertThat(field1).includes(entry("header", "ID"));
		assertThat(field1).includes(entry("width", 20));
		assertThat(field1).includes(entry("sortable", true));
		assertThat(field1).includes(entry("resizable", true));
		assertThat(field1).includes(entry("hideable", false));

		Map<String, Object> field2 = fields.get(1);
		assertThat(field2).includes(entry("name", "name"));
		assertThat(field2).includes(entry("type", "string"));
		assertThat(field2).includes(entry("header", "Name"));
		assertThat(field2).includes(entry("width", 70));
		assertThat(field2).includes(entry("sortable", true));
		assertThat(field2).includes(entry("resizable", true));
		assertThat(field2).includes(entry("hideable", false));

		Map<String, Object> field3 = fields.get(2);
		assertThat(field3).includes(entry("name", "admin"));
		assertThat(field3).includes(entry("type", "boolean"));
		assertThat(field3).includes(entry("header", "Administrator"));
		assertThat(field3).includes(entry("width", 30));
		assertThat(field3).includes(entry("sortable", true));
		assertThat(field3).includes(entry("resizable", true));
		assertThat(field3).includes(entry("hideable", true));

		Map<String, Object> field4 = fields.get(3);
		assertThat(field4).includes(entry("name", "salary"));
		assertThat(field4).includes(entry("type", "float"));
		assertThat(field4).includes(entry("header", "Salary"));
		assertThat(field4).includes(entry("width", 50));
		assertThat(field4).includes(entry("sortable", false));
		assertThat(field4).includes(entry("resizable", true));
		assertThat(field4).includes(entry("hideable", true));

	}

}
