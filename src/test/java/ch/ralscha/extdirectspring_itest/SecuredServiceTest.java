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
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.junit.Test;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class SecuredServiceTest extends JettyTest {

	@Test
	public void callSetDate() throws UnsupportedEncodingException, IOException, ClientProtocolException,
			JsonParseException, JsonMappingException {

		HttpClient client = new DefaultHttpClient();

		HttpPost post = new HttpPost("http://localhost:9998/controller/router");

		StringEntity postEntity = new StringEntity(
				"{\"action\":\"securedService\",\"method\":\"setDate\",\"data\":[102,\"26/04/2012\"],\"type\":\"rpc\",\"tid\":1}",
				"UTF-8");

		post.setEntity(postEntity);
		post.setHeader("Content-Type", "application/json; charset=UTF-8");

		HttpResponse response = client.execute(post);
		HttpEntity entity = response.getEntity();
		assertThat(entity).isNotNull();
		String responseString = EntityUtils.toString(entity);

		assertThat(responseString).isNotNull();
		assertThat(responseString.startsWith("[") && responseString.endsWith("]")).isTrue();
		ObjectMapper mapper = new ObjectMapper();
		@SuppressWarnings("unchecked")
		Map<String, Object> rootAsMap = mapper.readValue(responseString.substring(1, responseString.length() - 1),
				Map.class);
		assertThat(rootAsMap).hasSize(5);
		assertThat(rootAsMap.get("result")).isEqualTo("102,26.04.2012");
		assertThat(rootAsMap.get("method")).isEqualTo("setDate");
		assertThat(rootAsMap.get("type")).isEqualTo("rpc");
		assertThat(rootAsMap.get("action")).isEqualTo("securedService");
		assertThat(rootAsMap.get("tid")).isEqualTo(1);
	}

}
