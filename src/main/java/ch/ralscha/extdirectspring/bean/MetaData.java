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
package ch.ralscha.extdirectspring.bean;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
public class MetaData {

	private final Map<String, Object> metaData;

	public MetaData() {
		this.metaData = new LinkedHashMap<String, Object>();
		this.metaData.put("root", "records");
		this.metaData.put("totalProperty", "total");
		this.metaData.put("successProperty", "success");
	}

	public void setPagingParameter(int start, int limit) {
		this.metaData.put("start", start);
		this.metaData.put("limit", limit);
	}

	public void setIdProperty(String idProperty) {
		this.metaData.put("idProperty", idProperty);
	}

	public void setSortInfo(String field, SortDirection direction) {
		Map<String, String> sortInfo = new LinkedHashMap<String, String>();
		sortInfo.put("field", field);
		sortInfo.put("direction", direction.getName());
		this.metaData.put("sortInfo", sortInfo);
	}

	public void addFields(List<Field> fields) {
		if (fields != null) {
			for (Field field : fields) {
				addField(field);
			}
		}
	}

	@SuppressWarnings("unchecked")
	public void addField(Field field) {

		List<Map<String, Object>> fields = (List<Map<String, Object>>) this.metaData
				.get("fields");

		if (fields == null) {
			fields = new ArrayList<Map<String, Object>>();
			this.metaData.put("fields", fields);
		}

		fields.add(field.getFieldData());
	}

	public void addCustomProperty(String key, Object value) {
		this.metaData.put(key, value);
	}

	public Map<String, Object> getMetaData() {
		return Collections.unmodifiableMap(this.metaData);
	}

}
