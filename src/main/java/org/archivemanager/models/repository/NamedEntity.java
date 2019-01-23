package org.archivemanager.models.repository;
import java.util.ArrayList;
import java.util.List;


public class NamedEntity extends Result {
	private String function;
	private String role;
	private String note;
	private String dates;
	private String source;
	private List<Collection> collections = new ArrayList<Collection>();
	private List<Entry> entries = new ArrayList<Entry>();
	
	public NamedEntity() {}
	public NamedEntity(long id, long assoc, String title, String description, String contentType) {
		super(id, assoc, title, description, contentType);
	}
	
	public String getFunction() {
		return function;
	}
	public void setFunction(String function) {
		this.function = function;
	}
	public String getRole() {
		return role;
	}
	public void setRole(String role) {
		this.role = role;
	}
	public List<Collection> getCollections() {
		return collections;
	}
	public void setCollections(List<Collection> collections) {
		this.collections = collections;
	}
	public String getNote() {
		return note;
	}
	public void setNote(String note) {
		this.note = note;
	}
	public List<Entry> getEntries() {
		return entries;
	}
	public void setEntries(List<Entry> entries) {
		this.entries = entries;
	}
	public String getDates() {
		return dates;
	}
	public void setDates(String dates) {
		this.dates = dates;
	}
	public String getSource() {
		return source;
	}
	public void setSource(String source) {
		this.source = source;
	}	
}
