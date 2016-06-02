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
package ch.ralscha.extdirectspring.provider;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.security.Principal;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.assertj.core.data.Offset;
import org.joda.time.LocalDate;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;
import org.springframework.format.annotation.NumberFormat;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

import ch.ralscha.extdirectspring.annotation.ExtDirectMethod;
import ch.ralscha.extdirectspring.bean.ExtDirectRequest;

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

	@ExtDirectMethod(group = "")
	public String method2() {
		return "method2() called";
	}

	@ExtDirectMethod(group = "group2,groupX")
	public String method3(long i, Double d, String s, ExtDirectRequest directRequest) {
		return String.format("method3() called-%d-%.1f-%s-%s", i, d, s,
				directRequest != null);
	}

	@ExtDirectMethod(group = "group2,groupX")
	public String method3WithError(long i, Double d, @RequestParam("s") String s) {
		return String.format("method3() called-%d-%.1f-%s", i, d, s);
	}

	public String method4(long i, Double d, String s) {
		return "method4() called";
	}

	@ExtDirectMethod(event = "test", group = "    ")
	public String method4b(long i, Double d, String s) {
		return "method4b() called";
	}

	@ExtDirectMethod(group = "group2,group3", entryClass = String.class)
	public Boolean method5(String userName) {
		if ("ralph".equals(userName)) {
			return Boolean.TRUE;
		}
		else if ("joe".equals(userName)) {
			return Boolean.FALSE;
		}
		return null;
	}

	@ExtDirectMethod
	public int method6(int a, int b) {
		return a + b;
	}

	@SimpleEdsMethod
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

	@ExtDirectMethod(group = "groupX,group3")
	public long method9(HttpServletResponse response, HttpServletRequest request,
			HttpSession session, Locale locale, Principal principal) {
		assertThat(response).isNotNull();
		assertThat(request).isNotNull();
		assertThat(session).isNotNull();
		assertThat(locale).isEqualTo(Locale.ENGLISH);

		return 42;
	}

	@ExtDirectMethod
	public String method10(boolean flag, char aCharacter, StatusEnum status, int aInt,
			long aLong, double aDouble, float aFloat, short aShort, byte aByte) {
		assertThat(flag).isTrue();
		assertThat(aCharacter).isEqualTo('c');
		assertThat(status).isEqualTo(StatusEnum.ACTIVE);
		assertThat(aInt).isEqualTo(14);
		assertThat(aLong).isEqualTo(21);
		assertThat(aDouble).isEqualTo(3.14);
		assertThat(aFloat).isEqualTo(10.01f, Offset.offset(0.01f));
		assertThat(aShort).isEqualTo((short) 1);
		assertThat(aByte).isEqualTo((byte) 2);
		return String.format("method10() called-%b-%c-%s-%d-%d-%.2f-%.2f-%d-%d", flag,
				aCharacter, status, aInt, aLong, aDouble, aFloat, aShort, aByte);
	}

	@ExtDirectMethod
	public String method11() {
		throw new NullPointerException();
	}

	@ExtDirectMethod
	public String method11b() {
		throw new UnsupportedOperationException("not supported");
	}

	@ExtDirectMethod
	public String method12(Row aRow) {
		assertThat(aRow).isNotNull();
		assertThat(aRow.getId()).isEqualTo(104);
		assertThat(aRow.getName()).isEqualTo("myRow");
		assertThat(aRow.isAdmin()).isEqualTo(true);
		assertThat(aRow.getSalary().toPlainString()).isEqualTo("100.45");
		return aRow.toString();
	}

	@ExtDirectMethod
	public String method13(boolean flag, HttpServletResponse response, char aCharacter,
			final HttpServletRequest request, StatusEnum status, HttpSession session,
			int aInt, long aLong, Locale locale, double aDouble, float aFloat,
			Principal principal, short aShort, byte aByte) {

		assertThat(response).isNotNull();
		assertThat(request).isNotNull();
		assertThat(session).isNotNull();
		assertThat(locale).isEqualTo(Locale.ENGLISH);

		assertThat(flag).isTrue();
		assertThat(aCharacter).isEqualTo('c');
		assertThat(status).isEqualTo(StatusEnum.ACTIVE);
		assertThat(aInt).isEqualTo(14);
		assertThat(aLong).isEqualTo(21);
		assertThat(aDouble).isEqualTo(3.14);
		assertThat(aFloat).isEqualTo(10.01f, Offset.offset(0.01f));
		assertThat(aShort).isEqualTo((short) 1);
		assertThat(aByte).isEqualTo((byte) 2);
		return String.format("method13() called-%b-%c-%s-%d-%d-%.2f-%.2f-%d-%d", flag,
				aCharacter, status, aInt, aLong, aDouble, aFloat, aShort, aByte);

	}

	@ExtDirectMethod
	public Map<String, Object> method14(@DateTimeFormat(iso = ISO.DATE_TIME) Date endDate,
			final String normalParameter, HttpServletRequest request,
			@DateTimeFormat(iso = ISO.DATE) LocalDate aDate,
			@NumberFormat(style = NumberFormat.Style.PERCENT) BigDecimal percent) {

		Map<String, Object> result = new HashMap<String, Object>();
		result.put("endDate", endDate);
		result.put("jodaLocalDate", aDate);
		result.put("percent", percent);
		result.put("normalParameter", normalParameter);
		result.put("remoteAddr", request.getRemoteAddr());
		return result;
	}

	@ExtDirectMethod
	public String method15(Integer id, String dummy, @RequestHeader String header) {
		return id + ";" + dummy + ";" + header;
	}

	@ExtDirectMethod
	public String method16(Integer id, @RequestHeader("anotherName") String header) {
		return id + ";" + header;
	}

	@ExtDirectMethod
	public String method17(@RequestHeader(value = "anotherName",
			defaultValue = "default") String header) {
		return header;
	}

	@ExtDirectMethod(synchronizeOnSession = true)
	public String method18(
			@RequestHeader(defaultValue = "default", required = false) String header) {
		return header;
	}

	@ExtDirectMethod(synchronizeOnSession = true)
	public String method19(
			@RequestHeader(defaultValue = "default1", required = false) String header1,
			final Integer id,
			@RequestHeader(defaultValue = "default2", required = false) String header2,
			@RequestHeader(value = "last") String header3) {
		return id + ";" + header1 + ";" + header2 + ";" + header3;
	}

	@ExtDirectMethod(synchronizeOnSession = true)
	public String method20(@RequestHeader Integer intHeader,
			@RequestHeader Boolean booleanHeader) {
		return intHeader + ";" + booleanHeader;
	}

	@ExtDirectMethod
	public String method21(String name, List<String> strings, int id) {
		StringBuilder sb = new StringBuilder();
		if (strings != null) {
			for (String str : strings) {
				sb.append(str);
				sb.append("-");
			}
		}
		return name + ";" + sb.toString() + ";" + id;
	}

	@ExtDirectMethod
	public String method22(String name, Set<Integer> ids, int id) {
		StringBuilder sb = new StringBuilder();
		if (ids != null) {
			SortedSet<Integer> sorted = new TreeSet<Integer>(ids);
			for (int i : sorted) {
				sb.append(i);
				sb.append("+");
			}
		}
		return name + ";" + sb.toString() + ";" + id;
	}

	@ExtDirectMethod
	public String method23(String name, String[] strings, int id) {
		StringBuilder sb = new StringBuilder();
		if (strings != null) {
			for (String str : strings) {
				sb.append(str);
				sb.append("-");
			}
		}
		return name + ";" + sb.toString() + ";" + id;
	}

	@ExtDirectMethod
	public String method24(String name, int id, int... ids) {
		StringBuilder sb = new StringBuilder();
		if (ids != null) {
			for (int i : ids) {
				sb.append(i);
				sb.append("+");
			}
		}
		return name + ";" + sb.toString() + ";" + id;
	}

	@ExtDirectMethod
	public String method25(String name, List<BusinessObject> bos, int id) {
		StringBuilder sb = new StringBuilder();
		if (bos != null) {
			for (BusinessObject bo : bos) {
				sb.append(bo);
				sb.append("-");
			}
		}
		return name + ";" + sb.toString() + ";" + id;
	}

	@ExtDirectMethod
	public String method26(String name, BusinessObject[] bos, int id) {
		StringBuilder sb = new StringBuilder();
		if (bos != null) {
			for (BusinessObject bo : bos) {
				sb.append(bo);
				sb.append("-");
			}
		}
		return name + ";" + sb.toString() + ";" + id;
	}

	@ExtDirectMethod
	public String method27(@CookieValue Integer intCookie,
			@CookieValue Boolean booleanCookie) {
		return intCookie + ";" + booleanCookie;
	}

	@ExtDirectMethod
	public String method28(@CookieValue(required = false,
			defaultValue = "theDefaultValue") String stringCookie) {
		return stringCookie;
	}

	@ExtDirectMethod
	public String method29(@CookieValue(value = "nameOfTheCookie") String aStr) {
		return aStr;
	}

	@ExtDirectMethod(batched = true)
	public String method30(@CookieValue(required = false) String stringCookie) {
		return stringCookie;
	}

	@ExtDirectMethod(batched = false)
	public String method31(String input) {
		return input;
	}

	@ExtDirectMethod
	public String methodWithOptional(Optional<String> param1) {
		return param1.orElse("default value");
	}

	public static final class BusinessObject {
		private int id;

		private String name;

		private BigDecimal bd;

		public BusinessObject() {
			// default constructor for jackson
		}

		public BusinessObject(int id, String name, BigDecimal bd) {
			this.id = id;
			this.name = name;
			this.bd = bd;
		}

		public int getId() {
			return this.id;
		}

		public void setId(int id) {
			this.id = id;
		}

		public String getName() {
			return this.name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public BigDecimal getBd() {
			return this.bd;
		}

		public void setBd(BigDecimal bd) {
			this.bd = bd;
		}

		@Override
		public String toString() {
			return "BusinessObject [id=" + this.id + ", name=" + this.name + ", bd="
					+ this.bd + "]";
		}

	}
}
