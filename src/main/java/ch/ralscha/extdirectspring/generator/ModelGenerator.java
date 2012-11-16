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

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.ref.SoftReference;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.LogFactory;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.util.Assert;
import org.springframework.util.DigestUtils;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.ReflectionUtils.FieldCallback;
import org.springframework.util.StringUtils;

import ch.ralscha.extdirectspring.controller.RouterController;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Generator for creating ExtJS and Touch Model objects (JS code) based on a
 * provided class or {@link ModelBean}.
 * 
 * @author Ralph Schaer
 */
public abstract class ModelGenerator {

	private static final Map<JsCacheKey, SoftReference<String>> jsCache = new ConcurrentHashMap<JsCacheKey, SoftReference<String>>();

	private static final Map<ModelCacheKey, SoftReference<ModelBean>> modelCache = new ConcurrentHashMap<ModelCacheKey, SoftReference<ModelBean>>();

	/**
	 * Instrospects the provided class, creates a model object (JS code) and
	 * writes it into the response. Creates compressed JS code. Method ignores
	 * any validation annotations.
	 * 
	 * @param request the http servlet request
	 * @param response the http servlet response
	 * @param clazz class that the generator should introspect
	 * @param format specifies which code (ExtJS or Touch) the generator should
	 *        create.
	 * @throws IOException
	 * 
	 * @see #writeModel(HttpServletRequest, HttpServletResponse, Class,
	 *      OutputFormat, boolean)
	 */
	public static void writeModel(HttpServletRequest request, HttpServletResponse response, Class<?> clazz,
			OutputFormat format) throws IOException {
		writeModel(request, response, clazz, format, IncludeValidation.NONE, false);
	}

	/**
	 * Instrospects the provided class, creates a model object (JS code) and
	 * writes it into the response. Method ignores any validation annotations.
	 * 
	 * @param request the http servlet request
	 * @param response the http servlet response
	 * @param clazz class that the generator should introspect
	 * @param format specifies which code (ExtJS or Touch) the generator should
	 *        create
	 * @param debug if true the generator creates the output in pretty format,
	 *        false the output is compressed
	 * @throws IOException
	 */
	public static void writeModel(HttpServletRequest request, HttpServletResponse response, Class<?> clazz,
			OutputFormat format, boolean debug) throws IOException {
		writeModel(request, response, clazz, format, IncludeValidation.NONE, debug);
	}

	/**
	 * Instrospects the provided class, creates a model object (JS code) and
	 * writes it into the response.
	 * 
	 * @param request the http servlet request
	 * @param response the http servlet response
	 * @param clazz class that the generator should introspect
	 * @param format specifies which code (ExtJS or Touch) the generator should
	 *        create
	 * @param includeValidation specifies if any validation configurations
	 *        should be added to the model code
	 * @param debug if true the generator creates the output in pretty format,
	 *        false the output is compressed
	 * @throws IOException
	 */
	public static void writeModel(HttpServletRequest request, HttpServletResponse response, Class<?> clazz,
			OutputFormat format, IncludeValidation includeValidation, boolean debug) throws IOException {
		ModelBean model = createModel(clazz, includeValidation);
		writeModel(request, response, model, format, debug);
	}

	/**
	 * Creates a model object (JS code) based on the provided {@link ModelBean}
	 * and writes it into the response. Creates compressed JS code.
	 * 
	 * @param request the http servlet request
	 * @param response the http servlet response
	 * @param model {@link ModelBean} describing the model to be generated
	 * @param format specifies which code (ExtJS or Touch) the generator should
	 *        create.
	 * @throws IOException
	 */
	public static void writeModel(HttpServletRequest request, HttpServletResponse response, ModelBean model,
			OutputFormat format) throws IOException {
		writeModel(request, response, model, format, false);
	}

