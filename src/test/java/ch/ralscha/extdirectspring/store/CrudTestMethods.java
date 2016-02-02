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

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import com.fasterxml.jackson.core.type.TypeReference;

import ch.ralscha.extdirectspring.bean.ExtDirectResponse;
import ch.ralscha.extdirectspring.bean.ExtDirectStoreResult;
import ch.ralscha.extdirectspring.controller.ControllerUtil;

public class CrudTestMethods {

	private final String serviceName;

	public CrudTestMethods(String serviceName) {
		this.serviceName = serviceName;
	}

	public void testCreate(MockMvc mockMvc) throws Exception {
		testCreateRecordsOne(mockMvc);
		testCreateRecordsMany(mockMvc);
		testCreateOne(mockMvc);
		testCreateMany(mockMvc);
	}

	public void testUpdate(MockMvc mockMvc) throws Exception {
		testUpdateRecordsOne(mockMvc);
		testUpdateRecordsMany(mockMvc);
		testUpdateOne(mockMvc);
		testUpdateMany(mockMvc);
	}

	public void testDelete(MockMvc mockMvc) throws Exception {
		testDeleteRecordsOne(mockMvc);
		testDeleteRecordsMany(mockMvc);
		testDeleteOne(mockMvc);
		testDeleteMany(mockMvc);
	}

