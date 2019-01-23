package org.archivemanager.services.data.listener;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import org.archivemanager.models.SystemModel;
import org.archivemanager.models.dictionary.Model;
import org.archivemanager.models.system.QName;
import org.archivemanager.services.dictionary.DataDictionaryService;
import org.archivemanager.services.entity.Association;
import org.archivemanager.services.entity.BasePersistenceListener;
import org.archivemanager.services.entity.Entity;
import org.archivemanager.services.entity.EntityIndexer;
import org.archivemanager.services.search.indexing.IndexAssociation;
import org.archivemanager.services.search.indexing.IndexEntity;
import org.archivemanager.services.search.indexing.IndexField;
import org.archivemanager.services.search.indexing.IndexingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


@Component
public class IndexPersistenceListener extends BasePersistenceListener {
	private final static Logger log = Logger.getLogger(IndexPersistenceListener.class.getName());
	@Autowired private DataDictionaryService dictionaryService;
	@Autowired private IndexingService indexingService;
		
	@Autowired private EntityIndexer[] indexers;
	
	
	public IndexPersistenceListener(DataDictionaryService dictionaryService, IndexingService indexingService) {
		this.dictionaryService = dictionaryService;
		this.indexingService = indexingService;
	}
  
	@Override
	public void onIndex(Entity entity) {
		Model sourceModel = dictionaryService.getModel(entity.getQName());
		if(sourceModel.isSearchable()) {
			IndexEntity indexEntity = new IndexEntity(entity.getId());
			List<QName> qnames = dictionaryService.getQNames(entity.getQName());
			indexEntity.setQnames(qnames);
			for(EntityIndexer indexer : indexers) {
				indexer.index(entity, indexEntity);
			}			
			indexingService.indexEntity(indexEntity);
			log.info("index entity update id:"+entity.getId()+", qname:"+entity.getQName()+", name:"+entity.getName());
			for(String msg : indexEntity.getMessages()) {
				log.info("--message id:"+entity.getId()+", message:"+msg);
			}
			List<IndexAssociation> associations = new ArrayList<IndexAssociation>();
			for(Association association : entity.getSourceAssociations()) {
				IndexAssociation assoc = new IndexAssociation(association.getId(), association.getQName(), association.getSource(), association.getTarget());
				for(EntityIndexer indexer : indexers) {
					indexer.index(association, assoc);
				}
				associations.add(assoc);
			}
			if(associations.size() > 0) {
				indexingService.indexAssociations(associations);
				for(IndexAssociation association : associations) {
					log.info("index association update id:"+association.getId()+", qname:"+association.getQName()+", source:"+association.getSource()+", target:"+association.getTarget());
					for(String msg : association.getMessages()) {
						log.info("--message id:"+association.getId()+", message:"+msg);
					}
				}
			}
		}
	}
	public void onAfterAdd(Entity entity) {
		onIndex(entity);
	}
  
	public void onAfterUpdate(Entity oldValue, Entity entity) {
		onIndex(entity);
	}
  
	public void onAfterDelete(Entity entity) {
		Model sourceModel = dictionaryService.getModel(entity.getQName());
		if(sourceModel.isSearchable()) {
			indexingService.removeIndexEntity(entity.getId());
		}
	}
  
	public void onAfterAssociationAdd(Association association) {
		IndexAssociation assoc = new IndexAssociation(association.getId(), association.getQName(), association.getSource(), association.getTarget());
		if(association.getSourceName() != null) 
			assoc.getFields().add(new IndexField(SystemModel.SOURCE_NAME.toString(), association.getSourceName().toString()));
		if(association.getTargetName() != null)
			assoc.getFields().add(new IndexField(SystemModel.TARGET_NAME.toString(), association.getTargetName().toString()));
		indexingService.indexAssociation(assoc);
		log.info("index association update id:"+association.getId()+", qname:"+association.getQName()+", source="+assoc.getSource()+", target="+assoc.getTarget());
		for(String msg : assoc.getMessages()) {
			log.info("--message id:"+association.getId()+", message:"+msg);
		}		
	}
  
	public void onAfterAssociationDelete(Association association) {
		indexingService.removeIndexAssociation(association.getId());
	}	
}