/**
 * Copyright 2010 Ralph Schaer <ralphschaer@gmail.com>
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

package ch.ralscha.extdirectspring.itest;

import static org.junit.Assert.*;

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

public class UploadControllerTest {

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
		mpEntity.addPart("extAction", new StringBody("uploadController"));
		mpEntity.addPart("extMethod", new StringBody("uploadTest"));
		mpEntity.addPart("extType", new StringBody("rpc"));
		mpEntity.addPart("extUpload", new StringBody("true"));

		mpEntity.addPart("name", new StringBody("Jim"));
		mpEntity.addPart("age", new StringBody("25"));
		mpEntity.addPart("email", new StringBody("test@test.ch"));

		post.setEntity(mpEntity);
		HttpResponse response = client.execute(post);
		HttpEntity resEntity = response.getEntity();

		assertNotNull(resEntity);
		String responseString = EntityUtils.toString(resEntity);

		String prefix = "<html><body><textarea>";
		String postfix = "</textarea></body></html>";
		assertTrue(responseString.startsWith(prefix));
		assertTrue(responseString.endsWith(postfix));

		String json = responseString.substring(prefix.length(), responseString.length() - postfix.length());

		ObjectMapper mapper = new ObjectMapper();
		Map<String, Object> rootAsMap = mapper.readValue(json, Map.class);
		assertEquals(5, rootAsMap.size());
		assertEquals("uploadTest", rootAsMap.get("method"));
		assertEquals("rpc", rootAsMap.get("type"));
		assertEquals("uploadController", rootAsMap.get("action"));
		assertEquals(2, rootAsMap.get("tid"));

		Map<String, Object> result = (Map<String, Object>) rootAsMap.get("result");
		assertEquals(6, result.size());
		assertEquals("Jim", result.get("name"));
		assertEquals(25, result.get("age"));
		assertEquals("test@test.ch", result.get("email"));
		assertEquals("UploadTestFile.txt", result.get("fileName"));
		assertEquals("contents of upload file", result.get("fileContents"));
		assertEquals(true, result.get("success"));

		resEntity.consumeContent();
		client.getConnectionManager().shutdown();
		is.close();
	}

}
