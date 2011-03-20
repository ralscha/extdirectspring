/**
 * Copyright 2010-2011 Ralph Schaer <ralphschaer@gmail.com>
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
package ch.ralscha.extdirectspring.demo;

import java.beans.PropertyDescriptor;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.BeanUtils;
import org.springframework.util.StringUtils;

import ch.ralscha.extdirectspring.bean.DataType;
import ch.ralscha.extdirectspring.bean.Field;

import com.google.common.collect.Lists;

public enum DemoUtil {

	INSTANCE;

	public List<Field> getFields(Class<?> clazz, String... excludeProperties) {
		List<Field> fields = Lists.newArrayList();

		Set<String> exclude = new HashSet<String>();

		if (excludeProperties != null) {
			exclude.addAll(Arrays.asList(excludeProperties));
		}

		PropertyDescriptor[] descriptors = BeanUtils.getPropertyDescriptors(clazz);
		if (descriptors != null) {
			for (PropertyDescriptor descriptor : descriptors) {
				if (!exclude.contains(descriptor.getName())) {
					Field field = new Field(descriptor.getName());
					field.setType(getDataType(descriptor.getPropertyType()));
					field.addCustomProperty("header", StringUtils.capitalize(descriptor.getName()));
					fields.add(field);
				}
			}
		}

		return fields;
	}

	public DataType getDataType(Class<?> clazz) {
		if (clazz == String.class) {
			return DataType.STRING;
		}
		return null;

	}

}
