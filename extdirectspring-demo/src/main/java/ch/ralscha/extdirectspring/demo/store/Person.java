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
package ch.ralscha.extdirectspring.demo.store;

public class Person {

	private String fullName;

	private String firstName;

	private String lastName;

	private String id;

	private String street;

	private String city;

	private String state;

	private String zip;

	private String country;

	public Person() {
		// no action here
	}

	public Person(final String[] nextLine) {
		this.id = nextLine[0];
		this.firstName = nextLine[1];
		this.lastName = nextLine[2];
		this.fullName = this.firstName + " " + this.lastName;
		this.street = nextLine[3];
		this.city = nextLine[4];
		this.state = nextLine[5];
		this.zip = nextLine[6];
		this.country = nextLine[7];
	}

	public void update(final Person newValues) {
		this.firstName = newValues.getFirstName();
		this.lastName = newValues.getLastName();
		this.fullName = this.firstName + " " + this.lastName;
		this.street = newValues.getStreet();
		this.city = newValues.getCity();
		this.state = newValues.getState();
		this.zip = newValues.getZip();
		this.country = newValues.getCountry();

	}

	public String getFullName() {
		return fullName;
	}

	public void setFullName(final String fullName) {
		this.fullName = fullName;
	}

	public String getId() {
		return id;
	}

	public void setId(final String id) {
		this.id = id;
	}

	public String getStreet() {
		return street;
	}

	public void setStreet(final String street) {
		this.street = street;
	}

	public String getCity() {
		return city;
	}

	public void setCity(final String city) {
		this.city = city;
	}

	public String getState() {
		return state;
	}

	public void setState(final String state) {
		this.state = state;
	}

	public String getZip() {
		return zip;
	}

	public void setZip(final String zip) {
		this.zip = zip;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(final String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(final String lastName) {
		this.lastName = lastName;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(final String country) {
		this.country = country;
	}

	@Override
	public String toString() {
		return "Person [fullName=" + fullName + ", firstName=" + firstName + ", lastName=" + lastName + ", id=" + id
				+ ", street=" + street + ", city=" + city + ", state=" + state + ", zip=" + zip + ", country="
				+ country + "]";
	}

}
