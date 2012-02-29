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
package ch.ralscha.extdirectspring.store;

import static org.fest.assertions.Assertions.assertThat;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import ch.ralscha.extdirectspring.bean.ExtDirectResponse;
import ch.ralscha.extdirectspring.bean.ExtDirectStoreResponse;
import ch.ralscha.extdirectspring.controller.ControllerUtil;
import ch.ralscha.extdirectspring.controller.RouterController;

@SuppressWarnings("all")
public class CrudTestMethods {

	private String serviceName;

	public CrudTestMethods(String serviceName) {
		this.serviceName = serviceName;
	}

	public void testCreate(RouterController controller, MockHttpServletResponse response, MockHttpServletRequest request) {
		testCreateRecordsOne(controller, response, request);
		testCreateRecordsMany(controller, response, request);
		testCreateOne(controller, response, request);
		testCreateMany(controller, response, request);
	}

	public void testUpdate(RouterController controller, MockHttpServletResponse response, MockHttpServletRequest request) {
		testUpdateRecordsOne(controller, response, request);
		testUpdateRecordsMany(controller, response, request);
		testUpdateOne(controller, response, request);
		testUpdateMany(controller, response, request);
	}

	public void testDelete(RouterController controller, MockHttpServletResponse response, MockHttpServletRequest request) {
		testDeleteRecordsOne(controller, response, request);
		testDeleteRecordsMany(controller, response, request);
		testDeleteOne(controller, response, request);
		testDeleteMany(controller, response, request);
	}

	public void testRead(RouterController controller, MockHttpServletResponse response, MockHttpServletRequest request) {
		Map<String, Object> pagingParameters = new HashMap<String, Object>();
		pagingParameters.put("page", 1);
		pagingParameters.put("start", 0);
		pagingParameters.put("limit", 50);

		Map<String, Object> edRequest = ControllerUtil.createRequestJson(serviceName, "readWithPaging", 1,
				pagingParameters);
		List<ExtDirectResponse> responses = controller.router(request, response, Locale.ENGLISH, edRequest);

		assertThat(responses).hasSize(1);
		ExtDirectResponse resp = responses.get(0);
		assertResponse(resp, "readWithPaging");
		ExtDirectStoreResponse<Book> storeResponse = (ExtDirectStoreResponse<Book>) resp.getResult();
		assertThat(storeResponse.getTotal()).isEqualTo(Integer.valueOf(51));
		assertThat(storeResponse.isSuccess()).isTrue();
		assertThat(storeResponse.getRecords().size()).isEqualTo(2);

		Iterator<Book> it = storeResponse.getRecords().iterator();

		Book aBook = it.next();
		assertThat(aBook.getId().intValue()).isEqualTo(1);
		assertThat(aBook.getTitle()).isEqualTo("Ext JS in Action");
		assertThat(aBook.getIsbn()).isEqualTo("1935182110");

		aBook = it.next();
		assertThat(aBook.getId().intValue()).isEqualTo(2);
		assertThat(aBook.getTitle()).isEqualTo("Learning Ext JS 3.2");
		assertThat(aBook.getIsbn()).isEqualTo("1849511209");

		edRequest = ControllerUtil.createRequestJson(serviceName, "read", 1);
		responses = controller.router(request, response, Locale.ENGLISH, edRequest);

		assertThat(responses).hasSize(1);
		resp = responses.get(0);
		assertResponse(resp, "read");
		Collection<Book> books = (Collection<Book>) resp.getResult();
		it = books.iterator();

		aBook = it.next();
		assertThat(aBook.getId().intValue()).isEqualTo(1);
		assertThat(aBook.getTitle()).isEqualTo("Ext JS in Action");
		assertThat(aBook.getIsbn()).isEqualTo("1935182110");

		aBook = it.next();
		assertThat(aBook.getId().intValue()).isEqualTo(2);
		assertThat(aBook.getTitle()).isEqualTo("Learning Ext JS 3.2");
		assertThat(aBook.getIsbn()).isEqualTo("1849511209");
	}

	private void testUpdateRecordsOne(RouterController controller, MockHttpServletResponse response,
			MockHttpServletRequest request) {
		Map<String, Object> storeRequest = new LinkedHashMap<String, Object>();
		storeRequest.put("records", new Book(1, "an update", "9999999"));
		Map<String, Object> edRequest = ControllerUtil.createRequestJson(serviceName, "update3", 1, storeRequest);
		List<ExtDirectResponse> responses = controller.router(request, response, Locale.ENGLISH, edRequest);

		assertThat(responses).hasSize(1);
		ExtDirectResponse resp = responses.get(0);
		assertResponse(resp, "update3");
		assertUpdateResponse(resp, 1, 3);
	}