	/**
	 * Creates a model object (JS code) based on the provided ModelBean and
	 * writes it into the response.
	 * 
	 * @param request the http servlet request
	 * @param response the http servlet response
	 * @param model {@link ModelBean} describing the model to be generated
	 * @param format specifies which code (ExtJS or Touch) the generator should
	 *        create.
	 * @param debug if true the generator creates the output in pretty format,
	 *        false the output is compressed
	 * @throws IOException
	 */
	public static void writeModel(HttpServletRequest request, HttpServletResponse response, ModelBean model,
			OutputFormat format, boolean debug) throws IOException {

		byte[] data = generateJavascript(model, format, debug).getBytes(RouterController.UTF8_CHARSET);
		String ifNoneMatch = request.getHeader("If-None-Match");
		String etag = "\"0" + DigestUtils.md5DigestAsHex(data) + "\"";

		if (etag.equals(ifNoneMatch)) {
			response.setStatus(HttpServletResponse.SC_NOT_MODIFIED);
			return;
		}

		response.setContentType("application/javascript");
		response.setCharacterEncoding(RouterController.UTF8_CHARSET.name());
		response.setContentLength(data.length);

		response.setHeader("ETag", etag);

		@SuppressWarnings("resource")
		ServletOutputStream out = response.getOutputStream();
		out.write(data);
		out.flush();

	}

	/**
	 * Instrospects the provided class and creates a {@link ModelBean} instance.
	 * A program could customize this and call
	 * {@link #generateJavascript(ModelBean, OutputFormat, boolean)} or
	 * {@link #writeModel(HttpServletRequest, HttpServletResponse, ModelBean, OutputFormat)}
	 * to create the JS code. Calling this method does not add any validation
	 * configuration.
	 * 
	 * @param clazz the model will be created based on this class.
	 * @return a instance of {@link ModelBean} that describes the provided class
	 *         and can be used for Javascript generation.
	 */
	public static ModelBean createModel(Class<?> clazz) {
		return createModel(clazz, IncludeValidation.NONE);
	}

