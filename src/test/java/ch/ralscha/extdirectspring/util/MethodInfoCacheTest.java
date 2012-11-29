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

import static org.fest.assertions.api.Assertions.assertThat;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import nl.jqno.equalsverifier.EqualsVerifier;

import org.junit.Test;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import ch.ralscha.extdirectspring.annotation.ExtDirectMethod;

public class MethodInfoCacheTest {

	@Test
	public void verifyEquals() {
		EqualsVerifier.forClass(MethodInfoCache.Key.class).verify();
	}

	@Test
	@ExtDirectMethod
	public void testPutAndGet() throws SecurityException, NoSuchMethodException {
		assertThat(MethodInfoCache.INSTANCE).isNotNull();
		Method thisMethod = getClass().getMethod("testPutAndGet", (Class<?>[]) null);

		MethodInfoCache.INSTANCE.put("methodCacheTest", getClass(), thisMethod, null);
		assertThat(MethodInfoCache.INSTANCE.get("methodCacheTest", "testPu")).isNull();
		assertThat(MethodInfoCache.INSTANCE.get("methodCacheTes", "testPut")).isNull();
		assertThat(MethodInfoCache.INSTANCE.get("methodCacheTest", "testPutAndGet").getMethod()).isEqualTo(thisMethod);
	}

	@Test
	public void testKey() {
		MethodInfoCache.Key key1 = new MethodInfoCache.Key("bean", "method");
		MethodInfoCache.Key key2 = new MethodInfoCache.Key("bean", "otherMethod");
		MethodInfoCache.Key key3 = new MethodInfoCache.Key("otherBean", "otherMethod");

		assertThat(key1.equals(key1)).isTrue();
		assertThat(key2.equals(key2)).isTrue();
		assertThat(key3.equals(key3)).isTrue();

		assertThat(key1.equals(key2)).isFalse();
		assertThat(key1.equals(key3)).isFalse();

		assertThat(key1.equals("test")).isFalse();
	}

	@SuppressWarnings("unused")
	@Test
	public void testFindMethodWithAnnotation() {
		new ClassPathXmlApplicationContext("/testApplicationContextB.xml");
		MethodInfo methodBInfo = MethodInfoCache.INSTANCE.get("springManagedBean", "methodB");
		Method methodBWithAnnotation = MethodInfo.findMethodWithAnnotation(methodBInfo.getMethod(),
				ExtDirectMethod.class);
		assertThat(methodBWithAnnotation).isEqualTo(methodBInfo.getMethod());

		MethodInfo methodSubBInfo = MethodInfoCache.INSTANCE.get("springManagedSubBean", "methodB");
		methodBWithAnnotation = MethodInfo.findMethodWithAnnotation(methodSubBInfo.getMethod(), ExtDirectMethod.class);
		assertThat(methodSubBInfo.getMethod().equals(methodBWithAnnotation)).isFalse();
		assertThat(methodBInfo.getMethod().equals(methodBWithAnnotation)).isTrue();
	}

	@SuppressWarnings("unused")
	@Test(expected = NullPointerException.class)
	public void testInvokeWithNull() throws IllegalArgumentException, IllegalAccessException, InvocationTargetException {
		new ClassPathXmlApplicationContext("/testApplicationContextB.xml");
		ExtDirectSpringUtil.invoke(null, null, null, null);
	}

	@Test(expected = NoSuchBeanDefinitionException.class)
	public void testNonExistingBeanAndMethod() throws IllegalArgumentException, IllegalAccessException,
			InvocationTargetException {
		ApplicationContext context = new ClassPathXmlApplicationContext("/testApplicationContextB.xml");
		MethodInfo info = MethodInfoCache.INSTANCE.get("springManagedBeanA", "methodA");
		ExtDirectSpringUtil.invoke(context, "springManagedBeanA", info, null);
	}

	@Test(expected = NullPointerException.class)
	public void testExistingWithouEdsAnnotation() throws IllegalArgumentException, IllegalAccessException,
			InvocationTargetException {
		ApplicationContext context = new ClassPathXmlApplicationContext("/testApplicationContextB.xml");
		MethodInfo info = MethodInfoCache.INSTANCE.get("springManagedBean", "methodA");
		ExtDirectSpringUtil.invoke(context, "springManagedBean", info, null);
	}

	@Test
	public void testFindMethodAndInvoke() throws IllegalArgumentException, IllegalAccessException,
			InvocationTargetException {
		ApplicationContext context = new ClassPathXmlApplicationContext("/testApplicationContextB.xml");

		MethodInfo infoB = MethodInfoCache.INSTANCE.get("springManagedBean", "methodB");

		assertThat((Boolean) ExtDirectSpringUtil.invoke(context, "springManagedBean", infoB, null)).isFalse();
		assertThat((Boolean) ExtDirectSpringUtil.invoke(context, "springManagedBean", infoB, null)).isFalse();

		MethodInfo infoSum = MethodInfoCache.INSTANCE.get("springManagedBean", "sum");

		assertThat(ExtDirectSpringUtil.invoke(context, "springManagedBean", infoSum, new Object[] { 1, 2 })).isEqualTo(
				Integer.valueOf(3));
		assertThat(ExtDirectSpringUtil.invoke(context, "springManagedBean", infoSum, new Object[] { 6, 3 })).isEqualTo(
				Integer.valueOf(9));

		assertThat(MethodInfoCache.INSTANCE.get("springManagedBean", "methodC")).isNull();
	}

}
