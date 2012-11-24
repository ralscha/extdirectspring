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

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.util.Assert;

import ch.ralscha.extdirectspring.bean.ExtDirectStoreReadResult;
import ch.ralscha.extdirectspring.generator.validation.AbstractValidation;

/**
 * Represents a model. This object can be used to create JS code with
 * {@link ModelGenerator#writeModel(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse, ModelBean, OutputFormat, boolean)}
 * or
 * {@link ModelGenerator#generateJavascript(ModelBean, OutputFormat, boolean)}.
 * 
 * @author Ralph Schaer
 */
public class ModelBean {
	private String name;

	private String idProperty;

	private Map<String, ModelFieldBean> fields = new LinkedHashMap<String, ModelFieldBean>();

	private List<AbstractValidation> validations = new ArrayList<AbstractValidation>();

	private List<ModelAssociationBean> associations = new ArrayList<ModelAssociationBean>();

	private boolean paging;

	private String readMethod;

	private String createMethod;

	private String updateMethod;

	private String destroyMethod;

	public String getName() {
		return name;
	}

	/**
	 * "Classname" of the model. See <a
	 * href="http://docs.sencha.com/ext-js/4-1/#!/api/Ext.data.Model"
	 * >Ext.data.Model</a>.
	 * 
	 * @param name new name for the model object
	 */
	public void setName(String name) {
		this.name = name;
	}

	public String getIdProperty() {
		return idProperty;
	}

	/**
	 * Name of the id property. See <a href=
	 * "http://docs.sencha.com/ext-js/4-1/#!/api/Ext.data.Model-cfg-idProperty"
	 * >Ext.data.Model#idProperty</a>.
	 * 
	 * @param idProperty new value for the idProperty config option
	 */
	public void setIdProperty(String idProperty) {
		this.idProperty = idProperty;
	}

	public Map<String, ModelFieldBean> getFields() {
		return fields;
	}

	/**
	 * Overwrites all field definitions with the provided map.
	 * 
	 * @param fields new collection of {@link ModelFieldBean}
	 */
	public void setFields(Map<String, ModelFieldBean> fields) {
		this.fields = fields;
	}

	public List<AbstractValidation> getValidations() {
		return validations;
	}

	public void setValidations(List<AbstractValidation> validations) {
		this.validations = validations;
	}

	public List<ModelAssociationBean> getAssociations() {
		return associations;
	}

	public void setAssociations(List<ModelAssociationBean> associations) {
		this.associations = associations;
	}

	/**
	 * Add all provided fields to the collection of fields
	 * 
	 * @param modelFields collection of {@link ModelFieldBean}
	 */
	public void addFields(List<ModelFieldBean> modelFields) {
		Assert.notNull(modelFields, "modelFields must not be null");

		for (ModelFieldBean bean : modelFields) {
			fields.put(bean.getName(), bean);
		}
	}

	public void addValidations(List<AbstractValidation> fieldValidations) {
		Assert.notNull(fieldValidations, "fieldValidations must not be null");

		validations.addAll(fieldValidations);
	}

	public void addAssociations(List<ModelAssociationBean> associationsList) {
		Assert.notNull(associationsList, "associations must not be null");

		associations.addAll(associationsList);
	}

	/**
	 * Looks for the {@link ModelFieldBean} with the provided name and returns
	 * it.
	 * 
	 * @param fieldName name of a field
	 * @return a {@link ModelFieldBean} or null if not found
	 */
	public ModelFieldBean getField(String fieldName) {
		return fields.get(fieldName);
	}

	/**
	 * Adds one instance of {@link ModelFieldBean} to the internal collection of
	 * fields
	 * 
	 * @param bean instance of {@link ModelFieldBean}
	 */
	public void addField(ModelFieldBean bean) {
		Assert.notNull(bean, "ModelFieldBean must not be null");

		fields.put(bean.getName(), bean);
	}

	/**
	 * Adds one instance of {@link ModelFieldValidationBean} to the internal
	 * collection of validations
	 * 
	 * @param bean instance of {@link ModelFieldValidationBean}
	 */
	public void addValidation(AbstractValidation bean) {
		Assert.notNull(bean, "ModelFieldValidationBean must not be null");

		validations.add(bean);
	}

	/**
	 * Adds one instance of {@link ModelAssociationBean} to the internal
	 * collection of associations
	 * 
	 * @param bean instance of {@link ModelAssociationBean}
	 */
	public void addAssociation(ModelAssociationBean bean) {
		Assert.notNull(bean, "ModelAssociationBean must not be null");

		associations.add(bean);
	}

	public boolean isPaging() {
		return paging;
	}

