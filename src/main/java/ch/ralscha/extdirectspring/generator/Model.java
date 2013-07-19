/**
 * Copyright 2010-2013 Ralph Schaer <ralphschaer@gmail.com>
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

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import ch.ralscha.extdirectspring.bean.ExtDirectStoreReadResult;

/**
 * Annotation to configure different aspects of a model object
 */
@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface Model {
	/**
	 * "Classname" of the model. See <a
	 * href="http://docs.sencha.com/ext-js/4-2/#!/api/Ext.data.Model"
	 * >Ext.data.Model</a>.
	 * <p>
	 * If not present full qualified name of the class is used.
	 */
	String value() default "";

	/**
	 * Name of the id property. See <a href=
	 * "http://docs.sencha.com/ext-js/4-2/#!/api/Ext.data.Model-cfg-idProperty"
	 * >Ext.data.Model#idProperty</a>. This also sets the <a href=
	 * "http://docs.sencha.com/ext-js/4-2/#!/api/Ext.data.proxy.Server-cfg-idParam"
	 * >idParam</a> property on the proxy.
	 * <p>
	 * If not present default value of 'id' is used.
	 */
	String idProperty() default "id";

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
	 * Default value is false
	 */
	boolean paging() default false;

	/**
	 * Specifies the read method. This is a ExtDirect reference in the form
	 * action.methodName. See <a href=
	 * "http://docs.sencha.com/ext-js/4-2/#!/api/Ext.data.proxy.Direct-cfg-api"
	 * >Ext.data.proxy.Direct#api</a>.
	 * <p>
	 * If only the readMethod is specified generator will write property <a
	 * href=
	 * "http://docs.sencha.com/ext-js/4-2/#!/api/Ext.data.proxy.Direct-cfg-directFn"
	 * >directFn</a> instead.
	 */
	String readMethod() default "";

	/**
	 * Specifies the create method. This is a ExtDirect reference in the form
	 * action.methodName. See <a href=
	 * "http://docs.sencha.com/ext-js/4-2/#!/api/Ext.data.proxy.Direct-cfg-api"
	 * >Ext.data.proxy.Direct#api</a>.
	 */
	String createMethod() default "";

	/**
	 * Specifies the update method. This is a ExtDirect reference in the form
	 * action.methodName. See <a href=
	 * "http://docs.sencha.com/ext-js/4-2/#!/api/Ext.data.proxy.Direct-cfg-api"
	 * >Ext.data.proxy.Direct#api</a>.
	 */
	String updateMethod() default "";

	/**
	 * Specifies the destroy method. This is a ExtDirect reference in the form
	 * action.methodName. See <a href=
	 * "http://docs.sencha.com/ext-js/4-2/#!/api/Ext.data.proxy.Direct-cfg-api"
	 * >Ext.data.proxy.Direct#api</a>.
	 */
	String destroyMethod() default "";

}
