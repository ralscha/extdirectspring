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

package ch.ralscha.extdirectspring.itest;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;

import java.io.IOException;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Test;

public class SimpleServiceTest {

  @Test
  public void testSimpleApi() throws IllegalStateException, IOException {

    HttpClient client = new DefaultHttpClient();
    HttpGet get = new HttpGet("http://localhost:9998/controller/api-debug.js?group=itest_simple");
    HttpResponse response = client.execute(get);
    HttpEntity entity = response.getEntity();
    assertNotNull(entity);
    String responseString = EntityUtils.toString(entity);    
    entity.consumeContent();
    assertTrue(responseString.contains("\"name\" : \"toUpperCase\""));
  }
  
  @SuppressWarnings("unchecked")
  @Test
  public void testSimpleCall() throws IllegalStateException, IOException {
    HttpClient client = new DefaultHttpClient();
    HttpPost post = new HttpPost("http://localhost:9998/controller/router");
    
    StringEntity postEntity = new StringEntity("{\"action\":\"simpleService\",\"method\":\"toUpperCase\",\"data\":[\"ralph\"],\"type\":\"rpc\",\"tid\":1}", 
        "UTF-8");
    post.setEntity(postEntity);
    
    HttpResponse response = client.execute(post);
    HttpEntity entity = response.getEntity();
    assertNotNull(entity);
    String responseString = EntityUtils.toString(entity);
    
    assertNotNull(responseString);
    assertTrue(responseString.startsWith("[") && responseString.endsWith("]"));
    ObjectMapper mapper = new ObjectMapper();
    Map<String, Object> rootAsMap = mapper.readValue(responseString.substring(1, responseString.length()-1), Map.class);
    assertEquals(5, rootAsMap.size());
    assertEquals("RALPH", rootAsMap.get("result"));
    assertEquals("toUpperCase", rootAsMap.get("method"));
    assertEquals("rpc", rootAsMap.get("type"));
    assertEquals("simpleService", rootAsMap.get("action"));
    assertEquals(1, rootAsMap.get("tid"));
    
  }

}
