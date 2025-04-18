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

import ch.ralscha.extdirectspring.util.ApiCache;

@ExtendWith(SpringExtension.class)
@WebAppConfiguration
@ContextConfiguration("classpath:/testApplicationContext1.xml")
public class ApiControllerWithXMLConfig1Test {

	@Autowired
	private WebApplicationContext wac;

	@Autowired
	private ApiCache apiCache;

	private MockMvc mockMvc;

	@BeforeEach
	public void setupApiController() throws Exception {
		this.apiCache.clear();
		this.mockMvc = MockMvcBuilders.webAppContextSetup(this.wac).build();
	}

	@Test
	public void testGroup1() throws Exception {

		Configuration config = new Configuration();
		config.setTimeout(15000);
		config.setEnableBuffer(Boolean.FALSE);
		config.setMaxRetries(5);
		config.setStreamResponse(true);

		ApiRequestParams params = ApiRequestParams.builder()
			.group("group1")
			.configuration(config)
			.providerType("websocket")
			.build();
		ApiControllerTest.runTest(this.mockMvc, params, ApiControllerTest.group1Apis(null));
	}

}
