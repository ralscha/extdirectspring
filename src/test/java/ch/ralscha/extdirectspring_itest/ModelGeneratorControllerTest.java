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

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.junit.Test;

import ch.ralscha.extdirectspring.generator.GeneratorTestUtil;

public class ModelGeneratorControllerTest extends JettyTest {

	@Test
	public void testAuthor() throws ClientProtocolException, IOException {
		HttpClient client = new DefaultHttpClient();
		HttpGet g = new HttpGet("http://localhost:9998/controller/Author.js");
		HttpResponse response = client.execute(g);
		String responseString = EntityUtils.toString(response.getEntity());
		String contentType = response.getFirstHeader("Content-Type").getValue();
		String etag = response.getFirstHeader("ETag").getValue();

		GeneratorTestUtil.compareExtJs4Code("Author", responseString, false);
		assertThat(contentType).isEqualTo("application/javascript;charset=UTF-8");
		assertThat(etag).isNotEmpty();

		g = new HttpGet("http://localhost:9998/controller/Author.js");
		g.addHeader("If-None-Match", etag);
		response = client.execute(g);
		assertThat(response.getStatusLine().getStatusCode()).isEqualTo(304);
	}

	@Test
	public void testBook() throws ClientProtocolException, IOException {
		HttpClient client = new DefaultHttpClient();
		HttpGet g = new HttpGet("http://localhost:9998/controller/Book.js");
		HttpResponse response = client.execute(g);
		assertThat(response.getStatusLine().getStatusCode()).isEqualTo(200);

		String responseString = EntityUtils.toString(response.getEntity());
		String contentType = response.getFirstHeader("Content-Type").getValue();
		String etag = response.getFirstHeader("ETag").getValue();

		GeneratorTestUtil.compareTouch2Code("Book", responseString, false);
		assertThat(contentType).isEqualTo("application/javascript;charset=UTF-8");
		assertThat(etag).isNotEmpty();

		g = new HttpGet("http://localhost:9998/controller/Book.js");
		g.addHeader("If-None-Match", etag);
		response = client.execute(g);
		assertThat(response.getStatusLine().getStatusCode()).isEqualTo(304);
	}
}
