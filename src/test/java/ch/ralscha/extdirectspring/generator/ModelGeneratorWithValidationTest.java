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

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

public class ModelGeneratorWithValidationTest {

	@Test
	public void testFromModelBeanExtJS1() {
		ModelBean model = new ModelBean();
		model.setName("App.User");
		model.addField(new ModelFieldBean("id", ModelType.INTEGER));
		model.addField(new ModelFieldBean("name", ModelType.STRING));
		model.addValidation(new ModelFieldValidationBean("presence", "id"));

		ModelFieldValidationBean nameValidation = new ModelFieldValidationBean("length", "name");
		nameValidation.addOption("min", 2);
		model.addValidation(nameValidation);

		String code = ModelGenerator.generateJavascript(model, OutputFormat.EXTJS4, false);

		assertThat(code)
				.isEqualTo(
						"Ext.define('App.User',{extend:'Ext.data.Model',fields:[{name:'id',type:'int'},{name:'name',type:'string'}],validations:[{type:'presence',field:'id'},{type:'length',field:'name',min:2}]});");
	}

	@Test
	public void testFromModelBeanExtJS2() {
		ModelBean model = new ModelBean();
		model.setName("App.User");
		model.addField(new ModelFieldBean("id", ModelType.INTEGER));
		model.addField(new ModelFieldBean("email", ModelType.STRING));
		ModelFieldBean field = new ModelFieldBean("salary", ModelType.FLOAT);
		field.setUseNull(true);
		model.addField(field);

		model.addValidation(new ModelFieldValidationBean("presence", "id"));
		model.addValidation(new ModelFieldValidationBean("email", "email"));

		ModelFieldValidationBean salaryValidator = new ModelFieldValidationBean("format", "salary");
		salaryValidator.addOption("matcher", "/[0-9]*\\.[0-9]*/");
		model.addValidation(salaryValidator);

		String code = ModelGenerator.generateJavascript(model, OutputFormat.EXTJS4, false);
		assertThat(code)
				.isEqualTo(
						"Ext.define('App.User',{extend:'Ext.data.Model',fields:[{name:'id',type:'int'},{name:'email',type:'string'},{name:'salary',type:'float',useNull:true}],validations:[{type:'presence',field:'id'},{type:'email',field:'email'},{type:'format',field:'salary',matcher:/[0-9]*\\.[0-9]*/}]});");

		code = ModelGenerator.generateJavascript(model, OutputFormat.EXTJS4, false);
		assertThat(code)
				.isEqualTo(
						"Ext.define('App.User',{extend:'Ext.data.Model',fields:[{name:'id',type:'int'},{name:'email',type:'string'},{name:'salary',type:'float',useNull:true}],validations:[{type:'presence',field:'id'},{type:'email',field:'email'},{type:'format',field:'salary',matcher:/[0-9]*\\.[0-9]*/}]});");
	}

	@Test
	public void testFromModelBeanTouch1() {
		ModelBean model = new ModelBean();
		model.setName("App.User");

		List<ModelFieldBean> fields = new ArrayList<ModelFieldBean>();
		fields.add(new ModelFieldBean("id", ModelType.INTEGER));
		fields.add(new ModelFieldBean("name", ModelType.STRING));
		model.addFields(fields);

		model.addValidation(new ModelFieldValidationBean("presence", "id"));

		ModelFieldValidationBean nameValidation = new ModelFieldValidationBean("length", "name");
		nameValidation.addOption("min", 2);
		model.addValidation(nameValidation);

		String code = ModelGenerator.generateJavascript(model, OutputFormat.TOUCH2, false);
		assertThat(code)
				.isEqualTo(
						"Ext.define('App.User',{extend:'Ext.data.Model',config:{fields:[{name:'id',type:'int'},{name:'name',type:'string'}],validations:[{type:'presence',field:'id'},{type:'length',field:'name',min:2}]}});");

	}

	@Test
	public void testFromModelBeanTouch2() {
		ModelBean model = new ModelBean();
		model.setName("App.User");
		model.addField(new ModelFieldBean("id", ModelType.INTEGER));
		ModelFieldBean field = new ModelFieldBean("salary", ModelType.FLOAT);
		field.setUseNull(true);
		model.addField(field);

		model.addValidation(new ModelFieldValidationBean("presence", "id"));
		model.addValidation(new ModelFieldValidationBean("email", "email"));

		ModelFieldValidationBean salaryValidator = new ModelFieldValidationBean("format", "salary");
		salaryValidator.addOption("matcher", "/[0-9]*\\.[0-9]*/");
		model.addValidation(salaryValidator);

		String code = ModelGenerator.generateJavascript(model, OutputFormat.TOUCH2, false);
		assertThat(code)
				.isEqualTo(
						"Ext.define('App.User',{extend:'Ext.data.Model',config:{fields:[{name:'id',type:'int'},{name:'salary',type:'float',useNull:true}],validations:[{type:'presence',field:'id'},{type:'email',field:'email'},{type:'format',field:'salary',matcher:/[0-9]*\\.[0-9]*/}]}});");

		code = ModelGenerator.generateJavascript(model, OutputFormat.TOUCH2, false);
		assertThat(code)
				.isEqualTo(
						"Ext.define('App.User',{extend:'Ext.data.Model',config:{fields:[{name:'id',type:'int'},{name:'salary',type:'float',useNull:true}],validations:[{type:'presence',field:'id'},{type:'email',field:'email'},{type:'format',field:'salary',matcher:/[0-9]*\\.[0-9]*/}]}});");
	}

}
