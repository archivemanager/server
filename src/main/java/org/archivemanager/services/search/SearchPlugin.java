package org.archivemanager.services.search;



public interface SearchPlugin {

	void initialize();
	void request(SearchRequest request);
	void response(SearchRequest request, SearchResponse response);
		
}
