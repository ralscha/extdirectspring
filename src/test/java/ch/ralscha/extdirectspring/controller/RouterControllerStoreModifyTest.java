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
import static org.assertj.core.api.Assertions.extractProperty;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.joda.time.LocalDate;
import org.joda.time.format.ISODateTimeFormat;
import org.junit.Before;
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

import ch.ralscha.extdirectspring.bean.ExtDirectResponse;
import ch.ralscha.extdirectspring.provider.Row;

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration("classpath:/testApplicationContext.xml")
@SuppressWarnings("unchecked")
public class RouterControllerStoreModifyTest {

	@Autowired
	private WebApplicationContext wac;

	private MockMvc mockMvc;

	@Before
	public void setupMockMvc() throws Exception {
		this.mockMvc = MockMvcBuilders.webAppContextSetup(this.wac).build();
	}

	@Test
	public void testCreateNoData() {
		testCreateNoData("remoteProviderStoreModify");
		testCreateNoData("remoteProviderStoreModifyArray");
		testCreateNoData("remoteProviderStoreModifyInterface");
	}

	private void testCreateNoData(String action) {
		Map<String, Object> storeRequest = new LinkedHashMap<String, Object>();
		storeRequest.put("records", new ArrayList<Row>());

		List<Row> rows = (List<Row>) ControllerUtil.sendAndReceive(this.mockMvc, action,
				"create1", new TypeReference<List<Row>>() {/* nothing here */
				}, storeRequest);

		assertThat(rows).isEmpty();
	}

	@Test
	public void testCreateWithData() {
		testCreateWithData("remoteProviderStoreModify");
		testCreateWithData("remoteProviderStoreModifyArray");
		testCreateWithData("remoteProviderStoreModifyInterface");
	}

	private void testCreateWithData(String action) {
		Map<String, Object> storeRequest = new LinkedHashMap<String, Object>();
		List<Row> rowsToUpdate = new ArrayList<Row>();
		rowsToUpdate.add(new Row(10, "Ralph", true, "109.55"));
		rowsToUpdate.add(new Row(23, "John", false, "23.12"));
		storeRequest.put("records", rowsToUpdate);

		List<Row> rows = (List<Row>) ControllerUtil.sendAndReceive(this.mockMvc, action,
				"create1", new TypeReference<List<Row>>() {/* nothing here */
				}, storeRequest);

		assertThat(rows).hasSize(2);

		Collections.sort(rows);
		assertThat(extractProperty("id").from(rows)).containsSequence(10, 23);
		assertThat(extractProperty("name").from(rows)).containsSequence("Ralph", "John");
	}

	@Test
	public void testCreateWithDataSingle() {
		Row row = (Row) ControllerUtil.sendAndReceive(this.mockMvc,
				"remoteProviderStoreModifySingle", "create1", Row.class,
				new Row(10, "Ralph", true, "109.55"));
		assertThat(row.getId()).isEqualTo(10);
	}

	@Test
	public void testCreateWithDataAndSupportedArguments() {
		testCreateWithDataAndSupportedArguments("remoteProviderStoreModify");
		testCreateWithDataAndSupportedArguments("remoteProviderStoreModifyArray");
		testCreateWithDataAndSupportedArguments("remoteProviderStoreModifyInterface");
	}

	private void testCreateWithDataAndSupportedArguments(String action) {
		Map<String, Object> storeRequest = new LinkedHashMap<String, Object>();
		List<Row> rowsToUpdate = new ArrayList<Row>();
		rowsToUpdate.add(new Row(10, "Ralph", false, "109.55"));

		storeRequest.put("records", rowsToUpdate);

		List<Row> rows = (List<Row>) ControllerUtil.sendAndReceive(this.mockMvc, action,
				"create2", new TypeReference<List<Row>>() {/* nothing here */
				}, storeRequest);

		assertThat(rows).hasSize(1);
		assertThat(extractProperty("id").from(rows)).containsExactly(10);
	}

	@Test
	public void testCreateWithDataAndSupportedArgumentsSingle() {
		Row row = (Row) ControllerUtil.sendAndReceive(this.mockMvc,
				"remoteProviderStoreModifySingle", "create2", Row.class,
				new Row(10, "Ralph", false, "109.55"));
		assertThat(row.getId()).isEqualTo(10);
	}

	@Test
	public void testUpdate() throws Exception {
		testUpdate("remoteProviderStoreModify");
		testUpdate("remoteProviderStoreModifyArray");
		testUpdate("remoteProviderStoreModifyInterface");
	}

	private void testUpdate(String action) throws Exception {
		Map<String, Object> storeRequest = new LinkedHashMap<String, Object>();
		List<Row> rowsToUpdate = new ArrayList<Row>();
		rowsToUpdate.add(new Row(10, "Ralph", true, "109.55"));
		storeRequest.put("records", rowsToUpdate);
		executeUpdate(action, storeRequest, "update1");
	}

	@Test
	public void testUpdateWithRequestParam() throws Exception {
		testUpdateWithRequestParam("remoteProviderStoreModify");
		testUpdateWithRequestParam("remoteProviderStoreModifyArray");
		testUpdateWithRequestParam("remoteProviderStoreModifyInterface");
	}

	private void testUpdateWithRequestParam(String action) throws Exception {
		Map<String, Object> storeRequest = new LinkedHashMap<String, Object>();
		List<Row> rowsToUpdate = new ArrayList<Row>();
		rowsToUpdate.add(new Row(10, "Ralph", true, "109.55"));
		storeRequest.put("id", 10);
		storeRequest.put("records", rowsToUpdate);
		executeUpdate(action, storeRequest, "update2");
	}

