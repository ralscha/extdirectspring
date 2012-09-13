package ch.ralscha.extdirectspring.generator;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.IOException;
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

import org.springframework.util.DigestUtils;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.ReflectionUtils.FieldCallback;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.support.RequestContextUtils;

import ch.ralscha.extdirectspring.controller.RouterController;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @author Ralph Schaer
 */
public abstract class ModelGenerator {

	private static final Map<JsCacheKey, SoftReference<String>> jsCache = new ConcurrentHashMap<JsCacheKey, SoftReference<String>>();

	private static final Map<String, SoftReference<ModelBean>> modelCache = new ConcurrentHashMap<String, SoftReference<ModelBean>>();

	public static void writeModel(HttpServletRequest request, HttpServletResponse response, Class<?> clazz,
			OutputFormat format) throws IOException {
		writeModel(request, response, clazz, format, false);
	}

	public static void writeModel(HttpServletRequest request, HttpServletResponse response, Class<?> clazz,
			OutputFormat format, boolean debug) throws IOException {
		ModelBean model = createModel(clazz);
		writeModel(request, response, model, format, debug);
	}

	public static void writeModel(HttpServletRequest request, HttpServletResponse response, ModelBean model,
			OutputFormat format) throws IOException {
		writeModel(request, response, model, format, false);
	}

	public static void writeModel(HttpServletRequest request, HttpServletResponse response, ModelBean model,
			OutputFormat format, boolean debug) throws IOException {

		RouterController routerController = RequestContextUtils.getWebApplicationContext(request).getBean(
				RouterController.class);

		byte[] data = generateJavascript(model, format, debug).getBytes(RouterController.UTF8_CHARSET);
		String ifNoneMatch = request.getHeader("If-None-Match");
		String etag = "\"0" + DigestUtils.md5DigestAsHex(data) + "\"";

		if (etag.equals(ifNoneMatch)) {
			response.setStatus(HttpServletResponse.SC_NOT_MODIFIED);
			return;
		}

		response.setContentType(routerController.getConfiguration().getJsContentType());
		response.setCharacterEncoding(RouterController.UTF8_CHARSET.name());
		response.setContentLength(data.length);

		response.setHeader("ETag", etag);

		@SuppressWarnings("resource")
		ServletOutputStream out = response.getOutputStream();
		out.write(data);
		out.flush();

	}

	public static String generateJavascript(Class<?> clazz, OutputFormat format, boolean debug) {
		ModelBean model = createModel(clazz);
		return generateJavascript(model, format, debug);
	}

	public static ModelBean createModel(Class<?> clazz) {

		SoftReference<ModelBean> modelReference = modelCache.get(clazz.getName());
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
			model.setPageing(modelAnnotation.paging());
			model.setCreateMethod(modelAnnotation.createMethod());
			model.setReadMethod(modelAnnotation.readMethod());
			model.setUpdateMethod(modelAnnotation.updateMethod());
			model.setDestroyMethod(modelAnnotation.destroyMethod());
		}

		final Set<String> hasReadMethod = new HashSet<String>();

		BeanInfo bi;
		try {
			bi = Introspector.getBeanInfo(clazz);
		} catch (IntrospectionException e) {
			throw new RuntimeException(e);
		}

		for (PropertyDescriptor pd : bi.getPropertyDescriptors()) {
			if (pd.getReadMethod() != null) {
				hasReadMethod.add(pd.getName());
			}
		}

		final List<ModelFieldBean> modelFields = new ArrayList<ModelFieldBean>();

