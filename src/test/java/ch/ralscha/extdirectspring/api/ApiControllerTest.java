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

import static junit.framework.Assert.*;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import javax.inject.Inject;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
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

  @Test
  public void testNoActionNamespace() throws IOException {

    RemotingApi remotingApi = new RemotingApi("http://localhost:80/action/router", null);
    remotingApi.addAction("remoteProvider1", "method1", 0, false);
    remotingApi.addAction("remoteProvider1", "method2", 0, false);
    remotingApi.addAction("remoteProvider1", "method3", 3, false);
    remotingApi.addAction("remoteProvider1", "method5", 1, false);
    remotingApi.addAction("remoteProvider1", "method6", 2, false);
    remotingApi.addAction("remoteProvider1", "method7", 0, false);
    remotingApi.addAction("remoteProvider1", "method8", 1, false);
    remotingApi.addAction("remoteProvider1", "method9", 0, false);
    remotingApi.addAction("remoteProvider1", "method10", 1, false);
    remotingApi.addAction("remoteProvider1", "method11", 0, false);

    remotingApi.addAction("remoteProvider2", "method1", 0, false);
    remotingApi.addAction("remoteProvider2", "method2", 0, false);
    remotingApi.addAction("remoteProvider2", "method3", 0, false);
    remotingApi.addAction("remoteProvider2", "method4", 1, false);
    remotingApi.addAction("remoteProvider2", "method5", 1, false);
    remotingApi.addAction("remoteProvider2", "method6", 1, false);
    remotingApi.addAction("remoteProvider2", "method7", 0, false);

    remotingApi.addAction("remoteProvider3", "create1", 1, false);
    remotingApi.addAction("remoteProvider3", "create2", 1, false);
    remotingApi.addAction("remoteProvider3", "update1", 1, false);
    remotingApi.addAction("remoteProvider3", "update2", 1, false);
    remotingApi.addAction("remoteProvider3", "update3", 1, false);
    remotingApi.addAction("remoteProvider3", "update4", 1, false);
    remotingApi.addAction("remoteProvider3", "destroy", 1, false);
    
    remotingApi.addAction("formInfoController", "updateInfo", 0, true);
    remotingApi.addAction("formInfoController", "upload", 0, true);

    remotingApi.addPollingProvider("pollProvider", "handleMessage1", "message1");
    remotingApi.addPollingProvider("pollProvider", "handleMessage2", "message2");
    remotingApi.addPollingProvider("pollProvider", "handleMessage3", "message3");
    remotingApi.addPollingProvider("pollProvider", "handleMessage4", "message4");
    remotingApi.addPollingProvider("pollProvider", "handleMessage5", "message5");

    MockHttpServletRequest request = new MockHttpServletRequest("POST", "/action/api-debug.js");
    MockHttpServletResponse response = new MockHttpServletResponse();

    apiController.api("test", null, "TEST_REMOTING_API", "TEST_POLLING_URLS", null, request, response);

    compare(response, remotingApi, "test", "TEST_REMOTING_API", "TEST_POLLING_URLS");

  }

  private void compare(MockHttpServletResponse response, RemotingApi remotingApi, String apiNs, String remotingApiVar, String pollingUrlsVar)
      throws JsonParseException, JsonMappingException, IOException {
    String content = response.getContentAsString();
    assertTrue(StringUtils.hasText(content));

    String[] lines = content.split("\n");
    String extNsLine = "Ext.ns('" + apiNs + "');";
    String remotingApiLine = apiNs + "." + remotingApiVar + " = {";
    String pollingApiLine = apiNs + "." + pollingUrlsVar + " = {";

    assertContains(extNsLine, lines);
    int startRemotingApi = assertContains(remotingApiLine, lines);
    int startPollingApi = assertContains(pollingApiLine, lines);

    String remotingJson = "{";
    for (int i = startRemotingApi + 1; i < startPollingApi; i++) {
      remotingJson += lines[i];
    }
    
    String pollingJson = "{";
    for (int i = startPollingApi + 1; i < lines.length; i++) {
      pollingJson += lines[i];
    }

    ObjectMapper mapper = new ObjectMapper();
    Map<String, Object> rootAsMap = mapper.readValue(remotingJson, Map.class);
    assertEquals(3, rootAsMap.size());

    assertEquals(remotingApi.getUrl(), rootAsMap.get("url"));
    assertEquals("remoting", rootAsMap.get("type"));
    assertTrue(rootAsMap.containsKey("actions"));
    Map<String, Object> beans = (Map<String, Object>)rootAsMap.get("actions");

    assertEquals(remotingApi.getActions().size(), beans.size());
    for (String beanName : remotingApi.getActions().keySet()) {
      List<Map<String, Object>> actions = (List<Map<String, Object>>)beans.get(beanName);
      List<Action> expectedActions = remotingApi.getActions().get(beanName);
      compare(expectedActions, actions);
    }

    mapper = new ObjectMapper();
    Map<String, Object> pollingMap = mapper.readValue(pollingJson, Map.class);
    assertEquals(remotingApi.getPollingProviders().size(), pollingMap.size());
    for (PollingProvider pp : remotingApi.getPollingProviders()) {
      String url = (String)pollingMap.get(pp.getEvent());
      assertNotNull(url);
      assertEquals(String.format("%s/%s/%s/%s", remotingApi.getUrl().replace("router", "poll"), pp.getBeanName(), pp.getMethod(), pp
          .getEvent()), url);
    }

  }

  private void compare(List<Action> expectedActions, List<Map<String, Object>> actions) {
    assertEquals(expectedActions.size(), actions.size());
    for (Action expectedAction : expectedActions) {
      Map<String, Object> action = null;
      for (Map<String, Object> map : actions) {
        if (map.get("name").equals(expectedAction.getName())) {
          action = map;
          break;
        }
      }
      assertNotNull(action);
      assertEquals(expectedAction.getName(), action.get("name"));
      assertEquals(expectedAction.getLen(), action.get("len"));
      if (expectedAction.isFormHandler()) {
        assertEquals(expectedAction.isFormHandler(), action.get("formHandler"));
      } else {
        assertFalse(action.containsKey("formHandler"));
      }
    }
  }

  private int assertContains(String extNsLine, String[] lines) {
    if (lines == null) {
      fail("no lines");
    }

    int lineCount = 0;
    for (String line : lines) {
      if (line.startsWith(extNsLine)) {
        return lineCount;
      }
      lineCount++;
    }
    fail("lines does not contain : " + extNsLine);
    return -1;
  }

