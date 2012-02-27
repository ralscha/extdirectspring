/**
 * Copyright 2010-2011 Ralph Schaer <ralphschaer@gmail.com>
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

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
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

	private MockHttpServletResponse response;
	private MockHttpServletRequest request;

	@Before
	public void beforeTest() {
		response = new MockHttpServletResponse();
		request = new MockHttpServletRequest();
	}

	@Test
	public void testFormLoad() {
		Map<String, Object> data = new HashMap<String, Object>();
		data.put("d", 3.141);
		Map<String, Object> edRequest = ControllerUtil.createRequestJson("remoteProviderFormLoad", "method1", 1, data);
		List<ExtDirectResponse> responses = controller.router(request, response, Locale.ENGLISH, edRequest);

		assertThat(responses).hasSize(1);
		ExtDirectResponse resp = responses.get(0);

		checkFormLoadResult(resp, 3.141, 1);
	}

	@Test
	public void testFormLoadReturnsNull() {
		Map<String, Object> edRequest = ControllerUtil.createRequestJson("remoteProviderFormLoad", "method2", 1, null);
		List<ExtDirectResponse> responses = controller.router(request, response, Locale.ENGLISH, edRequest);

		assertThat(responses).hasSize(1);
		ExtDirectResponse resp = responses.get(0);

		assertThat(resp.getAction()).isEqualTo("remoteProviderFormLoad");
		assertThat(resp.getMethod()).isEqualTo("method2");
		assertThat(resp.getTid()).isEqualTo(1);
		assertThat(resp.getType()).isEqualTo("rpc");
		assertThat(resp.getWhere()).isNull();
		assertThat(resp.getMessage()).isNull();
		assertThat(resp.getResult()).isNull();
	}

	@Test
	public void testWithSupportedArguments() {
		Map<String, Object> edRequest = ControllerUtil.createRequestJson("remoteProviderFormLoad", "method3", 1, null);
		List<ExtDirectResponse> responses = controller.router(request, response, Locale.ENGLISH, edRequest);

		assertThat(responses).hasSize(1);
		ExtDirectResponse resp = responses.get(0);

		assertThat(resp.getAction()).isEqualTo("remoteProviderFormLoad");
		assertThat(resp.getMethod()).isEqualTo("method3");
		assertThat(resp.getTid()).isEqualTo(1);
		assertThat(resp.getType()).isEqualTo("rpc");
		assertThat(resp.getWhere()).isNull();
		assertThat(resp.getMessage()).isNull();
		assertThat(resp.getResult()).isNull();
	}

	@Test
	public void testWithRequestParam() {
		Map<String, Object> data = new HashMap<String, Object>();
		data.put("id", 10);
		Map<String, Object> edRequest = ControllerUtil.createRequestJson("remoteProviderFormLoad", "method4", 1, data);
		List<ExtDirectResponse> responses = controller.router(request, response, Locale.ENGLISH, edRequest);

		assertThat(responses).hasSize(1);
		ExtDirectResponse resp = responses.get(0);

		assertThat(resp.getAction()).isEqualTo("remoteProviderFormLoad");
		assertThat(resp.getMethod()).isEqualTo("method4");
		assertThat(resp.getTid()).isEqualTo(1);
		assertThat(resp.getType()).isEqualTo("rpc");
		assertThat(resp.getWhere()).isNull();
		assertThat(resp.getMessage()).isNull();
		assertThat(resp.getResult()).isNotNull();

		assertThat(resp.getResult() instanceof ExtDirectFormLoadResult).isTrue();
		ExtDirectFormLoadResult wrapper = (ExtDirectFormLoadResult) resp.getResult();
		assertThat(wrapper.isSuccess()).isTrue();
		assertThat(wrapper.getData()).isNotNull();
		assertThat(wrapper.getData() instanceof FormInfo).isTrue();
	}

	@Test
	public void testWithRequestParamDefaultValue() {
		Map<String, Object> edRequest = ControllerUtil.createRequestJson("remoteProviderFormLoad", "method5", 1, null);
		List<ExtDirectResponse> responses = controller.router(request, response, Locale.ENGLISH, edRequest);

		assertThat(responses).hasSize(1);
		ExtDirectResponse resp = responses.get(0);

		assertThat(resp.getAction()).isEqualTo("remoteProviderFormLoad");
		assertThat(resp.getMethod()).isEqualTo("method5");
		assertThat(resp.getTid()).isEqualTo(1);
		assertThat(resp.getType()).isEqualTo("rpc");
		assertThat(resp.getWhere()).isNull();
		assertThat(resp.getMessage()).isNull();
		assertThat(resp.getResult()).isNotNull();

		assertThat(resp.getResult() instanceof ExtDirectFormLoadResult).isTrue();
		ExtDirectFormLoadResult wrapper = (ExtDirectFormLoadResult) resp.getResult();
		assertThat(wrapper.isSuccess()).isTrue();
		assertThat(wrapper.getData()).isNull();
	}

	@Test
	public void testWithRequestParamOptional() {
		Map<String, Object> edRequest = ControllerUtil.createRequestJson("remoteProviderFormLoad", "method6", 1, null);
		List<ExtDirectResponse> responses = controller.router(request, response, Locale.ENGLISH, edRequest);

		assertThat(responses).hasSize(1);
		ExtDirectResponse resp = responses.get(0);

		assertThat(resp.getAction()).isEqualTo("remoteProviderFormLoad");
		assertThat(resp.getMethod()).isEqualTo("method6");
		assertThat(resp.getTid()).isEqualTo(1);
		assertThat(resp.getType()).isEqualTo("rpc");
		assertThat(resp.getWhere()).isNull();
		assertThat(resp.getMessage()).isNull();
		assertThat(resp.getResult()).isNotNull();

		assertThat(resp.getResult() instanceof ExtDirectFormLoadResult).isTrue();
		ExtDirectFormLoadResult wrapper = (ExtDirectFormLoadResult) resp.getResult();
		assertThat(wrapper.isSuccess()).isTrue();
		assertThat(wrapper.getData()).isEqualTo("TEST");

		Map<String, Object> data = new HashMap<String, Object>();
		data.put("id", 11);
		edRequest = ControllerUtil.createRequestJson("remoteProviderFormLoad", "method6", 1, data);
		responses = controller.router(request, response, Locale.ENGLISH, edRequest);

		assertThat(responses).hasSize(1);
		resp = responses.get(0);

		assertThat(resp.getAction()).isEqualTo("remoteProviderFormLoad");
		assertThat(resp.getMethod()).isEqualTo("method6");
		assertThat(resp.getTid()).isEqualTo(1);
		assertThat(resp.getType()).isEqualTo("rpc");
		assertThat(resp.getWhere()).isNull();
		assertThat(resp.getMessage()).isNull();
		assertThat(resp.getResult()).isNotNull();

		assertThat(resp.getResult() instanceof ExtDirectFormLoadResult).isTrue();
		wrapper = (ExtDirectFormLoadResult) resp.getResult();
		assertThat(wrapper.isSuccess()).isTrue();
		assertThat(wrapper.getData()).isEqualTo("TEST");
	}

	private void checkFormLoadResult(ExtDirectResponse resp, double back, int tid) {
		assertThat(resp.getAction()).isEqualTo("remoteProviderFormLoad");
		assertThat(resp.getMethod()).isEqualTo("method1");
		assertThat(resp.getTid()).isEqualTo(tid);
		assertThat(resp.getType()).isEqualTo("rpc");
		assertThat(resp.getWhere()).isNull();
		assertThat(resp.getMessage()).isNull();
		assertThat(resp.getResult()).isNotNull();

		assertThat(resp.getResult() instanceof ExtDirectFormLoadResult).isTrue();
		ExtDirectFormLoadResult wrapper = (ExtDirectFormLoadResult) resp.getResult();
		assertThat(wrapper.isSuccess()).isTrue();
		assertThat(wrapper.getData()).isNotNull();
		assertThat(wrapper.getData() instanceof FormInfo).isTrue();
		FormInfo info = (FormInfo) wrapper.getData();

		assertThat(Double.compare(back, info.getBack()) == 0).isTrue();
		assertThat(info.isAdmin()).isEqualTo(true);
		assertThat(info.getAge()).isEqualTo(31);
		assertThat(info.getName()).isEqualTo("Bob");
		assertThat(info.getSalary()).isEqualTo(new BigDecimal("10000.55"));
		assertThat(info.getBirthday()).isEqualTo(new GregorianCalendar(1980, Calendar.JANUARY, 15).getTime());
	}

	@Test
	public void testMultipleRequests() {
		List<Map<String, Object>> edRequests = new ArrayList<Map<String, Object>>();
		edRequests.add(ControllerUtil.createRequestJson("remoteProvider", "method1", 1, 3, 2.5, "string.param"));
		edRequests.add(ControllerUtil.createRequestJson("remoteProviderSimple", "method4", 2, 3, 2.5, "string.param"));
		edRequests.add(ControllerUtil.createRequestJson("remoteProviderSimple", "method1", 3, null));

		Map<String, Object> data = new HashMap<String, Object>();
		data.put("d", 1.1);
		edRequests.add(ControllerUtil.createRequestJson("remoteProviderFormLoad", "method1", 4, data));

		data = new HashMap<String, Object>();
		data.put("d", 2.2);
		edRequests.add(ControllerUtil.createRequestJson("remoteProviderFormLoad", "method1", 5, data));

		edRequests.add(ControllerUtil.createRequestJson("remoteProviderSimple", "method6", 6, 20, 20));

		List<ExtDirectResponse> responses = controller.router(request, response, Locale.ENGLISH, edRequests);

		assertThat(responses).hasSize(6);
		RouterControllerSimpleTest.checkBeanNotFoundResponse(responses.get(0));
		RouterControllerSimpleTest.checkMethodNotFoundResponse(responses.get(1));
		RouterControllerSimpleTest.checkNoParametersResponse(responses.get(2), 3);

		checkFormLoadResult(responses.get(3), 1.1, 4);
		checkFormLoadResult(responses.get(4), 2.2, 5);

		RouterControllerSimpleTest.checkIntParameterResult(responses.get(5), 6, 40);
	}

	@Test
	public void testResult() {
		Map<String, Object> data = new HashMap<String, Object>();
		data.put("data", "one");
		data.put("success", true);
		Map<String, Object> edRequest = ControllerUtil.createRequestJson("remoteProviderFormLoad", "method7", 1, data);
		List<ExtDirectResponse> responses = controller.router(request, response, Locale.ENGLISH, edRequest);

		assertThat(responses).hasSize(1);
		ExtDirectResponse resp = responses.get(0);

		assertThat(resp.getAction()).isEqualTo("remoteProviderFormLoad");
		assertThat(resp.getMethod()).isEqualTo("method7");
		assertThat(resp.getTid()).isEqualTo(1);
		assertThat(resp.getType()).isEqualTo("rpc");
		assertThat(resp.getWhere()).isNull();
		assertThat(resp.getMessage()).isNull();
		assertThat(resp.getResult()).isNotNull();

		assertThat(resp.getResult() instanceof ExtDirectFormLoadResult).isTrue();
		ExtDirectFormLoadResult wrapper = (ExtDirectFormLoadResult) resp.getResult();
		assertThat(wrapper.isSuccess()).isTrue();
		assertThat(wrapper.getData()).isEqualTo("one");

		data = new HashMap<String, Object>();
		data.put("data", "two");
		data.put("success", false);
		edRequest = ControllerUtil.createRequestJson("remoteProviderFormLoad", "method7", 1, data);
		responses = controller.router(request, response, Locale.ENGLISH, edRequest);

		assertThat(responses).hasSize(1);
		resp = responses.get(0);

		assertThat(resp.getAction()).isEqualTo("remoteProviderFormLoad");
		assertThat(resp.getMethod()).isEqualTo("method7");
		assertThat(resp.getTid()).isEqualTo(1);
		assertThat(resp.getType()).isEqualTo("rpc");
		assertThat(resp.getWhere()).isNull();
		assertThat(resp.getMessage()).isNull();
		assertThat(resp.getResult()).isNotNull();

		assertThat(resp.getResult() instanceof ExtDirectFormLoadResult).isTrue();
		wrapper = (ExtDirectFormLoadResult) resp.getResult();
		assertThat(wrapper.isSuccess()).isFalse();
		assertThat(wrapper.getData()).isEqualTo("two");
	}
}
