package ch.ralscha.extdirectspring.demo.filter;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.springframework.beans.BeanUtils;

import com.google.common.collect.Ordering;

public class PropertyOrdering extends Ordering<Object> {

  private Method readMethod;

  public PropertyOrdering(Class<?> clazz, String propertyName) {
    readMethod = BeanUtils.getPropertyDescriptor(clazz, propertyName).getReadMethod();
  }

  @SuppressWarnings("unchecked")
  @Override
  public int compare(Object o1, Object o2) {
    try {
      Object value1 = readMethod.invoke(o1);
      Object value2 = readMethod.invoke(o2);
      return ((Comparable<Object>)value1).compareTo(value2);
    } catch (IllegalArgumentException e) {
      e.printStackTrace();
    } catch (IllegalAccessException e) {
      e.printStackTrace();
    } catch (InvocationTargetException e) {
      e.printStackTrace();
    }

    return 0;

  }

}
