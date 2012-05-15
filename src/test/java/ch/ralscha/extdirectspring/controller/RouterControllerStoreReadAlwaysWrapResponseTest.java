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

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.codehaus.jackson.type.TypeReference;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

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

	@Test
	public void testNoArgumentsNoRequestParameters() throws IOException {
		ExtDirectStoreResponse<Row> rows = (ExtDirectStoreResponse<Row>) ControllerUtil
				.sendAndReceive(controller, "remoteProviderStoreRead",
						"method1", null,
						new TypeReference<ExtDirectStoreResponse<Row>>() {/*
																		 * nothing
																		 * here
																		 */
						});
		RouterControllerStoreReadTest.assert100Rows(
				new ArrayList<Row>(rows.getRecords()), "");
	}

	@Test
	public void testNoArgumentsWithRequestParameters() throws IOException {

		ExtDirectStoreReadRequest storeRead = new ExtDirectStoreReadRequest();
		storeRead.setQuery("ralph");

		ExtDirectStoreResponse<Row> rows = (ExtDirectStoreResponse<Row>) ControllerUtil
				.sendAndReceive(controller, "remoteProviderStoreRead",
						"method1", storeRead,
						new TypeReference<ExtDirectStoreResponse<Row>>() {/*
																		 * nothing
																		 * here
																		 */
						});
		RouterControllerStoreReadTest.assert100Rows(
				new ArrayList<Row>(rows.getRecords()), "");
	}

	@Test
	public void testSupportedArguments() throws IOException {

		ExtDirectStoreResponse<Row> rows = (ExtDirectStoreResponse<Row>) ControllerUtil
				.sendAndReceive(controller, "remoteProviderStoreRead",
						"method3", null,
						new TypeReference<ExtDirectStoreResponse<Row>>() {/*
																		 * nothing
																		 * here
																		 */
						});

		RouterControllerStoreReadTest.assert100Rows(
				new ArrayList<Row>(rows.getRecords()), ":true;true:true;en");

	}

	@Test
	public void testWithAdditionalParametersOptional() throws IOException {
		ExtDirectStoreResponse<Row> rows = (ExtDirectStoreResponse<Row>) ControllerUtil
				.sendAndReceive(controller, "remoteProviderStoreRead",
						"method7", null,
						new TypeReference<ExtDirectStoreResponse<Row>>() {
						});
		RouterControllerStoreReadTest.assert100Rows(
				new ArrayList<Row>(rows.getRecords()), ":null");

		Map<String, Object> readRequest = new HashMap<String, Object>();
		readRequest.put("id", 11);
		readRequest.put("query", "");

		rows = (ExtDirectStoreResponse<Row>) ControllerUtil.sendAndReceive(
				controller, "remoteProviderStoreRead", "method7", readRequest,
				new TypeReference<ExtDirectStoreResponse<Row>>() {
				});
		RouterControllerStoreReadTest.assert100Rows(
				new ArrayList<Row>(rows.getRecords()), ":11");
	}

	@Test
	public void testCreateWithDataSingle() throws IOException {
		ExtDirectStoreResponse<Row> rows = (ExtDirectStoreResponse<Row>) ControllerUtil
				.sendAndReceive(controller, "remoteProviderStoreModifySingle",
						"create1", new Row(10, "Ralph", true, "109.55"),
						new TypeReference<ExtDirectStoreResponse<Row>>() {
						});
		assertThat(rows.getRecords()).hasSize(1);
		assertThat(rows.isSuccess()).isTrue();
		Row row = rows.getRecords().iterator().next();
		assertThat(row.getId()).isEqualTo(10);
		assertThat(row.getName()).isEqualTo("Ralph");
		assertThat(row.getSalary()).isEqualTo(new BigDecimal("109.55"));
	}

}
