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
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.fasterxml.jackson.databind.ObjectMapper;

public class ExceptionFormPostServiceTest extends JettyTest {

	private CloseableHttpClient client;

	private HttpPost post;

	@Before
	public void beforeTest() {
		this.client = HttpClientBuilder.create().build();
		this.post = new HttpPost("http://localhost:9998/controller/router");
	}

	@After
	public void afterTest() {
		IOUtils.closeQuietly(this.client);
	}

	@Test
	public void testExceptionHandling() throws IOException {

		List<NameValuePair> formparams = new ArrayList<NameValuePair>();
		formparams.add(new BasicNameValuePair("extTID", "3"));
		formparams.add(new BasicNameValuePair("extAction", "exceptionFormPostService"));
		formparams.add(new BasicNameValuePair("extMethod", "throwAException"));
		formparams.add(new BasicNameValuePair("extType", "rpc"));
		formparams.add(new BasicNameValuePair("extUpload", "false"));

		UrlEncodedFormEntity postEntity = new UrlEncodedFormEntity(formparams, "UTF-8");

		this.post.setEntity(postEntity);

		CloseableHttpResponse response = this.client.execute(this.post);
		HttpEntity entity = response.getEntity();
		assertThat(entity).isNotNull();
		String responseString = EntityUtils.toString(entity);
		ObjectMapper mapper = new ObjectMapper();

		Map<String, Object> rootAsMap = mapper.readValue(responseString, Map.class);
		assertThat(rootAsMap).hasSize(6);
		assertThat(rootAsMap.get("method")).isEqualTo("throwAException");
		assertThat(rootAsMap.get("type")).isEqualTo("exception");
		assertThat(rootAsMap.get("action")).isEqualTo("exceptionFormPostService");
		assertThat(rootAsMap.get("tid")).isEqualTo(3);
		assertThat(rootAsMap.get("message")).isEqualTo("a null pointer");

		@SuppressWarnings("unchecked")
		Map<String, Object> result = (Map<String, Object>) rootAsMap.get("result");
		assertThat(result).hasSize(1);
		assertThat((Boolean) result.get("success")).isFalse();
		IOUtils.closeQuietly(response);
	}

	@Test
	public void testNonExistingMethod() throws IOException {

		List<NameValuePair> formparams = new ArrayList<NameValuePair>();
		formparams.add(new BasicNameValuePair("extTID", "3"));
		formparams.add(new BasicNameValuePair("extAction", "exceptionFormPostService"));
		formparams.add(new BasicNameValuePair("extMethod", "throwAExceptionNotExists"));
		formparams.add(new BasicNameValuePair("extType", "rpc"));
		formparams.add(new BasicNameValuePair("extUpload", "false"));

		UrlEncodedFormEntity postEntity = new UrlEncodedFormEntity(formparams, "UTF-8");

		this.post.setEntity(postEntity);

		CloseableHttpResponse response = this.client.execute(this.post);
		HttpEntity entity = response.getEntity();
		assertThat(entity).isNotNull();
		String responseString = EntityUtils.toString(entity);
		ObjectMapper mapper = new ObjectMapper();

		Map<String, Object> rootAsMap = mapper.readValue(responseString, Map.class);
		assertThat(rootAsMap).hasSize(5);
		assertThat(rootAsMap.get("method")).isEqualTo("throwAExceptionNotExists");
		assertThat(rootAsMap.get("type")).isEqualTo("exception");
		assertThat(rootAsMap.get("action")).isEqualTo("exceptionFormPostService");
		assertThat(rootAsMap.get("tid")).isEqualTo(3);
		assertThat(rootAsMap.get("message")).isEqualTo("Server Error");
		IOUtils.closeQuietly(response);
	}
}
