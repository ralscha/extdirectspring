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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;

public class ModelGeneratorTest {

	@Test
	public void testFromModelBeanExtJS1() {
		ModelBean model = new ModelBean();
		model.setName("App.User");
		ModelFieldBean idField = new ModelFieldBean("id", ModelType.INTEGER);
		model.addField(idField);
		ModelFieldBean nameField = new ModelFieldBean("name", ModelType.STRING);
		model.addField(nameField);

		String code = ModelGenerator.generateJavascript(model, OutputFormat.EXTJS4, false);
		assertThat(code)
				.isEqualTo(
						"Ext.define('App.User',{extend:'Ext.data.Model',fields:[{name:'id',type:'int'},{name:'name',type:'string'}]});");

		ModelFieldBean getIdField = model.getField("id");
		assertThat(getIdField).isEqualTo(idField);

		ModelFieldBean getNameField = model.getField("name");
		assertThat(getNameField).isEqualTo(nameField);
	}

	@Test
	public void testFromModelBeanExtJS2() {
		ModelBean model = new ModelBean();
		model.setName("App.User");
		model.addField(new ModelFieldBean("id", ModelType.INTEGER));
		ModelFieldBean field = new ModelFieldBean("salary", ModelType.FLOAT);
		field.setUseNull(true);
		model.addField(field);

		String code = ModelGenerator.generateJavascript(model, OutputFormat.EXTJS4, false);
		assertThat(code)
				.isEqualTo(
						"Ext.define('App.User',{extend:'Ext.data.Model',fields:[{name:'id',type:'int'},{name:'salary',type:'float',useNull:true}]});");

		code = ModelGenerator.generateJavascript(model, OutputFormat.EXTJS4, false);
		assertThat(code)
				.isEqualTo(
						"Ext.define('App.User',{extend:'Ext.data.Model',fields:[{name:'id',type:'int'},{name:'salary',type:'float',useNull:true}]});");
	}

	@Test
	public void testFromModelBeanExtJS3() {
		ModelBean model = new ModelBean();
		model.setName("App.Info");

		Map<String, ModelFieldBean> fields = new HashMap<String, ModelFieldBean>();
		fields.put("id", new ModelFieldBean("id", ModelType.INTEGER));

		ModelFieldBean field = new ModelFieldBean("active", ModelType.BOOLEAN);
		field.setDefaultValue(true);
		fields.put("active", field);

		field = new ModelFieldBean("date", ModelType.DATE);
		field.setDateFormat("c");
		fields.put("date", field);

		model.setFields(fields);

		String code = ModelGenerator.generateJavascript(model, OutputFormat.EXTJS4, false);
		assertThat(code)
				.isEqualTo(
						"Ext.define('App.Info',{extend:'Ext.data.Model',fields:[{name:'id',type:'int'},{name:'active',type:'boolean',defaultValue:true},{name:'date',type:'date',dateFormat:'c'}]});");

	}

	@Test
	public void testFromModelBeanTouch1() {
		ModelBean model = new ModelBean();
		model.setName("App.User");

		List<ModelFieldBean> fields = new ArrayList<ModelFieldBean>();
		fields.add(new ModelFieldBean("id", ModelType.INTEGER));
		fields.add(new ModelFieldBean("name", ModelType.STRING));
		model.addFields(fields);

		String code = ModelGenerator.generateJavascript(model, OutputFormat.TOUCH2, false);
		assertThat(code)
				.isEqualTo(
						"Ext.define('App.User',{extend:'Ext.data.Model',config:{fields:[{name:'id',type:'int'},{name:'name',type:'string'}]}});");

	}

	@Test
	public void testFromModelBeanTouch2() {
		ModelBean model = new ModelBean();
		model.setName("App.User");
		model.addField(new ModelFieldBean("id", ModelType.INTEGER));
		ModelFieldBean field = new ModelFieldBean("salary", ModelType.FLOAT);
		field.setUseNull(true);
		model.addField(field);

		String code = ModelGenerator.generateJavascript(model, OutputFormat.TOUCH2, false);
		assertThat(code)
				.isEqualTo(
						"Ext.define('App.User',{extend:'Ext.data.Model',config:{fields:[{name:'id',type:'int'},{name:'salary',type:'float',useNull:true}]}});");

		code = ModelGenerator.generateJavascript(model, OutputFormat.TOUCH2, false);
		assertThat(code)
				.isEqualTo(
						"Ext.define('App.User',{extend:'Ext.data.Model',config:{fields:[{name:'id',type:'int'},{name:'salary',type:'float',useNull:true}]}});");
	}

	@Test
	public void testFromModelBeanTouch3() {
		ModelBean model = new ModelBean();
		model.setName("App.Info");

		Map<String, ModelFieldBean> fields = new HashMap<String, ModelFieldBean>();
		fields.put("id", new ModelFieldBean("id", ModelType.INTEGER));

		ModelFieldBean field = new ModelFieldBean("active", ModelType.BOOLEAN);
		field.setDefaultValue(true);
		fields.put("active", field);

		field = new ModelFieldBean("date", ModelType.DATE);
		field.setDateFormat("c");
		fields.put("date", field);

		model.setFields(fields);

		String code = ModelGenerator.generateJavascript(model, OutputFormat.TOUCH2, false);
		assertThat(code)
				.isEqualTo(
						"Ext.define('App.Info',{extend:'Ext.data.Model',config:{fields:[{name:'id',type:'int'},{name:'active',type:'boolean',defaultValue:true},{name:'date',type:'date',dateFormat:'c'}]}});");

	}

}
