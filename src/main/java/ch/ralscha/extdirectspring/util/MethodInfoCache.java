/**
 * Copyright 2010-2014 Ralph Schaer <ralphschaer@gmail.com>
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
import java.util.Map.Entry;

import org.springframework.context.ApplicationContext;

/**
 * A simple cache for methods with key beanName/methodName
 */
public enum MethodInfoCache implements Iterable<Map.Entry<MethodInfoCache.Key, MethodInfo>> {

	/**
	 * Singleton enum pattern
	 */
	INSTANCE;

	private final Map<Key, MethodInfo> cache;

	private MethodInfoCache() {
		cache = new HashMap<Key, MethodInfo>();
	}

	/**
	 * Put a method into the MethodCache.
	 * 
	 * @param beanName the name of the bean
	 * @param clazz the class of the bean
	 * @param method the method
	 * @param context the Spring application context
	 */
	public void put(String beanName, Class<?> clazz, Method method, ApplicationContext context) {
		MethodInfo info = new MethodInfo(clazz, context, beanName, method);
		cache.put(new Key(beanName, method.getName()), info);
	}

	/**
	 * Get a method from the MethodCache.
	 * 
	 * @param beanName the name of the bean
	 * @param methodName the name of the method
	 * @return the found methodInfo object, null if there is no method found in the cache
	 */
	public MethodInfo get(String beanName, String methodName) {
		return cache.get(new Key(beanName, methodName));
	}

	public final static class Key {

		private final String beanName;

		private final String methodName;

		public Key(String beanName, String methodName) {
			this.beanName = beanName;
			this.methodName = methodName;
		}

		public String getBeanName() {
			return beanName;
		}

		@Override
		public boolean equals(Object o) {
			if (!(o instanceof Key)) {
				return false;
			}

			Key other = (Key) o;
			return ExtDirectSpringUtil.equal(beanName, other.beanName)
					&& ExtDirectSpringUtil.equal(methodName, other.methodName);
		}

		@Override
		public int hashCode() {
			return Arrays.hashCode(new Object[] { beanName, methodName });
		}

	}

	@Override
	public Iterator<Entry<Key, MethodInfo>> iterator() {
		return cache.entrySet().iterator();
	}

	public void clear() {
		cache.clear();
	}

}
