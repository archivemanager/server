package org.archivemanager.services.entity;

import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public abstract class ImportProcessor implements Serializable {
	private static final long serialVersionUID = -2811422016810080473L;
	private String id;
	private String name;
	private Map<String,Entity> entities = new HashMap<String,Entity>();
	private List<Association> associations = new ArrayList<Association>();
	
	private boolean processed;
		
	public abstract void process(InputStream stream, Map<String, Object> properties) throws Exception;
	
	public Entity getEntityById(String id) {
		return entities.get(id);
	}
	public Map<String, Entity> getEntities() {
		return entities;
	}
	public void setEntities(Map<String, Entity> entities) {
		this.entities = entities;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Entity getRoot() {
		return null;
	}
	public boolean isProcessed() {
		return processed;
	}
	public void setProcessed(boolean processed) {
		this.processed = processed;
	}
	public List<Association> getAssociations() {
		return associations;
	}
	public void setAssociations(List<Association> associations) {
		this.associations = associations;
	}
	
}
