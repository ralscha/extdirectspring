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

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;

import ch.ralscha.extdirectspring.annotation.ExtDirectMethod;

/**
 * Object holds information about a method like the method itself, the array of
 * the parameter types and the annotations of the parameters
 * 
 * @author Ralph Schaer
 */
public class MethodInfo {
  private Annotation[][] parameterAnnotations;
  private Class<?>[] parameterTypes;
  private Method method;
  private ExtDirectMethod extDirectMethodAnnotation;
  private String forwardPath;

  public MethodInfo(Method method) {
    this.method = method;
    this.parameterTypes = method.getParameterTypes();
    
    RequestMapping requestMappingAnnotation = AnnotationUtils.findAnnotation(method, RequestMapping.class);
    if (requestMappingAnnotation != null && StringUtils.hasText(requestMappingAnnotation.value()[0])) {
      String path = requestMappingAnnotation.value()[0];
      if (path.charAt(0) == '/' && path.length() > 1) {
        path = path.substring(1, path.length());
      }
      this.forwardPath = "forward:" + path;
    }    
    
    this.extDirectMethodAnnotation = AnnotationUtils.findAnnotation(method, ExtDirectMethod.class);

    Method methodWithAnnotation = ExtDirectSpringUtil.findMethodWithAnnotation(method, ExtDirectMethod.class);
    if (methodWithAnnotation != null) {
      this.parameterAnnotations = methodWithAnnotation.getParameterAnnotations();
    }
  }

  public Annotation[][] getParameterAnnotations() {
    return parameterAnnotations;
  }

  public Class<?>[] getParameterTypes() {
    return parameterTypes;
  }

  public Method getMethod() {
    return method;
  }

  public String getForwardPath() {
    return forwardPath;
  }

  public ExtDirectMethod getExtDirectMethodAnnotation() {
    return extDirectMethodAnnotation;
  }

}