package org.archivemanager.services.dictionary;
import java.io.Serializable;
import java.util.List;

import org.archivemanager.models.dictionary.DataDictionary;
import org.archivemanager.models.dictionary.Model;
import org.archivemanager.models.dictionary.ModelField;
import org.archivemanager.models.dictionary.ModelObject;
import org.archivemanager.models.dictionary.ModelRelation;
import org.archivemanager.models.system.QName;


public interface DataDictionaryService extends Serializable {
	
	ModelObject get(long id);	
	List<DataDictionary> getDictionaries();
	DataDictionary getDataDictionary(long id);
	DataDictionary getDataDictionary(QName qname);
	
	Model getModel(QName qname);
		
	List<QName> getQNames(QName qname);
	boolean isDescendant(QName parent, QName child);
	
	List<Model> getModels();
	List<Model> getChildModels(QName qname);
	
	List<ModelField> getModelFields(QName qname);
	List<ModelField> getModelFields(QName model, QName relation);
	
	List<ModelRelation> getModelRelations(QName qname);
	List<ModelRelation> getTargetModelRelations(QName qname);
	
	ModelRelation getModelRelation(QName modelQname, QName relationQname);
	ModelRelation getTargetModelRelation(QName modelQname, QName relationQname);
	
	ModelRelation getModelRelation(QName association, QName startNode, QName endNode);
	
	boolean isA(QName source, QName target);
	
	void save(ModelObject object);
	void remove(ModelObject object);
}
