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
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.databene.contiperf.PerfTest;
import org.databene.contiperf.junit.ContiPerfRule;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.junit.Rule;
import org.junit.Test;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import ch.ralscha.extdirectspring.bean.api.Action;
import ch.ralscha.extdirectspring.bean.api.PollingProvider;
import ch.ralscha.extdirectspring.bean.api.RemotingApi;
import ch.ralscha.extdirectspring.controller.ApiControllerTest;
import ch.ralscha.extdirectspring.controller.ApiRequestParams;

public class SimpleServiceTest extends JettyTest2 {

	@Rule
	public ContiPerfRule i = new ContiPerfRule();

	private final static AtomicInteger id = new AtomicInteger();

	private final static ObjectMapper mapper = new ObjectMapper();

	private static RemotingApi api() {
		RemotingApi remotingApi = new RemotingApi("remoting", "/controller/router", null);
		remotingApi.addAction("simpleService", Action.create("toUpperCase", 1));
		remotingApi.addAction("simpleService", Action.createNamed("echo",
				Arrays.asList("userId", "logLevel"), null, null));

		PollingProvider pollingProvider = new PollingProvider("simpleService", "poll",
				"poll");
		remotingApi.addPollingProvider(pollingProvider);

		return remotingApi;
	}

	@Test
	@PerfTest(invocations = 150, threads = 5)
	public void testSimpleApiDebug() throws IllegalStateException, IOException {
		CloseableHttpClient client = HttpClientBuilder.create().build();
		try {
			HttpGet get = new HttpGet(
					"http://localhost:9998/controller/api-debug.js?group=itest_simple");
			handleApi(client, get, false);
		}
		finally {
			IOUtils.closeQuietly(client);
		}
	}

	@Test
	@PerfTest(invocations = 150, threads = 5)
	public void testSimpleApi() throws IllegalStateException, IOException {
		CloseableHttpClient client = HttpClientBuilder.create().build();
		try {
			HttpGet get = new HttpGet(
					"http://localhost:9998/controller/api.js?group=itest_simple");
			handleApi(client, get, false);
		}
		finally {
			IOUtils.closeQuietly(client);
		}
	}

	@Test
	@PerfTest(invocations = 150, threads = 5)
	public void testSimpleApiFingerprinted() throws IllegalStateException, IOException {
		CloseableHttpClient client = HttpClientBuilder.create().build();
		try {
			HttpGet get = new HttpGet(
					"http://localhost:9998/controller/api-1.0.0.js?group=itest_simple");
			handleApi(client, get, true);
		}
		finally {
			IOUtils.closeQuietly(client);
		}
	}

	private static void handleApi(HttpClient client, HttpGet get, boolean fingerprinted)
			throws IOException, JsonParseException, JsonMappingException {
		HttpResponse response = client.execute(get);
		HttpEntity entity = response.getEntity();
		assertThat(entity).isNotNull();
		String responseString = EntityUtils.toString(entity);

		String contentType = response.getFirstHeader("Content-Type").getValue();
		ApiControllerTest.compare(responseString, contentType, api(),
				ApiRequestParams.builder().build());

		assertCacheHeaders(response, fingerprinted);
	}

