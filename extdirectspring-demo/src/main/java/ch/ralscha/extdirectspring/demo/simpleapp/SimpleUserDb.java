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
package ch.ralscha.extdirectspring.demo.simpleapp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import au.com.bytecode.opencsv.CSVReader;

import com.google.common.base.Charsets;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;

@Service
public class SimpleUserDb {

	@Autowired
	private Resource userdata;

	private int maxId;

	private Map<String, User> users;

	@PostConstruct
	public void readData() throws IOException {
		users = Maps.newHashMap();
		try (InputStream is = userdata.getInputStream();
				BufferedReader br = new BufferedReader(new InputStreamReader(is, Charsets.UTF_8.name()));
				CSVReader reader = new CSVReader(br, '|')) {
			String[] nextLine;
			while ((nextLine = reader.readNext()) != null) {
				User u = new User(nextLine);
				users.put(u.getId(), u);
				maxId = Math.max(maxId, Integer.valueOf(u.getId()));
			}
		}

	}

	public List<User> getAll() {
		return ImmutableList.copyOf(users.values());
	}

	public User findUser(String id) {
		return users.get(id);
	}

	public void deleteUser(User user) {
		users.remove(user.getId());
	}

	public User insert(User p) {
		maxId = maxId + 1;
		p.setId(String.valueOf(maxId));
		users.put(String.valueOf(maxId), p);
		return p;
	}

}
