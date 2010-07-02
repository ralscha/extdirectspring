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

package ch.ralscha.extdirectspring.controller;

import java.beans.PropertyDescriptor;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.codehaus.jackson.type.TypeReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.convert.support.ConversionServiceFactory;
import org.springframework.core.convert.support.GenericConversionService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ValueConstants;

import ch.ralscha.extdirectspring.annotation.ExtDirectMethod;
import ch.ralscha.extdirectspring.annotation.ExtDirectMethodType;
import ch.ralscha.extdirectspring.bean.ExtDirectFormLoadResult;
import ch.ralscha.extdirectspring.bean.ExtDirectPollResponse;
import ch.ralscha.extdirectspring.bean.ExtDirectRequest;
import ch.ralscha.extdirectspring.bean.ExtDirectResponse;
import ch.ralscha.extdirectspring.bean.ExtDirectStoreReadRequest;
import ch.ralscha.extdirectspring.bean.ExtDirectStoreResponse;
import ch.ralscha.extdirectspring.util.ExtDirectSpringUtil;
import ch.ralscha.extdirectspring.util.MethodInfo;
import ch.ralscha.extdirectspring.util.SupportedParameterTypes;

/**
 * Main router controller who handles polling, form handler and normal
 * Ext.Direct calls
 * 
 * @author mansari
 * @author Ralph Schaer
 */
@Controller
public class RouterController implements ApplicationContextAware {

  private static final GenericConversionService genericConversionService = ConversionServiceFactory
      .createDefaultConversionService();

  private static final Logger log = LoggerFactory.getLogger(RouterController.class);

  private ApplicationContext context;

  @Override
  public void setApplicationContext(ApplicationContext context) {
    this.context = context;
  }

  @RequestMapping(value = "/poll/{beanName}/{method}/{event}")
  @ResponseBody
  public ExtDirectPollResponse poll(@PathVariable String beanName, @PathVariable String method,
      @PathVariable String event, HttpServletRequest request, HttpServletResponse response, Locale locale)
      throws Exception {

    ExtDirectPollResponse directPollResponse = new ExtDirectPollResponse();
    directPollResponse.setName(event);

    MethodInfo methodInfo = ExtDirectSpringUtil.findMethodInfo(context, beanName, method);

    Class<?>[] parameterTypes = methodInfo.getParameterTypes();
    Object[] parameters = null;
    if (parameterTypes.length > 0) {
      parameters = new Object[parameterTypes.length];
      int paramIndex = 0;
      for (Class<?> parameterType : parameterTypes) {

        if (SupportedParameterTypes.SERVLET_RESPONSE.getSupportedClass().isAssignableFrom(parameterType)) {
          parameters[paramIndex] = response;
        } else if (SupportedParameterTypes.SERVLET_REQUEST.getSupportedClass().isAssignableFrom(parameterType)) {
          parameters[paramIndex] = request;
        } else if (SupportedParameterTypes.SESSION.getSupportedClass().isAssignableFrom(parameterType)) {
          parameters[paramIndex] = request.getSession();
        } else if (SupportedParameterTypes.LOCALE.getSupportedClass().isAssignableFrom(parameterType)) {
          parameters[paramIndex] = locale;
        } else {
          parameters[paramIndex] = handleRequestParam(request, null, methodInfo.getParameterAnnotations()[paramIndex],
              parameterType);
        }

        paramIndex++;
      }
    }

    directPollResponse.setData(ExtDirectSpringUtil.invoke(context, beanName, methodInfo, parameters));

    return directPollResponse;

  }

  @RequestMapping(value = "/router", method = RequestMethod.POST, params = "extAction")
  public String router(@RequestParam(value = "extAction") String extAction,
      @RequestParam(value = "extMethod") String extMethod) {

    MethodInfo methodInfo = ExtDirectSpringUtil.findMethodInfo(context, extAction, extMethod);
    if (methodInfo.getForwardPath() != null) {
      return methodInfo.getForwardPath();
    }
    throw new IllegalArgumentException("Invalid remoting form method: " + extAction + "." + extMethod);

  }

