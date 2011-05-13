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
import java.util.GregorianCalendar;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

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
	public String method2(long i, Double d, String s) {
		return String.format("method2() called-%d-%.3f-%s", i, d, s);
	}

	@ExtDirectMethod(value = ExtDirectMethodType.SIMPLE_NAMED, group = "named")
	public Boolean method3(String userName) {
		if ("ralph".equals(userName)) {
			return true;
		} else if ("joe".equals(userName)) {
			return false;
		}
		return null;
	}

	@ExtDirectMethod(value = ExtDirectMethodType.SIMPLE_NAMED, group = "named")
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
	public long method6(HttpServletResponse response, HttpServletRequest request, HttpSession session, Locale locale,
			Principal principal) {
		assertNotNull(response);
		assertNotNull(request);
		assertNotNull(session);
		assertEquals(Locale.ENGLISH, locale);

		return 42;
	}

	@ExtDirectMethod(value = ExtDirectMethodType.SIMPLE_NAMED, group = "named")
	public String method7(boolean flag, char aCharacter, Workflow workflow, int aInt, long aLong, double aDouble,
			float aFloat, short aShort, byte aByte) {
		assertTrue(flag);
		assertEquals('c', aCharacter);
		assertEquals(Workflow.PENDING, workflow);
		assertEquals(14, aInt);
		assertEquals(21, aLong);
		assertEquals(3.14, aDouble);
		assertEquals(10.01, aFloat, 0.01);
		assertEquals(1, aShort);
		assertEquals(2, aByte);
		return String.format("method7() called-%b-%c-%s-%d-%d-%.2f-%.2f-%d-%d", flag, aCharacter, workflow, aInt, aLong,
				aDouble, aFloat, aShort, aByte);
	}

	@ExtDirectMethod(value = ExtDirectMethodType.SIMPLE_NAMED, group = "named")
	public String method9(Row aRow) {
		assertNotNull(aRow);
		assertEquals(104, aRow.getId());
		assertEquals("myRow", aRow.getName());
		assertEquals(true, aRow.isAdmin());
		assertEquals("100.45", aRow.getSalary().toPlainString());
		return aRow.toString();
	}
	
	@ExtDirectMethod(value = ExtDirectMethodType.SIMPLE_NAMED, group = "named")	
	public String method10(boolean flag, HttpServletResponse response, char aCharacter, HttpServletRequest request,
			short aShort, byte aByte,
			Workflow workflow, HttpSession session, int aInt, long aLong,  Locale locale,
			double aDouble, float aFloat, Principal principal) {

		assertNotNull(response);
		assertNotNull(request);
		assertNotNull(session);
		assertEquals(Locale.ENGLISH, locale);
		
		assertTrue(flag);
		assertEquals('c', aCharacter);
		assertEquals(Workflow.PENDING, workflow);
		assertEquals(14, aInt);
		assertEquals(21, aLong);
		assertEquals(3.14, aDouble);
		assertEquals(10.01, aFloat, 0.01);
		assertEquals(1, aShort);
		assertEquals(2, aByte);
		return String.format("method10() called-%b-%c-%s-%d-%d-%.2f-%.2f-%d-%d", flag, aCharacter, workflow, aInt, aLong, aDouble, aFloat, aShort, aByte);
	}
	
	@ExtDirectMethod(value = ExtDirectMethodType.SIMPLE_NAMED, group = "named")	
	public ResultObject methodRP1(@RequestParam(value="lastName") String name, 
			                @RequestParam(value="theAge") Integer age, 
			                Boolean active, 
			                HttpServletRequest request) {	
		assertNotNull(request);		
		return new ResultObject(name, age, active);		
	}
	
	@ExtDirectMethod(value = ExtDirectMethodType.SIMPLE_NAMED, group = "named")	
	public ResultObject methodRP2(HttpSession session, 
			                @RequestParam(value="lastName", required=false, defaultValue="myName") String name, 
			                @RequestParam(value="theAge", defaultValue="20") Integer age, 
			                @RequestParam(defaultValue="true") Boolean active) {	
		assertNotNull(session);		
		return new ResultObject(name, age, active);		
	}
	
	@ExtDirectMethod(value = ExtDirectMethodType.SIMPLE_NAMED, group = "named")	
	public ResultObject methodRP3(HttpSession session, 
			                @RequestParam(value="lastName", required=false) String name, 
			                @RequestParam(value="theAge", required=false) Integer age, 
			                @RequestParam(required=false) Boolean active) {	
		assertNotNull(session);		
		return new ResultObject(name, age, active);		
	}

	
	public static class ResultObject {
		private String name;
		private Integer age;
		private Boolean active;
		public ResultObject(String name, Integer age, Boolean active) {
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
