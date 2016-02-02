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
package ch.ralscha.extdirectspring.controller;

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

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import ch.ralscha.extdirectspring.bean.EdFormLoadResult;
import ch.ralscha.extdirectspring.bean.ExtDirectFormLoadResult;
import ch.ralscha.extdirectspring.bean.ExtDirectResponse;
import ch.ralscha.extdirectspring.provider.FormInfo;

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration("classpath:/testApplicationContext.xml")
public class RouterControllerFormLoadTest {

	@Autowired
	private WebApplicationContext wac;

	private MockMvc mockMvc;

	@BeforeClass
	public static void beforeTest() {
		Locale.setDefault(Locale.US);
	}

	@Before
	public void setupMockMvc() throws Exception {
		this.mockMvc = MockMvcBuilders.webAppContextSetup(this.wac).build();
	}

	@Test
	public void testFormLoad() {
		Map<String, Object> data = new HashMap<String, Object>();
		data.put("d", 3.141);
		ExtDirectFormLoadResult wrapper = (ExtDirectFormLoadResult) ControllerUtil
				.sendAndReceive(this.mockMvc, "remoteProviderFormLoad", "method1",
						ExtDirectFormLoadResult.class, data);

		assertThat(wrapper.isSuccess()).isTrue();
		assertThat(wrapper.getData()).isNotNull();

		FormInfo info = ControllerUtil.convertValue(wrapper.getData(), FormInfo.class);
		assertThat(info.getBack()).isEqualTo(3.141);
		assertThat(info.isAdmin()).isEqualTo(true);
		assertThat(info.getAge()).isEqualTo(31);
		assertThat(info.getName()).isEqualTo("Bob");
		assertThat(info.getSalary()).isEqualTo(new BigDecimal("10000.55"));
		assertThat(info.getBirthday())
				.isEqualTo(new GregorianCalendar(1980, Calendar.JANUARY, 15).getTime());
	}

	@Test
	public void testFormLoadReturnsNull() {
		ControllerUtil.sendAndReceive(this.mockMvc, "remoteProviderFormLoad", "method2",
				null, Void.TYPE);
	}

	@Test
	public void testWithSupportedArguments() {
		ExtDirectFormLoadResult wrapper = (ExtDirectFormLoadResult) ControllerUtil
				.sendAndReceive(this.mockMvc, "remoteProviderFormLoad", "method3",
						ExtDirectFormLoadResult.class);
		assertThat(wrapper.isSuccess()).isTrue();
		assertThat(wrapper.getData()).isNotNull();
		FormInfo formInfo = ControllerUtil.convertValue(wrapper.getData(),
				FormInfo.class);
		assertThat(formInfo.getResult()).isEqualTo("true;true;true;en");
	}

	@Test
	public void testWithRequestParam() {
		Map<String, Object> data = new HashMap<String, Object>();
		data.put("id", 12);
		ExtDirectFormLoadResult wrapper = (ExtDirectFormLoadResult) ControllerUtil
				.sendAndReceive(this.mockMvc, "remoteProviderFormLoad", "method4",
						ExtDirectFormLoadResult.class, data);

		assertThat(wrapper.isSuccess()).isTrue();
		assertThat(wrapper.getData()).isNotNull();
		FormInfo formInfo = ControllerUtil.convertValue(wrapper.getData(),
				FormInfo.class);
		assertThat(formInfo.getResult()).isEqualTo("id=12;en");
	}

	@Test
	public void testWithRequestParamDefaultValue() {
		ExtDirectFormLoadResult wrapper = (ExtDirectFormLoadResult) ControllerUtil
				.sendAndReceive(this.mockMvc, "remoteProviderFormLoad", "method5",
						ExtDirectFormLoadResult.class);
		assertThat(wrapper.isSuccess()).isTrue();
		assertThat(wrapper.getData()).isNotNull();
		FormInfo formInfo = ControllerUtil.convertValue(wrapper.getData(),
				FormInfo.class);
		assertThat(formInfo.getResult()).isEqualTo("1;true");
	}

