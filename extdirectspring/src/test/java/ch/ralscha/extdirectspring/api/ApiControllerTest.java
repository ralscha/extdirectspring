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
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;
import static junit.framework.Assert.fail;
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
@SuppressWarnings("all")
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:/testApplicationContext.xml")
public class ApiControllerTest {

  @Inject
  private ApiController apiController;

  @Test
  public void testNoActionNamespaceDebug() throws IOException {
    MockHttpServletRequest request = new MockHttpServletRequest("POST", "/action/api-debug.js");
    MockHttpServletResponse response = new MockHttpServletResponse();
    apiController.api("test", null, "TEST_REMOTING_API", "TEST_POLLING_URLS", null, request, response);
    compare(response, allApis(null), "test", "TEST_REMOTING_API", "TEST_POLLING_URLS");

    request = new MockHttpServletRequest("POST", "/action/api.js");
    response = new MockHttpServletResponse();
    apiController.api("test", null, "TEST_REMOTING_API", "TEST_POLLING_URLS", null, request, response);
    compare(response, allApis(null), "test", "TEST_REMOTING_API", "TEST_POLLING_URLS");
  }

  @Test
  public void testWithActionNamespace() throws IOException {
    MockHttpServletRequest request = new MockHttpServletRequest("POST", "/action/api-debug.js");
    MockHttpServletResponse response = new MockHttpServletResponse();
    apiController.api("Ext.ns", "actionns", "TEST_REMOTING_API", "TEST_POLLING_URLS", null, request, response);
    compare(response, allApis("actionns"), "Ext.ns", "TEST_REMOTING_API", "TEST_POLLING_URLS");

    request = new MockHttpServletRequest("POST", "/action/api.js");
    response = new MockHttpServletResponse();
    apiController.api("Ext.ns", "actionns", "TEST_REMOTING_API", "TEST_POLLING_URLS", null, request, response);
    compare(response, allApis("actionns"), "Ext.ns", "TEST_REMOTING_API", "TEST_POLLING_URLS");
  }

  @Test
  public void testUnknownGroup() throws IOException {
    MockHttpServletRequest request = new MockHttpServletRequest("POST", "/action/api-debug.js");
    MockHttpServletResponse response = new MockHttpServletResponse();
    apiController.api("test", null, "TEST_REMOTING_API", "TEST_POLLING_URLS", "xy", request, response);
    compare(response, noApis(null), "test", "TEST_REMOTING_API", "TEST_POLLING_URLS");

    request = new MockHttpServletRequest("POST", "/action/api.js");
    response = new MockHttpServletResponse();
    apiController.api("test", null, "TEST_REMOTING_API", "TEST_POLLING_URLS", "xy", request, response);
    compare(response, noApis(null), "test", "TEST_REMOTING_API", "TEST_POLLING_URLS");
  }

  @Test
  public void testGroup1() throws IOException {
    MockHttpServletRequest request = new MockHttpServletRequest("POST", "/action/api-debug.js");
    MockHttpServletResponse response = new MockHttpServletResponse();
    apiController.api("Ext.ns", "actionns", "REMOTING_API", "POLLING_URLS", "group1", request, response);
    compare(response, group1Apis("actionns"), "Ext.ns", "REMOTING_API", "POLLING_URLS");

    request = new MockHttpServletRequest("POST", "/action/api.js");
    response = new MockHttpServletResponse();
    apiController.api("Ext.ns", "actionns", "REMOTING_API", "POLLING_URLS", "group1", request, response);
    compare(response, group1Apis("actionns"), "Ext.ns", "REMOTING_API", "POLLING_URLS");
  }

  @Test
  public void testGroup2() throws IOException {
    MockHttpServletRequest request = new MockHttpServletRequest("POST", "/action/api-debug.js");
    MockHttpServletResponse response = new MockHttpServletResponse();
    apiController.api("test", null, "TEST_REMOTING_API", "TEST_POLLING_URLS", "group2", request, response);
    compare(response, group2Apis(null), "test", "TEST_REMOTING_API", "TEST_POLLING_URLS");

    request = new MockHttpServletRequest("POST", "/action/api.js");
    response = new MockHttpServletResponse();
    apiController.api("test", null, "TEST_REMOTING_API", "TEST_POLLING_URLS", "group2", request, response);
    compare(response, group2Apis(null), "test", "TEST_REMOTING_API", "TEST_POLLING_URLS");
  }

  @Test
  public void testGroup3() throws IOException {
    MockHttpServletRequest request = new MockHttpServletRequest("POST", "/action/api-debug.js");
    MockHttpServletResponse response = new MockHttpServletResponse();
    apiController.api("Extns", "ns", "RAPI", "PURLS", "group3", request, response);
    compare(response, group3Apis("ns"), "Extns", "RAPI", "PURLS");

    request = new MockHttpServletRequest("POST", "/action/api.js");
    response = new MockHttpServletResponse();
    apiController.api("Extns", "ns", "RAPI", "PURLS", "group3", request, response);
    compare(response, group3Apis("ns"), "Extns", "RAPI", "PURLS");
  }

