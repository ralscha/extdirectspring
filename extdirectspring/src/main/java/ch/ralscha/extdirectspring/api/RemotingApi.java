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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonWriteNullProperties;
import org.springframework.util.StringUtils;

/**
 * @author Ralph Schaer
 */
@JsonWriteNullProperties(false)
class RemotingApi {

  private final String url;
  private final String namespace;
  private final String type = "remoting";
  private final Map<String, List<Action>> actions;

  private final List<PollingProvider> pollingProviders;

  public RemotingApi(final String url, final String namespace) {
    this.actions = new HashMap<String, List<Action>>();
    this.pollingProviders = new ArrayList<PollingProvider>();

    this.url = url;

    if (StringUtils.hasText(namespace)) {
      this.namespace = namespace.trim();
    } else {
      this.namespace = null;
    }
  }

  public Map<String, List<Action>> getActions() {
    return actions;
  }

  public String getType() {
    return type;
  }

  public String getUrl() {
    return url;
  }

  public String getNamespace() {
    return namespace;
  }

  @JsonIgnore
  public List<PollingProvider> getPollingProviders() {
    return pollingProviders;
  }

  public void addAction(final String beanName, final String methodName, final int len) {
    addAction(beanName, methodName, len, null);
  }

  public void addAction(final String beanName, final String methodName, final int len, final Boolean formHandler) {
    List<Action> beanActions = actions.get(beanName);
    if (beanActions == null) {
      beanActions = new ArrayList<Action>();
      actions.put(beanName, beanActions);
    }

    beanActions.add(new Action(methodName, len, formHandler));
  }

  public void addPollingProvider(final String beanName, final String method, final String event) {
    pollingProviders.add(new PollingProvider(beanName, method, event));
  }

}
