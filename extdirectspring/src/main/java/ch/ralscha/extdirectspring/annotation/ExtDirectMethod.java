/**
 * Copyright 2010 Ralph Schaer <ralphschaer@gmail.com>
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

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation for methods whose implement a call
 * 
 * @author Ralph Schaer
 */
@Target({ ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface ExtDirectMethod {

  /**
   * Specifies the type of the remote method. Defaults to simple method call.
   */
  ExtDirectMethodType value() default ExtDirectMethodType.SIMPLE;

  /**
   * The name of an api group this method is part of.
   */
  String group() default "";

  /**
   * Optional parameter if the method is a POLL method. The name of the event
   * this method is sending messages to If parameter is empty the name of the
   * method will be used as event name
   */
  String event() default "";

}
