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

import static org.fest.assertions.Assertions.assertThat;
import static org.junit.Assert.fail;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.commons.logging.LogFactory;
import org.codehaus.jackson.JsonEncoding;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import ch.ralscha.extdirectspring.bean.ExtDirectPollResponse;
import ch.ralscha.extdirectspring.bean.ExtDirectRequest;
import ch.ralscha.extdirectspring.bean.ExtDirectResponse;
import ch.ralscha.extdirectspring.util.JsonHandler;

public class ControllerUtil {

	private static ObjectMapper mapper = new ObjectMapper();

	public static Map<String, Object> createRequestJson(String action, String method, int tid, Object data) {
		return createRequestJson(action, method, false, tid, data);
	}

	@SuppressWarnings("unchecked")
	public static Map<String, Object> createRequestJson(String action, String method, boolean namedParameter, int tid, Object data) {
		ExtDirectRequest dr = new ExtDirectRequest();
		dr.setAction(action);
		dr.setMethod(method);
		dr.setTid(tid);
		dr.setType("rpc");

		if (namedParameter || data instanceof Object[] || data == null) {
			dr.setData(data);
		} else {
			dr.setData(new Object[] { data });
		}
		return mapper.convertValue(dr, LinkedHashMap.class);
	}

	public static Object sendAndReceive(RouterController controller, String action, String method, Object data, Object result) {
		MockHttpServletRequest request = new MockHttpServletRequest();
		return sendAndReceive(controller, request, action, method, false, data, result);
	}

	public static Object sendAndReceive(RouterController controller, String action, String method, boolean namedParameter, Object data,
			Object result) {
		MockHttpServletRequest request = new MockHttpServletRequest();
		return sendAndReceive(controller, request, action, method, namedParameter, data, result);
	}

	public static Object sendAndReceive(RouterController controller, MockHttpServletRequest request, String action, String method,
			Object data, Object result) {
		return sendAndReceive(controller, request, action, method, false, data, result);
	}

	public static Object sendAndReceive(RouterController controller, MockHttpServletRequest request, String action, String method,
			boolean namedParameter, Object data, Object result) {

		MockHttpServletResponse response = new MockHttpServletResponse();

		int tid = (int) (Math.random() * 1000);
		Map<String, Object> edRequest = createRequestJson(action, method, namedParameter, tid, data);

		request.setContent(ControllerUtil.writeAsByte(edRequest));
		try {
			controller.router(request, response, Locale.ENGLISH);
		} catch (IOException e) {
			fail("call controller.router: " + e.getMessage());
		}
		List<ExtDirectResponse> responses = readDirectResponses(response.getContentAsByteArray());
		assertThat(responses).hasSize(1);

		ExtDirectResponse edResponse = responses.get(0);

		assertThat(edResponse.getAction()).isEqualTo(action);
		assertThat(edResponse.getMethod()).isEqualTo(method);
		assertThat(edResponse.getTid()).isEqualTo(tid);
		assertThat(edResponse.getWhere()).isNull();

		if (result == null) {
			assertThat(edResponse.getType()).isEqualTo("exception");
			assertThat(edResponse.getResult()).isNull();
			assertThat(edResponse.getMessage()).isEqualTo("Server Error");
		} else {
			assertThat(edResponse.getType()).isEqualTo("rpc");
			assertThat(edResponse.getMessage()).isNull();
			if (result == Void.TYPE) {
				assertThat(edResponse.getResult()).isNull();
			} else if (result instanceof Class<?>) {
				Object r = ControllerUtil.convertValue(edResponse.getResult(), (Class<?>) result);
				return r;
			} else if (result instanceof TypeReference) {
				Object r = ControllerUtil.convertValue(edResponse.getResult(), (TypeReference<?>) result);
				return r;
			} else {
				assertThat(edResponse.getResult()).isEqualTo(result);
			}
		}

		return edResponse.getResult();
	}

	@SuppressWarnings("unchecked")
	public static Map<String, Object> createRequestJsonNamedParam(String action, String method, int tid, Map<String, Object> data) {
		ExtDirectRequest dr = new ExtDirectRequest();
		dr.setAction(action);
		dr.setMethod(method);
		dr.setTid(tid);
		dr.setType("rpc");
		dr.setData(data);
		return mapper.convertValue(dr, LinkedHashMap.class);
	}

	@SuppressWarnings("unchecked")
	public static <T> T readValue(final String json, final Class<?> clazz) {
		try {
			return (T) mapper.readValue(json, clazz);
		} catch (Exception e) {
			LogFactory.getLog(JsonHandler.class).info("deserialize json to object", e);
			return null;
		}
	}

	public static <T> T convertValue(final Object object, final Class<T> clazz) {
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
			mapper.getJsonFactory().createJsonGenerator(bos, JsonEncoding.UTF8);
			return mapper.writeValueAsBytes(obj);
		} catch (JsonGenerationException e) {
			throw new RuntimeException(e);
		} catch (JsonMappingException e) {
			throw new RuntimeException(e);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

	}

}
