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

import org.springframework.util.StringUtils;

public final class PollingProvider {

	private final String beanName;

	private final String method;

	private final String event;

	public PollingProvider(String beanName, String method, String event) {
		this.beanName = beanName;
		this.method = method;

		if (StringUtils.hasText(event)) {
			this.event = event.trim();
		}
		else {
			this.event = method;
		}
	}

	public String getBeanName() {
		return this.beanName;
	}

	public String getMethod() {
		return this.method;
	}

	public String getEvent() {
		return this.event;
	}

}
