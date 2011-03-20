/**
 * Copyright 2010-2011 Ralph Schaer <ralphschaer@gmail.com>
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
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * A simple cache for methods with key beanName/methodName
 * 
 * @author Ralph Schaer
 */
public enum MethodInfoCache {

	/**
	 * Singleton enum pattern
	 */
	INSTANCE;

	private final Map<Key, MethodInfo> cache;

	private MethodInfoCache() {
		cache = new ConcurrentHashMap<Key, MethodInfo>();
	}

	/**
	 * Put a method into the MethodCache.
	 * 
	 * @param beanName
	 *          the name of the bean
	 * @param methodName
	 *          the name of the method
	 * @param clazz 
	 * 			the class of the bean
	 * @param method
	 *          the method
	 * @return the methodInfo object of the method
	 */
	public MethodInfo put(final String beanName, final String methodName, final Class<?> clazz, final Method method) {
		if (method != null) {
			MethodInfo info = new MethodInfo(clazz, method);
			cache.put(new Key(beanName, methodName), info);
			return info;
		}
		return null;
	}

	/**
	 * Get a method from the MethodCache.
	 * 
	 * @param beanName
	 *          the name of the bean
	 * @param methodName
	 *          the name of the method
	 * @return the found methodInfo object, null if there is no method found in
	 *         the cache
	 */
	public MethodInfo get(final String beanName, final String methodName) {
		return cache.get(new Key(beanName, methodName));
	}

	final static class Key {

		private final String beanName;
		private final String methodName;

		public Key(final String beanName, final String methodName) {
			this.beanName = beanName;
			this.methodName = methodName;
		}

		@Override
		public boolean equals(Object o) {
			if (!(o instanceof Key)) {
				return false;
			}

			Key other = (Key) o;
			return (ExtDirectSpringUtil.equal(beanName, other.beanName) && ExtDirectSpringUtil.equal(methodName,
					other.methodName));
		}

		@Override
		public int hashCode() {
			return Arrays.hashCode(new Object[] { beanName, methodName });
		}

	}
}
