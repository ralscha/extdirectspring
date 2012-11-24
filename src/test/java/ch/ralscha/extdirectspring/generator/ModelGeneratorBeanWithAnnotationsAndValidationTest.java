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

import org.fest.assertions.MapAssert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import ch.ralscha.extdirectspring.generator.bean.BeanWithAnnotations;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:/testApplicationContext.xml")
public class ModelGeneratorBeanWithAnnotationsAndValidationTest {

	@Autowired
	private DefaultListableBeanFactory applicationContext;

	@Test
	public void testWriteModelHttpServletRequestHttpServletResponseClassOfQOutputFormatBoolean() throws IOException {
		MockHttpServletResponse response = new MockHttpServletResponse();
		ModelGenerator.writeModel(new MockHttpServletRequest(), response, BeanWithAnnotations.class,
				OutputFormat.EXTJS4, IncludeValidation.BUILTIN, true);
		GeneratorTestUtil.compareExtJs4Code("BeanWithAnnotationsValidation", response.getContentAsString(), true);

		response = new MockHttpServletResponse();
		ModelGenerator.writeModel(new MockHttpServletRequest(), response, BeanWithAnnotations.class,
				OutputFormat.TOUCH2, IncludeValidation.BUILTIN, false);
		GeneratorTestUtil.compareTouch2Code("BeanWithAnnotationsValidation", response.getContentAsString(), false);

		response = new MockHttpServletResponse();
		ModelGenerator.writeModel(new MockHttpServletRequest(), response, BeanWithAnnotations.class,
				OutputFormat.EXTJS4, IncludeValidation.BUILTIN, true);
		GeneratorTestUtil.compareExtJs4Code("BeanWithAnnotationsValidation", response.getContentAsString(), true);

		response = new MockHttpServletResponse();
		ModelGenerator.writeModel(new MockHttpServletRequest(), response, BeanWithAnnotations.class,
				OutputFormat.TOUCH2, IncludeValidation.BUILTIN, true);
		GeneratorTestUtil.compareTouch2Code("BeanWithAnnotationsValidation", response.getContentAsString(), true);
	}

	@Test
	public void testWriteModelHttpServletRequestHttpServletResponseModelBeanOutputFormat() throws IOException {
		MockHttpServletResponse response = new MockHttpServletResponse();
		ModelBean model = ModelGenerator.createModel(BeanWithAnnotations.class, IncludeValidation.BUILTIN);
		ModelGenerator.writeModel(new MockHttpServletRequest(), response, model, OutputFormat.EXTJS4);
		GeneratorTestUtil.compareExtJs4Code("BeanWithAnnotationsValidation", response.getContentAsString(), false);

		response = new MockHttpServletResponse();
		ModelGenerator.writeModel(new MockHttpServletRequest(), response, model, OutputFormat.TOUCH2);
		GeneratorTestUtil.compareTouch2Code("BeanWithAnnotationsValidation", response.getContentAsString(), false);
	}

	@Test
	public void testWriteModelHttpServletRequestHttpServletResponseModelBeanOutputFormatBoolean() throws IOException {
		MockHttpServletResponse response = new MockHttpServletResponse();
		ModelBean model = ModelGenerator.createModel(BeanWithAnnotations.class, IncludeValidation.BUILTIN);
		ModelGenerator.writeModel(new MockHttpServletRequest(), response, model, OutputFormat.EXTJS4, false);
		GeneratorTestUtil.compareExtJs4Code("BeanWithAnnotationsValidation", response.getContentAsString(), false);

		response = new MockHttpServletResponse();
		ModelGenerator.writeModel(new MockHttpServletRequest(), response, model, OutputFormat.TOUCH2, false);
		GeneratorTestUtil.compareTouch2Code("BeanWithAnnotationsValidation", response.getContentAsString(), false);

		response = new MockHttpServletResponse();
		ModelGenerator.writeModel(new MockHttpServletRequest(), response, model, OutputFormat.EXTJS4, true);
		GeneratorTestUtil.compareExtJs4Code("BeanWithAnnotationsValidation", response.getContentAsString(), true);

		response = new MockHttpServletResponse();
		ModelGenerator.writeModel(new MockHttpServletRequest(), response, model, OutputFormat.TOUCH2, true);
		GeneratorTestUtil.compareTouch2Code("BeanWithAnnotationsValidation", response.getContentAsString(), true);
	}