  @SuppressWarnings({ "rawtypes", "unchecked" })
  @RequestMapping(value = "/router", method = RequestMethod.POST, params = "!extAction")
  @ResponseBody
  public List<ExtDirectResponse> router(HttpServletRequest request, HttpServletResponse response, Locale locale,
      @RequestBody String rawRequestString) {

    List<ExtDirectRequest> directRequests = getExtDirectRequests(rawRequestString);
    List<ExtDirectResponse> directResponses = new ArrayList<ExtDirectResponse>();

    for (ExtDirectRequest directRequest : directRequests) {

      ExtDirectResponse directResponse = new ExtDirectResponse(directRequest);


      try {
        MethodInfo methodInfo = ExtDirectSpringUtil.findMethodInfo(context, directRequest.getAction(),
            directRequest.getMethod());

        Object result = processRemotingRequest(request, response, locale, directRequest, methodInfo);

        if (result != null) {
          ExtDirectMethod annotation = methodInfo.getExtDirectMethodAnnotation();

          if (annotation.value() == ExtDirectMethodType.FORM_LOAD) {
            if (!ExtDirectFormLoadResult.class.isAssignableFrom(result.getClass())) {
              result = new ExtDirectFormLoadResult(result);
            }
          } else if (annotation.value() == ExtDirectMethodType.STORE_MODIFY) {
            if (!ExtDirectStoreResponse.class.isAssignableFrom(result.getClass())) {
              result = new ExtDirectStoreResponse((Collection) result);
            }
          }
        }

        directResponse.setResult(result);
      } catch (Exception e) {
        log.error("Error on method: " + directRequest.getMethod(), e);

        directResponse.setType("exception");

        if (log.isDebugEnabled()) {
          directResponse.setMessage(e.getMessage());
          directResponse.setWhere(getStackTrace(e));
        } else {
          directResponse.setMessage("Server Error");
          directResponse.setWhere(null);
        }
      }

      directResponses.add(directResponse);
    }

    return directResponses;

  }

  private String getStackTrace(final Throwable t) {
    StringWriter sw = new StringWriter();
    PrintWriter pw = new PrintWriter(sw, true);
    t.printStackTrace(pw);
    pw.flush();
    sw.flush();
    return sw.toString();
  }

  @SuppressWarnings({ "unchecked", "rawtypes" })
  private Object processRemotingRequest(final HttpServletRequest request, final HttpServletResponse response,
      final Locale locale, final ExtDirectRequest directRequest, final MethodInfo methodInfo) throws Exception {

    ExtDirectMethod annotation = methodInfo.getExtDirectMethodAnnotation();

    ExtDirectMethodType type = annotation.value();

    int jsonParamIndex = 0;
    Annotation[][] parameterAnnotations = methodInfo.getParameterAnnotations();
    Map<String, Object> remainingParameters = null;
    ExtDirectStoreReadRequest directStoreReadRequest = null;

    List<Object> directStoreModifyRecords = null;
    Class<?> directStoreEntryClass;

    if (type == ExtDirectMethodType.STORE_READ || type == ExtDirectMethodType.FORM_LOAD) {

      if (directRequest.getData() != null && directRequest.getData().length > 0) {
        if (type == ExtDirectMethodType.STORE_READ) {
          directStoreReadRequest = new ExtDirectStoreReadRequest();
          remainingParameters = fillObjectFromMap(directStoreReadRequest, (Map) directRequest.getData()[0]);
        } else {
          remainingParameters = (Map) directRequest.getData()[0];
        }
        jsonParamIndex = 1;
      }
    } else if (type == ExtDirectMethodType.STORE_MODIFY) {
      directStoreEntryClass = annotation.entryClass();

      if (directRequest.getData() != null && directRequest.getData().length > 0) {
        Map<String, Object> jsonData = (LinkedHashMap<String, Object>) directRequest.getData()[0];

        ArrayList<Object> records = (ArrayList<Object>) jsonData.get("records");
        directStoreModifyRecords = convertObjectEntriesToType(records, directStoreEntryClass);
        jsonParamIndex = 1;

        remainingParameters = new HashMap<String, Object>();
        for (Entry<String, Object> entry : jsonData.entrySet()) {
          if (!"records".equals(entry.getKey())) {
            remainingParameters.put(entry.getKey(), entry.getValue());
          }
        }
      }
    } else if (type == ExtDirectMethodType.POLL) {
      throw new IllegalStateException("this controller does not handle poll calls");
    } else if (type == ExtDirectMethodType.FORM_POST) {
      throw new IllegalStateException("this controller does not handle form posts");
    }

    Class<?>[] parameterTypes = methodInfo.getParameterTypes();
    Object[] parameters = null;

    if (parameterTypes.length > 0) {
      parameters = new Object[parameterTypes.length];
      int paramIndex = 0;
      for (Class<?> parameterType : parameterTypes) {

        if (SupportedParameterTypes.SERVLET_RESPONSE.getSupportedClass().isAssignableFrom(parameterType)) {
          parameters[paramIndex] = response;
        } else if (SupportedParameterTypes.SERVLET_REQUEST.getSupportedClass().isAssignableFrom(parameterType)) {
          parameters[paramIndex] = request;
        } else if (SupportedParameterTypes.SESSION.getSupportedClass().isAssignableFrom(parameterType)) {
          parameters[paramIndex] = request.getSession();
        } else if (SupportedParameterTypes.LOCALE.getSupportedClass().isAssignableFrom(parameterType)) {
          parameters[paramIndex] = locale;
        } else if (ExtDirectStoreReadRequest.class.isAssignableFrom(parameterType)) {
          parameters[paramIndex] = directStoreReadRequest;
        } else if (directStoreModifyRecords != null
            && parameterType.isAssignableFrom(directStoreModifyRecords.getClass())) {
          parameters[paramIndex] = directStoreModifyRecords;
        } else if (parameterAnnotations != null
            && containsAnnotation(parameterAnnotations[paramIndex], RequestParam.class)) {
          parameters[paramIndex] = handleRequestParam(null, remainingParameters, parameterAnnotations[paramIndex],
              parameterType);
        } else if (directRequest.getData() != null && directRequest.getData().length > jsonParamIndex) {

          Object jsonParam = directRequest.getData()[jsonParamIndex];

          if (parameterType.getClass().equals(String.class)) {
            parameters[paramIndex] = ExtDirectSpringUtil.serializeObjectToJson(jsonParam);
          } else if (parameterType.isPrimitive()) {
            parameters[paramIndex] = jsonParam;
          } else {
            parameters[paramIndex] = ExtDirectSpringUtil.deserializeJsonToObject(
                ExtDirectSpringUtil.serializeObjectToJson(jsonParam), parameterType);
          }

          jsonParamIndex++;
        } else {
          throw new IllegalArgumentException(
              "Error, parameter mismatch. Please check your remoting method signature to ensure all supported parameters types are used.");
        }

        paramIndex++;
      }
    }

    return ExtDirectSpringUtil.invoke(context, directRequest.getAction(), methodInfo, parameters);
  }

