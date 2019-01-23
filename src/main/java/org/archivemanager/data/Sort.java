package org.archivemanager.data;
import java.io.Serializable;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;


public class Sort implements Serializable {
	private static final long serialVersionUID = 3425752653677079454L;
	private String field;
	private String type;
	private String direction;
	private Object sortField;
	
	public static final String SCORE = "score";
	public static final String DOC = "doc";
	public static final String DATE = "date";
	public static final String STRING = "string";	
	public static final String INTEGER = "integer";
	public static final String FLOAT = "float";
	public static final String LONG = "long";
	public static final String DOUBLE = "double";
	
	
	public Sort() {}
	public Sort(String type, String field, String direction) {
		this.type = type;
		this.field = field;
		this.direction = direction;
	}
	
	public String getField() {
		return field;
	}
	public void setField(String field) {
		this.field = field;
	}	
	public String getDirection() {
		return direction;
	}
	public void setDirection(String direction) {
		this.direction = direction;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public Object getSortField() {
		return sortField;
	}
	public void setSortField(Object sortField) {
		this.sortField = sortField;
	}
	
	public void fromJsonObject(JsonObject object) {
		if(object.containsKey("field")) setField(object.getString("field"));
		if(object.containsKey("type")) setType(object.getJsonString("type").getString());
		if(object.containsKey("direction")) setDirection(object.getString("direction"));
	}
	public JsonObject toJsonObject() {
		JsonObjectBuilder builder = Json.createObjectBuilder();	
		builder.add("field", getField());
		builder.add("type", getType());
		builder.add("direction", direction);
		return builder.build();
	}
}
