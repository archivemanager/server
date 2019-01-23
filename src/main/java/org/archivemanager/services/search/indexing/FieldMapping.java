package org.archivemanager.services.search.indexing;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;


@JsonSerialize
public class FieldMapping {
	private Map<String,Object> properties = new HashMap<String,Object>();
	
	
	public void addField(String name, String type, boolean fielddata, boolean store) {
		Map<String,Object> field = new HashMap<String,Object>();
		field.put("type", type);
		field.put("store", store);
		if(fielddata) field.put("fielddata", true);
		properties.put(name, field);
	}
	
	public Map<String, Object> getProperties() {
		return properties;
	}
	public void setProperties(Map<String, Object> properties) {
		this.properties = properties;
	}	
	
}
