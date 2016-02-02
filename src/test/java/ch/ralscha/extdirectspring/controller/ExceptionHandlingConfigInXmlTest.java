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

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import ch.ralscha.extdirectspring.bean.ExtDirectResponse;

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration("classpath:/testExceptionHandlingConfig.xml")
public class ExceptionHandlingConfigInXmlTest {

	@Autowired
	private WebApplicationContext wac;

	private MockMvc mockMvc;

	@Before
	public void setupMockMvc() throws Exception {
		this.mockMvc = MockMvcBuilders.webAppContextSetup(this.wac).build();
	}

	@Test
	public void testExceptionInMapping() throws Exception {

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
		assertThat(resp.getMessage()).isEqualTo("there is something wrong");
		assertThat(resp.getResult()).isNull();
		assertThat(resp.getWhere())
				.startsWith("java.lang.NumberFormatException: For input string: \"xxx\"");
	}

	@Test
	public void testBeanOrMethodNotFound() throws Exception {

		String edRequest = ControllerUtil.createEdsRequest("remoteProviderSimple2",
				"method4", 2, new Object[] { 3, 2.5, "string.param" });

		MvcResult result = ControllerUtil.performRouterRequest(this.mockMvc, edRequest);
		List<ExtDirectResponse> responses = ControllerUtil
				.readDirectResponses(result.getResponse().getContentAsByteArray());

		assertThat(responses).hasSize(1);
		ExtDirectResponse resp = responses.get(0);
		assertThat(resp.getAction()).isEqualTo("remoteProviderSimple2");
		assertThat(resp.getMethod()).isEqualTo("method4");
		assertThat(resp.getType()).isEqualTo("exception");
		assertThat(resp.getTid()).isEqualTo(2);
		assertThat(resp.getMessage()).isEqualTo("Panic!!!");
		assertThat(resp.getResult()).isNull();
		assertThat(resp.getWhere())
				.startsWith("Bean or Method 'remoteProviderSimple2.method4' not found");

	}

	@Test
	public void testExceptionInMappingWithNullValue() throws Exception {
		String edRequest = ControllerUtil.createEdsRequest("remoteProviderSimple",
				"method11b", 3, null);

		MvcResult result = ControllerUtil.performRouterRequest(this.mockMvc, edRequest);
		List<ExtDirectResponse> responses = ControllerUtil
				.readDirectResponses(result.getResponse().getContentAsByteArray());

		assertThat(responses).hasSize(1);
		ExtDirectResponse resp = responses.get(0);
		assertThat(resp.getAction()).isEqualTo("remoteProviderSimple");
		assertThat(resp.getMethod()).isEqualTo("method11b");
		assertThat(resp.getType()).isEqualTo("exception");
		assertThat(resp.getTid()).isEqualTo(3);
		assertThat(resp.getMessage()).isEqualTo("not supported");
		assertThat(resp.getResult()).isNull();
		assertThat(resp.getWhere()).startsWith("java.lang.UnsupportedOperationException");
	}

	@Test
	public void testExceptionNotInMapping() throws Exception {
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
		assertThat(resp.getMessage()).isEqualTo("Panic!!!");
		assertThat(resp.getResult()).isNull();
		assertThat(resp.getWhere()).startsWith("java.lang.NullPointerException");

	}
}
