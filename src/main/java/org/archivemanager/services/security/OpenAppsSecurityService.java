package org.archivemanager.services.security;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.archivemanager.models.SystemModel;
import org.archivemanager.models.system.Group;
import org.archivemanager.models.system.Role;
import org.archivemanager.models.system.User;
import org.archivemanager.services.cache.CacheService;
import org.archivemanager.services.entity.Entity;
import org.archivemanager.services.entity.EntityService;
import org.archivemanager.services.net.http.HttpRequest;
import org.archivemanager.services.search.SearchRequest;
import org.archivemanager.services.search.SearchResponse;
import org.archivemanager.services.search.SearchResult;
import org.archivemanager.services.search.SearchService;
import org.archivemanager.util.RandomPassword;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class OpenAppsSecurityService implements SecurityService {
	private final static Logger log = Logger.getLogger(OpenAppsSecurityService.class.getName());
	@Autowired private EntityService entityService;
	@Autowired private SearchService searchService;
	@Autowired private CacheService cacheService;
	
	
	@Override
	public int authenticate(String username, String password) {		
		try {
		    User user = getUserByUsername(username);
		    if(user == null) return -1;
		    if(user.getPassword().equals(password))
		    	return 1;
		    else return -2;
		} catch (Exception e) {
			return -1;
		}
	}
	@Override
	public User getCurrentUser(HttpRequest request) {
		String xid = (String)request.getHeaders().get("xid");
		User user = xid != null ? getUserByXid(Long.valueOf(xid)) : null;
		if(user != null) return user;
		return new OpenAppsGuestUser();
	}
	@Override
	public User getUserByXid(long xid) {
		try {
			Entity userEntity = entityService.getEntity(SystemModel.USER, SystemModel.XID, xid);
			User user = getUser(userEntity);
			return user;
		} catch(Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	@Override
	public User getUserByUsername(String username) {
		User user = null;
		try {
			if(username.equals("administrator")) {
				user = new User();
				user.setName("administrator");
			} else {
				Entity userEntity = entityService.getEntity(SystemModel.USER, SystemModel.USERNAME, username);
				user = getUser(userEntity);
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
		return user;
	}
	@Override
	public User getUserByEmail(String email) {
		try {
			Entity userEntity = entityService.getEntity(SystemModel.USER, SystemModel.EMAIL, email);
			User user = getUser(userEntity);
			return user;
		} catch(Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	@Override
	public List<User> getUsers(String query) {
		List<User> users = new ArrayList<User>();
		System.out.println("getUsers() not implemented");
		return users;
	}
	@Override
	public List<Group> getGroups(String query) {
		List<Group> users = new ArrayList<Group>();
		System.out.println("getGroups() not implemented");
		return users;
	}
	@Override
	public String generatePassword() {
		RandomPassword generator = new RandomPassword();
		return generator.getPass();
	}
	@Override
	public void removeUser(long id) {
		
	}
	@Override
	public void updateUser(User user) {
		try {
			Entity userEntity = entityService.getEntity(user.getId());
			if(user.getPassword() != null && user.getPassword().length() > 0) 
				userEntity.addProperty(SystemModel.USER_PASSWORD, user.getPassword());
			if(user.getFirstName() != null && user.getFirstName().length() > 0) 
				userEntity.addProperty(SystemModel.FIRSTNAME, user.getFirstName());
			if(user.getLastName() != null && user.getLastName().length() > 0) 
				userEntity.addProperty(SystemModel.LASTNAME, user.getLastName());
			if(user.getEmail() != null && user.getEmail().length() > 0) 
				userEntity.addProperty(SystemModel.EMAIL, user.getEmail());
			entityService.updateEntity(userEntity);
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	protected User getUser(Entity userEntity) {
		User user = new User();
		user.setId(userEntity.getId());
		user.setFirstName(userEntity.getPropertyValueString(SystemModel.FIRSTNAME));
		user.setLastName(userEntity.getPropertyValueString(SystemModel.LASTNAME));
		user.setEmail(userEntity.getPropertyValueString(SystemModel.EMAIL));
		user.setName(userEntity.getPropertyValueString(SystemModel.USERNAME));
		return user;
	}
	
	public User addUser(User user) {
		try {
			SearchRequest query = new SearchRequest(SystemModel.USER);
			query.addParameter("xid", String.valueOf(user.getXid()));
			SearchResponse userEntities = searchService.search(query);
			Entity userEntity = null;
			if(userEntities != null && userEntities.getResults().size() > 0) {
				if(userEntities.getResults().size() > 1) {
					for(SearchResult u : userEntities.getResults()) {
						if(userEntity == null) userEntity = entityService.getEntity(Long.valueOf(u.getId()));
						else entityService.removeEntity(Long.valueOf(u.getId()));
					}
				} else userEntity = entityService.getEntity(Long.valueOf(userEntities.getResults().get(0).getId()));
				//cacheService.put("openapps.security.users", String.valueOf(user.getXid()), userEntity.getId());
			} else {
				userEntity = new Entity(SystemModel.USER);
				userEntity.setXid(user.getXid());
				if(user.getPassword() != null && user.getPassword().length() > 0) 
					userEntity.addProperty(SystemModel.USER_PASSWORD, user.getPassword());
				if(user.getFirstName() != null && user.getFirstName().length() > 0) 
					userEntity.addProperty(SystemModel.FIRSTNAME, user.getFirstName());
				if(user.getLastName() != null && user.getLastName().length() > 0) 
					userEntity.addProperty(SystemModel.LASTNAME, user.getLastName());
				if(user.getEmail() != null && user.getEmail().length() > 0) 
					userEntity.addProperty(SystemModel.EMAIL, user.getEmail());
				if(user.getName() != null && user.getName().length() > 0) {
					userEntity.addProperty(SystemModel.USERNAME, user.getName());
					userEntity.addProperty(SystemModel.NAME, user.getName());
				}
				entityService.updateEntity(userEntity);
				user.setId(userEntity.getId());
			}
			List<Role> roles = user.getRoles();
			for(Role role : roles) {
				Role r = getRole(role);
				if(r != null) role.setId(r.getId());
			}
			List<Group> groups = user.getGroups();
			for(Group group : groups) {
				Group g = getGroup(group);
				if(g != null) group.setId(g.getId());
			}
		} catch(Exception e) {
			log.log(Level.SEVERE, "", e);
		}
		return user;
	}
	protected Role getRole(Role role) throws Exception {
		Long nodeId = (Long)cacheService.get("openapps.security.roles", String.valueOf(role.getXid()));
		if(nodeId == null) {
			SearchRequest query = new SearchRequest(SystemModel.ROLE);
			query.addParameter("xid", String.valueOf(role.getXid()));
			SearchResponse roleEntities = searchService.search(query);
			Entity roleEntity = null;
			if(roleEntities != null && roleEntities.getResults().size() > 0) {
				if(roleEntities.getResults().size() > 1) {
					for(SearchResult u : roleEntities.getResults()) {
						if(roleEntity == null) roleEntity = entityService.getEntity(Long.valueOf(u.getId()));
						else entityService.removeEntity(Long.valueOf(u.getId()));
					}
				} else roleEntity = entityService.getEntity(Long.valueOf(roleEntities.getResults().get(0).getId()));
				nodeId = roleEntity.getId();
				cacheService.put("openapps.security.roles", String.valueOf(role.getXid()), roleEntity.getId());
			} else {
				roleEntity = new Entity(SystemModel.ROLE);
				if(roleEntity != null) {
					roleEntity.addProperty(SystemModel.NAME, role.getName());
					roleEntity.setXid(role.getXid());
					entityService.updateEntity(roleEntity);
					nodeId = roleEntity.getId();
					cacheService.put("openapps.security.roles", String.valueOf(role.getXid()), roleEntity.getId());
				}
			}
		}
		role.setXid(role.getXid());
		role.setName(role.getName());
		role.setId(nodeId);
		return role;
	}
	protected Group getGroup(Group group) throws Exception {
		Long nodeId = (Long)cacheService.get("openapps.security.groups", String.valueOf(group.getXid()));
		if(nodeId == null) {
			SearchRequest query = new SearchRequest(SystemModel.GROUP);
			query.addParameter("xid", String.valueOf(group.getXid()));
			SearchResponse userEntities = searchService.search(query);
			Entity groupEntity = null;
			if(userEntities != null && userEntities.getResults().size() > 0) {
				if(userEntities.getResults().size() > 1) {
					for(SearchResult u : userEntities.getResults()) {
						if(groupEntity == null) groupEntity = entityService.getEntity(Long.valueOf(u.getId()));
						else entityService.removeEntity(Long.valueOf(u.getId()));
					}
				} else groupEntity = entityService.getEntity(Long.valueOf(userEntities.getResults().get(0).getId()));
				nodeId = groupEntity.getId();
				cacheService.put("openapps.security.groups", String.valueOf(group.getXid()), groupEntity.getId());
			} else {
				groupEntity = new Entity(SystemModel.GROUP);
				if(groupEntity != null) {
					groupEntity.addProperty(SystemModel.NAME, group.getName());
					groupEntity.setXid(group.getXid());
					entityService.updateEntity(groupEntity);
					nodeId = groupEntity.getId();
					cacheService.put("openapps.security.groups", String.valueOf(group.getXid()), groupEntity.getId());
				}
			}
		}
		group.setXid(group.getXid());
		group.setName(group.getName());
		group.setId(nodeId);
		return group;
	}
	
	public void setEntityService(EntityService entityService) {
		this.entityService = entityService;
	}
	public void setSearchService(SearchService searchService) {
		this.searchService = searchService;
	}
	public void setCacheService(CacheService cacheService) {
		this.cacheService = cacheService;
	}
			
}
