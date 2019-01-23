package org.archivemanager.models.dictionary;
import java.util.ArrayList;
import java.util.List;

import org.archivemanager.models.system.QName;


public class ModelFieldAspect implements ModelObject {
	private static final long serialVersionUID = 7314936515487882417L;
	private Long id;
	private String uid;
	private Long fieldId;
	private QName qname;
	private String label;
	private String description;
	private List<ModelField> fields = new ArrayList<ModelField>();
	
	
	public ModelFieldAspect() {}
	public ModelFieldAspect(Long id, QName qname) {
		this.id = id;
		this.qname = qname;
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
	public Long getFieldId() {
		return fieldId;
	}
	public void setFieldId(Long fieldId) {
		this.fieldId = fieldId;
	}
	public QName getQName() {
		return qname;
	}
	public void setQName(QName qname) {
		this.qname = qname;
	}	
	public String getLabel() {
		return label;
	}
	public void setLabel(String label) {
		this.label = label;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public List<ModelField> getFields() {
		return fields;
	}
	public void setFields(List<ModelField> fields) {
		this.fields = fields;
	}	
}
