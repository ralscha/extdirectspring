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
package ch.ralscha.extdirectspring.api;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.StringUtils;

import ch.ralscha.extdirectspring.util.JsonHandler;

/**
 * Tests for {@link ApiController}.
 * 
 * @author Ralph Schaer
 */
@SuppressWarnings("all")
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:/testApplicationContext.xml")
public class ApiControllerTest {

	@Autowired
	private JsonHandler jsonHandler;

	@Autowired
	private ApiController apiController;

	@Test
	public void testNoActionNamespaceDebug() throws IOException {
		MockHttpServletRequest request = new MockHttpServletRequest("POST", "/action/api-debug.js");
		MockHttpServletResponse response = new MockHttpServletResponse();
		apiController.api("test", null, "TEST_REMOTING_API", "TEST_POLLING_URLS", null, false, null, request, response);
		compare(response, allApis(null), "test", "TEST_REMOTING_API", "TEST_POLLING_URLS");

		request = new MockHttpServletRequest("POST", "/action/api.js");
		response = new MockHttpServletResponse();
		apiController.api("test", null, "TEST_REMOTING_API", "TEST_POLLING_URLS", null, false, null, request, response);
		compare(response, allApis(null), "test", "TEST_REMOTING_API", "TEST_POLLING_URLS");
	}

	@Test
	public void testWithActionNamespace() throws IOException {
		MockHttpServletRequest request = new MockHttpServletRequest("POST", "/action/api-debug.js");
		MockHttpServletResponse response = new MockHttpServletResponse();
		apiController.api("Ext.ns", "actionns", "TEST_REMOTING_API", "TEST_POLLING_URLS", null, false, null, request,
				response);
		compare(response, allApis("actionns"), "Ext.ns", "TEST_REMOTING_API", "TEST_POLLING_URLS");

		request = new MockHttpServletRequest("POST", "/action/api.js");
		response = new MockHttpServletResponse();
		apiController.api("Ext.ns", "actionns", "TEST_REMOTING_API", "TEST_POLLING_URLS", null, false, null, request,
				response);
		compare(response, allApis("actionns"), "Ext.ns", "TEST_REMOTING_API", "TEST_POLLING_URLS");
	}

	@Test
	public void testUnknownGroup() throws IOException {
		MockHttpServletRequest request = new MockHttpServletRequest("POST", "/action/api-debug.js");
		MockHttpServletResponse response = new MockHttpServletResponse();
		apiController.api("test", null, "TEST_REMOTING_API", "TEST_POLLING_URLS", "xy", false, null, request, response);
		compare(response, noApis(null), "test", "TEST_REMOTING_API", "TEST_POLLING_URLS");

		request = new MockHttpServletRequest("POST", "/action/api.js");
		response = new MockHttpServletResponse();
		apiController.api("test", null, "TEST_REMOTING_API", "TEST_POLLING_URLS", "xy", false, null, request, response);
		compare(response, noApis(null), "test", "TEST_REMOTING_API", "TEST_POLLING_URLS");
	}

	@Test
	public void testGroup1() throws IOException {
		MockHttpServletRequest request = new MockHttpServletRequest("POST", "/action/api-debug.js");
		MockHttpServletResponse response = new MockHttpServletResponse();
		apiController.api("Ext.ns", "actionns", "REMOTING_API", "POLLING_URLS", "group1", false, null, request,
				response);
		compare(response, group1Apis("actionns"), "Ext.ns", "REMOTING_API", "POLLING_URLS");

		request = new MockHttpServletRequest("POST", "/action/api.js");
		response = new MockHttpServletResponse();
		apiController.api("Ext.ns", "actionns", "REMOTING_API", "POLLING_URLS", "group1", false, null, request,
				response);
		compare(response, group1Apis("actionns"), "Ext.ns", "REMOTING_API", "POLLING_URLS");
	}

	@Test
	public void testGroup1Again() throws IOException {
		testGroup1();
	}

