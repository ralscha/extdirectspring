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

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:/testApplicationContext.xml")
public class ModelGeneratorBeanWithAnnotations2Test {

	@Autowired
	private DefaultListableBeanFactory applicationContext;

	@Test
	public void testWriteModelHttpServletRequestHttpServletResponseClassOfQOutputFormatBoolean() throws IOException {
		MockHttpServletResponse response = new MockHttpServletResponse();
		ModelGenerator.writeModel(new MockHttpServletRequest(), response, BeanWithAnnotations2.class,
				OutputFormat.EXTJS4, true);
		GeneratorTestUtil.compareExtJs4Code("BeanWithAnnotations2", response.getContentAsString(), true);

		response = new MockHttpServletResponse();
		ModelGenerator.writeModel(new MockHttpServletRequest(), response, BeanWithAnnotations2.class,
				OutputFormat.TOUCH2, false);
		GeneratorTestUtil.compareTouch2Code("BeanWithAnnotations2", response.getContentAsString(), false);

		response = new MockHttpServletResponse();
		ModelGenerator.writeModel(new MockHttpServletRequest(), response, BeanWithAnnotations2.class,
				OutputFormat.EXTJS4, true);
		GeneratorTestUtil.compareExtJs4Code("BeanWithAnnotations2", response.getContentAsString(), true);

		response = new MockHttpServletResponse();
		ModelGenerator.writeModel(new MockHttpServletRequest(), response, BeanWithAnnotations2.class,
				OutputFormat.TOUCH2, true);
		GeneratorTestUtil.compareTouch2Code("BeanWithAnnotations2", response.getContentAsString(), true);
	}

	@Test
	public void testWriteModelHttpServletRequestHttpServletResponseClassOfQOutputFormat() throws IOException {
		MockHttpServletResponse response = new MockHttpServletResponse();
		ModelGenerator.writeModel(new MockHttpServletRequest(), response, BeanWithAnnotations2.class,
				OutputFormat.EXTJS4);
		GeneratorTestUtil.compareExtJs4Code("BeanWithAnnotations2", response.getContentAsString(), false);

		response = new MockHttpServletResponse();
		ModelGenerator.writeModel(new MockHttpServletRequest(), response, BeanWithAnnotations2.class,
				OutputFormat.TOUCH2);
		GeneratorTestUtil.compareTouch2Code("BeanWithAnnotations2", response.getContentAsString(), false);
	}

	@Test
	public void testWriteModelHttpServletRequestHttpServletResponseModelBeanOutputFormat() throws IOException {
		MockHttpServletResponse response = new MockHttpServletResponse();
		ModelBean model = ModelGenerator.createModel(BeanWithAnnotations2.class);
		ModelGenerator.writeModel(new MockHttpServletRequest(), response, model, OutputFormat.EXTJS4);
		GeneratorTestUtil.compareExtJs4Code("BeanWithAnnotations2", response.getContentAsString(), false);

		response = new MockHttpServletResponse();
		ModelGenerator.writeModel(new MockHttpServletRequest(), response, model, OutputFormat.TOUCH2);
		GeneratorTestUtil.compareTouch2Code("BeanWithAnnotations2", response.getContentAsString(), false);
	}

	@Test
	public void testWriteModelHttpServletRequestHttpServletResponseModelBeanOutputFormatBoolean() throws IOException {
		MockHttpServletResponse response = new MockHttpServletResponse();
		ModelBean model = ModelGenerator.createModel(BeanWithAnnotations2.class);
		ModelGenerator.writeModel(new MockHttpServletRequest(), response, model, OutputFormat.EXTJS4, false);
		GeneratorTestUtil.compareExtJs4Code("BeanWithAnnotations2", response.getContentAsString(), false);

		response = new MockHttpServletResponse();
		ModelGenerator.writeModel(new MockHttpServletRequest(), response, model, OutputFormat.TOUCH2, false);
		GeneratorTestUtil.compareTouch2Code("BeanWithAnnotations2", response.getContentAsString(), false);

		response = new MockHttpServletResponse();
		ModelGenerator.writeModel(new MockHttpServletRequest(), response, model, OutputFormat.EXTJS4, true);
		GeneratorTestUtil.compareExtJs4Code("BeanWithAnnotations2", response.getContentAsString(), true);

		response = new MockHttpServletResponse();
		ModelGenerator.writeModel(new MockHttpServletRequest(), response, model, OutputFormat.TOUCH2, true);
		GeneratorTestUtil.compareTouch2Code("BeanWithAnnotations2", response.getContentAsString(), true);
	}

	@Test
	public void testGenerateJavascriptClassOfQOutputFormatBoolean() {
		GeneratorTestUtil.compareExtJs4Code("BeanWithAnnotations2",
				ModelGenerator.generateJavascript(BeanWithAnnotations2.class, OutputFormat.EXTJS4, true), true);
		GeneratorTestUtil.compareExtJs4Code("BeanWithAnnotations2",
				ModelGenerator.generateJavascript(BeanWithAnnotations2.class, OutputFormat.EXTJS4, false), false);

		GeneratorTestUtil.compareTouch2Code("BeanWithAnnotations2",
				ModelGenerator.generateJavascript(BeanWithAnnotations2.class, OutputFormat.TOUCH2, true), true);
		GeneratorTestUtil.compareTouch2Code("BeanWithAnnotations2",
				ModelGenerator.generateJavascript(BeanWithAnnotations2.class, OutputFormat.TOUCH2, false), false);
	}

	@Test
	public void testCreateModel() {
		ModelBean modelBean = ModelGenerator.createModel(BeanWithAnnotations2.class);
		assertThat(modelBean.getReadMethod()).isEqualTo("read");
		assertThat(modelBean.getCreateMethod()).isNull();
		assertThat(modelBean.getUpdateMethod()).isNull();
		assertThat(modelBean.getDestroyMethod()).isNull();
		assertThat(modelBean.getIdProperty()).isEqualTo("id");
		assertThat(modelBean.isPaging()).isFalse();
		assertThat(modelBean.getName()).isEqualTo("Sch.Bean2");
		assertThat(modelBean.getFields()).hasSize(3);
		assertThat(BeanWithAnnotations2.expectedFields).hasSize(3);

		for (ModelFieldBean expectedField : BeanWithAnnotations2.expectedFields) {
			ModelFieldBean field = modelBean.getFields().get(expectedField.getName());

			if (!field.equals(expectedField)) {
				System.out.println(field.getName() + "-->" + expectedField.getName());
			}

			Assert.assertTrue(field.equals(expectedField));
		}
	}

	@Test
	public void testGenerateJavascriptModelBeanOutputFormatBoolean() {
		ModelBean model = ModelGenerator.createModel(BeanWithAnnotations2.class);
		GeneratorTestUtil.compareExtJs4Code("BeanWithAnnotations2",
				ModelGenerator.generateJavascript(model, OutputFormat.EXTJS4, true), true);
		GeneratorTestUtil.compareExtJs4Code("BeanWithAnnotations2",
				ModelGenerator.generateJavascript(model, OutputFormat.EXTJS4, false), false);

		GeneratorTestUtil.compareTouch2Code("BeanWithAnnotations2",
				ModelGenerator.generateJavascript(model, OutputFormat.TOUCH2, true), true);
		GeneratorTestUtil.compareTouch2Code("BeanWithAnnotations2",
				ModelGenerator.generateJavascript(model, OutputFormat.TOUCH2, false), false);
	}

}
