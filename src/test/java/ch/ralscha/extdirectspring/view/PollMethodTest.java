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
import static org.junit.Assert.fail;

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

import ch.ralscha.extdirectspring.bean.ExtDirectPollResponse;
import ch.ralscha.extdirectspring.controller.ControllerUtil;

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration("classpath:/testApplicationContextView.xml")
public class PollMethodTest extends BaseViewTest {

	@Autowired
	private WebApplicationContext wac;

	private MockMvc mockMvc;

	@Before
	public void setupMockMvc() throws Exception {
		this.mockMvc = MockMvcBuilders.webAppContextSetup(this.wac).build();
	}

	@Test
	public void testNoView() {
		callMethod("pollMethodService", "noView", noView());

	}

	@Test
	public void testAnnotationSummaryView() {
		callMethod("pollMethodService", "annotationSummaryView", summaryView());
	}

	@Test
	public void testAnnotationDetailView() {
		callMethod("pollMethodService", "annotationDetailView", detailView());
	}

	@Test
	public void testMajSummaryView() {
		callMethod("pollMethodService", "majSummaryView", summaryView());
	}

	@Test
	public void testMajDetailView() {
		callMethod("pollMethodService", "majDetailView", detailView());
	}

	@Test
	public void testOverrideMajDetailView() {
		callMethod("pollMethodService", "overrideMajDetailView", detailView());
	}

	@Test
	public void testOverrideMajNoView() {
		callMethod("pollMethodService", "overrideMajNoView", noView());
	}

	@Test
	public void testSubclassSummaryView() {
		callMethod("pollMethodService", "subclassSummaryView", summaryView());
	}

	@Test
	public void testSubclassDetailView() {
		callMethod("pollMethodService", "subclassDetailView", detailView());
	}

	@Test
	public void testOverrideSubclassDetailView() {
		callMethod("pollMethodService", "overrideSubclassDetailView", detailView());
	}

	@Test
	public void testOverrideSubclassNoView() {
		callMethod("pollMethodService", "overrideSubclassNoView", noView());
	}

	@SuppressWarnings("unchecked")
	private void callMethod(String bean, String method, MapEntry... expectedEntries) {
		ExtDirectPollResponse response;
		try {
			response = ControllerUtil.performPollRequest(this.mockMvc, bean, method,
					"theEvent", null, null);
			Map<String, Object> data = (Map<String, Object>) response.getData();
			assertThat(data).hasSize(expectedEntries.length);
			assertThat(data).contains(expectedEntries);
		}
		catch (Exception e) {
			fail("call poll method: " + e.getMessage());
		}

	}
}
