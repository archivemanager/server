package org.archivemanager.models.repository;
import java.util.ArrayList;
import java.util.List;

import org.archivemanager.models.system.Permission;


public class Collection extends Result {
	private String url;	
	private String bioNote;
	private String scopeNote;
	private String abstractNote;
	private String container;
	private String dateExpression;	
	private String comment = "";
	private String code;
	private String identifier;
	private String accessionDate;
	private String begin;
	private String end;
	private String bulkBegin;
	private String bulkEnd;
	private String language;
	private String extentUnits;
	private String extentNumber;
	private boolean restrictions;
	private boolean internal;
	private boolean searchable;
	private boolean isPublic;
	
	private List<Category> categories = new ArrayList<Category>();
	private List<Note> notes = new ArrayList<Note>();
	private List<NamedEntity> people = new ArrayList<NamedEntity>();
	private List<NamedEntity> corporations = new ArrayList<NamedEntity>();
	private List<Subject> subjects = new ArrayList<Subject>();
	private List<DigitalObject> digitalObjects = new ArrayList<DigitalObject>();
	private List<WebLink> weblinks = new ArrayList<WebLink>();
	private List<Permission> permissions = new ArrayList<Permission>();
	
	public Collection() {}
	public Collection(long id, long assoc, String name, String description, String contentType) {
		super(id, assoc, name, description, contentType);
	}
		
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getBegin() {
		return begin;
	}
	public void setBegin(String begin) {
		this.begin = begin;
	}
	public String getEnd() {
		return end;
	}
	public void setEnd(String end) {
		this.end = end;
	}
	public List<Category> getCategories() {
		return categories;
	}
	public void setCategories(List<Category> categories) {
		this.categories = categories;
	}
	public String getContainer() {
		return container;
	}
	public void setContainer(String container) {
		this.container = container;
	}
	public String getDateExpression() {
		return dateExpression;
	}
	public void setDateExpression(String dateExpression) {
		this.dateExpression = dateExpression;
	}
	public String getBioNote() {
		return bioNote;
	}
	public void setBioNote(String bioNote) {
		this.bioNote = bioNote;
	}
	public String getScopeNote() {
		return scopeNote;
	}
	public void setScopeNote(String scopeNote) {
		this.scopeNote = scopeNote;
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getIdentifier() {
		return identifier;
	}
	public void setIdentifier(String identifier) {
		this.identifier = identifier;
	}
	public String getAccessionDate() {
		return accessionDate;
	}
	public void setAccessionDate(String accessionDate) {
		this.accessionDate = accessionDate;
	}
	public String getBulkBegin() {
		return bulkBegin;
	}
	public void setBulkBegin(String bulkBegin) {
		this.bulkBegin = bulkBegin;
	}
	public String getBulkEnd() {
		return bulkEnd;
	}
	public void setBulkEnd(String bulkEnd) {
		this.bulkEnd = bulkEnd;
	}
	public String getLanguage() {
		return language;
	}
	public void setLanguage(String language) {
		this.language = language;
	}
	public String getExtentUnits() {
		return extentUnits;
	}
	public void setExtentUnits(String extentUnits) {
		this.extentUnits = extentUnits;
	}
	public String getExtentNumber() {
		return extentNumber;
	}
	public void setExtentNumber(String extentNumber) {
		this.extentNumber = extentNumber;
	}
	public boolean isRestrictions() {
		return restrictions;
	}
	public void setRestrictions(boolean restrictions) {
		this.restrictions = restrictions;
	}
	public boolean isInternal() {
		return internal;
	}
	public void setInternal(boolean internal) {
		this.internal = internal;
	}
	public boolean isPublic() {
		return isPublic;
	}
	public void setPublic(boolean isPublic) {
		this.isPublic = isPublic;
	}	
	public boolean isSearchable() {
		return searchable;
	}
	public void setSearchable(boolean searchable) {
		this.searchable = searchable;
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
	public String getComment() {
		return comment;
	}
	public void setComment(String comment) {
		this.comment = comment;
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
	public List<Permission> getPermissions() {
		return permissions;
	}
	public void setPermissions(List<Permission> permissions) {
		this.permissions = permissions;
	}
	
}
