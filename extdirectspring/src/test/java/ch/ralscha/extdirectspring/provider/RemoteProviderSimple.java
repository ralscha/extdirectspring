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
package ch.ralscha.extdirectspring.provider;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;

import java.math.BigDecimal;
import java.security.Principal;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.joda.time.LocalDate;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;
import org.springframework.format.annotation.NumberFormat;
import org.springframework.stereotype.Service;

import ch.ralscha.extdirectspring.annotation.ExtDirectMethod;

@Service
@SuppressWarnings("unused")
public class RemoteProviderSimple {

	private enum StatusEnum {
		ACTIVE, INACTIVE
	}

	@ExtDirectMethod(group = "group1")
	public String method1() {
		return "method1() called";
	}

	@ExtDirectMethod
	public String method2() {
		return "method2() called";
	}

	@ExtDirectMethod(group = "group2")
	public String method3(long i, Double d, String s) {
		return String.format("method3() called-%d-%.1f-%s", i, d, s);
	}

	public String method4(long i, Double d, String s) {
		return "method4() called";
	}

	@ExtDirectMethod(group = "group2")
	public Boolean method5(String userName) {
		if ("ralph".equals(userName)) {
			return true;
		} else if ("joe".equals(userName)) {
			return false;
		}
		return null;
	}

	@ExtDirectMethod
	public int method6(int a, int b) {
		return a + b;
	}

	@ExtDirectMethod
	public String method7() {
		return null;
	}

	@ExtDirectMethod
	public FormInfo method8(double d) {
		FormInfo info = new FormInfo();
		info.setBack(d);
		info.setAdmin(false);
		info.setAge(32);
		info.setBirthday(new GregorianCalendar(1986, Calendar.JULY, 22).getTime());
		info.setName("John");
		info.setSalary(new BigDecimal("8720.20"));
		return info;
	}

	@ExtDirectMethod(group = "group3")
	public long method9(HttpServletResponse response, HttpServletRequest request, HttpSession session, Locale locale,
			Principal principal) {
		assertNotNull(response);
		assertNotNull(request);
		assertNotNull(session);
		assertEquals(Locale.ENGLISH, locale);

		return 42;
	}

	@ExtDirectMethod
	public String method10(boolean flag, char aCharacter, StatusEnum status, int aInt, long aLong, double aDouble,
			float aFloat, short aShort, byte aByte) {
		assertTrue(flag);
		assertEquals('c', aCharacter);
		assertEquals(StatusEnum.ACTIVE, status);
		assertEquals(14, aInt);
		assertEquals(21, aLong);
		assertEquals(3.14, aDouble);
		assertEquals(10.01, aFloat, 0.01);
		assertEquals(1, aShort);
		assertEquals(2, aByte);
		return String.format("method10() called-%b-%c-%s-%d-%d-%.2f-%.2f-%d-%d", flag, aCharacter, status, aInt, aLong,
				aDouble, aFloat, aShort, aByte);
	}

	@ExtDirectMethod
	public String method11() {
		throw new NullPointerException();
	}

	@ExtDirectMethod
	public String method12(Row aRow) {
		assertNotNull(aRow);
		assertEquals(104, aRow.getId());
		assertEquals("myRow", aRow.getName());
		assertEquals(true, aRow.isAdmin());
		assertEquals("100.45", aRow.getSalary().toPlainString());
		return aRow.toString();
	}

	@ExtDirectMethod
	public String method13(boolean flag, HttpServletResponse response, char aCharacter, HttpServletRequest request,
			StatusEnum status, HttpSession session, int aInt, long aLong, Locale locale, double aDouble, float aFloat,
			Principal principal, short aShort, byte aByte) {

		assertNotNull(response);
		assertNotNull(request);
		assertNotNull(session);
		assertEquals(Locale.ENGLISH, locale);

		assertTrue(flag);
		assertEquals('c', aCharacter);
		assertEquals(StatusEnum.ACTIVE, status);
		assertEquals(14, aInt);
		assertEquals(21, aLong);
		assertEquals(3.14, aDouble);
		assertEquals(10.01, aFloat, 0.01);
		assertEquals(1, aShort);
		assertEquals(2, aByte);
		return String.format("method13() called-%b-%c-%s-%d-%d-%.2f-%.2f-%d-%d", flag, aCharacter, status, aInt, aLong,
				aDouble, aFloat, aShort, aByte);

	}

	@ExtDirectMethod
	public Map<String, Object> method14(@DateTimeFormat(iso = ISO.DATE_TIME) Date endDate, String normalParameter,
			HttpServletRequest request, 
			@DateTimeFormat(iso=ISO.DATE) LocalDate aDate,
			@NumberFormat(style = NumberFormat.Style.PERCENT) BigDecimal percent) {

		Map<String, Object> result = new HashMap<String, Object>();
		result.put("endDate", endDate);
		result.put("jodaLocalDate", aDate);
		result.put("percent", percent);
		result.put("normalParameter", normalParameter);
		result.put("remoteAddr", request.getRemoteAddr());
		return result;
	}
}
