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
		model.addField(new ModelFieldBean("id", ModelType.INTEGER));
		model.addField(new ModelFieldBean("name", ModelType.STRING));

		String code = ModelGenerator.generateJavascript(model, OutputFormat.EXTJS4, false);
		assertThat(code)
				.isEqualTo(
						"Ext.define('App.User',{extend:'Ext.data.Model',fields:[{name:'id',type:'int'},{name:'name',type:'string'}]});");
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
