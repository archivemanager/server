package org.archivemanager.models.repository;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.archivemanager.models.SystemModel;

import com.fasterxml.jackson.annotation.JsonIgnore;


public class Item extends Result {
	private String language;
	private String dateExpression;
	private String container;
	private String summary;
	private String authors;
	private String collectionId;
	private String collectionName;
	private String collectionUrl;
	private String nativeContent;
	private String abstractNote = "";
	private List<Note> notes = new ArrayList<Note>();
	private List<NamedEntity> people = new ArrayList<NamedEntity>();
	private List<NamedEntity> corporations = new ArrayList<NamedEntity>();
	private List<Subject> subjects = new ArrayList<Subject>();	
	private List<PathNode> path = new ArrayList<PathNode>();
	private List<DigitalObject> digitalObjects = new ArrayList<DigitalObject>();
	private List<WebLink> weblinks = new ArrayList<WebLink>();
	private Map<String,Object> propertiesMap = new TreeMap<>();
	
		
	public Item() {
		super();
	}
	public Item(Long id, Long assoc, String name, String description, String contentType) {
		super(id, assoc, name, description, contentType);
		// TODO Auto-generated constructor stub
	}

	public Item(Long id, Long assoc, String name) {
		super(id, assoc, name);
		// TODO Auto-generated constructor stub
	}

	public Item(long id, long assoc, String title, String description, String contentType) {
		super(id, assoc, title, description, contentType);
	}
	@JsonIgnore
	public String getThumbnailUid() {
		for(DigitalObject obj : digitalObjects) {
			if(obj.getType().equals("thumbnail"))
				return obj.getUuid();
		}
		return null;
	}
	@JsonIgnore
	public String getTitle() {
		return (String)propertiesMap.get(SystemModel.NAME.toString());
	}
	public String getLanguage() {
		return language;
	}
	public void setLanguage(String language) {
		this.language = language;
	}
	public String getAbstractNote() {
		return abstractNote;
	}
	public void setAbstractNote(String abstractNote) {
		this.abstractNote = abstractNote;
	}
	public List<Note> getNotes() {
		return notes;
	}
	public void setNotes(List<Note> notes) {
		this.notes = notes;
	}
	public List<NamedEntity> getPeople() {
		return people;
	}
	public void setPeople(List<NamedEntity> people) {
		this.people = people;
	}
	public List<NamedEntity> getCorporations() {
		return corporations;
	}
	public void setCorporations(List<NamedEntity> corporations) {
		this.corporations = corporations;
	}
	public List<Subject> getSubjects() {
		return subjects;
	}
	public void setSubjects(List<Subject> subjects) {
		this.subjects = subjects;
	}
	public List<PathNode> getPath() {
		return path;
	}
	public void setPath(List<PathNode> path) {
		this.path = path;
	}
	public List<DigitalObject> getDigitalObjects() {
		return digitalObjects;
	}
	public void setDigitalObjects(List<DigitalObject> digitalObjects) {
		this.digitalObjects = digitalObjects;
	}
	public List<WebLink> getWeblinks() {
		return weblinks;
	}
	public void setWeblinks(List<WebLink> weblinks) {
		this.weblinks = weblinks;
	}
	public String getDateExpression() {
		return dateExpression;
	}
	public void setDateExpression(String dateExpression) {
		this.dateExpression = dateExpression;
	}
	public String getContainer() {
		return container;
	}
	public void setContainer(String container) {
		this.container = container;
	}
	public String getSummary() {
		return summary;
	}
	public void setSummary(String summary) {
		this.summary = summary;
	}
	public String getAuthors() {
		return authors;
	}
	public void setAuthors(String authors) {
		this.authors = authors;
	}
	public String getCollectionId() {
		return collectionId;
	}
	public void setCollectionId(String collectionId) {
		this.collectionId = collectionId;
	}
	public String getCollectionName() {
		return collectionName;
	}
	public void setCollectionName(String collectionName) {
		this.collectionName = collectionName;
	}
	public String getCollectionUrl() {
		return collectionUrl;
	}
	public void setCollectionUrl(String collectionUrl) {
		this.collectionUrl = collectionUrl;
	}	
	public void setData(Map<String,Object> data) {
		if(data.containsKey("collectionId")) setCollectionId((String)data.get("collectionId"));
		if(data.containsKey("collectionName")) setCollectionName((String)data.get("collectionName"));
		this.propertiesMap.putAll(data);
	}
	public Map<String,Object> getData(){ return this.propertiesMap; }
	public String getNativeContent() {
		return nativeContent;
	}
	public void setNativeContent(String nativeContent) {
		this.nativeContent = nativeContent;
	}
	
}