	@Test
	public void testGroup2() throws IOException {
		MockHttpServletRequest request = new MockHttpServletRequest("POST", "/action/api-debug.js");
		MockHttpServletResponse response = new MockHttpServletResponse();
		apiController.api("test", null, "TEST_REMOTING_API", "TEST_POLLING_URLS", "group2", false, null, request,
				response);
		compare(response, group2Apis(null), "test", "TEST_REMOTING_API", "TEST_POLLING_URLS");

		request = new MockHttpServletRequest("POST", "/action/api.js");
		response = new MockHttpServletResponse();
		apiController.api("test", null, "TEST_REMOTING_API", "TEST_POLLING_URLS", "group2", false, null, request,
				response);
		compare(response, group2Apis(null), "test", "TEST_REMOTING_API", "TEST_POLLING_URLS");
	}

	@Test
	public void testGroup2Again() throws IOException {
		testGroup2();
	}

	@Test
	public void testGroup3() throws IOException {
		MockHttpServletRequest request = new MockHttpServletRequest("POST", "/action/api-debug.js");
		MockHttpServletResponse response = new MockHttpServletResponse();
		apiController.api("Extns", "ns", "RAPI", "PURLS", "group3", false, null, request, response);
		compare(response, group3Apis("ns"), "Extns", "RAPI", "PURLS");

		request = new MockHttpServletRequest("POST", "/action/api.js");
		response = new MockHttpServletResponse();
		apiController.api("Extns", "ns", "RAPI", "PURLS", "group3", false, null, request, response);
		compare(response, group3Apis("ns"), "Extns", "RAPI", "PURLS");
	}

	@Test
	public void testGroup3Again() throws IOException {
		testGroup3();
	}

	@Test
	public void testGroup4() throws IOException {
		MockHttpServletRequest request = new MockHttpServletRequest("POST", "/action/api-debug.js");
		MockHttpServletResponse response = new MockHttpServletResponse();
		apiController.api("test", "", "TEST_REMOTING_API", "TEST_POLLING_URLS", "group4", false, null, request,
				response);
		compare(response, group4Apis(null), "test", "TEST_REMOTING_API", "TEST_POLLING_URLS");

		request = new MockHttpServletRequest("POST", "/action/api.js");
		response = new MockHttpServletResponse();
		apiController.api("test", null, "TEST_REMOTING_API", "TEST_POLLING_URLS", "group4", false, null, request,
				response);
		compare(response, group4Apis(null), "test", "TEST_REMOTING_API", "TEST_POLLING_URLS");
	}

	@Test
	public void testGroup4Again() throws IOException {
		testGroup4();
	}

	@Test
	public void testGroup1and2() throws IOException {
		MockHttpServletRequest request = new MockHttpServletRequest("POST", "/action/api-debug.js");
		MockHttpServletResponse response = new MockHttpServletResponse();
		apiController.api("Ext.ns", "actionns", "REMOTING_API", "POLLING_URLS", "group1,group2", false, null, request,
				response);
		compare(response, group1and2Apis("actionns"), "Ext.ns", "REMOTING_API", "POLLING_URLS");

		request = new MockHttpServletRequest("POST", "/action/api.js");
		response = new MockHttpServletResponse();
		apiController.api("Ext.ns", "actionns", "REMOTING_API", "POLLING_URLS", "group1,group2", false, null, request,
				response);
		compare(response, group1and2Apis("actionns"), "Ext.ns", "REMOTING_API", "POLLING_URLS");
	}
	
