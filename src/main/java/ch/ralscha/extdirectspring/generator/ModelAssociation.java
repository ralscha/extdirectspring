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
 * Annotation that configures the association to another object. If this
 * annotation is present the generator creates an associations config object in
 * the Model.
 * 
 * @author Ralph Schaer
 */
@Target({ ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface ModelAssociation {

	/**
	 * Type of the association.
	 */
	ModelAssociationType value();

	Class<?> model(); // hasMany, belongsTo, hasOne

	boolean autoLoad() default false; // hasMany

	String foreignKey() default ""; // hasMany, belongsTo, hasOne

	String name() default ""; // hasMany

	String primaryKey() default "";// hasMany, belongsTo, hasOne

	String setterName() default ""; // belongTo, hasOne

	String getterName() default ""; // belongTo, hasOne

}
