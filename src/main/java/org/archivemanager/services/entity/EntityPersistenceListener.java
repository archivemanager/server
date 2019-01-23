package org.archivemanager.services.entity;


public interface EntityPersistenceListener {

	void onIndex(Entity entity);
	void onSelect(Entity entity);
	
	void onBeforeAdd(Entity entity);
	void onAfterAdd(Entity entity);
	void onBeforeUpdate(Entity oldValue, Entity newValue);
	void onAfterUpdate(Entity oldValue, Entity newValue);
	void onBeforeDelete(Entity entity);
	void onAfterDelete(Entity entity);
	
	void onBeforeAssociationAdd(Association assoc);
	void onAfterAssociationAdd(Association assoc);
	void onBeforeAssociationUpdate(Association assoc);
	void onAfterAssociationUpdate(Association assoc);
	void onBeforeAssociationDelete(Association assoc);
	void onAfterAssociationDelete(Association assoc);
	
}
