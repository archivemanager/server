package org.archivemanager.services.search.indexing;
import java.util.ArrayList;
import java.util.List;

import org.archivemanager.models.system.QName;


public class IndexEntity {
	private Long id;
	private String content;
	private boolean delete;
	private List<QName> qnames = new ArrayList<QName>();
	private List<IndexField> fields = new ArrayList<IndexField>();
	private List<IndexFacet> facets = new ArrayList<IndexFacet>();
	private List<String> messages = new ArrayList<String>();
	
	
	public IndexEntity() {}
	public IndexEntity(Long id) {
		this.id = id;
	}
	public IndexEntity(Long id, List<QName> qnames, List<IndexField> fields) {
		this.id = id;
		this.qnames = qnames;
		this.fields = fields;
	}
	public IndexEntity(Long id, List<QName> qnames, List<IndexField> fields, List<IndexFacet> facets) {
		this.id = id;
		this.qnames = qnames;
		this.fields = fields;
		this.facets = facets;
	}
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public List<IndexField> getFields() {
		return fields;
	}
	public void setFields(List<IndexField> fields) {
		this.fields = fields;
	}
	public List<IndexFacet> getFacets() {
		return facets;
	}
	public void setFacets(List<IndexFacet> facets) {
		this.facets = facets;
	}	
	public List<QName> getQnames() {
		return qnames;
	}
	public void setQnames(List<QName> qnames) {
		this.qnames = qnames;
	}
	public boolean isDelete() {
		return delete;
	}
	public void setDelete(boolean delete) {
		this.delete = delete;
	}
	public List<String> getMessages() {
		return messages;
	}
	public void setMessages(List<String> messages) {
		this.messages = messages;
	}
	
}
