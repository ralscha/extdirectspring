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

import static org.fest.assertions.api.Assertions.assertThat;

import java.util.HashMap;
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
import org.springframework.test.util.ReflectionTestUtils;

import ch.ralscha.extdirectspring.bean.ExtDirectResponse;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:/testExceptionHandling.xml")
public class ExceptionHandlingTest {

	@Autowired
	private RouterController controller;

	@Autowired
	private ConfigurationService configurationService;

	private MockHttpServletResponse response;

	private MockHttpServletRequest request;

	@Before
	public void beforeTest() {
		response = new MockHttpServletResponse();
		request = new MockHttpServletRequest();
	}

	@Test
	public void testDefault() throws Exception {
		ExtDirectResponse resp = runTest(new Configuration());
		assertThat(resp.getMessage()).isEqualTo("Server Error");
		assertThat(resp.getWhere()).isNull();
	}

	@Test
	public void testDefaultExceptionMessage() throws Exception {
		Configuration configuration = new Configuration();
		configuration.setDefaultExceptionMessage("an error occured");
		ExtDirectResponse resp = runTest(configuration);
		assertThat(resp.getMessage()).isEqualTo("an error occured");
		assertThat(resp.getWhere()).isNull();
	}

	@Test
	public void testExceptionNameAsMessage() throws Exception {
		Configuration configuration = new Configuration();
		configuration.setSendExceptionMessage(true);
		ExtDirectResponse resp = runTest(configuration);
		assertThat(resp.getMessage()).isEqualTo("For input string: \"xxx\"");
		assertThat(resp.getWhere()).isNull();
	}

	@Test
	public void testExceptionToMessage() throws Exception {
		Configuration configuration = new Configuration();
		Map<Class<?>, String> exceptionMessageMapping = new HashMap<Class<?>, String>();
		exceptionMessageMapping.put(NullPointerException.class, "null pointer");
		configuration.setExceptionToMessage(exceptionMessageMapping);
		ExtDirectResponse resp = runTest11(configuration);
		assertThat(resp.getMessage()).isEqualTo("null pointer");
		assertThat(resp.getWhere()).isNull();
	}

	@Test
	public void testExceptionToMessageNullValue() throws Exception {
		Configuration configuration = new Configuration();
		configuration.setSendExceptionMessage(false);
		Map<Class<?>, String> exceptionMessageMapping = new HashMap<Class<?>, String>();
		exceptionMessageMapping.put(NumberFormatException.class, null);
		configuration.setExceptionToMessage(exceptionMessageMapping);
		ExtDirectResponse resp = runTest(configuration);
		assertThat(resp.getMessage()).isEqualTo("For input string: \"xxx\"");
		assertThat(resp.getWhere()).isNull();
	}

	@Test
	public void testDefaultExceptionMessageWithStacktrace() throws Exception {
		Configuration configuration = new Configuration();
		configuration.setSendStacktrace(true);
		configuration.setDefaultExceptionMessage("an error occured");
		ExtDirectResponse resp = runTest(configuration);
		assertThat(resp.getMessage()).isEqualTo("an error occured");
		assertThat(resp.getWhere()).startsWith("java.lang.NumberFormatException");
	}

	@Test
	public void testExceptionNameAsMessageWithStacktrace() throws Exception {
		Configuration configuration = new Configuration();
		configuration.setSendStacktrace(true);
		configuration.setSendExceptionMessage(true);
		ExtDirectResponse resp = runTest(configuration);
		assertThat(resp.getMessage()).isEqualTo("For input string: \"xxx\"");
		assertThat(resp.getWhere()).startsWith("java.lang.NumberFormatException");

	}

	@Test
	public void testExceptionToMessageWithStacktrace() throws Exception {
		Configuration configuration = new Configuration();
		configuration.setSendStacktrace(true);
		Map<Class<?>, String> exceptionMessageMapping = new HashMap<Class<?>, String>();
		exceptionMessageMapping.put(NullPointerException.class, "null pointer");
		configuration.setExceptionToMessage(exceptionMessageMapping);
		ExtDirectResponse resp = runTest11(configuration);
		assertThat(resp.getMessage()).isEqualTo("null pointer");
		assertThat(resp.getWhere()).startsWith("java.lang.NullPointerException");
	}

	@Test
	public void testExceptionToMessageNullValueWithStacktrace() throws Exception {
		Configuration configuration = new Configuration();
		configuration.setSendExceptionMessage(false);
		configuration.setSendStacktrace(true);
		Map<Class<?>, String> exceptionMessageMapping = new HashMap<Class<?>, String>();
		exceptionMessageMapping.put(NumberFormatException.class, null);
		configuration.setExceptionToMessage(exceptionMessageMapping);
		ExtDirectResponse resp = runTest(configuration);
		assertThat(resp.getMessage()).isEqualTo("For input string: \"xxx\"");
		assertThat(resp.getWhere()).startsWith("java.lang.NumberFormatException");
	}

	private ExtDirectResponse runTest(Configuration configuration) throws Exception {
		ReflectionTestUtils.setField(configurationService, "configuration", configuration);

		Map<String, Object> edRequest = ControllerUtil.createRequestJson("remoteProviderSimple", "method4b", 2,
				new Object[] { 3, "xxx", "string.param" });

		request.setContent(ControllerUtil.writeAsByte(edRequest));
		controller.router(request, response, Locale.ENGLISH);
		List<ExtDirectResponse> responses = ControllerUtil.readDirectResponses(response.getContentAsByteArray());

		assertThat(responses).hasSize(1);
		ExtDirectResponse resp = responses.get(0);
		assertThat(resp.getAction()).isEqualTo("remoteProviderSimple");
		assertThat(resp.getMethod()).isEqualTo("method4b");
		assertThat(resp.getType()).isEqualTo("exception");
		assertThat(resp.getTid()).isEqualTo(2);
		assertThat(resp.getResult()).isNull();

		ReflectionTestUtils.setField(configurationService, "configuration", new Configuration());

		return resp;
	}

	private ExtDirectResponse runTest11(Configuration configuration) throws Exception {
		ReflectionTestUtils.setField(configurationService, "configuration", configuration);

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
		assertThat(resp.getResult()).isNull();

		ReflectionTestUtils.setField(configurationService, "configuration", new Configuration());

		return resp;
	}

}
