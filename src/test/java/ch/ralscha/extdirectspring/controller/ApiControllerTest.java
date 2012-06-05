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
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.util.StringUtils;

import ch.ralscha.extdirectspring.bean.api.Action;
import ch.ralscha.extdirectspring.bean.api.PollingProvider;
import ch.ralscha.extdirectspring.bean.api.RemotingApi;
import ch.ralscha.extdirectspring.util.ApiCache;

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
	private ApplicationContext applicationContext;

	@Autowired
	private ApiController apiController;

	@Autowired
	private RouterController routerController;

	@Before
	public void setupApiController() throws Exception {
		routerController.afterPropertiesSet();
		ApiCache.INSTANCE.clear();
	}

	@Test
	public void testNoActionNamespaceDebug() throws IOException {
		MockHttpServletRequest request = new MockHttpServletRequest("GET", "/action/api-debug.js");
		MockHttpServletResponse response = new MockHttpServletResponse();
		apiController.api("test", null, "TEST_REMOTING_API", "TEST_POLLING_URLS", null, false, null, request, response);
		compare(response, allApis(null), "test", "TEST_REMOTING_API", "TEST_POLLING_URLS");

		request = new MockHttpServletRequest("GET", "/action/api.js");
		response = new MockHttpServletResponse();
		apiController.api("test", null, "TEST_REMOTING_API", "TEST_POLLING_URLS", null, false, null, request, response);
		compare(response, allApis(null), "test", "TEST_REMOTING_API", "TEST_POLLING_URLS");

		// With configuration
		ApiCache.INSTANCE.clear();
		Configuration config = new Configuration();
		config.setEnableBuffer(10);
		config.setMaxRetries(2);
		config.setTimeout(12000);
		ReflectionTestUtils.setField(routerController, "configuration", config);

		request = new MockHttpServletRequest("GET", "/action/api-debug.js");
		response = new MockHttpServletResponse();
		apiController.api("test", null, "TEST_REMOTING_API", "TEST_POLLING_URLS", null, false, null, request, response);
		compare(response, allApis(null), "test", "TEST_REMOTING_API", "TEST_POLLING_URLS", config);

		request = new MockHttpServletRequest("GET", "/action/api.js");
		response = new MockHttpServletResponse();
		apiController.api("test", null, "TEST_REMOTING_API", "TEST_POLLING_URLS", null, false, null, request, response);
		compare(response, allApis(null), "test", "TEST_REMOTING_API", "TEST_POLLING_URLS", config);

		ReflectionTestUtils.setField(routerController, "configuration", new Configuration());
	}

	@Test
	public void testWithActionNamespace() throws IOException {
		MockHttpServletRequest request = new MockHttpServletRequest("GET", "/action/api-debug.js");
		MockHttpServletResponse response = new MockHttpServletResponse();
		apiController.api("Ext.ns", "actionns", "TEST_REMOTING_API", "TEST_POLLING_URLS", null, false, null, request,
				response);
		compare(response, allApis("actionns"), "Ext.ns", "TEST_REMOTING_API", "TEST_POLLING_URLS");

		request = new MockHttpServletRequest("GET", "/action/api.js");
		response = new MockHttpServletResponse();
		apiController.api("Ext.ns", "actionns", "TEST_REMOTING_API", "TEST_POLLING_URLS", null, false, null, request,
				response);
		compare(response, allApis("actionns"), "Ext.ns", "TEST_REMOTING_API", "TEST_POLLING_URLS");

		// With configuration
		ApiCache.INSTANCE.clear();
		Configuration config = new Configuration();
		config.setEnableBuffer(false);
		config.setTimeout(10000);
		ReflectionTestUtils.setField(routerController, "configuration", config);

		request = new MockHttpServletRequest("GET", "/action/api-debug.js");
		response = new MockHttpServletResponse();
		apiController.api("Ext.ns", "actionns", "TEST_REMOTING_API", "TEST_POLLING_URLS", null, false, null, request,
				response);
		compare(response, allApis("actionns"), "Ext.ns", "TEST_REMOTING_API", "TEST_POLLING_URLS", config);

		request = new MockHttpServletRequest("GET", "/action/api.js");
		response = new MockHttpServletResponse();
		apiController.api("Ext.ns", "actionns", "TEST_REMOTING_API", "TEST_POLLING_URLS", null, false, null, request,
				response);
		compare(response, allApis("actionns"), "Ext.ns", "TEST_REMOTING_API", "TEST_POLLING_URLS", config);

		ReflectionTestUtils.setField(routerController, "configuration", new Configuration());
	}

	@Test
	public void testUnknownGroup() throws IOException {
		MockHttpServletRequest request = new MockHttpServletRequest("GET", "/action/api-debug.js");
		MockHttpServletResponse response = new MockHttpServletResponse();
		apiController.api("test", null, "TEST_REMOTING_API", "TEST_POLLING_URLS", "xy", false, null, request, response);
		compare(response, noApis(null), "test", "TEST_REMOTING_API", "TEST_POLLING_URLS");

		request = new MockHttpServletRequest("GET", "/action/api.js");
		response = new MockHttpServletResponse();
		apiController.api("test", null, "TEST_REMOTING_API", "TEST_POLLING_URLS", "xy", false, null, request, response);
		compare(response, noApis(null), "test", "TEST_REMOTING_API", "TEST_POLLING_URLS");

		// With configuration
		ApiCache.INSTANCE.clear();
		Configuration config = new Configuration();
		config.setEnableBuffer(true);
		ReflectionTestUtils.setField(routerController, "configuration", config);

		request = new MockHttpServletRequest("GET", "/action/api-debug.js");
		response = new MockHttpServletResponse();
		apiController.api("test", null, "TEST_REMOTING_API", "TEST_POLLING_URLS", "xy", false, null, request, response);
		compare(response, noApis(null), "test", "TEST_REMOTING_API", "TEST_POLLING_URLS", config);

		request = new MockHttpServletRequest("GET", "/action/api.js");
		response = new MockHttpServletResponse();
		apiController.api("test", null, "TEST_REMOTING_API", "TEST_POLLING_URLS", "xy", false, null, request, response);
		compare(response, noApis(null), "test", "TEST_REMOTING_API", "TEST_POLLING_URLS", config);

		ReflectionTestUtils.setField(routerController, "configuration", new Configuration());

	}

	@Test
	public void testGroup1() throws IOException {
		testGroup1(null);
		testGroup1(null);
	}

	@Test
	public void testGroup1WithConfig() throws IOException {
		Configuration config = new Configuration();
		config.setTimeout(12000);
		ReflectionTestUtils.setField(routerController, "configuration", config);

		testGroup1(config);
		testGroup1(config);

		ReflectionTestUtils.setField(routerController, "configuration", new Configuration());
	}

	private void testGroup1(final Configuration config) throws IOException {
		MockHttpServletRequest request = new MockHttpServletRequest("GET", "/action/api-debug.js");
		MockHttpServletResponse response = new MockHttpServletResponse();
		apiController.api("Ext.ns", "actionns", "REMOTING_API", "POLLING_URLS", "group1", false, null, request,
				response);
		compare(response, group1Apis("actionns"), "Ext.ns", "REMOTING_API", "POLLING_URLS", config);

		request = new MockHttpServletRequest("GET", "/action/api.js");
		response = new MockHttpServletResponse();
		apiController.api("Ext.ns", "actionns", "REMOTING_API", "POLLING_URLS", "group1", false, null, request,
				response);
		compare(response, group1Apis("actionns"), "Ext.ns", "REMOTING_API", "POLLING_URLS", config);
	}

	@Test
	public void testGroup2() throws IOException {
		MockHttpServletRequest request = new MockHttpServletRequest("GET", "/action/api-debug.js");
		MockHttpServletResponse response = new MockHttpServletResponse();
		apiController.api("test", null, "TEST_REMOTING_API", "TEST_POLLING_URLS", "group2", false, null, request,
				response);
		compare(response, group2Apis(null), "test", "TEST_REMOTING_API", "TEST_POLLING_URLS");

		request = new MockHttpServletRequest("GET", "/action/api.js");
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
		MockHttpServletRequest request = new MockHttpServletRequest("GET", "/action/api-debug.js");
		MockHttpServletResponse response = new MockHttpServletResponse();
		apiController.api("Extns", "ns", "RAPI", "PURLS", "group3", false, null, request, response);
		compare(response, group3Apis("ns"), "Extns", "RAPI", "PURLS");

		request = new MockHttpServletRequest("GET", "/action/api.js");
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
		MockHttpServletRequest request = new MockHttpServletRequest("GET", "/action/api-debug.js");
		MockHttpServletResponse response = new MockHttpServletResponse();
		apiController.api("test", "", "TEST_REMOTING_API", "TEST_POLLING_URLS", "group4", false, null, request,
				response);
		compare(response, group4Apis(null), "test", "TEST_REMOTING_API", "TEST_POLLING_URLS");

		request = new MockHttpServletRequest("GET", "/action/api.js");
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
		MockHttpServletRequest request = new MockHttpServletRequest("GET", "/action/api-debug.js");
		MockHttpServletResponse response = new MockHttpServletResponse();
		apiController.api("Ext.ns", "actionns", "REMOTING_API", "POLLING_URLS", "group1,group2", false, null, request,
				response);
		compare(response, group1and2Apis("actionns"), "Ext.ns", "REMOTING_API", "POLLING_URLS");

		request = new MockHttpServletRequest("GET", "/action/api.js");
		response = new MockHttpServletResponse();
		apiController.api("Ext.ns", "actionns", "REMOTING_API", "POLLING_URLS", "group1,group2", false, null, request,
				response);
		compare(response, group1and2Apis("actionns"), "Ext.ns", "REMOTING_API", "POLLING_URLS");
	}

	@Test
	public void testGroup1andUnknown() throws IOException {
		MockHttpServletRequest request = new MockHttpServletRequest("GET", "/action/api-debug.js");
		MockHttpServletResponse response = new MockHttpServletResponse();
		apiController.api("Ext.ns", "actionns", "REMOTING_API", "POLLING_URLS", "group1,unknown", false, null, request,
				response);
		compare(response, group1Apis("actionns"), "Ext.ns", "REMOTING_API", "POLLING_URLS");

		request = new MockHttpServletRequest("GET", "/action/api.js");
		response = new MockHttpServletResponse();
		apiController.api("Ext.ns", "actionns", "REMOTING_API", "POLLING_URLS", "group1,unknown", false, null, request,
				response);
		compare(response, group1Apis("actionns"), "Ext.ns", "REMOTING_API", "POLLING_URLS");
	}

	@Test
	public void testInterfaceGroup() throws IOException {
		MockHttpServletRequest request = new MockHttpServletRequest("GET", "/action/api-debug.js");
		MockHttpServletResponse response = new MockHttpServletResponse();
		apiController.api("test", "", "TEST_REMOTING_API", "TEST_POLLING_URLS", "interface", false, null, request,
				response);
		compare(response, interfaceApis(null), "test", "TEST_REMOTING_API", "TEST_POLLING_URLS");

		request = new MockHttpServletRequest("GET", "/action/api.js");
		response = new MockHttpServletResponse();
		apiController.api("test", null, "TEST_REMOTING_API", "TEST_POLLING_URLS", "interface", false, null, request,
				response);
		compare(response, interfaceApis(null), "test", "TEST_REMOTING_API", "TEST_POLLING_URLS");
	}

	@Test
	public void testNoApiNs() throws IOException {
		MockHttpServletRequest request = new MockHttpServletRequest("GET", "/action/api-debug.js");
		MockHttpServletResponse response = new MockHttpServletResponse();
		apiController.api("", "", "TEST_REMOTING_API", "TEST_POLLING_URLS", "group4", false, null, request, response);
		compare(response, group4Apis(null), null, "TEST_REMOTING_API", "TEST_POLLING_URLS");

		request = new MockHttpServletRequest("GET", "/action/api.js");
		response = new MockHttpServletResponse();
		apiController.api("", null, "TEST_REMOTING_API", "TEST_POLLING_URLS", "group4", false, null, request, response);
		compare(response, group4Apis(null), null, "TEST_REMOTING_API", "TEST_POLLING_URLS");
	}

	@Test
	public void testFullRouterUrl() throws IOException {
		MockHttpServletRequest request = new MockHttpServletRequest("GET", "/action/api-debug.js");
		MockHttpServletResponse response = new MockHttpServletResponse();
		apiController.api("apiNs", "", "TEST_RMT_API", "TEST_POLL_URLS", "group2", true, null, request, response);
		compare(response, group2Apis(null, "http://localhost:80/action/router"), "apiNs", "TEST_RMT_API",
				"TEST_POLL_URLS");

		request = new MockHttpServletRequest("GET", "/action/api.js");
		response = new MockHttpServletResponse();
		apiController.api("apiNs", null, "TEST_RMT_API", "TEST_POLL_URLS", "group2", true, null, request, response);
		compare(response, group2Apis(null, "http://localhost:80/action/router"), "apiNs", "TEST_RMT_API",
				"TEST_POLL_URLS");
	}

	@Test
	public void testFormat() throws IOException {

		MockHttpServletRequest request = new MockHttpServletRequest("GET", "/action/api-debug.js");
		MockHttpServletResponse response = new MockHttpServletResponse();
		apiController.api("apiNs", "", "TEST_RMT_API", "TEST_POLL_URLS", "group2", false, "json", request, response);
		compareJson(response, group2Apis(null, "http://localhost:80/action/router"), "apiNs", "TEST_RMT_API");

		request = new MockHttpServletRequest("GET", "/action/api.js");
		response = new MockHttpServletResponse();
		apiController.api("", "ns", "TEST_RMT_API", "TEST_POLL_URLS", "group2", true, "json", request, response);
		compareJson(response, group2Apis("ns", "http://localhost:80/action/router"), "", "TEST_RMT_API");

	}

	private RemotingApi noApis(final String namespace) {
		RemotingApi remotingApi = new RemotingApi("/action/router", namespace);
		return remotingApi;
	}

	static RemotingApi group1Apis(final String namespace) {
		RemotingApi remotingApi = new RemotingApi("/action/router", namespace);
		remotingApi.addAction("remoteProviderSimple", new Action("method1", 0, false));
		remotingApi.addAction("remoteProviderTreeLoad", new Action("method1", 1, false));
		return remotingApi;
	}

	private static RemotingApi group2Apis(final String namespace, final String url) {
		RemotingApi remotingApi = new RemotingApi(url, namespace);
		remotingApi.addAction("remoteProviderSimple", new Action("method3", 3, false));
		remotingApi.addAction("remoteProviderSimple", new Action("method5", 1, false));
		remotingApi.addAction("remoteProviderStoreRead", new Action("method6", 1, false));
		remotingApi.addAction("remoteProviderStoreRead", new Action("method7", 1, false));
		remotingApi.addAction("remoteProviderStoreModify", new Action("update4", 1, false));
		remotingApi.addAction("remoteProviderStoreModifyInterface", new Action("update4", 1, false));
		remotingApi.addAction("remoteProviderStoreModifySingle", new Action("update4", 1, false));
		remotingApi.addAction("formInfoController", new Action("upload", 0, true));
		remotingApi.addPollingProvider(new PollingProvider("pollProvider", "handleMessage1", "message1"));
		remotingApi.addPollingProvider(new PollingProvider("pollProvider", "handleMessage2", "message2"));
		remotingApi.addPollingProvider(new PollingProvider("pollProvider", "message6", "message6"));
		return remotingApi;
	}

	private RemotingApi group1and2Apis(final String namespace) {
		RemotingApi remotingApi = new RemotingApi("/action/router", namespace);
		remotingApi.addAction("remoteProviderSimple", new Action("method1", 0, false));
		remotingApi.addAction("remoteProviderTreeLoad", new Action("method1", 1, false));

		remotingApi.addAction("remoteProviderSimple", new Action("method3", 3, false));
		remotingApi.addAction("remoteProviderSimple", new Action("method5", 1, false));
		remotingApi.addAction("remoteProviderStoreRead", new Action("method6", 1, false));
		remotingApi.addAction("remoteProviderStoreRead", new Action("method7", 1, false));
		remotingApi.addAction("remoteProviderStoreModify", new Action("update4", 1, false));
		remotingApi.addAction("remoteProviderStoreModifyInterface", new Action("update4", 1, false));
		remotingApi.addAction("remoteProviderStoreModifySingle", new Action("update4", 1, false));
		remotingApi.addAction("formInfoController", new Action("upload", 0, true));
		remotingApi.addPollingProvider(new PollingProvider("pollProvider", "handleMessage1", "message1"));
		remotingApi.addPollingProvider(new PollingProvider("pollProvider", "handleMessage2", "message2"));
		remotingApi.addPollingProvider(new PollingProvider("pollProvider", "message6", "message6"));
		return remotingApi;
	}

	static RemotingApi group2Apis(final String namespace) {
		return group2Apis(namespace, "/action/router");
	}

	private RemotingApi group3Apis(final String namespace) {
		RemotingApi remotingApi = new RemotingApi("/action/router", namespace);
		remotingApi.addAction("remoteProviderSimple", new Action("method5", 1, false));
		remotingApi.addAction("remoteProviderSimple", new Action("method9", 0, false));
		remotingApi.addAction("remoteProviderStoreRead", new Action("method5", 1, false));
		remotingApi.addAction("remoteProviderStoreModify", new Action("destroy", 1, false));
		remotingApi.addAction("remoteProviderStoreModifySingle", new Action("destroy", 1, false));
		remotingApi.addAction("remoteProviderFormLoad", new Action("method1", 1, false));
		remotingApi.addAction("remoteProviderFormLoad", new Action("method5", 1, false));
		remotingApi.addAction("formInfoController", new Action("updateInfo", 0, true));
		remotingApi.addAction("formInfoController2", new Action("updateInfo1", 0, true));
		remotingApi.addAction("formInfoController2", new Action("updateInfo2", 0, true));
		remotingApi.addAction("remoteProviderTreeLoad", new Action("method3", 1, false));
		remotingApi.addPollingProvider(new PollingProvider("pollProvider", "handleMessage5", "message5"));
		return remotingApi;
	}

	private RemotingApi group4Apis(final String namespace) {
		RemotingApi remotingApi = new RemotingApi("/action/router", namespace);
		remotingApi.addPollingProvider(new PollingProvider("pollProvider", "handleMessage3", "message3"));
		return remotingApi;
	}

	private RemotingApi interfaceApis(final String namespace) {
		RemotingApi remotingApi = new RemotingApi("/action/router", namespace);
		remotingApi.addAction("remoteProviderImplementation", new Action("storeRead", 1, false));
		remotingApi.addAction("remoteProviderImplementation", new Action("method2", 0, false));
		remotingApi.addAction("remoteProviderImplementation", new Action("method3", 3, false));
		return remotingApi;
	}

	private RemotingApi allApis(final String namespace) {
		RemotingApi remotingApi = new RemotingApi("/action/router", namespace);
		remotingApi.addAction("remoteProviderSimple", new Action("method1", 0, false));
		remotingApi.addAction("remoteProviderSimple", new Action("method2", 0, false));
		remotingApi.addAction("remoteProviderSimple", new Action("method3", 3, false));
		remotingApi.addAction("remoteProviderSimple", new Action("method4b", 3, false));
		remotingApi.addAction("remoteProviderSimple", new Action("method5", 1, false));
		remotingApi.addAction("remoteProviderSimple", new Action("method6", 2, false));
		remotingApi.addAction("remoteProviderSimple", new Action("method7", 0, false));
		remotingApi.addAction("remoteProviderSimple", new Action("method8", 1, false));
		remotingApi.addAction("remoteProviderSimple", new Action("method9", 0, false));
		remotingApi.addAction("remoteProviderSimple", new Action("method10", 9, false));
		remotingApi.addAction("remoteProviderSimple", new Action("method11", 0, false));
		remotingApi.addAction("remoteProviderSimple", new Action("method11b", 0, false));
		remotingApi.addAction("remoteProviderSimple", new Action("method12", 1, false));
		remotingApi.addAction("remoteProviderSimple", new Action("method13", 9, false));
		remotingApi.addAction("remoteProviderSimple", new Action("method14", 4, false));

		remotingApi.addAction("remoteProviderSimple", new Action("method15", 2, false));
		remotingApi.addAction("remoteProviderSimple", new Action("method16", 1, false));
		remotingApi.addAction("remoteProviderSimple", new Action("method17", 0, false));
		remotingApi.addAction("remoteProviderSimple", new Action("method18", 0, false));
		remotingApi.addAction("remoteProviderSimple", new Action("method19", 1, false));
		remotingApi.addAction("remoteProviderSimple", new Action("method20", 0, false));

		remotingApi.addAction("remoteProviderStoreRead", new Action("method1", 1, false));
		remotingApi.addAction("remoteProviderStoreRead", new Action("method2", 1, false));
		remotingApi.addAction("remoteProviderStoreRead", new Action("method3", 1, false));
		remotingApi.addAction("remoteProviderStoreRead", new Action("method4", 1, false));
		remotingApi.addAction("remoteProviderStoreRead", new Action("method5", 1, false));
		remotingApi.addAction("remoteProviderStoreRead", new Action("method6", 1, false));
		remotingApi.addAction("remoteProviderStoreRead", new Action("method7", 1, false));
		remotingApi.addAction("remoteProviderStoreRead", new Action("method8", 1, false));
		remotingApi.addAction("remoteProviderStoreRead", new Action("methodFilter", 1, false));
		remotingApi.addAction("remoteProviderStoreRead", new Action("methodMetadata", 1, false));

		remotingApi.addAction("remoteProviderStoreModify", new Action("create1", 1, false));
		remotingApi.addAction("remoteProviderStoreModify", new Action("create2", 1, false));
		remotingApi.addAction("remoteProviderStoreModify", new Action("update1", 1, false));
		remotingApi.addAction("remoteProviderStoreModify", new Action("update2", 1, false));
		remotingApi.addAction("remoteProviderStoreModify", new Action("update3", 1, false));
		remotingApi.addAction("remoteProviderStoreModify", new Action("update4", 1, false));
		remotingApi.addAction("remoteProviderStoreModify", new Action("destroy", 1, false));

		remotingApi.addAction("remoteProviderStoreModifyInterface", new Action("create1", 1, false));
		remotingApi.addAction("remoteProviderStoreModifyInterface", new Action("create2", 1, false));
		remotingApi.addAction("remoteProviderStoreModifyInterface", new Action("update1", 1, false));
		remotingApi.addAction("remoteProviderStoreModifyInterface", new Action("update2", 1, false));
		remotingApi.addAction("remoteProviderStoreModifyInterface", new Action("update3", 1, false));
		remotingApi.addAction("remoteProviderStoreModifyInterface", new Action("update4", 1, false));

		remotingApi.addAction("remoteProviderStoreModifySingle", new Action("create1", 1, false));
		remotingApi.addAction("remoteProviderStoreModifySingle", new Action("create2", 1, false));
		remotingApi.addAction("remoteProviderStoreModifySingle", new Action("update1", 1, false));
		remotingApi.addAction("remoteProviderStoreModifySingle", new Action("update2", 1, false));
		remotingApi.addAction("remoteProviderStoreModifySingle", new Action("update3", 1, false));
		remotingApi.addAction("remoteProviderStoreModifySingle", new Action("update4", 1, false));
		remotingApi.addAction("remoteProviderStoreModifySingle", new Action("destroy", 1, false));

		remotingApi.addAction("remoteProviderFormLoad", new Action("method1", 1, false));
		remotingApi.addAction("remoteProviderFormLoad", new Action("method2", 1, false));
		remotingApi.addAction("remoteProviderFormLoad", new Action("method3", 1, false));
		remotingApi.addAction("remoteProviderFormLoad", new Action("method4", 1, false));
		remotingApi.addAction("remoteProviderFormLoad", new Action("method5", 1, false));
		remotingApi.addAction("remoteProviderFormLoad", new Action("method6", 1, false));
		remotingApi.addAction("remoteProviderFormLoad", new Action("method7", 1, false));

		remotingApi.addAction("formInfoController", new Action("updateInfo", 0, true));
		remotingApi.addAction("formInfoController", new Action("upload", 0, true));

		remotingApi.addAction("formInfoController2", new Action("updateInfo1", 0, true));
		remotingApi.addAction("formInfoController2", new Action("updateInfo2", 0, true));

		remotingApi.addAction("remoteProviderTreeLoad", new Action("method1", 1, false));
		remotingApi.addAction("remoteProviderTreeLoad", new Action("method2", 1, false));
		remotingApi.addAction("remoteProviderTreeLoad", new Action("method3", 1, false));
		remotingApi.addAction("remoteProviderTreeLoad", new Action("method4", 1, false));

		remotingApi.addAction("remoteProviderImplementation", new Action("storeRead", 1, false));
		remotingApi.addAction("remoteProviderImplementation", new Action("method2", 0, false));
		remotingApi.addAction("remoteProviderImplementation", new Action("method3", 3, false));

		remotingApi.addAction("remoteProviderSimpleNamed", new Action("method1", new ArrayList()));
		remotingApi.addAction("remoteProviderSimpleNamed", new Action("method2", Arrays.asList("i", "d", "s")));
		remotingApi.addAction("remoteProviderSimpleNamed", new Action("method3", Arrays.asList("userName")));
		remotingApi.addAction("remoteProviderSimpleNamed", new Action("method4", Arrays.asList("a", "b")));
		remotingApi.addAction("remoteProviderSimpleNamed", new Action("method5", Arrays.asList("d")));
		remotingApi.addAction("remoteProviderSimpleNamed", new Action("method6", new ArrayList()));
		remotingApi.addAction(
				"remoteProviderSimpleNamed",
				new Action("method7", Arrays.asList("flag", "aCharacter", "workflow", "aInt", "aLong", "aDouble",
						"aFloat", "aShort", "aByte")));
		remotingApi.addAction("remoteProviderSimpleNamed", new Action("method9", Arrays.asList("aRow")));
		remotingApi.addAction(
				"remoteProviderSimpleNamed",
				new Action("method10", Arrays.asList("flag", "aCharacter", "workflow", "aInt", "aLong", "aDouble",
						"aFloat", "aShort", "aByte")));

		remotingApi.addAction("remoteProviderSimpleNamed",
				new Action("method11", Arrays.asList("endDate", "normalParameter", "aDate", "percent")));

		remotingApi.addAction("remoteProviderSimpleNamed",
				new Action("methodRP1", Arrays.asList("lastName", "theAge", "active")));
		remotingApi.addAction("remoteProviderSimpleNamed",
				new Action("methodRP2", Arrays.asList("lastName", "theAge", "active")));
		remotingApi.addAction("remoteProviderSimpleNamed",
				new Action("methodRP3", Arrays.asList("lastName", "theAge", "active")));

		remotingApi.addPollingProvider(new PollingProvider("pollProvider", "handleMessage1", "message1"));
		remotingApi.addPollingProvider(new PollingProvider("pollProvider", "handleMessage2", "message2"));
		remotingApi.addPollingProvider(new PollingProvider("pollProvider", "handleMessage3", "message3"));
		remotingApi.addPollingProvider(new PollingProvider("pollProvider", "handleMessage4", "message4"));
		remotingApi.addPollingProvider(new PollingProvider("pollProvider", "handleMessage5", "message5"));
		remotingApi.addPollingProvider(new PollingProvider("pollProvider", "message6", "message6"));
		remotingApi.addPollingProvider(new PollingProvider("pollProvider", "message7", "message7"));
		remotingApi.addPollingProvider(new PollingProvider("pollProvider", "message8", "message8"));
		remotingApi.addPollingProvider(new PollingProvider("pollProvider", "message9", "message9"));
		remotingApi.addPollingProvider(new PollingProvider("pollProvider", "message10", "message10"));
		remotingApi.addPollingProvider(new PollingProvider("pollProvider", "message11", "message11"));
		remotingApi.addPollingProvider(new PollingProvider("pollProvider", "message12", "message12"));

		return remotingApi;
	}

	private void compareJson(final MockHttpServletResponse response, final RemotingApi remotingApi, final String apiNs,
			final String remotingApiVar) throws JsonParseException, JsonMappingException, IOException {
		String content = response.getContentAsString();
		assertThat(response.getContentType()).isEqualTo("application/json;charset=UTF-8");
		assertThat(content).isNotEmpty();

		Map<String, Object> rootAsMap = ControllerUtil.readValue(content, Map.class);

		if (remotingApi.getNamespace() == null) {
			assertThat(rootAsMap).hasSize(4);
		} else {
			assertThat(rootAsMap).hasSize(5);
			assertThat(rootAsMap.get("namespace")).isEqualTo(remotingApi.getNamespace());
		}

		assertThat(rootAsMap.get("url")).isEqualTo(remotingApi.getUrl());
		assertThat(rootAsMap.get("type")).isEqualTo("remoting");
		if (StringUtils.hasText(apiNs)) {
			assertThat(rootAsMap.get("descriptor")).isEqualTo(apiNs + "." + remotingApiVar);
		} else {
			assertThat(rootAsMap.get("descriptor")).isEqualTo(remotingApiVar);
		}
		assertThat(rootAsMap.containsKey("actions")).isTrue();

		if (remotingApi.getNamespace() != null) {
			assertThat(rootAsMap.get("namespace")).isEqualTo(remotingApi.getNamespace());
		}

		Map<String, Object> beans = (Map<String, Object>) rootAsMap.get("actions");

		assertThat(beans).hasSize(remotingApi.getActions().size());
		for (String beanName : remotingApi.getActions().keySet()) {
			List<Map<String, Object>> actions = (List<Map<String, Object>>) beans.get(beanName);
			List<Action> expectedActions = remotingApi.getActions().get(beanName);
			compare(expectedActions, actions);
		}

	}

	static void compare(final MockHttpServletResponse response, final RemotingApi remotingApi, final String apiNs,
			final String remotingApiVar, final String pollingUrlsVar) throws JsonParseException, JsonMappingException,
			IOException {
		compare(response, remotingApi, apiNs, remotingApiVar, pollingUrlsVar, null);
	}

	static void compare(final MockHttpServletResponse response, final RemotingApi remotingApi, final String apiNs,
			final String remotingApiVar, final String pollingUrlsVar, final Configuration config)
			throws JsonParseException, JsonMappingException, IOException {
		String content = response.getContentAsString();
		content = content.replace(";", ";\n");
		content = content.replace("{", "{\n");
		content = content.replace("}", "}\n");

		assertThat(response.getContentType()).isEqualTo("application/x-javascript");
		assertThat(content).isNotEmpty();

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

		int noOfconfigOptions = 0;
		if (config != null) {
			if (config.getTimeout() != null) {
				noOfconfigOptions++;
			}
			if (config.getEnableBuffer() != null) {
				noOfconfigOptions++;
			}
			if (config.getMaxRetries() != null) {
				noOfconfigOptions++;
			}
		}

		Map<String, Object> rootAsMap = ControllerUtil.readValue(remotingJson, Map.class);
		if (remotingApi.getNamespace() == null) {
			if (3 + noOfconfigOptions != rootAsMap.size()) {
				for (String key : rootAsMap.keySet()) {
					System.out.println(key + "->" + rootAsMap.get(key));
				}
			}
			assertThat(rootAsMap).hasSize(3 + noOfconfigOptions);
		} else {
			if (4 + noOfconfigOptions != rootAsMap.size()) {
				System.out.println("NOOFCONFIG: " + noOfconfigOptions);
				for (String key : rootAsMap.keySet()) {
					System.out.println(key + "->" + rootAsMap.get(key));
				}
			}
			assertThat(rootAsMap).hasSize(4 + noOfconfigOptions);
		}

		assertThat(rootAsMap.get("url")).isEqualTo(remotingApi.getUrl());
		assertThat(rootAsMap.get("type")).isEqualTo("remoting");
		assertThat(rootAsMap.containsKey("actions")).isTrue();

		if (remotingApi.getNamespace() != null) {
			assertThat(rootAsMap.get("namespace")).isEqualTo(remotingApi.getNamespace());
		}

		if (config != null) {
			if (config.getTimeout() != null) {
				assertThat(rootAsMap.get("timeout")).isEqualTo(config.getTimeout());
			} else {
				assertThat(rootAsMap.get("timeout")).isNull();
			}

			if (config.getEnableBuffer() != null) {
				assertThat(rootAsMap.get("enableBuffer")).isEqualTo(config.getEnableBuffer());
			} else {
				assertThat(rootAsMap.get("enableBuffer")).isNull();
			}

			if (config.getMaxRetries() != null) {
				assertThat(rootAsMap.get("maxRetries")).isEqualTo(config.getMaxRetries());
			} else {
				assertThat(rootAsMap.get("maxRetries")).isNull();
			}
		} else {
			assertThat(rootAsMap.get("timeout")).isNull();
			assertThat(rootAsMap.get("enableBuffer")).isNull();
			assertThat(rootAsMap.get("maxRetries")).isNull();
		}

		Map<String, Object> beans = (Map<String, Object>) rootAsMap.get("actions");

		assertThat(beans.size()).isEqualTo(remotingApi.getActions().size());
		for (String beanName : remotingApi.getActions().keySet()) {
			List<Map<String, Object>> actions = (List<Map<String, Object>>) beans.get(beanName);
			List<Action> expectedActions = remotingApi.getActions().get(beanName);
			compare(expectedActions, actions);
		}

		if (!remotingApi.getPollingProviders().isEmpty()) {
			Map<String, Object> pollingMap = ControllerUtil.readValue(pollingJson, Map.class);
			assertThat(pollingMap).hasSize(remotingApi.getPollingProviders().size());
			for (PollingProvider pp : remotingApi.getPollingProviders()) {
				String url = (String) pollingMap.get(pp.getEvent());
				assertThat(url).isNotNull();
				assertEquals(String.format("%s/%s/%s/%s", remotingApi.getUrl().replace("router", "poll"),
						pp.getBeanName(), pp.getMethod(), pp.getEvent()), url);
			}
		}
	}

	private static void compare(final List<Action> expectedActions, final List<Map<String, Object>> actions) {
		assertThat(actions).hasSize(expectedActions.size());
		for (Action expectedAction : expectedActions) {
			Map<String, Object> action = null;
			for (Map<String, Object> map : actions) {
				if (map.get("name").equals(expectedAction.getName())) {
					action = map;
					break;
				}
			}
			assertThat(action).isNotNull();
			assertThat(action.get("name")).isEqualTo(expectedAction.getName());
			assertThat(action.get("len")).isEqualTo(expectedAction.getLen());
			if (expectedAction.isFormHandler() != null && expectedAction.isFormHandler()) {
				assertThat(action.get("formHandler")).isEqualTo(expectedAction.isFormHandler());
			} else {
				assertThat(action.containsKey("formHandler")).isFalse();
			}

			List<String> params = (List<String>) action.get("params");
			assertTrue((params != null && expectedAction.getParams() != null)
					|| (params == null && expectedAction.getParams() == null));

			if (expectedAction.getParams() != null) {
				assertThat(params).hasSize(expectedAction.getParams().size());
				for (String param : expectedAction.getParams()) {
					assertThat(params.contains(param)).isTrue();
				}
			}
		}
	}

	private static int assertContains(final String extNsLine, final String[] lines) {
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
