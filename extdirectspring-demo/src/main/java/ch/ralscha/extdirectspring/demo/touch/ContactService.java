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
package ch.ralscha.extdirectspring.demo.touch;

import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;

import ch.ralscha.extdirectspring.annotation.ExtDirectMethod;
import ch.ralscha.extdirectspring.annotation.ExtDirectMethodType;

@Service
public class ContactService {
	@ExtDirectMethod(value = ExtDirectMethodType.STORE_READ, group = "touch")
	public Contact read(@RequestParam("id") Integer id) {
		Contact c = new Contact();
		c.setId(id);
		c.setEmail("john.doe@unknown.com");
		c.setName("John Doe");
		c.setMessage("This person has no messages");
		return c;
	}

	@ExtDirectMethod(value = ExtDirectMethodType.STORE_MODIFY, group = "touch")
	public Contact update(Contact modifiedContact) {
		System.out.println("update");
		System.out.println(modifiedContact);
		return modifiedContact;
	}

	@ExtDirectMethod(value = ExtDirectMethodType.STORE_MODIFY, group = "touch")
	public Contact create(Contact newContact) {
		System.out.println("create");
		newContact.setId((int)(Math.random() * 1000) + 1);
		System.out.println(newContact);
		return newContact;
	}

	@ExtDirectMethod(value = ExtDirectMethodType.STORE_MODIFY, group = "touch")
	public void destroy(Contact destroyContact) {
		System.out.println("destroy");
		System.out.println(destroyContact);
	}
}
