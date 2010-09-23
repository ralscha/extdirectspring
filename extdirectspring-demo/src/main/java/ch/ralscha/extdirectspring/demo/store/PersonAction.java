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

package ch.ralscha.extdirectspring.demo.store;

import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.inject.Inject;
import javax.inject.Named;

import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestParam;

import ch.ralscha.extdirectspring.annotation.ExtDirectMethod;
import ch.ralscha.extdirectspring.annotation.ExtDirectMethodType;
import ch.ralscha.extdirectspring.bean.DataType;
import ch.ralscha.extdirectspring.bean.ExtDirectStoreReadRequest;
import ch.ralscha.extdirectspring.bean.ExtDirectStoreResponse;
import ch.ralscha.extdirectspring.bean.Field;
import ch.ralscha.extdirectspring.bean.MetaData;
import ch.ralscha.extdirectspring.bean.SortDirection;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Ordering;
import com.google.common.collect.Sets;

@Named
public class PersonAction {

  @Inject
  private RandomDataBean dataBean;

  private Map<String, Ordering<Person>> orderingMap;

  public PersonAction() {
    orderingMap = Maps.newHashMap();
    orderingMap.put("fullName", new Ordering<Person>() {

      public int compare(Person left, Person right) {
        return left.getFullName().compareTo(right.getFullName());
      }
    });    
    orderingMap.put("lastName", new Ordering<Person>() {

      public int compare(Person left, Person right) {
        return left.getLastName().compareTo(right.getLastName());
      }
    });
    orderingMap.put("firstName", new Ordering<Person>() {

      public int compare(Person left, Person right) {
        return left.getFirstName().compareTo(right.getFirstName());
      }
    });
    orderingMap.put("street", new Ordering<Person>() {

      public int compare(Person left, Person right) {
        return left.getStreet().compareTo(right.getStreet());
      }
    });
    orderingMap.put("city", new Ordering<Person>() {

      public int compare(Person left, Person right) {
        return left.getCity().compareTo(right.getCity());
      }
    });
    orderingMap.put("state", new Ordering<Person>() {

      public int compare(Person left, Person right) {
        return left.getState().compareTo(right.getState());
      }
    });
    orderingMap.put("zip", new Ordering<Person>() {

      public int compare(Person left, Person right) {
        return left.getZip().compareTo(right.getZip());
      }
    });
    orderingMap.put("country", new Ordering<Person>() {

      public int compare(Person left, Person right) {
        return left.getCountry().compareTo(right.getCountry());
      }
    });

  }

  @ExtDirectMethod(value = ExtDirectMethodType.STORE_READ, group = "store")
  public List<Person> load(ExtDirectStoreReadRequest request) {
    return dataBean.findPersons(request.getQuery());
  }

  @ExtDirectMethod(value = ExtDirectMethodType.STORE_READ, group = "store")
  public ExtDirectStoreResponse<Person> loadWithPaging(ExtDirectStoreReadRequest request,
      @RequestParam(value = "no", defaultValue = "0") int no, @RequestParam(required = false) String name) {

    System.out.println("no   = " + no);
    System.out.println("name = " + name);

    List<Person> persons = dataBean.findPersons(request.getQuery());
    int totalSize = persons.size();

    Ordering<Person> ordering = null;

    if (StringUtils.hasText(request.getGroupBy())) {
      ordering = orderingMap.get(request.getGroupBy());
      if (ordering != null) {
        if (request.isDescendingGroupSort()) {
          ordering = ordering.reverse();
        }
      }
    }

    if (StringUtils.hasText(request.getSort())) {
      Ordering<Person> sortOrdering = orderingMap.get(request.getSort());
      if (sortOrdering != null) {
        if (request.isDescendingSort()) {
          sortOrdering = sortOrdering.reverse();
        }
      }
      if (ordering == null) {
        ordering = sortOrdering;
      } else {
        ordering = ordering.compound(sortOrdering);
      }
    }

    if (ordering != null) {
      persons = ordering.sortedCopy(persons);
    }

    if (request.getStart() != null && request.getLimit() != null) {
      persons = persons.subList(request.getStart(), Math.min(totalSize, request.getStart() + request.getLimit()));
    }

    return new ExtDirectStoreResponse<Person>(totalSize, persons);
  }

  @ExtDirectMethod(value = ExtDirectMethodType.STORE_MODIFY, group = "store")
  public List<Person> create(List<Person> newPersons) {
    List<Person> insertedPersons = Lists.newArrayList();

    for (Person newPerson : newPersons) {
      dataBean.insert(newPerson);
      insertedPersons.add(newPerson);
    }

    return insertedPersons;
  }

