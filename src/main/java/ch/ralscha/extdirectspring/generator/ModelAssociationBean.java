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

/**
 * This class represents an association to another object in a model.
 * 
 * @author Ralph Schaer
 */
@JsonInclude(Include.NON_NULL)
public class ModelAssociationBean {

	private final ModelAssociationType type;

	private final String model;

	private String associationKey;

	private Boolean autoLoad;

	private String foreignKey;

	private String name;

	private String primaryKey;

	private String setterName;

	private String getterName;

	/**
	 * Creates an instance of the ModelAssociationBean. Sets {@link #type} and
	 * {@link #model} to the provided parameters.
	 * 
	 * @param type The type of the association.
	 * @param model The name of the model that is being associated with.
	 */
	public ModelAssociationBean(ModelAssociationType type, String model) {
		this.type = type;
		this.model = model;
	}

	/**
	 * Creates an instance of the ModelAssociationBean. Sets {@link #type} to
	 * the provided value and sets {@link #model} to the full qualified name of
	 * the model class or the string from {@link Model#value()} if present on
	 * the class.
	 * 
	 * @param type The type of the association.
	 * @param model The class of the model that is being associated with.
	 */
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

	/**
	 * The name of the property in the data to read the association from.
	 * Defaults to the name of the associated model.
	 * <p>
	 * Corresponds to the <a href=
	 * "http://docs.sencha.com/ext-js/4-1/#!/api/Ext.data.association.Association-cfg-associationKey"
	 * >associationKey</a> config property.
	 * 
	 * @param associationKey name of the property in the json data
	 */
	public void setAssociationKey(String associationKey) {
		this.associationKey = associationKey;
	}

	public Boolean getAutoLoad() {
		return autoLoad;
	}

	/**
	 * True to automatically load the related store from a remote source when
	 * instantiated. Defaults to false.
	 * <p>
	 * Corresponds to the <a href=
	 * "http://docs.sencha.com/ext-js/4-1/#!/api/Ext.data.association.HasMany-cfg-autoLoad"
	 * >autoLoad</a> config property.
	 * <p>
	 * Only {@link ModelAssociationType#HAS_MANY} association support this
	 * property.
	 */
	public void setAutoLoad(Boolean autoLoad) {
		if (type != ModelAssociationType.HAS_MANY) {
			throw new IllegalArgumentException("Only hasMany association support the autoLoad property");
		}

		this.autoLoad = autoLoad;
	}

	public String getForeignKey() {
		return foreignKey;
	}

	/**
	 * The name of the foreign key on the associated model that links it to the
	 * owner model. Defaults to the lowercased name of the owner model plus
	 * "_id".
	 * <p>
	 * Corresponds to the <a href=
	 * "http://docs.sencha.com/ext-js/4-1/#!/api/Ext.data.association.HasMany-cfg-foreignKey"
	 * >foreignKey</a> config property.
	 */
	public void setForeignKey(String foreignKey) {
		this.foreignKey = foreignKey;
	}

	public String getName() {
		return name;
	}

	/**
	 * The name of the function to create on the owner model to retrieve the
	 * child store. If not specified, the pluralized name of the child model is
	 * used. Always specify this if the class name contains a package component.
	 * <p>
	 * Corresponds to the <a href=
	 * "http://docs.sencha.com/ext-js/4-1/#!/api/Ext.data.association.HasMany-cfg-name"
	 * >name</a> config property.
	 * <p>
	 * Only {@link ModelAssociationType#HAS_MANY} association support this
	 * property.
	 */
	public void setName(String name) {
		if (type != ModelAssociationType.HAS_MANY) {
			throw new IllegalArgumentException("Only hasMany association support the name property");
		}

		this.name = name;
	}

	public String getPrimaryKey() {
		return primaryKey;
	}

	/**
	 * The name of the primary key on the associated model. <br>
	 * In general this will be the value of {@link Model#idProperty()}.
	 * <p>
	 * Corresponds to the <a href=
	 * "http://docs.sencha.com/ext-js/4-1/#!/api/Ext.data.association.Association-cfg-primaryKey"
	 * >primaryKey</a> config property.
	 */
	public void setPrimaryKey(String primaryKey) {
		this.primaryKey = primaryKey;
	}

	public String getSetterName() {
		return setterName;
	}

	/**
	 * The name of the setter function that will be added to the local model's
	 * prototype. Defaults to 'set' + the name of the foreign model, e.g.
	 * setCategory.
	 * <p>
	 * Corresponds to the <a href=
	 * "http://docs.sencha.com/ext-js/4-1/#!/api/Ext.data.association.BelongsTo-cfg-setterName"
	 * >setterName</a> config property.
	 * <p>
	 * Only {@link ModelAssociationType#BELONGS_TO} and
	 * {@link ModelAssociationType#HAS_ONE} support this property.
	 */
	public void setSetterName(String setterName) {
		if (type != ModelAssociationType.BELONGS_TO && type != ModelAssociationType.HAS_ONE) {
			throw new IllegalArgumentException("Only belongsTo and hasOne associations support the setterName property");
		}
		this.setterName = setterName;
	}

	public String getGetterName() {
		return getterName;
	}

	/**
	 * The name of the getter function that will be added to the local model's
	 * prototype. Defaults to 'get' + the name of the foreign model, e.g.
	 * getCategory.
	 * <p>
	 * Corresponds to the <a href=
	 * "http://docs.sencha.com/ext-js/4-1/#!/api/Ext.data.association.BelongsTo-cfg-getterName"
	 * >getterName</a> config property.
	 * <p>
	 * Only {@link ModelAssociationType#BELONGS_TO} and
	 * {@link ModelAssociationType#HAS_ONE} support this property.
	 */
	public void setGetterName(String getterName) {
		if (type != ModelAssociationType.BELONGS_TO && type != ModelAssociationType.HAS_ONE) {
			throw new IllegalArgumentException("Only belongsTo and hasOne associations support the getterName property");
		}
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
