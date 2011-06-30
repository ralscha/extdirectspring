package ch.ralscha.extdirectspring.demo.simpleapp;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ch.ralscha.extdirectspring.annotation.ExtDirectMethod;
import ch.ralscha.extdirectspring.annotation.ExtDirectMethodType;
import ch.ralscha.extdirectspring.bean.ExtDirectStoreReadRequest;
import ch.ralscha.extdirectspring.bean.ExtDirectStoreResponse;
import ch.ralscha.extdirectspring.demo.util.ExtDirectStorePagingResponse;

import com.google.common.collect.Lists;

@Service
public class UserService {

	@Autowired
	private SimpleUserDb userDb;
		
	@ExtDirectMethod(value = ExtDirectMethodType.STORE_READ, group = "simpleapp")
	public ExtDirectStoreResponse<User> load(ExtDirectStoreReadRequest request) {
		List<User> users = userDb.getAll();
		return new ExtDirectStorePagingResponse<User>(request, users);
	}
	
	
	@ExtDirectMethod(value = ExtDirectMethodType.STORE_MODIFY, group = "simpleapp")
	public List<User> create(List<User> newUsers) {
		List<User> insertedUsers = Lists.newArrayList();

		for (User newUser : newUsers) {
			userDb.insert(newUser);
			insertedUsers.add(newUser);
		}

		return insertedUsers;
	}

	@ExtDirectMethod(value = ExtDirectMethodType.STORE_MODIFY, group = "simpleapp")
	public List<User> update(List<User> modifiedUsers) {
		List<User> updatedRecords = Lists.newArrayList();
		for (User modifiedUser : modifiedUsers) {
			User u = userDb.findUser(modifiedUser.getId());
			if (u != null) {
				u.update(modifiedUser);
				updatedRecords.add(u);
			}
		}
		return updatedRecords;
	}

	@ExtDirectMethod(value = ExtDirectMethodType.STORE_MODIFY, group = "simpleapp")
	public void destroy(List<User> destroyUsers) {
		for (User user : destroyUsers) {
			userDb.deleteUser(user);
		}
	}	
}
