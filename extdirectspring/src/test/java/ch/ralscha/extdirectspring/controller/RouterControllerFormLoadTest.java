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

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import ch.ralscha.extdirectspring.bean.ExtDirectFormLoadResult;
import ch.ralscha.extdirectspring.bean.ExtDirectResponse;
import ch.ralscha.extdirectspring.provider.FormInfo;

/**
 * Tests for {@link RouterController}.
 * 
 * @author Ralph Schaer
 */
@SuppressWarnings("all")
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:/testApplicationContext.xml")
public class RouterControllerFormLoadTest {

	@Autowired
	private RouterController controller;

	@BeforeClass
	public static void beforeTest() {
		Locale.setDefault(Locale.US);
	}

	@Test
	public void testFormLoad() throws IOException {
		Map<String, Object> data = new HashMap<String, Object>();
		data.put("d", 3.141);
		ExtDirectFormLoadResult wrapper = (ExtDirectFormLoadResult) ControllerUtil.sendAndReceive(controller,
				"remoteProviderFormLoad", "method1", data, ExtDirectFormLoadResult.class);

		assertThat(wrapper.isSuccess()).isTrue();
		assertThat(wrapper.getData()).isNotNull();

		FormInfo info = ControllerUtil.convertValue(wrapper.getData(), FormInfo.class);
		assertThat(Double.compare(3.141, info.getBack()) == 0).isTrue();
		assertThat(info.isAdmin()).isEqualTo(true);
		assertThat(info.getAge()).isEqualTo(31);
		assertThat(info.getName()).isEqualTo("Bob");
		assertThat(info.getSalary()).isEqualTo(new BigDecimal("10000.55"));
		assertThat(info.getBirthday()).isEqualTo(new GregorianCalendar(1980, Calendar.JANUARY, 15).getTime());
	}

	@Test
	public void testFormLoadReturnsNull() throws IOException {
		ControllerUtil.sendAndReceive(controller, "remoteProviderFormLoad", "method2", null, Void.TYPE);
	}

	@Test
	public void testWithSupportedArguments() throws IOException {
		ExtDirectFormLoadResult wrapper = (ExtDirectFormLoadResult) ControllerUtil.sendAndReceive(controller,
				"remoteProviderFormLoad", "method3", null, ExtDirectFormLoadResult.class);
		assertThat(wrapper.isSuccess()).isTrue();
		assertThat(wrapper.getData()).isNotNull();
		FormInfo formInfo = ControllerUtil.convertValue(wrapper.getData(), FormInfo.class);
		assertThat(formInfo.getResult()).isEqualTo("true;true;true;en");
	}

	@Test
	public void testWithRequestParam() throws IOException {
		Map<String, Object> data = new HashMap<String, Object>();
		data.put("id", 12);
		ExtDirectFormLoadResult wrapper = (ExtDirectFormLoadResult) ControllerUtil.sendAndReceive(controller,
				"remoteProviderFormLoad", "method4", data, ExtDirectFormLoadResult.class);

		assertThat(wrapper.isSuccess()).isTrue();
		assertThat(wrapper.getData()).isNotNull();
		FormInfo formInfo = ControllerUtil.convertValue(wrapper.getData(), FormInfo.class);
		assertThat(formInfo.getResult()).isEqualTo("id=12;en");
	}

	@Test
	public void testWithRequestParamDefaultValue() throws IOException {
		ExtDirectFormLoadResult wrapper = (ExtDirectFormLoadResult) ControllerUtil.sendAndReceive(controller,
				"remoteProviderFormLoad", "method5", null, ExtDirectFormLoadResult.class);
		assertThat(wrapper.isSuccess()).isTrue();
		assertThat(wrapper.getData()).isNotNull();
		FormInfo formInfo = ControllerUtil.convertValue(wrapper.getData(), FormInfo.class);
		assertThat(formInfo.getResult()).isEqualTo("1;true");
	}

	@Test
	public void testWithRequestParamOptional() throws IOException {

		ExtDirectFormLoadResult wrapper = (ExtDirectFormLoadResult) ControllerUtil.sendAndReceive(controller,
				"remoteProviderFormLoad", "method6", null, ExtDirectFormLoadResult.class);
		assertThat(wrapper.isSuccess()).isTrue();
		assertThat(wrapper.getData()).isEqualTo("TEST:null");

		Map<String, Object> data = new HashMap<String, Object>();
		data.put("id", 11);
		wrapper = (ExtDirectFormLoadResult) ControllerUtil.sendAndReceive(controller, "remoteProviderFormLoad",
				"method6", data, ExtDirectFormLoadResult.class);
		assertThat(wrapper.isSuccess()).isTrue();
		assertThat(wrapper.getData()).isEqualTo("TEST:11");
	}

	@Test
	public void testResult() throws IOException {
		Map<String, Object> data = new HashMap<String, Object>();
		data.put("data", "one");
		data.put("success", true);
		ExtDirectFormLoadResult wrapper = (ExtDirectFormLoadResult) ControllerUtil.sendAndReceive(controller,
				"remoteProviderFormLoad", "method7", data, ExtDirectFormLoadResult.class);
		assertThat(wrapper.isSuccess()).isTrue();
		assertThat(wrapper.getData()).isEqualTo("one");

		data = new HashMap<String, Object>();
		data.put("data", "two");
		data.put("success", false);
		wrapper = (ExtDirectFormLoadResult) ControllerUtil.sendAndReceive(controller, "remoteProviderFormLoad",
				"method7", data, ExtDirectFormLoadResult.class);
		assertThat(wrapper.isSuccess()).isFalse();
		assertThat(wrapper.getData()).isEqualTo("two");
	}

