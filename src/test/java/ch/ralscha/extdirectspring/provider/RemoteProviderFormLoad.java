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

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;

import ch.ralscha.extdirectspring.annotation.ExtDirectMethod;
import ch.ralscha.extdirectspring.annotation.ExtDirectMethodType;
import ch.ralscha.extdirectspring.bean.ExtDirectFormLoadResult;

@Service
public class RemoteProviderFormLoad {

	@ExtDirectMethod(value = ExtDirectMethodType.FORM_LOAD, group = "group3", event = "test")
	public FormInfo method1(@RequestParam(value = "d") final double d) {
		FormInfo info = new FormInfo();
		info.setBack(d);
		info.setAdmin(true);
		info.setAge(31);
		info.setBirthday(new GregorianCalendar(1980, Calendar.JANUARY, 15).getTime());
		info.setName("Bob");
		info.setSalary(new BigDecimal("10000.55"));
		return info;
	}

	@ExtDirectMethod(ExtDirectMethodType.FORM_LOAD)
	public FormInfo method2() {
		return null;
	}

	@ExtDirectMethod(ExtDirectMethodType.FORM_LOAD)
	public FormInfo method3(HttpServletResponse response, HttpServletRequest request, final HttpSession session,
			Locale locale) {
		FormInfo fi = new FormInfo();
		fi.setResult((response != null) + ";" + (request != null) + ";" + (session != null) + ";" + locale);
		return fi;
	}

	@ExtDirectMethod(ExtDirectMethodType.FORM_LOAD)
	public FormInfo method4(Locale locale, @RequestParam(value = "id") final int id) {
		FormInfo fi = new FormInfo();
		fi.setResult("id=" + id + ";" + locale);
		return fi;
	}

	@ExtDirectMethod(value = ExtDirectMethodType.FORM_LOAD, group = "group3", entryClass = String.class)
	public ExtDirectFormLoadResult method5(@RequestParam(value = "id", defaultValue = "1") final int id,
			final HttpServletRequest servletRequest) {
		FormInfo fi = new FormInfo();
		fi.setResult(id + ";" + (servletRequest != null));
		return new ExtDirectFormLoadResult(fi);
	}

	@ExtDirectMethod(ExtDirectMethodType.FORM_LOAD)
	public ExtDirectFormLoadResult method6(@RequestParam(value = "id", required = false) final Integer id) {
		return new ExtDirectFormLoadResult("TEST:" + id);
	}

	@ExtDirectMethod(ExtDirectMethodType.FORM_LOAD)
	public ExtDirectFormLoadResult method7(String data, boolean success) {
		ExtDirectFormLoadResult edflr = new ExtDirectFormLoadResult();
		edflr.setData(data);
		edflr.setSuccess(success);
		return edflr;
	}

}
