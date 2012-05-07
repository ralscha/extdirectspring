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

import static org.fest.assertions.Assertions.assertThat;

import java.lang.reflect.Method;

import org.junit.Test;

import ch.ralscha.extdirectspring.annotation.ExtDirectMethod;

/**
 * Tests for {@link MethodInfoCache}.
 * 
 * @author Ralph Schaer
 */
@SuppressWarnings("all")
public class MethodInfoCacheTest {

	@Test
	@ExtDirectMethod
	public void testPutAndGet() throws SecurityException, NoSuchMethodException {
		assertThat(MethodInfoCache.INSTANCE).isNotNull();
		Method thisMethod = getClass().getMethod("testPutAndGet", null);

		//todo re-enable these tests
		//		MethodInfoCache.INSTANCE.put(null, null, null);
		//		assertThat(MethodInfoCache.INSTANCE.get(null, null)).isNull();
		//
		//		MethodInfoCache.INSTANCE.put(null, getClass(), thisMethod);
		//		assertEquals(thisMethod, MethodInfoCache.INSTANCE.get(null, null).getMethod());
		//
		//		MethodInfoCache.INSTANCE.put(null, getClass(), thisMethod);
		//		assertThat(MethodInfoCache.INSTANCE.get(null, "testPu")).isNull();
		//		assertEquals(thisMethod, MethodInfoCache.INSTANCE.get(null, "testPut").getMethod());

		MethodInfoCache.INSTANCE.put("methodCacheTest", getClass(), thisMethod);
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

}
