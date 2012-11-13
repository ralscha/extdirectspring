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

import org.springframework.util.StringUtils;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
public class ModelAssociationBean {

	private final ModelAssociationType type;

	private final String model; // hasMany, belongsTo, hasOne

	private String associationKey; // hasMany, belongsTo, hasOne

	private Boolean autoLoad; // hasMany

	private String foreignKey; // hasMany, belongsTo, hasOne

	private String name; // hasMany

	private String primaryKey; // hasMany, belongsTo, hasOne

	private String setterName; // belongTo, hasOne

	private String getterName; // belongTo, hasOne

	public ModelAssociationBean(ModelAssociationType type, String model) {
		this.type = type;
		this.model = model;
	}

	public ModelAssociationBean(ModelAssociationType type, Class<?> model) {
		this.type = type;
		
		Model modelAnnotation = model.getAnnotation(Model.class);
		
		if (modelAnnotation != null && StringUtils.hasText(modelAnnotation.value())) {
			this.model = modelAnnotation.value();
		} else {
			this.model = model.getName();
		}

		if (modelAnnotation != null) {
			primaryKey = modelAnnotation.idProperty();
		}
		
		if (this.type == ModelAssociationType.HAS_MANY) {
			//todo set name here
		}
		
	}

	public String getAssociationKey() {
		return associationKey;
	}

	public void setAssociationKey(String associationKey) {
		this.associationKey = associationKey;
	}

	public Boolean getAutoLoad() {
		return autoLoad;
	}

	public void setAutoLoad(Boolean autoLoad) {
		this.autoLoad = autoLoad;
	}

	public String getForeignKey() {
		return foreignKey;
	}

	public void setForeignKey(String foreignKey) {
		this.foreignKey = foreignKey;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPrimaryKey() {
		return primaryKey;
	}

	public void setPrimaryKey(String primaryKey) {
		this.primaryKey = primaryKey;
	}

	public String getSetterName() {
		return setterName;
	}

	public void setSetterName(String setterName) {
		this.setterName = setterName;
	}

	public String getGetterName() {
		return getterName;
	}

	public void setGetterName(String getterName) {
		this.getterName = getterName;
	}

	public ModelAssociationType getType() {
		return type;
	}

	public String getModel() {
		return model;
	}

}