	/**
	 * Instrospects the provided class and creates a {@link ModelBean} instance.
	 * A program could customize this and call
	 * {@link #generateJavascript(ModelBean, OutputFormat, boolean)} or
	 * {@link #writeModel(HttpServletRequest, HttpServletResponse, ModelBean, OutputFormat)}
	 * to create the JS code.
	 * 
	 * @param clazz the model will be created based on this class.
	 * @param includeValidation specifies what validation configuration should
	 *        be added
	 * @return a instance of {@link ModelBean} that describes the provided class
	 *         and can be used for Javascript generation.
	 */
	public static ModelBean createModel(Class<?> clazz, final IncludeValidation includeValidation) {

		Assert.notNull(clazz, "clazz must not be null");
		Assert.notNull(includeValidation, "includeValidation must not be null");

		ModelCacheKey key = new ModelCacheKey(clazz.getName(), includeValidation);
		SoftReference<ModelBean> modelReference = modelCache.get(key);
		if (modelReference != null && modelReference.get() != null) {
			return modelReference.get();
		}

		Model modelAnnotation = clazz.getAnnotation(Model.class);

		final ModelBean model = new ModelBean();

		if (modelAnnotation != null && StringUtils.hasText(modelAnnotation.value())) {
			model.setName(modelAnnotation.value());
		} else {
			model.setName(clazz.getName());
		}

		if (modelAnnotation != null) {
			model.setIdProperty(modelAnnotation.idProperty());
			model.setPaging(modelAnnotation.paging());

			if (StringUtils.hasText(modelAnnotation.createMethod())) {
				model.setCreateMethod(modelAnnotation.createMethod());
			}

			if (StringUtils.hasText(modelAnnotation.readMethod())) {
				model.setReadMethod(modelAnnotation.readMethod());
			}

			if (StringUtils.hasText(modelAnnotation.updateMethod())) {
				model.setUpdateMethod(modelAnnotation.updateMethod());
			}

			if (StringUtils.hasText(modelAnnotation.destroyMethod())) {
				model.setDestroyMethod(modelAnnotation.destroyMethod());
			}
		}

		final Set<String> hasReadMethod = new HashSet<String>();

		BeanInfo bi;
		try {
			bi = Introspector.getBeanInfo(clazz);
		} catch (IntrospectionException e) {
			throw new RuntimeException(e);
		}

		for (PropertyDescriptor pd : bi.getPropertyDescriptors()) {
			if (pd.getReadMethod() != null && pd.getReadMethod().getAnnotation(JsonIgnore.class) == null) {
				hasReadMethod.add(pd.getName());
			}
		}

		final List<ModelFieldBean> modelFields = new ArrayList<ModelFieldBean>();
		final List<ModelAssociationBean> associations = new ArrayList<ModelAssociationBean>();

		ReflectionUtils.doWithFields(clazz, new FieldCallback() {
			private final Set<String> fields = new HashSet<String>();

			@Override
			public void doWith(Field field) throws IllegalArgumentException, IllegalAccessException {
				if ((Modifier.isPublic(field.getModifiers()) || hasReadMethod.contains(field.getName()))
						&& field.getAnnotation(JsonIgnore.class) == null) {
					if (fields.contains(field.getName())) {
						// ignore superclass declarations of fields already
						// found in a subclass
					} else {
						fields.add(field.getName());

						Class<?> javaType = field.getType();

						ModelType modelType = null;
						for (ModelType mt : ModelType.values()) {
							if (mt.supports(javaType)) {
								modelType = mt;
								break;
							}
						}

						ModelFieldBean modelFieldBean = null;

						ModelField modelFieldAnnotation = field.getAnnotation(ModelField.class);
						if (modelFieldAnnotation != null) {

							String name;
							if (StringUtils.hasText(modelFieldAnnotation.value())) {
								name = modelFieldAnnotation.value();
							} else {
								name = field.getName();
							}

							ModelType type;
							if (modelFieldAnnotation.type() != ModelType.AUTO) {
								type = modelFieldAnnotation.type();
							} else {
								if (modelType != null) {
									type = modelType;
								} else {
									type = ModelType.AUTO;
								}
							}

							modelFieldBean = new ModelFieldBean(name, type);

							if (StringUtils.hasText(modelFieldAnnotation.dateFormat()) && type == ModelType.DATE) {
								modelFieldBean.setDateFormat(modelFieldAnnotation.dateFormat());
							}

							if (StringUtils.hasText(modelFieldAnnotation.defaultValue())) {
								if (type == ModelType.BOOLEAN) {
									modelFieldBean.setDefaultValue(Boolean.parseBoolean(modelFieldAnnotation
											.defaultValue()));
								} else if (type == ModelType.INTEGER) {
									modelFieldBean.setDefaultValue(Long.valueOf(modelFieldAnnotation.defaultValue()));
								} else if (type == ModelType.FLOAT) {
									modelFieldBean.setDefaultValue(Double.valueOf(modelFieldAnnotation.defaultValue()));
								} else {
									modelFieldBean.setDefaultValue(modelFieldAnnotation.defaultValue());
								}
							}

							if (modelFieldAnnotation.useNull()
									&& (type == ModelType.INTEGER || type == ModelType.FLOAT
											|| type == ModelType.STRING || type == ModelType.BOOLEAN)) {
								modelFieldBean.setUseNull(true);
							}

							modelFields.add(modelFieldBean);
						} else {
							if (modelType != null) {
								modelFieldBean = new ModelFieldBean(field.getName(), modelType);
								modelFields.add(modelFieldBean);
							}
						}

						ModelAssociation modelAssociation = field.getAnnotation(ModelAssociation.class);
						if (modelAssociation != null) {

							ModelAssociationType type = modelAssociation.value();

							Class<?> associationClass = modelAssociation.model();
							if (associationClass == Object.class) {
								associationClass = field.getType();
							}

							ModelAssociationBean modelAssociationBean = new ModelAssociationBean(type, associationClass);

							if (StringUtils.hasText(modelAssociation.foreignKey())) {
								modelAssociationBean.setForeignKey(modelAssociation.foreignKey());
							}

							if (StringUtils.hasText(modelAssociation.primaryKey())) {
								modelAssociationBean.setPrimaryKey(modelAssociation.primaryKey());
							}

							if (type == ModelAssociationType.HAS_MANY) {

								if (StringUtils.hasText(modelAssociation.setterName())) {
									LogFactory.getLog(ModelGenerator.class).warn(
											getWarningText(field, modelAssociation.value().getJsName(), "setterName"));
								}

								if (StringUtils.hasText(modelAssociation.getterName())) {
									LogFactory.getLog(ModelGenerator.class).warn(
											getWarningText(field, modelAssociation.value().getJsName(), "getterName"));
								}

								if (modelAssociation.autoLoad()) {
									modelAssociationBean.setAutoLoad(true);
								}
								if (StringUtils.hasText(modelAssociation.name())) {
									modelAssociationBean.setName(modelAssociation.name());
								} else {
									modelAssociationBean.setName(field.getName());
								}

							} else {

								if (StringUtils.hasText(modelAssociation.setterName())) {
									modelAssociationBean.setSetterName(modelAssociation.setterName());
								} else {
									modelAssociationBean.setSetterName("set" + StringUtils.capitalize(field.getName()));
								}

								if (StringUtils.hasText(modelAssociation.getterName())) {
									modelAssociationBean.setGetterName(modelAssociation.getterName());
								} else {
									modelAssociationBean.setGetterName("get" + StringUtils.capitalize(field.getName()));
								}

								if (modelAssociation.autoLoad()) {
									LogFactory.getLog(ModelGenerator.class).warn(
											getWarningText(field, modelAssociation.value().getJsName(), "autoLoad"));
								}
								if (StringUtils.hasText(modelAssociation.name())) {
									LogFactory.getLog(ModelGenerator.class).warn(
											getWarningText(field, modelAssociation.value().getJsName(), "name"));
								}
							}

							associations.add(modelAssociationBean);
						}

						if (modelFieldBean != null && includeValidation != IncludeValidation.NONE) {
							Annotation[] fieldAnnotations = field.getAnnotations();

							for (Annotation fieldAnnotation : fieldAnnotations) {
								processValidationAnnotations(model, modelFieldBean, fieldAnnotation, includeValidation);
							}
						}
					}
				}
			}

		});

		model.addFields(modelFields);
		model.addAssociations(associations);

		modelCache.put(key, new SoftReference<ModelBean>(model));
		return model;
	}

