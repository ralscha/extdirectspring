/**
 * Copyright 2010-2014 Ralph Schaer <ralphschaer@gmail.com>
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

import static org.fest.assertions.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.util.StringUtils;
import org.springframework.web.context.WebApplicationContext;

import ch.ralscha.extdirectspring.bean.api.Action;
import ch.ralscha.extdirectspring.bean.api.PollingProvider;
import ch.ralscha.extdirectspring.bean.api.RemotingApi;
import ch.ralscha.extdirectspring.util.ApiCache;
import ch.ralscha.extdirectspring.util.MethodInfoCache;

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration("classpath:/testApplicationContext.xml")
public class ApiControllerTest {

	@Autowired
	private WebApplicationContext wac;

	private MockMvc mockMvc;

	@Autowired
	private ConfigurationService configurationService;

	@Autowired
	private MethodInfoCache methodInfoCache;

	@Autowired
	private ApiCache apiCache;

	@Before
	public void setupApiController() throws Exception {
		methodInfoCache.clear();
		apiCache.clear();
		wac.publishEvent(new ContextRefreshedEvent(wac));

		Configuration config = new Configuration();
		ReflectionTestUtils.setField(configurationService, "configuration", config);
		configurationService.afterPropertiesSet();

		mockMvc = MockMvcBuilders.webAppContextSetup(wac).build();
	}

	@Test
	public void testNoActionNamespaceDebugDefaultConfig() throws Exception {
		ApiRequestParams params = ApiRequestParams.builder().apiNs("test")
				.remotingApiVar("remotingApiVar").pollingUrlsVar("pollingUrlsVar")
				.sseVar("sseVar").build();
		runTest(mockMvc, params, allApis(null));
	}

	@Test
	public void testNoActionNamespaceDebugCustomConfig() throws Exception {
		Configuration config = new Configuration();
		config.setEnableBuffer(10);
		config.setMaxRetries(2);
		config.setTimeout(12000);
		ReflectionTestUtils.setField(configurationService, "configuration", config);
		configurationService.afterPropertiesSet();

		ApiRequestParams params = ApiRequestParams.builder().apiNs("testC")
				.remotingApiVar("remotingApiV").pollingUrlsVar("pollingUrlsV")
				.sseVar("sseV").configuration(config).build();
		runTest(mockMvc, params, allApis(null));
	}

	@Test
	public void testWithActionNamespaceDefaultConfig() throws Exception {
		ApiRequestParams params = ApiRequestParams.builder().apiNs("Ext.ns")
				.actionNs("actionns").remotingApiVar("TEST_REMOTING_API")
				.pollingUrlsVar("TEST_POLLING_URLS").sseVar("TEST_SSE").build();
		runTest(mockMvc, params, allApis("actionns"));
	}

	@Test
	public void testWithActionNamespaceCustomConfig() throws Exception {

		Configuration config = new Configuration();
		config.setEnableBuffer(Boolean.FALSE);
		config.setTimeout(10000);
		ReflectionTestUtils.setField(configurationService, "configuration", config);
		configurationService.afterPropertiesSet();

		ApiRequestParams params = ApiRequestParams.builder().apiNs("Ext.ns")
				.actionNs("actionns").remotingApiVar("TEST_REMOTING_API")
				.pollingUrlsVar("TEST_POLLING_URLS").sseVar("TEST_SSE")
				.configuration(config).build();
		runTest(mockMvc, params, allApis("actionns"));
	}

	@Test
	public void testEmptyGroupDefaultConfig() throws Exception {
		ApiRequestParams params = ApiRequestParams.builder().apiNs("test")
				.remotingApiVar("TEST_REMOTING_API").pollingUrlsVar("TEST_POLLING_URLS")
				.group("").sseVar("TEST_SSE").build();
		runTest(mockMvc, params, emptyGroupApis(null));
	}

	@Test
	public void testBlankStringGroupDefaultConfig() throws Exception {
		ApiRequestParams params = ApiRequestParams.builder().apiNs("test")
				.remotingApiVar("TEST_REMOTING_API").pollingUrlsVar("TEST_POLLING_URLS")
				.group("     ").sseVar("TEST_SSE").build();
		runTest(mockMvc, params, emptyGroupApis(null));
	}

	@Test
	public void testEmptyGroupCustomConfig() throws Exception {
		Configuration config = new Configuration();
		config.setEnableBuffer(Boolean.TRUE);
		config.setTimeout(33333);
		ReflectionTestUtils.setField(configurationService, "configuration", config);
		configurationService.afterPropertiesSet();

		ApiRequestParams params = ApiRequestParams.builder().apiNs("test")
				.remotingApiVar("TEST_REMOTING_API").pollingUrlsVar("TEST_POLLING_URLS")
				.group("").sseVar("TEST_SSE").configuration(config).build();
		runTest(mockMvc, params, emptyGroupApis(null));
	}

	@Test
	public void testBlankStringGroupCustomConfig() throws Exception {
		Configuration config = new Configuration();
		config.setEnableBuffer(Boolean.TRUE);
		config.setTimeout(33333);
		ReflectionTestUtils.setField(configurationService, "configuration", config);
		configurationService.afterPropertiesSet();

		ApiRequestParams params = ApiRequestParams.builder().apiNs("test")
				.remotingApiVar("TEST_REMOTING_API").pollingUrlsVar("TEST_POLLING_URLS")
				.group("        ").sseVar("TEST_SSE").configuration(config).build();
		runTest(mockMvc, params, emptyGroupApis(null));
	}

	@Test
	public void testUnknownGroupDefaultConfig() throws Exception {

		ApiRequestParams params = ApiRequestParams.builder().apiNs("test")
				.remotingApiVar("TEST_REMOTING_API").pollingUrlsVar("TEST_POLLING_URLS")
				.group("xy").sseVar("TEST_SSE").build();
		runTest(mockMvc, params, noApis(null));
	}

	@Test
	public void testUnknownGroupCustomConfig() throws Exception {
		Configuration config = new Configuration();
		config.setEnableBuffer(Boolean.TRUE);
		ReflectionTestUtils.setField(configurationService, "configuration", config);
		configurationService.afterPropertiesSet();

		ApiRequestParams params = ApiRequestParams.builder().apiNs("test")
				.remotingApiVar("TEST_REMOTING_API").pollingUrlsVar("TEST_POLLING_URLS")
				.group("xy").sseVar("TEST_SSE").configuration(config).build();
		runTest(mockMvc, params, noApis(null));
	}

	@Test
	public void testGroup1() throws Exception {
		testGroup1(null, null);
		testGroup1(null, null);
		apiCache.clear();
		testGroup1(null, "-1.0.0");
		apiCache.clear();
		testGroup1(null, "-fingerprinted");
	}

	@Test
	public void testGroup1WithConfig() throws Exception {
		Configuration config = new Configuration();
		config.setTimeout(12000);
		ReflectionTestUtils.setField(configurationService, "configuration", config);
		configurationService.afterPropertiesSet();

		testGroup1(config, null);
		testGroup1(config, null);
		apiCache.clear();
		testGroup1(config, "-1.0.0");
	}

	private void testGroup1(Configuration config, String fingerprint) throws Exception {
		ApiRequestParams params = ApiRequestParams.builder().apiNs("Ext.ns")
				.actionNs("actionns").group("group1").configuration(config).build();
		doTest("/api-debug-doc.js", params, group1Apis("actionns"));
		doTest("/api-debug.js", params, group1Apis("actionns"));

		if (fingerprint == null) {
			doTest("/api.js", params, group1Apis("actionns"));
		}
		else {
			MvcResult result = doTest("/api" + fingerprint + ".js", params,
					group1Apis("actionns"));

			MockHttpServletResponse response = result.getResponse();

			assertThat(response.getHeaderNames()).hasSize(5);
			assertThat(response.getHeader("ETag")).isNotNull();
			assertThat(response.getHeader("Cache-Control")).isEqualTo(
					"public, max-age=" + 6 * 30 * 24 * 60 * 60);

			Long expiresMillis = (Long) response.getHeaderValue("Expires");
			DateTime expires = new DateTime(expiresMillis, DateTimeZone.UTC);
			DateTime inSixMonths = DateTime.now(DateTimeZone.UTC).plusSeconds(
					6 * 30 * 24 * 60 * 60);
			assertThat(expires.getYear()).isEqualTo(inSixMonths.getYear());
			assertThat(expires.getMonthOfYear()).isEqualTo(inSixMonths.getMonthOfYear());
			assertThat(expires.getDayOfMonth()).isEqualTo(inSixMonths.getDayOfMonth());
			assertThat(expires.getHourOfDay()).isEqualTo(inSixMonths.getHourOfDay());
			assertThat(expires.getMinuteOfDay()).isEqualTo(inSixMonths.getMinuteOfDay());
		}
	}

	@Test
	public void testGroup2() throws Exception {
		ApiRequestParams params = ApiRequestParams.builder().apiNs("test")
				.remotingApiVar("TEST_REMOTING_API").pollingUrlsVar("TEST_POLLING_URLS")
				.group("group2").sseVar("TEST_SSE").build();
		runTest(mockMvc, params, group2Apis(null));
		runTest(mockMvc, params, group2Apis(null));
	}

	@Test
	public void testGroup3() throws Exception {
		ApiRequestParams params = ApiRequestParams.builder().apiNs("Extns")
				.actionNs("ns").remotingApiVar("RAPI").pollingUrlsVar("PURLS")
				.sseVar("ES").group("group3").build();
		runTest(mockMvc, params, group3Apis("ns"));
		runTest(mockMvc, params, group3Apis("ns"));
	}

	@Test
	public void testGroup4() throws Exception {
		ApiRequestParams params = ApiRequestParams.builder().apiNs("test").actionNs("")
				.remotingApiVar("TEST_REMOTING_API").pollingUrlsVar("TEST_POLLING_URLS")
				.sseVar("TEST_SSE").group("group4").build();
		runTest(mockMvc, params, group4Apis(null));

		params = ApiRequestParams.builder().apiNs("test")
				.remotingApiVar("TEST_REMOTING_API").pollingUrlsVar("TEST_POLLING_URLS")
				.sseVar("TEST_SSE").group("group4").build();
		runTest(mockMvc, params, group4Apis(null));
	}

	@Test
	public void testGroup1and2() throws Exception {
		ApiRequestParams params = ApiRequestParams.builder().apiNs("Ext.ns")
				.actionNs("actionns").group("group1,group2").build();
		runTest(mockMvc, params, group1and2Apis("actionns"));
	}

	@Test
	public void testGroup1andUnknown() throws Exception {
		ApiRequestParams params = ApiRequestParams.builder().apiNs("Ext.ns")
				.actionNs("actionns").group("group1,unknown").build();
		runTest(mockMvc, params, group1Apis("actionns"));
	}

	@Test
	public void testInterfaceGroup() throws Exception {
		ApiRequestParams params = ApiRequestParams.builder().apiNs("test").actionNs("")
				.remotingApiVar("TEST_REMOTING_API").pollingUrlsVar("TEST_POLLING_URLS")
				.sseVar("TEST_SSE").group("interface").build();
		runTest(mockMvc, params, interfaceApis(null));

		params = ApiRequestParams.builder().apiNs("test")
				.remotingApiVar("TEST_REMOTING_API").pollingUrlsVar("TEST_POLLING_URLS")
				.sseVar("TEST_SSE").group("interface").build();
		runTest(mockMvc, params, interfaceApis(null));
	}

	@Test
	public void testNoApiNs() throws Exception {
		ApiRequestParams params = ApiRequestParams.builder().apiNs("").actionNs("")
				.remotingApiVar("TEST_REMOTING_API").pollingUrlsVar("TEST_POLLING_URLS")
				.sseVar("TEST_SSE").group("group4").build();
		runTest(mockMvc, params, group4Apis(null));

		params = ApiRequestParams.builder().apiNs("").remotingApiVar("TEST_REMOTING_API")
				.pollingUrlsVar("TEST_POLLING_URLS").sseVar("TEST_SSE").group("group4")
				.build();
		runTest(mockMvc, params, group4Apis(null));
	}

	@Test
	public void testFullRouterUrl() throws Exception {
		ApiRequestParams params = ApiRequestParams.builder().apiNs("apiNs").actionNs("")
				.remotingApiVar("TEST_RMT_API").pollingUrlsVar("TEST_POLL_URLS")
				.sseVar("TEST_SSE").fullRouterUrl(Boolean.TRUE).group("group2").build();
		runTest(mockMvc, params, group2Apis(null, "http://localhost/router"));

		params = ApiRequestParams.builder().apiNs("apiNs").remotingApiVar("TEST_RMT_API")
				.pollingUrlsVar("TEST_POLL_URLS").sseVar("TEST_SSE")
				.fullRouterUrl(Boolean.TRUE).group("group2").build();
		runTest(mockMvc, params, group2Apis(null, "http://localhost/router"));

		params = ApiRequestParams.builder().apiNs("apiNs").actionNs("")
				.remotingApiVar("TEST_RMT_API").pollingUrlsVar("TEST_POLL_URLS")
				.sseVar("TEST_SSE").fullRouterUrl(Boolean.FALSE).group("group2").build();
		runTest(mockMvc, params, group2Apis(null, "/router"));

		params = ApiRequestParams.builder().apiNs("apiNs").remotingApiVar("TEST_RMT_API")
				.pollingUrlsVar("TEST_POLL_URLS").sseVar("TEST_SSE")
				.fullRouterUrl(Boolean.FALSE).group("group2").build();
		runTest(mockMvc, params, group2Apis(null, "/router"));
	}

	@Test
	public void testFormat() throws Exception {
		ApiRequestParams params = ApiRequestParams.builder().actionNs("").apiNs("apiNs")
				.remotingApiVar("TEST_RMT_API").pollingUrlsVar("TEST_POLL_URLS")
				.sseVar("TEST_SSE").group("group2").format("json").build();
		runTest(mockMvc, params, group2Apis(null, "http://localhost/router"));

		params = ApiRequestParams.builder().actionNs("ns").apiNs("")
				.remotingApiVar("TEST_RMT_API").pollingUrlsVar("TEST_POLL_URLS")
				.sseVar("TEST_SSE").group("group2").format("json")
				.fullRouterUrl(Boolean.TRUE).build();
		runTest(mockMvc, params, group2Apis("ns", "http://localhost/router"));
	}

	@Test
	public void testBaseRouterUrl() throws Exception {
		ApiRequestParams params = ApiRequestParams.builder().actionNs("").apiNs("an")
				.remotingApiVar("rapi").pollingUrlsVar("papi").sseVar("sseapi")
				.group("group2").baseRouterUrl("test").build();
		runTest(mockMvc, params, group2Apis(null, "test/router"));
	}

	@Test
	public void testBaseRouterUrlWithEndingSlash() throws Exception {
		ApiRequestParams params = ApiRequestParams.builder().actionNs("").apiNs("an")
				.remotingApiVar("rapi").pollingUrlsVar("papi").sseVar("sseapi")
				.group("group2").fullRouterUrl(Boolean.TRUE)
				.baseRouterUrl("service/test/").build();
		runTest(mockMvc, params, group2Apis(null, "service/test/router"));
	}

	@Test
	public void testBaseRouterUrlEmptyString() throws Exception {
		ApiRequestParams params = ApiRequestParams.builder().actionNs("").apiNs("an")
				.remotingApiVar("rapi").pollingUrlsVar("papi").sseVar("sseapi")
				.group("group2").baseRouterUrl("").build();
		runTest(mockMvc, params, group2Apis(null, "/router"));
	}

	static void runTest(MockMvc mockMvc, ApiRequestParams params, RemotingApi api)
			throws Exception {
		doTest(mockMvc, "/api-debug-doc.js", params, api);
		doTest(mockMvc, "/api-debug.js", params, api);
		doTest(mockMvc, "/api.js", params, api);
	}

	private MvcResult doTest(String url, ApiRequestParams params, RemotingApi expectedApi)
			throws Exception {
		return doTest(mockMvc, url, params, expectedApi);
	}

	public static MvcResult doTest(MockMvc mockMvc, String url, ApiRequestParams params,
			RemotingApi expectedApi) throws Exception {
		MockHttpServletRequestBuilder request = get(url).accept(MediaType.ALL)
				.characterEncoding("UTF-8");

		if (params.getApiNs() != null) {
			request.param("apiNs", params.getApiNs());
		}
		if (params.getActionNs() != null) {
			request.param("actionNs", params.getActionNs());
		}
		if (params.getFormat() != null) {
			request.param("format", params.getFormat());
		}
		if (params.getGroup() != null) {
			request.param("group", params.getGroup());
		}
		if (params.getPollingUrlsVar() != null) {
			request.param("pollingUrlsVar", params.getPollingUrlsVar());
		}
		if (params.getRemotingApiVar() != null) {
			request.param("remotingApiVar", params.getRemotingApiVar());
		}
		if (params.getSseVar() != null) {
			request.param("sseVar", params.getSseVar());
		}
		if (params.isFullRouterUrl() != null && params.isFullRouterUrl()) {
			request.param("fullRouterUrl", "true");
		}
		if (params.getBaseRouterUrl() != null) {
			request.param("baseRouterUrl", params.getBaseRouterUrl());
		}

		String contentType = "application/javascript";
		if ("json".equals(params.getFormat())) {
			contentType = "application/json;charset=UTF-8";
		}
		else if (params.getConfiguration() != null) {
			contentType = params.getConfiguration().getJsContentType();
		}

		MvcResult result = mockMvc.perform(request).andExpect(status().isOk())
				.andExpect(content().contentType(contentType)).andReturn();

		if ("json".equals(params.getFormat())) {
			compareJson(result, expectedApi, params);
		}
		else {
			compare(result, expectedApi, params);
		}

		return result;
	}

	public static RemotingApi noApis(String namespace) {
		return new RemotingApi("remoting", "/router", namespace);
	}

	static RemotingApi group1Apis(String namespace) {
		RemotingApi remotingApi = new RemotingApi("remoting", "/router", namespace);
		remotingApi.addAction("remoteProviderSimple", new Action("method1", 0,
				Boolean.FALSE));
		remotingApi.addAction("remoteProviderTreeLoad", new Action("method1", 1,
				Boolean.FALSE));
		return remotingApi;
	}

	static RemotingApi groupApisWithDoc(String namespace) {
		RemotingApi remotingApi = new RemotingApi("remoting", "/router", namespace);
		remotingApi.addAction("remoteProviderSimpleDoc", new Action("method1", 0,
				Boolean.FALSE));
		remotingApi.addAction("remoteProviderSimpleDoc", new Action("method2", 0,
				Boolean.FALSE));
		remotingApi.addAction("remoteProviderSimpleDoc", new Action("method3", 0,
				Boolean.FALSE));
		remotingApi.addAction("remoteProviderSimpleDoc", new Action("method4", 0,
				Boolean.FALSE));
		remotingApi.addAction("remoteProviderSimpleDoc", new Action("method5", 0,
				Boolean.FALSE));
		remotingApi.addAction("remoteProviderSimpleDoc", new Action("method6", 0,
				Boolean.FALSE));
		remotingApi.addAction("remoteProviderSimpleDoc", new Action("method7", 0,
				Boolean.FALSE));
		remotingApi.addAction("remoteProviderSimpleDoc", new Action("method8", 0,
				Boolean.FALSE));
		remotingApi.addAction("remoteProviderSimpleDoc", new Action("method9", 0,
				Boolean.FALSE));
		remotingApi.addAction("remoteProviderSimpleDoc", new Action("method10", 0,
				Boolean.FALSE));
		remotingApi.addAction("remoteProviderSimpleDoc", new Action("method11", 0,
				Boolean.FALSE));
		remotingApi.addAction("remoteProviderSimpleDoc", new Action("method12", 0,
				Boolean.FALSE));
		return remotingApi;
	}

	public static RemotingApi group2Apis(String namespace, String url) {
		RemotingApi remotingApi = new RemotingApi("remoting", url, namespace);
		remotingApi.addAction("remoteProviderSimple", new Action("method3", 3,
				Boolean.FALSE));
		remotingApi.addAction("remoteProviderSimple", new Action("method5", 1,
				Boolean.FALSE));
		remotingApi.addAction("remoteProviderStoreRead", new Action("method6", 1,
				Boolean.FALSE));
		remotingApi.addAction("remoteProviderStoreRead", new Action("method7", 1,
				Boolean.FALSE));
		remotingApi.addAction("remoteProviderStoreModify", new Action("update4", 1,
				Boolean.FALSE));
		remotingApi.addAction("remoteProviderStoreModifyArray", new Action("update4", 1,
				Boolean.FALSE));
		remotingApi.addAction("remoteProviderStoreModifyInterface", new Action("update4",
				1, Boolean.FALSE));
		remotingApi.addAction("remoteProviderStoreModifySingle", new Action("update4", 1,
				Boolean.FALSE));
		remotingApi
				.addAction("formInfoController", new Action("upload", 0, Boolean.TRUE));
		remotingApi.addAction("uploadService", new Action("upload", 0, Boolean.TRUE));
		remotingApi.addPollingProvider(new PollingProvider("pollProvider",
				"handleMessage1", "message1"));
		remotingApi.addPollingProvider(new PollingProvider("pollProvider",
				"handleMessage2", "message2"));
		remotingApi.addPollingProvider(new PollingProvider("pollProvider", "message6",
				"message6"));
		remotingApi.addSseProvider("sseProvider", "message1");
		remotingApi.addSseProvider("sseProvider", "message2");
		remotingApi.addSseProvider("sseProvider", "message6");
		return remotingApi;
	}

	public static RemotingApi group1and2Apis(String namespace) {
		RemotingApi remotingApi = new RemotingApi("remoting", "/router", namespace);
		remotingApi.addAction("remoteProviderSimple", new Action("method1", 0,
				Boolean.FALSE));
		remotingApi.addAction("remoteProviderTreeLoad", new Action("method1", 1,
				Boolean.FALSE));

		remotingApi.addAction("remoteProviderSimple", new Action("method3", 3,
				Boolean.FALSE));
		remotingApi.addAction("remoteProviderSimple", new Action("method5", 1,
				Boolean.FALSE));
		remotingApi.addAction("remoteProviderStoreRead", new Action("method6", 1,
				Boolean.FALSE));
		remotingApi.addAction("remoteProviderStoreRead", new Action("method7", 1,
				Boolean.FALSE));
		remotingApi.addAction("remoteProviderStoreModify", new Action("update4", 1,
				Boolean.FALSE));
		remotingApi.addAction("remoteProviderStoreModifyArray", new Action("update4", 1,
				Boolean.FALSE));
		remotingApi.addAction("remoteProviderStoreModifyInterface", new Action("update4",
				1, Boolean.FALSE));
		remotingApi.addAction("remoteProviderStoreModifySingle", new Action("update4", 1,
				Boolean.FALSE));
		remotingApi
				.addAction("formInfoController", new Action("upload", 0, Boolean.TRUE));
		remotingApi.addAction("uploadService", new Action("upload", 0, Boolean.TRUE));
		remotingApi.addPollingProvider(new PollingProvider("pollProvider",
				"handleMessage1", "message1"));
		remotingApi.addPollingProvider(new PollingProvider("pollProvider",
				"handleMessage2", "message2"));
		remotingApi.addPollingProvider(new PollingProvider("pollProvider", "message6",
				"message6"));
		remotingApi.addSseProvider("sseProvider", "message1");
		remotingApi.addSseProvider("sseProvider", "message2");
		remotingApi.addSseProvider("sseProvider", "message6");
		return remotingApi;
	}

	static RemotingApi group2Apis(String namespace) {
		return group2Apis(namespace, "/router");
	}

	public static RemotingApi group3Apis(String namespace) {
		RemotingApi remotingApi = new RemotingApi("remoting", "/router", namespace);
		remotingApi.addAction("remoteProviderSimple", new Action("method5", 1,
				Boolean.FALSE));
		remotingApi.addAction("remoteProviderSimple", new Action("method9", 0,
				Boolean.FALSE));
		remotingApi.addAction("remoteProviderStoreRead", new Action("method5", 1,
				Boolean.FALSE));
		remotingApi.addAction("remoteProviderStoreModify", new Action("destroy", 1,
				Boolean.FALSE));
		remotingApi.addAction("remoteProviderStoreModifyArray", new Action("destroy", 1,
				Boolean.FALSE));
		remotingApi.addAction("remoteProviderStoreModifySingle", new Action("destroy", 1,
				Boolean.FALSE));
		remotingApi.addAction("remoteProviderFormLoad", new Action("method1", 1,
				Boolean.FALSE));
		remotingApi.addAction("remoteProviderFormLoad", new Action("method5", 1,
				Boolean.FALSE));
		remotingApi.addAction("formInfoController", new Action("updateInfo", 0,
				Boolean.TRUE));
		remotingApi.addAction("formInfoController", new Action("updateInfoDirect", 0,
				Boolean.TRUE));
		remotingApi.addAction("formInfoController2", new Action("updateInfo1", 0,
				Boolean.TRUE));
		remotingApi.addAction("formInfoController2", new Action("updateInfo2", 0,
				Boolean.TRUE));
		remotingApi.addAction("remoteProviderTreeLoad", new Action("method3", 1,
				Boolean.FALSE));
		remotingApi.addPollingProvider(new PollingProvider("pollProvider",
				"handleMessage5", "message5"));
		remotingApi.addSseProvider("sseProvider", "message5");
		return remotingApi;
	}

	public static RemotingApi group4Apis(String namespace) {
		RemotingApi remotingApi = new RemotingApi("remoting", "/router", namespace);
		remotingApi.addPollingProvider(new PollingProvider("pollProvider",
				"handleMessage3", "message3"));
		remotingApi.addSseProvider("sseProvider", "message3");
		return remotingApi;
	}

	public static RemotingApi interfaceApis(String namespace) {
		RemotingApi remotingApi = new RemotingApi("remoting", "/router", namespace);
		remotingApi.addAction("remoteProviderImplementation", new Action("storeRead", 1,
				Boolean.FALSE));
		remotingApi.addAction("remoteProviderImplementation", new Action("method2", 0,
				Boolean.FALSE));
		remotingApi.addAction("remoteProviderImplementation", new Action("method3", 3,
				Boolean.FALSE));
		return remotingApi;
	}

	public static RemotingApi allApis(String namespace) {
		RemotingApi remotingApi = new RemotingApi("remoting", "/router", namespace);
		remotingApi.addAction("remoteProviderSimple", new Action("method1", 0,
				Boolean.FALSE));
		remotingApi.addAction("remoteProviderSimple", new Action("method2", 0,
				Boolean.FALSE));
		remotingApi.addAction("remoteProviderSimple", new Action("method3", 3,
				Boolean.FALSE));
		remotingApi.addAction("remoteProviderSimple", new Action("method4b", 3,
				Boolean.FALSE));
		remotingApi.addAction("remoteProviderSimple", new Action("method5", 1,
				Boolean.FALSE));
		remotingApi.addAction("remoteProviderSimple", new Action("method6", 2,
				Boolean.FALSE));
		remotingApi.addAction("remoteProviderSimple", new Action("method7", 0,
				Boolean.FALSE));
		remotingApi.addAction("remoteProviderSimple", new Action("method8", 1,
				Boolean.FALSE));
		remotingApi.addAction("remoteProviderSimple", new Action("method9", 0,
				Boolean.FALSE));
		remotingApi.addAction("remoteProviderSimple", new Action("method10", 9,
				Boolean.FALSE));
		remotingApi.addAction("remoteProviderSimple", new Action("method11", 0,
				Boolean.FALSE));
		remotingApi.addAction("remoteProviderSimple", new Action("method11b", 0,
				Boolean.FALSE));
		remotingApi.addAction("remoteProviderSimple", new Action("method12", 1,
				Boolean.FALSE));
		remotingApi.addAction("remoteProviderSimple", new Action("method13", 9,
				Boolean.FALSE));
		remotingApi.addAction("remoteProviderSimple", new Action("method14", 4,
				Boolean.FALSE));

		remotingApi.addAction("remoteProviderSimple", new Action("method15", 2,
				Boolean.FALSE));
		remotingApi.addAction("remoteProviderSimple", new Action("method16", 1,
				Boolean.FALSE));
		remotingApi.addAction("remoteProviderSimple", new Action("method17", 0,
				Boolean.FALSE));
		remotingApi.addAction("remoteProviderSimple", new Action("method18", 0,
				Boolean.FALSE));
		remotingApi.addAction("remoteProviderSimple", new Action("method19", 1,
				Boolean.FALSE));
		remotingApi.addAction("remoteProviderSimple", new Action("method20", 0,
				Boolean.FALSE));
		remotingApi.addAction("remoteProviderSimple", new Action("method21", 3,
				Boolean.FALSE));
		remotingApi.addAction("remoteProviderSimple", new Action("method22", 3,
				Boolean.FALSE));
		remotingApi.addAction("remoteProviderSimple", new Action("method23", 3,
				Boolean.FALSE));
		remotingApi.addAction("remoteProviderSimple", new Action("method24", 3,
				Boolean.FALSE));
		remotingApi.addAction("remoteProviderSimple", new Action("method25", 3,
				Boolean.FALSE));
		remotingApi.addAction("remoteProviderSimple", new Action("method26", 3,
				Boolean.FALSE));
		remotingApi.addAction("remoteProviderSimple", new Action("method27", 0,
				Boolean.FALSE));
		remotingApi.addAction("remoteProviderSimple", new Action("method28", 0,
				Boolean.FALSE));
		remotingApi.addAction("remoteProviderSimple", new Action("method29", 0,
				Boolean.FALSE));
		remotingApi.addAction("remoteProviderSimple", new Action("method30", 0,
				Boolean.FALSE));
		remotingApi.addAction("remoteProviderSimple", new Action("methodWithOptional", 1,
				Boolean.FALSE));

		remotingApi.addAction("remoteProviderSimpleDoc", new Action("method1", 0,
				Boolean.FALSE));
		remotingApi.addAction("remoteProviderSimpleDoc", new Action("method2", 0,
				Boolean.FALSE));
		remotingApi.addAction("remoteProviderSimpleDoc", new Action("method3", 0,
				Boolean.FALSE));
		remotingApi.addAction("remoteProviderSimpleDoc", new Action("method4", 0,
				Boolean.FALSE));
		remotingApi.addAction("remoteProviderSimpleDoc", new Action("method5", 0,
				Boolean.FALSE));
		remotingApi.addAction("remoteProviderSimpleDoc", new Action("method6", 0,
				Boolean.FALSE));
		remotingApi.addAction("remoteProviderSimpleDoc", new Action("method7", 0,
				Boolean.FALSE));
		remotingApi.addAction("remoteProviderSimpleDoc", new Action("method8", 0,
				Boolean.FALSE));
		remotingApi.addAction("remoteProviderSimpleDoc", new Action("method9", 0,
				Boolean.FALSE));
		remotingApi.addAction("remoteProviderSimpleDoc", new Action("method10", 0,
				Boolean.FALSE));
		remotingApi.addAction("remoteProviderSimpleDoc", new Action("method11", 0,
				Boolean.FALSE));
		remotingApi.addAction("remoteProviderSimpleDoc", new Action("method12", 0,
				Boolean.FALSE));

		remotingApi.addAction("remoteProviderStoreRead", new Action("method1", 1,
				Boolean.FALSE));
		remotingApi.addAction("remoteProviderStoreRead", new Action("method2", 1,
				Boolean.FALSE));
		remotingApi.addAction("remoteProviderStoreRead", new Action("method3", 1,
				Boolean.FALSE));
		remotingApi.addAction("remoteProviderStoreRead", new Action("method4", 1,
				Boolean.FALSE));
		remotingApi.addAction("remoteProviderStoreRead", new Action("method5", 1,
				Boolean.FALSE));
		remotingApi.addAction("remoteProviderStoreRead", new Action("method6", 1,
				Boolean.FALSE));
		remotingApi.addAction("remoteProviderStoreRead", new Action("method7", 1,
				Boolean.FALSE));
		remotingApi.addAction("remoteProviderStoreRead", new Action("method8", 1,
				Boolean.FALSE));
		remotingApi.addAction("remoteProviderStoreRead", new Action("method9", 1,
				Boolean.FALSE));
		remotingApi.addAction("remoteProviderStoreRead", new Action("method10", 1,
				Boolean.FALSE));
		remotingApi.addAction("remoteProviderStoreRead", new Action("method11", 1,
				Boolean.FALSE));
		remotingApi.addAction("remoteProviderStoreRead", new Action("methodFilter", 1,
				Boolean.FALSE));
		remotingApi.addAction("remoteProviderStoreRead", new Action("methodMetadata", 1,
				Boolean.FALSE));

		remotingApi.addAction("remoteProviderStoreModify", new Action("create1", 1,
				Boolean.FALSE));
		remotingApi.addAction("remoteProviderStoreModify", new Action("create2", 1,
				Boolean.FALSE));
		remotingApi.addAction("remoteProviderStoreModify", new Action("update1", 1,
				Boolean.FALSE));
		remotingApi.addAction("remoteProviderStoreModify", new Action("update2", 1,
				Boolean.FALSE));
		remotingApi.addAction("remoteProviderStoreModify", new Action("update3", 1,
				Boolean.FALSE));
		remotingApi.addAction("remoteProviderStoreModify", new Action("update4", 1,
				Boolean.FALSE));
		remotingApi.addAction("remoteProviderStoreModify", new Action("destroy", 1,
				Boolean.FALSE));

		remotingApi.addAction("remoteProviderStoreModifyArray", new Action("create1", 1,
				Boolean.FALSE));
		remotingApi.addAction("remoteProviderStoreModifyArray", new Action("create2", 1,
				Boolean.FALSE));
		remotingApi.addAction("remoteProviderStoreModifyArray", new Action("update1", 1,
				Boolean.FALSE));
		remotingApi.addAction("remoteProviderStoreModifyArray", new Action("update2", 1,
				Boolean.FALSE));
		remotingApi.addAction("remoteProviderStoreModifyArray", new Action("update3", 1,
				Boolean.FALSE));
		remotingApi.addAction("remoteProviderStoreModifyArray", new Action("update4", 1,
				Boolean.FALSE));
		remotingApi.addAction("remoteProviderStoreModifyArray", new Action("destroy", 1,
				Boolean.FALSE));

		remotingApi.addAction("remoteProviderStoreModifyInterface", new Action("create1",
				1, Boolean.FALSE));
		remotingApi.addAction("remoteProviderStoreModifyInterface", new Action("create2",
				1, Boolean.FALSE));
		remotingApi.addAction("remoteProviderStoreModifyInterface", new Action("update1",
				1, Boolean.FALSE));
		remotingApi.addAction("remoteProviderStoreModifyInterface", new Action("update2",
				1, Boolean.FALSE));
		remotingApi.addAction("remoteProviderStoreModifyInterface", new Action("update3",
				1, Boolean.FALSE));
		remotingApi.addAction("remoteProviderStoreModifyInterface", new Action("update4",
				1, Boolean.FALSE));

		remotingApi.addAction("remoteProviderStoreModifySingle", new Action("create1", 1,
				Boolean.FALSE));
		remotingApi.addAction("remoteProviderStoreModifySingle", new Action("create2", 1,
				Boolean.FALSE));
		remotingApi.addAction("remoteProviderStoreModifySingle", new Action("update1", 1,
				Boolean.FALSE));
		remotingApi.addAction("remoteProviderStoreModifySingle", new Action("update2", 1,
				Boolean.FALSE));
		remotingApi.addAction("remoteProviderStoreModifySingle", new Action("update3", 1,
				Boolean.FALSE));
		remotingApi.addAction("remoteProviderStoreModifySingle", new Action("update4", 1,
				Boolean.FALSE));
		remotingApi.addAction("remoteProviderStoreModifySingle", new Action("destroy", 1,
				Boolean.FALSE));

		remotingApi.addAction("remoteProviderFormLoad", new Action("method1", 1,
				Boolean.FALSE));
		remotingApi.addAction("remoteProviderFormLoad", new Action("method2", 1,
				Boolean.FALSE));
		remotingApi.addAction("remoteProviderFormLoad", new Action("method3", 1,
				Boolean.FALSE));
		remotingApi.addAction("remoteProviderFormLoad", new Action("method4", 1,
				Boolean.FALSE));
		remotingApi.addAction("remoteProviderFormLoad", new Action("method5", 1,
				Boolean.FALSE));
		remotingApi.addAction("remoteProviderFormLoad", new Action("method6", 1,
				Boolean.FALSE));
		remotingApi.addAction("remoteProviderFormLoad", new Action("method7", 1,
				Boolean.FALSE));

		remotingApi.addAction("formInfoController", new Action("updateInfo", 0,
				Boolean.TRUE));
		remotingApi.addAction("formInfoController", new Action("updateInfoDirect", 0,
				Boolean.TRUE));
		remotingApi
				.addAction("formInfoController", new Action("upload", 0, Boolean.TRUE));
		remotingApi.addAction("uploadService", new Action("upload", 0, Boolean.TRUE));

		remotingApi.addAction("formInfoController3", new Action("updateInfoJson", 1,
				Boolean.FALSE));
		remotingApi.addAction("formInfoController3", new Action("updateInfoJsonDirect",
				1, Boolean.FALSE));
		remotingApi.addAction("formInfoController3", new Action(
				"updateInfoJsonDirectError", 1, Boolean.FALSE));

		remotingApi.addAction("formInfoController2", new Action("updateInfo1", 0,
				Boolean.TRUE));
		remotingApi.addAction("formInfoController2", new Action("updateInfo2", 0,
				Boolean.TRUE));

		remotingApi.addAction("remoteProviderTreeLoad", new Action("method1", 1,
				Boolean.FALSE));
		remotingApi.addAction("remoteProviderTreeLoad", new Action("method2", 1,
				Boolean.FALSE));
		remotingApi.addAction("remoteProviderTreeLoad", new Action("method3", 1,
				Boolean.FALSE));
		remotingApi.addAction("remoteProviderTreeLoad", new Action("method4", 1,
				Boolean.FALSE));
		remotingApi.addAction("remoteProviderTreeLoad", new Action("method5", 1,
				Boolean.FALSE));
		remotingApi.addAction("remoteProviderTreeLoad", new Action("method6", 1,
				Boolean.FALSE));

		remotingApi.addAction("remoteProviderImplementation", new Action("storeRead", 1,
				Boolean.FALSE));
		remotingApi.addAction("remoteProviderImplementation", new Action("method2", 0,
				Boolean.FALSE));
		remotingApi.addAction("remoteProviderImplementation", new Action("method3", 3,
				Boolean.FALSE));

		remotingApi.addAction("bookService", new Action("read", 1, Boolean.FALSE));
		remotingApi.addAction("bookService", new Action("readWithPaging", 1,
				Boolean.FALSE));
		remotingApi.addAction("bookService", new Action("update3", 1, Boolean.FALSE));
		remotingApi.addAction("bookService", new Action("update4", 1, Boolean.FALSE));
		remotingApi.addAction("bookService", new Action("delete3", 1, Boolean.FALSE));
		remotingApi.addAction("bookService", new Action("delete4", 1, Boolean.FALSE));
		remotingApi.addAction("bookService", new Action("create3", 1, Boolean.FALSE));
		remotingApi.addAction("bookService", new Action("create4", 1, Boolean.FALSE));
		remotingApi.addAction("bookSubAopService", new Action("read", 1, Boolean.FALSE));
		remotingApi.addAction("bookSubAopService", new Action("readWithPaging", 1,
				Boolean.FALSE));
		remotingApi.addAction("bookSubAopService",
				new Action("update3", 1, Boolean.FALSE));
		remotingApi.addAction("bookSubAopService",
				new Action("update4", 1, Boolean.FALSE));
		remotingApi.addAction("bookSubAopService",
				new Action("delete3", 1, Boolean.FALSE));
		remotingApi.addAction("bookSubAopService",
				new Action("delete4", 1, Boolean.FALSE));
		remotingApi.addAction("bookSubAopService",
				new Action("create3", 1, Boolean.FALSE));
		remotingApi.addAction("bookSubAopService",
				new Action("create4", 1, Boolean.FALSE));
		remotingApi.addAction("bookSubService", new Action("read", 1, Boolean.FALSE));
		remotingApi.addAction("bookSubService", new Action("readWithPaging", 1,
				Boolean.FALSE));
		remotingApi.addAction("bookSubService", new Action("update3", 1, Boolean.FALSE));
		remotingApi.addAction("bookSubService", new Action("update4", 1, Boolean.FALSE));
		remotingApi.addAction("bookSubService", new Action("delete3", 1, Boolean.FALSE));
		remotingApi.addAction("bookSubService", new Action("delete4", 1, Boolean.FALSE));
		remotingApi.addAction("bookSubService", new Action("create3", 1, Boolean.FALSE));
		remotingApi.addAction("bookSubService", new Action("create4", 1, Boolean.FALSE));

		remotingApi.addAction("remoteProviderSimpleNamed", new Action("method1",
				new ArrayList<String>()));
		remotingApi.addAction("remoteProviderSimpleNamed",
				new Action("method2", Arrays.asList("i", "d", "s")));
		remotingApi.addAction("remoteProviderSimpleNamed",
				new Action("method3", Arrays.asList("userName")));
		remotingApi.addAction("remoteProviderSimpleNamed",
				new Action("method4", Arrays.asList("a", "b")));
		remotingApi.addAction("remoteProviderSimpleNamed",
				new Action("method5", Arrays.asList("d")));
		remotingApi.addAction("remoteProviderSimpleNamed", new Action("method6",
				new ArrayList<String>()));
		remotingApi.addAction(
				"remoteProviderSimpleNamed",
				new Action("method7", Arrays.asList("flag", "aCharacter", "workflow",
						"aInt", "aLong", "aDouble", "aFloat", "aShort", "aByte")));
		remotingApi.addAction("remoteProviderSimpleNamed",
				new Action("method9", Arrays.asList("aRow")));
		remotingApi.addAction(
				"remoteProviderSimpleNamed",
				new Action("method10", Arrays.asList("flag", "aCharacter", "workflow",
						"aInt", "aLong", "aDouble", "aFloat", "aShort", "aByte")));

		remotingApi.addAction(
				"remoteProviderSimpleNamed",
				new Action("method11", Arrays.asList("endDate", "normalParameter",
						"aDate", "percent")));

		remotingApi.addAction("remoteProviderSimpleNamed", new Action(
				"methodCollection1", Arrays.asList("name", "collections")));
		remotingApi.addAction("remoteProviderSimpleNamed", new Action(
				"methodCollection2", Arrays.asList("name", "collections")));
		remotingApi.addAction("remoteProviderSimpleNamed", new Action(
				"methodCollection3", Arrays.asList("name", "collections")));

		remotingApi.addAction("remoteProviderSimpleNamed", new Action("methodArray1",
				Arrays.asList("name", "array")));
		remotingApi.addAction("remoteProviderSimpleNamed", new Action("methodArray2",
				Arrays.asList("name", "array")));
		remotingApi.addAction("remoteProviderSimpleNamed", new Action("methodArray3",
				Arrays.asList("name", "array")));
		remotingApi.addAction("remoteProviderSimpleNamed", new Action("methodArray4",
				Arrays.asList("name", "array")));

		remotingApi.addAction("remoteProviderSimpleNamed",
				new Action("methodRP1", Arrays.asList("lastName", "theAge", "active")));
		remotingApi.addAction("remoteProviderSimpleNamed",
				new Action("methodRP2", Arrays.asList("lastName", "theAge", "active")));
		remotingApi.addAction("remoteProviderSimpleNamed",
				new Action("methodRP3", Arrays.asList("lastName", "theAge", "active")));

		remotingApi.addAction("remoteProviderSimpleNamed", new Action("withCookie",
				Arrays.asList("i")));
		remotingApi.addAction("remoteProviderSimpleNamed", new Action(
				"withRequiredCookie", Arrays.asList("i")));
		remotingApi.addAction("remoteProviderSimpleNamed", new Action(
				"withRequestHeader", Arrays.asList("bd")));
		remotingApi.addAction("remoteProviderSimpleNamed", new Action(
				"withRequiredRequestHeader", Arrays.asList("bd")));

		remotingApi.addAction("remoteProviderSimpleNamed", new Action("nonStrictMethod1",
				Collections.<String> emptyList(), Boolean.FALSE));
		remotingApi.addAction("remoteProviderSimpleNamed", new Action("nonStrictMethod2",
				Collections.<String> emptyList(), Boolean.FALSE));
		remotingApi.addAction("remoteProviderSimpleNamed", new Action("nonStrictMethod3",
				Collections.<String> emptyList(), Boolean.FALSE));

		remotingApi.addPollingProvider(new PollingProvider("pollProvider",
				"handleMessage1", "message1"));
		remotingApi.addPollingProvider(new PollingProvider("pollProvider",
				"handleMessage2", "message2"));
		remotingApi.addPollingProvider(new PollingProvider("pollProvider",
				"handleMessage3", "message3"));
		remotingApi.addPollingProvider(new PollingProvider("pollProvider",
				"handleMessage4", "message4"));
		remotingApi.addPollingProvider(new PollingProvider("pollProvider",
				"handleMessage5", "message5"));
		remotingApi.addPollingProvider(new PollingProvider("pollProvider", "message6",
				"message6"));
		remotingApi.addPollingProvider(new PollingProvider("pollProvider",
				"messageRequestHeader1", "messageRequestHeader1"));
		remotingApi.addPollingProvider(new PollingProvider("pollProvider",
				"messageRequestHeader2", "messageRequestHeader2"));
		remotingApi.addPollingProvider(new PollingProvider("pollProvider",
				"messageRequestHeader3", "messageRequestHeader3"));
		remotingApi.addPollingProvider(new PollingProvider("pollProvider",
				"messageRequestHeader4", "messageRequestHeader4"));
		remotingApi.addPollingProvider(new PollingProvider("pollProvider",
				"messageRequestHeader5", "messageRequestHeader5"));
		remotingApi.addPollingProvider(new PollingProvider("pollProvider",
				"messageRequestHeader6", "messageRequestHeader6"));
		remotingApi.addPollingProvider(new PollingProvider("pollProvider",
				"messageCookieValue1", "messageCookieValue1"));
		remotingApi.addPollingProvider(new PollingProvider("pollProvider",
				"messageCookieValue2", "messageCookieValue2"));
		remotingApi.addPollingProvider(new PollingProvider("pollProvider",
				"messageCookieValue3", "messageCookieValue3"));
		remotingApi.addPollingProvider(new PollingProvider("pollProvider",
				"messageCookieValue4", "messageCookieValue4"));
		remotingApi.addPollingProvider(new PollingProvider("pollProvider",
				"messageCookieValue5", "messageCookieValue5"));
		remotingApi.addPollingProvider(new PollingProvider("pollProvider",
				"messageCookieValue6", "messageCookieValue6"));

		remotingApi.addSseProvider("sseProvider", "message1");
		remotingApi.addSseProvider("sseProvider", "message2");
		remotingApi.addSseProvider("sseProvider", "message3");
		remotingApi.addSseProvider("sseProvider", "message4");
		remotingApi.addSseProvider("sseProvider", "message5");
		remotingApi.addSseProvider("sseProvider", "message6");
		remotingApi.addSseProvider("sseProvider", "messageRequestHeader1");
		remotingApi.addSseProvider("sseProvider", "messageRequestHeader2");
		remotingApi.addSseProvider("sseProvider", "messageRequestHeader3");
		remotingApi.addSseProvider("sseProvider", "messageRequestHeader4");
		remotingApi.addSseProvider("sseProvider", "messageRequestHeader5");
		remotingApi.addSseProvider("sseProvider", "messageRequestHeader6");
		remotingApi.addSseProvider("sseProvider", "messageCookieValue1");
		remotingApi.addSseProvider("sseProvider", "messageCookieValue2");
		remotingApi.addSseProvider("sseProvider", "messageCookieValue3");
		remotingApi.addSseProvider("sseProvider", "messageCookieValue4");
		remotingApi.addSseProvider("sseProvider", "messageCookieValue5");
		remotingApi.addSseProvider("sseProvider", "messageCookieValue6");
		remotingApi.addSseProvider("sseProvider", "message13");
		remotingApi.addSseProvider("sseProvider", "message14");
		remotingApi.addSseProvider("sseProvider", "message15");

		remotingApi.addAction("remoteProviderOptional", new Action("method1", 3,
				Boolean.FALSE));
		remotingApi.addAction("remoteProviderOptional", new Action("method2", 1,
				Boolean.FALSE));
		remotingApi.addAction("remoteProviderOptional", new Action("method4", 2,
				Boolean.FALSE));
		remotingApi.addAction("remoteProviderOptional", new Action("method5", 1,
				Boolean.FALSE));
		remotingApi.addAction("remoteProviderOptional", new Action("method6", 0,
				Boolean.FALSE));
		remotingApi.addAction("remoteProviderOptional", new Action("method7", 0,
				Boolean.FALSE));
		remotingApi.addAction("remoteProviderOptional", new Action("method8", 1,
				Boolean.FALSE));
		remotingApi.addAction("remoteProviderOptional", new Action("method9", 0,
				Boolean.FALSE));
		remotingApi.addAction("remoteProviderOptional", new Action("method10", 3,
				Boolean.FALSE));
		remotingApi.addAction("remoteProviderOptional", new Action("method11", 3,
				Boolean.FALSE));
		remotingApi.addAction("remoteProviderOptional", new Action("method12", 3,
				Boolean.FALSE));
		remotingApi.addAction("remoteProviderOptional", new Action("method13", 3,
				Boolean.FALSE));
		remotingApi.addAction("remoteProviderOptional", new Action("method16", 0,
				Boolean.FALSE));
		remotingApi.addAction("remoteProviderOptional", new Action("method17", 0,
				Boolean.FALSE));
		remotingApi.addAction("remoteProviderOptional", new Action("method18", 0,
				Boolean.FALSE));
		remotingApi.addAction("remoteProviderOptional", new Action("method19", 0,
				Boolean.FALSE));

		remotingApi.addPollingProvider(new PollingProvider("remoteProviderOptional",
				"opoll1", "opoll1"));
		remotingApi.addPollingProvider(new PollingProvider("remoteProviderOptional",
				"opoll2", "opoll2"));
		remotingApi.addPollingProvider(new PollingProvider("remoteProviderOptional",
				"opoll3", "opoll3"));
		remotingApi.addPollingProvider(new PollingProvider("remoteProviderOptional",
				"opoll4", "opoll4"));
		remotingApi.addPollingProvider(new PollingProvider("remoteProviderOptional",
				"opoll5", "opoll5"));

		remotingApi.addAction("remoteProviderOptional",
				new Action("namedMethod1", Arrays.asList("i", "d", "s")));
		remotingApi
				.addAction(
						"remoteProviderOptional",
						new Action("namedMethod2", Arrays.asList("lastName", "theAge",
								"active")));
		remotingApi.addAction("remoteProviderOptional",
				new Action("namedMethod3", Arrays.asList("i")));
		remotingApi.addAction("remoteProviderOptional",
				new Action("namedMethod4", Arrays.asList("bd")));

		remotingApi.addSseProvider("remoteProviderOptional", "sse1");
		remotingApi.addSseProvider("remoteProviderOptional", "sse2");
		remotingApi.addSseProvider("remoteProviderOptional", "sse3");

		remotingApi.addAction("remoteProviderOptional", new Action("storeRead1", 1,
				Boolean.FALSE));
		remotingApi.addAction("remoteProviderOptional", new Action("storeRead2", 1,
				Boolean.FALSE));

		remotingApi.addAction("remoteProviderOptional", new Action("treeLoad1", 1,
				Boolean.FALSE));
		remotingApi.addAction("remoteProviderOptional", new Action("treeLoad2", 1,
				Boolean.FALSE));

		remotingApi.addAction("remoteProviderMetadata", new Action("method1", 1,
				Collections.singletonList("mp")));
		remotingApi.addAction("remoteProviderMetadata", new Action("method2", 1,
				Collections.singletonList("id")));
		remotingApi.addAction("remoteProviderMetadata", new Action("method3", 1,
				Collections.singletonList("id")));
		remotingApi.addAction("remoteProviderMetadata", new Action("method4", 1,
				Collections.singletonList("id")));
		remotingApi.addAction("remoteProviderMetadata", new Action("method5", 1,
				Collections.singletonList("id")));
		remotingApi.addAction("remoteProviderMetadata", new Action("method6", 1,
				Collections.singletonList("id")));
		remotingApi.addAction("remoteProviderMetadata", new Action("update1", 1,
				Collections.singletonList("id")));
		remotingApi.addAction("remoteProviderMetadata", new Action("update2", 1,
				Collections.singletonList("id")));
		remotingApi.addAction("remoteProviderMetadata", new Action("update3", 1,
				Collections.singletonList("id")));
		remotingApi.addAction("remoteProviderMetadata", new Action("treeLoad1", 1,
				Collections.singletonList("id")));
		remotingApi.addAction("remoteProviderMetadata", new Action("treeLoad2", 1,
				Collections.singletonList("id")));
		remotingApi.addAction("remoteProviderMetadata", new Action("treeLoad3", 1,
				Collections.singletonList("id")));

		return remotingApi;
	}

	public static RemotingApi emptyGroupApis(String namespace) {
		RemotingApi remotingApi = new RemotingApi("remoting", "/router", namespace);
		remotingApi.addAction("remoteProviderSimple", new Action("method2", 0,
				Boolean.FALSE));
		remotingApi.addAction("remoteProviderSimple", new Action("method4b", 3,
				Boolean.FALSE));
		remotingApi.addAction("remoteProviderSimple", new Action("method6", 2,
				Boolean.FALSE));
		remotingApi.addAction("remoteProviderSimple", new Action("method7", 0,
				Boolean.FALSE));
		remotingApi.addAction("remoteProviderSimple", new Action("method8", 1,
				Boolean.FALSE));
		remotingApi.addAction("remoteProviderSimple", new Action("method10", 9,
				Boolean.FALSE));
		remotingApi.addAction("remoteProviderSimple", new Action("method11", 0,
				Boolean.FALSE));
		remotingApi.addAction("remoteProviderSimple", new Action("method11b", 0,
				Boolean.FALSE));
		remotingApi.addAction("remoteProviderSimple", new Action("method12", 1,
				Boolean.FALSE));
		remotingApi.addAction("remoteProviderSimple", new Action("method13", 9,
				Boolean.FALSE));
		remotingApi.addAction("remoteProviderSimple", new Action("method14", 4,
				Boolean.FALSE));

		remotingApi.addAction("remoteProviderSimple", new Action("method15", 2,
				Boolean.FALSE));
		remotingApi.addAction("remoteProviderSimple", new Action("method16", 1,
				Boolean.FALSE));
		remotingApi.addAction("remoteProviderSimple", new Action("method17", 0,
				Boolean.FALSE));
		remotingApi.addAction("remoteProviderSimple", new Action("method18", 0,
				Boolean.FALSE));
		remotingApi.addAction("remoteProviderSimple", new Action("method19", 1,
				Boolean.FALSE));
		remotingApi.addAction("remoteProviderSimple", new Action("method20", 0,
				Boolean.FALSE));
		remotingApi.addAction("remoteProviderSimple", new Action("method21", 3,
				Boolean.FALSE));
		remotingApi.addAction("remoteProviderSimple", new Action("method22", 3,
				Boolean.FALSE));
		remotingApi.addAction("remoteProviderSimple", new Action("method23", 3,
				Boolean.FALSE));
		remotingApi.addAction("remoteProviderSimple", new Action("method24", 3,
				Boolean.FALSE));
		remotingApi.addAction("remoteProviderSimple", new Action("method25", 3,
				Boolean.FALSE));
		remotingApi.addAction("remoteProviderSimple", new Action("method26", 3,
				Boolean.FALSE));
		remotingApi.addAction("remoteProviderSimple", new Action("method27", 0,
				Boolean.FALSE));
		remotingApi.addAction("remoteProviderSimple", new Action("method28", 0,
				Boolean.FALSE));
		remotingApi.addAction("remoteProviderSimple", new Action("method29", 0,
				Boolean.FALSE));
		remotingApi.addAction("remoteProviderSimple", new Action("method30", 0,
				Boolean.FALSE));
		remotingApi.addAction("remoteProviderSimple", new Action("methodWithOptional", 1,
				Boolean.FALSE));

		remotingApi.addAction("remoteProviderStoreRead", new Action("method1", 1,
				Boolean.FALSE));
		remotingApi.addAction("remoteProviderStoreRead", new Action("method2", 1,
				Boolean.FALSE));
		remotingApi.addAction("remoteProviderStoreRead", new Action("method3", 1,
				Boolean.FALSE));
		remotingApi.addAction("remoteProviderStoreRead", new Action("method4", 1,
				Boolean.FALSE));
		remotingApi.addAction("remoteProviderStoreRead", new Action("method8", 1,
				Boolean.FALSE));
		remotingApi.addAction("remoteProviderStoreRead", new Action("method9", 1,
				Boolean.FALSE));
		remotingApi.addAction("remoteProviderStoreRead", new Action("method10", 1,
				Boolean.FALSE));
		remotingApi.addAction("remoteProviderStoreRead", new Action("method11", 1,
				Boolean.FALSE));
		remotingApi.addAction("remoteProviderStoreRead", new Action("methodFilter", 1,
				Boolean.FALSE));
		remotingApi.addAction("remoteProviderStoreRead", new Action("methodMetadata", 1,
				Boolean.FALSE));

		remotingApi.addAction("remoteProviderStoreModify", new Action("create1", 1,
				Boolean.FALSE));
		remotingApi.addAction("remoteProviderStoreModify", new Action("create2", 1,
				Boolean.FALSE));
		remotingApi.addAction("remoteProviderStoreModify", new Action("update1", 1,
				Boolean.FALSE));
		remotingApi.addAction("remoteProviderStoreModify", new Action("update2", 1,
				Boolean.FALSE));
		remotingApi.addAction("remoteProviderStoreModify", new Action("update3", 1,
				Boolean.FALSE));

		remotingApi.addAction("remoteProviderStoreModifyArray", new Action("create1", 1,
				Boolean.FALSE));
		remotingApi.addAction("remoteProviderStoreModifyArray", new Action("create2", 1,
				Boolean.FALSE));
		remotingApi.addAction("remoteProviderStoreModifyArray", new Action("update1", 1,
				Boolean.FALSE));
		remotingApi.addAction("remoteProviderStoreModifyArray", new Action("update2", 1,
				Boolean.FALSE));
		remotingApi.addAction("remoteProviderStoreModifyArray", new Action("update3", 1,
				Boolean.FALSE));

		remotingApi.addAction("remoteProviderStoreModifyInterface", new Action("create1",
				1, Boolean.FALSE));
		remotingApi.addAction("remoteProviderStoreModifyInterface", new Action("create2",
				1, Boolean.FALSE));
		remotingApi.addAction("remoteProviderStoreModifyInterface", new Action("update1",
				1, Boolean.FALSE));
		remotingApi.addAction("remoteProviderStoreModifyInterface", new Action("update2",
				1, Boolean.FALSE));
		remotingApi.addAction("remoteProviderStoreModifyInterface", new Action("update3",
				1, Boolean.FALSE));

		remotingApi.addAction("remoteProviderStoreModifySingle", new Action("create1", 1,
				Boolean.FALSE));
		remotingApi.addAction("remoteProviderStoreModifySingle", new Action("create2", 1,
				Boolean.FALSE));
		remotingApi.addAction("remoteProviderStoreModifySingle", new Action("update1", 1,
				Boolean.FALSE));
		remotingApi.addAction("remoteProviderStoreModifySingle", new Action("update2", 1,
				Boolean.FALSE));
		remotingApi.addAction("remoteProviderStoreModifySingle", new Action("update3", 1,
				Boolean.FALSE));

		remotingApi.addAction("remoteProviderFormLoad", new Action("method2", 1,
				Boolean.FALSE));
		remotingApi.addAction("remoteProviderFormLoad", new Action("method3", 1,
				Boolean.FALSE));
		remotingApi.addAction("remoteProviderFormLoad", new Action("method4", 1,
				Boolean.FALSE));
		remotingApi.addAction("remoteProviderFormLoad", new Action("method6", 1,
				Boolean.FALSE));
		remotingApi.addAction("remoteProviderFormLoad", new Action("method7", 1,
				Boolean.FALSE));

		remotingApi.addAction("remoteProviderTreeLoad", new Action("method2", 1,
				Boolean.FALSE));
		remotingApi.addAction("remoteProviderTreeLoad", new Action("method4", 1,
				Boolean.FALSE));
		remotingApi.addAction("remoteProviderTreeLoad", new Action("method5", 1,
				Boolean.FALSE));
		remotingApi.addAction("remoteProviderTreeLoad", new Action("method6", 1,
				Boolean.FALSE));

		remotingApi.addAction("formInfoController3", new Action("updateInfoJson", 1,
				Boolean.FALSE));
		remotingApi.addAction("formInfoController3", new Action("updateInfoJsonDirect",
				1, Boolean.FALSE));
		remotingApi.addAction("formInfoController3", new Action(
				"updateInfoJsonDirectError", 1, Boolean.FALSE));

		remotingApi.addPollingProvider(new PollingProvider("pollProvider",
				"handleMessage4", "message4"));

		remotingApi.addSseProvider("sseProvider", "message4");
		return remotingApi;
	}

	@SuppressWarnings("unchecked")
	public static void compareJson(MvcResult result, RemotingApi remotingApi,
			ApiRequestParams params) throws IOException {
		String content = result.getResponse().getContentAsString();
		assertThat(result.getResponse().getContentType()).isEqualTo(
				"application/json;charset=UTF-8");
		assertThat(content).isNotEmpty();

		Map<String, Object> rootAsMap = ControllerUtil.readValue(content, Map.class);

		if (remotingApi.getNamespace() == null) {
			assertThat(rootAsMap).hasSize(4);
		}
		else {
			assertThat(rootAsMap).hasSize(5);
			assertThat(rootAsMap.get("namespace")).isEqualTo(remotingApi.getNamespace());
		}

		assertThat(rootAsMap.get("url")).isEqualTo(remotingApi.getUrl());
		assertThat(rootAsMap.get("type")).isEqualTo("remoting");
		if (StringUtils.hasText(params.getApiNs())) {
			assertThat(rootAsMap.get("descriptor")).isEqualTo(
					params.getApiNs() + "." + params.getRemotingApiVar());
		}
		else {
			assertThat(rootAsMap.get("descriptor")).isEqualTo(params.getRemotingApiVar());
		}
		assertThat(rootAsMap.containsKey("actions")).isTrue();

		if (remotingApi.getNamespace() != null) {
			assertThat(rootAsMap.get("namespace")).isEqualTo(remotingApi.getNamespace());
		}

		Map<String, Object> beans = (Map<String, Object>) rootAsMap.get("actions");

		assertThat(beans).hasSize(remotingApi.getActions().size());
		for (String beanName : remotingApi.getActions().keySet()) {
			List<Map<String, Object>> actions = (List<Map<String, Object>>) beans
					.get(beanName);
			List<Action> expectedActions = remotingApi.getActions().get(beanName);
			compare(expectedActions, actions);
		}
	}

	static void compare(MvcResult result, RemotingApi remotingApi, ApiRequestParams params)
			throws UnsupportedEncodingException {

		if (params.getConfiguration() == null
				|| !params.getConfiguration().isStreamResponse()) {
			assertThat(result.getResponse().getContentLength()).isEqualTo(
					result.getResponse().getContentAsByteArray().length);
		}

		compare(result.getResponse().getContentAsString(), result.getResponse()
				.getContentType(), remotingApi, params);
	}

	@SuppressWarnings("unchecked")
	public static void compare(String contentString, String contentTypeString,
			RemotingApi remotingApi, ApiRequestParams params) {

		String content = contentString;
		content = content.replace(";", ";\n");
		content = content.replace("{", "{\n");
		content = content.replace("}", "}\n");

		String contentType = contentTypeString;
		int cs = contentType.indexOf(';');
		if (cs != -1) {
			contentType = contentType.substring(0, cs);
		}

		if (params.getConfiguration() != null) {
			assertThat(contentType).isEqualTo(
					params.getConfiguration().getJsContentType());
		}
		else {
			assertThat(contentType).isEqualTo("application/javascript");
		}
		assertThat(content).isNotEmpty();

		String[] lines = content.split("\n");

		String remotingApiLine;
		String pollingApiLine;
		String sseApiLine;

		String apiNs = params.getApiNs();
		if (apiNs == null) {
			apiNs = "Ext.app";
		}

		String remotingApiVar = params.getRemotingApiVar();
		if (remotingApiVar == null) {
			remotingApiVar = "REMOTING_API";
		}

		String pollingUrlsVar = params.getPollingUrlsVar();
		if (pollingUrlsVar == null) {
			pollingUrlsVar = "POLLING_URLS";
		}
		String sseVar = params.getSseVar();
		if (sseVar == null) {
			sseVar = "SSE";
		}

		if (StringUtils.hasText(apiNs)) {
			String extNsLine = "Ext.ns('" + apiNs + "');";
			assertContains(extNsLine, lines);

			remotingApiLine = apiNs + "." + remotingApiVar + " = {";
			pollingApiLine = apiNs + "." + pollingUrlsVar + " = {";
			sseApiLine = apiNs + "." + sseVar + " = {";
		}
		else {
			assertDoesNotContains("Ext.ns(", lines);
			remotingApiLine = remotingApiVar + " = {";
			pollingApiLine = pollingUrlsVar + " = {";
			sseApiLine = sseVar + " = {";
		}

		int startRemotingApi = assertContains(remotingApiLine, lines);

		int startPollingApi = lines.length;
		if (!remotingApi.getPollingProviders().isEmpty()) {
			startPollingApi = assertContains(pollingApiLine, lines);
		}
		else {
			assertDoesNotContains(pollingApiLine, lines);
		}

		int startSseApi = lines.length;
		if (!remotingApi.getSseProviders().isEmpty()) {
			startSseApi = assertContains(sseApiLine, lines);
		}
		else {
			assertDoesNotContains(sseApiLine, lines);
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
			for (int i = startPollingApi + 1; i < startSseApi; i++) {
				pollingJson += lines[i];
			}
		}

		String sseJson = "{";
		if (!remotingApi.getSseProviders().isEmpty()) {
			for (int i = startSseApi + 1; i < lines.length; i++) {
				sseJson += lines[i];
			}
		}

		int noOfconfigOptions = 0;
		if (params.getConfiguration() != null) {
			if (params.getConfiguration().getTimeout() != null) {
				noOfconfigOptions++;
			}
			if (params.getConfiguration().getEnableBuffer() != null) {
				noOfconfigOptions++;
			}
			if (params.getConfiguration().getMaxRetries() != null) {
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
		}
		else {
			if (4 + noOfconfigOptions != rootAsMap.size()) {
				System.out.println("NOOFCONFIG: " + noOfconfigOptions);
				for (String key : rootAsMap.keySet()) {
					System.out.println(key + "->" + rootAsMap.get(key));
				}
			}
			assertThat(rootAsMap).hasSize(4 + noOfconfigOptions);
		}

		assertThat(rootAsMap.get("url")).isEqualTo(remotingApi.getUrl());
		if (params.getProviderType() != null) {
			assertThat(rootAsMap.get("type")).isEqualTo(params.getProviderType());
		}
		else {
			assertThat(rootAsMap.get("type")).isEqualTo("remoting");
		}
		assertThat(rootAsMap.containsKey("actions")).isTrue();

		if (remotingApi.getNamespace() != null) {
			assertThat(rootAsMap.get("namespace")).isEqualTo(remotingApi.getNamespace());
		}

		if (params.getConfiguration() != null) {
			if (params.getConfiguration().getTimeout() != null) {
				assertThat(rootAsMap.get("timeout")).isEqualTo(
						params.getConfiguration().getTimeout());
			}
			else {
				assertThat(rootAsMap.get("timeout")).isNull();
			}

			if (params.getConfiguration().getEnableBuffer() != null) {
				assertThat(rootAsMap.get("enableBuffer")).isEqualTo(
						params.getConfiguration().getEnableBuffer());
			}
			else {
				assertThat(rootAsMap.get("enableBuffer")).isNull();
			}

			if (params.getConfiguration().getMaxRetries() != null) {
				assertThat(rootAsMap.get("maxRetries")).isEqualTo(
						params.getConfiguration().getMaxRetries());
			}
			else {
				assertThat(rootAsMap.get("maxRetries")).isNull();
			}
		}
		else {
			assertThat(rootAsMap.get("timeout")).isNull();
			assertThat(rootAsMap.get("enableBuffer")).isNull();
			assertThat(rootAsMap.get("maxRetries")).isNull();
		}

		Map<String, Object> beans = (Map<String, Object>) rootAsMap.get("actions");

		assertThat(beans.size()).isEqualTo(remotingApi.getActions().size());
		for (String beanName : remotingApi.getActions().keySet()) {
			List<Map<String, Object>> actions = (List<Map<String, Object>>) beans
					.get(beanName);
			List<Action> expectedActions = remotingApi.getActions().get(beanName);
			compare(expectedActions, actions);
		}

		if (!remotingApi.getPollingProviders().isEmpty()) {
			Map<String, Object> pollingMap = ControllerUtil.readValue(pollingJson,
					Map.class);
			assertThat(pollingMap).hasSize(remotingApi.getPollingProviders().size());
			for (PollingProvider pp : remotingApi.getPollingProviders()) {
				String url = (String) pollingMap.get(pp.getEvent());
				assertThat(url).isNotNull();
				assertEquals(
						String.format("%s/%s/%s/%s",
								remotingApi.getUrl().replace("router", "poll"),
								pp.getBeanName(), pp.getMethod(), pp.getEvent()), url);
			}
		}

		if (!remotingApi.getSseProviders().isEmpty()) {
			String sseUrl = remotingApi.getUrl().replace("router", "sse");
			Map<String, Object> sseMap = ControllerUtil.readValue(sseJson, Map.class);
			assertThat(sseMap).hasSize(remotingApi.getSseProviders().size());
			for (String beanName : remotingApi.getSseProviders().keySet()) {
				Map<String, String> actions = (Map<String, String>) sseMap.get(beanName);
				List<String> expectedActions = remotingApi.getSseProviders()
						.get(beanName);
				compareSse(expectedActions, actions, beanName, sseUrl);
			}
		}
	}

	public static void compareSse(List<String> expectedActions,
			Map<String, String> actions, String beanName, String url) {
		assertThat(actions).isNotEmpty();
		assertThat(actions).hasSize(expectedActions.size());
		for (String expectedAction : expectedActions) {
			String actionUrl = actions.get(expectedAction);
			assertThat(actionUrl).isEqualTo(
					String.format("%s/%s/%s", url, beanName, expectedAction));
		}
	}

	@SuppressWarnings({ "null", "unchecked" })
	public static void compare(List<Action> expectedActions,
			List<Map<String, Object>> actions) {
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
				assertThat(action.get("formHandler")).isEqualTo(
						expectedAction.isFormHandler());
			}
			else {
				assertThat(action.containsKey("formHandler")).isFalse();
			}

			List<String> params = (List<String>) action.get("params");
			assertTrue(params != null && expectedAction.getParams() != null
					|| params == null && expectedAction.getParams() == null);

			if (expectedAction.getParams() != null) {
				assertThat(params).hasSize(expectedAction.getParams().size());
				for (String param : expectedAction.getParams()) {
					assertThat(params.contains(param)).isTrue();
				}
			}
		}
	}

	@SuppressWarnings("null")
	public static int assertContains(String extNsLine, String[] lines) {
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

	@SuppressWarnings("null")
	public static void assertDoesNotContains(String extNsLine, String[] lines) {
		if (lines == null) {
			fail("no lines");
		}

		for (String line : lines) {
			if (line.startsWith(extNsLine)) {
				fail("lines does contain : " + extNsLine);
			}
		}

	}

}
