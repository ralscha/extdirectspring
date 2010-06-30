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
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import ch.ralscha.extdirectspring.bean.ExtDirectFormLoadResult;
import ch.ralscha.extdirectspring.bean.ExtDirectRequest;
import ch.ralscha.extdirectspring.bean.ExtDirectResponse;
import ch.ralscha.extdirectspring.mock.FormInfo;
import ch.ralscha.extdirectspring.util.ExtDirectSpringUtil;

/**
 * Tests for {@link RouterController}.
 *
 * @author Ralph Schaer
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:/testApplicationContext.xml")
public class RouterControllerRemote1Test {

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
    String json = createRequestJson("remoteProvider", "method1", 1, 3, 2.5, "string.param");
    List<ExtDirectResponse> responses = controller.router(request, response, Locale.ENGLISH, json);
    assertEquals(1, responses.size());
    checkBeanNotFoundResponse(responses.get(0));
  }

  private void checkBeanNotFoundResponse(ExtDirectResponse resp) {
    assertEquals("remoteProvider", resp.getAction());
    assertEquals("method1", resp.getMethod());
    assertEquals("exception", resp.getType());
    assertEquals(1, resp.getTid());
    assertNull(resp.getResult());
    assertEquals("Server Error", resp.getMessage());
    assertNull(resp.getWhere());
  }

  @Test
  public void testMethodNotFound() {
    String json = createRequestJson("remoteProvider1", "method4", 2, 3, 2.5, "string.param");
    List<ExtDirectResponse> responses = controller.router(request, response, Locale.ENGLISH, json);

    assertEquals(1, responses.size());
    checkMethodNotFoundResponse(responses.get(0));

  }

  private void checkMethodNotFoundResponse(ExtDirectResponse resp) {
    assertEquals("remoteProvider1", resp.getAction());
    assertEquals("method4", resp.getMethod());
    assertEquals("exception", resp.getType());
    assertEquals(2, resp.getTid());
    assertNull(resp.getResult());
    assertEquals("Server Error", resp.getMessage());
    assertNull(resp.getWhere());
  }

  @Test
  public void testNoParameters() {
    String json = createRequestJson("remoteProvider1", "method1", 1, null);
    List<ExtDirectResponse> responses = controller.router(request, response, Locale.ENGLISH, json);

    assertEquals(1, responses.size());
    checkNoParametersResponse(responses.get(0), 1);
  }

  private void checkNoParametersResponse(ExtDirectResponse resp, int tid) {
    assertEquals("remoteProvider1", resp.getAction());
    assertEquals("method1", resp.getMethod());
    assertEquals(tid, resp.getTid());
    assertEquals("rpc", resp.getType());
    assertNull(resp.getWhere());
    assertNull(resp.getMessage());
    assertEquals("method1() called", resp.getResult());
  }

  @Test
  public void testNoParametersWithRequestParameter() {
    String json = createRequestJson("remoteProvider1", "method1", 1, "requestparameter");
    List<ExtDirectResponse> responses = controller.router(request, response, Locale.ENGLISH, json);

    assertEquals(1, responses.size());
    ExtDirectResponse resp = responses.get(0);
    checkNoParametersResponse(resp, 1);
  }

  @Test
  public void testNoParameters2() {
    String json = createRequestJson("remoteProvider1", "method2", 1, null);
    List<ExtDirectResponse> responses = controller.router(request, response, Locale.ENGLISH, json);

    assertEquals(1, responses.size());
    ExtDirectResponse resp = responses.get(0);
    assertEquals("remoteProvider1", resp.getAction());
    assertEquals("method2", resp.getMethod());
    assertEquals(1, resp.getTid());
    assertEquals("rpc", resp.getType());
    assertNull(resp.getWhere());
    assertNull(resp.getMessage());
    assertEquals("method2() called", resp.getResult());
  }

  @Test
  public void testWithParameters() {
    String json = createRequestJson("remoteProvider1", "method3", 10, 1, 3.1, "requestParameter");
    List<ExtDirectResponse> responses = controller.router(request, response, Locale.ENGLISH, json);

    assertEquals(1, responses.size());
    ExtDirectResponse resp = responses.get(0);
    assertEquals("remoteProvider1", resp.getAction());
    assertEquals("method3", resp.getMethod());
    assertEquals(10, resp.getTid());
    assertEquals("rpc", resp.getType());
    assertNull(resp.getWhere());
    assertNull(resp.getMessage());
    assertEquals("method3() called-1-3.1-requestParameter", resp.getResult());
  }

  @Test
  public void testWithParametersNoRequestParameter() {
    String json = createRequestJson("remoteProvider1", "method3", 1, null);
    List<ExtDirectResponse> responses = controller.router(request, response, Locale.ENGLISH, json);

    assertEquals(1, responses.size());
    ExtDirectResponse resp = responses.get(0);
    assertEquals("remoteProvider1", resp.getAction());
    assertEquals("method3", resp.getMethod());
    assertEquals(1, resp.getTid());
    assertEquals("exception", resp.getType());
    assertNull(resp.getWhere());
    assertEquals("Server Error", resp.getMessage());
    assertNull(resp.getResult());
  }

  @Test
  public void testResultTrue() {
    String json = createRequestJson("remoteProvider1", "method5", 1, "ralph");
    List<ExtDirectResponse> responses = controller.router(request, response, Locale.ENGLISH, json);

    assertEquals(1, responses.size());
    ExtDirectResponse resp = responses.get(0);
    assertEquals("remoteProvider1", resp.getAction());
    assertEquals("method5", resp.getMethod());
    assertEquals(1, resp.getTid());
    assertEquals("rpc", resp.getType());
    assertNull(resp.getWhere());
    assertNull(resp.getMessage());
    assertEquals(true, resp.getResult());
  }

  @Test
  public void testResultFalse() {
    String json = createRequestJson("remoteProvider1", "method5", 1, "joe");
    List<ExtDirectResponse> responses = controller.router(request, response, Locale.ENGLISH, json);

    assertEquals(1, responses.size());
    ExtDirectResponse resp = responses.get(0);
    assertEquals("remoteProvider1", resp.getAction());
    assertEquals("method5", resp.getMethod());
    assertEquals(1, resp.getTid());
    assertEquals("rpc", resp.getType());
    assertNull(resp.getWhere());
    assertNull(resp.getMessage());
    assertEquals(false, resp.getResult());
  }

  @Test
  public void testResultNull() {
    String json = createRequestJson("remoteProvider1", "method5", 1, "martin");
    List<ExtDirectResponse> responses = controller.router(request, response, Locale.ENGLISH, json);

    assertEquals(1, responses.size());
    ExtDirectResponse resp = responses.get(0);
    assertEquals("remoteProvider1", resp.getAction());
    assertEquals("method5", resp.getMethod());
    assertEquals(1, resp.getTid());
    assertEquals("rpc", resp.getType());
    assertNull(resp.getWhere());
    assertNull(resp.getMessage());
    assertNull(resp.getResult());
  }

  @Test
  public void testIntParameterAndResult() {
    String json = createRequestJson("remoteProvider1", "method6", 3, 10, 20);
    List<ExtDirectResponse> responses = controller.router(request, response, Locale.ENGLISH, json);

    assertEquals(1, responses.size());
    ExtDirectResponse resp = responses.get(0);
    checkIntParameterResult(resp, 3);
  }

  private void checkIntParameterResult(ExtDirectResponse resp, int tid) {
    assertEquals("remoteProvider1", resp.getAction());
    assertEquals("method6", resp.getMethod());
    assertEquals(tid, resp.getTid());
    assertEquals("rpc", resp.getType());
    assertNull(resp.getWhere());
    assertNull(resp.getMessage());
    assertEquals(30, resp.getResult());
  }

  @Test
  public void testResultStringNull() {
    String json = createRequestJson("remoteProvider1", "method7", 1, null);
    List<ExtDirectResponse> responses = controller.router(request, response, Locale.ENGLISH, json);

    assertEquals(1, responses.size());
    ExtDirectResponse resp = responses.get(0);
    assertEquals("remoteProvider1", resp.getAction());
    assertEquals("method7", resp.getMethod());
    assertEquals(1, resp.getTid());
    assertEquals("rpc", resp.getType());
    assertNull(resp.getWhere());
    assertNull(resp.getMessage());
    assertNull(resp.getResult());
  }

  @Test
  public void testFormLoad() {
    String json = createRequestJson("remoteProvider1", "method8", 1, 3.141);
    List<ExtDirectResponse> responses = controller.router(request, response, Locale.ENGLISH, json);

    assertEquals(1, responses.size());
    ExtDirectResponse resp = responses.get(0);

    checkFormLoadResult(resp, 3.141, 1);
  }

  private void checkFormLoadResult(ExtDirectResponse resp, double back, int tid) {
    assertEquals("remoteProvider1", resp.getAction());
    assertEquals("method8", resp.getMethod());
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
  public void testFormLoadReturnsNull() {
    String json = createRequestJson("remoteProvider1", "method9", 1, null);
    List<ExtDirectResponse> responses = controller.router(request, response, Locale.ENGLISH, json);

    assertEquals(1, responses.size());
    ExtDirectResponse resp = responses.get(0);

    assertEquals("remoteProvider1", resp.getAction());
    assertEquals("method9", resp.getMethod());
    assertEquals(1, resp.getTid());
    assertEquals("rpc", resp.getType());
    assertNull(resp.getWhere());
    assertNull(resp.getMessage());
    assertNull(resp.getResult());
  }

  @Test
  public void testReturnsObject() {
    String json = createRequestJson("remoteProvider1", "method10", 1, 7.34);
    List<ExtDirectResponse> responses = controller.router(request, response, Locale.ENGLISH, json);

    assertEquals(1, responses.size());
    ExtDirectResponse resp = responses.get(0);

    assertEquals("remoteProvider1", resp.getAction());
    assertEquals("method10", resp.getMethod());
    assertEquals(1, resp.getTid());
    assertEquals("rpc", resp.getType());
    assertNull(resp.getWhere());
    assertNull(resp.getMessage());
    assertNotNull(resp.getResult());

    assertTrue(resp.getResult() instanceof FormInfo);
    FormInfo info = (FormInfo)resp.getResult();

    assertTrue(Double.compare(7.34, info.getBack()) == 0);
    assertEquals(false, info.isAdmin());
    assertEquals(32, info.getAge());
    assertEquals("John", info.getName());
    assertEquals(new BigDecimal("8720.20"), info.getSalary());
    assertEquals(new GregorianCalendar(1986, Calendar.JULY, 22).getTime(), info.getBirthday());
  }

  @Test
  public void testSupportedArguments() {
    String json = createRequestJson("remoteProvider1", "method11", 1, null);

    List<ExtDirectResponse> responses = controller.router(request, response, Locale.ENGLISH, json);

    assertEquals(1, responses.size());
    ExtDirectResponse resp = responses.get(0);

    assertEquals("remoteProvider1", resp.getAction());
    assertEquals("method11", resp.getMethod());
    assertEquals(1, resp.getTid());
    assertEquals("rpc", resp.getType());
    assertNull(resp.getWhere());
    assertNull(resp.getMessage());
    assertEquals(42l, resp.getResult());
  }

  @Test
  public void testMultipleRequests() {
    String json1 = createRequestJson("remoteProvider", "method1", 1, 3, 2.5, "string.param");
    String json2 = createRequestJson("remoteProvider1", "method4", 2, 3, 2.5, "string.param");
    String json3 = createRequestJson("remoteProvider1", "method1", 3, null);
    String json4 = createRequestJson("remoteProvider1", "method8", 4, 1.1);
    String json5 = createRequestJson("remoteProvider1", "method8", 5, 2.2);
    String json6 = createRequestJson("remoteProvider1", "method6", 6, 10, 20);

    String requestBody = String.format("[%s,%s,%s,%s,%s,%s]", json1, json2, json3, json4, json5, json6);

    List<ExtDirectResponse> responses = controller.router(request, response, Locale.ENGLISH, requestBody);

    assertEquals(6, responses.size());
    checkBeanNotFoundResponse(responses.get(0));
    checkMethodNotFoundResponse(responses.get(1));
    checkNoParametersResponse(responses.get(2), 3);
    checkFormLoadResult(responses.get(3), 1.1, 4);
    checkFormLoadResult(responses.get(4), 2.2, 5);
    checkIntParameterResult(responses.get(5), 6);
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
