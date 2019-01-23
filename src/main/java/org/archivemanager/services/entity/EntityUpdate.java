package org.archivemanager.services.entity;

import java.util.List;

import org.archivemanager.models.system.QName;

public class EntityUpdate {
	private Long id;
	private Long source;
	private Long target;
	private QName qname;
	private List<Property> properties;
	private Entity entity;
	private int status;
	
	
	public EntityUpdate() {}
	public EntityUpdate(Entity entity) {
		this.entity = entity;
	}
	public EntityUpdate(Long source, Long target, QName qname, List<Property> properties, Entity entity) {
		this.source = source;
		this.target = target;
		this.qname = qname;
		this.properties = properties;
		this.entity = entity;
	}
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
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
	public QName getQname() {
		return qname;
	}
	public void setQname(QName qname) {
		this.qname = qname;
	}
	public List<Property> getProperties() {
		return properties;
	}
	public void setProperties(List<Property> properties) {
		this.properties = properties;
	}
	public Entity getEntity() {
		return entity;
	}
	public void setEntity(Entity entity) {
		this.entity = entity;
	}
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	
}
