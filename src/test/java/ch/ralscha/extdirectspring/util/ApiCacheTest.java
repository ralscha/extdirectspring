/**
 * Copyright 2010-2016 Ralph Schaer <ralphschaer@gmail.com>
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

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

import nl.jqno.equalsverifier.EqualsVerifier;

public class ApiCacheTest {

	@Test
	public void verifyEquals() {
		EqualsVerifier.forClass(ApiCacheKey.class).verify();
	}

	@Test
	public void testPutAndGet() {
		ApiCache cache = new ApiCache();

		ApiCacheKey key1 = new ApiCacheKey(null, null, null, null, null, null, false);
		ApiCacheKey key2 = new ApiCacheKey(null, null, null, null, null, null, true);
		ApiCacheKey key3 = new ApiCacheKey(null, null, null, null, null, "/router", true);
		ApiCacheKey key5 = new ApiCacheKey(null, null, null, null, "group", "/router",
				true);
		ApiCacheKey key6 = new ApiCacheKey(null, null, null, "polling", "group",
				"/router", true);
		ApiCacheKey key7 = new ApiCacheKey(null, null, "remoting", "polling", "group",
				"/router", true);
		ApiCacheKey key8 = new ApiCacheKey(null, "action", "remoting", "polling", "group",
				"/router", true);
		ApiCacheKey key9 = new ApiCacheKey("api", "action", "remoting", "polling",
				"group", "/router", true);

		cache.put(key1, "one");
		cache.put(key3, "three");
		cache.put(key5, "five");
		cache.put(key6, "six");
		cache.put(key7, "seven");
		cache.put(key8, "eight");
		cache.put(key9, "nine");

		assertThat(cache.get(null)).isNull();
		assertThat(cache.get(key2)).isNull();
		assertThat(cache.get(key1)).isNotNull();
		assertThat(cache.get(key1)).isEqualTo("one");
		assertThat(cache.get(key3)).isEqualTo("three");
		assertThat(cache.get(key5)).isEqualTo("five");
		assertThat(cache.get(key6)).isEqualTo("six");
		assertThat(cache.get(key7)).isEqualTo("seven");
		assertThat(cache.get(key8)).isEqualTo("eight");
		assertThat(cache.get(key9)).isEqualTo("nine");

		assertThat(key1.equals("test")).isFalse();
		assertThat(key1.equals(null)).isFalse();

		assertThat(key1.equals(key1)).isTrue();
		assertThat(key2.equals(key2)).isTrue();
		assertThat(key3.equals(key3)).isTrue();
		assertThat(key5.equals(key5)).isTrue();
		assertThat(key6.equals(key6)).isTrue();
		assertThat(key7.equals(key7)).isTrue();
		assertThat(key8.equals(key8)).isTrue();
		assertThat(key9.equals(key9)).isTrue();

		assertThat(key2.equals(key1)).isFalse();
		assertThat(key3.equals(key1)).isFalse();
		assertThat(key5.equals(key1)).isFalse();
		assertThat(key6.equals(key1)).isFalse();
		assertThat(key7.equals(key1)).isFalse();
		assertThat(key8.equals(key1)).isFalse();
		assertThat(key9.equals(key1)).isFalse();

		assertThat(key1.equals(key2)).isFalse();
		assertThat(key3.equals(key2)).isFalse();
		assertThat(key5.equals(key2)).isFalse();
		assertThat(key6.equals(key2)).isFalse();
		assertThat(key7.equals(key2)).isFalse();
		assertThat(key8.equals(key2)).isFalse();
		assertThat(key9.equals(key2)).isFalse();

		assertThat(key1.equals(key3)).isFalse();
		assertThat(key2.equals(key3)).isFalse();
		assertThat(key5.equals(key3)).isFalse();
		assertThat(key6.equals(key3)).isFalse();
		assertThat(key7.equals(key3)).isFalse();
		assertThat(key8.equals(key3)).isFalse();
		assertThat(key9.equals(key3)).isFalse();
	}

	@Test
	public void testEqualKeys() {
		ApiCache cache = new ApiCache();

		ApiCacheKey keyOne = new ApiCacheKey("api", "action", "remoting", "polling",
				"group", "/router", true);
		ApiCacheKey keyTwo = new ApiCacheKey("api", "action", "remoting", "polling",
				"group", "/router", true);

		cache.put(keyOne, "1");

		assertThat(cache.get(keyOne)).isEqualTo("1");
		assertThat(cache.get(keyTwo)).isEqualTo("1");

		cache.put(keyTwo, "2");
		assertThat(cache.get(keyOne)).isEqualTo("2");
		assertThat(cache.get(keyTwo)).isEqualTo("2");
	}

}
