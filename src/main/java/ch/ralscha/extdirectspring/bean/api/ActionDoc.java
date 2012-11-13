/**
 * 
 */
package ch.ralscha.extdirectspring.bean.api;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

/**
 * @author dbs
 *
 */
@JsonInclude(Include.NON_NULL)
public class ActionDoc {
	
	protected String methodComment ;
	
	protected String author;
	
	protected String version;
	
	/**
	 * map of method parameter names and descriptions
	 */
	protected Map<String, String> parameters;
	
	/**
	 * map of method return properties names and descriptions
	 */
	protected Map<String, String> returnMethod;

	
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
	public ActionDoc(String methodComment, String author, String version, Map<String, String> parameters,
			Map<String, String> returnMethod) {
		this.methodComment = methodComment;
		this.author = author;
		this.version = version;
		this.parameters = parameters;
		this.returnMethod = returnMethod;
	}
	
	public ActionDoc(String methodComment, String author, String version) {
		this.methodComment = methodComment;
		this.author = author;
		this.version = version;
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
}
