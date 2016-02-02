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
package ch.ralscha.extdirectspring.bean;

import ch.ralscha.extdirectspring.annotation.ExtDirectMethod;
import ch.ralscha.extdirectspring.annotation.ExtDirectMethodType;

/**
 * A {@link ExtDirectMethod} can return an instance of this class to specify a JsonView
 * that Jackson uses to serialize the response. Not supported for
 * {@link ExtDirectMethodType#FORM_POST}.
 * <p>
 * If the property jsonView is set it overrides a jsonView specified on
 * {@link ExtDirectMethod#jsonView()}.
 * <p>
 * To disable a JsonView specified on {@link ExtDirectMethod#jsonView()} set the property
 * jsonView to {@link ch.ralscha.extdirectspring.annotation.ExtDirectMethod.NoJsonView}.
 */
public class ModelAndJsonView extends JsonViewHint {

	public Object model;

	public ModelAndJsonView() {
		// default constructor
	}

	public ModelAndJsonView(Object model, Class<?> jsonView) {
		super(jsonView);
		this.model = model;
	}

	public Object getModel() {
		return this.model;
	}

	public void setModel(Object model) {
		this.model = model;
	}

}
