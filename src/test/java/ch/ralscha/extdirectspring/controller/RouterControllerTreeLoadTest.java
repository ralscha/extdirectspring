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
package ch.ralscha.extdirectspring.controller;

import static org.fest.assertions.Assertions.assertThat;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.codehaus.jackson.type.TypeReference;
import org.joda.time.LocalDate;
import org.joda.time.format.ISODateTimeFormat;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import ch.ralscha.extdirectspring.provider.RemoteProviderTreeLoad.Node;

/**
 * Tests for {@link RouterController}.
 * 
 * @author Ralph Schaer
 */
@SuppressWarnings("all")
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:/testApplicationContext.xml")
public class RouterControllerTreeLoadTest {

	@Autowired
	private RouterController controller;

	@Test
	public void testNoAdditionalParameters() throws IOException {

		Map<String, Object> requestParameters = new LinkedHashMap<String, Object>();
		requestParameters.put("node", "root");

		List<Node> nodes = (List<Node>) ControllerUtil.sendAndReceive(
				controller, "remoteProviderTreeLoad", "method1",
				requestParameters, new TypeReference<List<Node>>() {/*
																	 * nothing
																	 * here
																	 */
				});

		assertThat(nodes).hasSize(5).containsSequence(
				new Node("n1", "Node 1", false),
				new Node("n2", "Node 2", false),
				new Node("n3", "Node 3", false),
				new Node("n4", "Node 4", false),
				new Node("n5", "Node 5", false));

		requestParameters = new LinkedHashMap<String, Object>();
		requestParameters.put("node", "n1");

		nodes = (List<Node>) ControllerUtil.sendAndReceive(controller,
				"remoteProviderTreeLoad", "method1", requestParameters,
				new TypeReference<List<Node>>() {/* nothing here */
				});

		assertThat(nodes).hasSize(5).containsSequence(
				new Node("id1", "Node 1.1", true),
				new Node("id2", "Node 1.2", true),
				new Node("id3", "Node 1.3", true),
				new Node("id4", "Node 1.4", true),
				new Node("id5", "Node 1.5", true));
	}

	@Test
	public void testAdditionalParameters() throws IOException {

		Map<String, Object> requestParameters = new LinkedHashMap<String, Object>();
		requestParameters.put("node", "root");
		requestParameters.put("foo", "foo");
		requestParameters.put("today",
				ISODateTimeFormat.date().print(new LocalDate()));

		List<Node> nodes = (List<Node>) ControllerUtil.sendAndReceive(
				controller, "remoteProviderTreeLoad", "method2",
				requestParameters, new TypeReference<List<Node>>() {/*
																	 * nothing
																	 * here
																	 */
				});

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

		nodes = (List<Node>) ControllerUtil.sendAndReceive(controller,
				"remoteProviderTreeLoad", "method2", requestParameters,
				new TypeReference<List<Node>>() {/* nothing here */
				});

		appendix = ":defaultValue;" + new LocalDate().plusDays(10).toString();
		assertThat(nodes).hasSize(5).containsSequence(
				new Node("n1", "Node 1" + appendix, false),
				new Node("n2", "Node 2" + appendix, false),
				new Node("n3", "Node 3" + appendix, false),
				new Node("n4", "Node 4" + appendix, false),
				new Node("n5", "Node 5" + appendix, false));
	}

	@Test
	public void testSupportedParameters() throws IOException {
		Map<String, Object> requestParameters = new LinkedHashMap<String, Object>();
		requestParameters.put("node", "root");

		List<Node> nodes = (List<Node>) ControllerUtil.sendAndReceive(
				controller, "remoteProviderTreeLoad", "method3",
				requestParameters, new TypeReference<List<Node>>() {/*
																	 * nothing
																	 * here
																	 */
				});

		String appendix = ":defaultValue;true;true;true;en";

		assertThat(nodes).hasSize(5).containsSequence(
				new Node("n1", "Node 1" + appendix, false),
				new Node("n2", "Node 2" + appendix, false),
				new Node("n3", "Node 3" + appendix, false),
				new Node("n4", "Node 4" + appendix, false),
				new Node("n5", "Node 5" + appendix, false));

		requestParameters = new LinkedHashMap<String, Object>();
		requestParameters.put("node", "n2");
		requestParameters.put("foo", "f");

		nodes = (List<Node>) ControllerUtil.sendAndReceive(controller,
				"remoteProviderTreeLoad", "method3", requestParameters,
				new TypeReference<List<Node>>() {/* nothing here */
				});

		appendix = ":f;true;true;true;en";

		assertThat(nodes).hasSize(5).containsSequence(
				new Node("id1", "Node 2.1" + appendix, true),
				new Node("id2", "Node 2.2" + appendix, true),
				new Node("id3", "Node 2.3" + appendix, true),
				new Node("id4", "Node 2.4" + appendix, true),
				new Node("id5", "Node 2.5" + appendix, true));
	}

	@Test
	public void testWithHeader() throws IOException {
		MockHttpServletRequest request = new MockHttpServletRequest();
		request.addHeader("aHeader", "true");

		Map<String, Object> requestParameters = new LinkedHashMap<String, Object>();
		requestParameters.put("node", "root");

		List<Node> nodes = (List<Node>) ControllerUtil.sendAndReceive(
				controller, request, "remoteProviderTreeLoad", "method4",
				requestParameters, new TypeReference<List<Node>>() {/*
																	 * nothing
																	 * here
																	 */
				});

		String appendix = ":true;true;true";

		assertThat(nodes).hasSize(5).containsSequence(
				new Node("n1", "Node 1" + appendix, false),
				new Node("n2", "Node 2" + appendix, false),
				new Node("n3", "Node 3" + appendix, false),
				new Node("n4", "Node 4" + appendix, false),
				new Node("n5", "Node 5" + appendix, false));

		request = new MockHttpServletRequest();
		request.addHeader("aHeader", "false");

		nodes = (List<Node>) ControllerUtil.sendAndReceive(controller, request,
				"remoteProviderTreeLoad", "method4", requestParameters,
				new TypeReference<List<Node>>() {/* nothing here */
				});

		appendix = ":false;true;true";
		assertThat(nodes).hasSize(5).containsSequence(
				new Node("n1", "Node 1" + appendix, false),
				new Node("n2", "Node 2" + appendix, false),
				new Node("n3", "Node 3" + appendix, false),
				new Node("n4", "Node 4" + appendix, false),
				new Node("n5", "Node 5" + appendix, false));

	}

}
