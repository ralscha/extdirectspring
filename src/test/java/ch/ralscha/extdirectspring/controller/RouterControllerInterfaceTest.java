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

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.junit.jupiter.api.BeforeAll;
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

import com.fasterxml.jackson.core.type.TypeReference;

import ch.ralscha.extdirectspring.provider.Row;

@ExtendWith(SpringExtension.class)
@WebAppConfiguration
@ContextConfiguration("classpath:/testApplicationContext.xml")
public class RouterControllerInterfaceTest {

	@Autowired
	private WebApplicationContext wac;

	private MockMvc mockMvc;

	@BeforeAll
	public static void beforeTest() {
		Locale.setDefault(Locale.US);
	}

	@BeforeEach
	public void setupMockMvc() throws Exception {
		this.mockMvc = MockMvcBuilders.webAppContextSetup(this.wac).build();
	}

	@Test
	public void testNoParameters() {
		ControllerUtil.sendAndReceive(this.mockMvc, "remoteProviderImplementation", "method2", "method2() called");
	}

	@Test
	public void testNoParameterAnnotation() {
		ControllerUtil.sendAndReceive(this.mockMvc, "remoteProviderImplementation", "method3",
				"method3() called-21-3.1-aString2", 21, 3.1, "aString2");
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testWithRequestParamAnnotation() {

		Map<String, Object> readRequest = new HashMap<>();
		readRequest.put("lastName", "Smith");
		readRequest.put("active", Boolean.TRUE);

		List<Row> rows = (List<Row>) ControllerUtil.sendAndReceive(this.mockMvc, "remoteProviderImplementation",
				"storeRead", new TypeReference<List<Row>>() {/* nothing_here */
				}, readRequest);

		assertThat(rows).hasSize(1);
		Row theRow = rows.get(0);
		assertThat(theRow.getId()).isEqualTo(1);
		assertThat(theRow.getName()).isEqualTo("Smith");
		assertThat(theRow.getSalary()).isEqualTo(new BigDecimal("40"));
	}

}