  @ExtDirectMethod(value = ExtDirectMethodType.STORE_MODIFY, group = "store")
  public List<Person> update(@RequestParam(value = "no", defaultValue = "0") int no,
      @RequestParam(value = "name", required = false) String name, List<Person> modifiedPersons) {

    List<Person> updatedRecords = Lists.newArrayList();

    for (Person modifiedPerson : modifiedPersons) {
      Person p = dataBean.findPerson(modifiedPerson.getId());
      if (p != null) {
        p.update(modifiedPerson);
        updatedRecords.add(p);
      }
    }

    return updatedRecords;
  }

  @ExtDirectMethod(value = ExtDirectMethodType.STORE_MODIFY, group = "store")
  public List<Integer> destroy(List<Integer> destroyIds) {
    List<Integer> deletedPersonsId = Lists.newArrayList();

    for (Integer id : destroyIds) {
      dataBean.deletePerson(id);
      deletedPersonsId.add(id);
    }

    return deletedPersonsId;
  }

  @ExtDirectMethod(value = ExtDirectMethodType.STORE_READ, group = "store")
  public Set<State> getStates() {
    List<Person> persons = dataBean.findPersons(null);
    Set<State> states = Sets.newTreeSet();
    for (Person person : persons) {
      states.add(new State(person.getState()));
    }

    return states;
  }

  @ExtDirectMethod(value = ExtDirectMethodType.STORE_READ, group = "metadata")
  public ExtDirectStoreResponse<PersonFullName> loadPersonFullName(ExtDirectStoreReadRequest request) {

    List<Person> persons = dataBean.findPersons(null);
    int totalSize = persons.size();
    
    Ordering<Person> ordering = orderingMap.get("fullName");
    persons = ordering.reverse().sortedCopy(persons);
    
    if (request.getStart() != null && request.getLimit() != null) {
      persons = persons.subList(request.getStart(), Math.min(totalSize, request.getStart() + request.getLimit()));
    } else {
      persons = persons.subList(0, 100);
    }

    List<PersonFullName> personFullNameList = Lists.transform(persons, new Function<Person, PersonFullName>() {
      @Override
      public PersonFullName apply(Person person) {
        return new PersonFullName(person);
      }
    });

    ExtDirectStoreResponse<PersonFullName> response = new ExtDirectStoreResponse<PersonFullName>(totalSize,
        personFullNameList);
    
    //Send metadata only the first time
    if (request.getStart() == null) {
      MetaData metaData = new MetaData();
    
      metaData.setPagingParameter(0, 100);
      metaData.setSortInfo("fullName", SortDirection.DESC);
    
      Field field = new Field("fullName");
      field.setType(DataType.STRING);
      field.addCustomProperty("header", "Full Name");
      field.addCustomProperty("width", 60);
      field.addCustomProperty("sortable", false);
      field.addCustomProperty("resizable", true);
      field.addCustomProperty("hideable", false);
      metaData.addField(field);
  
      response.setMetaData(metaData);
    }
    return response;
  }
  
  @ExtDirectMethod(value = ExtDirectMethodType.STORE_READ, group = "metadata")
  public ExtDirectStoreResponse<PersonFullNameCity> loadPersonFullNameCity(ExtDirectStoreReadRequest request) {

    List<Person> persons = dataBean.findPersons(null);
    int totalSize = persons.size();
    
    Ordering<Person> ordering = orderingMap.get("city");
    persons = ordering.sortedCopy(persons);
    
    if (request.getStart() != null && request.getLimit() != null) {
      persons = persons.subList(request.getStart(), Math.min(totalSize, request.getStart() + request.getLimit()));
    } else {
      persons = persons.subList(0, 50);
    }

    List<PersonFullNameCity> personFullNameCityList = Lists.transform(persons, new Function<Person, PersonFullNameCity>() {
      @Override
      public PersonFullNameCity apply(Person person) {
        return new PersonFullNameCity(person);
      }
    });

    ExtDirectStoreResponse<PersonFullNameCity> response = new ExtDirectStoreResponse<PersonFullNameCity>(totalSize, personFullNameCityList);
    
    //Send metadata only the first time
    if (request.getStart() == null) {
      MetaData metaData = new MetaData();
    
      metaData.setPagingParameter(0, 50);
      metaData.setSortInfo("fullName", SortDirection.DESC);
    
      Field field = new Field("fullName");
      field.setType(DataType.STRING);
      field.addCustomProperty("header", "Full Name");
      field.addCustomProperty("width", 120);
      field.addCustomProperty("sortable", false);
      field.addCustomProperty("resizable", true);
      field.addCustomProperty("hideable", false);
      metaData.addField(field);
      
      field = new Field("city");
      field.setType(DataType.STRING);
      field.addCustomProperty("header", "City");
      field.addCustomProperty("width", 60);
      field.addCustomProperty("sortable", false);
      field.addCustomProperty("resizable", true);
      field.addCustomProperty("hideable", true);
      metaData.addField(field);      
  
      response.setMetaData(metaData);
    }
    return response;
  }  

}
