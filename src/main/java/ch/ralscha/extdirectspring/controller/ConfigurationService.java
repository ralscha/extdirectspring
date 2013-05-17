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
import java.util.concurrent.Executors;

import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.core.convert.ConversionService;
import org.springframework.format.support.DefaultFormattingConversionService;
import org.springframework.format.support.FormattingConversionService;
import org.springframework.stereotype.Service;

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

	private ParametersResolver parametersResolver;

	@Override
	public void afterPropertiesSet() {

		if (configuration == null) {
			configuration = new Configuration();
		}

		if (configuration.getJsonHandler() != null) {
			jsonHandler = configuration.getJsonHandler();
		}

		if (jsonHandler == null) {
			jsonHandler = new JsonHandler();
		}

		if (configuration.getBatchedMethodsExecutionPolicy() == BatchedMethodsExecutionPolicy.CONCURRENT
				&& configuration.getBatchedMethodsExecutorService() == null) {
			configuration.setBatchedMethodsExecutorService(Executors.newFixedThreadPool(5));
		}

		if (configuration.getConversionService() == null) {
			Map<String, ConversionService> conversionServices = context.getBeansOfType(ConversionService.class);
			if (conversionServices.isEmpty()) {
				configuration.setConversionService(new DefaultFormattingConversionService());
			} else if (conversionServices.size() == 1) {
				configuration.setConversionService(conversionServices.values().iterator().next());
			} else {
				if (conversionServices.containsKey("mvcConversionService")) {
					configuration.setConversionService(conversionServices.get("mvcConversionService"));
				} else {
					for (ConversionService conversionService : conversionServices.values()) {
						if (conversionService instanceof FormattingConversionService) {
							configuration.setConversionService(conversionService);
							break;
						}
					}
					if (configuration.getConversionService() == null) {
						configuration.setConversionService(conversionServices.values().iterator().next());
					}
				}
			}
		}

		parametersResolver = new ParametersResolver(configuration.getConversionService(), jsonHandler);

	}

	@Override
	public void destroy() throws Exception {
		if (configuration.getBatchedMethodsExecutorService() != null) {
			configuration.getBatchedMethodsExecutorService().shutdown();
		}
	}

	public Configuration getConfiguration() {
		return configuration;
	}

	public JsonHandler getJsonHandler() {
		return jsonHandler;
	}

	public ApplicationContext getApplicationContext() {
		return context;
	}

	public ParametersResolver getParametersResolver() {
		return parametersResolver;
	}

}
