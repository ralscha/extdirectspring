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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.LogFactory;
import org.codehaus.jackson.JsonEncoding;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;

import ch.ralscha.extdirectspring.bean.ExtDirectPollResponse;
import ch.ralscha.extdirectspring.bean.ExtDirectRequest;
import ch.ralscha.extdirectspring.bean.ExtDirectResponse;

public class ControllerUtil {

	private static ObjectMapper mapper = new ObjectMapper();

	@SuppressWarnings("unchecked")
	public static Map<String, Object> createRequestJson(String action, String method, int tid, Object... data) {
		ExtDirectRequest dr = new ExtDirectRequest();
		dr.setAction(action);
		dr.setMethod(method);
		dr.setTid(tid);
		dr.setType("rpc");
		dr.setData(data);
		return mapper.convertValue(dr, LinkedHashMap.class);
	}

	@SuppressWarnings("unchecked")
	public static Map<String, Object> createRequestJsonNamedParam(String action, String method, int tid,
			Map<String, Object> data) {
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
			return mapper.readValue(response, new TypeReference<List<ExtDirectResponse>>() {/*nothing here*/
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
