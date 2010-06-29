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
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializationConfig.Feature;
import org.codehaus.jackson.type.TypeReference;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.core.annotation.AnnotationUtils;
import ch.ralscha.extdirectspring.annotation.ExtDirectMethod;
import ch.ralscha.extdirectspring.annotation.ExtDirectPollMethod;
import ch.ralscha.extdirectspring.annotation.ExtDirectStoreModifyMethod;
import ch.ralscha.extdirectspring.annotation.ExtDirectStoreReadMethod;

/**
 * Utility class
 *
 * @author mansari
 * @author Ralph Schaer
 */
public class ExtDirectSpringUtil {

  private ExtDirectSpringUtil() {
    //singleton
  }

  /**
   * Checks if two objects are equal. Returns true if both objects are null
   * 
   * @param a object one
   * @param b object two
   * @return true if objects are equal
   */
  public static boolean equal(final Object a, final Object b) {
    return a == b || (a != null && a.equals(b));
  }

  /**
   * Retrieves a method in a spring managed bean. The found method will be cached in {@link MethodCache} 
   * with the key beanName,methodName
   * 
   * @param context Spring application context
   * @param beanName name of the bean to find the method in 
   * @param methodName name of the method to retrieve
   * @return the method
   * @throws IllegalArgumentException if the method is not annotated with a ExtDirectSpring annotation or there is no method in the bean
   */
  public static Method findMethod(final ApplicationContext context, final String beanName, final String methodName) {

    if (context == null) {
      throw new IllegalArgumentException("ApplicatonContext cannot be null");
    }

    if (beanName == null) {
      throw new IllegalArgumentException("beanName cannot be null");
    }

    if (methodName == null) {
      throw new IllegalArgumentException("methodName cannot be null");
    }

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

  /**
   * Find a method that is annotated with a specific annotation. Starts with the method and goes 
   * up to the superclasses of the class. 
   * 
   * @param method the starting method
   * @param annotation the annotation to look for
   * @return the method if there is a annotated method, else null
   */
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
   * @param method the method to check for the annotations
   * @return true if a supported annotation is present, else false
   * 
   * @see ch.ralscha.extdirectspring.annotation.ExtDirectMethod
   * @see ch.ralscha.extdirectspring.annotation.ExtDirectPollMethod
   * @see ch.ralscha.extdirectspring.annotation.ExtDirectStoreModifyMethod
   * @see ch.ralscha.extdirectspring.annotation.ExtDirectStoreReadMethod
   */
  public static boolean isSupportedAnnotationPresent(final Method method) {

    return AnnotationUtils.findAnnotation(method, ExtDirectMethod.class) != null
        || AnnotationUtils.findAnnotation(method, ExtDirectPollMethod.class) != null
        || AnnotationUtils.findAnnotation(method, ExtDirectStoreModifyMethod.class) != null
        || AnnotationUtils.findAnnotation(method, ExtDirectStoreReadMethod.class) != null;

  }

  /**
   * Invokes a method on a Spring managed bean. 
   * 
   * @param context a Spring application context
   * @param beanName the name of the bean
   * @param methodName the name of the method
   * @param params the parameters
   * @return the result of the method invokation
   * @throws IllegalArgumentException if there is no bean in the context
   * @throws IllegalAccessException
   * @throws InvocationTargetException
   */
  public static Object invoke(final ApplicationContext context, final String beanName, final String methodName, final Object[] params)
      throws IllegalArgumentException, IllegalAccessException, InvocationTargetException {
    Method method = findMethod(context, beanName, methodName);
    Object bean = context.getBean(beanName);
    return method.invoke(bean, params);
  }

  /**
   * Converts a object into a String containing the json representation of this object.
   * In case of an exception returns null and logs the exception. 
   * 
   * @param obj the object to serialize into json
   * @return obj in json format
   */
  public static String serializeObjectToJson(final Object obj) {
    return serializeObjectToJson(obj, false);
  }

  /**
   * Converts a object into a String containing the json representation of this object.
   * In case of an exceptions returns null and logs the exception.
   * 
   * @param obj the object to serialize into json
   * @param indent if false writes json on one line
   * @return obj in json format, null if there is an exception 
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
   * In case of an exception returns null and logs the exception.
   * 
   * @param <T> type of the object to create
   * @param json String with the json
   * @param typeReference TypeReference instance of the desired result type {@link org.codehaus.jackson.type.TypeReference}
   * @return the created object, null if there is an exception
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
   * In case of an exception returns null and logs the exception.
   * 
   * @param <T> type of the object to create
   * @param json String with the json
   * @param clazz Class of object to create
   * @return the created object, null if there is an exception
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