	@Test
	public void testGenerateJavascriptClassOfQOutputFormatBoolean() {
		GeneratorTestUtil.compareExtJs4Code("BeanWithAnnotationsValidation", ModelGenerator.generateJavascript(
				BeanWithAnnotations.class, OutputFormat.EXTJS4, IncludeValidation.BUILTIN, true), true);
		GeneratorTestUtil.compareExtJs4Code("BeanWithAnnotationsValidation", ModelGenerator.generateJavascript(
				BeanWithAnnotations.class, OutputFormat.EXTJS4, IncludeValidation.BUILTIN, false), false);

		GeneratorTestUtil.compareTouch2Code("BeanWithAnnotationsValidation", ModelGenerator.generateJavascript(
				BeanWithAnnotations.class, OutputFormat.TOUCH2, IncludeValidation.BUILTIN, true), true);
		GeneratorTestUtil.compareTouch2Code("BeanWithAnnotationsValidation", ModelGenerator.generateJavascript(
				BeanWithAnnotations.class, OutputFormat.TOUCH2, IncludeValidation.BUILTIN, false), false);
	}

	@Test
	public void testCreateModel() {
		ModelBean modelBean = ModelGenerator.createModel(BeanWithAnnotations.class, IncludeValidation.BUILTIN);
		assertThat(modelBean.getReadMethod()).isEqualTo("read");
		assertThat(modelBean.getCreateMethod()).isEqualTo("create");
		assertThat(modelBean.getUpdateMethod()).isEqualTo("update");
		assertThat(modelBean.getDestroyMethod()).isEqualTo("destroy");
		assertThat(modelBean.getIdProperty()).isEqualTo("aInt");
		assertThat(modelBean.isPaging()).isTrue();
		assertThat(modelBean.getName()).isEqualTo("Sch.Bean");
		assertThat(modelBean.getFields()).hasSize(24);
		assertThat(BeanWithAnnotations.expectedFields).hasSize(24);

		for (ModelFieldBean expectedField : BeanWithAnnotations.expectedFields) {
			ModelFieldBean field = modelBean.getFields().get(expectedField.getName());

			if (!field.equals(expectedField)) {
				System.out.println(field.getName() + "-->" + expectedField.getName());
			}

			assertThat(field).isEqualTo(expectedField);
		}

		assertThat(modelBean.getValidations()).hasSize(5);
		assertThat(modelBean.getValidations().get(0).getType()).isEqualTo("presence");
		assertThat(modelBean.getValidations().get(0).getField()).isEqualTo("aBigInteger");
		assertThat(modelBean.getValidations().get(1).getType()).isEqualTo("presence");
		assertThat(modelBean.getValidations().get(1).getField()).isEqualTo("aDouble");
		assertThat(modelBean.getValidations().get(2).getType()).isEqualTo("email");
		assertThat(modelBean.getValidations().get(2).getField()).isEqualTo("aString");
		assertThat(modelBean.getValidations().get(3).getType()).isEqualTo("length");
		assertThat(modelBean.getValidations().get(3).getField()).isEqualTo("aString");
		assertThat(modelBean.getValidations().get(3).getOptions()).hasSize(1);
		assertThat(modelBean.getValidations().get(3).getOptions()).includes(MapAssert.entry("max", 255));
		assertThat(modelBean.getValidations().get(4).getType()).isEqualTo("format");
		assertThat(modelBean.getValidations().get(4).getField()).isEqualTo("aString");
		assertThat(modelBean.getValidations().get(4).getOptions()).hasSize(1);
		assertThat(modelBean.getValidations().get(4).getOptions()).includes(
				MapAssert.entry("matcher", "/\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\]/"));
	}

	@Test
	public void testGenerateJavascriptModelBeanOutputFormatBoolean() {
		ModelBean model = ModelGenerator.createModel(BeanWithAnnotations.class, IncludeValidation.BUILTIN);
		GeneratorTestUtil.compareExtJs4Code("BeanWithAnnotationsValidation",
				ModelGenerator.generateJavascript(model, OutputFormat.EXTJS4, true), true);
		GeneratorTestUtil.compareExtJs4Code("BeanWithAnnotationsValidation",
				ModelGenerator.generateJavascript(model, OutputFormat.EXTJS4, false), false);

		GeneratorTestUtil.compareTouch2Code("BeanWithAnnotationsValidation",
				ModelGenerator.generateJavascript(model, OutputFormat.TOUCH2, true), true);
		GeneratorTestUtil.compareTouch2Code("BeanWithAnnotationsValidation",
				ModelGenerator.generateJavascript(model, OutputFormat.TOUCH2, false), false);
	}

}
