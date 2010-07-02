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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import java.util.Locale;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockHttpSession;

/**
 * Tests for {@link SupportedParameterTypes}.
 * 
 * @author Ralph Schaer
 */
public class SupportedParametersTest {

  @Test
  public void testIsSupported() {
    assertEquals(4, SupportedParameterTypes.values().length);
    assertFalse(SupportedParameterTypes.isSupported(String.class));
    assertFalse(SupportedParameterTypes.isSupported(null));
    assertTrue(SupportedParameterTypes.isSupported(MockHttpServletResponse.class));
    assertTrue(SupportedParameterTypes.isSupported(MockHttpServletRequest.class));
    assertTrue(SupportedParameterTypes.isSupported(MockHttpSession.class));
    assertTrue(SupportedParameterTypes.isSupported(Locale.class));
  }

}
