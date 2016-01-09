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
package ch.ralscha.extdirectspring.util;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.util.DigestUtils;

public class ExtDirectSpringUtilTest {

	@Test
	public void testEqual() {
		assertThat(ExtDirectSpringUtil.equal(1, 1)).isTrue();
		assertThat(ExtDirectSpringUtil.equal(1, 2)).isFalse();

		assertThat(ExtDirectSpringUtil.equal(true, true)).isTrue();
		assertThat(ExtDirectSpringUtil.equal(false, false)).isTrue();

		assertThat(ExtDirectSpringUtil.equal(true, false)).isFalse();
		assertThat(ExtDirectSpringUtil.equal(false, true)).isFalse();
		assertThat(ExtDirectSpringUtil.equal(false, null)).isFalse();

		assertThat(ExtDirectSpringUtil.equal(Boolean.TRUE, Boolean.TRUE)).isTrue();
		assertThat(ExtDirectSpringUtil.equal(Boolean.FALSE, Boolean.FALSE)).isTrue();

		assertThat(ExtDirectSpringUtil.equal(Boolean.TRUE, Boolean.FALSE)).isFalse();
		assertThat(ExtDirectSpringUtil.equal(Boolean.FALSE, Boolean.TRUE)).isFalse();
		assertThat(ExtDirectSpringUtil.equal(Boolean.FALSE, null)).isFalse();

		assertThat(ExtDirectSpringUtil.equal("a", "a")).isTrue();
		assertThat(ExtDirectSpringUtil.equal("a", "b")).isFalse();
		assertThat(ExtDirectSpringUtil.equal(null, "a")).isFalse();
		assertThat(ExtDirectSpringUtil.equal("a", null)).isFalse();
		assertThat(ExtDirectSpringUtil.equal(null, null)).isTrue();
	}

	@Test
	public void testIsMultipart() {
		MockHttpServletRequest request = new MockHttpServletRequest();
		request.setMethod("POST");
		request.setRequestURI("/demo/controller/router");

		request.addHeader("Host", "eds.rasc.ch");
		request.addHeader("Connection", "keep-alive");
		request.addHeader("Content-Length", "8277");
		request.addHeader("Cache-Control", "max-age=0");
		request.addHeader("Origin", "http://eds.rasc.ch");
		request.addHeader("User-Agent",
				"Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.1 (KHTML, like Gecko) Chrome/21.0.1180.75 Safari/537.1");
		request.addHeader("Content-Type",
				"multipart/form-data; boundary=----WebKitFormBoundaryux6D0mMa2PlsY016");

		assertThat(ExtDirectSpringUtil.isMultipart(request)).isTrue();

		request = new MockHttpServletRequest();
		request.setMethod("POST");
		request.setRequestURI("/demo/controller/router");

		request.addHeader("Host", "eds.rasc.ch");
		request.addHeader("Connection", "keep-alive");
		request.addHeader("Content-Length", "165");
		request.addHeader("Origin", "http://eds.rasc.ch");
		request.addHeader("X-Requested-With", "XMLHttpRequest");
		request.addHeader("User-Agent",
				"Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.1 (KHTML, like Gecko) Chrome/21.0.1180.75 Safari/537.1");
		request.addHeader("Content-Type",
				"application/x-www-form-urlencoded; charset=UTF-8");

		assertThat(ExtDirectSpringUtil.isMultipart(request)).isFalse();

		request = new MockHttpServletRequest();
		request.setMethod("GET");
		request.setRequestURI("/demo/controller/router");
		request.addHeader("Host", "eds.rasc.ch");
		request.addHeader("Connection", "keep-alive");
		request.addHeader("User-Agent",
				"Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.1 (KHTML, like Gecko) Chrome/21.0.1180.75 Safari/537.1");

		assertThat(ExtDirectSpringUtil.isMultipart(request)).isFalse();

	}

