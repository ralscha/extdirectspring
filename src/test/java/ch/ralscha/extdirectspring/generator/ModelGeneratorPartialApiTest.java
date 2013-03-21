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

import ch.ralscha.extdirectspring.generator.bean.PartialApi;

public class ModelGeneratorPartialApiTest {

	@Before
	public void clearCaches() {
		ModelGenerator.clearCaches();
	}

	@Test
	public void testWriteModelHttpServletRequestHttpServletResponseClassOfQOutputFormatBoolean() throws IOException {
		MockHttpServletResponse response = new MockHttpServletResponse();

		OutputConfig outputConfig = new OutputConfig();
		outputConfig.setUseSingleQuotes(true);
		outputConfig.setOutputFormat(OutputFormat.EXTJS4);
		outputConfig.setDebug(false);
		outputConfig.setSurroundApiWithQuotes(true);
		outputConfig.setIncludeValidation(IncludeValidation.NONE);
		response = new MockHttpServletResponse();
		ModelGenerator.writeModel(new MockHttpServletRequest(), response, PartialApi.class, outputConfig);
		GeneratorTestUtil.compareExtJs4Code("PartialApi", response.getContentAsString(), false, true);

		outputConfig = new OutputConfig();
		outputConfig.setUseSingleQuotes(true);
		outputConfig.setOutputFormat(OutputFormat.TOUCH2);
		outputConfig.setDebug(false);
		outputConfig.setSurroundApiWithQuotes(true);
		outputConfig.setIncludeValidation(IncludeValidation.NONE);
		response = new MockHttpServletResponse();
		ModelGenerator.writeModel(new MockHttpServletRequest(), response, PartialApi.class, outputConfig);
		GeneratorTestUtil.compareTouch2Code("PartialApi", response.getContentAsString(), false, true);

		outputConfig = new OutputConfig();
		outputConfig.setUseSingleQuotes(true);
		outputConfig.setOutputFormat(OutputFormat.EXTJS4);
		outputConfig.setDebug(true);
		outputConfig.setSurroundApiWithQuotes(true);
		outputConfig.setIncludeValidation(IncludeValidation.NONE);
		response = new MockHttpServletResponse();
		ModelGenerator.writeModel(new MockHttpServletRequest(), response, PartialApi.class, outputConfig);
		GeneratorTestUtil.compareExtJs4Code("PartialApi", response.getContentAsString(), true, true);

		outputConfig = new OutputConfig();
		outputConfig.setUseSingleQuotes(true);
		outputConfig.setOutputFormat(OutputFormat.TOUCH2);
		outputConfig.setDebug(true);
		outputConfig.setSurroundApiWithQuotes(true);
		outputConfig.setIncludeValidation(IncludeValidation.NONE);
		response = new MockHttpServletResponse();
		ModelGenerator.writeModel(new MockHttpServletRequest(), response, PartialApi.class, outputConfig);
		GeneratorTestUtil.compareTouch2Code("PartialApi", response.getContentAsString(), true, true);
	}

	@Test
	public void testWriteModelHttpServletRequestHttpServletResponseClassOfQOutputFormat() throws IOException {
		MockHttpServletResponse response = new MockHttpServletResponse();

		OutputConfig outputConfig = new OutputConfig();
		outputConfig.setUseSingleQuotes(true);
		outputConfig.setOutputFormat(OutputFormat.EXTJS4);
		outputConfig.setDebug(false);
		outputConfig.setSurroundApiWithQuotes(true);
		outputConfig.setIncludeValidation(IncludeValidation.NONE);
		response = new MockHttpServletResponse();
		ModelGenerator.writeModel(new MockHttpServletRequest(), response, PartialApi.class, outputConfig);
		GeneratorTestUtil.compareExtJs4Code("PartialApi", response.getContentAsString(), false, true);

		outputConfig = new OutputConfig();
		outputConfig.setUseSingleQuotes(true);
		outputConfig.setOutputFormat(OutputFormat.TOUCH2);
		outputConfig.setDebug(false);
		outputConfig.setSurroundApiWithQuotes(true);
		outputConfig.setIncludeValidation(IncludeValidation.NONE);
		response = new MockHttpServletResponse();
		ModelGenerator.writeModel(new MockHttpServletRequest(), response, PartialApi.class, outputConfig);
		GeneratorTestUtil.compareTouch2Code("PartialApi", response.getContentAsString(), false, true);
	}

