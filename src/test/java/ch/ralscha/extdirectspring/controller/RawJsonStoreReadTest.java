/*
 * Copyright the original author or authors.
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

import java.util.List;
import java.util.Map;

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

@ExtendWith(SpringExtension.class)
@WebAppConfiguration
@ContextConfiguration("classpath:/testApplicationContextRawJson.xml")
public class RawJsonStoreReadTest {

	@Autowired
	private WebApplicationContext wac;

	private MockMvc mockMvc;

	@BeforeEach
	public void setupMockMvc() {
		this.mockMvc = MockMvcBuilders.webAppContextSetup(this.wac).build();
	}

	@Test
	public void testRawJsonStoreResults() {
		assertStoreResult("listUsers1", null, true);
		assertStoreResult("listUsers2", 2L, true);
		assertStoreResult("listUsers3", 2L, false);
	}

	@Test
	public void testEdJsonStoreResults() {
		assertStoreResult("listUsers1Ed", null, true);
		assertStoreResult("listUsers2Ed", 2L, true);
		assertStoreResult("listUsers3Ed", 2L, false);
	}

	@SuppressWarnings("unchecked")
	private void assertStoreResult(String method, Long total, boolean success) {
		Map<String, Object> result = ControllerUtil.sendAndReceiveMap(this.mockMvc, "rawJsonController", method);

		if (total == null) {
			assertThat(result).hasSize(2);
		}
		else {
			assertThat(result).hasSize(3);
			assertThat(((Number) result.get("total")).longValue()).isEqualTo(total.longValue());
		}

		assertThat(result).containsEntry("success", success);

		List<Map<String, Object>> records = (List<Map<String, Object>>) result.get("records");
		assertThat(records).hasSize(2);
		assertThat(((Map<String, Object>) records.get(0).get("_id")).get("$oid")).isEqualTo("4cf8e5b8924e23349fb99454");
		assertThat(((Map<String, Object>) records.get(1).get("_id")).get("$oid")).isEqualTo("4cf8e5b8924e2334a0b99454");
	}

}