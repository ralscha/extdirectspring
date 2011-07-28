package ch.ralscha.starter.service;

import static ch.ralscha.extdirectspring.annotation.ExtDirectMethodType.POLL;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import ch.ralscha.extdirectspring.annotation.ExtDirectMethod;

@Service
public class PollService {

	private DateTimeFormatter fmt = DateTimeFormat.forPattern("HH:mm:ss");

	@ExtDirectMethod(value = POLL, event = "chartdata")
	@PreAuthorize("isAuthenticated()")
	public Poll getPollData() {
		return new Poll(fmt.print(new DateTime()), (int) (Math.random() * 1000));
	}
}
