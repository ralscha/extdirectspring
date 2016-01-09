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
package ch.ralscha.extdirectspring.controller;

import java.util.Collections;

import javax.servlet.http.HttpServletRequest;

import ch.ralscha.extdirectspring.annotation.ExtDirectMethodType;
import ch.ralscha.extdirectspring.bean.BaseResponse;
import ch.ralscha.extdirectspring.util.MethodInfo;

public class AppExceptionHandler implements RouterExceptionHandler {

	@Override
	public Object handleException(MethodInfo methodInfo, BaseResponse response,
			Exception e, HttpServletRequest request) {
		response.setType("exception");
		response.setMessage("Houston, we have a problem");
		response.setWhere("Space");

		if (methodInfo.isType(ExtDirectMethodType.FORM_POST)) {
			return Collections.singletonMap("success", Boolean.FALSE);
		}

		return null;
	}

}
