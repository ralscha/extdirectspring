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

package ch.ralscha.extdirectspring.api;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import ch.ralscha.extdirectspring.annotation.ExtDirectMethod;
import ch.ralscha.extdirectspring.annotation.ExtDirectMethodType;
import ch.ralscha.extdirectspring.util.ExtDirectSpringUtil;
import ch.ralscha.extdirectspring.util.SupportedParameterTypes;

/**
 * Spring managed controller that handles /api.jsp and /api-debug.js requests
 * 
 * @author Ralph Schaer
 */
@Controller
public class ApiController implements ApplicationContextAware {

  private ApplicationContext context;

  @Override
  public void setApplicationContext(ApplicationContext context) {
    this.context = context;
  }

  /**
   * Method that handles api.js calls. Generates a Javascript with the necessary code for Ext.Direct
   * 
   * @param apiNs Name of the namespace the variable remotingApiVar will live in. Defaults to Ext.app
   * @param actionNs Name of the namespace the action will live in. 
   * @param remotingApiVar Name of the remoting api variable. Defaults to REMOTING_API
   * @param pollingUrlsVar Name of the polling urls object. Defaults to POLLING_URLS
   * @param group Name of the api group 
   * @param request 
   * @param response
   * @throws IOException
   */
  @RequestMapping(value = { "/api.js", "/api-debug.js" }, method = RequestMethod.GET)
  public void api(
      @RequestParam(value = "apiNs", required = false, defaultValue = "Ext.app") final String apiNs,
      @RequestParam(value = "actionNs", required = false) final String actionNs,
      @RequestParam(value = "remotingApiVar", required = false, defaultValue = "REMOTING_API") final String remotingApiVar,
      @RequestParam(value = "pollingUrlsVar", required = false, defaultValue = "POLLING_URLS") final String pollingUrlsVar,
      @RequestParam(value = "group", required = false) final String group, final HttpServletRequest request,
      final HttpServletResponse response) throws IOException {

    response.setContentType("application/x-javascript");

    String requestUrlString = request.getRequestURL().toString();
    boolean debug = requestUrlString.contains("api-debug.js");

    ApiCacheKey apiKey = new ApiCacheKey(apiNs, actionNs, remotingApiVar, pollingUrlsVar, group, debug);
    String apiString = ApiCache.INSTANCE.get(apiKey);
    if (apiString == null) {

      String routerUrl;
      String basePollUrl;

      if (!debug) {
        routerUrl = requestUrlString.replace("api.js", "router");
        basePollUrl = requestUrlString.replace("api.js", "poll");
      } else {
        routerUrl = requestUrlString.replace("api-debug.js", "router");
        basePollUrl = requestUrlString.replace("api-debug.js", "poll");
      }
      apiString = buildApiString(apiNs, actionNs, remotingApiVar, pollingUrlsVar, routerUrl, basePollUrl, group, debug);
      ApiCache.INSTANCE.put(apiKey, apiString);
    }

    response.getOutputStream().write(apiString.getBytes());

  }

  private String buildApiString(final String apiNs, final String actionNs, final String remotingApiVar,
      final String pollingUrlsVar, final String routerUrl, final String basePollUrl, final String group,
      final boolean debug) {

    RemotingApi remotingApi = new RemotingApi(routerUrl, actionNs);
    scanForExtDirectMethods(remotingApi, group);

    StringBuilder sb = new StringBuilder();

    sb.append("Ext.ns('");
    sb.append(apiNs);
    sb.append("');");

    if (debug) {
      sb.append("\n\n");
    }

    if (actionNs != null && !actionNs.trim().isEmpty()) {
      sb.append("Ext.ns('");
      sb.append(actionNs);
      sb.append("');");

      if (debug) {
        sb.append("\n\n");
      }
    }

    String jsonConfig = ExtDirectSpringUtil.serializeObjectToJson(remotingApi, debug);

    sb.append(apiNs).append(".").append(remotingApiVar).append(" = ");
    sb.append(jsonConfig);
    sb.append(";");

    if (debug) {
      sb.append("\n\n");
    }

    List<PollingProvider> pollingProviders = remotingApi.getPollingProviders();
    if (!pollingProviders.isEmpty()) {

      sb.append(apiNs).append(".").append(pollingUrlsVar).append(" = {");
      if (debug) {
        sb.append("\n");
      }

      for (int i = 0; i < pollingProviders.size(); i++) {
        if (debug) {
          sb.append("  ");
        }

        sb.append("\"");
        sb.append(pollingProviders.get(i).getEvent());
        sb.append("\"");
        sb.append(" : \"").append(basePollUrl).append("/");
        sb.append(pollingProviders.get(i).getBeanName());
        sb.append("/");
        sb.append(pollingProviders.get(i).getMethod());
        sb.append("/");
        sb.append(pollingProviders.get(i).getEvent());
        sb.append("\"");
        if (i < pollingProviders.size() - 1) {
          sb.append(",");
          if (debug) {
            sb.append("\n");
          }
        }
      }
      if (debug) {
        sb.append("\n");
      }
      sb.append("};");
    }

    return sb.toString();
  }

