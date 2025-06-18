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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Service;

import ch.ralscha.extdirectspring.util.MethodInfoCache;

/**
 * Spring application listener that listens for ContextRefreshedEvent events. If such an
 * event is received the listener will scan for ExtDirectMethod annotated methods in the
 * current ApplicationContext. Found methods will be cached in the {@link MethodInfoCache}
 * . The class also reports warnings and errors of misconfigured methods.
 */
@Service
public class MethodRegistrar implements ApplicationListener<ContextRefreshedEvent>, Ordered {


	private final MethodInfoCache methodInfoCache;

	@Autowired
	public MethodRegistrar(MethodInfoCache methodInfoCache) {
		this.methodInfoCache = methodInfoCache;
	}

	@Override
	public void onApplicationEvent(ContextRefreshedEvent event) {
		ApplicationContext context = (ApplicationContext) event.getSource();
		this.methodInfoCache.populateMethodInfoCache(context);
	}
	
	@Override
	public int getOrder() {
		return Ordered.LOWEST_PRECEDENCE - 1000;
	}

}
