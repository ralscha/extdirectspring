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
package ch.ralscha.extdirectspring.provider;

import static org.fest.assertions.Assertions.assertThat;
import static org.fest.assertions.MapAssert.entry;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestParam;

import ch.ralscha.extdirectspring.annotation.ExtDirectMethod;
import ch.ralscha.extdirectspring.annotation.ExtDirectMethodType;
import ch.ralscha.extdirectspring.bean.DataType;
import ch.ralscha.extdirectspring.bean.ExtDirectStoreReadRequest;
import ch.ralscha.extdirectspring.bean.ExtDirectStoreResponse;
import ch.ralscha.extdirectspring.bean.Field;
import ch.ralscha.extdirectspring.bean.GroupInfo;
import ch.ralscha.extdirectspring.bean.MetaData;
import ch.ralscha.extdirectspring.bean.SortDirection;
import ch.ralscha.extdirectspring.bean.SortInfo;
import ch.ralscha.extdirectspring.filter.BooleanFilter;
import ch.ralscha.extdirectspring.filter.Comparison;
import ch.ralscha.extdirectspring.filter.DateFilter;
import ch.ralscha.extdirectspring.filter.Filter;
import ch.ralscha.extdirectspring.filter.ListFilter;
import ch.ralscha.extdirectspring.filter.NumericFilter;
import ch.ralscha.extdirectspring.filter.StringFilter;

@Service
public class RemoteProviderStoreRead {

	@ExtDirectMethod(ExtDirectMethodType.STORE_READ)
	public List<Row> method1() {
		return createRows();
	}

	@ExtDirectMethod(ExtDirectMethodType.STORE_READ)
	public List<Row> method2() {
		return null;
	}

	@ExtDirectMethod(ExtDirectMethodType.STORE_READ)
	public List<Row> method3(HttpServletResponse response, HttpServletRequest request, HttpSession session,
			Locale locale) {
		assertNotNull(response);
		assertNotNull(request);
		assertNotNull(session);
		assertEquals(Locale.ENGLISH, locale);

		return createRows();
	}

	@ExtDirectMethod(ExtDirectMethodType.STORE_READ)
	public ExtDirectStoreResponse<Row> method4(ExtDirectStoreReadRequest request) {
		return createExtDirectStoreResponse(request);
	}

	@ExtDirectMethod(value = ExtDirectMethodType.STORE_READ, group = "group3")
	public ExtDirectStoreResponse<Row> method5(ExtDirectStoreReadRequest request, Locale locale,
			@RequestParam(value = "id") int id) {
		assertEquals(10, id);
		assertEquals(Locale.ENGLISH, locale);
		
		assertEquals(1, request.getParams().size());
		assertThat(request.getParams()).includes(entry("id", 10));
		
		return createExtDirectStoreResponse(request);
	}

	@ExtDirectMethod(value = ExtDirectMethodType.STORE_READ, group = "group2")
	public ExtDirectStoreResponse<Row> method6(@RequestParam(value = "id", defaultValue = "1") int id,
			HttpServletRequest servletRequest, ExtDirectStoreReadRequest request) {
		assertEquals(1, id);
		assertNotNull(servletRequest);
		return createExtDirectStoreResponse(request);
	}

	@ExtDirectMethod(value = ExtDirectMethodType.STORE_READ, group = "group2")
	public List<Row> method7(@RequestParam(value = "id", required = false) Integer id) {
		if (id == null) {
			assertNull(id);
		} else {
			assertEquals(Integer.valueOf(11), id);
		}
		return createRows();
	}
	
	@ExtDirectMethod(value = ExtDirectMethodType.STORE_READ)
	public ExtDirectStoreResponse<Row> method8(@DateTimeFormat(iso = ISO.DATE_TIME) Date endDate,
			HttpServletRequest servletRequest, ExtDirectStoreReadRequest request) {
		assertNotNull(endDate);
		assertNotNull(servletRequest);
		return createExtDirectStoreResponse(request);
	}	

