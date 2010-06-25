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

package com.googlecode.extdirectspring.bean;

/**
*
* Class representing the request of a DirectStore read call
*
* @author Ralph Schaer
*/
public class ExtDirectStoreReadRequest {

  private String query;
  private Integer limit;
  private Integer start;
  private String dir;
  private String sort;

  public String getQuery() {
    return query;
  }

  public void setQuery(String query) {
    this.query = query;
  }

  public Integer getLimit() {
    return limit;
  }

  public void setLimit(Integer limit) {
    this.limit = limit;
  }

  public Integer getStart() {
    return start;
  }

  public void setStart(Integer start) {
    this.start = start;
  }

  public String getDir() {
    return dir;
  }

  public void setDir(String dir) {
    this.dir = dir;
  }

  public boolean isAscendingSort() {
    return ("ASC".equals(getDir()));
  }
  
  public boolean isDecendingSort() {
    return ("DESC".equals(getDir()));
  }
  
  public String getSort() {
    return sort;
  }

  public void setSort(String sort) {
    this.sort = sort;
  }

}
