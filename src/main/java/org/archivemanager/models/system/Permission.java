package org.archivemanager.models.system;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;

public class Permission {
	private Long id;
	private String name;	
	private long source;
	private long target;
	private int level;
	
	public static final int LEVEL_READ_ONLY = 1;
	public static final int LEVEL_READ_WRITE = 2;
	
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
	public long getSource() {
		return source;
	}
	public void setSource(long source) {
		this.source = source;
	}
	public long getTarget() {
		return target;
	}
	public void setTarget(long target) {
		this.target = target;
	}
	public int getLevel() {
		return level;
	}
	public void setLevel(int level) {
		this.level = level;
	}
	
	public JsonObject toJsonObject() {
		JsonObjectBuilder builder = Json.createObjectBuilder();		
		if(getId() != null) builder.add("id", getId());
		if(getName() != null) builder.add("name", getName());
		builder.add("source", getSource());
		builder.add("target", getTarget());
		builder.add("level", getLevel());
		return builder.build();
	}
}
