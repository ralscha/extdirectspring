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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.junit.Test;

import com.fasterxml.jackson.databind.ObjectMapper;

import ch.ralscha.extdirectspring.bean.api.Action;
import ch.ralscha.extdirectspring.bean.api.RemotingApi;
import ch.ralscha.extdirectspring.controller.ApiControllerTest;
import ch.ralscha.extdirectspring.controller.ApiRequestParams;

public class InfoControllerTest extends JettyTest {

	private static RemotingApi api() {
		RemotingApi remotingApi = new RemotingApi("remoting", "/controller/router", null);
		remotingApi.addAction("infoController",
				Action.createFormHandler("updateInfo", 0));
		return remotingApi;
	}

	@Test
	public void testApi() throws IOException {
		CloseableHttpClient client = HttpClientBuilder.create().build();
		CloseableHttpResponse response = null;
		try {
			HttpGet g = new HttpGet(
					"http://localhost:9998/controller/api.js?group=itest_info");
			response = client.execute(g);
			String responseString = EntityUtils.toString(response.getEntity());
			String contentType = response.getFirstHeader("Content-Type").getValue();
			ApiControllerTest.compare(responseString, contentType, api(),
					ApiRequestParams.builder().build());
			SimpleServiceTest.assertCacheHeaders(response, false);
		}
		finally {
			IOUtils.closeQuietly(response);
			IOUtils.closeQuietly(client);
		}
	}

	@Test
	public void testApiDebug() throws IOException {
		CloseableHttpClient client = HttpClientBuilder.create().build();
		CloseableHttpResponse response = null;
		try {
			HttpGet g = new HttpGet(
					"http://localhost:9998/controller/api-debug.js?group=itest_info");
			response = client.execute(g);
			String responseString = EntityUtils.toString(response.getEntity());
			String contentType = response.getFirstHeader("Content-Type").getValue();
			ApiControllerTest.compare(responseString, contentType, api(),
					ApiRequestParams.builder().build());
			SimpleServiceTest.assertCacheHeaders(response, false);
		}
		finally {
			IOUtils.closeQuietly(response);
			IOUtils.closeQuietly(client);
		}
	}

	@Test
	public void testApiFingerprinted() throws IOException {
		CloseableHttpClient client = HttpClientBuilder.create().build();
		CloseableHttpResponse response = null;
		try {
			HttpGet g = new HttpGet(
					"http://localhost:9998/controller/api-1.2.1.js?group=itest_info");
			response = client.execute(g);
			String responseString = EntityUtils.toString(response.getEntity());
			String contentType = response.getFirstHeader("Content-Type").getValue();
			ApiControllerTest.compare(responseString, contentType, api(),
					ApiRequestParams.builder().build());
			SimpleServiceTest.assertCacheHeaders(response, true);
		}
		finally {
			IOUtils.closeQuietly(response);
			IOUtils.closeQuietly(client);
		}
	}

	@Test
	public void testPost() throws IOException {
		CloseableHttpClient client = HttpClientBuilder.create().build();
		CloseableHttpResponse response = null;
		try {
			HttpPost post = new HttpPost("http://localhost:9998/controller/router");

			List<NameValuePair> formparams = new ArrayList<NameValuePair>();
			formparams.add(new BasicNameValuePair("extTID", "1"));
			formparams.add(new BasicNameValuePair("extAction", "infoController"));
			formparams.add(new BasicNameValuePair("extMethod", "updateInfo"));
			formparams.add(new BasicNameValuePair("extType", "rpc"));
			formparams.add(new BasicNameValuePair("extUpload", "false"));
			formparams.add(new BasicNameValuePair("userName", "RALPH"));
			UrlEncodedFormEntity postEntity = new UrlEncodedFormEntity(formparams,
					"UTF-8");

			post.setEntity(postEntity);

			response = client.execute(post);
			HttpEntity entity = response.getEntity();
			assertThat(entity).isNotNull();
			String responseString = EntityUtils.toString(entity);

			ObjectMapper mapper = new ObjectMapper();
			Map<String, Object> rootAsMap = mapper.readValue(responseString, Map.class);
			assertThat(rootAsMap).hasSize(5);
			assertThat(rootAsMap.get("method")).isEqualTo("updateInfo");
			assertThat(rootAsMap.get("type")).isEqualTo("rpc");
			assertThat(rootAsMap.get("action")).isEqualTo("infoController");
			assertThat(rootAsMap.get("tid")).isEqualTo(1);

			@SuppressWarnings("unchecked")
			Map<String, Object> result = (Map<String, Object>) rootAsMap.get("result");
			assertThat(result).hasSize(2);
			assertThat(result.get("userNameLowerCase")).isEqualTo("ralph");
			assertThat(result.get("success")).isEqualTo(Boolean.TRUE);
		}
		finally {
			IOUtils.closeQuietly(response);
			IOUtils.closeQuietly(client);
		}
	}

}