	@Test
	public void testGroup1andUnknown() throws IOException {
		MockHttpServletRequest request = new MockHttpServletRequest("POST", "/action/api-debug.js");
		MockHttpServletResponse response = new MockHttpServletResponse();
		apiController.api("Ext.ns", "actionns", "REMOTING_API", "POLLING_URLS", "group1,unknown", false, null, request,
				response);
		compare(response, group1Apis("actionns"), "Ext.ns", "REMOTING_API", "POLLING_URLS");

		request = new MockHttpServletRequest("POST", "/action/api.js");
		response = new MockHttpServletResponse();
		apiController.api("Ext.ns", "actionns", "REMOTING_API", "POLLING_URLS", "group1,unknown", false, null, request,
				response);
		compare(response, group1Apis("actionns"), "Ext.ns", "REMOTING_API", "POLLING_URLS");
	}

	
	@Test
	public void testInterfaceGroup() throws IOException {
		MockHttpServletRequest request = new MockHttpServletRequest("POST", "/action/api-debug.js");
		MockHttpServletResponse response = new MockHttpServletResponse();
		apiController.api("test", "", "TEST_REMOTING_API", "TEST_POLLING_URLS", "interface", false, null, request,
				response);
		compare(response, interfaceApis(null), "test", "TEST_REMOTING_API", "TEST_POLLING_URLS");

		request = new MockHttpServletRequest("POST", "/action/api.js");
		response = new MockHttpServletResponse();
		apiController.api("test", null, "TEST_REMOTING_API", "TEST_POLLING_URLS", "interface", false, null, request,
				response);
		compare(response, interfaceApis(null), "test", "TEST_REMOTING_API", "TEST_POLLING_URLS");
	}

	@Test
	public void testNoApiNs() throws IOException {
		MockHttpServletRequest request = new MockHttpServletRequest("POST", "/action/api-debug.js");
		MockHttpServletResponse response = new MockHttpServletResponse();
		apiController.api("", "", "TEST_REMOTING_API", "TEST_POLLING_URLS", "group4", false, null, request, response);
		compare(response, group4Apis(null), null, "TEST_REMOTING_API", "TEST_POLLING_URLS");

		request = new MockHttpServletRequest("POST", "/action/api.js");
		response = new MockHttpServletResponse();
		apiController.api("", null, "TEST_REMOTING_API", "TEST_POLLING_URLS", "group4", false, null, request, response);
		compare(response, group4Apis(null), null, "TEST_REMOTING_API", "TEST_POLLING_URLS");
	}

	@Test
	public void testFullRouterUrl() throws IOException {
		MockHttpServletRequest request = new MockHttpServletRequest("POST", "/action/api-debug.js");
		MockHttpServletResponse response = new MockHttpServletResponse();
		apiController.api("apiNs", "", "TEST_RMT_API", "TEST_POLL_URLS", "group2", true, null, request, response);
		compare(response, group2Apis(null, "http://localhost:80/action/router"), "apiNs", "TEST_RMT_API",
				"TEST_POLL_URLS");

		request = new MockHttpServletRequest("POST", "/action/api.js");
		response = new MockHttpServletResponse();
		apiController.api("apiNs", null, "TEST_RMT_API", "TEST_POLL_URLS", "group2", true, null, request, response);
		compare(response, group2Apis(null, "http://localhost:80/action/router"), "apiNs", "TEST_RMT_API",
				"TEST_POLL_URLS");
	}

	@Test
	public void testFormat() throws IOException {

		MockHttpServletRequest request = new MockHttpServletRequest("POST", "/action/api-debug.js");
		MockHttpServletResponse response = new MockHttpServletResponse();
		apiController.api("apiNs", "", "TEST_RMT_API", "TEST_POLL_URLS", "group2", false, "json", request, response);
		compareJson(response, group2Apis(null, "http://localhost:80/action/router"), "apiNs", "TEST_RMT_API");

		request = new MockHttpServletRequest("POST", "/action/api.js");
		response = new MockHttpServletResponse();
		apiController.api("", "ns", "TEST_RMT_API", "TEST_POLL_URLS", "group2", true, "json", request, response);
		compareJson(response, group2Apis("ns", "http://localhost:80/action/router"), "", "TEST_RMT_API");

	}

	private RemotingApi noApis(String namespace) {
		RemotingApi remotingApi = new RemotingApi("/action/router", namespace);
		return remotingApi;
	}

	private RemotingApi group1Apis(String namespace) {
		RemotingApi remotingApi = new RemotingApi("/action/router", namespace);
		remotingApi.addAction("remoteProviderSimple", "method1", 0, false);
		remotingApi.addAction("remoteProviderTreeLoad", "method1", 1, false);
		return remotingApi;
	}

