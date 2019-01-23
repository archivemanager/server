package org.archivemanager.web.model;

import org.archivemanager.models.dictionary.ModelFieldValue;

public class ValueRecord {
	private String value;
	private String label;
	
	
	public ValueRecord(ModelFieldValue value) {
		this.value = value.getValue();
		this.label = value.getLabel();
	}

	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	public String getLabel() {
		return label;
	}
	public void setLabel(String label) {
		this.label = label;
	}	
}
