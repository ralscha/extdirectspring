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

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation that configures different aspects of a model field. The annotation
 * does not have to be present on the field to be included in the generated JS
 * code. The generator takes all public readable fields into account.
 */
@Target({ ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface ModelField {
	/**
	 * Name of the field. Property '<a
	 * href="http://docs.sencha.com/ext-js/4-1/#!/api/Ext.data.Field-cfg-name"
	 * >name</a>' in JS.
	 * <p>
	 * If not present the name of the field is used.
	 */
	String value() default "";

	/**
	 * Type of the field. Property '<a
	 * href="http://docs.sencha.com/ext-js/4-1/#!/api/Ext.data.Field-cfg-type"
	 * >type</a>' in JS.
	 * <p>
	 * If not present the library tries to figure out the type with
	 * {@link ModelType}.
	 */
	ModelType type() default ModelType.AUTO;

	/**
	 * The default value. Property '<a href=
	 * "http://docs.sencha.com/ext-js/4-1/#!/api/Ext.data.Field-cfg-defaultValue"
	 * >defaultValue</a>' in JS.
	 * <p>
	 * Can be set to {@link #DEFAULTVALUE_UNDEFINED} to set defaultValue to the
	 * value undefined. This prevents defaulting a value.
	 */
	String defaultValue() default "";

	/**
	 * Specifies format of date. Property '<a href=
	 * "http://docs.sencha.com/ext-js/4-1/#!/api/Ext.data.Field-cfg-dateFormat"
	 * >dateFormat</a>' in JS.<br>
	 * For a list of all supported formats see Sencha Doc: <a
	 * href="http://docs.sencha.com/ext-js/4-1/#!/api/Ext.Date">Ext.Date</a>
	 * <p>
	 * Will be ignored if the field is not a {@link ModelType#DATE} field.
	 */
	String dateFormat() default "";

	/**
	 * If true null value is used if value cannot be parsed. If false default
	 * values are used (0 for integer and float, "" for string and false for
	 * boolean).<br>
	 * Property '<a href=
	 * "http://docs.sencha.com/ext-js/4-1/#!/api/Ext.data.Field-cfg-useNull"
	 * >useNull</a>' in JS.<br>
	 * <p>
	 * Only used if type of field is {@link ModelType#INTEGER},
	 * {@link ModelType#FLOAT}, {@link ModelType#STRING} or
	 * {@link ModelType#BOOLEAN}.
	 */
	boolean useNull() default false;

	/**
	 * Typical use for a virtual field to extract field data from the model
	 * object <br>
	 * Property '<a href=
	 * "http://docs.sencha.com/ext-js/4-1/#!/api/Ext.data.Field-cfg-mapping"
	 * >mapping</a>' in JS.
	 * <p>
	 */
	String mapping() default "";

	/**
	 * Prevent the value of this field to be serialized or written with
	 * Ext.data.writer.Writer <br>
	 * Typical use for a virtual field <br>
	 * Property '<a href=
	 * "http://docs.sencha.com/ext-js/4-1/#!/api/Ext.data.Field-cfg-persist"
	 * >persist</a>' in JS.
	 * <p>
	 */
	boolean persist() default true;

	/**
	 * Function which coerces string values in raw data into the field's type <br>
	 * Typical use for a virtual field <br>
	 * Property '<a href=
	 * "http://docs.sencha.com/ext-js/4-1/#!/api/Ext.data.Field-cfg-convert" >
	 * Ext.data.Field.convert</a>' in JS.
	 * <p>
	 */
	String convert() default "";

	/**
	 * Constant for the value undefined. Can only be used for the property
	 * {@link #defaultValue()}. According to the <a href=
	 * "http://docs.sencha.com/ext-js/4-1/#!/api/Ext.data.Field-cfg-defaultValue"
	 * >documentation</a> setting defaultValue to undefined prevents defaulting
	 * a value.
	 */
	public final static String DEFAULTVALUE_UNDEFINED = "undefined";
}
