/**
 * Copyright 2010-2014 Ralph Schaer <ralphschaer@gmail.com>
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

import org.springframework.util.StringUtils;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
public final class RemotingApi {

	private String descriptor;

	private final String url;

	private final String namespace;

	private final String type;

	private final Map<String, List<Action>> actions;

	private Integer timeout;

	private Integer maxRetries;

	private Object enableBuffer;

	private Integer bufferLimit;

	private final List<PollingProvider> pollingProviders;

	public RemotingApi(String type, String url, String namespace) {
		this.type = type;
		this.descriptor = null;
		this.actions = new HashMap<String, List<Action>>();
		this.pollingProviders = new ArrayList<PollingProvider>();

		this.url = url;

		if (StringUtils.hasText(namespace)) {
			this.namespace = namespace.trim();
		}
		else {
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

	public void setDescriptor(String descriptor) {
		this.descriptor = descriptor;
	}

	public Integer getTimeout() {
		return timeout;
	}

	public void setTimeout(Integer timeout) {
		this.timeout = timeout;
	}

	public Integer getMaxRetries() {
		return maxRetries;
	}

	public void setMaxRetries(Integer maxRetries) {
		this.maxRetries = maxRetries;
	}

	public Object getEnableBuffer() {
		return enableBuffer;
	}

	public void setEnableBuffer(Object enableBuffer) {
		this.enableBuffer = enableBuffer;
	}

	public Integer getBufferLimit() {
		return bufferLimit;
	}

	public void setBufferLimit(Integer bufferLimit) {
		this.bufferLimit = bufferLimit;
	}

	@JsonIgnore
	public List<PollingProvider> getPollingProviders() {
		return pollingProviders;
	}

	public void addAction(String beanName, Action action) {
		List<Action> beanActions = actions.get(beanName);
		if (beanActions == null) {
			beanActions = new ArrayList<Action>();
			actions.put(beanName, beanActions);
		}
		beanActions.add(action);
	}

	public void addPollingProvider(PollingProvider pollingProvider) {
		pollingProviders.add(pollingProvider);
	}

}
