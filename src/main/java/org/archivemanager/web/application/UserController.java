package org.archivemanager.web.application;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.archivemanager.data.RestResponse;
import org.archivemanager.models.SystemModel;
import org.archivemanager.models.binders.SystemModelEntityBinder;
import org.archivemanager.models.system.Role;
import org.archivemanager.models.system.User;
import org.archivemanager.services.entity.Entity;
import org.archivemanager.services.entity.EntityResultSet;
import org.archivemanager.services.entity.EntityService;
import org.archivemanager.services.entity.InvalidEntityException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;


@Controller
@RequestMapping("/app/users")
public class UserController {
	@Autowired private EntityService entityService;
	@Autowired private BCryptPasswordEncoder passwordEncoder;
	@Autowired private SystemModelEntityBinder binder;
	
	
	@RequestMapping(value = "", method = RequestMethod.GET)
	public String getUsers(final Model model, HttpServletRequest request, HttpServletResponse resp) {		
		List<Role> roles = getRoles();
		model.addAttribute("roles", roles);
		model.addAttribute("newUser", new User());
		return "user";
	}	
	
	/** Services **/
	@ResponseBody
	@RequestMapping(value="/search.json")
	public RestResponse<User> search(@RequestParam(required=false) String query, @RequestParam(required=false, defaultValue="1") int page, 
			@RequestParam(required=false, defaultValue="20") int size) throws Exception {
		RestResponse<User> data = new RestResponse<User>();		
		int start = (page*size) -  size;
		int end = page*size;
		data.setStartRow(start);
		data.setEndRow(end);
		EntityResultSet results = entityService.getEntities(SystemModel.USER, page, size);
		for(Entity result : results.getData()) {
			data.addRow(binder.getUser(result));
		}
		data.setTotal(results.getSize());
		return data;
	}	
	@ResponseBody
	@RequestMapping(value="/add.json", method=RequestMethod.POST)
	public RestResponse<User> addUser(@ModelAttribute("user") User user) throws Exception {
		RestResponse<User> data = new RestResponse<User>();
		User currentUser = getByUsername(user.getName());
		if(currentUser == null) {
			//user.setActive(true);
			user.setPassword(passwordEncoder.encode(user.getPassword()));
			entityService.updateEntity(binder.getEntity(user));
			data.setStatus(200);
		} else {
			data.setStatus(400);
			data.addMessage("username exists, please try again");
		}
		return data;
	}
	
	@ResponseBody
	@RequestMapping(value="/save.json", method=RequestMethod.POST)
	public RestResponse<User> saveUser(@ModelAttribute("user") User user) throws Exception {
		RestResponse<User> data = new RestResponse<User>();	
		User currentUser = binder.getUser(entityService.getEntity(user.getId()));
		if(!currentUser.getName().equals(user.getName())) {
			//username mismatch, we need to make sure the new username isn't taken
			User existingUser = getByUsername(user.getName());
			if(existingUser != null) {
				data.setStatus(400);
				data.addMessage("username exists, please try again");
				return data;
			}
		}
		currentUser.setName(user.getName());
		currentUser.setEmail(user.getEmail());
		currentUser.setActive(user.isActive());
		entityService.updateEntity(binder.getEntity(currentUser));
		data.setStatus(200);
		return data;
	}
	@ResponseBody
	@RequestMapping(value="/password/save.json", method=RequestMethod.POST)
	public RestResponse<User> savePassword(@RequestParam(required=false) Long id, @RequestParam String password) throws Exception {
		RestResponse<User> data = new RestResponse<User>();	
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		Entity entity = id == null ? entityService.getEntity(SystemModel.USER, SystemModel.EMAIL, auth.getName()) : entityService.getEntity(id);
		entity.addProperty(SystemModel.PASSWORD, passwordEncoder.encode(password));
		entityService.updateEntity(entity);
		data.setStatus(200);
		return data;
	}
	@ResponseBody
	@RequestMapping(value="/remove.json", method=RequestMethod.POST)
	public RestResponse<User> removeUser(@RequestParam long id) throws Exception {
		RestResponse<User> data = new RestResponse<User>();
		Entity entity = entityService.getEntity(id);
		entityService.removeEntity(entity.getId());
		return data;
	}
	
	protected List<Role> getRoles() {
		List<Role> roles = new ArrayList<Role>();
		EntityResultSet entities = entityService.getEntities(SystemModel.ROLE, 0, 0);
		for(Entity result : entities.getData()) {
			roles.add(binder.getRole(result));
		}
		return roles;
	}
	protected User getByUsername(String username) throws InvalidEntityException {
		User user = null;
		Entity entity = entityService.getEntity(SystemModel.USER, SystemModel.USERNAME, username);
		if(entity != null)
			user = binder.getUser(entity);
		return user;
	}
	protected User getByEmail(String email) throws InvalidEntityException {
		User user = null;
		Entity entity = entityService.getEntity(SystemModel.USER, SystemModel.EMAIL, email);
		if(entity != null)
			user = binder.getUser(entity);
		return user;
	}
}
