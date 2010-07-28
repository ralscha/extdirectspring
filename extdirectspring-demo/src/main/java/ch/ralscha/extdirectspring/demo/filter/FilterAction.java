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
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Named;

import org.springframework.util.StringUtils;

import ch.ralscha.extdirectspring.annotation.ExtDirectMethod;
import ch.ralscha.extdirectspring.annotation.ExtDirectMethodType;
import ch.ralscha.extdirectspring.bean.ExtDirectStoreReadRequest;
import ch.ralscha.extdirectspring.bean.ExtDirectStoreResponse;

import com.google.common.collect.Maps;
import com.google.common.collect.Ordering;
import com.google.common.primitives.Booleans;
import com.google.common.primitives.Ints;

@Named
public class FilterAction {

  @Inject
  private CompanyDataBean dataBean;

  private Map<String, Ordering<Company>> orderingMap;

  public FilterAction() {
    orderingMap = Maps.newHashMap();

    orderingMap.put("company", new Ordering<Company>() {
      public int compare(Company left, Company right) {
        return left.getCompany().compareTo(right.getCompany());
      }
    });

    orderingMap.put("id", new Ordering<Company>() {
      public int compare(Company left, Company right) {
        return Ints.compare(left.getId(), right.getId());
      }
    });

    orderingMap.put("price", new Ordering<Company>() {
      public int compare(Company left, Company right) {
        return left.getPrice().compareTo(right.getPrice());
      }
    });

    orderingMap.put("date", new Ordering<Company>() {
      public int compare(Company left, Company right) {
        return left.getDate().compareTo(right.getDate());
      }
    });

    orderingMap.put("visible", new Ordering<Company>() {
      public int compare(Company left, Company right) {
        return Booleans.compare(left.isVisible(), right.isVisible());
      }
    });

    orderingMap.put("size", new Ordering<Company>() {
      public int compare(Company left, Company right) {
        return left.getSize().ordinal() - right.getSize().ordinal();
      }
    });

  }

  @ExtDirectMethod(value = ExtDirectMethodType.STORE_READ, group = "filter")
  public ExtDirectStoreResponse<Company> load(ExtDirectStoreReadRequest request) {

    List<Company> companies = dataBean.listAllCompanies();
    int totalSize = companies.size();

    Ordering<Company> ordering = null;

    if (StringUtils.hasText(request.getSort())) {
      ordering = orderingMap.get(request.getSort());
      if (request.isDescendingSort()) {
        ordering = ordering.reverse();
      }
    }

    if (ordering != null) {
      companies = ordering.sortedCopy(companies);
    }

    if (request.getStart() != null && request.getLimit() != null) {
      companies = companies.subList(request.getStart(), Math.min(totalSize, request.getStart() + request.getLimit()));
    }

    return new ExtDirectStoreResponse<Company>(totalSize, companies);
  }

}
