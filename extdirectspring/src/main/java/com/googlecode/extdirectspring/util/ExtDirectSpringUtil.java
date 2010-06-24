/**
 * Copyright 2010 Ralph Schaer
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

package com.googlecode.extdirectspring.util;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializationConfig.Feature;
import org.codehaus.jackson.type.TypeReference;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.web.bind.annotation.RequestParam;
import com.googlecode.extdirectspring.annotation.ExtDirectMethod;
import com.googlecode.extdirectspring.annotation.ExtDirectPollMethod;
import com.googlecode.extdirectspring.annotation.ExtDirectStoreModifyMethod;
import com.googlecode.extdirectspring.annotation.ExtDirectStoreReadMethod;
import com.googlecode.extdirectspring.controller.MethodCache;

/**
 * Utility class with methods to support the library 
 *
 * @author mansari
 * @author Ralph Schaer
 */
public class ExtDirectSpringUtil {

  private ExtDirectSpringUtil() {
    //singleton
  }

  /**
   * Checks if two objects are equal. If both objects are null method returns true
   * 
   * @param a object one
   * @param b object two
   * @return true if the objects are equal
   */
  public static boolean equal(final Object a, final Object b) {
    return a == b || (a != null && a.equals(b));
  }

  public static Method findMethod(final ApplicationContext context, final String beanName, final String methodName) {
    Method method = MethodCache.INSTANCE.get(beanName, methodName);

    if (method != null) {
      return method;
    }

    Object bean = context.getBean(beanName);
    method = BeanUtils.findMethodWithMinimalParameters(bean.getClass(), methodName);

    if (method != null) {
      if (!isSupportedAnnotationPresent(method)) {
        throw new IllegalArgumentException("Invalid remoting method '" + beanName + "." + methodName
            + "'. Missing ExtDirectSpring annotation");
      }

      MethodCache.INSTANCE.put(beanName, methodName, method);
      return method;
    }

    throw new IllegalArgumentException("Method '" + beanName + "." + methodName + "' not found");

  }
  
  public static Method findMethodWithAnnotation(final Method method, final Class<? extends Annotation> annotation) {
    if (method.isAnnotationPresent(annotation)) {
      return method; 
    }
    
    Class<?> cl = method.getDeclaringClass();
    while (cl != null) {
      try {
        Method equivalentMethod = cl.getDeclaredMethod(method.getName(), method.getParameterTypes());
        if (equivalentMethod.isAnnotationPresent(annotation)) {
          return equivalentMethod; 
        }
      } catch (NoSuchMethodException e) {
        //do nothing here
      }
      cl = cl.getSuperclass();
    }
    
    return null;
  }

  /**
   * Checks if the method is annotated with a supported ExtDirectSpring annotation
   * 
   * @param method The method in question
   * @return true if a supported annotation is present
   * 
   * @see com.googlecode.extdirectspring.annotation.ExtDirectMethod
   * @see com.googlecode.extdirectspring.annotation.ExtDirectPollMethod
   */
  public static boolean isSupportedAnnotationPresent(final Method method) {

    return AnnotationUtils.findAnnotation(method, ExtDirectMethod.class) != null
        || AnnotationUtils.findAnnotation(method, ExtDirectPollMethod.class) != null
        || AnnotationUtils.findAnnotation(method, ExtDirectStoreModifyMethod.class) != null
        || AnnotationUtils.findAnnotation(method, ExtDirectStoreReadMethod.class) != null;

  }

  public static Object invoke(final ApplicationContext context, final String beanName, final String methodName, final Object[] params)
      throws IllegalArgumentException, IllegalAccessException, InvocationTargetException {
    Method method = findMethod(context, beanName, methodName);
    Object bean = context.getBean(beanName);

    if (bean == null) {
      throw new IllegalArgumentException("Bean '" + beanName + "' not found");
    }

    return method.invoke(bean, params);
  }

  /**
   * Converts a object into a String containing the json representation of this object.
   * In case of a exceptions returns null and logs the exception. 
   * 
   * @param obj the object to serialize into json
   * @return obj in json format
   */
  public static String serializeObjectToJson(final Object obj) {
    return serializeObjectToJson(obj, false);
  }

  public static boolean containsAnnotation(Annotation[] annotations, Class<RequestParam> requestedAnnotation) {
    for (Annotation annotation : annotations) {
      if (requestedAnnotation.isInstance(annotation)) {
        return true;
      }
    }
    return false;
  }
  
  /**
   * Converts a object into a String containing the json representation of this object.
   * In case of a exceptions returns null and logs the exception.
   * 
   * @param obj the object to serialize into json
   * @param indent if false writes json on one line
   * @return obj in json format
   */
  public static String serializeObjectToJson(final Object obj, final boolean indent) {
    try {
      ObjectMapper mapper = new ObjectMapper();

      if (indent) {
        mapper.getSerializationConfig().enable(Feature.INDENT_OUTPUT);
      }

      return mapper.writeValueAsString(obj);
    } catch (Exception e) {
      LoggerFactory.getLogger(ExtDirectSpringUtil.class).info("serialize object to json", e);
      return null;
    }
  }

  /**
   * Creates a object from a json String. 
   * In case of a exceptions returns null and logs the exception.
   * 
   * @param <T> type of the object to create
   * @param json String with the json
   * @param typeReference TypeReference instance of the desired result type {@link org.codehaus.jackson.type.TypeReference}
   * @return the created object
   */
  @SuppressWarnings("unchecked")
  public static <T> T deserializeJsonToObject(final String json, final TypeReference<T> typeReference) {
    try {
      ObjectMapper mapper = new ObjectMapper();
      return (T)mapper.readValue(json, typeReference);
    } catch (Exception e) {
      LoggerFactory.getLogger(ExtDirectSpringUtil.class).info("deserialize json to object", e);
      return null;
    }
  }

  /**
   * Creates a object from a json String. 
   * In case of a exceptions returns null and logs the exception.
   * 
   * @param <T> type of the object to create
   * @param json String with the json
   * @param clazz Class of object to create
   * @return the created object
   */
  public static <T> T deserializeJsonToObject(final String json, final Class<T> clazz) {
    try {
      ObjectMapper mapper = new ObjectMapper();
      return mapper.readValue(json, clazz);
    } catch (Exception e) {
      LoggerFactory.getLogger(ExtDirectSpringUtil.class).info("deserialize json to object", e);
      return null;
    }
  }

}
