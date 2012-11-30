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

import ch.ralscha.extdirectspring.generator.Model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

/**
 * A hasMany association
 */
@JsonInclude(Include.NON_NULL)
public class HasManyAssociation extends AbstractAssociation {

	private Boolean autoLoad;

	private String name;

	/**
	 * Creates an instance of a hasMany association. Sets {@link #model} to the
	 * provided parameters.
	 * 
	 * @param model The name of the model that is being associated with.
	 */
	public HasManyAssociation(String model) {
		super("hasMany", model);
	}

	/**
	 * Creates an instance of a hasMany association. Sets {@link #model} to the
	 * full qualified name of the model class or the string from
	 * {@link Model#value()} if present on the class.
	 * 
	 * @param model The class of the model that is being associated with.
	 */
	public HasManyAssociation(Class<?> model) {
		this(getModelName(model));
	}

	public Boolean getAutoLoad() {
		return autoLoad;
	}

	/**
	 * True to automatically load the related store from a remote source when
	 * instantiated. Defaults to false.
	 * <p>
	 * Corresponds to the <a href=
	 * "http://docs.sencha.com/ext-js/4-1/#!/api/Ext.data.association.HasMany-cfg-autoLoad"
	 * >autoLoad</a> config property.
	 * 
	 * @param autoLoad the new value for autoLoad
	 */
	public void setAutoLoad(Boolean autoLoad) {
		this.autoLoad = autoLoad;
	}

	public String getName() {
		return name;
	}

	/**
	 * The name of the function to create on the owner model to retrieve the
	 * child store. If not specified, the pluralized name of the child model is
	 * used. Always specify this if the class name contains a package component.
	 * <p>
	 * Corresponds to the <a href=
	 * "http://docs.sencha.com/ext-js/4-1/#!/api/Ext.data.association.HasMany-cfg-name"
	 * >name</a> config property.
	 * 
	 * @param name the new name for the function
	 */
	public void setName(String name) {
		this.name = name;
	}

}
