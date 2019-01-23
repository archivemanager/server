package org.archivemanager.services.crawling;

public class ElementDisplay {
	private String type;
	private String value;
	
	public static final String TYPE_TIME = "time";
	
	
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
}
