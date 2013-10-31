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
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import ch.ralscha.extdirectspring.generator.bean.BeanWithAnnotationsDisablePaging;

public class ModelGeneratorBeanWithAnnotationsDisablePagingTest {

	@Before
	public void clearCaches() {
		ModelGenerator.clearCaches();
	}

	@Test
	public void testWriteModelHttpServletRequestHttpServletResponseClassOfQOutputFormatBoolean() throws IOException {
		MockHttpServletResponse response = new MockHttpServletResponse();
		ModelGenerator.writeModel(new MockHttpServletRequest(), response, BeanWithAnnotationsDisablePaging.class,
				OutputFormat.EXTJS4, true);
		GeneratorTestUtil.compareExtJs4Code("BeanWithAnnotationsDisablePaging", response.getContentAsString(), true,
				false);

		response = new MockHttpServletResponse();
		ModelGenerator.writeModel(new MockHttpServletRequest(), response, BeanWithAnnotationsDisablePaging.class,
				OutputFormat.TOUCH2, false);
		GeneratorTestUtil.compareTouch2Code("BeanWithAnnotationsDisablePaging", response.getContentAsString(), false,
				false);

		response = new MockHttpServletResponse();
		ModelGenerator.writeModel(new MockHttpServletRequest(), response, BeanWithAnnotationsDisablePaging.class,
				OutputFormat.EXTJS4, true);
		GeneratorTestUtil.compareExtJs4Code("BeanWithAnnotationsDisablePaging", response.getContentAsString(), true,
				false);

		response = new MockHttpServletResponse();
		ModelGenerator.writeModel(new MockHttpServletRequest(), response, BeanWithAnnotationsDisablePaging.class,
				OutputFormat.TOUCH2, true);
		GeneratorTestUtil.compareTouch2Code("BeanWithAnnotationsDisablePaging", response.getContentAsString(), true,
				false);

		OutputConfig outputConfig = new OutputConfig();
		outputConfig.setOutputFormat(OutputFormat.EXTJS4);
		outputConfig.setDebug(true);
		outputConfig.setSurroundApiWithQuotes(true);
		outputConfig.setIncludeValidation(IncludeValidation.NONE);
		response = new MockHttpServletResponse();
		ModelGenerator.writeModel(new MockHttpServletRequest(), response, BeanWithAnnotationsDisablePaging.class,
				outputConfig);
		GeneratorTestUtil.compareExtJs4Code("BeanWithAnnotationsDisablePaging", response.getContentAsString(), true,
				true);

		outputConfig = new OutputConfig();
		outputConfig.setOutputFormat(OutputFormat.TOUCH2);
		outputConfig.setDebug(true);
		outputConfig.setSurroundApiWithQuotes(true);
		outputConfig.setIncludeValidation(IncludeValidation.NONE);
		response = new MockHttpServletResponse();
		ModelGenerator.writeModel(new MockHttpServletRequest(), response, BeanWithAnnotationsDisablePaging.class,
				outputConfig);
		GeneratorTestUtil.compareTouch2Code("BeanWithAnnotationsDisablePaging", response.getContentAsString(), true,
				true);

		outputConfig = new OutputConfig();
		outputConfig.setOutputFormat(OutputFormat.EXTJS4);
		outputConfig.setDebug(false);
		outputConfig.setSurroundApiWithQuotes(true);
		outputConfig.setIncludeValidation(IncludeValidation.NONE);
		response = new MockHttpServletResponse();
		ModelGenerator.writeModel(new MockHttpServletRequest(), response, BeanWithAnnotationsDisablePaging.class,
				outputConfig);
		GeneratorTestUtil.compareExtJs4Code("BeanWithAnnotationsDisablePaging", response.getContentAsString(), false,
				true);

		outputConfig = new OutputConfig();
		outputConfig.setOutputFormat(OutputFormat.TOUCH2);
		outputConfig.setDebug(true);
		outputConfig.setSurroundApiWithQuotes(true);
		outputConfig.setIncludeValidation(IncludeValidation.NONE);
		response = new MockHttpServletResponse();
		ModelGenerator.writeModel(new MockHttpServletRequest(), response, BeanWithAnnotationsDisablePaging.class,
				outputConfig);
		GeneratorTestUtil.compareTouch2Code("BeanWithAnnotationsDisablePaging", response.getContentAsString(), true,
				true);
	}

