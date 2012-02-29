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

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.joda.time.DateTime;
import org.joda.time.format.ISODateTimeFormat;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import ch.ralscha.extdirectspring.bean.ExtDirectResponse;
import ch.ralscha.extdirectspring.provider.FormInfo;
import ch.ralscha.extdirectspring.provider.RemoteProviderSimpleNamed.ResultObject;
import ch.ralscha.extdirectspring.provider.Row;

/**
 * Tests for {@link RouterController}.
 * 
 * @author Ralph Schaer
 */
@SuppressWarnings("all")
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:/testApplicationContext.xml")
public class RouterControllerSimpleNamedTest {

	@Autowired
	private RouterController controller;

	private MockHttpServletResponse response;
	private MockHttpServletRequest request;

	@Before
	public void beforeTest() {
		Locale.setDefault(Locale.US);
		response = new MockHttpServletResponse();
		request = new MockHttpServletRequest();
	}

	@Test
	public void testNoParameters() {
		Map<String, Object> edRequest = ControllerUtil.createRequestJsonNamedParam("remoteProviderSimpleNamed",
				"method1", 1, null);
		List<ExtDirectResponse> responses = controller.router(request, response, Locale.ENGLISH, edRequest);

		assertThat(responses).hasSize(1);
		checkNoParametersResponse(responses.get(0), 1);
	}

	@Test
	public void testNoParametersWithRequestParameter() {
		Map<String, Object> params = new LinkedHashMap<String, Object>();
		params.put("requestparameter", "aValue");
		Map<String, Object> edRequest = ControllerUtil.createRequestJsonNamedParam("remoteProviderSimpleNamed",
				"method1", 1, params);
		List<ExtDirectResponse> responses = controller.router(request, response, Locale.ENGLISH, edRequest);

		assertThat(responses).hasSize(1);
		ExtDirectResponse resp = responses.get(0);
		checkNoParametersResponse(resp, 1);
	}

	static void checkNoParametersResponse(ExtDirectResponse resp, int tid) {
		assertThat(resp.getAction()).isEqualTo("remoteProviderSimpleNamed");
		assertThat(resp.getMethod()).isEqualTo("method1");
		assertThat(resp.getTid()).isEqualTo(tid);
		assertThat(resp.getType()).isEqualTo("rpc");
		assertThat(resp.getWhere()).isNull();
		assertThat(resp.getMessage()).isNull();
		assertThat(resp.getResult()).isEqualTo("method1() called");
	}

	@Test
	public void testWithParameters() {
		Map<String, Object> params = new LinkedHashMap<String, Object>();
		params.put("d", 2.1);
		params.put("s", "aString");
		params.put("i", 20);

		Map<String, Object> edRequest = ControllerUtil.createRequestJsonNamedParam("remoteProviderSimpleNamed",
				"method2", 10, params);
		List<ExtDirectResponse> responses = controller.router(request, response, Locale.ENGLISH, edRequest);

		assertThat(responses).hasSize(1);
		ExtDirectResponse resp = responses.get(0);
		assertThat(resp.getAction()).isEqualTo("remoteProviderSimpleNamed");
		assertThat(resp.getMethod()).isEqualTo("method2");
		assertThat(resp.getTid()).isEqualTo(10);
		assertThat(resp.getType()).isEqualTo("rpc");
		assertThat(resp.getWhere()).isNull();
		assertThat(resp.getMessage()).isNull();
		assertThat(resp.getResult()).isEqualTo("method2() called-20-2.100-aString");
	}

	@Test
	public void testWithWrongParameters() {
		Map<String, Object> params = new LinkedHashMap<String, Object>();
		params.put("i", 20);
		params.put("de", 2.1);
		params.put("s", "aString");

		Map<String, Object> edRequest = ControllerUtil.createRequestJsonNamedParam("remoteProviderSimpleNamed",
				"method2", 10, params);
		controller.router(request, response, Locale.ENGLISH, edRequest);

		List<ExtDirectResponse> responses = controller.router(request, response, Locale.ENGLISH, edRequest);

		checkWrongParametersResult(responses);
	}

	@Test
	public void testWithMissingParameters() {
		Map<String, Object> params = new LinkedHashMap<String, Object>();
		params.put("i", 20);
		params.put("s", "aString");

		Map<String, Object> edRequest = ControllerUtil.createRequestJsonNamedParam("remoteProviderSimpleNamed",
				"method2", 10, params);
		controller.router(request, response, Locale.ENGLISH, edRequest);

		List<ExtDirectResponse> responses = controller.router(request, response, Locale.ENGLISH, edRequest);

		checkWrongParametersResult(responses);
	}

