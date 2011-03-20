/**
 * Copyright 2010-2011 Ralph Schaer <ralphschaer@gmail.com>
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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Before;
import org.junit.Test;

public class MyModelControlerTest {

	private HttpClient client;
	private HttpPost post;

	@Before
	public void beforeTest() {
		client = new DefaultHttpClient();
		post = new HttpPost("http://localhost:9998/controller/router");
	}

	

	@Test
	@SuppressWarnings("unchecked")
	public void testPost() throws ClientProtocolException, IOException {

		List<NameValuePair> formparams = new ArrayList<NameValuePair>();
		formparams.add(new BasicNameValuePair("extTID", "3"));
		formparams.add(new BasicNameValuePair("extAction", "myModelController"));
		formparams.add(new BasicNameValuePair("extMethod", "update"));
		formparams.add(new BasicNameValuePair("extType", "rpc"));
		formparams.add(new BasicNameValuePair("extUpload", "false"));
		formparams.add(new BasicNameValuePair("name", "Jim"));
		UrlEncodedFormEntity postEntity = new UrlEncodedFormEntity(formparams, "UTF-8");

		post.setEntity(postEntity);

		HttpResponse response = client.execute(post);
		HttpEntity entity = response.getEntity();
		assertNotNull(entity);
		String responseString = EntityUtils.toString(entity);

		ObjectMapper mapper = new ObjectMapper();
		Map<String, Object> rootAsMap = mapper.readValue(responseString, Map.class);
		assertEquals(5, rootAsMap.size());
		assertEquals("update", rootAsMap.get("method"));
		assertEquals("rpc", rootAsMap.get("type"));
		assertEquals("myModelController", rootAsMap.get("action"));
		assertEquals(3, rootAsMap.get("tid"));

		Map<String, Object> result = (Map<String, Object>) rootAsMap.get("result");
		assertEquals(true, result.get("success"));
	}
}
