/**
 * Copyright 2010 Ralph Schaer <ralphschaer@gmail.com>
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

package com.googlecode.extdirectspring.demo;

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
    //no action here
  }

  public Person(String[] nextLine) {
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
  
  public void update(Person newValues) {
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

  public void setFullName(String fullName) {
    this.fullName = fullName;
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getStreet() {
    return street;
  }

  public void setStreet(String street) {
    this.street = street;
  }

  public String getCity() {
    return city;
  }

  public void setCity(String city) {
    this.city = city;
  }

  public String getState() {
    return state;
  }

  public void setState(String state) {
    this.state = state;
  }

  public String getZip() {
    return zip;
  }

  public void setZip(String zip) {
    this.zip = zip;
  }

  public String getFirstName() {
    return firstName;
  }

  public void setFirstName(String firstName) {
    this.firstName = firstName;
  }

  public String getLastName() {
    return lastName;
  }

  public void setLastName(String lastName) {
    this.lastName = lastName;
  }

  public String getCountry() {
    return country;
  }

  public void setCountry(String country) {
    this.country = country;
  }


}
