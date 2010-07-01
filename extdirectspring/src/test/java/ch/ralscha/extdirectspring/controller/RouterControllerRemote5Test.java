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
import java.math.BigDecimal;
import java.util.Calendar;
import java.util.GregorianCalendar;
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
import ch.ralscha.extdirectspring.bean.ExtDirectFormLoadResult;
import ch.ralscha.extdirectspring.bean.ExtDirectResponse;
import ch.ralscha.extdirectspring.mock.FormInfo;

/**
 * Tests for {@link RouterController}.
 *
 * @author Ralph Schaer
 */
@SuppressWarnings("all")
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:/testApplicationContext.xml")
public class RouterControllerRemote5Test {

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
  public void testFormLoad() {
    Map<String,Object> data = new HashMap<String,Object>();
    data.put("d", 3.141);
    String json = TestUtil.createRequestJson("remoteProvider4", "method1", 1, data);
    List<ExtDirectResponse> responses = controller.router(request, response, Locale.ENGLISH, json);
  
    assertEquals(1, responses.size());
    ExtDirectResponse resp = responses.get(0);
  
    checkFormLoadResult(resp, 3.141, 1);
  }
  
  @Test
  public void testFormLoadReturnsNull() {
    String json = TestUtil.createRequestJson("remoteProvider4", "method2", 1, null);
    List<ExtDirectResponse> responses = controller.router(request, response, Locale.ENGLISH, json);
  
    assertEquals(1, responses.size());
    ExtDirectResponse resp = responses.get(0);
  
    assertEquals("remoteProvider4", resp.getAction());
    assertEquals("method2", resp.getMethod());
    assertEquals(1, resp.getTid());
    assertEquals("rpc", resp.getType());
    assertNull(resp.getWhere());
    assertNull(resp.getMessage());
    assertNull(resp.getResult());
  }
  
  @Test
  public void testWithSupportedArguments() {
    String json = TestUtil.createRequestJson("remoteProvider4", "method3", 1, null);
    List<ExtDirectResponse> responses = controller.router(request, response, Locale.ENGLISH, json);
  
    assertEquals(1, responses.size());
    ExtDirectResponse resp = responses.get(0);
  
    assertEquals("remoteProvider4", resp.getAction());
    assertEquals("method3", resp.getMethod());
    assertEquals(1, resp.getTid());
    assertEquals("rpc", resp.getType());
    assertNull(resp.getWhere());
    assertNull(resp.getMessage());
    assertNull(resp.getResult());
  }
  
  
  @Test
  public void testWithRequestParam() {
    Map<String,Object> data = new HashMap<String,Object>();
    data.put("id", 10);
    String json = TestUtil.createRequestJson("remoteProvider4", "method4", 1, data);
    List<ExtDirectResponse> responses = controller.router(request, response, Locale.ENGLISH, json);
  
    assertEquals(1, responses.size());
    ExtDirectResponse resp = responses.get(0);
    
    assertEquals("remoteProvider4", resp.getAction());
    assertEquals("method4", resp.getMethod());
    assertEquals(1, resp.getTid());
    assertEquals("rpc", resp.getType());
    assertNull(resp.getWhere());
    assertNull(resp.getMessage());
    assertNotNull(resp.getResult());
  
    assertTrue(resp.getResult() instanceof ExtDirectFormLoadResult);
    ExtDirectFormLoadResult wrapper = (ExtDirectFormLoadResult)resp.getResult();
    assertTrue(wrapper.isSuccess());
    assertNotNull(wrapper.getData());
    assertTrue(wrapper.getData() instanceof FormInfo);
  }

  @Test
  public void testWithRequestParamDefaultValue() {
    String json = TestUtil.createRequestJson("remoteProvider4", "method5", 1, null);
    List<ExtDirectResponse> responses = controller.router(request, response, Locale.ENGLISH, json);
  
    assertEquals(1, responses.size());
    ExtDirectResponse resp = responses.get(0);
    
    assertEquals("remoteProvider4", resp.getAction());
    assertEquals("method5", resp.getMethod());
    assertEquals(1, resp.getTid());
    assertEquals("rpc", resp.getType());
    assertNull(resp.getWhere());
    assertNull(resp.getMessage());
    assertNotNull(resp.getResult());
  
    assertTrue(resp.getResult() instanceof ExtDirectFormLoadResult);
    ExtDirectFormLoadResult wrapper = (ExtDirectFormLoadResult)resp.getResult();
    assertTrue(wrapper.isSuccess());
    assertNull(wrapper.getData());
  }

