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
package ch.ralscha.extdirectspring.demo;

import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;

import ch.ralscha.extdirectspring.annotation.ExtDirectMethod;
import ch.ralscha.extdirectspring.annotation.ExtDirectMethodType;

@Service
public class Poll {

	@ExtDirectMethod(value = ExtDirectMethodType.POLL, event = "message", group = "example")
	public String handleMessagePoll() {
		Date now = new Date();
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd 'at' hh:mm:ss");
		return "Successfully polled at: " + formatter.format(now);
	}

	@ExtDirectMethod(value = ExtDirectMethodType.POLL, event = "pollWithParams", group = "example")
	public String pollingWithParams(@RequestParam(value = "no") final int no,
			@RequestParam(value = "name") final String name,
			@RequestParam(value = "dummy", defaultValue = "CH") final String dummy, final HttpServletRequest request) {
		return request.getRequestURI() + ":  POST PARAMETERS: no=" + no + ", name=" + name + ", dummy=" + dummy;
	}
}