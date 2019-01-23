package org.archivemanager.models.dictionary;

import java.util.ArrayList;
import java.util.List;

public class ModelList {
	private String name;
	private List<ModelFieldValue> values = new ArrayList<ModelFieldValue>();
	
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public List<ModelFieldValue> getValues() {
		return values;
	}
	public void setValues(List<ModelFieldValue> values) {
		this.values = values;
	}	
}
