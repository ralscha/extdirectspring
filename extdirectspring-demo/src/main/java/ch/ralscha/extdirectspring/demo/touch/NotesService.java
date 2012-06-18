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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ch.ralscha.extdirectspring.annotation.ExtDirectMethod;
import ch.ralscha.extdirectspring.annotation.ExtDirectMethodType;

@Service
public class NotesService {

	private final static Logger logger = LoggerFactory.getLogger(NotesService.class);

	@Autowired
	private NotesDb notesDb;

	@ExtDirectMethod(group = "touchnote")
	public void log(final String msg) {
		logger.info(msg);
	}

	@ExtDirectMethod(value = ExtDirectMethodType.STORE_MODIFY, group = "touchnote")
	public List<Note> updateNotes(final List<Note> updatedNotes) {

		for (Note note : updatedNotes) {
			notesDb.addOrUpdate(note);
		}

		return updatedNotes;
	}

	@ExtDirectMethod(value = ExtDirectMethodType.STORE_READ, group = "touchnote")
	public List<Note> readNotes() {
		return notesDb.readAll();
	}

	@ExtDirectMethod(value = ExtDirectMethodType.STORE_MODIFY, group = "touchnote")
	public void destroyNotes(final List<Note> deleteIds) {
		for (Note note : deleteIds) {
			notesDb.delete(note);
		}
	}

}
