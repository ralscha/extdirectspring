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

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.Executors;

import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.core.convert.ConversionService;
import org.springframework.format.support.DefaultFormattingConversionService;
import org.springframework.format.support.FormattingConversionService;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.support.WebArgumentResolver;

import ch.ralscha.extdirectspring.util.JsonHandler;
import ch.ralscha.extdirectspring.util.ParametersResolver;

@Service
public class ConfigurationService implements InitializingBean, DisposableBean {

	@Autowired
	private ApplicationContext context;

	@Autowired(required = false)
	private Configuration configuration;

	@Autowired(required = false)
	private JsonHandler jsonHandler;

	@Autowired(required = false)
	private RouterExceptionHandler routerExceptionHandler;

	private ParametersResolver parametersResolver;

	@Override
	public void afterPropertiesSet() {

		if (this.configuration == null) {
			this.configuration = new Configuration();
		}

		if (this.configuration.getJsonHandler() != null) {
			this.jsonHandler = this.configuration.getJsonHandler();
		}

		if (this.jsonHandler == null) {
			this.jsonHandler = new JsonHandler();
		}

		if (this.routerExceptionHandler == null) {
			this.routerExceptionHandler = new DefaultRouterExceptionHandler(this);
		}

		if (this.configuration
				.getBatchedMethodsExecutionPolicy() == BatchedMethodsExecutionPolicy.CONCURRENT
				&& this.configuration.getBatchedMethodsExecutorService() == null) {
			this.configuration
					.setBatchedMethodsExecutorService(Executors.newFixedThreadPool(5));
		}

		if (this.configuration.getConversionService() == null) {
			Map<String, ConversionService> conversionServices = this.context
					.getBeansOfType(ConversionService.class);
			if (conversionServices.isEmpty()) {
				this.configuration
						.setConversionService(new DefaultFormattingConversionService());
			}
			else if (conversionServices.size() == 1) {
				this.configuration.setConversionService(
						conversionServices.values().iterator().next());
			}
			else {
				if (conversionServices.containsKey("mvcConversionService")) {
					this.configuration.setConversionService(
							conversionServices.get("mvcConversionService"));
				}
				else {
					for (ConversionService conversionService : conversionServices
							.values()) {
						if (conversionService instanceof FormattingConversionService) {
							this.configuration.setConversionService(conversionService);
							break;
						}
					}
					if (this.configuration.getConversionService() == null) {
						this.configuration.setConversionService(
								conversionServices.values().iterator().next());
					}
				}
			}
		}

		Collection<WebArgumentResolver> webResolvers = this.context
				.getBeansOfType(WebArgumentResolver.class).values();
		this.parametersResolver = new ParametersResolver(
				this.configuration.getConversionService(), this.jsonHandler,
				webResolvers);
	}

	@Override
	public void destroy() throws Exception {
		if (this.configuration.getBatchedMethodsExecutorService() != null) {
			this.configuration.getBatchedMethodsExecutorService().shutdown();
		}
	}

	public Configuration getConfiguration() {
		return this.configuration;
	}

	public JsonHandler getJsonHandler() {
		return this.jsonHandler;
	}

	public ApplicationContext getApplicationContext() {
		return this.context;
	}

	public ParametersResolver getParametersResolver() {
		return this.parametersResolver;
	}

	public RouterExceptionHandler getRouterExceptionHandler() {
		return this.routerExceptionHandler;
	}

}
