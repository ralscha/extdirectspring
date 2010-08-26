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

package ch.ralscha.extdirectspring.util;

import static org.junit.Assert.*;

import java.lang.reflect.Method;

import org.junit.Test;

/**
 * Tests for {@link MethodInfoCache}.
 * 
 * @author Ralph Schaer
 */
@SuppressWarnings("all")
public class MethodInfoCacheTest {

  @Test
  public void testPutAndGet() throws SecurityException, NoSuchMethodException {
    assertNotNull(MethodInfoCache.INSTANCE);
    Method thisMethod = getClass().getMethod("testPutAndGet", null);

    MethodInfoCache.INSTANCE.put(null, null, null);
    assertNull(MethodInfoCache.INSTANCE.get(null, null));

    MethodInfoCache.INSTANCE.put(null, null, thisMethod);
    assertEquals(thisMethod, MethodInfoCache.INSTANCE.get(null, null).getMethod());

    MethodInfoCache.INSTANCE.put(null, "testPut", thisMethod);
    assertNull(MethodInfoCache.INSTANCE.get(null, "testPu"));
    assertEquals(thisMethod, MethodInfoCache.INSTANCE.get(null, "testPut").getMethod());

    MethodInfoCache.INSTANCE.put("methodCacheTest", "testPut", thisMethod);
    assertNull(MethodInfoCache.INSTANCE.get("methodCacheTest", "testPu"));
    assertNull(MethodInfoCache.INSTANCE.get("methodCacheTes", "testPut"));
    assertEquals(thisMethod, MethodInfoCache.INSTANCE.get("methodCacheTest", "testPut").getMethod());
  }
  
  @Test
  public void testKey() {
    MethodInfoCache.Key key1 = new MethodInfoCache.Key("bean", "method");
    MethodInfoCache.Key key2 = new MethodInfoCache.Key("bean", "otherMethod");
    MethodInfoCache.Key key3 = new MethodInfoCache.Key("otherBean", "otherMethod");
    
    assertTrue(key1.equals(key1));
    assertTrue(key2.equals(key2));
    assertTrue(key3.equals(key3));
    
    assertFalse(key1.equals(key2));
    assertFalse(key1.equals(key3));
    
    assertFalse(key1.equals("test"));
  }

}
