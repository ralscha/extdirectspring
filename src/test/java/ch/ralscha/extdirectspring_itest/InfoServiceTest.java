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
import static org.assertj.core.data.MapEntry.entry;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
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
import org.assertj.core.data.MapEntry;
import org.junit.Test;

import com.fasterxml.jackson.databind.ObjectMapper;

import ch.ralscha.extdirectspring.bean.api.Action;
import ch.ralscha.extdirectspring.bean.api.RemotingApi;
import ch.ralscha.extdirectspring.controller.ApiControllerTest;
import ch.ralscha.extdirectspring.controller.ApiRequestParams;

public class InfoServiceTest extends JettyTest {

	private static RemotingApi api() {
		RemotingApi remotingApi = new RemotingApi("remoting", "/controller/router", null);
		remotingApi.addAction("infoService", Action.createFormHandler("updateInfo", 0));
		remotingApi.addAction("infoService",
				Action.createFormHandler("updateInfo2nd", 0));

		remotingApi.addAction("infoService",
				Action.createFormHandler("updateInfoUser1", 0));
		remotingApi.addAction("infoService",
				Action.createFormHandler("updateInfoUser2", 0));
		remotingApi.addAction("infoService",
				Action.createFormHandler("updateInfoUser3", 0));
		remotingApi.addAction("infoService",
				Action.createFormHandler("updateInfoUser4", 0));
		remotingApi.addAction("infoService",
				Action.createFormHandler("updateInfoUser5", 0));

		remotingApi.sort();

		return remotingApi;
	}

	@Test
	public void testApi() throws IOException {
		CloseableHttpClient client = HttpClientBuilder.create().build();
		CloseableHttpResponse response = null;
		try {
			HttpGet g = new HttpGet(
					"http://localhost:9998/controller/api.js?group=itest_info_service");
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
					"http://localhost:9998/controller/api-debug.js?group=itest_info_service");
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
					"http://localhost:9998/controller/api-1.2.1.js?group=itest_info_service");
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
	public void testPostFirst() throws IOException {
		testInfoPost("updateInfo");
	}

	@Test
	public void testPostSecond() throws IOException {
		testInfoPost("updateInfo2nd");
	}

	@Test
	public void testUpdateInfoUser1() throws IOException {

		Locale.setDefault(Locale.US);

		testUserPost("updateInfoUser1", "not a well-formed email address",
				entry("lc", "ralph"), entry("success", Boolean.FALSE));
	}

	@Test
	public void testUpdateInfoUser2() throws IOException {
		Locale.setDefault(Locale.GERMAN);
		testUserPost("updateInfoUser2", "keine gültige E-Mail-Adresse",
				entry("lc", "ralph"), entry("success", Boolean.FALSE));

	}

	@Test
	public void testUpdateInfoUser3() throws IOException {
		Locale.setDefault(Locale.US);
		testUserPost("updateInfoUser3", "Wrong E-Mail", entry("lc", "ralph"),
				entry("success", Boolean.FALSE));
	}

	@Test
	public void testUpdateInfoUser4() throws IOException {
		Locale.setDefault(Locale.US);
		testUserPost("updateInfoUser4", "Wrong E-Mail", entry("lc", "ralph"),
				entry("success", Boolean.TRUE));
	}

	@Test
	public void testUpdateInfoUser5() throws IOException {
		Locale.setDefault(Locale.US);
		testUserPost("updateInfoUser5", "Wrong E-Mail", entry("lc", "ralph"),
				entry("success", Boolean.FALSE));
	}

	@SuppressWarnings("unchecked")
	private static void testUserPost(String method, String errorMsg, MapEntry... entries)
			throws IOException {
		CloseableHttpClient client = HttpClientBuilder.create().build();
		CloseableHttpResponse response = null;
		try {
			HttpPost post = new HttpPost("http://localhost:9998/controller/router");

			List<NameValuePair> formparams = new ArrayList<NameValuePair>();
			formparams.add(new BasicNameValuePair("extTID", "1"));
			formparams.add(new BasicNameValuePair("extAction", "infoService"));
			formparams.add(new BasicNameValuePair("extMethod", method));
			formparams.add(new BasicNameValuePair("extType", "rpc"));
			formparams.add(new BasicNameValuePair("extUpload", "false"));
			formparams.add(new BasicNameValuePair("name", "RALPH"));
			formparams.add(new BasicNameValuePair("firstName", "firstName"));
			formparams.add(new BasicNameValuePair("age", "1"));
			formparams.add(new BasicNameValuePair("email", "invalidEmail"));

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
			assertThat(rootAsMap.get("method")).isEqualTo(method);
			assertThat(rootAsMap.get("type")).isEqualTo("rpc");
			assertThat(rootAsMap.get("action")).isEqualTo("infoService");
			assertThat(rootAsMap.get("tid")).isEqualTo(1);

			Map<String, Object> result = (Map<String, Object>) rootAsMap.get("result");

			int resultSize = entries.length;
			if (errorMsg != null) {
				resultSize += 1;
			}
			assertThat(result).hasSize(resultSize);
			assertThat(result).contains(entries);

			Map<String, Object> errors = (Map<String, Object>) result.get("errors");
			if (errorMsg != null) {
				assertThat(errors).isNotNull();
				assertThat(((List<String>) errors.get("email")).get(0))
						.isEqualTo(errorMsg);
			}
			else {
				assertThat(errors).isNull();
			}
		}
		finally {
			IOUtils.closeQuietly(response);
			IOUtils.closeQuietly(client);
		}
	}

	@SuppressWarnings("unchecked")
	private static void testInfoPost(String method) throws IOException {
		CloseableHttpClient client = HttpClientBuilder.create().build();
		CloseableHttpResponse response = null;
		try {
			HttpPost post = new HttpPost("http://localhost:9998/controller/router");

			List<NameValuePair> formparams = new ArrayList<NameValuePair>();
			formparams.add(new BasicNameValuePair("extTID", "1"));
			formparams.add(new BasicNameValuePair("extAction", "infoService"));
			formparams.add(new BasicNameValuePair("extMethod", method));
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
			assertThat(rootAsMap.get("method")).isEqualTo(method);
			assertThat(rootAsMap.get("type")).isEqualTo("rpc");
			assertThat(rootAsMap.get("action")).isEqualTo("infoService");
			assertThat(rootAsMap.get("tid")).isEqualTo(1);

			Map<String, Object> result = (Map<String, Object>) rootAsMap.get("result");
			assertThat(result).hasSize(2);
			assertThat(result.get("user-name-lower-case")).isEqualTo("ralph");
			assertThat(result.get("success")).isEqualTo(Boolean.TRUE);
		}
		finally {
			IOUtils.closeQuietly(response);
			IOUtils.closeQuietly(client);
		}
	}

}
