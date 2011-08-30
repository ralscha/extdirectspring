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

import java.util.Map;

public class GroupInfo {
	private String property;
	private SortDirection direction;

	public GroupInfo(String property, SortDirection direction) {
		this.property = property;
		this.direction = direction;
	}

	public String getProperty() {
		return property;
	}

	public SortDirection getDirection() {
		return direction;
	}

	public static GroupInfo create(final Map<String, Object> jsonData) {
		String property = (String) jsonData.get("property");
		String direction = (String) jsonData.get("direction");

		GroupInfo sortInfo = new GroupInfo(property, SortDirection.fromString(direction));

		return sortInfo;
	}

	@Override
	public String toString() {
		return "GroupInfo [property=" + property + ", direction=" + direction + "]";
	}

}
