package ch.ralscha.extdirectspring.generator;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation to configure different aspects of a model field. The annotation
 * has not to be present on the field. The generator takes all public readable
 * fields into account.
 * 
 * @author Ralph Schaer
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

}
