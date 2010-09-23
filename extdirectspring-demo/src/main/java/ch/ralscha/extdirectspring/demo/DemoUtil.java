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
