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

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.ref.SoftReference;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
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

import org.springframework.util.Assert;
import org.springframework.util.DigestUtils;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.ReflectionUtils.FieldCallback;
import org.springframework.util.StringUtils;

import ch.ralscha.extdirectspring.generator.association.AbstractAssociation;
import ch.ralscha.extdirectspring.generator.validation.AbstractValidation;
import ch.ralscha.extdirectspring.util.ExtDirectSpringUtil;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Generator for creating ExtJS and Touch Model objects (JS code) based on a
 * provided class or {@link ModelBean}.
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
	 *            create.
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
	 *            create
	 * @param debug if true the generator creates the output in pretty format,
	 *            false the output is compressed
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
	 *            create
	 * @param includeValidation specifies if any validation configurations
	 *            should be added to the model code
	 * @param debug if true the generator creates the output in pretty format,
	 *            false the output is compressed
	 * @throws IOException
	 */
	public static void writeModel(HttpServletRequest request, HttpServletResponse response, Class<?> clazz,
			OutputFormat format, IncludeValidation includeValidation, boolean debug) throws IOException {
		ModelBean model = createModel(clazz, includeValidation);
		writeModel(request, response, model, format, debug);
	}

	public static void writeModel(HttpServletRequest request, HttpServletResponse response, Class<?> clazz,
			OutputConfig outputConfig) throws IOException {
		ModelBean model = createModel(clazz, outputConfig);
		writeModel(request, response, model, outputConfig);
	}

	/**
	 * Creates a model object (JS code) based on the provided {@link ModelBean}
	 * and writes it into the response. Creates compressed JS code.
	 * 
	 * @param request the http servlet request
	 * @param response the http servlet response
	 * @param model {@link ModelBean} describing the model to be generated
	 * @param format specifies which code (ExtJS or Touch) the generator should
	 *            create.
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
	 *            create.
	 * @param debug if true the generator creates the output in pretty format,
	 *            false the output is compressed
	 * @throws IOException
	 */
	public static void writeModel(HttpServletRequest request, HttpServletResponse response, ModelBean model,
			OutputFormat format, boolean debug) throws IOException {
		OutputConfig outputConfig = new OutputConfig();
		outputConfig.setDebug(debug);
		outputConfig.setOutputFormat(format);
		writeModel(request, response, model, outputConfig);
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
	 * to create the JS code. Models are being cached. A second call with the
	 * same parameters will return the model from the cache.
	 * 
	 * @param clazz the model will be created based on this class.
	 * @param includeValidation specifies what validation configuration should
	 *            be added
	 * @return a instance of {@link ModelBean} that describes the provided class
	 *         and can be used for Javascript generation.
	 */
	public static ModelBean createModel(Class<?> clazz, IncludeValidation includeValidation) {
		OutputConfig outputConfig = new OutputConfig();
		outputConfig.setIncludeValidation(includeValidation);
		return createModel(clazz, outputConfig);
	}

	/**
	 * Instrospects the provided class, creates a model object (JS code) and
	 * returns it. This method does not add any validation configuration.
	 * 
	 * @param clazz class that the generator should introspect
	 * @param format specifies which code (ExtJS or Touch) the generator should
	 *            create
	 * @param debug if true the generator creates the output in pretty format,
	 *            false the output is compressed
	 * @return the generated model object (JS code)
	 */
	public static String generateJavascript(Class<?> clazz, OutputFormat format, boolean debug) {
		ModelBean model = createModel(clazz, IncludeValidation.NONE);
		return generateJavascript(model, format, debug);
	}

	public static String generateJavascript(Class<?> clazz, OutputConfig outputConfig) {
		ModelBean model = createModel(clazz, outputConfig);
		return generateJavascript(model, outputConfig);
	}

	/**
	 * Instrospects the provided class, creates a model object (JS code) and
	 * returns it.
	 * 
	 * @param clazz class that the generator should introspect
	 * @param format specifies which code (ExtJS or Touch) the generator should
	 *            create
	 * @param includeValidation specifies what validation configuration should
	 *            be added to the mode code
	 * @param debug if true the generator creates the output in pretty format,
	 *            false the output is compressed
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
	 * format. The generated code is cached unless debug is true. A second call
	 * to this method with the same model name and format will return the code
	 * from the cache.
	 * 
	 * @param model generate code based on this {@link ModelBean}
	 * @param format specifies which code (ExtJS or Touch) the generator should
	 *            create
	 * @param debug if true the generator creates the output in pretty format,
	 *            false the output is compressed
	 * @return the generated model object (JS code)
	 */
	public static String generateJavascript(ModelBean model, OutputFormat format, boolean debug) {
		OutputConfig outputConfig = new OutputConfig();
		outputConfig.setOutputFormat(format);
		outputConfig.setDebug(debug);
		return generateJavascript(model, outputConfig);
	}

	public static void writeModel(HttpServletRequest request, HttpServletResponse response, ModelBean model,
			OutputConfig outputConfig) throws IOException {

		byte[] data = generateJavascript(model, outputConfig).getBytes(ExtDirectSpringUtil.UTF8_CHARSET);
		String ifNoneMatch = request.getHeader("If-None-Match");
		String etag = "\"0" + DigestUtils.md5DigestAsHex(data) + "\"";

		if (etag.equals(ifNoneMatch)) {
			response.setStatus(HttpServletResponse.SC_NOT_MODIFIED);
			return;
		}

		response.setContentType("application/javascript");
		response.setCharacterEncoding(ExtDirectSpringUtil.UTF8_CHARSET.name());
		response.setContentLength(data.length);

		response.setHeader("ETag", etag);

		@SuppressWarnings("resource")
		ServletOutputStream out = response.getOutputStream();
		out.write(data);
		out.flush();

	}

	public static ModelBean createModel(Class<?> clazz, final OutputConfig outputConfig) {

		Assert.notNull(clazz, "clazz must not be null");
		Assert.notNull(outputConfig.getIncludeValidation(), "includeValidation must not be null");

		ModelCacheKey key = new ModelCacheKey(clazz.getName(), outputConfig.getIncludeValidation());
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
		final List<AbstractAssociation> associations = new ArrayList<AbstractAssociation>();

		ReflectionUtils.doWithFields(clazz, new FieldCallback() {
			private final Set<String> fields = new HashSet<String>();

			@Override
			public void doWith(Field field) throws IllegalArgumentException, IllegalAccessException {
				if (!fields.contains(field.getName())
						&& (field.getAnnotation(ModelField.class) != null
								|| field.getAnnotation(ModelAssociation.class) != null || ((Modifier.isPublic(field
								.getModifiers()) || hasReadMethod.contains(field.getName())) && field
								.getAnnotation(JsonIgnore.class) == null))) {

					// ignore superclass declarations of fields already found in
					// a subclass
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

						String defaultValue = modelFieldAnnotation.defaultValue();
						if (StringUtils.hasText(defaultValue)) {
							if (ModelField.DEFAULTVALUE_UNDEFINED.equals(defaultValue)) {
								modelFieldBean.setDefaultValue(ModelField.DEFAULTVALUE_UNDEFINED);
							} else {
								if (type == ModelType.BOOLEAN) {
									modelFieldBean.setDefaultValue(Boolean.parseBoolean(defaultValue));
								} else if (type == ModelType.INTEGER) {
									modelFieldBean.setDefaultValue(Long.valueOf(defaultValue));
								} else if (type == ModelType.FLOAT) {
									modelFieldBean.setDefaultValue(Double.valueOf(defaultValue));
								} else {
									modelFieldBean.setDefaultValue("\"" + defaultValue + "\"");
								}
							}
						}

						if (modelFieldAnnotation.useNull()
								&& (type == ModelType.INTEGER || type == ModelType.FLOAT || type == ModelType.STRING || type == ModelType.BOOLEAN)) {
							modelFieldBean.setUseNull(true);
						}

						if (StringUtils.hasText(modelFieldAnnotation.mapping())) {
							modelFieldBean.setMapping(modelFieldAnnotation.mapping());
						}

						if (!modelFieldAnnotation.persist()) {
							modelFieldBean.setPersist(modelFieldAnnotation.persist());
						}

						if (StringUtils.hasText(modelFieldAnnotation.convert())) {
							modelFieldBean.setConvert(modelFieldAnnotation.convert());
						}

						modelFields.add(modelFieldBean);
					} else {
						if (modelType != null) {
							modelFieldBean = new ModelFieldBean(field.getName(), modelType);
							modelFields.add(modelFieldBean);
						}
					}

					ModelAssociation modelAssociationAnnotation = field.getAnnotation(ModelAssociation.class);
					if (modelAssociationAnnotation != null) {
						associations.add(AbstractAssociation
								.createAssociation(modelAssociationAnnotation, model, field));
					}

					if (modelFieldBean != null && outputConfig.getIncludeValidation() != IncludeValidation.NONE) {
						Annotation[] fieldAnnotations = field.getAnnotations();

						for (Annotation fieldAnnotation : fieldAnnotations) {
							AbstractValidation.addValidationToModel(model, modelFieldBean, fieldAnnotation,
									outputConfig.getIncludeValidation());
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

	public static String generateJavascript(ModelBean model, OutputConfig config) {

		if (!config.isDebug()) {
			JsCacheKey key = new JsCacheKey(model, config);

			SoftReference<String> jsReference = jsCache.get(key);
			if (jsReference != null && jsReference.get() != null) {
				return jsReference.get();
			}
		}

		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(JsonGenerator.Feature.QUOTE_FIELD_NAMES, false);
		Map<String, Object> modelObject = new LinkedHashMap<String, Object>();
		modelObject.put("extend", "Ext.data.Model");

		if (!model.getAssociations().isEmpty()) {
			Set<String> requiredClasses = new HashSet<String>();
			for (AbstractAssociation association : model.getAssociations()) {
				requiredClasses.add(association.getModel());
			}
			modelObject.put("requires", requiredClasses);
		}

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

		if (config.isSurroundApiWithQuotes()) {
			ProxyObjectWithQuotes proxyObject = new ProxyObjectWithQuotes(model.getIdProperty(), model.getReadMethod(),
					model.getCreateMethod(), model.getUpdateMethod(), model.getDestroyMethod(), model.isPaging());
			if (proxyObject.hasMethods()) {
				configObject.put("proxy", proxyObject);
			}
		} else {
			ProxyObjectWithoutQuotes proxyObject = new ProxyObjectWithoutQuotes(model.getIdProperty(),
					model.getReadMethod(), model.getCreateMethod(), model.getUpdateMethod(), model.getDestroyMethod(),
					model.isPaging());
			if (proxyObject.hasMethods()) {
				configObject.put("proxy", proxyObject);
			}
		}

		if (config.getOutputFormat() == OutputFormat.EXTJS4) {
			modelObject.putAll(configObject);
		} else {
			modelObject.put("config", configObject);
		}

		StringBuilder sb = new StringBuilder();
		sb.append("Ext.define(\"").append(model.getName()).append("\",");
		if (config.isDebug()) {
			sb.append("\n");
		}

		String configObjectString;
		try {
			if (config.isDebug()) {
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

		sb.append(configObjectString);
		sb.append(");");

		String result = sb.toString();

		if (config.isUseSingleQuotes()) {
			result = result.replace('"', '\'');
		}

		if (!config.isDebug()) {
			jsCache.put(new JsCacheKey(model, config), new SoftReference<String>(result));
		}
		return result;
	}

	/**
	 * Clears the model and Javascript code caches
	 */
	public static void clearCaches() {
		modelCache.clear();
		jsCache.clear();
	}

}
