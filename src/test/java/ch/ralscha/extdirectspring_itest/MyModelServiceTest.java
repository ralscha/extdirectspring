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
import org.junit.Before;
import org.junit.Test;

import ch.ralscha.extdirectspring.bean.api.Action;
import ch.ralscha.extdirectspring.bean.api.RemotingApi;
import ch.ralscha.extdirectspring.controller.ApiControllerTest;
import ch.ralscha.extdirectspring.util.ApiCache;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class MyModelServiceTest extends JettyTest {

	private HttpClient client;

	private HttpPost post;

	@Before
	public void beforeTest() {
		client = new DefaultHttpClient();
		post = new HttpPost("http://localhost:9998/controller/router");
	}

	private static RemotingApi api() {
		RemotingApi remotingApi = new RemotingApi("/controller/router", null);
		remotingApi.addAction("myModelService", new Action("method1", 0, true));
		remotingApi.addAction("myModelService", new Action("method2", 0, true));
		remotingApi.addAction("myModelService", new Action("update", 0, true));
		return remotingApi;
	}

	@Test
	public void testApi() throws ClientProtocolException, IOException {
		HttpGet g = new HttpGet("http://localhost:9998/controller/api.js?group=itest_base_service");
		HttpResponse response = client.execute(g);
		String responseString = EntityUtils.toString(response.getEntity());
		String contentType = response.getFirstHeader("Content-Type").getValue();
		ApiControllerTest.compare(responseString, contentType, api(), "Ext.app", "REMOTING_API", "POLLING_URLS", "SSE");
		SimpleServiceTest.assertCacheHeaders(response, false);
		ApiCache.INSTANCE.clear();
	}

	@Test
	public void testApiDebug() throws ClientProtocolException, IOException {
		HttpGet g = new HttpGet("http://localhost:9998/controller/api-debug.js?group=itest_base_service");
		HttpResponse response = client.execute(g);
		String responseString = EntityUtils.toString(response.getEntity());
		String contentType = response.getFirstHeader("Content-Type").getValue();
		ApiControllerTest.compare(responseString, contentType, api(), "Ext.app", "REMOTING_API", "POLLING_URLS", "SSE");
		SimpleServiceTest.assertCacheHeaders(response, false);
		ApiCache.INSTANCE.clear();
	}

	@Test
	public void testApiFingerprinted() throws ClientProtocolException, IOException {
		HttpGet g = new HttpGet("http://localhost:9998/controller/api-1.1.1.js?group=itest_base_service");
		HttpResponse response = client.execute(g);
		String responseString = EntityUtils.toString(response.getEntity());
		String contentType = response.getFirstHeader("Content-Type").getValue();
		ApiControllerTest.compare(responseString, contentType, api(), "Ext.app", "REMOTING_API", "POLLING_URLS", "SSE");
		SimpleServiceTest.assertCacheHeaders(response, true);
		ApiCache.INSTANCE.clear();
	}

	@Test
	public void testPost() throws ClientProtocolException, IOException {
		callMethod("update");
		callMethod("method1");
		callMethod("method2");
	}

	private void callMethod(String method) throws UnsupportedEncodingException, IOException, ClientProtocolException,
			JsonParseException, JsonMappingException {
		List<NameValuePair> formparams = new ArrayList<NameValuePair>();
		formparams.add(new BasicNameValuePair("extTID", "3"));
		formparams.add(new BasicNameValuePair("extAction", "myModelService"));
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
		assertThat(rootAsMap.get("action")).isEqualTo("myModelService");
		assertThat(rootAsMap.get("tid")).isEqualTo(3);

		Map<String, Object> result = (Map<String, Object>) rootAsMap.get("result");
		assertThat((Boolean) result.get("success")).isTrue();
	}
}
