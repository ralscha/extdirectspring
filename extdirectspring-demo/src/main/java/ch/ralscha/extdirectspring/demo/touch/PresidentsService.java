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
package ch.ralscha.extdirectspring.demo.touch;

import java.util.List;

import org.springframework.stereotype.Service;

import ch.ralscha.extdirectspring.annotation.ExtDirectMethod;
import ch.ralscha.extdirectspring.annotation.ExtDirectMethodType;

import com.google.common.collect.Lists;

@Service
public class PresidentsService {

	@ExtDirectMethod(value = ExtDirectMethodType.STORE_READ, group = "touch")
	public List<President> getPresidents() {
		return createTestData();
	}

	private List<President> createTestData() {
		List<President> presidents = Lists.newArrayList();

		presidents.add(new President("George", "Washington"));
		presidents.add(new President("John", "Adams"));
		presidents.add(new President("Thomas", "Jefferson"));
		presidents.add(new President("James", "Madison"));
		presidents.add(new President("James", "Monroe"));
		presidents.add(new President("John", "Quincy Adams"));
		presidents.add(new President("Andrew", "Jackson"));
		presidents.add(new President("Martin", "Van Buren"));
		presidents.add(new President("William", "Henry Harrison"));
		presidents.add(new President("John", "Tyler"));
		presidents.add(new President("James", "K", "Polk"));
		presidents.add(new President("Zachary", "Taylor"));
		presidents.add(new President("Millard", "Fillmore"));
		presidents.add(new President("Franklin", "Pierce"));
		presidents.add(new President("James", "Buchanan"));
		presidents.add(new President("Abraham", "Lincoln"));
		presidents.add(new President("Andrew", "Johnson"));
		presidents.add(new President("Ulysses", "S", "Grant"));
		presidents.add(new President("Rutherford", "B", "Hayes"));
		presidents.add(new President("James", "A", "Garfield"));
		presidents.add(new President("Chester", "Arthur"));
		presidents.add(new President("Grover", "Cleveland"));
		presidents.add(new President("Benjamin", "Harrison"));
		presidents.add(new President("William", "McKinley"));
		presidents.add(new President("Theodore", "Roosevelt"));
		presidents.add(new President("William", "Howard Taft"));
		presidents.add(new President("Woodrow", "Wilson"));
		presidents.add(new President("Warren", "G", "Harding"));
		presidents.add(new President("Calvin", "Coolidge"));
		presidents.add(new President("Herbert", "Hoover"));
		presidents.add(new President("Franklin", "D", "Roosevelt"));
		presidents.add(new President("Harry", "S", "Truman"));
		presidents.add(new President("Dwight", "D", "Eisenhower"));
		presidents.add(new President("John", "F", "Kennedy"));
		presidents.add(new President("Lyndon", "B", "Johnson"));
		presidents.add(new President("Richard", "Nixon"));
		presidents.add(new President("Gerald", "Ford"));
		presidents.add(new President("Jimmy", "Carter"));
		presidents.add(new President("Ronald", "Reagan"));
		presidents.add(new President("George", "Bush"));
		presidents.add(new President("Bill", "Clinton"));
		presidents.add(new President("George", "W", "Bush"));
		presidents.add(new President("Barack", "Obama"));

		return presidents;
	}

}
