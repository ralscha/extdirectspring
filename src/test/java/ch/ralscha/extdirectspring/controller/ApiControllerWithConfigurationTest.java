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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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
public class ApiControllerWithConfigurationTest {

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

	private void updateConfiguration(ApiRequestParams params,
			Configuration preConfiguredConfig) {
		Configuration config;
		if (preConfiguredConfig == null) {
			config = new Configuration();
		}
		else {
			config = preConfiguredConfig;
		}

		if (params.getActionNs() != null) {
			config.setActionNs(params.getActionNs());
		}

		if (params.getApiNs() != null) {
			config.setApiNs(params.getApiNs());
		}

		if (params.getBaseRouterUrl() != null) {
			config.setBaseRouterUrl(params.getBaseRouterUrl());
		}

		if (params.getPollingUrlsVar() != null) {
			config.setPollingUrlsVar(params.getPollingUrlsVar());
		}

		if (params.getRemotingApiVar() != null) {
			config.setRemotingApiVar(params.getRemotingApiVar());
		}

		if (params.isFullRouterUrl() != null) {
			config.setFullRouterUrl(params.isFullRouterUrl());
		}

		ReflectionTestUtils.setField(this.configurationService, "configuration", config);
		this.configurationService.afterPropertiesSet();
	}

	@Test
	public void testNoActionNamespaceDebugDefaultConfig() throws Exception {
		ApiRequestParams params = ApiRequestParams.builder().apiNs("test")
				.remotingApiVar("remotingApiVar").pollingUrlsVar("pollingUrlsVar")
				.build();
		updateConfiguration(params, null);

		runTest(this.mockMvc, params, ApiControllerTest.allApis(null));
	}

	@Test
	public void testNoActionNamespaceDebugCustomConfig() throws Exception {
		Configuration config = new Configuration();
		config.setEnableBuffer(10);
		config.setMaxRetries(2);
		config.setTimeout(12000);

		ApiRequestParams params = ApiRequestParams.builder().apiNs("testC")
				.remotingApiVar("remotingApiV").pollingUrlsVar("pollingUrlsV")
				.configuration(config).build();
		updateConfiguration(params, config);

		runTest(this.mockMvc, params, ApiControllerTest.allApis(null));
	}

	@Test
	public void testWithActionNamespaceDefaultConfig() throws Exception {
		ApiRequestParams params = ApiRequestParams.builder().apiNs("Ext.ns")
				.actionNs("actionns").remotingApiVar("TEST_REMOTING_API")
				.pollingUrlsVar("TEST_POLLING_URLS").build();
		updateConfiguration(params, null);
		runTest(this.mockMvc, params, ApiControllerTest.allApis("actionns"));
	}

	@Test
	public void testWithActionNamespaceCustomConfig() throws Exception {
		Configuration config = new Configuration();
		config.setEnableBuffer(Boolean.FALSE);
		config.setTimeout(10000);

		ApiRequestParams params = ApiRequestParams.builder().apiNs("Ext.ns")
				.actionNs("actionns").remotingApiVar("TEST_REMOTING_API")
				.pollingUrlsVar("TEST_POLLING_URLS").configuration(config).build();
		updateConfiguration(params, config);
		runTest(this.mockMvc, params, ApiControllerTest.allApis("actionns"));
	}

	@Test
	public void testEmptyGroupDefaultConfig() throws Exception {
		ApiRequestParams params = ApiRequestParams.builder().apiNs("test")
				.remotingApiVar("TEST_REMOTING_API").pollingUrlsVar("TEST_POLLING_URLS")
				.group("").build();
		updateConfiguration(params, null);
		runTest(this.mockMvc, params, ApiControllerTest.emptyGroupApis(null));
	}

	@Test
	public void testBlankStringGroupDefaultConfig() throws Exception {
		ApiRequestParams params = ApiRequestParams.builder().apiNs("test")
				.remotingApiVar("TEST_REMOTING_API").pollingUrlsVar("TEST_POLLING_URLS")
				.group("     ").build();
		updateConfiguration(params, null);
		runTest(this.mockMvc, params, ApiControllerTest.emptyGroupApis(null));
	}

	@Test
	public void testEmptyGroupCustomConfig() throws Exception {
		Configuration config = new Configuration();
		config.setEnableBuffer(Boolean.TRUE);
		config.setTimeout(33333);

		ApiRequestParams params = ApiRequestParams.builder().apiNs("test")
				.remotingApiVar("TEST_REMOTING_API").pollingUrlsVar("TEST_POLLING_URLS")
				.group("").configuration(config).build();
		updateConfiguration(params, config);
		runTest(this.mockMvc, params, ApiControllerTest.emptyGroupApis(null));
	}

