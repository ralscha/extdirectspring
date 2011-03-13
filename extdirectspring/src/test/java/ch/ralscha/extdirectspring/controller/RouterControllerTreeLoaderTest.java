/**
 * Copyright 2010 Ralph Schaer <ralphschaer@gmail.com>
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

import java.util.ArrayList;
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
import ch.ralscha.extdirectspring.provider.Row;
import ch.ralscha.extdirectspring.provider.RemoteProviderTreeLoader.Node;

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
		
		String json = ControllerUtil.createRequestJson("remoteProviderTreeLoader", "method1", 1, requestParameters);
		List<ExtDirectResponse> responses = controller.router(request, response, Locale.ENGLISH, json);

		assertEquals(1, responses.size());

		ExtDirectResponse resp = responses.get(0);
		assertEquals("remoteProviderTreeLoader", resp.getAction());
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
		
		String json = ControllerUtil.createRequestJson("remoteProviderTreeLoader", "method2", 2, requestParameters);
		List<ExtDirectResponse> responses = controller.router(request, response, Locale.ENGLISH, json);

		assertEquals(1, responses.size());

		ExtDirectResponse resp = responses.get(0);
		assertEquals("remoteProviderTreeLoader", resp.getAction());
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
		
		String json = ControllerUtil.createRequestJson("remoteProviderTreeLoader", "method3", 3, requestParameters);
		List<ExtDirectResponse> responses = controller.router(request, response, Locale.ENGLISH, json);

		assertEquals(1, responses.size());

		ExtDirectResponse resp = responses.get(0);
		assertEquals("remoteProviderTreeLoader", resp.getAction());
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
	
	//	
	//	@Test
	//	public void testNoParameters() {
	//		String json = ControllerUtil.createRequestJson("remoteProviderSimple", "method1", 1, null);
	//		List<ExtDirectResponse> responses = controller.router(request, response, Locale.ENGLISH, json);
	//
	//		assertEquals(1, responses.size());
	//		checkNoParametersResponse(responses.get(0), 1);
	//	}
	//
	//	static void checkNoParametersResponse(ExtDirectResponse resp, int tid) {
	//		assertEquals("remoteProviderSimple", resp.getAction());
	//		assertEquals("method1", resp.getMethod());
	//		assertEquals(tid, resp.getTid());
	//		assertEquals("rpc", resp.getType());
	//		assertNull(resp.getWhere());
	//		assertNull(resp.getMessage());
	//		assertEquals("method1() called", resp.getResult());
	//	}
	//
	//	@Test
	//	public void testNoParametersWithRequestParameter() {
	//		String json = ControllerUtil.createRequestJson("remoteProviderSimple", "method1", 1, "requestparameter");
	//		List<ExtDirectResponse> responses = controller.router(request, response, Locale.ENGLISH, json);
	//
	//		assertEquals(1, responses.size());
	//		ExtDirectResponse resp = responses.get(0);
	//		checkNoParametersResponse(resp, 1);
	//	}
	//
	//	@Test
	//	public void testNoParameters2() {
	//		String json = ControllerUtil.createRequestJson("remoteProviderSimple", "method2", 1, null);
	//		List<ExtDirectResponse> responses = controller.router(request, response, Locale.ENGLISH, json);
	//
	//		assertEquals(1, responses.size());
	//		ExtDirectResponse resp = responses.get(0);
	//		assertEquals("remoteProviderSimple", resp.getAction());
	//		assertEquals("method2", resp.getMethod());
	//		assertEquals(1, resp.getTid());
	//		assertEquals("rpc", resp.getType());
	//		assertNull(resp.getWhere());
	//		assertNull(resp.getMessage());
	//		assertEquals("method2() called", resp.getResult());
	//	}
	//
	//	@Test
	//	public void testWithParameters() {
	//		String json = ControllerUtil.createRequestJson("remoteProviderSimple", "method3", 10, 1, 3.1,
	//				"requestParameter");
	//		List<ExtDirectResponse> responses = controller.router(request, response, Locale.ENGLISH, json);
	//
	//		assertEquals(1, responses.size());
	//		ExtDirectResponse resp = responses.get(0);
	//		assertEquals("remoteProviderSimple", resp.getAction());
	//		assertEquals("method3", resp.getMethod());
	//		assertEquals(10, resp.getTid());
	//		assertEquals("rpc", resp.getType());
	//		assertNull(resp.getWhere());
	//		assertNull(resp.getMessage());
	//		assertEquals("method3() called-1-3.1-requestParameter", resp.getResult());
	//	}
	//
	//	@Test
	//	public void testWithParametersNoRequestParameter() {
	//		String json = ControllerUtil.createRequestJson("remoteProviderSimple", "method3", 1, null);
	//		List<ExtDirectResponse> responses = controller.router(request, response, Locale.ENGLISH, json);
	//
	//		assertEquals(1, responses.size());
	//		ExtDirectResponse resp = responses.get(0);
	//		assertEquals("remoteProviderSimple", resp.getAction());
	//		assertEquals("method3", resp.getMethod());
	//		assertEquals(1, resp.getTid());
	//		assertEquals("exception", resp.getType());
	//		assertNull(resp.getWhere());
	//		assertEquals("Server Error", resp.getMessage());
	//		assertNull(resp.getResult());
	//	}
	//
	//	@Test
	//	public void testResultTrue() {
	//		String json = ControllerUtil.createRequestJson("remoteProviderSimple", "method5", 1, "ralph");
	//		List<ExtDirectResponse> responses = controller.router(request, response, Locale.ENGLISH, json);
	//
	//		assertEquals(1, responses.size());
	//		ExtDirectResponse resp = responses.get(0);
	//		assertEquals("remoteProviderSimple", resp.getAction());
	//		assertEquals("method5", resp.getMethod());
	//		assertEquals(1, resp.getTid());
	//		assertEquals("rpc", resp.getType());
	//		assertNull(resp.getWhere());
	//		assertNull(resp.getMessage());
	//		assertEquals(true, resp.getResult());
	//	}
	//
	//	@Test
	//	public void testResultFalse() {
	//		String json = ControllerUtil.createRequestJson("remoteProviderSimple", "method5", 1, "joe");
	//		List<ExtDirectResponse> responses = controller.router(request, response, Locale.ENGLISH, json);
	//
	//		assertEquals(1, responses.size());
	//		ExtDirectResponse resp = responses.get(0);
	//		assertEquals("remoteProviderSimple", resp.getAction());
	//		assertEquals("method5", resp.getMethod());
	//		assertEquals(1, resp.getTid());
	//		assertEquals("rpc", resp.getType());
	//		assertNull(resp.getWhere());
	//		assertNull(resp.getMessage());
	//		assertEquals(false, resp.getResult());
	//	}
	//
	//	@Test
	//	public void testResultNull() {
	//		String json = ControllerUtil.createRequestJson("remoteProviderSimple", "method5", 1, "martin");
	//		List<ExtDirectResponse> responses = controller.router(request, response, Locale.ENGLISH, json);
	//
	//		assertEquals(1, responses.size());
	//		ExtDirectResponse resp = responses.get(0);
	//		assertEquals("remoteProviderSimple", resp.getAction());
	//		assertEquals("method5", resp.getMethod());
	//		assertEquals(1, resp.getTid());
	//		assertEquals("rpc", resp.getType());
	//		assertNull(resp.getWhere());
	//		assertNull(resp.getMessage());
	//		assertNull(resp.getResult());
	//	}
	//
	//	@Test
	//	public void testIntParameterAndResult() {
	//		String json = ControllerUtil.createRequestJson("remoteProviderSimple", "method6", 3, 10, 20);
	//		List<ExtDirectResponse> responses = controller.router(request, response, Locale.ENGLISH, json);
	//
	//		assertEquals(1, responses.size());
	//		ExtDirectResponse resp = responses.get(0);
	//		checkIntParameterResult(resp, 3);
	//	}
	//
	//	static void checkIntParameterResult(ExtDirectResponse resp, int tid) {
	//		assertEquals("remoteProviderSimple", resp.getAction());
	//		assertEquals("method6", resp.getMethod());
	//		assertEquals(tid, resp.getTid());
	//		assertEquals("rpc", resp.getType());
	//		assertNull(resp.getWhere());
	//		assertNull(resp.getMessage());
	//		assertEquals(30, resp.getResult());
	//	}
	//
	//	@Test
	//	public void testResultStringNull() {
	//		String json = ControllerUtil.createRequestJson("remoteProviderSimple", "method7", 1, null);
	//		List<ExtDirectResponse> responses = controller.router(request, response, Locale.ENGLISH, json);
	//
	//		assertEquals(1, responses.size());
	//		ExtDirectResponse resp = responses.get(0);
	//		assertEquals("remoteProviderSimple", resp.getAction());
	//		assertEquals("method7", resp.getMethod());
	//		assertEquals(1, resp.getTid());
	//		assertEquals("rpc", resp.getType());
	//		assertNull(resp.getWhere());
	//		assertNull(resp.getMessage());
	//		assertNull(resp.getResult());
	//	}
	//
	//	@Test
	//	public void testReturnsObject() {
	//		String json = ControllerUtil.createRequestJson("remoteProviderSimple", "method8", 1, 7.34);
	//		List<ExtDirectResponse> responses = controller.router(request, response, Locale.ENGLISH, json);
	//
	//		assertEquals(1, responses.size());
	//		ExtDirectResponse resp = responses.get(0);
	//
	//		assertEquals("remoteProviderSimple", resp.getAction());
	//		assertEquals("method8", resp.getMethod());
	//		assertEquals(1, resp.getTid());
	//		assertEquals("rpc", resp.getType());
	//		assertNull(resp.getWhere());
	//		assertNull(resp.getMessage());
	//		assertNotNull(resp.getResult());
	//
	//		assertTrue(resp.getResult() instanceof FormInfo);
	//		FormInfo info = (FormInfo) resp.getResult();
	//
	//		assertTrue(Double.compare(7.34, info.getBack()) == 0);
	//		assertEquals(false, info.isAdmin());
	//		assertEquals(32, info.getAge());
	//		assertEquals("John", info.getName());
	//		assertEquals(new BigDecimal("8720.20"), info.getSalary());
	//		assertEquals(new GregorianCalendar(1986, Calendar.JULY, 22).getTime(), info.getBirthday());
	//	}
	//
	//	@Test
	//	public void testSupportedArguments() {
	//		String json = ControllerUtil.createRequestJson("remoteProviderSimple", "method9", 1, null);
	//
	//		List<ExtDirectResponse> responses = controller.router(request, response, Locale.ENGLISH, json);
	//
	//		assertEquals(1, responses.size());
	//		ExtDirectResponse resp = responses.get(0);
	//
	//		assertEquals("remoteProviderSimple", resp.getAction());
	//		assertEquals("method9", resp.getMethod());
	//		assertEquals(1, resp.getTid());
	//		assertEquals("rpc", resp.getType());
	//		assertNull(resp.getWhere());
	//		assertNull(resp.getMessage());
	//		assertEquals(42l, resp.getResult());
	//	}

}
