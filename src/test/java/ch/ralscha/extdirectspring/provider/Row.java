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
package ch.ralscha.extdirectspring.provider;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Row implements Comparable<Row>, RowInterface {

	private int id;

	private String name;

	private boolean admin;

	private BigDecimal salary;

	public Row() {
		// no action
	}

	public Row(int id, String name, boolean admin, String salary) {
		super();
		this.id = id;
		this.name = name;
		this.admin = admin;
		if (salary != null) {
			this.salary = new BigDecimal(salary);
		}
	}

	@Override
	public int getId() {
		return this.id;
	}

	public void setId(int id) {
		this.id = id;
	}

	@Override
	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public boolean isAdmin() {
		return this.admin;
	}

	public void setAdmin(boolean admin) {
		this.admin = admin;
	}

	@Override
	public BigDecimal getSalary() {
		return this.salary;
	}

	public void setSalary(BigDecimal salary) {
		this.salary = salary;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + this.id;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		Row other = (Row) obj;
		if (this.id != other.id) {
			return false;
		}
		return true;
	}

	// @Override
	@Override
	public int compareTo(Row o) {
		return this.id - o.id;
	}

	@Override
	public String toString() {
		return "Row [id=" + this.id + ", name=" + this.name + ", admin=" + this.admin
				+ ", salary=" + this.salary + "]";
	}

}