//  @SuppressWarnings("unchecked")
//  @Test
//  public void noActionNamespaceApiTest() throws JsonParseException, JsonMappingException, IOException {
//
//    MockHttpServletRequest request = new MockHttpServletRequest("POST", "/action/api-debug.js");
//    MockHttpServletResponse response = new MockHttpServletResponse();
//    
//    apiController.api("test", null, "TEST_REMOTING_API", "TEST_POLLING_URLS", null, request, response);
//    String content = response.getContentAsString();
//    assertTrue(StringUtils.hasText(content));
//
//    String[] lines = content.split("\n");
//    assertEquals(40, lines.length);
//
//    assertEquals("Ext.ns('test');", lines[0]);
//    assertTrue(lines[2].startsWith("test.TEST_REMOTING_API = {"));
//    assertEquals("test.TEST_POLLING_URLS = {", lines[34]);
//    assertEquals("  message : 'http://localhost:80/action/poll/poll/handleMessagePoll/message',", lines[35]);
//
//    assertEquals("  message2 : 'http://localhost:80/action/poll/poll/handleMessage2/message2',", lines[36]);
//    assertEquals("  message3 : 'http://localhost:80/action/poll/poll/handleMessage3/message3',", lines[37]);
//    assertEquals("  message4 : 'http://localhost:80/action/poll/poll/handleMessage4/message4'", lines[38]);
//
//    assertEquals("};", lines[32]);
//    assertEquals("};", lines[39]);
//
//    String configJson = "{";
//    for (int i = 3; i < 32; i++) {
//      configJson += lines[i];
//    }
//    configJson += "};";
//    
//    ObjectMapper mapper = new ObjectMapper();
//    Map<Object, ? > rootAsMap = mapper.readValue(configJson, Map.class);
//    assertEquals(3, rootAsMap.size());
//
//    assertEquals("http://localhost:80/action/router", rootAsMap.get("url"));
//    assertEquals("remoting", rootAsMap.get("type"));
//    assertTrue(rootAsMap.containsKey("actions"));
//    Map< ? , ? > actions = (Map< ? , ? >)rootAsMap.get("actions");
//    
//    
//    assertEquals(3, actions.size());
//
//    List values = (List)actions.get("formInfoController");
//    assertEquals(1, values.size());
//    
//    Map< ? , ? > method = (Map< ? , ? >)values.get(0);
//    assertEquals(3, method.size());
//    assertEquals("updateInfo", method.get("name"));
//    assertEquals(0, method.get("len"));
//    assertEquals(true, method.get("formHandler"));
//    
//    values = (List)actions.get("secondBeanWithExtDirectMethods");
//    assertEquals(2, values.size());
//
//    Collections.sort(values, new Comparator() {
//      @Override
//      public int compare(Object o1, Object o2) {
//        Map< ? , ? > m1 = (Map< ? , ? >)o1;
//        Map< ? , ? > m2 = (Map< ? , ? >)o2;
//        return ((String)m1.get("name")).compareTo((String)m2.get("name"));
//      }
//    });
//
//    method = (Map< ? , ? >)values.get(0);
//    assertEquals(2, method.size());
//    assertEquals("add", method.get("name"));
//    assertEquals(2, method.get("len"));
//
//    method = (Map< ? , ? >)values.get(1);
//    assertEquals(2, method.size());
//    assertEquals("getFormInfo", method.get("name"));
//    assertEquals(1, method.get("len"));
//
//    values = (List)actions.get("beanWithExtDirectMethods");
//    Collections.sort(values, new Comparator() {
//
//      @Override
//      public int compare(Object o1, Object o2) {
//        Map< ? , ? > m1 = (Map< ? , ? >)o1;
//        Map< ? , ? > m2 = (Map< ? , ? >)o2;
//        return ((String)m1.get("name")).compareTo((String)m2.get("name"));
//      }
//    });
//
//    assertEquals(4, values.size());
//
//    method = (Map< ? , ? >)values.get(2);
//    assertEquals(2, method.size());
//    assertEquals("getConfig", method.get("name"));
//    assertEquals(0, method.get("len"));
//
//    method = (Map< ? , ? >)values.get(0);
//    assertEquals(2, method.size());
//    assertEquals("doWork1", method.get("name"));
//    assertEquals(0, method.get("len"));
//
//    method = (Map< ? , ? >)values.get(1);
//    assertEquals(2, method.size());
//    assertEquals("doWork2", method.get("name"));
//    assertEquals(3, method.get("len"));
//
//    method = (Map< ? , ? >)values.get(3);
//    assertEquals(2, method.size());
//    assertEquals("hasPermission", method.get("name"));
//    assertEquals(1, method.get("len"));
//  }
//
//  @SuppressWarnings("unchecked")
//  @Test
//  public void withActionNamespaceApiTest() throws JsonParseException, JsonMappingException, IOException {
//
//    MockHttpServletRequest request = new MockHttpServletRequest("POST", "/action/api-debug.js");
//    MockHttpServletResponse mockResponse = new MockHttpServletResponse();
//    apiController.api("Ext.app", "actionns", "REMOTING_API", "POLLING_URLS", null, request, mockResponse); 
//    String response = mockResponse.getContentAsString();
//    assertTrue(StringUtils.hasText(response));
//
//    String[] lines = response.split("\n");
//    assertEquals(43, lines.length);
//
//    assertEquals("Ext.ns('Ext.app');", lines[0]);
//    assertEquals("Ext.ns('actionns');", lines[2]);
//    assertTrue(lines[4].startsWith("Ext.app.REMOTING_API = {"));
//    assertEquals("Ext.app.POLLING_URLS = {", lines[37]);
//    assertEquals("  message : 'http://localhost:80/action/poll/poll/handleMessagePoll/message',", lines[38]);
//    assertEquals("  message2 : 'http://localhost:80/action/poll/poll/handleMessage2/message2',", lines[39]);
//    assertEquals("  message3 : 'http://localhost:80/action/poll/poll/handleMessage3/message3',", lines[40]);
//    assertEquals("  message4 : 'http://localhost:80/action/poll/poll/handleMessage4/message4'", lines[41]);
//    assertEquals("};", lines[35]);
//    assertEquals("};", lines[42]);
//
//    String configJson = "{";
//    for (int i = 5; i < 35; i++) {
//      configJson += lines[i];
//    }
//    configJson += "};";
//    
//    ObjectMapper mapper = new ObjectMapper();
//    Map<Object, ? > rootAsMap = mapper.readValue(configJson, Map.class);
//    assertEquals(4, rootAsMap.size());
//    assertEquals("http://localhost:80/action/router", rootAsMap.get("url"));
//    assertEquals("remoting", rootAsMap.get("type"));
//    assertEquals("actionns", rootAsMap.get("namespace"));
//    assertTrue(rootAsMap.containsKey("actions"));
//  }
}
