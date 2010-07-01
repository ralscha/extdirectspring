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

package ch.ralscha.extdirectspring.bean;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import java.io.IOException;
import java.util.Map;
import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

/**
 * Tests for {@link ExtDirectResponseBuilder}.
 *
 * @author Ralph Schaer
 */
@SuppressWarnings("all")
public class ExtDirectResponseBuilderTest {

  @Test
  public void testBuilder() {

    MockHttpServletRequest request = new MockHttpServletRequest();
    request.setParameter("extAction", "action");
    request.setParameter("extMethod", "method");
    request.setParameter("extType", "type");
    request.setParameter("extTID", "1");

    ExtDirectResponseBuilder builder = new ExtDirectResponseBuilder(request);

    builder.addResultProperty("additionalProperty", 11);

    ExtDirectResponse response = builder.build();

    assertEquals("action", response.getAction());
    assertEquals("method", response.getMethod());
    assertEquals("type", response.getType());
    assertEquals(1, response.getTid());

    assertNotNull(response.getResult());
    assertNull(response.getWhere());
    assertNull(response.getMessage());

    Map<String, Object> data = (Map<String, Object>)response.getResult();
    assertEquals(2, data.size());
    assertEquals(11, data.get("additionalProperty"));
    assertEquals(true, data.get("success"));

    builder.unsuccessful();
    response = builder.build();
    data = (Map<String, Object>)response.getResult();
    assertEquals(2, data.size());
    assertEquals(11, data.get("additionalProperty"));
    assertEquals(false, data.get("success"));
  }

  @Test
  public void testBuilderUploadResponse() throws IOException {

    MockHttpServletRequest request = new MockHttpServletRequest();
    request.setParameter("extAction", "action");
    request.setParameter("extMethod", "method");
    request.setParameter("extType", "type");
    request.setParameter("extTID", "1");

    ExtDirectResponseBuilder builder = new ExtDirectResponseBuilder(request);
    builder.addResultProperty("additionalProperty", false);
    builder.addResultProperty("text", "a lot of &quot;text&quot;");

    MockHttpServletResponse response = new MockHttpServletResponse();
    builder.buildAndWriteUploadResponse(response);

    assertEquals("text/html", response.getContentType());
    String content = response.getContentAsString();
    assertTrue(content.startsWith("<html><body><textarea>"));
    assertTrue(content.endsWith("</textarea></body></html>"));

    String json = content.substring(content.indexOf("{"), content.lastIndexOf("}") + 1);
    assertTrue(json.contains("\\&quot;"));
    json = json.replace("\\&quot;", "\'");
    ObjectMapper mapper = new ObjectMapper();
    Map<String, Object> header = mapper.readValue(json, Map.class);

    assertEquals("action", header.get("action"));
    assertEquals("method", header.get("method"));
    assertEquals("type", header.get("type"));
    assertEquals(1, header.get("tid"));

    Map<String, Object> result = (Map<String, Object>)header.get("result");
    assertEquals(3, result.size());
    assertTrue((Boolean)result.get("success"));
    assertEquals("a lot of 'text'", result.get("text"));
    assertEquals(false, result.get("additionalProperty"));
  }

}
