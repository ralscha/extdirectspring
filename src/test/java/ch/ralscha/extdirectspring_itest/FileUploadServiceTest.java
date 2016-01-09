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
package ch.ralscha.extdirectspring_itest;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.ContentBody;
import org.apache.http.entity.mime.content.InputStreamBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.junit.Test;

import com.fasterxml.jackson.databind.ObjectMapper;

public class FileUploadServiceTest extends JettyTest {

	@Test
	public void testUpload() throws IOException {
		CloseableHttpClient client = HttpClientBuilder.create().build();
		CloseableHttpResponse response = null;
		try {

			HttpPost post = new HttpPost("http://localhost:9998/controller/router");

			InputStream is = getClass().getResourceAsStream("/UploadTestFile.txt");

			MultipartEntityBuilder builder = MultipartEntityBuilder.create();
			ContentBody cbFile = new InputStreamBody(is, ContentType.create("text/plain"),
					"UploadTestFile.txt");
			builder.addPart("fileUpload", cbFile);
			builder.addPart("extTID", new StringBody("2", ContentType.DEFAULT_TEXT));
			builder.addPart("extAction",
					new StringBody("fileUploadService", ContentType.DEFAULT_TEXT));
			builder.addPart("extMethod",
					new StringBody("uploadTest", ContentType.DEFAULT_TEXT));
			builder.addPart("extType", new StringBody("rpc", ContentType.DEFAULT_TEXT));
			builder.addPart("extUpload",
					new StringBody("true", ContentType.DEFAULT_TEXT));

			builder.addPart("name", new StringBody("Jimöäü",
					ContentType.create("text/plain", Charset.forName("UTF-8"))));
			builder.addPart("firstName",
					new StringBody("Ralph", ContentType.DEFAULT_TEXT));
			builder.addPart("age", new StringBody("25", ContentType.DEFAULT_TEXT));
			builder.addPart("email",
					new StringBody("test@test.ch", ContentType.DEFAULT_TEXT));

			post.setEntity(builder.build());
			response = client.execute(post);
			HttpEntity resEntity = response.getEntity();

			assertThat(resEntity).isNotNull();
			String responseString = EntityUtils.toString(resEntity);

			String prefix = "<html><body><textarea>";
			String postfix = "</textarea></body></html>";
			assertThat(responseString).startsWith(prefix);
			assertThat(responseString).endsWith(postfix);

			String json = responseString.substring(prefix.length(),
					responseString.length() - postfix.length());

			ObjectMapper mapper = new ObjectMapper();
			Map<String, Object> rootAsMap = mapper.readValue(json, Map.class);
			assertThat(rootAsMap).hasSize(5);
			assertThat(rootAsMap.get("method")).isEqualTo("uploadTest");
			assertThat(rootAsMap.get("type")).isEqualTo("rpc");
			assertThat(rootAsMap.get("action")).isEqualTo("fileUploadService");
			assertThat(rootAsMap.get("tid")).isEqualTo(2);

			@SuppressWarnings("unchecked")
			Map<String, Object> result = (Map<String, Object>) rootAsMap.get("result");
			assertThat(result).hasSize(7);
			assertThat(result.get("name")).isEqualTo("Jimöäü");
			assertThat(result.get("firstName")).isEqualTo("Ralph");
			assertThat(result.get("age")).isEqualTo(25);
			assertThat(result.get("e-mail")).isEqualTo("test@test.ch");
			assertThat(result.get("fileName")).isEqualTo("UploadTestFile.txt");
			assertThat(result.get("fileContents")).isEqualTo("contents of upload file");
			assertThat(result.get("success")).isEqualTo(Boolean.TRUE);

			EntityUtils.consume(resEntity);

			is.close();
		}
		finally {
			IOUtils.closeQuietly(response);
			IOUtils.closeQuietly(client);
		}
	}

}
