package org.archivemanager.web.model;

import org.archivemanager.models.system.QName;

public class PropertyRecord {
	private QName qname;
	private String type;
	private String label;
	private Object value;
	
	
	public PropertyRecord() {}
	public PropertyRecord(QName qname, String type, String label, Object value) {
		super();
		this.type = type;
		this.qname = qname;
		this.label = label;
		this.value = value;
	}
	
	public QName getQName() {
		return qname;
	}
	public void setQName(QName qname) {
		this.qname = qname;
	}	
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public Object getValue() {
		return value;
	}
	public void setValue(Object value) {
		this.value = value;
	}
	public String getLabel() {
		return label;
	}
	public void setLabel(String label) {
		this.label = label;
	}	
	
}
