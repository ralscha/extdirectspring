/**
 * Copyright 2010-2013 Ralph Schaer <ralphschaer@gmail.com>
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

import static org.fest.assertions.api.Assertions.assertThat;

import java.io.IOException;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import ch.ralscha.extdirectspring.generator.bean.BeanWithAnnotations;
import ch.ralscha.extdirectspring.generator.validation.EmailValidation;
import ch.ralscha.extdirectspring.generator.validation.FormatValidation;
import ch.ralscha.extdirectspring.generator.validation.LengthValidation;
import ch.ralscha.extdirectspring.generator.validation.PresenceValidation;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:/testApplicationContext.xml")
public class ModelGeneratorBeanWithAnnotationsAndValidationTest {

	@Autowired
	private DefaultListableBeanFactory applicationContext;

	@Before
	public void clearCaches() {
		ModelGenerator.clearCaches();
	}

	@Test
	public void testWriteModelHttpServletRequestHttpServletResponseClassOfQOutputFormatBoolean() throws IOException {
		MockHttpServletResponse response = new MockHttpServletResponse();
		ModelGenerator.writeModel(new MockHttpServletRequest(), response, BeanWithAnnotations.class,
				OutputFormat.EXTJS4, IncludeValidation.BUILTIN, true);
		GeneratorTestUtil
				.compareExtJs4Code("BeanWithAnnotationsValidation", response.getContentAsString(), true, false);

		response = new MockHttpServletResponse();
		ModelGenerator.writeModel(new MockHttpServletRequest(), response, BeanWithAnnotations.class,
				OutputFormat.TOUCH2, IncludeValidation.BUILTIN, false);
		GeneratorTestUtil.compareTouch2Code("BeanWithAnnotationsValidation", response.getContentAsString(), false,
				false);

		response = new MockHttpServletResponse();
		ModelGenerator.writeModel(new MockHttpServletRequest(), response, BeanWithAnnotations.class,
				OutputFormat.EXTJS4, IncludeValidation.BUILTIN, true);
		GeneratorTestUtil
				.compareExtJs4Code("BeanWithAnnotationsValidation", response.getContentAsString(), true, false);

		response = new MockHttpServletResponse();
		ModelGenerator.writeModel(new MockHttpServletRequest(), response, BeanWithAnnotations.class,
				OutputFormat.TOUCH2, IncludeValidation.BUILTIN, true);
		GeneratorTestUtil
				.compareTouch2Code("BeanWithAnnotationsValidation", response.getContentAsString(), true, false);

		OutputConfig outputConfig = new OutputConfig();
		outputConfig.setOutputFormat(OutputFormat.EXTJS4);
		outputConfig.setDebug(false);
		outputConfig.setSurroundApiWithQuotes(true);
		outputConfig.setIncludeValidation(IncludeValidation.BUILTIN);
		response = new MockHttpServletResponse();
		ModelGenerator.writeModel(new MockHttpServletRequest(), response, BeanWithAnnotations.class, outputConfig);
		GeneratorTestUtil
				.compareExtJs4Code("BeanWithAnnotationsValidation", response.getContentAsString(), false, true);

		outputConfig = new OutputConfig();
		outputConfig.setOutputFormat(OutputFormat.TOUCH2);
		outputConfig.setDebug(false);
		outputConfig.setSurroundApiWithQuotes(true);
		outputConfig.setIncludeValidation(IncludeValidation.BUILTIN);
		response = new MockHttpServletResponse();
		ModelGenerator.writeModel(new MockHttpServletRequest(), response, BeanWithAnnotations.class, outputConfig);
		GeneratorTestUtil
				.compareTouch2Code("BeanWithAnnotationsValidation", response.getContentAsString(), false, true);

		outputConfig = new OutputConfig();
		outputConfig.setOutputFormat(OutputFormat.EXTJS4);
		outputConfig.setDebug(true);
		outputConfig.setSurroundApiWithQuotes(true);
		outputConfig.setIncludeValidation(IncludeValidation.BUILTIN);
		response = new MockHttpServletResponse();
		ModelGenerator.writeModel(new MockHttpServletRequest(), response, BeanWithAnnotations.class, outputConfig);
		GeneratorTestUtil.compareExtJs4Code("BeanWithAnnotationsValidation", response.getContentAsString(), true, true);

		outputConfig = new OutputConfig();
		outputConfig.setOutputFormat(OutputFormat.TOUCH2);
		outputConfig.setDebug(true);
		outputConfig.setSurroundApiWithQuotes(true);
		outputConfig.setIncludeValidation(IncludeValidation.BUILTIN);
		response = new MockHttpServletResponse();
		ModelGenerator.writeModel(new MockHttpServletRequest(), response, BeanWithAnnotations.class, outputConfig);
		GeneratorTestUtil.compareTouch2Code("BeanWithAnnotationsValidation", response.getContentAsString(), true, true);
	}

	@Test
	public void testWriteModelHttpServletRequestHttpServletResponseModelBeanOutputFormat() throws IOException {
		MockHttpServletResponse response = new MockHttpServletResponse();
		ModelBean model = ModelGenerator.createModel(BeanWithAnnotations.class, IncludeValidation.BUILTIN);
		ModelGenerator.writeModel(new MockHttpServletRequest(), response, model, OutputFormat.EXTJS4);
		GeneratorTestUtil.compareExtJs4Code("BeanWithAnnotationsValidation", response.getContentAsString(), false,
				false);

		response = new MockHttpServletResponse();
		ModelGenerator.writeModel(new MockHttpServletRequest(), response, model, OutputFormat.TOUCH2);
		GeneratorTestUtil.compareTouch2Code("BeanWithAnnotationsValidation", response.getContentAsString(), false,
				false);

		OutputConfig outputConfig = new OutputConfig();
		outputConfig.setOutputFormat(OutputFormat.EXTJS4);
		outputConfig.setDebug(false);
		outputConfig.setSurroundApiWithQuotes(true);
		outputConfig.setIncludeValidation(IncludeValidation.BUILTIN);
		response = new MockHttpServletResponse();
		model = ModelGenerator.createModel(BeanWithAnnotations.class, IncludeValidation.BUILTIN);
		ModelGenerator.writeModel(new MockHttpServletRequest(), response, model, outputConfig);
		GeneratorTestUtil
				.compareExtJs4Code("BeanWithAnnotationsValidation", response.getContentAsString(), false, true);

		outputConfig = new OutputConfig();
		outputConfig.setOutputFormat(OutputFormat.TOUCH2);
		outputConfig.setDebug(false);
		outputConfig.setSurroundApiWithQuotes(true);
		outputConfig.setIncludeValidation(IncludeValidation.BUILTIN);
		response = new MockHttpServletResponse();
		ModelGenerator.writeModel(new MockHttpServletRequest(), response, model, outputConfig);
		GeneratorTestUtil
				.compareTouch2Code("BeanWithAnnotationsValidation", response.getContentAsString(), false, true);
	}

	@Test
	public void testWriteModelHttpServletRequestHttpServletResponseModelBeanOutputFormatBoolean() throws IOException {
		MockHttpServletResponse response = new MockHttpServletResponse();
		ModelBean model = ModelGenerator.createModel(BeanWithAnnotations.class, IncludeValidation.BUILTIN);
		ModelGenerator.writeModel(new MockHttpServletRequest(), response, model, OutputFormat.EXTJS4, false);
		GeneratorTestUtil.compareExtJs4Code("BeanWithAnnotationsValidation", response.getContentAsString(), false,
				false);

		response = new MockHttpServletResponse();
		ModelGenerator.writeModel(new MockHttpServletRequest(), response, model, OutputFormat.TOUCH2, false);
		GeneratorTestUtil.compareTouch2Code("BeanWithAnnotationsValidation", response.getContentAsString(), false,
				false);

		response = new MockHttpServletResponse();
		ModelGenerator.writeModel(new MockHttpServletRequest(), response, model, OutputFormat.EXTJS4, true);
		GeneratorTestUtil
				.compareExtJs4Code("BeanWithAnnotationsValidation", response.getContentAsString(), true, false);

		response = new MockHttpServletResponse();
		ModelGenerator.writeModel(new MockHttpServletRequest(), response, model, OutputFormat.TOUCH2, true);
		GeneratorTestUtil
				.compareTouch2Code("BeanWithAnnotationsValidation", response.getContentAsString(), true, false);

		OutputConfig outputConfig = new OutputConfig();
		outputConfig.setOutputFormat(OutputFormat.EXTJS4);
		outputConfig.setDebug(false);
		outputConfig.setSurroundApiWithQuotes(true);
		outputConfig.setIncludeValidation(IncludeValidation.BUILTIN);
		response = new MockHttpServletResponse();
		model = ModelGenerator.createModel(BeanWithAnnotations.class, IncludeValidation.BUILTIN);
		ModelGenerator.writeModel(new MockHttpServletRequest(), response, model, outputConfig);
		GeneratorTestUtil
				.compareExtJs4Code("BeanWithAnnotationsValidation", response.getContentAsString(), false, true);

		outputConfig = new OutputConfig();
		outputConfig.setOutputFormat(OutputFormat.TOUCH2);
		outputConfig.setDebug(false);
		outputConfig.setSurroundApiWithQuotes(true);
		outputConfig.setIncludeValidation(IncludeValidation.BUILTIN);
		response = new MockHttpServletResponse();
		ModelGenerator.writeModel(new MockHttpServletRequest(), response, model, outputConfig);
		GeneratorTestUtil
				.compareTouch2Code("BeanWithAnnotationsValidation", response.getContentAsString(), false, true);

		outputConfig = new OutputConfig();
		outputConfig.setOutputFormat(OutputFormat.EXTJS4);
		outputConfig.setDebug(true);
		outputConfig.setSurroundApiWithQuotes(true);
		outputConfig.setIncludeValidation(IncludeValidation.BUILTIN);
		response = new MockHttpServletResponse();
		ModelGenerator.writeModel(new MockHttpServletRequest(), response, model, outputConfig);
		GeneratorTestUtil.compareExtJs4Code("BeanWithAnnotationsValidation", response.getContentAsString(), true, true);

		outputConfig = new OutputConfig();
		outputConfig.setOutputFormat(OutputFormat.TOUCH2);
		outputConfig.setDebug(true);
		outputConfig.setSurroundApiWithQuotes(true);
		outputConfig.setIncludeValidation(IncludeValidation.BUILTIN);
		response = new MockHttpServletResponse();
		ModelGenerator.writeModel(new MockHttpServletRequest(), response, model, outputConfig);
		GeneratorTestUtil.compareTouch2Code("BeanWithAnnotationsValidation", response.getContentAsString(), true, true);
	}

	@Test
	public void testGenerateJavascriptClassOfQOutputFormatBoolean() {
		GeneratorTestUtil.compareExtJs4Code("BeanWithAnnotationsValidation", ModelGenerator.generateJavascript(
				BeanWithAnnotations.class, OutputFormat.EXTJS4, IncludeValidation.BUILTIN, true), true, false);
		GeneratorTestUtil.compareExtJs4Code("BeanWithAnnotationsValidation", ModelGenerator.generateJavascript(
				BeanWithAnnotations.class, OutputFormat.EXTJS4, IncludeValidation.BUILTIN, false), false, false);

		GeneratorTestUtil.compareTouch2Code("BeanWithAnnotationsValidation", ModelGenerator.generateJavascript(
				BeanWithAnnotations.class, OutputFormat.TOUCH2, IncludeValidation.BUILTIN, true), true, false);
		GeneratorTestUtil.compareTouch2Code("BeanWithAnnotationsValidation", ModelGenerator.generateJavascript(
				BeanWithAnnotations.class, OutputFormat.TOUCH2, IncludeValidation.BUILTIN, false), false, false);

		OutputConfig outputConfig = new OutputConfig();
		outputConfig.setOutputFormat(OutputFormat.EXTJS4);
		outputConfig.setDebug(true);
		outputConfig.setSurroundApiWithQuotes(true);
		outputConfig.setIncludeValidation(IncludeValidation.BUILTIN);
		GeneratorTestUtil.compareExtJs4Code("BeanWithAnnotationsValidation",
				ModelGenerator.generateJavascript(BeanWithAnnotations.class, outputConfig), true, true);

		outputConfig = new OutputConfig();
		outputConfig.setOutputFormat(OutputFormat.EXTJS4);
		outputConfig.setDebug(false);
		outputConfig.setSurroundApiWithQuotes(true);
		outputConfig.setIncludeValidation(IncludeValidation.BUILTIN);
		GeneratorTestUtil.compareExtJs4Code("BeanWithAnnotationsValidation",
				ModelGenerator.generateJavascript(BeanWithAnnotations.class, outputConfig), false, true);

		outputConfig = new OutputConfig();
		outputConfig.setOutputFormat(OutputFormat.TOUCH2);
		outputConfig.setDebug(true);
		outputConfig.setSurroundApiWithQuotes(true);
		outputConfig.setIncludeValidation(IncludeValidation.BUILTIN);
		GeneratorTestUtil.compareTouch2Code("BeanWithAnnotationsValidation",
				ModelGenerator.generateJavascript(BeanWithAnnotations.class, outputConfig), true, true);

		outputConfig = new OutputConfig();
		outputConfig.setOutputFormat(OutputFormat.TOUCH2);
		outputConfig.setDebug(false);
		outputConfig.setSurroundApiWithQuotes(true);
		outputConfig.setIncludeValidation(IncludeValidation.BUILTIN);
		GeneratorTestUtil.compareTouch2Code("BeanWithAnnotationsValidation",
				ModelGenerator.generateJavascript(BeanWithAnnotations.class, outputConfig), false, true);
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
		assertThat(modelBean.getFields()).hasSize(26);
		assertThat(BeanWithAnnotations.expectedFields).hasSize(26);

		for (ModelFieldBean expectedField : BeanWithAnnotations.expectedFields) {
			ModelFieldBean field = modelBean.getFields().get(expectedField.getName());
			assertThat(field).isEqualsToByComparingFields(expectedField);
		}

		assertThat(modelBean.getValidations()).hasSize(5);
		assertThat(modelBean.getValidations().get(0)).isInstanceOf(PresenceValidation.class);
		assertThat(modelBean.getValidations().get(0).getType()).isEqualTo("presence");
		assertThat(modelBean.getValidations().get(0).getField()).isEqualTo("aBigInteger");

		assertThat(modelBean.getValidations().get(1)).isInstanceOf(PresenceValidation.class);
		assertThat(modelBean.getValidations().get(1).getType()).isEqualTo("presence");
		assertThat(modelBean.getValidations().get(1).getField()).isEqualTo("aDouble");

		assertThat(modelBean.getValidations().get(2)).isInstanceOf(EmailValidation.class);
		assertThat(modelBean.getValidations().get(2).getType()).isEqualTo("email");
		assertThat(modelBean.getValidations().get(2).getField()).isEqualTo("aString");

		assertThat(modelBean.getValidations().get(3)).isInstanceOf(LengthValidation.class);
		LengthValidation lengthValidation = (LengthValidation) modelBean.getValidations().get(3);
		assertThat(lengthValidation.getType()).isEqualTo("length");
		assertThat(lengthValidation.getField()).isEqualTo("aString");
		assertThat(lengthValidation.getMax()).isEqualTo(255L);

		assertThat(modelBean.getValidations().get(4)).isInstanceOf(FormatValidation.class);
		FormatValidation formatValidation = (FormatValidation) modelBean.getValidations().get(4);
		assertThat(formatValidation.getType()).isEqualTo("format");
		assertThat(formatValidation.getField()).isEqualTo("aString");
		assertThat(formatValidation.getMatcher())
				.isEqualTo("/\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\]/");
	}

	@Test
	public void testGenerateJavascriptModelBeanOutputFormatBoolean() {
		ModelBean model = ModelGenerator.createModel(BeanWithAnnotations.class, IncludeValidation.BUILTIN);
		GeneratorTestUtil.compareExtJs4Code("BeanWithAnnotationsValidation",
				ModelGenerator.generateJavascript(model, OutputFormat.EXTJS4, true), true, false);
		GeneratorTestUtil.compareExtJs4Code("BeanWithAnnotationsValidation",
				ModelGenerator.generateJavascript(model, OutputFormat.EXTJS4, false), false, false);

		GeneratorTestUtil.compareTouch2Code("BeanWithAnnotationsValidation",
				ModelGenerator.generateJavascript(model, OutputFormat.TOUCH2, true), true, false);
		GeneratorTestUtil.compareTouch2Code("BeanWithAnnotationsValidation",
				ModelGenerator.generateJavascript(model, OutputFormat.TOUCH2, false), false, false);

		OutputConfig outputConfig = new OutputConfig();
		outputConfig.setIncludeValidation(IncludeValidation.BUILTIN);
		model = ModelGenerator.createModel(BeanWithAnnotations.class, outputConfig);

		outputConfig = new OutputConfig();
		outputConfig.setOutputFormat(OutputFormat.EXTJS4);
		outputConfig.setDebug(true);
		outputConfig.setSurroundApiWithQuotes(true);
		outputConfig.setIncludeValidation(IncludeValidation.NONE);
		GeneratorTestUtil.compareExtJs4Code("BeanWithAnnotationsValidation",
				ModelGenerator.generateJavascript(model, outputConfig), true, true);

		outputConfig = new OutputConfig();
		outputConfig.setOutputFormat(OutputFormat.EXTJS4);
		outputConfig.setDebug(false);
		outputConfig.setSurroundApiWithQuotes(true);
		outputConfig.setIncludeValidation(IncludeValidation.NONE);
		GeneratorTestUtil.compareExtJs4Code("BeanWithAnnotationsValidation",
				ModelGenerator.generateJavascript(model, outputConfig), false, true);

		outputConfig = new OutputConfig();
		outputConfig.setOutputFormat(OutputFormat.TOUCH2);
		outputConfig.setDebug(true);
		outputConfig.setSurroundApiWithQuotes(true);
		outputConfig.setIncludeValidation(IncludeValidation.NONE);
		GeneratorTestUtil.compareTouch2Code("BeanWithAnnotationsValidation",
				ModelGenerator.generateJavascript(model, outputConfig), true, true);

		outputConfig = new OutputConfig();
		outputConfig.setOutputFormat(OutputFormat.TOUCH2);
		outputConfig.setDebug(false);
		outputConfig.setSurroundApiWithQuotes(true);
		outputConfig.setIncludeValidation(IncludeValidation.NONE);
		GeneratorTestUtil.compareTouch2Code("BeanWithAnnotationsValidation",
				ModelGenerator.generateJavascript(model, outputConfig), false, true);
	}

}
