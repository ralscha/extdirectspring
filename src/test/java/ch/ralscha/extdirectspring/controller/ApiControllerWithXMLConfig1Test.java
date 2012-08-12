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

import java.io.IOException;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import ch.ralscha.extdirectspring.util.ApiCache;

/**
 * Tests for {@link ApiController}.
 * 
 * @author Ralph Schaer
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:/testApplicationContext1.xml")
public class ApiControllerWithXMLConfig1Test {

	@Autowired
	private ApplicationContext applicationContext;

	@Autowired
	private ApiController apiController;

	@Autowired
	private RouterController routerController;

	private Configuration config;

	@Before
	public void setupApiController() throws Exception {
		ApiCache.INSTANCE.clear();

		config = new Configuration();
		config.setTimeout(15000);
		config.setEnableBuffer(false);
		config.setMaxRetries(5);
		config.setStreamResponse(true);
	}

	@Test
	public void testGroup1() throws IOException {

		MockHttpServletRequest request = new MockHttpServletRequest("GET", "/action/api-debug.js");
		MockHttpServletResponse response = new MockHttpServletResponse();
		apiController.api("Ext.ns", "actionns", "REMOTING_API", "POLLING_URLS", "group1", false, null, request,
				response);
		ApiControllerTest.compare(response.getContentAsString(), response.getContentType(),
				ApiControllerTest.group1Apis("actionns"), "Ext.ns", "REMOTING_API", "POLLING_URLS", config);

		request = new MockHttpServletRequest("GET", "/action/api.js");
		response = new MockHttpServletResponse();
		apiController.api("Ext.ns", "actionns", "REMOTING_API", "POLLING_URLS", "group1", false, null, request,
				response);
		ApiControllerTest.compare(response.getContentAsString(), response.getContentType(),
				ApiControllerTest.group1Apis("actionns"), "Ext.ns", "REMOTING_API", "POLLING_URLS", config);
	}

}
