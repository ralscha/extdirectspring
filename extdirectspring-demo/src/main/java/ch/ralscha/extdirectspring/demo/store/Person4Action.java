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
package ch.ralscha.extdirectspring.demo.store;

import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ch.ralscha.extdirectspring.annotation.ExtDirectMethod;
import ch.ralscha.extdirectspring.annotation.ExtDirectMethodType;
import ch.ralscha.extdirectspring.bean.ExtDirectStoreReadRequest;
import ch.ralscha.extdirectspring.bean.ExtDirectStoreResponse;
import ch.ralscha.extdirectspring.demo.util.PropertyOrderingFactory;

import com.google.common.collect.Lists;
import com.google.common.collect.Ordering;
import com.google.common.collect.Sets;

@Service
public class Person4Action {

	@Autowired
	private RandomDataBean dataBean;

	@ExtDirectMethod(value = ExtDirectMethodType.STORE_READ, group = "store4")
	public List<Person> load(ExtDirectStoreReadRequest request) {
		List<Person> persons = dataBean.findPersons(request.getQuery());
		return persons.subList(0, Math.min(50, persons.size()));
	}

	@ExtDirectMethod(value = ExtDirectMethodType.STORE_READ, group = "store4")
	public ExtDirectStoreResponse<Person> loadWithPaging(ExtDirectStoreReadRequest request) {

		List<Person> persons = dataBean.findPersons(request.getQuery());
		int totalSize = persons.size();

		Ordering<Person> ordering = PropertyOrderingFactory.INSTANCE.createOrderingFromSorters(request.getSorters());
		if (ordering != null) {
			persons = ordering.sortedCopy(persons);
		}

		if (request.getStart() != null && request.getLimit() != null) {
			persons = persons.subList(request.getStart(), Math.min(totalSize, request.getStart() + request.getLimit()));
		}

		return new ExtDirectStoreResponse<Person>(totalSize, persons);
	}

	@ExtDirectMethod(value = ExtDirectMethodType.STORE_MODIFY, group = "store4")
	public List<Person> create(List<Person> newPersons) {
		List<Person> insertedPersons = Lists.newArrayList();

		for (Person newPerson : newPersons) {
			dataBean.insert(newPerson);
			insertedPersons.add(newPerson);
		}

		return insertedPersons;
	}

	@ExtDirectMethod(value = ExtDirectMethodType.STORE_MODIFY, group = "store4")
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

	@ExtDirectMethod(value = ExtDirectMethodType.STORE_MODIFY, group = "store4")
	public void destroy(List<Person> destroyPersons) {
		for (Person person : destroyPersons) {
			dataBean.deletePerson(person);
		}
	}

	@ExtDirectMethod(value = ExtDirectMethodType.STORE_READ, group = "store4")
	public Set<State> getStates() {
		List<Person> persons = dataBean.findPersons(null);
		Set<State> states = Sets.newTreeSet();
		for (Person person : persons) {
			states.add(new State(person.getState()));
		}

		return states;
	}

}
