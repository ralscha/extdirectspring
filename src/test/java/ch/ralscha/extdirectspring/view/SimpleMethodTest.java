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

import java.util.ArrayList;
import java.util.Arrays;
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

import ch.ralscha.extdirectspring.bean.BeanMethod;
import ch.ralscha.extdirectspring.controller.ControllerUtil;

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration("classpath:/testApplicationContextView.xml")
public class SimpleMethodTest extends BaseViewTest {

	@Autowired
	private WebApplicationContext wac;

	private MockMvc mockMvc;

	@Before
	public void setupMockMvc() throws Exception {
		this.mockMvc = MockMvcBuilders.webAppContextSetup(this.wac).build();
	}

	@Test
	public void testNoView() {
		callMethod("simpleMethodService", "noView", noView());
	}

	@Test
	public void testAnnotationSummaryView() {
		callMethod("simpleMethodService", "annotationSummaryView", summaryView());
	}

	@Test
	public void testAnnotationDetailView() {
		callMethod("simpleMethodService", "annotationDetailView", detailView());
	}

	@Test
	public void testMajSummaryView() {
		callMethod("simpleMethodService", "majSummaryView", summaryView());
	}

	@Test
	public void testMajDetailView() {
		callMethod("simpleMethodService", "majDetailView", detailView());
	}

	@Test
	public void testOverrideMajDetailView() {
		callMethod("simpleMethodService", "overrideMajDetailView", detailView());
	}

	@Test
	public void testOverrideMajNoView() {
		callMethod("simpleMethodService", "overrideMajNoView", noView());
	}

	@Test
	public void testSubclassSummaryView() {
		callMethod("simpleMethodService", "subclassSummaryView", summaryView());
	}

	@Test
	public void testSubclassDetailView() {
		callMethod("simpleMethodService", "subclassDetailView", detailView());
	}

	@Test
	public void testOverrideSubclassDetailView() {
		callMethod("simpleMethodService", "overrideSubclassDetailView", detailView());
	}

	@Test
	public void testOverrideSubclassNoView() {
		callMethod("simpleMethodService", "overrideSubclassNoView", noView());
	}

	@Test
	public void testMultiple1() {
		List<BeanMethod> bms = new ArrayList<BeanMethod>();
		bms.add(new BeanMethod("simpleMethodService", "noView"));
		List<Map<String, Object>> results = ControllerUtil
				.sendAndReceiveMultiple(this.mockMvc, bms);
		assertThat(results).hasSize(1);
		Map<String, Object> result = results.get(0);
		assertThat(result).hasSize(noView().length);
		assertThat(result).contains(noView());
	}

	@Test
	public void testMultiple2() {
		List<BeanMethod> bms = new ArrayList<BeanMethod>();
		bms.add(new BeanMethod("simpleMethodService", "noView"));
		bms.add(new BeanMethod("simpleMethodService", "annotationSummaryView"));
		bms.add(new BeanMethod("simpleMethodService", "annotationDetailView"));
		List<Map<String, Object>> results = ControllerUtil
				.sendAndReceiveMultiple(this.mockMvc, bms);
		assertThat(results).hasSize(3);

		int ix = 0;
		for (MapEntry[] entries : Arrays.asList(noView(), summaryView(), detailView())) {
			Map<String, Object> result = results.get(ix++);
			assertThat(result).hasSize(entries.length);
			assertThat(result).contains(entries);
		}
	}

	@Test
	public void testMultiple3() {
		List<BeanMethod> bms = new ArrayList<BeanMethod>();
		bms.add(new BeanMethod("simpleMethodService", "subclassSummaryView"));
		bms.add(new BeanMethod("simpleMethodService", "subclassDetailView"));
		bms.add(new BeanMethod("simpleMethodService", "noView"));
		bms.add(new BeanMethod("simpleMethodService", "overrideSubclassDetailView"));
		bms.add(new BeanMethod("simpleMethodService", "overrideSubclassNoView"));
		List<Map<String, Object>> results = ControllerUtil
				.sendAndReceiveMultiple(this.mockMvc, bms);
		assertThat(results).hasSize(5);

		int ix = 0;
		for (MapEntry[] entries : Arrays.asList(summaryView(), detailView(), noView(),
				detailView(), noView())) {
			Map<String, Object> result = results.get(ix++);
			assertThat(result).hasSize(entries.length);
			assertThat(result).contains(entries);
		}
	}

	private void callMethod(String bean, String method, MapEntry... expectedEntries) {
		Map<String, Object> result = ControllerUtil.sendAndReceiveMap(this.mockMvc, bean,
				method);
		assertThat(result).hasSize(expectedEntries.length);
		assertThat(result).contains(expectedEntries);
	}
}
