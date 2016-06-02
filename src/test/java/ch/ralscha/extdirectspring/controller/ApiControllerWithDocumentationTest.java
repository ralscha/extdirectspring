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
import static org.assertj.core.data.MapEntry.entry;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.util.Assert;
import org.springframework.web.context.WebApplicationContext;

import ch.ralscha.extdirectspring.bean.api.ActionDoc;
import ch.ralscha.extdirectspring.util.ApiCache;

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration("classpath:/testApplicationContext.xml")
public class ApiControllerWithDocumentationTest {
	@Autowired
	private WebApplicationContext wac;

	private MockMvc mockMvc;

	@Autowired
	private ConfigurationService configurationService;

	@Autowired
	private ApiCache apiCache;

	@Before
	public void setupApiController() throws Exception {
		this.apiCache.clear();

		Configuration config = new Configuration();
		config.setTimeout(15000);
		config.setEnableBuffer(Boolean.FALSE);
		config.setMaxRetries(5);
		config.setStreamResponse(true);
		ReflectionTestUtils.setField(this.configurationService, "configuration", config);
		this.configurationService.afterPropertiesSet();

		this.mockMvc = MockMvcBuilders.webAppContextSetup(this.wac).build();
	}

	/**
	 * to test the following need to activate Feature 'ALLOW_COMMENTS' for jackson parser
	 * <p>
	 * typical error is com.fasterxml.jackson.core.JsonParseException: Unexpected
	 * character ('/' (code 47)): maybe a (non-standard) comment?
	 *
	 * @throws Exception
	 */
	@Test
	public void testDoc1() throws Exception {
		ActionDoc doc = callApi("method1");

		assertThat(doc.isDeprecated()).isTrue();
		assertThat(doc.getMethodComment())
				.isEqualTo("this method is used to test the documentation generation");
		assertThat(doc.getAuthor()).isEqualTo("dbs");
		assertThat(doc.getVersion()).isEqualTo("0.1");
		assertThat(doc.getParameters()).hasSize(5);
		assertThat(doc.getParameters()).contains(entry("a", "property a integer"),
				entry("b", "property b string"), entry("c", "property c string"),
				entry("d", "property d boolean"), entry("e", "array of integers"));
		assertThat(doc.getReturnMethod()).hasSize(2);
		assertThat(doc.getReturnMethod()).contains(
				entry("errors", "list of failed fields"),
				entry("success", "true for success, false otherwise"));
	}

	@Test
	public void testDoc2() throws Exception {
		ActionDoc doc = callApi("method2");

		assertThat(doc.isDeprecated()).isFalse();
		assertThat(doc.getMethodComment()).isEqualTo("method two doc");
		assertThat(doc.getAuthor()).isEmpty();
		assertThat(doc.getVersion()).isEqualTo("1.0");
		assertThat(doc.getParameters()).isEmpty();
		assertThat(doc.getReturnMethod()).isEmpty();
	}

	@Test
	public void testDoc3() throws Exception {
		ActionDoc doc = callApi("method3");

		assertThat(doc.isDeprecated()).isFalse();
		assertThat(doc.getMethodComment()).isEqualTo("method three doc");
		assertThat(doc.getAuthor()).isEqualTo("dbs");
		assertThat(doc.getVersion()).isEqualTo("1.0");
		assertThat(doc.getParameters()).isEmpty();
		assertThat(doc.getReturnMethod()).isEmpty();
	}

	@Test
	public void testDoc4() throws Exception {
		ActionDoc doc = callApi("method4");

		assertThat(doc.isDeprecated()).isFalse();
		assertThat(doc.getMethodComment()).isEqualTo("method four doc");
		assertThat(doc.getAuthor()).isEqualTo("sr");
		assertThat(doc.getVersion()).isEqualTo("0.4");
		assertThat(doc.getParameters()).isEmpty();
		assertThat(doc.getReturnMethod()).isEmpty();
	}

	@Test
	public void testDoc5() throws Exception {
		ActionDoc doc = callApi("method5");

		assertThat(doc.isDeprecated()).isTrue();
		assertThat(doc.getMethodComment()).isEqualTo("method five doc");
		assertThat(doc.getAuthor()).isEqualTo("dbs");
		assertThat(doc.getVersion()).isEqualTo("0.5");
		assertThat(doc.getParameters()).isEmpty();
		assertThat(doc.getReturnMethod()).isEmpty();
	}