  private Object handleRequestParam(final HttpServletRequest request, final Map<String, Object> valueContainer,
      final Annotation[] parameterAnnotations, final Class<?> parameterType) {
    boolean required = false;
    String parameterName = null;
    String defaultValue = null;
    for (Annotation paramAnn : parameterAnnotations) {
      if (RequestParam.class.isInstance(paramAnn)) {
        RequestParam requestParam = (RequestParam) paramAnn;
        parameterName = requestParam.value();
        required = requestParam.required();
        defaultValue = (ValueConstants.DEFAULT_NONE.equals(requestParam.defaultValue()) ? null : requestParam
            .defaultValue());
        break;
      }
    }

    if (parameterName != null) {
      Object value;
      if (request != null) {
        value = request.getParameter(parameterName);
      } else if (valueContainer != null) {
        value = valueContainer.get(parameterName);
      } else {
        value = null;
      }

      if (value == null) {
        value = defaultValue;
      }

      if (value != null) {
        return genericConversionService.convert(value, parameterType);
      }
      if (required) {
        throw new IllegalArgumentException("Missing request parameter: " + parameterName);
      }
    }

    return null;
  }

  private Map<String, Object> fillObjectFromMap(final Object to, final Map<String, Object> from) {
    Set<String> foundParameters = new HashSet<String>();

    for (Entry<String, Object> entry : from.entrySet()) {
      PropertyDescriptor descriptor = BeanUtils.getPropertyDescriptor(to.getClass(), entry.getKey());
      if (descriptor != null && descriptor.getWriteMethod() != null) {
        try {
          descriptor.getWriteMethod().invoke(to,
              genericConversionService.convert(entry.getValue(), descriptor.getPropertyType()));
          foundParameters.add(entry.getKey());
        } catch (IllegalArgumentException e) {
          log.error("fillObjectFromMap", e);
        } catch (IllegalAccessException e) {
          log.error("fillObjectFromMap", e);
        } catch (InvocationTargetException e) {
          log.error("fillObjectFromMap", e);
        }
      }
    }

    Map<String, Object> remainingParameters = new HashMap<String, Object>();
    for (Entry<String, Object> entry : from.entrySet()) {
      if (!foundParameters.contains(entry.getKey())) {
        remainingParameters.put(entry.getKey(), entry.getValue());
      }
    }
    return remainingParameters;
  }

  private List<Object> convertObjectEntriesToType(final ArrayList<Object> records, final Class<?> directStoreType) {
    if (records != null) {
      List<Object> convertedList = new ArrayList<Object>();
      for (Object record : records) {
        Object convertedObject = ExtDirectSpringUtil.deserializeJsonToObject(
            ExtDirectSpringUtil.serializeObjectToJson(record), directStoreType);
        convertedList.add(convertedObject);
      }
      return convertedList;
    }
    return null;
  }

  private List<ExtDirectRequest> getExtDirectRequests(final String rawRequestString) {
    List<ExtDirectRequest> directRequests = new ArrayList<ExtDirectRequest>();

    if (rawRequestString.length() > 0 && rawRequestString.charAt(0) == '[') {
      directRequests.addAll(ExtDirectSpringUtil.deserializeJsonToObject(rawRequestString,
          new TypeReference<List<ExtDirectRequest>>() {/* empty */
          }));
    } else {
      ExtDirectRequest directRequest = ExtDirectSpringUtil.deserializeJsonToObject(rawRequestString,
          ExtDirectRequest.class);
      directRequests.add(directRequest);
    }

    return directRequests;
  }

  private boolean containsAnnotation(final Annotation[] annotations, final Class<RequestParam> requestedAnnotation) {
    if (annotations != null) {
      for (Annotation annotation : annotations) {
        if (requestedAnnotation.isInstance(annotation)) {
          return true;
        }
      }
    }
    return false;
  }
}
