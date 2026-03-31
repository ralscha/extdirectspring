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

import java.util.concurrent.ExecutorService;

import org.junit.jupiter.api.Test;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.support.DefaultConversionService;
import org.springframework.format.support.DefaultFormattingConversionService;
import org.springframework.format.support.FormattingConversionService;
import org.springframework.test.util.ReflectionTestUtils;

import ch.ralscha.extdirectspring.util.JsonHandler;

class ConfigurationServiceTest {

	@Test
	void afterPropertiesSetCreatesDefaultsWhenConfigurationIsMissing() throws Exception {
		try (GenericApplicationContext context = new GenericApplicationContext()) {
			context.refresh();

			ConfigurationService service = new ConfigurationService();
			ReflectionTestUtils.setField(service, "context", context);

			service.afterPropertiesSet();

			assertThat(service.getConfiguration()).isNotNull();
			assertThat(service.getJsonHandler()).isNotNull();
			assertThat(service.getRouterExceptionHandler()).isInstanceOf(DefaultRouterExceptionHandler.class);
			assertThat(service.getParametersResolver()).isNotNull();
			assertThat(service.getConfiguration().getConversionService())
				.isInstanceOf(DefaultFormattingConversionService.class);
		}
	}

	@Test
	void afterPropertiesSetUsesConfigurationJsonHandlerAndCreatesConcurrentExecutor() throws Exception {
		try (GenericApplicationContext context = new GenericApplicationContext()) {
			context.refresh();

			Configuration config = new Configuration();
			JsonHandler customJsonHandler = new JsonHandler();
			config.setJsonHandler(customJsonHandler);
			config.setBatchedMethodsExecutionPolicy(BatchedMethodsExecutionPolicy.CONCURRENT);

			ConfigurationService service = new ConfigurationService();
			ReflectionTestUtils.setField(service, "context", context);
			ReflectionTestUtils.setField(service, "configuration", config);
			ReflectionTestUtils.setField(service, "jsonHandler", new JsonHandler());

			service.afterPropertiesSet();

			ExecutorService executorService = service.getConfiguration().getBatchedMethodsExecutorService();
			assertThat(service.getJsonHandler()).isSameAs(customJsonHandler);
			assertThat(executorService).isNotNull();

			service.destroy();
			assertThat(executorService.isShutdown()).isTrue();
		}
	}

	@Test
	void afterPropertiesSetPrefersMvcConversionServiceAndKeepsInjectedExceptionHandler() throws Exception {
		try (GenericApplicationContext context = new GenericApplicationContext()) {
			ConversionService fallbackConversionService = new DefaultConversionService();
			FormattingConversionService mvcConversionService = new DefaultFormattingConversionService();
			context.registerBean("fallbackConversionService", ConversionService.class, () -> fallbackConversionService);
			context.registerBean("mvcConversionService", ConversionService.class, () -> mvcConversionService);
			context.refresh();

			Configuration config = new Configuration();
			RouterExceptionHandler customExceptionHandler = (methodInfo, response, e, request) -> "custom";

			ConfigurationService service = new ConfigurationService();
			ReflectionTestUtils.setField(service, "context", context);
			ReflectionTestUtils.setField(service, "configuration", config);
			ReflectionTestUtils.setField(service, "routerExceptionHandler", customExceptionHandler);

			service.afterPropertiesSet();

			assertThat(service.getRouterExceptionHandler()).isSameAs(customExceptionHandler);
			assertThat(service.getConfiguration().getConversionService()).isSameAs(mvcConversionService);
		}
	}

	@Test
	void afterPropertiesSetPrefersFormattingConversionServiceWhenNoMvcBeanExists() throws Exception {
		try (GenericApplicationContext context = new GenericApplicationContext()) {
			ConversionService plainConversionService = new DefaultConversionService();
			FormattingConversionService formattingConversionService = new DefaultFormattingConversionService();
			context.registerBean("plainConversionService", ConversionService.class, () -> plainConversionService);
			context.registerBean("formattingConversionService", ConversionService.class,
					() -> formattingConversionService);
			context.refresh();

			Configuration config = new Configuration();

			ConfigurationService service = new ConfigurationService();
			ReflectionTestUtils.setField(service, "context", context);
			ReflectionTestUtils.setField(service, "configuration", config);

			service.afterPropertiesSet();

			assertThat(service.getConfiguration().getConversionService()).isSameAs(formattingConversionService);
		}
	}

}