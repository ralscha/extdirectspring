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

import static org.fest.assertions.Assertions.assertThat;

import java.io.IOException;

import junit.framework.Assert;

import org.apache.commons.io.IOUtils;
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

import ch.ralscha.extdirectspring.controller.RouterController;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:/testApplicationContext.xml")
public class ModelGeneratorBeanWithAnnotationsTest {

	@Autowired
	private RouterController controller;

	@Autowired
	private DefaultListableBeanFactory applicationContext;

	private void compareModelString(String expectedValue, String value, boolean debug) {
		if (debug) {
			assertThat(value.replaceAll("\\r?\\n", "\n")).isEqualTo(expectedValue.replaceAll("\\r?\\n", "\n"));
		} else {
			assertThat(value).isEqualTo(expectedValue.replaceAll("\\r?\\n", "").replace(" ", ""));
		}
	}

	private void compareExtJs4Model(String value, boolean debug) {
		try {
			String expectedValue = IOUtils.toString(getClass().getResourceAsStream(
					"/BeanWithAnnotationsExtJs4Debug.json"));
			compareModelString(expectedValue, value, debug);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	private void compareTouch2Model(String value, boolean debug) {
		try {
			String expectedValue = IOUtils.toString(getClass().getResourceAsStream(
					"/BeanWithAnnotationsTouch2Debug.json"));
			compareModelString(expectedValue, value, debug);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	@Test
	public void testWriteModelHttpServletRequestHttpServletResponseClassOfQOutputFormatBoolean() throws IOException {
		MockHttpServletResponse response = new MockHttpServletResponse();
		ModelGenerator.writeModel(createRequest(), response, BeanWithAnnotations.class, OutputFormat.EXTJS4, true);
		compareExtJs4Model(response.getContentAsString(), true);

		response = new MockHttpServletResponse();
		ModelGenerator.writeModel(createRequest(), response, BeanWithAnnotations.class, OutputFormat.TOUCH2, false);
		compareTouch2Model(response.getContentAsString(), false);

		response = new MockHttpServletResponse();
		ModelGenerator.writeModel(createRequest(), response, BeanWithAnnotations.class, OutputFormat.EXTJS4, true);
		compareExtJs4Model(response.getContentAsString(), true);

		response = new MockHttpServletResponse();
		ModelGenerator.writeModel(createRequest(), response, BeanWithAnnotations.class, OutputFormat.TOUCH2, true);
		compareTouch2Model(response.getContentAsString(), true);
	}

	@Test
	public void testWriteModelHttpServletRequestHttpServletResponseClassOfQOutputFormat() throws IOException {
		MockHttpServletResponse response = new MockHttpServletResponse();
		ModelGenerator.writeModel(createRequest(), response, BeanWithAnnotations.class, OutputFormat.EXTJS4);
		compareExtJs4Model(response.getContentAsString(), false);

		response = new MockHttpServletResponse();
		ModelGenerator.writeModel(createRequest(), response, BeanWithAnnotations.class, OutputFormat.TOUCH2);
		compareTouch2Model(response.getContentAsString(), false);
	}

	@Test
	public void testWriteModelHttpServletRequestHttpServletResponseModelBeanOutputFormat() throws IOException {
		MockHttpServletResponse response = new MockHttpServletResponse();
		ModelBean model = ModelGenerator.createModel(BeanWithAnnotations.class);
		ModelGenerator.writeModel(createRequest(), response, model, OutputFormat.EXTJS4);
		compareExtJs4Model(response.getContentAsString(), false);

		response = new MockHttpServletResponse();
		ModelGenerator.writeModel(createRequest(), response, model, OutputFormat.TOUCH2);
		compareTouch2Model(response.getContentAsString(), false);
	}

	@Test
	public void testWriteModelHttpServletRequestHttpServletResponseModelBeanOutputFormatBoolean() throws IOException {
		MockHttpServletResponse response = new MockHttpServletResponse();
		ModelBean model = ModelGenerator.createModel(BeanWithAnnotations.class);
		ModelGenerator.writeModel(createRequest(), response, model, OutputFormat.EXTJS4, false);
		compareExtJs4Model(response.getContentAsString(), false);

		response = new MockHttpServletResponse();
		ModelGenerator.writeModel(createRequest(), response, model, OutputFormat.TOUCH2, false);
		compareTouch2Model(response.getContentAsString(), false);

		response = new MockHttpServletResponse();
		ModelGenerator.writeModel(createRequest(), response, model, OutputFormat.EXTJS4, true);
		compareExtJs4Model(response.getContentAsString(), true);

		response = new MockHttpServletResponse();
		ModelGenerator.writeModel(createRequest(), response, model, OutputFormat.TOUCH2, true);
		compareTouch2Model(response.getContentAsString(), true);
	}

	private MockHttpServletRequest createRequest() {
		MockHttpServletRequest request = new MockHttpServletRequest();
		request.setAttribute(DispatcherServlet.WEB_APPLICATION_CONTEXT_ATTRIBUTE, new GenericWebApplicationContext(
				applicationContext));
		return request;
	}

	@Test
	public void testGenerateJavascriptClassOfQOutputFormatBoolean() {
		compareExtJs4Model(ModelGenerator.generateJavascript(BeanWithAnnotations.class, OutputFormat.EXTJS4, true),
				true);
		compareExtJs4Model(ModelGenerator.generateJavascript(BeanWithAnnotations.class, OutputFormat.EXTJS4, false),
				false);

		compareTouch2Model(ModelGenerator.generateJavascript(BeanWithAnnotations.class, OutputFormat.TOUCH2, true),
				true);
		compareTouch2Model(ModelGenerator.generateJavascript(BeanWithAnnotations.class, OutputFormat.TOUCH2, false),
				false);
	}

	@Test
	public void testCreateModel() {
		ModelBean modelBean = ModelGenerator.createModel(BeanWithAnnotations.class);
		assertThat(modelBean.getReadMethod()).isEqualTo("read");
		assertThat(modelBean.getCreateMethod()).isEqualTo("create");
		assertThat(modelBean.getUpdateMethod()).isEqualTo("update");
		assertThat(modelBean.getDestroyMethod()).isEqualTo("destroy");
		assertThat(modelBean.getIdProperty()).isEqualTo("aInt");
		assertThat(modelBean.isPageing()).isTrue();
		assertThat(modelBean.getName()).isEqualTo("Sch.Bean");
		assertThat(modelBean.getFields()).hasSize(22);
		assertThat(BeanWithAnnotations.expectedFields).hasSize(22);

		for (ModelFieldBean expectedField : BeanWithAnnotations.expectedFields) {
			ModelFieldBean field = modelBean.getFields().get(expectedField.getName());

			if (!field.equals(expectedField)) {
				System.out.println();
			}

			Assert.assertTrue(field.equals(expectedField));
		}
	}

	@Test
	public void testGenerateJavascriptModelBeanOutputFormatBoolean() {
		ModelBean model = ModelGenerator.createModel(BeanWithAnnotations.class);
		compareExtJs4Model(ModelGenerator.generateJavascript(model, OutputFormat.EXTJS4, true), true);
		compareExtJs4Model(ModelGenerator.generateJavascript(model, OutputFormat.EXTJS4, false), false);

		compareTouch2Model(ModelGenerator.generateJavascript(model, OutputFormat.TOUCH2, true), true);
		compareTouch2Model(ModelGenerator.generateJavascript(model, OutputFormat.TOUCH2, false), false);
	}

}
