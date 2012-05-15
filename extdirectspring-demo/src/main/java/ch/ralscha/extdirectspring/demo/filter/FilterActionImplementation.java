/**
 * Copyright 2010-2012 Ralph Schaer <ralphschaer@gmail.com>
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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;

import ch.ralscha.extdirectspring.bean.ExtDirectStoreReadRequest;
import ch.ralscha.extdirectspring.bean.ExtDirectStoreResponse;
import ch.ralscha.extdirectspring.demo.util.PropertyOrderingFactory;

import com.google.common.collect.Ordering;

@Service
public class FilterActionImplementation implements FilterActionInterface {

	@Autowired
	private CompanyDataBean dataBean;

	@Override
	public ExtDirectStoreResponse<Company> load(ExtDirectStoreReadRequest request, @RequestParam(required = false) String dRif) {

		List<Company> companies;
		if (!request.getFilters().isEmpty()) {
			companies = dataBean.findCompanies(request.getFilters());
		} else {
			companies = dataBean.findAllCompanies();
		}

		int totalSize = companies.size();

		Ordering<Company> ordering = PropertyOrderingFactory.INSTANCE.createOrderingFromSorters(request.getSorters());
		if (ordering != null) {
			companies = ordering.sortedCopy(companies);
		}

		if (request.getStart() != null && request.getLimit() != null) {
			companies = companies.subList(request.getStart(), Math.min(totalSize, request.getStart() + request.getLimit()));
		}

		return new ExtDirectStoreResponse<Company>(totalSize, companies);
	}

}
