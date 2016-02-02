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
package ch.ralscha.extdirectspring.view;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Map;

import org.assertj.core.data.MapEntry;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import ch.ralscha.extdirectspring.controller.ControllerUtil;

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration("classpath:/testApplicationContextView.xml")
public class TreeLoadMethodTest extends BaseViewTest {

	@Autowired
	private WebApplicationContext wac;

	private MockMvc mockMvc;

	@Before
	public void setupMockMvc() throws Exception {
		this.mockMvc = MockMvcBuilders.webAppContextSetup(this.wac).build();
	}

	@Test
	public void testNoView() {
		callMethod("treeLoadMethodService", "noView", noView());

	}

	@Test
	public void testAnnotationSummaryView() {
		callMethod("treeLoadMethodService", "annotationSummaryView", summaryView());
	}

	@Test
	public void testAnnotationDetailView() {
		callMethod("treeLoadMethodService", "annotationDetailView", detailView());
	}

	@Test
	public void testMajSummaryView() {
		callMethod("treeLoadMethodService", "majSummaryView", summaryView());
	}

	@Test
	public void testMajDetailView() {
		callMethod("treeLoadMethodService", "majDetailView", detailView());
	}

	@Test
	public void testOverrideMajDetailView() {
		callMethod("treeLoadMethodService", "overrideMajDetailView", detailView());
	}

	@Test
	public void testOverrideMajNoView() {
		callMethod("treeLoadMethodService", "overrideMajNoView", noView());
	}

	@SuppressWarnings("unchecked")
	private void callMethod(String bean, String method, MapEntry... expectedEntries) {
		List<Map<String, Object>> result = (List<Map<String, Object>>) ControllerUtil
				.sendAndReceiveObject(this.mockMvc, bean, method);
		assertThat(result).hasSize(2);
		for (int i = 1; i <= result.size(); i++) {
			Map<String, Object> model = result.get(i - 1);
			assertThat(model).hasSize(expectedEntries.length);

			for (MapEntry<String, Object> entry : expectedEntries) {
				if (entry.key.equals("id")) {
					assertThat(model).contains(MapEntry.entry("id", i));
				}
				else {
					assertThat(model)
							.contains(MapEntry.entry(entry.key, "" + entry.value + i));
				}
			}
		}
	}
}
