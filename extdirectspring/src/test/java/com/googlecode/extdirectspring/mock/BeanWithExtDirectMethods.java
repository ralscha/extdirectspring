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

package com.googlecode.extdirectspring.mock;

import javax.inject.Named;
import com.googlecode.extdirectspring.annotation.ExtDirectMethod;

@Named
public class BeanWithExtDirectMethods {

  @ExtDirectMethod
  public String getConfig() {
    return "getConfig.called";
  }

  @ExtDirectMethod
  public String doWork1() {
    return "doWork1.called";
  }

  @ExtDirectMethod
  public String doWork2(long i, Double d, String s) {
    return String.format("doWork2.called-%d-%.1f-%s", i, d, s);
  }

  public String doWork3(long i, Double d, String s) {
    return "doWork3.called";
  }
  
  @ExtDirectMethod
  public Boolean hasPermission(String userName) {
    if ("ralph".equals(userName)) {
      return true;
    } else if ("joe".equals(userName)) {
      return false;
    }
    return null;
  }
}
