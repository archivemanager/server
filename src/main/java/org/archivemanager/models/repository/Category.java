package org.archivemanager.models.repository;

import java.util.ArrayList;
import java.util.List;


public class Category extends Result {
	private List<Category> categories = new ArrayList<Category>();
	private List<Item> items = new ArrayList<Item>();
	
	
	public Category() {}
	public Category(long id, long assoc, String name) {
		super(id, assoc, name);
	}
	
	public List<Category> getCategories() {
		return categories;
	}
	public void setCategories(List<Category> categories) {
		this.categories = categories;
	}
	public List<Item> getItems() {
		return items;
	}
	public void setItems(List<Item> items) {
		this.items = items;
	}	
}