	@Test
	public void testWriteModelHttpServletRequestHttpServletResponseClassOfQOutputFormat() throws IOException {
		MockHttpServletResponse response = new MockHttpServletResponse();
		ModelGenerator.writeModel(new MockHttpServletRequest(), response, BeanWithAnnotationsDisablePaging.class,
				OutputFormat.EXTJS4);
		GeneratorTestUtil.compareExtJs4Code("BeanWithAnnotationsDisablePaging", response.getContentAsString(), false,
				false);

		response = new MockHttpServletResponse();
		ModelGenerator.writeModel(new MockHttpServletRequest(), response, BeanWithAnnotationsDisablePaging.class,
				OutputFormat.TOUCH2);
		GeneratorTestUtil.compareTouch2Code("BeanWithAnnotationsDisablePaging", response.getContentAsString(), false,
				false);

		OutputConfig outputConfig = new OutputConfig();
		outputConfig.setOutputFormat(OutputFormat.EXTJS4);
		outputConfig.setDebug(false);
		outputConfig.setSurroundApiWithQuotes(true);
		outputConfig.setIncludeValidation(IncludeValidation.NONE);
		response = new MockHttpServletResponse();
		ModelGenerator.writeModel(new MockHttpServletRequest(), response, BeanWithAnnotationsDisablePaging.class,
				outputConfig);
		GeneratorTestUtil.compareExtJs4Code("BeanWithAnnotationsDisablePaging", response.getContentAsString(), false,
				true);

		outputConfig = new OutputConfig();
		outputConfig.setOutputFormat(OutputFormat.TOUCH2);
		outputConfig.setDebug(false);
		outputConfig.setSurroundApiWithQuotes(true);
		outputConfig.setIncludeValidation(IncludeValidation.NONE);
		response = new MockHttpServletResponse();
		ModelGenerator.writeModel(new MockHttpServletRequest(), response, BeanWithAnnotationsDisablePaging.class,
				outputConfig);
		GeneratorTestUtil.compareTouch2Code("BeanWithAnnotationsDisablePaging", response.getContentAsString(), false,
				true);
	}

	@Test
	public void testWriteModelHttpServletRequestHttpServletResponseModelBeanOutputFormat() throws IOException {
		MockHttpServletResponse response = new MockHttpServletResponse();
		ModelBean model = ModelGenerator.createModel(BeanWithAnnotationsDisablePaging.class);
		ModelGenerator.writeModel(new MockHttpServletRequest(), response, model, OutputFormat.EXTJS4);
		GeneratorTestUtil.compareExtJs4Code("BeanWithAnnotationsDisablePaging", response.getContentAsString(), false,
				false);

		response = new MockHttpServletResponse();
		ModelGenerator.writeModel(new MockHttpServletRequest(), response, model, OutputFormat.TOUCH2);
		GeneratorTestUtil.compareTouch2Code("BeanWithAnnotationsDisablePaging", response.getContentAsString(), false,
				false);

		OutputConfig outputConfig = new OutputConfig();
		outputConfig.setOutputFormat(OutputFormat.EXTJS4);
		outputConfig.setDebug(false);
		outputConfig.setSurroundApiWithQuotes(true);
		outputConfig.setIncludeValidation(IncludeValidation.NONE);
		response = new MockHttpServletResponse();
		model = ModelGenerator.createModel(BeanWithAnnotationsDisablePaging.class);
		ModelGenerator.writeModel(new MockHttpServletRequest(), response, model, outputConfig);
		GeneratorTestUtil.compareExtJs4Code("BeanWithAnnotationsDisablePaging", response.getContentAsString(), false,
				true);

		outputConfig = new OutputConfig();
		outputConfig.setOutputFormat(OutputFormat.TOUCH2);
		outputConfig.setDebug(false);
		outputConfig.setSurroundApiWithQuotes(true);
		outputConfig.setIncludeValidation(IncludeValidation.NONE);
		response = new MockHttpServletResponse();
		ModelGenerator.writeModel(new MockHttpServletRequest(), response, model, outputConfig);
		GeneratorTestUtil.compareTouch2Code("BeanWithAnnotationsDisablePaging", response.getContentAsString(), false,
				true);

	}