	/**
	 * If true a reader config with root : 'records' will be added to the model
	 * object. This configuration is needef it the STORE_READ method return an
	 * instance of {@link ExtDirectStoreReadResult}
	 * 
	 * <pre>
	 * reader : {
	 *   root : 'records'
	 * }
	 * </pre>
	 * 
	 * @param paging new value for paging
	 */
	public void setPaging(boolean paging) {
		this.paging = paging;
	}

	public String getReadMethod() {
		return readMethod;
	}

	/**
	 * Specifies the read method. This is a ExtDirect reference in the form
	 * action.methodName. See <a href=
	 * "http://docs.sencha.com/ext-js/4-1/#!/api/Ext.data.proxy.Direct-cfg-api"
	 * >Ext.data.proxy.Direct#api</a>.
	 * <p>
	 * If only the readMethod is specified generator will write property <a
	 * href=
	 * "http://docs.sencha.com/ext-js/4-1/#!/api/Ext.data.proxy.Direct-cfg-directFn"
	 * >directFn</a> instead.
	 * 
	 * @param readMethod new value for read method
	 */
	public void setReadMethod(String readMethod) {
		this.readMethod = readMethod;
	}

	public String getCreateMethod() {
		return createMethod;
	}

	/**
	 * Specifies the create method. This is a ExtDirect reference in the form
	 * action.methodName. See <a href=
	 * "http://docs.sencha.com/ext-js/4-1/#!/api/Ext.data.proxy.Direct-cfg-api"
	 * >Ext.data.proxy.Direct#api</a>.
	 * 
	 * @param createMethod new value for create method
	 */
	public void setCreateMethod(String createMethod) {
		this.createMethod = createMethod;
	}

	public String getUpdateMethod() {
		return updateMethod;
	}

	/**
	 * Specifies the update method. This is a ExtDirect reference in the form
	 * action.methodName. See <a href=
	 * "http://docs.sencha.com/ext-js/4-1/#!/api/Ext.data.proxy.Direct-cfg-api"
	 * >Ext.data.proxy.Direct#api</a>.
	 * 
	 * @param updateMethod new value for update method
	 */
	public void setUpdateMethod(String updateMethod) {
		this.updateMethod = updateMethod;
	}

	public String getDestroyMethod() {
		return destroyMethod;
	}

	/**
	 * Specifies the destroy method. This is a ExtDirect reference in the form
	 * action.methodName. See <a href=
	 * "http://docs.sencha.com/ext-js/4-1/#!/api/Ext.data.proxy.Direct-cfg-api"
	 * >Ext.data.proxy.Direct#api</a>.
	 * 
	 * @param destroyMethod new value for destroy method
	 */
	public void setDestroyMethod(String destroyMethod) {
		this.destroyMethod = destroyMethod;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((associations == null) ? 0 : associations.hashCode());
		result = prime * result + ((createMethod == null) ? 0 : createMethod.hashCode());
		result = prime * result + ((destroyMethod == null) ? 0 : destroyMethod.hashCode());
		result = prime * result + ((fields == null) ? 0 : fields.hashCode());
		result = prime * result + ((idProperty == null) ? 0 : idProperty.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + (paging ? 1231 : 1237);
		result = prime * result + ((readMethod == null) ? 0 : readMethod.hashCode());
		result = prime * result + ((updateMethod == null) ? 0 : updateMethod.hashCode());
		result = prime * result + ((validations == null) ? 0 : validations.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		ModelBean other = (ModelBean) obj;
		if (associations == null) {
			if (other.associations != null) {
				return false;
			}
		} else if (!associations.equals(other.associations)) {
			return false;
		}
		if (createMethod == null) {
			if (other.createMethod != null) {
				return false;
			}
		} else if (!createMethod.equals(other.createMethod)) {
			return false;
		}
		if (destroyMethod == null) {
			if (other.destroyMethod != null) {
				return false;
			}
		} else if (!destroyMethod.equals(other.destroyMethod)) {
			return false;
		}
		if (fields == null) {
			if (other.fields != null) {
				return false;
			}
		} else if (!fields.equals(other.fields)) {
			return false;
		}
		if (idProperty == null) {
			if (other.idProperty != null) {
				return false;
			}
		} else if (!idProperty.equals(other.idProperty)) {
			return false;
		}
		if (name == null) {
			if (other.name != null) {
				return false;
			}
		} else if (!name.equals(other.name)) {
			return false;
		}
		if (paging != other.paging) {
			return false;
		}
		if (readMethod == null) {
			if (other.readMethod != null) {
				return false;
			}
		} else if (!readMethod.equals(other.readMethod)) {
			return false;
		}
		if (updateMethod == null) {
			if (other.updateMethod != null) {
				return false;
			}
		} else if (!updateMethod.equals(other.updateMethod)) {
			return false;
		}
		if (validations == null) {
			if (other.validations != null) {
				return false;
			}
		} else if (!validations.equals(other.validations)) {
			return false;
		}
		return true;
	}

}
