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
package ch.ralscha.extdirectspring.itest;

import static org.fest.assertions.Assertions.assertThat;
import static org.fest.assertions.MapAssert.entry;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Before;
import org.junit.Test;

public class MyModelControlerTest extends JettyTest {

	private HttpClient client;

	private HttpPost post;

	@Before
	public void beforeTest() {
		client = new DefaultHttpClient();
		post = new HttpPost("http://localhost:9998/controller/router");
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testApi() throws ClientProtocolException, IOException {
		HttpGet g = new HttpGet("http://localhost:9998/controller/api.js?group=itest_base");
		HttpResponse response = client.execute(g);

		String responseString = EntityUtils.toString(response.getEntity());

		assertThat(responseString).startsWith("Ext.ns('Ext.app');");

		int openBracePos = responseString.indexOf("{");
		int closeBracePos = responseString.lastIndexOf("}");

		ObjectMapper mapper = new ObjectMapper();
		Map<String, Object> api = mapper
				.readValue(responseString.substring(openBracePos, closeBracePos + 1), Map.class);

		assertThat(api).hasSize(3);
		assertThat(api).includes(entry("type", "remoting"));
		assertThat(api).includes(entry("url", "/controller/router"));

		Map<String, Object> actions = (Map<String, Object>) api.get("actions");
		assertThat(actions).hasSize(1);
		List<Map<String, Object>> actionList = (List<Map<String, Object>>) actions.get("myModelController");
		assertThat(actionList).hasSize(3);

		for (Map<String, Object> map : actionList) {
			assertThat((Boolean) map.get("formHandler")).isTrue();
			assertThat((Integer) map.get("len")).isZero();
			assertThat((String) map.get("name")).isIn("method1", "method2", "update");
		}
	}

	@Test
	public void testPost() throws ClientProtocolException, IOException {
		callMethod("update");
		callMethod("method1");
		callMethod("method2");
	}

	@SuppressWarnings("unchecked")
	private void callMethod(String method) throws UnsupportedEncodingException, IOException, ClientProtocolException,
			JsonParseException, JsonMappingException {
		List<NameValuePair> formparams = new ArrayList<NameValuePair>();
		formparams.add(new BasicNameValuePair("extTID", "3"));
		formparams.add(new BasicNameValuePair("extAction", "myModelController"));
		formparams.add(new BasicNameValuePair("extMethod", method));
		formparams.add(new BasicNameValuePair("extType", "rpc"));
		formparams.add(new BasicNameValuePair("extUpload", "false"));
		formparams.add(new BasicNameValuePair("name", "Jim"));
		UrlEncodedFormEntity postEntity = new UrlEncodedFormEntity(formparams, "UTF-8");

		post.setEntity(postEntity);

		HttpResponse response = client.execute(post);
		HttpEntity entity = response.getEntity();
		assertThat(entity).isNotNull();
		String responseString = EntityUtils.toString(entity);

		ObjectMapper mapper = new ObjectMapper();
		Map<String, Object> rootAsMap = mapper.readValue(responseString, Map.class);
		assertThat(rootAsMap).hasSize(5);
		assertThat(rootAsMap.get("method")).isEqualTo(method);
		assertThat(rootAsMap.get("type")).isEqualTo("rpc");
		assertThat(rootAsMap.get("action")).isEqualTo("myModelController");
		assertThat(rootAsMap.get("tid")).isEqualTo(3);

		Map<String, Object> result = (Map<String, Object>) rootAsMap.get("result");
		assertThat((Boolean) result.get("success")).isTrue();
	}
}