  private void scanForExtDirectMethods(RemotingApi remotingApi, String group) {
    Map<String, Object> beanDefinitions = getAllBeanDefinitions();

    for (Entry<String, Object> entry : beanDefinitions.entrySet()) {
      Object bean = entry.getValue();
      String beanName = entry.getKey();

      Method[] methods = bean.getClass().getMethods();

      for (Method method : methods) {
        ExtDirectMethod annotation = AnnotationUtils.findAnnotation(method, ExtDirectMethod.class);
        if (annotation != null) {
          if (isSameGroup(group, annotation.group())) {
            ExtDirectMethodType type = annotation.value();

            switch (type) {
            case SIMPLE:
              remotingApi.addAction(beanName, method.getName(), numberOfParameters(method));
              break;
            case FORM_LOAD:
            case STORE_READ:
              remotingApi.addAction(beanName, method.getName(), 1);
              break;
            case STORE_MODIFY:
              if (annotation.entryClass() != ExtDirectMethod.class) {
                remotingApi.addAction(beanName, method.getName(), 1);
              } else {
                LoggerFactory.getLogger(getClass()).warn(
                    "Method '{}.{}' is annotated as a store modify method but does "
                        + "not specify a entryClass. Method ignored.", beanName, method.getName());
              }
              break;
            case FORM_POST:
              if (isValidFormPostMethod(method)) {
                remotingApi.addAction(beanName, method.getName(), 0, true);
              } else {
                LoggerFactory
                    .getLogger(getClass())
                    .warn(
                        "Method '{}.{}' is annotated as a form post method but is not valid. "
                            + "A form post method must be annotated with @RequestMapping and method=RequestMethod.POST. Method ignored.",
                        beanName, method.getName());
              }
              break;
            case POLL:
              remotingApi.addPollingProvider(beanName, method.getName(), annotation.event());
              break;

            }

          }
        }

      }

    }
  }

  private int numberOfParameters(Method method) {
    Class<?>[] parameterTypes = method.getParameterTypes();
    int paramLength = 0;
    for (Class<?> parameterType : parameterTypes) {
      if (!SupportedParameterTypes.isSupported(parameterType)) {
        paramLength++;
      }
    }
    return paramLength;
  }

  private boolean isSameGroup(String requestedGroup, String annotationGroup) {
    if (requestedGroup != null) {
      return ExtDirectSpringUtil.equal(requestedGroup, annotationGroup);
    }

    return true;
  }

  private boolean isValidFormPostMethod(Method method) {
    RequestMapping annotation = AnnotationUtils.findAnnotation(method, RequestMapping.class);
    if (annotation != null) {
      RequestMethod[] requestMethods = annotation.method();
      if (requestMethods != null && requestMethods.length > 0) {
        if (requestMethods[0].equals(RequestMethod.POST)) {
          return true;
        }
      }
    }
    return false;
  }

  private Map<String, Object> getAllBeanDefinitions() {
    Map<String, Object> beanDefinitions = new HashMap<String, Object>();

    ApplicationContext currentCtx = context;
    do {

      for (String beanName : currentCtx.getBeanDefinitionNames()) {
        beanDefinitions.put(beanName, currentCtx.getBean(beanName));
      }
      currentCtx = currentCtx.getParent();

    } while (currentCtx != null);

    return beanDefinitions;
  }

}
