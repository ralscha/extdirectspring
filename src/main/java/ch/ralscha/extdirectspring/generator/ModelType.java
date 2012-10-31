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
package ch.ralscha.extdirectspring.generator;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Timestamp;
import java.util.Date;

/**
 * Enumeration of all types that are valid in a ExtJS and Touch model object.
 * 
 * @author Ralph Schaer
 */
public enum ModelType {

	AUTO("auto") {
		@Override
		public boolean supports(Class<?> type) {
			return false;
		}
	},

	INTEGER("int") {
		@Override
		public boolean supports(Class<?> type) {
			return type.equals(Byte.class) || type.equals(Short.class) || type.equals(Integer.class)
					|| type.equals(Long.class) || type.equals(BigInteger.class) || type.equals(Byte.TYPE)
					|| type.equals(Short.TYPE) || type.equals(Integer.TYPE) || type.equals(Long.TYPE);
		}
	},
	FLOAT("float") {
		@Override
		public boolean supports(Class<?> type) {
			return type.equals(Float.class) || type.equals(Double.class) || type.equals(BigDecimal.class)
					|| type.equals(Float.TYPE) || type.equals(Double.TYPE);
		}
	},
	STRING("string") {
		@Override
		public boolean supports(Class<?> type) {
			return type.equals(String.class);
		}
	},
	DATE("date") {
		@Override
		public boolean supports(Class<?> type) {
			return type.equals(Date.class) || type.equals(java.sql.Date.class) || type.equals(Timestamp.class)
					|| type.getName().equals("org.joda.time.DateTime")
					|| type.getName().equals("org.joda.time.LocalDate");
		}
	},
	BOOLEAN("boolean") {
		@Override
		public boolean supports(Class<?> type) {
			return type.equals(Boolean.class) || type.equals(Boolean.TYPE);
		}
	};

	private String jsName;

	private ModelType(String jsName) {
		this.jsName = jsName;
	}

	/**
	 * @return the name of the type for JS code
	 */
	public String getJsName() {
		return jsName;
	}

	/**
	 * @param type any class
	 * @return true if the type supports the provided Java class
	 */
	public abstract boolean supports(Class<?> type);

}
