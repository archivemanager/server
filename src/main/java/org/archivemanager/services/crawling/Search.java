package org.archivemanager.services.crawling;

import javax.json.JsonObject;

import org.archivemanager.models.CrawlingModel;
import org.archivemanager.models.SystemModel;
import org.archivemanager.services.entity.Entity;


public class Search extends Entity {
	private static final long serialVersionUID = 4020915406392831327L;
	
	
	public Search() {
		setQName(CrawlingModel.SEARCH);
	}
	public Search(String query) {
		setQName(CrawlingModel.SEARCH);
		addProperty(SystemModel.NAME, query);
	}
	
	public Search(Entity entity) {
		setId(entity.getId());
		addProperty(SystemModel.NAME, entity.getName());
		setProperties(entity.getProperties());
		setSourceAssociations(entity.getSourceAssociations());
		setTargetAssociations(entity.getTargetAssociations());
	}
	
	public void setEntity(Entity entity) {
		setId(entity.getId());
		addProperty(SystemModel.NAME, entity.getName());
		setProperties(entity.getProperties());
		setSourceAssociations(entity.getSourceAssociations());
		setTargetAssociations(entity.getTargetAssociations());
	}
	public Search(JsonObject object) {
		setQName(CrawlingModel.SEARCH);
		if(object.containsKey("id")) setId(object.getJsonNumber("id").longValue());
		if(object.containsKey("uid")) setUid(object.getString("uid"));
		if(object.containsKey("name")) addProperty(SystemModel.NAME, object.getString("name"));
		if(object.containsKey("active")) setActive(object.getBoolean("active"));
	}
	
	public boolean isActive() {
		String loaded = getPropertyValueString(CrawlingModel.ACTIVE);
		if(loaded != null && loaded.length() > 0 && loaded.equals("true")) return true;
		return false;
	}
	public void setActive(boolean loaded) {
		try {
			addProperty(CrawlingModel.LOADED, loaded);
		} catch(Exception e) {
			System.out.println("InvalidPropertyException "+CrawlingModel.ACTIVE+" -> "+loaded);
		}
	}
}
