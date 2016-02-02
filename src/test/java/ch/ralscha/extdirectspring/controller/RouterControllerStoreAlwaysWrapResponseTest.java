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
import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.fasterxml.jackson.core.type.TypeReference;

import ch.ralscha.extdirectspring.bean.ExtDirectStoreReadRequest;
import ch.ralscha.extdirectspring.bean.ExtDirectStoreResult;
import ch.ralscha.extdirectspring.provider.Row;

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration("classpath:/testApplicationContextWrapResponse.xml")
@SuppressWarnings("unchecked")
public class RouterControllerStoreAlwaysWrapResponseTest {

	@Autowired
	private WebApplicationContext wac;

	private MockMvc mockMvc;

	@Before
	public void setupMockMvc() throws Exception {
		this.mockMvc = MockMvcBuilders.webAppContextSetup(this.wac).build();
	}

	@Test
	public void testNoArgumentsNoRequestParameters() {
		ExtDirectStoreResult<Row> rows = (ExtDirectStoreResult<Row>) ControllerUtil
				.sendAndReceive(this.mockMvc, "remoteProviderStoreRead", "method1",
						new TypeReference<ExtDirectStoreResult<Row>>() {/* nothing_here */
						});
		RouterControllerStoreTest.assert100Rows(new ArrayList<Row>(rows.getRecords()),
				"");
	}

	@Test
	public void testNoArgumentsWithRequestParameters() {

		ExtDirectStoreReadRequest storeRead = new ExtDirectStoreReadRequest();
		storeRead.setQuery("ralph");

		ExtDirectStoreResult<Row> rows = (ExtDirectStoreResult<Row>) ControllerUtil
				.sendAndReceive(this.mockMvc, "remoteProviderStoreRead", "method1",
						new TypeReference<ExtDirectStoreResult<Row>>() {/* nothing_here */
						}, storeRead);
		RouterControllerStoreTest.assert100Rows(new ArrayList<Row>(rows.getRecords()),
				"");
	}

	@Test
	public void testSupportedArguments() {

		ExtDirectStoreResult<Row> rows = (ExtDirectStoreResult<Row>) ControllerUtil
				.sendAndReceive(this.mockMvc, "remoteProviderStoreRead", "method3",
						new TypeReference<ExtDirectStoreResult<Row>>() {// nothing
							// here
						});

		RouterControllerStoreTest.assert100Rows(new ArrayList<Row>(rows.getRecords()),
				":true;true:true;en");

	}

	@Test
	public void testWithAdditionalParametersOptional() {
		ExtDirectStoreResult<Row> rows = (ExtDirectStoreResult<Row>) ControllerUtil
				.sendAndReceive(this.mockMvc, "remoteProviderStoreRead", "method7",
						new TypeReference<ExtDirectStoreResult<Row>>() {/* nothing_here */
						});
		RouterControllerStoreTest.assert100Rows(new ArrayList<Row>(rows.getRecords()),
				":null");

		Map<String, Object> readRequest = new HashMap<String, Object>();
		readRequest.put("id", 11);
		readRequest.put("query", "");

		rows = (ExtDirectStoreResult<Row>) ControllerUtil.sendAndReceive(this.mockMvc,
				"remoteProviderStoreRead", "method7",
				new TypeReference<ExtDirectStoreResult<Row>>() {/* nothing_here */
				}, readRequest);
		RouterControllerStoreTest.assert100Rows(new ArrayList<Row>(rows.getRecords()),
				":11");
	}

	@Test
	public void testCreateWithDataSingle() {
		ExtDirectStoreResult<Row> rows = (ExtDirectStoreResult<Row>) ControllerUtil
				.sendAndReceive(this.mockMvc, "remoteProviderStoreModifySingle",
						"create1", new TypeReference<ExtDirectStoreResult<Row>>() {
							/* nothing here */
						}, new Row(10, "Ralph", true, "109.55"));
		assertThat(rows.getRecords()).hasSize(1);
		assertThat(rows.isSuccess()).isTrue();
		Row row = rows.getRecords().iterator().next();
		assertThat(row.getId()).isEqualTo(10);
		assertThat(row.getName()).isEqualTo("Ralph");
		assertThat(row.getSalary()).isEqualTo(new BigDecimal("109.55"));
	}

}