	@Test
	public void testWriteModelHttpServletRequestHttpServletResponseModelBeanOutputFormatBoolean() throws IOException {
		MockHttpServletResponse response = new MockHttpServletResponse();
		ModelBean model = ModelGenerator.createModel(BeanWithAnnotationsDisablePaging.class);
		ModelGenerator.writeModel(new MockHttpServletRequest(), response, model, OutputFormat.EXTJS4, false);
		GeneratorTestUtil.compareExtJs4Code("BeanWithAnnotationsDisablePaging", response.getContentAsString(), false,
				false);

		response = new MockHttpServletResponse();
		ModelGenerator.writeModel(new MockHttpServletRequest(), response, model, OutputFormat.TOUCH2, false);
		GeneratorTestUtil.compareTouch2Code("BeanWithAnnotationsDisablePaging", response.getContentAsString(), false,
				false);

		response = new MockHttpServletResponse();
		ModelGenerator.writeModel(new MockHttpServletRequest(), response, model, OutputFormat.EXTJS4, true);
		GeneratorTestUtil.compareExtJs4Code("BeanWithAnnotationsDisablePaging", response.getContentAsString(), true,
				false);

		response = new MockHttpServletResponse();
		ModelGenerator.writeModel(new MockHttpServletRequest(), response, model, OutputFormat.TOUCH2, true);
		GeneratorTestUtil.compareTouch2Code("BeanWithAnnotationsDisablePaging", response.getContentAsString(), true,
				false);

		OutputConfig outputConfig = new OutputConfig();
		outputConfig.setOutputFormat(OutputFormat.EXTJS4);
		outputConfig.setDebug(false);
		outputConfig.setSurroundApiWithQuotes(true);
		outputConfig.setIncludeValidation(IncludeValidation.NONE);
		response = new MockHttpServletResponse();
		model = ModelGenerator.createModel(BeanWithAnnotationsDisablePaging.class);
		ModelGenerator.writeModel(new MockHttpServletRequest(), response, model, outputConfig);
		GeneratorTestUtil.compareExtJs4Code("BeanWithAnnotationsDisablePaging", response.getContentAsString(), false,
				true);

		outputConfig = new OutputConfig();
		outputConfig.setOutputFormat(OutputFormat.TOUCH2);
		outputConfig.setDebug(false);
		outputConfig.setSurroundApiWithQuotes(true);
		outputConfig.setIncludeValidation(IncludeValidation.NONE);
		response = new MockHttpServletResponse();
		ModelGenerator.writeModel(new MockHttpServletRequest(), response, model, outputConfig);
		GeneratorTestUtil.compareTouch2Code("BeanWithAnnotationsDisablePaging", response.getContentAsString(), false,
				true);

		outputConfig = new OutputConfig();
		outputConfig.setOutputFormat(OutputFormat.EXTJS4);
		outputConfig.setDebug(true);
		outputConfig.setSurroundApiWithQuotes(true);
		outputConfig.setIncludeValidation(IncludeValidation.NONE);
		response = new MockHttpServletResponse();
		ModelGenerator.writeModel(new MockHttpServletRequest(), response, model, outputConfig);
		GeneratorTestUtil.compareExtJs4Code("BeanWithAnnotationsDisablePaging", response.getContentAsString(), true,
				true);

		outputConfig = new OutputConfig();
		outputConfig.setOutputFormat(OutputFormat.TOUCH2);
		outputConfig.setDebug(true);
		outputConfig.setSurroundApiWithQuotes(true);
		outputConfig.setIncludeValidation(IncludeValidation.NONE);
		response = new MockHttpServletResponse();
		ModelGenerator.writeModel(new MockHttpServletRequest(), response, model, outputConfig);
		GeneratorTestUtil.compareTouch2Code("BeanWithAnnotationsDisablePaging", response.getContentAsString(), true,
				true);
	}

