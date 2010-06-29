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

import static org.junit.Assert.*;
import javax.inject.Inject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * Tests for {@link RouterController}.
 *
 * @author Ralph Schaer
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:/testApplicationContext.xml")
public class RouterControllerRemote4Test {

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
  public void testFormPostRouter() {
    try {
      controller.router("remoteProvider1", "method1");
      fail("has to throw a IllegalArgumentException");
    } catch (Exception e) {
      assertTrue(e instanceof IllegalArgumentException);
      assertEquals("Invalid remoting form method: remoteProvider1.method1", e.getMessage());
    }

    try {
      controller.router("RemoteProvider1", "method1");
      fail("has to throw a NoSuchBeanDefinitionException");
    } catch (Exception e) {
      assertTrue(e instanceof NoSuchBeanDefinitionException);
      assertEquals("No bean named 'RemoteProvider1' is defined", e.getMessage());
    }
    
    String redirect = controller.router("formInfoController", "updateInfo");
    assertEquals("forward:updateInfo", redirect);
  }

}
