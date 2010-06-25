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

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.GregorianCalendar;
import javax.inject.Named;
import com.googlecode.extdirectspring.annotation.ExtDirectMethod;

@Named
public class SecondBeanWithExtDirectMethods {

  @ExtDirectMethod
  public int add(int a, int b) {
    return a + b;
  }

  @ExtDirectMethod(formLoad = true)
  public FormInfo getFormInfo(double d) {
    FormInfo info = new FormInfo();
    info.setBack(d);
    info.setAdmin(true);
    info.setAge(31);
    info.setBirthday(new GregorianCalendar(1980, Calendar.JANUARY, 15).getTime());
    info.setName("Bob");
    info.setSalary(new BigDecimal("10000.55"));
    return info;    
  }
}
