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

package ch.ralscha.extdirectspring.util;

/**
 * Object holds information about a parameter like the name, type and the
 * attributes of a RequestParam annotation
 * 
 * @author Ralph Schaer
 */
public class ParameterInfo {
  private Class<?> type;
  private Class<?> collectionType;
  private String name;

  private boolean hasRequestParamAnnotation;
  private boolean required;
  private String defaultValue;

  private boolean supportedParameter;

  public Class<?> getType() {
    return type;
  }

  public void setType(Class<?> type) {
    this.type = type;
  }

  public Class<?> getCollectionType() {
    return collectionType;
  }

  public void setCollectionType(Class<?> collectionType) {
    this.collectionType = collectionType;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public boolean isHasRequestParamAnnotation() {
    return hasRequestParamAnnotation;
  }

  public void setHasRequestParamAnnotation(boolean hasRequestParamAnnotation) {
    this.hasRequestParamAnnotation = hasRequestParamAnnotation;
  }

  public boolean isRequired() {
    return required;
  }

  public void setRequired(boolean required) {
    this.required = required;
  }

  public String getDefaultValue() {
    return defaultValue;
  }

  public void setDefaultValue(String defaultValue) {
    this.defaultValue = defaultValue;
  }

  public boolean isSupportedParameter() {
    return supportedParameter;
  }

  public void setSupportedParameter(boolean supportedParameter) {
    this.supportedParameter = supportedParameter;
  }

}
