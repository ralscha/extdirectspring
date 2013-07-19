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
package ch.ralscha.extdirectspring.generator.association;

import java.lang.reflect.Field;

import org.apache.commons.logging.LogFactory;
import org.springframework.util.StringUtils;

import ch.ralscha.extdirectspring.generator.Model;
import ch.ralscha.extdirectspring.generator.ModelAssociation;
import ch.ralscha.extdirectspring.generator.ModelAssociationType;
import ch.ralscha.extdirectspring.generator.ModelBean;
import ch.ralscha.extdirectspring.generator.ModelGenerator;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

/**
 * Base class for the association objects
 */
@JsonInclude(Include.NON_NULL)
public abstract class AbstractAssociation {

	private final String type;

	private final String model;

	private String associationKey;

	private String foreignKey;

	private String primaryKey;

	private String instanceName;

	/**
	 * Creates an instance of the AbstractAssociation. Sets {@link #getType()}
	 * and {@link #getModel()} to the provided parameters.
	 * 
	 * @param type The type of the association.
	 * @param model The name of the model that is being associated with.
	 */
	public AbstractAssociation(String type, String model) {
		this.type = type;
		this.model = model;
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

	public String getForeignKey() {
		return foreignKey;
	}

	/**
	 * The name of the foreign key on the associated model that links it to the
	 * owner model. Defaults to the lowercase name of the owner model + "_id"
	 * (HAS_MANY) or to the field name (BELONGS_TO, HAS_ONE) + "_id".
	 * <p>
	 * Corresponds to the <a href=
	 * "http://docs.sencha.com/ext-js/4-1/#!/api/Ext.data.association.HasMany-cfg-foreignKey"
	 * >foreignKey</a> config property.
	 * 
	 * @param foreignKey the new name for the foreignKey
	 */
	public void setForeignKey(String foreignKey) {
		this.foreignKey = foreignKey;
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
	 * 
	 * @param primaryKey the new name for the primaryKey
	 */
	public void setPrimaryKey(String primaryKey) {
		this.primaryKey = primaryKey;
	}

	public String getType() {
		return type;
	}

	public String getModel() {
		return model;
	}

	public String getInstanceName() {
		return instanceName;
	}

	public void setInstanceName(String instanceName) {
		this.instanceName = instanceName;
	}

	protected static String getModelName(Class<?> model) {
		Model modelAnnotation = model.getAnnotation(Model.class);

		if (modelAnnotation != null && StringUtils.hasText(modelAnnotation.value())) {
			return modelAnnotation.value();
		}
		return model.getName();
	}

	public static AbstractAssociation createAssociation(ModelAssociation associationAnnotation, ModelBean model,
			Field field) {
		ModelAssociationType type = associationAnnotation.value();

		Class<?> associationClass = associationAnnotation.model();
		if (associationClass == Object.class) {
			associationClass = field.getType();
		}

		AbstractAssociation association;

		if (type == ModelAssociationType.HAS_MANY) {
			association = new HasManyAssociation(associationClass);
		} else if (type == ModelAssociationType.BELONGS_TO) {
			association = new BelongsToAssociation(associationClass);
		} else {
			association = new HasOneAssociation(associationClass);
		}

		association.setAssociationKey(field.getName());

		if (StringUtils.hasText(associationAnnotation.foreignKey())) {
			association.setForeignKey(associationAnnotation.foreignKey());
		} else if (type == ModelAssociationType.HAS_MANY) {
			association.setForeignKey(StringUtils.uncapitalize(field.getDeclaringClass().getSimpleName()) + "_id");
		} else if (type == ModelAssociationType.BELONGS_TO || type == ModelAssociationType.HAS_ONE) {
			association.setForeignKey(field.getName() + "_id");
		}

		if (StringUtils.hasText(associationAnnotation.primaryKey())) {
			association.setPrimaryKey(associationAnnotation.primaryKey());
		} else if (type == ModelAssociationType.HAS_MANY && StringUtils.hasText(model.getIdProperty())
				&& !model.getIdProperty().equals("id")) {
			association.setPrimaryKey(model.getIdProperty());
		} else if (type == ModelAssociationType.BELONGS_TO || type == ModelAssociationType.HAS_ONE) {
			Model associationModelAnnotation = associationClass.getAnnotation(Model.class);
			if (associationModelAnnotation != null && StringUtils.hasText(associationModelAnnotation.idProperty())
					&& !associationModelAnnotation.idProperty().equals("id")) {
				association.setPrimaryKey(associationModelAnnotation.idProperty());
			}
		}

		if (type == ModelAssociationType.HAS_MANY) {
			HasManyAssociation hasManyAssociation = (HasManyAssociation) association;

			if (StringUtils.hasText(associationAnnotation.setterName())) {
				LogFactory.getLog(ModelGenerator.class)
						.warn(getWarningText(field, association.getType(), "setterName"));
			}

			if (StringUtils.hasText(associationAnnotation.getterName())) {
				LogFactory.getLog(ModelGenerator.class)
						.warn(getWarningText(field, association.getType(), "getterName"));
			}

			if (associationAnnotation.autoLoad()) {
				hasManyAssociation.setAutoLoad(true);
			}
			if (StringUtils.hasText(associationAnnotation.name())) {
				hasManyAssociation.setName(associationAnnotation.name());
			} else {
				hasManyAssociation.setName(field.getName());
			}

		} else if (type == ModelAssociationType.BELONGS_TO) {
			BelongsToAssociation belongsToAssociation = (BelongsToAssociation) association;

			if (StringUtils.hasText(associationAnnotation.setterName())) {
				belongsToAssociation.setSetterName(associationAnnotation.setterName());
			} else {
				belongsToAssociation.setSetterName("set" + StringUtils.capitalize(field.getName()));
			}

			if (StringUtils.hasText(associationAnnotation.getterName())) {
				belongsToAssociation.setGetterName(associationAnnotation.getterName());
			} else {
				belongsToAssociation.setGetterName("get" + StringUtils.capitalize(field.getName()));
			}

			if (associationAnnotation.autoLoad()) {
				LogFactory.getLog(ModelGenerator.class).warn(getWarningText(field, association.getType(), "autoLoad"));
			}
			if (StringUtils.hasText(associationAnnotation.name())) {
				LogFactory.getLog(ModelGenerator.class).warn(getWarningText(field, association.getType(), "name"));
			}
		} else {
			HasOneAssociation hasOneAssociation = (HasOneAssociation) association;

			if (StringUtils.hasText(associationAnnotation.setterName())) {
				hasOneAssociation.setSetterName(associationAnnotation.setterName());
			} else {
				hasOneAssociation.setSetterName("set" + StringUtils.capitalize(field.getName()));
			}

			if (StringUtils.hasText(associationAnnotation.getterName())) {
				hasOneAssociation.setGetterName(associationAnnotation.getterName());
			} else {
				hasOneAssociation.setGetterName("get" + StringUtils.capitalize(field.getName()));
			}

			if (associationAnnotation.autoLoad()) {
				LogFactory.getLog(ModelGenerator.class).warn(getWarningText(field, association.getType(), "autoLoad"));
			}
			if (StringUtils.hasText(associationAnnotation.name())) {
				LogFactory.getLog(ModelGenerator.class).warn(getWarningText(field, association.getType(), "name"));
			}
		}

		if (StringUtils.hasText(associationAnnotation.instanceName())) {
			association.setInstanceName(associationAnnotation.instanceName());
		}

		return association;
	}

	private static String getWarningText(Field field, String type, String propertyName) {
		String warning = "Field ";
		warning += field.getDeclaringClass().getName();
		warning += ".";
		warning += field.getName();
		return warning + ": A '" + type + "' association does not support property '" + propertyName
				+ "'. Property will be ignored.";
	}

}
