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

package ch.ralscha.extdirectspring.demo.filter;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.springframework.util.StringUtils;

import ch.ralscha.extdirectspring.annotation.ExtDirectMethod;
import ch.ralscha.extdirectspring.annotation.ExtDirectMethodType;
import ch.ralscha.extdirectspring.bean.ExtDirectStoreReadRequest;
import ch.ralscha.extdirectspring.bean.ExtDirectStoreResponse;

import com.google.common.collect.Ordering;

@Named
public class FilterAction {

  @Inject
  private CompanyDataBean dataBean;

  @ExtDirectMethod(value = ExtDirectMethodType.STORE_READ, group = "filter")
  public ExtDirectStoreResponse<Company> load(ExtDirectStoreReadRequest request) {

    List<Company> companies;
    if (!request.getFilters().isEmpty()) {
      companies = dataBean.findCompanies(request.getFilters());
    } else {
      companies = dataBean.findAllCompanies();
    }

    int totalSize = companies.size();

    if (StringUtils.hasText(request.getSort())) {
      Ordering<Object> ordering = new PropertyOrdering(Company.class, request.getSort());
      if (request.isDescendingSort()) {
        ordering = ordering.reverse();
      }
      companies = ordering.sortedCopy(companies);
    }

    if (request.getStart() != null && request.getLimit() != null) {
      companies = companies.subList(request.getStart(), Math.min(totalSize, request.getStart() + request.getLimit()));
    }

    return new ExtDirectStoreResponse<Company>(totalSize, companies);
  }

}