	private ExtDirectStoreResponse<Row> createExtDirectStoreResponse(ExtDirectStoreReadRequest request) {
		List<Row> rows = createRows();

		int totalSize = rows.size();

		if (request != null) {

			if ("name".equals(request.getQuery())) {
				for (Iterator<Row> iterator = rows.listIterator(); iterator.hasNext();) {
					Row row = iterator.next();
					if (!row.getName().startsWith("name")) {
						iterator.remove();
					}
				}
			} else if ("firstname".equals(request.getQuery())) {
				for (Iterator<Row> iterator = rows.listIterator(); iterator.hasNext();) {
					Row row = iterator.next();
					if (!row.getName().startsWith("firstname")) {
						iterator.remove();
					}
				}
			}

			totalSize = rows.size();

			Collection<SortInfo> sorters = request.getSorters();
			
			if (!sorters.isEmpty()) {
				SortInfo sortInfo = sorters.iterator().next();
				assertEquals("id", sortInfo.getProperty());
				
				if (sortInfo.getDirection() == SortDirection.ASCENDING) {
					Collections.sort(rows);
				} else {
					Collections.sort(rows, new Comparator<Row>() {

						//@Override
						public int compare(Row o1, Row o2) {
							return o2.getId() - o1.getId();
						}
					});
				}
			} else if (StringUtils.hasText(request.getSort())) {
				assertEquals("id", request.getSort());

				if (request.isAscendingSort()) {
					Collections.sort(rows);
				} else if (request.isDescendingSort()) {
					Collections.sort(rows, new Comparator<Row>() {

						//@Override
						public int compare(Row o1, Row o2) {
							return o2.getId() - o1.getId();
						}
					});
				}
			}
			

			Collection<GroupInfo> groups = request.getGroups();
			if (!groups.isEmpty()) {
				GroupInfo groupInfo = groups.iterator().next();

				assertEquals("id", groupInfo.getProperty());
				if (groupInfo.getDirection() == SortDirection.ASCENDING) {
					Collections.sort(rows);
				} else {
					Collections.sort(rows, new Comparator<Row>() {

						//@Override
						public int compare(Row o1, Row o2) {
							return o2.getId() - o1.getId();
						}
					});
				}
				 
			} else if (StringUtils.hasText(request.getGroupBy())) {
				assertEquals("id", request.getGroupBy());

				if (request.isAscendingGroupSort()) {
					Collections.sort(rows);
				} else if (request.isDescendingGroupSort()) {
					Collections.sort(rows, new Comparator<Row>() {

						//@Override
						public int compare(Row o1, Row o2) {
							return o2.getId() - o1.getId();
						}
					});
				}
			}
			
			if (request.getStart() != null && request.getLimit() != null) {
				rows = rows.subList(request.getStart(), Math.min(totalSize, request.getStart() + request.getLimit()));
			} else {
				rows = rows.subList(0, 50);
			}

		}

		return new ExtDirectStoreResponse<Row>(totalSize, rows);

	}

	private List<Row> createRows() {
		List<Row> rows = new ArrayList<Row>();
		for (int i = 0; i < 100; i += 2) {
			rows.add(new Row(i, "name: " + i, true, "" + (1000 + i)));
			rows.add(new Row(i + 1, "firstname: " + (i + 1), false, "" + (10 + i + 1)));
		}
		return rows;
	}

	@ExtDirectMethod(value = ExtDirectMethodType.STORE_READ)
	public ExtDirectStoreResponse<Row> methodMetadata(ExtDirectStoreReadRequest request) {
		ExtDirectStoreResponse<Row> response = createExtDirectStoreResponse(request);

		if (request.getStart() == null && request.getSort() == null) {
			MetaData metaData = new MetaData();

			metaData.setPagingParameter(0, 50);
			metaData.setSortInfo("name", SortDirection.ASCENDING);

			Field field = new Field("id");
			field.setType(DataType.INTEGER);
			field.addCustomProperty("header", "ID");
			field.addCustomProperty("width", 20);
			field.addCustomProperty("sortable", true);
			field.addCustomProperty("resizable", true);
			field.addCustomProperty("hideable", false);
			metaData.addField(field);

			field = new Field("name");
			field.setType(DataType.STRING);
			field.addCustomProperty("header", "Name");
			field.addCustomProperty("width", 70);
			field.addCustomProperty("sortable", true);
			field.addCustomProperty("resizable", true);
			field.addCustomProperty("hideable", false);
			metaData.addField(field);

			field = new Field("admin");
			field.setType(DataType.BOOLEAN);
			field.addCustomProperty("header", "Administrator");
			field.addCustomProperty("width", 30);
			field.addCustomProperty("sortable", true);
			field.addCustomProperty("resizable", true);
			field.addCustomProperty("hideable", true);
			metaData.addField(field);

			field = new Field("salary");
			field.setType(DataType.FLOAT);
			field.addCustomProperty("header", "Salary");
			field.addCustomProperty("width", 50);
			field.addCustomProperty("sortable", false);
			field.addCustomProperty("resizable", true);
			field.addCustomProperty("hideable", true);
			metaData.addField(field);

			response.setMetaData(metaData);
		}

		return response;
	}

