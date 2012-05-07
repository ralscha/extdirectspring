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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.codehaus.jackson.type.TypeReference;
import org.joda.time.LocalDate;
import org.joda.time.format.ISODateTimeFormat;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import ch.ralscha.extdirectspring.bean.ExtDirectResponse;
import ch.ralscha.extdirectspring.provider.RemoteProviderTreeLoad.Node;

/**
 * Tests for {@link RouterController}.
 * 
 * @author Ralph Schaer
 */
@SuppressWarnings("all")
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:/testApplicationContext.xml")
public class RouterControllerTreeLoaderTest {

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
	public void testNoAdditionalParameters() throws IOException {

		Map<String, Object> requestParameters = new LinkedHashMap<String, Object>();
		requestParameters.put("node", "root");

		Map<String, Object> edRequest = ControllerUtil.createRequestJson("remoteProviderTreeLoad", "method1", 1,
				requestParameters);

		request.setContent(ControllerUtil.writeAsByte(edRequest));
		controller.router(request, response, Locale.ENGLISH);
		List<ExtDirectResponse> responses = ControllerUtil.readDirectResponses(response.getContentAsByteArray());

		assertThat(responses).hasSize(1);

		ExtDirectResponse resp = responses.get(0);
		assertThat(resp.getAction()).isEqualTo("remoteProviderTreeLoad");
		assertThat(resp.getMethod()).isEqualTo("method1");
		assertThat(resp.getTid()).isEqualTo(1);
		assertThat(resp.getType()).isEqualTo("rpc");
		assertThat(resp.getWhere()).isNull();
		assertThat(resp.getMessage()).isNull();

		assertResult(ControllerUtil.convertValue(resp.getResult(), new TypeReference<List<Node>>() {
		}));

	}

	@Test
	public void testAdditionalParameters() throws IOException {

		Map<String, Object> requestParameters = new LinkedHashMap<String, Object>();
		requestParameters.put("node", "root");
		requestParameters.put("foo", "foo");
		requestParameters.put("today", ISODateTimeFormat.date().print(new LocalDate()));

		Map<String, Object> edRequest = ControllerUtil.createRequestJson("remoteProviderTreeLoad", "method2", 2,
				requestParameters);

		request.setContent(ControllerUtil.writeAsByte(edRequest));
		controller.router(request, response, Locale.ENGLISH);
		List<ExtDirectResponse> responses = ControllerUtil.readDirectResponses(response.getContentAsByteArray());

		assertThat(responses).hasSize(1);

		ExtDirectResponse resp = responses.get(0);
		assertThat(resp.getAction()).isEqualTo("remoteProviderTreeLoad");
		assertThat(resp.getMethod()).isEqualTo("method2");
		assertThat(resp.getTid()).isEqualTo(2);
		assertThat(resp.getType()).isEqualTo("rpc");
		assertThat(resp.getWhere()).isNull();
		assertThat(resp.getMessage()).isNull();

		assertResult(ControllerUtil.convertValue(resp.getResult(), new TypeReference<List<Node>>() {
		}));

	}

	@Test
	public void testSupportedParameters() throws IOException {
		Map<String, Object> requestParameters = new LinkedHashMap<String, Object>();
		requestParameters.put("node", "root");

		Map<String, Object> edRequest = ControllerUtil.createRequestJson("remoteProviderTreeLoad", "method3", 3,
				requestParameters);

		request.setContent(ControllerUtil.writeAsByte(edRequest));
		controller.router(request, response, Locale.ENGLISH);
		List<ExtDirectResponse> responses = ControllerUtil.readDirectResponses(response.getContentAsByteArray());

		assertThat(responses).hasSize(1);

		ExtDirectResponse resp = responses.get(0);
		assertThat(resp.getAction()).isEqualTo("remoteProviderTreeLoad");
		assertThat(resp.getMethod()).isEqualTo("method3");
		assertThat(resp.getTid()).isEqualTo(3);
		assertThat(resp.getType()).isEqualTo("rpc");
		assertThat(resp.getWhere()).isNull();
		assertThat(resp.getMessage()).isNull();

		assertResult(ControllerUtil.convertValue(resp.getResult(), new TypeReference<List<Node>>() {
		}));
	}

	private void assertResult(List<Node> nodes) {
		assertThat(nodes).hasSize(5);

		for (int i = 1; i <= 5; ++i) {
			Node node = nodes.get(i - 1);
			assertThat(node.id).isEqualTo("n" + i);
			assertThat(node.text).isEqualTo("Node " + i);
			assertThat(node.leaf).isEqualTo(false);
		}

	}

}
