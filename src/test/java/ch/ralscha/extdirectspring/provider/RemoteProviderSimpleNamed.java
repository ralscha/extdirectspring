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
import ch.ralscha.extdirectspring.annotation.ExtDirectMethodType;

@Service
@SuppressWarnings("unused")
public class RemoteProviderSimpleNamed {

	private enum Workflow {
		WAITING, PENDING, STARTED, FINISHED
	}

	@NamedEdsMethod
	public String nonStrictMethod1(Map<String, Object> parameters) {
		return String.format("nonStrictMethod1() called-%d-%.3f-%s", parameters.get("i"),
				parameters.get("d"), parameters.get("s"));
	}

	@NamedEdsMethod
	public String nonStrictMethod2(HttpServletResponse response,
			HttpServletRequest request, HttpSession session,
			Map<String, Object> parameters, Locale locale) {

		assertThat(response).isNotNull();
		assertThat(request).isNotNull();
		assertThat(session).isNotNull();
		assertThat(locale).isEqualTo(Locale.ENGLISH);

		return String.format("nonStrictMethod2() called-%d-%.3f-%s", parameters.get("i"),
				parameters.get("d"), parameters.get("s"));
	}

	@NamedEdsMethod
	public String nonStrictMethod3(@CookieValue("aSimpleCookie") String cookie,
			Map<String, Object> parameters,
			@RequestHeader("aSimpleHeader") String header) {
		return String.format("nonStrictMethod3() called-%d-%s-%s", parameters.get("i"),
				cookie, header);
	}

	@ExtDirectMethod(value = ExtDirectMethodType.SIMPLE_NAMED, group = "named")
	public String method1() {
		return "method1() called";
	}

	@NamedEdsMethod
	public String method2(long i, Double d, String s) {
		return String.format("method2() called-%d-%.3f-%s", i, d, s);
	}

	@ExtDirectMethod(value = ExtDirectMethodType.SIMPLE_NAMED, group = "named",
			event = "test")
	public Boolean method3(String userName) {
		if ("ralph".equals(userName)) {
			return Boolean.TRUE;
		}
		else if ("joe".equals(userName)) {
			return Boolean.FALSE;
		}
		return null;
	}

	@ExtDirectMethod(value = ExtDirectMethodType.SIMPLE_NAMED, group = "named",
			entryClass = String.class)
	public int method4(int a, int b) {
		return a + b;
	}

	@ExtDirectMethod(value = ExtDirectMethodType.SIMPLE_NAMED, group = "named")
	public FormInfo method5(double d) {
		FormInfo info = new FormInfo();
		info.setBack(d);
		info.setAdmin(false);
		info.setAge(32);
		info.setBirthday(new GregorianCalendar(1986, Calendar.JULY, 22).getTime());
		info.setName("John");
		info.setSalary(new BigDecimal("8720.20"));
		return info;
	}

	@ExtDirectMethod(value = ExtDirectMethodType.SIMPLE_NAMED, group = "named")
	public long method6(HttpServletResponse response, HttpServletRequest request,
			HttpSession session, Locale locale, Principal principal) {
		assertThat(response).isNotNull();
		assertThat(request).isNotNull();
		assertThat(session).isNotNull();
		assertThat(locale).isEqualTo(Locale.ENGLISH);

		return 42;
	}

	@ExtDirectMethod(value = ExtDirectMethodType.SIMPLE_NAMED, group = "named")
	public String method7(boolean flag, char aCharacter, Workflow workflow, int aInt,
			long aLong, double aDouble, float aFloat, short aShort, byte aByte) {
		assertThat(flag).isTrue();
		assertThat(aCharacter).isEqualTo('c');
		assertThat(workflow).isEqualTo(Workflow.PENDING);
		assertThat(aInt).isEqualTo(14);
		assertThat(aLong).isEqualTo(21);
		assertThat(aDouble).isEqualTo(3.14);
		assertThat(aFloat).isEqualTo(10.01f, Offset.offset(0.01f));
		assertThat(aShort).isEqualTo((short) 1);
		assertThat(aByte).isEqualTo((byte) 2);
		return String.format("method7() called-%b-%c-%s-%d-%d-%.2f-%.2f-%d-%d", flag,
				aCharacter, workflow, aInt, aLong, aDouble, aFloat, aShort, aByte);
	}