	@Test
	public void testWriteModelHttpServletRequestHttpServletResponseModelBeanOutputFormat() throws IOException {
		MockHttpServletResponse response = new MockHttpServletResponse();

		OutputConfig outputConfig = new OutputConfig();
		outputConfig.setUseSingleQuotes(true);
		outputConfig.setOutputFormat(OutputFormat.EXTJS4);
		outputConfig.setDebug(false);
		outputConfig.setSurroundApiWithQuotes(true);
		outputConfig.setIncludeValidation(IncludeValidation.NONE);
		response = new MockHttpServletResponse();
		ModelBean model = ModelGenerator.createModel(PartialApi.class);
		ModelGenerator.writeModel(new MockHttpServletRequest(), response, model, outputConfig);
		GeneratorTestUtil.compareExtJs4Code("PartialApi", response.getContentAsString(), false, true);

		outputConfig = new OutputConfig();
		outputConfig.setUseSingleQuotes(true);
		outputConfig.setOutputFormat(OutputFormat.TOUCH2);
		outputConfig.setDebug(false);
		outputConfig.setSurroundApiWithQuotes(true);
		outputConfig.setIncludeValidation(IncludeValidation.NONE);
		response = new MockHttpServletResponse();
		ModelGenerator.writeModel(new MockHttpServletRequest(), response, model, outputConfig);
		GeneratorTestUtil.compareTouch2Code("PartialApi", response.getContentAsString(), false, true);
	}

	@Test
	public void testWriteModelHttpServletRequestHttpServletResponseModelBeanOutputFormatBoolean() throws IOException {
		MockHttpServletResponse response = new MockHttpServletResponse();

		OutputConfig outputConfig = new OutputConfig();
		outputConfig.setUseSingleQuotes(true);
		outputConfig.setOutputFormat(OutputFormat.EXTJS4);
		outputConfig.setDebug(false);
		outputConfig.setSurroundApiWithQuotes(true);
		outputConfig.setIncludeValidation(IncludeValidation.NONE);
		response = new MockHttpServletResponse();
		ModelBean model = ModelGenerator.createModel(PartialApi.class);
		ModelGenerator.writeModel(new MockHttpServletRequest(), response, model, outputConfig);
		GeneratorTestUtil.compareExtJs4Code("PartialApi", response.getContentAsString(), false, true);

		outputConfig = new OutputConfig();
		outputConfig.setUseSingleQuotes(true);
		outputConfig.setOutputFormat(OutputFormat.TOUCH2);
		outputConfig.setDebug(false);
		outputConfig.setSurroundApiWithQuotes(true);
		outputConfig.setIncludeValidation(IncludeValidation.NONE);
		response = new MockHttpServletResponse();
		ModelGenerator.writeModel(new MockHttpServletRequest(), response, model, outputConfig);
		GeneratorTestUtil.compareTouch2Code("PartialApi", response.getContentAsString(), false, true);

		outputConfig = new OutputConfig();
		outputConfig.setUseSingleQuotes(true);
		outputConfig.setOutputFormat(OutputFormat.EXTJS4);
		outputConfig.setDebug(true);
		outputConfig.setSurroundApiWithQuotes(true);
		outputConfig.setIncludeValidation(IncludeValidation.NONE);
		response = new MockHttpServletResponse();
		ModelGenerator.writeModel(new MockHttpServletRequest(), response, model, outputConfig);
		GeneratorTestUtil.compareExtJs4Code("PartialApi", response.getContentAsString(), true, true);

		outputConfig = new OutputConfig();
		outputConfig.setUseSingleQuotes(true);
		outputConfig.setOutputFormat(OutputFormat.TOUCH2);
		outputConfig.setDebug(true);
		outputConfig.setSurroundApiWithQuotes(true);
		outputConfig.setIncludeValidation(IncludeValidation.NONE);
		response = new MockHttpServletResponse();
		ModelGenerator.writeModel(new MockHttpServletRequest(), response, model, outputConfig);
		GeneratorTestUtil.compareTouch2Code("PartialApi", response.getContentAsString(), true, true);
	}

