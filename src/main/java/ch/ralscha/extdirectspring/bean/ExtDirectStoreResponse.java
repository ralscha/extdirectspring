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
import java.util.Map;

import org.codehaus.jackson.annotate.JsonPropertyOrder;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.codehaus.jackson.map.annotate.JsonSerialize.Inclusion;

/**
 * Class representing the response of a DirectStore request
 * 
 * @author Ralph Schaer
 * @param <T>
 *          Type of the entry inside the collection
 */
@JsonSerialize(include=Inclusion.NON_NULL)
@JsonPropertyOrder(value={"metaData","success","total", "records"})
public class ExtDirectStoreResponse<T> {

  private Integer total;
  private Collection<T> records;
  private Boolean success;
  private MetaData metaData;

  public ExtDirectStoreResponse() {
    // default constructor
  }

  public ExtDirectStoreResponse(final Collection<T> records) {
    this(null, records, true);
  }

  public ExtDirectStoreResponse(final Integer total, final Collection<T> records) {
    this(total, records, true);
  }

  public ExtDirectStoreResponse(final Integer total, final Collection<T> records, final Boolean success) {
    this.total = total;
    this.records = records;
    this.success = success;
  }

  public Integer getTotal() {
    return total;
  }

  public void setTotal(final Integer total) {
    this.total = total;
  }

  public Collection<T> getRecords() {
    return records;
  }

  public void setRecords(final Collection<T> records) {
    this.records = records;
  }

  public Boolean isSuccess() {
    return success;
  }

  public void setSuccess(final Boolean success) {
    this.success = success;
  }

  public Map<String,Object> getMetaData() {
    if (metaData != null) {
      return metaData.getMetaData();
    }
    return null;
  }
  
  public void setMetaData(MetaData metaData) {
    this.metaData = metaData;
  }

  @Override
  public String toString() {
    return "ExtDirectStoreResponse [records=" + records + ", success=" + success + ", total=" + total + "]";
  }

}
