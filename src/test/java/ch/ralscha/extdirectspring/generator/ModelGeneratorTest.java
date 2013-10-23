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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import ch.ralscha.extdirectspring.generator.association.AbstractAssociation;
import ch.ralscha.extdirectspring.generator.association.BelongsToAssociation;
import ch.ralscha.extdirectspring.generator.association.HasManyAssociation;
import ch.ralscha.extdirectspring.generator.association.HasOneAssociation;

public class ModelGeneratorTest {

	@Before
	public void clearCaches() {
		ModelGenerator.clearCaches();
	}

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
						"Ext.define(\"App.User\",{extend:\"Ext.data.Model\",fields:[{name:\"id\",type:\"int\"},{name:\"name\",type:\"string\"}]});");

		ModelFieldBean getIdField = model.getField("id");
		assertThat(getIdField).isEqualsToByComparingFields(idField);

		ModelFieldBean getNameField = model.getField("name");
		assertThat(getNameField).isEqualsToByComparingFields(nameField);

		ModelGenerator.clearCaches();
		nameField.setType(ModelType.INTEGER);
		code = ModelGenerator.generateJavascript(model, OutputFormat.EXTJS4, false);
		assertThat(code)
				.isEqualTo(
						"Ext.define(\"App.User\",{extend:\"Ext.data.Model\",fields:[{name:\"id\",type:\"int\"},{name:\"name\",type:\"int\"}]});");

		ModelGenerator.clearCaches();
		idField.setName("nameOfId");
		code = ModelGenerator.generateJavascript(model, OutputFormat.EXTJS4, false);
		assertThat(code)
				.isEqualTo(
						"Ext.define(\"App.User\",{extend:\"Ext.data.Model\",fields:[{name:\"nameOfId\",type:\"int\"},{name:\"name\",type:\"int\"}]});");
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
						"Ext.define(\"App.User\",{extend:\"Ext.data.Model\",fields:[{name:\"id\",type:\"int\"},{name:\"salary\",type:\"float\",useNull:true}]});");

		code = ModelGenerator.generateJavascript(model, OutputFormat.EXTJS4, false);
		assertThat(code)
				.isEqualTo(
						"Ext.define(\"App.User\",{extend:\"Ext.data.Model\",fields:[{name:\"id\",type:\"int\"},{name:\"salary\",type:\"float\",useNull:true}]});");
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
						"Ext.define(\"App.Info\",{extend:\"Ext.data.Model\",fields:[{name:\"id\",type:\"int\"},{name:\"active\",type:\"boolean\",defaultValue:true},{name:\"date\",type:\"date\",dateFormat:\"c\"}]});");

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
						"Ext.define(\"App.User\",{extend:\"Ext.data.Model\",config:{fields:[{name:\"id\",type:\"int\"},{name:\"name\",type:\"string\"}]}});");

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
						"Ext.define(\"App.User\",{extend:\"Ext.data.Model\",config:{fields:[{name:\"id\",type:\"int\"},{name:\"salary\",type:\"float\",useNull:true}]}});");

		code = ModelGenerator.generateJavascript(model, OutputFormat.TOUCH2, false);
		assertThat(code)
				.isEqualTo(
						"Ext.define(\"App.User\",{extend:\"Ext.data.Model\",config:{fields:[{name:\"id\",type:\"int\"},{name:\"salary\",type:\"float\",useNull:true}]}});");
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
						"Ext.define(\"App.Info\",{extend:\"Ext.data.Model\",config:{fields:[{name:\"id\",type:\"int\"},{name:\"active\",type:\"boolean\",defaultValue:true},{name:\"date\",type:\"date\",dateFormat:\"c\"}]}});");

	}

	@Test
	public void testFromModelBeanWithHasManyAssociationExtJs() {
		ModelBean model = new ModelBean();
		model.setName("App.Info");

		Map<String, ModelFieldBean> fields = new HashMap<String, ModelFieldBean>();
		fields.put("id", new ModelFieldBean("id", ModelType.INTEGER));
		model.setFields(fields);

		HasManyAssociation association = new HasManyAssociation("User");
		model.addAssociation(association);

		String code = ModelGenerator.generateJavascript(model, OutputFormat.EXTJS4, false);
		assertThat(code)
				.isEqualTo(
						"Ext.define(\"App.Info\",{extend:\"Ext.data.Model\",uses:[\"User\"],fields:[{name:\"id\",type:\"int\"}],associations:[{type:\"hasMany\",model:\"User\"}]});");

		ModelGenerator.clearCaches();
		association.setAutoLoad(false);
		association.setAssociationKey("test");
		code = ModelGenerator.generateJavascript(model, OutputFormat.EXTJS4, false);
		assertThat(code)
				.isEqualTo(
						"Ext.define(\"App.Info\",{extend:\"Ext.data.Model\",uses:[\"User\"],fields:[{name:\"id\",type:\"int\"}],associations:[{type:\"hasMany\",model:\"User\",associationKey:\"test\",autoLoad:false}]});");

		ModelGenerator.clearCaches();
		association.setAutoLoad(true);
		association.setAssociationKey(null);
		association.setPrimaryKey("id");
		code = ModelGenerator.generateJavascript(model, OutputFormat.EXTJS4, false);
		assertThat(code)
				.isEqualTo(
						"Ext.define(\"App.Info\",{extend:\"Ext.data.Model\",uses:[\"User\"],fields:[{name:\"id\",type:\"int\"}],associations:[{type:\"hasMany\",model:\"User\",primaryKey:\"id\",autoLoad:true}]});");

		ModelGenerator.clearCaches();
		association.setPrimaryKey(null);
		association.setName("users");
		code = ModelGenerator.generateJavascript(model, OutputFormat.EXTJS4, false);
		assertThat(code)
				.isEqualTo(
						"Ext.define(\"App.Info\",{extend:\"Ext.data.Model\",uses:[\"User\"],fields:[{name:\"id\",type:\"int\"}],associations:[{type:\"hasMany\",model:\"User\",autoLoad:true,name:\"users\"}]});");

		ModelGenerator.clearCaches();
		association.setPrimaryKey(null);
		association.setName(null);
		association.setForeignKey("user_id");
		code = ModelGenerator.generateJavascript(model, OutputFormat.EXTJS4, false);
		assertThat(code)
				.isEqualTo(
						"Ext.define(\"App.Info\",{extend:\"Ext.data.Model\",uses:[\"User\"],fields:[{name:\"id\",type:\"int\"}],associations:[{type:\"hasMany\",model:\"User\",foreignKey:\"user_id\",autoLoad:true}]});");
	}

	@Test
	public void testFromModelBeanWithHasManyAssociationTouch() {
		ModelBean model = new ModelBean();
		model.setName("App.Info");

		Map<String, ModelFieldBean> fields = new HashMap<String, ModelFieldBean>();
		fields.put("id", new ModelFieldBean("id", ModelType.INTEGER));
		model.setFields(fields);

		HasManyAssociation association = new HasManyAssociation("User");
		model.addAssociation(association);

		String code = ModelGenerator.generateJavascript(model, OutputFormat.TOUCH2, false);
		assertThat(code)
				.isEqualTo(
						"Ext.define(\"App.Info\",{extend:\"Ext.data.Model\",uses:[\"User\"],config:{fields:[{name:\"id\",type:\"int\"}],associations:[{type:\"hasMany\",model:\"User\"}]}});");

		ModelGenerator.clearCaches();
		association.setAutoLoad(false);
		association.setAssociationKey("test");
		code = ModelGenerator.generateJavascript(model, OutputFormat.TOUCH2, false);
		assertThat(code)
				.isEqualTo(
						"Ext.define(\"App.Info\",{extend:\"Ext.data.Model\",uses:[\"User\"],config:{fields:[{name:\"id\",type:\"int\"}],associations:[{type:\"hasMany\",model:\"User\",associationKey:\"test\",autoLoad:false}]}});");

		ModelGenerator.clearCaches();
		association.setAutoLoad(true);
		association.setAssociationKey(null);
		association.setPrimaryKey("id");
		code = ModelGenerator.generateJavascript(model, OutputFormat.TOUCH2, false);
		assertThat(code)
				.isEqualTo(
						"Ext.define(\"App.Info\",{extend:\"Ext.data.Model\",uses:[\"User\"],config:{fields:[{name:\"id\",type:\"int\"}],associations:[{type:\"hasMany\",model:\"User\",primaryKey:\"id\",autoLoad:true}]}});");

		ModelGenerator.clearCaches();
		association.setPrimaryKey(null);
		association.setName("users");
		code = ModelGenerator.generateJavascript(model, OutputFormat.TOUCH2, false);
		assertThat(code)
				.isEqualTo(
						"Ext.define(\"App.Info\",{extend:\"Ext.data.Model\",uses:[\"User\"],config:{fields:[{name:\"id\",type:\"int\"}],associations:[{type:\"hasMany\",model:\"User\",autoLoad:true,name:\"users\"}]}});");

		ModelGenerator.clearCaches();
		association.setPrimaryKey(null);
		association.setName(null);
		association.setForeignKey("user_id");
		code = ModelGenerator.generateJavascript(model, OutputFormat.TOUCH2, false);
		assertThat(code)
				.isEqualTo(
						"Ext.define(\"App.Info\",{extend:\"Ext.data.Model\",uses:[\"User\"],config:{fields:[{name:\"id\",type:\"int\"}],associations:[{type:\"hasMany\",model:\"User\",foreignKey:\"user_id\",autoLoad:true}]}});");
	}

	@Test
	public void testFromModelBeanWithBelongsToAssociationExtJs() {
		ModelBean model = new ModelBean();
		model.setName("App.Info");

		Map<String, ModelFieldBean> fields = new HashMap<String, ModelFieldBean>();
		fields.put("id", new ModelFieldBean("id", ModelType.INTEGER));
		model.setFields(fields);

		BelongsToAssociation association = new BelongsToAssociation("User");
		model.setAssociations(Collections.<AbstractAssociation> singletonList(association));

		String code = ModelGenerator.generateJavascript(model, OutputFormat.EXTJS4, false);
		assertThat(code)
				.isEqualTo(
						"Ext.define(\"App.Info\",{extend:\"Ext.data.Model\",uses:[\"User\"],fields:[{name:\"id\",type:\"int\"}],associations:[{type:\"belongsTo\",model:\"User\"}]});");

		ModelGenerator.clearCaches();
		association.setAssociationKey("test");
		code = ModelGenerator.generateJavascript(model, OutputFormat.EXTJS4, false);
		assertThat(code)
				.isEqualTo(
						"Ext.define(\"App.Info\",{extend:\"Ext.data.Model\",uses:[\"User\"],fields:[{name:\"id\",type:\"int\"}],associations:[{type:\"belongsTo\",model:\"User\",associationKey:\"test\"}]});");

		ModelGenerator.clearCaches();
		association.setAssociationKey(null);
		association.setPrimaryKey("id");
		code = ModelGenerator.generateJavascript(model, OutputFormat.EXTJS4, false);
		assertThat(code)
				.isEqualTo(
						"Ext.define(\"App.Info\",{extend:\"Ext.data.Model\",uses:[\"User\"],fields:[{name:\"id\",type:\"int\"}],associations:[{type:\"belongsTo\",model:\"User\",primaryKey:\"id\"}]});");

		ModelGenerator.clearCaches();
		association.setPrimaryKey(null);
		association.setGetterName("getUser");
		code = ModelGenerator.generateJavascript(model, OutputFormat.EXTJS4, false);
		assertThat(code)
				.isEqualTo(
						"Ext.define(\"App.Info\",{extend:\"Ext.data.Model\",uses:[\"User\"],fields:[{name:\"id\",type:\"int\"}],associations:[{type:\"belongsTo\",model:\"User\",getterName:\"getUser\"}]});");

		ModelGenerator.clearCaches();
		association.setPrimaryKey(null);
		association.setSetterName("setUser");
		association.setForeignKey("user_id");
		code = ModelGenerator.generateJavascript(model, OutputFormat.EXTJS4, false);
		assertThat(code)
				.isEqualTo(
						"Ext.define(\"App.Info\",{extend:\"Ext.data.Model\",uses:[\"User\"],fields:[{name:\"id\",type:\"int\"}],associations:[{type:\"belongsTo\",model:\"User\",foreignKey:\"user_id\",setterName:\"setUser\",getterName:\"getUser\"}]});");
	}

	@Test
	public void testFromModelBeanWithBelongsToAssociationTouch() {
		ModelBean model = new ModelBean();
		model.setName("App.Info");

		Map<String, ModelFieldBean> fields = new HashMap<String, ModelFieldBean>();
		fields.put("id", new ModelFieldBean("id", ModelType.INTEGER));
		model.setFields(fields);

		BelongsToAssociation association = new BelongsToAssociation("User");
		model.addAssociation(association);

		String code = ModelGenerator.generateJavascript(model, OutputFormat.TOUCH2, false);
		assertThat(code)
				.isEqualTo(
						"Ext.define(\"App.Info\",{extend:\"Ext.data.Model\",uses:[\"User\"],config:{fields:[{name:\"id\",type:\"int\"}],associations:[{type:\"belongsTo\",model:\"User\"}]}});");

		ModelGenerator.clearCaches();
		association.setAssociationKey("test");
		code = ModelGenerator.generateJavascript(model, OutputFormat.TOUCH2, false);
		assertThat(code)
				.isEqualTo(
						"Ext.define(\"App.Info\",{extend:\"Ext.data.Model\",uses:[\"User\"],config:{fields:[{name:\"id\",type:\"int\"}],associations:[{type:\"belongsTo\",model:\"User\",associationKey:\"test\"}]}});");

		ModelGenerator.clearCaches();
		association.setAssociationKey(null);
		association.setPrimaryKey("id");
		code = ModelGenerator.generateJavascript(model, OutputFormat.TOUCH2, false);
		assertThat(code)
				.isEqualTo(
						"Ext.define(\"App.Info\",{extend:\"Ext.data.Model\",uses:[\"User\"],config:{fields:[{name:\"id\",type:\"int\"}],associations:[{type:\"belongsTo\",model:\"User\",primaryKey:\"id\"}]}});");

		ModelGenerator.clearCaches();
		association.setPrimaryKey(null);
		association.setGetterName("getUser");
		code = ModelGenerator.generateJavascript(model, OutputFormat.TOUCH2, false);
		assertThat(code)
				.isEqualTo(
						"Ext.define(\"App.Info\",{extend:\"Ext.data.Model\",uses:[\"User\"],config:{fields:[{name:\"id\",type:\"int\"}],associations:[{type:\"belongsTo\",model:\"User\",getterName:\"getUser\"}]}});");

		ModelGenerator.clearCaches();
		association.setPrimaryKey(null);
		association.setSetterName("setUser");
		association.setForeignKey("user_id");
		code = ModelGenerator.generateJavascript(model, OutputFormat.TOUCH2, false);
		assertThat(code)
				.isEqualTo(
						"Ext.define(\"App.Info\",{extend:\"Ext.data.Model\",uses:[\"User\"],config:{fields:[{name:\"id\",type:\"int\"}],associations:[{type:\"belongsTo\",model:\"User\",foreignKey:\"user_id\",setterName:\"setUser\",getterName:\"getUser\"}]}});");
	}

	@Test
	public void testFromModelBeanWithHasOneAssociationExtJs() {
		ModelBean model = new ModelBean();
		model.setName("App.Info");

		Map<String, ModelFieldBean> fields = new HashMap<String, ModelFieldBean>();
		fields.put("id", new ModelFieldBean("id", ModelType.INTEGER));
		model.setFields(fields);

		HasOneAssociation association = new HasOneAssociation("User");
		model.addAssociation(association);

		String code = ModelGenerator.generateJavascript(model, OutputFormat.EXTJS4, false);
		assertThat(code)
				.isEqualTo(
						"Ext.define(\"App.Info\",{extend:\"Ext.data.Model\",uses:[\"User\"],fields:[{name:\"id\",type:\"int\"}],associations:[{type:\"hasOne\",model:\"User\"}]});");

		ModelGenerator.clearCaches();
		association.setAssociationKey("test");
		code = ModelGenerator.generateJavascript(model, OutputFormat.EXTJS4, false);
		assertThat(code)
				.isEqualTo(
						"Ext.define(\"App.Info\",{extend:\"Ext.data.Model\",uses:[\"User\"],fields:[{name:\"id\",type:\"int\"}],associations:[{type:\"hasOne\",model:\"User\",associationKey:\"test\"}]});");

		ModelGenerator.clearCaches();
		association.setAssociationKey(null);
		association.setPrimaryKey("id");
		code = ModelGenerator.generateJavascript(model, OutputFormat.EXTJS4, false);
		assertThat(code)
				.isEqualTo(
						"Ext.define(\"App.Info\",{extend:\"Ext.data.Model\",uses:[\"User\"],fields:[{name:\"id\",type:\"int\"}],associations:[{type:\"hasOne\",model:\"User\",primaryKey:\"id\"}]});");

		ModelGenerator.clearCaches();
		association.setPrimaryKey(null);
		association.setGetterName("getUser");
		code = ModelGenerator.generateJavascript(model, OutputFormat.EXTJS4, false);
		assertThat(code)
				.isEqualTo(
						"Ext.define(\"App.Info\",{extend:\"Ext.data.Model\",uses:[\"User\"],fields:[{name:\"id\",type:\"int\"}],associations:[{type:\"hasOne\",model:\"User\",getterName:\"getUser\"}]});");

		ModelGenerator.clearCaches();
		association.setPrimaryKey(null);
		association.setSetterName("setUser");
		association.setForeignKey("user_id");
		code = ModelGenerator.generateJavascript(model, OutputFormat.EXTJS4, false);
		assertThat(code)
				.isEqualTo(
						"Ext.define(\"App.Info\",{extend:\"Ext.data.Model\",uses:[\"User\"],fields:[{name:\"id\",type:\"int\"}],associations:[{type:\"hasOne\",model:\"User\",foreignKey:\"user_id\",setterName:\"setUser\",getterName:\"getUser\"}]});");
	}

	@Test
	public void testFromModelBeanWithHasOneAssociationTouch() {
		ModelBean model = new ModelBean();
		model.setName("App.Info");

		Map<String, ModelFieldBean> fields = new HashMap<String, ModelFieldBean>();
		fields.put("id", new ModelFieldBean("id", ModelType.INTEGER));
		model.setFields(fields);

		HasOneAssociation association = new HasOneAssociation("User");
		model.addAssociation(association);

		String code = ModelGenerator.generateJavascript(model, OutputFormat.TOUCH2, false);
		assertThat(code)
				.isEqualTo(
						"Ext.define(\"App.Info\",{extend:\"Ext.data.Model\",uses:[\"User\"],config:{fields:[{name:\"id\",type:\"int\"}],associations:[{type:\"hasOne\",model:\"User\"}]}});");

		ModelGenerator.clearCaches();
		association.setAssociationKey("test");
		code = ModelGenerator.generateJavascript(model, OutputFormat.TOUCH2, false);
		assertThat(code)
				.isEqualTo(
						"Ext.define(\"App.Info\",{extend:\"Ext.data.Model\",uses:[\"User\"],config:{fields:[{name:\"id\",type:\"int\"}],associations:[{type:\"hasOne\",model:\"User\",associationKey:\"test\"}]}});");

		ModelGenerator.clearCaches();
		association.setAssociationKey(null);
		association.setPrimaryKey("id");
		code = ModelGenerator.generateJavascript(model, OutputFormat.TOUCH2, false);
		assertThat(code)
				.isEqualTo(
						"Ext.define(\"App.Info\",{extend:\"Ext.data.Model\",uses:[\"User\"],config:{fields:[{name:\"id\",type:\"int\"}],associations:[{type:\"hasOne\",model:\"User\",primaryKey:\"id\"}]}});");

		ModelGenerator.clearCaches();
		association.setPrimaryKey(null);
		association.setGetterName("getUser");
		code = ModelGenerator.generateJavascript(model, OutputFormat.TOUCH2, false);
		assertThat(code)
				.isEqualTo(
						"Ext.define(\"App.Info\",{extend:\"Ext.data.Model\",uses:[\"User\"],config:{fields:[{name:\"id\",type:\"int\"}],associations:[{type:\"hasOne\",model:\"User\",getterName:\"getUser\"}]}});");

		ModelGenerator.clearCaches();
		association.setPrimaryKey(null);
		association.setSetterName("setUser");
		association.setForeignKey("user_id");
		code = ModelGenerator.generateJavascript(model, OutputFormat.TOUCH2, false);
		assertThat(code)
				.isEqualTo(
						"Ext.define(\"App.Info\",{extend:\"Ext.data.Model\",uses:[\"User\"],config:{fields:[{name:\"id\",type:\"int\"}],associations:[{type:\"hasOne\",model:\"User\",foreignKey:\"user_id\",setterName:\"setUser\",getterName:\"getUser\"}]}});");
	}

	@Test
	public void testInstanceNameExtJs() {
		ModelBean model = new ModelBean();
		model.setName("App.Info");

		Map<String, ModelFieldBean> fields = new HashMap<String, ModelFieldBean>();
		fields.put("id", new ModelFieldBean("id", ModelType.INTEGER));
		model.setFields(fields);

		HasOneAssociation association = new HasOneAssociation("User");
		association.setInstanceName("userBelongsToInstance");
		model.addAssociation(association);

		String code = ModelGenerator.generateJavascript(model, OutputFormat.EXTJS4, false);
		assertThat(code)
				.isEqualTo(
						"Ext.define(\"App.Info\",{extend:\"Ext.data.Model\",uses:[\"User\"],fields:[{name:\"id\",type:\"int\"}],associations:[{type:\"hasOne\",model:\"User\",instanceName:\"userBelongsToInstance\"}]});");

		ModelGenerator.clearCaches();
		association.setAssociationKey("test");
		association.setInstanceName("userBelongsToInstance");
		code = ModelGenerator.generateJavascript(model, OutputFormat.EXTJS4, false);
		assertThat(code)
				.isEqualTo(
						"Ext.define(\"App.Info\",{extend:\"Ext.data.Model\",uses:[\"User\"],fields:[{name:\"id\",type:\"int\"}],associations:[{type:\"hasOne\",model:\"User\",associationKey:\"test\",instanceName:\"userBelongsToInstance\"}]});");

		ModelGenerator.clearCaches();
		association.setAssociationKey(null);
		association.setPrimaryKey("id");
		association.setInstanceName("userBelongsToInstance");
		code = ModelGenerator.generateJavascript(model, OutputFormat.EXTJS4, false);
		assertThat(code)
				.isEqualTo(
						"Ext.define(\"App.Info\",{extend:\"Ext.data.Model\",uses:[\"User\"],fields:[{name:\"id\",type:\"int\"}],associations:[{type:\"hasOne\",model:\"User\",primaryKey:\"id\",instanceName:\"userBelongsToInstance\"}]});");

		ModelGenerator.clearCaches();
		association.setPrimaryKey(null);
		association.setGetterName("getUser");
		association.setInstanceName("userBelongsToInstance");
		code = ModelGenerator.generateJavascript(model, OutputFormat.EXTJS4, false);
		assertThat(code)
				.isEqualTo(
						"Ext.define(\"App.Info\",{extend:\"Ext.data.Model\",uses:[\"User\"],fields:[{name:\"id\",type:\"int\"}],associations:[{type:\"hasOne\",model:\"User\",instanceName:\"userBelongsToInstance\",getterName:\"getUser\"}]});");

		ModelGenerator.clearCaches();
		association.setPrimaryKey(null);
		association.setSetterName("setUser");
		association.setForeignKey("user_id");
		association.setInstanceName("userBelongsToInstance");
		code = ModelGenerator.generateJavascript(model, OutputFormat.EXTJS4, false);
		assertThat(code)
				.isEqualTo(
						"Ext.define(\"App.Info\",{extend:\"Ext.data.Model\",uses:[\"User\"],fields:[{name:\"id\",type:\"int\"}],associations:[{type:\"hasOne\",model:\"User\",foreignKey:\"user_id\",instanceName:\"userBelongsToInstance\",setterName:\"setUser\",getterName:\"getUser\"}]});");
	}

	@Test
	public void testMessagePropertyExtJS() {
		ModelBean model = new ModelBean();
		model.setName("App.User");
		model.setPaging(true);
		model.setMessageProperty("mp");
		model.setReadMethod("read");

		ModelFieldBean idField = new ModelFieldBean("id", ModelType.INTEGER);
		model.addField(idField);
		ModelFieldBean nameField = new ModelFieldBean("name", ModelType.STRING);
		model.addField(nameField);

		OutputConfig config = new OutputConfig();
		config.setOutputFormat(OutputFormat.EXTJS4);
		config.setDebug(false);
		config.setSurroundApiWithQuotes(true);
		String code = ModelGenerator.generateJavascript(model, config);
		assertThat(code)
				.isEqualTo(
						"Ext.define(\"App.User\",{extend:\"Ext.data.Model\",fields:[{name:\"id\",type:\"int\"},{name:\"name\",type:\"string\"}],proxy:{type:\"direct\",directFn:\"read\",reader:{messageProperty:\"mp\",root:\"records\"}}});");
	}

	@Test
	public void testDisablePagingExtJS() {
		ModelBean model = new ModelBean();
		model.setName("App.User");
		model.setPaging(true);
		model.setMessageProperty("mp");
		model.setReadMethod("read");
		model.setDisablePagingParameters(true);

		ModelFieldBean idField = new ModelFieldBean("id", ModelType.INTEGER);
		model.addField(idField);
		ModelFieldBean nameField = new ModelFieldBean("name", ModelType.STRING);
		model.addField(nameField);

		OutputConfig config = new OutputConfig();
		config.setOutputFormat(OutputFormat.EXTJS4);
		config.setDebug(false);
		config.setSurroundApiWithQuotes(true);
		String code = ModelGenerator.generateJavascript(model, config);
		assertThat(code)
				.isEqualTo(
						"Ext.define(\"App.User\",{extend:\"Ext.data.Model\",fields:[{name:\"id\",type:\"int\"},{name:\"name\",type:\"string\"}],proxy:{type:\"direct\",pageParam:undefined,startParam:undefined,limitParam:undefined,directFn:\"read\",reader:{messageProperty:\"mp\",root:\"records\"}}});");
	}

	@Test
	public void testDisablePagingTouch() {
		ModelBean model = new ModelBean();
		model.setName("App.User");
		model.setPaging(true);
		model.setMessageProperty("mp");
		model.setReadMethod("read");
		model.setDisablePagingParameters(true);

		ModelFieldBean idField = new ModelFieldBean("id", ModelType.INTEGER);
		model.addField(idField);
		ModelFieldBean nameField = new ModelFieldBean("name", ModelType.STRING);
		model.addField(nameField);

		OutputConfig config = new OutputConfig();
		config.setOutputFormat(OutputFormat.TOUCH2);
		config.setDebug(false);
		config.setSurroundApiWithQuotes(true);
		String code = ModelGenerator.generateJavascript(model, config);
		assertThat(code)
				.isEqualTo(
						"Ext.define(\"App.User\",{extend:\"Ext.data.Model\",config:{fields:[{name:\"id\",type:\"int\"},{name:\"name\",type:\"string\"}],proxy:{type:\"direct\",pageParam:false,startParam:false,limitParam:false,directFn:\"read\",reader:{rootProperty:\"records\",messageProperty:\"mp\"}}}});");
	}
}