	@Test
	public void testDoc6() throws Exception {
		ActionDoc doc = callApi("method6");

		assertThat(doc.isDeprecated()).isFalse();
		assertThat(doc.getMethodComment()).isEqualTo("method six doc");
		assertThat(doc.getAuthor()).isEqualTo("sr");
		assertThat(doc.getVersion()).isEqualTo("0.6");
		assertThat(doc.getParameters()).isEmpty();
		assertThat(doc.getReturnMethod()).isEmpty();
	}

	@Test
	public void testDoc7() throws Exception {
		ActionDoc doc = callApi("method7");

		assertThat(doc.isDeprecated()).isTrue();
		assertThat(doc.getMethodComment()).isEqualTo("method seven doc");
		assertThat(doc.getAuthor()).isEqualTo("sr");
		assertThat(doc.getVersion()).isEqualTo("0.7");
		assertThat(doc.getParameters()).isEmpty();
		assertThat(doc.getReturnMethod()).hasSize(1);
		assertThat(doc.getReturnMethod()).contains(entry("p1", "p1 desc"));
	}

	@Test
	public void testDoc8() throws Exception {
		ActionDoc doc = callApi("method8");

		assertThat(doc.isDeprecated()).isFalse();
		assertThat(doc.getMethodComment()).isEqualTo("method eight doc");
		assertThat(doc.getAuthor()).isEqualTo("sr");
		assertThat(doc.getVersion()).isEqualTo("0.8");
		assertThat(doc.getParameters()).isEmpty();
		assertThat(doc.getReturnMethod()).hasSize(2);
		assertThat(doc.getReturnMethod()).contains(entry("p1", "p1 desc"),
				entry("p2", "p2 desc"));
	}

	@Test
	public void testDoc9() throws Exception {
		ActionDoc doc = callApi("method9");

		assertThat(doc.isDeprecated()).isFalse();
		assertThat(doc.getMethodComment()).isEqualTo("method nine doc");
		assertThat(doc.getAuthor()).isEqualTo("dbs");
		assertThat(doc.getVersion()).isEqualTo("0.9");
		assertThat(doc.getParameters()).isEmpty();
		assertThat(doc.getReturnMethod()).isEmpty();
	}

	@Test
	public void testDoc10() throws Exception {
		ActionDoc doc = callApi("method10");

		assertThat(doc.isDeprecated()).isFalse();
		assertThat(doc.getMethodComment()).isEqualTo("method ten doc");
		assertThat(doc.getAuthor()).isEqualTo("sr");
		assertThat(doc.getVersion()).isEqualTo("1.0");
		assertThat(doc.getParameters()).hasSize(1);
		assertThat(doc.getParameters()).contains(entry("a", "a desc"));
		assertThat(doc.getReturnMethod()).hasSize(2);
		assertThat(doc.getReturnMethod()).contains(entry("p1", "p1 desc"),
				entry("p2", "p2 desc"));

	}

	@Test
	public void testDoc11() throws Exception {
		ActionDoc doc = callApi("method11");

		assertThat(doc.isDeprecated()).isFalse();
		assertThat(doc.getMethodComment()).isEqualTo("method eleven doc");
		assertThat(doc.getAuthor()).isEmpty();
		assertThat(doc.getVersion()).isEqualTo("1.0");
		assertThat(doc.getParameters()).hasSize(2);
		assertThat(doc.getParameters()).contains(entry("a", "a desc"),
				entry("b", "b desc"));
		assertThat(doc.getReturnMethod()).isEmpty();
	}

	@Test
	public void testDoc12() throws Exception {
		ActionDoc doc = callApi("method12");

		assertThat(doc.isDeprecated()).isFalse();
		assertThat(doc.getMethodComment()).isEqualTo("method twelve doc");
		assertThat(doc.getAuthor()).isEqualTo("sr");
		assertThat(doc.getVersion()).isEqualTo("1.0");
		assertThat(doc.getParameters()).isEmpty();
		assertThat(doc.getReturnMethod()).isEmpty();
	}

