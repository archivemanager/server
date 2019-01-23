package org.archivemanager.services.search;

import org.archivemanager.models.RepositoryModel;
import org.archivemanager.models.SystemModel;
import org.archivemanager.services.entity.EntityService;
import org.archivemanager.services.entity.Property;


public class ItemSearchProvider  implements SearchPlugin {
	protected EntityService entityService;
	protected SearchService searchService;
	
	
	@Override
	public void initialize() {
		
	}

	@Override
	public void request(SearchRequest request) {
		if(request.getContext() != null && request.getContext().length() > 0 && request.getQName().equals(RepositoryModel.ITEM) && !request.getContext().equals("archive")) {			
			SearchRequest sQuery = new SearchRequest(RepositoryModel.COLLECTION, "code:"+request.getContext());
			sQuery.setStartRow(0);
			sQuery.setEndRow(100);
			
			SearchResponse collectionResults = searchService.search(sQuery);
			if(collectionResults.getResults().size() == 1) {
				SearchResult collection = collectionResults.getResults().get(0);
				request.addParameter("path", String.valueOf(collection.getId()));
			} else {
				Clause clause = new Clause();
				clause.setOperator(Clause.OPERATOR_OR);
				for(SearchResult collection : collectionResults.getResults()) {
					clause.addProperty(new Property(SystemModel.PATH, String.valueOf(collection.getId())));
				}
				request.addClause(clause);
			}
		}
	}
	protected void search() {
		/*
		 * EntityQuery eQuery = new EntityQuery(RepositoryModel.COLLECTION, RepositoryModel.CODE.toString()+":"+request.getContext());		
			eQuery.setType(EntityQuery.TYPE_LUCENE);
			eQuery.setStartRow(0);
			eQuery.setEndRow(10);
			EntityResultSet results = getEntityService().search(eQuery);
			
			if(results.getResults().size() == 1) {
				Entity collection = results.getResults().get(0);
				request.addParameter("path", String.valueOf(collection.getId()));
			} else {
				Clause clause = new Clause();
				clause.setOperator(Clause.OPERATOR_OR);
				for(Entity collection : results.getResults()) {
					clause.addParamater(new Parameter("path", String.valueOf(collection.getId())));
				}
				request.addClause(clause);
			}
			
		*/
	}
	@Override
	public void response(SearchRequest request, SearchResponse response) {
		
	}
	
	public EntityService getEntityService() {
		return entityService;
	}
	public void setEntityService(EntityService entityService) {
		this.entityService = entityService;
	}
	public SearchService getSearchService() {
		return searchService;
	}
	public void setSearchService(SearchService searchService) {
		this.searchService = searchService;
	}
	
}
