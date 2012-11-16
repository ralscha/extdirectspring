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

	@Test
	public void testFromModelBeanWithHasManyAssociationExtJs() {
		ModelBean model = new ModelBean();
		model.setName("App.Info");

		Map<String, ModelFieldBean> fields = new HashMap<String, ModelFieldBean>();
		fields.put("id", new ModelFieldBean("id", ModelType.INTEGER));
		model.setFields(fields);

		ModelAssociationBean association = new ModelAssociationBean(ModelAssociationType.HAS_MANY, "User");
		model.addAssociation(association);

		String code = ModelGenerator.generateJavascript(model, OutputFormat.EXTJS4, false);
		assertThat(code)
				.isEqualTo(
						"Ext.define('App.Info',{extend:'Ext.data.Model',fields:[{name:'id',type:'int'}],associations:[{type:'hasMany',model:'User'}]});");

		association.setAutoLoad(false);
		association.setAssociationKey("test");
		code = ModelGenerator.generateJavascript(model, OutputFormat.EXTJS4, false);
		assertThat(code)
				.isEqualTo(
						"Ext.define('App.Info',{extend:'Ext.data.Model',fields:[{name:'id',type:'int'}],associations:[{type:'hasMany',model:'User',associationKey:'test',autoLoad:false}]});");

		association.setAutoLoad(true);
		association.setAssociationKey(null);
		association.setPrimaryKey("id");
		code = ModelGenerator.generateJavascript(model, OutputFormat.EXTJS4, false);
		assertThat(code)
				.isEqualTo(
						"Ext.define('App.Info',{extend:'Ext.data.Model',fields:[{name:'id',type:'int'}],associations:[{type:'hasMany',model:'User',autoLoad:true,primaryKey:'id'}]});");

		association.setPrimaryKey(null);
		association.setName("users");
		code = ModelGenerator.generateJavascript(model, OutputFormat.EXTJS4, false);
		assertThat(code)
				.isEqualTo(
						"Ext.define('App.Info',{extend:'Ext.data.Model',fields:[{name:'id',type:'int'}],associations:[{type:'hasMany',model:'User',autoLoad:true,name:'users'}]});");

		association.setPrimaryKey(null);
		association.setName(null);
		association.setForeignKey("user_id");
		code = ModelGenerator.generateJavascript(model, OutputFormat.EXTJS4, false);
		assertThat(code)
				.isEqualTo(
						"Ext.define('App.Info',{extend:'Ext.data.Model',fields:[{name:'id',type:'int'}],associations:[{type:'hasMany',model:'User',autoLoad:true,foreignKey:'user_id'}]});");
	}

	@Test
	public void testFromModelBeanWithHasManyAssociationTouch() {
		ModelBean model = new ModelBean();
		model.setName("App.Info");

		Map<String, ModelFieldBean> fields = new HashMap<String, ModelFieldBean>();
		fields.put("id", new ModelFieldBean("id", ModelType.INTEGER));
		model.setFields(fields);

		ModelAssociationBean association = new ModelAssociationBean(ModelAssociationType.HAS_MANY, "User");
		model.addAssociation(association);

		String code = ModelGenerator.generateJavascript(model, OutputFormat.TOUCH2, false);
		assertThat(code)
				.isEqualTo(
						"Ext.define('App.Info',{extend:'Ext.data.Model',config:{fields:[{name:'id',type:'int'}],associations:[{type:'hasMany',model:'User'}]}});");

		association.setAutoLoad(false);
		association.setAssociationKey("test");
		code = ModelGenerator.generateJavascript(model, OutputFormat.TOUCH2, false);
		assertThat(code)
				.isEqualTo(
						"Ext.define('App.Info',{extend:'Ext.data.Model',config:{fields:[{name:'id',type:'int'}],associations:[{type:'hasMany',model:'User',associationKey:'test',autoLoad:false}]}});");

		association.setAutoLoad(true);
		association.setAssociationKey(null);
		association.setPrimaryKey("id");
		code = ModelGenerator.generateJavascript(model, OutputFormat.TOUCH2, false);
		assertThat(code)
				.isEqualTo(
						"Ext.define('App.Info',{extend:'Ext.data.Model',config:{fields:[{name:'id',type:'int'}],associations:[{type:'hasMany',model:'User',autoLoad:true,primaryKey:'id'}]}});");

		association.setPrimaryKey(null);
		association.setName("users");
		code = ModelGenerator.generateJavascript(model, OutputFormat.TOUCH2, false);
		assertThat(code)
				.isEqualTo(
						"Ext.define('App.Info',{extend:'Ext.data.Model',config:{fields:[{name:'id',type:'int'}],associations:[{type:'hasMany',model:'User',autoLoad:true,name:'users'}]}});");

		association.setPrimaryKey(null);
		association.setName(null);
		association.setForeignKey("user_id");
		code = ModelGenerator.generateJavascript(model, OutputFormat.TOUCH2, false);
		assertThat(code)
				.isEqualTo(
						"Ext.define('App.Info',{extend:'Ext.data.Model',config:{fields:[{name:'id',type:'int'}],associations:[{type:'hasMany',model:'User',autoLoad:true,foreignKey:'user_id'}]}});");
	}

	@Test
	public void testFromModelBeanWithBelongsToAssociationExtJs() {
		ModelBean model = new ModelBean();
		model.setName("App.Info");

		Map<String, ModelFieldBean> fields = new HashMap<String, ModelFieldBean>();
		fields.put("id", new ModelFieldBean("id", ModelType.INTEGER));
		model.setFields(fields);

		ModelAssociationBean association = new ModelAssociationBean(ModelAssociationType.BELONGS_TO, "User");
		model.addAssociation(association);

		String code = ModelGenerator.generateJavascript(model, OutputFormat.EXTJS4, false);
		assertThat(code)
				.isEqualTo(
						"Ext.define('App.Info',{extend:'Ext.data.Model',fields:[{name:'id',type:'int'}],associations:[{type:'belongsTo',model:'User'}]});");

		association.setAssociationKey("test");
		code = ModelGenerator.generateJavascript(model, OutputFormat.EXTJS4, false);
		assertThat(code)
				.isEqualTo(
						"Ext.define('App.Info',{extend:'Ext.data.Model',fields:[{name:'id',type:'int'}],associations:[{type:'belongsTo',model:'User',associationKey:'test'}]});");

		association.setAssociationKey(null);
		association.setPrimaryKey("id");
		code = ModelGenerator.generateJavascript(model, OutputFormat.EXTJS4, false);
		assertThat(code)
				.isEqualTo(
						"Ext.define('App.Info',{extend:'Ext.data.Model',fields:[{name:'id',type:'int'}],associations:[{type:'belongsTo',model:'User',primaryKey:'id'}]});");

		association.setPrimaryKey(null);
		association.setGetterName("getUser");
		code = ModelGenerator.generateJavascript(model, OutputFormat.EXTJS4, false);
		assertThat(code)
				.isEqualTo(
						"Ext.define('App.Info',{extend:'Ext.data.Model',fields:[{name:'id',type:'int'}],associations:[{type:'belongsTo',model:'User',getterName:'getUser'}]});");

		association.setPrimaryKey(null);
		association.setSetterName("setUser");
		association.setForeignKey("user_id");
		code = ModelGenerator.generateJavascript(model, OutputFormat.EXTJS4, false);
		assertThat(code)
				.isEqualTo(
						"Ext.define('App.Info',{extend:'Ext.data.Model',fields:[{name:'id',type:'int'}],associations:[{type:'belongsTo',model:'User',foreignKey:'user_id',setterName:'setUser',getterName:'getUser'}]});");
	}

	@Test
	public void testFromModelBeanWithBelongsToAssociationTouch() {
		ModelBean model = new ModelBean();
		model.setName("App.Info");

		Map<String, ModelFieldBean> fields = new HashMap<String, ModelFieldBean>();
		fields.put("id", new ModelFieldBean("id", ModelType.INTEGER));
		model.setFields(fields);

		ModelAssociationBean association = new ModelAssociationBean(ModelAssociationType.BELONGS_TO, "User");
		model.addAssociation(association);

		String code = ModelGenerator.generateJavascript(model, OutputFormat.TOUCH2, false);
		assertThat(code)
				.isEqualTo(
						"Ext.define('App.Info',{extend:'Ext.data.Model',config:{fields:[{name:'id',type:'int'}],associations:[{type:'belongsTo',model:'User'}]}});");

		association.setAssociationKey("test");
		code = ModelGenerator.generateJavascript(model, OutputFormat.TOUCH2, false);
		assertThat(code)
				.isEqualTo(
						"Ext.define('App.Info',{extend:'Ext.data.Model',config:{fields:[{name:'id',type:'int'}],associations:[{type:'belongsTo',model:'User',associationKey:'test'}]}});");

		association.setAssociationKey(null);
		association.setPrimaryKey("id");
		code = ModelGenerator.generateJavascript(model, OutputFormat.TOUCH2, false);
		assertThat(code)
				.isEqualTo(
						"Ext.define('App.Info',{extend:'Ext.data.Model',config:{fields:[{name:'id',type:'int'}],associations:[{type:'belongsTo',model:'User',primaryKey:'id'}]}});");

		association.setPrimaryKey(null);
		association.setGetterName("getUser");
		code = ModelGenerator.generateJavascript(model, OutputFormat.TOUCH2, false);
		assertThat(code)
				.isEqualTo(
						"Ext.define('App.Info',{extend:'Ext.data.Model',config:{fields:[{name:'id',type:'int'}],associations:[{type:'belongsTo',model:'User',getterName:'getUser'}]}});");

		association.setPrimaryKey(null);
		association.setSetterName("setUser");
		association.setForeignKey("user_id");
		code = ModelGenerator.generateJavascript(model, OutputFormat.TOUCH2, false);
		assertThat(code)
				.isEqualTo(
						"Ext.define('App.Info',{extend:'Ext.data.Model',config:{fields:[{name:'id',type:'int'}],associations:[{type:'belongsTo',model:'User',foreignKey:'user_id',setterName:'setUser',getterName:'getUser'}]}});");
	}

	@Test
	public void testFromModelBeanWithHasOneAssociationExtJs() {
		ModelBean model = new ModelBean();
		model.setName("App.Info");

		Map<String, ModelFieldBean> fields = new HashMap<String, ModelFieldBean>();
		fields.put("id", new ModelFieldBean("id", ModelType.INTEGER));
		model.setFields(fields);

		ModelAssociationBean association = new ModelAssociationBean(ModelAssociationType.HAS_ONE, "User");
		model.addAssociation(association);

		String code = ModelGenerator.generateJavascript(model, OutputFormat.EXTJS4, false);
		assertThat(code)
				.isEqualTo(
						"Ext.define('App.Info',{extend:'Ext.data.Model',fields:[{name:'id',type:'int'}],associations:[{type:'hasOne',model:'User'}]});");

		association.setAssociationKey("test");
		code = ModelGenerator.generateJavascript(model, OutputFormat.EXTJS4, false);
		assertThat(code)
				.isEqualTo(
						"Ext.define('App.Info',{extend:'Ext.data.Model',fields:[{name:'id',type:'int'}],associations:[{type:'hasOne',model:'User',associationKey:'test'}]});");

		association.setAssociationKey(null);
		association.setPrimaryKey("id");
		code = ModelGenerator.generateJavascript(model, OutputFormat.EXTJS4, false);
		assertThat(code)
				.isEqualTo(
						"Ext.define('App.Info',{extend:'Ext.data.Model',fields:[{name:'id',type:'int'}],associations:[{type:'hasOne',model:'User',primaryKey:'id'}]});");

		association.setPrimaryKey(null);
		association.setGetterName("getUser");
		code = ModelGenerator.generateJavascript(model, OutputFormat.EXTJS4, false);
		assertThat(code)
				.isEqualTo(
						"Ext.define('App.Info',{extend:'Ext.data.Model',fields:[{name:'id',type:'int'}],associations:[{type:'hasOne',model:'User',getterName:'getUser'}]});");

		association.setPrimaryKey(null);
		association.setSetterName("setUser");
		association.setForeignKey("user_id");
		code = ModelGenerator.generateJavascript(model, OutputFormat.EXTJS4, false);
		assertThat(code)
				.isEqualTo(
						"Ext.define('App.Info',{extend:'Ext.data.Model',fields:[{name:'id',type:'int'}],associations:[{type:'hasOne',model:'User',foreignKey:'user_id',setterName:'setUser',getterName:'getUser'}]});");
	}

	@Test
	public void testFromModelBeanWithHasOneAssociationTouch() {
		ModelBean model = new ModelBean();
		model.setName("App.Info");

		Map<String, ModelFieldBean> fields = new HashMap<String, ModelFieldBean>();
		fields.put("id", new ModelFieldBean("id", ModelType.INTEGER));
		model.setFields(fields);

		ModelAssociationBean association = new ModelAssociationBean(ModelAssociationType.HAS_ONE, "User");
		model.addAssociation(association);

		String code = ModelGenerator.generateJavascript(model, OutputFormat.TOUCH2, false);
		assertThat(code)
				.isEqualTo(
						"Ext.define('App.Info',{extend:'Ext.data.Model',config:{fields:[{name:'id',type:'int'}],associations:[{type:'hasOne',model:'User'}]}});");

		association.setAssociationKey("test");
		code = ModelGenerator.generateJavascript(model, OutputFormat.TOUCH2, false);
		assertThat(code)
				.isEqualTo(
						"Ext.define('App.Info',{extend:'Ext.data.Model',config:{fields:[{name:'id',type:'int'}],associations:[{type:'hasOne',model:'User',associationKey:'test'}]}});");

		association.setAssociationKey(null);
		association.setPrimaryKey("id");
		code = ModelGenerator.generateJavascript(model, OutputFormat.TOUCH2, false);
		assertThat(code)
				.isEqualTo(
						"Ext.define('App.Info',{extend:'Ext.data.Model',config:{fields:[{name:'id',type:'int'}],associations:[{type:'hasOne',model:'User',primaryKey:'id'}]}});");

		association.setPrimaryKey(null);
		association.setGetterName("getUser");
		code = ModelGenerator.generateJavascript(model, OutputFormat.TOUCH2, false);
		assertThat(code)
				.isEqualTo(
						"Ext.define('App.Info',{extend:'Ext.data.Model',config:{fields:[{name:'id',type:'int'}],associations:[{type:'hasOne',model:'User',getterName:'getUser'}]}});");

		association.setPrimaryKey(null);
		association.setSetterName("setUser");
		association.setForeignKey("user_id");
		code = ModelGenerator.generateJavascript(model, OutputFormat.TOUCH2, false);
		assertThat(code)
				.isEqualTo(
						"Ext.define('App.Info',{extend:'Ext.data.Model',config:{fields:[{name:'id',type:'int'}],associations:[{type:'hasOne',model:'User',foreignKey:'user_id',setterName:'setUser',getterName:'getUser'}]}});");
	}

}
