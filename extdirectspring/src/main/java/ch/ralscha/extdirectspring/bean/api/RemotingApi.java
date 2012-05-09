/**
 * Copyright 2010-2012 Ralph Schaer <ralphschaer@gmail.com>
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
package ch.ralscha.extdirectspring.bean.api;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.codehaus.jackson.map.annotate.JsonSerialize.Inclusion;
import org.springframework.util.StringUtils;

/**
 * @author Ralph Schaer
 */
@JsonSerialize(include = Inclusion.NON_NULL)
public final class RemotingApi {

	private String descriptor;
	private final String url;
	private final String namespace;
	private final String type = "remoting";
	private final Map<String, List<Action>> actions;
	private Integer timeout;
	private Integer maxRetries;
	private Object enableBuffer;

	private final List<PollingProvider> pollingProviders;

	public RemotingApi(final String url, final String namespace) {
		this.descriptor = null;
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

	public String getDescriptor() {
		return descriptor;
	}

	public void setDescriptor(final String descriptor) {
		this.descriptor = descriptor;
	}

	public Integer getTimeout() {
		return timeout;
	}

	public void setTimeout(final Integer timeout) {
		this.timeout = timeout;
	}

	public Integer getMaxRetries() {
		return maxRetries;
	}

	public void setMaxRetries(final Integer maxRetries) {
		this.maxRetries = maxRetries;
	}

	public Object getEnableBuffer() {
		return enableBuffer;
	}

	public void setEnableBuffer(final Object enableBuffer) {
		this.enableBuffer = enableBuffer;
	}

	@JsonIgnore
	public List<PollingProvider> getPollingProviders() {
		return pollingProviders;
	}

	public void addAction(final String beanName, final Action action) {
		List<Action> beanActions = actions.get(beanName);
		if (beanActions == null) {
			beanActions = new ArrayList<Action>();
			actions.put(beanName, beanActions);
		}
		beanActions.add(action);
	}

	public void addPollingProvider(final PollingProvider pollingProvider) {
		pollingProviders.add(pollingProvider);
	}

}
