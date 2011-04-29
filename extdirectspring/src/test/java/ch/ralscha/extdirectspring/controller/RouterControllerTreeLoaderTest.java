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

import static junit.framework.Assert.assertNull;
import static org.junit.Assert.assertEquals;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.inject.Inject;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
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

	@Inject
	private RouterController controller;

	private MockHttpServletResponse response;
	private MockHttpServletRequest request;

	@Before
	public void beforeTest() {
		response = new MockHttpServletResponse();
		request = new MockHttpServletRequest();
	}

	@Test
	public void testNoAdditionalParameters() {
		
		Map<String, Object> requestParameters = new LinkedHashMap<String, Object>();
		requestParameters.put("node", "root");
		
		Map<String,Object> edRequest = ControllerUtil.createRequestJson("remoteProviderTreeLoad", "method1", 1, requestParameters);
		List<ExtDirectResponse> responses = controller.router(request, response, Locale.ENGLISH, edRequest);

		assertEquals(1, responses.size());

		ExtDirectResponse resp = responses.get(0);
		assertEquals("remoteProviderTreeLoad", resp.getAction());
		assertEquals("method1", resp.getMethod());
		assertEquals(1, resp.getTid());
		assertEquals("rpc", resp.getType());
		assertNull(resp.getWhere());
		assertNull(resp.getMessage());
		
		assertResult((List<Node>)resp.getResult());

	}

	@Test
	public void testAdditionalParameters() {
		
		Map<String, Object> requestParameters = new LinkedHashMap<String, Object>();
		requestParameters.put("node", "root");
		requestParameters.put("foo", "foo");
		
		Map<String,Object> edRequest = ControllerUtil.createRequestJson("remoteProviderTreeLoad", "method2", 2, requestParameters);
		List<ExtDirectResponse> responses = controller.router(request, response, Locale.ENGLISH, edRequest);

		assertEquals(1, responses.size());

		ExtDirectResponse resp = responses.get(0);
		assertEquals("remoteProviderTreeLoad", resp.getAction());
		assertEquals("method2", resp.getMethod());
		assertEquals(2, resp.getTid());
		assertEquals("rpc", resp.getType());
		assertNull(resp.getWhere());
		assertNull(resp.getMessage());
		
		assertResult((List<Node>)resp.getResult());		
		
	}

	@Test
	public void testSupportedParameters() {
		Map<String, Object> requestParameters = new LinkedHashMap<String, Object>();
		requestParameters.put("node", "root");
		
		Map<String,Object> edRequest = ControllerUtil.createRequestJson("remoteProviderTreeLoad", "method3", 3, requestParameters);
		List<ExtDirectResponse> responses = controller.router(request, response, Locale.ENGLISH, edRequest);

		assertEquals(1, responses.size());

		ExtDirectResponse resp = responses.get(0);
		assertEquals("remoteProviderTreeLoad", resp.getAction());
		assertEquals("method3", resp.getMethod());
		assertEquals(3, resp.getTid());
		assertEquals("rpc", resp.getType());
		assertNull(resp.getWhere());
		assertNull(resp.getMessage());
		
		assertResult((List<Node>)resp.getResult());			
	}

	
	private void assertResult(List<Node> nodes) {
		assertEquals(5, nodes.size());
		
		for (int i = 1; i <= 5; ++i) {
			Node node = nodes.get(i-1);
			assertEquals("n" + i, node.id);
			assertEquals("Node " + i, node.text);
			assertEquals(false, node.leaf);
		}
		
	}
	
}
