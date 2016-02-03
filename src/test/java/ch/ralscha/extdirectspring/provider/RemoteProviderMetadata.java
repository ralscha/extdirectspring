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

import java.util.List;
import java.util.Locale;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;

import ch.ralscha.extdirectspring.annotation.ExtDirectMethod;
import ch.ralscha.extdirectspring.annotation.ExtDirectMethodType;
import ch.ralscha.extdirectspring.annotation.MetadataParam;
import ch.ralscha.extdirectspring.bean.EdStoreResult;
import ch.ralscha.extdirectspring.bean.ExtDirectStoreReadRequest;
import ch.ralscha.extdirectspring.bean.ExtDirectStoreResult;
import ch.ralscha.extdirectspring.provider.RemoteProviderTreeLoad.Node;

@Service
public class RemoteProviderMetadata {

	@ExtDirectMethod(value = ExtDirectMethodType.STORE_READ, group = "metadata")
	public ExtDirectStoreResult<Row> method1(ExtDirectStoreReadRequest request,
			Locale locale, @RequestParam(value = "id") int id,
			@MetadataParam(value = "mp") String mp) {
		assertThat(id).isEqualTo(10);
		assertThat(locale).isEqualTo(Locale.ENGLISH);

		assertThat(request.getParams().size()).isEqualTo(1);
		assertThat(request.getParams()).contains(entry("id", 10));

		return RemoteProviderStoreRead.createExtDirectStoreResult(request,
				":" + id + ";" + mp + ";" + locale);
	}

	@ExtDirectMethod(value = ExtDirectMethodType.STORE_READ, group = "metadata")
	public ExtDirectStoreResult<Row> method2(
			@MetadataParam(value = "id", defaultValue = "1") int id,
			final HttpServletRequest servletRequest, ExtDirectStoreReadRequest request) {
		assertThat(id).isEqualTo(1);
		assertThat(servletRequest).isNotNull();
		return RemoteProviderStoreRead.createExtDirectStoreResult(request,
				":" + id + ";" + (servletRequest != null));
	}

	@ExtDirectMethod(value = ExtDirectMethodType.STORE_READ, group = "metadata")
	public EdStoreResult method1Ed(ExtDirectStoreReadRequest request, Locale locale,
			@RequestParam(value = "id") int id, @MetadataParam(value = "mp") String mp) {
		assertThat(id).isEqualTo(10);
		assertThat(locale).isEqualTo(Locale.ENGLISH);

		assertThat(request.getParams().size()).isEqualTo(1);
		assertThat(request.getParams()).contains(entry("id", 10));

		return RemoteProviderStoreRead.createEdStoreResult(request,
				":" + id + ";" + mp + ";" + locale, null);
	}

	@ExtDirectMethod(value = ExtDirectMethodType.STORE_READ, group = "metadata")
	public EdStoreResult method2Ed(
			@MetadataParam(value = "id", defaultValue = "1") int id,
			final HttpServletRequest servletRequest, ExtDirectStoreReadRequest request) {
		assertThat(id).isEqualTo(1);
		assertThat(servletRequest).isNotNull();
		return RemoteProviderStoreRead.createEdStoreResult(request,
				":" + id + ";" + (servletRequest != null), null);
	}

	@ExtDirectMethod(value = ExtDirectMethodType.STORE_READ, group = "metadata")
	public List<Row> method3(@MetadataParam(value = "id", required = false) Integer id) {
		if (id == null) {
			assertThat(id).isNull();
		}
		else {
			assertThat(id).isEqualTo(Integer.valueOf(12));
		}
		return RemoteProviderStoreRead.createRows(":" + id);
	}

	@ExtDirectMethod(value = ExtDirectMethodType.STORE_READ, group = "metadata")
	public List<Row> method4(@MetadataParam(value = "id") Optional<Integer> id) {
		if (!id.isPresent()) {
			assertThat(id.orElse(null)).isNull();
		}
		else {
			assertThat(id.get()).isEqualTo(Integer.valueOf(13));
		}
		return RemoteProviderStoreRead.createRows(":" + id);
	}

	@ExtDirectMethod(value = ExtDirectMethodType.STORE_READ, group = "metadata")
	public ExtDirectStoreResult<Row> method5(ExtDirectStoreReadRequest request,
			Locale locale, @MetadataParam(value = "id", required = false,
					defaultValue = "20") Integer id) {
		assertThat(request.getParams().isEmpty()).isTrue();
		if (!id.equals(20)) {
			assertThat(id).isEqualTo(10);
		}
		else {
			assertThat(id).isEqualTo(20);
		}
		assertThat(locale).isEqualTo(Locale.ENGLISH);

		return RemoteProviderStoreRead.createExtDirectStoreResult(request,
				":" + id + ";" + locale);
	}

