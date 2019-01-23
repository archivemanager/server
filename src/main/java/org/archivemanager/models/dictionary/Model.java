package org.archivemanager.models.dictionary;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;

import org.archivemanager.models.system.QName;
import org.archivemanager.services.entity.ImportProcessor;
import org.archivemanager.util.StringFormatUtility;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(value={ "localName" }, allowGetters=true)
public class Model implements ModelObject {
	private static final long serialVersionUID = 8464798888371448240L;
	private final static Logger log = Logger.getLogger(Model.class.getName());
	private Long id;
	private String uid;
	private String label;
	private String header;
	private String description;
	private DataDictionary dictionary;	
	private Model parent;	
	private QName parentName;
	private QName labelName;
	private QName qname;
	private boolean stored;
	private boolean searchable;
	private boolean freetext;
	private boolean auditable = true;
	private List<Model> children = new ArrayList<Model>();
	private List<ModelField> fields = new ArrayList<ModelField>();
	private List<ModelRelation> sourceRelations = new ArrayList<ModelRelation>();
	private List<ModelRelation> targetRelations = new ArrayList<ModelRelation>();
	private List<ImportProcessor> processors = new ArrayList<ImportProcessor>();
	
	
	public Model() {}
	public Model(Long id, QName qname) {
		this.id = id;
		this.qname = qname;
	}	
	public Model(QName qname, String description) {
		this.qname = qname;
		this.description = description;
	}	
	public Model(Long id, QName qname, String description) {
		this.id = id;
		this.qname = qname;
		this.description = description;
	}
	
	public String getLocalName() {
		return qname.getLocalName();
	}
	public String getName() {
		if(label == null || label.length() == 0) 
			return StringFormatUtility.toTitleCase(qname.getLocalName().replace("_", " "));
		return label;
	}	
	public boolean containsRelation(QName qname) {
		for(ModelRelation rel : getSourceRelations()) {
			if(rel.getQName() != null && rel.getQName().equals(qname)) 
				return true;
		}
		for(ModelRelation rel : getTargetRelations()) {
			if(rel.getQName() != null && rel.getQName().equals(qname)) 
				return true;
		}
		return false;
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
	public String getLabel() {
		return label;
	}
	public void setLabel(String label) {
		this.label = label;
	}	
	public String getHeader() {
		return header;
	}
	public void setHeader(String header) {
		this.header = header;
	}
	@JsonIgnore
	public DataDictionary getDictionary() {
		return dictionary;
	}
	public void setDictionary(DataDictionary dictionary) {
		this.dictionary = dictionary;
	}
	@JsonIgnore
	public Model getParent() {
		return parent;
	}
	public void setParent(Model parent) {
		this.parent = parent;
	}	
	public List<Model> getChildren() {
		return children;
	}
	public void setChildren(List<Model> children) {
		this.children = children;
	}
	public QName getParentName() {
		return parentName;
	}
	public void setParentName(QName parentName) {
		this.parentName = parentName;
	}	
	public QName getLabelName() {
		return labelName;
	}
	public void setLabelName(QName labelName) {
		this.labelName = labelName;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}			
	public boolean isSearchable() {
		return searchable;
	}
	public void setSearchable(boolean searchable) {
		this.searchable = searchable;
	}
	public boolean isAuditable() {
		return auditable;
	}
	public void setAuditable(boolean auditable) {
		this.auditable = auditable;
	}	
	public boolean isFreetext() {
		return freetext;
	}
	public void setFreetext(boolean freetext) {
		this.freetext = freetext;
	}
	public List<ModelField> getFields() {
		return fields;
	}
	public void setFields(List<ModelField> fields) {
		Collections.sort(fields, new ModelFieldSorter());
		this.fields = fields;		
	}
	public List<ModelRelation> getRelations() {
		List<ModelRelation> list = new ArrayList<ModelRelation>();
		list.addAll(sourceRelations);
		list.addAll(targetRelations);
		return list;
	}
	public ModelRelation getModelRelation(QName qname) {
		List<ModelRelation> relations = new ArrayList<ModelRelation>();
		relations.addAll(sourceRelations);
		relations.addAll(targetRelations);
		Model parentModel = parent;
		while(parentModel != null) {	
			relations.addAll(parentModel.getSourceRelations());
			relations.addAll(parentModel.getTargetRelations());
			parentModel = parentModel.getParent();
		}		
		for(ModelRelation relation : relations) {
			if(relation != null && relation.toString().equals(qname.toString())) {
				return relation;
			}
		}
		return null;
	}
	public ModelField getField(QName qname) {
		for(ModelField field : fields) {
			if(field.getQName().equals(qname))
				return field;
		}
		Model parentModel = parent;
		while(parentModel != null) {	
			for(ModelField field : parentModel.getFields()) {
				if(field.getQName().equals(qname))
					return field;
			}
			parentModel = parentModel.getParent();
		}
		log.severe("field not found : "+this.qname+", "+qname.toString());
		return null;
	}
	public ModelField getAssociationField(QName qname, QName field) {
		List<ModelRelation> relations = new ArrayList<ModelRelation>();
		relations.addAll(sourceRelations);
		relations.addAll(targetRelations);
		Model parentModel = parent;
		while(parentModel != null) {	
			relations.addAll(parentModel.getSourceRelations());
			relations.addAll(parentModel.getTargetRelations());
			parentModel = parentModel.getParent();
		}		
		for(ModelRelation relation : relations) {
			if(relation != null) {
				for(ModelField modelField : relation.getFields()) {
					if(modelField.getQName().equals(field))
						return modelField;
				}
			}
		}
		log.severe("association field not found : "+this.qname+", "+qname.toString()+", "+field.toString());
		return null;
	}
	
	public ModelRelation getSourceRelation(QName qname) {
		for(ModelRelation field : sourceRelations) {
			if(field.getQName().equals(qname))
				return field;
		}
		return null;
	}
	public ModelRelation getTargetRelation(QName qname) {
		for(ModelRelation field : targetRelations) {
			if(field.getQName().equals(qname))
				return field;
		}
		return null;
	}
	public List<ModelField> getUniqueFields(boolean inherited) {
		List<ModelField> uniqueFields = new ArrayList<ModelField>();
		for(ModelField field : fields) {
			if(field.isUnique()) uniqueFields.add(field);
		}
		return uniqueFields;
	}
	public List<ModelField> getFields(boolean inherited) {
		if(inherited) {
			List<ModelField> fields = new ArrayList<ModelField>();
			fields.addAll(this.getFields());
			/*
			if(this.parent != null) 
				fields.addAll(this.parent.getFields(true));
			*/
			return fields;
		} else return getFields();
	}
	public List<ModelRelation> getSourceRelations() {
		return sourceRelations;
	}
	public List<ModelRelation> getTargetRelations() {
		return targetRelations;
	}
	public List<ImportProcessor> getProcessors() {
		return processors;
	}
	public void setProcessors(List<ImportProcessor> processors) {
		this.processors = processors;
	}

	/*
	public List<Model> getPath(QName qname) {
		List<Model> models = new ArrayList<Model>();
		Model parent = this.parent;
		while(parent != null) {
			models.add(parent);
			parent = parent.getParent();
		}
		
		return models;
	}
	*/
	public QName getQName() {
		return qname;
	}
	public void setQName(QName qname) {
		this.qname = qname;
	}
	public boolean isStored() {
		return stored;
	}
	public void setStored(boolean stored) {
		this.stored = stored;
	}
	
}
