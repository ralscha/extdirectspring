/*
 * Copyright the original author or authors.
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

import java.lang.ref.SoftReference;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.jspecify.annotations.Nullable;
import org.springframework.stereotype.Service;

@Service
public class ApiCache {

	private final Map<ApiCacheKey, SoftReference<String>> cache;

	public ApiCache() {
		this.cache = new ConcurrentHashMap<>();
	}

	public void put(ApiCacheKey key, String apiString) {
		this.cache.put(key, new SoftReference<>(apiString));
	}

	public @Nullable String get(@Nullable ApiCacheKey key) {
		if (key != null) {
			SoftReference<String> apiStringReference = this.cache.get(key);
			if (apiStringReference != null) {
				String value = apiStringReference.get();
				if (value != null) {
					return value;
				}
			}
		}
		return null;
	}

	/**
	 * for unit tests
	 */
	public void clear() {
		this.cache.clear();
	}

}
