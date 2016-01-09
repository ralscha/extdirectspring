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

import java.text.SimpleDateFormat;
import java.util.Date;

import javax.validation.Valid;

import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;

import ch.ralscha.extdirectspring.annotation.ExtDirectMethod;
import ch.ralscha.extdirectspring.annotation.ExtDirectMethodType;
import ch.ralscha.extdirectspring.bean.ExtDirectFormPostResult;

@Service
public class UserServiceInitBinderService {

	@ExtDirectMethod(value = ExtDirectMethodType.FORM_POST,
			group = "itest_userib_service")
	public ExtDirectFormPostResult updateUser(@Valid UserExtended user,
			BindingResult result) {

		ExtDirectFormPostResult resp = new ExtDirectFormPostResult(result);
		resp.addResultProperty("name", user.getName());
		resp.addResultProperty("firstName", user.getFirstName());
		resp.addResultProperty("email", user.getEmail());
		resp.addResultProperty("age", user.getAge());

		if (user.getDateOfBirth() != null) {
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
			resp.addResultProperty("dateOfBirth",
					dateFormat.format(user.getDateOfBirth()));
		}

		resp.addResultProperty("flag", user.isFlag());
		return resp;
	}

	@InitBinder
	public void initBinder(WebDataBinder binder) {
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
		dateFormat.setLenient(false);
		binder.registerCustomEditor(Date.class, new CustomDateEditor(dateFormat, true));
	}

}