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

import static org.fest.assertions.api.Assertions.assertThat;
import static org.fest.assertions.api.Assertions.entry;

import java.io.IOException;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.util.ReflectionTestUtils;

import ch.ralscha.extdirectspring.bean.ExtDirectResponse;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:/testApplicationContext.xml")
public class RouterControllerFormPostTest {

	@Autowired
	private RouterController controller;

	@Autowired
	private ConfigurationService configurationService;

	private MockHttpServletResponse response;

	private MockHttpServletRequest request;

	@Before
	public void beforeTest() {
		response = new MockHttpServletResponse();
		request = new MockHttpServletRequest();
	}

	@Test
	public void testCallNonExistsFormPostMethod() throws IOException {
		request.setParameter("extTID", "11");
		request.setParameter("extAction", "remoteProviderSimple");
		request.setParameter("extMethod", "method1");
		request.setParameter("extType", "rpc");

		controller.router(request, response, "remoteProviderSimple", "method1");
		ExtDirectResponse edsResponse = ControllerUtil.readDirectResponse(response.getContentAsByteArray());

		assertThat(edsResponse.getType()).isEqualTo("exception");
		assertThat(edsResponse.getMessage()).isEqualTo("Server Error");
		assertThat(edsResponse.getWhere()).isNull();
		assertThat(edsResponse.getTid()).isEqualTo(11);
		assertThat(edsResponse.getAction()).isEqualTo("remoteProviderSimple");
		assertThat(edsResponse.getMethod()).isEqualTo("method1");
	}

	@Test
	public void testCallNonExistsFormPostMethodWithConfig() throws IOException {
		Configuration conf = new Configuration();
		conf.setDefaultExceptionMessage("something wrong");
		conf.setSendStacktrace(true);
		ReflectionTestUtils.setField(configurationService, "configuration", conf);

		request.setParameter("extTID", "12");
		request.setParameter("extAction", "remoteProviderSimple");
		request.setParameter("extMethod", "method1");
		request.setParameter("extType", "rpc");
		controller.router(request, response, "remoteProviderSimple", "method1");
		ExtDirectResponse edsResponse = ControllerUtil.readDirectResponse(response.getContentAsByteArray());

		assertThat(edsResponse.getType()).isEqualTo("exception");
		assertThat(edsResponse.getMessage()).isEqualTo("something wrong");
		assertThat(edsResponse.getWhere()).isEqualTo("Bean or Method 'remoteProviderSimple.method1' not found");

		assertThat(edsResponse.getTid()).isEqualTo(12);
		assertThat(edsResponse.getAction()).isEqualTo("remoteProviderSimple");
		assertThat(edsResponse.getMethod()).isEqualTo("method1");

		ReflectionTestUtils.setField(configurationService, "configuration", new Configuration());
	}

	public void testCallExistsFormPostMethod() throws IOException {
		String redirect = controller.router(request, response, "formInfoController", "updateInfo");
		assertThat(redirect).isEqualTo("forward:updateInfo");
	}

	@Test
	public void testCallDirectWay() throws IOException {
		request.setParameter("extTID", "12");
		request.setParameter("extAction", "formInfoController");
		request.setParameter("extMethod", "updateInfoDirect");
		request.setParameter("extType", "rpc");
		request.setParameter("name", "Ralph");
		request.setParameter("age", "20");
		request.setParameter("admin", "true");
		request.setParameter("salary", "12.3");
		request.setParameter("result", "theResult");
		controller.router(request, response, "formInfoController", "updateInfoDirect");
		ExtDirectResponse edsResponse = ControllerUtil.readDirectResponse(response.getContentAsByteArray());

		assertThat(edsResponse.getType()).isEqualTo("rpc");
		assertThat(edsResponse.getMessage()).isNull();
		assertThat(edsResponse.getWhere()).isNull();
		assertThat(edsResponse.getTid()).isEqualTo(12);
		assertThat(edsResponse.getAction()).isEqualTo("formInfoController");
		assertThat(edsResponse.getMethod()).isEqualTo("updateInfoDirect");

		Map<String, Object> result = (Map<String, Object>) edsResponse.getResult();
		assertThat(result).hasSize(6).contains(entry("name", "RALPH"), entry("age", 30), entry("admin", false),
				entry("salary", 1012.3), entry("result", "theResultRESULT"), entry("success", true));
	}

}
