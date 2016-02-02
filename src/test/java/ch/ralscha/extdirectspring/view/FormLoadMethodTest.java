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
public class FormLoadMethodTest extends BaseViewTest {

	@Autowired
	private WebApplicationContext wac;

	private MockMvc mockMvc;

	@Before
	public void setupMockMvc() throws Exception {
		this.mockMvc = MockMvcBuilders.webAppContextSetup(this.wac).build();
	}

	@Test
	public void testNoView() {
		callMethod("formLoadMethodService", "noView", noView());

	}

	@Test
	public void testAnnotationSummaryView() {
		callMethod("formLoadMethodService", "annotationSummaryView", summaryView());
	}

	@Test
	public void testAnnotationDetailView() {
		callMethod("formLoadMethodService", "annotationDetailView", detailView());
	}

	@Test
	public void testMajSummaryView() {
		callMethod("formLoadMethodService", "majSummaryView", summaryView());
	}

	@Test
	public void testMajDetailView() {
		callMethod("formLoadMethodService", "majDetailView", detailView());
	}

	@Test
	public void testOverrideMajDetailView() {
		callMethod("formLoadMethodService", "overrideMajDetailView", detailView());
	}

	@Test
	public void testOverrideMajNoView() {
		callMethod("formLoadMethodService", "overrideMajNoView", noView());
	}

	@Test
	public void testSubclassSummaryView() {
		callMethod("formLoadMethodService", "subclassSummaryView", summaryView());
	}

	@Test
	public void testSubclassDetailView() {
		callMethod("formLoadMethodService", "subclassDetailView", detailView());
	}

	@Test
	public void testOverrideSubclassDetailView() {
		callMethod("formLoadMethodService", "overrideSubclassDetailView", detailView());
	}

	@Test
	public void testOverrideSubclassNoView() {
		callMethod("formLoadMethodService", "overrideSubclassNoView", noView());
	}

	@Test
	public void testResultSummaryView() {
		callMethod("formLoadMethodService", "resultSummaryView", summaryView());
	}

	@Test
	public void testResultDetailView() {
		callMethod("formLoadMethodService", "resultDetailView", detailView());
	}

	@Test
	public void testOverrideResultDetailView() {
		callMethod("formLoadMethodService", "overrideResultDetailView", detailView());
	}

	@Test
	public void testOverrideResultNoView() {
		callMethod("formLoadMethodService", "overrideResultNoView", noView());
	}

	@Test
	public void testResultSummaryViewEd() {
		callMethod("formLoadMethodService", "resultSummaryViewEd", summaryView());
	}

	@Test
	public void testResultDetailViewEd() {
		callMethod("formLoadMethodService", "resultDetailViewEd", detailView());
	}

	@Test
	public void testOverrideResultDetailViewEd() {
		callMethod("formLoadMethodService", "overrideResultDetailViewEd", detailView());
	}

	@Test
	public void testOverrideResultNoViewEd() {
		callMethod("formLoadMethodService", "overrideResultNoViewEd", noView());
	}

	@SuppressWarnings("unchecked")
	private void callMethod(String bean, String method, MapEntry... expectedEntries) {
		Map<String, Object> result = ControllerUtil.sendAndReceiveMap(this.mockMvc, bean,
				method);
		assertThat(result).hasSize(2).contains(MapEntry.entry("success", Boolean.TRUE));
		Map<String, Object> data = (Map<String, Object>) result.get("data");
		assertThat(data).hasSize(expectedEntries.length);
		assertThat(data).contains(expectedEntries);
	}
}
