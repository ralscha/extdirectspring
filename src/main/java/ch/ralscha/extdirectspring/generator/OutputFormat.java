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
package ch.ralscha.extdirectspring.generator;

/**
 * Enumeration of all possible output formats for the model generator.
 * Difference between Touch and ExtJS is the config system that Touch uses.
 */
public enum OutputFormat {
	/**
	 * Orders the model generator to create ExtJS4 compatible code.
	 */
	EXTJS4,

	/**
	 * Orders the model generator to create Touch2 compatible code.
	 */
	TOUCH2
}
