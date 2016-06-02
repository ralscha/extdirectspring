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
import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.junit.Test;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class RawJsonControllerTest extends JettyTest {

	@Test
	public void testRawResponse() throws IOException {
		testAndCheck("rawJsonController", "listUsers1", null, true);
		testAndCheck("rawJsonController", "listUsers2", 2, true);
		testAndCheck("rawJsonController", "listUsers3", 2, false);
		testAndCheck("rawJsonController", "listUsers4", 2, true);
		testAndCheck("rawJsonController", "listUsers5", 2, true);

		testAndCheck("rawJsonController", "listUsers1Ed", null, true);
		testAndCheck("rawJsonController", "listUsers2Ed", 2, true);
		testAndCheck("rawJsonController", "listUsers3Ed", 2, false);
		testAndCheck("rawJsonController", "listUsers4Ed", 2, true);
		testAndCheck("rawJsonController", "listUsers5Ed", 2, true);
	}

	@SuppressWarnings("unchecked")
	private static void testAndCheck(String action, String method, Integer total,
			boolean success)
			throws IOException, JsonParseException, JsonMappingException {
		CloseableHttpClient client = HttpClientBuilder.create().build();
		CloseableHttpResponse response = null;
		try {
			HttpPost post = new HttpPost("http://localhost:9998/controller/router");

			StringEntity postEntity = new StringEntity(
					"{\"action\":\"" + action + "\",\"method\":\"" + method
							+ "\",\"data\":[],\"type\":\"rpc\",\"tid\":1}",
					"UTF-8");
			post.setEntity(postEntity);
			post.setHeader("Content-Type", "application/json; charset=UTF-8");

			response = client.execute(post);
			HttpEntity entity = response.getEntity();
			assertThat(entity).isNotNull();
			String responseString = EntityUtils.toString(entity);

			assertThat(responseString).isNotNull();
			assertThat(responseString).startsWith("[").endsWith("]");

			ObjectMapper mapper = new ObjectMapper();
			Map<String, Object> rootAsMap = mapper.readValue(
					responseString.substring(1, responseString.length() - 1), Map.class);
			assertEquals(5, rootAsMap.size());

			assertEquals(method, rootAsMap.get("method"));
			assertEquals("rpc", rootAsMap.get("type"));
			assertEquals(action, rootAsMap.get("action"));
			assertEquals(1, rootAsMap.get("tid"));

			Map<String, Object> result = (Map<String, Object>) rootAsMap.get("result");
			if (total != null) {
				assertEquals(3, result.size());
				assertThat((Integer) result.get("total")).isEqualTo(total);
			}
			else {
				assertEquals(2, result.size());
			}
			assertThat((Boolean) result.get("success")).isEqualTo(success);

			List<Map<String, Object>> records = (List<Map<String, Object>>) result
					.get("records");
			assertEquals(2, records.size());

			assertEquals("4cf8e5b8924e23349fb99454",
					((Map<String, Object>) records.get(0).get("_id")).get("$oid"));
			assertEquals("4cf8e5b8924e2334a0b99454",
					((Map<String, Object>) records.get(1).get("_id")).get("$oid"));
		}
		finally {
			IOUtils.closeQuietly(response);
			IOUtils.closeQuietly(client);
		}
	}

}
