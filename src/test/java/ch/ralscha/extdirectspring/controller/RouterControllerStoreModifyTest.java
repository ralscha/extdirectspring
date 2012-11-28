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

import static org.fest.assertions.api.Assertions.assertThat;
import static org.fest.assertions.api.Assertions.extractProperty;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.joda.time.LocalDate;
import org.joda.time.format.ISODateTimeFormat;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import ch.ralscha.extdirectspring.bean.ExtDirectResponse;
import ch.ralscha.extdirectspring.provider.Row;

import com.fasterxml.jackson.core.type.TypeReference;

/**
 * Tests for {@link RouterController}.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:/testApplicationContext.xml")
public class RouterControllerStoreModifyTest {

	@Autowired
	private RouterController controller;

	@Test
	public void testCreateNoData() {
		testCreateNoData("remoteProviderStoreModify");
		testCreateNoData("remoteProviderStoreModifyArray");
		testCreateNoData("remoteProviderStoreModifyInterface");
	}

	private void testCreateNoData(String action) {
		Map<String, Object> storeRequest = new LinkedHashMap<String, Object>();
		storeRequest.put("records", new ArrayList<Row>());

		List<Row> rows = (List<Row>) ControllerUtil.sendAndReceive(controller, action, "create1", storeRequest,
				new TypeReference<List<Row>>() {/* nothing here */
				});

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

		List<Row> rows = (List<Row>) ControllerUtil.sendAndReceive(controller, action, "create1", storeRequest,
				new TypeReference<List<Row>>() {/* nothing here */
				});

		assertThat(rows).hasSize(2);

		Collections.sort(rows);
		assertThat(extractProperty("id").from(rows)).containsSequence(10, 23);
		assertThat(extractProperty("name").from(rows)).containsSequence("Ralph", "John");
	}

	@Test
	public void testCreateWithDataSingle() {
		Row row = (Row) ControllerUtil.sendAndReceive(controller, "remoteProviderStoreModifySingle", "create1",
				new Row(10, "Ralph", true, "109.55"), Row.class);
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

		List<Row> rows = (List<Row>) ControllerUtil.sendAndReceive(controller, action, "create2", storeRequest,
				new TypeReference<List<Row>>() {/* nothing here */
				});

		assertThat(rows).hasSize(1);
		assertThat(extractProperty("id").from(rows)).containsExactly(10);
	}

	@Test
	public void testCreateWithDataAndSupportedArgumentsSingle() {
		Row row = (Row) ControllerUtil.sendAndReceive(controller, "remoteProviderStoreModifySingle", "create2",
				new Row(10, "Ralph", false, "109.55"), Row.class);
		assertThat(row.getId()).isEqualTo(10);
	}

	@Test
	public void testUpdate() throws IOException {
		testUpdate("remoteProviderStoreModify");
		testUpdate("remoteProviderStoreModifyArray");
		testUpdate("remoteProviderStoreModifyInterface");
	}

	private void testUpdate(String action) throws IOException {
		Map<String, Object> storeRequest = new LinkedHashMap<String, Object>();
		List<Row> rowsToUpdate = new ArrayList<Row>();
		rowsToUpdate.add(new Row(10, "Ralph", true, "109.55"));
		storeRequest.put("records", rowsToUpdate);
		executeUpdate(action, storeRequest, "update1");
	}

	@Test
	public void testUpdateWithRequestParam() throws IOException {
		testUpdateWithRequestParam("remoteProviderStoreModify");
		testUpdateWithRequestParam("remoteProviderStoreModifyArray");
		testUpdateWithRequestParam("remoteProviderStoreModifyInterface");
	}

	private void testUpdateWithRequestParam(String action) throws IOException {
		Map<String, Object> storeRequest = new LinkedHashMap<String, Object>();
		List<Row> rowsToUpdate = new ArrayList<Row>();
		rowsToUpdate.add(new Row(10, "Ralph", true, "109.55"));
		storeRequest.put("id", 10);
		storeRequest.put("records", rowsToUpdate);
		executeUpdate(action, storeRequest, "update2");
	}

	@Test
	public void testUpdateWithRequestParamDefaultValue() throws IOException {
		testUpdateWithRequestParamDefaultValue("remoteProviderStoreModify");
		testUpdateWithRequestParamDefaultValue("remoteProviderStoreModifyArray");
		testUpdateWithRequestParamDefaultValue("remoteProviderStoreModifyInterface");
	}

	private void testUpdateWithRequestParamDefaultValue(String action) throws IOException {
		Map<String, Object> storeRequest = new LinkedHashMap<String, Object>();
		List<Row> rowsToUpdate = new ArrayList<Row>();
		rowsToUpdate.add(new Row(10, "Ralph", true, "109.55"));
		storeRequest.put("records", rowsToUpdate);
		executeUpdate(action, storeRequest, "update3");
	}

	@Test
	public void testUpdateWithRequestParamOptional() throws IOException {
		testUpdateWithRequestParamOptional("remoteProviderStoreModify");
		testUpdateWithRequestParamOptional("remoteProviderStoreModifyArray");
		testUpdateWithRequestParamOptional("remoteProviderStoreModifyInterface");
	}

	private void testUpdateWithRequestParamOptional(String action) throws IOException {
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
		storeRequest.put("yesterday", ISODateTimeFormat.date().print(new LocalDate().minusDays(1)));
		executeUpdate(action, storeRequest, "update4");
	}

	@Test
	public void testUpdateSingle() throws IOException {
		Map<String, Object> storeRequest = new LinkedHashMap<String, Object>();
		Row row = new Row(10, "Ralph", true, "109.55");
		storeRequest.putAll(ControllerUtil.convertValue(row, Map.class));
		executeUpdate("remoteProviderStoreModifySingle", storeRequest, "update1");
	}

	@Test
	public void testUpdateWithRequestParamSingle() throws IOException {
		Map<String, Object> storeRequest = new LinkedHashMap<String, Object>();
		Row row = new Row(10, "Ralph", true, "109.55");
		storeRequest.put("aParam", 42);
		storeRequest.putAll(ControllerUtil.convertValue(row, Map.class));
		executeUpdate("remoteProviderStoreModifySingle", storeRequest, "update2");
	}

	@Test
	public void testUpdateWithRequestParamDefaultValueSingle() throws IOException {
		Map<String, Object> storeRequest = new LinkedHashMap<String, Object>();
		Row row = new Row(10, "Ralph", true, "109.55");
		storeRequest.putAll(ControllerUtil.convertValue(row, Map.class));
		executeUpdate("remoteProviderStoreModifySingle", storeRequest, "update3");
	}

	@Test
	public void testUpdateWithRequestParamOptionalSingle() throws IOException {
		Map<String, Object> storeRequest = new LinkedHashMap<String, Object>();
		Row row = new Row(10, "Ralph", true, "109.55");
		storeRequest.putAll(ControllerUtil.convertValue(row, Map.class));
		executeUpdate("remoteProviderStoreModifySingle", storeRequest, "update4");

		storeRequest = new LinkedHashMap<String, Object>();
		storeRequest.putAll(ControllerUtil.convertValue(row, Map.class));
		storeRequest.put("aParam", 11);
		storeRequest.put("yesterday", ISODateTimeFormat.date().print(new LocalDate().minusDays(1)));
		executeUpdate("remoteProviderStoreModifySingle", storeRequest, "update4");
	}

	private void executeUpdate(String action, Map<String, Object> storeRequest, String method) throws IOException {
		Map<String, Object> edRequest = ControllerUtil.createRequestJson(action, method, 1, storeRequest);

		MockHttpServletRequest request = new MockHttpServletRequest();
		MockHttpServletResponse response = new MockHttpServletResponse();

		request.setContent(ControllerUtil.writeAsByte(edRequest));
		controller.router(request, response, Locale.ENGLISH);
		List<ExtDirectResponse> responses = ControllerUtil.readDirectResponses(response.getContentAsByteArray());

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
			List<Row> storeResponse = ControllerUtil.convertValue(result, new TypeReference<List<Row>>() {/*
																										 * nothing
																										 * here
																										 */
			});
			assertThat(storeResponse).hasSize(1);
			assertThat(storeResponse.get(0).getId()).isEqualTo(10);
			assertThat(storeResponse.get(0).getName()).isEqualTo("Ralph");
			assertThat(storeResponse.get(0).isAdmin()).isTrue();
			assertThat(storeResponse.get(0).getSalary()).isEqualTo(new BigDecimal("109.55"));
		} else {
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

		List<Integer> rows = (List<Integer>) ControllerUtil.sendAndReceive(controller, "remoteProviderStoreModify",
				"destroy", storeRequest, new TypeReference<List<Integer>>() {/*
																			 * nothing
																			 * here
																			 */
				});

		assertThat(rows).hasSize(1);
		assertThat(rows.get(0)).isEqualTo(Integer.valueOf(10));
	}

	@Test
	public void testDestroyArray() {
		Map<String, Object> storeRequest = new LinkedHashMap<String, Object>();
		List<Integer> rowsToUpdate = new ArrayList<Integer>();
		rowsToUpdate.add(10);
		storeRequest.put("records", rowsToUpdate);

		List<Integer> rows = (List<Integer>) ControllerUtil.sendAndReceive(controller,
				"remoteProviderStoreModifyArray", "destroy", storeRequest, new TypeReference<List<Integer>>() {/*
																												 * nothing
																												 * here
																												 */
				});

		assertThat(rows).hasSize(1);
		assertThat(rows.get(0)).isEqualTo(Integer.valueOf(10));
	}

	@Test
	public void testDestroySingle() {
		ControllerUtil.sendAndReceive(controller, "remoteProviderStoreModifySingle", "destroy", new Object[] { 1 }, 1);
	}

}
