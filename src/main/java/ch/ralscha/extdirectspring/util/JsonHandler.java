/**
 * Copyright 2010-2011 Ralph Schaer <ralphschaer@gmail.com>
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

import org.apache.commons.logging.LogFactory;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializationConfig.Feature;
import org.codehaus.jackson.type.TypeReference;
import org.springframework.stereotype.Component;

@Component
public class JsonHandler {

	private ObjectMapper mapper;
	private ObjectMapper indentMapper;

	public JsonHandler() {
		mapper = new ObjectMapper();
		indentMapper = new ObjectMapper();
		indentMapper.getSerializationConfig().enable(Feature.INDENT_OUTPUT);
	}

	public void setMapper(ObjectMapper mapper) {
		if (mapper == null) {
			throw new IllegalArgumentException("ObjectMapper must not be null");
		}

		this.mapper = mapper;
	}

	public void setIndentMapper(ObjectMapper indentMapper) {
		if (indentMapper == null) {
			throw new IllegalArgumentException("ObjectMapper must not be null");
		}

		this.indentMapper = indentMapper;
	}

	/**
	 * Converts a object into a String containing the json representation of this
	 * object. In case of an exception returns null and logs the exception.
	 * 
	 * @param obj
	 *          the object to serialize into json
	 * @return obj in json format
	 */
	public String writeValueAsString(Object obj) {
		return writeValueAsString(obj, false);
	}

	/**
	 * Converts a object into a String containing the json representation of this
	 * object. In case of an exceptions returns null and logs the exception.
	 * 
	 * @param obj
	 *          the object to serialize into json
	 * @param indent
	 *          if false writes json on one line
	 * @return obj in json format, null if there is an exception
	 */
	public String writeValueAsString(Object obj, boolean indent) {
		try {
			if (indent) {
				return indentMapper.writeValueAsString(obj);
			}
			return mapper.writeValueAsString(obj);
		} catch (Exception e) {
			LogFactory.getLog(ExtDirectSpringUtil.class).info("serialize object to json", e);
			return null;
		}
	}

	/**
	 * Creates a object from a json String. In case of an exception returns null
	 * and logs the exception.
	 * 
	 * @param <T>
	 *          type of the object to create
	 * @param json
	 *          String with the json
	 * @param typeReference
	 *          TypeReference instance of the desired result type
	 *          {@link org.codehaus.jackson.type.TypeReference}
	 * @return the created object, null if there is an exception
	 */
	@SuppressWarnings("unchecked")
	public <T> T readValue(final String json, final TypeReference<T> typeReference) {
		try {
			return (T) mapper.readValue(json, typeReference);
		} catch (Exception e) {
			LogFactory.getLog(ExtDirectSpringUtil.class).info("deserialize json to object", e);
			return null;
		}
	}

	/**
	 * Creates a object from a json String. In case of an exception returns null
	 * and logs the exception.
	 * 
	 * @param <T>
	 *          type of the object to create
	 * @param json
	 *          String with the json
	 * @param clazz
	 *          Class of object to create
	 * @return the created object, null if there is an exception
	 */
	public <T> T readValue(final String json, final Class<T> clazz) {
		try {
			return mapper.readValue(json, clazz);
		} catch (Exception e) {
			LogFactory.getLog(ExtDirectSpringUtil.class).info("deserialize json to object", e);
			return null;
		}
	}

	public <T> T convertValue(final Object object, final Class<T> clazz) {
		return mapper.convertValue(object, clazz);
	}

}
