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

import org.joda.time.LocalDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import ch.ralscha.extdirectspring.annotation.ExtDirectMethod;
import ch.ralscha.extdirectspring.annotation.ExtDirectMethodType;

import com.google.common.collect.Lists;

@Service
public class NotesService {

	private final static Logger logger = LoggerFactory.getLogger(NotesService.class);

	@ExtDirectMethod(group = "touchnote")
	public void log(String msg) {
		logger.info(msg);
	}

	@ExtDirectMethod(value = ExtDirectMethodType.STORE_READ, group = "touchnote")
	public List<Note> readNotes() {
		List<Note> notes = Lists.newArrayList();

		Note n = new Note();
		n.setId(1);
		n.setDateCreated(LocalDate.now().toDate());
		n.setTitle("Test Note");
		n.setNarrative("This is a simple test note");
		notes.add(n);

		n = new Note();
		n.setId(2);
		n.setDateCreated(LocalDate.now().plusDays(1).toDate());
		n.setTitle("Test Note 2 ");
		n.setNarrative("This is a second test note");
		notes.add(n);

		n = new Note();
		n.setId(3);
		n.setDateCreated(LocalDate.now().plusDays(2).toDate());
		n.setTitle("Test Note 3 ");
		n.setNarrative("This is a third test note");
		notes.add(n);

		return notes;
	}
}
