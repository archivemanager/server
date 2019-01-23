package org.archivemanager.models.repository;

public class Breadcrumb {
	private String name;
	private String query;
	
	
	public Breadcrumb() {}
	public Breadcrumb(String name, String query) {
		this.name = name;
		this.query = query;
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getQuery() {
		return query;
	}
	public void setQuery(String query) {
		this.query = query;
	}
		
}
