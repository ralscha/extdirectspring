/**
 * Copyright 2010-2013 Ralph Schaer <ralphschaer@gmail.com>
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

import static org.fest.assertions.api.Assertions.assertThat;

import java.io.IOException;

import org.apache.commons.io.IOUtils;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.junit.Test;

import ch.ralscha.extdirectspring.generator.GeneratorTestUtil;

public class ModelGeneratorControllerTest extends JettyTest {

	@Test
	public void testAuthor() throws ClientProtocolException, IOException {
		CloseableHttpClient client = HttpClientBuilder.create().build();
		CloseableHttpResponse response = null;
		try {
			HttpGet g = new HttpGet("http://localhost:9998/controller/Author.js");
			response = client.execute(g);
			String responseString = EntityUtils.toString(response.getEntity());
			String contentType = response.getFirstHeader("Content-Type").getValue();
			String etag = response.getFirstHeader("ETag").getValue();

			GeneratorTestUtil.compareExtJs4Code("Author", responseString, false, false);
			assertThat(contentType).isEqualTo("application/javascript;charset=UTF-8");
			assertThat(etag).isNotEmpty();

			g = new HttpGet("http://localhost:9998/controller/Author.js");
			g.addHeader("If-None-Match", etag);
			IOUtils.closeQuietly(response);
			response = client.execute(g);
			assertThat(response.getStatusLine().getStatusCode()).isEqualTo(304);
		} finally {
			IOUtils.closeQuietly(response);
			IOUtils.closeQuietly(client);
		}
	}

	@Test
	public void testBook() throws ClientProtocolException, IOException {
		CloseableHttpClient client = HttpClientBuilder.create().build();
		CloseableHttpResponse response = null;
		try {
			HttpGet g = new HttpGet("http://localhost:9998/controller/Book.js");
			response = client.execute(g);
			assertThat(response.getStatusLine().getStatusCode()).isEqualTo(200);

			String responseString = EntityUtils.toString(response.getEntity());
			String contentType = response.getFirstHeader("Content-Type").getValue();
			String etag = response.getFirstHeader("ETag").getValue();

			GeneratorTestUtil.compareTouch2Code("Book", responseString, false, false);
			assertThat(contentType).isEqualTo("application/javascript;charset=UTF-8");
			assertThat(etag).isNotEmpty();

			g = new HttpGet("http://localhost:9998/controller/Book.js");
			g.addHeader("If-None-Match", etag);
			IOUtils.closeQuietly(response);
			response = client.execute(g);
			assertThat(response.getStatusLine().getStatusCode()).isEqualTo(304);
		} finally {
			IOUtils.closeQuietly(response);
			IOUtils.closeQuietly(client);
		}
	}
}
