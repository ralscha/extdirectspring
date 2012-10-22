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
import java.math.BigDecimal;

import org.fest.assertions.MapAssert;
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
public class ModelGeneratorBeanWithValidationTest {

	@Autowired
	private RouterController controller;

	@Autowired
	private DefaultListableBeanFactory applicationContext;

	private static void compareExtJs4Model(String value, boolean debug) {
		GeneratorTestUtil.compareExtJs4Model("/BeanWithValidationExtJs4Debug.json", value, debug);
	}

	private static void compareTouch2Model(String value, boolean debug) {
		GeneratorTestUtil.compareTouch2Model("/BeanWithValidationTouch2Debug.json", value, debug);
	}

	@Test
	public void testWriteModelHttpServletRequestHttpServletResponseClassOfQOutputFormatBoolean() throws IOException {
		MockHttpServletResponse response = new MockHttpServletResponse();
		ModelGenerator.writeModel(createRequest(), response, BeanWithValidation.class, OutputFormat.EXTJS4,
				IncludeValidation.ALL, true);
		compareExtJs4Model(response.getContentAsString(), true);

		response = new MockHttpServletResponse();
		ModelGenerator.writeModel(createRequest(), response, BeanWithValidation.class, OutputFormat.TOUCH2,
				IncludeValidation.ALL, false);
		compareTouch2Model(response.getContentAsString(), false);

		response = new MockHttpServletResponse();
		ModelGenerator.writeModel(createRequest(), response, BeanWithValidation.class, OutputFormat.EXTJS4,
				IncludeValidation.ALL, true);
		compareExtJs4Model(response.getContentAsString(), true);

		response = new MockHttpServletResponse();
		ModelGenerator.writeModel(createRequest(), response, BeanWithValidation.class, OutputFormat.TOUCH2,
				IncludeValidation.ALL, true);
		compareTouch2Model(response.getContentAsString(), true);
	}

	@Test
	public void testWriteModelHttpServletRequestHttpServletResponseModelBeanOutputFormat() throws IOException {
		MockHttpServletResponse response = new MockHttpServletResponse();
		ModelBean model = ModelGenerator.createModel(BeanWithValidation.class, IncludeValidation.ALL);
		ModelGenerator.writeModel(createRequest(), response, model, OutputFormat.EXTJS4);
		compareExtJs4Model(response.getContentAsString(), false);

		response = new MockHttpServletResponse();
		ModelGenerator.writeModel(createRequest(), response, model, OutputFormat.TOUCH2);
		compareTouch2Model(response.getContentAsString(), false);
	}

	@Test
	public void testWriteModelHttpServletRequestHttpServletResponseModelBeanOutputFormatBoolean() throws IOException {
		MockHttpServletResponse response = new MockHttpServletResponse();
		ModelBean model = ModelGenerator.createModel(BeanWithValidation.class, IncludeValidation.ALL);
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
		compareExtJs4Model(ModelGenerator.generateJavascript(BeanWithValidation.class, OutputFormat.EXTJS4,
				IncludeValidation.ALL, true), true);
		compareExtJs4Model(ModelGenerator.generateJavascript(BeanWithValidation.class, OutputFormat.EXTJS4,
				IncludeValidation.ALL, false), false);

		compareTouch2Model(ModelGenerator.generateJavascript(BeanWithValidation.class, OutputFormat.TOUCH2,
				IncludeValidation.ALL, true), true);
		compareTouch2Model(ModelGenerator.generateJavascript(BeanWithValidation.class, OutputFormat.TOUCH2,
				IncludeValidation.ALL, false), false);
	}