	@ExtDirectMethod(value = ExtDirectMethodType.STORE_READ, group = "metadata")
	public ExtDirectStoreResult<Row> method6(ExtDirectStoreReadRequest request,
			Locale locale, @MetadataParam Optional<Integer> id) {

		Integer i = id.orElse(20);
		assertThat(request.getParams().isEmpty()).isTrue();
		if (!i.equals(20)) {
			assertThat(i).isEqualTo(10);
		}
		else {
			assertThat(i).isEqualTo(20);
		}
		assertThat(locale).isEqualTo(Locale.ENGLISH);

		return RemoteProviderStoreRead.createExtDirectStoreResult(request,
				":" + i + ";" + locale);
	}

	@ExtDirectMethod(value = ExtDirectMethodType.STORE_READ, group = "metadata")
	public EdStoreResult method5Ed(ExtDirectStoreReadRequest request, Locale locale,
			@MetadataParam(value = "id", required = false,
					defaultValue = "20") Integer id) {
		assertThat(request.getParams().isEmpty()).isTrue();
		if (!id.equals(20)) {
			assertThat(id).isEqualTo(10);
		}
		else {
			assertThat(id).isEqualTo(20);
		}
		assertThat(locale).isEqualTo(Locale.ENGLISH);

		return RemoteProviderStoreRead.createEdStoreResult(request,
				":" + id + ";" + locale, null);
	}

	@ExtDirectMethod(value = ExtDirectMethodType.STORE_READ, group = "metadata")
	public EdStoreResult method6Ed(ExtDirectStoreReadRequest request, Locale locale,
			@MetadataParam Optional<Integer> id) {

		Integer i = id.orElse(20);
		assertThat(request.getParams().isEmpty()).isTrue();
		if (!i.equals(20)) {
			assertThat(i).isEqualTo(10);
		}
		else {
			assertThat(i).isEqualTo(20);
		}
		assertThat(locale).isEqualTo(Locale.ENGLISH);

		return RemoteProviderStoreRead.createEdStoreResult(request,
				":" + i + ";" + locale, null);
	}

	@ExtDirectMethod(value = ExtDirectMethodType.STORE_MODIFY, group = "metadata")
	public List<Row> update1(Locale locale, @MetadataParam(value = "id") int id,
			List<Row> rows) {
		assertThat(id).isEqualTo(10);
		assertThat(locale).isEqualTo(Locale.ENGLISH);
		return rows;
	}

	@ExtDirectMethod(value = ExtDirectMethodType.STORE_MODIFY, group = "metadata")
	public List<Row> update2(List<Row> rows,
			@MetadataParam(value = "id", defaultValue = "1") int id,
			final HttpServletRequest servletRequest) {
		assertThat(id).isEqualTo(1);
		assertThat(servletRequest).isNotNull();
		return rows;
	}

	@ExtDirectMethod(value = ExtDirectMethodType.STORE_MODIFY, group = "metadata")
	public List<Row> update3(List<Row> rows,
			@MetadataParam(value = "id") Optional<Integer> id,
			final HttpServletRequest servletRequest) {
		assertThat(id.orElse(2)).isEqualTo(2);
		assertThat(servletRequest).isNotNull();
		return rows;
	}

	@ExtDirectMethod(value = ExtDirectMethodType.TREE_LOAD, group = "metadata")
	public List<Node> treeLoad1(@RequestParam("node") String node,
			@RequestParam(defaultValue = "defaultValue") String foo,
			@MetadataParam(value = "id") Integer id) {
		return RemoteProviderTreeLoad.createTreeList(node, ":" + foo + ";" + id);
	}

	@ExtDirectMethod(value = ExtDirectMethodType.TREE_LOAD, group = "metadata")
	public List<Node> treeLoad2(@RequestParam("node") String node,
			@RequestParam(defaultValue = "defaultValue") String foo,
			@MetadataParam(value = "id", defaultValue = "22") Integer id) {
		return RemoteProviderTreeLoad.createTreeList(node, ":" + foo + ";" + id);
	}

	@ExtDirectMethod(value = ExtDirectMethodType.TREE_LOAD, group = "metadata")
	public List<Node> treeLoad3(@RequestParam("node") String node,
			@RequestParam(defaultValue = "defaultValue") String foo,
			@MetadataParam(value = "id") Optional<Integer> id) {
		return RemoteProviderTreeLoad.createTreeList(node,
				":" + foo + ";" + id.orElse(23));
	}

}
