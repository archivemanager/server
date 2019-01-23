package org.archivemanager.services.data;

import java.util.List;

import javax.annotation.PostConstruct;

import org.archivemanager.models.RepositoryModel;
import org.archivemanager.models.SystemModel;
import org.archivemanager.services.entity.Association;
import org.archivemanager.services.entity.Entity;
import org.archivemanager.services.entity.EntityResultSet;
import org.archivemanager.services.entity.EntityService;
import org.springframework.beans.factory.annotation.Autowired;


//@Component
public class OrphanedCollectionProcessor {
	@Autowired private EntityService entityService;
	
	
	@PostConstruct
	public void process() {
		Entity repository = entityService.getEntity(RepositoryModel.REPOSITORY, SystemModel.NAME, "Unclassified");
		if(repository == null) {
			repository = new Entity(RepositoryModel.REPOSITORY);
			repository.addProperty(SystemModel.NAME, "Unclassified");
			entityService.updateEntity(repository);
		}
		EntityResultSet results = entityService.getEntities(RepositoryModel.COLLECTION, 0, 5000);
		for(Entity collection : results.getData()) {
			List<Association> associations = collection.getTargetAssociations(RepositoryModel.COLLECTIONS);
			if(associations == null || associations.size() == 0) {
				Association association = new Association(RepositoryModel.COLLECTIONS, repository, collection);
				entityService.updateAssociation(association);
				System.out.println("orphaned collection "+collection.getName()+" processed.");
			}
		}
	}
}
