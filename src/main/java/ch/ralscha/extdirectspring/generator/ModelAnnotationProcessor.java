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

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedOptions;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;
import javax.tools.FileObject;
import javax.tools.StandardLocation;

import org.springframework.util.StringUtils;

import ch.ralscha.extdirectspring.util.ExtDirectSpringUtil;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@SupportedAnnotationTypes({ "ch.ralscha.extdirectspring.generator.Model" })
@SupportedOptions({ "outputFormat", "debug", "includeValidation" })
public class ModelAnnotationProcessor extends AbstractProcessor {

	private static final boolean ALLOW_OTHER_PROCESSORS_TO_CLAIM_ANNOTATIONS = false;

	private static final String OPTION_OUTPUTFORMAT = "outputFormat";

	private static final String OPTION_DEBUG = "debug";

	private static final String OPTION_INCLUDEVALIDATION = "includeValidation";

	private static final String OPTION_CREATEBASEANDSUBCLASS = "createBaseAndSubclass";

	private static final String OPTION_USESINGLEQUOTES = "useSingleQuotes";

	private static final String OPTION_SURROUNDAPIWITHQUOTES = "surroundApiWithQuotes";

	private final static ObjectMapper mapper = new ObjectMapper();

	static {
		mapper.configure(JsonGenerator.Feature.QUOTE_FIELD_NAMES, false);
	}

	@Override
	public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
		processingEnv.getMessager().printMessage(Diagnostic.Kind.NOTE, "Running " + getClass().getSimpleName());

		if (roundEnv.processingOver() || annotations.size() == 0) {
			return ALLOW_OTHER_PROCESSORS_TO_CLAIM_ANNOTATIONS;
		}

		if (roundEnv.getRootElements() == null || roundEnv.getRootElements().isEmpty()) {
			processingEnv.getMessager().printMessage(Diagnostic.Kind.NOTE, "No sources to process");
			return ALLOW_OTHER_PROCESSORS_TO_CLAIM_ANNOTATIONS;
		}

		OutputConfig outputConfig = new OutputConfig();

		outputConfig.setDebug(!"false".equals(processingEnv.getOptions().get(OPTION_DEBUG)));
		boolean createBaseAndSubclass = "true".equals(processingEnv.getOptions().get(OPTION_CREATEBASEANDSUBCLASS));

		String outputFormatString = processingEnv.getOptions().get(OPTION_OUTPUTFORMAT);
		outputConfig.setOutputFormat(OutputFormat.EXTJS4);
		if (StringUtils.hasText(outputFormatString)) {
			if (OutputFormat.TOUCH2.name().equalsIgnoreCase(outputFormatString)) {
				outputConfig.setOutputFormat(OutputFormat.TOUCH2);
			}
		}

		String includeValidationString = processingEnv.getOptions().get(OPTION_INCLUDEVALIDATION);
		outputConfig.setIncludeValidation(IncludeValidation.NONE);
		if (StringUtils.hasText(includeValidationString)) {
			if (IncludeValidation.ALL.name().equalsIgnoreCase(includeValidationString)) {
				outputConfig.setIncludeValidation(IncludeValidation.ALL);
			} else if (IncludeValidation.BUILTIN.name().equalsIgnoreCase(includeValidationString)) {
				outputConfig.setIncludeValidation(IncludeValidation.BUILTIN);
			}
		}

		outputConfig.setUseSingleQuotes("true".equals(processingEnv.getOptions().get(OPTION_USESINGLEQUOTES)));
		outputConfig.setSurroundApiWithQuotes("true".equals(processingEnv.getOptions()
				.get(OPTION_SURROUNDAPIWITHQUOTES)));

		for (TypeElement annotation : annotations) {
			Set<? extends Element> elements = roundEnv.getElementsAnnotatedWith(annotation);
			for (Element element : elements) {

				try {
					TypeElement typeElement = (TypeElement) element;

					String qualifiedName = typeElement.getQualifiedName().toString();
					Class<?> modelClass = Class.forName(qualifiedName);

					String code = ModelGenerator.generateJavascript(modelClass, outputConfig);

					Model modelAnnotation = element.getAnnotation(Model.class);
					String modelName = modelAnnotation.value();
					String fileName;
					String packageName = "";
					if (StringUtils.hasText(modelName)) {
						int lastDot = modelName.lastIndexOf('.');
						if (lastDot != -1) {
							fileName = modelName.substring(lastDot + 1);
							int firstDot = modelName.indexOf('.');
							if (firstDot < lastDot) {
								packageName = modelName.substring(firstDot + 1, lastDot);
							}
						} else {
							fileName = modelName;
						}
					} else {
						fileName = typeElement.getSimpleName().toString();
					}

					if (createBaseAndSubclass) {
						code = code.replaceFirst("(Ext.define\\(\"[^\"]+?)(\",)", "$1Base$2");
						FileObject fo = processingEnv.getFiler().createResource(StandardLocation.SOURCE_OUTPUT,
								packageName, fileName + "Base.js");
						OutputStream os = fo.openOutputStream();
						os.write(code.getBytes(ExtDirectSpringUtil.UTF8_CHARSET));
						os.close();

						try {
							fo = processingEnv.getFiler().getResource(StandardLocation.SOURCE_OUTPUT, packageName,
									fileName + ".js");
							InputStream is = fo.openInputStream();
							is.close();
						} catch (FileNotFoundException e) {
							String subClassCode = generateSubclassCode(modelClass, outputConfig.isDebug());
							fo = processingEnv.getFiler().createResource(StandardLocation.SOURCE_OUTPUT, packageName,
									fileName + ".js");
							os = fo.openOutputStream();
							os.write(subClassCode.getBytes(ExtDirectSpringUtil.UTF8_CHARSET));
							os.close();
						}

					} else {
						FileObject fo = processingEnv.getFiler().createResource(StandardLocation.SOURCE_OUTPUT,
								packageName, fileName + ".js");
						OutputStream os = fo.openOutputStream();
						os.write(code.getBytes(ExtDirectSpringUtil.UTF8_CHARSET));
						os.close();
					}

				} catch (ClassNotFoundException e) {
					processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, e.getMessage());
				} catch (IOException e) {
					processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, e.getMessage());
				}

			}
		}

		return ALLOW_OTHER_PROCESSORS_TO_CLAIM_ANNOTATIONS;
	}

	private static String generateSubclassCode(Class<?> clazz, boolean debug) {
		Model modelAnnotation = clazz.getAnnotation(Model.class);

		String name;
		if (modelAnnotation != null && StringUtils.hasText(modelAnnotation.value())) {
			name = modelAnnotation.value();
		} else {
			name = clazz.getName();
		}

		Map<String, Object> modelObject = new LinkedHashMap<String, Object>();
		modelObject.put("extend", name + "Base");

		StringBuilder sb = new StringBuilder(100);
		sb.append("Ext.define(\"").append(name).append("\",");
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

		sb.append(configObjectString);
		sb.append(");");

		return sb.toString();

	}
}
