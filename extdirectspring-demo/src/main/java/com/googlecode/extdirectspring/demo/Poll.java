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

import java.text.SimpleDateFormat;
import java.util.Date;
import javax.inject.Named;
import javax.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.RequestParam;
import com.googlecode.extdirectspring.annotation.ExtDirectPollMethod;

@Named
public class Poll {

  @ExtDirectPollMethod(event = "message")
  public String handleMessagePoll() {
    Date now = new Date();
    SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd 'at' hh:mm:ss");
    return "Successfully polled at: " + formatter.format(now);
  }
  
  @ExtDirectPollMethod(event = "pollWithParams")
  public String pollingWithParams(@RequestParam(value="no") int no, @RequestParam(value="name") String name,
                                  @RequestParam(value="dummy", defaultValue="CH") String dummy, HttpServletRequest request) {    
    return request.getRequestURI() + ":  POST PARAMETERS: no=" + no + ", name=" + name + ", dummy=" + dummy;
  }
}