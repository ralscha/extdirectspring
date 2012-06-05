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
package ch.ralscha.extdirectspring.util;

public class JsonTestBean {

	private Integer a;

	private String b;

	private String c;

	private Boolean d;

	private Integer[] e;

	public JsonTestBean() {
		// no action here
	}

	public JsonTestBean(final Integer a, final String b, final String c, final Boolean d, final Integer[] e) {
		this.a = a;
		this.b = b;
		this.c = c;
		this.d = d;
		this.e = e;
	}

	public Integer getA() {
		return a;
	}

	public void setA(final Integer a) {
		this.a = a;
	}

	public String getB() {
		return b;
	}

	public void setB(final String b) {
		this.b = b;
	}

	public String getC() {
		return c;
	}

	public void setC(final String c) {
		this.c = c;
	}

	public Boolean getD() {
		return d;
	}

	public void setD(final Boolean d) {
		this.d = d;
	}

	public Integer[] getE() {
		return e;
	}

	public void setE(final Integer[] e) {
		this.e = e;
	}

}