	public static void assertCacheHeaders(HttpResponse response, boolean fingerprinted) {
		if (fingerprinted) {
			assertThat(response.getFirstHeader("Content-Type").getValue())
					.isEqualTo("application/javascript;charset=UTF-8");
			assertThat(response.getFirstHeader("Content-Length")).isNotNull();

			String expiresString = response.getFirstHeader("Expires").getValue();
			DateTimeFormatter fmt = DateTimeFormat
					.forPattern("E, dd MMM yyyy HH:mm:ss ZZZ").withLocale(Locale.ENGLISH);

			DateTime expires = DateTime.parse(expiresString, fmt);
			DateTime inSixMonths = DateTime.now(DateTimeZone.UTC)
					.plusSeconds(6 * 30 * 24 * 60 * 60);
			assertThat(expires.getYear()).isEqualTo(inSixMonths.getYear());
			assertThat(expires.getMonthOfYear()).isEqualTo(inSixMonths.getMonthOfYear());
			assertThat(expires.getDayOfMonth()).isEqualTo(inSixMonths.getDayOfMonth());
			assertThat(expires.getHourOfDay()).isEqualTo(inSixMonths.getHourOfDay());
			assertThat(expires.getMinuteOfDay()).isEqualTo(inSixMonths.getMinuteOfDay());

			assertThat(response.getFirstHeader("ETag").getValue()).isNotNull();
			assertThat(response.getFirstHeader("Cache-Control").getValue())
					.isEqualTo("public, max-age=15552000");

		}
		else {
			assertThat(response.getFirstHeader("Content-Type").getValue())
					.isEqualTo("application/javascript;charset=UTF-8");
			assertThat(response.getFirstHeader("Content-Length")).isNotNull();
			assertThat(response.getFirstHeader("Expires")).isNull();
			assertThat(response.getFirstHeader("ETag")).isNull();
			assertThat(response.getFirstHeader("Cache-Control")).isNull();
		}
	}

	@Test
	@PerfTest(invocations = 150, threads = 5)
	public void testSimpleCall() throws IllegalStateException, IOException {
		CloseableHttpClient client = HttpClientBuilder.create().build();
		try {
			postToUpperCase("ralph", client);
			postToUpperCase("renee", client);
			postToUpperCase("andrea", client);
		}
		finally {
			IOUtils.closeQuietly(client);
		}
	}

	@Test
	@PerfTest(invocations = 150, threads = 5)
	public void testPoll() throws IOException {
		String _id = String.valueOf(id.incrementAndGet());
		CloseableHttpClient client = HttpClientBuilder.create().build();
		CloseableHttpResponse response = null;
		try {

			HttpGet get = new HttpGet(
					"http://localhost:9998/controller/poll/simpleService/poll/poll?id="
							+ _id);
			response = client.execute(get);

			assertThat(response.getFirstHeader("Content-Type").getValue())
					.isEqualTo("application/json;charset=UTF-8");

			String responseString = EntityUtils.toString(response.getEntity());
			Map<String, Object> rootAsMap = mapper.readValue(responseString, Map.class);
			assertThat(rootAsMap).hasSize(3);
			assertThat(rootAsMap.get("type")).isEqualTo("event");
			assertThat(rootAsMap.get("name")).isEqualTo("poll");
			assertThat(rootAsMap.get("data")).isEqualTo(_id);
		}
		finally {
			IOUtils.closeQuietly(response);
			IOUtils.closeQuietly(client);
		}
	}

	private static void postToUpperCase(String text, HttpClient client)
			throws IOException, JsonParseException, JsonMappingException {
		HttpPost post = new HttpPost("http://localhost:9998/controller/router");

		StringEntity postEntity = new StringEntity(
				"{\"action\":\"simpleService\",\"method\":\"toUpperCase\",\"data\":[\""
						+ text + "\"],\"type\":\"rpc\",\"tid\":1}",
				"UTF-8");
		post.setEntity(postEntity);
		post.setHeader("Content-Type", "application/json; charset=UTF-8");

		HttpResponse response = client.execute(post);

		HttpEntity entity = response.getEntity();
		assertThat(entity).isNotNull();
		String responseString = EntityUtils.toString(entity);
		assertThat(response.getFirstHeader("Content-Length").getValue())
				.isEqualTo("" + responseString.length());

		assertThat(responseString).isNotNull();
		assertThat(responseString).startsWith("[").endsWith("]");

		Map<String, Object> rootAsMap = mapper.readValue(
				responseString.substring(1, responseString.length() - 1), Map.class);
		assertThat(rootAsMap).hasSize(5);
		assertThat(rootAsMap.get("result")).isEqualTo(text.toUpperCase());
		assertThat(rootAsMap.get("method")).isEqualTo("toUpperCase");
		assertThat(rootAsMap.get("type")).isEqualTo("rpc");
		assertThat(rootAsMap.get("action")).isEqualTo("simpleService");
		assertThat(rootAsMap.get("tid")).isEqualTo(1);
	}

