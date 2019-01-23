package org.archivemanager.services.search;

import java.util.HashMap;
import java.util.Map;

import org.archivemanager.services.search.indexing.FieldMapping;

public class IndexCreationRequest {
	private Map<String,FieldMapping> mappings = new HashMap<String,FieldMapping>();

	
	
	public Map<String, FieldMapping> getMappings() {
		return mappings;
	}
	public void setMappings(Map<String, FieldMapping> mappings) {
		this.mappings = mappings;
	}	
		
}