	@Test
	public void testAddCacheHeaders() {
		MockHttpServletResponse response = new MockHttpServletResponse();
		ExtDirectSpringUtil.addCacheHeaders(response, "1", null);
		assertResponse(response, 3, "1", 6);

		response = new MockHttpServletResponse();
		ExtDirectSpringUtil.addCacheHeaders(response, "2", 1);
		assertResponse(response, 3, "2", 1);

		response = new MockHttpServletResponse();
		ExtDirectSpringUtil.addCacheHeaders(response, "3", 12);
		assertResponse(response, 3, "3", 12);
	}

	private static void assertResponse(MockHttpServletResponse response, int noOfHeaders,
			String etag, int month) {
		assertThat(response.getHeaderNames()).hasSize(noOfHeaders);
		assertThat(response.getHeader("ETag")).isEqualTo(etag);
		assertThat(response.getHeader("Cache-Control"))
				.isEqualTo("public, max-age=" + month * 30 * 24 * 60 * 60);

		String expiresHeader = (String) response.getHeaderValue("Expires");
		DateTimeFormatter fmt = DateTimeFormat.forPattern("EEE, dd MMM yyyy HH:mm:ss z");
		DateTime expires = DateTime.parse(expiresHeader, fmt);

		DateTime inSixMonths = DateTime.now(DateTimeZone.UTC)
				.plusSeconds(month * 30 * 24 * 60 * 60);
		assertThat(expires.getYear()).isEqualTo(inSixMonths.getYear());
		assertThat(expires.getMonthOfYear()).isEqualTo(inSixMonths.getMonthOfYear());
		assertThat(expires.getDayOfMonth()).isEqualTo(inSixMonths.getDayOfMonth());
		assertThat(expires.getHourOfDay()).isEqualTo(inSixMonths.getHourOfDay());
		assertThat(expires.getMinuteOfDay()).isEqualTo(inSixMonths.getMinuteOfDay());
	}

	@Test(expected = IllegalArgumentException.class)
	public void testAddCacheHeadersWithNullEtag() {
		MockHttpServletResponse response = new MockHttpServletResponse();
		ExtDirectSpringUtil.addCacheHeaders(response, null, null);
	}

	@Test
	public void testHandleCacheableResponseWithoutIfNoneMatch() throws IOException {
		MockHttpServletRequest request = new MockHttpServletRequest();
		MockHttpServletResponse response = new MockHttpServletResponse();
		byte[] data = "the response data".getBytes();
		String etag = "\"0" + DigestUtils.md5DigestAsHex(data) + '"';
		String contentType = "application/javascript;charset=UTF-8";
		ExtDirectSpringUtil.handleCacheableResponse(request, response, data, contentType);

		assertThat(response.getStatus()).isEqualTo(200);
		assertResponse(response, 5, etag, 6);
		assertThat(response.getContentLength()).isEqualTo(data.length);
		assertThat(response.getContentType()).isEqualTo(contentType);
		assertThat(response.getContentAsByteArray()).isEqualTo(data);
	}

	@Test
	public void testHandleCacheableResponseWithIfNoneMatch() throws IOException {
		byte[] data = "the response data".getBytes();
		String etag = "\"0" + DigestUtils.md5DigestAsHex(data) + '"';
		String contentType = "application/javascript;charset=UTF-8";

		MockHttpServletRequest request = new MockHttpServletRequest();
		request.addHeader("If-None-Match", etag);
		MockHttpServletResponse response = new MockHttpServletResponse();

		ExtDirectSpringUtil.handleCacheableResponse(request, response, data, contentType);
		assertThat(response.getStatus()).isEqualTo(304);

		request = new MockHttpServletRequest();
		request.addHeader("If-None-Match", etag);
		response = new MockHttpServletResponse();
		data = "new response data".getBytes();
		etag = "\"0" + DigestUtils.md5DigestAsHex(data) + '"';
		ExtDirectSpringUtil.handleCacheableResponse(request, response, data, contentType);
		assertThat(response.getStatus()).isEqualTo(200);
		assertResponse(response, 5, etag, 6);
		assertThat(response.getContentLength()).isEqualTo(data.length);
		assertThat(response.getContentType()).isEqualTo(contentType);
		assertThat(response.getContentAsByteArray()).isEqualTo(data);
	}
}
