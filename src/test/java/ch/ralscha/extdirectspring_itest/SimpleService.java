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
package ch.ralscha.extdirectspring_itest;

import java.util.concurrent.TimeUnit;

import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;

import ch.ralscha.extdirectspring.annotation.ExtDirectMethod;
import ch.ralscha.extdirectspring.annotation.ExtDirectMethodType;

@Service
public class SimpleService {

	@ExtDirectMethod(group = "itest_simple")
	public String toUpperCase(String in) {
		if (in != null) {
			return in.toUpperCase();
		}
		return null;
	}

	@ExtDirectMethod(value = ExtDirectMethodType.SIMPLE_NAMED, group = "itest_simple",
			streamResponse = true)
	public String echo(String userId, @RequestParam(defaultValue = "10") int logLevel) {
		// Simulate some work
		try {
			TimeUnit.MILLISECONDS.sleep(200);
		}
		catch (InterruptedException e) {
			// do nothing here
		}
		return String.format("UserId: %s LogLevel: %d", userId, logLevel);
	}

	@ExtDirectMethod(value = ExtDirectMethodType.POLL, group = "itest_simple")
	public String poll(@RequestParam String id) {
		return id;
	}

}
