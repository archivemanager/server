package org.archivemanager.models.repository;
import java.util.ArrayList;
import java.util.List;


public class Subject extends Result {
	private String type;
	private String source;
	private List<Collection> collections = new ArrayList<Collection>();
	
	
	public Subject() {}
	public Subject(long id, long assoc, String title, String description, String contentType) {
		super(id, assoc, title, description, contentType);
	}	
	
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getSource() {
		return source;
	}
	public void setSource(String source) {
		this.source = source;
	}
	public List<Collection> getCollections() {
		return collections;
	}
	public void setCollections(List<Collection> collections) {
		this.collections = collections;
	}
	
}