	@Test
	public void testBlankStringGroupCustomConfig() throws Exception {
		Configuration config = new Configuration();
		config.setEnableBuffer(Boolean.TRUE);
		config.setTimeout(33333);
		config.setBufferLimit(null);

		ApiRequestParams params = ApiRequestParams.builder().apiNs("test")
				.remotingApiVar("TEST_REMOTING_API").pollingUrlsVar("TEST_POLLING_URLS")
				.group("        ").configuration(config).build();
		updateConfiguration(params, config);
		runTest(this.mockMvc, params, ApiControllerTest.emptyGroupApis(null));
	}

	@Test
	public void testBlankStringGroupCustomConfigBufferLimit() throws Exception {
		Configuration config = new Configuration();
		config.setEnableBuffer(Boolean.TRUE);
		config.setTimeout(222);
		config.setBufferLimit(5);

		ApiRequestParams params = ApiRequestParams.builder().apiNs("test")
				.remotingApiVar("TEST_REMOTING_API").pollingUrlsVar("TEST_POLLING_URLS")
				.group("        ").configuration(config).build();
		updateConfiguration(params, config);
		runTest(this.mockMvc, params, ApiControllerTest.emptyGroupApis(null));
	}

	@Test
	public void testUnknownGroupDefaultConfig() throws Exception {
		ApiRequestParams params = ApiRequestParams.builder().apiNs("test")
				.remotingApiVar("TEST_REMOTING_API").pollingUrlsVar("TEST_POLLING_URLS")
				.group("xy").build();
		updateConfiguration(params, null);
		runTest(this.mockMvc, params, ApiControllerTest.noApis(null));
	}

	@Test
	public void testUnknownGroupCustomConfig() throws Exception {
		Configuration config = new Configuration();
		config.setEnableBuffer(Boolean.TRUE);

		ApiRequestParams params = ApiRequestParams.builder().apiNs("test")
				.remotingApiVar("TEST_REMOTING_API").pollingUrlsVar("TEST_POLLING_URLS")
				.group("xy").configuration(config).build();
		updateConfiguration(params, config);
		runTest(this.mockMvc, params, ApiControllerTest.noApis(null));
	}

	@Test
	public void testGroup1() throws Exception {
		ApiRequestParams params = ApiRequestParams.builder().apiNs("Ext.ns")
				.actionNs("actionns").group("group1").configuration(null).build();
		updateConfiguration(params, null);

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
		ApiRequestParams params = ApiRequestParams.builder().apiNs("Ext.ns")
				.actionNs("actionns").group("group1").configuration(config).build();
		updateConfiguration(params, config);

		testGroup1(config, null);
		testGroup1(config, null);
		this.apiCache.clear();
		testGroup1(config, "-1.0.0");
	}

