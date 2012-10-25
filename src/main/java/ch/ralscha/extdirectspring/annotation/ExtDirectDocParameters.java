package ch.ralscha.extdirectspring.annotation;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * @author dbs
 *
 *TODO add javascript type?
 */
@Target({ })
@Retention(RUNTIME)
@Documented
public @interface ExtDirectDocParameters {
	/**
    * (Optional) name of parameters
    * <p/>
    * add a &#64;param {property} for each of the parameters the method accept
    * <p/>
    * Defaults to empty.
    */
	String[] params() default {};
	
	/**
    * (Optional) description of return properties
    * <p/>
    * add description to the &#64;param {paramName}
    * <p/>
    * Defaults to empty.
    */
	String[] descriptions() default {};
}
