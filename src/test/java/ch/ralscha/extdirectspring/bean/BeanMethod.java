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
package ch.ralscha.extdirectspring.bean;

public class BeanMethod {
	private final String bean;
	private final String method;
	private final Object data;

	private int tid;

	public BeanMethod(String bean, String method) {
		this(bean, method, null);
	}

	public BeanMethod(String bean, String method, Object data) {
		this.bean = bean;
		this.method = method;
		this.data = data;
	}

	public String getBean() {
		return this.bean;
	}

	public String getMethod() {
		return this.method;
	}

	public Object getData() {
		return this.data;
	}

	public int getTid() {
		return this.tid;
	}

	public void setTid(int tid) {
		this.tid = tid;
	}

}
