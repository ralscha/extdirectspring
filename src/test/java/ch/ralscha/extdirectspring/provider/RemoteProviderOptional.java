/**
 * Copyright 2010-2014 Ralph Schaer <ralphschaer@gmail.com>
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

import static org.fest.assertions.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

import ch.ralscha.extdirectspring.annotation.ExtDirectMethod;
import ch.ralscha.extdirectspring.annotation.ExtDirectMethodType;

@Service
public class RemoteProviderOptional {

	@ExtDirectMethod(group = "optional")
	public String method1(Optional<Integer> i, Optional<BigDecimal> bd, String s) {
		return String.format("method1() called-%d-%.3f-%s", i.orElse(-1),
				bd.orElse(new BigDecimal("3.141")), s);
	}

	@ExtDirectMethod(group = "optional")
	public Optional<String> method2(Optional<String> str) {
		if (str.isPresent()) {
			return Optional.of(str.get());
		}
		return Optional.of("default");
	}

	@ExtDirectMethod(group = "optional")
	public String method4(Optional<Integer> id, Optional<String> dummy,
			@RequestHeader Optional<String> header) {
		return id.get() + ";" + dummy.get() + ";" + header.get();
	}

	@ExtDirectMethod(group = "optional")
	public Optional<String> method5(Optional<Integer> id,
			@RequestHeader("anotherName") Optional<String> header) {
		return Optional.of(id.get() + ";" + header.get());
	}

	@ExtDirectMethod(group = "optional")
	public String method6(
			@RequestHeader(value = "anotherName", defaultValue = "default") Optional<String> header) {
		return header.orElse("another default");
	}

	@ExtDirectMethod(group = "optional", synchronizeOnSession = true)
	public String method7(
			@RequestHeader(defaultValue = "default", required = false) Optional<String> header) {
		return header.orElse("this is the default");
	}

	@ExtDirectMethod(group = "optional", synchronizeOnSession = true)
	public String method8(
			@RequestHeader(defaultValue = "default1", required = false) Optional<String> header1,
			final Optional<Integer> id, @RequestHeader(defaultValue = "default2",
					required = false) Optional<String> header2, @RequestHeader(
					value = "last") Optional<String> header3) {
		return id.get() + ";" + header1.get() + ";" + header2.get() + ";" + header3.get();
	}

	@ExtDirectMethod(group = "optional", synchronizeOnSession = true)
	public String method9(@RequestHeader Optional<Integer> intHeader,
			@RequestHeader Optional<Boolean> booleanHeader) {
		return intHeader.get() + ";" + booleanHeader.get();
	}

	@ExtDirectMethod(group = "optional")
	public String method10(Optional<String> name, Optional<List<String>> strings,
			Optional<Integer> id) {
		StringBuilder sb = new StringBuilder();
		if (strings.isPresent()) {
			for (String str : strings.get()) {
				sb.append(str);
				sb.append("-");
			}
		}
		return name.get() + ";" + sb.toString() + ";" + id.get();
	}

	@ExtDirectMethod(group = "optional")
	public String method11(Optional<String> name, Optional<Set<Integer>> ids,
			Optional<Integer> id) {
		StringBuilder sb = new StringBuilder();
		if (ids.isPresent()) {
			SortedSet<Integer> sorted = new TreeSet<Integer>(ids.get());
			for (int i : sorted) {
				sb.append(i);
				sb.append("+");
			}
		}
		return name.get() + ";" + sb.toString() + ";" + id.get();
	}

	@ExtDirectMethod(group = "optional")
	public String method12(Optional<String> name, Optional<List<String>> strings,
			Optional<Integer> id) {
		StringBuilder sb = new StringBuilder();
		if (strings.isPresent()) {
			for (String str : strings.get()) {
				sb.append(str);
				sb.append("-");
			}
		}
		return name.get() + ";" + sb.toString() + ";" + id.get();
	}

	@ExtDirectMethod(group = "optional")
	public String method13(Optional<String> name, Optional<Integer> id, int... ids) {
		StringBuilder sb = new StringBuilder();
		if (ids != null) {
			for (int i : ids) {
				sb.append(i);
				sb.append("+");
			}
		}
		return name.get() + ";" + sb.toString() + ";" + id.get();
	}

	@ExtDirectMethod(group = "optional")
	public String method16(@CookieValue Optional<Integer> intCookie,
			@CookieValue Optional<Boolean> booleanCookie) {
		return intCookie.orElse(-1) + ";" + booleanCookie.orElse(false);
	}

	@ExtDirectMethod(group = "optional")
	public String method17(@CookieValue(required = false,
			defaultValue = "theDefaultValue") Optional<String> stringCookie) {
		return stringCookie.orElse("anotherDefault");
	}

	@ExtDirectMethod(group = "optional")
	public String method18(@CookieValue(value = "nameOfTheCookie") Optional<String> aStr) {
		return aStr.orElse("default");
	}

	@ExtDirectMethod(group = "optional")
	public String method19(@CookieValue Optional<String> stringCookie) {
		return stringCookie.orElse(null);
	}

	@ExtDirectMethod(value = ExtDirectMethodType.POLL, event = "opoll1",
			group = "optional")
	public Optional<String> opoll1(Locale locale,
			@RequestParam(value = "id") Optional<Integer> id) {
		assertThat(locale).isEqualTo(Locale.ENGLISH);
		return Optional.of("Result: " + id.get());
	}

	@ExtDirectMethod(value = ExtDirectMethodType.POLL, event = "opoll2",
			synchronizeOnSession = true, group = "optional")
	public int opoll2(@RequestParam(value = "id") Optional<Integer> id,
			HttpServletRequest request) {
		assertThat(request).isNotNull();
		return id.orElse(2) * 2;
	}

	@ExtDirectMethod(value = ExtDirectMethodType.POLL, event = "opoll3",
			group = "optional")
	public Optional<Integer> opoll3(@RequestParam(value = "id") Optional<Integer> id,
			Optional<String> dummy) {
		assertThat(dummy.isPresent()).isFalse();
		if (id.isPresent()) {
			return Optional.of(id.get() * 2);
		}
		return Optional.empty();
	}

	@ExtDirectMethod(value = ExtDirectMethodType.POLL, group = "optional")
	public Optional<String> opoll4(@RequestParam(value = "id") Optional<Integer> id,
			Optional<String> dummy, @RequestHeader Optional<String> header) {
		return Optional.of(id.orElse(100) + ";" + dummy.orElse("dummy") + ";"
				+ header.orElse("header"));
	}

	@ExtDirectMethod(value = ExtDirectMethodType.POLL, group = "optional")
	public Optional<String> opoll5(
			@RequestParam(value = "id", required = false) Optional<Integer> id,
			Optional<String> dummy, @CookieValue Optional<String> cookie) {
		return Optional.of(id.orElse(23) + ";" + dummy.orElse("dummy") + ";"
				+ cookie.orElse("cookie"));
	}

}
