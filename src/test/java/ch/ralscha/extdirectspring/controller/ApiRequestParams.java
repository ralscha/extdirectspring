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
package ch.ralscha.extdirectspring.controller;

public class ApiRequestParams {

	private ApiRequestParams() {
		// this is private
	}

	private String apiNs;

	private String actionNs;

	private String remotingApiVar;

	private String pollingUrlsVar;

	private String group;

	private Boolean fullRouterUrl;

	private String baseRouterUrl;

	private String format;

	private String providerType;

	private Configuration configuration;

	public static Builder builder() {
		return new Builder();
	}

	public static class Builder {

		private final ApiRequestParams params = new ApiRequestParams();

		Builder apiNs(String value) {
			this.params.apiNs = value;
			return this;
		}

		Builder actionNs(String value) {
			this.params.actionNs = value;
			return this;
		}

		Builder remotingApiVar(String value) {
			this.params.remotingApiVar = value;
			return this;
		}

		Builder pollingUrlsVar(String value) {
			this.params.pollingUrlsVar = value;
			return this;
		}

		Builder group(String value) {
			this.params.group = value;
			return this;
		}

		Builder fullRouterUrl(Boolean value) {
			this.params.fullRouterUrl = value;
			return this;
		}

		Builder format(String value) {
			this.params.format = value;
			return this;
		}

		Builder providerType(String value) {
			this.params.providerType = value;
			return this;
		}

		Builder configuration(Configuration configuration) {
			this.params.configuration = configuration;
			return this;
		}

		Builder baseRouterUrl(String value) {
			this.params.baseRouterUrl = value;
			return this;
		}

		public ApiRequestParams build() {
			return this.params;
		}
	}

	public String getApiNs() {
		return this.apiNs;
	}

	public String getActionNs() {
		return this.actionNs;
	}

	public String getRemotingApiVar() {
		return this.remotingApiVar;
	}

	public String getPollingUrlsVar() {
		return this.pollingUrlsVar;
	}

	public String getGroup() {
		return this.group;
	}

	public Boolean isFullRouterUrl() {
		return this.fullRouterUrl;
	}

	public String getBaseRouterUrl() {
		return this.baseRouterUrl;
	}

	public String getFormat() {
		return this.format;
	}

	public String getProviderType() {
		return this.providerType;
	}

	public Configuration getConfiguration() {
		return this.configuration;
	}

}
