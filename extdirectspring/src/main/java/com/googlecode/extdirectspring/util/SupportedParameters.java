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

package com.googlecode.extdirectspring.util;

import java.util.Locale;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpSession;

/**
 * Enum of all supported parameter types
 * 
 * @author mansari
 * @author Ralph Schaer
 */
public enum SupportedParameters {
  
  SERVLET_REQUEST(ServletRequest.class),
  SERVLET_RESPONSE(ServletResponse.class),
  SESSION(HttpSession.class),
  LOCALE(Locale.class);
  
  private Class<?> clazz;
  
  private SupportedParameters(Class<?> clazz) {
    this.clazz = clazz;
  }
  
  public Class<?> getSupportedClass() {
    return clazz;
  }
  
  public static boolean isSupported(Class<?> clazz) {
    for (SupportedParameters supportedParameter : SupportedParameters.values()) {
      if (supportedParameter.clazz.isAssignableFrom(clazz)) {
        return true;
      }
    }
    return false;
  }
  
  
  
}
