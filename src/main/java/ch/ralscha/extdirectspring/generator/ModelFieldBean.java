package ch.ralscha.extdirectspring.generator;

import java.io.IOException;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

/**
 * Represents one field in a {@link ModelBean}
 * 
 * @author Ralph Schaer
 */
@JsonInclude(Include.NON_NULL)
public class ModelFieldBean {
	private String name;

	private ModelType type;

	private Object defaultValue;

	private String dateFormat;

	private Boolean useNull;

	public ModelFieldBean(String name, ModelType type) {
		this.name = name;
		this.type = type;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@JsonSerialize(using = ModelTypeSerializer.class)
	public ModelType getType() {
		return type;
	}

	public void setType(ModelType type) {
		this.type = type;
	}

	public Object getDefaultValue() {
		return defaultValue;
	}

	public void setDefaultValue(Object defaultValue) {
		this.defaultValue = defaultValue;
	}

	public String getDateFormat() {
		return dateFormat;
	}

	public void setDateFormat(String dateFormat) {
		this.dateFormat = dateFormat;
	}

	public Boolean getUseNull() {
		return useNull;
	}

	public void setUseNull(Boolean useNull) {
		this.useNull = useNull;
	}

	private final static class ModelTypeSerializer extends JsonSerializer<ModelType> {
		@Override
		public void serialize(ModelType value, JsonGenerator jgen, SerializerProvider provider) throws IOException,
				JsonProcessingException {
			jgen.writeString(value.getJsName());

		}
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((dateFormat == null) ? 0 : dateFormat.hashCode());
		result = prime * result + ((defaultValue == null) ? 0 : defaultValue.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((type == null) ? 0 : type.hashCode());
		result = prime * result + ((useNull == null) ? 0 : useNull.hashCode());
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
		ModelFieldBean other = (ModelFieldBean) obj;
		if (dateFormat == null) {
			if (other.dateFormat != null) {
				return false;
			}
		} else if (!dateFormat.equals(other.dateFormat)) {
			return false;
		}
		if (defaultValue == null) {
			if (other.defaultValue != null) {
				return false;
			}
		} else if (!defaultValue.equals(other.defaultValue)) {
			return false;
		}
		if (name == null) {
			if (other.name != null) {
				return false;
			}
		} else if (!name.equals(other.name)) {
			return false;
		}
		if (type != other.type) {
			return false;
		}
		if (useNull == null) {
			if (other.useNull != null) {
				return false;
			}
		} else if (!useNull.equals(other.useNull)) {
			return false;
		}
		return true;
	}

}