  @Test
  public void testGroup4() throws IOException {
    MockHttpServletRequest request = new MockHttpServletRequest("POST", "/action/api-debug.js");
    MockHttpServletResponse response = new MockHttpServletResponse();
    apiController.api("test", null, "TEST_REMOTING_API", "TEST_POLLING_URLS", "group4", request, response);
    compare(response, group4Apis(null), "test", "TEST_REMOTING_API", "TEST_POLLING_URLS");

    request = new MockHttpServletRequest("POST", "/action/api.js");
    response = new MockHttpServletResponse();
    apiController.api("test", null, "TEST_REMOTING_API", "TEST_POLLING_URLS", "group4", request, response);
    compare(response, group4Apis(null), "test", "TEST_REMOTING_API", "TEST_POLLING_URLS");
  }

  private RemotingApi noApis(String namespace) {
    RemotingApi remotingApi = new RemotingApi("http://localhost:80/action/router", namespace);
    return remotingApi;
  }

  private RemotingApi group1Apis(String namespace) {
    RemotingApi remotingApi = new RemotingApi("http://localhost:80/action/router", namespace);
    remotingApi.addAction("remoteProvider1", "method1", 0, false);
    return remotingApi;
  }

  private RemotingApi group2Apis(String namespace) {
    RemotingApi remotingApi = new RemotingApi("http://localhost:80/action/router", namespace);
    remotingApi.addAction("remoteProvider1", "method3", 3, false);
    remotingApi.addAction("remoteProvider1", "method5", 1, false);
    remotingApi.addAction("remoteProvider2", "method6", 1, false);
    remotingApi.addAction("remoteProvider2", "method7", 1, false);
    remotingApi.addAction("remoteProvider3", "update4", 1, false);
    remotingApi.addAction("formInfoController", "upload", 0, true);
    remotingApi.addPollingProvider("pollProvider", "handleMessage1", "message1");
    remotingApi.addPollingProvider("pollProvider", "handleMessage2", "message2");
    remotingApi.addPollingProvider("pollProvider", "message6", "message6");
    return remotingApi;
  }

  private RemotingApi group3Apis(String namespace) {
    RemotingApi remotingApi = new RemotingApi("http://localhost:80/action/router", namespace);
    remotingApi.addAction("remoteProvider1", "method9", 0, false);
    remotingApi.addAction("remoteProvider2", "method5", 1, false);
    remotingApi.addAction("remoteProvider3", "destroy", 1, false);
    remotingApi.addAction("remoteProvider4", "method1", 1, false);
    remotingApi.addAction("remoteProvider4", "method5", 1, false);
    remotingApi.addAction("formInfoController", "updateInfo", 0, true);
    remotingApi.addAction("formInfoController2", "updateInfo1", 0, true);
    remotingApi.addAction("formInfoController2", "updateInfo2", 0, true);
    remotingApi.addPollingProvider("pollProvider", "handleMessage5", "message5");
    return remotingApi;
  }

  private RemotingApi group4Apis(String namespace) {
    RemotingApi remotingApi = new RemotingApi("http://localhost:80/action/router", namespace);
    remotingApi.addPollingProvider("pollProvider", "handleMessage3", "message3");
    return remotingApi;
  }

