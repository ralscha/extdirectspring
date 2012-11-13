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

import java.io.IOException;

import org.springframework.util.StringUtils;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

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

	@JsonSerialize(using = ModelAssociationTypeSerializer.class)
	public ModelAssociationType getType() {
		return type;
	}

	public String getModel() {
		return model;
	}

	private final static class ModelAssociationTypeSerializer extends JsonSerializer<ModelAssociationType> {
		@Override
		public void serialize(ModelAssociationType value, JsonGenerator jgen, SerializerProvider provider)
				throws IOException, JsonProcessingException {
			jgen.writeString(value.getJsName());

		}
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((associationKey == null) ? 0 : associationKey.hashCode());
		result = prime * result + ((autoLoad == null) ? 0 : autoLoad.hashCode());
		result = prime * result + ((foreignKey == null) ? 0 : foreignKey.hashCode());
		result = prime * result + ((getterName == null) ? 0 : getterName.hashCode());
		result = prime * result + ((model == null) ? 0 : model.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((primaryKey == null) ? 0 : primaryKey.hashCode());
		result = prime * result + ((setterName == null) ? 0 : setterName.hashCode());
		result = prime * result + ((type == null) ? 0 : type.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		ModelAssociationBean other = (ModelAssociationBean) obj;
		if (associationKey == null) {
			if (other.associationKey != null) {
				return false;
			}
		} else if (!associationKey.equals(other.associationKey)) {
			return false;
		}
		if (autoLoad == null) {
			if (other.autoLoad != null) {
				return false;
			}
		} else if (!autoLoad.equals(other.autoLoad)) {
			return false;
		}
		if (foreignKey == null) {
			if (other.foreignKey != null) {
				return false;
			}
		} else if (!foreignKey.equals(other.foreignKey)) {
			return false;
		}
		if (getterName == null) {
			if (other.getterName != null) {
				return false;
			}
		} else if (!getterName.equals(other.getterName)) {
			return false;
		}
		if (model == null) {
			if (other.model != null) {
				return false;
			}
		} else if (!model.equals(other.model)) {
			return false;
		}
		if (name == null) {
			if (other.name != null) {
				return false;
			}
		} else if (!name.equals(other.name)) {
			return false;
		}
		if (primaryKey == null) {
			if (other.primaryKey != null) {
				return false;
			}
		} else if (!primaryKey.equals(other.primaryKey)) {
			return false;
		}
		if (setterName == null) {
			if (other.setterName != null) {
				return false;
			}
		} else if (!setterName.equals(other.setterName)) {
			return false;
		}
		if (type != other.type) {
			return false;
		}
		return true;
	}

}
