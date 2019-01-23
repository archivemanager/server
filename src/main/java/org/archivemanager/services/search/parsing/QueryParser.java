package org.archivemanager.services.search.parsing;

import org.archivemanager.services.search.SearchRequest;


public interface QueryParser {

	SearchRequest parse(SearchRequest request);
		
}
