package org.archivemanager.models.repository;


public class Entry extends Result {
	private String collection;
	private String namedEntity;
	
	public Entry() {}
	public Entry(long id, long assoc, String title, String description, String contentType) {
		super(id, assoc, title, description, contentType);
	}
	
	public String getCollection() {
		return collection;
	}
	public void setCollection(String collection) {
		this.collection = collection;
	}
	public String getNamedEntity() {
		return namedEntity;
	}
	public void setNamedEntity(String namedEntity) {
		this.namedEntity = namedEntity;
	}	
}
