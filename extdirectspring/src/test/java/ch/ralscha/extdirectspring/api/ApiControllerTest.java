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

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;
import java.io.IOException;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import javax.inject.Inject;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.StringUtils;

/**
 * Tests for {@link ApiController}.
 *
 * @author Ralph Schaer
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:/testApplicationContext.xml")
public class ApiControllerTest {

  @Inject
  private ApiController apiController;

  @SuppressWarnings("unchecked")
  @Test
  public void noActionNamespaceApiTest() throws JsonParseException, JsonMappingException, IOException {

    MockHttpServletRequest request = new MockHttpServletRequest("POST", "/action/api-debug.js");
    String response = apiController.api("test", null, "TEST_REMOTING_API", "TEST_POLLING_URLS", null, request);

    assertTrue(StringUtils.hasText(response));

    String[] lines = response.split("\n");
    assertEquals(40, lines.length);

    assertEquals("Ext.ns('test');", lines[0]);
    assertTrue(lines[2].startsWith("test.TEST_REMOTING_API = {"));
    assertEquals("test.TEST_POLLING_URLS = {", lines[34]);
    assertEquals("  message : 'http://localhost:80/action/poll/poll/handleMessagePoll/message',", lines[35]);

    assertEquals("  message2 : 'http://localhost:80/action/poll/poll/handleMessage2/message2',", lines[36]);
    assertEquals("  message3 : 'http://localhost:80/action/poll/poll/handleMessage3/message3',", lines[37]);
    assertEquals("  message4 : 'http://localhost:80/action/poll/poll/handleMessage4/message4'", lines[38]);

    assertEquals("};", lines[32]);
    assertEquals("};", lines[39]);

    String configJson = "{";
    for (int i = 3; i < 32; i++) {
      configJson += lines[i];
    }
    configJson += "};";
    
    ObjectMapper mapper = new ObjectMapper();
    Map<Object, ? > rootAsMap = mapper.readValue(configJson, Map.class);
    assertEquals(3, rootAsMap.size());

    assertEquals("http://localhost:80/action/router", rootAsMap.get("url"));
    assertEquals("remoting", rootAsMap.get("type"));
    assertTrue(rootAsMap.containsKey("actions"));
    Map< ? , ? > actions = (Map< ? , ? >)rootAsMap.get("actions");
    
    
    assertEquals(3, actions.size());

    List values = (List)actions.get("formInfoController");
    assertEquals(1, values.size());
    
    Map< ? , ? > method = (Map< ? , ? >)values.get(0);
    assertEquals(3, method.size());
    assertEquals("updateInfo", method.get("name"));
    assertEquals(0, method.get("len"));
    assertEquals(true, method.get("formHandler"));
    
    values = (List)actions.get("secondBeanWithExtDirectMethods");
    assertEquals(2, values.size());

    Collections.sort(values, new Comparator() {
      @Override
      public int compare(Object o1, Object o2) {
        Map< ? , ? > m1 = (Map< ? , ? >)o1;
        Map< ? , ? > m2 = (Map< ? , ? >)o2;
        return ((String)m1.get("name")).compareTo((String)m2.get("name"));
      }
    });

    method = (Map< ? , ? >)values.get(0);
    assertEquals(2, method.size());
    assertEquals("add", method.get("name"));
    assertEquals(2, method.get("len"));

    method = (Map< ? , ? >)values.get(1);
    assertEquals(2, method.size());
    assertEquals("getFormInfo", method.get("name"));
    assertEquals(1, method.get("len"));

    values = (List)actions.get("beanWithExtDirectMethods");
    Collections.sort(values, new Comparator() {

      @Override
      public int compare(Object o1, Object o2) {
        Map< ? , ? > m1 = (Map< ? , ? >)o1;
        Map< ? , ? > m2 = (Map< ? , ? >)o2;
        return ((String)m1.get("name")).compareTo((String)m2.get("name"));
      }
    });

    assertEquals(4, values.size());

    method = (Map< ? , ? >)values.get(2);
    assertEquals(2, method.size());
    assertEquals("getConfig", method.get("name"));
    assertEquals(0, method.get("len"));

    method = (Map< ? , ? >)values.get(0);
    assertEquals(2, method.size());
    assertEquals("doWork1", method.get("name"));
    assertEquals(0, method.get("len"));

    method = (Map< ? , ? >)values.get(1);
    assertEquals(2, method.size());
    assertEquals("doWork2", method.get("name"));
    assertEquals(3, method.get("len"));

    method = (Map< ? , ? >)values.get(3);
    assertEquals(2, method.size());
    assertEquals("hasPermission", method.get("name"));
    assertEquals(1, method.get("len"));
  }

  @SuppressWarnings("unchecked")
  @Test
  public void withActionNamespaceApiTest() throws JsonParseException, JsonMappingException, IOException {

    MockHttpServletRequest request = new MockHttpServletRequest("POST", "/action/api-debug.js");
    String response = apiController.api("Ext.app", "actionns", "REMOTING_API", "POLLING_URLS", null, request); 
    assertTrue(StringUtils.hasText(response));

    String[] lines = response.split("\n");
    assertEquals(43, lines.length);

    assertEquals("Ext.ns('Ext.app');", lines[0]);
    assertEquals("Ext.ns('actionns');", lines[2]);
    assertTrue(lines[4].startsWith("Ext.app.REMOTING_API = {"));
    assertEquals("Ext.app.POLLING_URLS = {", lines[37]);
    assertEquals("  message : 'http://localhost:80/action/poll/poll/handleMessagePoll/message',", lines[38]);
    assertEquals("  message2 : 'http://localhost:80/action/poll/poll/handleMessage2/message2',", lines[39]);
    assertEquals("  message3 : 'http://localhost:80/action/poll/poll/handleMessage3/message3',", lines[40]);
    assertEquals("  message4 : 'http://localhost:80/action/poll/poll/handleMessage4/message4'", lines[41]);
    assertEquals("};", lines[35]);
    assertEquals("};", lines[42]);

    String configJson = "{";
    for (int i = 5; i < 35; i++) {
      configJson += lines[i];
    }
    configJson += "};";
    
    ObjectMapper mapper = new ObjectMapper();
    Map<Object, ? > rootAsMap = mapper.readValue(configJson, Map.class);
    assertEquals(4, rootAsMap.size());
    assertEquals("http://localhost:80/action/router", rootAsMap.get("url"));
    assertEquals("remoting", rootAsMap.get("type"));
    assertEquals("actionns", rootAsMap.get("namespace"));
    assertTrue(rootAsMap.containsKey("actions"));
  }
}
