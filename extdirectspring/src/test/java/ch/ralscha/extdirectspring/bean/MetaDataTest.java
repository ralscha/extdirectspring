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

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.junit.Test;

public class MetaDataTest {

	@Test
	public void testSimpleMetaData() {
		MetaData metaData = new MetaData();
		Map<String, Object> meta = metaData.getMetaData();
		assertThat(meta).hasSize(3);

		assertThat(meta).includes(entry("root", "records"));
		assertThat(meta).includes(entry("totalProperty", "total"));
		assertThat(meta).includes(entry("successProperty", "success"));

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
		assertThat(meta.size()).isEqualTo(10);
		metaData.addFields(null);
		assertThat(meta.size()).isEqualTo(10);

		assertThat(meta).includes(entry("root", "records"));
		assertThat(meta).includes(entry("totalProperty", "total"));
		assertThat(meta).includes(entry("successProperty", "success"));
		assertThat(meta).includes(entry("idProperty", "id"));
		assertThat(meta).includes(entry("start", 0));
		assertThat(meta).includes(entry("limit", 50));
		assertThat(meta).includes(entry("customProperty1", 10));
		assertThat(meta).includes(entry("customProperty2", "aValue"));
		assertThat(meta.containsKey("sortInfo")).isTrue();
		assertThat(meta.containsKey("fields")).isTrue();

		@SuppressWarnings("unchecked")
		Map<String, String> sortInfo = (Map<String, String>) meta.get("sortInfo");
		assertThat(sortInfo).hasSize(2);
		assertThat(sortInfo).includes(entry("field", "name"));
		assertThat(sortInfo).includes(entry("direction", "ASC"));

		@SuppressWarnings("unchecked")
		List<Map<String, Object>> fields = (List<Map<String, Object>>) meta.get("fields");
		assertThat(fields).hasSize(3);

		Map<String, Object> field1 = fields.get(0);
		assertThat(field1).includes(entry("name", "name"));

		Map<String, Object> field2 = fields.get(1);
		assertThat(field2).includes(entry("name", "city"));

		Map<String, Object> field3 = fields.get(2);
		assertThat(field3).includes(entry("name", "country"));

	}
}
