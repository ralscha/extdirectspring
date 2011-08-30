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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.junit.Test;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import ch.ralscha.extdirectspring.annotation.ExtDirectMethod;

/**
 * Tests for {@link ExtDirectSpringUtil}.
 * 
 * @author Ralph Schaer
 */
public class ExtDirectSpringUtilTest {

	@Test
	public void testEqual() {
		assertTrue(ExtDirectSpringUtil.equal(1, 1));
		assertFalse(ExtDirectSpringUtil.equal(1, 2));

		assertTrue(ExtDirectSpringUtil.equal(true, true));
		assertTrue(ExtDirectSpringUtil.equal(false, false));

		assertFalse(ExtDirectSpringUtil.equal(true, false));
		assertFalse(ExtDirectSpringUtil.equal(false, true));
		assertFalse(ExtDirectSpringUtil.equal(false, null));

		assertTrue(ExtDirectSpringUtil.equal("a", "a"));
		assertFalse(ExtDirectSpringUtil.equal("a", "b"));
		assertFalse(ExtDirectSpringUtil.equal(null, "a"));
		assertFalse(ExtDirectSpringUtil.equal("a", null));
		assertTrue(ExtDirectSpringUtil.equal(null, null));
	}

	@Test(expected = IllegalArgumentException.class)
	public void testFindMethodInfoWithEmptyContext() {
		ExtDirectSpringUtil.findMethodInfo(null, "springManagedBean", "methodB");
	}

	@Test(expected = IllegalArgumentException.class)
	public void testFindMethodInfoWithEmptyBeanName() {
		ApplicationContext context = new ClassPathXmlApplicationContext("/testApplicationContextB.xml");
		ExtDirectSpringUtil.findMethodInfo(context, null, "methodB");
	}

	@Test(expected = IllegalArgumentException.class)
	public void testFindMethodInfoWithEmptyMethodName() {
		ApplicationContext context = new ClassPathXmlApplicationContext("/testApplicationContextB.xml");
		ExtDirectSpringUtil.findMethodInfo(context, "springManagedBean", null);
	}

	@Test
	public void testFindMethodWithAnnotation() {
		ApplicationContext context = new ClassPathXmlApplicationContext("/testApplicationContextB.xml");
		MethodInfo methodBInfo = ExtDirectSpringUtil.findMethodInfo(context, "springManagedBean", "methodB");
		Method methodBWithAnnotation = MethodInfo.findMethodWithAnnotation(methodBInfo.getMethod(),
				ExtDirectMethod.class);
		assertEquals(methodBInfo.getMethod(), methodBWithAnnotation);

		MethodInfo methodSubBInfo = ExtDirectSpringUtil.findMethodInfo(context, "springManagedSubBean", "methodB");
		methodBWithAnnotation = MethodInfo.findMethodWithAnnotation(methodSubBInfo.getMethod(), ExtDirectMethod.class);
		assertFalse(methodSubBInfo.getMethod().equals(methodBWithAnnotation));
		assertTrue(methodBInfo.getMethod().equals(methodBWithAnnotation));
	}

	@Test
	public void testFindMethodAndInvoke() throws IllegalArgumentException, IllegalAccessException,
			InvocationTargetException {
		ApplicationContext context = new ClassPathXmlApplicationContext("/testApplicationContextB.xml");

		try {
			assertNull(ExtDirectSpringUtil.invoke(null, null, null, null));
			fail("has to throw a IllegalArgumentException");
		} catch (Exception e) {
			assertTrue(e instanceof NullPointerException);
		}

		try {
			MethodInfo info = ExtDirectSpringUtil.findMethodInfo(context, "springManagedBeanA", "methodA");

			assertNull(ExtDirectSpringUtil.invoke(context, "springManagedBeanA", info, null));
			fail("has to throw a NoSuchBeanDefinitionException");
		} catch (Exception e) {
			assertTrue(e instanceof NoSuchBeanDefinitionException);
			assertEquals("No bean named 'springManagedBeanA' is defined", e.getMessage());
		}

		try {
			MethodInfo info = ExtDirectSpringUtil.findMethodInfo(context, "springManagedBean", "methodA");
			ExtDirectSpringUtil.invoke(context, "springManagedBean", info, null);
			fail("has to throw a IllegalArgumentException");
		} catch (Exception e) {
			assertTrue(e instanceof IllegalArgumentException);
			assertEquals("Invalid remoting method 'springManagedBean.methodA'. Missing ExtDirectMethod annotation",
					e.getMessage());
		}

		MethodInfo infoB = ExtDirectSpringUtil.findMethodInfo(context, "springManagedBean", "methodB");

		assertFalse((Boolean) ExtDirectSpringUtil.invoke(context, "springManagedBean", infoB, null));
		assertFalse((Boolean) ExtDirectSpringUtil.invoke(context, "springManagedBean", infoB, null));

		MethodInfo infoSum = ExtDirectSpringUtil.findMethodInfo(context, "springManagedBean", "sum");

		assertEquals(Integer.valueOf(3),
				ExtDirectSpringUtil.invoke(context, "springManagedBean", infoSum, new Object[] { 1, 2 }));
		assertEquals(Integer.valueOf(9),
				ExtDirectSpringUtil.invoke(context, "springManagedBean", infoSum, new Object[] { 6, 3 }));

		try {
			ExtDirectSpringUtil.findMethodInfo(context, "springManagedBean", "methodC");
			fail("has to throw a IllegalArgumentException");
		} catch (Exception e) {
			assertTrue(e instanceof IllegalArgumentException);
			assertEquals("Method 'springManagedBean.methodC' not found", e.getMessage());
		}
	}

}
