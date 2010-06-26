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

package ch.ralscha.extdirectspring.controller;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import javax.inject.Inject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import ch.ralscha.extdirectspring.bean.ExtDirectRequest;
import ch.ralscha.extdirectspring.bean.ExtDirectResponse;
import ch.ralscha.extdirectspring.bean.ExtDirectStoreReadRequest;
import ch.ralscha.extdirectspring.bean.ExtDirectStoreResponse;
import ch.ralscha.extdirectspring.mock.Row;
import ch.ralscha.extdirectspring.util.ExtDirectSpringUtil;

/**
 * Tests for {@link RouterController}.
 *
 * @author Ralph Schaer
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:/testApplicationContext.xml")
public class RouterControllerRemote2Test {

  @Inject
  private RouterController controller;

  private MockHttpServletResponse response;
  private MockHttpServletRequest request;

  @Before
  public void beforeTest() {
    response = new MockHttpServletResponse();
    request = new MockHttpServletRequest();
  }

  @Test
  public void testNoArgumentsNoRequestParameters() {
    String json = createRequestJson("remoteProvider2", "method1", 1, null);
    List<ExtDirectResponse> responses = controller.router(request, response, Locale.ENGLISH, json);

    assertEquals(1, responses.size());
    ExtDirectResponse resp = responses.get(0);
    assertEquals("remoteProvider2", resp.getAction());
    assertEquals("method1", resp.getMethod());
    assertEquals("rpc", resp.getType());
    assertEquals(1, resp.getTid());
    assertTrue(resp.isSuccess());    
    assertNull(resp.getMessage());
    assertNull(resp.getWhere());
    assertNotNull(resp.getResult());
    
    List<Row> rows = (List<Row>)resp.getResult();
    assertEquals(100, rows.size());
  }
  
  @Test
  public void testNoArgumentsWithRequestParameters() {
    ExtDirectStoreReadRequest storeRead = new ExtDirectStoreReadRequest();
    storeRead.setQuery("ralph");
    
    String json = createRequestJson("remoteProvider2", "method1", 1, storeRead);
    List<ExtDirectResponse> responses = controller.router(request, response, Locale.ENGLISH, json);

    assertEquals(1, responses.size());
    ExtDirectResponse resp = responses.get(0);
    assertEquals("remoteProvider2", resp.getAction());
    assertEquals("method1", resp.getMethod());
    assertEquals("rpc", resp.getType());
    assertEquals(1, resp.getTid());
    assertTrue(resp.isSuccess());    
    assertNull(resp.getMessage());
    assertNull(resp.getWhere());
    assertNotNull(resp.getResult());
    
    List<Row> rows = (List<Row>)resp.getResult();
    assertEquals(100, rows.size());
  }
  
  @Test
  public void testReturnsNull() {
    String json = createRequestJson("remoteProvider2", "method2", 1, null);
    List<ExtDirectResponse> responses = controller.router(request, response, Locale.ENGLISH, json);

    assertEquals(1, responses.size());
    ExtDirectResponse resp = responses.get(0);
    assertEquals("remoteProvider2", resp.getAction());
    assertEquals("method2", resp.getMethod());
    assertEquals("rpc", resp.getType());
    assertEquals(1, resp.getTid());
    assertTrue(resp.isSuccess());    
    assertNull(resp.getMessage());
    assertNull(resp.getWhere());
    assertNull(resp.getResult());
  }  

  @Test
  public void testSupportedArguments() {
    String json = createRequestJson("remoteProvider2", "method3", 1, null);
    List<ExtDirectResponse> responses = controller.router(request, response, Locale.ENGLISH, json);

    assertEquals(1, responses.size());
    ExtDirectResponse resp = responses.get(0);
    assertEquals("remoteProvider2", resp.getAction());
    assertEquals("method3", resp.getMethod());
    assertEquals("rpc", resp.getType());
    assertEquals(1, resp.getTid());
    assertTrue(resp.isSuccess());    
    assertNull(resp.getMessage());
    assertNull(resp.getWhere());
    assertNotNull(resp.getResult());
    
    List<Row> rows = (List<Row>)resp.getResult();
    assertEquals(100, rows.size());
  }
  
  @Test
  public void testWithExtDirectStoreReadRequest() {
    ExtDirectStoreReadRequest storeRead = new ExtDirectStoreReadRequest();
    storeRead.setQuery("name");    
    ExtDirectResponse resp = executeWithExtDirectStoreReadRequest(storeRead);    
    ExtDirectStoreResponse<Row> storeResponse = (ExtDirectStoreResponse<Row>)resp.getResult();
    assertEquals(Integer.valueOf(50), storeResponse.getTotal());
    assertEquals(50, storeResponse.getRecords().size());
    for (Row row : storeResponse.getRecords()) {
      assertTrue(row.getName().startsWith("name"));
    }
    
    storeRead = new ExtDirectStoreReadRequest();
    storeRead.setQuery("firstname");    
    resp = executeWithExtDirectStoreReadRequest(storeRead);    
    storeResponse = (ExtDirectStoreResponse<Row>)resp.getResult();
    assertEquals(Integer.valueOf(50), storeResponse.getTotal());
    assertEquals(50, storeResponse.getRecords().size());
    for (Row row : storeResponse.getRecords()) {
      assertTrue(row.getName().startsWith("firstname"));
    }
    
    storeRead = new ExtDirectStoreReadRequest();
    storeRead.setQuery("");
    storeRead.setSort("id");
    storeRead.setDir("ASC");
    storeRead.setLimit(10);
    storeRead.setStart(10);
    resp = executeWithExtDirectStoreReadRequest(storeRead);    
    storeResponse = (ExtDirectStoreResponse<Row>)resp.getResult();
    assertEquals(Integer.valueOf(100), storeResponse.getTotal());
    assertEquals(10, storeResponse.getRecords().size());
    int id = 10;
    for (Row row : storeResponse.getRecords()) {
      assertEquals(id, row.getId());
      id++;
    }
    
    storeRead = new ExtDirectStoreReadRequest();
    storeRead.setQuery("");
    storeRead.setSort("id");
    storeRead.setDir("DESC");
    storeRead.setLimit(10);
    storeRead.setStart(20);
    resp = executeWithExtDirectStoreReadRequest(storeRead);    
    storeResponse = (ExtDirectStoreResponse<Row>)resp.getResult();
    assertEquals(Integer.valueOf(100), storeResponse.getTotal());
    assertEquals(10, storeResponse.getRecords().size());
    id = 79;
    for (Row row : storeResponse.getRecords()) {
      assertEquals(id, row.getId());
      id--;
    }
  }

  private ExtDirectResponse executeWithExtDirectStoreReadRequest(ExtDirectStoreReadRequest storeRead) {
    String json = createRequestJson("remoteProvider2", "method4", 1, storeRead);
    List<ExtDirectResponse> responses = controller.router(request, response, Locale.ENGLISH, json);

    assertEquals(1, responses.size());
    ExtDirectResponse resp = responses.get(0);
    
    assertEquals("remoteProvider2", resp.getAction());
    assertEquals("method4", resp.getMethod());
    assertEquals("rpc", resp.getType());
    assertEquals(1, resp.getTid());
    assertTrue(resp.isSuccess());    
    assertNull(resp.getMessage());
    assertNull(resp.getWhere());
    assertNotNull(resp.getResult());
    return resp;
  }
  
  @Test
  public void testWithAdditionalParameters() {
    Map<String,Object> readRequest = new HashMap<String,Object>();
    readRequest.put("id", 10);
    readRequest.put("query", "name");

    String json = createRequestJson("remoteProvider2", "method5", 1, readRequest);
    List<ExtDirectResponse> responses = controller.router(request, response, Locale.ENGLISH, json);

    assertEquals(1, responses.size());
    ExtDirectResponse resp = responses.get(0);
    
    assertEquals("remoteProvider2", resp.getAction());
    assertEquals("method5", resp.getMethod());
    assertEquals("rpc", resp.getType());
    assertEquals(1, resp.getTid());
    assertTrue(resp.isSuccess());    
    assertNull(resp.getMessage());
    assertNull(resp.getWhere());
    assertNotNull(resp.getResult());
    
    ExtDirectStoreResponse<Row> storeResponse = (ExtDirectStoreResponse<Row>)resp.getResult();
    assertEquals(Integer.valueOf(50), storeResponse.getTotal());
    assertEquals(50, storeResponse.getRecords().size());
    for (Row row : storeResponse.getRecords()) {
      assertTrue(row.getName().startsWith("name"));
    }
    
    
    readRequest = new HashMap<String,Object>();
    readRequest.put("query", "name");

    json = createRequestJson("remoteProvider2", "method5", 1, readRequest);
    responses = controller.router(request, response, Locale.ENGLISH, json);

    assertEquals(1, responses.size());
    resp = responses.get(0);
    
    assertEquals("remoteProvider2", resp.getAction());
    assertEquals("method5", resp.getMethod());
    assertEquals("rpc", resp.getType());
    assertEquals(1, resp.getTid());
    assertFalse(resp.isSuccess());    
    assertEquals("server error", resp.getMessage());
  }
  
  @Test
  public void testWithAdditionalParametersDefaultValue() {
    Map<String,Object> readRequest = new HashMap<String,Object>();
    readRequest.put("query", "firstname");

    String json = createRequestJson("remoteProvider2", "method6", 1, readRequest);
    List<ExtDirectResponse> responses = controller.router(request, response, Locale.ENGLISH, json);

    assertEquals(1, responses.size());
    ExtDirectResponse resp = responses.get(0);
    
    assertEquals("remoteProvider2", resp.getAction());
    assertEquals("method6", resp.getMethod());
    assertEquals("rpc", resp.getType());
    assertEquals(1, resp.getTid());
    assertTrue(resp.isSuccess());    
    assertNull(resp.getMessage());
    assertNull(resp.getWhere());
    assertNotNull(resp.getResult());
    
    ExtDirectStoreResponse<Row> storeResponse = (ExtDirectStoreResponse<Row>)resp.getResult();
    assertEquals(Integer.valueOf(50), storeResponse.getTotal());
    assertEquals(50, storeResponse.getRecords().size());
    for (Row row : storeResponse.getRecords()) {
      assertTrue(row.getName().startsWith("firstname"));
    }
  }
  
  @Test
  public void testWithAdditionalParametersOptional() {

    String json = createRequestJson("remoteProvider2", "method7", 1, null);
    List<ExtDirectResponse> responses = controller.router(request, response, Locale.ENGLISH, json);

    assertEquals(1, responses.size());
    ExtDirectResponse resp = responses.get(0);
    
    assertEquals("remoteProvider2", resp.getAction());
    assertEquals("method7", resp.getMethod());
    assertEquals("rpc", resp.getType());
    assertEquals(1, resp.getTid());
    assertTrue(resp.isSuccess());    
    assertNull(resp.getMessage());
    assertNull(resp.getWhere());
    assertNotNull(resp.getResult());
    
    List<Row> rows = (List<Row>)resp.getResult();
    assertEquals(100, rows.size());
    
    
    Map<String,Object> readRequest = new HashMap<String,Object>();
    readRequest.put("id", 11);
    readRequest.put("query", "");

    json = createRequestJson("remoteProvider2", "method7", 1, readRequest);
    responses = controller.router(request, response, Locale.ENGLISH, json);

    assertEquals(1, responses.size());
    resp = responses.get(0);
    
    assertEquals("remoteProvider2", resp.getAction());
    assertEquals("method7", resp.getMethod());
    assertEquals("rpc", resp.getType());
    assertEquals(1, resp.getTid());
    assertTrue(resp.isSuccess());    
    assertNull(resp.getMessage());
    assertNull(resp.getWhere());
    assertNotNull(resp.getResult());
    
    rows = (List<Row>)resp.getResult();
    assertEquals(100, rows.size());
  }
  
  private String createRequestJson(String action, String method, int tid, Object... data) {
    ExtDirectRequest dr = new ExtDirectRequest();
    dr.setAction(action);
    dr.setMethod(method);
    dr.setTid(tid);
    dr.setType("rpc");
    dr.setData(data);
    return ExtDirectSpringUtil.serializeObjectToJson(dr);
  }

}
