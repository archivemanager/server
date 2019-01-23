package org.archivemanager.models.binders;

import org.archivemanager.services.entity.Entity;


public interface EntityBinder {
	
	Entity getEntity(Object model);
	<T> T getModel(Class<T> clazz, Entity entity);
	
}