	private void testUpdateRecordsMany(RouterController controller, MockHttpServletResponse response,
			MockHttpServletRequest request) {
		Map<String, Object> storeRequest = new LinkedHashMap<String, Object>();
		List<Book> newBooks = new ArrayList<Book>();
		newBooks.add(new Book(1, "an update", "9999999"));
		newBooks.add(new Book(2, "a second update", "8888888"));

		storeRequest.put("records", newBooks);
		Map<String, Object> edRequest = ControllerUtil.createRequestJson(serviceName, "update3", 1, storeRequest);
		List<ExtDirectResponse> responses = controller.router(request, response, Locale.ENGLISH, edRequest);

		assertThat(responses).hasSize(1);
		ExtDirectResponse resp = responses.get(0);
		assertResponse(resp, "update3");
		assertUpdateResponse(resp, 2, 3);
	}

	private void testUpdateOne(RouterController controller, MockHttpServletResponse response,
			MockHttpServletRequest request) {
		Book updatedBook = new Book(1, "an update", "9999999");
		Map<String, Object> edRequest = ControllerUtil.createRequestJson(serviceName, "update4", 1, updatedBook);
		List<ExtDirectResponse> responses = controller.router(request, response, Locale.ENGLISH, edRequest);

		assertThat(responses).hasSize(1);
		ExtDirectResponse resp = responses.get(0);
		assertResponse(resp, "update4");
		assertUpdateResponse(resp, 1, 4);
	}

	private void testUpdateMany(RouterController controller, MockHttpServletResponse response,
			MockHttpServletRequest request) {
		List<Book> newBooks = new ArrayList<Book>();
		newBooks.add(new Book(1, "an update", "9999999"));
		newBooks.add(new Book(2, "a second update", "8888888"));
		Map<String, Object> edRequest = ControllerUtil.createRequestJson(serviceName, "update4", 1, newBooks);
		List<ExtDirectResponse> responses = controller.router(request, response, Locale.ENGLISH, edRequest);

		assertThat(responses).hasSize(1);
		ExtDirectResponse resp = responses.get(0);
		assertResponse(resp, "update4");
		assertUpdateResponse(resp, 2, 4);
	}

	private void assertUpdateResponse(ExtDirectResponse resp, int noOfRecords, int version) {

		Iterator<Book> it = null;
		if (version == 3) {
			ExtDirectStoreResponse<Book> storeResponse = (ExtDirectStoreResponse<Book>) resp.getResult();
			assertThat(storeResponse.getTotal()).isNull();
			assertThat(storeResponse.isSuccess()).isTrue();
			assertThat(storeResponse.getRecords().size()).isEqualTo(noOfRecords);
			it = storeResponse.getRecords().iterator();
		} else {
			Collection<Book> books = (Collection<Book>) resp.getResult();
			it = books.iterator();
		}

		Book aBook = it.next();
		assertThat(aBook.getId().intValue()).isEqualTo(1);
		assertThat(aBook.getTitle()).isEqualTo("an update");
		assertThat(aBook.getIsbn()).isEqualTo("UPDATED_9999999");

		if (noOfRecords > 1) {
			aBook = it.next();
			assertThat(aBook.getId().intValue()).isEqualTo(2);
			assertThat(aBook.getTitle()).isEqualTo("a second update");
			assertThat(aBook.getIsbn()).isEqualTo("UPDATED_8888888");
		}

	}

	private void testCreateRecordsOne(RouterController controller, MockHttpServletResponse response,
			MockHttpServletRequest request) {
		Map<String, Object> storeRequest = new LinkedHashMap<String, Object>();
		storeRequest.put("records", new Book(-1, "Ext JS 3.0 Cookbook", "1847198708"));
		Map<String, Object> edRequest = ControllerUtil.createRequestJson(serviceName, "create3", 1, storeRequest);
		List<ExtDirectResponse> responses = controller.router(request, response, Locale.ENGLISH, edRequest);

		assertThat(responses).hasSize(1);
		ExtDirectResponse resp = responses.get(0);
		assertResponse(resp, "create3");
		assertCreateResponse(resp, 1, 3);
	}