	@Test
	@PerfTest(invocations = 150, threads = 5)
	public void testSimpleNamedCall() throws IllegalStateException, IOException {
		CloseableHttpClient client = HttpClientBuilder.create().build();
		try {
			postToEcho(
					Collections.singletonList("\"userId\":\"ralph\", \"logLevel\": 100"),
					Collections.singletonList("UserId: ralph LogLevel: 100"), client);
			postToEcho(Collections.singletonList("\"userId\":\"tom\""),
					Collections.singletonList("UserId: tom LogLevel: 10"), client);
			postToEcho(Collections.singletonList("\"userId\":\"renee\", \"logLevel\": 1"),
					Collections.singletonList("UserId: renee LogLevel: 1"), client);
			postToEcho(Collections.singletonList("\"userId\":\"andrea\""),
					Collections.singletonList("UserId: andrea LogLevel: 10"), client);
		}
		finally {
			IOUtils.closeQuietly(client);
		}
	}

	@Test
	@PerfTest(invocations = 150, threads = 5)
	public void testSimpleNamedCallBatched() throws IllegalStateException, IOException {
		CloseableHttpClient client = HttpClientBuilder.create().build();
		try {
			postToEcho(
					Arrays.asList("\"userId\":\"Ralph\", \"logLevel\": 100",
							"\"userId\":\"Tom\"", "\"userId\":\"Renee\", \"logLevel\": 1",
							"\"userId\":\"Andrea\""),
					Arrays.asList("UserId: Ralph LogLevel: 100",
							"UserId: Tom LogLevel: 10", "UserId: Renee LogLevel: 1",
							"UserId: Andrea LogLevel: 10"),
					client);
		}
		finally {
			IOUtils.closeQuietly(client);
		}
	}

	private static void postToEcho(List<String> datas, List<String> expectedResult,
			HttpClient client)
			throws IOException, JsonParseException, JsonMappingException {

		HttpPost post = new HttpPost("http://localhost:9998/controller/router");

		StringBuilder postData = new StringBuilder();

		if (datas.size() > 1) {
			postData.append("[");
		}

		for (int i = 0; i < datas.size(); i++) {
			postData.append(
					"{\"action\":\"simpleService\",\"method\":\"echo\",\"data\":{");
			postData.append(datas.get(i));
			postData.append("},\"type\":\"rpc\",\"tid\":");
			postData.append(i + 1);
			postData.append("}");
			if (i < datas.size() - 1) {
				postData.append(",");
			}
		}

		if (datas.size() > 1) {
			postData.append("]");
		}

		StringEntity postEntity = new StringEntity(postData.toString(), "UTF-8");

		post.setEntity(postEntity);
		post.setHeader("Content-Type", "application/json; charset=UTF-8");

		HttpResponse response = client.execute(post);
		HttpEntity entity = response.getEntity();
		assertThat(entity).isNotNull();
		String responseString = EntityUtils.toString(entity);

		assertThat(response.getFirstHeader("Content-Length")).isNull();

		assertThat(responseString).isNotNull();

		assertThat(responseString).startsWith("[").endsWith("]");

		List<Map<String, Object>> results = mapper.readValue(responseString, List.class);
		assertThat(results).hasSize(expectedResult.size());
		int tid = 1;
		for (Map<String, Object> map : results) {
			assertThat(map).hasSize(5);
			assertThat(map.get("result")).isEqualTo(expectedResult.get(tid - 1));
			assertThat(map.get("method")).isEqualTo("echo");
			assertThat(map.get("type")).isEqualTo("rpc");
			assertThat(map.get("action")).isEqualTo("simpleService");
			assertThat(map.get("tid")).isEqualTo(tid++);
		}

	}

}