	@Test
	public void testMultipleRequests() throws IOException {
		List<Map<String, Object>> edRequests = new ArrayList<Map<String, Object>>();
		MockHttpServletResponse response = new MockHttpServletResponse();
		MockHttpServletRequest request = new MockHttpServletRequest();

		edRequests.add(ControllerUtil.createRequestJson("remoteProvider", "method1", 1, new Object[] { 3, 2.5,
				"string.param" }));
		edRequests.add(ControllerUtil.createRequestJson("remoteProviderSimple", "method4", 2, new Object[] { 3, 2.5,
				"string.param" }));
		edRequests.add(ControllerUtil.createRequestJson("remoteProviderSimple", "method1", 3, null));

		Map<String, Object> data = new HashMap<String, Object>();
		data.put("d", 1.1);
		edRequests.add(ControllerUtil.createRequestJson("remoteProviderFormLoad", "method1", 4, data));

		data = new HashMap<String, Object>();
		data.put("d", 2.2);
		edRequests.add(ControllerUtil.createRequestJson("remoteProviderFormLoad", "method1", 5, data));

		edRequests.add(ControllerUtil.createRequestJson("remoteProviderSimple", "method6", 6, new Object[] { 20, 20 }));

		request.setContent(ControllerUtil.writeAsByte(edRequests));
		controller.router(request, response, Locale.ENGLISH);
		List<ExtDirectResponse> responses = ControllerUtil.readDirectResponses(response.getContentAsByteArray());

		assertThat(responses).hasSize(6);
		ExtDirectResponse resp = responses.get(0);
		assertThat(resp.getAction()).isEqualTo("remoteProvider");
		assertThat(resp.getMethod()).isEqualTo("method1");
		assertThat(resp.getType()).isEqualTo("exception");
		assertThat(resp.getTid()).isEqualTo(1);
		assertThat(resp.getResult()).isNull();
		assertThat(resp.getMessage()).isEqualTo("Server Error");
		assertThat(resp.getWhere()).isNull();
		checkMethodNotFoundResponse(responses.get(1));
		checkNoParametersResponse(responses.get(2), 3);

		checkFormLoadResult(responses.get(3), 1.1, 4);
		checkFormLoadResult(responses.get(4), 2.2, 5);

		checkIntParameterResult(responses.get(5), 6, 40);
	}

	private void checkIntParameterResult(ExtDirectResponse resp, int tid, int result) {
		assertThat(resp.getAction()).isEqualTo("remoteProviderSimple");
		assertThat(resp.getMethod()).isEqualTo("method6");
		assertThat(resp.getTid()).isEqualTo(tid);
		assertThat(resp.getType()).isEqualTo("rpc");
		assertThat(resp.getWhere()).isNull();
		assertThat(resp.getMessage()).isNull();
		assertThat(resp.getResult()).isEqualTo(result);
	}

	private void checkMethodNotFoundResponse(ExtDirectResponse resp) {
		assertThat(resp.getAction()).isEqualTo("remoteProviderSimple");
		assertThat(resp.getMethod()).isEqualTo("method4");
		assertThat(resp.getType()).isEqualTo("exception");
		assertThat(resp.getTid()).isEqualTo(2);
		assertThat(resp.getResult()).isNull();
		assertThat(resp.getMessage()).isEqualTo("Server Error");
		assertThat(resp.getWhere()).isNull();
	}

	private void checkFormLoadResult(ExtDirectResponse resp, double back, int tid) {
		assertThat(resp.getAction()).isEqualTo("remoteProviderFormLoad");
		assertThat(resp.getMethod()).isEqualTo("method1");
		assertThat(resp.getTid()).isEqualTo(tid);
		assertThat(resp.getType()).isEqualTo("rpc");
		assertThat(resp.getWhere()).isNull();
		assertThat(resp.getMessage()).isNull();
		assertThat(resp.getResult()).isNotNull();

		ExtDirectFormLoadResult wrapper = ControllerUtil.convertValue(resp.getResult(), ExtDirectFormLoadResult.class);
		assertThat(wrapper.isSuccess()).isTrue();
		assertThat(wrapper.getData()).isNotNull();

		FormInfo info = ControllerUtil.convertValue(wrapper.getData(), FormInfo.class);

		assertThat(Double.compare(back, info.getBack()) == 0).isTrue();
		assertThat(info.isAdmin()).isEqualTo(true);
		assertThat(info.getAge()).isEqualTo(31);
		assertThat(info.getName()).isEqualTo("Bob");
		assertThat(info.getSalary()).isEqualTo(new BigDecimal("10000.55"));
		assertThat(info.getBirthday()).isEqualTo(new GregorianCalendar(1980, Calendar.JANUARY, 15).getTime());
	}

	private void checkNoParametersResponse(ExtDirectResponse resp, int tid) {
		assertThat(resp.getAction()).isEqualTo("remoteProviderSimple");
		assertThat(resp.getMethod()).isEqualTo("method1");
		assertThat(resp.getTid()).isEqualTo(tid);
		assertThat(resp.getType()).isEqualTo("rpc");
		assertThat(resp.getWhere()).isNull();
		assertThat(resp.getMessage()).isNull();
		assertThat(resp.getResult()).isEqualTo("method1() called");
	}

}
