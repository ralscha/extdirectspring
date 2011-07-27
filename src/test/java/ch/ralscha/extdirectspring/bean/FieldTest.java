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
package ch.ralscha.extdirectspring.bean;

import static org.fest.assertions.Assertions.assertThat;
import static org.fest.assertions.MapAssert.entry;
import static org.junit.Assert.assertEquals;

import java.util.Map;

import org.junit.Test;

public class FieldTest {

	@Test
	public void testSimpleField() {
		Field aField = new Field("fieldName");
		Map<String, Object> fieldData = aField.getFieldData();
		assertEquals(1, fieldData.size());
		assertThat(fieldData).includes(entry("name", "fieldName"));
	}

	@Test
	public void testComplexField() {
		Field aField = new Field("fieldName");
		aField.setAllowBlank(true);
		aField.setDateFormat("Y-m-d");
		aField.setType(DataType.DATE);
		aField.addCustomProperty("customProperty1", 10);
		aField.addCustomProperty("customProperty2", "aValue");

		Map<String, Object> fieldData = aField.getFieldData();
		assertEquals(6, fieldData.size());
		assertThat(fieldData).includes(entry("name", "fieldName"));
		assertThat(fieldData).includes(entry("allowBlank", true));
		assertThat(fieldData).includes(entry("dateFormat", "Y-m-d"));
		assertThat(fieldData).includes(entry("type", "date"));
		assertThat(fieldData).includes(entry("customProperty1", 10));
		assertThat(fieldData).includes(entry("customProperty2", "aValue"));

	}
}
