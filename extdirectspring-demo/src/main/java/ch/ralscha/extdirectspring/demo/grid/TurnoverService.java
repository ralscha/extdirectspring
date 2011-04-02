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
package ch.ralscha.extdirectspring.demo.grid;

import java.util.List;

import org.springframework.stereotype.Service;

import ch.ralscha.extdirectspring.annotation.ExtDirectMethod;
import ch.ralscha.extdirectspring.annotation.ExtDirectMethodType;

import com.google.common.collect.ImmutableList;

@Service
public class TurnoverService {

	private ImmutableList<Company> companies;

	public TurnoverService() {
		ImmutableList.Builder<Company> builder = new ImmutableList.Builder<Company>();

		builder.add(new Company("ABC Accounting", 50000));
		builder.add(new Company("Ezy Video Rental", 106300));
		builder.add(new Company("Greens Fruit Grocery", 120000));
		builder.add(new Company("Icecream Express", 73000));
		builder.add(new Company("Ripped Gym", 88400));
		builder.add(new Company("Smith Auto Mechanic", 222980));

		companies = builder.build();
	}

	@ExtDirectMethod(value = ExtDirectMethodType.STORE_READ, group = "turnover")
	public List<Company> getTurnovers() {
		return companies;
	}

}
