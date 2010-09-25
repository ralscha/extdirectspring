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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.inject.Inject;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import ch.ralscha.extdirectspring.bean.ExtDirectResponse;
import ch.ralscha.extdirectspring.provider.Row;

/**
 * Tests for {@link RouterController}.
 * 
 * @author Ralph Schaer
 */
@SuppressWarnings("all")
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:/testApplicationContext.xml")
public class RouterControllerFilterTest {

  @Inject
  private RouterController controller;

  private MockHttpServletResponse response;
  private MockHttpServletRequest request;

  private static List<String> jsonList;

  @BeforeClass
  public static void readJson() throws IOException {
    jsonList = new ArrayList<String>();
    InputStream is = RouterControllerFilterTest.class.getResourceAsStream("/filterjson.txt");
    BufferedReader br = new BufferedReader(new InputStreamReader(is));
    String line = null;
    while ((line = br.readLine()) != null) {
      jsonList.add(line);
    }
    br.close();
    is.close();
  }

  @Before
  public void beforeTest() {
    response = new MockHttpServletResponse();
    request = new MockHttpServletRequest();
  }

  @Test
  public void testFilters() {

    int index = 1;
    for (String json : jsonList) {

      List<ExtDirectResponse> responses = controller.router(request, response, Locale.ENGLISH, json);

      assertEquals(1, responses.size());
      ExtDirectResponse resp = responses.get(0);
      assertEquals("remoteProviderStoreRead", resp.getAction());
      assertEquals("methodFilter", resp.getMethod());
      assertEquals("rpc", resp.getType());
      assertEquals(index, resp.getTid());
      assertNull(resp.getMessage());
      assertNull(resp.getWhere());
      assertNotNull(resp.getResult());
      List<Row> rows = (List<Row>)resp.getResult();
      assertEquals(1, rows.size());
      assertEquals(index, rows.get(0).getId());

      index++;
    }

    assertEquals(10, index);

  }

}
