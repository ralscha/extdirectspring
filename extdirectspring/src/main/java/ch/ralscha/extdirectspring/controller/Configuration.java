/**
 * Copyright 2010 Ralph Schaer <ralphschaer@gmail.com>
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

import java.util.Map;

public class Configuration {
	private String defaultExceptionMessage = "Server Error";
	private boolean exceptionNameAsMessage = false;
	private boolean sendStacktrace = false;
	private Map<Class<?>, String> exceptionToMessage;

	public String getDefaultExceptionMessage() {
		return defaultExceptionMessage;
	}

	public void setDefaultExceptionMessage(String defaultExceptionMessage) {
		this.defaultExceptionMessage = defaultExceptionMessage;
	}

	public boolean isExceptionNameAsMessage() {
		return exceptionNameAsMessage;
	}

	public void setExceptionNameAsMessage(boolean exceptionNameAsMessage) {
		this.exceptionNameAsMessage = exceptionNameAsMessage;
	}

	public boolean isSendStacktrace() {
		return sendStacktrace;
	}

	public void setSendStacktrace(boolean sendStacktrace) {
		this.sendStacktrace = sendStacktrace;
	}

	public Map<Class<?>, String> getExceptionToMessage() {
		return exceptionToMessage;
	}

	public void setExceptionToMessage(Map<Class<?>, String> exceptionToMessage) {
		this.exceptionToMessage = exceptionToMessage;
	}

	public String getMessage(Class<?> exceptionClass) {
		String message = null;
		if (getExceptionToMessage() != null) {
			message = getExceptionToMessage().get(exceptionClass);
		}

		if (message == null) {
			if (isExceptionNameAsMessage()) {
				return exceptionClass.getName();
			}
			return getDefaultExceptionMessage();
		}

		return message;
	}
}
