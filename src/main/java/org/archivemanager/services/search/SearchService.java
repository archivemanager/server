package org.archivemanager.services.search;
import java.io.Serializable;


public interface SearchService extends Serializable {
	
	int count(SearchRequest request);
	SearchResponse search(SearchRequest request);
	SearchResult search(String index, long id);
	
}
