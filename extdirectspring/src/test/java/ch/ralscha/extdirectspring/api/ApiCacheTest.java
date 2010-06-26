package ch.ralscha.extdirectspring.api;

import static org.junit.Assert.*;
import org.junit.Test;


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
    
    
    
  }


}
