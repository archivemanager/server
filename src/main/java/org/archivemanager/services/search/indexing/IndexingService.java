package org.archivemanager.services.search.indexing;
import java.util.List;


public interface IndexingService {

	void indexEntity(IndexEntity entity);
	void indexEntities(List<IndexEntity> entities);
	
	void indexAssociation(IndexAssociation association);
	void indexAssociations(List<IndexAssociation> associations);
	
	void removeIndexEntity(Long id);
	void removeIndexAssociation(Long id);
	
}