	/**
	 * Instrospects the provided class, creates a model object (JS code) and
	 * returns it. This method does not add any validation configuration.
	 * 
	 * @param clazz class that the generator should introspect
	 * @param format specifies which code (ExtJS or Touch) the generator should
	 *        create
	 * @param debug if true the generator creates the output in pretty format,
	 *        false the output is compressed
	 * @return the generated model object (JS code)
	 */
	public static String generateJavascript(Class<?> clazz, OutputFormat format, boolean debug) {
		ModelBean model = createModel(clazz, IncludeValidation.NONE);
		return generateJavascript(model, format, debug);
	}

	/**
	 * Instrospects the provided class, creates a model object (JS code) and
	 * returns it.
	 * 
	 * @param clazz class that the generator should introspect
	 * @param format specifies which code (ExtJS or Touch) the generator should
	 *        create
	 * @param includeValidation specifies what validation configuration should
	 *        be added to the mode code
	 * @param debug if true the generator creates the output in pretty format,
	 *        false the output is compressed
	 * @return the generated model object (JS code)
	 */
	public static String generateJavascript(Class<?> clazz, OutputFormat format, IncludeValidation includeValidation,
			boolean debug) {
		ModelBean model = createModel(clazz, includeValidation);
		return generateJavascript(model, format, debug);
	}