	private void checkWrongParametersResult(List<ExtDirectResponse> responses) {
		assertThat(responses).hasSize(1);
		ExtDirectResponse resp = responses.get(0);
		assertThat(resp.getAction()).isEqualTo("remoteProviderSimpleNamed");
		assertThat(resp.getMethod()).isEqualTo("method2");
		assertThat(resp.getTid()).isEqualTo(10);
		assertThat(resp.getType()).isEqualTo("exception");
		assertThat(resp.getWhere()).isNull();
		assertThat(resp.getMessage()).isEqualTo("Server Error");
		assertThat(resp.getResult()).isNull();
	}

	@Test
	public void testWithParametersWithTypeConversion() {
		Map<String, Object> params = new LinkedHashMap<String, Object>();
		params.put("i", "30");
		params.put("s", 100.45);
		params.put("d", "3.141");

		Map<String, Object> edRequest = ControllerUtil.createRequestJsonNamedParam("remoteProviderSimpleNamed",
				"method2", 11, params);
		List<ExtDirectResponse> responses = controller.router(request, response, Locale.ENGLISH, edRequest);

		assertThat(responses).hasSize(1);
		ExtDirectResponse resp = responses.get(0);
		assertThat(resp.getAction()).isEqualTo("remoteProviderSimpleNamed");
		assertThat(resp.getMethod()).isEqualTo("method2");
		assertThat(resp.getTid()).isEqualTo(11);
		assertThat(resp.getType()).isEqualTo("rpc");
		assertThat(resp.getWhere()).isNull();
		assertThat(resp.getMessage()).isNull();
		assertThat(resp.getResult()).isEqualTo("method2() called-30-3.141-100.45");
	}

	@Test
	public void testResultTrue() {
		Map<String, Object> params = new LinkedHashMap<String, Object>();
		params.put("userName", "ralph");

		Map<String, Object> edRequest = ControllerUtil.createRequestJsonNamedParam("remoteProviderSimpleNamed",
				"method3", 1, params);
		List<ExtDirectResponse> responses = controller.router(request, response, Locale.ENGLISH, edRequest);

		assertThat(responses).hasSize(1);
		ExtDirectResponse resp = responses.get(0);
		assertThat(resp.getAction()).isEqualTo("remoteProviderSimpleNamed");
		assertThat(resp.getMethod()).isEqualTo("method3");
		assertThat(resp.getTid()).isEqualTo(1);
		assertThat(resp.getType()).isEqualTo("rpc");
		assertThat(resp.getWhere()).isNull();
		assertThat(resp.getMessage()).isNull();
		assertThat(resp.getResult()).isEqualTo(true);
	}

	@Test
	public void testResultFalse() {
		Map<String, Object> params = new LinkedHashMap<String, Object>();
		params.put("userName", "joe");

		Map<String, Object> edRequest = ControllerUtil.createRequestJsonNamedParam("remoteProviderSimpleNamed",
				"method3", 1, params);
		List<ExtDirectResponse> responses = controller.router(request, response, Locale.ENGLISH, edRequest);

		assertThat(responses).hasSize(1);
		ExtDirectResponse resp = responses.get(0);
		assertThat(resp.getAction()).isEqualTo("remoteProviderSimpleNamed");
		assertThat(resp.getMethod()).isEqualTo("method3");
		assertThat(resp.getTid()).isEqualTo(1);
		assertThat(resp.getType()).isEqualTo("rpc");
		assertThat(resp.getWhere()).isNull();
		assertThat(resp.getMessage()).isNull();
		assertThat(resp.getResult()).isEqualTo(false);
	}

	@Test
	public void testResultNull() {

		Map<String, Object> params = new LinkedHashMap<String, Object>();
		params.put("userName", "martin");

		Map<String, Object> edRequest = ControllerUtil.createRequestJsonNamedParam("remoteProviderSimpleNamed",
				"method3", 1, params);
		List<ExtDirectResponse> responses = controller.router(request, response, Locale.ENGLISH, edRequest);

		assertThat(responses).hasSize(1);
		ExtDirectResponse resp = responses.get(0);
		assertThat(resp.getAction()).isEqualTo("remoteProviderSimpleNamed");
		assertThat(resp.getMethod()).isEqualTo("method3");
		assertThat(resp.getTid()).isEqualTo(1);
		assertThat(resp.getType()).isEqualTo("rpc");
		assertThat(resp.getWhere()).isNull();
		assertThat(resp.getMessage()).isNull();
		assertThat(resp.getResult()).isNull();
	}

