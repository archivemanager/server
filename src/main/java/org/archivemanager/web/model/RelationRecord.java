package org.archivemanager.web.model;

import java.util.ArrayList;
import java.util.List;

import org.archivemanager.models.dictionary.ModelField;
import org.archivemanager.models.dictionary.ModelRelation;
import org.archivemanager.models.system.QName;

public class RelationRecord {
	private QName qname;
	private String label;
	private QName target;
	private String view;
	private boolean cascades;
	private List<FieldRecord> fields = new ArrayList<FieldRecord>();
	
	
	public RelationRecord() {}
	public RelationRecord(ModelRelation relation) {
		this.qname = relation.getQName();
		this.label = relation.getName();
		this.target = relation.getEndName();
		this.view = relation.getView();
		this.cascades = relation.isCascade();
		for(ModelField f : relation.getFields()) {
			FieldRecord field = new FieldRecord(f);
			this.fields.add(field);
		}
	}
	
	public QName getQname() {
		return qname;
	}
	public void setQname(QName qname) {
		this.qname = qname;
	}
	public String getLabel() {
		return label;
	}
	public void setLabel(String label) {
		this.label = label;
	}
	public QName getTarget() {
		return target;
	}
	public void setTarget(QName target) {
		this.target = target;
	}
	public boolean isCascades() {
		return cascades;
	}
	public void setCascades(boolean cascades) {
		this.cascades = cascades;
	}
	public List<FieldRecord> getFields() {
		return fields;
	}
	public void setFields(List<FieldRecord> fields) {
		this.fields = fields;
	}
	public String getView() {
		return view;
	}
	public void setView(String view) {
		this.view = view;
	}
	
}
