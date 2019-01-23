package org.archivemanager.services.dictionary;
import java.io.File;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;

import org.archivemanager.models.dictionary.DataDictionary;
import org.archivemanager.models.dictionary.Model;
import org.archivemanager.models.dictionary.ModelField;
import org.archivemanager.models.dictionary.ModelFieldValue;
import org.archivemanager.models.dictionary.ModelObject;
import org.archivemanager.models.dictionary.ModelRelation;
import org.springframework.beans.factory.annotation.Autowired;

import com.fasterxml.jackson.databind.ObjectMapper;


//@Service
public class JsonDataDictionaryService extends DataDictionaryServiceSupport {
	private static final long serialVersionUID = -8594531361920252635L;
	private final static Logger log = Logger.getLogger(JsonDataDictionaryService.class.getName());
	@Autowired private ObjectMapper mapper;
	
	private Map<Long,ModelObject> idMap = new HashMap<Long,ModelObject>();
	private Long nextId = 1L;
	
	@PostConstruct
	public void initialize() {			
		try	{
			getDictionaries().clear();
			List<File> dictionaries = new ArrayList<File>();
			File homeDir = new File(getPropertyService().getHomeDirectory()+"/resources/dictionaries");
			File[] files = homeDir.listFiles(new FilenameFilter() {
				@Override
				public boolean accept(File dir, String name) {
					return name.endsWith(".json");
				}				
			});
			for(File f : files) {
				dictionaries.add(f);
			}
			List<Model> models = new ArrayList<Model>();
			for(File importFile : dictionaries) {						
				DataDictionary dictionary = mapper.readValue(importFile, DataDictionary.class);
				dictionary.setId(nextId);
				nextId++;
				idMap.put(dictionary.getId(), dictionary);
				getDictionaries().add(dictionary);
				for(Model model : dictionary.getModels()) {
					model.setDictionary(dictionary);
				}
				models.addAll(dictionary.getModels());				
			}
			for(Model model : models) {
				model.setId(nextId);
				idMap.put(model.getId(), model);
				nextId++;				
				for(Model m : models) {
					for(ModelRelation relation : m.getSourceRelations()) {
						if(relation.getEndName() != null && relation.getEndName().equals(model.getQName()))
							model.getTargetRelations().add(relation);
						relation.setId(nextId);
						relation.setModel(m);						
						relation.setId(nextId);
						idMap.put(relation.getId(), relation);
						nextId++;
						for(ModelField field : relation.getFields()) {						
							field.setModel(m);
							field.setId(nextId);
							idMap.put(field.getId(), field);
							nextId++;
							for(ModelFieldValue value : field.getValues()) {							
								value.setField(field);							
								value.setId(nextId);
								idMap.put(value.getId(), value);
								nextId++;
							}
						}
					}
					for(ModelField field : m.getFields()) {						
						field.setModel(m);
						field.setId(nextId);
						idMap.put(field.getId(), field);
						nextId++;
						for(ModelFieldValue value : field.getValues()) {							
							value.setField(field);							
							value.setId(nextId);
							idMap.put(value.getId(), value);
							nextId++;
						}
					}
					if(m.getParentName() != null && m.getParentName().toString().equals(model.getQName().toString())) {
						m.setParent(model);
						model.getChildren().add(m);
					}				
				}
			}
			for(DataDictionary dictionary : getDictionaries()) {
				save(dictionary);
			}
			log.info("system data dictionary loaded, "+models.size()+" models loaded...");					
		} catch(Exception e) {
			e.printStackTrace();
		} 
	}
	public void reload() {			
		try	{
			getDictionaries().clear();
			List<File> dictionaries = new ArrayList<File>();
			File homeDir = new File(getPropertyService().getHomeDirectory()+"/resources/dictionaries");
			File[] files = homeDir.listFiles(new FilenameFilter() {
				@Override
				public boolean accept(File dir, String name) {
					return name.endsWith(".json");
				}				
			});
			for(File f : files) {
				dictionaries.add(f);
			}
			List<Model> models = new ArrayList<Model>();
			for(File importFile : dictionaries) {						
				DataDictionary dictionary = mapper.readValue(importFile, DataDictionary.class);				
				getDictionaries().add(dictionary);
				for(Model model : dictionary.getModels()) {
					model.setDictionary(dictionary);
				}
				models.addAll(dictionary.getModels());				
			}
			for(Model model : models) {
				for(Model m : models) {
					for(ModelRelation relation : m.getSourceRelations()) {
						if(relation.getEndName() != null && relation.getEndName().equals(model.getQName()))
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
		String homeDir = getPropertyService().getHomeDirectory() != null ? getPropertyService().getHomeDirectory()+"\\resources\\dictionaries" : "";
		if(homeDir.length() > 0 && !homeDir.endsWith("/")) homeDir = homeDir + "\\";
		File file = new File(homeDir + dictionary.getQName()+".json");		
		try {
			FileWriter writer = new FileWriter(file, false);
			writer.append(mapper.writeValueAsString(dictionary));
			writer.close();
		} catch(Exception e) {
			log.log(Level.SEVERE, "", e);
		}
	}

	@Override
	public ModelObject get(long id) {
		return idMap.get(id);
	}

	@Override
	public void save(ModelObject object) {
		if(object.getId() == null || object.getId() == 0) {
			object.setId(nextId);
			idMap.put(nextId, object);
			nextId++;
		}
		if(object instanceof DataDictionary) {
			DataDictionary dictionary = (DataDictionary)object;
			save(dictionary);
		} else if(object instanceof Model) {
			Model model = (Model)object;
			save(model.getDictionary());
		} else if(object instanceof ModelField) {
			ModelField field = (ModelField)object;
			if(field.getModel() != null) save(field.getModel().getDictionary());
			else if(field.getRelation() != null) save(field.getRelation().getModel().getDictionary());
		} else if(object instanceof ModelRelation) {
			ModelRelation relation = (ModelRelation)object;
			save(relation.getModel().getDictionary());
		} else if(object instanceof ModelFieldValue) {
			ModelFieldValue value = (ModelFieldValue)object;
			save(value.getField().getModel().getDictionary());
		}
		reload();
		log.info("dictionary service initialized.");
	}

	@Override
	public void remove(ModelObject object) {
		idMap.remove(object.getId());
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
