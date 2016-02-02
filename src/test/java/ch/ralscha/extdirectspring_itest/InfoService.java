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

import java.util.Locale;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;

import ch.ralscha.extdirectspring.annotation.ExtDirectMethod;
import ch.ralscha.extdirectspring.annotation.ExtDirectMethodType;
import ch.ralscha.extdirectspring.bean.ExtDirectFormPostResult;

@Service
public class InfoService {

	@Autowired
	private MessageSource messageSource;

	@ExtDirectMethod(value = ExtDirectMethodType.FORM_POST, group = "itest_info_service",
			streamResponse = true)
	public ExtDirectFormPostResult updateInfo(Info info) {
		ExtDirectFormPostResult resp = new ExtDirectFormPostResult(true);
		resp.addResultProperty("user-name-lower-case", info.getUserName().toLowerCase());
		return resp;
	}

	@ExtDirectMethod(value = ExtDirectMethodType.FORM_POST, group = "itest_info_service")
	public ExtDirectFormPostResult updateInfo2nd(Info info) {
		ExtDirectFormPostResult resp = new ExtDirectFormPostResult();
		resp.addResultProperty("user-name-lower-case", info.getUserName().toLowerCase());
		return resp;
	}

	@ExtDirectMethod(value = ExtDirectMethodType.FORM_POST, group = "itest_info_service")
	public ExtDirectFormPostResult updateInfoUser1(@Valid User user,
			BindingResult bindingResult) {
		ExtDirectFormPostResult resp = new ExtDirectFormPostResult(null, null,
				bindingResult);
		resp.addResultProperty("lc", user.getName().toLowerCase());
		return resp;
	}

	@ExtDirectMethod(value = ExtDirectMethodType.FORM_POST, group = "itest_info_service")
	public ExtDirectFormPostResult updateInfoUser2(Locale locale, @Valid User user,
			BindingResult bindingResult) {
		ExtDirectFormPostResult resp = new ExtDirectFormPostResult(locale, null,
				bindingResult);
		resp.addResultProperty("lc", user.getName().toLowerCase());
		return resp;
	}

	@ExtDirectMethod(value = ExtDirectMethodType.FORM_POST, group = "itest_info_service")
	public ExtDirectFormPostResult updateInfoUser3(Locale locale, @Valid User user,
			BindingResult bindingResult) {
		ExtDirectFormPostResult resp = new ExtDirectFormPostResult(locale,
				this.messageSource, bindingResult);
		resp.addResultProperty("lc", user.getName().toLowerCase());
		return resp;
	}

	@ExtDirectMethod(value = ExtDirectMethodType.FORM_POST, group = "itest_info_service")
	public ExtDirectFormPostResult updateInfoUser4(Locale locale, @Valid User user,
			BindingResult bindingResult) {
		ExtDirectFormPostResult resp = new ExtDirectFormPostResult(locale,
				this.messageSource, bindingResult, true);
		resp.addResultProperty("lc", user.getName().toLowerCase());
		return resp;
	}

	@ExtDirectMethod(value = ExtDirectMethodType.FORM_POST, group = "itest_info_service")
	public ExtDirectFormPostResult updateInfoUser5(@Valid User user,
			BindingResult bindingResult) {
		ExtDirectFormPostResult resp = new ExtDirectFormPostResult(null,
				this.messageSource, bindingResult);
		resp.addResultProperty("lc", user.getName().toLowerCase());
		return resp;
	}
}
