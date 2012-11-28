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

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.Map;

import org.joda.time.DateTime;
import org.joda.time.format.ISODateTimeFormat;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import ch.ralscha.extdirectspring.provider.FormInfo;
import ch.ralscha.extdirectspring.provider.RemoteProviderSimple.BusinessObject;
import ch.ralscha.extdirectspring.provider.Row;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Tests for {@link RouterController}.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:/testApplicationContext.xml")
public class RouterControllerSimpleTest {

	@Autowired
	private RouterController controller;

	@BeforeClass
	public static void beforeTest() {
		Locale.setDefault(Locale.US);
	}

	public Object[] a(Object... r) {
		return r;
	}

	@Test
	public void testBeanNotFound() {
		ControllerUtil.sendAndReceive(controller, "remoteProvider", "method1", a(3, 2.5, "string.param"), null);
	}

	@Test
	public void testMethodNotFound() {
		ControllerUtil.sendAndReceive(controller, "remoteProviderSimple", "method4", a(3, 2.5, "string.param"), null);
	}

	@Test
	public void testNoParameters() {
		ControllerUtil.sendAndReceive(controller, "remoteProviderSimple", "method1", null, "method1() called");
	}

	@Test
	public void testNoParametersWithRequestParameter() {
		ControllerUtil.sendAndReceive(controller, "remoteProviderSimple", "method1", a(1, "requestparameter"),
				"method1() called");
	}

	@Test
	public void testNoParameters2() {
		ControllerUtil.sendAndReceive(controller, "remoteProviderSimple", "method2", null, "method2() called");
	}

	@Test
	public void testWithParameters() {
		ControllerUtil.sendAndReceive(controller, "remoteProviderSimple", "method3", a(1, 3.1, "requestParameter"),
				"method3() called-1-3.1-requestParameter");
	}

	@Test
	public void testWithParametersWithTypeConversion() {
		ControllerUtil.sendAndReceive(controller, "remoteProviderSimple", "method3", a("10", "4.2", 20),
				"method3() called-10-4.2-20");
	}

	@Test
	public void testWithParametersNoRequestParameter() {
		ControllerUtil.sendAndReceive(controller, "remoteProviderSimple", "method3", null, null);
	}

	@Test
	public void testResultTrue() {
		ControllerUtil.sendAndReceive(controller, "remoteProviderSimple", "method5", a("ralph"), true);
	}

	@Test
	public void testResultFalse() {
		ControllerUtil.sendAndReceive(controller, "remoteProviderSimple", "method5", a("joe"), false);
	}

	@Test
	public void testResultNull() {
		ControllerUtil.sendAndReceive(controller, "remoteProviderSimple", "method5", a("martin"), Void.TYPE);

	}

	@Test
	public void testIntParameterAndResult() {
		ControllerUtil.sendAndReceive(controller, "remoteProviderSimple", "method6", a(10, 20), 30);
	}

	@Test
	public void testIntParameterAndResultWithTypeConversion() {
		ControllerUtil.sendAndReceive(controller, "remoteProviderSimple", "method6", a("30", "40"), 70);
	}

	@Test
	public void testResultStringNull() {
		ControllerUtil.sendAndReceive(controller, "remoteProviderSimple", "method7", null, Void.TYPE);
	}

	@Test
	public void testReturnsObject() {
		FormInfo info = (FormInfo) ControllerUtil.sendAndReceive(controller, "remoteProviderSimple", "method8",
				a(7.34), FormInfo.class);

		assertThat(info.getBack()).isEqualTo(7.34);
		assertThat(info.isAdmin()).isEqualTo(false);
		assertThat(info.getAge()).isEqualTo(32);
		assertThat(info.getName()).isEqualTo("John");
		assertThat(info.getSalary()).isEqualTo(new BigDecimal("8720.2"));
		assertThat(info.getBirthday()).isEqualTo(new GregorianCalendar(1986, Calendar.JULY, 22).getTime());
	}

	@Test
	public void testSupportedArguments() {
		ControllerUtil.sendAndReceive(controller, "remoteProviderSimple", "method9", null, 42);
	}

