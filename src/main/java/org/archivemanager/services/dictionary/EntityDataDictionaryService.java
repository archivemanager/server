package org.archivemanager.services.dictionary;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;

import org.archivemanager.models.DictionaryModel;
import org.archivemanager.models.binders.DataDictionaryModelEntityBinder;
import org.archivemanager.models.dictionary.DataDictionary;
import org.archivemanager.models.dictionary.Model;
import org.archivemanager.models.dictionary.ModelField;
import org.archivemanager.models.dictionary.ModelFieldValue;
import org.archivemanager.models.dictionary.ModelObject;
import org.archivemanager.models.dictionary.ModelRelation;
import org.archivemanager.models.system.QName;
import org.archivemanager.services.entity.Entity;
import org.archivemanager.services.entity.EntityService;
import org.archivemanager.util.FileUtility;
import org.springframework.beans.factory.annotation.Autowired;

import com.fasterxml.jackson.databind.ObjectMapper;


public class EntityDataDictionaryService extends DataDictionaryServiceSupport {
	private static final long serialVersionUID = -8594531361920252635L;
	private final static Logger log = Logger.getLogger(JsonDataDictionaryService.class.getName());
	@Autowired private EntityService entityService;
	@Autowired private DataDictionaryModelEntityBinder entityBinder;
	@Autowired private ObjectMapper mapper;
	
	
	@PostConstruct
	public void initialize() {			
		try	{
			getDictionaries().clear();
			List<Entity> dictionaries = entityService.getEntities(new QName[] {DictionaryModel.DICTIONARY});			
			List<Model> models = new ArrayList<Model>();
			for(Entity dictionaryEntity : dictionaries) {	
				DataDictionary dictionary = entityBinder.getDataDictionary(dictionaryEntity);
				getDictionaries().add(dictionary);
				for(Model model : dictionary.getModels()) {
					model.setDictionary(dictionary);
				}
				models.addAll(dictionary.getModels());				
			}
			for(Model model : models) {
				for(Model m : models) {
					for(ModelRelation relation : m.getSourceRelations()) {
						if(relation.getEndName().equals(model.getQName()))
							model.getTargetRelations().add(relation);
						relation.setModel(m);
					}
					for(ModelField field : m.getFields()) {						
						field.setModel(m);						
						for(ModelFieldValue value : field.getValues()) {							
							value.setField(field);
						}
					}
					if(m.getParentName() != null && m.getParentName().toString().equals(model.getQName().toString())) {
						m.setParent(model);
						model.getChildren().add(m);
					}				
				}
			}			
			log.info("system data dictionary loaded, "+models.size()+" models loaded...");					
		} catch(Exception e) {
			e.printStackTrace();
		} 
	}
	
	public void save(DataDictionary dictionary) {
		String homeDir = getPropertyService().getHomeDirectory() != null ? getPropertyService().getHomeDirectory()+"/resources/dictionaries" : "";
		if(homeDir.length() > 0 && !homeDir.endsWith("/")) homeDir = homeDir + "/";
		File file = new File(homeDir + dictionary.getQName()+".json");
		try {
			FileUtility.writeToFile(mapper.writeValueAsString(dictionary), file);
		} catch(Exception e) {
			log.log(Level.SEVERE, "", e);
		}
	}

	@Override
	public ModelObject get(long id) {
		return entityBinder.getModelObject(entityService.getEntity(id));
	}

	@Override
	public void save(ModelObject object) {
		if(object instanceof DataDictionary) {
			DataDictionary dictionary = (DataDictionary)object;
			save(dictionary);
		} else if(object instanceof Model) {
			Model model = (Model)object;
			save(model.getDictionary());
		} else if(object instanceof ModelField) {
			ModelField field = (ModelField)object;
			save(field.getModel().getDictionary());
		} else if(object instanceof ModelRelation) {
			ModelRelation relation = (ModelRelation)object;
			save(relation.getModel().getDictionary());
		} else if(object instanceof ModelFieldValue) {
			ModelFieldValue value = (ModelFieldValue)object;
			save(value.getField().getModel().getDictionary());
		}
		initialize();
		log.info("dictionary service initialized.");
	}

	@Override
	public void remove(ModelObject object) {
		if(object instanceof Model) {
			Model model = (Model)object;
			model.getDictionary().getModels().remove(model);
		} else if(object instanceof ModelField) {
			ModelField field = (ModelField)object;
			field.getModel().getFields().remove(field);
		} else if(object instanceof ModelRelation) {
			ModelRelation relation = (ModelRelation)object;
			relation.getModel().getRelations().remove(relation);
		} else if(object instanceof ModelFieldValue) {
			ModelFieldValue value = (ModelFieldValue)object;
			value.getField().getValues().remove(value);
		}
		save(object);
	}
}