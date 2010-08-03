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
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.core.GenericCollectionTypeResolver;
import org.springframework.core.LocalVariableTableParameterNameDiscoverer;
import org.springframework.core.MethodParameter;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ValueConstants;

import ch.ralscha.extdirectspring.annotation.ExtDirectMethod;
import ch.ralscha.extdirectspring.annotation.ExtDirectMethodType;

/**
 * Object holds information about a method like the method itself and a list of
 * parameters
 * 
 * @author Ralph Schaer
 */
public class MethodInfo {
  private static final LocalVariableTableParameterNameDiscoverer discoverer = new LocalVariableTableParameterNameDiscoverer();

  private List<ParameterInfo> parameters;
  private Method method;
  private String forwardPath;

  private ExtDirectMethodType type;
  private Class<?> collectionType;

  public MethodInfo(final Method method) {

    this.method = method;

    RequestMapping methodAnnotation = AnnotationUtils.findAnnotation(method, RequestMapping.class);
    if (methodAnnotation != null) {
      
      RequestMapping classAnnotation = AnnotationUtils.findAnnotation(method.getDeclaringClass(), RequestMapping.class);
      
      String path = null;
      if (hasValue(classAnnotation)) {
        path = classAnnotation.value()[0];
      }

      if (hasValue(methodAnnotation)) {
        String methodPath = methodAnnotation.value()[0];
        if (path != null) {
          path = path + methodPath;
        } else {
          path = methodPath;
        }
      }
      
      if (path != null) {
        if (path.charAt(0) == '/' && path.length() > 1) {
          path = path.substring(1, path.length());
        }
        this.forwardPath = "forward:" + path;
      }
    }

    ExtDirectMethod extDirectMethodAnnotation = AnnotationUtils.findAnnotation(method, ExtDirectMethod.class);
    if (extDirectMethodAnnotation != null) {
      this.type = extDirectMethodAnnotation.value();
    }

    this.parameters = buildParameterList(method);

    for (ParameterInfo parameter : parameters) {
      if (parameter.getCollectionType() != null) {
        this.collectionType = parameter.getCollectionType();
        break;
      }
    }

  }
  
  private boolean hasValue(RequestMapping requestMapping) {
    return (requestMapping != null && requestMapping.value() != null && 
        requestMapping.value().length > 0 && StringUtils.hasText(requestMapping.value()[0]));
  }

  private static List<ParameterInfo> buildParameterList(Method m) {
    List<ParameterInfo> params = new ArrayList<ParameterInfo>();

    Class<?>[] parameterTypes = m.getParameterTypes();
    Annotation[][] parameterAnnotations = null;
    String[] parameterNames = null;

    Method methodWithAnnotation = ExtDirectSpringUtil.findMethodWithAnnotation(m, ExtDirectMethod.class);
    if (methodWithAnnotation != null) {
      parameterAnnotations = methodWithAnnotation.getParameterAnnotations();
      parameterNames = discoverer.getParameterNames(methodWithAnnotation);
    }

    for (int paramIndex = 0; paramIndex < parameterTypes.length; paramIndex++) {

      ParameterInfo parameterInfo = new ParameterInfo();
      parameterInfo.setType(parameterTypes[paramIndex]);

      parameterInfo.setSupportedParameter(SupportedParameterTypes.isSupported(parameterTypes[paramIndex]));

      if (parameterNames != null) {
        parameterInfo.setName(parameterNames[paramIndex]);
      }

      if (parameterAnnotations != null) {

        for (Annotation paramAnn : parameterAnnotations[paramIndex]) {
          if (RequestParam.class.isInstance(paramAnn)) {
            RequestParam requestParam = (RequestParam)paramAnn;
            if (StringUtils.hasText(requestParam.value())) {
              parameterInfo.setName(requestParam.value());
            }
            parameterInfo.setRequired(requestParam.required());
            parameterInfo.setDefaultValue(ValueConstants.DEFAULT_NONE.equals(requestParam.defaultValue()) ? null
                : requestParam.defaultValue());
            parameterInfo.setHasRequestParamAnnotation(true);
            break;
          }
        }
      }

      if (Collection.class.isAssignableFrom(parameterTypes[paramIndex])) {
        parameterInfo.setCollectionType(GenericCollectionTypeResolver.getCollectionParameterType(new MethodParameter(m,
            paramIndex)));
      }

      params.add(parameterInfo);
    }

    return params;
  }

  public Method getMethod() {
    return method;
  }

  public String getForwardPath() {
    return forwardPath;
  }

  public List<ParameterInfo> getParameters() {
    return parameters;
  }

  public Class<?> getCollectionType() {
    return collectionType;
  }

  public boolean isType(ExtDirectMethodType methodType) {
    return this.type == methodType;
  }

}