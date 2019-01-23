package org.archivemanager.services.entity;

import java.util.ArrayList;
import java.util.List;

public class EntityResultSet {
	private List<Entity> data = new ArrayList<Entity>();
	private int size;
	
	
	public List<Entity> getData() {
		return data;
	}
	public void setData(List<Entity> data) {
		this.data = data;
	}
	public int getSize() {
		return size;
	}
	public void setSize(int size) {
		this.size = size;
	}
	
}
