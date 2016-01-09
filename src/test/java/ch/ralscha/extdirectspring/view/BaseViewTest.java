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
package ch.ralscha.extdirectspring.view;

import org.assertj.core.data.MapEntry;

public class BaseViewTest {

	protected MapEntry[] noView() {
		return new MapEntry[] { MapEntry.entry("id", 1),
				MapEntry.entry("firstName", "firstName"),
				MapEntry.entry("lastName", "lastName"), MapEntry.entry("phone", "phone"),
				MapEntry.entry("address", "address"),
				MapEntry.entry("secretKey", "mySecret") };
	}

	protected MapEntry[] summaryView() {
		return new MapEntry[] { MapEntry.entry("id", 1),
				MapEntry.entry("firstName", "firstName"),
				MapEntry.entry("lastName", "lastName") };
	}

	protected MapEntry[] detailView() {
		return new MapEntry[] { MapEntry.entry("id", 1),
				MapEntry.entry("firstName", "firstName"),
				MapEntry.entry("lastName", "lastName"), MapEntry.entry("phone", "phone"),
				MapEntry.entry("address", "address") };
	}
}
