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

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;

import ch.ralscha.extdirectspring.annotation.ExtDirectMethod;
import ch.ralscha.extdirectspring.annotation.ExtDirectMethodType;
import ch.ralscha.extdirectspring.bean.ExtDirectFormPostResult;

@Service
public class UserService {

	@ExtDirectMethod(value = ExtDirectMethodType.FORM_POST, group = "itest_user_service",
			synchronizeOnSession = true)
	public ExtDirectFormPostResult updateUser(HttpServletRequest request,
			@Valid User user, BindingResult result) {

		if (request.getParameter("addemailerror") != null) {
			result.rejectValue("email", "", "another email error");
			result.rejectValue("name", "", "a name error");
		}

		ExtDirectFormPostResult resp = new ExtDirectFormPostResult(result);

		resp.addResultProperty("name", user.getName());
		resp.addResultProperty("age", user.getAge());

		return resp;
	}

}