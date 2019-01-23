package org.archivemanager.services.content;

public class FileSettings {
	private String uid;
	private String name;
	private String path;
	private long size;
	private boolean deleted = true; //we start it deleted so that we can iterate through the files and mark the existing
	
	
	public FileSettings() {}
	public FileSettings(String uid, String name, String path, long size) {
		this.uid = uid;
		this.name = name;
		this.path = path;
		this.size = size;
	}
	
	public String getUid() {
		return uid;
	}
	public void setUid(String uid) {
		this.uid = uid;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getPath() {
		return path;
	}
	public void setPath(String path) {
		this.path = path;
	}
	public long getSize() {
		return size;
	}
	public void setSize(long size) {
		this.size = size;
	}
	public boolean isDeleted() {
		return deleted;
	}
	public void setDeleted(boolean deleted) {
		this.deleted = deleted;
	}
	
}