	private RemotingApi group2Apis(String namespace, String url) {
		RemotingApi remotingApi = new RemotingApi(url, namespace);
		remotingApi.addAction("remoteProviderSimple", "method3", 3, false);
		remotingApi.addAction("remoteProviderSimple", "method5", 1, false);
		remotingApi.addAction("remoteProviderStoreRead", "method6", 1, false);
		remotingApi.addAction("remoteProviderStoreRead", "method7", 1, false);
		remotingApi.addAction("remoteProviderStoreModify", "update4", 1, false);
		remotingApi.addAction("formInfoController", "upload", 0, true);
		remotingApi.addPollingProvider("pollProvider", "handleMessage1", "message1");
		remotingApi.addPollingProvider("pollProvider", "handleMessage2", "message2");
		remotingApi.addPollingProvider("pollProvider", "message6", "message6");
		return remotingApi;
	}

	private RemotingApi group1and2Apis(String namespace) {
		RemotingApi remotingApi = new RemotingApi("/action/router", namespace);
		remotingApi.addAction("remoteProviderSimple", "method1", 0, false);
		remotingApi.addAction("remoteProviderTreeLoad", "method1", 1, false);

		remotingApi.addAction("remoteProviderSimple", "method3", 3, false);
		remotingApi.addAction("remoteProviderSimple", "method5", 1, false);
		remotingApi.addAction("remoteProviderStoreRead", "method6", 1, false);
		remotingApi.addAction("remoteProviderStoreRead", "method7", 1, false);
		remotingApi.addAction("remoteProviderStoreModify", "update4", 1, false);
		remotingApi.addAction("formInfoController", "upload", 0, true);
		remotingApi.addPollingProvider("pollProvider", "handleMessage1", "message1");
		remotingApi.addPollingProvider("pollProvider", "handleMessage2", "message2");
		remotingApi.addPollingProvider("pollProvider", "message6", "message6");
		return remotingApi;
	}

	
	private RemotingApi group2Apis(String namespace) {
		return group2Apis(namespace, "/action/router");
	}

	private RemotingApi group3Apis(String namespace) {
		RemotingApi remotingApi = new RemotingApi("/action/router", namespace);
		remotingApi.addAction("remoteProviderSimple", "method9", 0, false);
		remotingApi.addAction("remoteProviderStoreRead", "method5", 1, false);
		remotingApi.addAction("remoteProviderStoreModify", "destroy", 1, false);
		remotingApi.addAction("remoteProviderFormLoad", "method1", 1, false);
		remotingApi.addAction("remoteProviderFormLoad", "method5", 1, false);
		remotingApi.addAction("formInfoController", "updateInfo", 0, true);
		remotingApi.addAction("formInfoController2", "updateInfo1", 0, true);
		remotingApi.addAction("formInfoController2", "updateInfo2", 0, true);
		remotingApi.addAction("remoteProviderTreeLoad", "method3", 1, false);
		remotingApi.addPollingProvider("pollProvider", "handleMessage5", "message5");
		return remotingApi;
	}

	private RemotingApi group4Apis(String namespace) {
		RemotingApi remotingApi = new RemotingApi("/action/router", namespace);
		remotingApi.addPollingProvider("pollProvider", "handleMessage3", "message3");
		return remotingApi;
	}

	private RemotingApi interfaceApis(String namespace) {
		RemotingApi remotingApi = new RemotingApi("/action/router", namespace);
		remotingApi.addAction("remoteProviderImplementation", "storeRead", 1, false);
		remotingApi.addAction("remoteProviderImplementation", "method2", 0, false);
		remotingApi.addAction("remoteProviderImplementation", "method3", 3, false);
		return remotingApi;
	}

