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

import static org.junit.Assert.*;

import java.util.Locale;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockHttpSession;

/**
 * Tests for {@link SupportedParameters}.
 *
 * @author Ralph Schaer
 */
public class SupportedParametersTest {

  @Test
  public void testIsSupported() {
    assertEquals(4, SupportedParameters.values().length);
    assertFalse(SupportedParameters.isSupported(String.class));
    assertFalse(SupportedParameters.isSupported(null));
    assertTrue(SupportedParameters.isSupported(MockHttpServletResponse.class));
    assertTrue(SupportedParameters.isSupported(MockHttpServletRequest.class));
    assertTrue(SupportedParameters.isSupported(MockHttpSession.class));
    assertTrue(SupportedParameters.isSupported(Locale.class));
  }

}
