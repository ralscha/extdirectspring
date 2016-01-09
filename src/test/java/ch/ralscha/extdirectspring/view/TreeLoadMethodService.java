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

import java.util.List;

import org.springframework.stereotype.Service;

import ch.ralscha.extdirectspring.annotation.ExtDirectMethod;
import ch.ralscha.extdirectspring.annotation.ExtDirectMethodType;
import ch.ralscha.extdirectspring.bean.ModelAndJsonView;

@Service
public class TreeLoadMethodService extends BaseViewService {

	@ExtDirectMethod(value = ExtDirectMethodType.TREE_LOAD)
	public List<Employee> noView() {
		return createEmployees(2);
	}

	@ExtDirectMethod(value = ExtDirectMethodType.TREE_LOAD,
			jsonView = Views.Summary.class)
	public List<Employee> annotationSummaryView() {
		return createEmployees(2);
	}

	@ExtDirectMethod(value = ExtDirectMethodType.TREE_LOAD, jsonView = Views.Detail.class)
	public List<Employee> annotationDetailView() {
		return createEmployees(2);
	}

	@ExtDirectMethod(value = ExtDirectMethodType.TREE_LOAD)
	public ModelAndJsonView majSummaryView() {
		return new ModelAndJsonView(createEmployees(2), Views.Summary.class);
	}

	@ExtDirectMethod(value = ExtDirectMethodType.TREE_LOAD)
	public ModelAndJsonView majDetailView() {
		return new ModelAndJsonView(createEmployees(2), Views.Detail.class);
	}

	@ExtDirectMethod(value = ExtDirectMethodType.TREE_LOAD,
			jsonView = Views.Summary.class)
	public ModelAndJsonView overrideMajDetailView() {
		return new ModelAndJsonView(createEmployees(2), Views.Detail.class);
	}

	@ExtDirectMethod(value = ExtDirectMethodType.TREE_LOAD,
			jsonView = Views.Summary.class)
	public ModelAndJsonView overrideMajNoView() {
		return new ModelAndJsonView(createEmployees(2), ExtDirectMethod.NoJsonView.class);
	}

}
