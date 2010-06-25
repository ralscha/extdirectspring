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

import java.util.Locale;
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
public class FormInfoController {

  @ExtDirectMethod
  @ResponseBody
  @RequestMapping(value = "/updateInfo", method = RequestMethod.POST)
  public ExtDirectResponse updateInfo(Locale locale, HttpServletRequest request, @Valid FormInfo formInfo, BindingResult result) {
    
    ExtDirectResponseBuilder builder = new ExtDirectResponseBuilder(request);
    builder.addErrors(locale, result);
    return builder.build();
  }

}
