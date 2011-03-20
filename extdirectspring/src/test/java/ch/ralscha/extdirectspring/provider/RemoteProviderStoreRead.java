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

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import javax.inject.Named;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.junit.Assert;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestParam;
import ch.ralscha.extdirectspring.annotation.ExtDirectMethod;
import ch.ralscha.extdirectspring.annotation.ExtDirectMethodType;
import ch.ralscha.extdirectspring.bean.DataType;
import ch.ralscha.extdirectspring.bean.ExtDirectStoreReadRequest;
import ch.ralscha.extdirectspring.bean.ExtDirectStoreResponse;
import ch.ralscha.extdirectspring.bean.Field;
import ch.ralscha.extdirectspring.bean.MetaData;
import ch.ralscha.extdirectspring.bean.SortDirection;
import ch.ralscha.extdirectspring.filter.BooleanFilter;
import ch.ralscha.extdirectspring.filter.Comparison;
import ch.ralscha.extdirectspring.filter.DateFilter;
import ch.ralscha.extdirectspring.filter.ListFilter;
import ch.ralscha.extdirectspring.filter.NumericFilter;
import ch.ralscha.extdirectspring.filter.StringFilter;

@Named
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
		Assert.assertNotNull(response);
		Assert.assertNotNull(request);
		Assert.assertNotNull(session);
		Assert.assertEquals(Locale.ENGLISH, locale);

		return createRows();
	}

	@ExtDirectMethod(ExtDirectMethodType.STORE_READ)
	public ExtDirectStoreResponse<Row> method4(ExtDirectStoreReadRequest request) {
		return createExtDirectStoreResponse(request);
	}

	@ExtDirectMethod(value = ExtDirectMethodType.STORE_READ, group = "group3")
	public ExtDirectStoreResponse<Row> method5(ExtDirectStoreReadRequest request, Locale locale,
			@RequestParam(value = "id") int id) {
		Assert.assertEquals(10, id);
		Assert.assertEquals(Locale.ENGLISH, locale);
		return createExtDirectStoreResponse(request);
	}

	@ExtDirectMethod(value = ExtDirectMethodType.STORE_READ, group = "group2")
	public ExtDirectStoreResponse<Row> method6(@RequestParam(value = "id", defaultValue = "1") int id,
			HttpServletRequest servletRequest, ExtDirectStoreReadRequest request) {
		Assert.assertEquals(1, id);
		Assert.assertNotNull(servletRequest);
		return createExtDirectStoreResponse(request);
	}

	@ExtDirectMethod(value = ExtDirectMethodType.STORE_READ, group = "group2")
	public List<Row> method7(@RequestParam(value = "id", required = false) Integer id) {
		if (id == null) {
			Assert.assertNull(id);
		} else {
			Assert.assertEquals(Integer.valueOf(11), id);
		}
		return createRows();
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

			if (StringUtils.hasText(request.getSort())) {
				Assert.assertEquals("id", request.getSort());

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

			if (StringUtils.hasText(request.getGroupBy())) {
				Assert.assertEquals("id", request.getGroupBy());

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

		switch (type) {
		case 1:
			assertEquals(1, request.getFilters().size());
			assertTrue(request.getFilters().get(0) instanceof NumericFilter);

			NumericFilter nf = (NumericFilter) request.getFilters().get(0);
			assertEquals(2, nf.getValue());
			assertEquals("id", nf.getField());
			assertEquals(Comparison.EQUAL, nf.getComparison());

			return createResult(1);
		case 2:
			assertEquals(2, request.getFilters().size());
			assertTrue(request.getFilters().get(0) instanceof NumericFilter);
			assertTrue(request.getFilters().get(1) instanceof NumericFilter);

			nf = (NumericFilter) request.getFilters().get(0);
			assertEquals(100, nf.getValue());
			assertEquals("id", nf.getField());
			assertEquals(Comparison.LESS_THAN, nf.getComparison());

			nf = (NumericFilter) request.getFilters().get(1);
			assertEquals(90, nf.getValue());
			assertEquals("id", nf.getField());
			assertEquals(Comparison.GREATER_THAN, nf.getComparison());
			return createResult(2);
		case 3:
			assertEquals(1, request.getFilters().size());
			assertTrue(request.getFilters().get(0) instanceof BooleanFilter);

			BooleanFilter bf = (BooleanFilter) request.getFilters().get(0);
			assertEquals(true, bf.getValue());
			assertEquals("visible", bf.getField());

			return createResult(3);
		case 4:
			assertEquals(1, request.getFilters().size());
			assertTrue(request.getFilters().get(0) instanceof BooleanFilter);

			bf = (BooleanFilter) request.getFilters().get(0);
			assertEquals(false, bf.getValue());
			assertEquals("visible", bf.getField());

			return createResult(4);
		case 5:
			assertEquals(1, request.getFilters().size());
			assertTrue(request.getFilters().get(0) instanceof StringFilter);

			StringFilter sf = (StringFilter) request.getFilters().get(0);
			assertEquals("abb", sf.getValue());
			assertEquals("company", sf.getField());

			return createResult(5);

		case 6:
			assertEquals(1, request.getFilters().size());
			assertTrue(request.getFilters().get(0) instanceof ListFilter);

			ListFilter lf = (ListFilter) request.getFilters().get(0);
			assertEquals(1, lf.getValue().size());
			assertEquals("small", lf.getValue().get(0));
			assertEquals("size", lf.getField());

			return createResult(6);

		case 7:
			assertEquals(1, request.getFilters().size());
			assertTrue(request.getFilters().get(0) instanceof ListFilter);

			lf = (ListFilter) request.getFilters().get(0);
			assertEquals(2, lf.getValue().size());
			assertEquals("small", lf.getValue().get(0));
			assertEquals("medium", lf.getValue().get(1));
			assertEquals("size", lf.getField());

			return createResult(7);

		case 8:

			assertEquals(2, request.getFilters().size());
			assertTrue(request.getFilters().get(0) instanceof DateFilter);
			assertTrue(request.getFilters().get(1) instanceof DateFilter);

			DateFilter df = (DateFilter) request.getFilters().get(0);
			assertEquals("07/31/2010", df.getValue());
			assertEquals("date", df.getField());
			assertEquals(Comparison.LESS_THAN, df.getComparison());

			df = (DateFilter) request.getFilters().get(1);
			assertEquals("07/01/2010", df.getValue());
			assertEquals("date", df.getField());
			assertEquals(Comparison.GREATER_THAN, df.getComparison());

			return createResult(8);

		case 9:
			assertEquals(1, request.getFilters().size());
			assertTrue(request.getFilters().get(0) instanceof DateFilter);

			df = (DateFilter) request.getFilters().get(0);
			assertEquals("07/01/2010", df.getValue());
			assertEquals("date", df.getField());
			assertEquals(Comparison.EQUAL, df.getComparison());

			return createResult(9);

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
