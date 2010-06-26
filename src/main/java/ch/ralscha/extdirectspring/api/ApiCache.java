/**
 * Copyright 2010 Ralph Schaer <ralphschaer@gmail.com>
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

package ch.ralscha.extdirectspring.api;

import java.lang.ref.SoftReference;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 
 * 
 * @author Ralph Schaer
 */
public enum ApiCache {

  /**
   * Singleton enum pattern
   */
  INSTANCE;
  
  private Map<ApiCacheKey, SoftReference<String>> cache;

  private ApiCache() {
    cache = new ConcurrentHashMap<ApiCacheKey, SoftReference<String>>();
  }

  public void put(ApiCacheKey key, String apiString) {
    cache.put(key, new SoftReference<String>(apiString));
  }

  public String get(ApiCacheKey key) {
    if (key != null) {
      SoftReference<String> apiStringReference = cache.get(key);
      if (apiStringReference != null && apiStringReference.get() != null) {
        return apiStringReference.get();
      }
    }
    return null;
  }
}
