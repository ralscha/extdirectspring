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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import ch.ralscha.extdirectspring.bean.ExtDirectResponse;

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration("classpath:/testApplicationContext.xml")
public class ExceptionHandlingTest {

	@Autowired
	private WebApplicationContext wac;

	private MockMvc mockMvc;

	@Autowired
	private ConfigurationService configurationService;

	@Before
	public void setupMockMvc() throws Exception {
		this.mockMvc = MockMvcBuilders.webAppContextSetup(this.wac).build();
	}

	@Test
	public void testDefault() throws Exception {
		ExtDirectResponse resp = runTest(new Configuration());
		assertThat(resp.getMessage()).isEqualTo("Server Error");
		assertThat(resp.getWhere()).isNull();
	}

	@Test
	public void testDefaultExceptionMessage() throws Exception {
		Configuration configuration = new Configuration();
		configuration.setDefaultExceptionMessage("an error occured");
		ExtDirectResponse resp = runTest(configuration);
		assertThat(resp.getMessage()).isEqualTo("an error occured");
		assertThat(resp.getWhere()).isNull();
	}

	@Test
	public void testExceptionNameAsMessage() throws Exception {
		Configuration configuration = new Configuration();
		configuration.setSendExceptionMessage(true);
		ExtDirectResponse resp = runTest(configuration);
		assertThat(resp.getMessage()).isEqualTo("For input string: \"xxx\"");
		assertThat(resp.getWhere()).isNull();
	}

	@Test
	public void testExceptionToMessage() throws Exception {
		Configuration configuration = new Configuration();
		Map<Class<?>, String> exceptionMessageMapping = new HashMap<Class<?>, String>();
		exceptionMessageMapping.put(NullPointerException.class, "null pointer");
		configuration.setExceptionToMessage(exceptionMessageMapping);
		ExtDirectResponse resp = runTest11(configuration);
		assertThat(resp.getMessage()).isEqualTo("null pointer");
		assertThat(resp.getWhere()).isNull();
	}

	@Test
	public void testExceptionToMessageNullValue() throws Exception {
		Configuration configuration = new Configuration();
		configuration.setSendExceptionMessage(false);
		Map<Class<?>, String> exceptionMessageMapping = new HashMap<Class<?>, String>();
		exceptionMessageMapping.put(NumberFormatException.class, null);
		configuration.setExceptionToMessage(exceptionMessageMapping);
		ExtDirectResponse resp = runTest(configuration);
		assertThat(resp.getMessage()).isEqualTo("For input string: \"xxx\"");
		assertThat(resp.getWhere()).isNull();
	}

	@Test
	public void testDefaultExceptionMessageWithStacktrace() throws Exception {
		Configuration configuration = new Configuration();
		configuration.setSendStacktrace(true);
		configuration.setDefaultExceptionMessage("an error occured");
		ExtDirectResponse resp = runTest(configuration);
		assertThat(resp.getMessage()).isEqualTo("an error occured");
		assertThat(resp.getWhere()).startsWith("java.lang.NumberFormatException");
	}

	@Test
	public void testExceptionNameAsMessageWithStacktrace() throws Exception {
		Configuration configuration = new Configuration();
		configuration.setSendStacktrace(true);
		configuration.setSendExceptionMessage(true);
		ExtDirectResponse resp = runTest(configuration);
		assertThat(resp.getMessage()).isEqualTo("For input string: \"xxx\"");
		assertThat(resp.getWhere()).startsWith("java.lang.NumberFormatException");

	}

	@Test
	public void testExceptionToMessageWithStacktrace() throws Exception {
		Configuration configuration = new Configuration();
		configuration.setSendStacktrace(true);
		Map<Class<?>, String> exceptionMessageMapping = new HashMap<Class<?>, String>();
		exceptionMessageMapping.put(NullPointerException.class, "null pointer");
		configuration.setExceptionToMessage(exceptionMessageMapping);
		ExtDirectResponse resp = runTest11(configuration);
		assertThat(resp.getMessage()).isEqualTo("null pointer");
		assertThat(resp.getWhere()).startsWith("java.lang.NullPointerException");
	}

	@Test
	public void testExceptionToMessageNullValueWithStacktrace() throws Exception {
		Configuration configuration = new Configuration();
		configuration.setSendExceptionMessage(false);
		configuration.setSendStacktrace(true);
		Map<Class<?>, String> exceptionMessageMapping = new HashMap<Class<?>, String>();
		exceptionMessageMapping.put(NumberFormatException.class, null);
		configuration.setExceptionToMessage(exceptionMessageMapping);
		ExtDirectResponse resp = runTest(configuration);
		assertThat(resp.getMessage()).isEqualTo("For input string: \"xxx\"");
		assertThat(resp.getWhere()).startsWith("java.lang.NumberFormatException");
	}

	private ExtDirectResponse runTest(Configuration configuration) throws Exception {
		ReflectionTestUtils.setField(this.configurationService, "configuration",
				configuration);
		this.configurationService.afterPropertiesSet();

		String edRequest = ControllerUtil.createEdsRequest("remoteProviderSimple",
				"method4b", 2, new Object[] { 3, "xxx", "string.param" });
		MvcResult result = ControllerUtil.performRouterRequest(this.mockMvc, edRequest);
		List<ExtDirectResponse> responses = ControllerUtil
				.readDirectResponses(result.getResponse().getContentAsByteArray());

		assertThat(responses).hasSize(1);
		ExtDirectResponse resp = responses.get(0);
		assertThat(resp.getAction()).isEqualTo("remoteProviderSimple");
		assertThat(resp.getMethod()).isEqualTo("method4b");
		assertThat(resp.getType()).isEqualTo("exception");
		assertThat(resp.getTid()).isEqualTo(2);
		assertThat(resp.getResult()).isNull();

		ReflectionTestUtils.setField(this.configurationService, "configuration",
				new Configuration());
		this.configurationService.afterPropertiesSet();

		return resp;
	}

	private ExtDirectResponse runTest11(Configuration configuration) throws Exception {
		ReflectionTestUtils.setField(this.configurationService, "configuration",
				configuration);
		this.configurationService.afterPropertiesSet();

		String edRequest = ControllerUtil.createEdsRequest("remoteProviderSimple",
				"method11", 3, null);
		MvcResult result = ControllerUtil.performRouterRequest(this.mockMvc, edRequest);
		List<ExtDirectResponse> responses = ControllerUtil
				.readDirectResponses(result.getResponse().getContentAsByteArray());

		assertThat(responses).hasSize(1);
		ExtDirectResponse resp = responses.get(0);
		assertThat(resp.getAction()).isEqualTo("remoteProviderSimple");
		assertThat(resp.getMethod()).isEqualTo("method11");
		assertThat(resp.getType()).isEqualTo("exception");
		assertThat(resp.getTid()).isEqualTo(3);
		assertThat(resp.getResult()).isNull();

		ReflectionTestUtils.setField(this.configurationService, "configuration",
				new Configuration());
		this.configurationService.afterPropertiesSet();

		return resp;
	}

}
