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

package com.googlecode.extdirectspring.api;

import org.codehaus.jackson.annotate.JsonWriteNullProperties;

/**
 * 
 * 
 * @author Ralph Schaer
 */
public class Action {

  private String name;
  private int len;
  private Boolean formHandler;

  public Action(final String name, final int len, final Boolean formHandler) {
    this.name = name;
    this.len = len;
    this.formHandler = formHandler;
  }

  @JsonWriteNullProperties(false)
  public Boolean isFormHandler() {
    return formHandler;
  }

  public int getLen() {
    return len;
  }

  public String getName() {
    return name;
  }

}
