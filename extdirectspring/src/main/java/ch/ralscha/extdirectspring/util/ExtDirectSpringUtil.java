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

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.springframework.context.ApplicationContext;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.util.ReflectionUtils;

import ch.ralscha.extdirectspring.annotation.ExtDirectMethod;

/**
 * Utility class
 * 
 * @author mansari
 * @author Ralph Schaer
 */
public class ExtDirectSpringUtil {

	private ExtDirectSpringUtil() {
		//singleton
	}

	/**
	 * Checks if two objects are equal. Returns true if both objects are null
	 * 
	 * @param a
	 *          object one
	 * @param b
	 *          object two
	 * @return true if objects are equal
	 */
	public static boolean equal(final Object a, final Object b) {
		return a == b || (a != null && a.equals(b));
	}

	/**
	 * Retrieves a methodInfo from a method in a spring managed bean. The found
	 * method will be cached in {@link MethodInfoCache} with the key
	 * beanName,methodName
	 * 
	 * @param context
	 *          Spring application context
	 * @param beanName
	 *          name of the bean to find the method in
	 * @param methodName
	 *          name of the method to retrieve
	 * @return the method
	 * @throws IllegalArgumentException
	 *           if the method is not annotated with a ExtDirectSpring annotation
	 *           or there is no method in the bean
	 */
	public static MethodInfo findMethodInfo(final ApplicationContext context, final String beanName,
			final String methodName) {

		if (context == null) {
			throw new IllegalArgumentException("ApplicatonContext cannot be null");
		}

		if (beanName == null) {
			throw new IllegalArgumentException("beanName cannot be null");
		}

		if (methodName == null) {
			throw new IllegalArgumentException("methodName cannot be null");
		}

		MethodInfo methodInfo = MethodInfoCache.INSTANCE.get(beanName, methodName);

		if (methodInfo != null) {
			return methodInfo;
		}

		Object bean = context.getBean(beanName);
		List<Method> methods = findMethodsWithMinimalParameters(bean.getClass(), methodName);

		if (methods != null) {
			for (Method method : methods) {
				if (AnnotationUtils.findAnnotation(method, ExtDirectMethod.class) != null) {
					return MethodInfoCache.INSTANCE.put(beanName, methodName, bean.getClass(), method);
				}
			}

			throw new IllegalArgumentException("Invalid remoting method '" + beanName + "." + methodName
					+ "'. Missing ExtDirectMethod annotation");
		}

		throw new IllegalArgumentException("Method '" + beanName + "." + methodName + "' not found");

	}

	/**
	 * Invokes a method on a Spring managed bean.
	 * 
	 * @param context
	 *          a Spring application context
	 * @param beanName
	 *          the name of the bean
	 * @param methodInfo
	 *          the methodInfo object
	 * @param params
	 *          the parameters
	 * @return the result of the method invokation
	 * @throws IllegalArgumentException
	 *           if there is no bean in the context
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public static Object invoke(final ApplicationContext context, final String beanName, final MethodInfo methodInfo,
			final Object[] params) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException {
		Object bean = context.getBean(beanName);

		Method handlerMethod = methodInfo.getMethod();
		ReflectionUtils.makeAccessible(handlerMethod);
		return handlerMethod.invoke(bean, params);
	}

	private static List<Method> findMethodsWithMinimalParameters(Class<?> clazz, String methodName)
			throws IllegalArgumentException {

		List<Method> targetMethod = findMethodsWithMinimalParameters(clazz.getMethods(), methodName);
		if (targetMethod == null) {
			targetMethod = findDeclaredMethodsWithMinimalParameters(clazz, methodName);
		}
		return targetMethod;
	}

	private static List<Method> findDeclaredMethodsWithMinimalParameters(Class<?> clazz, String methodName)
			throws IllegalArgumentException {

		List<Method> targetMethod = findMethodsWithMinimalParameters(clazz.getDeclaredMethods(), methodName);
		if (targetMethod == null && clazz.getSuperclass() != null) {
			targetMethod = findDeclaredMethodsWithMinimalParameters(clazz.getSuperclass(), methodName);
		}
		return targetMethod;
	}

	private static List<Method> findMethodsWithMinimalParameters(Method[] methods, String methodName)
			throws IllegalArgumentException {

		List<Method> targetMethods = null;
		for (Method method : methods) {
			if (method.getName().equals(methodName)) {
				int numParams = method.getParameterTypes().length;
				if (targetMethods == null || numParams < targetMethods.get(0).getParameterTypes().length) {
					targetMethods = new ArrayList<Method>();
					targetMethods.add(method);
				} else {
					if (targetMethods.get(0).getParameterTypes().length == numParams) {
						targetMethods.add(method);
					}
				}
			}
		}

		return targetMethods;
	}

}
