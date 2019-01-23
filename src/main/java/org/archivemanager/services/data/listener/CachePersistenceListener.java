package org.archivemanager.services.data.listener;

import org.archivemanager.services.cache.CacheService;
import org.archivemanager.services.entity.Association;
import org.archivemanager.services.entity.BasePersistenceListener;
import org.archivemanager.services.entity.Entity;
import org.springframework.stereotype.Component;


@Component
public class CachePersistenceListener extends BasePersistenceListener {
	private CacheService cacheService;
	
	
	public CachePersistenceListener(CacheService cacheService) {
		this.cacheService = cacheService;
	}
  
	public void onAfterAdd(Entity entity) {
		cacheService.put("nodes", String.valueOf(entity.getId()), entity);
	}
  
	public void onAfterUpdate(Entity entity) {
		cacheService.remove("nodes", String.valueOf(entity.getId()));
	}
  
	public void onAfterDelete(Entity entity) {
		cacheService.remove("nodes", String.valueOf(entity.getId()));
	}
  
	public void onAfterAssociationAdd(Association association) {
		cacheService.remove("nodes", String.valueOf(association.getSource()));
		cacheService.remove("nodes", String.valueOf(association.getTarget()));
	}
  
	public void onAfterAssociationDelete(Association association) {
		cacheService.remove("nodes", String.valueOf(association.getSource()));
		cacheService.remove("nodes", String.valueOf(association.getTarget()));
	}
  
}
