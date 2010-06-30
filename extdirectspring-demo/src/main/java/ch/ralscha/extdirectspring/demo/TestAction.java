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

package ch.ralscha.extdirectspring.demo;

import javax.inject.Named;
import ch.ralscha.extdirectspring.annotation.ExtDirectMethod;

@Named
public class TestAction {

  @ExtDirectMethod(group = "example")
  public Long multiply(String num) {
    try {
      return Long.valueOf(num) * 8;
    } catch (NumberFormatException e) {
      return -1l;
    }
  }

  @ExtDirectMethod(group = "example")
  public String doEcho(String message) {
    return message;
  }

}
