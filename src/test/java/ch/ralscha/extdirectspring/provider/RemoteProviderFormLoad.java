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

package ch.ralscha.extdirectspring.provider;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;
import javax.inject.Named;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.junit.Assert;
import org.springframework.web.bind.annotation.RequestParam;
import ch.ralscha.extdirectspring.annotation.ExtDirectMethod;
import ch.ralscha.extdirectspring.annotation.ExtDirectMethodType;
import ch.ralscha.extdirectspring.bean.ExtDirectFormLoadResult;

@Named
@SuppressWarnings("unused")
public class RemoteProviderFormLoad {

  @ExtDirectMethod(value = ExtDirectMethodType.FORM_LOAD, group = "group3")
  public FormInfo method1(@RequestParam(value = "d") double d) {
    FormInfo info = new FormInfo();
    info.setBack(d);
    info.setAdmin(true);
    info.setAge(31);
    info.setBirthday(new GregorianCalendar(1980, Calendar.JANUARY, 15).getTime());
    info.setName("Bob");
    info.setSalary(new BigDecimal("10000.55"));
    return info;
  }

  @ExtDirectMethod(ExtDirectMethodType.FORM_LOAD)
  public FormInfo method2() {
    return null;
  }

  @ExtDirectMethod(ExtDirectMethodType.FORM_LOAD)
  public FormInfo method3(HttpServletResponse response, HttpServletRequest request, HttpSession session, Locale locale) {
    Assert.assertNotNull(response);
    Assert.assertNotNull(request);
    Assert.assertNotNull(session);
    Assert.assertEquals(Locale.ENGLISH, locale);

    return null;
  }

  @ExtDirectMethod(ExtDirectMethodType.FORM_LOAD)
  public FormInfo method4(Locale locale, @RequestParam(value = "id") int id) {
    Assert.assertEquals(10, id);
    Assert.assertEquals(Locale.ENGLISH, locale);
    return new FormInfo();
  }

  @ExtDirectMethod(value = ExtDirectMethodType.FORM_LOAD, group = "group3")
  public ExtDirectFormLoadResult method5(@RequestParam(value = "id", defaultValue = "1") int id,
      HttpServletRequest servletRequest) {
    Assert.assertEquals(1, id);
    Assert.assertNotNull(servletRequest);
    return new ExtDirectFormLoadResult();
  }

  @ExtDirectMethod(ExtDirectMethodType.FORM_LOAD)
  public ExtDirectFormLoadResult method6(@RequestParam(value = "id", required = false) Integer id) {
    if (id == null) {
      Assert.assertNull(id);
    } else {
      Assert.assertEquals(Integer.valueOf(11), id);
    }
    return new ExtDirectFormLoadResult("TEST");
  }

}
