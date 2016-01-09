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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import ch.ralscha.extdirectspring.annotation.ExtDirectMethod;
import ch.ralscha.extdirectspring.annotation.ExtDirectMethodType;
import ch.ralscha.extdirectspring.bean.ExtDirectResponse;
import ch.ralscha.extdirectspring.bean.ExtDirectResponseBuilder;

@Service
public class WrongFormPostController {

	@ExtDirectMethod(ExtDirectMethodType.FORM_POST)
	@RequestMapping(value = "/wrong", method = RequestMethod.POST)
	public void updateInfo1(HttpServletRequest request, HttpServletResponse response) {
		ExtDirectResponseBuilder.create(request, response).buildAndWrite();
	}

	@ExtDirectMethod(ExtDirectMethodType.FORM_POST)
	public void updateInfo2(HttpServletRequest request, HttpServletResponse response) {
		ExtDirectResponseBuilder.create(request, response).buildAndWrite();
	}

	@ExtDirectMethod(ExtDirectMethodType.FORM_POST)
	public ExtDirectResponse updateInfo3(HttpServletRequest request,
			HttpServletResponse response) {
		ExtDirectResponseBuilder.create(request, response).buildAndWrite();
		return null;
	}

	@ExtDirectMethod(ExtDirectMethodType.FORM_POST)
	@RequestMapping(value = "/wrong", method = RequestMethod.POST)
	@ResponseBody
	public void updateInfo4(HttpServletRequest request, HttpServletResponse response) {
		ExtDirectResponseBuilder.create(request, response).buildAndWrite();
	}
}
