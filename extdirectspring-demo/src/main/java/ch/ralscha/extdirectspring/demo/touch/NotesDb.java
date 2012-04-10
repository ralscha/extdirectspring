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
		} else {
			map.put(note.getId(), note);
		}		
	}
	
	public void delete(Note note) {
		map.replace(note.getId(), note);
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
