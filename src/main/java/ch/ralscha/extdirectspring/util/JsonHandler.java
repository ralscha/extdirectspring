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

import java.io.InputStream;

import org.apache.commons.logging.LogFactory;
import org.springframework.util.Assert;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Object contains an {@link ObjectMapper} and provides convenient methods.
 */
public class JsonHandler {

	private ObjectMapper mapper;

	public JsonHandler() {
		this.mapper = new ObjectMapper();
	}

	public JsonHandler(ObjectMapper mapper) {
		this.mapper = mapper;
	}

	/**
	 * Sets a new instance of {@link ObjectMapper}.
	 *
	 * @param mapper a new object mapper. must not be <code>null</code>
	 */
	public void setMapper(ObjectMapper mapper) {
		Assert.notNull(mapper, "ObjectMapper must not be null");

		this.mapper = mapper;
	}

	/**
	 * @return the currently assigned {@link ObjectMapper}
	 */
	public ObjectMapper getMapper() {
		return this.mapper;
	}

	/**
	 * Converts an object into a JSON string. In case of an exception returns null and
	 * logs the exception.
	 *
	 * @param obj the source object
	 * @return obj JSON string, <code>null</code> if an exception occurred
	 */
	public String writeValueAsString(Object obj) {
		return writeValueAsString(obj, false);
	}

	/**
	 * Converts an object into a JSON string. In case of an exceptions returns null and
	 * logs the exception.
	 *
	 * @param obj the source object
	 * @param indent if true JSON is written in a human readable format, if false JSON is
	 * written on one line
	 * @return obj JSON string, <code>null</code> if an exception occurred
	 */
	public String writeValueAsString(Object obj, boolean indent) {
		try {
			if (indent) {
				return this.mapper.writer().withDefaultPrettyPrinter()
						.writeValueAsString(obj);
			}
			return this.mapper.writeValueAsString(obj);
		}
		catch (Exception e) {
			LogFactory.getLog(JsonHandler.class).info("serialize object to json", e);
			return null;
		}
	}

	/**
	 * Converts a JSON string into an object. In case of an exception returns null and
	 * logs the exception.
	 *
	 * @param <T> type of the object to create
	 * @param json string with the JSON
	 * @param typeReference {@link TypeReference} instance of the desired result type
	 * {@link com.fasterxml.jackson.core.type.TypeReference}
	 * @return the created object, null if there was an exception
	 */
	@SuppressWarnings("unchecked")
	public <T> T readValue(String json, TypeReference<T> typeReference) {
		try {
			return (T) this.mapper.readValue(json, typeReference);
		}
		catch (Exception e) {
			LogFactory.getLog(JsonHandler.class).info("deserialize json to object", e);
			return null;
		}
	}

	/**
	 * Converts a JSON string into an object. In case of an exception returns null and
	 * logs the exception.
	 *
	 * @param <T> type of the object to create
	 * @param json string with the JSON
	 * @param clazz class of object to create
	 * @return the converted object, null if there is an exception
	 */
	public <T> T readValue(String json, Class<T> clazz) {
		try {
			return this.mapper.readValue(json, clazz);
		}
		catch (Exception e) {
			LogFactory.getLog(JsonHandler.class).info("deserialize json to object", e);
			return null;
		}
	}

	/**
	 * Converts a JSON string into an object. The input is read from an InputStream. In
	 * case of an exception returns null and logs the exception.
	 *
	 * @param is a InputStream
	 * @param clazz class of object to create
	 * @return the converted object, null if there is an exception
	 */
	public Object readValue(InputStream is, Class<Object> clazz) {
		try {
			return this.mapper.readValue(is, clazz);
		}
		catch (Exception e) {
			LogFactory.getLog(JsonHandler.class).info("deserialize json to object", e);
			return null;
		}
	}

	/**
	 * Converts one object into another.
	 *
	 * @param object the source
	 * @param clazz the type of the target
	 * @return the converted object
	 */
	public <T> T convertValue(Object object, Class<T> clazz) {
		return this.mapper.convertValue(object, clazz);
	}

	/**
	 * Converts one object into another.
	 *
	 * @param object the source
	 * @param toValueTypeRef the type of the target
	 * @return the converted object
	 */
	public <T> T convertValue(Object object, JavaType toValueTypeRef) {
		return this.mapper.convertValue(object, toValueTypeRef);
	}
}
