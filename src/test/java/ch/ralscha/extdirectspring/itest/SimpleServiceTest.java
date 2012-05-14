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

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.databene.contiperf.PerfTest;
import org.databene.contiperf.junit.ContiPerfRule;
import org.junit.Rule;
import org.junit.Test;

public class SimpleServiceTest extends JettyTest {

	@Rule
	public ContiPerfRule i = new ContiPerfRule();

	@Test
	@PerfTest(invocations = 200, threads = 10)
	public void testSimpleApi() throws IllegalStateException, IOException {

		HttpClient client = new DefaultHttpClient();
		HttpGet get = new HttpGet("http://localhost:9998/controller/api-debug.js?group=itest_simple");
		HttpResponse response = client.execute(get);
		HttpEntity entity = response.getEntity();
		assertThat(entity).isNotNull();
		String responseString = EntityUtils.toString(entity);
		EntityUtils.consume(entity);
		assertThat(responseString).contains("\"name\" : \"toUpperCase\"");
		assertThat(responseString).contains("\"name\" : \"echo\"");
	}

	@Test
	@PerfTest(invocations = 200, threads = 10)
	public void testSimpleCall() throws IllegalStateException, IOException {
		HttpClient client = new DefaultHttpClient();
		postToUpperCase("ralph", client);
		postToUpperCase("renee", client);
		postToUpperCase("andrea", client);
	}

	private void postToUpperCase(String text, HttpClient client) throws UnsupportedEncodingException, IOException,
			ClientProtocolException, JsonParseException, JsonMappingException {
		HttpPost post = new HttpPost("http://localhost:9998/controller/router");

		StringEntity postEntity = new StringEntity(
				"{\"action\":\"simpleService\",\"method\":\"toUpperCase\",\"data\":[\"" + text
						+ "\"],\"type\":\"rpc\",\"tid\":1}", "UTF-8");
		post.setEntity(postEntity);
		post.setHeader("Content-Type", "application/json; charset=UTF-8");

		HttpResponse response = client.execute(post);
						
		HttpEntity entity = response.getEntity();
		assertThat(entity).isNotNull();
		String responseString = EntityUtils.toString(entity);
		assertThat(response.getFirstHeader("Content-Length").getValue()).isEqualTo(""+responseString.length());

		assertThat(responseString).isNotNull();
		assertThat(responseString).startsWith("[").endsWith("]");
		ObjectMapper mapper = new ObjectMapper();
		@SuppressWarnings("unchecked")
		Map<String, Object> rootAsMap = mapper.readValue(responseString.substring(1, responseString.length() - 1),
				Map.class);
		assertThat(rootAsMap).hasSize(5);
		assertThat(rootAsMap.get("result")).isEqualTo(text.toUpperCase());
		assertThat(rootAsMap.get("method")).isEqualTo("toUpperCase");
		assertThat(rootAsMap.get("type")).isEqualTo("rpc");
		assertThat(rootAsMap.get("action")).isEqualTo("simpleService");
		assertThat(rootAsMap.get("tid")).isEqualTo(1);
	}

	@Test
	@PerfTest(invocations = 200, threads = 10)
	public void testSimpleNamedCall() throws IllegalStateException, IOException {
		HttpClient client = new DefaultHttpClient();
		postToEcho("\"userId\":\"ralph\", \"logLevel\": 100", "UserId: ralph LogLevel: 100", client);
		postToEcho("\"userId\":\"tom\"", "UserId: tom LogLevel: 10", client);
		postToEcho("\"userId\":\"renee\", \"logLevel\": 1", "UserId: renee LogLevel: 1", client);
		postToEcho("\"userId\":\"andrea\"", "UserId: andrea LogLevel: 10", client);
	}

	private void postToEcho(String data, String expectedResult, HttpClient client) throws UnsupportedEncodingException,
			IOException, ClientProtocolException, JsonParseException, JsonMappingException {

		HttpPost post = new HttpPost("http://localhost:9998/controller/router");

		StringEntity postEntity = new StringEntity("{\"action\":\"simpleService\",\"method\":\"echo\",\"data\":{"
				+ data + "},\"type\":\"rpc\",\"tid\":1}", "UTF-8");

		post.setEntity(postEntity);
		post.setHeader("Content-Type", "application/json; charset=UTF-8");

		HttpResponse response = client.execute(post);
		HttpEntity entity = response.getEntity();
		assertThat(entity).isNotNull();
		String responseString = EntityUtils.toString(entity);
		
		assertThat(response.getFirstHeader("Content-Length")).isNull();
		
		assertThat(responseString).isNotNull();

		assertThat(responseString).startsWith("[").endsWith("]");
		ObjectMapper mapper = new ObjectMapper();
		@SuppressWarnings("unchecked")
		Map<String, Object> rootAsMap = mapper.readValue(responseString.substring(1, responseString.length() - 1),
				Map.class);
		assertThat(rootAsMap).hasSize(5);
		assertThat(rootAsMap.get("result")).isEqualTo(expectedResult);
		assertThat(rootAsMap.get("method")).isEqualTo("echo");
		assertThat(rootAsMap.get("type")).isEqualTo("rpc");
		assertThat(rootAsMap.get("action")).isEqualTo("simpleService");
		assertThat(rootAsMap.get("tid")).isEqualTo(1);
	}

}
