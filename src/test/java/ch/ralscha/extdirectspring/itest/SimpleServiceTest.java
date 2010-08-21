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

import java.io.IOException;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.junit.Test;

public class SimpleServiceTest {

  @Test
  public void testSimpleApi() throws IllegalStateException, IOException {

    HttpClient client = new DefaultHttpClient();
    HttpGet get = new HttpGet("http://localhost:9998/controller/api-debug.js?group=itest");
    HttpResponse response = client.execute(get);
    HttpEntity entity = response.getEntity();
    assertNotNull(entity);
    String responseString = IOUtils.toString(entity.getContent());    
    entity.consumeContent();
    
    String[] lines = responseString.split("\n");
    assertEquals(12, lines.length);

    String[] expected = { 
        "Ext.ns('Ext.app');", 
        "",
        "Ext.app.REMOTING_API = {", 
        "\"actions\" : {", 
        "\"simpleService\" : [ {", 
        "\"name\" : \"toUpperCase\",",
        "\"len\" : 1", 
        "} ]", 
        "},", 
        "\"type\" : \"remoting\",",
        "\"url\" : \"http://localhost:9998/controller/router\"", 
        "};" };

    for (int i = 0; i < lines.length; i++) {
      assertEquals(expected[i], lines[i].trim());
    }

  }
  
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
    String responseString = IOUtils.toString(entity.getContent());
    String expected = "[{\"method\":\"toUpperCase\",\"type\":\"rpc\",\"result\":\"RALPH\",\"action\":\"simpleService\",\"tid\":1}]";
    assertEquals(expected, responseString);
    
  }

}
