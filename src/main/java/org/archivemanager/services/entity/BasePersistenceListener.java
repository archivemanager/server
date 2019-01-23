package org.archivemanager.services.entity;

public class BasePersistenceListener implements EntityPersistenceListener {

	
	@Override
	public void onBeforeAdd(Entity entity) {}

	@Override
	public void onAfterAdd(Entity entity) {}

	@Override
	public void onBeforeUpdate(Entity oldValue, Entity newValue) {}

	@Override
	public void onAfterUpdate(Entity oldValue, Entity newValue) {}

	@Override
	public void onBeforeDelete(Entity entity) {}

	@Override
	public void onAfterDelete(Entity entity) {}

	@Override
	public void onBeforeAssociationAdd(Association assoc) {}

	@Override
	public void onAfterAssociationAdd(Association assoc) {}

	@Override
	public void onBeforeAssociationUpdate(Association assoc) {}

	@Override
	public void onAfterAssociationUpdate(Association assoc) {}

	@Override
	public void onBeforeAssociationDelete(Association assoc) {}

	@Override
	public void onAfterAssociationDelete(Association assoc) {}

	@Override
	public void onSelect(Entity entity) {}

	@Override
	public void onIndex(Entity entity) {}
	
}
