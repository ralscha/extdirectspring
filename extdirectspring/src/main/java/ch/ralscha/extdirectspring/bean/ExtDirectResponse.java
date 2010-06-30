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

import org.codehaus.jackson.annotate.JsonWriteNullProperties;

/**
 * Class representing the response of a Ext.Direct call
 *
 * @author mansari
 * @author Ralph Schaer
 */
public class ExtDirectResponse {

  private String type;
  private int tid;
  private String action;
  private String method;
  private Object result;
  private String message;
  private String where;

  public String getAction() {
    return action;
  }

  public void setAction(String action) {
    this.action = action;
  }

  public String getMethod() {
    return method;
  }

  public void setMethod(String method) {
    this.method = method;
  }

  public Object getResult() {
    return result;
  }

  public void setResult(Object result) {
    this.result = result;
  }

  public int getTid() {
    return tid;
  }

  public void setTid(int tid) {
    this.tid = tid;
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  @JsonWriteNullProperties(false)
  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }

  @JsonWriteNullProperties(false)
  public String getWhere() {
    return where;
  }

  public void setWhere(String where) {
    this.where = where;
  }

  @Override
  public String toString() {
    return "ExtDirectResponse [action=" + action + ", message=" + message + ", method=" + method + ", result=" + result + ", tid=" + tid
        + ", type=" + type + ", where=" + where + "]";
  }

}