	@ExtDirectMethod(ExtDirectMethodType.STORE_READ)
	public List<Row> methodFilter(@RequestParam("type") int type, ExtDirectStoreReadRequest request) {

		List<Filter> filters = new ArrayList<Filter>(request.getFilters());
		switch (type) {
		case 1:
			assertEquals(1, request.getFilters().size());
			assertTrue(filters.get(0) instanceof NumericFilter);

			NumericFilter nf = (NumericFilter) filters.get(0);
			assertEquals(2, nf.getValue());
			assertEquals("id", nf.getField());
			assertEquals(Comparison.EQUAL, nf.getComparison());

			return createResult(1);
		case 2:
			assertEquals(2, request.getFilters().size());
			assertTrue(filters.get(0) instanceof NumericFilter);
			assertTrue(filters.get(1) instanceof NumericFilter);

			nf = (NumericFilter) filters.get(0);
			assertEquals(100, nf.getValue());
			assertEquals("id", nf.getField());
			assertEquals(Comparison.LESS_THAN, nf.getComparison());

			nf = (NumericFilter) filters.get(1);
			assertEquals(90, nf.getValue());
			assertEquals("id", nf.getField());
			assertEquals(Comparison.GREATER_THAN, nf.getComparison());
			return createResult(2);
		case 3:
			assertEquals(1, filters.size());
			assertTrue(filters.get(0) instanceof BooleanFilter);

			BooleanFilter bf = (BooleanFilter) filters.get(0);
			assertEquals(true, bf.getValue());
			assertEquals("visible", bf.getField());

			return createResult(3);
		case 4:
			assertEquals(1, filters.size());
			assertTrue(filters.get(0) instanceof BooleanFilter);

			bf = (BooleanFilter) filters.get(0);
			assertEquals(false, bf.getValue());
			assertEquals("visible", bf.getField());

			return createResult(4);
		case 5:
			assertEquals(1, filters.size());
			assertTrue(filters.get(0) instanceof StringFilter);

			StringFilter sf = (StringFilter) filters.get(0);
			assertEquals("abb", sf.getValue());
			assertEquals("company", sf.getField());

			return createResult(5);

		case 6:
			assertEquals(1, filters.size());
			assertTrue(filters.get(0) instanceof ListFilter);

			ListFilter lf = (ListFilter) filters.get(0);
			assertEquals(1, lf.getValue().size());
			assertEquals("small", lf.getValue().get(0));
			assertEquals("size", lf.getField());

			return createResult(6);

		case 7:
			assertEquals(1, filters.size());
			assertTrue(filters.get(0) instanceof ListFilter);

			lf = (ListFilter) filters.get(0);
			assertEquals(2, lf.getValue().size());
			assertEquals("small", lf.getValue().get(0));
			assertEquals("medium", lf.getValue().get(1));
			assertEquals("size", lf.getField());

			return createResult(7);

		case 8:

			assertEquals(2, filters.size());
			assertTrue(filters.get(0) instanceof DateFilter);
			assertTrue(filters.get(1) instanceof DateFilter);

			DateFilter df = (DateFilter) filters.get(0);
			assertEquals("07/31/2010", df.getValue());
			assertEquals("date", df.getField());
			assertEquals(Comparison.LESS_THAN, df.getComparison());

			df = (DateFilter) filters.get(1);
			assertEquals("07/01/2010", df.getValue());
			assertEquals("date", df.getField());
			assertEquals(Comparison.GREATER_THAN, df.getComparison());

			return createResult(8);

		case 9:
			assertEquals(1, filters.size());
			assertTrue(filters.get(0) instanceof DateFilter);

			df = (DateFilter) filters.get(0);
			assertEquals("07/01/2010", df.getValue());
			assertEquals("date", df.getField());
			assertEquals(Comparison.EQUAL, df.getComparison());

			return createResult(9);

		case 10:
			assertEquals(1, filters.size());
			assertTrue(filters.get(0) instanceof StringFilter);

			sf = (StringFilter) filters.get(0);
			assertEquals("ERROR", sf.getValue());
			assertEquals("level", sf.getField());

			return createResult(10);			
		case 11:
			assertEquals(1, request.getFilters().size());
			assertTrue(filters.get(0) instanceof NumericFilter);

			nf = (NumericFilter) filters.get(0);
			assertEquals(1, nf.getValue());
			assertEquals("level", nf.getField());
			assertNull(nf.getComparison());

			return createResult(11);			
		case 12:
			assertEquals(1, filters.size());
			assertTrue(filters.get(0) instanceof BooleanFilter);

			bf = (BooleanFilter) filters.get(0);
			assertEquals(true, bf.getValue());
			assertEquals("level", bf.getField());

			return createResult(12);			
		case 13:
			assertEquals(1, filters.size());
			assertTrue(filters.get(0) instanceof ListFilter);

			lf = (ListFilter) filters.get(0);
			assertEquals(1, lf.getValue().size());
			assertEquals("small", lf.getValue().get(0));
			assertEquals("size", lf.getField());

			return createResult(13);

		case 14:
			assertEquals(1, filters.size());
			assertTrue(filters.get(0) instanceof ListFilter);

			lf = (ListFilter) filters.get(0);
			assertEquals(2, lf.getValue().size());
			assertEquals("small", lf.getValue().get(0));
			assertEquals("medium", lf.getValue().get(1));
			assertEquals("size", lf.getField());

			return createResult(14);			
		}
		
		

		return Collections.emptyList();
	}

	private List<Row> createResult(int i) {
		Row r = new Row(i, null, false, null);
		List<Row> result = new ArrayList<Row>();
		result.add(r);
		return result;
	}

}
