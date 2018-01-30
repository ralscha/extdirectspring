/**
 * Copyright 2010-2018 Ralph Schaer <ralphschaer@gmail.com>
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
public class RemoteProviderStoreModifySingle {

	@ExtDirectMethod(value = ExtDirectMethodType.STORE_MODIFY)
	public Row create1(Row row) {
		return row;
	}

	@ExtDirectMethod(value = ExtDirectMethodType.STORE_MODIFY)
	public Row create2(Row row, HttpServletResponse response, HttpServletRequest request,
			HttpSession session, Locale locale) {
		assertThat(response).isNotNull();
		assertThat(request).isNotNull();
		assertThat(session).isNotNull();
		assertThat(locale).isEqualTo(Locale.ENGLISH);

		return row;
	}

	@ExtDirectMethod(value = ExtDirectMethodType.STORE_MODIFY)
	public Row update1(Row row) {
		return row;
	}

	@ExtDirectMethod(value = ExtDirectMethodType.STORE_MODIFY)
	public Row update2(Locale locale, @RequestParam(name = "aParam") int theParam,
			Row row) {
		assertThat(theParam).isEqualTo(42);
		assertThat(locale).isEqualTo(Locale.ENGLISH);
		return row;
	}

	@ExtDirectMethod(value = ExtDirectMethodType.STORE_MODIFY)
	public Row update3(Row row,
			@RequestParam(name = "aParam", defaultValue = "1") int theParam,
			final HttpServletRequest servletRequest) {
		assertThat(theParam).isEqualTo(1);
		assertThat(servletRequest).isNotNull();
		return row;
	}

	@ExtDirectMethod(value = ExtDirectMethodType.STORE_MODIFY, group = "group2")
	public Row update4(@RequestParam(name = "aParam", required = false) Integer theParam,
			@RequestParam(required = false) @DateTimeFormat(
					iso = ISO.DATE) LocalDate yesterday,
			Row row) {

		if (theParam == null) {
			assertThat(theParam).isNull();
			assertThat(yesterday).isNull();
		}
		else {
			assertThat(yesterday).isNotNull();
			assertThat(yesterday).isEqualTo(new LocalDate().minusDays(1));
			assertThat(theParam).isEqualTo(Integer.valueOf(11));
		}
		return row;
	}

	@ExtDirectMethod(value = ExtDirectMethodType.STORE_MODIFY, group = "group3")
	public Integer destroy(Integer rowId) {
		return rowId;
	}

}
