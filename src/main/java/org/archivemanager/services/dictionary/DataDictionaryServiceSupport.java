package org.archivemanager.services.dictionary;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.archivemanager.config.PropertyConfiguration;
import org.archivemanager.exception.InvalidQualifiedNameException;
import org.archivemanager.models.dictionary.DataDictionary;
import org.archivemanager.models.dictionary.Model;
import org.archivemanager.models.dictionary.ModelField;
import org.archivemanager.models.dictionary.ModelRelation;
import org.archivemanager.models.system.QName;
import org.springframework.beans.factory.annotation.Autowired;

public abstract class DataDictionaryServiceSupport implements DataDictionaryService {
	private static final long serialVersionUID = 1260267132186921536L;
	private final static Logger log = Logger.getLogger(DataDictionaryServiceSupport.class.getName());
	@Autowired private PropertyConfiguration propertyService;
	
	private List<DataDictionary> dictionaries = new ArrayList<DataDictionary>();
	
		
	@Override
	public boolean isA(QName source, QName target) {
		List<QName> qnames = getQNames(source);
		for(QName qname : qnames) {
			if(qname.toString().equals(target.toString()))
				return true;
		}
		return false;
	}
	
	@Override
	public Model getModel(QName qname) {
		for(DataDictionary dictionary : dictionaries) {
			for(Model model : dictionary.getModels()) {
				if(model.getQName().toString().equals(qname.toString()))
					return model;
			}
		}
		log.severe("model not found for : "+qname.toString());
		return null;
	}
	
	@Override
	public DataDictionary getDataDictionary(long dictionaryid) {
		for(DataDictionary dictionary : dictionaries) {
			if(dictionary.getId() == dictionaryid)
				return dictionary;
		}
		log.log(Level.SEVERE, "DataDictionary not found dictionary="+dictionaryid);
		return null;
	}
	public DataDictionary getDataDictionary(QName qname) {
		for(DataDictionary dictionary : dictionaries) {
			if(dictionary.getQName().equals(qname))
				return dictionary;
		}
		log.log(Level.SEVERE, "DataDictionary not found qname="+qname);
		return null;
	}
		