	private void testCreateRecordsMany(RouterController controller, MockHttpServletResponse response,
			MockHttpServletRequest request) {
		Map<String, Object> storeRequest = new LinkedHashMap<String, Object>();
		List<Book> newBooks = new ArrayList<Book>();
		newBooks.add(new Book(-1, "Ext JS 3.0 Cookbook", "1847198708"));
		newBooks.add(new Book(-1, "Learning Ext JS 3.2", "1849511209"));

		storeRequest.put("records", newBooks);
		Map<String, Object> edRequest = ControllerUtil.createRequestJson(serviceName, "create3", 1, storeRequest);
		List<ExtDirectResponse> responses = controller.router(request, response, Locale.ENGLISH, edRequest);

		assertThat(responses).hasSize(1);
		ExtDirectResponse resp = responses.get(0);
		assertResponse(resp, "create3");
		assertCreateResponse(resp, 2, 3);
	}

	private void testCreateOne(RouterController controller, MockHttpServletResponse response,
			MockHttpServletRequest request) {

		Book newBook = new Book(-1, "Ext JS 3.0 Cookbook", "1847198708");
		Map<String, Object> edRequest = ControllerUtil.createRequestJson(serviceName, "create4", 1, newBook);
		List<ExtDirectResponse> responses = controller.router(request, response, Locale.ENGLISH, edRequest);

		assertThat(responses).hasSize(1);
		ExtDirectResponse resp = responses.get(0);
		assertResponse(resp, "create4");
		assertCreateResponse(resp, 1, 4);
	}

	private void testCreateMany(RouterController controller, MockHttpServletResponse response,
			MockHttpServletRequest request) {
		List<Book> newBooks = new ArrayList<Book>();
		newBooks.add(new Book(-1, "Ext JS 3.0 Cookbook", "1847198708"));
		newBooks.add(new Book(-1, "Learning Ext JS 3.2", "1849511209"));

		Map<String, Object> edRequest = ControllerUtil.createRequestJson(serviceName, "create4", 1, newBooks);
		List<ExtDirectResponse> responses = controller.router(request, response, Locale.ENGLISH, edRequest);

		assertThat(responses).hasSize(1);
		ExtDirectResponse resp = responses.get(0);
		assertResponse(resp, "create4");
		assertCreateResponse(resp, 2, 4);
	}

	private void testDeleteRecordsOne(RouterController controller, MockHttpServletResponse response,
			MockHttpServletRequest request) {
		Map<String, Object> storeRequest = new LinkedHashMap<String, Object>();
		storeRequest.put("records", new Integer(1));
		Map<String, Object> edRequest = ControllerUtil.createRequestJson(serviceName, "delete3", 1, storeRequest);
		List<ExtDirectResponse> responses = controller.router(request, response, Locale.ENGLISH, edRequest);

		assertThat(responses).hasSize(1);
		ExtDirectResponse resp = responses.get(0);
		assertResponse(resp, "delete3");

		ExtDirectStoreResponse<Integer> storeResponse = (ExtDirectStoreResponse<Integer>) resp.getResult();
		assertThat(storeResponse.getTotal()).isNull();
		assertThat(storeResponse.isSuccess()).isTrue();
		assertThat(storeResponse.getRecords().size()).isEqualTo(1);
		Integer deleteBookId = storeResponse.getRecords().iterator().next();
		assertThat(deleteBookId.intValue()).isEqualTo(1);
	}

	private void testDeleteRecordsMany(RouterController controller, MockHttpServletResponse response,
			MockHttpServletRequest request) {
		Map<String, Object> storeRequest = new LinkedHashMap<String, Object>();
		List<Integer> booksToDelete = new ArrayList<Integer>();
		booksToDelete.add(1);
		booksToDelete.add(2);

		storeRequest.put("records", booksToDelete);
		Map<String, Object> edRequest = ControllerUtil.createRequestJson(serviceName, "delete3", 1, storeRequest);
		List<ExtDirectResponse> responses = controller.router(request, response, Locale.ENGLISH, edRequest);

		assertThat(responses).hasSize(1);
		ExtDirectResponse resp = responses.get(0);
		assertResponse(resp, "delete3");

		ExtDirectStoreResponse<Integer> storeResponse = (ExtDirectStoreResponse<Integer>) resp.getResult();
		assertThat(storeResponse.getTotal()).isNull();
		assertThat(storeResponse.isSuccess()).isTrue();
		assertThat(storeResponse.getRecords().size()).isEqualTo(2);
		Iterator<Integer> it = storeResponse.getRecords().iterator();
		Integer deleteBookId = it.next();
		assertThat(deleteBookId.intValue()).isEqualTo(1);
		deleteBookId = it.next();
		assertThat(deleteBookId.intValue()).isEqualTo(2);
	}