	@Test
	public void testWithRequestParamOptional() {

		ExtDirectFormLoadResult wrapper = (ExtDirectFormLoadResult) ControllerUtil
				.sendAndReceive(this.mockMvc, "remoteProviderFormLoad", "method6",
						ExtDirectFormLoadResult.class);
		assertThat(wrapper.isSuccess()).isTrue();
		assertThat(wrapper.getData()).isEqualTo("TEST:null");

		Map<String, Object> data = new HashMap<String, Object>();
		data.put("id", 11);
		wrapper = (ExtDirectFormLoadResult) ControllerUtil.sendAndReceive(this.mockMvc,
				"remoteProviderFormLoad", "method6", ExtDirectFormLoadResult.class, data);
		assertThat(wrapper.isSuccess()).isTrue();
		assertThat(wrapper.getData()).isEqualTo("TEST:11");
	}

	@Test
	public void testResult() {
		Map<String, Object> data = new HashMap<String, Object>();
		data.put("data", "one");
		data.put("success", Boolean.TRUE);
		ExtDirectFormLoadResult wrapper = (ExtDirectFormLoadResult) ControllerUtil
				.sendAndReceive(this.mockMvc, "remoteProviderFormLoad", "method7",
						ExtDirectFormLoadResult.class, data);
		assertThat(wrapper.isSuccess()).isTrue();
		assertThat(wrapper.getData()).isEqualTo("one");

		data = new HashMap<String, Object>();
		data.put("data", "two");
		data.put("success", Boolean.FALSE);
		wrapper = (ExtDirectFormLoadResult) ControllerUtil.sendAndReceive(this.mockMvc,
				"remoteProviderFormLoad", "method7", ExtDirectFormLoadResult.class, data);
		assertThat(wrapper.isSuccess()).isFalse();
		assertThat(wrapper.getData()).isEqualTo("two");
	}

	@Test
	public void testWithRequestParamDefaultValueEd() {
		EdFormLoadResult wrapper = (EdFormLoadResult) ControllerUtil.sendAndReceive(
				this.mockMvc, "remoteProviderFormLoad", "method5Ed",
				EdFormLoadResult.class);
		assertThat(wrapper.success()).isTrue();
		assertThat(wrapper.data()).isNotNull();
		FormInfo formInfo = ControllerUtil.convertValue(wrapper.data(), FormInfo.class);
		assertThat(formInfo.getResult()).isEqualTo("1;true");
	}

	@Test
	public void testWithRequestParamOptionalEd() {

		EdFormLoadResult wrapper = (EdFormLoadResult) ControllerUtil.sendAndReceive(
				this.mockMvc, "remoteProviderFormLoad", "method6Ed",
				EdFormLoadResult.class);
		assertThat(wrapper.success()).isTrue();
		assertThat(wrapper.data()).isEqualTo("TEST:null");

		Map<String, Object> data = new HashMap<String, Object>();
		data.put("id", 11);
		wrapper = (EdFormLoadResult) ControllerUtil.sendAndReceive(this.mockMvc,
				"remoteProviderFormLoad", "method6Ed", EdFormLoadResult.class, data);
		assertThat(wrapper.success()).isTrue();
		assertThat(wrapper.data()).isEqualTo("TEST:11");
	}

	@Test
	public void testResultEd() {
		Map<String, Object> data = new HashMap<String, Object>();
		data.put("data", "one");
		data.put("success", Boolean.TRUE);
		EdFormLoadResult wrapper = (EdFormLoadResult) ControllerUtil.sendAndReceive(
				this.mockMvc, "remoteProviderFormLoad", "method7Ed",
				EdFormLoadResult.class, data);
		assertThat(wrapper.success()).isTrue();
		assertThat(wrapper.data()).isEqualTo("one");

		data = new HashMap<String, Object>();
		data.put("data", "two");
		data.put("success", Boolean.FALSE);
		wrapper = (EdFormLoadResult) ControllerUtil.sendAndReceive(this.mockMvc,
				"remoteProviderFormLoad", "method7Ed", EdFormLoadResult.class, data);
		assertThat(wrapper.success()).isFalse();
		assertThat(wrapper.data()).isEqualTo("two");
	}

