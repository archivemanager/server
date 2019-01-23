package org.archivemanager.models.repository;

import java.util.ArrayList;
import java.util.List;

public class Attribute {
	private String name;
	private List<AttributeValue> values = new ArrayList<AttributeValue>();
	private String count;
	private boolean display = true;
	
	public Attribute() {}
	public Attribute(String name) {
		this.name = name;
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public List<AttributeValue> getValues() {
		return values;
	}
	public void setValues(List<AttributeValue> values) {
		this.values = values;
	}
	public String getCount() {
		return count;
	}
	public void setCount(String count) {
		this.count = count;
	}
	public boolean isDisplay() {
		return display;
	}
	public void setDisplay(boolean display) {
		this.display = display;
	}
	
}
