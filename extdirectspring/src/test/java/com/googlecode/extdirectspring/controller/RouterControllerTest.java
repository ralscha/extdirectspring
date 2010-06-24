/**
 * Copyright 2010 Ralph Schaer
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

package com.googlecode.extdirectspring.controller;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import java.math.BigDecimal;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;
import javax.inject.Inject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.annotation.ExpectedException;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import com.googlecode.extdirectspring.bean.ExtDirectFormLoadResult;
import com.googlecode.extdirectspring.bean.ExtDirectPollResponse;
import com.googlecode.extdirectspring.bean.ExtDirectResponse;
import com.googlecode.extdirectspring.mock.FormInfo;

/**
 * Tests for {@link RouterController}.
 *
 * @author mansari
 * @author Ralph Schaer
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:/testApplicationContext.xml")
public class RouterControllerTest {

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
  public void testBeanNotFound() {
    String requestBody = "{\"action\":\"Remoting\",\"method\":\"doWork3\",\"data\":[3,2.5, \"string.param\"],\"type\":\"rpc\",\"tid\":2}";

    List<ExtDirectResponse> responses = controller.router(request, response, Locale.ENGLISH, requestBody);

    assertEquals(1, responses.size());
    ExtDirectResponse resp = responses.get(0);
    assertEquals("Remoting", resp.getAction());
    assertEquals("doWork3", resp.getMethod());
    assertEquals("rpc", resp.getType());
    assertEquals(2, resp.getTid());
    assertFalse(resp.isSuccess());
    assertNull(resp.getResult());
    assertEquals("server error", resp.getMessage());
    assertNull(resp.getWhere());
  }

  @Test
  public void testMethodNotFound() {

    String requestBody = "{\"action\":\"beanWithExtDirectMethods\",\"method\":\"doWork3\",\"data\":[3,2.5, \"string.param\"],\"type\":\"rpc\",\"tid\":2}";
    List<ExtDirectResponse> responses = controller.router(request, response, Locale.ENGLISH, requestBody);

    assertEquals(1, responses.size());
    ExtDirectResponse resp = responses.get(0);
    assertEquals("beanWithExtDirectMethods", resp.getAction());
    assertEquals("doWork3", resp.getMethod());
    assertEquals("rpc", resp.getType());
    assertEquals(2, resp.getTid());
    assertFalse(resp.isSuccess());
    assertNull(resp.getResult());
    assertEquals("server error", resp.getMessage());
    assertNull(resp.getWhere());

  }

  @Test
  public void testSingleValidRequestWithNoParams() {
    String requestBody = "{\"action\":\"beanWithExtDirectMethods\",\"method\":\"doWork1\",\"data\":null,\"type\":\"rpc\",\"tid\":2}";
    List<ExtDirectResponse> responses = controller.router(request, response, Locale.ENGLISH, requestBody);

    assertEquals(1, responses.size());
    ExtDirectResponse resp = responses.get(0);
    assertEquals("beanWithExtDirectMethods", resp.getAction());
    assertEquals("doWork1", resp.getMethod());
    assertEquals(2, resp.getTid());
    assertEquals("rpc", resp.getType());
    assertNull(resp.getWhere());
    assertTrue(resp.isSuccess());
    assertNull(resp.getMessage());
    assertEquals("doWork1.called", resp.getResult());
  }

  @Test
  public void testSingleValidRequestWithParams() {
    String requestBody = "{\"action\":\"beanWithExtDirectMethods\",\"method\":\"doWork2\",\"data\":[3,2.5, \"string.param\"],\"type\":\"rpc\",\"tid\":3}";
    List<ExtDirectResponse> responses = controller.router(request, response, Locale.ENGLISH, requestBody);

    assertEquals(1, responses.size());
    ExtDirectResponse resp = responses.get(0);
    assertEquals("beanWithExtDirectMethods", resp.getAction());
    assertEquals("doWork2", resp.getMethod());
    assertEquals(3, resp.getTid());
    assertEquals("rpc", resp.getType());
    assertNull(resp.getWhere());
    assertTrue(resp.isSuccess());
    assertNull(resp.getMessage());
    assertEquals("doWork2.called-3-2.5-string.param", resp.getResult());
  }

  @Test
  public void testAdd() {
    String requestBody = "{\"action\":\"secondBeanWithExtDirectMethods\",\"method\":\"add\",\"data\":[2,3],\"type\":\"rpc\",\"tid\":124}";
    List<ExtDirectResponse> responses = controller.router(request, response, Locale.ENGLISH, requestBody);

    assertEquals(1, responses.size());
    ExtDirectResponse resp = responses.get(0);
    assertEquals("secondBeanWithExtDirectMethods", resp.getAction());
    assertEquals("add", resp.getMethod());
    assertEquals(124, resp.getTid());
    assertEquals("rpc", resp.getType());
    assertNull(resp.getWhere());
    assertTrue(resp.isSuccess());
    assertNull(resp.getMessage());
    assertEquals(5, resp.getResult());
  }

  @Test
  public void testHasPermissionTrue() {
    String requestBody = "{\"action\":\"beanWithExtDirectMethods\",\"method\":\"hasPermission\",\"data\":[\"ralph\"],\"type\":\"rpc\",\"tid\":1}";
    List<ExtDirectResponse> responses = controller.router(request, response, Locale.ENGLISH, requestBody);

    assertEquals(1, responses.size());
    ExtDirectResponse resp = responses.get(0);
    assertEquals("beanWithExtDirectMethods", resp.getAction());
    assertEquals("hasPermission", resp.getMethod());
    assertEquals(1, resp.getTid());
    assertEquals("rpc", resp.getType());
    assertNull(resp.getWhere());
    assertTrue(resp.isSuccess());
    assertNull(resp.getMessage());
    assertEquals(true, resp.getResult());
  }

  @Test
  public void testHasPermissionFalse() {
    String requestBody = "{\"action\":\"beanWithExtDirectMethods\",\"method\":\"hasPermission\",\"data\":[\"joe\"],\"type\":\"rpc\",\"tid\":1}";
    List<ExtDirectResponse> responses = controller.router(request, response, Locale.ENGLISH, requestBody);

    assertEquals(1, responses.size());
    ExtDirectResponse resp = responses.get(0);
    assertEquals("beanWithExtDirectMethods", resp.getAction());
    assertEquals("hasPermission", resp.getMethod());
    assertEquals(1, resp.getTid());
    assertEquals("rpc", resp.getType());
    assertNull(resp.getWhere());
    assertTrue(resp.isSuccess());
    assertNull(resp.getMessage());
    assertEquals(false, resp.getResult());
  }

  @Test
  public void testHasPermissionNull() {
    String requestBody = "{\"action\":\"beanWithExtDirectMethods\",\"method\":\"hasPermission\",\"data\":[\"mike\"],\"type\":\"rpc\",\"tid\":1}";
    List<ExtDirectResponse> responses = controller.router(request, response, Locale.ENGLISH, requestBody);

    assertEquals(1, responses.size());
    ExtDirectResponse resp = responses.get(0);
    assertEquals("beanWithExtDirectMethods", resp.getAction());
    assertEquals("hasPermission", resp.getMethod());
    assertEquals(1, resp.getTid());
    assertEquals("rpc", resp.getType());
    assertNull(resp.getWhere());
    assertTrue(resp.isSuccess());
    assertNull(resp.getMessage());
    assertNull(resp.getResult());
  }

  @Test
  public void testFormLoad() {
    String requestBody = "{\"action\":\"secondBeanWithExtDirectMethods\",\"method\":\"getFormInfo\",\"data\":[3.141],\"type\":\"rpc\",\"tid\":1}";
    List<ExtDirectResponse> responses = controller.router(request, response, Locale.ENGLISH, requestBody);

    assertEquals(1, responses.size());
    ExtDirectResponse resp = responses.get(0);
    System.out.println(resp);
    assertEquals("secondBeanWithExtDirectMethods", resp.getAction());
    assertEquals("getFormInfo", resp.getMethod());
    assertEquals(1, resp.getTid());
    assertEquals("rpc", resp.getType());
    assertNull(resp.getWhere());
    assertTrue(resp.isSuccess());
    assertNull(resp.getMessage());
    assertNotNull(resp.getResult());

    assertTrue(resp.getResult() instanceof ExtDirectFormLoadResult);
    ExtDirectFormLoadResult wrapper = (ExtDirectFormLoadResult)resp.getResult();
    assertTrue(wrapper.isSuccess());
    assertNotNull(wrapper.getData());
    assertTrue(wrapper.getData() instanceof FormInfo);
    FormInfo info = (FormInfo)wrapper.getData();

    assertTrue(Double.compare(3.141, info.getBack()) == 0);
    assertEquals(true, info.isAdmin());
    assertEquals(31, info.getAge());
    assertEquals("Bob", info.getName());
    assertEquals(new BigDecimal("10000.55"), info.getSalary());
    assertEquals(new GregorianCalendar(1980, Calendar.JANUARY, 15).getTime(), info.getBirthday());
  }

  @Test
  public void testMultipleValidRequestWithNoParams() {
    String requestBody = "[{\"action\":\"beanWithExtDirectMethods\",\"method\":\"doWork1\",\"data\":null,\"type\":\"rpc\",\"tid\":1},{\"action\":\"beanWithExtDirectMethods\",\"method\":\"doWork2\",\"data\":[10,7.3, \"ralph\"],\"type\":\"rpc\",\"tid\":2}]";

    List<ExtDirectResponse> responses = controller.router(request, response, Locale.ENGLISH, requestBody);

    assertEquals(2, responses.size());

    ExtDirectResponse resp = responses.get(0);
    assertEquals("beanWithExtDirectMethods", resp.getAction());
    assertEquals("doWork1", resp.getMethod());
    assertEquals(1, resp.getTid());
    assertEquals("rpc", resp.getType());
    assertNull(resp.getWhere());
    assertTrue(resp.isSuccess());
    assertNull(resp.getMessage());
    assertEquals("doWork1.called", resp.getResult());

    resp = responses.get(1);
    assertEquals("beanWithExtDirectMethods", resp.getAction());
    assertEquals("doWork2", resp.getMethod());
    assertEquals(2, resp.getTid());
    assertEquals("rpc", resp.getType());
    assertNull(resp.getWhere());
    assertTrue(resp.isSuccess());
    assertNull(resp.getMessage());
    assertEquals("doWork2.called-10-7.3-ralph", resp.getResult());
  }


  @Test
  public void pollNoArguments() throws Exception {
    ExtDirectPollResponse resp = controller.poll("poll", "handleMessagePoll", "message", request, response, Locale.ENGLISH);
    assertNotNull(resp);
    assertEquals("event", resp.getType());
    assertEquals("message", resp.getName());
    assertTrue(((String)resp.getData()).startsWith("Successfully polled at: "));
  }

  @Test
  public void pollRequiredArgument() throws Exception {
    request.setParameter("id", "2");
    ExtDirectPollResponse resp = controller.poll("poll", "handleMessage2", "message2", request, response, Locale.ENGLISH);
    assertNotNull(resp);
    assertEquals("event", resp.getType());
    assertEquals("message2", resp.getName());
    assertEquals(Integer.valueOf(4), resp.getData());
  }

  @Test
  @ExpectedException(IllegalArgumentException.class)
  public void pollRequiredArgumentNoRequestParameter() throws Exception {
    ExtDirectPollResponse resp = controller.poll("poll", "handleMessage2", "message2", request, response, Locale.ENGLISH);
    assertNotNull(resp);
    assertEquals("event", resp.getType());
    assertEquals("message2", resp.getName());
    assertEquals(Integer.valueOf(4), resp.getData());
  }

  @Test
  public void pollDefaultValueArgumentWithRequestParameter() throws Exception {
    request.setParameter("id", "7");
    ExtDirectPollResponse resp = controller.poll("poll", "handleMessage3", "message3", request, response, Locale.ENGLISH);
    assertNotNull(resp);
    assertEquals("event", resp.getType());
    assertEquals("message3", resp.getName());
    assertEquals(Integer.valueOf(14), resp.getData());
  }

  @Test
  public void pollDefaultValueArgumentNoRequestParameter() throws Exception {
    ExtDirectPollResponse resp = controller.poll("poll", "handleMessage3", "message3", request, response, Locale.ENGLISH);
    assertNotNull(resp);
    assertEquals("event", resp.getType());
    assertEquals("message3", resp.getName());
    assertEquals(Integer.valueOf(2), resp.getData());
  }

  @Test
  public void pollNotRequiredArgumentWithRequestParameter() throws Exception {
    request.setParameter("id", "3");
    ExtDirectPollResponse resp = controller.poll("poll", "handleMessage4", "message4", request, response, Locale.ENGLISH);
    assertNotNull(resp);
    assertEquals("event", resp.getType());
    assertEquals("message4", resp.getName());
    assertEquals(Integer.valueOf(6), resp.getData());
  }

  @Test
  public void pollNotRequiredArgumentNoRequestParameter() throws Exception {
    ExtDirectPollResponse resp = controller.poll("poll", "handleMessage4", "message4", request, response, Locale.ENGLISH);
    assertNotNull(resp);
    assertEquals("event", resp.getType());
    assertEquals("message4", resp.getName());
    assertNull(resp.getData());
  }

  
//  @Test
//  public void testSingleGetDirectRequests() {
//    String singleMethodRequestWithParamString = "{\"action\":\"Remoting\",\"method\":\"getConfig\",\"data\":[3,2.5, \"string.param\"],\"type\":\"rpc\",\"tid\":2}";
//
//    List<ExtDirectRequest> extReqs = new RouterController().getExtDirectRequests(singleMethodRequestWithParamString);
//    assertEquals(1, extReqs.size());
//
//    ExtDirectRequest extReq = extReqs.get(0);
//    assertEquals("Remoting", extReq.getAction());
//    assertEquals(3, extReq.getData().length);
//    assertEquals("getConfig", extReq.getMethod());
//    assertEquals(2, extReq.getTid());
//    assertEquals("rpc", extReq.getType());
//
//    assertEquals(Integer.class, extReq.getData()[0].getClass());
//    assertEquals(Double.class, extReq.getData()[1].getClass());
//    assertEquals(String.class, extReq.getData()[2].getClass());
//
//  }
//
//  @Test
//  public void testMultipleGetDirectRequests() {
//    String multipleMethodRequestWithParamString = "[{\"action\":\"Remoting\",\"method\":\"getConfig\",\"data\":[3,2.5, \"string.param\"],\"type\":\"rpc\",\"tid\":3},";
//    multipleMethodRequestWithParamString += "{\"action\":\"Remoting2\",\"method\":\"getConfig2\",\"data\":[1,7.5, \"string.param\"],\"type\":\"rpc\",\"tid\":4}]";
//
//    List<ExtDirectRequest> extReqs = new RouterController().getExtDirectRequests(multipleMethodRequestWithParamString);
//    assertEquals(2, extReqs.size());
//
//    ExtDirectRequest extReq = extReqs.get(0);
//    assertEquals("Remoting", extReq.getAction());
//    assertEquals(3, extReq.getData().length);
//    assertEquals("getConfig", extReq.getMethod());
//    assertEquals(3, extReq.getTid());
//    assertEquals("rpc", extReq.getType());
//
//    assertEquals(Integer.class, extReq.getData()[0].getClass());
//    assertEquals(Double.class, extReq.getData()[1].getClass());
//    assertEquals(String.class, extReq.getData()[2].getClass());
//
//    extReq = extReqs.get(1);
//    assertEquals("Remoting2", extReq.getAction());
//    assertEquals(3, extReq.getData().length);
//    assertEquals("getConfig2", extReq.getMethod());
//    assertEquals(4, extReq.getTid());
//    assertEquals("rpc", extReq.getType());
//
//    assertEquals(Integer.class, extReq.getData()[0].getClass());
//    assertEquals(Double.class, extReq.getData()[1].getClass());
//    assertEquals(String.class, extReq.getData()[2].getClass());
//  }

}
