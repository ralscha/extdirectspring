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
package ch.ralscha.extdirectspring.provider;

import static org.fest.assertions.Assertions.assertThat;

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

import org.fest.assertions.Delta;
import org.joda.time.LocalDate;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;
import org.springframework.format.annotation.NumberFormat;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;

import ch.ralscha.extdirectspring.annotation.ExtDirectMethod;
import ch.ralscha.extdirectspring.annotation.ExtDirectMethodType;

@Service
@SuppressWarnings("unused")
public class RemoteProviderSimpleNamed {

	private enum Workflow {
		WAITING, PENDING, STARTED, FINISHED
	}

	@ExtDirectMethod(value = ExtDirectMethodType.SIMPLE_NAMED, group = "named")
	public String method1() {
		return "method1() called";
	}

	@ExtDirectMethod(value = ExtDirectMethodType.SIMPLE_NAMED, group = "named")
	public String method2(final long i, final Double d, final String s) {
		return String.format("method2() called-%d-%.3f-%s", i, d, s);
	}

	@ExtDirectMethod(value = ExtDirectMethodType.SIMPLE_NAMED, group = "named", event = "test")
	public Boolean method3(final String userName) {
		if ("ralph".equals(userName)) {
			return true;
		} else if ("joe".equals(userName)) {
			return false;
		}
		return null;
	}

	@ExtDirectMethod(value = ExtDirectMethodType.SIMPLE_NAMED, group = "named", entryClass = String.class)
	public int method4(final int a, final int b) {
		return a + b;
	}

	@ExtDirectMethod(value = ExtDirectMethodType.SIMPLE_NAMED, group = "named")
	public FormInfo method5(final double d) {
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
	public long method6(final HttpServletResponse response, final HttpServletRequest request,
			final HttpSession session, final Locale locale, final Principal principal) {
		assertThat(response).isNotNull();
		assertThat(request).isNotNull();
		assertThat(session).isNotNull();
		assertThat(locale).isEqualTo(Locale.ENGLISH);

		return 42;
	}

	@ExtDirectMethod(value = ExtDirectMethodType.SIMPLE_NAMED, group = "named")
	public String method7(final boolean flag, final char aCharacter, final Workflow workflow, final int aInt,
			final long aLong, final double aDouble, final float aFloat, final short aShort, final byte aByte) {
		assertThat(flag).isTrue();
		assertThat(aCharacter).isEqualTo('c');
		assertThat(workflow).isEqualTo(Workflow.PENDING);
		assertThat(aInt).isEqualTo(14);
		assertThat(aLong).isEqualTo(21);
		assertThat(aDouble).isEqualTo(3.14);
		assertThat(aFloat).isEqualTo(10.01f, Delta.delta(0.01f));
		assertThat(aShort).isEqualTo((short) 1);
		assertThat(aByte).isEqualTo((byte) 2);
		return String.format("method7() called-%b-%c-%s-%d-%d-%.2f-%.2f-%d-%d", flag, aCharacter, workflow, aInt,
				aLong, aDouble, aFloat, aShort, aByte);
	}

	@ExtDirectMethod(value = ExtDirectMethodType.SIMPLE_NAMED, group = "named")
	public String method9(final Row aRow) {
		assertThat(aRow).isNotNull();
		assertThat(aRow.getId()).isEqualTo(104);
		assertThat(aRow.getName()).isEqualTo("myRow");
		assertThat(aRow.isAdmin()).isEqualTo(true);
		assertThat(aRow.getSalary().toPlainString()).isEqualTo("100.45");
		return aRow.toString();
	}

	@ExtDirectMethod(value = ExtDirectMethodType.SIMPLE_NAMED, group = "named")
	public String method10(final boolean flag, final HttpServletResponse response, final char aCharacter,
			final HttpServletRequest request, final short aShort, final byte aByte, final Workflow workflow,
			final HttpSession session, final int aInt, final long aLong, final Locale locale, final double aDouble,
			final float aFloat, final Principal principal) {

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
		assertThat(aFloat).isEqualTo(10.01f, Delta.delta(0.01f));
		assertThat(aShort).isEqualTo((short) 1);
		assertThat(aByte).isEqualTo((byte) 2);
		return String.format("method10() called-%b-%c-%s-%d-%d-%.2f-%.2f-%d-%d", flag, aCharacter, workflow, aInt,
				aLong, aDouble, aFloat, aShort, aByte);
	}

	@ExtDirectMethod(value = ExtDirectMethodType.SIMPLE_NAMED, group = "named")
	public Map<String, Object> method11(@DateTimeFormat(iso = ISO.DATE_TIME) final Date endDate,
			final String normalParameter, final HttpServletRequest request,
			@DateTimeFormat(iso = ISO.DATE) final LocalDate aDate,
			@NumberFormat(style = NumberFormat.Style.PERCENT) final BigDecimal percent) {

		Map<String, Object> result = new HashMap<String, Object>();
		result.put("endDate", endDate);
		result.put("jodaLocalDate", aDate);
		result.put("percent", percent);
		result.put("normalParameter", normalParameter);
		result.put("remoteAddr", request.getRemoteAddr());
		return result;
	}

	@ExtDirectMethod(value = ExtDirectMethodType.SIMPLE_NAMED, group = "named")
	public ResultObject methodRP1(@RequestParam(value = "lastName") final String name,
			@RequestParam(value = "theAge") final Integer age, final Boolean active, final HttpServletRequest request) {
		assertThat(request).isNotNull();
		return new ResultObject(name, age, active);
	}

	@ExtDirectMethod(value = ExtDirectMethodType.SIMPLE_NAMED, group = "named")
	public ResultObject methodRP2(final HttpSession session,
			@RequestParam(value = "lastName", required = false, defaultValue = "myName") final String name,
			@RequestParam(value = "theAge", defaultValue = "20") final Integer age,
			@RequestParam(defaultValue = "true") final Boolean active) {
		assertThat(session).isNotNull();
		return new ResultObject(name, age, active);
	}

	@ExtDirectMethod(value = ExtDirectMethodType.SIMPLE_NAMED, group = "named")
	public ResultObject methodRP3(final HttpSession session,
			@RequestParam(value = "lastName", required = false) final String name,
			@RequestParam(value = "theAge", required = false) final Integer age,
			@RequestParam(required = false) final Boolean active) {
		assertThat(session).isNotNull();
		return new ResultObject(name, age, active);
	}

	public static class ResultObject {
		private String name;

		private Integer age;

		private Boolean active;

		public ResultObject() {
			// default constructor
		}

		public ResultObject(final String name, final Integer age, final Boolean active) {
			this.name = name;
			this.age = age;
			this.active = active;
		}

		public String getName() {
			return name;
		}

		public Integer getAge() {
			return age;
		}

		public Boolean getActive() {
			return active;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((active == null) ? 0 : active.hashCode());
			result = prime * result + ((age == null) ? 0 : age.hashCode());
			result = prime * result + ((name == null) ? 0 : name.hashCode());
			return result;
		}

		@Override
		public boolean equals(final Object obj) {
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
			if (active == null) {
				if (other.active != null) {
					return false;
				}
			} else if (!active.equals(other.active)) {
				return false;
			}
			if (age == null) {
				if (other.age != null) {
					return false;
				}
			} else if (!age.equals(other.age)) {
				return false;
			}
			if (name == null) {
				if (other.name != null) {
					return false;
				}
			} else if (!name.equals(other.name)) {
				return false;
			}
			return true;
		}

		@Override
		public String toString() {
			return "ResultObject [name=" + name + ", age=" + age + ", active=" + active + "]";
		}

	}

}