	@Test
	public void testIntParameterAndResult() {
		Map<String, Object> params = new LinkedHashMap<String, Object>();
		params.put("a", 10);
		params.put("b", 20);

		Map<String, Object> edRequest = ControllerUtil.createRequestJsonNamedParam("remoteProviderSimpleNamed",
				"method4", 3, params);
		List<ExtDirectResponse> responses = controller.router(request, response, Locale.ENGLISH, edRequest);

		assertThat(responses).hasSize(1);
		ExtDirectResponse resp = responses.get(0);
		checkIntParameterResult(resp, 3, 30);
	}

	@Test
	public void testIntParameterAndResultWithTypeConversion() {
		Map<String, Object> params = new LinkedHashMap<String, Object>();
		params.put("b", "40");
		params.put("a", "30");

		Map<String, Object> edRequest = ControllerUtil.createRequestJsonNamedParam("remoteProviderSimpleNamed",
				"method4", 4, params);
		List<ExtDirectResponse> responses = controller.router(request, response, Locale.ENGLISH, edRequest);

		assertThat(responses).hasSize(1);
		ExtDirectResponse resp = responses.get(0);
		checkIntParameterResult(resp, 4, 70);
	}

	static void checkIntParameterResult(ExtDirectResponse resp, int tid, int result) {
		assertThat(resp.getAction()).isEqualTo("remoteProviderSimpleNamed");
		assertThat(resp.getMethod()).isEqualTo("method4");
		assertThat(resp.getTid()).isEqualTo(tid);
		assertThat(resp.getType()).isEqualTo("rpc");
		assertThat(resp.getWhere()).isNull();
		assertThat(resp.getMessage()).isNull();
		assertThat(resp.getResult()).isEqualTo(result);
	}

	@Test
	public void testReturnsObject() {
		Map<String, Object> params = new LinkedHashMap<String, Object>();
		params.put("d", 7.34);

		Map<String, Object> edRequest = ControllerUtil.createRequestJsonNamedParam("remoteProviderSimpleNamed",
				"method5", 1, params);
		List<ExtDirectResponse> responses = controller.router(request, response, Locale.ENGLISH, edRequest);

		assertThat(responses).hasSize(1);
		ExtDirectResponse resp = responses.get(0);

		assertThat(resp.getAction()).isEqualTo("remoteProviderSimpleNamed");
		assertThat(resp.getMethod()).isEqualTo("method5");
		assertThat(resp.getTid()).isEqualTo(1);
		assertThat(resp.getType()).isEqualTo("rpc");
		assertThat(resp.getWhere()).isNull();
		assertThat(resp.getMessage()).isNull();
		assertThat(resp.getResult()).isNotNull();

		assertThat(resp.getResult() instanceof FormInfo).isTrue();
		FormInfo info = (FormInfo) resp.getResult();

		assertThat(Double.compare(7.34, info.getBack()) == 0).isTrue();
		assertThat(info.isAdmin()).isEqualTo(false);
		assertThat(info.getAge()).isEqualTo(32);
		assertThat(info.getName()).isEqualTo("John");
		assertThat(info.getSalary()).isEqualTo(new BigDecimal("8720.20"));
		assertThat(info.getBirthday()).isEqualTo(new GregorianCalendar(1986, Calendar.JULY, 22).getTime());
	}

	@Test
	public void testSupportedArguments() {
		Map<String, Object> edRequest = ControllerUtil.createRequestJsonNamedParam("remoteProviderSimpleNamed",
				"method6", 1, null);

		List<ExtDirectResponse> responses = controller.router(request, response, Locale.ENGLISH, edRequest);

		assertThat(responses).hasSize(1);
		ExtDirectResponse resp = responses.get(0);

		assertThat(resp.getAction()).isEqualTo("remoteProviderSimpleNamed");
		assertThat(resp.getMethod()).isEqualTo("method6");
		assertThat(resp.getTid()).isEqualTo(1);
		assertThat(resp.getType()).isEqualTo("rpc");
		assertThat(resp.getWhere()).isNull();
		assertThat(resp.getMessage()).isNull();
		assertThat(resp.getResult()).isEqualTo(42l);
	}

