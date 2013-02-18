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

import java.io.IOException;
import java.io.OutputStream;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;
import javax.tools.FileObject;
import javax.tools.StandardLocation;

import org.springframework.util.StringUtils;

import ch.ralscha.extdirectspring.util.ExtDirectSpringUtil;

@SupportedAnnotationTypes({ "ch.ralscha.extdirectspring.generator.Model" })
public class ModelAnnotationProcessor extends AbstractProcessor {

	private static final boolean ALLOW_OTHER_PROCESSORS_TO_CLAIM_ANNOTATIONS = false;

	private static final String OPTION_OUTPUTFORMAT = "outputFormat";

	private static final String OPTION_DEBUG = "debug";

	private static final String OPTION_INCLUDEVALIDATION = "includeValidation";

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

		for (TypeElement annotation : annotations) {
			Set<? extends Element> elements = roundEnv.getElementsAnnotatedWith(annotation);
			for (Element element : elements) {

				String outputFormatString = processingEnv.getOptions().get(OPTION_OUTPUTFORMAT);
				boolean debugOutput = !"false".equals(processingEnv.getOptions().get(OPTION_DEBUG));
				String includeValidationString = processingEnv.getOptions().get(OPTION_INCLUDEVALIDATION);

				OutputFormat outputFormat = OutputFormat.EXTJS4;
				if (StringUtils.hasText(outputFormatString)) {
					if (OutputFormat.TOUCH2.name().equalsIgnoreCase(outputFormatString)) {
						outputFormat = OutputFormat.TOUCH2;
					}
				}

				IncludeValidation includeValidation = IncludeValidation.NONE;
				if (StringUtils.hasText(includeValidationString)) {
					if (IncludeValidation.ALL.name().equalsIgnoreCase(includeValidationString)) {
						includeValidation = IncludeValidation.ALL;
					} else if (IncludeValidation.BUILTIN.name().equalsIgnoreCase(includeValidationString)) {
						includeValidation = IncludeValidation.BUILTIN;
					}
				}

				try {
					TypeElement typeElement = (TypeElement) element;

					String qualifiedName = typeElement.getQualifiedName().toString();
					Class<?> modelClass = Class.forName(qualifiedName);

					String code = ModelGenerator.generateJavascript(modelClass, outputFormat, includeValidation,
							debugOutput);

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

					FileObject fo = processingEnv.getFiler().createResource(StandardLocation.SOURCE_OUTPUT,
							packageName, fileName + ".js");
					OutputStream os = fo.openOutputStream();
					os.write(code.getBytes(ExtDirectSpringUtil.UTF8_CHARSET));
					os.close();

				} catch (ClassNotFoundException e) {
					processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, e.getMessage());
				} catch (IOException e) {
					processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, e.getMessage());
				}

			}
		}

		return ALLOW_OTHER_PROCESSORS_TO_CLAIM_ANNOTATIONS;
	}

}
