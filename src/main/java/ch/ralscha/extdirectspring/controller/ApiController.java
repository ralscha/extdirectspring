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
package ch.ralscha.extdirectspring.controller;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.NumberUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import ch.ralscha.extdirectspring.bean.api.PollingProvider;
import ch.ralscha.extdirectspring.bean.api.RemotingApi;
import ch.ralscha.extdirectspring.util.ApiCache;
import ch.ralscha.extdirectspring.util.ApiCacheKey;
import ch.ralscha.extdirectspring.util.ExtDirectSpringUtil;
import ch.ralscha.extdirectspring.util.MethodInfo;
import ch.ralscha.extdirectspring.util.MethodInfoCache;

/**
 * Spring managed controller that handles /api.jsp and /api-debug.js requests.
 * 
 * @author Ralph Schaer
 * @author jeffreiffers
 */
@Controller
public class ApiController {

	@Autowired
	private RouterController routerController;

	/**
	 * Method that handles api.js calls. Generates a javascript with the
	 * necessary code for Ext Direct.
	 * 
	 * @param apiNs name of the namespace the variable remotingApiVar will live
	 * in. Defaults to Ext.app
	 * @param actionNs name of the namespace the action will live in.
	 * @param remotingApiVar name of the remoting api variable. Defaults to
	 * REMOTING_API
	 * @param pollingUrlsVar name of the polling urls object. Defaults to
	 * POLLING_URLS
	 * @param group name of the api group. Multiple groups delimited with comma
	 * @param fullRouterUrl if true the router property contains the full
	 * request URL with method, server and port. Defaults to false returns only
	 * the URL without method, server and port
	 * @param format only valid value is "json2. Ext Designer sends this
	 * parameter and the response is a JSON. Defaults to null and response is
	 * Javascript.
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
			@RequestParam(value = "group", required = false) final String group,
			@RequestParam(value = "fullRouterUrl", required = false, defaultValue = "false") final boolean fullRouterUrl,
			@RequestParam(value = "format", required = false) final String format, final HttpServletRequest request,
			final HttpServletResponse response) throws IOException {

		final ServletOutputStream outputStream = response.getOutputStream();

		if (format == null) {
			response.setContentType("application/x-javascript");

			String requestUrlString;

			if (fullRouterUrl) {
				requestUrlString = request.getRequestURL().toString();
			} else {
				requestUrlString = request.getRequestURI();
			}

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
				apiString = buildApiString(apiNs, actionNs, remotingApiVar, pollingUrlsVar, routerUrl, basePollUrl,
						group, debug);
				ApiCache.INSTANCE.put(apiKey, apiString);
			}

			response.setContentLength(apiString.getBytes().length);
			outputStream.write(apiString.getBytes());
		} else {
			response.setContentType(RouterController.APPLICATION_JSON.toString());
			response.setCharacterEncoding(RouterController.APPLICATION_JSON.getCharSet().name());

			String requestUrlString = request.getRequestURL().toString();

			boolean debug = requestUrlString.contains("api-debug.js");

			String routerUrl;
			if (!debug) {
				routerUrl = requestUrlString.replace("api.js", "router");
			} else {
				routerUrl = requestUrlString.replace("api-debug.js", "router");
			}

			String apiString = buildApiJson(apiNs, actionNs, remotingApiVar, routerUrl, group, debug);
			response.setContentLength(apiString.getBytes().length);
			outputStream.write(apiString.getBytes());
		}

		outputStream.flush();
	}

	private String buildApiString(final String apiNs, final String actionNs, final String remotingApiVar,
			final String pollingUrlsVar, final String routerUrl, final String basePollUrl, final String group,
			final boolean debug) {

		RemotingApi remotingApi = new RemotingApi(routerUrl, actionNs);

		remotingApi.setTimeout(routerController.getConfiguration().getTimeout());
		remotingApi.setMaxRetries(routerController.getConfiguration().getMaxRetries());

		Object enableBuffer = routerController.getConfiguration().getEnableBuffer();
		if (enableBuffer instanceof String && StringUtils.hasText((String) enableBuffer)) {
			String enableBufferString = (String) enableBuffer;
			if (enableBufferString.equalsIgnoreCase("true")) {
				remotingApi.setEnableBuffer(true);
			} else if (enableBufferString.equalsIgnoreCase("false")) {
				remotingApi.setEnableBuffer(false);
			} else {
				Integer enableBufferMs = NumberUtils.parseNumber(enableBufferString, Integer.class);
				remotingApi.setEnableBuffer(enableBufferMs);
			}
		} else if (enableBuffer instanceof Number || enableBuffer instanceof Boolean) {
			remotingApi.setEnableBuffer(enableBuffer);
		}

		buildRemotingApi(remotingApi, group);

		StringBuilder sb = new StringBuilder();

		if (StringUtils.hasText(apiNs)) {
			sb.append("Ext.ns('");
			sb.append(apiNs);
			sb.append("');");
		}

		if (debug) {
			sb.append("\n\n");
		}

		if (StringUtils.hasText(actionNs)) {
			sb.append("Ext.ns('");
			sb.append(actionNs);
			sb.append("');");

			if (debug) {
				sb.append("\n\n");
			}
		}

		String jsonConfig = routerController.getJsonHandler().writeValueAsString(remotingApi, debug);

		if (StringUtils.hasText(apiNs)) {
			sb.append(apiNs).append(".");
		}
		sb.append(remotingApiVar).append(" = ");
		sb.append(jsonConfig);
		sb.append(";");

		if (debug) {
			sb.append("\n\n");
		}

		List<PollingProvider> pollingProviders = remotingApi.getPollingProviders();
		if (!pollingProviders.isEmpty()) {

			if (StringUtils.hasText(apiNs)) {
				sb.append(apiNs).append(".");
			}
			sb.append(pollingUrlsVar).append(" = {");
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

	private String buildApiJson(final String apiNs, final String actionNs, final String remotingApiVar,
			final String routerUrl, final String group, final boolean debug) {

		RemotingApi remotingApi = new RemotingApi(routerUrl, actionNs);

		if (StringUtils.hasText(apiNs)) {
			remotingApi.setDescriptor(apiNs + "." + remotingApiVar);
		} else {
			remotingApi.setDescriptor(remotingApiVar);
		}

		buildRemotingApi(remotingApi, group);

		return routerController.getJsonHandler().writeValueAsString(remotingApi, debug);

	}

	private void buildRemotingApi(final RemotingApi remotingApi, final String group) {

		for (Map.Entry<MethodInfoCache.Key, MethodInfo> entry : MethodInfoCache.INSTANCE) {
			final MethodInfo methodInfo = entry.getValue();
			if (isSameGroup(group, methodInfo.getGroup())) {
				if (methodInfo.getAction() != null) {
					remotingApi.addAction(entry.getKey().getBeanName(), methodInfo.getAction());
				} else {
					remotingApi.addPollingProvider(methodInfo.getPollingProvider());
				}
			}
		}
	}

	private boolean isSameGroup(final String requestedGroupString, final String annotationGroupString) {
		if (requestedGroupString != null) {
			if (annotationGroupString != null) {
				for (String requestedGroup : requestedGroupString.split(",")) {
					for (String annotationGroup : annotationGroupString.split(",")) {
						if (ExtDirectSpringUtil.equal(requestedGroup, annotationGroup)) {
							return true;
						}
					}
				}
			}
			return false;
		}

		return true;
	}

}
