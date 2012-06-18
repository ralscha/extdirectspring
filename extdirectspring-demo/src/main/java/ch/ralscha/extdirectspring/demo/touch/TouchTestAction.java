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

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.springframework.stereotype.Service;

import ch.ralscha.extdirectspring.annotation.ExtDirectMethod;
import ch.ralscha.extdirectspring.annotation.ExtDirectMethodType;
import ch.ralscha.extdirectspring.bean.ExtDirectStoreReadRequest;
import ch.ralscha.extdirectspring.demo.util.PropertyOrderingFactory;

import com.google.common.collect.Lists;
import com.google.common.collect.Ordering;

@Service
public class TouchTestAction {

	@ExtDirectMethod(group = "touchdirect")
	public long multiply(final Long num) {
		if (num != null) {
			return num * 8;
		}
		return 0;
	}

	@ExtDirectMethod(group = "touchdirect")
	public String doEcho(final String message) {
		return message;
	}

	@ExtDirectMethod(value = ExtDirectMethodType.POLL, event = "message", group = "touchdirect")
	public String handleMessagePoll() {
		Date now = new Date();
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd 'at' hh:mm:ss");
		return "Successfully polled at: " + formatter.format(now);
	}

	@ExtDirectMethod(value = ExtDirectMethodType.SIMPLE_NAMED, group = "touchdirect")
	public String showDetails(final String firstName, final String lastName, final int age) {
		return String.format("Hi %s %s, you are %d years old.", firstName, lastName, age);
	}

	@ExtDirectMethod(value = ExtDirectMethodType.STORE_READ, group = "touchdirect")
	public List<Turnover> getGrid(final ExtDirectStoreReadRequest request) {
		List<Turnover> result = Lists.newArrayList();

		result.add(new Turnover("ABC Accounting", new BigDecimal("50000")));
		result.add(new Turnover("Ezy Video Rental", new BigDecimal("106300")));
		result.add(new Turnover("Greens Fruit Grocery", new BigDecimal("120000")));
		result.add(new Turnover("Icecream Express", new BigDecimal("73000")));
		result.add(new Turnover("Ripped Gym", new BigDecimal("88400")));
		result.add(new Turnover("Smith Auto Mechanic", new BigDecimal("222980")));

		Ordering<Turnover> ordering = PropertyOrderingFactory.INSTANCE.createOrderingFromSorters(request.getSorters());
		if (ordering != null) {
			return ordering.sortedCopy(result);
		}

		return result;
	}

}
