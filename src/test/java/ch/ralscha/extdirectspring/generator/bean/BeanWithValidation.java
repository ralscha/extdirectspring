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
package ch.ralscha.extdirectspring.generator.bean;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.validation.constraints.DecimalMax;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Digits;
import javax.validation.constraints.Future;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.Past;

import org.hibernate.validator.constraints.CreditCardNumber;
import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotBlank;
import org.hibernate.validator.constraints.Range;

import ch.ralscha.extdirectspring.generator.ModelFieldBean;
import ch.ralscha.extdirectspring.generator.ModelType;

public class BeanWithValidation {

	@Email
	public String email;

	@DecimalMax("100")
	@DecimalMin("1")
	public BigDecimal minMax1;

	@Max(10000)
	@Min(20)
	public int minMax2;

	@Range(min = 20, max = 50)
	public long minMax3;

	@Digits(integer = 10, fraction = 2)
	public String digits;

	@Future
	public Date future;

	@Past
	public Date past;

	@NotBlank
	public String notBlank;

	@CreditCardNumber
	public String creditCardNumber;

	public static List<ModelFieldBean> expectedFields = new ArrayList<ModelFieldBean>();
	static {

		ModelFieldBean field = new ModelFieldBean("email", ModelType.STRING);
		expectedFields.add(field);

		field = new ModelFieldBean("minMax1", ModelType.FLOAT);
		expectedFields.add(field);

		field = new ModelFieldBean("minMax2", ModelType.INTEGER);
		expectedFields.add(field);

		field = new ModelFieldBean("minMax3", ModelType.INTEGER);
		expectedFields.add(field);

		field = new ModelFieldBean("digits", ModelType.STRING);
		expectedFields.add(field);

		field = new ModelFieldBean("future", ModelType.DATE);
		expectedFields.add(field);

		field = new ModelFieldBean("past", ModelType.DATE);
		expectedFields.add(field);

		field = new ModelFieldBean("notBlank", ModelType.STRING);
		expectedFields.add(field);

		field = new ModelFieldBean("creditCardNumber", ModelType.STRING);
		expectedFields.add(field);
	}
}
