package org.archivemanager.web.model;
import java.util.ArrayList;
import java.util.List;

import org.archivemanager.models.dictionary.ModelObject;
import org.archivemanager.models.system.QName;


public class DataDictionaryRecord {
	private Long id;
	private String uid;
	private String name;
	private QName qname;
	private String description;
	private String inheritance;
	private boolean isPublic;
	private boolean searchable;
	
	private List<ModelObject> inheritedModels = new ArrayList<ModelObject>();
	private List<ModelObject> localModels = new ArrayList<ModelObject>();
	
	public DataDictionaryRecord() {}
	public DataDictionaryRecord(org.archivemanager.models.dictionary.DataDictionary d) {
		this.id = d.getId();
		this.name = d.getName();
		this.description = d.getDescription();
		this.qname = d.getQName();
	}
	public DataDictionaryRecord(org.archivemanager.models.dictionary.Model m) {
		this.id = m.getId();
		this.name = m.getName();
		this.qname = m.getQName();
		this.description = m.getDescription();
		this.searchable = m.isSearchable();
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
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}	
	public QName getQname() {
		return qname;
	}
	public void setQname(QName qname) {
		this.qname = qname;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getInheritance() {
		return inheritance;
	}
	public void setInheritance(String inheritance) {
		this.inheritance = inheritance;
	}
	public boolean isPublic() {
		return isPublic;
	}
	public void setPublic(boolean isPublic) {
		this.isPublic = isPublic;
	}
	public List<ModelObject> getInheritedModels() {
		return inheritedModels;
	}
	public void setInheritedModels(List<ModelObject> inheritedModels) {
		this.inheritedModels = inheritedModels;
	}
	public List<ModelObject> getLocalModels() {
		return localModels;
	}
	public void setLocalModels(List<ModelObject> localModels) {
		this.localModels = localModels;
	}
	public boolean isSearchable() {
		return searchable;
	}
	public void setSearchable(boolean searchable) {
		this.searchable = searchable;
	}
	
}
