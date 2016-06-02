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
package ch.ralscha.extdirectspring.provider;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import ch.ralscha.extdirectspring.annotation.ExtDirectMethod;
import ch.ralscha.extdirectspring.annotation.ExtDirectMethodType;
import ch.ralscha.extdirectspring.bean.EdFormPostResult;
import ch.ralscha.extdirectspring.bean.ExtDirectFormPostResult;
import ch.ralscha.extdirectspring.bean.ExtDirectResponseBuilder;

@Controller
public class FormInfoController {

	@SuppressWarnings("unused")
	@ExtDirectMethod(value = ExtDirectMethodType.FORM_POST, group = "group3",
			streamResponse = true)
	@RequestMapping(value = "/updateInfo", method = RequestMethod.POST)
	public void updateInfo(Locale locale, HttpServletRequest request,
			HttpServletResponse response, FormInfo formInfo, BindingResult result) {
		ExtDirectResponseBuilder.create(request, response).addErrors(result)
				.buildAndWrite();
	}

	@SuppressWarnings("unused")
	@ExtDirectMethod(value = ExtDirectMethodType.FORM_POST, group = "group2")
	@RequestMapping(value = "/upload", method = RequestMethod.POST)
	public void upload(Locale locale, HttpServletRequest request,
			HttpServletResponse response, FormInfo formInfo, BindingResult result)
			throws IOException {
		ExtDirectResponseBuilder.create(request, response).addErrors(result)
				.buildAndWrite();
	}

	@SuppressWarnings("unused")
	@ExtDirectMethod(value = ExtDirectMethodType.FORM_POST, group = "group2")
	public void invalidMethod1(Locale locale, HttpServletRequest request,
			HttpServletResponse response, FormInfo formInfo, BindingResult result) {
		// dummy test method
	}

	@SuppressWarnings("unused")
	@ExtDirectMethod(value = ExtDirectMethodType.FORM_POST, group = "group2")
	@RequestMapping(value = "/upload", method = RequestMethod.GET)
	public void invalidMethod2(Locale locale, HttpServletRequest request,
			HttpServletResponse response, FormInfo formInfo, BindingResult result) {
		// dummy test method
	}

	@ExtDirectMethod(value = ExtDirectMethodType.FORM_POST, group = "group3")
	public ExtDirectFormPostResult updateInfoDirect(FormInfo formInfo,
			BindingResult result) {
		ExtDirectFormPostResult e = new ExtDirectFormPostResult(result);
		e.addResultProperty("name", formInfo.getName().toUpperCase());
		e.addResultProperty("age", formInfo.getAge() + 10);
		e.addResultProperty("admin", !formInfo.isAdmin());
		BigDecimal bd = new BigDecimal("1000");
		bd = bd.add(formInfo.getSalary());
		e.addResultProperty("salary", bd);
		e.addResultProperty("result", formInfo.getResult() + "RESULT");
		return e;
	}

	@ExtDirectMethod(value = ExtDirectMethodType.FORM_POST, group = "group3")
	public EdFormPostResult updateInfoDirectEd(FormInfo formInfo, BindingResult result) {
		EdFormPostResult.Builder e = EdFormPostResult.builder().addError(result);
		e.putResult("name", formInfo.getName().toUpperCase());
		e.putResult("age", formInfo.getAge() + 10);
		e.putResult("admin", !formInfo.isAdmin());
		BigDecimal bd = new BigDecimal("1000");
		bd = bd.add(formInfo.getSalary());
		e.putResult("salary", bd);
		e.putResult("result", formInfo.getResult() + "RESULT");
		return e.build();
	}
}
