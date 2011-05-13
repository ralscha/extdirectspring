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

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.springframework.beans.BeanUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.core.annotation.AnnotationUtils;

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
		Method method = BeanUtils.findMethodWithMinimalParameters(bean.getClass(), methodName);

		if (method != null) {
			if (AnnotationUtils.findAnnotation(method, ExtDirectMethod.class) == null) {
				throw new IllegalArgumentException("Invalid remoting method '" + beanName + "." + methodName
						+ "'. Missing ExtDirectMethod annotation");
			}

			return MethodInfoCache.INSTANCE.put(beanName, methodName, bean.getClass(), method);
		}

		throw new IllegalArgumentException("Method '" + beanName + "." + methodName + "' not found");

	}

	/**
	 * Find a method that is annotated with a specific annotation. Starts with the
	 * method and goes up to the superclasses of the class.
	 * 
	 * @param method
	 *          the starting method
	 * @param annotation
	 *          the annotation to look for
	 * @return the method if there is a annotated method, else null
	 */
	public static Method findMethodWithAnnotation(final Method method, final Class<? extends Annotation> annotation) {
		if (method.isAnnotationPresent(annotation)) {
			return method;
		}

		Class<?> cl = method.getDeclaringClass();
		while (cl != null) {
			try {
				Method equivalentMethod = cl.getDeclaredMethod(method.getName(), method.getParameterTypes());
				if (equivalentMethod.isAnnotationPresent(annotation)) {
					return equivalentMethod;
				}
			} catch (NoSuchMethodException e) {
				// do nothing here
			}
			cl = cl.getSuperclass();
		}

		return null;
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
		return methodInfo.getMethod().invoke(bean, params);
	}



}
