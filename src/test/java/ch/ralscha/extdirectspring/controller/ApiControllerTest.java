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
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
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
		this.methodInfoCache.clear();
		this.apiCache.clear();
		this.wac.publishEvent(new ContextRefreshedEvent(this.wac));

		Configuration config = new Configuration();
		ReflectionTestUtils.setField(this.configurationService, "configuration", config);
		this.configurationService.afterPropertiesSet();

		this.mockMvc = MockMvcBuilders.webAppContextSetup(this.wac).build();
	}

	@Test
	public void testNoActionNamespaceDebugDefaultConfig() throws Exception {
		ApiRequestParams params = ApiRequestParams.builder().apiNs("test")
				.remotingApiVar("remotingApiVar").pollingUrlsVar("pollingUrlsVar")
				.build();
		runTest(this.mockMvc, params, allApis(null));
	}

	@Test
	public void testNoActionNamespaceDebugCustomConfig() throws Exception {
		Configuration config = new Configuration();
		config.setEnableBuffer(10);
		config.setMaxRetries(2);
		config.setTimeout(12000);
		ReflectionTestUtils.setField(this.configurationService, "configuration", config);
		this.configurationService.afterPropertiesSet();

		ApiRequestParams params = ApiRequestParams.builder().apiNs("testC")
				.remotingApiVar("remotingApiV").pollingUrlsVar("pollingUrlsV")
				.configuration(config).build();
		runTest(this.mockMvc, params, allApis(null));
	}

	@Test
	public void testWithActionNamespaceDefaultConfig() throws Exception {
		ApiRequestParams params = ApiRequestParams.builder().apiNs("Ext.ns")
				.actionNs("actionns").remotingApiVar("TEST_REMOTING_API")
				.pollingUrlsVar("TEST_POLLING_URLS").build();
		runTest(this.mockMvc, params, allApis("actionns"));
	}

	@Test
	public void testWithActionNamespaceCustomConfig() throws Exception {

		Configuration config = new Configuration();
		config.setEnableBuffer(Boolean.FALSE);
		config.setTimeout(10000);
		ReflectionTestUtils.setField(this.configurationService, "configuration", config);
		this.configurationService.afterPropertiesSet();

		ApiRequestParams params = ApiRequestParams.builder().apiNs("Ext.ns")
				.actionNs("actionns").remotingApiVar("TEST_REMOTING_API")
				.pollingUrlsVar("TEST_POLLING_URLS").configuration(config).build();
		runTest(this.mockMvc, params, allApis("actionns"));
	}

	@Test
	public void testEmptyGroupDefaultConfig() throws Exception {
		ApiRequestParams params = ApiRequestParams.builder().apiNs("test")
				.remotingApiVar("TEST_REMOTING_API").pollingUrlsVar("TEST_POLLING_URLS")
				.group("").build();
		runTest(this.mockMvc, params, emptyGroupApis(null));
	}

	@Test
	public void testBlankStringGroupDefaultConfig() throws Exception {
		ApiRequestParams params = ApiRequestParams.builder().apiNs("test")
				.remotingApiVar("TEST_REMOTING_API").pollingUrlsVar("TEST_POLLING_URLS")
				.group("     ").build();
		runTest(this.mockMvc, params, emptyGroupApis(null));
	}

	@Test
	public void testEmptyGroupCustomConfig() throws Exception {
		Configuration config = new Configuration();
		config.setEnableBuffer(Boolean.TRUE);
		config.setTimeout(33333);
		ReflectionTestUtils.setField(this.configurationService, "configuration", config);
		this.configurationService.afterPropertiesSet();

		ApiRequestParams params = ApiRequestParams.builder().apiNs("test")
				.remotingApiVar("TEST_REMOTING_API").pollingUrlsVar("TEST_POLLING_URLS")
				.group("").configuration(config).build();
		runTest(this.mockMvc, params, emptyGroupApis(null));
	}

	@Test
	public void testBlankStringGroupCustomConfig() throws Exception {
		Configuration config = new Configuration();
		config.setEnableBuffer(Boolean.TRUE);
		config.setTimeout(33333);
		ReflectionTestUtils.setField(this.configurationService, "configuration", config);
		this.configurationService.afterPropertiesSet();

		ApiRequestParams params = ApiRequestParams.builder().apiNs("test")
				.remotingApiVar("TEST_REMOTING_API").pollingUrlsVar("TEST_POLLING_URLS")
				.group("        ").configuration(config).build();
		runTest(this.mockMvc, params, emptyGroupApis(null));
	}

	@Test
	public void testBlankStringGroupCustomConfigBufferLimit() throws Exception {
		Configuration config = new Configuration();
		config.setEnableBuffer(Boolean.TRUE);
		config.setTimeout(333);
		config.setBufferLimit(4);
		ReflectionTestUtils.setField(this.configurationService, "configuration", config);
		this.configurationService.afterPropertiesSet();

		ApiRequestParams params = ApiRequestParams.builder().apiNs("test")
				.remotingApiVar("TEST_REMOTING_API").pollingUrlsVar("TEST_POLLING_URLS")
				.group("        ").configuration(config).build();
		runTest(this.mockMvc, params, emptyGroupApis(null));
	}

	@Test
	public void testUnknownGroupDefaultConfig() throws Exception {

		ApiRequestParams params = ApiRequestParams.builder().apiNs("test")
				.remotingApiVar("TEST_REMOTING_API").pollingUrlsVar("TEST_POLLING_URLS")
				.group("xy").build();
		runTest(this.mockMvc, params, noApis(null));
	}

	@Test
	public void testUnknownGroupCustomConfig() throws Exception {
		Configuration config = new Configuration();
		config.setEnableBuffer(Boolean.TRUE);
		ReflectionTestUtils.setField(this.configurationService, "configuration", config);
		this.configurationService.afterPropertiesSet();

		ApiRequestParams params = ApiRequestParams.builder().apiNs("test")
				.remotingApiVar("TEST_REMOTING_API").pollingUrlsVar("TEST_POLLING_URLS")
				.group("xy").configuration(config).build();
		runTest(this.mockMvc, params, noApis(null));
	}

	@Test
	public void testGroup1() throws Exception {
		testGroup1(null, null);
		testGroup1(null, null);
		this.apiCache.clear();
		testGroup1(null, "-1.0.0");
		this.apiCache.clear();
		testGroup1(null, "-fingerprinted");
	}

	@Test
	public void testGroup1WithConfig() throws Exception {
		Configuration config = new Configuration();
		config.setTimeout(12000);
		ReflectionTestUtils.setField(this.configurationService, "configuration", config);
		this.configurationService.afterPropertiesSet();

		testGroup1(config, null);
		testGroup1(config, null);
		this.apiCache.clear();
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
			assertThat(response.getHeader("Cache-Control"))
					.isEqualTo("public, max-age=" + 6 * 30 * 24 * 60 * 60);

			String expiresHeader = (String) response.getHeaderValue("Expires");
			DateTimeFormatter fmt = DateTimeFormat
					.forPattern("EEE, dd MMM yyyy HH:mm:ss z");
			DateTime expires = DateTime.parse(expiresHeader, fmt);

			DateTime inSixMonths = DateTime.now(DateTimeZone.UTC)
					.plusSeconds(6 * 30 * 24 * 60 * 60);
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
				.group("group2").build();
		runTest(this.mockMvc, params, group2Apis(null));
		runTest(this.mockMvc, params, group2Apis(null));
	}

	@Test
	public void testGroup3() throws Exception {
		ApiRequestParams params = ApiRequestParams.builder().apiNs("Extns").actionNs("ns")
				.remotingApiVar("RAPI").pollingUrlsVar("PURLS").group("group3").build();
		runTest(this.mockMvc, params, group3Apis("ns"));
		runTest(this.mockMvc, params, group3Apis("ns"));
	}

	@Test
	public void testGroup4() throws Exception {
		ApiRequestParams params = ApiRequestParams.builder().apiNs("test").actionNs("")
				.remotingApiVar("TEST_REMOTING_API").pollingUrlsVar("TEST_POLLING_URLS")
				.group("group4").build();
		runTest(this.mockMvc, params, group4Apis(null));

		params = ApiRequestParams.builder().apiNs("test")
				.remotingApiVar("TEST_REMOTING_API").pollingUrlsVar("TEST_POLLING_URLS")
				.group("group4").build();
		runTest(this.mockMvc, params, group4Apis(null));
	}

	@Test
	public void testGroup1and2() throws Exception {
		ApiRequestParams params = ApiRequestParams.builder().apiNs("Ext.ns")
				.actionNs("actionns").group("group1,group2").build();
		runTest(this.mockMvc, params, group1and2Apis("actionns"));
	}

	@Test
	public void testGroup1andUnknown() throws Exception {
		ApiRequestParams params = ApiRequestParams.builder().apiNs("Ext.ns")
				.actionNs("actionns").group("group1,unknown").build();
		runTest(this.mockMvc, params, group1Apis("actionns"));
	}

	@Test
	public void testInterfaceGroup() throws Exception {
		ApiRequestParams params = ApiRequestParams.builder().apiNs("test").actionNs("")
				.remotingApiVar("TEST_REMOTING_API").pollingUrlsVar("TEST_POLLING_URLS")
				.group("interface").build();
		runTest(this.mockMvc, params, interfaceApis(null));

		params = ApiRequestParams.builder().apiNs("test")
				.remotingApiVar("TEST_REMOTING_API").pollingUrlsVar("TEST_POLLING_URLS")
				.group("interface").build();
		runTest(this.mockMvc, params, interfaceApis(null));
	}

	@Test
	public void testNoApiNs() throws Exception {
		ApiRequestParams params = ApiRequestParams.builder().apiNs("").actionNs("")
				.remotingApiVar("TEST_REMOTING_API").pollingUrlsVar("TEST_POLLING_URLS")
				.group("group4").build();
		runTest(this.mockMvc, params, group4Apis(null));

		params = ApiRequestParams.builder().apiNs("").remotingApiVar("TEST_REMOTING_API")
				.pollingUrlsVar("TEST_POLLING_URLS").group("group4").build();
		runTest(this.mockMvc, params, group4Apis(null));
	}

	@Test
	public void testFullRouterUrl() throws Exception {
		ApiRequestParams params = ApiRequestParams.builder().apiNs("apiNs").actionNs("")
				.remotingApiVar("TEST_RMT_API").pollingUrlsVar("TEST_POLL_URLS")
				.fullRouterUrl(Boolean.TRUE).group("group2").build();
		runTest(this.mockMvc, params, group2Apis(null, "http://localhost/router"));

		params = ApiRequestParams.builder().apiNs("apiNs").remotingApiVar("TEST_RMT_API")
				.pollingUrlsVar("TEST_POLL_URLS").fullRouterUrl(Boolean.TRUE)
				.group("group2").build();
		runTest(this.mockMvc, params, group2Apis(null, "http://localhost/router"));

		params = ApiRequestParams.builder().apiNs("apiNs").actionNs("")
				.remotingApiVar("TEST_RMT_API").pollingUrlsVar("TEST_POLL_URLS")
				.fullRouterUrl(Boolean.FALSE).group("group2").build();
		runTest(this.mockMvc, params, group2Apis(null, "/router"));

		params = ApiRequestParams.builder().apiNs("apiNs").remotingApiVar("TEST_RMT_API")
				.pollingUrlsVar("TEST_POLL_URLS").fullRouterUrl(Boolean.FALSE)
				.group("group2").build();
		runTest(this.mockMvc, params, group2Apis(null, "/router"));
	}

	@Test
	public void testFormat() throws Exception {
		ApiRequestParams params = ApiRequestParams.builder().actionNs("").apiNs("apiNs")
				.remotingApiVar("TEST_RMT_API").pollingUrlsVar("TEST_POLL_URLS")
				.group("group2").format("json").build();
		runTest(this.mockMvc, params, group2Apis(null, "http://localhost/router"));

		params = ApiRequestParams.builder().actionNs("ns").apiNs("")
				.remotingApiVar("TEST_RMT_API").pollingUrlsVar("TEST_POLL_URLS")
				.group("group2").format("json").fullRouterUrl(Boolean.TRUE).build();
		runTest(this.mockMvc, params, group2Apis("ns", "http://localhost/router"));
	}

	@Test
	public void testBaseRouterUrl() throws Exception {
		ApiRequestParams params = ApiRequestParams.builder().actionNs("").apiNs("an")
				.remotingApiVar("rapi").pollingUrlsVar("papi").group("group2")
				.baseRouterUrl("test").build();
		runTest(this.mockMvc, params, group2Apis(null, "test/router"));
	}

	@Test
	public void testBaseRouterUrlWithEndingSlash() throws Exception {
		ApiRequestParams params = ApiRequestParams.builder().actionNs("").apiNs("an")
				.remotingApiVar("rapi").pollingUrlsVar("papi").group("group2")
				.fullRouterUrl(Boolean.TRUE).baseRouterUrl("service/test/").build();
		runTest(this.mockMvc, params, group2Apis(null, "service/test/router"));
	}

	@Test
	public void testBaseRouterUrlEmptyString() throws Exception {
		ApiRequestParams params = ApiRequestParams.builder().actionNs("").apiNs("an")
				.remotingApiVar("rapi").pollingUrlsVar("papi").group("group2")
				.baseRouterUrl("").build();
		runTest(this.mockMvc, params, group2Apis(null, "/router"));
	}

	static void runTest(MockMvc mockMvc, ApiRequestParams params, RemotingApi api)
			throws Exception {
		doTest(mockMvc, "/api-debug-doc.js", params, api);
		doTest(mockMvc, "/api-debug.js", params, api);
		doTest(mockMvc, "/api.js", params, api);
	}

	private MvcResult doTest(String url, ApiRequestParams params, RemotingApi expectedApi)
			throws Exception {
		return doTest(this.mockMvc, url, params, expectedApi);
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
		remotingApi.addAction("remoteProviderSimple", Action.create("method1", 0));
		remotingApi.addAction("remoteProviderTreeLoad", Action.create("method1", 1));
		return remotingApi;
	}

	static RemotingApi groupApisWithDoc(String namespace) {
		RemotingApi remotingApi = new RemotingApi("remoting", "/router", namespace);
		remotingApi.addAction("remoteProviderSimpleDoc", Action.create("method1", 0));
		remotingApi.addAction("remoteProviderSimpleDoc", Action.create("method2", 0));
		remotingApi.addAction("remoteProviderSimpleDoc", Action.create("method3", 0));
		remotingApi.addAction("remoteProviderSimpleDoc", Action.create("method4", 0));
		remotingApi.addAction("remoteProviderSimpleDoc", Action.create("method5", 0));
		remotingApi.addAction("remoteProviderSimpleDoc", Action.create("method6", 0));
		remotingApi.addAction("remoteProviderSimpleDoc", Action.create("method7", 0));
		remotingApi.addAction("remoteProviderSimpleDoc", Action.create("method8", 0));
		remotingApi.addAction("remoteProviderSimpleDoc", Action.create("method9", 0));
		remotingApi.addAction("remoteProviderSimpleDoc", Action.create("method10", 0));
		remotingApi.addAction("remoteProviderSimpleDoc", Action.create("method11", 0));
		remotingApi.addAction("remoteProviderSimpleDoc", Action.create("method12", 0));
		return remotingApi;
	}

	public static RemotingApi group2Apis(String namespace, String url) {
		RemotingApi remotingApi = new RemotingApi("remoting", url, namespace);
		remotingApi.addAction("remoteProviderSimple", Action.create("method3", 3));
		remotingApi.addAction("remoteProviderSimple", Action.create("method5", 1));
		remotingApi.addAction("remoteProviderStoreRead", Action.create("method6", 1));
		remotingApi.addAction("remoteProviderStoreRead", Action.create("method6Ed", 1));
		remotingApi.addAction("remoteProviderStoreRead", Action.create("method7", 1));
		remotingApi.addAction("remoteProviderStoreModify", Action.create("update4", 1));
		remotingApi.addAction("remoteProviderStoreModifyArray",
				Action.create("update4", 1));
		remotingApi.addAction("remoteProviderStoreModifyInterface",
				Action.create("update4", 1));
		remotingApi.addAction("remoteProviderStoreModifySingle",
				Action.create("update4", 1));
		remotingApi.addAction("formInfoController",
				Action.createFormHandler("upload", 0));
		remotingApi.addAction("uploadService", Action.createFormHandler("upload", 0));
		remotingApi.addAction("uploadService", Action.createFormHandler("uploadEd", 0));
		remotingApi.addPollingProvider(
				new PollingProvider("pollProvider", "handleMessage1", "message1"));
		remotingApi.addPollingProvider(
				new PollingProvider("pollProvider", "handleMessage2", "message2"));
		remotingApi.addPollingProvider(
				new PollingProvider("pollProvider", "message6", "message6"));
		return remotingApi;
	}

	public static RemotingApi group1and2Apis(String namespace) {
		RemotingApi remotingApi = new RemotingApi("remoting", "/router", namespace);
		remotingApi.addAction("remoteProviderSimple", Action.create("method1", 0));
		remotingApi.addAction("remoteProviderTreeLoad", Action.create("method1", 1));

		remotingApi.addAction("remoteProviderSimple", Action.create("method3", 3));
		remotingApi.addAction("remoteProviderSimple", Action.create("method5", 1));
		remotingApi.addAction("remoteProviderStoreRead", Action.create("method6", 1));
		remotingApi.addAction("remoteProviderStoreRead", Action.create("method6Ed", 1));
		remotingApi.addAction("remoteProviderStoreRead", Action.create("method7", 1));
		remotingApi.addAction("remoteProviderStoreModify", Action.create("update4", 1));
		remotingApi.addAction("remoteProviderStoreModifyArray",
				Action.create("update4", 1));
		remotingApi.addAction("remoteProviderStoreModifyInterface",
				Action.create("update4", 1));
		remotingApi.addAction("remoteProviderStoreModifySingle",
				Action.create("update4", 1));
		remotingApi.addAction("formInfoController",
				Action.createFormHandler("upload", 0));
		remotingApi.addAction("uploadService", Action.createFormHandler("upload", 0));
		remotingApi.addAction("uploadService", Action.createFormHandler("uploadEd", 0));
		remotingApi.addPollingProvider(
				new PollingProvider("pollProvider", "handleMessage1", "message1"));
		remotingApi.addPollingProvider(
				new PollingProvider("pollProvider", "handleMessage2", "message2"));
		remotingApi.addPollingProvider(
				new PollingProvider("pollProvider", "message6", "message6"));
		return remotingApi;
	}

	static RemotingApi group2Apis(String namespace) {
		return group2Apis(namespace, "/router");
	}

	public static RemotingApi group3Apis(String namespace) {
		RemotingApi remotingApi = new RemotingApi("remoting", "/router", namespace);
		remotingApi.addAction("remoteProviderSimple", Action.create("method5", 1));
		remotingApi.addAction("remoteProviderSimple", Action.create("method9", 0));
		remotingApi.addAction("remoteProviderStoreRead", Action.create("method5", 1));
		remotingApi.addAction("remoteProviderStoreRead", Action.create("method5Ed", 1));
		remotingApi.addAction("remoteProviderStoreModify", Action.create("destroy", 1));
		remotingApi.addAction("remoteProviderStoreModify",
				Action.create("destroyNotBatched", 1, false));
		remotingApi.addAction("remoteProviderStoreModifyArray",
				Action.create("destroy", 1));
		remotingApi.addAction("remoteProviderStoreModifySingle",
				Action.create("destroy", 1));
		remotingApi.addAction("remoteProviderFormLoad", Action.create("method1", 1));
		remotingApi.addAction("remoteProviderFormLoad", Action.create("method5", 1));
		remotingApi.addAction("remoteProviderFormLoad", Action.create("method5Ed", 1));
		remotingApi.addAction("formInfoController",
				Action.createFormHandler("updateInfo", 0));
		remotingApi.addAction("formInfoController",
				Action.createFormHandler("updateInfoDirect", 0));
		remotingApi.addAction("formInfoController",
				Action.createFormHandler("updateInfoDirectEd", 0));
		remotingApi.addAction("formInfoController2",
				Action.createFormHandler("updateInfo1", 0));
		remotingApi.addAction("formInfoController2",
				Action.createFormHandler("updateInfo2", 0));
		remotingApi.addAction("remoteProviderTreeLoad", Action.create("method3", 1));
		remotingApi.addPollingProvider(
				new PollingProvider("pollProvider", "handleMessage5", "message5"));
		return remotingApi;
	}

	public static RemotingApi group4Apis(String namespace) {
		RemotingApi remotingApi = new RemotingApi("remoting", "/router", namespace);
		remotingApi.addPollingProvider(
				new PollingProvider("pollProvider", "handleMessage3", "message3"));
		return remotingApi;
	}

	public static RemotingApi interfaceApis(String namespace) {
		RemotingApi remotingApi = new RemotingApi("remoting", "/router", namespace);
		remotingApi.addAction("remoteProviderImplementation",
				Action.create("storeRead", 1));
		remotingApi.addAction("remoteProviderImplementation",
				Action.create("method2", 0));
		remotingApi.addAction("remoteProviderImplementation",
				Action.create("method3", 3));
		return remotingApi;
	}

	public static RemotingApi allApis(String namespace) {
		RemotingApi remotingApi = new RemotingApi("remoting", "/router", namespace);
		remotingApi.addAction("remoteProviderSimple", Action.create("method1", 0));
		remotingApi.addAction("remoteProviderSimple", Action.create("method2", 0));
		remotingApi.addAction("remoteProviderSimple", Action.create("method3", 3));
		remotingApi.addAction("remoteProviderSimple", Action.create("method4b", 3));
		remotingApi.addAction("remoteProviderSimple", Action.create("method5", 1));
		remotingApi.addAction("remoteProviderSimple", Action.create("method6", 2));
		remotingApi.addAction("remoteProviderSimple", Action.create("method7", 0));
		remotingApi.addAction("remoteProviderSimple", Action.create("method8", 1));
		remotingApi.addAction("remoteProviderSimple", Action.create("method9", 0));
		remotingApi.addAction("remoteProviderSimple", Action.create("method10", 9));
		remotingApi.addAction("remoteProviderSimple", Action.create("method11", 0));
		remotingApi.addAction("remoteProviderSimple", Action.create("method11b", 0));
		remotingApi.addAction("remoteProviderSimple", Action.create("method12", 1));
		remotingApi.addAction("remoteProviderSimple", Action.create("method13", 9));
		remotingApi.addAction("remoteProviderSimple", Action.create("method14", 4));

		remotingApi.addAction("remoteProviderSimple", Action.create("method15", 2));
		remotingApi.addAction("remoteProviderSimple", Action.create("method16", 1));
		remotingApi.addAction("remoteProviderSimple", Action.create("method17", 0));
		remotingApi.addAction("remoteProviderSimple", Action.create("method18", 0));
		remotingApi.addAction("remoteProviderSimple", Action.create("method19", 1));
		remotingApi.addAction("remoteProviderSimple", Action.create("method20", 0));
		remotingApi.addAction("remoteProviderSimple", Action.create("method21", 3));
		remotingApi.addAction("remoteProviderSimple", Action.create("method22", 3));
		remotingApi.addAction("remoteProviderSimple", Action.create("method23", 3));
		remotingApi.addAction("remoteProviderSimple", Action.create("method24", 3));
		remotingApi.addAction("remoteProviderSimple", Action.create("method25", 3));
		remotingApi.addAction("remoteProviderSimple", Action.create("method26", 3));
		remotingApi.addAction("remoteProviderSimple", Action.create("method27", 0));
		remotingApi.addAction("remoteProviderSimple", Action.create("method28", 0));
		remotingApi.addAction("remoteProviderSimple", Action.create("method29", 0));
		remotingApi.addAction("remoteProviderSimple", Action.create("method30", 0));
		remotingApi.addAction("remoteProviderSimple",
				Action.create("method31", 1, false));
		remotingApi.addAction("remoteProviderSimple",
				Action.create("methodWithOptional", 1));

		remotingApi.addAction("remoteProviderSimpleDoc", Action.create("method1", 0));
		remotingApi.addAction("remoteProviderSimpleDoc", Action.create("method2", 0));
		remotingApi.addAction("remoteProviderSimpleDoc", Action.create("method3", 0));
		remotingApi.addAction("remoteProviderSimpleDoc", Action.create("method4", 0));
		remotingApi.addAction("remoteProviderSimpleDoc", Action.create("method5", 0));
		remotingApi.addAction("remoteProviderSimpleDoc", Action.create("method6", 0));
		remotingApi.addAction("remoteProviderSimpleDoc", Action.create("method7", 0));
		remotingApi.addAction("remoteProviderSimpleDoc", Action.create("method8", 0));
		remotingApi.addAction("remoteProviderSimpleDoc", Action.create("method9", 0));
		remotingApi.addAction("remoteProviderSimpleDoc", Action.create("method10", 0));
		remotingApi.addAction("remoteProviderSimpleDoc", Action.create("method11", 0));
		remotingApi.addAction("remoteProviderSimpleDoc", Action.create("method12", 0));

		remotingApi.addAction("remoteProviderStoreRead", Action.create("method1", 1));
		remotingApi.addAction("remoteProviderStoreRead", Action.create("method2", 1));
		remotingApi.addAction("remoteProviderStoreRead", Action.create("method3", 1));
		remotingApi.addAction("remoteProviderStoreRead", Action.create("method4", 1));
		remotingApi.addAction("remoteProviderStoreRead", Action.create("method4Ed", 1));
		remotingApi.addAction("remoteProviderStoreRead", Action.create("method5", 1));
		remotingApi.addAction("remoteProviderStoreRead", Action.create("method6", 1));
		remotingApi.addAction("remoteProviderStoreRead", Action.create("method5Ed", 1));
		remotingApi.addAction("remoteProviderStoreRead", Action.create("method6Ed", 1));
		remotingApi.addAction("remoteProviderStoreRead", Action.create("method7", 1));
		remotingApi.addAction("remoteProviderStoreRead", Action.create("method8", 1));
		remotingApi.addAction("remoteProviderStoreRead", Action.create("method9", 1));
		remotingApi.addAction("remoteProviderStoreRead", Action.create("method10", 1));
		remotingApi.addAction("remoteProviderStoreRead", Action.create("method11", 1));
		remotingApi.addAction("remoteProviderStoreRead",
				Action.create("method12", 1, false));
		remotingApi.addAction("remoteProviderStoreRead", Action.create("method8Ed", 1));
		remotingApi.addAction("remoteProviderStoreRead", Action.create("method9Ed", 1));
		remotingApi.addAction("remoteProviderStoreRead", Action.create("method10Ed", 1));
		remotingApi.addAction("remoteProviderStoreRead", Action.create("method11Ed", 1));
		remotingApi.addAction("remoteProviderStoreRead",
				Action.create("method12Ed", 1, false));

		remotingApi.addAction("remoteProviderStoreRead",
				Action.create("methodFilter", 1));
		remotingApi.addAction("remoteProviderStoreRead",
				Action.create("methodMetadata", 1));
		remotingApi.addAction("remoteProviderStoreRead",
				Action.create("methodMetadataEd", 1));

		remotingApi.addAction("remoteProviderStoreModify", Action.create("create1", 1));
		remotingApi.addAction("remoteProviderStoreModify", Action.create("create2", 1));
		remotingApi.addAction("remoteProviderStoreModify", Action.create("update1", 1));
		remotingApi.addAction("remoteProviderStoreModify", Action.create("update2", 1));
		remotingApi.addAction("remoteProviderStoreModify", Action.create("update3", 1));
		remotingApi.addAction("remoteProviderStoreModify", Action.create("update4", 1));
		remotingApi.addAction("remoteProviderStoreModify", Action.create("destroy", 1));
		remotingApi.addAction("remoteProviderStoreModify",
				Action.create("destroyNotBatched", 1, false));

		remotingApi.addAction("remoteProviderStoreModifyArray",
				Action.create("create1", 1));
		remotingApi.addAction("remoteProviderStoreModifyArray",
				Action.create("create2", 1));
		remotingApi.addAction("remoteProviderStoreModifyArray",
				Action.create("update1", 1));
		remotingApi.addAction("remoteProviderStoreModifyArray",
				Action.create("update2", 1));
		remotingApi.addAction("remoteProviderStoreModifyArray",
				Action.create("update3", 1));
		remotingApi.addAction("remoteProviderStoreModifyArray",
				Action.create("update4", 1));
		remotingApi.addAction("remoteProviderStoreModifyArray",
				Action.create("destroy", 1));

		remotingApi.addAction("remoteProviderStoreModifyInterface",
				Action.create("create1", 1));
		remotingApi.addAction("remoteProviderStoreModifyInterface",
				Action.create("create2", 1));
		remotingApi.addAction("remoteProviderStoreModifyInterface",
				Action.create("update1", 1));
		remotingApi.addAction("remoteProviderStoreModifyInterface",
				Action.create("update2", 1));
		remotingApi.addAction("remoteProviderStoreModifyInterface",
				Action.create("update3", 1));
		remotingApi.addAction("remoteProviderStoreModifyInterface",
				Action.create("update4", 1));

		remotingApi.addAction("remoteProviderStoreModifySingle",
				Action.create("create1", 1));
		remotingApi.addAction("remoteProviderStoreModifySingle",
				Action.create("create2", 1));
		remotingApi.addAction("remoteProviderStoreModifySingle",
				Action.create("update1", 1));
		remotingApi.addAction("remoteProviderStoreModifySingle",
				Action.create("update2", 1));
		remotingApi.addAction("remoteProviderStoreModifySingle",
				Action.create("update3", 1));
		remotingApi.addAction("remoteProviderStoreModifySingle",
				Action.create("update4", 1));
		remotingApi.addAction("remoteProviderStoreModifySingle",
				Action.create("destroy", 1));

		remotingApi.addAction("remoteProviderFormLoad", Action.create("method1", 1));
		remotingApi.addAction("remoteProviderFormLoad", Action.create("method2", 1));
		remotingApi.addAction("remoteProviderFormLoad", Action.create("method3", 1));
		remotingApi.addAction("remoteProviderFormLoad", Action.create("method4", 1));
		remotingApi.addAction("remoteProviderFormLoad", Action.create("method5", 1));
		remotingApi.addAction("remoteProviderFormLoad", Action.create("method6", 1));
		remotingApi.addAction("remoteProviderFormLoad", Action.create("method7", 1));
		remotingApi.addAction("remoteProviderFormLoad", Action.create("method5Ed", 1));
		remotingApi.addAction("remoteProviderFormLoad", Action.create("method6Ed", 1));
		remotingApi.addAction("remoteProviderFormLoad", Action.create("method7Ed", 1));

		remotingApi.addAction("formInfoController",
				Action.createFormHandler("updateInfo", 0));
		remotingApi.addAction("formInfoController",
				Action.createFormHandler("updateInfoDirect", 0));
		remotingApi.addAction("formInfoController",
				Action.createFormHandler("updateInfoDirectEd", 0));
		remotingApi.addAction("formInfoController",
				Action.createFormHandler("upload", 0));
		remotingApi.addAction("uploadService", Action.createFormHandler("upload", 0));
		remotingApi.addAction("uploadService", Action.createFormHandler("uploadEd", 0));

		remotingApi.addAction("formInfoController3", Action.create("updateInfoJson", 1));
		remotingApi.addAction("formInfoController3",
				Action.create("updateInfoJsonDirect", 1));
		remotingApi.addAction("formInfoController3",
				Action.create("updateInfoJsonDirectError", 1));

		remotingApi.addAction("formInfoController3",
				Action.create("updateInfoJsonEd", 1));
		remotingApi.addAction("formInfoController3",
				Action.create("updateInfoJsonDirectEd", 1));
		remotingApi.addAction("formInfoController3",
				Action.create("updateInfoJsonDirectErrorEd", 1));

		remotingApi.addAction("formInfoController2",
				Action.createFormHandler("updateInfo1", 0));
		remotingApi.addAction("formInfoController2",
				Action.createFormHandler("updateInfo2", 0));

		remotingApi.addAction("remoteProviderTreeLoad", Action.create("method1", 1));
		remotingApi.addAction("remoteProviderTreeLoad", Action.create("method2", 1));
		remotingApi.addAction("remoteProviderTreeLoad", Action.create("method3", 1));
		remotingApi.addAction("remoteProviderTreeLoad", Action.create("method4", 1));
		remotingApi.addAction("remoteProviderTreeLoad", Action.create("method5", 1));
		remotingApi.addAction("remoteProviderTreeLoad", Action.create("method6", 1));
		remotingApi.addAction("remoteProviderTreeLoad",
				Action.create("method7", 1, false));

		remotingApi.addAction("remoteProviderImplementation",
				Action.create("storeRead", 1));
		remotingApi.addAction("remoteProviderImplementation",
				Action.create("method2", 0));
		remotingApi.addAction("remoteProviderImplementation",
				Action.create("method3", 3));

		remotingApi.addAction("bookService", Action.create("read", 1));
		remotingApi.addAction("bookService", Action.create("readWithPaging", 1));
		remotingApi.addAction("bookService", Action.create("readWithPagingEd", 1));
		remotingApi.addAction("bookService", Action.create("update3", 1));
		remotingApi.addAction("bookService", Action.create("update4", 1));
		remotingApi.addAction("bookService", Action.create("delete3", 1));
		remotingApi.addAction("bookService", Action.create("delete4", 1));
		remotingApi.addAction("bookService", Action.create("create3", 1));
		remotingApi.addAction("bookService", Action.create("create4", 1));
		remotingApi.addAction("bookSubAopService", Action.create("read", 1));
		remotingApi.addAction("bookSubAopService", Action.create("readWithPaging", 1));
		remotingApi.addAction("bookSubAopService", Action.create("update3", 1));
		remotingApi.addAction("bookSubAopService", Action.create("update4", 1));
		remotingApi.addAction("bookSubAopService", Action.create("delete3", 1));
		remotingApi.addAction("bookSubAopService", Action.create("delete4", 1));
		remotingApi.addAction("bookSubAopService", Action.create("create3", 1));
		remotingApi.addAction("bookSubAopService", Action.create("create4", 1));
		remotingApi.addAction("bookSubService", Action.create("read", 1));
		remotingApi.addAction("bookSubService", Action.create("readWithPaging", 1));
		remotingApi.addAction("bookSubService", Action.create("update3", 1));
		remotingApi.addAction("bookSubService", Action.create("update4", 1));
		remotingApi.addAction("bookSubService", Action.create("delete3", 1));
		remotingApi.addAction("bookSubService", Action.create("delete4", 1));
		remotingApi.addAction("bookSubService", Action.create("create3", 1));
		remotingApi.addAction("bookSubService", Action.create("create4", 1));

		remotingApi.addAction("remoteProviderSimpleNamed",
				Action.createNamed("method1", new ArrayList<String>(), null, null));
		remotingApi.addAction("remoteProviderSimpleNamed",
				Action.createNamed("method2", Arrays.asList("i", "d", "s"), null, null));
		remotingApi.addAction("remoteProviderSimpleNamed",
				Action.createNamed("method3", Arrays.asList("userName"), null, null));
		remotingApi.addAction("remoteProviderSimpleNamed",
				Action.createNamed("method4", Arrays.asList("a", "b"), null, null));
		remotingApi.addAction("remoteProviderSimpleNamed",
				Action.createNamed("method5", Arrays.asList("d"), null, null));
		remotingApi.addAction("remoteProviderSimpleNamed",
				Action.createNamed("method6", new ArrayList<String>(), null, null));
		remotingApi.addAction("remoteProviderSimpleNamed",
				Action.createNamed(
						"method7", Arrays.asList("flag", "aCharacter", "workflow", "aInt",
								"aLong", "aDouble", "aFloat", "aShort", "aByte"),
						null, null));
		remotingApi.addAction("remoteProviderSimpleNamed",
				Action.createNamed("method9", Arrays.asList("aRow"), null, null));
		remotingApi.addAction("remoteProviderSimpleNamed",
				Action.createNamed(
						"method10", Arrays.asList("flag", "aCharacter", "workflow",
								"aInt", "aLong", "aDouble", "aFloat", "aShort", "aByte"),
						null, null));

		remotingApi.addAction("remoteProviderSimpleNamed",
				Action.createNamed("method11",
						Arrays.asList("endDate", "normalParameter", "aDate", "percent"),
						null, null));

		remotingApi.addAction("remoteProviderSimpleNamed", Action.createNamed(
				"methodCollection1", Arrays.asList("name", "collections"), null, null));
		remotingApi.addAction("remoteProviderSimpleNamed", Action.createNamed(
				"methodCollection2", Arrays.asList("name", "collections"), null, null));
		remotingApi.addAction("remoteProviderSimpleNamed", Action.createNamed(
				"methodCollection3", Arrays.asList("name", "collections"), null, null));

		remotingApi.addAction("remoteProviderSimpleNamed", Action
				.createNamed("methodArray1", Arrays.asList("name", "array"), null, null));
		remotingApi.addAction("remoteProviderSimpleNamed", Action
				.createNamed("methodArray2", Arrays.asList("name", "array"), null, null));
		remotingApi.addAction("remoteProviderSimpleNamed", Action
				.createNamed("methodArray3", Arrays.asList("name", "array"), null, null));
		remotingApi.addAction("remoteProviderSimpleNamed", Action
				.createNamed("methodArray4", Arrays.asList("name", "array"), null, null));

		remotingApi.addAction("remoteProviderSimpleNamed", Action.createNamed("methodRP1",
				Arrays.asList("lastName", "theAge", "active"), null, null));
		remotingApi.addAction("remoteProviderSimpleNamed", Action.createNamed("methodRP2",
				Arrays.asList("lastName", "theAge", "active"), null, null));
		remotingApi.addAction("remoteProviderSimpleNamed", Action.createNamed("methodRP3",
				Arrays.asList("lastName", "theAge", "active"), null, null));

		remotingApi.addAction("remoteProviderSimpleNamed",
				Action.createNamed("withCookie", Arrays.asList("i"), null, null));
		remotingApi.addAction("remoteProviderSimpleNamed",
				Action.createNamed("withRequiredCookie", Arrays.asList("i"), null, null));
		remotingApi.addAction("remoteProviderSimpleNamed",
				Action.createNamed("withRequestHeader", Arrays.asList("bd"), null, null));
		remotingApi.addAction("remoteProviderSimpleNamed", Action.createNamed(
				"withRequiredRequestHeader", Arrays.asList("bd"), null, null));
		remotingApi.addAction("remoteProviderSimpleNamed", Action
				.createNamed("notBatched", Collections.<String>emptyList(), null, false));

		remotingApi.addAction("remoteProviderSimpleNamed", Action.createNamed(
				"nonStrictMethod1", Collections.<String>emptyList(), null, null));
		remotingApi.addAction("remoteProviderSimpleNamed", Action.createNamed(
				"nonStrictMethod2", Collections.<String>emptyList(), null, null));
		remotingApi.addAction("remoteProviderSimpleNamed", Action.createNamed(
				"nonStrictMethod3", Collections.<String>emptyList(), null, null));

		remotingApi.addPollingProvider(
				new PollingProvider("pollProvider", "handleMessage1", "message1"));
		remotingApi.addPollingProvider(
				new PollingProvider("pollProvider", "handleMessage2", "message2"));
		remotingApi.addPollingProvider(
				new PollingProvider("pollProvider", "handleMessage3", "message3"));
		remotingApi.addPollingProvider(
				new PollingProvider("pollProvider", "handleMessage4", "message4"));
		remotingApi.addPollingProvider(
				new PollingProvider("pollProvider", "handleMessage5", "message5"));
		remotingApi.addPollingProvider(
				new PollingProvider("pollProvider", "message6", "message6"));
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

		remotingApi.addAction("remoteProviderOptional", Action.create("method1", 3));
		remotingApi.addAction("remoteProviderOptional", Action.create("method2", 1));
		remotingApi.addAction("remoteProviderOptional", Action.create("method4", 2));
		remotingApi.addAction("remoteProviderOptional", Action.create("method5", 1));
		remotingApi.addAction("remoteProviderOptional", Action.create("method6", 0));
		remotingApi.addAction("remoteProviderOptional", Action.create("method7", 0));
		remotingApi.addAction("remoteProviderOptional", Action.create("method8", 1));
		remotingApi.addAction("remoteProviderOptional", Action.create("method9", 0));
		remotingApi.addAction("remoteProviderOptional", Action.create("method10", 3));
		remotingApi.addAction("remoteProviderOptional", Action.create("method11", 3));
		remotingApi.addAction("remoteProviderOptional", Action.create("method12", 3));
		remotingApi.addAction("remoteProviderOptional", Action.create("method13", 3));
		remotingApi.addAction("remoteProviderOptional", Action.create("method16", 0));
		remotingApi.addAction("remoteProviderOptional", Action.create("method17", 0));
		remotingApi.addAction("remoteProviderOptional", Action.create("method18", 0));
		remotingApi.addAction("remoteProviderOptional", Action.create("method19", 0));

		remotingApi.addPollingProvider(
				new PollingProvider("remoteProviderOptional", "opoll1", "opoll1"));
		remotingApi.addPollingProvider(
				new PollingProvider("remoteProviderOptional", "opoll2", "opoll2"));
		remotingApi.addPollingProvider(
				new PollingProvider("remoteProviderOptional", "opoll3", "opoll3"));
		remotingApi.addPollingProvider(
				new PollingProvider("remoteProviderOptional", "opoll4", "opoll4"));
		remotingApi.addPollingProvider(
				new PollingProvider("remoteProviderOptional", "opoll5", "opoll5"));

		remotingApi.addAction("remoteProviderOptional", Action.createNamed("namedMethod1",
				Arrays.asList("i", "d", "s"), null, null));
		remotingApi.addAction("remoteProviderOptional", Action.createNamed("namedMethod2",
				Arrays.asList("lastName", "theAge", "active"), null, null));
		remotingApi.addAction("remoteProviderOptional",
				Action.createNamed("namedMethod3", Arrays.asList("i"), null, null));
		remotingApi.addAction("remoteProviderOptional",
				Action.createNamed("namedMethod4", Arrays.asList("bd"), null, null));

		remotingApi.addAction("remoteProviderOptional", Action.create("storeRead1", 1));
		remotingApi.addAction("remoteProviderOptional", Action.create("storeRead2", 1));

		remotingApi.addAction("remoteProviderOptional", Action.create("treeLoad1", 1));
		remotingApi.addAction("remoteProviderOptional", Action.create("treeLoad2", 1));

		remotingApi.addAction("remoteProviderMetadata", Action.createTreeLoad("method1",
				1, Collections.singletonList("mp"), null));
		remotingApi.addAction("remoteProviderMetadata", Action.createTreeLoad("method2",
				1, Collections.singletonList("id"), null));
		remotingApi.addAction("remoteProviderMetadata", Action.createTreeLoad("method1Ed",
				1, Collections.singletonList("mp"), null));
		remotingApi.addAction("remoteProviderMetadata", Action.createTreeLoad("method2Ed",
				1, Collections.singletonList("id"), null));
		remotingApi.addAction("remoteProviderMetadata", Action.createTreeLoad("method3",
				1, Collections.singletonList("id"), null));
		remotingApi.addAction("remoteProviderMetadata", Action.createTreeLoad("method4",
				1, Collections.singletonList("id"), null));
		remotingApi.addAction("remoteProviderMetadata", Action.createTreeLoad("method5",
				1, Collections.singletonList("id"), null));
		remotingApi.addAction("remoteProviderMetadata", Action.createTreeLoad("method6",
				1, Collections.singletonList("id"), null));
		remotingApi.addAction("remoteProviderMetadata", Action.createTreeLoad("method5Ed",
				1, Collections.singletonList("id"), null));
		remotingApi.addAction("remoteProviderMetadata", Action.createTreeLoad("method6Ed",
				1, Collections.singletonList("id"), null));
		remotingApi.addAction("remoteProviderMetadata", Action.createTreeLoad("update1",
				1, Collections.singletonList("id"), null));
		remotingApi.addAction("remoteProviderMetadata", Action.createTreeLoad("update2",
				1, Collections.singletonList("id"), null));
		remotingApi.addAction("remoteProviderMetadata", Action.createTreeLoad("update3",
				1, Collections.singletonList("id"), null));
		remotingApi.addAction("remoteProviderMetadata", Action.createTreeLoad("treeLoad1",
				1, Collections.singletonList("id"), null));
		remotingApi.addAction("remoteProviderMetadata", Action.createTreeLoad("treeLoad2",
				1, Collections.singletonList("id"), null));
		remotingApi.addAction("remoteProviderMetadata", Action.createTreeLoad("treeLoad3",
				1, Collections.singletonList("id"), null));

		return remotingApi;
	}

	public static RemotingApi emptyGroupApis(String namespace) {
		RemotingApi remotingApi = new RemotingApi("remoting", "/router", namespace);
		remotingApi.addAction("remoteProviderSimple", Action.create("method2", 0));
		remotingApi.addAction("remoteProviderSimple", Action.create("method4b", 3));
		remotingApi.addAction("remoteProviderSimple", Action.create("method6", 2));
		remotingApi.addAction("remoteProviderSimple", Action.create("method7", 0));
		remotingApi.addAction("remoteProviderSimple", Action.create("method8", 1));
		remotingApi.addAction("remoteProviderSimple", Action.create("method10", 9));
		remotingApi.addAction("remoteProviderSimple", Action.create("method11", 0));
		remotingApi.addAction("remoteProviderSimple", Action.create("method11b", 0));
		remotingApi.addAction("remoteProviderSimple", Action.create("method12", 1));
		remotingApi.addAction("remoteProviderSimple", Action.create("method13", 9));
		remotingApi.addAction("remoteProviderSimple", Action.create("method14", 4));

		remotingApi.addAction("remoteProviderSimple", Action.create("method15", 2));
		remotingApi.addAction("remoteProviderSimple", Action.create("method16", 1));
		remotingApi.addAction("remoteProviderSimple", Action.create("method17", 0));
		remotingApi.addAction("remoteProviderSimple", Action.create("method18", 0));
		remotingApi.addAction("remoteProviderSimple", Action.create("method19", 1));
		remotingApi.addAction("remoteProviderSimple", Action.create("method20", 0));
		remotingApi.addAction("remoteProviderSimple", Action.create("method21", 3));
		remotingApi.addAction("remoteProviderSimple", Action.create("method22", 3));
		remotingApi.addAction("remoteProviderSimple", Action.create("method23", 3));
		remotingApi.addAction("remoteProviderSimple", Action.create("method24", 3));
		remotingApi.addAction("remoteProviderSimple", Action.create("method25", 3));
		remotingApi.addAction("remoteProviderSimple", Action.create("method26", 3));
		remotingApi.addAction("remoteProviderSimple", Action.create("method27", 0));
		remotingApi.addAction("remoteProviderSimple", Action.create("method28", 0));
		remotingApi.addAction("remoteProviderSimple", Action.create("method29", 0));
		remotingApi.addAction("remoteProviderSimple", Action.create("method30", 0));
		remotingApi.addAction("remoteProviderSimple",
				Action.create("method31", 1, false));
		remotingApi.addAction("remoteProviderSimple",
				Action.create("methodWithOptional", 1));

		remotingApi.addAction("remoteProviderStoreRead", Action.create("method1", 1));
		remotingApi.addAction("remoteProviderStoreRead", Action.create("method2", 1));
		remotingApi.addAction("remoteProviderStoreRead", Action.create("method3", 1));
		remotingApi.addAction("remoteProviderStoreRead", Action.create("method4", 1));
		remotingApi.addAction("remoteProviderStoreRead", Action.create("method4Ed", 1));
		remotingApi.addAction("remoteProviderStoreRead", Action.create("method8", 1));
		remotingApi.addAction("remoteProviderStoreRead", Action.create("method9", 1));
		remotingApi.addAction("remoteProviderStoreRead", Action.create("method10", 1));
		remotingApi.addAction("remoteProviderStoreRead", Action.create("method11", 1));
		remotingApi.addAction("remoteProviderStoreRead",
				Action.create("method12", 1, false));
		remotingApi.addAction("remoteProviderStoreRead", Action.create("method8Ed", 1));
		remotingApi.addAction("remoteProviderStoreRead", Action.create("method9Ed", 1));
		remotingApi.addAction("remoteProviderStoreRead", Action.create("method10Ed", 1));
		remotingApi.addAction("remoteProviderStoreRead", Action.create("method11Ed", 1));
		remotingApi.addAction("remoteProviderStoreRead",
				Action.create("method12Ed", 1, false));
		remotingApi.addAction("remoteProviderStoreRead",
				Action.create("methodFilter", 1));
		remotingApi.addAction("remoteProviderStoreRead",
				Action.create("methodMetadata", 1));
		remotingApi.addAction("remoteProviderStoreRead",
				Action.create("methodMetadataEd", 1));

		remotingApi.addAction("remoteProviderStoreModify", Action.create("create1", 1));
		remotingApi.addAction("remoteProviderStoreModify", Action.create("create2", 1));
		remotingApi.addAction("remoteProviderStoreModify", Action.create("update1", 1));
		remotingApi.addAction("remoteProviderStoreModify", Action.create("update2", 1));
		remotingApi.addAction("remoteProviderStoreModify", Action.create("update3", 1));

		remotingApi.addAction("remoteProviderStoreModifyArray",
				Action.create("create1", 1));
		remotingApi.addAction("remoteProviderStoreModifyArray",
				Action.create("create2", 1));
		remotingApi.addAction("remoteProviderStoreModifyArray",
				Action.create("update1", 1));
		remotingApi.addAction("remoteProviderStoreModifyArray",
				Action.create("update2", 1));
		remotingApi.addAction("remoteProviderStoreModifyArray",
				Action.create("update3", 1));

		remotingApi.addAction("remoteProviderStoreModifyInterface",
				Action.create("create1", 1));
		remotingApi.addAction("remoteProviderStoreModifyInterface",
				Action.create("create2", 1));
		remotingApi.addAction("remoteProviderStoreModifyInterface",
				Action.create("update1", 1));
		remotingApi.addAction("remoteProviderStoreModifyInterface",
				Action.create("update2", 1));
		remotingApi.addAction("remoteProviderStoreModifyInterface",
				Action.create("update3", 1));

		remotingApi.addAction("remoteProviderStoreModifySingle",
				Action.create("create1", 1));
		remotingApi.addAction("remoteProviderStoreModifySingle",
				Action.create("create2", 1));
		remotingApi.addAction("remoteProviderStoreModifySingle",
				Action.create("update1", 1));
		remotingApi.addAction("remoteProviderStoreModifySingle",
				Action.create("update2", 1));
		remotingApi.addAction("remoteProviderStoreModifySingle",
				Action.create("update3", 1));

		remotingApi.addAction("remoteProviderFormLoad", Action.create("method2", 1));
		remotingApi.addAction("remoteProviderFormLoad", Action.create("method3", 1));
		remotingApi.addAction("remoteProviderFormLoad", Action.create("method4", 1));
		remotingApi.addAction("remoteProviderFormLoad", Action.create("method6", 1));
		remotingApi.addAction("remoteProviderFormLoad", Action.create("method7", 1));
		remotingApi.addAction("remoteProviderFormLoad", Action.create("method6Ed", 1));
		remotingApi.addAction("remoteProviderFormLoad", Action.create("method7Ed", 1));

		remotingApi.addAction("remoteProviderTreeLoad", Action.create("method2", 1));
		remotingApi.addAction("remoteProviderTreeLoad", Action.create("method4", 1));
		remotingApi.addAction("remoteProviderTreeLoad", Action.create("method5", 1));
		remotingApi.addAction("remoteProviderTreeLoad", Action.create("method6", 1));
		remotingApi.addAction("remoteProviderTreeLoad",
				Action.create("method7", 1, false));

		remotingApi.addAction("formInfoController3", Action.create("updateInfoJson", 1));
		remotingApi.addAction("formInfoController3",
				Action.create("updateInfoJsonDirect", 1));
		remotingApi.addAction("formInfoController3",
				Action.create("updateInfoJsonDirectError", 1));

		remotingApi.addAction("formInfoController3",
				Action.create("updateInfoJsonEd", 1));
		remotingApi.addAction("formInfoController3",
				Action.create("updateInfoJsonDirectEd", 1));
		remotingApi.addAction("formInfoController3",
				Action.create("updateInfoJsonDirectErrorEd", 1));

		remotingApi.addPollingProvider(
				new PollingProvider("pollProvider", "handleMessage4", "message4"));

		return remotingApi;
	}

	@SuppressWarnings("unchecked")
	public static void compareJson(MvcResult result, RemotingApi remotingApi,
			ApiRequestParams params) throws IOException {
		String content = result.getResponse().getContentAsString();
		assertThat(result.getResponse().getContentType())
				.isEqualTo("application/json;charset=UTF-8");
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
			assertThat(rootAsMap.get("descriptor"))
					.isEqualTo(params.getApiNs() + "." + params.getRemotingApiVar());
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

	static void compare(MvcResult result, RemotingApi remotingApi,
			ApiRequestParams params) throws UnsupportedEncodingException {

		if (params.getConfiguration() == null
				|| !params.getConfiguration().isStreamResponse()) {
			assertThat(result.getResponse().getContentLength())
					.isEqualTo(result.getResponse().getContentAsByteArray().length);
		}

		compare(result.getResponse().getContentAsString(),
				result.getResponse().getContentType(), remotingApi, params);
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
			assertThat(contentType)
					.isEqualTo(params.getConfiguration().getJsContentType());
		}
		else {
			assertThat(contentType).isEqualTo("application/javascript");
		}
		assertThat(content).isNotEmpty();

		String[] lines = content.split("\n");

		String remotingApiLine;
		String pollingApiLine;

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

		if (StringUtils.hasText(apiNs)) {
			String extNsLine = "Ext.ns('" + apiNs + "');";
			assertContains(extNsLine, lines);

			remotingApiLine = apiNs + "." + remotingApiVar + " = {";
			pollingApiLine = apiNs + "." + pollingUrlsVar + " = {";
		}
		else {
			assertDoesNotContains("Ext.ns(", lines);
			remotingApiLine = remotingApiVar + " = {";
			pollingApiLine = pollingUrlsVar + " = {";
		}

		int startRemotingApi = assertContains(remotingApiLine, lines);

		int startPollingApi = lines.length;
		if (!remotingApi.getPollingProviders().isEmpty()) {
			startPollingApi = assertContains(pollingApiLine, lines);
		}
		else {
			assertDoesNotContains(pollingApiLine, lines);
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
			if (params.getConfiguration().getBufferLimit() != null) {
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
				assertThat(rootAsMap.get("timeout"))
						.isEqualTo(params.getConfiguration().getTimeout());
			}
			else {
				assertThat(rootAsMap.containsKey("timeout")).isFalse();
			}

			if (params.getConfiguration().getEnableBuffer() != null) {
				assertThat(rootAsMap.get("enableBuffer"))
						.isEqualTo(params.getConfiguration().getEnableBuffer());
			}
			else {
				assertThat(rootAsMap.containsKey("enableBuffer")).isFalse();
			}

			if (params.getConfiguration().getBufferLimit() != null) {
				assertThat(rootAsMap.get("bufferLimit"))
						.isEqualTo(params.getConfiguration().getBufferLimit());
			}
			else {
				assertThat(rootAsMap.containsKey("bufferLimit")).isFalse();
			}

			if (params.getConfiguration().getMaxRetries() != null) {
				assertThat(rootAsMap.get("maxRetries"))
						.isEqualTo(params.getConfiguration().getMaxRetries());
			}
			else {
				assertThat(rootAsMap.containsKey("maxRetries")).isFalse();
			}

			if (params.getConfiguration().getBufferLimit() != null) {
				assertThat(rootAsMap.get("bufferLimit"))
						.isEqualTo(params.getConfiguration().getBufferLimit());
			}
			else {
				assertThat(rootAsMap.containsKey("bufferLimit")).isFalse();
			}
		}
		else {
			assertThat(rootAsMap.get("timeout")).isNull();
			assertThat(rootAsMap.get("enableBuffer")).isNull();
			assertThat(rootAsMap.get("maxRetries")).isNull();
			assertThat(rootAsMap.get("bufferLimit")).isNull();
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
				assertEquals(String.format("%s/%s/%s/%s",
						remotingApi.getUrl().replace("router", "poll"), pp.getBeanName(),
						pp.getMethod(), pp.getEvent()), url);
			}
		}

	}

	public static void compareSse(List<String> expectedActions,
			Map<String, String> actions, String beanName, String url) {
		assertThat(actions).isNotEmpty();
		assertThat(actions).hasSize(expectedActions.size());
		for (String expectedAction : expectedActions) {
			String actionUrl = actions.get(expectedAction);
			assertThat(actionUrl)
					.isEqualTo(String.format("%s/%s/%s", url, beanName, expectedAction));
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

			if (expectedAction.getBatched() != null
					&& !expectedAction.getBatched().booleanValue()) {
				assertThat(action.get("batched")).isEqualTo(Boolean.FALSE);
			}
			else {
				assertThat(action.containsKey("batched")).isFalse();
			}

			if (expectedAction.getFormHandler() != null
					&& expectedAction.getFormHandler()) {
				assertThat(action.get("formHandler"))
						.isEqualTo(expectedAction.getFormHandler());
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