  @Test
  public void testWithRequestParamOptional() {
    String json = TestUtil.createRequestJson("remoteProvider4", "method6", 1, null);
    List<ExtDirectResponse> responses = controller.router(request, response, Locale.ENGLISH, json);
  
    assertEquals(1, responses.size());
    ExtDirectResponse resp = responses.get(0);
    
    assertEquals("remoteProvider4", resp.getAction());
    assertEquals("method6", resp.getMethod());
    assertEquals(1, resp.getTid());
    assertEquals("rpc", resp.getType());
    assertNull(resp.getWhere());
    assertNull(resp.getMessage());
    assertNotNull(resp.getResult());
  
    assertTrue(resp.getResult() instanceof ExtDirectFormLoadResult);
    ExtDirectFormLoadResult wrapper = (ExtDirectFormLoadResult)resp.getResult();
    assertTrue(wrapper.isSuccess());
    assertEquals("TEST", wrapper.getData());
    
    
    Map<String,Object> data = new HashMap<String,Object>();
    data.put("id", 11);
    json = TestUtil.createRequestJson("remoteProvider4", "method6", 1, data);
    responses = controller.router(request, response, Locale.ENGLISH, json);
  
    assertEquals(1, responses.size());
    resp = responses.get(0);
    
    assertEquals("remoteProvider4", resp.getAction());
    assertEquals("method6", resp.getMethod());
    assertEquals(1, resp.getTid());
    assertEquals("rpc", resp.getType());
    assertNull(resp.getWhere());
    assertNull(resp.getMessage());
    assertNotNull(resp.getResult());
  
    assertTrue(resp.getResult() instanceof ExtDirectFormLoadResult);
    wrapper = (ExtDirectFormLoadResult)resp.getResult();
    assertTrue(wrapper.isSuccess());
    assertEquals("TEST", wrapper.getData());
  }
  
  
  private void checkFormLoadResult(ExtDirectResponse resp, double back, int tid) {
    assertEquals("remoteProvider4", resp.getAction());
    assertEquals("method1", resp.getMethod());
    assertEquals(tid, resp.getTid());
    assertEquals("rpc", resp.getType());
    assertNull(resp.getWhere());
    assertNull(resp.getMessage());
    assertNotNull(resp.getResult());
  
    assertTrue(resp.getResult() instanceof ExtDirectFormLoadResult);
    ExtDirectFormLoadResult wrapper = (ExtDirectFormLoadResult)resp.getResult();
    assertTrue(wrapper.isSuccess());
    assertNotNull(wrapper.getData());
    assertTrue(wrapper.getData() instanceof FormInfo);
    FormInfo info = (FormInfo)wrapper.getData();
  
    assertTrue(Double.compare(back, info.getBack()) == 0);
    assertEquals(true, info.isAdmin());
    assertEquals(31, info.getAge());
    assertEquals("Bob", info.getName());
    assertEquals(new BigDecimal("10000.55"), info.getSalary());
    assertEquals(new GregorianCalendar(1980, Calendar.JANUARY, 15).getTime(), info.getBirthday());
  }
  


  @Test
  public void testMultipleRequests() {
    String json1 = TestUtil.createRequestJson("remoteProvider", "method1", 1, 3, 2.5, "string.param");
    String json2 = TestUtil.createRequestJson("remoteProvider1", "method4", 2, 3, 2.5, "string.param");
    String json3 = TestUtil.createRequestJson("remoteProvider1", "method1", 3, null);
    
    Map<String,Object> data = new HashMap<String,Object>();
    data.put("d", 1.1);
    String json4 = TestUtil.createRequestJson("remoteProvider4", "method1", 4, data);
    
    data = new HashMap<String,Object>();
    data.put("d", 2.2);
    String json5 = TestUtil.createRequestJson("remoteProvider4", "method1", 5, data);
    
    String json6 = TestUtil.createRequestJson("remoteProvider1", "method6", 6, 10, 20);

    String requestBody = String.format("[%s,%s,%s,%s,%s,%s]", json1, json2, json3, json4, json5, json6);

    List<ExtDirectResponse> responses = controller.router(request, response, Locale.ENGLISH, requestBody);

    assertEquals(6, responses.size());
    RouterControllerRemote1Test.checkBeanNotFoundResponse(responses.get(0));
    RouterControllerRemote1Test.checkMethodNotFoundResponse(responses.get(1));
    RouterControllerRemote1Test.checkNoParametersResponse(responses.get(2), 3);
    
    checkFormLoadResult(responses.get(3), 1.1, 4);
    checkFormLoadResult(responses.get(4), 2.2, 5);
    
    RouterControllerRemote1Test.checkIntParameterResult(responses.get(5), 6);
  }
}
