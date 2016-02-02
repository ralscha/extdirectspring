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

import java.util.Map;

/**
 * Class contains information about the property/field on which to perform the grouping
 * and if sort is ascending or descending. Ext JS can send more than one group info in one
 * request.
 */
public class GroupInfo {
	private final String property;

	private final SortDirection direction;

	public GroupInfo(String property, SortDirection direction) {
		this.property = property;
		this.direction = direction;
	}

	public String getProperty() {
		return this.property;
	}

	public SortDirection getDirection() {
		return this.direction;
	}

	public static GroupInfo create(Map<String, Object> jsonData) {
		String property = (String) jsonData.get("property");
		String direction = (String) jsonData.get("direction");

		return new GroupInfo(property, SortDirection.fromString(direction));
	}

	@Override
	public String toString() {
		return "GroupInfo [property=" + this.property + ", direction=" + this.direction
				+ "]";
	}

}