	@Test
	public void testTypeConversion() {

		Map<String, Object> params = new LinkedHashMap<String, Object>();
		params.put("flag", "true");
		params.put("aCharacter", "c");
		params.put("workflow", "PENDING");
		params.put("aInt", "14");
		params.put("aLong", "21");
		params.put("aByte", "2");
		params.put("aDouble", "3.14");
		params.put("aFloat", "10.01");
		params.put("aShort", "1");

		Map<String, Object> edRequest = ControllerUtil.createRequestJsonNamedParam("remoteProviderSimpleNamed",
				"method7", 1, params);

		List<ExtDirectResponse> responses = controller.router(request, response, Locale.ENGLISH, edRequest);

		assertThat(responses).hasSize(1);
		ExtDirectResponse resp = responses.get(0);
		assertThat(resp.getAction()).isEqualTo("remoteProviderSimpleNamed");
		assertThat(resp.getMethod()).isEqualTo("method7");
		assertThat(resp.getTid()).isEqualTo(1);
		assertThat(resp.getType()).isEqualTo("rpc");
		assertThat(resp.getWhere()).isNull();
		assertThat(resp.getMessage()).isNull();
		assertThat(resp.getResult()).isEqualTo("method7() called-true-c-PENDING-14-21-3.14-10.01-1-2");
	}

	@Test
	public void testMixParameterAndSupportedParameters() {

		Map<String, Object> params = new LinkedHashMap<String, Object>();
		params.put("aLong", "21");
		params.put("aDouble", "3.14");
		params.put("aFloat", "10.01");
		params.put("flag", "true");
		params.put("aCharacter", "c");
		params.put("workflow", "PENDING");
		params.put("aInt", "14");
		params.put("aShort", "1");
		params.put("aByte", "2");

		Map<String, Object> edRequest = ControllerUtil.createRequestJsonNamedParam("remoteProviderSimpleNamed",
				"method10", 1, params);

		List<ExtDirectResponse> responses = controller.router(request, response, Locale.ENGLISH, edRequest);

		assertThat(responses).hasSize(1);
		ExtDirectResponse resp = responses.get(0);
		assertThat(resp.getAction()).isEqualTo("remoteProviderSimpleNamed");
		assertThat(resp.getMethod()).isEqualTo("method10");
		assertThat(resp.getTid()).isEqualTo(1);
		assertThat(resp.getType()).isEqualTo("rpc");
		assertThat(resp.getWhere()).isNull();
		assertThat(resp.getMessage()).isNull();
		assertThat(resp.getResult()).isEqualTo("method10() called-true-c-PENDING-14-21-3.14-10.01-1-2");
	}

	@Test
	public void testTypeConversionWithObjects() {
		Row aRow = new Row(104, "myRow", true, "100.45");
		Map<String, Object> params = new LinkedHashMap<String, Object>();
		params.put("aRow", aRow);

		Map<String, Object> edRequest = ControllerUtil.createRequestJsonNamedParam("remoteProviderSimpleNamed",
				"method9", 5, params);

		List<ExtDirectResponse> responses = controller.router(request, response, Locale.ENGLISH, edRequest);

		assertThat(responses).hasSize(1);
		ExtDirectResponse resp = responses.get(0);
		assertThat(resp.getAction()).isEqualTo("remoteProviderSimpleNamed");
		assertThat(resp.getMethod()).isEqualTo("method9");
		assertThat(resp.getTid()).isEqualTo(5);
		assertThat(resp.getType()).isEqualTo("rpc");
		assertThat(resp.getWhere()).isNull();
		assertThat(resp.getMessage()).isNull();

		assertThat(resp.getResult()).isEqualTo("Row [id=104, name=myRow, admin=true, salary=100.45]");
	}

