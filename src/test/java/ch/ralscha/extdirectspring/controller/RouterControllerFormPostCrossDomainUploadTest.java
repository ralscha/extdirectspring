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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMultipartHttpServletRequestBuilder;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import ch.ralscha.extdirectspring.bean.ExtDirectResponse;
import ch.ralscha.extdirectspring.util.ExtDirectSpringUtil;

@ExtendWith(SpringExtension.class)
@WebAppConfiguration
@ContextConfiguration("classpath:/testApplicationContextCrossDomainFileUpload.xml")
public class RouterControllerFormPostCrossDomainUploadTest {

	@Autowired
	private WebApplicationContext wac;

	private MockMvc mockMvc;

	@BeforeEach
	public void setupMockMvc() throws Exception {
		this.mockMvc = MockMvcBuilders.webAppContextSetup(this.wac).build();
	}

	@Test
	public void testUpload() throws Exception {
		MockMultipartHttpServletRequestBuilder request = multipart("/router");
		request.accept(MediaType.ALL).characterEncoding("UTF-8")
				.session(new MockHttpSession());

		request.param("extTID", "1");
		request.param("extAction", "uploadService");
		request.param("extMethod", "upload");
		request.param("extType", "rpc");
		request.param("result", "theResult");

		request.file("fileUpload", "the content of the file".getBytes());

		MvcResult resultMvc = this.mockMvc.perform(request).andExpect(status().isOk())
				.andExpect(content().contentTypeCompatibleWith("text/html;charset=utf-8"))
				.andExpect(content().encoding("UTF-8")).andReturn();

		String response = resultMvc.getResponse().getContentAsString();
		String prefix = "<html><body><textarea>";
		String suffix = "</textarea><script type=\"text/javascript\">document.domain = 'rootdomain.com';</script></body></html>";
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
	}

	@Test
	public void testUploadEd() throws Exception {
		MockMultipartHttpServletRequestBuilder request = multipart("/router");
		request.accept(MediaType.ALL).characterEncoding("UTF-8")
				.session(new MockHttpSession());

		request.param("extTID", "1");
		request.param("extAction", "uploadService");
		request.param("extMethod", "uploadEd");
		request.param("extType", "rpc");
		request.param("result", "theResult");

		request.file("fileUpload", "the content of the file".getBytes());

		MvcResult resultMvc = this.mockMvc.perform(request).andExpect(status().isOk())
				.andExpect(content().contentTypeCompatibleWith("text/html;charset=utf-8"))
				.andExpect(content().encoding("UTF-8")).andReturn();

		String response = resultMvc.getResponse().getContentAsString();
		String prefix = "<html><body><textarea>";
		String suffix = "</textarea><script type=\"text/javascript\">document.domain = 'rootdomain.com';</script></body></html>";
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
	}
}
