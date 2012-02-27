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

import java.security.Principal;
import java.util.ArrayList;
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
@SuppressWarnings("unused")
public class RemoteProviderTreeLoad {

	public static class Node {
		public String id;
		public String text;
		public boolean leaf;
	}

	@ExtDirectMethod(value = ExtDirectMethodType.TREE_LOAD, group = "group1")
	public List<Node> method1(@RequestParam("node") String node) {
		return createTreeList(node);
	}

	@ExtDirectMethod(value = ExtDirectMethodType.TREE_LOAD)
	public List<Node> method2(@RequestParam("node") String node,
			@RequestParam(defaultValue = "defaultValue") String foo, @DateTimeFormat(iso = ISO.DATE) LocalDate today) {
		assertThat(foo).isEqualTo("foo");
		assertThat(today).isNotNull();
		assertThat(today).isEqualTo(new LocalDate());
		return createTreeList(node);
	}

	@ExtDirectMethod(value = ExtDirectMethodType.TREE_LOAD, group = "group3")
	public List<Node> method3(@RequestParam("node") String node, HttpServletResponse response,
			HttpServletRequest request, @RequestParam(defaultValue = "defaultValue") String foo, HttpSession session,
			Locale locale, Principal principal) {
		assertThat(foo).isEqualTo("defaultValue");
		assertThat(response).isNotNull();
		assertThat(request).isNotNull();
		assertThat(session).isNotNull();
		assertThat(locale).isEqualTo(Locale.ENGLISH);

		return createTreeList(node);
	}

	private List<Node> createTreeList(String id) {
		List<Node> result = new ArrayList<Node>();
		if (id.equals("root")) {
			for (int i = 1; i <= 5; ++i) {
				Node node = new Node();
				node.id = "n" + i;
				node.text = "Node " + i;
				node.leaf = false;
				result.add(node);
			}
		} else if (id.length() == 2) {
			String num = id.substring(1);
			for (int i = 1; i <= 5; ++i) {
				Node node = new Node();
				node.id = "id" + i;
				node.text = "Node " + num + "." + i;
				node.leaf = true;
				result.add(node);
			}
		}
		return result;
	}

}
