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
import ch.ralscha.extdirectspring.bean.EdStoreResult;
import ch.ralscha.extdirectspring.bean.ExtDirectStoreResult;
import ch.ralscha.extdirectspring.bean.ModelAndJsonView;

@Service
public class StoreModifyMethodService extends BaseViewService {

	@ExtDirectMethod(value = ExtDirectMethodType.STORE_MODIFY)
	public List<Employee> noView() {
		return createEmployees(2);
	}

	@ExtDirectMethod(value = ExtDirectMethodType.STORE_MODIFY,
			jsonView = Views.Summary.class)
	public List<Employee> annotationSummaryView() {
		return createEmployees(2);
	}

	@ExtDirectMethod(value = ExtDirectMethodType.STORE_MODIFY,
			jsonView = Views.Detail.class)
	public List<Employee> annotationDetailView() {
		return createEmployees(2);
	}

	@ExtDirectMethod(value = ExtDirectMethodType.STORE_MODIFY)
	public ModelAndJsonView majSummaryView() {
		return new ModelAndJsonView(createEmployees(2), Views.Summary.class);
	}

	@ExtDirectMethod(value = ExtDirectMethodType.STORE_MODIFY)
	public ModelAndJsonView majDetailView() {
		return new ModelAndJsonView(createEmployees(2), Views.Detail.class);
	}

	@ExtDirectMethod(value = ExtDirectMethodType.STORE_MODIFY,
			jsonView = Views.Summary.class)
	public ModelAndJsonView overrideMajDetailView() {
		return new ModelAndJsonView(createEmployees(2), Views.Detail.class);
	}

	@ExtDirectMethod(value = ExtDirectMethodType.STORE_MODIFY,
			jsonView = Views.Summary.class)
	public ModelAndJsonView overrideMajNoView() {
		return new ModelAndJsonView(createEmployees(2), ExtDirectMethod.NoJsonView.class);
	}

	@ExtDirectMethod(value = ExtDirectMethodType.STORE_MODIFY)
	public ExtDirectStoreResult<Employee> resultSummaryView() {
		ExtDirectStoreResult<Employee> result = new ExtDirectStoreResult<Employee>(
				createEmployees(2));
		result.setJsonView(Views.Summary.class);
		return result;
	}

	@ExtDirectMethod(value = ExtDirectMethodType.STORE_MODIFY)
	public EdStoreResult resultSummaryViewEd() {
		return EdStoreResult.success(createEmployees(2), Views.Summary.class);
	}

	@ExtDirectMethod(value = ExtDirectMethodType.STORE_MODIFY)
	public ExtDirectStoreResult<Employee> resultDetailView() {
		ExtDirectStoreResult<Employee> result = new ExtDirectStoreResult<Employee>(
				createEmployees(2));
		result.setJsonView(Views.Detail.class);
		return result;
	}

	@ExtDirectMethod(value = ExtDirectMethodType.STORE_MODIFY)
	public EdStoreResult resultDetailViewEd() {
		return EdStoreResult.success(createEmployees(2), Views.Detail.class);
	}

	@ExtDirectMethod(value = ExtDirectMethodType.STORE_MODIFY,
			jsonView = Views.Summary.class)
	public ExtDirectStoreResult<Employee> overrideResultDetailView() {
		ExtDirectStoreResult<Employee> result = new ExtDirectStoreResult<Employee>(
				createEmployees(2));
		result.setJsonView(Views.Detail.class);
		return result;
	}

	@ExtDirectMethod(value = ExtDirectMethodType.STORE_MODIFY,
			jsonView = Views.Summary.class)
	public EdStoreResult overrideResultDetailViewEd() {
		return EdStoreResult.success(createEmployees(2), Views.Detail.class);
	}

	@ExtDirectMethod(value = ExtDirectMethodType.STORE_MODIFY,
			jsonView = Views.Summary.class)
	public ExtDirectStoreResult<Employee> overrideResultNoView() {
		ExtDirectStoreResult<Employee> result = new ExtDirectStoreResult<Employee>(
				createEmployees(2));
		result.setJsonView(ExtDirectMethod.NoJsonView.class);
		return result;
	}

	@ExtDirectMethod(value = ExtDirectMethodType.STORE_MODIFY,
			jsonView = Views.Summary.class)
	public EdStoreResult overrideResultNoViewEd() {
		return EdStoreResult.success(createEmployees(2),
				ExtDirectMethod.NoJsonView.class);
	}
}
