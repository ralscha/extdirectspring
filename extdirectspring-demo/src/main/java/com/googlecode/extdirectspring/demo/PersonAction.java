/**
 * Copyright 2010 Ralph Schaer
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

package com.googlecode.extdirectspring.demo;

import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.inject.Inject;
import javax.inject.Named;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestParam;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Ordering;
import com.google.common.collect.Sets;
import com.googlecode.extdirectspring.annotation.ExtDirectMethod;
import com.googlecode.extdirectspring.annotation.ExtDirectStoreModifyMethod;
import com.googlecode.extdirectspring.annotation.ExtDirectStoreReadMethod;
import com.googlecode.extdirectspring.bean.ExtDirectStoreReadRequest;
import com.googlecode.extdirectspring.bean.ExtDirectStoreResponse;

@Named
public class PersonAction {

  @Inject
  private RandomDataBean dataBean;

  private Map<String, Ordering<Person>> orderingMap;

  public PersonAction() {
    orderingMap = Maps.newHashMap();
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

  @ExtDirectStoreReadMethod
  public List<Person> load(ExtDirectStoreReadRequest request) {
    return dataBean.findPersons(request.getQuery());
  }

  @ExtDirectStoreReadMethod
  public ExtDirectStoreResponse<Person> loadWithPaging(ExtDirectStoreReadRequest request, 
      @RequestParam(value="no", required=false)int no, 
      @RequestParam(value="name", required=false)String name) {
        
    List<Person> persons = dataBean.findPersons(request.getQuery());
    int totalSize = persons.size();

    if (StringUtils.hasText(request.getSort())) {
      Ordering<Person> ordering = orderingMap.get(request.getSort());
      if (ordering != null) {
        if (request.isDecendingSort()) {
          ordering = ordering.reverse();
        }
        persons = ordering.sortedCopy(persons);
      }
    }

    if (request.getStart() != null && request.getLimit() != null) {
      persons = persons.subList(request.getStart(), Math.min(totalSize, request.getStart() + request.getLimit()));
    }

    return new ExtDirectStoreResponse<Person>(totalSize, persons);
  }

  @ExtDirectStoreModifyMethod(type=Person.class)
  public ExtDirectStoreResponse<Person> create(DirectStorePersonRequest newPersons) {
    List<Person> insertedPersons = Lists.newArrayList();

    if (newPersons != null && newPersons.getRecords() != null) {
      for (Person newPerson : newPersons.getRecords()) {
        dataBean.insert(newPerson);
        insertedPersons.add(newPerson);
      }
    }

    return new ExtDirectStoreResponse<Person>(insertedPersons);
  }

  @ExtDirectStoreModifyMethod(type=Person.class)
  public List<Person> update(List<Person> modifiedPersons) {
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

  @ExtDirectStoreModifyMethod(type=Integer.class)
  public List<Integer> destroy(List<Integer> destroyIds) {
    List<Integer> deletedPersonsId = Lists.newArrayList();

    for (Integer id : destroyIds) {
      dataBean.deletePerson(id);
      deletedPersonsId.add(id);
    }

    return deletedPersonsId;
  }

  @ExtDirectMethod
  public Set<State> getStates() {
    List<Person> persons = dataBean.findPersons(null);
    Set<State> states = Sets.newTreeSet();
    for (Person person : persons) {
      states.add(new State(person.getState()));
    }

    return states;
  }

}