	@Test
	public void testWithConversion() {

		DateTime today = new DateTime();

		Map<String, Object> params = new LinkedHashMap<String, Object>();
		params.put("endDate", ISODateTimeFormat.dateTime().print(today));
		params.put("aDate", ISODateTimeFormat.date().print(today));
		params.put("normalParameter", "normalParameter");
		params.put("percent", "99.9%");

		Map<String, Object> edRequest = ControllerUtil.createRequestJsonNamedParam("remoteProviderSimpleNamed",
				"method11", 1, params);

		List<ExtDirectResponse> responses = controller.router(request, response, Locale.ENGLISH, edRequest);

		assertThat(responses).hasSize(1);
		ExtDirectResponse resp = responses.get(0);
		assertThat(resp.getAction()).isEqualTo("remoteProviderSimpleNamed");
		assertThat(resp.getMethod()).isEqualTo("method11");
		assertThat(resp.getTid()).isEqualTo(1);
		assertThat(resp.getType()).isEqualTo("rpc");
		assertThat(resp.getWhere()).isNull();
		assertThat(resp.getMessage()).isNull();

		Map<String, Object> resultMap = (Map<String, Object>) resp.getResult();
		assertThat(resultMap.get("endDate")).isEqualTo(today.toDate());
		assertThat(resultMap.get("jodaLocalDate")).isEqualTo(today.toLocalDate());
		assertThat(resultMap.get("percent")).isEqualTo(new BigDecimal("0.999"));
		assertThat(resultMap.get("normalParameter")).isEqualTo("normalParameter");
		assertThat(resultMap.get("remoteAddr")).isEqualTo("127.0.0.1");
	}

	@Test
	public void testDifferentParameterNames() {
		ResultObject expectedResult = new ResultObject("Miller", 10, true);
		Map<String, Object> params = new LinkedHashMap<String, Object>();
		params.put("lastName", expectedResult.getName());
		params.put("theAge", expectedResult.getAge());
		params.put("active", expectedResult.getActive());

		Map<String, Object> edRequest = ControllerUtil.createRequestJsonNamedParam("remoteProviderSimpleNamed",
				"methodRP1", 1, params);
		List<ExtDirectResponse> responses = controller.router(request, response, Locale.ENGLISH, edRequest);
		assertResponse("remoteProviderSimpleNamed", "methodRP1", 1, expectedResult, responses);
	}

