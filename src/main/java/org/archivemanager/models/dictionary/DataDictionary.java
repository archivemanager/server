package org.archivemanager.models.dictionary;
import java.util.ArrayList;
import java.util.List;

import org.archivemanager.models.system.QName;


public class DataDictionary implements ModelObject {
	private static final long serialVersionUID = 4736326787129643097L;
	private Long id;
	private String uid;
	private String name;
	private String label;
	private QName qname;
	private String description;
	private String inheritance;
	private boolean shared;
	
	private List<Model> models = new ArrayList<Model>();
	private List<ModelList> lists = new ArrayList<ModelList>();
	
	protected ModelSorter modelSorter = new ModelSorter();
	
	public DataDictionary() {}
	public DataDictionary(String name) {
		this.name = name;
	}
	public DataDictionary(QName qname, String name, String description) {
		this.qname = qname;
		this.name = name;
		this.description = description;
	}
	public DataDictionary(Long id, QName qname, String name, String description) {
		this.id = id;
		this.qname = qname;
		this.name = name;
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
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}	
	public String getLabel() {
		return label;
	}
	public void setLabel(String label) {
		this.label = label;
	}
	public QName getQName() {
		return qname;
	}
	public void setQName(QName qname) {
		this.qname = qname;
	}
	public String getDescription() {
		return description;
	}	
	public boolean isShared() {
		return shared;
	}
	public void setShared(boolean shared) {
		this.shared = shared;
	}
	public void setDescription(String description) {
		this.description = description;
	}	
	public List<Model> getModels() {
		return models;
	}
	public void setModels(List<Model> models) {
		this.models = models;
	}		
	public String getInheritance() {
		return inheritance;
	}
	public void setInheritance(String inheritance) {
		this.inheritance = inheritance;
	}
	public List<ModelList> getLists() {
		return lists;
	}
	public void setLists(List<ModelList> lists) {
		this.lists = lists;
	}
	
}
