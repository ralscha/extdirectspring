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
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.codehaus.jackson.type.TypeReference;
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

/**
 * Tests for {@link RouterController}.
 * 
 * @author Ralph Schaer
 */
@SuppressWarnings("all")
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:/testApplicationContext.xml")
public class RouterControllerInterfaceTest {

	@Autowired
	private RouterController controller;

	private MockHttpServletResponse response;
	private MockHttpServletRequest request;

	@Before
	public void beforeTest() {
		Locale.setDefault(Locale.US);
		response = new MockHttpServletResponse();
		request = new MockHttpServletRequest();
	}

	@Test
	public void testNoParameters() throws IOException {
		Map<String, Object> edRequest = ControllerUtil.createRequestJson("remoteProviderImplementation", "method2", 1,
				null);

		request.setContent(ControllerUtil.writeAsByte(edRequest));
		controller.router(request, response, Locale.ENGLISH);
		List<ExtDirectResponse> responses = ControllerUtil.readDirectResponses(response.getContentAsByteArray());

		ExtDirectResponse resp = responses.get(0);
		assertThat(responses).hasSize(1);
		assertThat(resp.getAction()).isEqualTo("remoteProviderImplementation");
		assertThat(resp.getMethod()).isEqualTo("method2");
		assertThat(resp.getTid()).isEqualTo(1);
		assertThat(resp.getType()).isEqualTo("rpc");
		assertThat(resp.getWhere()).isNull();
		assertThat(resp.getMessage()).isNull();
		assertThat(resp.getResult()).isEqualTo("method2() called");
	}

	@Test
	public void testNoParameterAnnotation() throws IOException {
		Map<String, Object> edRequest = ControllerUtil.createRequestJson("remoteProviderImplementation", "method3", 1,
				new Object[] { 20, 2.1, "aString" });

		request.setContent(ControllerUtil.writeAsByte(edRequest));
		controller.router(request, response, Locale.ENGLISH);
		List<ExtDirectResponse> responses = ControllerUtil.readDirectResponses(response.getContentAsByteArray());

		ExtDirectResponse resp = responses.get(0);
		assertThat(responses).hasSize(1);
		assertThat(resp.getAction()).isEqualTo("remoteProviderImplementation");
		assertThat(resp.getMethod()).isEqualTo("method3");
		assertThat(resp.getTid()).isEqualTo(1);
		assertThat(resp.getType()).isEqualTo("rpc");
		assertThat(resp.getWhere()).isNull();
		assertThat(resp.getMessage()).isNull();
		assertThat(resp.getResult()).isEqualTo("method3() called-20-2.1-aString");
	}

	@Test
	public void testWithRequestParamAnnotation() throws IOException {

		Map<String, Object> readRequest = new HashMap<String, Object>();
		readRequest.put("lastName", "Smith");
		readRequest.put("active", true);
		Map<String, Object> edRequest = ControllerUtil.createRequestJson("remoteProviderImplementation", "storeRead",
				1, readRequest);

		request.setContent(ControllerUtil.writeAsByte(edRequest));
		controller.router(request, response, Locale.ENGLISH);
		List<ExtDirectResponse> responses = ControllerUtil.readDirectResponses(response.getContentAsByteArray());

		assertThat(responses).hasSize(1);
		ExtDirectResponse resp = responses.get(0);

		assertThat(resp.getAction()).isEqualTo("remoteProviderImplementation");
		assertThat(resp.getMethod()).isEqualTo("storeRead");
		assertThat(resp.getType()).isEqualTo("rpc");
		assertThat(resp.getTid()).isEqualTo(1);
		assertThat(resp.getMessage()).isNull();
		assertThat(resp.getWhere()).isNull();
		assertThat(resp.getResult()).isNotNull();

		List<Row> rows = ControllerUtil.convertValue(resp.getResult(), new TypeReference<List<Row>>() {
		});
		assertThat(rows).hasSize(1);
		Row theRow = rows.get(0);
		assertThat(theRow.getId()).isEqualTo(1);
		assertThat(theRow.getName()).isEqualTo("Smith");
		assertThat(theRow.getSalary()).isEqualTo(new BigDecimal("40"));
	}
}
