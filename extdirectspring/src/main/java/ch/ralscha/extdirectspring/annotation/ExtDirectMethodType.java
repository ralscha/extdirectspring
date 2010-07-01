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

/**
 * Enumeration of all possible remote method types. 
 * 
 * @author Ralph Schaer
 */
public enum ExtDirectMethodType {
  /**
   * Specifies a simple remote method
   */
  SIMPLE, 
    
  /**
   * Specifies a method that handles a form load
   */
  FORM_LOAD, 
  
  /**
   * Specifies a method that handles a read call from DirectStore
   */  
  STORE_READ, 
  
  /**
   * Specifies a method that handles a create, update or delete call from DirectStore
   */  
  STORE_MODIFY, 
  
  
  /**
   * Specifies a method that handles a form post (with or without upload)
   */
  FORM_POST,
  
  /**
   * Specifies a method that handles polling
   */
  POLL 
  
}
