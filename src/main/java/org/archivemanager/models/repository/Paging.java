package org.archivemanager.models.repository;

public class Paging {
	private String name;
	private String query;
	private int page;
	private int size;
	private String sort = "";
	
	public Paging() {}
	public Paging(String name, String query) {
		this.name = name;
		this.query = query;
	}
	public Paging(String name, String query, int page, int size) {
		this.name = name;
		this.query = query;
		this.page = page;
		this.size = size;
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
	public int getPage() {
		return page;
	}
	public void setPage(int page) {
		this.page = page;
	}
	public int getSize() {
		return size;
	}
	public void setSize(int size) {
		this.size = size;
	}
	public String getSort() {
		return sort;
	}
	public void setSort(String sort) {
		this.sort = sort;
	}
		
}
