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

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;

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
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

import ch.ralscha.extdirectspring.annotation.ExtDirectMethod;
import ch.ralscha.extdirectspring.annotation.ExtDirectMethodType;
import ch.ralscha.extdirectspring.bean.DataType;
import ch.ralscha.extdirectspring.bean.EdStoreResult;
import ch.ralscha.extdirectspring.bean.EdStoreResult.Builder;
import ch.ralscha.extdirectspring.bean.ExtDirectStoreReadRequest;
import ch.ralscha.extdirectspring.bean.ExtDirectStoreResult;
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

	@ExtDirectMethod(value = ExtDirectMethodType.STORE_READ, event = "test")
	public List<Row> method2() {
		return null;
	}

	@ExtDirectMethod(ExtDirectMethodType.STORE_READ)
	public List<Row> method3(HttpServletResponse response, HttpServletRequest request,
			HttpSession session, Locale locale) {
		return createRows(":" + (response != null) + ";" + (request != null) + ";"
				+ (session != null) + ";" + locale);
	}

	@ExtDirectMethod(value = ExtDirectMethodType.STORE_READ, entryClass = String.class)
	public ExtDirectStoreResult<Row> method4(ExtDirectStoreReadRequest request) {
		return createExtDirectStoreResult(request, "");
	}

	@ExtDirectMethod(value = ExtDirectMethodType.STORE_READ, entryClass = String.class)
	public EdStoreResult method4Ed(ExtDirectStoreReadRequest request) {
		return createEdStoreResult(request, "", null);
	}

	@ExtDirectMethod(value = ExtDirectMethodType.STORE_READ, group = "group3")
	public ExtDirectStoreResult<Row> method5(ExtDirectStoreReadRequest request,
			Locale locale, @RequestParam(value = "id") int id) {
		assertThat(id).isEqualTo(10);
		assertThat(locale).isEqualTo(Locale.ENGLISH);

		assertThat(request.getParams().size()).isEqualTo(1);
		assertThat(request.getParams()).contains(entry("id", 10));

		return createExtDirectStoreResult(request, ":" + id + ";" + locale);
	}

	@ExtDirectMethod(value = ExtDirectMethodType.STORE_READ, group = "group3")
	public EdStoreResult method5Ed(ExtDirectStoreReadRequest request, Locale locale,
			@RequestParam(value = "id") int id) {
		assertThat(id).isEqualTo(10);
		assertThat(locale).isEqualTo(Locale.ENGLISH);

		assertThat(request.getParams().size()).isEqualTo(1);
		assertThat(request.getParams()).contains(entry("id", 10));

		return createEdStoreResult(request, ":" + id + ";" + locale, null);
	}

	@ExtDirectMethod(value = ExtDirectMethodType.STORE_READ, group = "group2")
	public ExtDirectStoreResult<Row> method6(
			@RequestParam(value = "id", defaultValue = "1") int id,
			final HttpServletRequest servletRequest, ExtDirectStoreReadRequest request) {
		assertThat(id).isEqualTo(1);
		assertThat(servletRequest).isNotNull();
		return createExtDirectStoreResult(request,
				":" + id + ";" + (servletRequest != null));
	}

	@ExtDirectMethod(value = ExtDirectMethodType.STORE_READ, group = "group2")
	public EdStoreResult method6Ed(@RequestParam(value = "id", defaultValue = "1") int id,
			final HttpServletRequest servletRequest, ExtDirectStoreReadRequest request) {
		assertThat(id).isEqualTo(1);
		assertThat(servletRequest).isNotNull();
		return createEdStoreResult(request, ":" + id + ";" + (servletRequest != null),
				null);
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
	public ExtDirectStoreResult<Row> method8(
			@DateTimeFormat(iso = ISO.DATE_TIME) Date endDate,
			final HttpServletRequest servletRequest, ExtDirectStoreReadRequest request) {
		assertThat(endDate).isNotNull();
		assertThat(servletRequest).isNotNull();
		return createExtDirectStoreResult(request,
				":" + endDate.toString() + ";" + (servletRequest != null));
	}

	@ExtDirectMethod(value = ExtDirectMethodType.STORE_READ)
	public ExtDirectStoreResult<Row> method9(ExtDirectStoreReadRequest request) {
		ExtDirectStoreResult<Row> result = createExtDirectStoreResult(request, "");
		result.setMessage("everything is okay");
		return result;
	}

	@ExtDirectMethod(value = ExtDirectMethodType.STORE_READ)
	public ExtDirectStoreResult<Row> method10(ExtDirectStoreReadRequest request,
			Locale locale, @RequestParam(value = "id", required = false,
					defaultValue = "20") Integer id) {

		if (!id.equals(20)) {
			assertThat(id).isEqualTo(10);
			assertThat(request.getParams().size()).isEqualTo(1);
			assertThat(request.getParams()).contains(entry("id", 10));
		}
		else {
			assertThat(id).isEqualTo(20);
			assertThat(request.getParams().isEmpty()).isTrue();
		}
		assertThat(locale).isEqualTo(Locale.ENGLISH);

		return RemoteProviderStoreRead.createExtDirectStoreResult(request,
				":" + id + ";" + locale);
	}

	@ExtDirectMethod(value = ExtDirectMethodType.STORE_READ, batched = true)
	public ExtDirectStoreResult<Row> method11(ExtDirectStoreReadRequest request,
			@CookieValue(defaultValue = "defaultCookie") String cookie,
			@RequestHeader(defaultValue = "defaultHeader") String requestHeader) {
		return RemoteProviderStoreRead.createExtDirectStoreResult(request,
				":" + cookie + ":" + requestHeader);
	}

	@ExtDirectMethod(value = ExtDirectMethodType.STORE_READ, batched = false)
	public ExtDirectStoreResult<Row> method12(ExtDirectStoreReadRequest request) {
		return RemoteProviderStoreRead.createExtDirectStoreResult(request, null);
	}

	@ExtDirectMethod(value = ExtDirectMethodType.STORE_READ)
	public EdStoreResult method8Ed(@DateTimeFormat(iso = ISO.DATE_TIME) Date endDate,
			final HttpServletRequest servletRequest, ExtDirectStoreReadRequest request) {
		assertThat(endDate).isNotNull();
		assertThat(servletRequest).isNotNull();
		return createEdStoreResult(request,
				":" + endDate.toString() + ";" + (servletRequest != null), null);
	}

	@ExtDirectMethod(value = ExtDirectMethodType.STORE_READ)
	public EdStoreResult method9Ed(ExtDirectStoreReadRequest request) {
		return createEdStoreResult(request, "", "everything is okay");
	}

	@ExtDirectMethod(value = ExtDirectMethodType.STORE_READ)
	public EdStoreResult method10Ed(ExtDirectStoreReadRequest request, Locale locale,
			@RequestParam(value = "id", required = false,
					defaultValue = "20") Integer id) {

		if (!id.equals(20)) {
			assertThat(id).isEqualTo(10);
			assertThat(request.getParams().size()).isEqualTo(1);
			assertThat(request.getParams()).contains(entry("id", 10));
		}
		else {
			assertThat(id).isEqualTo(20);
			assertThat(request.getParams().isEmpty()).isTrue();
		}
		assertThat(locale).isEqualTo(Locale.ENGLISH);

		return RemoteProviderStoreRead.createEdStoreResult(request,
				":" + id + ";" + locale, null);
	}

	@ExtDirectMethod(value = ExtDirectMethodType.STORE_READ, batched = true)
	public EdStoreResult method11Ed(ExtDirectStoreReadRequest request,
			@CookieValue(defaultValue = "defaultCookie") String cookie,
			@RequestHeader(defaultValue = "defaultHeader") String requestHeader) {
		return RemoteProviderStoreRead.createEdStoreResult(request,
				":" + cookie + ":" + requestHeader, null);
	}

	@ExtDirectMethod(value = ExtDirectMethodType.STORE_READ, batched = false)
	public EdStoreResult method12Ed(ExtDirectStoreReadRequest request) {
		return createEdStoreResult(request, null, null);
	}

	public static ExtDirectStoreResult<Row> createExtDirectStoreResult(
			ExtDirectStoreReadRequest request, final String appendix) {
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
						@Override
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
						@Override
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
						@Override
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
						@Override
						public int compare(Row o1, Row o2) {
							return o2.getId() - o1.getId();
						}
					});
				}
			}

			if (request.getStart() != null && request.getLimit() != null) {
				rows = rows.subList(request.getStart(),
						Math.min(totalSize, request.getStart() + request.getLimit()));
			}
			else {
				rows = rows.subList(0, 50);
			}

		}

		return new ExtDirectStoreResult<Row>().setTotal(Long.valueOf(totalSize))
				.setRecords(rows).setSuccess(Boolean.TRUE);

	}

	public static EdStoreResult createEdStoreResult(ExtDirectStoreReadRequest request,
			final String appendix, final String message) {
		return createEdStoreResult(request, appendix, message, null);
	}

	public static EdStoreResult createEdStoreResult(ExtDirectStoreReadRequest request,
			final String appendix, final String message, final MetaData metaData) {
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
						@Override
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
						@Override
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
						@Override
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
						@Override
						public int compare(Row o1, Row o2) {
							return o2.getId() - o1.getId();
						}
					});
				}
			}

			if (request.getStart() != null && request.getLimit() != null) {
				rows = rows.subList(request.getStart(),
						Math.min(totalSize, request.getStart() + request.getLimit()));
			}
			else {
				rows = rows.subList(0, 50);
			}

		}

		Builder<Row> builder = EdStoreResult.<Row>builder().records(rows)
				.total(Long.valueOf(totalSize)).message(message);
		if (metaData != null) {
			return builder.metaData(metaData.getMetaData()).build();
		}
		return builder.build();
	}

	public static List<Row> createRows(String appendix) {
		List<Row> rows = new ArrayList<Row>();
		for (int i = 0; i < 100; i += 2) {
			rows.add(new Row(i, "name: " + i + appendix, true, "" + (1000 + i)));
			rows.add(new Row(i + 1, "firstname: " + (i + 1) + appendix, false,
					"" + (10 + i + 1)));
		}
		return rows;
	}

	@ExtDirectMethod(value = ExtDirectMethodType.STORE_READ)
	public ExtDirectStoreResult<Row> methodMetadata(ExtDirectStoreReadRequest request) {
		ExtDirectStoreResult<Row> response = createExtDirectStoreResult(request, "");

		if (request.getStart() == null && request.getSort() == null) {
			MetaData metaData = new MetaData();

			metaData.setPagingParameter(0, 50);
			metaData.setSortInfo("name", SortDirection.ASCENDING);

			Field field = new Field("id");
			field.setType(DataType.INTEGER);
			field.addCustomProperty("header", "ID");
			field.addCustomProperty("width", 20);
			field.addCustomProperty("sortable", Boolean.TRUE);
			field.addCustomProperty("resizable", Boolean.TRUE);
			field.addCustomProperty("hideable", Boolean.FALSE);
			metaData.addField(field);

			field = new Field("name");
			field.setType(DataType.STRING);
			field.addCustomProperty("header", "Name");
			field.addCustomProperty("width", 70);
			field.addCustomProperty("sortable", Boolean.TRUE);
			field.addCustomProperty("resizable", Boolean.TRUE);
			field.addCustomProperty("hideable", Boolean.FALSE);
			metaData.addField(field);

			field = new Field("admin");
			field.setType(DataType.BOOLEAN);
			field.addCustomProperty("header", "Administrator");
			field.addCustomProperty("width", 30);
			field.addCustomProperty("sortable", Boolean.TRUE);
			field.addCustomProperty("resizable", Boolean.TRUE);
			field.addCustomProperty("hideable", Boolean.TRUE);
			metaData.addField(field);

			field = new Field("salary");
			field.setType(DataType.FLOAT);
			field.addCustomProperty("header", "Salary");
			field.addCustomProperty("width", 50);
			field.addCustomProperty("sortable", Boolean.FALSE);
			field.addCustomProperty("resizable", Boolean.TRUE);
			field.addCustomProperty("hideable", Boolean.TRUE);
			metaData.addField(field);

			response.setMetaData(metaData);
		}

		return response;
	}

	@ExtDirectMethod(value = ExtDirectMethodType.STORE_READ)
	public EdStoreResult methodMetadataEd(ExtDirectStoreReadRequest request) {
		if (request.getStart() == null && request.getSort() == null) {
			MetaData metaData = new MetaData();

			metaData.setPagingParameter(0, 50);
			metaData.setSortInfo("name", SortDirection.ASCENDING);

			Field field = new Field("id");
			field.setType(DataType.INTEGER);
			field.addCustomProperty("header", "ID");
			field.addCustomProperty("width", 20);
			field.addCustomProperty("sortable", Boolean.TRUE);
			field.addCustomProperty("resizable", Boolean.TRUE);
			field.addCustomProperty("hideable", Boolean.FALSE);
			metaData.addField(field);

			field = new Field("name");
			field.setType(DataType.STRING);
			field.addCustomProperty("header", "Name");
			field.addCustomProperty("width", 70);
			field.addCustomProperty("sortable", Boolean.TRUE);
			field.addCustomProperty("resizable", Boolean.TRUE);
			field.addCustomProperty("hideable", Boolean.FALSE);
			metaData.addField(field);

			field = new Field("admin");
			field.setType(DataType.BOOLEAN);
			field.addCustomProperty("header", "Administrator");
			field.addCustomProperty("width", 30);
			field.addCustomProperty("sortable", Boolean.TRUE);
			field.addCustomProperty("resizable", Boolean.TRUE);
			field.addCustomProperty("hideable", Boolean.TRUE);
			metaData.addField(field);

			field = new Field("salary");
			field.setType(DataType.FLOAT);
			field.addCustomProperty("header", "Salary");
			field.addCustomProperty("width", 50);
			field.addCustomProperty("sortable", Boolean.FALSE);
			field.addCustomProperty("resizable", Boolean.TRUE);
			field.addCustomProperty("hideable", Boolean.TRUE);
			metaData.addField(field);

			return createEdStoreResult(request, "", null, metaData);
		}

		return createEdStoreResult(request, "", null, null);
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@ExtDirectMethod(ExtDirectMethodType.STORE_READ)
	public List<Row> methodFilter(@RequestParam("type") int type,
			ExtDirectStoreReadRequest request) {

		List<Filter> filters = new ArrayList<Filter>(request.getFilters());
		switch (type) {
		case 1:
		case 15: {
			assertThat(request.getFilters()).hasSize(1);
			assertThat(filters.get(0)).isInstanceOf(NumericFilter.class);

			NumericFilter nf = (NumericFilter) filters.get(0);
			assertThat(nf.getValue()).isEqualTo(2);
			assertThat(nf.getField()).isEqualTo("id");
			assertThat(nf.getComparison()).isEqualTo(Comparison.EQUAL);
			if (type == 15) {
				assertThat(nf.getRawComparison()).isEqualTo("=");
			}
			else {
				assertThat(nf.getRawComparison()).isEqualTo("eq");
			}

			NumericFilter nf2 = request.getFirstFilterForField("id");
			assertThat(nf2).isSameAs(nf);

			List<Filter> allFiltersForField = request.getAllFiltersForField("id");
			assertThat(allFiltersForField).hasSize(1);
			Filter nf3 = allFiltersForField.iterator().next();
			assertThat(nf3).isInstanceOf(NumericFilter.class);
			assertThat(nf3).isSameAs(nf);

			assertThat((Filter) request.getFirstFilterForField("xy")).isNull();
			assertThat(request.getAllFiltersForField("xy")).isEmpty();

			return createResult(type);
		}
		case 2:
		case 16: {
			assertThat(request.getFilters()).hasSize(2);
			assertThat(filters.get(0)).isInstanceOf(NumericFilter.class);
			assertThat(filters.get(1)).isInstanceOf(NumericFilter.class);

			NumericFilter nf = (NumericFilter) filters.get(0);
			assertThat(nf.getValue()).isEqualTo(100);
			assertThat(nf.getField()).isEqualTo("id");
			assertThat(nf.getComparison()).isEqualTo(Comparison.LESS_THAN);
			if (type == 16) {
				assertThat(nf.getRawComparison()).isEqualTo("<");
			}
			else {
				assertThat(nf.getRawComparison()).isEqualTo("lt");
			}

			nf = (NumericFilter) filters.get(1);
			assertThat(nf.getValue()).isEqualTo(90);
			assertThat(nf.getField()).isEqualTo("id");
			assertThat(nf.getComparison()).isEqualTo(Comparison.GREATER_THAN);
			if (type == 16) {
				assertThat(nf.getRawComparison()).isEqualTo(">");
			}
			else {
				assertThat(nf.getRawComparison()).isEqualTo("gt");
			}

			NumericFilter nf2 = request.getFirstFilterForField("id");
			assertThat(nf2).isSameAs((NumericFilter) filters.get(0));

			List<Filter> allFiltersForField = request.getAllFiltersForField("id");
			assertThat(allFiltersForField).containsExactly(filters.get(0),
					filters.get(1));

			assertThat((Filter) request.getFirstFilterForField("xy")).isNull();
			assertThat(request.getAllFiltersForField("xy")).isEmpty();

			return createResult(type);
		}
		case 3: {
			assertThat(filters).hasSize(1);
			assertThat(filters.get(0)).isInstanceOf(BooleanFilter.class);

			BooleanFilter bf1 = (BooleanFilter) filters.get(0);
			assertThat(bf1.getValue()).isEqualTo(true);
			assertThat(bf1.getField()).isEqualTo("visible");

			BooleanFilter bf2 = request.getFirstFilterForField("visible");
			assertThat(bf2).isSameAs(bf1);

			List<Filter> allFiltersForField = request.getAllFiltersForField("visible");
			assertThat(allFiltersForField).containsExactly(bf1);

			assertThat((Filter) request.getFirstFilterForField("xy")).isNull();
			assertThat(request.getAllFiltersForField("xy")).isEmpty();

			return createResult(type);
		}
		case 4: {
			assertThat(filters).hasSize(1);
			assertThat(filters.get(0)).isInstanceOf(BooleanFilter.class);

			BooleanFilter bf1 = (BooleanFilter) filters.get(0);
			assertThat(bf1.getValue()).isEqualTo(false);
			assertThat(bf1.getField()).isEqualTo("visible");

			BooleanFilter bf2 = request.getFirstFilterForField("visible");
			assertThat(bf2).isSameAs(bf1);

			List<Filter> allFiltersForField = request.getAllFiltersForField("visible");
			assertThat(allFiltersForField).containsExactly(bf1);

			return createResult(type);
		}
		case 5: {
			assertThat(filters).hasSize(1);
			assertThat(filters.get(0)).isInstanceOf(StringFilter.class);

			StringFilter sf1 = (StringFilter) filters.get(0);
			assertThat(sf1.getValue()).isEqualTo("abb");
			assertThat(sf1.getField()).isEqualTo("company");

			StringFilter sf2 = request.getFirstFilterForField("company");
			assertThat(sf2).isSameAs(sf1);

			List<Filter> allFiltersForField = request.getAllFiltersForField("company");
			assertThat(allFiltersForField).containsExactly(sf1);

			return createResult(type);
		}
		case 6: {
			assertThat(filters).hasSize(1);
			assertThat(filters.get(0)).isInstanceOf(ListFilter.class);

			ListFilter<String> lf1 = (ListFilter) filters.get(0);
			assertThat(lf1.getValue().size()).isEqualTo(1);
			assertThat(lf1.getValue().get(0)).isEqualTo("small");
			assertThat(lf1.getField()).isEqualTo("size");

			ListFilter<String> lf2 = request.getFirstFilterForField("size");
			assertThat(lf2).isSameAs(lf1);

			List<Filter> allFiltersForField = request.getAllFiltersForField("size");
			assertThat(allFiltersForField).containsExactly(lf1);

			return createResult(type);
		}
		case 7: {
			assertThat(filters).hasSize(1);
			assertThat(filters.get(0)).isInstanceOf(ListFilter.class);

			ListFilter<String> lf1 = (ListFilter) filters.get(0);
			assertThat(lf1.getValue().size()).isEqualTo(2);
			assertThat(lf1.getValue().get(0)).isEqualTo("small");
			assertThat(lf1.getValue().get(1)).isEqualTo("medium");
			assertThat(lf1.getField()).isEqualTo("size");

			ListFilter<String> lf2 = request.getFirstFilterForField("size");
			assertThat(lf2).isSameAs(lf1);

			List<Filter> allFiltersForField = request.getAllFiltersForField("size");
			assertThat(allFiltersForField).containsExactly(lf1);

			return createResult(type);
		}
		case 8:
		case 17: {
			assertThat(filters).hasSize(2);
			assertThat(filters.get(0)).isInstanceOf(DateFilter.class);
			assertThat(filters.get(1)).isInstanceOf(DateFilter.class);

			DateFilter df = (DateFilter) filters.get(0);
			assertThat(df.getValue()).isEqualTo("07/31/2010");
			assertThat(df.getField()).isEqualTo("date");
			assertThat(df.getComparison()).isEqualTo(Comparison.LESS_THAN);

			if (type == 17) {
				assertThat(df.getRawComparison()).isEqualTo("<");
			}
			else {
				assertThat(df.getRawComparison()).isEqualTo("lt");
			}

			df = (DateFilter) filters.get(1);
			assertThat(df.getValue()).isEqualTo("07/01/2010");
			assertThat(df.getField()).isEqualTo("date");
			assertThat(df.getComparison()).isEqualTo(Comparison.GREATER_THAN);
			if (type == 17) {
				assertThat(df.getRawComparison()).isEqualTo(">");
			}
			else {
				assertThat(df.getRawComparison()).isEqualTo("gt");
			}

			DateFilter df2 = request.getFirstFilterForField("date");
			assertThat(df2).isSameAs((DateFilter) filters.get(0));

			List<Filter> allFiltersForField = request.getAllFiltersForField("date");
			assertThat(allFiltersForField).containsExactly(filters.get(0),
					filters.get(1));

			return createResult(type);
		}
		case 9:
		case 18: {
			assertThat(filters).hasSize(1);
			assertThat(filters.get(0)).isInstanceOf(DateFilter.class);

			DateFilter df1 = (DateFilter) filters.get(0);
			assertThat(df1.getValue()).isEqualTo("07/01/2010");
			assertThat(df1.getField()).isEqualTo("date");
			assertThat(df1.getComparison()).isEqualTo(Comparison.EQUAL);
			if (type == 18) {
				assertThat(df1.getRawComparison()).isEqualTo("=");
			}
			else {
				assertThat(df1.getRawComparison()).isEqualTo("eq");
			}

			DateFilter df2 = request.getFirstFilterForField("date");
			assertThat(df2).isSameAs(df1);

			List<Filter> allFiltersForField = request.getAllFiltersForField("date");
			assertThat(allFiltersForField).containsExactly(df1);

			return createResult(type);
		}
		case 10: {
			assertThat(filters).hasSize(1);
			assertThat(filters.get(0)).isInstanceOf(StringFilter.class);

			StringFilter sf1 = (StringFilter) filters.get(0);
			assertThat(sf1.getValue()).isEqualTo("ERROR");
			assertThat(sf1.getField()).isEqualTo("level");

			StringFilter sf2 = request.getFirstFilterForField("level");
			assertThat(sf2).isSameAs(sf1);

			List<Filter> allFiltersForField = request.getAllFiltersForField("level");
			assertThat(allFiltersForField).containsExactly(sf1);

			return createResult(type);
		}
		case 11: {
			assertThat(request.getFilters()).hasSize(1);
			assertThat(filters.get(0)).isInstanceOf(NumericFilter.class);

			NumericFilter nf1 = (NumericFilter) filters.get(0);
			assertThat(nf1.getValue()).isEqualTo(1);
			assertThat(nf1.getField()).isEqualTo("level");
			assertThat(nf1.getComparison()).isNull();
			assertThat(nf1.getRawComparison()).isNull();

			NumericFilter nf2 = request.getFirstFilterForField("level");
			assertThat(nf2).isSameAs(nf1);

			List<Filter> allFiltersForField = request.getAllFiltersForField("level");
			assertThat(allFiltersForField).containsExactly(nf1);

			return createResult(type);
		}
		case 12: {
			assertThat(filters).hasSize(1);
			assertThat(filters.get(0)).isInstanceOf(BooleanFilter.class);

			BooleanFilter bf1 = (BooleanFilter) filters.get(0);
			assertThat(bf1.getValue()).isEqualTo(true);
			assertThat(bf1.getField()).isEqualTo("level");

			BooleanFilter bf2 = request.getFirstFilterForField("level");
			assertThat(bf2).isSameAs(bf1);

			List<Filter> allFiltersForField = request.getAllFiltersForField("level");
			assertThat(allFiltersForField).containsExactly(bf1);

			return createResult(type);
		}
		case 13: {
			assertThat(filters).hasSize(1);
			assertThat(filters.get(0)).isInstanceOf(ListFilter.class);

			ListFilter<String> lf1 = (ListFilter) filters.get(0);
			assertThat(lf1.getValue().size()).isEqualTo(1);
			assertThat(lf1.getValue().get(0)).isEqualTo("small");
			assertThat(lf1.getField()).isEqualTo("size");

			ListFilter<String> lf2 = request.getFirstFilterForField("size");
			assertThat(lf2).isSameAs(lf1);

			List<Filter> allFiltersForField = request.getAllFiltersForField("size");
			assertThat(allFiltersForField).containsExactly(lf1);

			return createResult(type);
		}
		case 14: {
			assertThat(filters).hasSize(1);
			assertThat(filters.get(0)).isInstanceOf(ListFilter.class);

			ListFilter<String> lf1 = (ListFilter) filters.get(0);
			assertThat(lf1.getValue().size()).isEqualTo(2);
			assertThat(lf1.getValue().get(0)).isEqualTo("small");
			assertThat(lf1.getValue().get(1)).isEqualTo("medium");
			assertThat(lf1.getField()).isEqualTo("size");

			ListFilter<String> lf2 = request.getFirstFilterForField("size");
			assertThat(lf2).isSameAs(lf1);

			List<Filter> allFiltersForField = request.getAllFiltersForField("size");
			assertThat(allFiltersForField).containsExactly(lf1);

			return createResult(type);
		}
		case 19: {
			assertThat(filters).hasSize(1);
			assertThat(filters.get(0)).isInstanceOf(StringFilter.class);

			StringFilter sf1 = (StringFilter) filters.get(0);
			assertThat(sf1.getValue()).isNull();
			assertThat(sf1.getField()).isEqualTo("name");

			StringFilter sf2 = request.getFirstFilterForField("name");
			assertThat(sf2).isSameAs(sf1);

			List<Filter> allFiltersForField = request.getAllFiltersForField("name");
			assertThat(allFiltersForField).containsExactly(sf1);

			return createResult(type);
		}
		case 20: {
			assertThat(filters).hasSize(1);
			assertThat(filters.get(0)).isInstanceOf(StringFilter.class);

			StringFilter sf1 = (StringFilter) filters.get(0);
			assertThat(sf1.getValue()).isNull();
			assertThat(sf1.getField()).isEqualTo("firstname");

			StringFilter sf2 = request.getFirstFilterForField("firstname");
			assertThat(sf2).isSameAs(sf1);

			List<Filter> allFiltersForField = request.getAllFiltersForField("firstname");
			assertThat(allFiltersForField).containsExactly(sf1);

			return createResult(type);
		}
		case 21: {
			assertThat(filters).hasSize(1);
			assertThat(filters.get(0)).isInstanceOf(StringFilter.class);

			StringFilter sf1 = (StringFilter) filters.get(0);
			assertThat(sf1.getValue()).isNull();
			assertThat(sf1.getField()).isEqualTo("firstname");

			StringFilter sf2 = request.getFirstFilterForField("firstname");
			assertThat(sf2).isSameAs(sf1);

			List<Filter> allFiltersForField = request.getAllFiltersForField("firstname");
			assertThat(allFiltersForField).containsExactly(sf1);

			return createResult(type);
		}
		case 22: {
			assertThat(request.getFilters()).hasSize(1);
			assertThat(filters.get(0)).isInstanceOf(NumericFilter.class);

			NumericFilter nf = (NumericFilter) filters.get(0);
			assertThat(nf.getValue()).isEqualTo(2);
			assertThat(nf.getField()).isEqualTo("id");
			assertThat(nf.getComparison()).isEqualTo(Comparison.EQUAL);
			assertThat(nf.getRawComparison()).isEqualTo("eq");

			NumericFilter nf2 = request.getFirstFilterForField("id");
			assertThat(nf2).isSameAs(nf);

			List<Filter> allFiltersForField = request.getAllFiltersForField("id");
			assertThat(allFiltersForField).hasSize(1);
			Filter nf3 = allFiltersForField.iterator().next();
			assertThat(nf3).isInstanceOf(NumericFilter.class);
			assertThat(nf3).isSameAs(nf);

			assertThat((Filter) request.getFirstFilterForField("xy")).isNull();
			assertThat(request.getAllFiltersForField("xy")).isEmpty();

			assertThat(request.getSorters()).hasSize(1);
			SortInfo sortInfo = request.getSorters().iterator().next();
			assertThat(sortInfo.getDirection()).isEqualTo(SortDirection.ASCENDING);
			assertThat(sortInfo.getProperty()).isEqualTo("company");

			return createResult(type);
		}
		case 23: {
			assertThat(request.getFilters()).hasSize(2);
			assertThat(filters.get(0)).isInstanceOf(NumericFilter.class);
			assertThat(filters.get(1)).isInstanceOf(NumericFilter.class);

			NumericFilter nf = (NumericFilter) filters.get(0);
			assertThat(nf.getValue()).isEqualTo(100);
			assertThat(nf.getField()).isEqualTo("id");
			assertThat(nf.getComparison()).isEqualTo(Comparison.LESS_THAN);
			assertThat(nf.getRawComparison()).isEqualTo("lt");

			nf = (NumericFilter) filters.get(1);
			assertThat(nf.getValue()).isEqualTo(90);
			assertThat(nf.getField()).isEqualTo("id");
			assertThat(nf.getComparison()).isEqualTo(Comparison.GREATER_THAN);
			assertThat(nf.getRawComparison()).isEqualTo("gt");

			NumericFilter nf2 = request.getFirstFilterForField("id");
			assertThat(nf2).isSameAs((NumericFilter) filters.get(0));

			List<Filter> allFiltersForField = request.getAllFiltersForField("id");
			assertThat(allFiltersForField).containsExactly(filters.get(0),
					filters.get(1));

			assertThat((Filter) request.getFirstFilterForField("xy")).isNull();
			assertThat(request.getAllFiltersForField("xy")).isEmpty();

			assertThat(request.getSorters()).hasSize(1);
			SortInfo sortInfo = request.getSorters().iterator().next();
			assertThat(sortInfo.getDirection()).isEqualTo(SortDirection.ASCENDING);
			assertThat(sortInfo.getProperty()).isEqualTo("company");

			return createResult(type);
		}
		case 24: {
			assertThat(filters).hasSize(1);
			assertThat(filters.get(0)).isInstanceOf(BooleanFilter.class);

			BooleanFilter bf1 = (BooleanFilter) filters.get(0);
			assertThat(bf1.getValue()).isEqualTo(true);
			assertThat(bf1.getField()).isEqualTo("visible");
			assertThat(bf1.getComparison()).isEqualTo(Comparison.EQUAL);
			assertThat(bf1.getRawComparison()).isEqualTo("=");

			BooleanFilter bf2 = request.getFirstFilterForField("visible");
			assertThat(bf2).isSameAs(bf1);

			List<Filter> allFiltersForField = request.getAllFiltersForField("visible");
			assertThat(allFiltersForField).containsExactly(bf1);

			assertThat((Filter) request.getFirstFilterForField("xy")).isNull();
			assertThat(request.getAllFiltersForField("xy")).isEmpty();

			assertThat(request.getSorters()).hasSize(1);
			SortInfo sortInfo = request.getSorters().iterator().next();
			assertThat(sortInfo.getDirection()).isEqualTo(SortDirection.ASCENDING);
			assertThat(sortInfo.getProperty()).isEqualTo("company");

			return createResult(type);
		}
		case 25: {
			assertThat(filters).hasSize(1);
			assertThat(filters.get(0)).isInstanceOf(BooleanFilter.class);

			BooleanFilter bf1 = (BooleanFilter) filters.get(0);
			assertThat(bf1.getValue()).isEqualTo(false);
			assertThat(bf1.getField()).isEqualTo("visible");
			assertThat(bf1.getComparison()).isEqualTo(Comparison.EQUAL);
			assertThat(bf1.getRawComparison()).isEqualTo("=");

			BooleanFilter bf2 = request.getFirstFilterForField("visible");
			assertThat(bf2).isSameAs(bf1);

			List<Filter> allFiltersForField = request.getAllFiltersForField("visible");
			assertThat(allFiltersForField).containsExactly(bf1);

			assertThat(request.getSorters()).hasSize(1);
			SortInfo sortInfo = request.getSorters().iterator().next();
			assertThat(sortInfo.getDirection()).isEqualTo(SortDirection.ASCENDING);
			assertThat(sortInfo.getProperty()).isEqualTo("company");

			return createResult(type);
		}
		case 26: {
			assertThat(filters).hasSize(1);
			assertThat(filters.get(0)).isInstanceOf(StringFilter.class);

			StringFilter sf1 = (StringFilter) filters.get(0);
			assertThat(sf1.getValue()).isEqualTo("abb");
			assertThat(sf1.getField()).isEqualTo("company");
			assertThat(sf1.getComparison()).isEqualTo(Comparison.LIKE);
			assertThat(sf1.getRawComparison()).isEqualTo("like");

			StringFilter sf2 = request.getFirstFilterForField("company");
			assertThat(sf2).isSameAs(sf1);

			List<Filter> allFiltersForField = request.getAllFiltersForField("company");
			assertThat(allFiltersForField).containsExactly(sf1);

			assertThat(request.getSorters()).hasSize(1);
			SortInfo sortInfo = request.getSorters().iterator().next();
			assertThat(sortInfo.getDirection()).isEqualTo(SortDirection.ASCENDING);
			assertThat(sortInfo.getProperty()).isEqualTo("company");

			return createResult(type);
		}
		case 27: {
			assertThat(filters).hasSize(1);
			assertThat(filters.get(0)).isInstanceOf(ListFilter.class);

			ListFilter<String> lf1 = (ListFilter) filters.get(0);
			assertThat(lf1.getValue().size()).isEqualTo(1);
			assertThat(lf1.getValue().get(0)).isEqualTo("small");
			assertThat(lf1.getField()).isEqualTo("size");
			assertThat(lf1.getComparison()).isEqualTo(Comparison.IN);
			assertThat(lf1.getRawComparison()).isEqualTo("in");

			ListFilter<String> lf2 = request.getFirstFilterForField("size");
			assertThat(lf2).isSameAs(lf1);

			List<Filter> allFiltersForField = request.getAllFiltersForField("size");
			assertThat(allFiltersForField).containsExactly(lf1);

			assertThat(request.getSorters()).hasSize(1);
			SortInfo sortInfo = request.getSorters().iterator().next();
			assertThat(sortInfo.getDirection()).isEqualTo(SortDirection.ASCENDING);
			assertThat(sortInfo.getProperty()).isEqualTo("company");

			return createResult(type);
		}
		case 28: {
			assertThat(filters).hasSize(1);
			assertThat(filters.get(0)).isInstanceOf(ListFilter.class);

			ListFilter<String> lf1 = (ListFilter) filters.get(0);
			assertThat(lf1.getValue().size()).isEqualTo(2);
			assertThat(lf1.getValue().get(0)).isEqualTo("small");
			assertThat(lf1.getValue().get(1)).isEqualTo("medium");
			assertThat(lf1.getField()).isEqualTo("size");
			assertThat(lf1.getComparison()).isEqualTo(Comparison.IN);
			assertThat(lf1.getRawComparison()).isEqualTo("in");

			ListFilter<String> lf2 = request.getFirstFilterForField("size");
			assertThat(lf2).isSameAs(lf1);

			List<Filter> allFiltersForField = request.getAllFiltersForField("size");
			assertThat(allFiltersForField).containsExactly(lf1);

			assertThat(request.getSorters()).hasSize(1);
			SortInfo sortInfo = request.getSorters().iterator().next();
			assertThat(sortInfo.getDirection()).isEqualTo(SortDirection.ASCENDING);
			assertThat(sortInfo.getProperty()).isEqualTo("company");

			return createResult(type);
		}
		case 29: {
			assertThat(request.getFilters()).hasSize(1);
			assertThat(filters.get(0)).isInstanceOf(NumericFilter.class);

			NumericFilter nf = (NumericFilter) filters.get(0);
			assertThat(nf.getValue()).isEqualTo(1407103200000L);
			assertThat(nf.getField()).isEqualTo("date");
			assertThat(nf.getComparison()).isEqualTo(Comparison.EQUAL);
			assertThat(nf.getRawComparison()).isEqualTo("eq");

			NumericFilter nf2 = request.getFirstFilterForField("date");
			assertThat(nf2).isSameAs(nf);

			List<Filter> allFiltersForField = request.getAllFiltersForField("date");
			assertThat(allFiltersForField).hasSize(1);
			Filter nf3 = allFiltersForField.iterator().next();
			assertThat(nf3).isInstanceOf(NumericFilter.class);
			assertThat(nf3).isSameAs(nf);

			assertThat((Filter) request.getFirstFilterForField("xy")).isNull();
			assertThat(request.getAllFiltersForField("xy")).isEmpty();

			assertThat(request.getSorters()).hasSize(1);
			SortInfo sortInfo = request.getSorters().iterator().next();
			assertThat(sortInfo.getDirection()).isEqualTo(SortDirection.ASCENDING);
			assertThat(sortInfo.getProperty()).isEqualTo("company");

			return createResult(type);
		}
		case 30: {
			assertThat(request.getFilters()).hasSize(2);
			assertThat(filters.get(0)).isInstanceOf(NumericFilter.class);
			assertThat(filters.get(1)).isInstanceOf(NumericFilter.class);

			NumericFilter nf = (NumericFilter) filters.get(0);
			assertThat(nf.getValue()).isEqualTo(1407448800000L);
			assertThat(nf.getField()).isEqualTo("date");
			assertThat(nf.getComparison()).isEqualTo(Comparison.LESS_THAN);
			assertThat(nf.getRawComparison()).isEqualTo("lt");

			nf = (NumericFilter) filters.get(1);
			assertThat(nf.getValue()).isEqualTo(1406844000000L);
			assertThat(nf.getField()).isEqualTo("date");
			assertThat(nf.getComparison()).isEqualTo(Comparison.GREATER_THAN);
			assertThat(nf.getRawComparison()).isEqualTo("gt");

			NumericFilter nf2 = request.getFirstFilterForField("date");
			assertThat(nf2).isSameAs((NumericFilter) filters.get(0));

			List<Filter> allFiltersForField = request.getAllFiltersForField("date");
			assertThat(allFiltersForField).containsExactly(filters.get(0),
					filters.get(1));

			assertThat((Filter) request.getFirstFilterForField("xy")).isNull();
			assertThat(request.getAllFiltersForField("xy")).isEmpty();

			assertThat(request.getSorters()).hasSize(1);
			SortInfo sortInfo = request.getSorters().iterator().next();
			assertThat(sortInfo.getDirection()).isEqualTo(SortDirection.ASCENDING);
			assertThat(sortInfo.getProperty()).isEqualTo("company");

			return createResult(type);
		}
		case 31: {
			assertThat(filters).hasSize(1);
			assertThat(filters.get(0)).isInstanceOf(ListFilter.class);

			ListFilter<Integer> lf1 = (ListFilter) filters.get(0);
			assertThat(lf1.getValue().size()).isEqualTo(1);
			assertThat(lf1.getValue().get(0)).isEqualTo(1);
			assertThat(lf1.getField()).isEqualTo("size");
			assertThat(lf1.getComparison()).isEqualTo(Comparison.IN);
			assertThat(lf1.getRawComparison()).isEqualTo("in");

			ListFilter<Integer> lf2 = request.getFirstFilterForField("size");
			assertThat(lf2).isSameAs(lf1);

			List<Filter> allFiltersForField = request.getAllFiltersForField("size");
			assertThat(allFiltersForField).containsExactly(lf1);

			assertThat(request.getSorters()).hasSize(1);
			SortInfo sortInfo = request.getSorters().iterator().next();
			assertThat(sortInfo.getDirection()).isEqualTo(SortDirection.ASCENDING);
			assertThat(sortInfo.getProperty()).isEqualTo("company");
			assertThat(request.getPage()).isEqualTo(1);
			assertThat(request.getStart()).isEqualTo(0);
			assertThat(request.getLimit()).isEqualTo(50);

			return createResult(type);
		}
		case 32: {
			assertThat(filters).hasSize(1);
			assertThat(filters.get(0)).isInstanceOf(ListFilter.class);

			ListFilter<Integer> lf1 = (ListFilter) filters.get(0);
			assertThat(lf1.getValue().size()).isEqualTo(2);
			assertThat(lf1.getValue().get(0)).isEqualTo(1);
			assertThat(lf1.getValue().get(1)).isEqualTo(2);
			assertThat(lf1.getField()).isEqualTo("size");
			assertThat(lf1.getComparison()).isEqualTo(Comparison.IN);
			assertThat(lf1.getRawComparison()).isEqualTo("in");

			ListFilter<Integer> lf2 = request.getFirstFilterForField("size");
			assertThat(lf2).isSameAs(lf1);

			List<Filter> allFiltersForField = request.getAllFiltersForField("size");
			assertThat(allFiltersForField).containsExactly(lf1);

			assertThat(request.getSorters()).hasSize(1);
			SortInfo sortInfo = request.getSorters().iterator().next();
			assertThat(sortInfo.getDirection()).isEqualTo(SortDirection.ASCENDING);
			assertThat(sortInfo.getProperty()).isEqualTo("company");
			assertThat(request.getPage()).isEqualTo(1);
			assertThat(request.getStart()).isEqualTo(0);
			assertThat(request.getLimit()).isEqualTo(50);

			return createResult(type);
		}
		case 33: {
			assertThat(filters).hasSize(1);
			assertThat(filters.get(0)).isInstanceOf(StringFilter.class);

			StringFilter sf1 = (StringFilter) filters.get(0);
			assertThat(sf1.getValue()).isEqualTo("abb");
			assertThat(sf1.getField()).isEqualTo("company");
			assertThat(sf1.getComparison()).isNull();
			assertThat(sf1.getRawComparison()).isEqualTo("fuzzy");

			StringFilter sf2 = request.getFirstFilterForField("company");
			assertThat(sf2).isSameAs(sf1);

			List<Filter> allFiltersForField = request.getAllFiltersForField("company");
			assertThat(allFiltersForField).containsExactly(sf1);

			assertThat(request.getSorters()).hasSize(1);
			SortInfo sortInfo = request.getSorters().iterator().next();
			assertThat(sortInfo.getDirection()).isEqualTo(SortDirection.ASCENDING);
			assertThat(sortInfo.getProperty()).isEqualTo("company");

			return createResult(type);
		}
		default: // do nothing
		}

		return Collections.emptyList();
	}

	private static List<Row> createResult(int i) {
		Row r = new Row(i, null, false, null);
		List<Row> result = new ArrayList<Row>();
		result.add(r);
		return result;
	}

}
