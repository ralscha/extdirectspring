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
package ch.ralscha.extdirectspring.generator;

import java.io.IOException;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.context.support.GenericWebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:/testApplicationContext.xml")
public class ModelGeneratorBeansWithAssociationTest {

	@Autowired
	private DefaultListableBeanFactory applicationContext;

	private static void compareExtJs4Model(String model, String value, boolean debug) {
		GeneratorTestUtil.compareExtJs4Model("/"+model+"ExtJs4Debug.json", value, debug);
	}

	private static void compareTouch2Model(String model, String value, boolean debug) {
		GeneratorTestUtil.compareTouch2Model("/"+model+"Touch2Debug.json", value, debug);
	}

	private MockHttpServletRequest createRequest() {
		MockHttpServletRequest request = new MockHttpServletRequest();
		request.setAttribute(DispatcherServlet.WEB_APPLICATION_CONTEXT_ATTRIBUTE, new GenericWebApplicationContext(
				applicationContext));
		return request;
	}
	
	@Test
	public void testBook() throws IOException {
		MockHttpServletResponse response = new MockHttpServletResponse();
		ModelGenerator.writeModel(createRequest(), response, Book.class, OutputFormat.EXTJS4, true);
		compareExtJs4Model("Book", response.getContentAsString(), true);

		response = new MockHttpServletResponse();
		ModelGenerator.writeModel(createRequest(), response, Book.class, OutputFormat.TOUCH2, false);
		compareTouch2Model("Book", response.getContentAsString(), false);

		response = new MockHttpServletResponse();
		ModelGenerator.writeModel(createRequest(), response, Book.class, OutputFormat.EXTJS4, true);
		compareExtJs4Model("Book", response.getContentAsString(), true);

		response = new MockHttpServletResponse();
		ModelGenerator.writeModel(createRequest(), response, Book.class, OutputFormat.TOUCH2, true);
		compareTouch2Model("Book", response.getContentAsString(), true);
	}

	@Test
	public void testAuthor() throws IOException {
		MockHttpServletResponse response = new MockHttpServletResponse();
		ModelGenerator.writeModel(createRequest(), response, Author.class, OutputFormat.EXTJS4, true);
		compareExtJs4Model("Author", response.getContentAsString(), true);

		response = new MockHttpServletResponse();
		ModelGenerator.writeModel(createRequest(), response, Author.class, OutputFormat.TOUCH2, false);
		compareTouch2Model("Author", response.getContentAsString(), false);

		response = new MockHttpServletResponse();
		ModelGenerator.writeModel(createRequest(), response, Author.class, OutputFormat.EXTJS4, true);
		compareExtJs4Model("Author", response.getContentAsString(), true);

		response = new MockHttpServletResponse();
		ModelGenerator.writeModel(createRequest(), response, Author.class, OutputFormat.TOUCH2, true);
		compareTouch2Model("Author", response.getContentAsString(), true);
	}

}
