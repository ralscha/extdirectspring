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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

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

import ch.ralscha.extdirectspring.bean.ExtDirectResponse;
import ch.ralscha.extdirectspring.provider.Row;

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration("classpath:/testApplicationContext.xml")
public class RouterControllerFilterTest {

	@Autowired
	private WebApplicationContext wac;

	private MockMvc mockMvc;

	private static List<String> jsonList;

	@Before
	public void setupMockMvc() throws Exception {
		this.mockMvc = MockMvcBuilders.webAppContextSetup(this.wac).build();
	}

	@BeforeClass
	public static void readJson() throws IOException {
		jsonList = new ArrayList<String>();
		InputStream is = RouterControllerFilterTest.class
				.getResourceAsStream("/filterjson.txt");
		BufferedReader br = new BufferedReader(new InputStreamReader(is));
		String line = null;
		while ((line = br.readLine()) != null) {
			jsonList.add(line);
		}
		br.close();
		is.close();
	}

	@Test
	public void testFilters() throws Exception {

		int index = 1;
		for (String json : jsonList) {
			MvcResult result = ControllerUtil.performRouterRequest(this.mockMvc, json);
			List<ExtDirectResponse> responses = ControllerUtil
					.readDirectResponses(result.getResponse().getContentAsByteArray());

			assertThat(responses).hasSize(1);
			ExtDirectResponse resp = responses.get(0);
			assertThat(resp.getAction()).isEqualTo("remoteProviderStoreRead");
			assertThat(resp.getMethod()).isEqualTo("methodFilter");
			assertThat(resp.getType()).isEqualTo("rpc");
			assertThat(resp.getTid()).isEqualTo(index);
			assertThat(resp.getMessage()).isNull();
			assertThat(resp.getWhere()).isNull();
			assertThat(resp.getResult()).isNotNull();

			List<Row> rows = ControllerUtil.convertValue(resp.getResult(),
					new TypeReference<List<Row>>() {
						// nothing here
					});

			assertThat(rows).hasSize(1);
			assertThat(rows.get(0).getId()).isEqualTo(index);

			index++;
		}

		assertThat(index).isEqualTo(34);

	}

}
