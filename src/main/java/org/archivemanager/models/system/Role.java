package org.archivemanager.models.system;
import java.io.StringWriter;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.JsonWriter;

import org.springframework.security.core.GrantedAuthority;


public class Role implements GrantedAuthority {
	private static final long serialVersionUID = 964479552394450265L;
	private Long id;
	private Long xid;
	private String name;
	
	
	public Role() {}
	public Role(String name) {
		this.name = name;
	}
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Long getXid() {
		return xid;
	}
	public void setXid(Long xid) {
		this.xid = xid;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	public String toJson() {
		StringWriter writer = new StringWriter();
		JsonWriter jsonWriter = Json.createWriter(writer);				
		JsonObject obj = toJsonObject();		
		jsonWriter.write(obj);
		return writer.toString();
	}
    public JsonObject toSimpleJsonObject() {
    	JsonObjectBuilder builder = Json.createObjectBuilder();		
		if(getId() != null) builder.add("id", getId());
		if(getXid() != null) builder.add("xid", getXid());
		if(getName() != null) builder.add("name", getName());
		return builder.build();
    }
	public JsonObject toJsonObject() {
		JsonObjectBuilder builder = Json.createObjectBuilder();	
		if(getId() != null) builder.add("id", getId());
		if(getXid() != null) builder.add("xid", getXid());
		builder.add("name", getName());		
		return builder.build();
	}
	public void fromJson(JsonObject object) {
		if(object.containsKey("id")) setId(object.getJsonNumber("id").longValue());
		if(object.containsKey("xid")) setXid(object.getJsonNumber("xid").longValue());
		if(object.containsKey("name")) setName(object.getString("name"));		
	}
	@Override
	public String getAuthority() {
		return name;
	}
}
