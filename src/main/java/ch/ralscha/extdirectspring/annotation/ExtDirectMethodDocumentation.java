package ch.ralscha.extdirectspring.annotation;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * 
 * These are only used for api-debug.js generation to self 
 * documents the client server interface.
 * TODO comment
  method to ...
  @param blah
  @param blah
  @return blah
  
  <p/>
 * @author dbs
 *
 */
@Target({ })
@Retention(RUNTIME)
@Documented
public @interface ExtDirectMethodDocumentation {
	
	/**
	 * (Optional) the method comment
	 * TODO should it be required instead of optional?
	 */
	String value() default "method purpose: please fill in";
	
	/**
    * (Optional) method author
    * <p/>
    * add a &#64;author to the method currently documented
    * <p/>
    * Defaults to empty.
    */
	String author() default "";
	
	/**
    * (Optional) method version
    * <p/>
    * add a &#64;version to the method currently documented
    * <p/>
    * Defaults to 1.0.
    */
	String version() default "1.0";
	
	/**
	 * (Optional) Parameters documentation that are to be placed on
	 * the api method. 
	 * <p/>
	 * Defaults to no parameters.
	 */
	ExtDirectDocParameters parameters() default @ExtDirectDocParameters;
	
	/**
	 * (Optional) objects to be returned when this method is called
	 * <p/>
	 * Defaults to no return.
	 */
	ExtDirectDocReturn returnMethod() default @ExtDirectDocReturn;
	
	/**
    * (Optional) Whether this method is deprecated
    * <p/>
    * add a &#64;deprecated to the method currently documented
    * <p/>
    * Defaults to false.
    */
   boolean deprecated() default false;
}
