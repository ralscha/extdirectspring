/**
 * Copyright 2010-2018 the original author or authors.
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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import ch.ralscha.extdirectspring.controller.ControllerUtil;

@ExtendWith(SpringExtension.class)
@WebAppConfiguration
@ContextConfiguration("classpath:/testApplicationContextDefaultViewExclusion.xml")
public class DefaultViewExclusionTest {

	@Autowired
	private WebApplicationContext wac;

	private MockMvc mockMvc;

	@BeforeEach
	public void setupMockMvc() throws Exception {
		this.mockMvc = MockMvcBuilders.webAppContextSetup(this.wac).build();
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
	public void testNoView() {
		callMethod("simpleMethodService", "noView", noView());
	}

	@SafeVarargs
	private final void callMethod(String bean, String method,
			MapEntry<String, Object>... expectedEntries) {
		Map<String, Object> result = ControllerUtil.sendAndReceiveMap(this.mockMvc, bean,
				method);
		assertThat(result).hasSize(expectedEntries.length);
		assertThat(result).contains(expectedEntries);
	}

	@SuppressWarnings("unchecked")
	private static MapEntry<String, Object>[] noView() {
		return new MapEntry[] { MapEntry.entry("id", 1),
				MapEntry.entry("firstName", "firstName"),
				MapEntry.entry("lastName", "lastName"), MapEntry.entry("phone", "phone"),
				MapEntry.entry("address", "address"),
				MapEntry.entry("secretKey", "mySecret") };
	}

	@SuppressWarnings("unchecked")
	private static MapEntry<String, Object>[] summaryView() {
		return new MapEntry[] { MapEntry.entry("firstName", "firstName"),
				MapEntry.entry("lastName", "lastName") };
	}

	@SuppressWarnings("unchecked")
	private static MapEntry<String, Object>[] detailView() {
		return new MapEntry[] { MapEntry.entry("firstName", "firstName"),
				MapEntry.entry("lastName", "lastName"), MapEntry.entry("phone", "phone"),
				MapEntry.entry("address", "address") };
	}
}
