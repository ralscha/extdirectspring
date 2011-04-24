package ch.ralscha.extdirectspring.demo.named;

import org.springframework.stereotype.Service;

import ch.ralscha.extdirectspring.annotation.ExtDirectMethod;
import ch.ralscha.extdirectspring.annotation.ExtDirectMethodType;

@Service
public class NamedService {
	
	@ExtDirectMethod(value = ExtDirectMethodType.SIMPLE_NAMED, group = "named")
	public String showDetails(String firstName, String lastName, int age) {
		return String.format("Hi %s %s, you are %d years old.", firstName, lastName, age);
	}
}
