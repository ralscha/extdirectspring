/**
 * Copyright 2010-2016 Ralph Schaer <ralphschaer@gmail.com>
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
package ch.ralscha.extdirectspring.annotation;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/* Example:
 An annotation like following

 @ExtDirectMethod(value = ExtDirectMethodType.SIMPLE, group = "groupdoc", documentation=
 @ExtDirectMethodDocumentation(value="this method is used to test the documentation generation",
 author="dbs",
 version="0.1",
 deprecated = true,
 returnMethod=@ExtDirectDocReturn(properties= {"success", "errors"}, descriptions= {"true for success, false otherwise", "list of failed fields"}),
 parameters=@ExtDirectDocParameters(params = {"a", "b", "c", "d", "e"},descriptions= {"property a integer", "property b string", "property c string", "property d boolean", "array of integers"}))
 )
 public String methodDoc() {
 return "methodDoc() called";
 }

 will produce this comment inserted in api-debug-doc.js

 /**
 * @deprecated
 * methodDoc: this method is used to test the documentation generation
 * @author: dbs
 * @version: 0.1
 *
 * @param: [d] property d boolean
 * @param: [e] array of integers
 * @param: [b] property b string
 * @param: [c] property c string
 * @param: [a] property a integer
 * @return
 *	 [errors] list of failed fields
 *	 [success] true for success, false otherwise
 *\/
 */
/**
 * These are only used for api-debug-doc.js generation to self documents the client server
 * interface.
 * <p/>
 * see example above
 */
@Target({ ElementType.METHOD, ElementType.ANNOTATION_TYPE })
@Retention(RUNTIME)
@Inherited
@Documented
public @interface ExtDirectMethodDocumentation {

	/**
	 * (Optional) the method comment if method description is empty, the documentation for
	 * this method will be skipped
	 */
	String value() default "";

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
	 * (Optional) Parameters documentation that are to be placed on the api method.
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
