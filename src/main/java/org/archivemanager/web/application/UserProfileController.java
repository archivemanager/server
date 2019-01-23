package org.archivemanager.web.application;
import java.util.ArrayList;
import java.util.List;

import org.archivemanager.data.RestResponse;
import org.archivemanager.models.SystemModel;
import org.archivemanager.models.binders.SystemModelEntityBinder;
import org.archivemanager.models.system.Role;
import org.archivemanager.models.system.User;
import org.archivemanager.services.entity.Entity;
import org.archivemanager.services.entity.EntityResultSet;
import org.archivemanager.services.entity.EntityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;


@Controller
@RequestMapping("/app/user/profile")
public class UserProfileController {
	@Autowired private EntityService entityService;
	@Autowired private SystemModelEntityBinder binder;
	
	
	@RequestMapping(value = "", method = RequestMethod.GET)
    public String registration(Model model) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		User user = getByEmail((String)auth.getPrincipal());
        model.addAttribute("user", user);
        return "profile";
    }

	@ResponseBody
	@RequestMapping(value="/save.json", method=RequestMethod.POST)
	public RestResponse<User> saveUserProfile(@ModelAttribute("user") User user) throws Exception {
		RestResponse<User> data = new RestResponse<User>();
		Entity entity = entityService.getEntity(user.getId());
		entity.addProperty(SystemModel.NAME, user.getName());
		entity.addProperty(SystemModel.EMAIL, user.getEmail());
		entityService.updateEntity(entity);
		data.setStatus(200);
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
	protected User getByEmail(String username) {
		User user = null;
		Entity entity = entityService.getEntity(SystemModel.USER, SystemModel.EMAIL, username);
		if(entity != null)
			user = binder.getUser(entity);
		return user;
	}
}
