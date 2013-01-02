/**
 * Copyright 2010-2013 Ralph Schaer <ralphschaer@gmail.com>
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
package ch.ralscha.extdirectspring.generator;

import static org.fest.assertions.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.GregorianCalendar;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.junit.Test;

public class ModelTypeTest {

	@Test
	public void testAutoGetJsName() {
		assertThat(ModelType.AUTO.getJsName()).isEqualTo("auto");

		assertThat(ModelType.AUTO.supports(Object.class)).isFalse();
	}

	@Test
	public void testIntegerGetJsName() {
		assertThat(ModelType.INTEGER.getJsName()).isEqualTo("int");

		assertThat(ModelType.INTEGER.supports(Object.class)).isFalse();
		assertThat(ModelType.INTEGER.supports(Float.valueOf("1.1").getClass())).isFalse();
		assertThat(ModelType.INTEGER.supports(Byte.valueOf("1").getClass())).isTrue();
		assertThat(ModelType.INTEGER.supports(Short.valueOf("2").getClass())).isTrue();
		assertThat(ModelType.INTEGER.supports(Integer.valueOf("3").getClass())).isTrue();
		assertThat(ModelType.INTEGER.supports(Long.valueOf("4").getClass())).isTrue();
		BigInteger bi = new BigInteger("5");
		assertThat(ModelType.INTEGER.supports(bi.getClass())).isTrue();
		assertThat(ModelType.INTEGER.supports(Byte.TYPE)).isTrue();
		assertThat(ModelType.INTEGER.supports(Short.TYPE)).isTrue();
		assertThat(ModelType.INTEGER.supports(Integer.TYPE)).isTrue();
		assertThat(ModelType.INTEGER.supports(Long.TYPE)).isTrue();
	}

	@Test
	public void testFloatGetJsName() {
		assertThat(ModelType.FLOAT.getJsName()).isEqualTo("float");

		assertThat(ModelType.FLOAT.supports(Object.class)).isFalse();
		assertThat(ModelType.FLOAT.supports(Integer.valueOf("3").getClass())).isFalse();
		assertThat(ModelType.FLOAT.supports(Float.valueOf("1.1").getClass())).isTrue();
		assertThat(ModelType.FLOAT.supports(Double.valueOf("2.2").getClass())).isTrue();
		BigDecimal bd = new BigDecimal("3.3");
		assertThat(ModelType.FLOAT.supports(bd.getClass())).isTrue();
		assertThat(ModelType.FLOAT.supports(Float.TYPE)).isTrue();
		assertThat(ModelType.FLOAT.supports(Double.TYPE)).isTrue();
	}

	@Test
	public void testStringGetJsName() {
		assertThat(ModelType.STRING.getJsName()).isEqualTo("string");

		assertThat(ModelType.STRING.supports(Object.class)).isFalse();
		assertThat(ModelType.STRING.supports(Integer.valueOf("3").getClass())).isFalse();
		assertThat(ModelType.STRING.supports("string".getClass())).isTrue();

	}

	@Test
	public void testDateGetJsName() {
		assertThat(ModelType.DATE.getJsName()).isEqualTo("date");

		assertThat(ModelType.DATE.supports(Object.class)).isFalse();
		assertThat(ModelType.DATE.supports(Integer.valueOf("3").getClass())).isFalse();

		Date d = new Date(System.currentTimeMillis());
		assertThat(ModelType.DATE.supports(d.getClass())).isTrue();

		Timestamp t = new Timestamp(System.currentTimeMillis());
		assertThat(ModelType.DATE.supports(t.getClass())).isTrue();

		java.util.Date ud = new java.util.Date();
		assertThat(ModelType.DATE.supports(ud.getClass())).isTrue();

		Calendar cal = Calendar.getInstance();
		assertThat(ModelType.DATE.supports(cal.getClass())).isTrue();

		cal = new GregorianCalendar();
		assertThat(ModelType.DATE.supports(cal.getClass())).isTrue();

		DateTime dt = DateTime.now();
		assertThat(ModelType.DATE.supports(dt.getClass())).isTrue();

		LocalDate ld = LocalDate.now();
		assertThat(ModelType.DATE.supports(ld.getClass())).isTrue();
	}

	@Test
	public void testBooleanGetJsName() {
		assertThat(ModelType.BOOLEAN.getJsName()).isEqualTo("boolean");

		assertThat(ModelType.BOOLEAN.supports(Object.class)).isFalse();
		assertThat(ModelType.BOOLEAN.supports(Integer.valueOf("3").getClass())).isFalse();
		assertThat(ModelType.BOOLEAN.supports(Boolean.valueOf("true").getClass())).isTrue();
		assertThat(ModelType.BOOLEAN.supports(Boolean.TYPE)).isTrue();
	}
}
