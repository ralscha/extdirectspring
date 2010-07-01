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

package ch.ralscha.extdirectspring.util;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
import org.junit.Test;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.util.StringUtils;
import ch.ralscha.extdirectspring.annotation.ExtDirectMethod;
import ch.ralscha.extdirectspring.bean.ExtDirectRequest;

/**
 * Tests for {@link ExtDirectSpringUtil}.
 *
 * @author Ralph Schaer
 */
public class ExtDirectSpringUtilTest {

  @Test
  public void testEqual() {
    assertTrue(ExtDirectSpringUtil.equal(1, 1));
    assertFalse(ExtDirectSpringUtil.equal(1, 2));

    assertTrue(ExtDirectSpringUtil.equal(true, true));
    assertTrue(ExtDirectSpringUtil.equal(false, false));

    assertFalse(ExtDirectSpringUtil.equal(true, false));
    assertFalse(ExtDirectSpringUtil.equal(false, true));
    assertFalse(ExtDirectSpringUtil.equal(false, null));

    assertTrue(ExtDirectSpringUtil.equal("a", "a"));
    assertFalse(ExtDirectSpringUtil.equal("a", "b"));
    assertFalse(ExtDirectSpringUtil.equal(null, "a"));
    assertFalse(ExtDirectSpringUtil.equal("a", null));
    assertTrue(ExtDirectSpringUtil.equal(null, null));
  }

  @Test
  public void testFindMethodWithAnnotation() {
    ApplicationContext context = new ClassPathXmlApplicationContext("/testApplicationContextB.xml");
    Method methodB = ExtDirectSpringUtil.findMethod(context, "springManagedBean", "methodB");
    Method methodBWithAnnotation = ExtDirectSpringUtil.findMethodWithAnnotation(methodB, ExtDirectMethod.class);
    assertEquals(methodB, methodBWithAnnotation);

    Method methodSubB = ExtDirectSpringUtil.findMethod(context, "springManagedSubBean", "methodB");
    methodBWithAnnotation = ExtDirectSpringUtil.findMethodWithAnnotation(methodSubB, ExtDirectMethod.class);
    assertFalse(methodSubB.equals(methodBWithAnnotation));
    assertTrue(methodB.equals(methodBWithAnnotation));
  }

  @Test
  public void testFindMethodAndInvoke() throws IllegalArgumentException, IllegalAccessException, InvocationTargetException {
    ApplicationContext context = new ClassPathXmlApplicationContext("/testApplicationContextB.xml");

    try {
      assertNull(ExtDirectSpringUtil.invoke(null, null, null, null));
      fail("has to throw a IllegalArgumentException");
    } catch (Exception e) {
      assertTrue(e instanceof IllegalArgumentException);
      assertEquals("ApplicatonContext cannot be null", e.getMessage());
    }

    try {
      assertNull(ExtDirectSpringUtil.invoke(context, null, null, null));
      fail("has to throw a IllegalArgumentException");
    } catch (Exception e) {
      assertTrue(e instanceof IllegalArgumentException);
      assertEquals("beanName cannot be null", e.getMessage());
    }

    try {
      assertNull(ExtDirectSpringUtil.invoke(context, "springManagedBean", null, null));
      fail("has to throw a IllegalArgumentException");
    } catch (Exception e) {
      assertTrue(e instanceof IllegalArgumentException);
      assertEquals("methodName cannot be null", e.getMessage());
    }

    try {
      assertNull(ExtDirectSpringUtil.invoke(context, "springManagedBeanA", "methodA", null));
      fail("has to throw a NoSuchBeanDefinitionException");
    } catch (Exception e) {
      assertTrue(e instanceof NoSuchBeanDefinitionException);
      assertEquals("No bean named 'springManagedBeanA' is defined", e.getMessage());
    }

    try {
      ExtDirectSpringUtil.invoke(context, "springManagedBean", "methodA", null);
      fail("has to throw a IllegalArgumentException");
    } catch (Exception e) {
      assertTrue(e instanceof IllegalArgumentException);
      assertEquals("Invalid remoting method 'springManagedBean.methodA'. Missing ExtDirectMethod annotation", e.getMessage());
    }

    assertFalse((Boolean)ExtDirectSpringUtil.invoke(context, "springManagedBean", "methodB", null));
    assertFalse((Boolean)ExtDirectSpringUtil.invoke(context, "springManagedBean", "methodB", null));
    assertEquals(Integer.valueOf(3), ExtDirectSpringUtil.invoke(context, "springManagedBean", "sum", new Object[]{1, 2}));
    assertEquals(Integer.valueOf(9), ExtDirectSpringUtil.invoke(context, "springManagedBean", "sum", new Object[]{6, 3}));
    
    
    try {
      ExtDirectSpringUtil.findMethod(context, "springManagedBean", "methodC");
      fail("has to throw a IllegalArgumentException");
    } catch (Exception e) {
      assertTrue(e instanceof IllegalArgumentException);
      assertEquals("Method 'springManagedBean.methodC' not found", e.getMessage());
    }
  }

