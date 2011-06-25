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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

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
public class RemoteProviderStoreModify {

	@ExtDirectMethod(value = ExtDirectMethodType.STORE_MODIFY)
	public List<Row> create1(List<Row> rows) {
		return rows;
	}

	@ExtDirectMethod(value = ExtDirectMethodType.STORE_MODIFY)
	public List<Row> create2(List<Row> rows, HttpServletResponse response, HttpServletRequest request,
			HttpSession session, Locale locale) {
		assertNotNull(response);
		assertNotNull(request);
		assertNotNull(session);
		assertEquals(Locale.ENGLISH, locale);

		return rows;
	}

	@ExtDirectMethod(value = ExtDirectMethodType.STORE_MODIFY)
	public List<Row> update1(List<Row> rows) {
		return rows;
	}

	@ExtDirectMethod(value = ExtDirectMethodType.STORE_MODIFY)
	public List<Row> update2(Locale locale, @RequestParam(value = "id") int id, List<Row> rows) {
		assertEquals(10, id);
		assertEquals(Locale.ENGLISH, locale);
		return rows;
	}

	@ExtDirectMethod(value = ExtDirectMethodType.STORE_MODIFY)
	public List<Row> update3(List<Row> rows, @RequestParam(value = "id", defaultValue = "1") int id,
			HttpServletRequest servletRequest) {
		assertEquals(1, id);
		assertNotNull(servletRequest);
		return rows;
	}

	@ExtDirectMethod(value = ExtDirectMethodType.STORE_MODIFY, group = "group2")
	public List<Row> update4(@RequestParam(value = "id", required = false) Integer id, 
			@RequestParam(required=false) @DateTimeFormat(iso = ISO.DATE) LocalDate yesterday,
			List<Row> rows) {
		
		if (id == null) {
			assertNull(id);
			assertNull(yesterday);
		} else {
			assertNotNull(yesterday);
			assertEquals(new LocalDate().minusDays(1), yesterday);
			assertEquals(Integer.valueOf(11), id);
		}
		return rows;
	}

	@ExtDirectMethod(value = ExtDirectMethodType.STORE_MODIFY, group = "group3")
	public List<Integer> destroy(List<Integer> rows) {
		return rows;
	}

}
