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
package ch.ralscha.extdirectspring.generator.association;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

/**
 * A belongsTo association
 * 
 * @author Ralph Schaer
 */
@JsonInclude(Include.NON_NULL)
public class BelongsToAssociation extends AbstractAssociation {

	private String setterName;

	private String getterName;

	/**
	 * Creates an instance of a belongsTo association. Sets {@link #model} to
	 * the provided parameters.
	 * 
	 * @param model The name of the model that is being associated with.
	 */
	public BelongsToAssociation(String model) {
		super("belongsTo", model);
	}

	/**
	 * Creates an instance of a belongsTo association. Sets {@link #model} to
	 * the full qualified name of the model class or the string from
	 * {@link Model#value()} if present on the class.
	 * 
	 * @param model The class of the model that is being associated with.
	 */
	public BelongsToAssociation(Class<?> model) {
		this(getModelName(model));
	}

	public String getSetterName() {
		return setterName;
	}

	/**
	 * The name of the setter function that will be added to the local model's
	 * prototype. Defaults to 'set' + the name of the foreign model, e.g.
	 * setCategory.
	 * <p>
	 * Corresponds to the <a href=
	 * "http://docs.sencha.com/ext-js/4-1/#!/api/Ext.data.association.BelongsTo-cfg-setterName"
	 * >setterName</a> config property.
	 */
	public void setSetterName(String setterName) {
		this.setterName = setterName;
	}

	public String getGetterName() {
		return getterName;
	}

	/**
	 * The name of the getter function that will be added to the local model's
	 * prototype. Defaults to 'get' + the name of the foreign model, e.g.
	 * getCategory.
	 * <p>
	 * Corresponds to the <a href=
	 * "http://docs.sencha.com/ext-js/4-1/#!/api/Ext.data.association.BelongsTo-cfg-getterName"
	 * >getterName</a> config property.
	 */
	public void setGetterName(String getterName) {
		this.getterName = getterName;
	}

}