		ReflectionUtils.doWithFields(clazz, new FieldCallback() {
			private final Set<String> fields = new HashSet<String>();

			@Override
			public void doWith(Field field) throws IllegalArgumentException, IllegalAccessException {
				if (Modifier.isPublic(field.getModifiers()) || hasReadMethod.contains(field.getName())) {
					if (fields.contains(field.getName())) {
						// ignore superclass declarations of fields already
						// found in a subclass
					} else {
						fields.add(field.getName());

						Class<?> javaType = field.getType();

						ModelType modelType = null;
						for (ModelType t : ModelType.values()) {
							if (t.supports(javaType)) {
								modelType = t;
								break;
							}
						}

						if (modelType != null) {

							ModelField mf = field.getAnnotation(ModelField.class);
							if (mf != null) {

								String name;
								if (StringUtils.hasText(mf.value())) {
									name = mf.value();
								} else {
									name = field.getName();
								}

								ModelType type;
								if (mf.type() != ModelType.AUTO) {
									type = mf.type();
								} else {
									type = modelType;
								}

								ModelFieldBean modelFieldBean = new ModelFieldBean(name, type);

								if (StringUtils.hasText(mf.dateFormat()) && type == ModelType.DATE) {
									modelFieldBean.setDateFormat(mf.dateFormat());
								}

								if (StringUtils.hasText(mf.defaultValue())) {
									if (type == ModelType.BOOLEAN) {
										modelFieldBean.setDefaultValue(Boolean.parseBoolean(mf.defaultValue()));
									} else if (type == ModelType.INTEGER) {
										modelFieldBean.setDefaultValue(Long.valueOf(mf.defaultValue()));
									} else if (type == ModelType.FLOAT) {
										modelFieldBean.setDefaultValue(Double.valueOf(mf.defaultValue()));
									} else {
										modelFieldBean.setDefaultValue(mf.defaultValue());
									}
								}

								if (mf.useNull()
										&& (type == ModelType.INTEGER || type == ModelType.FLOAT
												|| type == ModelType.STRING || type == ModelType.BOOLEAN)) {
									modelFieldBean.setUseNull(true);
								}

								modelFields.add(modelFieldBean);
							} else {
								modelFields.add(new ModelFieldBean(field.getName(), modelType));
							}

						}

					}
				}
			}
		});

		model.addFields(modelFields);

		modelCache.put(clazz.getName(), new SoftReference<ModelBean>(model));
		return model;
	}

	public static String generateJavascript(ModelBean model, OutputFormat format, boolean debug) {

		JsCacheKey key = new JsCacheKey(model, format);

		SoftReference<String> jsReference = jsCache.get(key);
		if (jsReference != null && jsReference.get() != null) {
			return jsReference.get();
		}

		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(JsonGenerator.Feature.QUOTE_FIELD_NAMES, false);
		Map<String, Object> modelObject = new LinkedHashMap<String, Object>();
		modelObject.put("extend", "Ext.data.Model");

		Map<String, Object> configObject = new LinkedHashMap<String, Object>();

		configObject.put("fields", model.getFields().values());

		if (StringUtils.hasText(model.getIdProperty()) && !model.getIdProperty().equals("id")) {
			configObject.put("idProperty", model.getIdProperty());
		}

		Map<String, Object> proxyObject = new LinkedHashMap<String, Object>();
		proxyObject.put("type", "direct");

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

		if (model.isPageing()) {
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
		configObjectString = configObjectString.replaceAll("directFn : '([^']+)'", "directFn : $1");
		configObjectString = configObjectString.replaceAll("read : '([^']+)'", "read : $1");
		configObjectString = configObjectString.replaceAll("create : '([^']+)'", "create : $1");
		configObjectString = configObjectString.replaceAll("update : '([^']+)'", "update : $1");
		configObjectString = configObjectString.replaceAll("destroy : '([^']+)'", "destroy : $1");
		sb.append(configObjectString);
		sb.append(");");

		String result = sb.toString();
		jsCache.put(key, new SoftReference<String>(result));
		return result;
	}

	private static class JsCacheKey {
		private final ModelBean modelBean;

		private final OutputFormat format;

		JsCacheKey(ModelBean modelBean, OutputFormat format) {
			this.modelBean = modelBean;
			this.format = format;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
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