	@Test
	public void testUpdateWithRequestParamDefaultValue() throws Exception {
		testUpdateWithRequestParamDefaultValue("remoteProviderStoreModify");
		testUpdateWithRequestParamDefaultValue("remoteProviderStoreModifyArray");
		testUpdateWithRequestParamDefaultValue("remoteProviderStoreModifyInterface");
	}

	private void testUpdateWithRequestParamDefaultValue(String action) throws Exception {
		Map<String, Object> storeRequest = new LinkedHashMap<String, Object>();
		List<Row> rowsToUpdate = new ArrayList<Row>();
		rowsToUpdate.add(new Row(10, "Ralph", true, "109.55"));
		storeRequest.put("records", rowsToUpdate);
		executeUpdate(action, storeRequest, "update3");
	}

	@Test
	public void testUpdateWithRequestParamOptional() throws Exception {
		testUpdateWithRequestParamOptional("remoteProviderStoreModify");
		testUpdateWithRequestParamOptional("remoteProviderStoreModifyArray");
		testUpdateWithRequestParamOptional("remoteProviderStoreModifyInterface");
	}

	private void testUpdateWithRequestParamOptional(String action) throws Exception {
		Map<String, Object> storeRequest = new LinkedHashMap<String, Object>();
		List<Row> rowsToUpdate = new ArrayList<Row>();
		rowsToUpdate.add(new Row(10, "Ralph", true, "109.55"));
		storeRequest.put("records", rowsToUpdate);
		executeUpdate(action, storeRequest, "update4");

		storeRequest = new LinkedHashMap<String, Object>();
		rowsToUpdate = new ArrayList<Row>();
		rowsToUpdate.add(new Row(10, "Ralph", true, "109.55"));
		storeRequest.put("records", rowsToUpdate);
		storeRequest.put("id", 11);
		storeRequest.put("yesterday",
				ISODateTimeFormat.date().print(new LocalDate().minusDays(1)));
		executeUpdate(action, storeRequest, "update4");
	}

	@Test
	public void testUpdateSingle() throws Exception {
		Map<String, Object> storeRequest = new LinkedHashMap<String, Object>();
		Row row = new Row(10, "Ralph", true, "109.55");
		storeRequest.putAll(ControllerUtil.convertValue(row, Map.class));
		executeUpdate("remoteProviderStoreModifySingle", storeRequest, "update1");
	}

	@Test
	public void testUpdateWithRequestParamSingle() throws Exception {
		Map<String, Object> storeRequest = new LinkedHashMap<String, Object>();
		Row row = new Row(10, "Ralph", true, "109.55");
		storeRequest.put("aParam", 42);
		storeRequest.putAll(ControllerUtil.convertValue(row, Map.class));
		executeUpdate("remoteProviderStoreModifySingle", storeRequest, "update2");
	}

	@Test
	public void testUpdateWithRequestParamDefaultValueSingle() throws Exception {
		Map<String, Object> storeRequest = new LinkedHashMap<String, Object>();
		Row row = new Row(10, "Ralph", true, "109.55");
		storeRequest.putAll(ControllerUtil.convertValue(row, Map.class));
		executeUpdate("remoteProviderStoreModifySingle", storeRequest, "update3");
	}

	@Test
	public void testUpdateWithRequestParamOptionalSingle() throws Exception {
		Map<String, Object> storeRequest = new LinkedHashMap<String, Object>();
		Row row = new Row(10, "Ralph", true, "109.55");
		storeRequest.putAll(ControllerUtil.convertValue(row, Map.class));
		executeUpdate("remoteProviderStoreModifySingle", storeRequest, "update4");

		storeRequest = new LinkedHashMap<String, Object>();
		storeRequest.putAll(ControllerUtil.convertValue(row, Map.class));
		storeRequest.put("aParam", 11);
		storeRequest.put("yesterday",
				ISODateTimeFormat.date().print(new LocalDate().minusDays(1)));
		executeUpdate("remoteProviderStoreModifySingle", storeRequest, "update4");
	}

	private void executeUpdate(String action, Map<String, Object> storeRequest,
			String method) throws Exception {
		String edRequest = ControllerUtil.createEdsRequest(action, method, 1,
				storeRequest);

		MvcResult mvcResult = ControllerUtil.performRouterRequest(this.mockMvc,
				edRequest);
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
	public void testDestroy() {
		Map<String, Object> storeRequest = new LinkedHashMap<String, Object>();
		List<Integer> rowsToUpdate = new ArrayList<Integer>();
		rowsToUpdate.add(10);
		storeRequest.put("records", rowsToUpdate);

		List<Integer> rows = (List<Integer>) ControllerUtil.sendAndReceive(this.mockMvc,
				"remoteProviderStoreModify", "destroy",
				new TypeReference<List<Integer>>() {/* nothing_here */
				}, storeRequest);

		assertThat(rows).hasSize(1);
		assertThat(rows.get(0)).isEqualTo(Integer.valueOf(10));
	}

	@Test
	public void testDestroyArray() {
		Map<String, Object> storeRequest = new LinkedHashMap<String, Object>();
		List<Integer> rowsToUpdate = new ArrayList<Integer>();
		rowsToUpdate.add(10);
		storeRequest.put("records", rowsToUpdate);

		List<Integer> rows = (List<Integer>) ControllerUtil.sendAndReceive(this.mockMvc,
				"remoteProviderStoreModifyArray", "destroy",
				new TypeReference<List<Integer>>() {/* nothing_here */
				}, storeRequest);

		assertThat(rows).hasSize(1);
		assertThat(rows.get(0)).isEqualTo(Integer.valueOf(10));
	}

	@Test
	public void testDestroySingle() {
		ControllerUtil.sendAndReceive(this.mockMvc, "remoteProviderStoreModifySingle",
				"destroy", 1, new Object[] { 1 });
	}

}
