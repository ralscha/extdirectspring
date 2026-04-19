/*
 * Copyright the original author or authors.
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
package ch.ralscha.extdirectspring.bean.api;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
public class ActionDoc extends Action {

	@JsonIgnore
	protected String methodComment;

	@JsonIgnore
	protected String author;

	@JsonIgnore
	protected String version;

	/**
	 * map of method parameter names and descriptions
	 */
	@JsonIgnore
	protected Map<String, String> parameters;

	/**
	 * map of method return properties names and descriptions
	 */
	@JsonIgnore
	protected Map<String, String> returnMethod;

	@JsonIgnore
	protected boolean deprecated;

	public ActionDoc(String name, List<String> params) {
		super(name, null, null, null, params, null, null);
	}

	public ActionDoc(Action toCopy, String methodComment, String author, String version, boolean deprecated) {
		super(toCopy);
		this.methodComment = methodComment;
		this.author = author;
		this.version = version;
		this.deprecated = deprecated;
	}

	/**
	 * Returns the method comment.
	 * @return the methodComment
	 */
	public String getMethodComment() {
		return this.methodComment;
	}

	/**
	 * Sets the method comment.
	 * @param methodComment the methodComment to set
	 */
	public void setMethodComment(String methodComment) {
		this.methodComment = methodComment;
	}

	/**
	 * Returns the author.
	 * @return the author
	 */
	public String getAuthor() {
		return this.author;
	}

	/**
	 * Sets the author.
	 * @param author the author to set
	 */
	public void setAuthor(String author) {
		this.author = author;
	}

	/**
	 * Returns the version.
	 * @return the version
	 */
	public String getVersion() {
		return this.version;
	}

	/**
	 * Sets the version.
	 * @param version the version to set
	 */
	public void setVersion(String version) {
		this.version = version;
	}

	/**
	 * Returns the documented parameters.
	 * @return the parameters
	 */
	public Map<String, String> getParameters() {
		if (null == this.parameters) {
			this.parameters = new HashMap<>();
		}
		return this.parameters;
	}

	/**
	 * Sets the documented parameters.
	 * @param parameters the parameters to set
	 */
	public void setParameters(Map<String, String> parameters) {
		this.parameters = parameters;
	}

	/**
	 * Returns the documented return values.
	 * @return the returnMethod
	 */
	public Map<String, String> getReturnMethod() {
		if (null == this.returnMethod) {
			this.returnMethod = new HashMap<>();
		}
		return this.returnMethod;
	}

	/**
	 * Sets the documented return values.
	 * @param returnMethod the returnMethod to set
	 */
	public void setReturnMethod(Map<String, String> returnMethod) {
		this.returnMethod = returnMethod;
	}

	/**
	 * Returns whether the action is deprecated.
	 * @return the deprecated
	 */
	public boolean isDeprecated() {
		return this.deprecated;
	}

	/**
	 * Sets whether the action is deprecated.
	 * @param deprecated the deprecated to set
	 */
	public void setDeprecated(boolean deprecated) {
		this.deprecated = deprecated;
	}

}
