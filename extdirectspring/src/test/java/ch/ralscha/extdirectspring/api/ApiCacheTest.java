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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import org.junit.Test;

/**
 * @author Ralph Schaer
 */
public class ApiCacheTest {

  @Test
  public void testPutAndGet() {
    assertNotNull(ApiCache.INSTANCE);

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

    assertNull(ApiCache.INSTANCE.get(null));
    assertNull(ApiCache.INSTANCE.get(key2));
    assertNotNull(ApiCache.INSTANCE.get(key1));
    assertEquals("one", ApiCache.INSTANCE.get(key1));
    assertEquals("three", ApiCache.INSTANCE.get(key3));
    assertEquals("four", ApiCache.INSTANCE.get(key4));
    assertEquals("five", ApiCache.INSTANCE.get(key5));
    assertEquals("six", ApiCache.INSTANCE.get(key6));
    assertEquals("seven", ApiCache.INSTANCE.get(key7));

    assertFalse(key1.equals("test"));
    assertFalse(key1.equals(null));
    
    assertTrue(key1.equals(key1));
    assertTrue(key2.equals(key2));
    assertTrue(key3.equals(key3));
    assertTrue(key4.equals(key4));
    assertTrue(key5.equals(key5));
    assertTrue(key6.equals(key6));
    assertTrue(key7.equals(key7));

    assertFalse(key2.equals(key1));
    assertFalse(key3.equals(key1));
    assertFalse(key4.equals(key1));
    assertFalse(key5.equals(key1));
    assertFalse(key6.equals(key1));
    assertFalse(key7.equals(key1));
    
  }

}