	public void testRead(MockMvc mockMvc) throws Exception {
		Map<String, Object> pagingParameters = new HashMap<String, Object>();
		pagingParameters.put("page", 1);
		pagingParameters.put("start", 0);
		pagingParameters.put("limit", 50);

		String edRequest = ControllerUtil.createEdsRequest(this.serviceName,
				"readWithPaging", 1, pagingParameters);
		MvcResult result = ControllerUtil.performRouterRequest(mockMvc, edRequest);
		List<ExtDirectResponse> responses = ControllerUtil
				.readDirectResponses(result.getResponse().getContentAsByteArray());

		assertThat(responses).hasSize(1);
		ExtDirectResponse resp = responses.get(0);
		assertResponse(resp, "readWithPaging");
		ExtDirectStoreResult<Book> storeResponse = ControllerUtil.convertValue(
				resp.getResult(),
				new TypeReference<ExtDirectStoreResult<Book>>() {/* nothing_here */
				});
		assertThat(storeResponse.getTotal()).isEqualTo(51L);
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

		edRequest = ControllerUtil.createEdsRequest(this.serviceName, "read", 1, null);
		result = ControllerUtil.performRouterRequest(mockMvc, edRequest);
		responses = ControllerUtil
				.readDirectResponses(result.getResponse().getContentAsByteArray());

		assertThat(responses).hasSize(1);
		resp = responses.get(0);
		assertResponse(resp, "read");
		Collection<Book> books = ControllerUtil.convertValue(resp.getResult(),
				new TypeReference<Collection<Book>>() {
					// nothing here
				});
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

	private void testUpdateRecordsOne(MockMvc mockMvc) throws Exception {
		Map<String, Object> storeRequest = new LinkedHashMap<String, Object>();
		storeRequest.put("records", new Book(1, "an update", "9999999"));
		String edRequest = ControllerUtil.createEdsRequest(this.serviceName, "update3", 1,
				storeRequest);
		MvcResult result = ControllerUtil.performRouterRequest(mockMvc, edRequest);
		List<ExtDirectResponse> responses = ControllerUtil
				.readDirectResponses(result.getResponse().getContentAsByteArray());

		assertThat(responses).hasSize(1);
		ExtDirectResponse resp = responses.get(0);
		assertResponse(resp, "update3");
		assertUpdateResponse(resp, 1, 3);
	}

	private void testUpdateRecordsMany(MockMvc mockMvc) throws Exception {
		Map<String, Object> storeRequest = new LinkedHashMap<String, Object>();
		List<Book> newBooks = new ArrayList<Book>();
		newBooks.add(new Book(1, "an update", "9999999"));
		newBooks.add(new Book(2, "a second update", "8888888"));

		storeRequest.put("records", newBooks);
		String edRequest = ControllerUtil.createEdsRequest(this.serviceName, "update3", 1,
				storeRequest);

		MvcResult result = ControllerUtil.performRouterRequest(mockMvc, edRequest);
		List<ExtDirectResponse> responses = ControllerUtil
				.readDirectResponses(result.getResponse().getContentAsByteArray());

		assertThat(responses).hasSize(1);
		ExtDirectResponse resp = responses.get(0);
		assertResponse(resp, "update3");
		assertUpdateResponse(resp, 2, 3);
	}

	private void testUpdateOne(MockMvc mockMvc) throws Exception {
		Book updatedBook = new Book(1, "an update", "9999999");
		String edRequest = ControllerUtil.createEdsRequest(this.serviceName, "update4", 1,
				updatedBook);

		MvcResult result = ControllerUtil.performRouterRequest(mockMvc, edRequest);
		List<ExtDirectResponse> responses = ControllerUtil
				.readDirectResponses(result.getResponse().getContentAsByteArray());

		assertThat(responses).hasSize(1);
		ExtDirectResponse resp = responses.get(0);
		assertResponse(resp, "update4");
		assertUpdateResponse(resp, 1, 4);
	}

	private void testUpdateMany(MockMvc mockMvc) throws Exception {
		List<Book> newBooks = new ArrayList<Book>();
		newBooks.add(new Book(1, "an update", "9999999"));
		newBooks.add(new Book(2, "a second update", "8888888"));
		String edRequest = ControllerUtil.createEdsRequest(this.serviceName, "update4", 1,
				newBooks);

		MvcResult result = ControllerUtil.performRouterRequest(mockMvc, edRequest);
		List<ExtDirectResponse> responses = ControllerUtil
				.readDirectResponses(result.getResponse().getContentAsByteArray());

		assertThat(responses).hasSize(1);
		ExtDirectResponse resp = responses.get(0);
		assertResponse(resp, "update4");
		assertUpdateResponse(resp, 2, 4);
	}

	private static void assertUpdateResponse(ExtDirectResponse resp, int noOfRecords,
			int version) {

		Iterator<Book> it = null;
		if (version == 3) {
			ExtDirectStoreResult<Book> storeResponse = ControllerUtil.convertValue(
					resp.getResult(), new TypeReference<ExtDirectStoreResult<Book>>() {
						// nothing here
					});
			assertThat(storeResponse.getTotal()).isNull();
			assertThat(storeResponse.isSuccess()).isTrue();
			assertThat(storeResponse.getRecords().size()).isEqualTo(noOfRecords);
			it = storeResponse.getRecords().iterator();
		}
		else {
			Collection<Book> books = ControllerUtil.convertValue(resp.getResult(),
					new TypeReference<Collection<Book>>() {
						// nothing here
					});
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

	private void testCreateRecordsOne(MockMvc mockMvc) throws Exception {
		Map<String, Object> storeRequest = new LinkedHashMap<String, Object>();
		storeRequest.put("records", new Book(-1, "Ext JS 3.0 Cookbook", "1847198708"));
		String edRequest = ControllerUtil.createEdsRequest(this.serviceName, "create3", 1,
				storeRequest);

		MvcResult result = ControllerUtil.performRouterRequest(mockMvc, edRequest);
		List<ExtDirectResponse> responses = ControllerUtil
				.readDirectResponses(result.getResponse().getContentAsByteArray());

		assertThat(responses).hasSize(1);
		ExtDirectResponse resp = responses.get(0);
		assertResponse(resp, "create3");
		assertCreateResponse(resp, 1, 3);
	}

	private void testCreateRecordsMany(MockMvc mockMvc) throws Exception {
		Map<String, Object> storeRequest = new LinkedHashMap<String, Object>();
		List<Book> newBooks = new ArrayList<Book>();
		newBooks.add(new Book(-1, "Ext JS 3.0 Cookbook", "1847198708"));
		newBooks.add(new Book(-1, "Learning Ext JS 3.2", "1849511209"));

		storeRequest.put("records", newBooks);
		String edRequest = ControllerUtil.createEdsRequest(this.serviceName, "create3", 1,
				storeRequest);

		MvcResult result = ControllerUtil.performRouterRequest(mockMvc, edRequest);
		List<ExtDirectResponse> responses = ControllerUtil
				.readDirectResponses(result.getResponse().getContentAsByteArray());

		assertThat(responses).hasSize(1);
		ExtDirectResponse resp = responses.get(0);
		assertResponse(resp, "create3");
		assertCreateResponse(resp, 2, 3);
	}

	private void testCreateOne(MockMvc mockMvc) throws Exception {

		Book newBook = new Book(-1, "Ext JS 3.0 Cookbook", "1847198708");
		String edRequest = ControllerUtil.createEdsRequest(this.serviceName, "create4", 1,
				newBook);

		MvcResult result = ControllerUtil.performRouterRequest(mockMvc, edRequest);
		List<ExtDirectResponse> responses = ControllerUtil
				.readDirectResponses(result.getResponse().getContentAsByteArray());

		assertThat(responses).hasSize(1);
		ExtDirectResponse resp = responses.get(0);
		assertResponse(resp, "create4");
		assertCreateResponse(resp, 1, 4);
	}

	private void testCreateMany(MockMvc mockMvc) throws Exception {
		List<Book> newBooks = new ArrayList<Book>();
		newBooks.add(new Book(-1, "Ext JS 3.0 Cookbook", "1847198708"));
		newBooks.add(new Book(-1, "Learning Ext JS 3.2", "1849511209"));

		String edRequest = ControllerUtil.createEdsRequest(this.serviceName, "create4", 1,
				newBooks);

		MvcResult result = ControllerUtil.performRouterRequest(mockMvc, edRequest);
		List<ExtDirectResponse> responses = ControllerUtil
				.readDirectResponses(result.getResponse().getContentAsByteArray());

		assertThat(responses).hasSize(1);
		ExtDirectResponse resp = responses.get(0);
		assertResponse(resp, "create4");
		assertCreateResponse(resp, 2, 4);
	}

	private void testDeleteRecordsOne(MockMvc mockMvc) throws Exception {
		Map<String, Object> storeRequest = new LinkedHashMap<String, Object>();
		storeRequest.put("records", new Integer(1));
		String edRequest = ControllerUtil.createEdsRequest(this.serviceName, "delete3", 1,
				storeRequest);

		MvcResult result = ControllerUtil.performRouterRequest(mockMvc, edRequest);
		List<ExtDirectResponse> responses = ControllerUtil
				.readDirectResponses(result.getResponse().getContentAsByteArray());

		assertThat(responses).hasSize(1);
		ExtDirectResponse resp = responses.get(0);
		assertResponse(resp, "delete3");

		ExtDirectStoreResult<Integer> storeResponse = ControllerUtil.convertValue(
				resp.getResult(), new TypeReference<ExtDirectStoreResult<Integer>>() {
					// nothing here
				});
		assertThat(storeResponse.getTotal()).isNull();
		assertThat(storeResponse.isSuccess()).isTrue();
		assertThat(storeResponse.getRecords().size()).isEqualTo(1);
		Integer deleteBookId = storeResponse.getRecords().iterator().next();
		assertThat(deleteBookId.intValue()).isEqualTo(1);
	}

	private void testDeleteRecordsMany(MockMvc mockMvc) throws Exception {
		Map<String, Object> storeRequest = new LinkedHashMap<String, Object>();
		List<Integer> booksToDelete = new ArrayList<Integer>();
		booksToDelete.add(1);
		booksToDelete.add(2);

		storeRequest.put("records", booksToDelete);
		String edRequest = ControllerUtil.createEdsRequest(this.serviceName, "delete3", 1,
				storeRequest);

		MvcResult result = ControllerUtil.performRouterRequest(mockMvc, edRequest);
		List<ExtDirectResponse> responses = ControllerUtil
				.readDirectResponses(result.getResponse().getContentAsByteArray());

		assertThat(responses).hasSize(1);
		ExtDirectResponse resp = responses.get(0);
		assertResponse(resp, "delete3");

		ExtDirectStoreResult<Integer> storeResponse = ControllerUtil.convertValue(
				resp.getResult(), new TypeReference<ExtDirectStoreResult<Integer>>() {
					// nothing here
				});
		assertThat(storeResponse.getTotal()).isNull();
		assertThat(storeResponse.isSuccess()).isTrue();
		assertThat(storeResponse.getRecords().size()).isEqualTo(2);
		Iterator<Integer> it = storeResponse.getRecords().iterator();
		Integer deleteBookId = it.next();
		assertThat(deleteBookId.intValue()).isEqualTo(1);
		deleteBookId = it.next();
		assertThat(deleteBookId.intValue()).isEqualTo(2);
	}

	private void testDeleteOne(MockMvc mockMvc) throws Exception {

		Book deleteBook = new Book(11, "Ext JS 3.0 Cookbook", "1847198708");
		String edRequest = ControllerUtil.createEdsRequest(this.serviceName, "delete4", 1,
				deleteBook);

		MvcResult result = ControllerUtil.performRouterRequest(mockMvc, edRequest);
		List<ExtDirectResponse> responses = ControllerUtil
				.readDirectResponses(result.getResponse().getContentAsByteArray());

		assertThat(responses).hasSize(1);
		ExtDirectResponse resp = responses.get(0);
		assertResponse(resp, "delete4");

		Collection<Book> storeResponse = ControllerUtil.convertValue(resp.getResult(),
				new TypeReference<Collection<Book>>() {
					// nothing here
				});
		assertThat(storeResponse).hasSize(1);
		Book book = storeResponse.iterator().next();
		assertThat(book.getId()).isEqualTo(Integer.valueOf(11));
		assertThat(book.getTitle()).isNull();
		assertThat(book.getIsbn()).isEqualTo("DELETED_1847198708");
	}

	private void testDeleteMany(MockMvc mockMvc) throws Exception {

		List<Book> deletedBooks = new ArrayList<Book>();
		deletedBooks.add(new Book(9, "Ext JS 3.0 Cookbook", "1847198708"));
		deletedBooks.add(new Book(10, "Learning Ext JS 3.2", "1849511209"));

		String edRequest = ControllerUtil.createEdsRequest(this.serviceName, "delete4", 1,
				deletedBooks);

		MvcResult result = ControllerUtil.performRouterRequest(mockMvc, edRequest);
		List<ExtDirectResponse> responses = ControllerUtil
				.readDirectResponses(result.getResponse().getContentAsByteArray());

		assertThat(responses).hasSize(1);
		ExtDirectResponse resp = responses.get(0);
		assertResponse(resp, "delete4");

		Collection<Book> storeResponse = ControllerUtil.convertValue(resp.getResult(),
				new TypeReference<Collection<Book>>() {
					// nothing here
				});
		assertThat(storeResponse).hasSize(2);
		Iterator<Book> it = storeResponse.iterator();

		Book book = it.next();
		assertThat(book.getId()).isEqualTo(Integer.valueOf(9));
		assertThat(book.getTitle()).isNull();
		assertThat(book.getIsbn()).isEqualTo("DELETED_1847198708");

		book = it.next();
		assertThat(book.getId()).isEqualTo(10);
		assertThat(book.getTitle()).isNull();
		assertThat(book.getIsbn()).isEqualTo("DELETED_1849511209");
	}

	private static void assertCreateResponse(ExtDirectResponse resp, int noOfRecords,
			int version) {

		Iterator<Book> it = null;
		if (version == 3) {
			ExtDirectStoreResult<Book> storeResponse = ControllerUtil.convertValue(
					resp.getResult(), new TypeReference<ExtDirectStoreResult<Book>>() {
						// nothing here
					});
			assertThat(storeResponse.getTotal()).isNull();
			assertThat(storeResponse.isSuccess()).isTrue();
			assertThat(storeResponse.getRecords().size()).isEqualTo(noOfRecords);
			it = ControllerUtil.convertValue(storeResponse.getRecords(),
					new TypeReference<Collection<Book>>() {
						// nothing here
					}).iterator();
		}
		else {
			Collection<Book> books = ControllerUtil.convertValue(resp.getResult(),
					new TypeReference<Collection<Book>>() {
						// nothing here
					});
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
		assertThat(resp.getAction()).isEqualTo(this.serviceName);
		assertThat(resp.getMethod()).isEqualTo(method);
		assertThat(resp.getType()).isEqualTo("rpc");
		assertThat(resp.getTid()).isEqualTo(1);
		assertThat(resp.getMessage()).isNull();
		assertThat(resp.getWhere()).isNull();
		assertThat(resp.getResult()).isNotNull();
	}

}
