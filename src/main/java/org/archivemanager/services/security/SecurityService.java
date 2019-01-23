package org.archivemanager.services.security;
import java.util.List;

import org.archivemanager.models.system.Group;
import org.archivemanager.models.system.User;
import org.archivemanager.services.net.http.HttpRequest;


public interface SecurityService {

	List<User> getUsers(String query);
	List<Group> getGroups(String query);
	
	User getCurrentUser(HttpRequest request);
	User getUserByXid(long xid);
	User getUserByUsername(String username);
	User getUserByEmail(String email);
	
	User addUser(User user);
	void updateUser(User user);
	void removeUser(long id);
	
	int authenticate(String username, String password);
	String generatePassword();
		
}
