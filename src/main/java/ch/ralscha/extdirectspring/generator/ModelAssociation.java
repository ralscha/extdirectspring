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

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation that configures an association to another object. If this
 * annotation is present on a field the generator creates an associations config
 * object in the model.
 * 
 * @author Ralph Schaer
 */
@Target({ ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface ModelAssociation {

	/**
	 * The type of the association.
	 * <p>
	 * Corresponds to the <a href=
	 * "http://docs.sencha.com/ext-js/4-1/#!/api/Ext.data.association.HasMany-cfg-type"
	 * >type</a> config property.
	 */
	ModelAssociationType value();

	/**
	 * The class of the model that is being associated with.
	 * <p>
	 * Corresponds to the <a href=
	 * "http://docs.sencha.com/ext-js/4-1/#!/api/Ext.data.association.Association-cfg-model"
	 * >model</a> config property. The generated Javascript code contains either
	 * the full qualified class name of the class or the string from
	 * {@link Model#value()} if present on the class.
	 */
	Class<?> model() default Object.class;

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
	boolean autoLoad() default false;

	/**
	 * The name of the foreign key on the associated model that links it to the
	 * owner model. Defaults to the lowercased name of the owner model plus
	 * "_id".
	 * <p>
	 * Corresponds to the <a href=
	 * "http://docs.sencha.com/ext-js/4-1/#!/api/Ext.data.association.HasMany-cfg-foreignKey"
	 * >foreignKey</a> config property.
	 */
	String foreignKey() default "";

	/**
	 * The name of the function to create on the owner model to retrieve the
	 * child store. If not specified, the name of the field is used.
	 * <p>
	 * Corresponds to the <a href=
	 * "http://docs.sencha.com/ext-js/4-1/#!/api/Ext.data.association.HasMany-cfg-name"
	 * >name</a> config property.
	 * <p>
	 * Only {@link ModelAssociationType#HAS_MANY} association support this
	 * property.
	 */
	String name() default "";

	/**
	 * The name of the primary key on the associated model. <br>
	 * In general this will be the value of {@link Model#idProperty()}.
	 * <p>
	 * Corresponds to the <a href=
	 * "http://docs.sencha.com/ext-js/4-1/#!/api/Ext.data.association.Association-cfg-primaryKey"
	 * >primaryKey</a> config property.
	 */
	String primaryKey() default "";

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
	 * {@link ModelAssociationType#HAS_ONE} associations support this property.
	 */
	String setterName() default "";

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
	 * {@link ModelAssociationType#HAS_ONE} associations support this property.
	 */
	String getterName() default "";

}
