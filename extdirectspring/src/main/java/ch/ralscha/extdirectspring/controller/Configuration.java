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
package ch.ralscha.extdirectspring.controller;

import java.util.Map;

import org.springframework.util.StringUtils;

import ch.ralscha.extdirectspring.bean.BaseResponse;
import ch.ralscha.extdirectspring.bean.ExtDirectStoreResponse;

/**
 * Configuration class to configure different aspects of extdirectspring.
 * 
 * If there is a mapping for the exception in exceptionToMessage and the value
 * is not null send this value. If there is a mapping for the exception in
 * exceptionToMessage and the value is null send exception.getMessage(). If
 * there is no mapping and sendExceptionMessage is true send
 * exception.getMessage(). If there is no mapping and sendExceptionMessage is
 * false send defaultExceptionMessage.
 * 
 * 
 * 
 * 
 * 
 * 
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

	private Object enableBuffer = null;

	private boolean streamResponse = false;

	public String getDefaultExceptionMessage() {
		return defaultExceptionMessage;
	}

	public void setDefaultExceptionMessage(final String defaultExceptionMessage) {
		this.defaultExceptionMessage = defaultExceptionMessage;
	}

	public boolean isSendExceptionMessage() {
		return sendExceptionMessage;
	}

	public void setSendExceptionMessage(final boolean sendExceptionMessage) {
		this.sendExceptionMessage = sendExceptionMessage;
	}

	public boolean isSendStacktrace() {
		return sendStacktrace;
	}

	/**
	 * If sendStacktrace is true, the library sends the full stacktrace in
	 * {@link BaseResponse#setWhere(String)} back to the client in case of an
	 * exception. Should only set to true in development.
	 * @param sendStacktrace new flag
	 */
	public void setSendStacktrace(final boolean sendStacktrace) {
		this.sendStacktrace = sendStacktrace;
	}

	public Map<Class<?>, String> getExceptionToMessage() {
		return exceptionToMessage;
	}

	public void setExceptionToMessage(final Map<Class<?>, String> exceptionToMessage) {
		this.exceptionToMessage = exceptionToMessage;
	}

	public boolean isAlwaysWrapStoreResponse() {
		return alwaysWrapStoreResponse;
	}

	/**
	 * If alwaysWrapStoreResponse is true, responses of STORE_READ and
	 * STORE_MODIFY methods are always wrapped in an
	 * {@link ExtDirectStoreResponse} object
	 * @param alwaysWrapStoreResponse new flag
	 */
	public void setAlwaysWrapStoreResponse(final boolean alwaysWrapStoreResponse) {
		this.alwaysWrapStoreResponse = alwaysWrapStoreResponse;
	}

	public boolean isSynchronizeOnSession() {
		return synchronizeOnSession;
	}

	/**
	 * If synchronizeOnSession is true, execution of all methods is synchronized
	 * on the session, to serialize parallel invocations from the same client.
	 * 
	 * @param synchronizeOnSession new flag
	 */
	public void setSynchronizeOnSession(final boolean synchronizeOnSession) {
		this.synchronizeOnSession = synchronizeOnSession;
	}

	public Integer getTimeout() {
		return timeout;
	}

	public void setTimeout(final Integer timeout) {
		this.timeout = timeout;
	}

	public Integer getMaxRetries() {
		return maxRetries;
	}

	public void setMaxRetries(final Integer maxRetries) {
		this.maxRetries = maxRetries;
	}

	public Object getEnableBuffer() {
		return enableBuffer;
	}

	public void setEnableBuffer(final Object enableBuffer) {
		this.enableBuffer = enableBuffer;
	}

	public String getMessage(final Throwable exception) {
		String message = null;
		if (getExceptionToMessage() != null) {
			message = getExceptionToMessage().get(exception.getClass());
			if (StringUtils.hasText(message)) {
				return message;
			}

			// map entry with a null value
			if (getExceptionToMessage().containsKey(exception.getClass())) {
				return exception.getMessage();
			}
		}

		if (isSendExceptionMessage()) {
			return exception.getMessage();
		}
		return getDefaultExceptionMessage();

	}

	public boolean isStreamResponse() {
		return streamResponse;
	}

	public void setStreamResponse(final boolean streamResponse) {
		this.streamResponse = streamResponse;
	}
}
