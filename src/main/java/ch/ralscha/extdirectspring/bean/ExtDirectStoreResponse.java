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
package ch.ralscha.extdirectspring.bean;

import java.util.Collection;

/**
 * {@link Deprecated} use {@link ExtDirectStoreReadResult} instead
 * 
 * @param <T> Type of the entry inside the collection
 */
@Deprecated
public class ExtDirectStoreResponse<T> extends ExtDirectStoreReadResult<T> {

	public ExtDirectStoreResponse() {
		// default constructor
	}

	public ExtDirectStoreResponse(T record) {
		super(record);
	}

	public ExtDirectStoreResponse(T[] record) {
		super(record);
	}

	public ExtDirectStoreResponse(Collection<T> records) {
		super(records);
	}

	public ExtDirectStoreResponse(Integer total, Collection<T> records) {
		super(total, records);
	}

	public ExtDirectStoreResponse(Integer total, Collection<T> records, Boolean success) {
		super(total, records, success);
	}

}