	private RemotingApi allApis(String namespace) {
		RemotingApi remotingApi = new RemotingApi("/action/router", namespace);
		remotingApi.addAction("remoteProviderSimple", "method1", 0, false);
		remotingApi.addAction("remoteProviderSimple", "method2", 0, false);
		remotingApi.addAction("remoteProviderSimple", "method3", 3, false);
		remotingApi.addAction("remoteProviderSimple", "method5", 1, false);
		remotingApi.addAction("remoteProviderSimple", "method6", 2, false);
		remotingApi.addAction("remoteProviderSimple", "method7", 0, false);
		remotingApi.addAction("remoteProviderSimple", "method8", 1, false);
		remotingApi.addAction("remoteProviderSimple", "method9", 0, false);
		remotingApi.addAction("remoteProviderSimple", "method10", 9, false);
		remotingApi.addAction("remoteProviderSimple", "method11", 0, false);
		remotingApi.addAction("remoteProviderSimple", "method12", 1, false);
		remotingApi.addAction("remoteProviderSimple", "method13", 9, false);
		remotingApi.addAction("remoteProviderSimple", "method14", 4, false);

		remotingApi.addAction("remoteProviderStoreRead", "method1", 1, false);
		remotingApi.addAction("remoteProviderStoreRead", "method2", 1, false);
		remotingApi.addAction("remoteProviderStoreRead", "method3", 1, false);
		remotingApi.addAction("remoteProviderStoreRead", "method4", 1, false);
		remotingApi.addAction("remoteProviderStoreRead", "method5", 1, false);
		remotingApi.addAction("remoteProviderStoreRead", "method6", 1, false);
		remotingApi.addAction("remoteProviderStoreRead", "method7", 1, false);
		remotingApi.addAction("remoteProviderStoreRead", "method8", 1, false);
		remotingApi.addAction("remoteProviderStoreRead", "methodFilter", 1, false);
		remotingApi.addAction("remoteProviderStoreRead", "methodMetadata", 1, false);

		remotingApi.addAction("remoteProviderStoreModify", "create1", 1, false);
		remotingApi.addAction("remoteProviderStoreModify", "create2", 1, false);
		remotingApi.addAction("remoteProviderStoreModify", "update1", 1, false);
		remotingApi.addAction("remoteProviderStoreModify", "update2", 1, false);
		remotingApi.addAction("remoteProviderStoreModify", "update3", 1, false);
		remotingApi.addAction("remoteProviderStoreModify", "update4", 1, false);
		remotingApi.addAction("remoteProviderStoreModify", "destroy", 1, false);

		remotingApi.addAction("remoteProviderFormLoad", "method1", 1, false);
		remotingApi.addAction("remoteProviderFormLoad", "method2", 1, false);
		remotingApi.addAction("remoteProviderFormLoad", "method3", 1, false);
		remotingApi.addAction("remoteProviderFormLoad", "method4", 1, false);
		remotingApi.addAction("remoteProviderFormLoad", "method5", 1, false);
		remotingApi.addAction("remoteProviderFormLoad", "method6", 1, false);
		remotingApi.addAction("remoteProviderFormLoad", "method7", 1, false);

		remotingApi.addAction("formInfoController", "updateInfo", 0, true);
		remotingApi.addAction("formInfoController", "upload", 0, true);

		remotingApi.addAction("formInfoController2", "updateInfo1", 0, true);
		remotingApi.addAction("formInfoController2", "updateInfo2", 0, true);

		remotingApi.addAction("remoteProviderTreeLoad", "method1", 1, false);
		remotingApi.addAction("remoteProviderTreeLoad", "method2", 1, false);
		remotingApi.addAction("remoteProviderTreeLoad", "method3", 1, false);

		remotingApi.addAction("remoteProviderImplementation", "storeRead", 1, false);
		remotingApi.addAction("remoteProviderImplementation", "method2", 0, false);
		remotingApi.addAction("remoteProviderImplementation", "method3", 3, false);

		remotingApi.addAction("remoteProviderSimpleNamed", "method1", new ArrayList());
		remotingApi.addAction("remoteProviderSimpleNamed", "method2", Arrays.asList("i", "d", "s"));
		remotingApi.addAction("remoteProviderSimpleNamed", "method3", Arrays.asList("userName"));
		remotingApi.addAction("remoteProviderSimpleNamed", "method4", Arrays.asList("a", "b"));
		remotingApi.addAction("remoteProviderSimpleNamed", "method5", Arrays.asList("d"));
		remotingApi.addAction("remoteProviderSimpleNamed", "method6", new ArrayList());
		remotingApi.addAction("remoteProviderSimpleNamed", "method7", Arrays.asList("flag", "aCharacter", "workflow",
				"aInt", "aLong", "aDouble", "aFloat", "aShort", "aByte"));
		remotingApi.addAction("remoteProviderSimpleNamed", "method9", Arrays.asList("aRow"));
		remotingApi.addAction("remoteProviderSimpleNamed", "method10", Arrays.asList("flag", "aCharacter", "workflow",
				"aInt", "aLong", "aDouble", "aFloat", "aShort", "aByte"));

		remotingApi.addAction("remoteProviderSimpleNamed", "method11",
				Arrays.asList("endDate", "normalParameter", "aDate", "percent"));

		remotingApi.addAction("remoteProviderSimpleNamed", "methodRP1", Arrays.asList("lastName", "theAge", "active"));
		remotingApi.addAction("remoteProviderSimpleNamed", "methodRP2", Arrays.asList("lastName", "theAge", "active"));
		remotingApi.addAction("remoteProviderSimpleNamed", "methodRP3", Arrays.asList("lastName", "theAge", "active"));

		remotingApi.addPollingProvider("pollProvider", "handleMessage1", "message1");
		remotingApi.addPollingProvider("pollProvider", "handleMessage2", "message2");
		remotingApi.addPollingProvider("pollProvider", "handleMessage3", "message3");
		remotingApi.addPollingProvider("pollProvider", "handleMessage4", "message4");
		remotingApi.addPollingProvider("pollProvider", "handleMessage5", "message5");
		remotingApi.addPollingProvider("pollProvider", "message6", "message6");

		return remotingApi;
	}

