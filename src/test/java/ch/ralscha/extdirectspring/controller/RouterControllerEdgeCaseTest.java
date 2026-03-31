/*
 * Copyright the original author or authors.
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

import static org.assertj.core.api.Assertions.assertThat;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;

import org.junit.jupiter.api.Test;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.util.ReflectionTestUtils;

import ch.ralscha.extdirectspring.bean.ExtDirectRequest;
import ch.ralscha.extdirectspring.bean.ExtDirectResponse;
import ch.ralscha.extdirectspring.provider.RemoteProviderSimple;
import ch.ralscha.extdirectspring.util.MethodInfoCache;

class RouterControllerEdgeCaseTest {

	@Test
	void handleMethodCallNotifiesListenersBeforeAndAfterWhenInvocationFails() throws Exception {
		try (GenericApplicationContext context = createContext()) {
			ConfigurationService configurationService = createConfigurationService(context, new Configuration());
			MethodInfoCache methodInfoCache = createMethodInfoCache(context);
			RouterController controller = new RouterController(null, configurationService, methodInfoCache);

			RecordingExtRequestListener listener = new RecordingExtRequestListener();
			ReflectionTestUtils.setField(controller, "extRequestListeners", Set.of(listener));

			MockHttpServletRequest request = new MockHttpServletRequest();
			request.setMethod("POST");
			MockHttpServletResponse response = new MockHttpServletResponse();

			ExtDirectRequest directRequest = createDirectRequest("remoteProviderSimple", "method11", 1, null);

			ExtDirectResponse directResponse = controller.handleMethodCall(directRequest, request, response,
					Locale.ENGLISH);

			assertThat(directResponse.getType()).isEqualTo("exception");
			assertThat(directResponse.getMessage()).isEqualTo("Server Error");
			assertThat(listener.beforeMethods).containsExactly("method11");
			assertThat(listener.afterMethods).containsExactly("method11");
			assertThat(listener.afterResponseTypes).containsExactly("exception");
		}
	}

	@Test
	void routerProcessesConcurrentBatchRequestsAndInvokesListenersForEachCall() throws Exception {
		try (GenericApplicationContext context = createContext()) {
			Configuration configuration = new Configuration();
			configuration.setBatchedMethodsExecutionPolicy(BatchedMethodsExecutionPolicy.CONCURRENT);
			ConfigurationService configurationService = createConfigurationService(context, configuration);
			MethodInfoCache methodInfoCache = createMethodInfoCache(context);
			RouterController controller = new RouterController(null, configurationService, methodInfoCache);

			RecordingExtRequestListener listener = new RecordingExtRequestListener();
			ReflectionTestUtils.setField(controller, "extRequestListeners", Set.of(listener));

			List<ExtDirectRequest> directRequests = new ArrayList<>();
			directRequests.add(createDirectRequest("remoteProviderSimple", "method6", 10, List.of(1, 2)));
			directRequests.add(createDirectRequest("remoteProviderSimple", "method6", 11, List.of(3, 4)));

			MockHttpServletRequest request = new MockHttpServletRequest();
			request.setMethod("POST");
			request.setContentType(MediaType.APPLICATION_JSON_VALUE);
			request.setContent(configurationService.getJsonHandler().getMapper().writeValueAsBytes(directRequests));
			MockHttpServletResponse response = new MockHttpServletResponse();

			controller.router(request, response, Locale.ENGLISH);

			List<ExtDirectResponse> directResponses = ControllerUtil
				.readDirectResponses(response.getContentAsByteArray());

			assertThat(response.getContentType()).startsWith("application/json");
			assertThat(new String(response.getContentAsByteArray(), StandardCharsets.UTF_8)).contains("\"result\":3")
				.contains("\"result\":7");
			assertThat(directResponses).hasSize(2);
			assertThat(directResponses).extracting(ExtDirectResponse::getResult).containsExactly(3, 7);
			assertThat(listener.beforeMethods).containsExactlyInAnyOrder("method6", "method6");
			assertThat(listener.afterMethods).containsExactlyInAnyOrder("method6", "method6");
			assertThat(listener.afterResponseTypes).containsExactlyInAnyOrder("rpc", "rpc");
		}
	}

	private static GenericApplicationContext createContext() {
		GenericApplicationContext context = new GenericApplicationContext();
		context.registerBean("remoteProviderSimple", RemoteProviderSimple.class, RemoteProviderSimple::new);
		context.refresh();
		return context;
	}

	private static ConfigurationService createConfigurationService(GenericApplicationContext context,
			Configuration configuration) throws Exception {
		ConfigurationService configurationService = new ConfigurationService();
		ReflectionTestUtils.setField(configurationService, "context", context);
		ReflectionTestUtils.setField(configurationService, "configuration", configuration);
		configurationService.afterPropertiesSet();
		return configurationService;
	}

	private static MethodInfoCache createMethodInfoCache(GenericApplicationContext context) {
		MethodInfoCache methodInfoCache = new MethodInfoCache();
		methodInfoCache.populateMethodInfoCache(context);
		return methodInfoCache;
	}

	private static ExtDirectRequest createDirectRequest(String action, String method, int tid, Object data) {
		ExtDirectRequest directRequest = new ExtDirectRequest();
		directRequest.setAction(action);
		directRequest.setMethod(method);
		directRequest.setTid(tid);
		directRequest.setType("rpc");
		directRequest.setData(data);
		return directRequest;
	}

	private static final class RecordingExtRequestListener implements ExtRequestListener {

		private final List<String> beforeMethods = new CopyOnWriteArrayList<>();

		private final List<String> afterMethods = new CopyOnWriteArrayList<>();

		private final List<String> afterResponseTypes = new CopyOnWriteArrayList<>();

		@Override
		public void beforeRequest(ExtDirectRequest directRequest, ExtDirectResponse directResponse,
				jakarta.servlet.http.HttpServletRequest request, jakarta.servlet.http.HttpServletResponse response,
				Locale locale) {
			this.beforeMethods.add(directRequest.getMethod());
		}

		@Override
		public void afterRequest(ExtDirectRequest directRequest, ExtDirectResponse directResponse,
				jakarta.servlet.http.HttpServletRequest request, jakarta.servlet.http.HttpServletResponse response,
				Locale locale) {
			this.afterMethods.add(directRequest.getMethod());
			this.afterResponseTypes.add(directResponse.getType());
		}

	}

}