package org.archivemanager.models.repository;


public class Audio extends Item {
	private String rendition;
	private String avatar;
	
	
	public Audio(long id, long assoc, String title, String description, String contentType) {
		super(id, assoc, title, description, contentType);
	}
	
	public String getRendition() {
		return rendition;
	}
	public void setRendition(String rendition) {
		this.rendition = rendition;
	}
	public String getAvatar() {
		return avatar;
	}
	public void setAvatar(String avatar) {
		this.avatar = avatar;
	}
		
}