	@Test
	public void testDefaultValues() {
		List<Map<String, Object>> multiRequests = new ArrayList<Map<String, Object>>();

		Map<String, Object> params = new LinkedHashMap<String, Object>();
		params.put("lastName", "Olstead");
		params.put("theAge", "33");
		params.put("active", false);
		Map<String, Object> edRequest = ControllerUtil.createRequestJsonNamedParam("remoteProviderSimpleNamed",
				"methodRP2", 2, params);
		multiRequests.add(edRequest);
		List<ExtDirectResponse> responses = controller.router(request, response, Locale.ENGLISH, edRequest);
		assertResponse("remoteProviderSimpleNamed", "methodRP2", 2, new ResultObject("Olstead", 33, false), responses);

		params = new LinkedHashMap<String, Object>();
		params.put("theAge", "33");
		params.put("active", false);
		edRequest = ControllerUtil.createRequestJsonNamedParam("remoteProviderSimpleNamed", "methodRP2", 3, params);
		multiRequests.add(edRequest);
		responses = controller.router(request, response, Locale.ENGLISH, edRequest);
		assertResponse("remoteProviderSimpleNamed", "methodRP2", 3, new ResultObject("myName", 33, false), responses);

		params = new LinkedHashMap<String, Object>();
		params.put("lastName", "Olstead");
		params.put("active", false);
		edRequest = ControllerUtil.createRequestJsonNamedParam("remoteProviderSimpleNamed", "methodRP2", 4, params);
		multiRequests.add(edRequest);
		responses = controller.router(request, response, Locale.ENGLISH, edRequest);
		assertResponse("remoteProviderSimpleNamed", "methodRP2", 4, new ResultObject("Olstead", 20, false), responses);

		params = new LinkedHashMap<String, Object>();
		params.put("lastName", "Olstead");
		params.put("theAge", 36);
		edRequest = ControllerUtil.createRequestJsonNamedParam("remoteProviderSimpleNamed", "methodRP2", 5, params);
		multiRequests.add(edRequest);
		responses = controller.router(request, response, Locale.ENGLISH, edRequest);
		assertResponse("remoteProviderSimpleNamed", "methodRP2", 5, new ResultObject("Olstead", 36, true), responses);

		params = new LinkedHashMap<String, Object>();
		params.put("active", false);
		edRequest = ControllerUtil.createRequestJsonNamedParam("remoteProviderSimpleNamed", "methodRP2", 6, params);
		multiRequests.add(edRequest);
		responses = controller.router(request, response, Locale.ENGLISH, edRequest);
		assertResponse("remoteProviderSimpleNamed", "methodRP2", 6, new ResultObject("myName", 20, false), responses);

		params = new LinkedHashMap<String, Object>();
		params.put("lastName", "Miller");
		edRequest = ControllerUtil.createRequestJsonNamedParam("remoteProviderSimpleNamed", "methodRP2", 7, params);
		multiRequests.add(edRequest);
		responses = controller.router(request, response, Locale.ENGLISH, edRequest);
		assertResponse("remoteProviderSimpleNamed", "methodRP2", 7, new ResultObject("Miller", 20, true), responses);

		params = new LinkedHashMap<String, Object>();
		params.put("theAge", 55);
		edRequest = ControllerUtil.createRequestJsonNamedParam("remoteProviderSimpleNamed", "methodRP2", 8, params);
		multiRequests.add(edRequest);
		responses = controller.router(request, response, Locale.ENGLISH, edRequest);
		assertResponse("remoteProviderSimpleNamed", "methodRP2", 8, new ResultObject("myName", 55, true), responses);

		params = new LinkedHashMap<String, Object>();
		edRequest = ControllerUtil.createRequestJsonNamedParam("remoteProviderSimpleNamed", "methodRP2", 9, params);
		multiRequests.add(edRequest);
		responses = controller.router(request, response, Locale.ENGLISH, edRequest);
		assertResponse("remoteProviderSimpleNamed", "methodRP2", 9, new ResultObject("myName", 20, true), responses);

		responses = controller.router(request, response, Locale.ENGLISH, multiRequests);
		assertThat(responses).hasSize(8);
		assertResponse("remoteProviderSimpleNamed", "methodRP2", 2, new ResultObject("Olstead", 33, false),
				responses.subList(0, 1));
		assertResponse("remoteProviderSimpleNamed", "methodRP2", 3, new ResultObject("myName", 33, false),
				responses.subList(1, 2));
		assertResponse("remoteProviderSimpleNamed", "methodRP2", 4, new ResultObject("Olstead", 20, false),
				responses.subList(2, 3));
		assertResponse("remoteProviderSimpleNamed", "methodRP2", 5, new ResultObject("Olstead", 36, true),
				responses.subList(3, 4));
		assertResponse("remoteProviderSimpleNamed", "methodRP2", 6, new ResultObject("myName", 20, false),
				responses.subList(4, 5));
		assertResponse("remoteProviderSimpleNamed", "methodRP2", 7, new ResultObject("Miller", 20, true),
				responses.subList(5, 6));
		assertResponse("remoteProviderSimpleNamed", "methodRP2", 8, new ResultObject("myName", 55, true),
				responses.subList(6, 7));
		assertResponse("remoteProviderSimpleNamed", "methodRP2", 9, new ResultObject("myName", 20, true),
				responses.subList(7, 8));
	}

