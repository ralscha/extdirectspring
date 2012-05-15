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
package ch.ralscha.extdirectspring.provider;

import static org.fest.assertions.Assertions.assertThat;
import static org.fest.assertions.MapAssert.entry;

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
		return createRows("");
	}

	@ExtDirectMethod(ExtDirectMethodType.STORE_READ)
	public List<Row> method2() {
		return null;
	}

	@ExtDirectMethod(ExtDirectMethodType.STORE_READ)
	public List<Row> method3(HttpServletResponse response, HttpServletRequest request, HttpSession session, Locale locale) {
		return createRows(":" + (response != null) + ";" + (request != null) + ";" + (session != null) + ";" + locale);
	}

	@ExtDirectMethod(ExtDirectMethodType.STORE_READ)
	public ExtDirectStoreResponse<Row> method4(ExtDirectStoreReadRequest request) {
		return createExtDirectStoreResponse(request, "");
	}

	@ExtDirectMethod(value = ExtDirectMethodType.STORE_READ, group = "group3")
	public ExtDirectStoreResponse<Row> method5(ExtDirectStoreReadRequest request, Locale locale, @RequestParam(value = "id") int id) {
		assertThat(id).isEqualTo(10);
		assertThat(locale).isEqualTo(Locale.ENGLISH);

		assertThat(request.getParams().size()).isEqualTo(1);
		assertThat(request.getParams()).includes(entry("id", 10));

		return createExtDirectStoreResponse(request, ":" + id + ";" + locale);
	}

	@ExtDirectMethod(value = ExtDirectMethodType.STORE_READ, group = "group2")
	public ExtDirectStoreResponse<Row> method6(@RequestParam(value = "id", defaultValue = "1") int id, HttpServletRequest servletRequest,
			ExtDirectStoreReadRequest request) {
		assertThat(id).isEqualTo(1);
		assertThat(servletRequest).isNotNull();
		return createExtDirectStoreResponse(request, ":" + id + ";" + (servletRequest != null));
	}

	@ExtDirectMethod(value = ExtDirectMethodType.STORE_READ, group = "group2")
	public List<Row> method7(@RequestParam(value = "id", required = false) Integer id) {
		if (id == null) {
			assertThat(id).isNull();
		}
		else {
			assertThat(id).isEqualTo(Integer.valueOf(11));
		}
		return createRows(":" + id);
	}

	@ExtDirectMethod(value = ExtDirectMethodType.STORE_READ)
	public ExtDirectStoreResponse<Row> method8(@DateTimeFormat(iso = ISO.DATE_TIME) Date endDate, HttpServletRequest servletRequest,
			ExtDirectStoreReadRequest request) {
		assertThat(endDate).isNotNull();
		assertThat(servletRequest).isNotNull();
		return createExtDirectStoreResponse(request, ":" + endDate.toString() + ";" + (servletRequest != null));
	}

	private ExtDirectStoreResponse<Row> createExtDirectStoreResponse(ExtDirectStoreReadRequest request, String appendix) {
		List<Row> rows = createRows(appendix);

		int totalSize = rows.size();

		if (request != null) {

			if ("name".equals(request.getQuery())) {
				for (Iterator<Row> iterator = rows.listIterator(); iterator.hasNext();) {
					Row row = iterator.next();
					if (!row.getName().startsWith("name")) {
						iterator.remove();
					}
				}
			}
			else if ("firstname".equals(request.getQuery())) {
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
				assertThat(sortInfo.getProperty()).isEqualTo("id");

				if (sortInfo.getDirection() == SortDirection.ASCENDING) {
					Collections.sort(rows);
				}
				else {
					Collections.sort(rows, new Comparator<Row>() {

						// @Override
						public int compare(Row o1, Row o2) {
							return o2.getId() - o1.getId();
						}
					});
				}
			}
			else if (StringUtils.hasText(request.getSort())) {
				assertThat(request.getSort()).isEqualTo("id");

				if (request.isAscendingSort()) {
					Collections.sort(rows);
				}
				else if (request.isDescendingSort()) {
					Collections.sort(rows, new Comparator<Row>() {

						// @Override
						public int compare(Row o1, Row o2) {
							return o2.getId() - o1.getId();
						}
					});
				}
			}

			Collection<GroupInfo> groups = request.getGroups();
			if (!groups.isEmpty()) {
				GroupInfo groupInfo = groups.iterator().next();

				assertThat(groupInfo.getProperty()).isEqualTo("id");
				if (groupInfo.getDirection() == SortDirection.ASCENDING) {
					Collections.sort(rows);
				}
				else {
					Collections.sort(rows, new Comparator<Row>() {

						// @Override
						public int compare(Row o1, Row o2) {
							return o2.getId() - o1.getId();
						}
					});
				}

			}
			else if (StringUtils.hasText(request.getGroupBy())) {
				assertThat(request.getGroupBy()).isEqualTo("id");

				if (request.isAscendingGroupSort()) {
					Collections.sort(rows);
				}
				else if (request.isDescendingGroupSort()) {
					Collections.sort(rows, new Comparator<Row>() {

						// @Override
						public int compare(Row o1, Row o2) {
							return o2.getId() - o1.getId();
						}
					});
				}
			}

			if (request.getStart() != null && request.getLimit() != null) {
				rows = rows.subList(request.getStart(), Math.min(totalSize, request.getStart() + request.getLimit()));
			}
			else {
				rows = rows.subList(0, 50);
			}

		}

		return new ExtDirectStoreResponse<Row>(totalSize, rows);

	}

	private List<Row> createRows(String appendix) {
		List<Row> rows = new ArrayList<Row>();
		for (int i = 0; i < 100; i += 2) {
			rows.add(new Row(i, "name: " + i + appendix, true, "" + (1000 + i)));
			rows.add(new Row(i + 1, "firstname: " + (i + 1) + appendix, false, "" + (10 + i + 1)));
		}
		return rows;
	}

	@ExtDirectMethod(value = ExtDirectMethodType.STORE_READ)
	public ExtDirectStoreResponse<Row> methodMetadata(ExtDirectStoreReadRequest request) {
		ExtDirectStoreResponse<Row> response = createExtDirectStoreResponse(request, "");

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
			assertThat(request.getFilters().size()).isEqualTo(1);
			assertThat(filters.get(0) instanceof NumericFilter).isTrue();

			NumericFilter nf = (NumericFilter) filters.get(0);
			assertThat(nf.getValue()).isEqualTo(2);
			assertThat(nf.getField()).isEqualTo("id");
			assertThat(nf.getComparison()).isEqualTo(Comparison.EQUAL);

			return createResult(1);
		case 2:
			assertThat(request.getFilters().size()).isEqualTo(2);
			assertThat(filters.get(0)).isInstanceOf(NumericFilter.class);
			assertThat(filters.get(1)).isInstanceOf(NumericFilter.class);

			nf = (NumericFilter) filters.get(0);
			assertThat(nf.getValue()).isEqualTo(100);
			assertThat(nf.getField()).isEqualTo("id");
			assertThat(nf.getComparison()).isEqualTo(Comparison.LESS_THAN);

			nf = (NumericFilter) filters.get(1);
			assertThat(nf.getValue()).isEqualTo(90);
			assertThat(nf.getField()).isEqualTo("id");
			assertThat(nf.getComparison()).isEqualTo(Comparison.GREATER_THAN);
			return createResult(2);
		case 3:
			assertThat(filters).hasSize(1);
			assertThat(filters.get(0)).isInstanceOf(BooleanFilter.class);

			BooleanFilter bf = (BooleanFilter) filters.get(0);
			assertThat(bf.getValue()).isEqualTo(true);
			assertThat(bf.getField()).isEqualTo("visible");

			return createResult(3);
		case 4:
			assertThat(filters).hasSize(1);
			assertThat(filters.get(0)).isInstanceOf(BooleanFilter.class);

			bf = (BooleanFilter) filters.get(0);
			assertThat(bf.getValue()).isEqualTo(false);
			assertThat(bf.getField()).isEqualTo("visible");

			return createResult(4);
		case 5:
			assertThat(filters).hasSize(1);
			assertThat(filters.get(0)).isInstanceOf(StringFilter.class);

			StringFilter sf = (StringFilter) filters.get(0);
			assertThat(sf.getValue()).isEqualTo("abb");
			assertThat(sf.getField()).isEqualTo("company");

			return createResult(5);

		case 6:
			assertThat(filters).hasSize(1);
			assertThat(filters.get(0)).isInstanceOf(ListFilter.class);

			ListFilter lf = (ListFilter) filters.get(0);
			assertThat(lf.getValue().size()).isEqualTo(1);
			assertThat(lf.getValue().get(0)).isEqualTo("small");
			assertThat(lf.getField()).isEqualTo("size");

			return createResult(6);

		case 7:
			assertThat(filters).hasSize(1);
			assertThat(filters.get(0)).isInstanceOf(ListFilter.class);

			lf = (ListFilter) filters.get(0);
			assertThat(lf.getValue().size()).isEqualTo(2);
			assertThat(lf.getValue().get(0)).isEqualTo("small");
			assertThat(lf.getValue().get(1)).isEqualTo("medium");
			assertThat(lf.getField()).isEqualTo("size");

			return createResult(7);

		case 8:

			assertThat(filters).hasSize(2);
			assertThat(filters.get(0)).isInstanceOf(DateFilter.class);
			assertThat(filters.get(1)).isInstanceOf(DateFilter.class);

			DateFilter df = (DateFilter) filters.get(0);
			assertThat(df.getValue()).isEqualTo("07/31/2010");
			assertThat(df.getField()).isEqualTo("date");
			assertThat(df.getComparison()).isEqualTo(Comparison.LESS_THAN);

			df = (DateFilter) filters.get(1);
			assertThat(df.getValue()).isEqualTo("07/01/2010");
			assertThat(df.getField()).isEqualTo("date");
			assertThat(df.getComparison()).isEqualTo(Comparison.GREATER_THAN);

			return createResult(8);

		case 9:
			assertThat(filters).hasSize(1);
			assertThat(filters.get(0)).isInstanceOf(DateFilter.class);

			df = (DateFilter) filters.get(0);
			assertThat(df.getValue()).isEqualTo("07/01/2010");
			assertThat(df.getField()).isEqualTo("date");
			assertThat(df.getComparison()).isEqualTo(Comparison.EQUAL);

			return createResult(9);

		case 10:
			assertThat(filters).hasSize(1);
			assertThat(filters.get(0)).isInstanceOf(StringFilter.class);

			sf = (StringFilter) filters.get(0);
			assertThat(sf.getValue()).isEqualTo("ERROR");
			assertThat(sf.getField()).isEqualTo("level");

			return createResult(10);
		case 11:
			assertThat(request.getFilters().size()).isEqualTo(1);
			assertThat(filters.get(0)).isInstanceOf(NumericFilter.class);

			nf = (NumericFilter) filters.get(0);
			assertThat(nf.getValue()).isEqualTo(1);
			assertThat(nf.getField()).isEqualTo("level");
			assertThat(nf.getComparison()).isNull();

			return createResult(11);
		case 12:
			assertThat(filters).hasSize(1);
			assertThat(filters.get(0)).isInstanceOf(BooleanFilter.class);

			bf = (BooleanFilter) filters.get(0);
			assertThat(bf.getValue()).isEqualTo(true);
			assertThat(bf.getField()).isEqualTo("level");

			return createResult(12);
		case 13:
			assertThat(filters).hasSize(1);
			assertThat(filters.get(0)).isInstanceOf(ListFilter.class);

			lf = (ListFilter) filters.get(0);
			assertThat(lf.getValue().size()).isEqualTo(1);
			assertThat(lf.getValue().get(0)).isEqualTo("small");
			assertThat(lf.getField()).isEqualTo("size");

			return createResult(13);

		case 14:
			assertThat(filters).hasSize(1);
			assertThat(filters.get(0)).isInstanceOf(ListFilter.class);

			lf = (ListFilter) filters.get(0);
			assertThat(lf.getValue().size()).isEqualTo(2);
			assertThat(lf.getValue().get(0)).isEqualTo("small");
			assertThat(lf.getValue().get(1)).isEqualTo("medium");
			assertThat(lf.getField()).isEqualTo("size");

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
