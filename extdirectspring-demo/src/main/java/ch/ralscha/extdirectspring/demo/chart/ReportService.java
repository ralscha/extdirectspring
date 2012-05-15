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
package ch.ralscha.extdirectspring.demo.chart;

import java.util.List;
import java.util.Random;

import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;

import ch.ralscha.extdirectspring.annotation.ExtDirectMethod;
import ch.ralscha.extdirectspring.annotation.ExtDirectMethodType;

import com.google.common.collect.ImmutableList;

@Service
public class ReportService {

	private Random randomGenerator = new Random();

	@ExtDirectMethod(value = ExtDirectMethodType.STORE_READ, group = "pie")
	public List<SeasonData> getSeasonData(@RequestParam(defaultValue = "50") int minRandomValue,
			@RequestParam(value = "maxRandomValue", defaultValue = "250") int maxRandomValue) {

		return new ImmutableList.Builder<SeasonData>()
				.add(new SeasonData("Summer", randomGenerator.nextInt(maxRandomValue - minRandomValue) + minRandomValue))
				.add(new SeasonData("Fall", randomGenerator.nextInt(maxRandomValue - minRandomValue) + minRandomValue))
				.add(new SeasonData("Winter", randomGenerator.nextInt(maxRandomValue - minRandomValue) + minRandomValue))
				.add(new SeasonData("Spring", randomGenerator.nextInt(maxRandomValue - minRandomValue) + minRandomValue))
				.build();
	}
}
