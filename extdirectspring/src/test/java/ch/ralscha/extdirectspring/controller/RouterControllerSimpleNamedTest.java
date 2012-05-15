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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.codehaus.jackson.map.ObjectMapper;
import org.joda.time.DateTime;
import org.joda.time.format.ISODateTimeFormat;
import org.junit.BeforeClass;
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

	@BeforeClass
	public static void beforeTest() {
		Locale.setDefault(Locale.US);
	}

	@Test
	public void testNoParameters() throws IOException {
		ControllerUtil.sendAndReceive(controller, "remoteProviderSimpleNamed",
				"method1", true, null, "method1() called");
	}

	@Test
	public void testNoParametersWithRequestParameter() throws IOException {
		Map<String, Object> params = new LinkedHashMap<String, Object>();
		params.put("requestparameter", "aValue");
		ControllerUtil.sendAndReceive(controller, "remoteProviderSimpleNamed",
				"method1", true, params, "method1() called");
	}

	@Test
	public void testWithParameters() throws IOException {
		Map<String, Object> params = new LinkedHashMap<String, Object>();
		params.put("d", 2.1);
		params.put("s", "aString");
		params.put("i", 30);
		ControllerUtil.sendAndReceive(controller, "remoteProviderSimpleNamed",
				"method2", true, params, "method2() called-30-2.100-aString");
	}

	@Test
	public void testWithWrongParameters() throws IOException {
		Map<String, Object> params = new LinkedHashMap<String, Object>();
		params.put("i", 20);
		params.put("de", 2.1);
		params.put("s", "aString");
		ControllerUtil.sendAndReceive(controller, "remoteProviderSimpleNamed",
				"method2", true, params, null);
	}

	@Test
	public void testWithMissingParameters() throws IOException {
		Map<String, Object> params = new LinkedHashMap<String, Object>();
		params.put("i", 20);
		params.put("s", "aString");
		ControllerUtil.sendAndReceive(controller, "remoteProviderSimpleNamed",
				"method2", true, params, null);
	}

	@Test
	public void testWithParametersWithTypeConversion() throws IOException {
		Map<String, Object> params = new LinkedHashMap<String, Object>();
		params.put("i", "30");
		params.put("s", 100.45);
		params.put("d", "3.141");
		ControllerUtil.sendAndReceive(controller, "remoteProviderSimpleNamed",
				"method2", true, params, "method2() called-30-3.141-100.45");
	}

	@Test
	public void testResultTrue() throws IOException {
		Map<String, Object> params = new LinkedHashMap<String, Object>();
		params.put("userName", "ralph");
		ControllerUtil.sendAndReceive(controller, "remoteProviderSimpleNamed",
				"method3", true, params, true);
	}

	@Test
	public void testResultFalse() throws IOException {
		Map<String, Object> params = new LinkedHashMap<String, Object>();
		params.put("userName", "joe");
		ControllerUtil.sendAndReceive(controller, "remoteProviderSimpleNamed",
				"method3", true, params, false);
	}

	@Test
	public void testResultNull() throws IOException {
		Map<String, Object> params = new LinkedHashMap<String, Object>();
		params.put("userName", "martin");
		ControllerUtil.sendAndReceive(controller, "remoteProviderSimpleNamed",
				"method3", true, params, Void.TYPE);
	}

	@Test
	public void testIntParameterAndResult() throws IOException {
		Map<String, Object> params = new LinkedHashMap<String, Object>();
		params.put("a", 10);
		params.put("b", 20);
		ControllerUtil.sendAndReceive(controller, "remoteProviderSimpleNamed",
				"method4", true, params, 30);
	}

	@Test
	public void testIntParameterAndResultWithTypeConversion()
			throws IOException {
		Map<String, Object> params = new LinkedHashMap<String, Object>();
		params.put("b", "40");
		params.put("a", "30");
		ControllerUtil.sendAndReceive(controller, "remoteProviderSimpleNamed",
				"method4", true, params, 70);
	}

	@Test
	public void testReturnsObject() throws IOException {
		Map<String, Object> params = new LinkedHashMap<String, Object>();
		params.put("d", 7.34);
		FormInfo info = (FormInfo) ControllerUtil.sendAndReceive(controller,
				"remoteProviderSimpleNamed", "method5", true, params,
				FormInfo.class);

		assertThat(info.getBack()).isEqualTo(7.34);
		assertThat(info.isAdmin()).isEqualTo(false);
		assertThat(info.getAge()).isEqualTo(32);
		assertThat(info.getName()).isEqualTo("John");
		assertThat(info.getSalary()).isEqualTo(new BigDecimal("8720.2"));
		assertThat(info.getBirthday()).isEqualTo(
				new GregorianCalendar(1986, Calendar.JULY, 22).getTime());
	}

	@Test
	public void testSupportedArguments() throws IOException {
		ControllerUtil.sendAndReceive(controller, "remoteProviderSimpleNamed",
				"method6", true, null, 42);
	}

	@Test
	public void testTypeConversion() throws IOException {
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
		ControllerUtil.sendAndReceive(controller, "remoteProviderSimpleNamed",
				"method7", true, params,
				"method7() called-true-c-PENDING-14-21-3.14-10.01-1-2");
	}

	@Test
	public void testMixParameterAndSupportedParameters() throws IOException {
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
		ControllerUtil.sendAndReceive(controller, "remoteProviderSimpleNamed",
				"method10", true, params,
				"method10() called-true-c-PENDING-14-21-3.14-10.01-1-2");
	}

	@Test
	public void testTypeConversionWithObjects() throws IOException {
		Row aRow = new Row(104, "myRow", true, "100.45");
		Map<String, Object> params = new LinkedHashMap<String, Object>();
		params.put("aRow", aRow);
		ControllerUtil.sendAndReceive(controller, "remoteProviderSimpleNamed",
				"method9", true, params,
				"Row [id=104, name=myRow, admin=true, salary=100.45]");
	}

	@Test
	public void testWithConversion() throws IOException {

		DateTime today = new DateTime();

		Map<String, Object> params = new LinkedHashMap<String, Object>();
		params.put("endDate", ISODateTimeFormat.dateTime().print(today));
		params.put("aDate", ISODateTimeFormat.date().print(today));
		params.put("normalParameter", "normalParameter");
		params.put("percent", "99.9%");

		Map<String, Object> resultMap = (Map<String, Object>) ControllerUtil
				.sendAndReceive(controller, "remoteProviderSimpleNamed",
						"method11", true, params, Map.class);

		assertThat(resultMap.get("endDate")).isEqualTo(today.getMillis());
		ObjectMapper mapper = new ObjectMapper();

		List<Object> expectedValue = mapper.readValue(
				mapper.writeValueAsString(today.toLocalDate()), List.class);
		Object actualValue = resultMap.get("jodaLocalDate");

		assertThat((List<Object>) resultMap.get("jodaLocalDate")).isEqualTo(
				expectedValue);
		assertThat(resultMap.get("percent")).isEqualTo(0.999);
		assertThat(resultMap.get("normalParameter")).isEqualTo(
				"normalParameter");
		assertThat(resultMap.get("remoteAddr")).isEqualTo("127.0.0.1");

	}

	@Test
	public void testDifferentParameterNames() throws IOException {
		ResultObject expectedResult = new ResultObject("Miller", 10, true);
		Map<String, Object> params = new LinkedHashMap<String, Object>();
		params.put("lastName", expectedResult.getName());
		params.put("theAge", expectedResult.getAge());
		params.put("active", expectedResult.getActive());
		ResultObject result = (ResultObject) ControllerUtil.sendAndReceive(
				controller, "remoteProviderSimpleNamed", "methodRP1", true,
				params, ResultObject.class);
		assertThat(result).isEqualTo(expectedResult);
	}

	@Test
	public void testDefaultValues() throws IOException {
		List<Map<String, Object>> multiRequests = new ArrayList<Map<String, Object>>();

		MockHttpServletResponse response = new MockHttpServletResponse();
		MockHttpServletRequest request = new MockHttpServletRequest();

		Map<String, Object> params = new LinkedHashMap<String, Object>();
		params.put("lastName", "Olstead");
		params.put("theAge", "33");
		params.put("active", false);
		Map<String, Object> edRequest = ControllerUtil
				.createRequestJsonNamedParam("remoteProviderSimpleNamed",
						"methodRP2", 2, params);
		multiRequests.add(edRequest);
		request.setContent(ControllerUtil.writeAsByte(edRequest));
		controller.router(request, response, Locale.ENGLISH);
		List<ExtDirectResponse> responses = ControllerUtil
				.readDirectResponses(response.getContentAsByteArray());
		assertResponse("remoteProviderSimpleNamed", "methodRP2", 2,
				new ResultObject("Olstead", 33, false), responses);

		params = new LinkedHashMap<String, Object>();
		params.put("theAge", "33");
		params.put("active", false);
		edRequest = ControllerUtil.createRequestJsonNamedParam(
				"remoteProviderSimpleNamed", "methodRP2", 3, params);
		multiRequests.add(edRequest);

		response = new MockHttpServletResponse();
		request = new MockHttpServletRequest();
		request.setContent(ControllerUtil.writeAsByte(edRequest));
		controller.router(request, response, Locale.ENGLISH);
		responses = ControllerUtil.readDirectResponses(response
				.getContentAsByteArray());
		assertResponse("remoteProviderSimpleNamed", "methodRP2", 3,
				new ResultObject("myName", 33, false), responses);

		params = new LinkedHashMap<String, Object>();
		params.put("lastName", "Olstead");
		params.put("active", false);
		edRequest = ControllerUtil.createRequestJsonNamedParam(
				"remoteProviderSimpleNamed", "methodRP2", 4, params);
		multiRequests.add(edRequest);

		response = new MockHttpServletResponse();
		request = new MockHttpServletRequest();
		request.setContent(ControllerUtil.writeAsByte(edRequest));
		controller.router(request, response, Locale.ENGLISH);
		responses = ControllerUtil.readDirectResponses(response
				.getContentAsByteArray());
		assertResponse("remoteProviderSimpleNamed", "methodRP2", 4,
				new ResultObject("Olstead", 20, false), responses);

		params = new LinkedHashMap<String, Object>();
		params.put("lastName", "Olstead");
		params.put("theAge", 36);
		edRequest = ControllerUtil.createRequestJsonNamedParam(
				"remoteProviderSimpleNamed", "methodRP2", 5, params);
		multiRequests.add(edRequest);

		response = new MockHttpServletResponse();
		request = new MockHttpServletRequest();
		request.setContent(ControllerUtil.writeAsByte(edRequest));
		controller.router(request, response, Locale.ENGLISH);
		responses = ControllerUtil.readDirectResponses(response
				.getContentAsByteArray());
		assertResponse("remoteProviderSimpleNamed", "methodRP2", 5,
				new ResultObject("Olstead", 36, true), responses);

		params = new LinkedHashMap<String, Object>();
		params.put("active", false);
		edRequest = ControllerUtil.createRequestJsonNamedParam(
				"remoteProviderSimpleNamed", "methodRP2", 6, params);
		multiRequests.add(edRequest);

		response = new MockHttpServletResponse();
		request = new MockHttpServletRequest();
		request.setContent(ControllerUtil.writeAsByte(edRequest));
		controller.router(request, response, Locale.ENGLISH);
		responses = ControllerUtil.readDirectResponses(response
				.getContentAsByteArray());
		assertResponse("remoteProviderSimpleNamed", "methodRP2", 6,
				new ResultObject("myName", 20, false), responses);

		params = new LinkedHashMap<String, Object>();
		params.put("lastName", "Miller");
		edRequest = ControllerUtil.createRequestJsonNamedParam(
				"remoteProviderSimpleNamed", "methodRP2", 7, params);
		multiRequests.add(edRequest);

		response = new MockHttpServletResponse();
		request = new MockHttpServletRequest();
		request.setContent(ControllerUtil.writeAsByte(edRequest));
		controller.router(request, response, Locale.ENGLISH);
		responses = ControllerUtil.readDirectResponses(response
				.getContentAsByteArray());
		assertResponse("remoteProviderSimpleNamed", "methodRP2", 7,
				new ResultObject("Miller", 20, true), responses);

		params = new LinkedHashMap<String, Object>();
		params.put("theAge", 55);
		edRequest = ControllerUtil.createRequestJsonNamedParam(
				"remoteProviderSimpleNamed", "methodRP2", 8, params);
		multiRequests.add(edRequest);

		response = new MockHttpServletResponse();
		request = new MockHttpServletRequest();
		request.setContent(ControllerUtil.writeAsByte(edRequest));
		controller.router(request, response, Locale.ENGLISH);
		responses = ControllerUtil.readDirectResponses(response
				.getContentAsByteArray());
		assertResponse("remoteProviderSimpleNamed", "methodRP2", 8,
				new ResultObject("myName", 55, true), responses);

		params = new LinkedHashMap<String, Object>();
		edRequest = ControllerUtil.createRequestJsonNamedParam(
				"remoteProviderSimpleNamed", "methodRP2", 9, params);
		multiRequests.add(edRequest);

		response = new MockHttpServletResponse();
		request = new MockHttpServletRequest();
		request.setContent(ControllerUtil.writeAsByte(edRequest));
		controller.router(request, response, Locale.ENGLISH);
		responses = ControllerUtil.readDirectResponses(response
				.getContentAsByteArray());
		assertResponse("remoteProviderSimpleNamed", "methodRP2", 9,
				new ResultObject("myName", 20, true), responses);

		response = new MockHttpServletResponse();
		request = new MockHttpServletRequest();
		request.setContent(ControllerUtil.writeAsByte(multiRequests));
		controller.router(request, response, Locale.ENGLISH);
		responses = ControllerUtil.readDirectResponses(response
				.getContentAsByteArray());
		assertThat(responses).hasSize(8);
		assertResponse("remoteProviderSimpleNamed", "methodRP2", 2,
				new ResultObject("Olstead", 33, false), responses.subList(0, 1));
		assertResponse("remoteProviderSimpleNamed", "methodRP2", 3,
				new ResultObject("myName", 33, false), responses.subList(1, 2));
		assertResponse("remoteProviderSimpleNamed", "methodRP2", 4,
				new ResultObject("Olstead", 20, false), responses.subList(2, 3));
		assertResponse("remoteProviderSimpleNamed", "methodRP2", 5,
				new ResultObject("Olstead", 36, true), responses.subList(3, 4));
		assertResponse("remoteProviderSimpleNamed", "methodRP2", 6,
				new ResultObject("myName", 20, false), responses.subList(4, 5));
		assertResponse("remoteProviderSimpleNamed", "methodRP2", 7,
				new ResultObject("Miller", 20, true), responses.subList(5, 6));
		assertResponse("remoteProviderSimpleNamed", "methodRP2", 8,
				new ResultObject("myName", 55, true), responses.subList(6, 7));
		assertResponse("remoteProviderSimpleNamed", "methodRP2", 9,
				new ResultObject("myName", 20, true), responses.subList(7, 8));
	}

	@Test
	public void testOptionalNoDefaultValue() throws IOException {
		List<Map<String, Object>> multiRequests = new ArrayList<Map<String, Object>>();

		MockHttpServletResponse response = new MockHttpServletResponse();
		MockHttpServletRequest request = new MockHttpServletRequest();

		Map<String, Object> params = new LinkedHashMap<String, Object>();
		params.put("lastName", "Olstead");
		params.put("theAge", "33");
		params.put("active", false);
		Map<String, Object> edRequest = ControllerUtil
				.createRequestJsonNamedParam("remoteProviderSimpleNamed",
						"methodRP3", 2, params);
		multiRequests.add(edRequest);
		request.setContent(ControllerUtil.writeAsByte(edRequest));
		controller.router(request, response, Locale.ENGLISH);
		List<ExtDirectResponse> responses = ControllerUtil
				.readDirectResponses(response.getContentAsByteArray());
		assertResponse("remoteProviderSimpleNamed", "methodRP3", 2,
				new ResultObject("Olstead", 33, false), responses);

		params = new LinkedHashMap<String, Object>();
		params.put("theAge", "33");
		params.put("active", false);
		edRequest = ControllerUtil.createRequestJsonNamedParam(
				"remoteProviderSimpleNamed", "methodRP3", 3, params);
		multiRequests.add(edRequest);

		response = new MockHttpServletResponse();
		request = new MockHttpServletRequest();
		request.setContent(ControllerUtil.writeAsByte(edRequest));
		controller.router(request, response, Locale.ENGLISH);
		responses = ControllerUtil.readDirectResponses(response
				.getContentAsByteArray());
		assertResponse("remoteProviderSimpleNamed", "methodRP3", 3,
				new ResultObject(null, 33, false), responses);

		params = new LinkedHashMap<String, Object>();
		params.put("lastName", "Olstead");
		params.put("active", false);
		edRequest = ControllerUtil.createRequestJsonNamedParam(
				"remoteProviderSimpleNamed", "methodRP3", 4, params);
		multiRequests.add(edRequest);

		response = new MockHttpServletResponse();
		request = new MockHttpServletRequest();
		request.setContent(ControllerUtil.writeAsByte(edRequest));
		controller.router(request, response, Locale.ENGLISH);
		responses = ControllerUtil.readDirectResponses(response
				.getContentAsByteArray());
		assertResponse("remoteProviderSimpleNamed", "methodRP3", 4,
				new ResultObject("Olstead", null, false), responses);

		params = new LinkedHashMap<String, Object>();
		params.put("lastName", "Olstead");
		params.put("theAge", 36);
		edRequest = ControllerUtil.createRequestJsonNamedParam(
				"remoteProviderSimpleNamed", "methodRP3", 5, params);
		multiRequests.add(edRequest);

		response = new MockHttpServletResponse();
		request = new MockHttpServletRequest();
		request.setContent(ControllerUtil.writeAsByte(edRequest));
		controller.router(request, response, Locale.ENGLISH);
		responses = ControllerUtil.readDirectResponses(response
				.getContentAsByteArray());
		assertResponse("remoteProviderSimpleNamed", "methodRP3", 5,
				new ResultObject("Olstead", 36, null), responses);

		params = new LinkedHashMap<String, Object>();
		params.put("active", false);
		edRequest = ControllerUtil.createRequestJsonNamedParam(
				"remoteProviderSimpleNamed", "methodRP3", 6, params);
		multiRequests.add(edRequest);

		response = new MockHttpServletResponse();
		request = new MockHttpServletRequest();
		request.setContent(ControllerUtil.writeAsByte(edRequest));
		controller.router(request, response, Locale.ENGLISH);
		responses = ControllerUtil.readDirectResponses(response
				.getContentAsByteArray());
		assertResponse("remoteProviderSimpleNamed", "methodRP3", 6,
				new ResultObject(null, null, false), responses);

		params = new LinkedHashMap<String, Object>();
		params.put("lastName", "Miller");
		edRequest = ControllerUtil.createRequestJsonNamedParam(
				"remoteProviderSimpleNamed", "methodRP3", 7, params);
		multiRequests.add(edRequest);

		response = new MockHttpServletResponse();
		request = new MockHttpServletRequest();
		request.setContent(ControllerUtil.writeAsByte(edRequest));
		controller.router(request, response, Locale.ENGLISH);
		responses = ControllerUtil.readDirectResponses(response
				.getContentAsByteArray());
		assertResponse("remoteProviderSimpleNamed", "methodRP3", 7,
				new ResultObject("Miller", null, null), responses);

		params = new LinkedHashMap<String, Object>();
		params.put("theAge", 55);
		edRequest = ControllerUtil.createRequestJsonNamedParam(
				"remoteProviderSimpleNamed", "methodRP3", 8, params);
		multiRequests.add(edRequest);

		response = new MockHttpServletResponse();
		request = new MockHttpServletRequest();
		request.setContent(ControllerUtil.writeAsByte(edRequest));
		controller.router(request, response, Locale.ENGLISH);
		responses = ControllerUtil.readDirectResponses(response
				.getContentAsByteArray());
		assertResponse("remoteProviderSimpleNamed", "methodRP3", 8,
				new ResultObject(null, 55, null), responses);

		params = new LinkedHashMap<String, Object>();
		edRequest = ControllerUtil.createRequestJsonNamedParam(
				"remoteProviderSimpleNamed", "methodRP3", 9, params);
		multiRequests.add(edRequest);

		response = new MockHttpServletResponse();
		request = new MockHttpServletRequest();
		request.setContent(ControllerUtil.writeAsByte(edRequest));
		controller.router(request, response, Locale.ENGLISH);
		responses = ControllerUtil.readDirectResponses(response
				.getContentAsByteArray());
		assertResponse("remoteProviderSimpleNamed", "methodRP3", 9,
				new ResultObject(null, null, null), responses);

		response = new MockHttpServletResponse();
		request = new MockHttpServletRequest();
		request.setContent(ControllerUtil.writeAsByte(multiRequests));
		controller.router(request, response, Locale.ENGLISH);
		responses = ControllerUtil.readDirectResponses(response
				.getContentAsByteArray());
		assertThat(responses).hasSize(8);
		assertResponse("remoteProviderSimpleNamed", "methodRP3", 2,
				new ResultObject("Olstead", 33, false), responses.subList(0, 1));
		assertResponse("remoteProviderSimpleNamed", "methodRP3", 3,
				new ResultObject(null, 33, false), responses.subList(1, 2));
		assertResponse("remoteProviderSimpleNamed", "methodRP3", 4,
				new ResultObject("Olstead", null, false),
				responses.subList(2, 3));
		assertResponse("remoteProviderSimpleNamed", "methodRP3", 5,
				new ResultObject("Olstead", 36, null), responses.subList(3, 4));
		assertResponse("remoteProviderSimpleNamed", "methodRP3", 6,
				new ResultObject(null, null, false), responses.subList(4, 5));
		assertResponse("remoteProviderSimpleNamed", "methodRP3", 7,
				new ResultObject("Miller", null, null), responses.subList(5, 6));
		assertResponse("remoteProviderSimpleNamed", "methodRP3", 8,
				new ResultObject(null, 55, null), responses.subList(6, 7));
		assertResponse("remoteProviderSimpleNamed", "methodRP3", 9,
				new ResultObject(null, null, null), responses.subList(7, 8));
	}

	private void assertResponse(String bean, String method, int tid,
			ResultObject expectedResult, List<ExtDirectResponse> responses) {

		assertThat(responses).hasSize(1);
		ExtDirectResponse resp = responses.get(0);
		assertThat(resp.getAction()).isEqualTo(bean);
		assertThat(resp.getMethod()).isEqualTo(method);
		assertThat(resp.getTid()).isEqualTo(tid);
		assertThat(resp.getType()).isEqualTo("rpc");
		assertThat(resp.getWhere()).isNull();
		assertThat(resp.getMessage()).isNull();

		ResultObject result = ControllerUtil.convertValue(resp.getResult(),
				ResultObject.class);

		assertThat(result).isEqualTo(expectedResult);
	}
}
