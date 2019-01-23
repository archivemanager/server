package org.archivemanager.services.entity;

import org.archivemanager.services.search.indexing.IndexAssociation;
import org.archivemanager.services.search.indexing.IndexEntity;


public interface EntityIndexer {

	void index(Entity entity, IndexEntity data);
	void index(Association association, IndexAssociation data);
	
}
