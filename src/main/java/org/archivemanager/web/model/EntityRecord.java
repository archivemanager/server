package org.archivemanager.web.model;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.archivemanager.models.system.QName;
import org.archivemanager.services.entity.Entity;


public class EntityRecord {
	private long id; 
	private long source;
	private QName qname;
	private String update;
	private List<PropertyRecord> properties = new ArrayList<PropertyRecord>(0);
    private Map<QName, List<AssociationRecord>> associations = new HashMap<QName, List<AssociationRecord>>();
	
    
    public EntityRecord() {}
    public EntityRecord(long id, QName qname, String update) {
		this.id = id;
		this.qname = qname;
		this.update = update;
	}
    public EntityRecord(Entity entity) {
    	this.id = entity.getId();
		this.qname = entity.getQName();
	}
    
    public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}	
	public QName getQName() {
		return qname;
	}
	public void setQName(QName qname) {
		this.qname = qname;
	}	
	public String getUpdate() {
		return update;
	}
	public void setUpdate(String update) {
		this.update = update;
	}	
	public long getSource() {
		return source;
	}
	public void setSource(long source) {
		this.source = source;
	}
	public List<PropertyRecord> getProperties() {
		return properties;
	}
	public void setProperties(List<PropertyRecord> properties) {
		this.properties = properties;
	}	
	public Map<QName, List<AssociationRecord>> getAssociations() {
		return associations;
	}
	public void setAssociations(Map<QName, List<AssociationRecord>> associations) {
		this.associations = associations;
	}	
}
