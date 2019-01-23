package org.archivemanager.models.dictionary;
import java.util.List;

import org.archivemanager.models.system.QName;


public class ModelForm {
	private QName qname;
	private List<ModelField> longtextFields;
	private List<ModelField> mediumtextFields;
	private List<ModelField> smalltextFields;
	private List<ModelField> multivalueFields;
	private List<ModelField> integerFields;
	private List<ModelField> booleanFields;
	private List<ModelRelation> relations;
	
	
	public ModelForm(QName qname) {
		this.qname = qname;
	}
	
	public String getLocalName() {
		return qname.getLocalName();
	}
	
	public QName getQname() {
		return qname;
	}

	public void setQname(QName qname) {
		this.qname = qname;
	}

	public List<ModelField> getLongtextFields() {
		return longtextFields;
	}

	public void setLongtextFields(List<ModelField> longtextFields) {
		this.longtextFields = longtextFields;
	}

	public List<ModelField> getMediumtextFields() {
		return mediumtextFields;
	}

	public void setMediumtextFields(List<ModelField> mediumtextFields) {
		this.mediumtextFields = mediumtextFields;
	}

	public List<ModelField> getSmalltextFields() {
		return smalltextFields;
	}

	public void setSmalltextFields(List<ModelField> smalltextFields) {
		this.smalltextFields = smalltextFields;
	}

	public List<ModelField> getMultivalueFields() {
		return multivalueFields;
	}

	public void setMultivalueFields(List<ModelField> multivalueFields) {
		this.multivalueFields = multivalueFields;
	}

	public List<ModelField> getIntegerFields() {
		return integerFields;
	}

	public void setIntegerFields(List<ModelField> integerFields) {
		this.integerFields = integerFields;
	}

	public List<ModelField> getBooleanFields() {
		return booleanFields;
	}

	public void setBooleanFields(List<ModelField> booleanFields) {
		this.booleanFields = booleanFields;
	}

	public List<ModelRelation> getRelations() {
		return relations;
	}

	public void setRelations(List<ModelRelation> relations) {
		this.relations = relations;
	}
	
}