	@ExtDirectMethod(value = ExtDirectMethodType.SIMPLE_NAMED, group = "named")
	public String method9(Row aRow) {
		assertThat(aRow).isNotNull();
		assertThat(aRow.getId()).isEqualTo(104);
		assertThat(aRow.getName()).isEqualTo("myRow");
		assertThat(aRow.isAdmin()).isEqualTo(true);
		assertThat(aRow.getSalary().toPlainString()).isEqualTo("100.45");
		return aRow.toString();
	}

	@ExtDirectMethod(value = ExtDirectMethodType.SIMPLE_NAMED, group = "named")
	public String method10(boolean flag, HttpServletResponse response, char aCharacter,
			final HttpServletRequest request, short aShort, byte aByte, Workflow workflow,
			HttpSession session, int aInt, long aLong, Locale locale, double aDouble,
			float aFloat, Principal principal) {

		assertThat(response).isNotNull();
		assertThat(request).isNotNull();
		assertThat(session).isNotNull();
		assertThat(locale).isEqualTo(Locale.ENGLISH);

		assertThat(flag).isTrue();
		assertThat(aCharacter).isEqualTo('c');
		assertThat(workflow).isEqualTo(Workflow.PENDING);
		assertThat(aInt).isEqualTo(14);
		assertThat(aLong).isEqualTo(21);
		assertThat(aDouble).isEqualTo(3.14);
		assertThat(aFloat).isEqualTo(10.01f, Offset.offset(0.01f));
		assertThat(aShort).isEqualTo((short) 1);
		assertThat(aByte).isEqualTo((byte) 2);
		return String.format("method10() called-%b-%c-%s-%d-%d-%.2f-%.2f-%d-%d", flag,
				aCharacter, workflow, aInt, aLong, aDouble, aFloat, aShort, aByte);
	}

