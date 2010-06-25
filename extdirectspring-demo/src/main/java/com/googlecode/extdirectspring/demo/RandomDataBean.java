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

package com.googlecode.extdirectspring.demo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;
import org.springframework.core.io.Resource;
import au.com.bytecode.opencsv.CSVReader;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

@Named
public class RandomDataBean {

  @Inject
  private Resource randomdata;
  
  private int maxId;

  private Map<Integer, Person> persons;

  @PostConstruct
  public void readData() throws IOException {
    persons = Maps.newHashMap();
    InputStream is = randomdata.getInputStream();

    BufferedReader br = new BufferedReader(new InputStreamReader(is));

    CSVReader reader = new CSVReader(br, '|');
    String[] nextLine;
    while ((nextLine = reader.readNext()) != null) {
      Person p = new Person(nextLine);
      persons.put(Integer.valueOf(p.getId()), p);
      maxId = Math.max(maxId, Integer.valueOf(p.getId()));
    }

    br.close();
    is.close();
  }

  public List<Person> findPersons(final String query) {
    if (query != null && !query.trim().isEmpty()) {
      Iterable<Person> filtered = Iterables.filter(persons.values(), new Predicate<Person>() {

        public boolean apply(Person input) {
          return input.getLastName().toLowerCase().startsWith(query.toLowerCase());
        }
      });
      return Lists.newArrayList(filtered);
    }

    return Lists.newArrayList(persons.values());
  }
  
  public Person findPerson(final String id) {
    return persons.get(Integer.valueOf(id));
  }

  public void deletePerson(int personId) {
    persons.remove(personId);
  }

  public Person insert(Person p) {
    maxId = maxId + 1;
    p.setId(String.valueOf(maxId));
    persons.put(maxId, p);
    return p;
  }

}
