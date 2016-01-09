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
package ch.ralscha.extdirectspring.provider;

import java.util.List;

import ch.ralscha.extdirectspring.annotation.ExtDirectMethod;
import ch.ralscha.extdirectspring.annotation.ExtDirectMethodType;

public class BeanNotSpringWExtDirectMethods {

	@ExtDirectMethod
	public void methodA() {
		// a dummy method
	}

	@ExtDirectMethod(value = ExtDirectMethodType.POLL)
	public void methodB() {
		// a dummy method
	}

	@ExtDirectMethod(value = ExtDirectMethodType.STORE_MODIFY)
	public List<Integer> methodC(@SuppressWarnings("unused") List<Integer> ids) {
		// a dummy method
		return null;
	}

	@ExtDirectMethod(value = ExtDirectMethodType.STORE_READ)
	public List<Integer> methodD() {
		// a dummy method
		return null;
	}
}
