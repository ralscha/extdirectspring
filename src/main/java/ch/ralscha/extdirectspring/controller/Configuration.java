/**
 * Copyright 2010-2011 Ralph Schaer <ralphschaer@gmail.com>
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

import org.springframework.util.StringUtils;

/**
 * Configuration class to configure the way exception messages get sent back to the client 
 * 
 * If there is a mapping for the exception in exceptionToMessage and the value is not null send this value.
 * If there is a mapping for the exception in exceptionToMessage and the value is null send exception.getMessage().
 * If there is no mapping and sendExceptionMessage is true send exception.getMessage().
 * If there is no mapping and sendExceptionMessage is false send defaultExceptionMessage.
 * 
 * If sendStacktrace is true, send the full stacktrace in the json field 'where'.
 * 
 * If alwaysWrapStoreResponse is true, always wrap a response of a STORE_READ and STORE_MODIFY response 
 * in a ExtDirectStoreResponse object
 * 
 * If synchronizeOnSession is true, execution of the methods is synchronized on the session,
 * to serialize parallel invocations from the same client.
 * 
 * @author Ralph Schaer
 */
public class Configuration {
	private String defaultExceptionMessage = "Server Error";
	private boolean sendExceptionMessage = false;
	private boolean sendStacktrace = false;
	private Map<Class<?>, String> exceptionToMessage;
	private boolean alwaysWrapStoreResponse = false;
	private boolean synchronizeOnSession = false;
	private Integer timeout = null;
	private Integer maxRetries = null;
	private String enableBuffer = null;

	public String getDefaultExceptionMessage() {
		return defaultExceptionMessage;
	}

	public void setDefaultExceptionMessage(String defaultExceptionMessage) {
		this.defaultExceptionMessage = defaultExceptionMessage;
	}

	public boolean isSendExceptionMessage() {
		return sendExceptionMessage;
	}

	public void setSendExceptionMessage(boolean sendExceptionMessage) {
		this.sendExceptionMessage = sendExceptionMessage;
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

	public boolean isAlwaysWrapStoreResponse() {
		return alwaysWrapStoreResponse;
	}

	public void setAlwaysWrapStoreResponse(boolean alwaysWrapStoreResponse) {
		this.alwaysWrapStoreResponse = alwaysWrapStoreResponse;
	}

	public boolean isSynchronizeOnSession() {
		return synchronizeOnSession;
	}

	public void setSynchronizeOnSession(boolean synchronizeOnSession) {
		this.synchronizeOnSession = synchronizeOnSession;
	}

	public Integer getTimeout() {
		return timeout;
	}

	public void setTimeout(Integer timeout) {
		this.timeout = timeout;
	}

	public Integer getMaxRetries() {
		return maxRetries;
	}

	public void setMaxRetries(Integer maxRetries) {
		this.maxRetries = maxRetries;
	}

	public String getEnableBuffer() {
		return enableBuffer;
	}

	public void setEnableBuffer(String enableBuffer) {
		this.enableBuffer = enableBuffer;
	}

	public String getMessage(Throwable exception) {
		String message = null;
		if (getExceptionToMessage() != null) {
			message = getExceptionToMessage().get(exception.getClass());
			if (StringUtils.hasText(message)) {
				return message;
			}

			//map entry with a null value
			if (getExceptionToMessage().containsKey(exception.getClass())) {
				return exception.getMessage();
			}
		}

		if (isSendExceptionMessage()) {
			return exception.getMessage();
		}
		return getDefaultExceptionMessage();

	}
}