  @Test
  public void testSerializeObjectToJsonObject() {
    assertEquals("null", ExtDirectSpringUtil.serializeObjectToJson(null));
    assertEquals("\"a\"", ExtDirectSpringUtil.serializeObjectToJson("a"));
    assertEquals("1", ExtDirectSpringUtil.serializeObjectToJson(1));
    assertEquals("true", ExtDirectSpringUtil.serializeObjectToJson(true));

    Map<String, Object> map = new LinkedHashMap<String, Object>();
    map.put("one", 1);
    map.put("two", "2");
    map.put("three", null);
    map.put("four", false);
    map.put("five", new int[]{1, 2});

    String expected = "{\"one\":1,\"two\":\"2\",\"three\":null,\"four\":false,\"five\":[1,2]}";
    assertEquals(expected, ExtDirectSpringUtil.serializeObjectToJson(map));

    JsonTestBean testBean = new JsonTestBean(1, "2", null, false, new Integer[]{1, 2});
    expected = "{\"a\":1,\"b\":\"2\",\"c\":null,\"d\":false,\"e\":[1,2]}";
    assertEquals(expected, ExtDirectSpringUtil.serializeObjectToJson(testBean));

  }

  @Test
  public void testSerializeObjectToJsonObjectBoolean() {
    assertEquals("null", ExtDirectSpringUtil.serializeObjectToJson(null, true));
    assertEquals("\"a\"", ExtDirectSpringUtil.serializeObjectToJson("a", true));
    assertEquals("1", ExtDirectSpringUtil.serializeObjectToJson(1, true));
    assertEquals("true", ExtDirectSpringUtil.serializeObjectToJson(true, true));

    Map<String, Object> map = new LinkedHashMap<String, Object>();
    map.put("one", 1);
    map.put("two", "2");
    map.put("three", null);
    map.put("four", false);
    map.put("five", new int[]{1, 2});

    String expected = "{\n  \"one\" : 1,\n  \"two\" : \"2\",\n  \"three\" : null,\n  \"four\" : false,\n  \"five\" : [ 1, 2 ]\n}";
    assertEquals(expected, ExtDirectSpringUtil.serializeObjectToJson(map, true).replace("\r", ""));

    JsonTestBean testBean = new JsonTestBean(1, "2", null, false, new Integer[]{1, 2});
    expected = "{\n  \"a\" : 1,\n  \"b\" : \"2\",\n  \"c\" : null,\n  \"d\" : false,\n  \"e\" : [ 1, 2 ]\n}";
    assertEquals(expected, ExtDirectSpringUtil.serializeObjectToJson(testBean, true).replace("\r", ""));

  }

  @Test
  public void testDeserializeJsonToObjectStringTypeReferenceOfT() {
    String json = "[\"1\",\"2\",\"3\",\"4\"]";
    List<String> result = ExtDirectSpringUtil.deserializeJsonToObject(json, new TypeReference<List<String>>() {/*empty*/
    });
    assertEquals(4, result.size());
    assertEquals("1", result.get(0));
    assertEquals("2", result.get(1));
    assertEquals("3", result.get(2));
    assertEquals("4", result.get(3));
  }

