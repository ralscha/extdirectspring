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
import java.util.Locale;
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

@SuppressWarnings("unchecked")
public class UserInitBinderServiceTest extends JettyTest {

	private CloseableHttpClient client;

	private HttpPost post;

	private final ObjectMapper mapper = new ObjectMapper();

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
	public void testPostWithoutDate() throws IOException {
		Locale.setDefault(Locale.ENGLISH);
		List<NameValuePair> formparams = new ArrayList<NameValuePair>();
		formparams.add(new BasicNameValuePair("extTID", "1"));
		formparams
				.add(new BasicNameValuePair("extAction", "userServiceInitBinderService"));
		formparams.add(new BasicNameValuePair("extMethod", "updateUser"));
		formparams.add(new BasicNameValuePair("extType", "rpc"));
		formparams.add(new BasicNameValuePair("extUpload", "false"));
		formparams.add(new BasicNameValuePair("name", "Garner"));
		formparams.add(new BasicNameValuePair("firstName", "Joe"));
		formparams.add(new BasicNameValuePair("email", "test@test.com"));
		formparams.add(new BasicNameValuePair("age", "28"));
		formparams.add(new BasicNameValuePair("flag", "false"));
		formparams.add(new BasicNameValuePair("dateOfBirth", ""));
		UrlEncodedFormEntity postEntity = new UrlEncodedFormEntity(formparams, "UTF-8");

		this.post.setEntity(postEntity);

		CloseableHttpResponse response = this.client.execute(this.post);
		try {
			HttpEntity entity = response.getEntity();
			assertThat(entity).isNotNull();
			String responseString = EntityUtils.toString(entity);

			Map<String, Object> rootAsMap = this.mapper.readValue(responseString,
					Map.class);
			assertThat(rootAsMap).hasSize(5);
			assertThat(rootAsMap.get("method")).isEqualTo("updateUser");
			assertThat(rootAsMap.get("type")).isEqualTo("rpc");
			assertThat(rootAsMap.get("action")).isEqualTo("userServiceInitBinderService");
			assertThat(rootAsMap.get("tid")).isEqualTo(1);

			Map<String, Object> result = (Map<String, Object>) rootAsMap.get("result");
			assertThat(result).hasSize(6);
			assertThat(result.get("name")).isEqualTo("Garner");
			assertThat(result.get("firstName")).isEqualTo("Joe");
			assertThat(result.get("age")).isEqualTo(28);
			assertThat(result.get("email")).isEqualTo("test@test.com");
			assertThat(result.get("flag")).isEqualTo(Boolean.FALSE);
			assertThat(result.get("dateOfBirth")).isNull();
			assertThat(result.get("success")).isEqualTo(Boolean.TRUE);
		}
		finally {
			IOUtils.closeQuietly(response);
		}
	}

	@Test
	public void testPostWithDate() throws IOException {
		Locale.setDefault(Locale.ENGLISH);
		List<NameValuePair> formparams = new ArrayList<NameValuePair>();
		formparams.add(new BasicNameValuePair("extTID", "2"));
		formparams
				.add(new BasicNameValuePair("extAction", "userServiceInitBinderService"));
		formparams.add(new BasicNameValuePair("extMethod", "updateUser"));
		formparams.add(new BasicNameValuePair("extType", "rpc"));
		formparams.add(new BasicNameValuePair("extUpload", "false"));
		formparams.add(new BasicNameValuePair("name", "Bacon"));
		formparams.add(new BasicNameValuePair("firstName", "Kevin"));
		formparams.add(new BasicNameValuePair("email", "kevin@test.com"));
		formparams.add(new BasicNameValuePair("age", "45"));
		formparams.add(new BasicNameValuePair("flag", "true"));
		formparams.add(new BasicNameValuePair("dateOfBirth", "21.12.1966"));
		UrlEncodedFormEntity postEntity = new UrlEncodedFormEntity(formparams, "UTF-8");

		this.post.setEntity(postEntity);

		CloseableHttpResponse response = this.client.execute(this.post);
		try {
			HttpEntity entity = response.getEntity();
			assertThat(entity).isNotNull();
			String responseString = EntityUtils.toString(entity);

			Map<String, Object> rootAsMap = this.mapper.readValue(responseString,
					Map.class);
			assertThat(rootAsMap).hasSize(5);
			assertThat(rootAsMap.get("method")).isEqualTo("updateUser");
			assertThat(rootAsMap.get("type")).isEqualTo("rpc");
			assertThat(rootAsMap.get("action")).isEqualTo("userServiceInitBinderService");
			assertThat(rootAsMap.get("tid")).isEqualTo(2);

			Map<String, Object> result = (Map<String, Object>) rootAsMap.get("result");
			assertThat(result).hasSize(7);
			assertThat(result.get("name")).isEqualTo("Bacon");
			assertThat(result.get("firstName")).isEqualTo("Kevin");
			assertThat(result.get("age")).isEqualTo(45);
			assertThat(result.get("email")).isEqualTo("kevin@test.com");
			assertThat(result.get("flag")).isEqualTo(Boolean.TRUE);
			assertThat(result.get("dateOfBirth")).isEqualTo("1966-12-21");
			assertThat(result.get("success")).isEqualTo(Boolean.TRUE);
		}
		finally {
			IOUtils.closeQuietly(response);
		}
	}

}