	@Test
	public void testTypeConversion() {
		ControllerUtil.sendAndReceive(controller, "remoteProviderSimple", "method10",
				a("true", "c", "ACTIVE", "14", "21", "3.14", "10.01", "1", "2"),
				"method10() called-true-c-ACTIVE-14-21-3.14-10.01-1-2");
	}

	@Test
	public void testMixParameterAndSupportedParameters() {
		ControllerUtil.sendAndReceive(controller, "remoteProviderSimple", "method13",
				a("true", "c", "ACTIVE", "14", "21", "3.14", "10.01", "1", "2"),
				"method13() called-true-c-ACTIVE-14-21-3.14-10.01-1-2");
	}

	@Test
	public void testWithConversion() throws IOException {

		DateTime today = new DateTime();

		Map<String, Object> resultMap = (Map<String, Object>) ControllerUtil.sendAndReceive(
				controller,
				"remoteProviderSimple",
				"method14",
				a(ISODateTimeFormat.dateTime().print(today), "normalParameter", ISODateTimeFormat.date().print(today),
						"99.9%"), Map.class);

		assertThat(resultMap.get("endDate")).isEqualTo(today.getMillis());
		ObjectMapper mapper = new ObjectMapper();

		Map<String, Object> expectedValue = mapper.readValue(mapper.writeValueAsString(today.toLocalDate()), Map.class);

		assertThat((Map<String, Object>) resultMap.get("jodaLocalDate")).isEqualTo(expectedValue);
		assertThat(resultMap.get("percent")).isEqualTo(0.999);
		assertThat(resultMap.get("normalParameter")).isEqualTo("normalParameter");
		assertThat(resultMap.get("remoteAddr")).isEqualTo("127.0.0.1");
	}

	@Test
	public void testTypeConversionWithObjects() {
		Row aRow = new Row(104, "myRow", true, "100.45");
		ControllerUtil.sendAndReceive(controller, "remoteProviderSimple", "method12", a(aRow),
				"Row [id=104, name=myRow, admin=true, salary=100.45]");
	}

	@Test
	public void methodRequiredHeaderWithoutValue() throws Exception {
		MockHttpServletRequest request = new MockHttpServletRequest();
		request.addHeader("header", "headerValue");
		ControllerUtil.sendAndReceive(controller, request, "remoteProviderSimple", "method15", a(1, "v"),
				"1;v;headerValue");
	}

	@Test
	public void methodRequiredHeaderWithValue() throws Exception {
		MockHttpServletRequest request = new MockHttpServletRequest();
		request.addHeader("header", "headerValue");
		request.addHeader("anotherName", "headerValue1");
		request.addHeader("anotherName", "headerValue2");
		ControllerUtil
				.sendAndReceive(controller, request, "remoteProviderSimple", "method16", a(11), "11;headerValue1");
	}

	@Test
	public void methodRequiredHeaderWithValueAndDefault1() throws Exception {
		MockHttpServletRequest request = new MockHttpServletRequest();
		request.addHeader("header", "headerValue");
		request.addHeader("anotherName", "headerValue1");

		ControllerUtil.sendAndReceive(controller, request, "remoteProviderSimple", "method17", null, "headerValue1");
	}

	@Test
	public void methodRequiredHeaderWithValueAndDefault2() throws Exception {
		ControllerUtil.sendAndReceive(controller, "remoteProviderSimple", "method17", null, "default");
	}

	@Test
	public void methodOptionalHeaderWithoutValueAndDefault1() throws Exception {
		MockHttpServletRequest request = new MockHttpServletRequest();
		request.addHeader("header", "headerValue");
		ControllerUtil.sendAndReceive(controller, request, "remoteProviderSimple", "method18", null, "headerValue");
	}

	@Test
	public void methodOptionalHeaderWithoutValueAndDefault2() throws Exception {
		ControllerUtil.sendAndReceive(controller, "remoteProviderSimple", "method18", null, "default");
	}

