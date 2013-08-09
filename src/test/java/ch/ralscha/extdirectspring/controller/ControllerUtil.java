/**
 * Copyright 2010-2013 Ralph Schaer <ralphschaer@gmail.com>
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

import static org.fest.assertions.api.Assertions.assertThat;
import static org.junit.Assert.fail;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.LogFactory;
import org.fest.util.Arrays;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import ch.ralscha.extdirectspring.bean.ExtDirectPollResponse;
import ch.ralscha.extdirectspring.bean.ExtDirectRequest;
import ch.ralscha.extdirectspring.bean.ExtDirectResponse;
import ch.ralscha.extdirectspring.bean.SSEvent;
import ch.ralscha.extdirectspring.util.ExtDirectSpringUtil;
import ch.ralscha.extdirectspring.util.JsonHandler;

import com.fasterxml.jackson.core.JsonEncoding;
import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class ControllerUtil {

	private static ObjectMapper mapper = new ObjectMapper();

	static {
		mapper.configure(JsonParser.Feature.ALLOW_COMMENTS, true);
	}

	public static ExtDirectPollResponse performPollRequest(MockMvc mockMvc, String bean, String method, String event,
			Map<String, String> params, HttpHeaders headers) throws Exception {
		return performPollRequest(mockMvc, bean, method, event, params, headers, false);
	}

	public static ExtDirectPollResponse performPollRequest(MockMvc mockMvc, String bean, String method, String event,
			Map<String, String> params, HttpHeaders headers, boolean withSession) throws Exception {
		MockHttpServletRequestBuilder request = post("/poll/" + bean + "/" + method + "/" + event)
				.accept(MediaType.ALL).contentType(MediaType.APPLICATION_JSON).characterEncoding("UTF-8");

		if (withSession) {
			request.session(new MockHttpSession());
		}

		if (params != null) {
			for (String paramName : params.keySet()) {
				request.param(paramName, params.get(paramName));
			}
		}

		if (headers != null) {
			request.headers(headers);
		}

		MvcResult result = mockMvc.perform(request).andExpect(status().isOk())
				.andExpect(content().contentType("application/json;charset=UTF-8"))
				.andExpect(content().encoding("UTF-8")).andReturn();

		return readDirectPollResponse(result.getResponse().getContentAsByteArray());
	}

	public static List<SSEvent> performSseRequest(MockMvc mockMvc, String bean, String method,
			Map<String, String> params, HttpHeaders headers) throws Exception {
		return performSseRequest(mockMvc, bean, method, params, headers, false);
	}

	public static List<SSEvent> performSseRequest(MockMvc mockMvc, String bean, String method,
			Map<String, String> params, HttpHeaders headers, boolean withSession) throws Exception {
		MockHttpServletRequestBuilder request = post("/sse/" + bean + "/" + method).accept(MediaType.ALL)
				.contentType(MediaType.parseMediaType("text/event-stream")).characterEncoding("UTF-8");

		if (withSession) {
			request.session(new MockHttpSession());
		}

		if (params != null) {
			for (String paramName : params.keySet()) {
				request.param(paramName, params.get(paramName));
			}
		}

		if (headers != null) {
			request.headers(headers);
		}

		MvcResult result = mockMvc.perform(request).andExpect(status().isOk())
				.andExpect(content().contentType("text/event-stream;charset=UTF-8"))
				.andExpect(content().encoding("UTF-8")).andReturn();

		return readDirectSseResponse(result.getResponse().getContentAsByteArray());
	}

	public static MvcResult performRouterRequest(MockMvc mockMvc, String content) throws Exception {
		return performRouterRequest(mockMvc, content, null, null, false);
	}

	public static MvcResult performRouterRequest(MockMvc mockMvc, String content, Map<String, String> params,
			HttpHeaders headers, boolean withSession) throws Exception {

		MockHttpServletRequestBuilder request = post("/router").accept(MediaType.ALL)
				.contentType(MediaType.APPLICATION_JSON).characterEncoding("UTF-8");

		if (withSession) {
			request.session(new MockHttpSession());
		}

		if (content != null) {
			request.content(content);
		}

		if (params != null) {
			for (String paramName : params.keySet()) {
				request.param(paramName, params.get(paramName));
			}

		}

		if (headers != null) {
			request.headers(headers);
		}

		return mockMvc.perform(request).andExpect(status().isOk())
				.andExpect(content().contentType("application/json;charset=UTF-8"))
				.andExpect(content().encoding("UTF-8")).andReturn();

	}

	public static String createEdsRequest(String action, String method, int tid, Object data) {
		return createEdsRequest(action, method, false, tid, data);
	}

	public static String createEdsRequest(String action, String method, boolean namedParameter, int tid, Object data) {
		ExtDirectRequest dr = new ExtDirectRequest();
		dr.setAction(action);
		dr.setMethod(method);
		dr.setTid(tid);
		dr.setType("rpc");

		if (namedParameter && data != null) {
			if (Arrays.isArray(data)) {
				dr.setData(((Object[]) data)[0]);
			} else {
				dr.setData(data);
			}
		} else if (data instanceof Object[] || data == null) {
			dr.setData(data);
		} else {
			dr.setData(new Object[] { data });
		}
		try {
			return mapper.writeValueAsString(dr);
		} catch (JsonProcessingException e) {
			fail("createEdsRequest: " + e.getMessage());
		}
		return null;
	}

	public static Object sendAndReceive(MockMvc mockMvc, HttpHeaders headers, String bean, String method,
			Object expectedResultOrType, Object... requestData) {
		return sendAndReceive(mockMvc, false, headers, bean, method, false, expectedResultOrType, requestData);
	}

	public static Object sendAndReceive(MockMvc mockMvc, String bean, String method, Object expectedResultOrType,
			Object... requestData) {
		return sendAndReceive(mockMvc, false, null, bean, method, false, expectedResultOrType, requestData);
	}

	public static Object sendAndReceiveNamed(MockMvc mockMvc, String bean, String method, Object expectedResultOrType,
			Map<String, Object> requestData) {
		return sendAndReceive(mockMvc, false, null, bean, method, true, expectedResultOrType,
				new Object[] { requestData });
	}

	public static Object sendAndReceiveWithSession(MockMvc mockMvc, HttpHeaders headers, String bean, String method,
			Object expectedResultOrType, Object... requestData) {
		return sendAndReceive(mockMvc, true, headers, bean, method, true, expectedResultOrType,
				new Object[] { requestData });
	}

	public static Object sendAndReceive(MockMvc mockMvc, boolean withSession, HttpHeaders headers, String bean,
			String method, boolean namedParameters, Object expectedResultOrType, Object... requestData) {

		int tid = (int) (Math.random() * 1000);

		MvcResult result = null;
		try {
			result = performRouterRequest(mockMvc, createEdsRequest(bean, method, namedParameters, tid, requestData),
					null, headers, withSession);
		} catch (JsonProcessingException e) {
			fail("perform post to /router" + e.getMessage());
			return null;
		} catch (Exception e) {
			fail("perform post to /router" + e.getMessage());
			return null;
		}

		List<ExtDirectResponse> responses = readDirectResponses(result.getResponse().getContentAsByteArray());
		assertThat(responses).hasSize(1);

		ExtDirectResponse edResponse = responses.get(0);

		assertThat(edResponse.getAction()).isEqualTo(bean);
		assertThat(edResponse.getMethod()).isEqualTo(method);
		assertThat(edResponse.getTid()).isEqualTo(tid);
		assertThat(edResponse.getWhere()).isNull();

		if (expectedResultOrType == null) {
			assertThat(edResponse.getType()).isEqualTo("exception");
			assertThat(edResponse.getResult()).isNull();
			assertThat(edResponse.getMessage()).isEqualTo("Server Error");
		} else {
			assertThat(edResponse.getType()).isEqualTo("rpc");
			assertThat(edResponse.getMessage()).isNull();
			if (expectedResultOrType == Void.TYPE) {
				assertThat(edResponse.getResult()).isNull();
			} else if (expectedResultOrType instanceof Class<?>) {
				Object r = ControllerUtil.convertValue(edResponse.getResult(), (Class<?>) expectedResultOrType);
				return r;
			} else if (expectedResultOrType instanceof TypeReference) {
				Object r = ControllerUtil.convertValue(edResponse.getResult(), (TypeReference<?>) expectedResultOrType);
				return r;
			} else {
				assertThat(edResponse.getResult()).isEqualTo(expectedResultOrType);
			}
		}

		return edResponse.getResult();

	}

	public static Object sendAndReceiveObject(MockMvc mockMvc, String bean, String method) {
		int tid = (int) (Math.random() * 1000);

		MvcResult result = null;
		try {
			result = performRouterRequest(mockMvc, createEdsRequest(bean, method, false, tid, null), null, null, false);
		} catch (JsonProcessingException e) {
			fail("perform post to /router" + e.getMessage());
			return null;
		} catch (Exception e) {
			fail("perform post to /router" + e.getMessage());
			return null;
		}

		List<ExtDirectResponse> responses = readDirectResponses(result.getResponse().getContentAsByteArray());
		assertThat(responses).hasSize(1);

		ExtDirectResponse edResponse = responses.get(0);

		assertThat(edResponse.getAction()).isEqualTo(bean);
		assertThat(edResponse.getMethod()).isEqualTo(method);
		assertThat(edResponse.getTid()).isEqualTo(tid);
		assertThat(edResponse.getWhere()).isNull();

		return edResponse.getResult();
	}

	@SuppressWarnings("unchecked")
	public static Map<String, Object> sendAndReceiveMap(MockMvc mockMvc, String bean, String method) {
		return (Map<String, Object>) sendAndReceiveObject(mockMvc, bean, method);
	}

	public static <T> T readValue(String json, Class<?> clazz) {
		try {
			return (T) mapper.readValue(json, clazz);
		} catch (Exception e) {
			LogFactory.getLog(JsonHandler.class).info("deserialize json to object", e);
			return null;
		}
	}

	public static <T> T convertValue(Object object, Class<T> clazz) {
		return mapper.convertValue(object, clazz);
	}

	public static <T> T convertValue(Object object, TypeReference<T> typeReference) {
		return mapper.convertValue(object, typeReference);
	}

	public static List<ExtDirectResponse> readDirectResponses(byte[] response) {
		try {
			return mapper.readValue(response, new TypeReference<List<ExtDirectResponse>>() {/*
																							 * nothing
																							 * here
																							 */
			});
		} catch (JsonParseException e) {
			throw new RuntimeException(e);
		} catch (JsonMappingException e) {
			throw new RuntimeException(e);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public static ExtDirectResponse readDirectResponse(byte[] response) {
		try {
			return mapper.readValue(response, ExtDirectResponse.class);
		} catch (JsonParseException e) {
			throw new RuntimeException(e);
		} catch (JsonMappingException e) {
			throw new RuntimeException(e);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public static ExtDirectPollResponse readDirectPollResponse(byte[] response) {
		try {
			return mapper.readValue(response, ExtDirectPollResponse.class);
		} catch (JsonParseException e) {
			throw new RuntimeException(e);
		} catch (JsonMappingException e) {
			throw new RuntimeException(e);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public static byte[] writeAsByte(Object obj) {
		try {
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			mapper.getFactory().createGenerator(bos, JsonEncoding.UTF8);
			return mapper.writeValueAsBytes(obj);
		} catch (JsonGenerationException e) {
			throw new RuntimeException(e);
		} catch (JsonMappingException e) {
			throw new RuntimeException(e);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public static List<SSEvent> readDirectSseResponse(byte[] contentAsByteArray) {

		List<SSEvent> events = new ArrayList<SSEvent>();

		StringBuilder commentLines = new StringBuilder(32);
		StringBuilder dataLines = new StringBuilder(32);

		SSEvent event = null;
		String content = new String(contentAsByteArray, ExtDirectSpringUtil.UTF8_CHARSET);
		for (String line : content.split("\\n")) {

			if (line.isEmpty() && event != null) {
				if (dataLines.length() > 0) {
					event.setData(dataLines.toString());
				}
				if (commentLines.length() > 0) {
					event.setComment(commentLines.toString());
				}
				events.add(event);
				event = null;
				commentLines = new StringBuilder(32);
				dataLines = new StringBuilder(32);
				continue;
			} else if (event == null) {
				event = new SSEvent();
			}

			if (line.startsWith(":")) {
				if (commentLines.length() > 0) {
					commentLines.append("\n");
				}
				commentLines.append(line.substring(1).trim());
			} else if (line.startsWith("data:")) {
				if (dataLines.length() > 0) {
					dataLines.append("\n");
				}
				dataLines.append(line.substring(5).trim());
			} else if (line.startsWith("retry:")) {
				event.setRetry(Integer.valueOf(line.substring(6).trim()));
			} else if (line.startsWith("event:")) {
				event.setEvent(line.substring(6).trim());
			} else if (line.startsWith("id:")) {
				event.setId(line.substring(3).trim());
			}
		}

		if (event != null) {
			if (dataLines.length() > 0) {
				event.setData(dataLines.toString());
			}
			if (commentLines.length() > 0) {
				event.setComment(commentLines.toString());
			}
			events.add(event);
		}

		return events;
	}

}
