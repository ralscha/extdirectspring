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
import ch.ralscha.extdirectspring.bean.ExtDirectFormLoadResult;

/**
 * Annotation for methods whose implement a simple remote call
 * 
 * @author Ralph Schaer
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface ExtDirectMethod {

  /**
   * The name of a api group this method is part of. 
   */
  String group() default "";

  /**
   * Set this attribute to true if the result should automatically wrapped with {@link ExtDirectFormLoadResult}
   * Necessary return type for load methods 
   */
  boolean formLoad() default false;

}
