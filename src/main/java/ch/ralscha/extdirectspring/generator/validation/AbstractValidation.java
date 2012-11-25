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
package ch.ralscha.extdirectspring.generator.validation;

import java.lang.annotation.Annotation;
import java.math.BigDecimal;

import org.springframework.core.annotation.AnnotationUtils;

import ch.ralscha.extdirectspring.generator.IncludeValidation;
import ch.ralscha.extdirectspring.generator.ModelBean;
import ch.ralscha.extdirectspring.generator.ModelFieldBean;

/**
 * Base class for the validation objects
 * 
 * @author Ralph Schaer
 */
public abstract class AbstractValidation {
	private final String type;

	private final String field;

	public AbstractValidation(String type, String field) {
		super();
		this.type = type;
		this.field = field;
	}

	public String getType() {
		return type;
	}

	public String getField() {
		return field;
	}

	public static void addValidationToModel(ModelBean model, ModelFieldBean modelFieldBean, Annotation fieldAnnotation,
			IncludeValidation includeValidation) {
		String annotationClassName = fieldAnnotation.annotationType().getName();

		if (includeValidation == IncludeValidation.BUILTIN || includeValidation == IncludeValidation.ALL) {

			if (annotationClassName.equals("javax.validation.constraints.NotNull")
					|| (annotationClassName.equals("org.hibernate.validator.constraints.NotEmpty"))) {
				model.addValidation(new PresenceValidation(modelFieldBean.getName()));
			} else if (annotationClassName.equals("javax.validation.constraints.Size")
					|| (annotationClassName.equals("org.hibernate.validator.constraints.Length"))) {

				Integer min = (Integer) AnnotationUtils.getValue(fieldAnnotation, "min");
				Integer max = (Integer) AnnotationUtils.getValue(fieldAnnotation, "max");
				model.addValidation(new LengthValidation(modelFieldBean.getName(), min, max));

			} else if (annotationClassName.equals("javax.validation.constraints.Pattern")) {
				String regexp = (String) AnnotationUtils.getValue(fieldAnnotation, "regexp");
				model.addValidation(new FormatValidation(modelFieldBean.getName(), regexp));
			} else if (annotationClassName.equals("org.hibernate.validator.constraints.Email")) {
				model.addValidation(new EmailValidation(modelFieldBean.getName()));
			}
		}

		if (includeValidation == IncludeValidation.ALL) {

			if (annotationClassName.equals("javax.validation.constraints.DecimalMax")) {
				String value = (String) AnnotationUtils.getValue(fieldAnnotation);
				model.addValidation(new RangeValidation(modelFieldBean.getName(), null, new BigDecimal(value)));
			} else if (annotationClassName.equals("javax.validation.constraints.DecimalMin")) {
				String value = (String) AnnotationUtils.getValue(fieldAnnotation);
				model.addValidation(new RangeValidation(modelFieldBean.getName(), new BigDecimal(value), null));
			} else if (annotationClassName.equals("javax.validation.constraints.Digits")) {
				Integer integer = (Integer) AnnotationUtils.getValue(fieldAnnotation, "integer");
				Integer fraction = (Integer) AnnotationUtils.getValue(fieldAnnotation, "fraction");
				model.addValidation(new DigitsValidation(modelFieldBean.getName(), integer, fraction));
			} else if (annotationClassName.equals("javax.validation.constraints.Future")) {
				model.addValidation(new FutureValidation(modelFieldBean.getName()));
			} else if (annotationClassName.equals("javax.validation.constraints.Max")) {
				Long value = (Long) AnnotationUtils.getValue(fieldAnnotation);
				model.addValidation(new RangeValidation(modelFieldBean.getName(), null, value));
			} else if (annotationClassName.equals("javax.validation.constraints.Min")) {
				Long value = (Long) AnnotationUtils.getValue(fieldAnnotation);
				model.addValidation(new RangeValidation(modelFieldBean.getName(), value, null));
			} else if (annotationClassName.equals("javax.validation.constraints.Past")) {
				model.addValidation(new PastValidation(modelFieldBean.getName()));
			} else if (annotationClassName.equals("org.hibernate.validator.constraints.CreditCardNumber")) {
				model.addValidation(new CreditCardNumberValidation(modelFieldBean.getName()));
			} else if (annotationClassName.equals("org.hibernate.validator.constraints.NotBlank")) {
				model.addValidation(new NotBlankValidation(modelFieldBean.getName()));
			} else if (annotationClassName.equals("org.hibernate.validator.constraints.Range")) {
				Long min = (Long) AnnotationUtils.getValue(fieldAnnotation, "min");
				Long max = (Long) AnnotationUtils.getValue(fieldAnnotation, "max");
				model.addValidation(new RangeValidation(modelFieldBean.getName(), min, max));
			}
		}
	}

}