	@Test
	public void testOptionalNoDefaultValue() {
		List<Map<String, Object>> multiRequests = new ArrayList<Map<String, Object>>();

		Map<String, Object> params = new LinkedHashMap<String, Object>();
		params.put("lastName", "Olstead");
		params.put("theAge", "33");
		params.put("active", false);
		Map<String, Object> edRequest = ControllerUtil.createRequestJsonNamedParam("remoteProviderSimpleNamed",
				"methodRP3", 2, params);
		multiRequests.add(edRequest);
		List<ExtDirectResponse> responses = controller.router(request, response, Locale.ENGLISH, edRequest);
		assertResponse("remoteProviderSimpleNamed", "methodRP3", 2, new ResultObject("Olstead", 33, false), responses);

		params = new LinkedHashMap<String, Object>();
		params.put("theAge", "33");
		params.put("active", false);
		edRequest = ControllerUtil.createRequestJsonNamedParam("remoteProviderSimpleNamed", "methodRP3", 3, params);
		multiRequests.add(edRequest);
		responses = controller.router(request, response, Locale.ENGLISH, edRequest);
		assertResponse("remoteProviderSimpleNamed", "methodRP3", 3, new ResultObject(null, 33, false), responses);

		params = new LinkedHashMap<String, Object>();
		params.put("lastName", "Olstead");
		params.put("active", false);
		edRequest = ControllerUtil.createRequestJsonNamedParam("remoteProviderSimpleNamed", "methodRP3", 4, params);
		multiRequests.add(edRequest);
		responses = controller.router(request, response, Locale.ENGLISH, edRequest);
		assertResponse("remoteProviderSimpleNamed", "methodRP3", 4, new ResultObject("Olstead", null, false), responses);

		params = new LinkedHashMap<String, Object>();
		params.put("lastName", "Olstead");
		params.put("theAge", 36);
		edRequest = ControllerUtil.createRequestJsonNamedParam("remoteProviderSimpleNamed", "methodRP3", 5, params);
		multiRequests.add(edRequest);
		responses = controller.router(request, response, Locale.ENGLISH, edRequest);
		assertResponse("remoteProviderSimpleNamed", "methodRP3", 5, new ResultObject("Olstead", 36, null), responses);

		params = new LinkedHashMap<String, Object>();
		params.put("active", false);
		edRequest = ControllerUtil.createRequestJsonNamedParam("remoteProviderSimpleNamed", "methodRP3", 6, params);
		multiRequests.add(edRequest);
		responses = controller.router(request, response, Locale.ENGLISH, edRequest);
		assertResponse("remoteProviderSimpleNamed", "methodRP3", 6, new ResultObject(null, null, false), responses);

		params = new LinkedHashMap<String, Object>();
		params.put("lastName", "Miller");
		edRequest = ControllerUtil.createRequestJsonNamedParam("remoteProviderSimpleNamed", "methodRP3", 7, params);
		multiRequests.add(edRequest);
		responses = controller.router(request, response, Locale.ENGLISH, edRequest);
		assertResponse("remoteProviderSimpleNamed", "methodRP3", 7, new ResultObject("Miller", null, null), responses);

		params = new LinkedHashMap<String, Object>();
		params.put("theAge", 55);
		edRequest = ControllerUtil.createRequestJsonNamedParam("remoteProviderSimpleNamed", "methodRP3", 8, params);
		multiRequests.add(edRequest);
		responses = controller.router(request, response, Locale.ENGLISH, edRequest);
		assertResponse("remoteProviderSimpleNamed", "methodRP3", 8, new ResultObject(null, 55, null), responses);

		params = new LinkedHashMap<String, Object>();
		edRequest = ControllerUtil.createRequestJsonNamedParam("remoteProviderSimpleNamed", "methodRP3", 9, params);
		multiRequests.add(edRequest);
		responses = controller.router(request, response, Locale.ENGLISH, edRequest);
		assertResponse("remoteProviderSimpleNamed", "methodRP3", 9, new ResultObject(null, null, null), responses);

		responses = controller.router(request, response, Locale.ENGLISH, multiRequests);
		assertThat(responses).hasSize(8);
		assertResponse("remoteProviderSimpleNamed", "methodRP3", 2, new ResultObject("Olstead", 33, false),
				responses.subList(0, 1));
		assertResponse("remoteProviderSimpleNamed", "methodRP3", 3, new ResultObject(null, 33, false),
				responses.subList(1, 2));
		assertResponse("remoteProviderSimpleNamed", "methodRP3", 4, new ResultObject("Olstead", null, false),
				responses.subList(2, 3));
		assertResponse("remoteProviderSimpleNamed", "methodRP3", 5, new ResultObject("Olstead", 36, null),
				responses.subList(3, 4));
		assertResponse("remoteProviderSimpleNamed", "methodRP3", 6, new ResultObject(null, null, false),
				responses.subList(4, 5));
		assertResponse("remoteProviderSimpleNamed", "methodRP3", 7, new ResultObject("Miller", null, null),
				responses.subList(5, 6));
		assertResponse("remoteProviderSimpleNamed", "methodRP3", 8, new ResultObject(null, 55, null),
				responses.subList(6, 7));
		assertResponse("remoteProviderSimpleNamed", "methodRP3", 9, new ResultObject(null, null, null),
				responses.subList(7, 8));
	}

	private void assertResponse(String bean, String method, int tid, ResultObject expectedResult,
			List<ExtDirectResponse> responses) {

		assertThat(responses).hasSize(1);
		ExtDirectResponse resp = responses.get(0);
		assertThat(resp.getAction()).isEqualTo(bean);
		assertThat(resp.getMethod()).isEqualTo(method);
		assertThat(resp.getTid()).isEqualTo(tid);
		assertThat(resp.getType()).isEqualTo("rpc");
		assertThat(resp.getWhere()).isNull();
		assertThat(resp.getMessage()).isNull();

		assertThat(resp.getResult() instanceof ResultObject).isTrue();
		ResultObject result = (ResultObject) resp.getResult();

		assertThat(result).isEqualTo(expectedResult);
	}
}
