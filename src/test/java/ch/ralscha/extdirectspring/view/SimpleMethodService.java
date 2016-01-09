/**
 * Copyright 2010-2016 Ralph Schaer <ralphschaer@gmail.com>
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
package ch.ralscha.extdirectspring.view;

import org.springframework.stereotype.Service;

import ch.ralscha.extdirectspring.annotation.ExtDirectMethod;
import ch.ralscha.extdirectspring.bean.ModelAndJsonView;

@Service
public class SimpleMethodService extends BaseViewService {

	@ExtDirectMethod
	public Employee noView() {
		return createEmployee();
	}

	@ExtDirectMethod(jsonView = Views.Summary.class)
	public Employee annotationSummaryView() {
		return createEmployee();
	}

	@ExtDirectMethod(jsonView = Views.Detail.class)
	public Employee annotationDetailView() {
		return createEmployee();
	}

	@ExtDirectMethod
	public ModelAndJsonView majSummaryView() {
		return new ModelAndJsonView(createEmployee(), Views.Summary.class);
	}

	@ExtDirectMethod
	public ModelAndJsonView majDetailView() {
		return new ModelAndJsonView(createEmployee(), Views.Detail.class);
	}

	@ExtDirectMethod(jsonView = Views.Summary.class)
	public ModelAndJsonView overrideMajDetailView() {
		return new ModelAndJsonView(createEmployee(), Views.Detail.class);
	}

	@ExtDirectMethod(jsonView = Views.Summary.class)
	public ModelAndJsonView overrideMajNoView() {
		return new ModelAndJsonView(createEmployee(), ExtDirectMethod.NoJsonView.class);
	}

	@ExtDirectMethod
	public EmployeeWithJsonView subclassSummaryView() {
		return createEmployeeWithJsonView(Views.Summary.class);
	}

	@ExtDirectMethod
	public EmployeeWithJsonView subclassDetailView() {
		return createEmployeeWithJsonView(Views.Detail.class);
	}

	@ExtDirectMethod(jsonView = Views.Summary.class)
	public EmployeeWithJsonView overrideSubclassDetailView() {
		return createEmployeeWithJsonView(Views.Detail.class);
	}

	@ExtDirectMethod(jsonView = Views.Summary.class)
	public EmployeeWithJsonView overrideSubclassNoView() {
		return createEmployeeWithJsonView(ExtDirectMethod.NoJsonView.class);
	}

}
