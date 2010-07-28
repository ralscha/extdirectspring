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
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
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
import ch.ralscha.extdirectspring.bean.ExtDirectStoreResponse;
import ch.ralscha.extdirectspring.mock.Row;
import ch.ralscha.extdirectspring.util.ExtDirectSpringUtil;

/**
 * Tests for {@link RouterController}.
 * 
 * @author Ralph Schaer
 */
@SuppressWarnings("all")
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:/testApplicationContext.xml")
public class RouterControllerRemote3Test {

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
  public void testCreateNoData() {
    Map<String, Object> storeRequest = new LinkedHashMap<String, Object>();
    storeRequest.put("records", new ArrayList<Row>());
    String json = createRequestJson("remoteProvider3", "create1", 1, storeRequest);
    List<ExtDirectResponse> responses = controller.router(request, response, Locale.ENGLISH, json);

    assertEquals(1, responses.size());
    ExtDirectResponse resp = responses.get(0);
    assertEquals("remoteProvider3", resp.getAction());
    assertEquals("create1", resp.getMethod());
    assertEquals("rpc", resp.getType());
    assertEquals(1, resp.getTid());
    assertNull(resp.getMessage());
    assertNull(resp.getWhere());
    assertNotNull(resp.getResult());

    ExtDirectStoreResponse<Row> storeResponse = (ExtDirectStoreResponse<Row>)resp.getResult();
    assertTrue(storeResponse.getRecords().isEmpty());
    assertNull(storeResponse.getTotal());
    assertTrue(storeResponse.isSuccess());
  }

  @Test
  public void testCreateWithData() {
    Map<String, Object> storeRequest = new LinkedHashMap<String, Object>();
    List<Row> rowsToUpdate = new ArrayList<Row>();
    rowsToUpdate.add(new Row(10, "Ralph", true, "109.55"));
    rowsToUpdate.add(new Row(23, "John", false, "23.12"));

    storeRequest.put("records", rowsToUpdate);
    String json = createRequestJson("remoteProvider3", "create1", 1, storeRequest);
    List<ExtDirectResponse> responses = controller.router(request, response, Locale.ENGLISH, json);

    assertEquals(1, responses.size());
    ExtDirectResponse resp = responses.get(0);
    assertEquals("remoteProvider3", resp.getAction());
    assertEquals("create1", resp.getMethod());
    assertEquals("rpc", resp.getType());
    assertEquals(1, resp.getTid());
    assertNull(resp.getMessage());
    assertNull(resp.getWhere());
    assertNotNull(resp.getResult());

    ExtDirectStoreResponse<Row> storeResponse = (ExtDirectStoreResponse<Row>)resp.getResult();
    assertNull(storeResponse.getTotal());
    assertTrue(storeResponse.isSuccess());
    assertEquals(2, storeResponse.getRecords().size());
    List<Row> returnedRows = new ArrayList<Row>(storeResponse.getRecords());
    Collections.sort(returnedRows);
    assertEquals(10, returnedRows.get(0).getId());
    assertEquals(23, returnedRows.get(1).getId());
  }

  @Test
  public void testCreateWithDataAndSupportedArguments() {
    Map<String, Object> storeRequest = new LinkedHashMap<String, Object>();
    List<Row> rowsToUpdate = new ArrayList<Row>();
    rowsToUpdate.add(new Row(10, "Ralph", false, "109.55"));

    storeRequest.put("records", rowsToUpdate);
    String json = createRequestJson("remoteProvider3", "create2", 1, storeRequest);
    List<ExtDirectResponse> responses = controller.router(request, response, Locale.ENGLISH, json);

    assertEquals(1, responses.size());
    ExtDirectResponse resp = responses.get(0);
    assertEquals("remoteProvider3", resp.getAction());
    assertEquals("create2", resp.getMethod());
    assertEquals("rpc", resp.getType());
    assertEquals(1, resp.getTid());
    assertNull(resp.getMessage());
    assertNull(resp.getWhere());
    assertNotNull(resp.getResult());

    ExtDirectStoreResponse<Row> storeResponse = (ExtDirectStoreResponse<Row>)resp.getResult();
    assertNull(storeResponse.getTotal());
    assertTrue(storeResponse.isSuccess());
    assertEquals(1, storeResponse.getRecords().size());
    List<Row> returnedRows = new ArrayList<Row>(storeResponse.getRecords());
    assertEquals(10, returnedRows.get(0).getId());
  }