	public void testRequestToApiDebugDoesNotContainDocs() throws Exception {
		doRequestWithoutDocs("/api-debug.js");
	}

	public void testRequestToApiDoesNotContainDocs() throws Exception {
		doRequestWithoutDocs("/api.js");
	}

	private void doRequestWithoutDocs(String url) throws Exception {
		ApiRequestParams params = ApiRequestParams.builder().apiNs("Ext.ns")
				.actionNs("actionns").group("doc")
				.configuration(this.configurationService.getConfiguration()).build();
		MockHttpServletRequestBuilder request = get(url).accept(MediaType.ALL)
				.characterEncoding("UTF-8");
		request.param("apiNs", params.getApiNs());
		request.param("actionNs", params.getActionNs());
		request.param("group", params.getGroup());

		MvcResult result = this.mockMvc.perform(request).andExpect(status().isOk())
				.andExpect(content().contentType("application/javascript")).andReturn();

		ApiControllerTest.compare(result, ApiControllerTest.groupApisWithDoc("actionns"),
				params);
		Assert.doesNotContain("/**", result.getResponse().getContentAsString(),
				"generation of api.js should not contain method documentation");
	}

	private ActionDoc callApi(String method) throws Exception {
		ApiRequestParams params = ApiRequestParams.builder().apiNs("Ext.ns")
				.actionNs("actionns").group("doc")
				.configuration(this.configurationService.getConfiguration()).build();
		MockHttpServletRequestBuilder request = get("/api-debug-doc.js")
				.accept(MediaType.ALL).characterEncoding("UTF-8");
		request.param("apiNs", params.getApiNs());
		request.param("actionNs", params.getActionNs());
		request.param("group", params.getGroup());

		MvcResult result = this.mockMvc.perform(request).andExpect(status().isOk())
				.andExpect(content().contentType("application/javascript")).andReturn();

		ApiControllerTest.compare(result, ApiControllerTest.groupApisWithDoc("actionns"),
				params);
		ActionDoc doc = getCommentForMethod(result.getResponse().getContentAsString(),
				method);
		return doc;
	}

	private final static Pattern COMMENT_PATTERN = Pattern.compile("/\\*\\*([^/]*)\\*/",
			Pattern.MULTILINE);

	private static ActionDoc getCommentForMethod(String apiString, String method) {
		ActionDoc doc = new ActionDoc(method, Collections.<String>emptyList());

		String block = findCommentBlock(apiString, method);
		if (block != null) {
			doc.setDeprecated(block.contains("* @deprecated"));

			int p = block.indexOf("@author:");
			if (p != -1) {
				doc.setAuthor(block.substring(p + 9, block.indexOf('\n', p)));
			}

			p = block.indexOf("@version:");
			if (p != -1) {
				doc.setVersion(block.substring(p + 10, block.indexOf('\n', p)));
			}

			p = block.indexOf(method);
			if (p != -1) {
				doc.setMethodComment(
						block.substring(p + method.length() + 2, block.indexOf('\n', p)));
			}

			Map<String, String> params = new HashMap<String, String>();
			p = block.indexOf("@param:");
			while (p != -1) {
				int p2 = block.indexOf('\n', p);
				String pc = block.substring(p + 8, p2);
				int c1 = pc.indexOf('[');
				int c2 = pc.indexOf(']');
				params.put(pc.substring(c1 + 1, c2), pc.substring(c2 + 2));
				p = block.indexOf("@param:", p2);
			}
			doc.setParameters(params);

			Map<String, String> returns = new HashMap<String, String>();
			p = block.indexOf("@return");
			if (p != -1) {
				p = block.indexOf('[', p);
				while (p != -1) {
					int p2 = block.indexOf(']', p);
					returns.put(block.substring(p + 1, p2),
							block.substring(p2 + 2, block.indexOf('\n', p2)));
					p = block.indexOf('[', p2);
				}
			}

			doc.setReturnMethod(returns);
		}

		return doc;
	}

	private static String findCommentBlock(String apiString, String method) {
		Matcher m = COMMENT_PATTERN.matcher(apiString);
		while (m.find()) {
			String block = m.group(1);
			if (block.contains(method + ":")) {
				return block;
			}
		}
		return null;
	}
}
