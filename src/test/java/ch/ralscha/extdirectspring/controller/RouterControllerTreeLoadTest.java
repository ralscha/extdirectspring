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
package ch.ralscha.extdirectspring.controller;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.Cookie;

import org.joda.time.LocalDate;
import org.joda.time.format.ISODateTimeFormat;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.fasterxml.jackson.core.type.TypeReference;

import ch.ralscha.extdirectspring.provider.RemoteProviderTreeLoad.Node;

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration("classpath:/testApplicationContext.xml")
public class RouterControllerTreeLoadTest {

	@Autowired
	private WebApplicationContext wac;

	private MockMvc mockMvc;

	@Before
	public void setupMockMvc() throws Exception {
		this.mockMvc = MockMvcBuilders.webAppContextSetup(this.wac).build();
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testNoAdditionalParameters() {

		Map<String, Object> requestParameters = new LinkedHashMap<String, Object>();
		requestParameters.put("node", "root");

		List<Node> nodes = (List<Node>) ControllerUtil.sendAndReceive(this.mockMvc,
				"remoteProviderTreeLoad", "method1",
				new TypeReference<List<Node>>() {/* nothinghere */
				}, requestParameters);

		assertThat(nodes).hasSize(5).containsSequence(new Node("n1", "Node 1", false),
				new Node("n2", "Node 2", false), new Node("n3", "Node 3", false),
				new Node("n4", "Node 4", false), new Node("n5", "Node 5", false));

		requestParameters = new LinkedHashMap<String, Object>();
		requestParameters.put("node", "n1");

		nodes = (List<Node>) ControllerUtil.sendAndReceive(this.mockMvc,
				"remoteProviderTreeLoad", "method1",
				new TypeReference<List<Node>>() {/* nothinghere */
				}, requestParameters);

		assertThat(nodes).hasSize(5).containsSequence(new Node("id1", "Node 1.1", true),
				new Node("id2", "Node 1.2", true), new Node("id3", "Node 1.3", true),
				new Node("id4", "Node 1.4", true), new Node("id5", "Node 1.5", true));
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testAdditionalParameters() {

		Map<String, Object> requestParameters = new LinkedHashMap<String, Object>();
		requestParameters.put("node", "root");
		requestParameters.put("foo", "foo");
		requestParameters.put("today", ISODateTimeFormat.date().print(new LocalDate()));

		List<Node> nodes = (List<Node>) ControllerUtil.sendAndReceive(this.mockMvc,
				"remoteProviderTreeLoad", "method2",
				new TypeReference<List<Node>>() {/* nothinghere */
				}, requestParameters);

		String appendix = ":foo;" + new LocalDate().toString();
		assertThat(nodes).hasSize(5).containsSequence(
				new Node("n1", "Node 1" + appendix, false),
				new Node("n2", "Node 2" + appendix, false),
				new Node("n3", "Node 3" + appendix, false),
				new Node("n4", "Node 4" + appendix, false),
				new Node("n5", "Node 5" + appendix, false));

		requestParameters = new LinkedHashMap<String, Object>();
		requestParameters.put("node", "root");
		requestParameters.put("today",
				ISODateTimeFormat.date().print(new LocalDate().plusDays(10)));

		nodes = (List<Node>) ControllerUtil.sendAndReceive(this.mockMvc,
				"remoteProviderTreeLoad", "method2",
				new TypeReference<List<Node>>() {/* nothinghere */
				}, requestParameters);

		appendix = ":defaultValue;" + new LocalDate().plusDays(10).toString();
		assertThat(nodes).hasSize(5).containsSequence(
				new Node("n1", "Node 1" + appendix, false),
				new Node("n2", "Node 2" + appendix, false),
				new Node("n3", "Node 3" + appendix, false),
				new Node("n4", "Node 4" + appendix, false),
				new Node("n5", "Node 5" + appendix, false));
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testSupportedParameters() {
		Map<String, Object> requestParameters = new LinkedHashMap<String, Object>();
		requestParameters.put("node", "root");

		List<Cookie> cookies = new ArrayList<Cookie>();
		cookies.add(new Cookie("theCookie", "value"));

		List<Node> nodes = (List<Node>) ControllerUtil.sendAndReceive(this.mockMvc, false,
				null, cookies, null, "remoteProviderTreeLoad", "method3", false,
				new TypeReference<List<Node>>() {/* nothinghere */
				}, requestParameters);

		String appendix = ":defaultValue;value;true;true;true;en";

		assertThat(nodes).hasSize(5).containsSequence(
				new Node("n1", "Node 1" + appendix, false),
				new Node("n2", "Node 2" + appendix, false),
				new Node("n3", "Node 3" + appendix, false),
				new Node("n4", "Node 4" + appendix, false),
				new Node("n5", "Node 5" + appendix, false));

		requestParameters = new LinkedHashMap<String, Object>();
		requestParameters.put("node", "n2");
		requestParameters.put("foo", "f");

		nodes = (List<Node>) ControllerUtil.sendAndReceive(this.mockMvc, false, null,
				cookies, null, "remoteProviderTreeLoad", "method3", false,
				new TypeReference<List<Node>>() {/* nothinghere */
				}, requestParameters);

		appendix = ":f;value;true;true;true;en";

		assertThat(nodes).hasSize(5).containsSequence(
				new Node("id1", "Node 2.1" + appendix, true),
				new Node("id2", "Node 2.2" + appendix, true),
				new Node("id3", "Node 2.3" + appendix, true),
				new Node("id4", "Node 2.4" + appendix, true),
				new Node("id5", "Node 2.5" + appendix, true));
	}

	@Test
	public void testWithHeader() {
		callTreeLoadAndCheckResult("method4");
	}

	@Test
	public void testWithArrayAsReturnType() {
		callTreeLoadAndCheckResult("method5");
	}

	@SuppressWarnings("unchecked")
	private void callTreeLoadAndCheckResult(String method) {
		HttpHeaders headers = new HttpHeaders();
		headers.add("aHeader", "true");

		Map<String, Object> requestParameters = new LinkedHashMap<String, Object>();
		requestParameters.put("node", "root");

		List<Node> nodes = (List<Node>) ControllerUtil.sendAndReceive(this.mockMvc,
				headers, "remoteProviderTreeLoad", method,
				new TypeReference<List<Node>>() {/* nothinghere */
				}, requestParameters);

		String appendix = ":true;true;true";

		assertThat(nodes).hasSize(5).containsSequence(
				new Node("n1", "Node 1" + appendix, false),
				new Node("n2", "Node 2" + appendix, false),
				new Node("n3", "Node 3" + appendix, false),
				new Node("n4", "Node 4" + appendix, false),
				new Node("n5", "Node 5" + appendix, false));

		headers = new HttpHeaders();
		headers.add("aHeader", "false");

		nodes = (List<Node>) ControllerUtil.sendAndReceive(this.mockMvc, headers,
				"remoteProviderTreeLoad", method,
				new TypeReference<List<Node>>() {/* nothinghere */
				}, requestParameters);

		appendix = ":false;true;true";
		assertThat(nodes).hasSize(5).containsSequence(
				new Node("n1", "Node 1" + appendix, false),
				new Node("n2", "Node 2" + appendix, false),
				new Node("n3", "Node 3" + appendix, false),
				new Node("n4", "Node 4" + appendix, false),
				new Node("n5", "Node 5" + appendix, false));
	}

	@Test
	public void testWithSingleObjectAsReturnType() {
		Map<String, Object> requestParameters = new LinkedHashMap<String, Object>();
		requestParameters.put("node", "root");

		Node node = (Node) ControllerUtil.sendAndReceive(this.mockMvc,
				"remoteProviderTreeLoad", "method6", Node.class, requestParameters);

		assertThat(node.id).isEqualTo("n1");
		assertThat(node.text).isEqualTo("Node 1;true;true");
		assertThat(node.leaf).isEqualTo(false);

	}

}
