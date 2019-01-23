package org.archivemanager.services.search;

import org.archivemanager.models.RepositoryModel;
import org.archivemanager.models.system.QName;
import org.archivemanager.services.entity.Association;
import org.archivemanager.services.entity.Entity;
import org.archivemanager.services.entity.EntityService;
import org.archivemanager.services.entity.Property;


public class CollectionSearchProvider implements SearchPlugin {
	private EntityService entityService;
	
	
	@Override
	public void initialize() {
		
	}
	
	@Override
	public void request(SearchRequest request) {
		if(request.getQName().equals(RepositoryModel.COLLECTION) && !request.getContext().equals("archive")) {
			if(request.getQuery() != null && request.getQuery().length() == 1) {
				request.setQuery("name_e:"+request.getQuery()+"*");
			}
			if(request.getContext() != null && request.getContext().length() > 0) {
				Clause clause = new Clause();
				clause.setOperator(Clause.OPERATOR_AND);
				clause.addProperty(new Property(RepositoryModel.CODE, request.getContext()));				
				request.addClause(clause);
			}
		}
	}
	@Override
	public void response(SearchRequest request, SearchResponse response) {
		for(SearchResult result : response.getResults()) {
			try {
				Association parent_assoc = entityService.getEntity(result.getId()).getTargetAssociation(RepositoryModel.CATEGORIES);
				if(parent_assoc == null) parent_assoc = entityService.getEntity(result.getId()).getTargetAssociation(RepositoryModel.ITEMS);
				if(parent_assoc != null && parent_assoc.getSource() != null) {
					try {
						Entity parent = entityService.getEntity(parent_assoc.getSource());
						while(parent != null) {
							QName parent_qname = parent.getQName();
							if(parent_qname.equals(RepositoryModel.COLLECTION)) {
								addCollection(result, parent);
								break;
							}
							parent_assoc = parent.getTargetAssociation(RepositoryModel.CATEGORIES);
							if(parent_assoc == null) parent_assoc = parent.getTargetAssociation(RepositoryModel.ITEMS);
							if(parent_assoc != null && parent_assoc.getSource() != null) {
								parent = entityService.getEntity(parent_assoc.getSource());
							} else parent = null;
						}
					} catch(Exception e) {
						e.printStackTrace();
					}
				}
			} catch(Exception e) {
				e.printStackTrace();
			}
		}
	}
	protected void addCollection(SearchResult result, Entity collection) {
		result.getData().put("collectionId", String.valueOf(collection.getId()));
		result.getData().put("collectionName", collection.getName());
	}
	
	public EntityService getEntityService() {
		return entityService;
	}
	public void setEntityService(EntityService entityService) {
		this.entityService = entityService;
	}
	
}