  @Test
  public void testUpdate() {
    Map<String, Object> storeRequest = new LinkedHashMap<String, Object>();
    List<Row> rowsToUpdate = new ArrayList<Row>();
    rowsToUpdate.add(new Row(10, "Ralph", true, "109.55"));
    storeRequest.put("records", rowsToUpdate);
    executeUpdate(storeRequest, "update1");
  }

  @Test
  public void testUpdateWithRequestParam() {
    Map<String, Object> storeRequest = new LinkedHashMap<String, Object>();
    List<Row> rowsToUpdate = new ArrayList<Row>();
    rowsToUpdate.add(new Row(10, "Ralph", true, "109.55"));
    storeRequest.put("id", 10);
    storeRequest.put("records", rowsToUpdate);
    executeUpdate(storeRequest, "update2");
  }

  @Test
  public void testUpdateWithRequestParamDefaultValue() {
    Map<String, Object> storeRequest = new LinkedHashMap<String, Object>();
    List<Row> rowsToUpdate = new ArrayList<Row>();
    rowsToUpdate.add(new Row(10, "Ralph", true, "109.55"));
    storeRequest.put("records", rowsToUpdate);
    executeUpdate(storeRequest, "update3");
  }

  @Test
  public void testUpdateWithRequestParamOptional() {
    Map<String, Object> storeRequest = new LinkedHashMap<String, Object>();
    List<Row> rowsToUpdate = new ArrayList<Row>();
    rowsToUpdate.add(new Row(10, "Ralph", true, "109.55"));
    storeRequest.put("records", rowsToUpdate);
    executeUpdate(storeRequest, "update4");

    storeRequest = new LinkedHashMap<String, Object>();
    rowsToUpdate = new ArrayList<Row>();
    rowsToUpdate.add(new Row(10, "Ralph", true, "109.55"));
    storeRequest.put("records", rowsToUpdate);
    storeRequest.put("id", 11);
    executeUpdate(storeRequest, "update4");
  }

  private void executeUpdate(Map<String, Object> storeRequest, String method) {
    String json = createRequestJson("remoteProvider3", method, 1, storeRequest);
    List<ExtDirectResponse> responses = controller.router(request, response, Locale.ENGLISH, json);

    assertEquals(1, responses.size());
    ExtDirectResponse resp = responses.get(0);
    assertEquals("remoteProvider3", resp.getAction());
    assertEquals(method, resp.getMethod());
    assertEquals("rpc", resp.getType());
    assertEquals(1, resp.getTid());
    assertNull(resp.getMessage());
    assertNull(resp.getWhere());
    assertNotNull(resp.getResult());

    ExtDirectStoreResponse<Row> storeResponse = (ExtDirectStoreResponse<Row>)resp.getResult();
    assertNull(storeResponse.getTotal());
    assertTrue(storeResponse.isSuccess());
    assertEquals(1, storeResponse.getRecords().size());
    List<Row> returnedRows = new ArrayList<Row>(storeResponse.getRecords());
    assertEquals(10, returnedRows.get(0).getId());
  }

  @Test
  public void testDestroy() {
    Map<String, Object> storeRequest = new LinkedHashMap<String, Object>();
    List<Integer> rowsToUpdate = new ArrayList<Integer>();
    rowsToUpdate.add(10);

    storeRequest.put("records", rowsToUpdate);
    String json = createRequestJson("remoteProvider3", "destroy", 1, storeRequest);
    List<ExtDirectResponse> responses = controller.router(request, response, Locale.ENGLISH, json);

    assertEquals(1, responses.size());
    ExtDirectResponse resp = responses.get(0);
    assertEquals("remoteProvider3", resp.getAction());
    assertEquals("destroy", resp.getMethod());
    assertEquals("rpc", resp.getType());
    assertEquals(1, resp.getTid());
    assertNull(resp.getMessage());
    assertNull(resp.getWhere());
    assertNotNull(resp.getResult());

    ExtDirectStoreResponse<Integer> storeResponse = (ExtDirectStoreResponse<Integer>)resp.getResult();
    assertNull(storeResponse.getTotal());
    assertTrue(storeResponse.isSuccess());
    assertEquals(1, storeResponse.getRecords().size());
    List<Integer> returnedRows = new ArrayList<Integer>(storeResponse.getRecords());
    assertEquals(Integer.valueOf(10), returnedRows.get(0));
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
