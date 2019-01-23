package org.archivemanager.services.data.listener;

import org.archivemanager.services.entity.Association;
import org.archivemanager.services.entity.BasePersistenceListener;
import org.archivemanager.services.entity.Entity;
import org.springframework.stereotype.Component;

@Component
public class EntityAuditPersistenceListener extends BasePersistenceListener {

	@Override
	public void onBeforeAdd(Entity entity) {
		entity.setCreated(System.currentTimeMillis());
	}

	@Override
	public void onBeforeUpdate(Entity oldValue, Entity newValue) {
		newValue.setModified(System.currentTimeMillis());
	}

	@Override
	public void onBeforeAssociationAdd(Association assoc) {
		assoc.setCreated(System.currentTimeMillis());
	}
	
	@Override
	public void onBeforeAssociationUpdate(Association assoc) {
		assoc.setModified(System.currentTimeMillis());
	}

}