	@Test
	public void methodMultipleHeaders1() throws Exception {
		MockHttpServletRequest request = new MockHttpServletRequest();
		request.setSession(new MockHttpSession());
		request.addHeader("last", "lastHeader");

		ControllerUtil.sendAndReceive(controller, request, "remoteProviderSimple", "method19", a(100),
				"100;default1;default2;lastHeader");
	}

	@Test
	public void methodMultipleHeaders2() throws Exception {
		MockHttpServletRequest request = new MockHttpServletRequest();
		request.addHeader("last", "lastHeader");
		request.addHeader("header2", "2ndHeader");

		ControllerUtil.sendAndReceive(controller, request, "remoteProviderSimple", "method19", a(100),
				"100;default1;2ndHeader;lastHeader");
	}

	@Test
	public void methodMultipleHeaders3() throws Exception {
		MockHttpServletRequest request = new MockHttpServletRequest();
		request.addHeader("last", "last");
		request.addHeader("header1", "1st");
		request.addHeader("header2", "2nd");

		ControllerUtil.sendAndReceive(controller, request, "remoteProviderSimple", "method19", a(100),
				"100;1st;2nd;last");
	}

	@Test
	public void methodHeaderWithConversion() throws Exception {
		MockHttpServletRequest request = new MockHttpServletRequest();
		request.setSession(new MockHttpSession());
		request.addHeader("intHeader", "2");
		request.addHeader("booleanHeader", "true");

		ControllerUtil.sendAndReceive(controller, request, "remoteProviderSimple", "method20", null, "2;true");
	}

	@Test
	public void methodWithSimpleCollections() throws Exception {
		MockHttpServletRequest request = new MockHttpServletRequest();
		ControllerUtil.sendAndReceive(controller, request, "remoteProviderSimple", "method21",
				a("Ralph", new String[] { "one", "two" }, 10), "Ralph;one-two-;10");
		request = new MockHttpServletRequest();
		ControllerUtil.sendAndReceive(controller, request, "remoteProviderSimple", "method21",
				a("Ralph", new String[] { "one" }, 11), "Ralph;one-;11");
		request = new MockHttpServletRequest();
		ControllerUtil.sendAndReceive(controller, request, "remoteProviderSimple", "method21",
				a("Ralph", new String[] {}, 12), "Ralph;;12");
		request = new MockHttpServletRequest();
		ControllerUtil.sendAndReceive(controller, request, "remoteProviderSimple", "method21", a("Ralph", null, 13),
				"Ralph;;13");

		request = new MockHttpServletRequest();
		ControllerUtil.sendAndReceive(controller, request, "remoteProviderSimple", "method22",
				a("aStr", new int[] { 1, 2 }, 20), "aStr;1+2+;20");
		request = new MockHttpServletRequest();
		ControllerUtil.sendAndReceive(controller, request, "remoteProviderSimple", "method22",
				a("aStr", new int[] { 3, 1, 2 }, 21), "aStr;1+2+3+;21");
		request = new MockHttpServletRequest();
		ControllerUtil.sendAndReceive(controller, request, "remoteProviderSimple", "method22",
				a("aStr", new int[] { 3 }, 22), "aStr;3+;22");
		request = new MockHttpServletRequest();
		ControllerUtil.sendAndReceive(controller, request, "remoteProviderSimple", "method22",
				a("aStr", new int[] {}, 23), "aStr;;23");
		request = new MockHttpServletRequest();
		ControllerUtil.sendAndReceive(controller, request, "remoteProviderSimple", "method22", a("aStr", null, 24),
				"aStr;;24");
	}

