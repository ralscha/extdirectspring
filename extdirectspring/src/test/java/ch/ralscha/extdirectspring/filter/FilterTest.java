/**
 * Copyright 2010 Ralph Schaer <ralphschaer@gmail.com>
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

package ch.ralscha.extdirectspring.filter;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertNull;
import static junit.framework.Assert.assertSame;
import static junit.framework.Assert.assertTrue;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;


public class FilterTest {

  @Test
  public void testNumericFilter() {
    Map<String,Object> json = new HashMap<String,Object>();
    json.put("field", "aField");
    json.put("type", "numeric");
    json.put("comparison", "lt");
    json.put("value", 12);
    
    Filter filter = Filter.createFilter(json);    
    assertTrue(filter instanceof NumericFilter);
    NumericFilter numericFilter = (NumericFilter)filter;
    assertEquals("aField", numericFilter.getField());
    assertEquals(12, numericFilter.getValue());
    assertSame(ComparisonEnum.LESS_THAN, numericFilter.getComparison());    
  }
  
  
  @Test
  public void testStringFilter() {
    Map<String,Object> json = new HashMap<String,Object>();
    json.put("field", "aField");
    json.put("type", "string");
    json.put("value", "aString");
    
    Filter filter = Filter.createFilter(json);    
    assertTrue(filter instanceof StringFilter);
    StringFilter stringFilter = (StringFilter)filter;
    assertEquals("aField", stringFilter.getField());
    assertEquals("aString", stringFilter.getValue());    
  }
  
  @Test
  public void testDateFilter() {
    Map<String,Object> json = new HashMap<String,Object>();
    json.put("field", "aField");
    json.put("type", "date");
    json.put("value", "12.12.2010");
    json.put("comparison", "gt");
    
    Filter filter = Filter.createFilter(json);    
    assertTrue(filter instanceof DateFilter);
    DateFilter dateFilter = (DateFilter)filter;
    assertEquals("aField", dateFilter.getField());
    assertEquals("12.12.2010", dateFilter.getValue());   
    assertSame(ComparisonEnum.GREATER_THAN, dateFilter.getComparison()); 
  }  
  
  @Test
  public void testListFilter() {
    Map<String,Object> json = new HashMap<String,Object>();
    json.put("field", "aField");
    json.put("type", "list");
    json.put("value", "one,two,three");
    
    Filter filter = Filter.createFilter(json);    
    assertTrue(filter instanceof ListFilter);
    ListFilter listFilter = (ListFilter)filter;
    assertEquals("aField", listFilter.getField());
    
    List<String> list = listFilter.getValue();
    assertEquals(3, list.size());
    assertTrue(list.contains("one"));
    assertTrue(list.contains("two"));
    assertTrue(list.contains("three"));
    assertFalse(list.contains("four"));       
  }
  
  @Test
  public void testBooleanFilter() {
    Map<String,Object> json = new HashMap<String,Object>();
    json.put("field", "aField");
    json.put("type", "boolean");
    json.put("value", false);
    
    Filter filter = Filter.createFilter(json);    
    assertTrue(filter instanceof BooleanFilter);
    BooleanFilter booleanFilter = (BooleanFilter)filter;
    assertEquals("aField", booleanFilter.getField());
    assertEquals(false, booleanFilter.getValue());    
  }
  
  @Test
  public void testNotExistsFilter() {
    Map<String,Object> json = new HashMap<String,Object>();
    json.put("field", "aField");
    json.put("type", "xy");
    json.put("value", "aValue");
    
    Filter filter = Filter.createFilter(json);    
    assertNull(filter);
  }
}