	@ExtDirectMethod(value = ExtDirectMethodType.SIMPLE_NAMED, group = "named")
	public Map<String, Object> method11(@DateTimeFormat(iso = ISO.DATE_TIME) Date endDate,
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

	@ExtDirectMethod(value = ExtDirectMethodType.SIMPLE_NAMED, group = "named")
	public ResultObject methodRP1(@RequestParam(value = "lastName") String name,
			@RequestParam(value = "theAge") Integer age, Boolean active,
			HttpServletRequest request) {
		assertThat(request).isNotNull();
		return new ResultObject(name, age, active);
	}

	@ExtDirectMethod(value = ExtDirectMethodType.SIMPLE_NAMED, group = "named")
	public ResultObject methodRP2(HttpSession session,
			@RequestParam(value = "lastName", required = false,
					defaultValue = "myName") String name,
			@RequestParam(value = "theAge", defaultValue = "20") Integer age,
			@RequestParam(defaultValue = "true") Boolean active) {
		assertThat(session).isNotNull();
		return new ResultObject(name, age, active);
	}

	@ExtDirectMethod(value = ExtDirectMethodType.SIMPLE_NAMED, group = "named")
	public ResultObject methodRP3(HttpSession session,
			@RequestParam(value = "lastName", required = false) String name,
			@RequestParam(value = "theAge", required = false) Integer age,
			@RequestParam(required = false) Boolean active) {
		assertThat(session).isNotNull();
		return new ResultObject(name, age, active);
	}

	@ExtDirectMethod(value = ExtDirectMethodType.SIMPLE_NAMED, group = "named")
	public String methodCollection1(String name, List<TestObject> collections) {
		return String.format("1->%s;%s", name, collections);
	}

	@ExtDirectMethod(value = ExtDirectMethodType.SIMPLE_NAMED, group = "named")
	public String methodCollection2(String name,
			@RequestParam(required = false) List<TestObject> collections) {
		return String.format("2->%s;%s", name, collections);
	}

	@ExtDirectMethod(value = ExtDirectMethodType.SIMPLE_NAMED, group = "named")
	public String methodCollection3(String name,
			@SuppressWarnings("rawtypes") List collections) {
		return String.format("3->%s;%s", name, collections);
	}

	@ExtDirectMethod(value = ExtDirectMethodType.SIMPLE_NAMED, group = "named")
	public String methodArray1(String name, TestObject[] array) {
		StringBuilder sb = new StringBuilder();
		if (array != null) {
			for (TestObject element : array) {
				sb.append(element.toString());
				sb.append("-");
			}
		}
		return String.format("3->%s;%s", name, sb.toString());
	}

	@ExtDirectMethod(value = ExtDirectMethodType.SIMPLE_NAMED, group = "named")
	public String methodArray2(String name,
			@RequestParam(required = false) TestObject[] array) {
		StringBuilder sb = new StringBuilder();
		if (array != null) {
			for (TestObject element : array) {
				sb.append(element.toString());
			}
		}
		return String.format("4->%s;%s", name, sb.toString());
	}

	@ExtDirectMethod(value = ExtDirectMethodType.SIMPLE_NAMED, group = "named")
	public String methodArray3(String name, TestObject... array) {
		StringBuilder sb = new StringBuilder();
		if (array != null) {
			for (TestObject element : array) {
				sb.append(element.toString());
				sb.append("-");
			}
		}
		return String.format("5->%s;%s", name, sb.toString());
	}

	@ExtDirectMethod(value = ExtDirectMethodType.SIMPLE_NAMED, group = "named")
	public String methodArray4(String name,
			@RequestParam(required = false) TestObject... array) {
		StringBuilder sb = new StringBuilder();
		if (array != null) {
			for (TestObject element : array) {
				sb.append(element.toString());
			}
		}
		return String.format("6->%s;%s", name, sb.toString());
	}

	@ExtDirectMethod(value = ExtDirectMethodType.SIMPLE_NAMED, group = "named")
	public String withCookie(@CookieValue(value = "aSimpleCookie", required = false,
			defaultValue = "defaultCookie") String cookie, Long i) {
		return i + ":" + cookie;
	}

	@ExtDirectMethod(value = ExtDirectMethodType.SIMPLE_NAMED, group = "named")
	public String withRequiredCookie(@CookieValue(value = "aSimpleCookie") String cookie,
			Long i) {
		return i + ":" + cookie;
	}

	@ExtDirectMethod(value = ExtDirectMethodType.SIMPLE_NAMED, group = "named")
	public String withRequestHeader(@RequestHeader(value = "aSimpleHeader",
			required = false, defaultValue = "defaultHeader") String header,
			BigDecimal bd) {
		return bd + ":" + header;
	}

	@ExtDirectMethod(value = ExtDirectMethodType.SIMPLE_NAMED, group = "named",
			batched = true)
	public String withRequiredRequestHeader(
			@RequestHeader(value = "aSimpleHeader") String header, BigDecimal bd) {
		return bd + ":" + header;
	}

	@ExtDirectMethod(value = ExtDirectMethodType.SIMPLE_NAMED, group = "named",
			batched = false)
	public String notBatched() {
		return "ralph";
	}

	public static class TestObject {
		private int id;

		private String name;

		private Boolean active;

		private BigDecimal amount;

		public TestObject() {
			// default constructor
		}

		public TestObject(int id, String name, Boolean active, BigDecimal amount) {
			super();
			this.id = id;
			this.name = name;
			this.active = active;
			this.amount = amount;
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

		public Boolean getActive() {
			return this.active;
		}

		public void setActive(Boolean active) {
			this.active = active;
		}

		public BigDecimal getAmount() {
			return this.amount;
		}

		public void setAmount(BigDecimal amount) {
			this.amount = amount;
		}

		@Override
		public String toString() {
			return "TestObject [id=" + this.id + ", name=" + this.name + ", active="
					+ this.active + ", amount=" + this.amount + "]";
		}

	}

	public static class ResultObject {
		private String name;

		private Integer age;

		private Boolean active;

		public ResultObject() {
			// default constructor
		}

		public ResultObject(String name, Integer age, Boolean active) {
			this.name = name;
			this.age = age;
			this.active = active;
		}

		public String getName() {
			return this.name;
		}

		public Integer getAge() {
			return this.age;
		}

		public Boolean getActive() {
			return this.active;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + (this.active == null ? 0 : this.active.hashCode());
			result = prime * result + (this.age == null ? 0 : this.age.hashCode());
			result = prime * result + (this.name == null ? 0 : this.name.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj) {
				return true;
			}
			if (obj == null) {
				return false;
			}
			if (getClass() != obj.getClass()) {
				return false;
			}
			ResultObject other = (ResultObject) obj;
			if (this.active == null) {
				if (other.active != null) {
					return false;
				}
			}
			else if (!this.active.equals(other.active)) {
				return false;
			}
			if (this.age == null) {
				if (other.age != null) {
					return false;
				}
			}
			else if (!this.age.equals(other.age)) {
				return false;
			}
			if (this.name == null) {
				if (other.name != null) {
					return false;
				}
			}
			else if (!this.name.equals(other.name)) {
				return false;
			}
			return true;
		}

		@Override
		public String toString() {
			return "ResultObject [name=" + this.name + ", age=" + this.age + ", active="
					+ this.active + "]";
		}

	}

}
