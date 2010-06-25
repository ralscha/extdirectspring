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


public class MethodCacheTest {

  @Test
  public void testPutAndGet() throws SecurityException, NoSuchMethodException {
    assertNotNull(MethodCache.INSTANCE);
    Method thisMethod = getClass().getMethod("testPutAndGet", null);
    
    MethodCache.INSTANCE.put(null, null, null);    
    assertNull(MethodCache.INSTANCE.get(null, null));
    
    MethodCache.INSTANCE.put(null, null, thisMethod);
    assertEquals(thisMethod, MethodCache.INSTANCE.get(null, null));
    
    MethodCache.INSTANCE.put(null, "testPut", thisMethod);
    assertNull(MethodCache.INSTANCE.get(null, "testPu"));
    assertEquals(thisMethod, MethodCache.INSTANCE.get(null, "testPut"));
    
    MethodCache.INSTANCE.put("methodCacheTest", "testPut", thisMethod);
    assertNull(MethodCache.INSTANCE.get("methodCacheTest", "testPu"));
    assertNull(MethodCache.INSTANCE.get("methodCacheTes", "testPut"));
    assertEquals(thisMethod, MethodCache.INSTANCE.get("methodCacheTest", "testPut"));
    
  }

 

}
