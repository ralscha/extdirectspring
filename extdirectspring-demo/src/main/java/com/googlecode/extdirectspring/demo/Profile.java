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

package com.googlecode.extdirectspring.demo;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import com.googlecode.extdirectspring.annotation.ExtDirectMethod;
import com.googlecode.extdirectspring.bean.ExtDirectResponse;
import com.googlecode.extdirectspring.bean.ExtDirectResponseBuilder;

@Controller
public class Profile {

  public static class PhoneInfo {
    public String cell;
    public String office;
    public String home;
  }

  @ExtDirectMethod(formLoad=true)
  public BasicInfo getBasicInfo(Long userId, String foo) {

    BasicInfo basicInfo = new BasicInfo();
    basicInfo.setFoo(foo);
    basicInfo.setName("Aaron Conran");
    basicInfo.setCompany("Ext JS, LLC");
    basicInfo.setEmail("aaron@extjs.com");
    return basicInfo;
  }

  @ExtDirectMethod(formLoad=true)
  public PhoneInfo getPhoneInfo(Long userId) {

    PhoneInfo phoneInfo = new PhoneInfo();
    phoneInfo.cell = "443-555-1234";
    phoneInfo.office = "1-800-CALLEXT";
    phoneInfo.home = "";
    return phoneInfo;
  }

  @ExtDirectMethod(formLoad=true)
  public Map<String, String> getLocationInfo(Long userId) {
    Map<String, String> data = new HashMap<String, String>();
    data.put("street", "1234 Red Dog Rd.");
    data.put("city", "Seminole");
    data.put("state", "FL");
    data.put("zip", "33776");
    return data;
  }

  @ExtDirectMethod
  @ResponseBody
  @RequestMapping(value = "/updateBasicInfo", method=RequestMethod.POST)
  public ExtDirectResponse updateBasicInfo(Locale locale, HttpServletRequest request, @Valid BasicInfo basicInfo, BindingResult result) {

    if (!result.hasErrors()) {
      if (basicInfo.getEmail().equals("aaron@extjs.com")) {
        result.rejectValue("email", null, "email already taken");
      }
    }

    ExtDirectResponseBuilder builder = new ExtDirectResponseBuilder(request);
    builder.addErrors(locale, result);
    return builder.build();
    
  }
}
