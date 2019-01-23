package org.archivemanager.web.model;
import java.util.ArrayList;
import java.util.List;

import org.archivemanager.models.system.QName;


public class AssociationRecord {
	private Long id;
	private QName qname;
	private String name;
	private String view;
	private Long source;
    private Long target;
    private QName targetName;
    private List<PropertyRecord> properties = new ArrayList<PropertyRecord>(0);
    private List<PropertyRecord> attributes = new ArrayList<PropertyRecord>(0);
    
    
    public AssociationRecord() {}
    public AssociationRecord(Long id, QName qname, Long source, Long target) {
    	this.id = id;
    	this.qname = qname;
    	this.source = source;
    	this.target = target;
    }
    public AssociationRecord(QName qname, Long source, Long target) {
    	this.qname = qname;
    	this.source = source;
    	this.target = target;
    }
		
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Long getSource() {
		return source;
	}
	public void setSource(Long source) {
		this.source = source;
	}
	public Long getTarget() {
		return target;
	}
	public void setTarget(Long target) {
		this.target = target;
	}
	public QName getTargetName() {
		return targetName;
	}
	public void setTargetName(QName targetName) {
		this.targetName = targetName;
	}	
	public QName getQName() {
		return qname;
	}
	public void setQName(QName qname) {
		this.qname = qname;
	}
	public String getView() {
		return view;
	}
	public void setView(String view) {
		this.view = view;
	}
	public List<PropertyRecord> getProperties() {
		return properties;
	}
	public void setProperties(List<PropertyRecord> properties) {
		this.properties = properties;
	}
	public List<PropertyRecord> getAttributes() {
		return attributes;
	}
	public void setAttributes(List<PropertyRecord> attributes) {
		this.attributes = attributes;
	}
	
}
