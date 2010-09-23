package ch.ralscha.extdirectspring.bean;

import java.beans.PropertyDescriptor;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.BeanUtils;

public class MetaData {

  private Map<String,Object> metaData;
  
  public MetaData() {
    metaData = new LinkedHashMap<String,Object>();
    
    
    metaData.put("root", "records");
    metaData.put("totalProperty", "total");
    metaData.put("successProperty", "success");
  }
  
  public void setPagingParameter(int start, int limit) {
    metaData.put("start", start);
    metaData.put("limit", limit);
  }
  
  public void setIdProperty(String idProperty) {
    metaData.put("idProperty", idProperty);
  }

  public void setSortInfo(String field, SortDirection direction) {
    Map<String,String> sortInfo = new LinkedHashMap<String,String>();
    sortInfo.put("field", field);
    sortInfo.put("direction", direction.name());
    metaData.put("sortInfo", sortInfo);
  }  
  
  public void addFields(Class<?> clazz, String... excludeProperties) {
    Set<String> exclude = new HashSet<String>();
    
    if (excludeProperties != null) {
      exclude.addAll(Arrays.asList(excludeProperties));
    }
    
    PropertyDescriptor[] descriptors = BeanUtils.getPropertyDescriptors(clazz);
    if (descriptors != null) {
      for (PropertyDescriptor descriptor : descriptors) {
        if (!exclude.contains(descriptor.getName())) {
          addField(descriptor.getName());
        }
      }
    }
  }
  
  @SuppressWarnings("unchecked")
  public void addField(String name) {
    Map<String,Object> field = new LinkedHashMap<String,Object>();
    field.put("name", name); 
    
    List<Map<String,Object>> fields = (List<Map<String,Object>>)metaData.get("fields");
    if (fields == null) {
      fields = new ArrayList<Map<String,Object>>();
      metaData.put("fields", fields);
    }
    
    fields.add(field);
  }
  
  public void addCustomProperty(String key, Object value) {
    metaData.put(key, value);
  }
  
  public Map<String,Object> getMetaData() {
    return Collections.unmodifiableMap(metaData);
  }
  
}
