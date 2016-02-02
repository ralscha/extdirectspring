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
package ch.ralscha.extdirectspring.bean;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockMultipartHttpServletRequest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;

import com.fasterxml.jackson.databind.ObjectMapper;

import ch.ralscha.extdirectspring.controller.ControllerUtil;

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration(locations = "classpath:/testApplicationContext.xml")
public class ExtDirectResponseBuilderTest {

	@Autowired
	private WebApplicationContext wac;

	@SuppressWarnings("unchecked")
	@Test
	public void testBuilder() {

		MockHttpServletRequest request = createRequest();

		MockHttpServletResponse servletResponse = new MockHttpServletResponse();
		ExtDirectResponseBuilder.create(request, servletResponse)
				.addResultProperty("additionalProperty", 11).buildAndWrite();

		ExtDirectResponse response = ControllerUtil
				.readDirectResponse(servletResponse.getContentAsByteArray());
		assertThat(response.getAction()).isEqualTo("action");
		assertThat(response.getMethod()).isEqualTo("method");
		assertThat(response.getType()).isEqualTo("type");
		assertThat(response.getTid()).isEqualTo(1);

		assertThat(response.getResult()).isNotNull();
		assertThat(response.getWhere()).isNull();
		assertThat(response.getMessage()).isNull();

		Map<String, Object> data = (Map<String, Object>) response.getResult();
		assertThat(data).hasSize(2);
		assertThat(data.get("additionalProperty")).isEqualTo(11);
		assertThat(data.get("success")).isEqualTo(Boolean.TRUE);

		servletResponse = new MockHttpServletResponse();
		ExtDirectResponseBuilder.create(request, servletResponse).unsuccessful()
				.addResultProperty("additionalProperty", 9).buildAndWrite();
		response = ControllerUtil
				.readDirectResponse(servletResponse.getContentAsByteArray());
		data = (Map<String, Object>) response.getResult();
		assertThat(data).hasSize(2);
		assertThat(data.get("additionalProperty")).isEqualTo(9);
		assertThat(data.get("success")).isEqualTo(Boolean.FALSE);
	}

	@Test
	public void testBuilderUploadResponse() throws IOException {

		MockMultipartHttpServletRequest request = new MockMultipartHttpServletRequest();
		request.setAttribute(DispatcherServlet.WEB_APPLICATION_CONTEXT_ATTRIBUTE,
				this.wac);

		request.setParameter("extAction", "action");
		request.setParameter("extMethod", "method");
		request.setParameter("extType", "type");
		request.setParameter("extTID", "1");

		MockHttpServletResponse servletResponse = new MockHttpServletResponse();
		ExtDirectResponseBuilder.create(request, servletResponse)
				.addResultProperty("additionalProperty", Boolean.FALSE)
				.addResultProperty("text", "a lot of &quot;text&quot;").buildAndWrite();

		assertThat(servletResponse.getContentType()).isEqualTo("text/html;charset=UTF-8");
		String content = servletResponse.getContentAsString();
		assertThat(servletResponse.getContentLength())
				.isEqualTo(content.getBytes("UTF-8").length);

		assertThat(content).startsWith("<html><body><textarea>");
		assertThat(content).endsWith("</textarea></body></html>");

		String json = content.substring(content.indexOf("{"),
				content.lastIndexOf("}") + 1);
		assertThat(json).contains("\\&quot;");
		json = json.replace("\\&quot;", "\'");
		ObjectMapper mapper = new ObjectMapper();

		Map<String, Object> header = mapper.readValue(json, Map.class);

		assertThat(header.get("action")).isEqualTo("action");
		assertThat(header.get("method")).isEqualTo("method");
		assertThat(header.get("type")).isEqualTo("type");
		assertThat(header.get("tid")).isEqualTo(1);

		@SuppressWarnings("unchecked")
		Map<String, Object> result = (Map<String, Object>) header.get("result");
		assertThat(result).hasSize(3);
		assertThat((Boolean) result.get("success")).isTrue();
		assertThat(result.get("text")).isEqualTo("a lot of 'text'");
		assertThat(result.get("additionalProperty")).isEqualTo(Boolean.FALSE);
	}

	@Test
	public void testBuilderSuccessful() {
		MockHttpServletRequest request = createRequest();

		MockHttpServletResponse servletResponse = new MockHttpServletResponse();
		ExtDirectResponseBuilder.create(request, servletResponse).successful()
				.buildAndWrite();

		checkResponse(servletResponse, true);
	}

	@Test
	public void testBuilderUnsuccessful() {
		MockHttpServletRequest request = createRequest();

		MockHttpServletResponse servletResponse = new MockHttpServletResponse();
		ExtDirectResponseBuilder.create(request, servletResponse).unsuccessful()
				.buildAndWrite();

		checkResponse(servletResponse, false);
	}

	@Test
	public void testBuilderSetSuccessTrue() {
		MockHttpServletRequest request = createRequest();

		MockHttpServletResponse servletResponse = new MockHttpServletResponse();
		ExtDirectResponseBuilder.create(request, servletResponse).setSuccess(true)
				.buildAndWrite();

		checkResponse(servletResponse, true);
	}

	@Test
	public void testBuilderSetSuccessFalse() {
		MockHttpServletRequest request = createRequest();

		MockHttpServletResponse servletResponse = new MockHttpServletResponse();
		ExtDirectResponseBuilder.create(request, servletResponse).setSuccess(false)
				.buildAndWrite();

		checkResponse(servletResponse, false);
	}

	private MockHttpServletRequest createRequest() {
		MockHttpServletRequest request = new MockHttpServletRequest();
		request.setAttribute(DispatcherServlet.WEB_APPLICATION_CONTEXT_ATTRIBUTE,
				this.wac);

		request.setParameter("extAction", "action");
		request.setParameter("extMethod", "method");
		request.setParameter("extType", "type");
		request.setParameter("extTID", "1");
		return request;
	}

	private static void checkResponse(MockHttpServletResponse servletResponse,
			boolean flag) {
		ExtDirectResponse response = ControllerUtil
				.readDirectResponse(servletResponse.getContentAsByteArray());
		assertThat(response.getAction()).isEqualTo("action");
		assertThat(response.getMethod()).isEqualTo("method");
		assertThat(response.getType()).isEqualTo("type");
		assertThat(response.getTid()).isEqualTo(1);

		assertThat(response.getResult()).isNotNull();
		assertThat(response.getWhere()).isNull();
		assertThat(response.getMessage()).isNull();

		@SuppressWarnings("unchecked")
		Map<String, Object> data = (Map<String, Object>) response.getResult();
		assertThat(data).hasSize(1);
		assertThat(data.get("success")).isEqualTo(flag);
	}

}
