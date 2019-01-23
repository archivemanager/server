package org.archivemanager.models.binders;

import org.archivemanager.models.SystemModel;
import org.archivemanager.models.system.Group;
import org.archivemanager.models.system.Role;
import org.archivemanager.models.system.User;
import org.archivemanager.services.entity.Association;
import org.archivemanager.services.entity.Entity;
import org.archivemanager.services.entity.EntityService;
import org.springframework.stereotype.Component;

@Component
public class SystemModelEntityBinder implements EntityBinder {
	private EntityService entityService;
	
	
	public SystemModelEntityBinder(EntityService entityService) {
		this.entityService = entityService;
	}
	
	@Override
	public Entity getEntity(Object model) {
		if(model instanceof User)
			return getEntity((User)model);
		if(model instanceof Role)
			return getEntity((Role)model);
		if(model instanceof Group)
			return getEntity((Group)model);
		return null;
	}

	@Override
	public <T> T getModel(Class<T> clazz, Entity entity) {
		if(clazz.equals(User.class))
			return clazz.cast(getUser(entity));
		if(clazz.equals(Role.class))
			return clazz.cast(getRole(entity));
		if(clazz.equals(Group.class))
			return clazz.cast(getGroup(entity));
		return null;
	}
	
	public User getUser(Entity entity) {
		User user = new User();
		user.setId(entity.getId());
		user.setXid(entity.getXid());
		user.setName(entity.getName());
		user.setActive(entity.getPropertyValueBoolean(SystemModel.ACTIVE));
		user.setName(entity.getPropertyValueString(SystemModel.NAME));
		user.setEmail(entity.getPropertyValueString(SystemModel.EMAIL));
		user.setPassword(entity.getPropertyValueString(SystemModel.PASSWORD));
		if(entity.getSourceAssociations().size() > 0) {
			for(int i=0; i < entity.getSourceAssociations().size(); i++) {
				Association association = entity.getSourceAssociations().get(i);
				Entity targetEntity = entityService.getEntity(association.getTarget());
				try {
					if(targetEntity == null) targetEntity = entityService.getEntity(association.getTarget());
					if(association.getQName().getLocalName().equals("roles")) {
						Role role = getRole(targetEntity);
						user.getRoles().add(role);
					} else if(association.getQName().getLocalName().equals("groups")) {
						Group group = getGroup(targetEntity);
						user.getGroups().add(group);
					}					
				} catch(Exception e) {
					e.printStackTrace();
				}
				
			}
		}
		return user;
	}
	public Entity getEntity(User user) {
		Entity entity =  (user.getId() != null) ? new Entity(user.getId(), SystemModel.USER) : new Entity(SystemModel.USER);			
		if(user.getXid() != null) entity.setXid(user.getXid());
		entity.addProperty(SystemModel.NAME, user.getName());
		entity.addProperty(SystemModel.EMAIL, user.getEmail());
		entity.addProperty(SystemModel.PASSWORD, user.getPassword());
		try {
			for(Role role : user.getRoles()) {
				entity.getAssociations().add(new Association(SystemModel.ROLES, entity, getEntity(role)));
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
		return entity;
	}
	
	public Role getRole(Entity entity) {
		Role role = new Role();
		role.setId(entity.getId());
		role.setName(entity.getName());
		role.setXid(entity.getXid());
		return role;
	}
	public Entity getEntity(Role role) {
		Entity entity =  new Entity(role.getId(), SystemModel.ROLE);			
		entity.setXid(role.getXid());
		entity.addProperty(SystemModel.NAME, role.getName());		
		return entity;
	}
	
	public Group getGroup(Entity entity) {
		Group group = new Group();
		group.setId(entity.getId());
		group.setName(entity.getName());
		group.setXid(entity.getXid());
		return group;
	}
	public Entity getEntity(Group group) {
		Entity entity =  new Entity(group.getId(), SystemModel.GROUP);			
		entity.setXid(group.getXid());
		entity.addProperty(SystemModel.NAME, group.getName());		
		return entity;
	}
}
