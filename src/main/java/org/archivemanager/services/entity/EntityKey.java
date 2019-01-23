package org.archivemanager.services.entity;

import org.archivemanager.models.system.QName;

public class EntityKey {
	private long id;
	private QName qname;
	private String name;
	
	public EntityKey() {}
	public EntityKey(long id, QName qname, String name) {
		this.id = id;
		this.qname = qname;
		this.name = name;
	}
	
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public QName getQname() {
		return qname;
	}
	public void setQname(QName qname) {
		this.qname = qname;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}		
}