	@Test
	public void testCreateModel() {
		ModelBean modelBean = ModelGenerator.createModel(BeanWithValidation.class, IncludeValidation.ALL);
		assertThat(modelBean.getReadMethod()).isNull();
		assertThat(modelBean.getCreateMethod()).isNull();
		assertThat(modelBean.getUpdateMethod()).isNull();
		assertThat(modelBean.getDestroyMethod()).isNull();
		assertThat(modelBean.getIdProperty()).isNull();
		assertThat(modelBean.isPaging()).isFalse();
		assertThat(modelBean.getName()).isEqualTo("ch.ralscha.extdirectspring.generator.BeanWithValidation");
		assertThat(modelBean.getFields()).hasSize(10);
		assertThat(BeanWithValidation.expectedFields).hasSize(10);

		for (ModelFieldBean expectedField : BeanWithValidation.expectedFields) {
			ModelFieldBean field = modelBean.getFields().get(expectedField.getName());

			if (!field.equals(expectedField)) {
				System.out.println(field.getName() + "-->" + expectedField.getName());
			}

			assertThat(field).isEqualTo(expectedField);
		}

		assertThat(modelBean.getValidations()).hasSize(11);
		assertThat(modelBean.getValidations().get(0).getType()).isEqualTo("email");
		assertThat(modelBean.getValidations().get(0).getField()).isEqualTo("email");

		assertThat(modelBean.getValidations().get(1).getType()).isEqualTo("range");
		assertThat(modelBean.getValidations().get(1).getField()).isEqualTo("minMax1");
		assertThat(modelBean.getValidations().get(1).getOptions()).hasSize(1);
		assertThat(modelBean.getValidations().get(1).getOptions()).includes(
				MapAssert.entry("max", new BigDecimal("100")));

		assertThat(modelBean.getValidations().get(2).getType()).isEqualTo("range");
		assertThat(modelBean.getValidations().get(2).getField()).isEqualTo("minMax1");
		assertThat(modelBean.getValidations().get(2).getOptions()).hasSize(1);
		assertThat(modelBean.getValidations().get(2).getOptions())
				.includes(MapAssert.entry("min", new BigDecimal("1")));

		assertThat(modelBean.getValidations().get(3).getType()).isEqualTo("range");
		assertThat(modelBean.getValidations().get(3).getField()).isEqualTo("minMax2");
		assertThat(modelBean.getValidations().get(3).getOptions()).hasSize(1);
		assertThat(modelBean.getValidations().get(3).getOptions()).includes(MapAssert.entry("max", 10000L));

		assertThat(modelBean.getValidations().get(4).getType()).isEqualTo("range");
		assertThat(modelBean.getValidations().get(4).getField()).isEqualTo("minMax3");
		assertThat(modelBean.getValidations().get(4).getOptions()).hasSize(2);
		assertThat(modelBean.getValidations().get(4).getOptions()).includes(MapAssert.entry("min", 20L));
		assertThat(modelBean.getValidations().get(4).getOptions()).includes(MapAssert.entry("max", 50L));

		assertThat(modelBean.getValidations().get(5).getType()).isEqualTo("digits");
		assertThat(modelBean.getValidations().get(5).getField()).isEqualTo("digits");
		assertThat(modelBean.getValidations().get(5).getOptions()).hasSize(2);
		assertThat(modelBean.getValidations().get(5).getOptions()).includes(MapAssert.entry("integer", 10));
		assertThat(modelBean.getValidations().get(5).getOptions()).includes(MapAssert.entry("fraction", 2));

		assertThat(modelBean.getValidations().get(6).getType()).isEqualTo("future");
		assertThat(modelBean.getValidations().get(6).getField()).isEqualTo("future");
		assertThat(modelBean.getValidations().get(7).getType()).isEqualTo("past");
		assertThat(modelBean.getValidations().get(7).getField()).isEqualTo("past");
		assertThat(modelBean.getValidations().get(8).getType()).isEqualTo("null");
		assertThat(modelBean.getValidations().get(8).getField()).isEqualTo("nullable");
		assertThat(modelBean.getValidations().get(9).getType()).isEqualTo("notBlank");
		assertThat(modelBean.getValidations().get(9).getField()).isEqualTo("notBlank");
		assertThat(modelBean.getValidations().get(10).getType()).isEqualTo("creditCardNumber");
		assertThat(modelBean.getValidations().get(10).getField()).isEqualTo("creditCardNumber");

	}

	@Test
	public void testGenerateJavascriptModelBeanOutputFormatBoolean() {
		ModelBean model = ModelGenerator.createModel(BeanWithValidation.class, IncludeValidation.ALL);
		compareExtJs4Model(ModelGenerator.generateJavascript(model, OutputFormat.EXTJS4, true), true);
		compareExtJs4Model(ModelGenerator.generateJavascript(model, OutputFormat.EXTJS4, false), false);

		compareTouch2Model(ModelGenerator.generateJavascript(model, OutputFormat.TOUCH2, true), true);
		compareTouch2Model(ModelGenerator.generateJavascript(model, OutputFormat.TOUCH2, false), false);
	}

}
