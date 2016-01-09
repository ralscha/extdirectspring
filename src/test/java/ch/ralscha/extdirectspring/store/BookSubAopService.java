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

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import ch.ralscha.extdirectspring.annotation.ExtDirectMethod;
import ch.ralscha.extdirectspring.annotation.ExtDirectMethodType;
import ch.ralscha.extdirectspring.bean.ExtDirectStoreReadRequest;
import ch.ralscha.extdirectspring.bean.ExtDirectStoreResult;

@Service
public class BookSubAopService extends BaseService<Book> {

	@ExtDirectMethod(value = ExtDirectMethodType.STORE_READ, group = "store")
	public List<Book> read() {
		List<Book> books = new ArrayList<Book>();
		books.add(new Book(1, "Ext JS in Action", "1935182110"));
		books.add(new Book(2, "Learning Ext JS 3.2", "1849511209"));
		return books;
	}

	@ExtDirectMethod(value = ExtDirectMethodType.STORE_READ, group = "store")
	public ExtDirectStoreResult<Book> readWithPaging(ExtDirectStoreReadRequest request) {
		int total = request.getPage() + request.getLimit() + request.getStart();
		return new ExtDirectStoreResult<Book>(total, read());
	}
}
