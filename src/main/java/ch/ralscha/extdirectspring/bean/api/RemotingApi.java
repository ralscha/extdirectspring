/**
 * Copyright 2010-2016 Ralph Schaer <ralphschaer@gmail.com>
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
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

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

	private Map<String, List<Action>> actions;

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

	public void sort() {
		this.actions = new TreeMap<String, List<Action>>(this.actions);

		for (List<Action> action : this.actions.values()) {
			Collections.sort(action, new Comparator<Action>() {
				@Override
				public int compare(Action o1, Action o2) {
					return o1.getName().compareTo(o2.getName());
				}
			});
		}

		Collections.sort(this.pollingProviders, new Comparator<PollingProvider>() {
			@Override
			public int compare(PollingProvider o1, PollingProvider o2) {
				int c = o1.getBeanName().compareTo(o2.getBeanName());
				if (c == 0) {
					return o1.getMethod().compareTo(o2.getMethod());
				}
				return c;
			}
		});
	}

	public Map<String, List<Action>> getActions() {
		return this.actions;
	}

	public String getType() {
		return this.type;
	}

	public String getUrl() {
		return this.url;
	}

	public String getNamespace() {
		return this.namespace;
	}

	public String getDescriptor() {
		return this.descriptor;
	}

	public void setDescriptor(String descriptor) {
		this.descriptor = descriptor;
	}

	public Integer getTimeout() {
		return this.timeout;
	}

	public void setTimeout(Integer timeout) {
		this.timeout = timeout;
	}

	public Integer getMaxRetries() {
		return this.maxRetries;
	}

	public void setMaxRetries(Integer maxRetries) {
		this.maxRetries = maxRetries;
	}

	public Object getEnableBuffer() {
		return this.enableBuffer;
	}

	public void setEnableBuffer(Object enableBuffer) {
		this.enableBuffer = enableBuffer;
	}

	public Integer getBufferLimit() {
		return this.bufferLimit;
	}

	public void setBufferLimit(Integer bufferLimit) {
		this.bufferLimit = bufferLimit;
	}

	@JsonIgnore
	public List<PollingProvider> getPollingProviders() {
		return this.pollingProviders;
	}

	public void addAction(String beanName, Action action) {
		List<Action> beanActions = this.actions.get(beanName);
		if (beanActions == null) {
			beanActions = new ArrayList<Action>();
			this.actions.put(beanName, beanActions);
		}
		beanActions.add(action);
	}

	public void addPollingProvider(PollingProvider pollingProvider) {
		this.pollingProviders.add(pollingProvider);
	}

}