	@Test
	public void methodWithSimpleArrays() throws Exception {
		MockHttpServletRequest request = new MockHttpServletRequest();
		ControllerUtil.sendAndReceive(controller, request, "remoteProviderSimple", "method23",
				a("Ralph", new String[] { "one", "two" }, 10), "Ralph;one-two-;10");
		request = new MockHttpServletRequest();
		ControllerUtil.sendAndReceive(controller, request, "remoteProviderSimple", "method23",
				a("Ralph", new String[] { "one" }, 11), "Ralph;one-;11");
		request = new MockHttpServletRequest();
		ControllerUtil.sendAndReceive(controller, request, "remoteProviderSimple", "method23",
				a("Ralph", new String[] {}, 12), "Ralph;;12");
		request = new MockHttpServletRequest();
		ControllerUtil.sendAndReceive(controller, request, "remoteProviderSimple", "method23", a("Ralph", null, 13),
				"Ralph;;13");

		request = new MockHttpServletRequest();
		ControllerUtil.sendAndReceive(controller, request, "remoteProviderSimple", "method24",
				a("aStr", 20, new int[] { 1, 2 }), "aStr;1+2+;20");
		request = new MockHttpServletRequest();
		ControllerUtil.sendAndReceive(controller, request, "remoteProviderSimple", "method24",
				a("aStr", 21, new int[] { 3, 1, 2 }), "aStr;3+1+2+;21");
		request = new MockHttpServletRequest();
		ControllerUtil.sendAndReceive(controller, request, "remoteProviderSimple", "method24",
				a("aStr", 22, new int[] { 3 }), "aStr;3+;22");
		request = new MockHttpServletRequest();
		ControllerUtil.sendAndReceive(controller, request, "remoteProviderSimple", "method24",
				a("aStr", 23, new int[] {}), "aStr;;23");
		request = new MockHttpServletRequest();
		ControllerUtil.sendAndReceive(controller, request, "remoteProviderSimple", "method24", a("aStr", 24, null),
				"aStr;;24");
	}

	@Test
	public void methodWithComplexCollections() throws Exception {
		BusinessObject bo1 = new BusinessObject(1, "one", new BigDecimal("1.11"));
		BusinessObject bo2 = new BusinessObject(2, "two", new BigDecimal("2.22"));
		BusinessObject bo3 = new BusinessObject(3, "three", new BigDecimal("3.33"));

		MockHttpServletRequest request = new MockHttpServletRequest();
		ControllerUtil.sendAndReceive(controller, request, "remoteProviderSimple", "method25",
				a("a", new BusinessObject[] { bo1 }, 1), "a;" + bo1.toString() + "-;1");
		request = new MockHttpServletRequest();
		ControllerUtil.sendAndReceive(controller, request, "remoteProviderSimple", "method25",
				a("b", new BusinessObject[] { bo1, bo2, bo3 }, 2), "b;" + bo1 + "-" + bo2 + "-" + bo3 + "-;2");
		request = new MockHttpServletRequest();
		ControllerUtil.sendAndReceive(controller, request, "remoteProviderSimple", "method25",
				a("c", new BusinessObject[] {}, 3), "c;;3");
		request = new MockHttpServletRequest();
		ControllerUtil.sendAndReceive(controller, request, "remoteProviderSimple", "method25", a("d", null, 3), "d;;3");
	}

	@Test
	public void methodWithComplexArrays() throws Exception {
		BusinessObject bo1 = new BusinessObject(4, "four", new BigDecimal("4.44"));
		BusinessObject bo2 = new BusinessObject(5, "five", new BigDecimal("5.55"));
		BusinessObject bo3 = new BusinessObject(6, "six", new BigDecimal("6.66"));

		MockHttpServletRequest request = new MockHttpServletRequest();
		ControllerUtil.sendAndReceive(controller, request, "remoteProviderSimple", "method26",
				a("e", new BusinessObject[] { bo1 }, 4), "e;" + bo1.toString() + "-;4");
		request = new MockHttpServletRequest();
		ControllerUtil.sendAndReceive(controller, request, "remoteProviderSimple", "method26",
				a("f", new BusinessObject[] { bo1, bo2, bo3 }, 5), "f;" + bo1 + "-" + bo2 + "-" + bo3 + "-;5");
		request = new MockHttpServletRequest();
		ControllerUtil.sendAndReceive(controller, request, "remoteProviderSimple", "method26",
				a("g", new BusinessObject[] {}, 6), "g;;6");
		request = new MockHttpServletRequest();
		ControllerUtil.sendAndReceive(controller, request, "remoteProviderSimple", "method26", a("h", null, 7), "h;;7");
	}
}
