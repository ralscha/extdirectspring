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

import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.servlet.http.HttpServletResponse;

import org.springframework.core.convert.ConversionService;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import ch.ralscha.extdirectspring.annotation.ExtDirectMethod;
import ch.ralscha.extdirectspring.bean.BaseResponse;
import ch.ralscha.extdirectspring.bean.ExtDirectResponse;
import ch.ralscha.extdirectspring.bean.ExtDirectStoreReadResult;
import ch.ralscha.extdirectspring.util.JsonHandler;

/**
 * Configuration class to configure different aspects of extdirectspring.
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

	private String jsContentType = "application/javascript";

	private BatchedMethodsExecutionPolicy batchedMethodsExecutionPolicy = BatchedMethodsExecutionPolicy.SEQUENTIAL;

	private ExecutorService batchedMethodsExecutorService = null;

	private String providerType = "remoting";

	private JsonHandler jsonHandler;

	private ConversionService conversionService;

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
	 * {@link ExtDirectStoreReadResult} object.
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
	 * "http://docs.sencha.com/ext-js/4-2/#!/api/Ext.direct.RemotingProvider"
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
	 * "http://docs.sencha.com/ext-js/4-2/#!/api/Ext.direct.RemotingProvider"
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
	 * "http://docs.sencha.com/ext-js/4-2/#!/api/Ext.direct.RemotingProvider"
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

	/**
	 * Specifies the Content-Type for api.js and api-debug.js.
	 * <p>
	 * Until version 1.2.1 extdirectspring sends "application/x-javascript". But
	 * according to <a
	 * href="http://www.rfc-editor.org/rfc/rfc4329.txt">RFC4329</a> the official
	 * mime type is 'application/javascript'.
	 * <p>
	 * Default value is "application/javascript"
	 * 
	 * @param jsContentType new Content-type
	 */
	public void setJsContentType(String jsContentType) {
		this.jsContentType = jsContentType;
	}

	public String getJsContentType() {
		return jsContentType;
	}

	public BatchedMethodsExecutionPolicy getBatchedMethodsExecutionPolicy() {
		return batchedMethodsExecutionPolicy;
	}

	/**
	 * Specifies how batched methods sent from the client should be executed on
	 * the server. {@link BatchedMethodsExecutionPolicy#SEQUENTIAL} executes
	 * methods one after the other.
	 * {@link BatchedMethodsExecutionPolicy#CONCURRENT} executes methods
	 * concurrently with the help of a thread pool.
	 * 
	 * <p>
	 * Default value is {@link BatchedMethodsExecutionPolicy#SEQUENTIAL}
	 * 
	 * @see #setBatchedMethodsExecutorService(ExecutorService)
	 * @param batchedMethodsExecutionPolicy new policy
	 */
	public void setBatchedMethodsExecutionPolicy(BatchedMethodsExecutionPolicy batchedMethodsExecutionPolicy) {
		Assert.notNull(batchedMethodsExecutionPolicy, "batchedMethodsExecutionPolicy must not be null");
		this.batchedMethodsExecutionPolicy = batchedMethodsExecutionPolicy;
	}

	public ExecutorService getBatchedMethodsExecutorService() {
		return batchedMethodsExecutorService;
	}

	/**
	 * Sets the thread pool used for executing batched methods concurrently.
	 * <p>
	 * If batchedMethodsExecutionPolicy is set to
	 * {@link BatchedMethodsExecutionPolicy#CONCURRENT} but no
	 * batchedMethodsExecutorService is specified the library creates a
	 * {@link Executors#newFixedThreadPool(int)} with 5 threads.
	 * 
	 * @see #setBatchedMethodsExecutionPolicy(BatchedMethodsExecutionPolicy)
	 * @param batchedMethodsExecutorService the new thread pool
	 */
	public void setBatchedMethodsExecutorService(ExecutorService batchedMethodsExecutorService) {
		this.batchedMethodsExecutorService = batchedMethodsExecutorService;
	}

	public String getProviderType() {
		return providerType;
	}

	/**
	 * Sets the type of the provider. The type is sent to the client in the api
	 * configuration.
	 * <p>
	 * Default value is "remoting" and it creates an Ext.direct.RemotingProvider
	 * on the client side.
	 * 
	 * @param providerType new provider type
	 */
	public void setProviderType(String providerType) {
		this.providerType = providerType;
	}

	public ConversionService getConversionService() {
		return conversionService;
	}

	public void setConversionService(ConversionService conversionService) {
		this.conversionService = conversionService;
	}

	public JsonHandler getJsonHandler() {
		return jsonHandler;
	}

	public void setJsonHandler(JsonHandler jsonHandler) {
		this.jsonHandler = jsonHandler;
	}

}
