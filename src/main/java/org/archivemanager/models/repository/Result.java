package org.archivemanager.models.repository;


public class Result {
	private Long id;
	private Long assoc;
	private Long user;
	private String username;
	private String name = "";
	private String description = "";
	private String contentType = "";
		
	
	public Result() {}
	public Result(Long id, Long assoc, String name) {
		this.id = id;
		this.assoc = assoc;
		this.name = name;
	}
	public Result(Long id, Long assoc, String name, String description, String contentType) {
		this.id = id;
		this.assoc = assoc;
		this.name = name;
		if(description != null) this.description = description;
		if(contentType != null) this.contentType = contentType;
	}
			
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}	
	public long getAssoc() {
		return assoc;
	}
	public void setAssoc(long assoc) {
		this.assoc = assoc;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		if(description != null && !description.equals("null")) 
			this.description = description;
	}
	public String getContentType() {
		return contentType;
	}
	public void setContentType(String contentType) {
		this.contentType = contentType;
	}	
	public Long getUser() {
		return user;
	}
	public void setUser(Long user) {
		this.user = user;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
		
}
