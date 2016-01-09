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

import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.joda.time.LocalDate;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;

import ch.ralscha.extdirectspring.annotation.ExtDirectMethod;
import ch.ralscha.extdirectspring.annotation.ExtDirectMethodType;

@Service
public class RemoteProviderStoreModifyArray {

	@ExtDirectMethod(value = ExtDirectMethodType.STORE_MODIFY)
	public Row[] create1(List<Row> rows) {
		return rows.toArray(new Row[rows.size()]);
	}

	@ExtDirectMethod(value = ExtDirectMethodType.STORE_MODIFY, event = "test")
	public Row[] create2(List<Row> rows, HttpServletResponse response,
			HttpServletRequest request, final HttpSession session, Locale locale) {
		assertThat(response).isNotNull();
		assertThat(request).isNotNull();
		assertThat(session).isNotNull();
		assertThat(locale).isEqualTo(Locale.ENGLISH);

		return rows.toArray(new Row[rows.size()]);
	}

	@ExtDirectMethod(value = ExtDirectMethodType.STORE_MODIFY)
	public Row[] update1(List<Row> rows) {
		return rows.toArray(new Row[rows.size()]);
	}

	@ExtDirectMethod(value = ExtDirectMethodType.STORE_MODIFY)
	public Row[] update2(Locale locale, @RequestParam(value = "id") int id,
			List<Row> rows) {
		assertThat(id).isEqualTo(10);
		assertThat(locale).isEqualTo(Locale.ENGLISH);
		return rows.toArray(new Row[rows.size()]);
	}

	@ExtDirectMethod(value = ExtDirectMethodType.STORE_MODIFY)
	public Row[] update3(List<Row> rows,
			@RequestParam(value = "id", defaultValue = "1") int id,
			final HttpServletRequest servletRequest) {
		assertThat(id).isEqualTo(1);
		assertThat(servletRequest).isNotNull();
		return rows.toArray(new Row[rows.size()]);
	}

	@ExtDirectMethod(value = ExtDirectMethodType.STORE_MODIFY, group = "group2")
	public Row[] update4(
			@RequestParam(value = "id", required = false) Integer id, @RequestParam(
					required = false) @DateTimeFormat(iso = ISO.DATE) LocalDate yesterday,
			final List<Row> rows) {

		if (id == null) {
			assertThat(id).isNull();
			assertThat(yesterday).isNull();
		}
		else {
			assertThat(yesterday).isNotNull();
			assertThat(yesterday).isEqualTo(new LocalDate().minusDays(1));
			assertThat(id).isEqualTo(Integer.valueOf(11));
		}
		return rows.toArray(new Row[rows.size()]);
	}

	@ExtDirectMethod(value = ExtDirectMethodType.STORE_MODIFY, group = "group3")
	public Integer[] destroy(List<Integer> rows) {
		return rows.toArray(new Integer[rows.size()]);
	}

}
