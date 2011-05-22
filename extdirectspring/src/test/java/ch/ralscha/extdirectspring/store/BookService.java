/**
 * Copyright 2010-2011 Ralph Schaer <ralphschaer@gmail.com>
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
import ch.ralscha.extdirectspring.bean.ExtDirectStoreResponse;

@Service
public class BookService {
	
	@ExtDirectMethod(ExtDirectMethodType.STORE_READ)
	public List<Book> read() {
		List<Book> books = new ArrayList<Book>();
		books.add(new Book(1, "Ext JS in Action", "1935182110"));
		books.add(new Book(2, "Learning Ext JS 3.2", "1849511209"));
		return books;
	}
	
	@ExtDirectMethod(ExtDirectMethodType.STORE_READ)
	public ExtDirectStoreResponse<Book> readWithPaging(ExtDirectStoreReadRequest request) {
		ExtDirectStoreResponse<Book> response = new ExtDirectStoreResponse<Book>(read());
		response.setTotal(request.getPage() + request.getLimit() + request.getStart());
		return response;
	}
	
	@ExtDirectMethod(ExtDirectMethodType.STORE_MODIFY)
	public ExtDirectStoreResponse<Book> update3(List<Book> updates) {
		return new ExtDirectStoreResponse<Book>(update4(updates));
	}
	
	@ExtDirectMethod(ExtDirectMethodType.STORE_MODIFY)
	public List<Book> update4(List<Book> updates) {
		for (Book book : updates) {
			book.setIsbn("UPDATED_"+book.getIsbn());
		}
		return updates;
	}	
	
	@ExtDirectMethod(ExtDirectMethodType.STORE_MODIFY)
	public ExtDirectStoreResponse<Integer> delete3(List<Integer> deletes) {		
		return new ExtDirectStoreResponse<Integer>(deletes);
	}
	
	@ExtDirectMethod(ExtDirectMethodType.STORE_MODIFY)
	public List<Book> delete4(List<Book> deletes) {		
		for (Book book : deletes) {
			book.setTitle(null);
			book.setIsbn("DELETED_"+book.getIsbn());
		}
		return deletes;
	}	
	
	@ExtDirectMethod(ExtDirectMethodType.STORE_MODIFY)
	public ExtDirectStoreResponse<Book> create3(List<Book> inserts) {
		return new ExtDirectStoreResponse<Book>(create4(inserts));
	}
	
	@ExtDirectMethod(ExtDirectMethodType.STORE_MODIFY)
	public List<Book> create4(List<Book> inserts) {
		int id = 3;
		for (Book book : inserts) {
			book.setId(id++);
		}
		return inserts;
	}	
}
