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

import java.lang.reflect.Method;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.core.MethodIntrospector;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.ClassUtils;
import org.springframework.util.ReflectionUtils.MethodFilter;
import org.springframework.util.StringUtils;

import ch.ralscha.extdirectspring.annotation.ExtDirectMethod;
import ch.ralscha.extdirectspring.util.MethodInfoCache;

/**
 * Spring application listener that listens for ContextRefreshedEvent events. If such an
 * event is received the listener will scan for ExtDirectMethod annotated methods in the
 * current ApplicationContext. Found methods will be cached in the {@link MethodInfoCache}
 * . The class also reports warnings and errors of misconfigured methods.
 */
@Service
public class MethodRegistrar implements ApplicationListener<ContextRefreshedEvent>, Ordered {

	private static final Log log = LogFactory.getLog(MethodRegistrar.class);

	private final MethodInfoCache methodInfoCache;

	@Autowired
	public MethodRegistrar(MethodInfoCache methodInfoCache) {
		this.methodInfoCache = methodInfoCache;
	}

	@Override
	public void onApplicationEvent(ContextRefreshedEvent event) {
		ApplicationContext context = (ApplicationContext) event.getSource();
		String[] beanNames = context.getBeanNamesForType(Object.class);
		int totalBeans = beanNames.length;
		int totalMethods = 0;
		int registeredMethods = 0;
		for (String beanName : beanNames) {
			try {
				Class<?> handlerType = context.getType(beanName);
				final Class<?> userType = ClassUtils.getUserClass(handlerType);
				Set<Method> methods = MethodIntrospector.selectMethods(userType,
						(MethodFilter) method -> AnnotationUtils.findAnnotation(method, ExtDirectMethod.class) != null);
				totalMethods += methods.size();
				for (Method method : methods) {
					ExtDirectMethod directMethodAnnotation = AnnotationUtils.findAnnotation(method, ExtDirectMethod.class);
					final String beanAndMethodName = beanName + "." + method.getName();
					if (directMethodAnnotation.value().isValid(beanAndMethodName, userType, method)) {
						this.methodInfoCache.put(beanName, handlerType, method, event.getApplicationContext());
						registeredMethods++;
						if (log.isDebugEnabled()) {
							String info = "Register " + beanAndMethodName + "(" + directMethodAnnotation.value();
							if (StringUtils.hasText(directMethodAnnotation.group())) {
								info += ", " + directMethodAnnotation.group();
							}
							info += ")";
							log.debug(info);
						}
					}
				}
			} catch (Exception e) {
				log.error("Exception while registering methods for bean: " + beanName, e);
			}
		}
		log.info("MethodRegistrar: total beans scanned=" + totalBeans + ", total methods found=" + totalMethods + ", methods registered=" + registeredMethods);
		if (registeredMethods == 0) {
			log.warn("No ExtDirect methods registered. MethodInfoCache may be empty.");
		}
	}

	@Override
	public int getOrder() {
		return Ordered.LOWEST_PRECEDENCE - 1000;
	}

}
