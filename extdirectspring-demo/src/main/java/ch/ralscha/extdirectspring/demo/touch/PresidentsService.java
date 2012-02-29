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

		presidents.add(new President(1, "George", null, "Washington", "georgewashington.png"));
		presidents.add(new President(2, "John", null, "Adams", "johnadams.png"));
		presidents.add(new President(3, "Thomas", null, "Jefferson"));
		presidents.add(new President(4, "James", null, "Madison"));
		presidents.add(new President(5, "James", null, "Monroe"));
		presidents.add(new President(6, "John", null, "Quincy Adams"));
		presidents.add(new President(7, "Andrew", null, "Jackson"));
		presidents.add(new President(8, "Martin", null, "Van Buren"));
		presidents.add(new President(9, "William", null, "Henry Harrison"));
		presidents.add(new President(10, "John", null, "Tyler"));
		presidents.add(new President(11, "James", "K", "Polk"));
		presidents.add(new President(12, "Zachary", null, "Taylor"));
		presidents.add(new President(13, "Millard", null, "Fillmore"));
		presidents.add(new President(14, "Franklin", null, "Pierce"));
		presidents.add(new President(15, "James", null, "Buchanan", "15jp_header_sm.jpg"));
		presidents.add(new President(16, "Abraham", null, "Lincoln"));
		presidents.add(new President(17, "Andrew", null, "Johnson"));
		presidents.add(new President(18, "Ulysses", "S", "Grant"));
		presidents.add(new President(19, "Rutherford", "B", "Hayes"));
		presidents.add(new President(20, "James", "A", "Garfield"));
		presidents.add(new President(21, "Chester", null, "Arthur"));
		presidents.add(new President(22, "Grover", null, "Cleveland"));
		presidents.add(new President(23, "Benjamin", null, "Harrison"));
		presidents.add(new President(24, "Grover", null, "Cleveland"));
		presidents.add(new President(25, "William", null, "McKinley"));
		presidents.add(new President(26, "Theodore", null, "Roosevelt"));
		presidents.add(new President(27, "William", null, "Howard Taft"));
		presidents.add(new President(28, "Woodrow", null, "Wilson"));
		presidents.add(new President(29, "Warren", "G", "Harding"));
		presidents.add(new President(30, "Calvin", null, "Coolidge"));
		presidents.add(new President(31, "Herbert", null, "Hoover"));
		presidents.add(new President(32, "Franklin", "D", "Roosevelt"));
		presidents.add(new President(33, "Harry", "S", "Truman"));
		presidents.add(new President(34, "Dwight", "D", "Eisenhower"));
		presidents.add(new President(35, "John", "F", "Kennedy"));
		presidents.add(new President(36, "Lyndon", "B", "Johnson"));
		presidents.add(new President(37, "Richard", null, "Nixon"));
		presidents.add(new President(38, "Gerald", null, "Ford"));
		presidents.add(new President(39, "Jimmy", null, "Carter"));
		presidents.add(new President(40, "Ronald", null, "Reagan"));
		presidents.add(new President(41, "George", null, "Bush"));
		presidents.add(new President(42, "Bill", null, "Clinton"));
		presidents.add(new President(43, "George", "W", "Bush"));
		presidents.add(new President(44, "Barack", null, "Obama"));

		return presidents;
	}

}