	private void compareJson(MockHttpServletResponse response, RemotingApi remotingApi, String apiNs,
			String remotingApiVar) throws JsonParseException, JsonMappingException, IOException {
		String content = response.getContentAsString();
		assertEquals("application/json", response.getContentType());
		assertTrue(StringUtils.hasText(content));

		Map<String, Object> rootAsMap = jsonHandler.readValue(content, Map.class);

		if (remotingApi.getNamespace() == null) {
			assertEquals(4, rootAsMap.size());
		} else {
			assertEquals(5, rootAsMap.size());
			assertEquals(remotingApi.getNamespace(), rootAsMap.get("namespace"));
		}

		assertEquals(remotingApi.getUrl(), rootAsMap.get("url"));
		assertEquals("remoting", rootAsMap.get("type"));
		if (StringUtils.hasText(apiNs)) {
			assertEquals(apiNs + "." + remotingApiVar, rootAsMap.get("descriptor"));
		} else {
			assertEquals(remotingApiVar, rootAsMap.get("descriptor"));
		}
		assertTrue(rootAsMap.containsKey("actions"));

		if (remotingApi.getNamespace() != null) {
			assertEquals(remotingApi.getNamespace(), rootAsMap.get("namespace"));
		}

		Map<String, Object> beans = (Map<String, Object>) rootAsMap.get("actions");

		assertEquals(remotingApi.getActions().size(), beans.size());
		for (String beanName : remotingApi.getActions().keySet()) {
			List<Map<String, Object>> actions = (List<Map<String, Object>>) beans.get(beanName);
			List<Action> expectedActions = remotingApi.getActions().get(beanName);
			compare(expectedActions, actions);
		}

	}

