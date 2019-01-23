package org.archivemanager.models.repository;

public class DigitalObject {
	private String id;
	private String uuid;
	private String url;
	private String type;
	private String group;
	private int order;
	private String title;
	private String summary;
	private String thumbnail;
	private String mimetype;
	
	public boolean isImage() {
		if(mimetype == null) return false;
		return mimetype.startsWith("image");
	}
	public boolean isVideo() {
		if(mimetype == null) return false;
		return mimetype.startsWith("video");
	}
	public boolean isAudio() {
		if(mimetype == null) return false;
		return mimetype.startsWith("audio");
	}
	public boolean isFlash() {
		if(mimetype == null) return false;
		return mimetype.equals("application/x-shockwave-flash");
	}
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getUuid() {
		return uuid;
	}
	public void setUuid(String uuid) {
		this.uuid = uuid;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getGroup() {
		return group;
	}
	public void setGroup(String group) {
		this.group = group;
	}
	public int getOrder() {
		return order;
	}
	public void setOrder(int order) {
		this.order = order;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getSummary() {
		return summary;
	}
	public void setSummary(String summary) {
		this.summary = summary;
	}
	public String getThumbnail() {
		return thumbnail;
	}
	public void setThumbnail(String thumbnail) {
		this.thumbnail = thumbnail;
	}
	public String getMimetype() {
		return mimetype;
	}
	public void setMimetype(String mimetype) {
		this.mimetype = mimetype;
	}
	
}
