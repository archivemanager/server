package org.archivemanager.services.data.listener;

import org.archivemanager.models.RepositoryModel;
import org.archivemanager.models.SystemModel;
import org.archivemanager.services.entity.BasePersistenceListener;
import org.archivemanager.services.entity.Entity;


public class LocationPersistenceListener extends BasePersistenceListener {

	
	@Override
	public void onBeforeUpdate(Entity oldValue, Entity newValue) {
		addTitle(newValue);
	}

	@Override
	public void onBeforeAdd(Entity entity) {
		addTitle(entity);
		
	}
	protected void addTitle(Entity entity) {
		String building = entity.getPropertyValueString(RepositoryModel.BUILDING);
		String floor = entity.getPropertyValueString(RepositoryModel.FLOOR);
		String aisle = entity.getPropertyValueString(RepositoryModel.AISLE);
		String bay = entity.getPropertyValueString(RepositoryModel.BAY);
		String name = "";
		if(building != null && building.length() > 0) name += building;
		if(floor != null && floor.length() > 0) name += " Floor "+floor;
		if(aisle != null && aisle.length() > 0) name += " Aisle "+aisle;
		if(bay != null && bay.length() > 0) name += " Bay "+bay;
		entity.addProperty(SystemModel.NAME, name.trim());		
	}
	
}