	private void testDeleteOne(RouterController controller, MockHttpServletResponse response,
			MockHttpServletRequest request) {

		Book deleteBook = new Book(11, "Ext JS 3.0 Cookbook", "1847198708");
		Map<String, Object> edRequest = ControllerUtil.createRequestJson(serviceName, "delete4", 1, deleteBook);
		List<ExtDirectResponse> responses = controller.router(request, response, Locale.ENGLISH, edRequest);

		assertThat(responses).hasSize(1);
		ExtDirectResponse resp = responses.get(0);
		assertResponse(resp, "delete4");

		Collection<Book> storeResponse = (Collection<Book>) resp.getResult();
		assertThat(storeResponse).hasSize(1);
		Book book = storeResponse.iterator().next();
		assertThat(book.getId()).isEqualTo(Integer.valueOf(11));
		assertThat(book.getTitle()).isNull();
		assertThat(book.getIsbn()).isEqualTo("DELETED_1847198708");
	}

	private void testDeleteMany(RouterController controller, MockHttpServletResponse response,
			MockHttpServletRequest request) {

		List<Book> deletedBooks = new ArrayList<Book>();
		deletedBooks.add(new Book(9, "Ext JS 3.0 Cookbook", "1847198708"));
		deletedBooks.add(new Book(10, "Learning Ext JS 3.2", "1849511209"));

		Map<String, Object> edRequest = ControllerUtil.createRequestJson(serviceName, "delete4", 1, deletedBooks);
		List<ExtDirectResponse> responses = controller.router(request, response, Locale.ENGLISH, edRequest);

		assertThat(responses).hasSize(1);
		ExtDirectResponse resp = responses.get(0);
		assertResponse(resp, "delete4");

		Collection<Book> storeResponse = (Collection<Book>) resp.getResult();
		assertThat(storeResponse).hasSize(2);
		Iterator<Book> it = storeResponse.iterator();

		Book book = it.next();
		assertThat(book.getId()).isEqualTo(Integer.valueOf(9));
		assertThat(book.getTitle()).isNull();
		assertThat(book.getIsbn()).isEqualTo("DELETED_1847198708");

		book = it.next();
		assertThat(book.getId()).isEqualTo(Integer.valueOf(10));
		assertThat(book.getTitle()).isNull();
		assertThat(book.getIsbn()).isEqualTo("DELETED_1849511209");
	}

	private void assertCreateResponse(ExtDirectResponse resp, int noOfRecords, int version) {

		Iterator<Book> it = null;
		if (version == 3) {
			ExtDirectStoreResponse<Book> storeResponse = (ExtDirectStoreResponse<Book>) resp.getResult();
			assertThat(storeResponse.getTotal()).isNull();
			assertThat(storeResponse.isSuccess()).isTrue();
			assertThat(storeResponse.getRecords().size()).isEqualTo(noOfRecords);
			it = storeResponse.getRecords().iterator();
		} else {
			Collection<Book> books = (Collection<Book>) resp.getResult();
			it = books.iterator();
		}

		Book aBook = it.next();
		assertThat(aBook.getId().intValue()).isEqualTo(3);
		assertThat(aBook.getTitle()).isEqualTo("Ext JS 3.0 Cookbook");
		assertThat(aBook.getIsbn()).isEqualTo("1847198708");

		if (noOfRecords > 1) {
			aBook = it.next();
			assertThat(aBook.getId().intValue()).isEqualTo(4);
			assertThat(aBook.getTitle()).isEqualTo("Learning Ext JS 3.2");
			assertThat(aBook.getIsbn()).isEqualTo("1849511209");
		}
	}

	private void assertResponse(ExtDirectResponse resp, String method) {
		assertThat(resp.getAction()).isEqualTo(serviceName);
		assertThat(resp.getMethod()).isEqualTo(method);
		assertThat(resp.getType()).isEqualTo("rpc");
		assertThat(resp.getTid()).isEqualTo(1);
		assertThat(resp.getMessage()).isNull();
		assertThat(resp.getWhere()).isNull();
		assertThat(resp.getResult()).isNotNull();
	}

}
