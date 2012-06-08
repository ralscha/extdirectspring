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
package ch.ralscha.extdirectspring_itest;

import static org.fest.assertions.Assertions.assertThat;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.ContentBody;
import org.apache.http.entity.mime.content.InputStreamBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Test;

public class FileUploadControllerTest extends JettyTest {

	@SuppressWarnings("unchecked")
	@Test
	public void testUpload() throws ClientProtocolException, IOException {
		HttpClient client = new DefaultHttpClient();
		HttpPost post = new HttpPost("http://localhost:9998/controller/router");

		InputStream is = getClass().getResourceAsStream("/UploadTestFile.txt");

		MultipartEntity mpEntity = new MultipartEntity();
		ContentBody cbFile = new InputStreamBody(is, "text/plain", "UploadTestFile.txt");
		mpEntity.addPart("fileUpload", cbFile);
		mpEntity.addPart("extTID", new StringBody("2"));
		mpEntity.addPart("extAction", new StringBody("fileUploadController"));
		mpEntity.addPart("extMethod", new StringBody("uploadTest"));
		mpEntity.addPart("extType", new StringBody("rpc"));
		mpEntity.addPart("extUpload", new StringBody("true"));

		mpEntity.addPart("name", new StringBody("Jim"));
		mpEntity.addPart("age", new StringBody("25"));
		mpEntity.addPart("email", new StringBody("test@test.ch"));

		post.setEntity(mpEntity);
		HttpResponse response = client.execute(post);
		HttpEntity resEntity = response.getEntity();

		assertThat(resEntity).isNotNull();
		String responseString = EntityUtils.toString(resEntity);

		String prefix = "<html><body><textarea>";
		String postfix = "</textarea></body></html>";
		assertThat(responseString).startsWith(prefix);
		assertThat(responseString).endsWith(postfix);

		String json = responseString.substring(prefix.length(), responseString.length() - postfix.length());

		ObjectMapper mapper = new ObjectMapper();
		Map<String, Object> rootAsMap = mapper.readValue(json, Map.class);
		assertThat(rootAsMap).hasSize(5);
		assertThat(rootAsMap.get("method")).isEqualTo("uploadTest");
		assertThat(rootAsMap.get("type")).isEqualTo("rpc");
		assertThat(rootAsMap.get("action")).isEqualTo("fileUploadController");
		assertThat(rootAsMap.get("tid")).isEqualTo(2);

		Map<String, Object> result = (Map<String, Object>) rootAsMap.get("result");
		assertThat(result).hasSize(6);
		assertThat(result.get("name")).isEqualTo("Jim");
		assertThat(result.get("age")).isEqualTo(25);
		assertThat(result.get("email")).isEqualTo("test@test.ch");
		assertThat(result.get("fileName")).isEqualTo("UploadTestFile.txt");
		assertThat(result.get("fileContents")).isEqualTo("contents of upload file");
		assertThat(result.get("success")).isEqualTo(true);

		EntityUtils.consume(resEntity);
		client.getConnectionManager().shutdown();
		is.close();
	}

}