	@Override
	public List<Model> getModels() {
		List<Model> models = new ArrayList<Model>();
		for(DataDictionary dictionary : dictionaries) {
			models.addAll(dictionary.getModels());
		}
		return models;
	}
	public List<QName> getQNames(QName qname) {
		List<QName> qnames = new ArrayList<QName>();
		if(qname == null) return qnames;
		qnames.add(qname);
		try	{
			Model model = getModel(qname);
			if(model != null && model.getParentName() != null) {
				try {
					//Model parent = (Model)getModel(model.getParent());
					QName parentQname = model.getParentName();
					if(parentQname != null) {
						List<QName> parentNodes = getQNames(parentQname);
						if(parentNodes != null) 
							qnames.addAll(parentNodes);
					}
				} catch(Exception e) {
					throw new InvalidQualifiedNameException("getNames() failed, invalid parent:"+qname.toString());
				}
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
		return qnames;
	}
	public ModelField getModelField(QName model, QName field) throws DataDictionaryException {
		List<ModelField> fields = getModelFields(model);
		for(ModelField modelField : fields) {
			if(modelField.getQName().equals(field))
				return modelField;
		}
		log.log(Level.SEVERE, "ModelField not found model="+model+", field="+field);
		return null;
	}
	public List<Model> getChildModels(QName qname) {
		List<Model> models = new ArrayList<Model>();
		for(DataDictionary dictionary : dictionaries) {
			for(Model m : dictionary.getModels()) {
				if(m.getParentName() != null && m.getParentName().equals(qname))
					models.add(m);
			}
		}
		return models;
	}
	public boolean isDescendant(QName parent, QName child) {
		if(parent.equals(child)) return true;
		Model parentModel = getModel(parent);
		Model childModel = getModel(child);
		if(parentModel != null && childModel != null && childModel.getParent() != null) {
			if(childModel.getParent().equals(parentModel.getId())) return true;
		}
		return false;
	}
	public Model getModel(long id) {
		Model model = null;
		for(DataDictionary dictionary : dictionaries) {
			for(Model m : dictionary.getModels()) {
				if(m.getId() == id) model = m;
			}			
		}
		return model;
	}
	public List<ModelField> getModelFields(QName qname) {
		List<String> qnames = new ArrayList<String>();
		List<ModelField> fields = new ArrayList<ModelField>();
		List<Model> models = new ArrayList<Model>();
		try {
			Model model = getModel(qname);
			while(model != null) {
				models.add(model);								
				model = model.getParent();
			}
			Collections.reverse(models);
			for(Model m : models) {
				for(ModelField field : m.getFields()) {
					if(!qnames.contains(field.getQName().toString())) {
						fields.add(field);
						qnames.add(field.getQName().toString());
					} else {
						for(ModelField f : fields) {
							if(field.getQName().toString().equals(f.getQName().toString())) {
								ModelField newField = (ModelField)f.clone();
								if(field.getFormat() != null) newField.setFormat(field.getFormat());
								if(field.getLabel() != null) newField.setLabel(field.getLabel());
								fields.set(fields.indexOf(f), newField);
							}
						}
					}
				}
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
		return fields;
	}
	public List<ModelField> getModelFields(QName modelName, QName relationName) {
		List<String> qnames = new ArrayList<String>();
		List<ModelField> fields = new ArrayList<ModelField>();
		List<Model> models = new ArrayList<Model>();
		try {
			Model model = getModel(modelName);
			while(model != null) {
				models.add(model);								
				model = model.getParent();
			}
			Collections.reverse(models);
			for(Model m : models) {
				for(ModelRelation relation : m.getRelations()) {
					if(relation.getQName().toString().equals(relationName.toString())) {
						for(ModelField field : relation.getFields()) {
							if(!qnames.contains(field.getQName().toString())) {
								fields.add(field);
								qnames.add(field.getQName().toString());
							} else {
								for(ModelField f : fields) {
									if(field.getQName().toString().equals(f.getQName().toString())) {
										ModelField newField = (ModelField)f.clone();
										if(field.getFormat() != null) newField.setFormat(field.getFormat());
										if(field.getLabel() != null) newField.setLabel(field.getLabel());
										fields.set(fields.indexOf(f), newField);
									}
								}
							}
						}
					}
				}
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
		return fields;
	}
	@Override
	public List<ModelRelation> getModelRelations(QName modelName) {
		List<ModelRelation> relations = new ArrayList<ModelRelation>();
		try {
			Model model = getModel(modelName);
			while(model != null) {
				relations.addAll(model.getSourceRelations());
				model = model.getParent();
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
		return relations;
	}
	@Override
	public List<ModelRelation> getTargetModelRelations(QName modelName) {
		List<ModelRelation> relations = new ArrayList<ModelRelation>();
		try {
			Model model = getModel(modelName);
			while(model != null) {
				relations.addAll(model.getTargetRelations());
				model = model.getParent();
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
		return relations;
	}
	@Override
	public ModelRelation getModelRelation(QName modelQname, QName relationQname) {
		List<ModelRelation> relations = getModelRelations(modelQname);
		for(ModelRelation relation : relations) {
			if(relation.getQName().toString().equals(relationQname.toString()))
				return relation;
		}
		return null;
	}
	@Override
	public ModelRelation getTargetModelRelation(QName modelQname, QName relationQname) {
		List<ModelRelation> relations = getTargetModelRelations(modelQname);
		for(ModelRelation relation : relations) {
			if(relation.getQName().toString().equals(relationQname.toString()))
				return relation;
		}
		return null;
	}
	@Override
	public ModelRelation getModelRelation(QName association, QName startNode, QName endNode) {
		for(DataDictionary dictionary : dictionaries) {
			for(Model model : dictionary.getModels()) {
				for(ModelRelation relation : model.getRelations()) {
					if(relation.getQName().toString().equals(association.toString()) &&
							relation.getStartName().toString().equals(startNode.toString()) &&
							relation.getEndName().toString().equals(endNode.toString())) {
						return relation;
					}
				}
			}
		}
		return null;
	}
	
	public List<DataDictionary> getDictionaries() {
		return dictionaries;
	}
	public void setDictionaries(List<DataDictionary> dictionaries) {
		this.dictionaries = dictionaries;
	}
	public PropertyConfiguration getPropertyService() {
		return propertyService;
	}

}
