/**
 * Copyright 2010-2014 Ralph Schaer <ralphschaer@gmail.com>
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

import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.util.WebUtils;

import ch.ralscha.extdirectspring.bean.SSEvent;
import ch.ralscha.extdirectspring.util.ExtDirectSpringUtil;
import ch.ralscha.extdirectspring.util.MethodInfo;
import ch.ralscha.extdirectspring.util.MethodInfoCache;

@Service
public class SSEHandler {

	private static final Log log = LogFactory.getLog(SSEHandler.class);

	private final ConfigurationService configurationService;

	private final MethodInfoCache methodInfoCache;

	@Autowired
	public SSEHandler(ConfigurationService configurationService,
			MethodInfoCache methodInfoCache) {
		this.configurationService = configurationService;
		this.methodInfoCache = methodInfoCache;
	}

	public void handle(String beanName, String method, HttpServletRequest request,
			HttpServletResponse response, Locale locale) throws Exception {

		MethodInfo methodInfo = methodInfoCache.get(beanName, method);

		SSEvent result = null;
		SSEWriter sseWriter = new SSEWriter(response);

		if (methodInfo != null) {
			try {

				Object[] parameters = configurationService.getParametersResolver()
						.prepareParameters(request, response, locale, methodInfo,
								sseWriter);
				Object methodReturnValue;

				if (configurationService.getConfiguration().isSynchronizeOnSession()
						|| methodInfo.isSynchronizeOnSession()) {
					HttpSession session = request.getSession(false);
					if (session != null) {
						Object mutex = WebUtils.getSessionMutex(session);
						synchronized (mutex) {
							methodReturnValue = ExtDirectSpringUtil.invoke(
									configurationService.getApplicationContext(),
									beanName, methodInfo, parameters);
						}
					}
					else {
						methodReturnValue = ExtDirectSpringUtil.invoke(
								configurationService.getApplicationContext(), beanName,
								methodInfo, parameters);
					}
				}
				else {
					methodReturnValue = ExtDirectSpringUtil.invoke(
							configurationService.getApplicationContext(), beanName,
							methodInfo, parameters);
				}

				if (methodReturnValue instanceof SSEvent) {
					result = (SSEvent) methodReturnValue;
				}
				else if (methodReturnValue != null) {
					result = new SSEvent();
					result.setData(methodReturnValue.toString());
				}

			}
			catch (Exception e) {
				log.error("Error polling method '" + beanName + "." + method + "'",
						e.getCause() != null ? e.getCause() : e);

				Throwable cause;
				if (e.getCause() != null) {
					cause = e.getCause();
				}
				else {
					cause = e;
				}

				result = new SSEvent();
				result.setEvent("error");
				result.setData(configurationService.getConfiguration().getMessage(cause));

				if (configurationService.getConfiguration().isSendStacktrace()) {
					result.setComment(ExtDirectSpringUtil.getStackTrace(cause));
				}
			}
		}
		else {
			log.error("Error invoking method '" + beanName + "." + method
					+ "'. Method or Bean not found");

			result = new SSEvent();
			result.setEvent("error");
			result.setData(configurationService.getConfiguration()
					.getDefaultExceptionMessage());

			if (configurationService.getConfiguration().isSendStacktrace()) {
				result.setComment("Bean or Method '" + beanName + "." + method
						+ "' not found");
			}
		}

		if (result != null) {
			sseWriter.write(result);
		}
	}

}
