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

import java.math.BigDecimal;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import ch.ralscha.extdirectspring.annotation.ExtDirectMethod;
import ch.ralscha.extdirectspring.annotation.ExtDirectMethodType;
import ch.ralscha.extdirectspring.bean.EdFormPostResult;
import ch.ralscha.extdirectspring.bean.ExtDirectFormPostResult;

@Controller
public class FormInfoController3 {

	@SuppressWarnings("unused")
	@ExtDirectMethod(value = ExtDirectMethodType.FORM_POST_JSON)
	@RequestMapping(value = "/updateInfoJson", method = RequestMethod.POST)
	public ExtDirectFormPostResult updateInfoJson(Locale locale,
			HttpServletRequest request, HttpServletResponse response,
			@Valid FormInfo formInfo) {

		return new ExtDirectFormPostResult(true);
	}

	@SuppressWarnings("unused")
	@ExtDirectMethod(value = ExtDirectMethodType.FORM_POST_JSON)
	public ExtDirectFormPostResult updateInfoJsonDirect(Locale locale,
			@RequestParam(value = "p1", required = true) Long param1,
			@RequestParam(value = "p2", required = true) String param2,
			@Valid FormInfo formInfo) {

		ExtDirectFormPostResult e = new ExtDirectFormPostResult();
		e.addResultProperty("name", formInfo.getName().toUpperCase());
		e.addResultProperty("age", formInfo.getAge() + 10);
		e.addResultProperty("admin", !formInfo.isAdmin());
		BigDecimal bd = new BigDecimal("1000");
		bd = bd.add(formInfo.getSalary());
		e.addResultProperty("salary", bd);
		e.addResultProperty("result", formInfo.getResult() + "RESULT");
		return e;
	}

	@SuppressWarnings("unused")
	@ExtDirectMethod(value = ExtDirectMethodType.FORM_POST_JSON)
	public ExtDirectFormPostResult updateInfoJsonDirectError(Locale locale,
			HttpServletRequest request, HttpServletResponse response,
			@Valid FormInfo formInfo) {

		ExtDirectFormPostResult e = new ExtDirectFormPostResult();
		e.addError("age", "age is wrong");
		return e;
	}

	@SuppressWarnings("unused")
	@ExtDirectMethod(value = ExtDirectMethodType.FORM_POST_JSON)
	@RequestMapping(value = "/updateInfoJsonEd", method = RequestMethod.POST)
	public EdFormPostResult updateInfoJsonEd(Locale locale, HttpServletRequest request,
			HttpServletResponse response, @Valid FormInfo formInfo) {
		return EdFormPostResult.success();
	}

	@SuppressWarnings("unused")
	@ExtDirectMethod(value = ExtDirectMethodType.FORM_POST_JSON)
	public EdFormPostResult updateInfoJsonDirectEd(Locale locale,
			@RequestParam(value = "p1", required = true) Long param1,
			@RequestParam(value = "p2", required = true) String param2,
			@Valid FormInfo formInfo) {

		EdFormPostResult.Builder e = EdFormPostResult.builder();
		e.putResult("name", formInfo.getName().toUpperCase());
		e.putResult("age", formInfo.getAge() + 10);
		e.putResult("admin", !formInfo.isAdmin());
		BigDecimal bd = new BigDecimal("1000");
		bd = bd.add(formInfo.getSalary());
		e.putResult("salary", bd);
		e.putResult("result", formInfo.getResult() + "RESULT");
		e.success();
		return e.build();
	}

	@SuppressWarnings("unused")
	@ExtDirectMethod(value = ExtDirectMethodType.FORM_POST_JSON)
	public EdFormPostResult updateInfoJsonDirectErrorEd(Locale locale,
			HttpServletRequest request, HttpServletResponse response,
			@Valid FormInfo formInfo) {
		return EdFormPostResult.builder().addError("age", "age is wrong").build();
	}

	@SuppressWarnings("unused")
	@ExtDirectMethod(value = ExtDirectMethodType.FORM_POST_JSON)
	public void updateInfoJsonDirectNotRegisteredWithBindingResultAsParameter(
			Locale locale, HttpServletRequest request, HttpServletResponse response,
			@Valid FormInfo formInfo, BindingResult result) {
		// nothing here
	}

	@SuppressWarnings("unused")
	@ExtDirectMethod(value = ExtDirectMethodType.FORM_POST_JSON)
	public void updateInfoJsonDirectNotRegisteredWithMultipartFileAsParameter(
			Locale locale, HttpServletRequest request, HttpServletResponse response,
			@Valid FormInfo formInfo, MultipartFile multipartFile) {
		// nothing here
	}
}
