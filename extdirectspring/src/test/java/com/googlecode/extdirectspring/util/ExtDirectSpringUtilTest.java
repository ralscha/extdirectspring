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

package com.googlecode.extdirectspring.util;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
import org.junit.Test;
import org.springframework.util.StringUtils;
import com.googlecode.extdirectspring.bean.ExtDirectRequest;

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

    assertTrue(ExtDirectSpringUtil.equal("a", "a"));
    assertFalse(ExtDirectSpringUtil.equal("a", "b"));
    assertFalse(ExtDirectSpringUtil.equal(null, "a"));
    assertFalse(ExtDirectSpringUtil.equal("a", null));
    assertTrue(ExtDirectSpringUtil.equal(null, null));
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

    List<ExtDirectRequest> desRequests = ExtDirectSpringUtil.deserializeJsonToObject(json, new TypeReference<List<ExtDirectRequest>>() {/*empty*/});

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
