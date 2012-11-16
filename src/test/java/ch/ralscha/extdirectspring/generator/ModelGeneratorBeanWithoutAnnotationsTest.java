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

import org.junit.Assert;
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
public class ModelGeneratorBeanWithoutAnnotationsTest {

	@Autowired
	private RouterController controller;

	@Autowired
	private DefaultListableBeanFactory applicationContext;

	private static void compareExtJs4Model(String value, boolean debug) {
		GeneratorTestUtil.compareExtJs4Model("/BeanWithoutAnnotationsExtJs4Debug.json", value, debug);
	}

	private static void compareTouch2Model(String value, boolean debug) {
		GeneratorTestUtil.compareTouch2Model("/BeanWithoutAnnotationsTouch2Debug.json", value, debug);
	}

	@Test
	public void testWriteModelHttpServletRequestHttpServletResponseClassOfQOutputFormat() throws IOException {
		MockHttpServletResponse response = new MockHttpServletResponse();
		ModelGenerator.writeModel(createRequest(), response, BeanWithoutAnnotations.class, OutputFormat.EXTJS4);
		compareExtJs4Model(response.getContentAsString(), false);

		response = new MockHttpServletResponse();
		ModelGenerator.writeModel(createRequest(), response, BeanWithoutAnnotations.class, OutputFormat.TOUCH2);
		compareTouch2Model(response.getContentAsString(), false);
	}

	@Test
	public void testWriteModelHttpServletRequestHttpServletResponseClassOfQOutputFormatBoolean() throws IOException {
		MockHttpServletResponse response = new MockHttpServletResponse();
		ModelGenerator.writeModel(createRequest(), response, BeanWithoutAnnotations.class, OutputFormat.EXTJS4, false);
		compareExtJs4Model(response.getContentAsString(), false);

		response = new MockHttpServletResponse();
		ModelGenerator.writeModel(createRequest(), response, BeanWithoutAnnotations.class, OutputFormat.TOUCH2, false);
		compareTouch2Model(response.getContentAsString(), false);

		response = new MockHttpServletResponse();
		ModelGenerator.writeModel(createRequest(), response, BeanWithoutAnnotations.class, OutputFormat.EXTJS4, true);
		compareExtJs4Model(response.getContentAsString(), true);

		response = new MockHttpServletResponse();
		ModelGenerator.writeModel(createRequest(), response, BeanWithoutAnnotations.class, OutputFormat.TOUCH2, true);
		compareTouch2Model(response.getContentAsString(), true);
	}

	@Test
	public void testWriteModelHttpServletRequestHttpServletResponseModelBeanOutputFormat() throws IOException {
		MockHttpServletResponse response = new MockHttpServletResponse();
		ModelBean model = ModelGenerator.createModel(BeanWithoutAnnotations.class);
		ModelGenerator.writeModel(createRequest(), response, model, OutputFormat.EXTJS4);
		compareExtJs4Model(response.getContentAsString(), false);

		response = new MockHttpServletResponse();
		ModelGenerator.writeModel(createRequest(), response, model, OutputFormat.TOUCH2);
		compareTouch2Model(response.getContentAsString(), false);
	}

	@Test
	public void testWriteModelHttpServletRequestHttpServletResponseModelBeanOutputFormatBoolean() throws IOException {
		MockHttpServletResponse response = new MockHttpServletResponse();
		ModelBean model = ModelGenerator.createModel(BeanWithoutAnnotations.class);
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
		compareExtJs4Model(ModelGenerator.generateJavascript(BeanWithoutAnnotations.class, OutputFormat.EXTJS4, true),
				true);
		compareExtJs4Model(ModelGenerator.generateJavascript(BeanWithoutAnnotations.class, OutputFormat.EXTJS4, false),
				false);

		compareTouch2Model(ModelGenerator.generateJavascript(BeanWithoutAnnotations.class, OutputFormat.TOUCH2, true),
				true);
		compareTouch2Model(ModelGenerator.generateJavascript(BeanWithoutAnnotations.class, OutputFormat.TOUCH2, false),
				false);
	}

	@Test
	public void testCreateModel() {
		ModelBean modelBean = ModelGenerator.createModel(BeanWithoutAnnotations.class);
		assertThat(modelBean.getReadMethod()).isNull();
		assertThat(modelBean.getCreateMethod()).isNull();
		assertThat(modelBean.getUpdateMethod()).isNull();
		assertThat(modelBean.getDestroyMethod()).isNull();
		assertThat(modelBean.getIdProperty()).isNull();
		assertThat(modelBean.isPaging()).isFalse();
		assertThat(modelBean.getName()).isEqualTo("ch.ralscha.extdirectspring.generator.BeanWithoutAnnotations");
		assertThat(modelBean.getFields()).hasSize(24);
		assertThat(BeanWithoutAnnotations.expectedFields).hasSize(24);

		for (ModelFieldBean expectedField : BeanWithoutAnnotations.expectedFields) {
			ModelFieldBean field = modelBean.getFields().get(expectedField.getName());
			Assert.assertTrue(field.equals(expectedField));
		}
	}

	@Test
	public void testGenerateJavascriptModelBeanOutputFormatBoolean() {
		ModelBean model = ModelGenerator.createModel(BeanWithoutAnnotations.class);
		compareExtJs4Model(ModelGenerator.generateJavascript(model, OutputFormat.EXTJS4, true), true);
		compareExtJs4Model(ModelGenerator.generateJavascript(model, OutputFormat.EXTJS4, false), false);

		compareTouch2Model(ModelGenerator.generateJavascript(model, OutputFormat.TOUCH2, true), true);
		compareTouch2Model(ModelGenerator.generateJavascript(model, OutputFormat.TOUCH2, false), false);
	}

}
