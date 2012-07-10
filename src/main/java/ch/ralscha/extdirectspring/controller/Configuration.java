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

import javax.servlet.http.HttpServletResponse;

import org.springframework.util.StringUtils;

import ch.ralscha.extdirectspring.annotation.ExtDirectMethod;
import ch.ralscha.extdirectspring.bean.BaseResponse;
import ch.ralscha.extdirectspring.bean.ExtDirectResponse;
import ch.ralscha.extdirectspring.bean.ExtDirectStoreResponse;

/**
 * Configuration class to configure different aspects of extdirectspring.
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

	/**
	 * Changes the default message when an exception occurred and there is no
	 * mapping found in {@link #getExceptionToMessage()} and
	 * {@link #isSendExceptionMessage()} is false.
	 * <p>
	 * Default value is "Server Error".
	 * <p>
	 * This value is set into {@link ExtDirectResponse#setMessage(String)} and
	 * sent to the client.
	 * 
	 * @see #setExceptionToMessage(Map)
	 * @see #setDefaultExceptionMessage(String)
	 * 
	 * @param defaultExceptionMessage new default exception message
	 */
	public void setDefaultExceptionMessage(String defaultExceptionMessage) {
		this.defaultExceptionMessage = defaultExceptionMessage;
	}

	public boolean isSendExceptionMessage() {
		return sendExceptionMessage;
	}

	/**
	 * Changes the way {@link ExtDirectResponse#setMessage(String)} is called.
	 * <p>
	 * If this flag is set to true and an exception occurred instead of
	 * {@link #getDefaultExceptionMessage()} {@link Throwable#getMessage()} is
	 * put into the message field of the response. Only if there is no mapping
	 * found in {@link #getExceptionToMessage()}.
	 * <p>
	 * Default value is false.
	 * 
	 * @see #setExceptionToMessage(Map)
	 * @see #setDefaultExceptionMessage(String)
	 * 
	 * @param sendExceptionMessage new flag
	 */
	public void setSendExceptionMessage(boolean sendExceptionMessage) {
		this.sendExceptionMessage = sendExceptionMessage;
	}

	public boolean isSendStacktrace() {
		return sendStacktrace;
	}

	/**
	 * If sendStacktrace is true, the library sends, in case of an exception,
	 * the full stacktrace in {@link BaseResponse#setWhere(String)} back to the
	 * client.
	 * <p>
	 * Should only set to true in development.
	 * <p>
	 * Default value is false
	 * 
	 * @param sendStacktrace new flag
	 */
	public void setSendStacktrace(boolean sendStacktrace) {
		this.sendStacktrace = sendStacktrace;
	}

	public Map<Class<?>, String> getExceptionToMessage() {
		return exceptionToMessage;
	}

	/**
	 * Sets the new exception-to-message map.
	 * <p>
	 * If there is a mapping for the exception in
	 * {@link #getExceptionToMessage()} and the value is not null put this value
	 * in {@link ExtDirectResponse#setMessage(String)}.
	 * <p>
	 * If there is a mapping for the exception in
	 * {@link #getExceptionToMessage()} and the value is null use
	 * {@link Throwable#getMessage()}.
	 * <p>
	 * If there is no mapping and {@link #isSendExceptionMessage()} is true use
	 * {@link Throwable#getMessage()}.
	 * <p>
	 * If there is no mapping and {@link #isSendExceptionMessage()} is false use
	 * {@link #getDefaultExceptionMessage()}.
	 * 
	 * @see #setDefaultExceptionMessage(String)
	 * @see #setSendExceptionMessage(boolean)
	 * 
	 * @param exceptionToMessage new mapping from exception to message
	 */
	public void setExceptionToMessage(Map<Class<?>, String> exceptionToMessage) {
		this.exceptionToMessage = exceptionToMessage;
	}

	public boolean isAlwaysWrapStoreResponse() {
		return alwaysWrapStoreResponse;
	}

	/**
	 * If alwaysWrapStoreResponse is true, responses of STORE_READ and
	 * STORE_MODIFY methods are always wrapped in an
	 * {@link ExtDirectStoreResponse} object.
	 * 
	 * @param alwaysWrapStoreResponse new flag
	 */
	public void setAlwaysWrapStoreResponse(boolean alwaysWrapStoreResponse) {
		this.alwaysWrapStoreResponse = alwaysWrapStoreResponse;
	}

	public boolean isSynchronizeOnSession() {
		return synchronizeOnSession;
	}

	/**
	 * If synchronizeOnSession is true, execution of all methods is synchronized
	 * on the session object. To serialize parallel invocations from the same
	 * client and to prevent concurrency issues if the server accesses global or
	 * session resources.
	 * <p>
	 * Instead of globally enable this it's possible to set the flag on a per
	 * method basis with {@link ExtDirectMethod#synchronizeOnSession()}.
	 * 
	 * @param synchronizeOnSession new flag
	 */
	public void setSynchronizeOnSession(boolean synchronizeOnSession) {
		this.synchronizeOnSession = synchronizeOnSession;
	}

	public Integer getTimeout() {
		return timeout;
	}

	/**
	 * Sets the timeout in milliseconds for remote calls. This parameter is part
	 * of the configuration object api.js sends to the client and configures the
	 * timeout property of the <a href=
	 * "http://docs.sencha.com/ext-js/4-1/#!/api/Ext.direct.RemotingProvider"
	 * >RemotingProvider</a>.
	 * 
	 * @param timeout new timeout value
	 */
	public void setTimeout(Integer timeout) {
		this.timeout = timeout;
	}

	public Integer getMaxRetries() {
		return maxRetries;
	}

	/**
	 * Sets the number of times the client will try to send a message to the
	 * server before throwing a failure. Default value is 1. This parameter is
	 * part of the configuration object api.js sends to the client and
	 * configures the maxRetries property of the <a href=
	 * "http://docs.sencha.com/ext-js/4-1/#!/api/Ext.direct.RemotingProvider"
	 * >RemotingProvider</a>.
	 * 
	 * @param maxRetries new number of max retries
	 */
	public void setMaxRetries(Integer maxRetries) {
		this.maxRetries = maxRetries;
	}

	public Object getEnableBuffer() {
		return enableBuffer;
	}

	/**
	 * true or false to enable or disable combining of method calls. If a number
	 * is specified this is the amount of time in milliseconds to wait before
	 * sending a batched request. Calls which are received within the specified
	 * timeframe will be concatenated together and sent in a single request,
	 * optimizing the application by reducing the amount of round trips that
	 * have to be made to the server.
	 * <p>
	 * This parameter is part of the configuration object api.js sends to the
	 * client and configures the enableBuffer property of the <a href=
	 * "http://docs.sencha.com/ext-js/4-1/#!/api/Ext.direct.RemotingProvider"
	 * >RemotingProvider</a>.
	 * <p>
	 * Defaults to: 10
	 * 
	 * @param enableBuffer new enableBuffer value
	 */
	public void setEnableBuffer(Object enableBuffer) {
		this.enableBuffer = enableBuffer;
	}

	/**
	 * Returns an error message for the supplied exception and based on this
	 * configuration.
	 * 
	 * @see #setDefaultExceptionMessage(String)
	 * @see #setSendExceptionMessage(boolean)
	 * @see #setExceptionToMessage(Map)
	 * 
	 * @param exception the thrown exception
	 * @return exception message
	 */
	public String getMessage(Throwable exception) {
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

	/**
	 * If streamResponse is true, the JSON response will be directly written
	 * into the {@link HttpServletResponse#getOutputStream()} without setting
	 * the Content-Length header. The old ExtDirectSpring 1.0.x behavior.
	 * <p>
	 * If false the {@link RouterController} writes the JSON into an internal
	 * buffer, sets the Content-Length header in {@link HttpServletResponse} and
	 * writes the buffer into {@link HttpServletResponse#getOutputStream()}.
	 * <p>
	 * Instead of globally enable this it's possible to set the flag on a per
	 * method basis with {@link ExtDirectMethod#streamResponse()}.
	 * <p>
	 * Default value is false
	 * 
	 * @param streamResponse new flag
	 */
	public void setStreamResponse(boolean streamResponse) {
		this.streamResponse = streamResponse;
	}
}
