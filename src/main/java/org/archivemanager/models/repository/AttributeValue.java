package org.archivemanager.models.repository;

public class AttributeValue {
	private String name;
	private String query;
	private String count;
	
	public AttributeValue() {}
	public AttributeValue(String name, String query, String count) {
		this.name = name;
		this.query = query;
		this.count = count;
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
	public String getCount() {
		return count;
	}
	public void setCount(String count) {
		this.count = count;
	}
	
}
