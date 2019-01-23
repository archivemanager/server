package org.archivemanager.web.model;
import java.util.ArrayList;
import java.util.List;

import org.archivemanager.models.RepositoryModel;
import org.archivemanager.models.dictionary.Model;
import org.archivemanager.models.dictionary.ModelField;
import org.archivemanager.models.dictionary.ModelRelation;
import org.archivemanager.models.system.QName;


public class ModelRecord {
	private QName qname;
	private String label;
	private boolean item;
	private boolean searchable;
	private List<FieldRecord> fields = new ArrayList<FieldRecord>();
	private List<RelationRecord> relations = new ArrayList<RelationRecord>();
	
	
	public ModelRecord() {}
	public ModelRecord(Model model) {
		this.qname = model.getQName();
		this.label = model.getName();
		this.searchable = model.isSearchable();
	}
	public ModelRecord(Model model, List<ModelField> fields, List<ModelRelation> relations) {
		this(model);
		this.qname = model.getQName();
		item = model.getParentName() != null ? model.getParentName().toString().equals(RepositoryModel.ITEM.toString()) : false;
		for(ModelField f : fields) {
			FieldRecord field = new FieldRecord(f);
			this.fields.add(field);
		}
		for(ModelRelation r : relations) {
			RelationRecord relation = new RelationRecord(r);
			this.relations.add(relation);
		}
	}	
	public String getLocalName() {
		return qname.getLocalName();
	}
	
	public String getLabel() {
		return label;
	}
	public void setLabel(String label) {
		this.label = label;
	}	
	public QName getQname() {
		return qname;
	}
	public void setQname(QName qname) {
		this.qname = qname;
	}	
	public boolean isItem() {
		return item;
	}
	public void setItem(boolean item) {
		this.item = item;
	}	
	public boolean isSearchable() {
		return searchable;
	}
	public void setSearchable(boolean searchable) {
		this.searchable = searchable;
	}
	public void setRelations(List<RelationRecord> relations) {
		this.relations = relations;
	}
	public List<FieldRecord> getFields() {
		return fields;
	}
	public void setFields(List<FieldRecord> fields) {
		this.fields = fields;		
	}
	public List<RelationRecord> getRelations() {
		return relations;
	}
	public FieldRecord getField(QName qname) {
		for(FieldRecord field : fields) {
			if(field.getQName().equals(qname))
				return field;
		}
		return null;
	}
	public List<FieldRecord> getFields(boolean inherited) {
		if(inherited) {
			List<FieldRecord> fields = new ArrayList<FieldRecord>();
			fields.addAll(this.getFields());
			return fields;
		} else return getFields();
	}
	
}
