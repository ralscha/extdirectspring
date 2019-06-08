/**
 * Copyright 2010-2018 the original author or authors.
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
import static org.assertj.core.api.Assertions.entry;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.forwardedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMultipartHttpServletRequestBuilder;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import ch.ralscha.extdirectspring.bean.ExtDirectResponse;
import ch.ralscha.extdirectspring.util.ExtDirectSpringUtil;

@ExtendWith(SpringExtension.class)
@WebAppConfiguration
@ContextConfiguration("classpath:/testApplicationContextN.xml")
public class RouterControllerFormPostTest {

	@Autowired
	private WebApplicationContext wac;

	private MockMvc mockMvc;

	@Autowired
	private ConfigurationService configurationService;

	@BeforeEach
	public void setupMockMvc() throws Exception {
		this.mockMvc = MockMvcBuilders.webAppContextSetup(this.wac).build();
	}

	@Test
	public void testCallNonExistsFormPostMethod() throws Exception {
		Map<String, String> parameters = new LinkedHashMap<>();
		parameters.put("extTID", "11");
		parameters.put("extAction", "remoteProviderSimple");
		parameters.put("extMethod", "method1");
		parameters.put("extType", "rpc");

		MvcResult result = ControllerUtil.performRouterRequest(this.mockMvc, null,
				parameters, null, null, false);
		ExtDirectResponse edsResponse = ControllerUtil
				.readDirectResponse(result.getResponse().getContentAsByteArray());

		assertThat(edsResponse.getType()).isEqualTo("exception");
		assertThat(edsResponse.getMessage()).isEqualTo("Server Error");
		assertThat(edsResponse.getWhere()).isNull();
		assertThat(edsResponse.getTid()).isEqualTo(11);
		assertThat(edsResponse.getAction()).isEqualTo("remoteProviderSimple");
		assertThat(edsResponse.getMethod()).isEqualTo("method1");
	}

	@Test
	public void testCallNonExistsFormPostMethodWithConfig() throws Exception {
		Configuration conf = new Configuration();
		conf.setDefaultExceptionMessage("something wrong");
		conf.setSendStacktrace(true);
		ReflectionTestUtils.setField(this.configurationService, "configuration", conf);
		this.configurationService.afterPropertiesSet();

		Map<String, String> parameters = new LinkedHashMap<>();
		parameters.put("extTID", "12");
		parameters.put("extAction", "remoteProviderSimple");
		parameters.put("extMethod", "method1");
		parameters.put("extType", "rpc");
		MvcResult result = ControllerUtil.performRouterRequest(this.mockMvc, null,
				parameters, null, null, false);
		ExtDirectResponse edsResponse = ControllerUtil
				.readDirectResponse(result.getResponse().getContentAsByteArray());

		assertThat(edsResponse.getType()).isEqualTo("exception");
		assertThat(edsResponse.getMessage()).isEqualTo("something wrong");
		assertThat(edsResponse.getWhere())
				.isEqualTo("Bean or Method 'remoteProviderSimple.method1' not found");

		assertThat(edsResponse.getTid()).isEqualTo(12);
		assertThat(edsResponse.getAction()).isEqualTo("remoteProviderSimple");
		assertThat(edsResponse.getMethod()).isEqualTo("method1");

		ReflectionTestUtils.setField(this.configurationService, "configuration",
				new Configuration());
		this.configurationService.afterPropertiesSet();
	}

	@Test
	public void testCallExistsFormPostMethod() throws Exception {
		MockHttpServletRequestBuilder request = post("/router").accept(MediaType.ALL)
				.contentType(MediaType.MULTIPART_FORM_DATA).characterEncoding("UTF-8");

		request.param("extTID", "12");
		request.param("extAction", "formInfoController");
		request.param("extMethod", "updateInfo");
		request.param("extType", "rpc");
		request.param("name", "Ralph");
		request.param("age", "20");
		request.param("admin", "true");
		request.param("salary", "12.3");
		request.param("result", "theResult");

		this.mockMvc.perform(request).andExpect(status().isOk())
				.andExpect(forwardedUrl("updateInfo"));
	}

	@Test
	public void testCallDirect() throws Exception {
		Map<String, String> parameters = new LinkedHashMap<>();
		parameters.put("extTID", "12");
		parameters.put("extAction", "formInfoController");
		parameters.put("extMethod", "updateInfoDirect");
		parameters.put("extType", "rpc");
		parameters.put("name", "Ralph");
		parameters.put("age", "20");
		parameters.put("admin", "true");
		parameters.put("salary", "12.3");
		parameters.put("result", "theResult");

		MvcResult resultMvc = ControllerUtil.performRouterRequest(this.mockMvc, null,
				parameters, null, null, false);
		ExtDirectResponse edsResponse = ControllerUtil
				.readDirectResponse(resultMvc.getResponse().getContentAsByteArray());

		assertThat(edsResponse.getType()).isEqualTo("rpc");
		assertThat(edsResponse.getMessage()).isNull();
		assertThat(edsResponse.getWhere()).isNull();
		assertThat(edsResponse.getTid()).isEqualTo(12);
		assertThat(edsResponse.getAction()).isEqualTo("formInfoController");
		assertThat(edsResponse.getMethod()).isEqualTo("updateInfoDirect");

		@SuppressWarnings("unchecked")
		Map<String, Object> result = (Map<String, Object>) edsResponse.getResult();
		assertThat(result).hasSize(6).contains(entry("name", "RALPH"), entry("age", 30),
				entry("admin", Boolean.FALSE), entry("salary", 1012.3),
				entry("result", "theResultRESULT"), entry("success", Boolean.TRUE));
	}

	@Test
	public void testCallDirectEd() throws Exception {
		Map<String, String> parameters = new LinkedHashMap<>();
		parameters.put("extTID", "12");
		parameters.put("extAction", "formInfoController");
		parameters.put("extMethod", "updateInfoDirectEd");
		parameters.put("extType", "rpc");
		parameters.put("name", "Ralph");
		parameters.put("age", "20");
		parameters.put("admin", "true");
		parameters.put("salary", "12.3");
		parameters.put("result", "theResult");

		MvcResult resultMvc = ControllerUtil.performRouterRequest(this.mockMvc, null,
				parameters, null, null, false);
		ExtDirectResponse edsResponse = ControllerUtil
				.readDirectResponse(resultMvc.getResponse().getContentAsByteArray());

		assertThat(edsResponse.getType()).isEqualTo("rpc");
		assertThat(edsResponse.getMessage()).isNull();
		assertThat(edsResponse.getWhere()).isNull();
		assertThat(edsResponse.getTid()).isEqualTo(12);
		assertThat(edsResponse.getAction()).isEqualTo("formInfoController");
		assertThat(edsResponse.getMethod()).isEqualTo("updateInfoDirectEd");

		@SuppressWarnings("unchecked")
		Map<String, Object> result = (Map<String, Object>) edsResponse.getResult();
		assertThat(result).hasSize(6).contains(entry("name", "RALPH"), entry("age", 30),
				entry("admin", Boolean.FALSE), entry("salary", 1012.3),
				entry("result", "theResultRESULT"), entry("success", Boolean.TRUE));
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testUpload() throws Exception {
		MockMultipartHttpServletRequestBuilder request = multipart("/router");
		request.accept(MediaType.ALL).characterEncoding("UTF-8")
				.session(new MockHttpSession());

		request.param("extTID", "1");
		request.param("extAction", "uploadService");
		request.param("extMethod", "upload");
		request.param("extType", "rpc");
		request.param("name", "Ralph");
		request.param("age", "20");
		request.param("admin", "true");
		request.param("salary", "12.3");
		request.param("result", "theResult");

		request.file("fileUpload", "the content of the file".getBytes());

		MvcResult resultMvc = this.mockMvc.perform(request).andExpect(status().isOk())
				.andExpect(content().contentType("text/html;charset=utf-8"))
				.andExpect(content().encoding("UTF-8")).andReturn();

		String response = resultMvc.getResponse().getContentAsString();
		String prefix = "<html><body><textarea>";
		String suffix = "</textarea></body></html>";
		assertThat(response).startsWith(prefix).endsWith(suffix);
		String json = response.substring(prefix.length(), response.indexOf(suffix));

		ExtDirectResponse edsResponse = ControllerUtil
				.readDirectResponse(json.getBytes(ExtDirectSpringUtil.UTF8_CHARSET));

		assertThat(edsResponse.getType()).isEqualTo("rpc");
		assertThat(edsResponse.getMessage()).isNull();
		assertThat(edsResponse.getWhere()).isNull();
		assertThat(edsResponse.getTid()).isEqualTo(1);
		assertThat(edsResponse.getAction()).isEqualTo("uploadService");
		assertThat(edsResponse.getMethod()).isEqualTo("upload");

		Map<String, Object> result = (Map<String, Object>) edsResponse.getResult();
		assertThat(result).hasSize(8);
		assertThat(result).contains(entry("e-mail", null), entry("age", 20),
				entry("name", "Ralph"), entry("fileName", ""),
				entry("fileContents", "the content of the file"),
				entry("firstName", null), entry("success", Boolean.TRUE));
		Map<String, Object> error = (Map<String, Object>) result.get("errors");
		assertThat(error).containsKey("email");
		assertThat((List<String>) error.get("email"))
				.containsExactly("must not be empty");
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testUploadEd() throws Exception {
		MockMultipartHttpServletRequestBuilder request = multipart("/router");
		request.accept(MediaType.ALL).characterEncoding("UTF-8")
				.session(new MockHttpSession());

		request.param("extTID", "1");
		request.param("extAction", "uploadService");
		request.param("extMethod", "uploadEd");
		request.param("extType", "rpc");
		request.param("name", "Ralph");
		request.param("age", "20");
		request.param("admin", "true");
		request.param("salary", "12.3");
		request.param("result", "theResult");

		request.file("fileUpload", "the content of the file".getBytes());

		MvcResult resultMvc = this.mockMvc.perform(request).andExpect(status().isOk())
				.andExpect(content().contentType("text/html;charset=utf-8"))
				.andExpect(content().encoding("UTF-8")).andReturn();

		String response = resultMvc.getResponse().getContentAsString();
		String prefix = "<html><body><textarea>";
		String suffix = "</textarea></body></html>";
		assertThat(response).startsWith(prefix).endsWith(suffix);
		String json = response.substring(prefix.length(), response.indexOf(suffix));

		ExtDirectResponse edsResponse = ControllerUtil
				.readDirectResponse(json.getBytes(ExtDirectSpringUtil.UTF8_CHARSET));

		assertThat(edsResponse.getType()).isEqualTo("rpc");
		assertThat(edsResponse.getMessage()).isNull();
		assertThat(edsResponse.getWhere()).isNull();
		assertThat(edsResponse.getTid()).isEqualTo(1);
		assertThat(edsResponse.getAction()).isEqualTo("uploadService");
		assertThat(edsResponse.getMethod()).isEqualTo("uploadEd");

		Map<String, Object> result = (Map<String, Object>) edsResponse.getResult();
		assertThat(result).hasSize(6);
		assertThat(result).contains(entry("age", 20), entry("name", "Ralph"),
				entry("fileName", ""), entry("fileContents", "the content of the file"),
				entry("success", Boolean.TRUE));
		Map<String, Object> error = (Map<String, Object>) result.get("errors");
		assertThat(error).containsKey("email");
		assertThat((List<String>) error.get("email"))
				.containsExactly("must not be empty");
	}

}