  @Test
  public void testDeserializeJsonToObjectStringClassOfT() {
    assertNull(ExtDirectSpringUtil.deserializeJsonToObject("null", String.class));
    assertEquals("a", ExtDirectSpringUtil.deserializeJsonToObject("\"a\"", String.class));
    assertEquals(Integer.valueOf(1), ExtDirectSpringUtil.deserializeJsonToObject("1", Integer.class));
    assertTrue(ExtDirectSpringUtil.deserializeJsonToObject("true", Boolean.class));

    String json1 = "{\"a\":1,\"b\":\"2\",\"c\":null,\"d\":false,\"e\":[1,2]}";
    String json2 = "{\r\n  \"a\" : 1,\r\n  \"b\" : \"2\",\r\n  \"c\" : null,\r\n  \"d\" : false,\r\n  \"e\" : [ 1, 2 ]\r\n}";
    JsonTestBean testBean = ExtDirectSpringUtil.deserializeJsonToObject(json1, JsonTestBean.class);
    assertEquals(Integer.valueOf(1), testBean.getA());
    assertEquals("2", testBean.getB());
    assertNull(testBean.getC());
    assertFalse(testBean.getD());
    assertArrayEquals(new Integer[]{1, 2}, testBean.getE());

    testBean = ExtDirectSpringUtil.deserializeJsonToObject(json2, JsonTestBean.class);
    assertEquals(Integer.valueOf(1), testBean.getA());
    assertEquals("2", testBean.getB());
    assertNull(testBean.getC());
    assertFalse(testBean.getD());
    assertArrayEquals(new Integer[]{1, 2}, testBean.getE());

  }

  @Test
  public void testJsonUtilObject() {
    ExtDirectRequest req = new ExtDirectRequest();
    req.setAction("testAction");
    req.setMethod("testMethod");
    req.setTid(1);
    req.setType("testType");
    req.setData(new Object[]{"one", "two"});

    String json = ExtDirectSpringUtil.serializeObjectToJson(req);
    assertNotNull(json);
    assertTrue(StringUtils.hasText(json));

    ExtDirectRequest desReq = ExtDirectSpringUtil.deserializeJsonToObject(json, ExtDirectRequest.class);
    assertNotNull(desReq);

    assertEquals(req.getAction(), desReq.getAction());
    assertArrayEquals(req.getData(), desReq.getData());
    assertEquals(req.getMethod(), desReq.getMethod());
    assertEquals(req.getTid(), desReq.getTid());
    assertEquals(req.getType(), desReq.getType());
  }

  @Test
  public void testJsonList() throws JsonGenerationException, JsonMappingException, IOException {
    List<ExtDirectRequest> requests = new ArrayList<ExtDirectRequest>();

    ExtDirectRequest req = new ExtDirectRequest();
    req.setAction("testAction1");
    req.setMethod("testMethod1");
    req.setTid(1);
    req.setType("testType1");
    req.setData(new Object[]{"one"});
    requests.add(req);

    req = new ExtDirectRequest();
    req.setAction("testAction2");
    req.setMethod("testMethod2");
    req.setTid(2);
    req.setType("testType2");
    req.setData(new Object[]{"two"});
    requests.add(req);

    ObjectMapper mapper = new ObjectMapper();
    String json = mapper.writeValueAsString(requests);

    List<ExtDirectRequest> desRequests = ExtDirectSpringUtil.deserializeJsonToObject(json, new TypeReference<List<ExtDirectRequest>>() {/*empty*/
    });

    assertEquals(requests.size(), desRequests.size());
    for (int i = 0; i < requests.size(); i++) {
      req = requests.get(i);
      ExtDirectRequest desReq = desRequests.get(i);

      assertEquals(req.getAction(), desReq.getAction());
      assertArrayEquals(req.getData(), desReq.getData());
      assertEquals(req.getMethod(), desReq.getMethod());
      assertEquals(req.getTid(), desReq.getTid());
      assertEquals(req.getType(), desReq.getType());
    }
  }

}
