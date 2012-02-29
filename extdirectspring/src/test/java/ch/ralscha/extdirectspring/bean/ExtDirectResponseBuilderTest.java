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
package ch.ralscha.extdirectspring.bean;

import static org.fest.assertions.Assertions.assertThat;

import java.io.IOException;
import java.util.Map;

import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

/**
 * Tests for {@link ExtDirectResponseBuilder}.
 * 
 * @author Ralph Schaer
 */
@SuppressWarnings("all")
public class ExtDirectResponseBuilderTest {

	@Test
	public void testBuilder() {

		MockHttpServletRequest request = new MockHttpServletRequest();
		request.setParameter("extAction", "action");
		request.setParameter("extMethod", "method");
		request.setParameter("extType", "type");
		request.setParameter("extTID", "1");

		ExtDirectResponseBuilder builder = new ExtDirectResponseBuilder(request);

		builder.addResultProperty("additionalProperty", 11);

		ExtDirectResponse response = builder.build();

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
		assertThat(data.get("success")).isEqualTo(true);

		builder.unsuccessful();
		response = builder.build();
		data = (Map<String, Object>) response.getResult();
		assertThat(data).hasSize(2);
		assertThat(data.get("additionalProperty")).isEqualTo(11);
		assertThat(data.get("success")).isEqualTo(false);
	}

	@Test
	public void testBuilderUploadResponse() throws IOException {

		MockHttpServletRequest request = new MockHttpServletRequest();
		request.setParameter("extAction", "action");
		request.setParameter("extMethod", "method");
		request.setParameter("extType", "type");
		request.setParameter("extTID", "1");

		ExtDirectResponseBuilder builder = new ExtDirectResponseBuilder(request);
		builder.addResultProperty("additionalProperty", false);
		builder.addResultProperty("text", "a lot of &quot;text&quot;");

		MockHttpServletResponse response = new MockHttpServletResponse();
		builder.buildAndWriteUploadResponse(response);

		assertThat(response.getContentType()).isEqualTo("text/html");
		String content = response.getContentAsString();
		assertThat(content.startsWith("<html><body><textarea>")).isTrue();
		assertThat(content.endsWith("</textarea></body></html>")).isTrue();

		String json = content.substring(content.indexOf("{"), content.lastIndexOf("}") + 1);
		assertThat(json.contains("\\&quot;")).isTrue();
		json = json.replace("\\&quot;", "\'");
		ObjectMapper mapper = new ObjectMapper();
		Map<String, Object> header = mapper.readValue(json, Map.class);

		assertThat(header.get("action")).isEqualTo("action");
		assertThat(header.get("method")).isEqualTo("method");
		assertThat(header.get("type")).isEqualTo("type");
		assertThat(header.get("tid")).isEqualTo(1);

		Map<String, Object> result = (Map<String, Object>) header.get("result");
		assertThat(result).hasSize(3);
		assertThat((Boolean) result.get("success")).isTrue();
		assertThat(result.get("text")).isEqualTo("a lot of 'text'");
		assertThat(result.get("additionalProperty")).isEqualTo(false);
	}

}
