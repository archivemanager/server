package org.archivemanager.services.entity;

public class EntityRemove {
	private long id;
	private int status;
	
	
	public EntityRemove() {}
	public EntityRemove(long id) {
		this.id = id;
	}
	
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
}
