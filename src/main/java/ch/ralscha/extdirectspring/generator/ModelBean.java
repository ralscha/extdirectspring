package ch.ralscha.extdirectspring.generator;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Represents the model.
 * 
 * @author Ralph Schaer
 */
public class ModelBean {
	private String name;

	private String idProperty;

	private Map<String, ModelFieldBean> fields = new LinkedHashMap<String, ModelFieldBean>();

	private boolean pageing;

	private String readMethod;

	private String createMethod;

	private String updateMethod;

	private String destroyMethod;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getIdProperty() {
		return idProperty;
	}

	public void setIdProperty(String idProperty) {
		this.idProperty = idProperty;
	}

	public Map<String, ModelFieldBean> getFields() {
		return fields;
	}

	public void setFields(Map<String, ModelFieldBean> fields) {
		this.fields = fields;
	}

	public void addFields(List<ModelFieldBean> modelFields) {
		for (ModelFieldBean bean : modelFields) {
			fields.put(bean.getName(), bean);
		}
	}

	public ModelFieldBean getField(String fieldName) {
		return fields.get(fieldName);
	}

	public void addField(ModelFieldBean bean) {
		fields.put(bean.getName(), bean);
	}

	public boolean isPageing() {
		return pageing;
	}

	public void setPageing(boolean pageing) {
		this.pageing = pageing;
	}

	public String getReadMethod() {
		return readMethod;
	}

	public void setReadMethod(String readMethod) {
		this.readMethod = readMethod;
	}

	public String getCreateMethod() {
		return createMethod;
	}

	public void setCreateMethod(String createMethod) {
		this.createMethod = createMethod;
	}

	public String getUpdateMethod() {
		return updateMethod;
	}

	public void setUpdateMethod(String updateMethod) {
		this.updateMethod = updateMethod;
	}

	public String getDestroyMethod() {
		return destroyMethod;
	}

	public void setDestroyMethod(String destroyMethod) {
		this.destroyMethod = destroyMethod;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((createMethod == null) ? 0 : createMethod.hashCode());
		result = prime * result + ((destroyMethod == null) ? 0 : destroyMethod.hashCode());
		result = prime * result + ((fields == null) ? 0 : fields.hashCode());
		result = prime * result + ((idProperty == null) ? 0 : idProperty.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + (pageing ? 1231 : 1237);
		result = prime * result + ((readMethod == null) ? 0 : readMethod.hashCode());
		result = prime * result + ((updateMethod == null) ? 0 : updateMethod.hashCode());
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
		if (pageing != other.pageing) {
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
		return true;
	}

}
