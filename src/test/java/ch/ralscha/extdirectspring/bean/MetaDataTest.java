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

import static org.hamcrest.Matchers.hasEntry;
import static org.hamcrest.Matchers.hasKey;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.junit.Test;

public class MetaDataTest {

	@Test
	public void testSimpleMetaData() {
		MetaData metaData = new MetaData();
		Map<String, Object> meta = metaData.getMetaData();
		assertEquals(3, meta.size());

		assertThat(meta, hasEntry("root", (Object) "records"));
		assertThat(meta, hasEntry("totalProperty", (Object) "total"));
		assertThat(meta, hasEntry("successProperty", (Object) "success"));

	}

	@Test
	public void testComplexMetaData() {
		MetaData metaData = new MetaData();
		metaData.setIdProperty("id");
		metaData.setPagingParameter(0, 50);
		metaData.setSortInfo("name", SortDirection.ASCENDING);

		Field nameField = new Field("name");
		Field cityField = new Field("city");
		Field countryField = new Field("country");

		metaData.addField(nameField);
		metaData.addFields(Arrays.asList(cityField, countryField));

		metaData.addCustomProperty("customProperty1", 10);
		metaData.addCustomProperty("customProperty2", "aValue");

		Map<String, Object> meta = metaData.getMetaData();
		assertEquals(10, meta.size());

		assertThat(meta, hasEntry("root", (Object) "records"));
		assertThat(meta, hasEntry("totalProperty", (Object) "total"));
		assertThat(meta, hasEntry("successProperty", (Object) "success"));
		assertThat(meta, hasEntry("idProperty", (Object) "id"));
		assertThat(meta, hasEntry("start", (Object) 0));
		assertThat(meta, hasEntry("limit", (Object) 50));
		assertThat(meta, hasEntry("customProperty1", (Object) 10));
		assertThat(meta, hasEntry("customProperty2", (Object) "aValue"));
		assertThat(meta, hasKey("sortInfo"));
		assertThat(meta, hasKey("fields"));

		@SuppressWarnings("unchecked")
		Map<String, String> sortInfo = (Map<String, String>) meta.get("sortInfo");
		assertEquals(2, sortInfo.size());
		assertThat(sortInfo, hasEntry("field", "name"));
		assertThat(sortInfo, hasEntry("direction", "ASC"));

		@SuppressWarnings("unchecked")
		List<Map<String, Object>> fields = (List<Map<String, Object>>) meta.get("fields");
		assertEquals(3, fields.size());

		Map<String, Object> field1 = fields.get(0);
		assertThat(field1, hasEntry("name", (Object) "name"));

		Map<String, Object> field2 = fields.get(1);
		assertThat(field2, hasEntry("name", (Object) "city"));

		Map<String, Object> field3 = fields.get(2);
		assertThat(field3, hasEntry("name", (Object) "country"));

	}
}
