/**
 * Copyright 2010-2012 Ralph Schaer <ralphschaer@gmail.com>
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
package ch.ralscha.extdirectspring.demo.touch;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import javax.annotation.PostConstruct;

import org.joda.time.LocalDate;
import org.springframework.stereotype.Service;

import com.google.common.collect.ImmutableList;

@Service
public class NotesDb {

	private AtomicInteger lastId = new AtomicInteger(0);

	private ConcurrentHashMap<Integer, Note> map = new ConcurrentHashMap<Integer, Note>();

	public void addOrUpdate(Note note) {
		if (note.getId() == null || note.getId() < 0) {
			int id = lastId.incrementAndGet();
			note.setId(id);
			map.put(id, note);
		}
		else {
			map.put(note.getId(), note);
		}
	}

	public void delete(Note note) {
		map.remove(note.getId());
	}

	@PostConstruct
	public void addTestData() {
		Note n = new Note();
		n.setDateCreated(LocalDate.now().toDate());
		n.setTitle("Test Note");
		n.setNarrative("This is a simple test note");
		addOrUpdate(n);

		n = new Note();
		n.setDateCreated(LocalDate.now().plusDays(1).toDate());
		n.setTitle("Test Note 2 ");
		n.setNarrative("This is a second test note");
		addOrUpdate(n);

		n = new Note();
		n.setDateCreated(LocalDate.now().plusDays(2).toDate());
		n.setTitle("Test Note 3 ");
		n.setNarrative("This is a third test note");
		addOrUpdate(n);
	}

	public List<Note> readAll() {
		ImmutableList.Builder<Note> builder = ImmutableList.builder();
		builder.addAll(map.values());
		return builder.build();
	}
}
