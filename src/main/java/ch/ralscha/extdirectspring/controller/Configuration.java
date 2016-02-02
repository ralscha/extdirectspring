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
import ch.ralscha.extdirectspring.bean.ExtDirectStoreResult;
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

	private String apiNs = "Ext.app";

	private String actionNs = null;

	private String remotingApiVar = "REMOTING_API";

	private String pollingUrlsVar = "POLLING_URLS";

	private boolean fullRouterUrl = false;

	private String baseRouterUrl = null;

	private Integer timeout = null;

	private Integer maxRetries = null;

	private Object enableBuffer = null;

	private Integer bufferLimit = null;

	private boolean streamResponse = false;

	private String jsContentType = "application/javascript";

	private BatchedMethodsExecutionPolicy batchedMethodsExecutionPolicy = BatchedMethodsExecutionPolicy.SEQUENTIAL;

	private ExecutorService batchedMethodsExecutorService = null;

	private String providerType = "remoting";

	private String frameDomain = null;

	private String frameDomainScript = "<script type=\"text/javascript\">document.domain = '%s';</script>";

	private JsonHandler jsonHandler;

	private ConversionService conversionService;

	public String getDefaultExceptionMessage() {
		return this.defaultExceptionMessage;
	}

	/**
	 * Changes the default message when an exception occurred and there is no mapping
	 * found in {@link #getExceptionToMessage()} and {@link #isSendExceptionMessage()} is
	 * false.
	 * <p>
	 * Default value is "Server Error".
	 * <p>
	 * This value is set into {@link ExtDirectResponse#setMessage(String)} and sent to the
	 * client.
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
		return this.sendExceptionMessage;
	}

	/**
	 * Changes the way {@link ExtDirectResponse#setMessage(String)} is called.
	 * <p>
	 * If this flag is set to true and an exception occurred instead of
	 * {@link #getDefaultExceptionMessage()} {@link Throwable#getMessage()} is put into
	 * the message field of the response. Only if there is no mapping found in
	 * {@link #getExceptionToMessage()}.
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
		return this.sendStacktrace;
	}

	/**
	 * If sendStacktrace is true, the library sends, in case of an exception, the full
	 * stacktrace in {@link BaseResponse#setWhere(String)} back to the client.
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
		return this.exceptionToMessage;
	}

	/**
	 * Sets the new exception-to-message map.
	 * <p>
	 * If there is a mapping for the exception in {@link #getExceptionToMessage()} and the
	 * value is not null put this value in {@link ExtDirectResponse#setMessage(String)}.
	 * <p>
	 * If there is a mapping for the exception in {@link #getExceptionToMessage()} and the
	 * value is null use {@link Throwable#getMessage()}.
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
		return this.alwaysWrapStoreResponse;
	}

	/**
	 * If alwaysWrapStoreResponse is true, responses of STORE_READ and STORE_MODIFY
	 * methods are always wrapped in an {@link ExtDirectStoreResult} object.
	 *
	 * @param alwaysWrapStoreResponse new flag
	 */
	public void setAlwaysWrapStoreResponse(boolean alwaysWrapStoreResponse) {
		this.alwaysWrapStoreResponse = alwaysWrapStoreResponse;
	}

	public boolean isSynchronizeOnSession() {
		return this.synchronizeOnSession;
	}

	/**
	 * If synchronizeOnSession is true, execution of all methods is synchronized on the
	 * session object. To serialize parallel invocations from the same client and to
	 * prevent concurrency issues if the server accesses global or session resources.
	 * <p>
	 * Instead of globally enable this it's possible to set the flag on a per method basis
	 * with {@link ExtDirectMethod#synchronizeOnSession()}.
	 *
	 * @param synchronizeOnSession new flag
	 */
	public void setSynchronizeOnSession(boolean synchronizeOnSession) {
		this.synchronizeOnSession = synchronizeOnSession;
	}

	public Integer getTimeout() {
		return this.timeout;
	}

	/**
	 * Sets the timeout in milliseconds for remote calls. This parameter is part of the
	 * configuration object api.js sends to the client and configures the timeout property
	 * of the <a href=
	 * "http://docs.sencha.com/extjs/6.0/6.0.0-classic/#!/api/Ext.direct.RemotingProvider"
	 * > RemotingProvider</a>.
	 *
	 * @param timeout new timeout value
	 */
	public void setTimeout(Integer timeout) {
		this.timeout = timeout;
	}

	public Integer getMaxRetries() {
		return this.maxRetries;
	}

	/**
	 * Sets the number of times the client will try to send a message to the server before
	 * throwing a failure. Default value is 1. This parameter is part of the configuration
	 * object api.js sends to the client and configures the maxRetries property of the
	 * <a href=
	 * "http://docs.sencha.com/extjs/6.0/6.0.0-classic/#!/api/Ext.direct.RemotingProvider"
	 * > RemotingProvider</a>.
	 *
	 * @param maxRetries new number of max retries
	 */
	public void setMaxRetries(Integer maxRetries) {
		this.maxRetries = maxRetries;
	}

	public Object getEnableBuffer() {
		return this.enableBuffer;
	}

	/**
	 * true or false to enable or disable combining of method calls. If a number is
	 * specified this is the amount of time in milliseconds to wait before sending a
	 * batched request. Calls which are received within the specified timeframe will be
	 * concatenated together and sent in a single request, optimizing the application by
	 * reducing the amount of round trips that have to be made to the server.
	 * <p>
	 * This parameter is part of the configuration object api.js sends to the client and
	 * configures the enableBuffer property of the <a href=
	 * "http://docs.sencha.com/extjs/6.0/6.0.0-classic/#!/api/Ext.direct.RemotingProvider"
	 * > RemotingProvider</a>.
	 * <p>
	 * Defaults to: 10
	 *
	 * @param enableBuffer new enableBuffer value
	 */
	public void setEnableBuffer(Object enableBuffer) {
		this.enableBuffer = enableBuffer;
	}

	public Integer getBufferLimit() {
		return this.bufferLimit;
	}

	/**
	 * The maximum number of requests to batch together. By default, an unlimited number
	 * of requests will be batched. This option will allow to wait only for a certain
	 * number of Direct method calls before dispatching a request to the server, even if
	 * {@link #enableBuffer} timeout has not yet expired.
	 * <p>
	 * Note that this option does nothing if {@link #enableBuffer} is set to `false`.
	 * <p>
	 * Defaults to: Number.MAX_VALUE
	 *
	 * @param bufferLimit new value for buffer limit
	 */
	@SuppressWarnings("javadoc")
	public void setBufferLimit(Integer bufferLimit) {
		this.bufferLimit = bufferLimit;
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
		String message;
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
		return this.streamResponse;
	}

	/**
	 * If streamResponse is true, the JSON response will be directly written into the
	 * {@link HttpServletResponse#getOutputStream()} without setting the Content-Length
	 * header. The old ExtDirectSpring 1.0.x behavior.
	 * <p>
	 * If false the {@link RouterController} writes the JSON into an internal buffer, sets
	 * the Content-Length header in {@link HttpServletResponse} and writes the buffer into
	 * {@link HttpServletResponse#getOutputStream()}.
	 * <p>
	 * Instead of globally enable this it's possible to set the flag on a per method basis
	 * with {@link ExtDirectMethod#streamResponse()}.
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
	 * Until version 1.2.1 extdirectspring sends "application/x-javascript". But according
	 * to <a href="http://www.rfc-editor.org/rfc/rfc4329.txt">RFC4329</a> the official
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
		return this.jsContentType;
	}

	public BatchedMethodsExecutionPolicy getBatchedMethodsExecutionPolicy() {
		return this.batchedMethodsExecutionPolicy;
	}

	/**
	 * Specifies how batched methods sent from the client should be executed on the
	 * server. {@link BatchedMethodsExecutionPolicy#SEQUENTIAL} executes methods one after
	 * the other. {@link BatchedMethodsExecutionPolicy#CONCURRENT} executes methods
	 * concurrently with the help of a thread pool.
	 *
	 * <p>
	 * Default value is {@link BatchedMethodsExecutionPolicy#SEQUENTIAL}
	 *
	 * @see #setBatchedMethodsExecutorService(ExecutorService)
	 * @param batchedMethodsExecutionPolicy new policy
	 */
	public void setBatchedMethodsExecutionPolicy(
			BatchedMethodsExecutionPolicy batchedMethodsExecutionPolicy) {
		Assert.notNull(batchedMethodsExecutionPolicy,
				"batchedMethodsExecutionPolicy must not be null");
		this.batchedMethodsExecutionPolicy = batchedMethodsExecutionPolicy;
	}

	public ExecutorService getBatchedMethodsExecutorService() {
		return this.batchedMethodsExecutorService;
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
	public void setBatchedMethodsExecutorService(
			ExecutorService batchedMethodsExecutorService) {
		this.batchedMethodsExecutorService = batchedMethodsExecutorService;
	}

	public String getProviderType() {
		return this.providerType;
	}

	/**
	 * Sets the type of the provider. The type is sent to the client in the api
	 * configuration.
	 * <p>
	 * Default value is "remoting" and it creates an Ext.direct.RemotingProvider on the
	 * client side.
	 *
	 * @param providerType new provider type
	 */
	public void setProviderType(String providerType) {
		this.providerType = providerType;
	}

	public String getFrameDomain() {
		return this.frameDomain;
	}

	/**
	 * Sets the passed domain to be included in the file upload's temporary frame. This is
	 * used to grant the main document access to the POST response on the frame in a
	 * cross-domain environment.
	 *
	 * @param frameDomain the new domain to set the frame to
	 */
	public void setFrameDomain(String frameDomain) {
		this.frameDomain = frameDomain;
	}

	public String getFrameDomainScript() {
		return this.frameDomainScript;
	}

	/**
	 * Updates the script that is used to set the domain values on the file upload frame.
	 * This is useful for cross-browser compatibility. If other browsers require a
	 * modified script as workaround, frameDomainScript should allow for it.
	 *
	 * @param frameDomainScript the javascript code used to set the frame domain
	 */
	public void setFrameDomainScript(String frameDomainScript) {
		this.frameDomainScript = frameDomainScript;
	}

	public String getApiNs() {
		return this.apiNs;
	}

	/**
	 * Sets the name of the namespace in which the remotingApiVar variable will reside.
	 * <p>
	 * Defaults to Ext.app
	 *
	 * @param apiNs new namespace
	 */
	public void setApiNs(String apiNs) {
		this.apiNs = apiNs;
	}

	public String getActionNs() {
		return this.actionNs;
	}

	/**
	 * Sets the name of the namespace in which the actions will reside.
	 * <p>
	 * Defaults to none
	 *
	 * @param actionNs new namespace
	 */
	public void setActionNs(String actionNs) {
		this.actionNs = actionNs;
	}

	public String getRemotingApiVar() {
		return this.remotingApiVar;
	}

	/**
	 * Changes the name of the remoting api variable.
	 * <p>
	 * Defaults to REMOTING_API
	 *
	 * @param remotingApiVar new remoting api varaible name
	 */
	public void setRemotingApiVar(String remotingApiVar) {
		this.remotingApiVar = remotingApiVar;
	}

	public String getPollingUrlsVar() {
		return this.pollingUrlsVar;
	}

	/**
	 * Changes the name of the polling urls object variable
	 * <p>
	 * Defaults to POLLING_URLS
	 *
	 * @param pollingUrlsVar new polling urls object variable name
	 */
	public void setPollingUrlsVar(String pollingUrlsVar) {
		this.pollingUrlsVar = pollingUrlsVar;
	}

	public boolean isFullRouterUrl() {
		return this.fullRouterUrl;
	}

	/**
	 * Specifies if the router property should contain the full URL including protocol,
	 * server name, port number, and server path (true) or only the server path (false)
	 * <p>
	 * Defaults to false
	 *
	 * @param fullRouterUrl new flag value
	 */
	public void setFullRouterUrl(boolean fullRouterUrl) {
		this.fullRouterUrl = fullRouterUrl;
	}

	public String getBaseRouterUrl() {
		return this.baseRouterUrl;
	}

	/**
	 * If not null the {@link ApiController} does not use the url of the request to
	 * determine the router url instead he uses the value of this variable as the base and
	 * adds /router and /poll.<br>
	 * The fullRouterUrl setting is ignored when this variable is not null
	 * <p>
	 * Defaults to null.
	 *
	 * @param baseRouterUrl new base router url
	 */
	public void setBaseRouterUrl(String baseRouterUrl) {
		this.baseRouterUrl = baseRouterUrl;
	}

	public ConversionService getConversionService() {
		return this.conversionService;
	}

	public void setConversionService(ConversionService conversionService) {
		this.conversionService = conversionService;
	}

	public JsonHandler getJsonHandler() {
		return this.jsonHandler;
	}

	public void setJsonHandler(JsonHandler jsonHandler) {
		this.jsonHandler = jsonHandler;
	}

}
