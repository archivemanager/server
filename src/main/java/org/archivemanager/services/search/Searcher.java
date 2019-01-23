package org.archivemanager.services.search;

import java.util.List;


public interface Searcher {

	//void reload(Entity entity);
	void search(SearchRequest request, SearchResponse response);
	
	List<SearchPlugin> getPlugins();
	
}
