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
package ch.ralscha.extdirectspring.provider;

import java.io.IOException;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import ch.ralscha.extdirectspring.annotation.ExtDirectMethod;
import ch.ralscha.extdirectspring.annotation.ExtDirectMethodType;
import ch.ralscha.extdirectspring.bean.ExtDirectResponseBuilder;

@Controller
@SuppressWarnings("all")
public class FormInfoController {

	@ExtDirectMethod(value = ExtDirectMethodType.FORM_POST, group = "group3")
	@RequestMapping(value = "/updateInfo", method = RequestMethod.POST)
	public void updateInfo(Locale locale, HttpServletRequest request, HttpServletResponse response,
			final FormInfo formInfo, BindingResult result) {
		ExtDirectResponseBuilder.create(request, response).addErrors(result).buildAndWrite();
	}

	@ExtDirectMethod(value = ExtDirectMethodType.FORM_POST, group = "group2")
	@RequestMapping(value = "/upload", method = RequestMethod.POST)
	public void upload(Locale locale, HttpServletRequest request, HttpServletResponse response,
			final FormInfo formInfo, BindingResult result) throws IOException {
		ExtDirectResponseBuilder.create(request, response).addErrors(result).buildAndWrite();
	}

	@ExtDirectMethod(value = ExtDirectMethodType.FORM_POST, group = "group2")
	public void invalidMethod1(Locale locale, HttpServletRequest request, final HttpServletResponse response,
			FormInfo formInfo, BindingResult result) {
		// dummy test method
	}

	@ExtDirectMethod(value = ExtDirectMethodType.FORM_POST, group = "group2")
	@RequestMapping(value = "/upload", method = RequestMethod.GET)
	public void invalidMethod2(Locale locale, HttpServletRequest request, final HttpServletResponse response,
			FormInfo formInfo, BindingResult result) {
		// dummy test method
	}
}
