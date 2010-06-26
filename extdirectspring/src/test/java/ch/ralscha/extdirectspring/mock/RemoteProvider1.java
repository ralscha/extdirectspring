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

package ch.ralscha.extdirectspring.mock;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;
import javax.inject.Named;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.junit.Assert;
import ch.ralscha.extdirectspring.annotation.ExtDirectMethod;

@Named
public class RemoteProvider1 {

  @ExtDirectMethod(group = "group1")
  public String method1() {
    return "method1() called";
  }

  @ExtDirectMethod
  public String method2() {
    return "method2() called";
  }

  @ExtDirectMethod(group = "group2")
  public String method3(long i, Double d, String s) {
    return String.format("method3() called-%d-%.1f-%s", i, d, s);
  }

  public String method4(long i, Double d, String s) {
    return "method4() called";
  }

  @ExtDirectMethod(group = "group2")
  public Boolean method5(String userName) {
    if ("ralph".equals(userName)) {
      return true;
    } else if ("joe".equals(userName)) {
      return false;
    }
    return null;
  }

  @ExtDirectMethod
  public int method6(int a, int b) {
    return a + b;
  }

  @ExtDirectMethod
  public String method7() {
    return null;
  }

  @ExtDirectMethod(formLoad = true, group = "group3")
  public FormInfo method8(double d) {
    FormInfo info = new FormInfo();
    info.setBack(d);
    info.setAdmin(true);
    info.setAge(31);
    info.setBirthday(new GregorianCalendar(1980, Calendar.JANUARY, 15).getTime());
    info.setName("Bob");
    info.setSalary(new BigDecimal("10000.55"));
    return info;
  }

  @ExtDirectMethod(formLoad = true)
  public FormInfo method9() {
    return null;
  }

  @ExtDirectMethod
  public FormInfo method10(double d) {
    FormInfo info = new FormInfo();
    info.setBack(d);
    info.setAdmin(false);
    info.setAge(32);
    info.setBirthday(new GregorianCalendar(1986, Calendar.JULY, 22).getTime());
    info.setName("John");
    info.setSalary(new BigDecimal("8720.20"));
    return info;
  }

  @ExtDirectMethod(group = "group3")
  public long method11(HttpServletResponse response, HttpServletRequest request, HttpSession session, Locale locale) {
    Assert.assertNotNull(response);
    Assert.assertNotNull(request);
    Assert.assertNotNull(session);
    Assert.assertEquals(Locale.ENGLISH, locale);

    return 42;
  }
}
