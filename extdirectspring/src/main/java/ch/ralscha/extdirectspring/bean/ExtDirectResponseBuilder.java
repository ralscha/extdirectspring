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

package ch.ralscha.extdirectspring.bean;

import java.io.IOException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.context.MessageSource;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import ch.ralscha.extdirectspring.util.ExtDirectSpringUtil;

/**
* An utility class that helps building a {@link ExtDirectResponse}
* A form handler must return such a response
*
* @author Ralph Schaer
*/
public class ExtDirectResponseBuilder {

  private ExtDirectResponse response;
  private Map<String, Object> result;

  /**
   * Creates a builder that builds the response object 
   * needed for form handler and form upload handler methods.
   * Sets the successful flag to true, can be overriden with the
   * successful() and unsuccessful() methods
   * 
   * @param request the current request
   */
  public ExtDirectResponseBuilder(final HttpServletRequest request) {
    response = new ExtDirectResponse();
    result = new HashMap<String, Object>();

    response.setSuccess(true);
    response.setAction(request.getParameter("extAction"));
    response.setMethod(request.getParameter("extMethod"));
    response.setType(request.getParameter("extType"));
    response.setTid(Integer.parseInt(request.getParameter("extTID")));

    successful();
    response.setResult(result);
  }

  /**
   * Creates a errors property in the response if there are any errors in the bindingResult
   * Sets the success flag to false if there are errors
   * 
   * @param bindingResult
   */
  public void addErrors(final BindingResult bindingResult) {
    addErrors(null, null, bindingResult);
  }

  /**
   * Creates a errors property in the response if there are any errors in the bindingResult
   * Sets the success flag to false if there are errors
   * 
   * @param locale
   * @param bindingResult
   */
  public void addErrors(final Locale locale, final BindingResult bindingResult) {
    addErrors(locale, null, bindingResult);
  }

  /**
   * Creates a errors property in the response if there are any errors in the bindingResult
   * Sets the success flag to false if there are errors
   * 
   * @param locale
   * @param messageSource
   * @param bindingResult
   */
  public void addErrors(final Locale locale, final MessageSource messageSource, final BindingResult bindingResult) {
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

  /**
   * Add additional property to the response
   * 
   * @param key the key of the property
   * @param value the value of this property
   */
  public void addResultProperty(final String key, final Object value) {
    result.put(key, value);
  }

  /**
   * Sets success flag to true
   */
  public void successful() {
    result.put("success", true);
  }

  /**
   * Sets success flag to false
   */
  public void unsuccessful() {
    result.put("success", false);
  }

  /**
   * Builds the response object
   * 
   * @return the response object
   */
  public ExtDirectResponse build() {
    return response;
  }

  /**
   * Builds and writes the response to the OutputStream of the response.
   * This methods has to be called at the end of a form upload handler method.
   * 
   * @param servletResponse current servlet response
   * @throws IOException 
   */
  public void buildAndWriteUploadResponse(final HttpServletResponse servletResponse) throws IOException {
    servletResponse.setContentType("text/html");

    servletResponse.getOutputStream().write("<html><body><textarea>".getBytes());
    String responseJson = ExtDirectSpringUtil.serializeObjectToJson(response);
    responseJson = responseJson.replace("&quot;", "\\&quot;");
    servletResponse.getOutputStream().write(responseJson.getBytes());
    servletResponse.getOutputStream().write("</textarea></body></html>".getBytes());
  }

}
