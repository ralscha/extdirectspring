/**
 * Copyright 2010-2013 Ralph Schaer <ralphschaer@gmail.com>
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

import ch.ralscha.extdirectspring.bean.BaseResponse;
import ch.ralscha.extdirectspring.util.ExtDirectSpringUtil;

public class DefaultRouterExceptionHandler implements RouterExceptionHandler {

	private final ConfigurationService configurationService;

	public DefaultRouterExceptionHandler(ConfigurationService configurationService) {
		this.configurationService = configurationService;
	}

	@Override
	public Object handleException(BaseResponse response, Exception e, HttpServletRequest request) {
		Throwable cause;
		if (e.getCause() != null) {
			cause = e.getCause();
		} else {
			cause = e;
		}

		response.setType("exception");
		response.setMessage(configurationService.getConfiguration().getMessage(cause));

		if (configurationService.getConfiguration().isSendStacktrace()) {
			response.setWhere(ExtDirectSpringUtil.getStackTrace(cause));
		} else {
			response.setWhere(null);
		}

		return Collections.singletonMap("success", false);
	}
}