	@Test
	public void testGenerateJavascriptClassOfQOutputFormatBoolean() {
		OutputConfig outputConfig = new OutputConfig();
		outputConfig.setUseSingleQuotes(true);
		outputConfig.setOutputFormat(OutputFormat.EXTJS4);
		outputConfig.setDebug(true);
		outputConfig.setSurroundApiWithQuotes(true);
		outputConfig.setIncludeValidation(IncludeValidation.NONE);
		GeneratorTestUtil.compareExtJs4Code("PartialApi",
				ModelGenerator.generateJavascript(PartialApi.class, outputConfig), true, true);

		outputConfig = new OutputConfig();
		outputConfig.setUseSingleQuotes(true);
		outputConfig.setOutputFormat(OutputFormat.EXTJS4);
		outputConfig.setDebug(false);
		outputConfig.setSurroundApiWithQuotes(true);
		outputConfig.setIncludeValidation(IncludeValidation.NONE);
		GeneratorTestUtil.compareExtJs4Code("PartialApi",
				ModelGenerator.generateJavascript(PartialApi.class, outputConfig), false, true);

		outputConfig = new OutputConfig();
		outputConfig.setUseSingleQuotes(true);
		outputConfig.setOutputFormat(OutputFormat.TOUCH2);
		outputConfig.setDebug(true);
		outputConfig.setSurroundApiWithQuotes(true);
		outputConfig.setIncludeValidation(IncludeValidation.NONE);
		GeneratorTestUtil.compareTouch2Code("PartialApi",
				ModelGenerator.generateJavascript(PartialApi.class, outputConfig), true, true);

		outputConfig = new OutputConfig();
		outputConfig.setUseSingleQuotes(true);
		outputConfig.setOutputFormat(OutputFormat.TOUCH2);
		outputConfig.setDebug(false);
		outputConfig.setSurroundApiWithQuotes(true);
		outputConfig.setIncludeValidation(IncludeValidation.NONE);
		GeneratorTestUtil.compareTouch2Code("PartialApi",
				ModelGenerator.generateJavascript(PartialApi.class, outputConfig), false, true);
	}

	@Test
	public void testCreateModel() {
		ModelBean modelBean = ModelGenerator.createModel(PartialApi.class);
		assertThat(modelBean.getReadMethod()).isEqualTo("read");
		assertThat(modelBean.getCreateMethod()).isNull();
		assertThat(modelBean.getUpdateMethod()).isNull();
		assertThat(modelBean.getDestroyMethod()).isEqualTo("destroy");
		assertThat(modelBean.getIdProperty()).isEqualTo("id");
		assertThat(modelBean.isPaging()).isFalse();
		assertThat(modelBean.getName()).isEqualTo("App.PartialApi");
		assertThat(modelBean.getFields()).hasSize(2);
		assertThat(PartialApi.expectedFields).hasSize(2);
		assertThat(modelBean.getValidations()).isEmpty();

		for (ModelFieldBean expectedField : PartialApi.expectedFields) {
			ModelFieldBean field = modelBean.getFields().get(expectedField.getName());
			assertThat(field).isEqualsToByComparingFields(expectedField);
		}
	}

	@Test
	public void testGenerateJavascriptModelBeanOutputFormatBoolean() {
		ModelBean model = ModelGenerator.createModel(PartialApi.class);

		OutputConfig outputConfig = new OutputConfig();
		outputConfig.setUseSingleQuotes(true);
		outputConfig.setOutputFormat(OutputFormat.EXTJS4);
		outputConfig.setDebug(true);
		outputConfig.setSurroundApiWithQuotes(true);
		outputConfig.setIncludeValidation(IncludeValidation.NONE);
		GeneratorTestUtil.compareExtJs4Code("PartialApi", ModelGenerator.generateJavascript(model, outputConfig), true,
				true);

		outputConfig = new OutputConfig();
		outputConfig.setUseSingleQuotes(true);
		outputConfig.setOutputFormat(OutputFormat.EXTJS4);
		outputConfig.setDebug(false);
		outputConfig.setSurroundApiWithQuotes(true);
		outputConfig.setIncludeValidation(IncludeValidation.NONE);
		GeneratorTestUtil.compareExtJs4Code("PartialApi", ModelGenerator.generateJavascript(model, outputConfig),
				false, true);

		outputConfig = new OutputConfig();
		outputConfig.setUseSingleQuotes(true);
		outputConfig.setOutputFormat(OutputFormat.TOUCH2);
		outputConfig.setDebug(true);
		outputConfig.setSurroundApiWithQuotes(true);
		outputConfig.setIncludeValidation(IncludeValidation.NONE);
		GeneratorTestUtil.compareTouch2Code("PartialApi", ModelGenerator.generateJavascript(model, outputConfig), true,
				true);

		outputConfig = new OutputConfig();
		outputConfig.setUseSingleQuotes(true);
		outputConfig.setOutputFormat(OutputFormat.TOUCH2);
		outputConfig.setDebug(false);
		outputConfig.setSurroundApiWithQuotes(true);
		outputConfig.setIncludeValidation(IncludeValidation.NONE);
		GeneratorTestUtil.compareTouch2Code("PartialApi", ModelGenerator.generateJavascript(model, outputConfig),
				false, true);
	}
}
