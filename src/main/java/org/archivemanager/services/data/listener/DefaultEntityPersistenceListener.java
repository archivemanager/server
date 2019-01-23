package org.archivemanager.services.data.listener;

import org.archivemanager.services.dictionary.DataDictionaryService;
import org.archivemanager.services.entity.BasePersistenceListener;
import org.archivemanager.services.entity.EntityService;
import org.archivemanager.services.scheduling.SchedulingService;
import org.archivemanager.services.search.indexing.IndexingService;
import org.archivemanager.services.security.SecurityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DefaultEntityPersistenceListener extends BasePersistenceListener {
	@Autowired protected EntityService entityService;
	@Autowired protected DataDictionaryService dictionaryService;
	@Autowired protected SecurityService securityService;
	@Autowired protected SchedulingService schedulingService;
	@Autowired protected IndexingService indexingService;
	
	protected String hash = "SHA-1";
		

	public void setEntityService(EntityService entityService) {
		this.entityService = entityService;
	}
	public EntityService getEntityService() {
		return entityService;
	}
	public DataDictionaryService getDictionaryService() {
		return dictionaryService;
	}
	public void setDictionaryService(DataDictionaryService dictionaryService) {
		this.dictionaryService = dictionaryService;
	}
	public String getHash() {
		return hash;
	}
	public void setHash(String hash) {
		this.hash = hash;
	}
	public void setSecurityService(SecurityService securityService) {
		this.securityService = securityService;
	}
	public void setSchedulingService(SchedulingService schedulingService) {
		this.schedulingService = schedulingService;
	}
	public SchedulingService getSchedulingService() {
		return schedulingService;
	}
	public void setIndexingService(IndexingService indexingService) {
		this.indexingService = indexingService;
	}
	
}