	private void compare(MockHttpServletResponse response, RemotingApi remotingApi, String apiNs,
			String remotingApiVar, String pollingUrlsVar) throws JsonParseException, JsonMappingException, IOException {
		String content = response.getContentAsString();
		content = content.replace(";", ";\n");
		content = content.replace("{", "{\n");
		content = content.replace("}", "}\n");

		assertEquals("application/x-javascript", response.getContentType());
		assertTrue(StringUtils.hasText(content));

		String[] lines = content.split("\n");

		String remotingApiLine;
		String pollingApiLine;

		if (StringUtils.hasText(apiNs)) {
			String extNsLine = "Ext.ns('" + apiNs + "');";
			assertContains(extNsLine, lines);

			remotingApiLine = apiNs + "." + remotingApiVar + " = {";
			pollingApiLine = apiNs + "." + pollingUrlsVar + " = {";
		} else {
			remotingApiLine = remotingApiVar + " = {";
			pollingApiLine = pollingUrlsVar + " = {";
		}

		int startRemotingApi = assertContains(remotingApiLine, lines);

		int startPollingApi = lines.length;
		if (!remotingApi.getPollingProviders().isEmpty()) {
			startPollingApi = assertContains(pollingApiLine, lines);
		}

		if (remotingApi.getNamespace() != null) {
			String actionNs = "Ext.ns('" + remotingApi.getNamespace() + "');";
			assertContains(actionNs, lines);
		}

		String remotingJson = "{";
		for (int i = startRemotingApi + 1; i < startPollingApi; i++) {
			remotingJson += lines[i];
		}

		String pollingJson = "{";
		if (!remotingApi.getPollingProviders().isEmpty()) {
			for (int i = startPollingApi + 1; i < lines.length; i++) {
				pollingJson += lines[i];
			}
		}

		Map<String, Object> rootAsMap = jsonHandler.readValue(remotingJson, Map.class);
		if (remotingApi.getNamespace() == null) {
			assertEquals(3, rootAsMap.size());
		} else {
			assertEquals(4, rootAsMap.size());
		}

		assertEquals(remotingApi.getUrl(), rootAsMap.get("url"));
		assertEquals("remoting", rootAsMap.get("type"));
		assertTrue(rootAsMap.containsKey("actions"));

		if (remotingApi.getNamespace() != null) {
			assertEquals(remotingApi.getNamespace(), rootAsMap.get("namespace"));
		}

		Map<String, Object> beans = (Map<String, Object>) rootAsMap.get("actions");

		assertEquals(remotingApi.getActions().size(), beans.size());
		for (String beanName : remotingApi.getActions().keySet()) {
			List<Map<String, Object>> actions = (List<Map<String, Object>>) beans.get(beanName);
			List<Action> expectedActions = remotingApi.getActions().get(beanName);
			compare(expectedActions, actions);
		}

		if (!remotingApi.getPollingProviders().isEmpty()) {
			Map<String, Object> pollingMap = jsonHandler.readValue(pollingJson, Map.class);
			assertEquals(remotingApi.getPollingProviders().size(), pollingMap.size());
			for (PollingProvider pp : remotingApi.getPollingProviders()) {
				String url = (String) pollingMap.get(pp.getEvent());
				assertNotNull(url);
				assertEquals(String.format("%s/%s/%s/%s", remotingApi.getUrl().replace("router", "poll"),
						pp.getBeanName(), pp.getMethod(), pp.getEvent()), url);
			}
		}
	}

	private void compare(List<Action> expectedActions, List<Map<String, Object>> actions) {
		assertEquals(expectedActions.size(), actions.size());
		for (Action expectedAction : expectedActions) {
			Map<String, Object> action = null;
			for (Map<String, Object> map : actions) {
				if (map.get("name").equals(expectedAction.getName())) {
					action = map;
					break;
				}
			}
			assertNotNull(action);
			assertEquals(expectedAction.getName(), action.get("name"));
			assertEquals(expectedAction.getLen(), action.get("len"));
			if (expectedAction.isFormHandler() != null && expectedAction.isFormHandler()) {
				assertEquals(expectedAction.isFormHandler(), action.get("formHandler"));
			} else {
				assertFalse(action.containsKey("formHandler"));
			}

			List<String> params = (List<String>) action.get("params");
			assertTrue((params != null && expectedAction.getParams() != null)
					|| (params == null && expectedAction.getParams() == null));

			if (expectedAction.getParams() != null) {
				assertEquals(expectedAction.getParams().size(), params.size());
				for (String param : expectedAction.getParams()) {
					assertTrue(params.contains(param));
				}
			}
		}
	}

	private int assertContains(String extNsLine, String[] lines) {
		if (lines == null) {
			fail("no lines");
		}

		int lineCount = 0;
		for (String line : lines) {
			if (line.startsWith(extNsLine)) {
				return lineCount;
			}
			lineCount++;
		}
		fail("lines does not contain : " + extNsLine);
		return -1;
	}

}
