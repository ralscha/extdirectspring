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
package ch.ralscha.extdirectspring.store;

import java.io.IOException;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import ch.ralscha.extdirectspring.controller.RouterController;

@SuppressWarnings("all")
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:/teststorewithaop.xml")
public class BookSubServiceWithAopTest {

	@Autowired
	private RouterController controller;

	private MockHttpServletResponse response;
	private MockHttpServletRequest request;

	@Before
	public void beforeTest() {
		response = new MockHttpServletResponse();
		request = new MockHttpServletRequest();
	}

	@Test
	public void testCreate() throws IOException {
		new CrudTestMethods("bookSubAopService").testCreate(controller);
	}

	@Test
	public void testRead() throws IOException {
		new CrudTestMethods("bookSubAopService").testRead(controller);
	}

	@Test
	public void testUpdate() throws IOException {
		new CrudTestMethods("bookSubAopService").testUpdate(controller);
	}

	@Test
	public void testDelete() throws IOException {
		new CrudTestMethods("bookSubAopService").testDelete(controller);
	}
}