	@Test
	public void testMultipleRequests() throws Exception {
		List<String> edRequests = new ArrayList<String>();

		edRequests.add(ControllerUtil.createEdsRequest("remoteProvider", "method1", 1,
				new Object[] { 3, 2.5, "string.param" }));
		edRequests.add(ControllerUtil.createEdsRequest("remoteProviderSimple", "method4",
				2, new Object[] { 3, 2.5, "string.param" }));
		edRequests.add(ControllerUtil.createEdsRequest("remoteProviderSimple", "method1",
				3, null));

		Map<String, Object> data = new HashMap<String, Object>();
		data.put("d", 1.1);
		edRequests.add(ControllerUtil.createEdsRequest("remoteProviderFormLoad",
				"method1", 4, data));

		data = new HashMap<String, Object>();
		data.put("d", 2.2);
		edRequests.add(ControllerUtil.createEdsRequest("remoteProviderFormLoad",
				"method1", 5, data));

		edRequests.add(ControllerUtil.createEdsRequest("remoteProviderSimple", "method6",
				6, new Object[] { 20, 20 }));

		StringBuilder sb = new StringBuilder();
		sb.append("[");
		for (String requests : edRequests) {
			sb.append(requests);
			sb.append(",");
		}
		sb.replace(sb.length() - 1, sb.length(), "]");

		MvcResult result = ControllerUtil.performRouterRequest(this.mockMvc,
				sb.toString());
		List<ExtDirectResponse> responses = ControllerUtil
				.readDirectResponses(result.getResponse().getContentAsByteArray());

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

	private static void checkIntParameterResult(ExtDirectResponse resp, int tid,
			int result) {
		assertThat(resp.getAction()).isEqualTo("remoteProviderSimple");
		assertThat(resp.getMethod()).isEqualTo("method6");
		assertThat(resp.getTid()).isEqualTo(tid);
		assertThat(resp.getType()).isEqualTo("rpc");
		assertThat(resp.getWhere()).isNull();
		assertThat(resp.getMessage()).isNull();
		assertThat(resp.getResult()).isEqualTo(result);
	}

	private static void checkMethodNotFoundResponse(ExtDirectResponse resp) {
		assertThat(resp.getAction()).isEqualTo("remoteProviderSimple");
		assertThat(resp.getMethod()).isEqualTo("method4");
		assertThat(resp.getType()).isEqualTo("exception");
		assertThat(resp.getTid()).isEqualTo(2);
		assertThat(resp.getResult()).isNull();
		assertThat(resp.getMessage()).isEqualTo("Server Error");
		assertThat(resp.getWhere()).isNull();
	}

	private static void checkFormLoadResult(ExtDirectResponse resp, double back,
			int tid) {
		assertThat(resp.getAction()).isEqualTo("remoteProviderFormLoad");
		assertThat(resp.getMethod()).isEqualTo("method1");
		assertThat(resp.getTid()).isEqualTo(tid);
		assertThat(resp.getType()).isEqualTo("rpc");
		assertThat(resp.getWhere()).isNull();
		assertThat(resp.getMessage()).isNull();
		assertThat(resp.getResult()).isNotNull();

		ExtDirectFormLoadResult wrapper = ControllerUtil.convertValue(resp.getResult(),
				ExtDirectFormLoadResult.class);
		assertThat(wrapper.isSuccess()).isTrue();
		assertThat(wrapper.getData()).isNotNull();

		FormInfo info = ControllerUtil.convertValue(wrapper.getData(), FormInfo.class);

		assertThat(Double.compare(back, info.getBack()) == 0).isTrue();
		assertThat(info.isAdmin()).isEqualTo(true);
		assertThat(info.getAge()).isEqualTo(31);
		assertThat(info.getName()).isEqualTo("Bob");
		assertThat(info.getSalary()).isEqualTo(new BigDecimal("10000.55"));
		assertThat(info.getBirthday())
				.isEqualTo(new GregorianCalendar(1980, Calendar.JANUARY, 15).getTime());
	}

	private static void checkNoParametersResponse(ExtDirectResponse resp, int tid) {
		assertThat(resp.getAction()).isEqualTo("remoteProviderSimple");
		assertThat(resp.getMethod()).isEqualTo("method1");
		assertThat(resp.getTid()).isEqualTo(tid);
		assertThat(resp.getType()).isEqualTo("rpc");
		assertThat(resp.getWhere()).isNull();
		assertThat(resp.getMessage()).isNull();
		assertThat(resp.getResult()).isEqualTo("method1() called");
	}

}
