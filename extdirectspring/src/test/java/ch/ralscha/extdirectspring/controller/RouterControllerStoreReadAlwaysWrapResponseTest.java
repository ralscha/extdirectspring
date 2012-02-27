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

import static org.fest.assertions.Assertions.assertThat;

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
@ContextConfiguration(locations = "classpath:/testApplicationContextWrapResponse.xml")
public class RouterControllerStoreReadAlwaysWrapResponseTest {

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
		Map<String, Object> edRequest = ControllerUtil.createRequestJson("remoteProviderStoreRead", "method1", 1, null);
		List<ExtDirectResponse> responses = controller.router(request, response, Locale.ENGLISH, edRequest);

		assertThat(responses).hasSize(1);
		ExtDirectResponse resp = responses.get(0);
		assertThat(resp.getAction()).isEqualTo("remoteProviderStoreRead");
		assertThat(resp.getMethod()).isEqualTo("method1");
		assertThat(resp.getType()).isEqualTo("rpc");
		assertThat(resp.getTid()).isEqualTo(1);
		assertThat(resp.getMessage()).isNull();
		assertThat(resp.getWhere()).isNull();
		assertThat(resp.getResult()).isNotNull();

		ExtDirectStoreResponse storeResponse = (ExtDirectStoreResponse) resp.getResult();
		List<Row> rows = (List<Row>) storeResponse.getRecords();
		assertThat(rows.size()).isEqualTo(100);

	}

	@Test
	public void testNoArgumentsWithRequestParameters() {

		ExtDirectStoreReadRequest storeRead = new ExtDirectStoreReadRequest();
		storeRead.setQuery("ralph");

		Map<String, Object> edRequest = ControllerUtil.createRequestJson("remoteProviderStoreRead", "method1", 1,
				storeRead);
		List<ExtDirectResponse> responses = controller.router(request, response, Locale.ENGLISH, edRequest);

		assertThat(responses).hasSize(1);
		ExtDirectResponse resp = responses.get(0);
		assertThat(resp.getAction()).isEqualTo("remoteProviderStoreRead");
		assertThat(resp.getMethod()).isEqualTo("method1");
		assertThat(resp.getType()).isEqualTo("rpc");
		assertThat(resp.getTid()).isEqualTo(1);
		assertThat(resp.getMessage()).isNull();
		assertThat(resp.getWhere()).isNull();
		assertThat(resp.getResult()).isNotNull();

		ExtDirectStoreResponse storeResponse = (ExtDirectStoreResponse) resp.getResult();
		List<Row> rows = (List<Row>) storeResponse.getRecords();
		assertThat(rows.size()).isEqualTo(100);

	}

	@Test
	public void testSupportedArguments() {

		Map<String, Object> edRequest = ControllerUtil.createRequestJson("remoteProviderStoreRead", "method3", 1, null);
		List<ExtDirectResponse> responses = controller.router(request, response, Locale.ENGLISH, edRequest);

		assertThat(responses).hasSize(1);
		ExtDirectResponse resp = responses.get(0);
		assertThat(resp.getAction()).isEqualTo("remoteProviderStoreRead");
		assertThat(resp.getMethod()).isEqualTo("method3");
		assertThat(resp.getType()).isEqualTo("rpc");
		assertThat(resp.getTid()).isEqualTo(1);
		assertThat(resp.getMessage()).isNull();
		assertThat(resp.getWhere()).isNull();
		assertThat(resp.getResult()).isNotNull();

		ExtDirectStoreResponse storeResponse = (ExtDirectStoreResponse) resp.getResult();
		List<Row> rows = (List<Row>) storeResponse.getRecords();
		assertThat(rows.size()).isEqualTo(100);

	}

	@Test
	public void testWithAdditionalParametersOptional() {

		Map<String, Object> edRequest = ControllerUtil.createRequestJson("remoteProviderStoreRead", "method7", 1, null);
		List<ExtDirectResponse> responses = controller.router(request, response, Locale.ENGLISH, edRequest);

		assertThat(responses).hasSize(1);
		ExtDirectResponse resp = responses.get(0);

		assertThat(resp.getAction()).isEqualTo("remoteProviderStoreRead");
		assertThat(resp.getMethod()).isEqualTo("method7");
		assertThat(resp.getType()).isEqualTo("rpc");
		assertThat(resp.getTid()).isEqualTo(1);
		assertThat(resp.getMessage()).isNull();
		assertThat(resp.getWhere()).isNull();
		assertThat(resp.getResult()).isNotNull();

		ExtDirectStoreResponse storeResponse = (ExtDirectStoreResponse) resp.getResult();
		List<Row> rows = (List<Row>) storeResponse.getRecords();
		assertThat(rows.size()).isEqualTo(100);

		Map<String, Object> readRequest = new HashMap<String, Object>();
		readRequest.put("id", 11);
		readRequest.put("query", "");

		edRequest = ControllerUtil.createRequestJson("remoteProviderStoreRead", "method7", 1, readRequest);
		responses = controller.router(request, response, Locale.ENGLISH, edRequest);

		assertThat(responses).hasSize(1);
		resp = responses.get(0);

		assertThat(resp.getAction()).isEqualTo("remoteProviderStoreRead");
		assertThat(resp.getMethod()).isEqualTo("method7");
		assertThat(resp.getType()).isEqualTo("rpc");
		assertThat(resp.getTid()).isEqualTo(1);
		assertThat(resp.getMessage()).isNull();
		assertThat(resp.getWhere()).isNull();
		assertThat(resp.getResult()).isNotNull();

		storeResponse = (ExtDirectStoreResponse) resp.getResult();
		rows = (List<Row>) storeResponse.getRecords();
		assertThat(rows.size()).isEqualTo(100);

	}

}
