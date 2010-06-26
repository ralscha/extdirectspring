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

import org.codehaus.jackson.annotate.JsonIgnore;
import ch.ralscha.extdirectspring.annotation.ExtDirectMethod;
import ch.ralscha.extdirectspring.annotation.ExtDirectPollMethod;
import ch.ralscha.extdirectspring.annotation.ExtDirectStoreModifyMethod;
import ch.ralscha.extdirectspring.annotation.ExtDirectStoreReadMethod;

class SimpleBean {

  @ExtDirectMethod
  public void methodA() {
    //no code here
  }

  @ExtDirectPollMethod
  public void methodB() {
    //nothing here
  }

  @ExtDirectStoreModifyMethod(type = Integer.class)
  public void methodC() {
    //nothing here
  }

  @ExtDirectStoreReadMethod
  public void methodD() {
    //nothing here
  }

  @JsonIgnore
  public void methodE() {
    //nothing here
  }

  public void methodF() {
    //nothing here
  }
}