  private RemotingApi allApis(String namespace) {
    RemotingApi remotingApi = new RemotingApi("http://localhost:80/action/router", namespace);
    remotingApi.addAction("remoteProvider1", "method1", 0, false);
    remotingApi.addAction("remoteProvider1", "method2", 0, false);
    remotingApi.addAction("remoteProvider1", "method3", 3, false);
    remotingApi.addAction("remoteProvider1", "method5", 1, false);
    remotingApi.addAction("remoteProvider1", "method6", 2, false);
    remotingApi.addAction("remoteProvider1", "method7", 0, false);
    remotingApi.addAction("remoteProvider1", "method8", 1, false);
    remotingApi.addAction("remoteProvider1", "method9", 0, false);

    remotingApi.addAction("remoteProvider2", "method1", 1, false);
    remotingApi.addAction("remoteProvider2", "method2", 1, false);
    remotingApi.addAction("remoteProvider2", "method3", 1, false);
    remotingApi.addAction("remoteProvider2", "method4", 1, false);
    remotingApi.addAction("remoteProvider2", "method5", 1, false);
    remotingApi.addAction("remoteProvider2", "method6", 1, false);
    remotingApi.addAction("remoteProvider2", "method7", 1, false);

    remotingApi.addAction("remoteProvider3", "create1", 1, false);
    remotingApi.addAction("remoteProvider3", "create2", 1, false);
    remotingApi.addAction("remoteProvider3", "update1", 1, false);
    remotingApi.addAction("remoteProvider3", "update2", 1, false);
    remotingApi.addAction("remoteProvider3", "update3", 1, false);
    remotingApi.addAction("remoteProvider3", "update4", 1, false);
    remotingApi.addAction("remoteProvider3", "destroy", 1, false);

    remotingApi.addAction("remoteProvider4", "method1", 1, false);
    remotingApi.addAction("remoteProvider4", "method2", 1, false);
    remotingApi.addAction("remoteProvider4", "method3", 1, false);
    remotingApi.addAction("remoteProvider4", "method4", 1, false);
    remotingApi.addAction("remoteProvider4", "method5", 1, false);
    remotingApi.addAction("remoteProvider4", "method6", 1, false);

    remotingApi.addAction("remoteProvider5", "method1", 1, false);

    remotingApi.addAction("formInfoController", "updateInfo", 0, true);
    remotingApi.addAction("formInfoController", "upload", 0, true);
    
    remotingApi.addAction("formInfoController2", "updateInfo1", 0, true);
    remotingApi.addAction("formInfoController2", "updateInfo2", 0, true);
    

    remotingApi.addPollingProvider("pollProvider", "handleMessage1", "message1");
    remotingApi.addPollingProvider("pollProvider", "handleMessage2", "message2");
    remotingApi.addPollingProvider("pollProvider", "handleMessage3", "message3");
    remotingApi.addPollingProvider("pollProvider", "handleMessage4", "message4");
    remotingApi.addPollingProvider("pollProvider", "handleMessage5", "message5");
    remotingApi.addPollingProvider("pollProvider", "message6", "message6");

    return remotingApi;
  }

  private void compare(MockHttpServletResponse response, RemotingApi remotingApi, String apiNs, String remotingApiVar,
      String pollingUrlsVar) throws JsonParseException, JsonMappingException, IOException {
    String content = response.getContentAsString();
    content = content.replace(";", ";\n");
    content = content.replace("{", "{\n");
    content = content.replace("}", "}\n");

    assertTrue(StringUtils.hasText(content));

    String[] lines = content.split("\n");
    String extNsLine = "Ext.ns('" + apiNs + "');";
    String remotingApiLine = apiNs + "." + remotingApiVar + " = {";
    String pollingApiLine = apiNs + "." + pollingUrlsVar + " = {";

    assertContains(extNsLine, lines);
    int startRemotingApi = assertContains(remotingApiLine, lines);

    int startPollingApi = lines.length;
    if (!remotingApi.getPollingProviders().isEmpty()) {
      startPollingApi = assertContains(pollingApiLine, lines);
    }

    if (remotingApi.getNamespace() != null) {
      String actionNs = "Ext.ns('" + remotingApi.getNamespace() + "');";
      assertContains(actionNs, lines);
    }

    String remotingJson = "{";
    for (int i = startRemotingApi + 1; i < startPollingApi; i++) {
      remotingJson += lines[i];
    }

    String pollingJson = "{";
    if (!remotingApi.getPollingProviders().isEmpty()) {
      for (int i = startPollingApi + 1; i < lines.length; i++) {
        pollingJson += lines[i];
      }
    }

    ObjectMapper mapper = new ObjectMapper();
    Map<String, Object> rootAsMap = mapper.readValue(remotingJson, Map.class);
    if (remotingApi.getNamespace() == null) {
      assertEquals(3, rootAsMap.size());
    } else {
      assertEquals(4, rootAsMap.size());
    }

    assertEquals(remotingApi.getUrl(), rootAsMap.get("url"));
    assertEquals("remoting", rootAsMap.get("type"));
    assertTrue(rootAsMap.containsKey("actions"));

    if (remotingApi.getNamespace() != null) {
      assertEquals(remotingApi.getNamespace(), rootAsMap.get("namespace"));
    }

    Map<String, Object> beans = (Map<String, Object>)rootAsMap.get("actions");

    assertEquals(remotingApi.getActions().size(), beans.size());
    for (String beanName : remotingApi.getActions().keySet()) {
      List<Map<String, Object>> actions = (List<Map<String, Object>>)beans.get(beanName);
      List<Action> expectedActions = remotingApi.getActions().get(beanName);
      compare(expectedActions, actions);
    }

    if (!remotingApi.getPollingProviders().isEmpty()) {
      mapper = new ObjectMapper();
      Map<String, Object> pollingMap = mapper.readValue(pollingJson, Map.class);
      assertEquals(remotingApi.getPollingProviders().size(), pollingMap.size());
      for (PollingProvider pp : remotingApi.getPollingProviders()) {
        String url = (String)pollingMap.get(pp.getEvent());
        assertNotNull(url);
        assertEquals(
            String.format("%s/%s/%s/%s", remotingApi.getUrl().replace("router", "poll"), pp.getBeanName(),
                pp.getMethod(), pp.getEvent()), url);
      }
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

}
