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
package ch.ralscha.extdirectspring.controller;

public class ApiRequestParams {

	private ApiRequestParams() {
		// this is private
	}

	private String apiNs;

	private String actionNs;

	private String remotingApiVar;

	private String pollingUrlsVar;

	private String sseVar;

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
			params.apiNs = value;
			return this;
		}

		Builder actionNs(String value) {
			params.actionNs = value;
			return this;
		}

		Builder remotingApiVar(String value) {
			params.remotingApiVar = value;
			return this;
		}

		Builder pollingUrlsVar(String value) {
			params.pollingUrlsVar = value;
			return this;
		}

		Builder sseVar(String value) {
			params.sseVar = value;
			return this;
		}

		Builder group(String value) {
			params.group = value;
			return this;
		}

		Builder fullRouterUrl(Boolean value) {
			params.fullRouterUrl = value;
			return this;
		}

		Builder format(String value) {
			params.format = value;
			return this;
		}

		Builder providerType(String value) {
			params.providerType = value;
			return this;
		}

		Builder configuration(Configuration configuration) {
			params.configuration = configuration;
			return this;
		}

		Builder baseRouterUrl(String value) {
			params.baseRouterUrl = value;
			return this;
		}

		public ApiRequestParams build() {
			return params;
		}
	}

	public String getApiNs() {
		return apiNs;
	}

	public String getActionNs() {
		return actionNs;
	}

	public String getRemotingApiVar() {
		return remotingApiVar;
	}

	public String getPollingUrlsVar() {
		return pollingUrlsVar;
	}

	public String getSseVar() {
		return sseVar;
	}

	public String getGroup() {
		return group;
	}

	public Boolean isFullRouterUrl() {
		return fullRouterUrl;
	}

	public String getBaseRouterUrl() {
		return baseRouterUrl;
	}

	public String getFormat() {
		return format;
	}

	public String getProviderType() {
		return providerType;
	}

	public Configuration getConfiguration() {
		return configuration;
	}

}
