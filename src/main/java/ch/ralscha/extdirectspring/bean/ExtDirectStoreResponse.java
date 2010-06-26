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

import java.util.Collection;
import org.codehaus.jackson.annotate.JsonWriteNullProperties;

/**
* Class representing the response of a DirectStore request
*
* @author Ralph Schaer
*/
@JsonWriteNullProperties(false)
public class ExtDirectStoreResponse<T> {

  private Integer total;
  private Collection<T> records;
  private Boolean success;

     
  public ExtDirectStoreResponse() {
    // default constructor
  }

  public ExtDirectStoreResponse(Collection<T> records) {
    this(null, records, true);
  }
  
  public ExtDirectStoreResponse(Integer total, Collection<T> records) {
    this(total, records, true);
  }

  public ExtDirectStoreResponse(Integer total, Collection<T> records, Boolean success) {
    this.total = total;
    this.records = records;
    this.success = success;
  }

  public Integer getTotal() {
    return total;
  }

  public void setTotal(Integer total) {
    this.total = total;
  }

  public Collection<T> getRecords() {
    return records;
  }

  public void setRecords(Collection<T> records) {
    this.records = records;
  }

  public Boolean isSuccess() {
    return success;
  }

  public void setSuccess(Boolean success) {
    this.success = success;
  }

  @Override
  public String toString() {
    return "ExtDirectStoreResponse [records=" + records + ", success=" + success + ", total=" + total + "]";
  }

}
