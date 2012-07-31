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
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.context.ApplicationContext;
import org.springframework.util.ReflectionUtils;

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

}
