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
package ch.ralscha.extdirectspring.util;

import java.util.Arrays;

public final class ApiCacheKey {

	private final String apiNs;

	private final String actionNs;

	private final String remotingApiVar;

	private final String pollingUrlsVar;

	private final String group;

	private final String routerUrl;

	private final boolean debug;

	public ApiCacheKey(String apiNs, String actionNs, String remotingApiVar,
			String pollingUrlsVar, String group, String routerUrl, boolean debug) {
		this.apiNs = apiNs;
		this.actionNs = actionNs;
		this.remotingApiVar = remotingApiVar;
		this.pollingUrlsVar = pollingUrlsVar;
		this.group = group;
		this.routerUrl = routerUrl;
		this.debug = debug;
	}

	@Override
	public boolean equals(Object o) {
		if (!(o instanceof ApiCacheKey)) {
			return false;
		}

		ApiCacheKey other = (ApiCacheKey) o;
		return ExtDirectSpringUtil.equal(this.apiNs, other.apiNs)
				&& ExtDirectSpringUtil.equal(this.actionNs, other.actionNs)
				&& ExtDirectSpringUtil.equal(this.remotingApiVar, other.remotingApiVar)
				&& ExtDirectSpringUtil.equal(this.pollingUrlsVar, other.pollingUrlsVar)
				&& ExtDirectSpringUtil.equal(this.group, other.group)
				&& ExtDirectSpringUtil.equal(this.routerUrl, other.routerUrl)
				&& ExtDirectSpringUtil.equal(this.debug, other.debug);
	}

	@Override
	public int hashCode() {
		return Arrays
				.hashCode(new Object[] { this.apiNs, this.actionNs, this.remotingApiVar,
						this.pollingUrlsVar, this.routerUrl, this.group, this.debug });
	}

}
