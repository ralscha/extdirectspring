/**
 * Copyright 2010 Ralph Schaer <ralphschaer@gmail.com>
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

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.junit.Test;
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
		Map<String, Object> storeRequest = new LinkedHashMap<String, Object>();
		List<Book> newBooks = new ArrayList<Book>();
		newBooks.add(new Book(-1, "Ext JS 3.0 Cookbook", "1847198708"));

		storeRequest.put("records", newBooks);
		String json = ControllerUtil.createRequestJson(serviceName, "create", 1, storeRequest);
		List<ExtDirectResponse> responses = controller.router(request, response, Locale.ENGLISH, json);

		assertEquals(1, responses.size());
		ExtDirectResponse resp = responses.get(0);
		assertEquals(serviceName, resp.getAction());
		assertEquals("create", resp.getMethod());
		assertEquals("rpc", resp.getType());
		assertEquals(1, resp.getTid());
		assertNull(resp.getMessage());
		assertNull(resp.getWhere());
		assertNotNull(resp.getResult());

		ExtDirectStoreResponse<Book> storeResponse = (ExtDirectStoreResponse<Book>) resp.getResult();
		assertNull(storeResponse.getTotal());
		assertTrue(storeResponse.isSuccess());
		assertEquals(1, storeResponse.getRecords().size());
		Book aBook = storeResponse.getRecords().iterator().next();
		assertEquals(3, aBook.getId().intValue());
		assertEquals("Ext JS 3.0 Cookbook", aBook.getTitle());
		assertEquals("1847198708", aBook.getIsbn());
	}
	
	public void testRead(RouterController controller, MockHttpServletResponse response, MockHttpServletRequest request) {
		String json = ControllerUtil.createRequestJson(serviceName, "read", 1);
		List<ExtDirectResponse> responses = controller.router(request, response, Locale.ENGLISH, json);

		assertEquals(1, responses.size());
		ExtDirectResponse resp = responses.get(0);
		assertEquals(serviceName, resp.getAction());
		assertEquals("read", resp.getMethod());
		assertEquals("rpc", resp.getType());
		assertEquals(1, resp.getTid());
		assertNull(resp.getMessage());
		assertNull(resp.getWhere());
		assertNotNull(resp.getResult());
		
		ExtDirectStoreResponse<Book> storeResponse = (ExtDirectStoreResponse<Book>) resp.getResult();
		assertNull(storeResponse.getTotal());
		assertTrue(storeResponse.isSuccess());
		assertEquals(2, storeResponse.getRecords().size());
		
	}
	
	public void testUpdate(RouterController controller, MockHttpServletResponse response, MockHttpServletRequest request) {
		Map<String, Object> storeRequest = new LinkedHashMap<String, Object>();
		List<Book> newBooks = new ArrayList<Book>();
		newBooks.add(new Book(1, "an update", "9999999"));

		storeRequest.put("records", newBooks);
		String json = ControllerUtil.createRequestJson(serviceName, "update", 1, storeRequest);
		List<ExtDirectResponse> responses = controller.router(request, response, Locale.ENGLISH, json);

		assertEquals(1, responses.size());
		ExtDirectResponse resp = responses.get(0);
		assertEquals(serviceName, resp.getAction());
		assertEquals("update", resp.getMethod());
		assertEquals("rpc", resp.getType());
		assertEquals(1, resp.getTid());
		assertNull(resp.getMessage());
		assertNull(resp.getWhere());
		assertNotNull(resp.getResult());

		ExtDirectStoreResponse<Book> storeResponse = (ExtDirectStoreResponse<Book>) resp.getResult();
		assertNull(storeResponse.getTotal());
		assertTrue(storeResponse.isSuccess());
		assertEquals(1, storeResponse.getRecords().size());
		Book aBook = storeResponse.getRecords().iterator().next();
		assertEquals(1, aBook.getId().intValue());
		assertEquals("an update", aBook.getTitle());
		assertEquals("9999999", aBook.getIsbn());
	}
	
	public void testDelete(RouterController controller, MockHttpServletResponse response, MockHttpServletRequest request) {
		Map<String, Object> storeRequest = new LinkedHashMap<String, Object>();
		List<Integer> booksToDelete = new ArrayList<Integer>();
		booksToDelete.add(1);

		storeRequest.put("records", booksToDelete);
		String json = ControllerUtil.createRequestJson(serviceName, "delete", 1, storeRequest);
		List<ExtDirectResponse> responses = controller.router(request, response, Locale.ENGLISH, json);
		
		assertEquals(1, responses.size());
		ExtDirectResponse resp = responses.get(0);
		assertEquals(serviceName, resp.getAction());
		assertEquals("delete", resp.getMethod());
		assertEquals("rpc", resp.getType());
		assertEquals(1, resp.getTid());
		assertNull(resp.getMessage());
		assertNull(resp.getWhere());
		assertNotNull(resp.getResult());

		ExtDirectStoreResponse<Integer> storeResponse = (ExtDirectStoreResponse<Integer>) resp.getResult();
		assertNull(storeResponse.getTotal());
		assertTrue(storeResponse.isSuccess());
		assertEquals(1, storeResponse.getRecords().size());
		Integer deleteBookId = storeResponse.getRecords().iterator().next();
		assertEquals(1, deleteBookId.intValue());		
	}
}
