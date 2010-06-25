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

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import javax.inject.Named;
import javax.servlet.http.HttpServletRequest;
import org.junit.Assert;
import org.springframework.web.bind.annotation.RequestParam;
import com.googlecode.extdirectspring.annotation.ExtDirectPollMethod;

@Named
public class Poll {

  @ExtDirectPollMethod(event="message")
  public String handleMessagePoll() {    
    Date now = new Date();
    SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd 'at' hh:mm:ss");
    return "Successfully polled at: " + formatter.format( now );
  }
  
  @ExtDirectPollMethod(event="message2")
  public int handleMessage2(Locale locale, @RequestParam(value="id") int id) {
    Assert.assertNotNull(locale);
    return id*2;
  }
  
  @ExtDirectPollMethod(event="message3")
  public int handleMessage3(@RequestParam(value="id", defaultValue="1") int id, HttpServletRequest request) {
    Assert.assertNotNull(request);
    return id*2;
  }
  
  @ExtDirectPollMethod(event="message4")
  public Integer handleMessage4(@RequestParam(value="id", required=false) Integer id, String dummy) {
    Assert.assertNull(dummy);
    if (id != null) {
      return id*2;
    } 
    return null;
  }  
}