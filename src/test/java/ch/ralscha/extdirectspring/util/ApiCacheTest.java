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
import nl.jqno.equalsverifier.EqualsVerifier;

import org.junit.Test;

/**
 * @author Ralph Schaer
 */
public class ApiCacheTest {

	@Test
	public void verifyEquals() {
		EqualsVerifier.forClass(ApiCacheKey.class).verify();
	}

	@Test
	public void testPutAndGet() {
		assertThat(ApiCache.INSTANCE).isNotNull();

		ApiCacheKey key1 = new ApiCacheKey(null, null, null, null, null, false);
		ApiCacheKey key2 = new ApiCacheKey(null, null, null, null, null, true);
		ApiCacheKey key3 = new ApiCacheKey(null, null, null, null, "group", true);
		ApiCacheKey key4 = new ApiCacheKey(null, null, null, "polling", "group", true);
		ApiCacheKey key5 = new ApiCacheKey(null, null, "remoting", "polling", "group", true);
		ApiCacheKey key6 = new ApiCacheKey(null, "action", "remoting", "polling", "group", true);
		ApiCacheKey key7 = new ApiCacheKey("api", "action", "remoting", "polling", "group", true);
		ApiCache.INSTANCE.put(key1, "one");
		ApiCache.INSTANCE.put(key3, "three");
		ApiCache.INSTANCE.put(key4, "four");
		ApiCache.INSTANCE.put(key5, "five");
		ApiCache.INSTANCE.put(key6, "six");
		ApiCache.INSTANCE.put(key7, "seven");

		assertThat(ApiCache.INSTANCE.get(null)).isNull();
		assertThat(ApiCache.INSTANCE.get(key2)).isNull();
		assertThat(ApiCache.INSTANCE.get(key1)).isNotNull();
		assertThat(ApiCache.INSTANCE.get(key1)).isEqualTo("one");
		assertThat(ApiCache.INSTANCE.get(key3)).isEqualTo("three");
		assertThat(ApiCache.INSTANCE.get(key4)).isEqualTo("four");
		assertThat(ApiCache.INSTANCE.get(key5)).isEqualTo("five");
		assertThat(ApiCache.INSTANCE.get(key6)).isEqualTo("six");
		assertThat(ApiCache.INSTANCE.get(key7)).isEqualTo("seven");

		assertThat(key1.equals("test")).isFalse();
		assertThat(key1.equals(null)).isFalse();

		assertThat(key1.equals(key1)).isTrue();
		assertThat(key2.equals(key2)).isTrue();
		assertThat(key3.equals(key3)).isTrue();
		assertThat(key4.equals(key4)).isTrue();
		assertThat(key5.equals(key5)).isTrue();
		assertThat(key6.equals(key6)).isTrue();
		assertThat(key7.equals(key7)).isTrue();

		assertThat(key2.equals(key1)).isFalse();
		assertThat(key3.equals(key1)).isFalse();
		assertThat(key4.equals(key1)).isFalse();
		assertThat(key5.equals(key1)).isFalse();
		assertThat(key6.equals(key1)).isFalse();
		assertThat(key7.equals(key1)).isFalse();

	}

}
