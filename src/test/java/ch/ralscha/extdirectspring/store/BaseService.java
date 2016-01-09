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
package ch.ralscha.extdirectspring.store;

import java.util.List;

import ch.ralscha.extdirectspring.annotation.ExtDirectMethod;
import ch.ralscha.extdirectspring.annotation.ExtDirectMethodType;
import ch.ralscha.extdirectspring.bean.ExtDirectStoreResult;

@SuppressWarnings("unused")
public class BaseService<T extends BaseModel> {

	@ExtDirectMethod(value = ExtDirectMethodType.STORE_MODIFY, group = "store")
	public ExtDirectStoreResult<Book> update3(List<Book> updates) {
		return new ExtDirectStoreResult<Book>(update4(updates));
	}

	@ExtDirectMethod(value = ExtDirectMethodType.STORE_MODIFY, group = "store")
	public List<Book> update4(List<Book> updates) {
		for (Book book : updates) {
			book.setIsbn("UPDATED_" + book.getIsbn());
		}
		return updates;
	}

	@ExtDirectMethod(value = ExtDirectMethodType.STORE_MODIFY, group = "store")
	public ExtDirectStoreResult<Integer> delete3(List<Integer> deletes) {
		return new ExtDirectStoreResult<Integer>(
				deletes.toArray(new Integer[deletes.size()]));
	}

	@ExtDirectMethod(value = ExtDirectMethodType.STORE_MODIFY, group = "store")
	public List<Book> delete4(List<Book> deletes) {
		for (Book book : deletes) {
			book.setTitle(null);
			book.setIsbn("DELETED_" + book.getIsbn());
		}
		return deletes;
	}

	@ExtDirectMethod(value = ExtDirectMethodType.STORE_MODIFY, group = "store")
	public ExtDirectStoreResult<Book> create3(List<Book> inserts) {
		return new ExtDirectStoreResult<Book>(create4(inserts));
	}

	@ExtDirectMethod(value = ExtDirectMethodType.STORE_MODIFY, group = "store")
	public List<Book> create4(List<Book> inserts) {
		int id = 3;
		for (Book book : inserts) {
			book.setId(id++);
		}
		return inserts;
	}
}
