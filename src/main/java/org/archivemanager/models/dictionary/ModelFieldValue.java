package org.archivemanager.models.dictionary;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;

import org.archivemanager.models.system.QName;

import com.fasterxml.jackson.annotation.JsonIgnore;


public class ModelFieldValue implements ModelObject {
	private static final long serialVersionUID = -6495065331933283259L;
	private long id;
	private String uid;
	private String name;
	private String description;
	private String category;
	private String value;
	private String label;
	private ModelField field;
	
	public ModelFieldValue(){}
	public ModelFieldValue(String name, String description) {
		this.name = name;
		this.description = description;
	}
	public ModelFieldValue(String name, String value, String description) {
		this.name = name;
		this.value = value;
		this.description = description;
	}
	public ModelFieldValue(Long id, String name, String description) {
		this.id = id;
		this.name = name;
		this.description = description;
	}
	public ModelFieldValue(String name, String value, String label, String description) {
		this.name = name;
		this.value = value;
		this.label = label;
		this.description = description;
	}
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getUid() {
		return uid;
	}
	public void setUid(String uid) {
		this.uid = uid;
	}	
	public QName getQName() {
		return null;
	}
	public void setQName(QName qname) {
		
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
		this.description = description;
	}
	@JsonIgnore
	public ModelField getField() {
		return field;
	}
	public void setField(ModelField field) {
		this.field = field;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	public String getCategory() {
		return category;
	}
	public void setCategory(String category) {
		this.category = category;
	}
	public String getLabel() {
		return label;
	}
	public void setLabel(String label) {
		this.label = label;
	}
	
	public void fromJson(JsonObject object) {
    	if(object.containsKey("id")) setId(object.getJsonNumber("id").longValue());
    	if(object.containsKey("name")) setName(object.getString("name"));
    	if(object.containsKey("value")) setValue(object.getString("value"));
    	if(object.containsKey("label")) setLabel(object.getString("label"));
    	if(object.containsKey("category")) setCategory(object.getString("category"));
	}
	public JsonObject toJsonObject() {
		JsonObjectBuilder agentBuilder = Json.createObjectBuilder();
		if(id > 0) agentBuilder.add("id", getId());
		if(name != null) agentBuilder.add("name", getName());
		if(description != null) agentBuilder.add("description", getDescription());
		if( value != null) agentBuilder.add("value", getValue());	
		if(label != null) agentBuilder.add("label", getLabel());	
		return agentBuilder.build();
	}
}