	@Test
	public void testGenerateJavascriptClassOfQOutputFormatBoolean() {
		GeneratorTestUtil.compareExtJs4Code("BeanWithAnnotationsDisablePaging",
				ModelGenerator.generateJavascript(BeanWithAnnotationsDisablePaging.class, OutputFormat.EXTJS4, true),
				true, false);
		GeneratorTestUtil.compareExtJs4Code("BeanWithAnnotationsDisablePaging",
				ModelGenerator.generateJavascript(BeanWithAnnotationsDisablePaging.class, OutputFormat.EXTJS4, false),
				false, false);

		GeneratorTestUtil.compareTouch2Code("BeanWithAnnotationsDisablePaging",
				ModelGenerator.generateJavascript(BeanWithAnnotationsDisablePaging.class, OutputFormat.TOUCH2, true),
				true, false);
		GeneratorTestUtil.compareTouch2Code("BeanWithAnnotationsDisablePaging",
				ModelGenerator.generateJavascript(BeanWithAnnotationsDisablePaging.class, OutputFormat.TOUCH2, false),
				false, false);

		OutputConfig outputConfig = new OutputConfig();
		outputConfig.setOutputFormat(OutputFormat.EXTJS4);
		outputConfig.setDebug(true);
		outputConfig.setSurroundApiWithQuotes(true);
		outputConfig.setIncludeValidation(IncludeValidation.NONE);
		GeneratorTestUtil.compareExtJs4Code("BeanWithAnnotationsDisablePaging",
				ModelGenerator.generateJavascript(BeanWithAnnotationsDisablePaging.class, outputConfig), true, true);

		outputConfig = new OutputConfig();
		outputConfig.setOutputFormat(OutputFormat.EXTJS4);
		outputConfig.setDebug(false);
		outputConfig.setSurroundApiWithQuotes(true);
		outputConfig.setIncludeValidation(IncludeValidation.NONE);
		GeneratorTestUtil.compareExtJs4Code("BeanWithAnnotationsDisablePaging",
				ModelGenerator.generateJavascript(BeanWithAnnotationsDisablePaging.class, outputConfig), false, true);

		outputConfig = new OutputConfig();
		outputConfig.setOutputFormat(OutputFormat.TOUCH2);
		outputConfig.setDebug(true);
		outputConfig.setSurroundApiWithQuotes(true);
		outputConfig.setIncludeValidation(IncludeValidation.NONE);
		GeneratorTestUtil.compareTouch2Code("BeanWithAnnotationsDisablePaging",
				ModelGenerator.generateJavascript(BeanWithAnnotationsDisablePaging.class, outputConfig), true, true);

		outputConfig = new OutputConfig();
		outputConfig.setOutputFormat(OutputFormat.TOUCH2);
		outputConfig.setDebug(false);
		outputConfig.setSurroundApiWithQuotes(true);
		outputConfig.setIncludeValidation(IncludeValidation.NONE);
		GeneratorTestUtil.compareTouch2Code("BeanWithAnnotationsDisablePaging",
				ModelGenerator.generateJavascript(BeanWithAnnotationsDisablePaging.class, outputConfig), false, true);
	}

