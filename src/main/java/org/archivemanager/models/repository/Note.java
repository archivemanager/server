package org.archivemanager.models.repository;

public class Note extends Result {	
	private String type;
	private String content;
	
	public Note() {}
	public Note(long id, long assoc, String type, String content) {
		super(id, assoc, type);
		this.type = type;
		this.content = content;
	}
		
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	
}
