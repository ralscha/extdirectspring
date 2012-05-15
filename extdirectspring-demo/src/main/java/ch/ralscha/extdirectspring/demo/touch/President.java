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
package ch.ralscha.extdirectspring.demo.touch;

public class President {
	private int id;

	private String firstName;

	private String lastName;

	private String middleInitial;

	private String imageUrl;

	public President(int id, String firstName, String middleInitial, String lastName) {
		this(id, firstName, middleInitial, lastName, null);
	}

	public President(int id, String firstName, String middleInitial, String lastName, String imageUrl) {
		this.id = id;
		this.firstName = firstName;
		this.middleInitial = middleInitial;
		this.lastName = lastName;

		if (imageUrl != null) {
			this.imageUrl = imageUrl;
		} else {
			this.imageUrl = id + firstName.substring(0, 1).toLowerCase() + lastName.substring(0, 1).toLowerCase()
					+ "_header_sm.jpg";
		}
	}

	public String getFirstName() {
		return firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public String getMiddleInitial() {
		return middleInitial;
	}

	public int getId() {
		return id;
	}

	public String getImageUrl() {
		return imageUrl;
	}

}
