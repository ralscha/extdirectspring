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
package ch.ralscha.extdirectspring.controller;

import static org.fest.assertions.Assertions.assertThat;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import ch.ralscha.extdirectspring.bean.ExtDirectResponse;

/**
 * Tests for {@link RouterController}.
 * 
 * @author Ralph Schaer
 */
@SuppressWarnings("all")
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:/testExceptionHandlingConfig.xml")
public class ExceptionHandlingConfigInXmlTest {

	@Autowired
	private RouterController controller;

	private MockHttpServletResponse response;
	private MockHttpServletRequest request;

	@Before
	public void beforeTest() {
		response = new MockHttpServletResponse();
		request = new MockHttpServletRequest();
	}

	@Test
	public void testExceptionInMapping() throws Exception {

		Map<String, Object> edRequest = ControllerUtil.createRequestJson("remoteProviderSimple", "method4", 2,
				new Object[] { 3, 2.5, "string.param" });

		request.setContent(ControllerUtil.writeAsByte(edRequest));
		controller.router(request, response, Locale.ENGLISH);
		List<ExtDirectResponse> responses = ControllerUtil.readDirectResponses(response.getContentAsByteArray());

		assertThat(responses).hasSize(1);
		ExtDirectResponse resp = responses.get(0);
		assertThat(resp.getAction()).isEqualTo("remoteProviderSimple");
		assertThat(resp.getMethod()).isEqualTo("method4");
		assertThat(resp.getType()).isEqualTo("exception");
		assertThat(resp.getTid()).isEqualTo(2);
		assertThat(resp.getMessage()).isEqualTo("Panic!!!");
		assertThat(resp.getResult()).isNull();
		assertTrue(resp.getWhere().startsWith("Method 'remoteProviderSimple.method4' not found"));

	}

	@Test
	public void testExceptionInMappingWithNullValue() throws Exception {

		Map<String, Object> edRequest = ControllerUtil.createRequestJson("remoteProviderSimple2", "method4", 2,
				new Object[] { 3, 2.5, "string.param" });

		request.setContent(ControllerUtil.writeAsByte(edRequest));
		controller.router(request, response, Locale.ENGLISH);
		List<ExtDirectResponse> responses = ControllerUtil.readDirectResponses(response.getContentAsByteArray());

		assertThat(responses).hasSize(1);
		ExtDirectResponse resp = responses.get(0);
		assertThat(resp.getAction()).isEqualTo("remoteProviderSimple2");
		assertThat(resp.getMethod()).isEqualTo("method4");
		assertThat(resp.getType()).isEqualTo("exception");
		assertThat(resp.getTid()).isEqualTo(2);
		assertThat(resp.getMessage()).isEqualTo("Panic!!!");
		assertThat(resp.getResult()).isNull();
		assertTrue(resp.getWhere().startsWith("Method 'remoteProviderSimple2.method4' not found"));

	}

	@Test
	public void testExceptionNotInMapping() throws IOException {
		Map<String, Object> edRequest = ControllerUtil.createRequestJson("remoteProviderSimple", "method11", 3, null);

		request.setContent(ControllerUtil.writeAsByte(edRequest));
		controller.router(request, response, Locale.ENGLISH);
		List<ExtDirectResponse> responses = ControllerUtil.readDirectResponses(response.getContentAsByteArray());

		assertThat(responses).hasSize(1);
		ExtDirectResponse resp = responses.get(0);
		assertThat(resp.getAction()).isEqualTo("remoteProviderSimple");
		assertThat(resp.getMethod()).isEqualTo("method11");
		assertThat(resp.getType()).isEqualTo("exception");
		assertThat(resp.getTid()).isEqualTo(3);
		assertThat(resp.getMessage()).isEqualTo("Panic!!!");
		assertThat(resp.getResult()).isNull();
		assertThat(resp.getWhere().startsWith("java.lang.NullPointerException")).isTrue();

	}
}