	private void testGroup1(Configuration config, String fingerprint) throws Exception {
		ApiRequestParams params = ApiRequestParams.builder().apiNs("Ext.ns")
				.actionNs("actionns").group("group1").configuration(config).build();

		doTest("/api-debug-doc.js", params, ApiControllerTest.group1Apis("actionns"));
		doTest("/api-debug.js", params, ApiControllerTest.group1Apis("actionns"));

		if (fingerprint == null) {
			doTest("/api.js", params, ApiControllerTest.group1Apis("actionns"));
		}
		else {
			MvcResult result = doTest("/api" + fingerprint + ".js", params,
					ApiControllerTest.group1Apis("actionns"));

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
		updateConfiguration(params, null);
		runTest(this.mockMvc, params, ApiControllerTest.group2Apis(null));
		runTest(this.mockMvc, params, ApiControllerTest.group2Apis(null));
	}

	@Test
	public void testGroup3() throws Exception {
		ApiRequestParams params = ApiRequestParams.builder().apiNs("Extns").actionNs("ns")
				.remotingApiVar("RAPI").pollingUrlsVar("PURLS").group("group3").build();
		updateConfiguration(params, null);
		runTest(this.mockMvc, params, ApiControllerTest.group3Apis("ns"));
		runTest(this.mockMvc, params, ApiControllerTest.group3Apis("ns"));
	}

	@Test
	public void testGroup4() throws Exception {
		ApiRequestParams params = ApiRequestParams.builder().apiNs("test").actionNs("")
				.remotingApiVar("TEST_REMOTING_API").pollingUrlsVar("TEST_POLLING_URLS")
				.group("group4").build();
		updateConfiguration(params, null);
		runTest(this.mockMvc, params, ApiControllerTest.group4Apis(null));

		params = ApiRequestParams.builder().apiNs("test")
				.remotingApiVar("TEST_REMOTING_API").pollingUrlsVar("TEST_POLLING_URLS")
				.group("group4").build();
		runTest(this.mockMvc, params, ApiControllerTest.group4Apis(null));
	}

	@Test
	public void testGroup1and2() throws Exception {
		ApiRequestParams params = ApiRequestParams.builder().apiNs("Ext.ns")
				.actionNs("actionns").group("group1,group2").build();
		updateConfiguration(params, null);
		runTest(this.mockMvc, params, ApiControllerTest.group1and2Apis("actionns"));
	}

	@Test
	public void testGroup1andUnknown() throws Exception {
		ApiRequestParams params = ApiRequestParams.builder().apiNs("Ext.ns")
				.actionNs("actionns").group("group1,unknown").build();
		updateConfiguration(params, null);
		runTest(this.mockMvc, params, ApiControllerTest.group1Apis("actionns"));
	}

	@Test
	public void testInterfaceGroup() throws Exception {
		ApiRequestParams params = ApiRequestParams.builder().apiNs("test").actionNs("")
				.remotingApiVar("TEST_REMOTING_API").pollingUrlsVar("TEST_POLLING_URLS")
				.group("interface").build();
		updateConfiguration(params, null);
		runTest(this.mockMvc, params, ApiControllerTest.interfaceApis(null));

		params = ApiRequestParams.builder().apiNs("test")
				.remotingApiVar("TEST_REMOTING_API").pollingUrlsVar("TEST_POLLING_URLS")
				.group("interface").build();
		updateConfiguration(params, null);
		runTest(this.mockMvc, params, ApiControllerTest.interfaceApis(null));
	}

	@Test
	public void testNoApiNs() throws Exception {
		ApiRequestParams params = ApiRequestParams.builder().apiNs("").actionNs("")
				.remotingApiVar("TEST_REMOTING_API").pollingUrlsVar("TEST_POLLING_URLS")
				.group("group4").build();
		updateConfiguration(params, null);
		runTest(this.mockMvc, params, ApiControllerTest.group4Apis(null));

		params = ApiRequestParams.builder().apiNs("").remotingApiVar("TEST_REMOTING_API")
				.pollingUrlsVar("TEST_POLLING_URLS").group("group4").build();
		updateConfiguration(params, null);
		runTest(this.mockMvc, params, ApiControllerTest.group4Apis(null));
	}

	@Test
	public void testFullRouterUrl() throws Exception {
		ApiRequestParams params = ApiRequestParams.builder().apiNs("apiNs").actionNs("")
				.remotingApiVar("TEST_RMT_API").pollingUrlsVar("TEST_POLL_URLS")
				.fullRouterUrl(Boolean.TRUE).group("group2").build();
		updateConfiguration(params, null);
		runTest(this.mockMvc, params,
				ApiControllerTest.group2Apis(null, "http://localhost/router"));

		params = ApiRequestParams.builder().apiNs("apiNs").remotingApiVar("TEST_RMT_API")
				.pollingUrlsVar("TEST_POLL_URLS").fullRouterUrl(Boolean.TRUE)
				.group("group2").build();
		updateConfiguration(params, null);
		runTest(this.mockMvc, params,
				ApiControllerTest.group2Apis(null, "http://localhost/router"));

		params = ApiRequestParams.builder().apiNs("apiNs").actionNs("")
				.remotingApiVar("TEST_RMT_API").pollingUrlsVar("TEST_POLL_URLS")
				.fullRouterUrl(Boolean.FALSE).group("group2").build();
		updateConfiguration(params, null);
		runTest(this.mockMvc, params, ApiControllerTest.group2Apis(null, "/router"));

		params = ApiRequestParams.builder().apiNs("apiNs").remotingApiVar("TEST_RMT_API")
				.pollingUrlsVar("TEST_POLL_URLS").fullRouterUrl(Boolean.FALSE)
				.group("group2").build();
		updateConfiguration(params, null);
		runTest(this.mockMvc, params, ApiControllerTest.group2Apis(null, "/router"));
	}

	@Test
	public void testFormat() throws Exception {
		ApiRequestParams params = ApiRequestParams.builder().actionNs("").apiNs("apiNs")
				.remotingApiVar("TEST_RMT_API").pollingUrlsVar("TEST_POLL_URLS")
				.group("group2").format("json").build();
		updateConfiguration(params, null);
		runTest(this.mockMvc, params,
				ApiControllerTest.group2Apis(null, "http://localhost/router"));

		params = ApiRequestParams.builder().actionNs("ns").apiNs("")
				.remotingApiVar("TEST_RMT_API").pollingUrlsVar("TEST_POLL_URLS")
				.group("group2").format("json").fullRouterUrl(Boolean.TRUE).build();
		updateConfiguration(params, null);
		runTest(this.mockMvc, params,
				ApiControllerTest.group2Apis("ns", "http://localhost/router"));
	}

	@Test
	public void testBaseRouterUrl() throws Exception {
		ApiRequestParams params = ApiRequestParams.builder().actionNs("").apiNs("an")
				.remotingApiVar("rapi").pollingUrlsVar("papi").group("group2")
				.baseRouterUrl("test").build();
		updateConfiguration(params, null);
		runTest(this.mockMvc, params, ApiControllerTest.group2Apis(null, "test/router"));
	}

	@Test
	public void testBaseRouterUrlWithEndingSlash() throws Exception {
		ApiRequestParams params = ApiRequestParams.builder().actionNs("").apiNs("an")
				.remotingApiVar("rapi").pollingUrlsVar("papi").group("group2")
				.fullRouterUrl(Boolean.TRUE).baseRouterUrl("service/test/").build();
		updateConfiguration(params, null);
		runTest(this.mockMvc, params,
				ApiControllerTest.group2Apis(null, "service/test/router"));
	}

	@Test
	public void testBaseRouterUrlEmptyString() throws Exception {
		ApiRequestParams params = ApiRequestParams.builder().actionNs("").apiNs("an")
				.remotingApiVar("rapi").pollingUrlsVar("papi").group("group2")
				.baseRouterUrl("").build();
		updateConfiguration(params, null);
		runTest(this.mockMvc, params, ApiControllerTest.group2Apis(null, "/router"));
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

	private static MvcResult doTest(MockMvc mockMvc, String url, ApiRequestParams params,
			RemotingApi expectedApi) throws Exception {
		MockHttpServletRequestBuilder request = get(url).accept(MediaType.ALL)
				.characterEncoding("UTF-8");

		if (params.getFormat() != null) {
			request.param("format", params.getFormat());
		}
		if (params.getGroup() != null) {
			request.param("group", params.getGroup());
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
			ApiControllerTest.compareJson(result, expectedApi, params);
		}
		else {
			ApiControllerTest.compare(result, expectedApi, params);
		}

		return result;
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

		Configuration configuration = params.getConfiguration();
		if (configuration != null) {
			assertThat(contentType).isEqualTo(configuration.getJsContentType());
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
			ApiControllerTest.assertContains(extNsLine, lines);

			remotingApiLine = apiNs + "." + remotingApiVar + " = {";
			pollingApiLine = apiNs + "." + pollingUrlsVar + " = {";
		}
		else {
			ApiControllerTest.assertDoesNotContains("Ext.ns(", lines);
			remotingApiLine = remotingApiVar + " = {";
			pollingApiLine = pollingUrlsVar + " = {";
		}

		int startRemotingApi = ApiControllerTest.assertContains(remotingApiLine, lines);

		int startPollingApi = lines.length;
		if (!remotingApi.getPollingProviders().isEmpty()) {
			startPollingApi = ApiControllerTest.assertContains(pollingApiLine, lines);
		}
		else {
			ApiControllerTest.assertDoesNotContains(pollingApiLine, lines);
		}

		if (remotingApi.getNamespace() != null) {
			String actionNs = "Ext.ns('" + remotingApi.getNamespace() + "');";
			ApiControllerTest.assertContains(actionNs, lines);
		}

		String remotingJson = "{";
		for (int i = startRemotingApi + 1; i < startPollingApi; i++) {
			remotingJson += lines[i];
		}

		String pollingJson = "{";
		if (!remotingApi.getPollingProviders().isEmpty()) {
			for (int i = startPollingApi + 1; i < startPollingApi; i++) {
				pollingJson += lines[i];
			}
		}

		int noOfconfigOptions = 0;
		if (configuration != null) {
			if (configuration.getTimeout() != null) {
				noOfconfigOptions++;
			}
			if (configuration.getEnableBuffer() != null) {
				noOfconfigOptions++;
			}
			if (configuration.getMaxRetries() != null) {
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

		if (configuration != null) {
			if (configuration.getTimeout() != null) {
				assertThat(rootAsMap.get("timeout"))
						.isEqualTo(configuration.getTimeout());
			}
			else {
				assertThat(rootAsMap.get("timeout")).isNull();
			}

			if (configuration.getEnableBuffer() != null) {
				assertThat(rootAsMap.get("enableBuffer"))
						.isEqualTo(configuration.getEnableBuffer());
			}
			else {
				assertThat(rootAsMap.get("enableBuffer")).isNull();
			}

			if (configuration.getMaxRetries() != null) {
				assertThat(rootAsMap.get("maxRetries"))
						.isEqualTo(configuration.getMaxRetries());
			}
			else {
				assertThat(rootAsMap.get("maxRetries")).isNull();
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
			ApiControllerTest.compare(expectedActions, actions);
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

}
