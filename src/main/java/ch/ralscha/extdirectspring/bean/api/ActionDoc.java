/**
 * 
 */
package ch.ralscha.extdirectspring.bean.api;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

/**
 * @author dbs
 *
 */
@JsonInclude(Include.NON_NULL)
public class ActionDoc extends Action{
	
	@JsonIgnore
	protected String methodComment ;
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

	public ActionDoc(String name, Integer len, Boolean formHandler) {
		super(name, len, formHandler);
	}

	public ActionDoc(String name, List<String> params) {
		super(name, params);
	}
	
	/**
	 * @param name
	 * @param len
	 * @param formHandler
	 * @param methodComment
	 * @param author
	 * @param version
	 * @param parameters
	 * @param returnMethod
	 */
	public ActionDoc(String name, Integer len, Boolean formHandler, String methodComment, String author, String version, boolean deprecated) {
		this(name, len, formHandler);
		this.methodComment = methodComment;
		this.author = author;
		this.version = version;
		this.deprecated = deprecated;
	}
	
	public ActionDoc(String name, List<String> params, String methodComment, String author, String version, boolean deprecated) {
		this(name, params);
		this.methodComment = methodComment;
		this.author = author;
		this.version = version;
		this.deprecated = deprecated;
	}
	
	public ActionDoc(Action toCopy, String methodComment, String author, String version, boolean deprecated) {
		super(toCopy);
		this.methodComment = methodComment;
		this.author = author;
		this.version = version;
		this.deprecated = deprecated;
	}
	

	/**
	 * @return the methodComment
	 */
	public String getMethodComment() {
		return methodComment;
	}

	/**
	 * @param methodComment the methodComment to set
	 */
	public void setMethodComment(String methodComment) {
		this.methodComment = methodComment;
	}

	/**
	 * @return the author
	 */
	public String getAuthor() {
		return author;
	}

	/**
	 * @param author the author to set
	 */
	public void setAuthor(String author) {
		this.author = author;
	}

	/**
	 * @return the version
	 */
	public String getVersion() {
		return version;
	}

	/**
	 * @param version the version to set
	 */
	public void setVersion(String version) {
		this.version = version;
	}

	/**
	 * @return the parameters
	 */
	public Map<String, String> getParameters() {
		if(null == parameters)
			parameters = new HashMap<String, String>();
		return parameters;
	}

	/**
	 * @param parameters the parameters to set
	 */
	public void setParameters(Map<String, String> parameters) {
		this.parameters = parameters;
	}

	/**
	 * @return the returnMethod
	 */
	public Map<String, String> getReturnMethod() {
		if(null == returnMethod)
			returnMethod = new HashMap<String, String>();
		return returnMethod;
	}

	/**
	 * @param returnMethod the returnMethod to set
	 */
	public void setReturnMethod(Map<String, String> returnMethod) {
		this.returnMethod = returnMethod;
	}

	/**
	 * @return the deprecated
	 */
	public boolean isDeprecated() {
		return deprecated;
	}

	/**
	 * @param deprecated the deprecated to set
	 */
	public void setDeprecated(boolean deprecated) {
		this.deprecated = deprecated;
	}
}
