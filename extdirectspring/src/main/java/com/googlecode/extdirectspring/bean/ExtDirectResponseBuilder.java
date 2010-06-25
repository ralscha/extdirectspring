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

package com.googlecode.extdirectspring.bean;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import org.springframework.context.MessageSource;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import com.googlecode.extdirectspring.util.ExtDirectSpringUtil;

/**
* An utility class that helps building a {@link ExtDirectResponse}
*
* @author Ralph Schaer
*/
public class ExtDirectResponseBuilder {

  private ExtDirectResponse response;
  private Map<String, Object> result;
  
  public ExtDirectResponseBuilder(HttpServletRequest request) {
    response = new ExtDirectResponse();
    result = new HashMap<String, Object>();
      
    response.setSuccess(true);
    response.setAction(request.getParameter("extAction"));
    response.setMethod(request.getParameter("extMethod"));
    response.setType(request.getParameter("extType"));
    response.setTid(Integer.parseInt(request.getParameter("extTID")));
        
    response.setResult(result);
    
  }
  
  public void addErrors(BindingResult bindingResult) {      
    addErrors(null, null, bindingResult);
  }
  
  public void addErrors(Locale locale, BindingResult bindingResult) {
    addErrors(locale, null, bindingResult);
  }
  
  public void addErrors(Locale locale, MessageSource messageSource, BindingResult bindingResult) {      
    if (bindingResult != null && bindingResult.hasFieldErrors()) {
      Map<String, String> errorMap = new HashMap<String, String>();
      for (FieldError fieldError : bindingResult.getFieldErrors()) {
        String message = fieldError.getDefaultMessage();
        if (messageSource != null) {
          Locale loc = (locale != null ? locale : Locale.getDefault());
          message = messageSource.getMessage(fieldError.getCode(), fieldError.getArguments(), loc);
        }
        errorMap.put(fieldError.getField(), message);
      }
      if (errorMap.isEmpty()) {
        result.put("success", true);
      } else {
        result.put("errors", errorMap);
        result.put("success", false);
      }
    }
  }  
  
  public void addResultProperty(String key, Object value) {
    result.put(key, value);    
  }
  
  public void successful() {
    result.put("success", true);
  }
  
  public void unsuccessful() {
    result.put("success", false);
  }  
  
  public ExtDirectResponse build() {
    return response;
  }
  
  public String buildUploadResponse() {
    StringBuilder sb = new StringBuilder();
    sb.append("<html><body><textarea>");
    sb.append(ExtDirectSpringUtil.serializeObjectToJson(response));
    sb.append("</textarea></body></html>");
    return sb.toString();
  }


  
}
