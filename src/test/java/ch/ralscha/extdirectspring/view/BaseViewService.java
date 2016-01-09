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

import java.util.ArrayList;
import java.util.List;

public class BaseViewService {

	protected Employee createEmployee() {
		Employee e = new Employee();
		e.setId(1L);
		e.setFirstName("firstName");
		e.setLastName("lastName");
		e.setPhone("phone");
		e.setAddress("address");
		e.setSecretKey("mySecret");
		return e;
	}

	protected List<Employee> createEmployees(long no) {
		List<Employee> employees = new ArrayList<Employee>();
		for (long i = 1L; i <= no; i++) {
			Employee e = new Employee();
			e.setId(i);
			e.setFirstName("firstName" + i);
			e.setLastName("lastName" + i);
			e.setPhone("phone" + i);
			e.setAddress("address" + i);
			e.setSecretKey("mySecret" + i);
			employees.add(e);
		}
		return employees;
	}

	protected EmployeeWithJsonView createEmployeeWithJsonView(Class<?> jsonView) {
		EmployeeWithJsonView e = new EmployeeWithJsonView();
		e.setId(1L);
		e.setFirstName("firstName");
		e.setLastName("lastName");
		e.setPhone("phone");
		e.setAddress("address");
		e.setSecretKey("mySecret");
		e.setJsonView(jsonView);
		return e;
	}
}