	@Test
	public void testCreateModel() {
		ModelBean modelBean = ModelGenerator.createModel(BeanWithAnnotationsDisablePaging.class);
		assertThat(modelBean.getReadMethod()).isEqualTo("read");
		assertThat(modelBean.getCreateMethod()).isNull();
		assertThat(modelBean.getUpdateMethod()).isNull();
		assertThat(modelBean.getDestroyMethod()).isNull();
		assertThat(modelBean.getIdProperty()).isEqualTo("id");
		assertThat(modelBean.isDisablePagingParameters()).isTrue();
		assertThat(modelBean.isPaging()).isFalse();
		assertThat(modelBean.getMessageProperty()).isEqualTo("theMessageProperty");
		assertThat(modelBean.getName()).isEqualTo("Sch.Bean2");
		assertThat(modelBean.getFields()).hasSize(3);
		assertThat(BeanWithAnnotationsDisablePaging.expectedFields).hasSize(3);

		for (ModelFieldBean expectedField : BeanWithAnnotationsDisablePaging.expectedFields) {
			ModelFieldBean field = modelBean.getFields().get(expectedField.getName());
			assertThat(field).isEqualsToByComparingFields(expectedField);
		}
	}

	@Test
	public void testGenerateJavascriptModelBeanOutputFormatBoolean() {
		ModelBean model = ModelGenerator.createModel(BeanWithAnnotationsDisablePaging.class);
		GeneratorTestUtil.compareExtJs4Code("BeanWithAnnotationsDisablePaging",
				ModelGenerator.generateJavascript(model, OutputFormat.EXTJS4, true), true, false);
		GeneratorTestUtil.compareExtJs4Code("BeanWithAnnotationsDisablePaging",
				ModelGenerator.generateJavascript(model, OutputFormat.EXTJS4, false), false, false);

		GeneratorTestUtil.compareTouch2Code("BeanWithAnnotationsDisablePaging",
				ModelGenerator.generateJavascript(model, OutputFormat.TOUCH2, true), true, false);
		GeneratorTestUtil.compareTouch2Code("BeanWithAnnotationsDisablePaging",
				ModelGenerator.generateJavascript(model, OutputFormat.TOUCH2, false), false, false);

		OutputConfig outputConfig = new OutputConfig();
		outputConfig.setOutputFormat(OutputFormat.EXTJS4);
		outputConfig.setDebug(true);
		outputConfig.setSurroundApiWithQuotes(true);
		GeneratorTestUtil.compareExtJs4Code("BeanWithAnnotationsDisablePaging",
				ModelGenerator.generateJavascript(model, outputConfig), true, true);

		outputConfig = new OutputConfig();
		outputConfig.setOutputFormat(OutputFormat.EXTJS4);
		outputConfig.setDebug(false);
		outputConfig.setSurroundApiWithQuotes(true);
		GeneratorTestUtil.compareExtJs4Code("BeanWithAnnotationsDisablePaging",
				ModelGenerator.generateJavascript(model, outputConfig), false, true);

		outputConfig = new OutputConfig();
		outputConfig.setOutputFormat(OutputFormat.TOUCH2);
		outputConfig.setDebug(true);
		outputConfig.setSurroundApiWithQuotes(true);
		GeneratorTestUtil.compareTouch2Code("BeanWithAnnotationsDisablePaging",
				ModelGenerator.generateJavascript(model, outputConfig), true, true);

		outputConfig = new OutputConfig();
		outputConfig.setOutputFormat(OutputFormat.TOUCH2);
		outputConfig.setDebug(false);
		outputConfig.setSurroundApiWithQuotes(true);
		GeneratorTestUtil.compareTouch2Code("BeanWithAnnotationsDisablePaging",
				ModelGenerator.generateJavascript(model, outputConfig), false, true);
	}

}
