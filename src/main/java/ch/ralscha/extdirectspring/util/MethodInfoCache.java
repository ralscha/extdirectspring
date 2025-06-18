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
package ch.ralscha.extdirectspring.util;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.core.MethodIntrospector;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.ClassUtils;
import org.springframework.util.StringUtils;
import org.springframework.util.ReflectionUtils.MethodFilter;

import ch.ralscha.extdirectspring.annotation.ExtDirectMethod;
import ch.ralscha.extdirectspring.controller.RouterController;

/**
 * A simple cache for methods with key beanName/methodName
 */
@Service
public class MethodInfoCache implements Iterable<Map.Entry<MethodInfoCache.Key, MethodInfo>> {
	private static final Log log = LogFactory.getLog(MethodInfoCache.class);
	private final Map<Key, MethodInfo> cache;

	public MethodInfoCache() {
		this.cache = new HashMap<>();
	}

	/**
	 * Is the cache empty?
	 */
	public boolean isEmpty() {
		return this.cache.isEmpty();
	}
	
	/**
	 * Put a method into the MethodCache.
	 * @param beanName the name of the bean
	 * @param clazz the class of the bean
	 * @param method the method
	 * @param context the Spring application context
	 */
	public void put(String beanName, Class<?> clazz, Method method, ApplicationContext context) {
		MethodInfo info = new MethodInfo(clazz, context, beanName, method);
		this.cache.put(new Key(beanName, method.getName()), info);
	}

	/**
	 * Get a method from the MethodCache.
	 * @param beanName the name of the bean
	 * @param methodName the name of the method
	 * @return the found methodInfo object, null if there is no method found in the cache
	 */
	public MethodInfo get(String beanName, String methodName) {
		return this.cache.get(new Key(beanName, methodName));
	}

	public final static class Key {

		private final String beanName;

		private final String methodName;

		public Key(String beanName, String methodName) {
			this.beanName = beanName;
			this.methodName = methodName;
		}

		public String getBeanName() {
			return this.beanName;
		}

		@Override
		public boolean equals(Object o) {
			if (!(o instanceof Key)) {
				return false;
			}

			Key other = (Key) o;
			return ExtDirectSpringUtil.equal(this.beanName, other.beanName)
					&& ExtDirectSpringUtil.equal(this.methodName, other.methodName);
		}

		@Override
		public int hashCode() {
			return Arrays.hashCode(new Object[] { this.beanName, this.methodName });
		}

	}

	@Override
	public Iterator<Entry<Key, MethodInfo>> iterator() {
		return this.cache.entrySet().iterator();
	}

	public void clear() {
		this.cache.clear();
	}

	public void populateMethodInfoCache(ApplicationContext context) {
		String[] beanNames = context.getBeanNamesForType(Object.class);

		for (String beanName : beanNames) {

			Class<?> handlerType = context.getType(beanName);
			final Class<?> userType = ClassUtils.getUserClass(handlerType);

			Set<Method> methods = MethodIntrospector.selectMethods(userType,
					(MethodFilter) method -> AnnotationUtils.findAnnotation(method, ExtDirectMethod.class) != null);

			for (Method method : methods) {
				ExtDirectMethod directMethodAnnotation = AnnotationUtils.findAnnotation(method, ExtDirectMethod.class);
				final String beanAndMethodName = beanName + "." + method.getName();
				if (directMethodAnnotation.value().isValid(beanAndMethodName, userType, method)) {
					this.put(beanName, handlerType, method, context);

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

		}
	}
}