	/**
	 * Creates JS code based on the provided {@link ModelBean} in the specified
	 * {@link OutputFormat}. Code can be generated in pretty or compressed
	 * format.
	 * 
	 * @param model generate code based on this {@link ModelBean}
	 * @param format specifies which code (ExtJS or Touch) the generator should
	 *        create
	 * @param debug if true the generator creates the output in pretty format,
	 *        false the output is compressed
	 * @return the generated model object (JS code)
	 */
	public static String generateJavascript(ModelBean model, OutputFormat format, boolean debug) {

		JsCacheKey key = new JsCacheKey(model, format, debug);

		SoftReference<String> jsReference = jsCache.get(key);
		if (jsReference != null && jsReference.get() != null) {
			return jsReference.get();
		}

		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(JsonGenerator.Feature.QUOTE_FIELD_NAMES, false);
		Map<String, Object> modelObject = new LinkedHashMap<String, Object>();
		modelObject.put("extend", "Ext.data.Model");

		Map<String, Object> configObject = new LinkedHashMap<String, Object>();

		if (StringUtils.hasText(model.getIdProperty()) && !model.getIdProperty().equals("id")) {
			configObject.put("idProperty", model.getIdProperty());
		}

		configObject.put("fields", model.getFields().values());

		if (!model.getAssociations().isEmpty()) {
			configObject.put("associations", model.getAssociations());
		}

		if (!model.getValidations().isEmpty()) {
			configObject.put("validations", model.getValidations());
		}

		Map<String, Object> proxyObject = new LinkedHashMap<String, Object>();
		proxyObject.put("type", "direct");
		
		if (StringUtils.hasText(model.getIdProperty()) && !model.getIdProperty().equals("id")) {
			proxyObject.put("idParam", model.getIdProperty());
		}		

		Map<String, Object> apiObject = new LinkedHashMap<String, Object>();

		if (StringUtils.hasText(model.getReadMethod()) && !StringUtils.hasText(model.getCreateMethod())
				&& !StringUtils.hasText(model.getUpdateMethod()) && !StringUtils.hasText(model.getDestroyMethod())) {
			proxyObject.put("directFn", model.getReadMethod());

		} else {

			if (StringUtils.hasText(model.getReadMethod())) {
				apiObject.put("read", model.getReadMethod());
			}

			if (StringUtils.hasText(model.getCreateMethod())) {
				apiObject.put("create", model.getCreateMethod());
			}

			if (StringUtils.hasText(model.getUpdateMethod())) {
				apiObject.put("update", model.getUpdateMethod());
			}

			if (StringUtils.hasText(model.getDestroyMethod())) {
				apiObject.put("destroy", model.getDestroyMethod());
			}

			if (!apiObject.isEmpty()) {
				proxyObject.put("api", apiObject);
			}
		}

		if (model.isPaging()) {
			Map<String, Object> readerObject = new LinkedHashMap<String, Object>();
			readerObject.put("root", "records");
			proxyObject.put("reader", readerObject);
		}

		if (!apiObject.isEmpty() || proxyObject.containsKey("directFn")) {
			configObject.put("proxy", proxyObject);
		}

		if (format == OutputFormat.EXTJS4) {
			modelObject.putAll(configObject);
		} else {
			modelObject.put("config", configObject);
		}

		StringBuilder sb = new StringBuilder();
		sb.append("Ext.define('").append(model.getName()).append("',");
		if (debug) {
			sb.append("\n");
		}

		String configObjectString;
		try {
			if (debug) {
				configObjectString = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(modelObject);
			} else {
				configObjectString = mapper.writeValueAsString(modelObject);
			}

		} catch (JsonGenerationException e) {
			throw new RuntimeException(e);
		} catch (JsonMappingException e) {
			throw new RuntimeException(e);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

		configObjectString = configObjectString.replace("\"", "'");
		configObjectString = configObjectString.replaceAll("directFn( ?: ?)'([^']+)'", "directFn$1$2");
		configObjectString = configObjectString.replaceAll("read( ?: ?)'([^']+)'", "read$1$2");
		configObjectString = configObjectString.replaceAll("create( ?: ?)'([^']+)'", "create$1$2");
		configObjectString = configObjectString.replaceAll("update( ?: ?)'([^']+)'", "update$1$2");
		configObjectString = configObjectString.replaceAll("destroy( ?: ?)'([^']+)'", "destroy$1$2");

		configObjectString = configObjectString.replaceAll("matcher( ?: ?)'(/[^']+/)'", "matcher$1$2");
		configObjectString = configObjectString.replace("\\\\", "\\");

		sb.append(configObjectString);
		sb.append(");");

		String result = sb.toString();
		jsCache.put(key, new SoftReference<String>(result));
		return result;
	}

	private static String getWarningText(Field field, String type, String propertyName) {
		String warning = "Field ";
		warning += field.getDeclaringClass().getName();
		warning += ".";
		warning += field.getName();
		return warning + ": A '" + type + "' association does not support property '" + propertyName
				+ "'. Property will be ignored.";
	}

	private static void processValidationAnnotations(ModelBean model, ModelFieldBean modelFieldBean,
			Annotation fieldAnnotation, IncludeValidation includeValidation) {
		String annotationClassName = fieldAnnotation.annotationType().getName();

		if (includeValidation == IncludeValidation.BUILTIN || includeValidation == IncludeValidation.ALL) {

			if (annotationClassName.equals("javax.validation.constraints.NotNull")
					|| (annotationClassName.equals("org.hibernate.validator.constraints.NotEmpty"))) {
				model.addValidation(new ModelFieldValidationBean("presence", modelFieldBean.getName()));
			} else if (annotationClassName.equals("javax.validation.constraints.Size")
					|| (annotationClassName.equals("org.hibernate.validator.constraints.Length"))) {
				ModelFieldValidationBean lengthValidation = new ModelFieldValidationBean("length",
						modelFieldBean.getName());

				Integer min = (Integer) AnnotationUtils.getValue(fieldAnnotation, "min");
				Integer max = (Integer) AnnotationUtils.getValue(fieldAnnotation, "max");
				if (min > 0) {
					lengthValidation.addOption("min", min);
				}
				if (max < Integer.MAX_VALUE) {
					lengthValidation.addOption("max", max);
				}

				model.addValidation(lengthValidation);
			} else if (annotationClassName.equals("javax.validation.constraints.Pattern")) {
				ModelFieldValidationBean formatConstraint = new ModelFieldValidationBean("format",
						modelFieldBean.getName());
				String regexp = (String) AnnotationUtils.getValue(fieldAnnotation, "regexp");
				formatConstraint.addOption("matcher", "/" + regexp + "/");
				model.addValidation(formatConstraint);
			} else if (annotationClassName.equals("org.hibernate.validator.constraints.Email")) {
				model.addValidation(new ModelFieldValidationBean("email", modelFieldBean.getName()));
			}
		}

		if (includeValidation == IncludeValidation.ALL) {

			if (annotationClassName.equals("javax.validation.constraints.DecimalMax")) {
				String value = (String) AnnotationUtils.getValue(fieldAnnotation);
				if (StringUtils.hasText(value)) {
					ModelFieldValidationBean rangeValidation = new ModelFieldValidationBean("range",
							modelFieldBean.getName());
					rangeValidation.addOption("max", new BigDecimal(value));
					model.addValidation(rangeValidation);
				}
			} else if (annotationClassName.equals("javax.validation.constraints.DecimalMin")) {
				String value = (String) AnnotationUtils.getValue(fieldAnnotation);
				if (StringUtils.hasText(value)) {
					ModelFieldValidationBean rangeValidation = new ModelFieldValidationBean("range",
							modelFieldBean.getName());
					rangeValidation.addOption("min", new BigDecimal(value));
					model.addValidation(rangeValidation);
				}
			} else if (annotationClassName.equals("javax.validation.constraints.Digits")) {
				ModelFieldValidationBean digitValidation = new ModelFieldValidationBean("digits",
						modelFieldBean.getName());

				Integer integer = (Integer) AnnotationUtils.getValue(fieldAnnotation, "integer");
				Integer fraction = (Integer) AnnotationUtils.getValue(fieldAnnotation, "fraction");

				if (integer > 0) {
					digitValidation.addOption("integer", integer);
				}

				if (fraction > 0) {
					digitValidation.addOption("fraction", fraction);
				}

				model.addValidation(digitValidation);
			} else if (annotationClassName.equals("javax.validation.constraints.Future")) {
				model.addValidation(new ModelFieldValidationBean("future", modelFieldBean.getName()));
			} else if (annotationClassName.equals("javax.validation.constraints.Max")) {
				Long value = (Long) AnnotationUtils.getValue(fieldAnnotation);
				if (value != null && value > 0) {
					ModelFieldValidationBean rangeValidation = new ModelFieldValidationBean("range",
							modelFieldBean.getName());
					rangeValidation.addOption("max", value);
					model.addValidation(rangeValidation);
				}
			} else if (annotationClassName.equals("javax.validation.constraints.Min")) {
				Long value = (Long) AnnotationUtils.getValue(fieldAnnotation);
				if (value != null && value > 0) {
					ModelFieldValidationBean rangeValidation = new ModelFieldValidationBean("range",
							modelFieldBean.getName());
					rangeValidation.addOption("min", value);
					model.addValidation(rangeValidation);
				}

			} else if (annotationClassName.equals("javax.validation.constraints.Past")) {
				model.addValidation(new ModelFieldValidationBean("past", modelFieldBean.getName()));

			} else if (annotationClassName.equals("org.hibernate.validator.constraints.CreditCardNumber")) {
				model.addValidation(new ModelFieldValidationBean("creditCardNumber", modelFieldBean.getName()));

			} else if (annotationClassName.equals("org.hibernate.validator.constraints.NotBlank")) {
				model.addValidation(new ModelFieldValidationBean("notBlank", modelFieldBean.getName()));

			} else if (annotationClassName.equals("org.hibernate.validator.constraints.Range")) {
				ModelFieldValidationBean rangeValidation = new ModelFieldValidationBean("range",
						modelFieldBean.getName());

				Long min = (Long) AnnotationUtils.getValue(fieldAnnotation, "min");
				Long max = (Long) AnnotationUtils.getValue(fieldAnnotation, "max");
				if (min > 0) {
					rangeValidation.addOption("min", min);
				}
				if (max < Integer.MAX_VALUE) {
					rangeValidation.addOption("max", max);
				}

				model.addValidation(rangeValidation);
			}
		}
	}

	private static class ModelCacheKey {
		private final String className;

		private final IncludeValidation includeValidation;

		public ModelCacheKey(String className, IncludeValidation includeValidation) {
			this.className = className;
			this.includeValidation = includeValidation;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((className == null) ? 0 : className.hashCode());
			result = prime * result + ((includeValidation == null) ? 0 : includeValidation.hashCode());
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
			ModelCacheKey other = (ModelCacheKey) obj;
			if (className == null) {
				if (other.className != null) {
					return false;
				}
			} else if (!className.equals(other.className)) {
				return false;
			}
			if (includeValidation != other.includeValidation) {
				return false;
			}
			return true;
		}

	}

	private static class JsCacheKey {
		private final ModelBean modelBean;

		private final OutputFormat format;

		private final boolean debug;

		JsCacheKey(ModelBean modelBean, OutputFormat format, boolean debug) {
			this.modelBean = modelBean;
			this.format = format;
			this.debug = debug;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + (debug ? 1231 : 1237);
			result = prime * result + ((format == null) ? 0 : format.hashCode());
			result = prime * result + ((modelBean == null) ? 0 : modelBean.hashCode());
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
			JsCacheKey other = (JsCacheKey) obj;
			if (debug != other.debug) {
				return false;
			}
			if (format != other.format) {
				return false;
			}
			if (modelBean == null) {
				if (other.modelBean != null) {
					return false;
				}
			} else if (!modelBean.equals(other.modelBean)) {
				return false;
			}
			return true;
		}

	}
}
