package org.archivemanager.services.data.listener;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import org.archivemanager.models.RepositoryModel;
import org.archivemanager.services.entity.Association;
import org.archivemanager.services.entity.BasePersistenceListener;
import org.archivemanager.services.entity.Entity;
import org.archivemanager.services.entity.EntityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


@Component
public class RepositoryPersistenceListener extends BasePersistenceListener {
	private final static Logger log = Logger.getLogger(RepositoryPersistenceListener.class.getName());
	@Autowired private EntityService entityService;
	
	
	public RepositoryPersistenceListener(EntityService entityService) {
		super();
		this.entityService = entityService;
	}

	@Override
	public void onAfterUpdate(Entity oldValue, Entity entity) {
		if(entity.getQName().equals(RepositoryModel.COLLECTION) || entity.getQName().equals(RepositoryModel.CATEGORY)) {
			List<Entity> children = getChildren(entity);
			entityService.index(children);
			log.info("found "+children.size()+" children to reindex after entity update id="+entity.getId()+", qname="+entity.getQName().toString());
		}
	}
	
	protected List<Entity> getChildren(Entity entity) {
		List<Entity> path = new ArrayList<Entity>();
		path.add(entity);
		Association association = entity.getSourceAssociation(RepositoryModel.CATEGORIES,RepositoryModel.ITEMS);
		if(association != null) {
			Entity node = entityService.getEntity(association.getTarget());
			while(node != null) {
				path.add(node);
				association = node.getSourceAssociation(RepositoryModel.CATEGORIES,RepositoryModel.ITEMS);
				if(association == null) break;
				node = entityService.getEntity(association.getTarget());
			}
		}
		return path;
	}
}
