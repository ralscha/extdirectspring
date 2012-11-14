package ch.ralscha.extdirectspring.annotation;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Target({ })
@Retention(RUNTIME)
@Documented
public @interface ExtDirectDocReturn {

	/**
    * (Optional) name of return properties
    * <p/>
    * add a &#64;return {property} for each of the properties the method will return
    * <p/>
    * Defaults to empty.
    */
	String[] properties() default {};
	
	/**
    * (Optional) description of return properties
    * <p/>
    * add description to the &#64;return {property}
    * <p/>
    * Defaults to empty.
    */
	String[] descriptions() default {};
}
