/**
 * Copyright 2010-2012 Ralph Schaer <ralphschaer@gmail.com>
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

import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.LinkedHashSet;
import java.util.Locale;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.context.ApplicationContext;
import org.springframework.core.BridgeMethodResolver;
import org.springframework.util.ClassUtils;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.ReflectionUtils.MethodFilter;

import ch.ralscha.extdirectspring.bean.ExtDirectRequest;

/**
 * Utility class
 * 
 * @author mansari
 * @author Ralph Schaer
 */
public final class ExtDirectSpringUtil {

	private ExtDirectSpringUtil() {
		// singleton
	}

	/**
	 * Checks if two objects are equal. Returns true if both objects are null
	 * 
	 * @param a object one
	 * @param b object two
	 * @return true if objects are equal
	 */
	public static boolean equal(Object a, Object b) {
		return a == b || (a != null && a.equals(b));
	}

	/**
	 * Invokes a method on a Spring managed bean.
	 * 
	 * @param context a Spring application context
	 * @param beanName the name of the bean
	 * @param methodInfo the methodInfo object
	 * @param params the parameters
	 * @return the result of the method invocation
	 * @throws IllegalArgumentException if there is no bean in the context
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public static Object invoke(ApplicationContext context, String beanName, MethodInfo methodInfo,
			final Object[] params) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException {
		Object bean = context.getBean(beanName);

		Method handlerMethod = methodInfo.getMethod();
		ReflectionUtils.makeAccessible(handlerMethod);
		return handlerMethod.invoke(bean, params);
	}

	public static Object invoke(HttpServletRequest request, HttpServletResponse response, final Locale locale,
			ApplicationContext context, ExtDirectRequest directRequest, final ParametersResolver parametersResolver)
			throws IllegalArgumentException, IllegalAccessException, InvocationTargetException, Exception {

		MethodInfo methodInfo = MethodInfoCache.INSTANCE.get(directRequest.getAction(), directRequest.getMethod());
		Object[] resolvedParams = parametersResolver.resolveParameters(request, response, locale, directRequest,
				methodInfo);
		return invoke(context, directRequest.getAction(), methodInfo, resolvedParams);
	}

	public static String getStackTrace(Throwable t) {
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw, true);
		t.printStackTrace(pw);
		pw.flush();
		sw.flush();
		return sw.toString();
	}

	/**
	 * Selects handler methods for the given handler type. Callers of this
	 * method define handler methods of interest through the
	 * {@link MethodFilter} parameter.
	 * 
	 * From the Spring 3.1 Source Code. We can delete this method as soon we
	 * update the library to Spring 3.1
	 * 
	 * @param handlerType the handler type to search handler methods on
	 * @param handlerMethodFilter a {@link MethodFilter} to help recognize
	 * handler methods of interest
	 * @return the selected methods, or an empty set
	 */
	public static Set<Method> selectMethods(Class<?> handlerType, final MethodFilter handlerMethodFilter) {
		final Set<Method> handlerMethods = new LinkedHashSet<Method>();
		Set<Class<?>> handlerTypes = new LinkedHashSet<Class<?>>();

		Class<?> specificHandlerType = null;
		if (!Proxy.isProxyClass(handlerType)) {
			handlerTypes.add(handlerType);
			specificHandlerType = handlerType;
		}

		for (Class<?> handlerTypeInterface : handlerType.getInterfaces()) {
			handlerTypes.add(handlerTypeInterface);
		}

		for (Class<?> currentHandlerType : handlerTypes) {
			final Class<?> targetClass = (specificHandlerType != null ? specificHandlerType : currentHandlerType);
			ReflectionUtils.doWithMethods(currentHandlerType, new ReflectionUtils.MethodCallback() {
				public void doWith(Method method) {
					Method specificMethod = ClassUtils.getMostSpecificMethod(method, targetClass);
					Method bridgedMethod = BridgeMethodResolver.findBridgedMethod(specificMethod);
					if (handlerMethodFilter.matches(specificMethod)
							&& (bridgedMethod == specificMethod || !handlerMethodFilter.matches(bridgedMethod))) {
						handlerMethods.add(specificMethod);
					}
				}
			}, ReflectionUtils.USER_DECLARED_METHODS);
		}
		return handlerMethods;
	}

}
