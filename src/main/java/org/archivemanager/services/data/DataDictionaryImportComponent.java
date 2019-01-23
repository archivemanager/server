package org.archivemanager.services.data;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;

import org.archivemanager.config.PropertyConfiguration;
import org.archivemanager.models.dictionary.DataDictionary;
import org.archivemanager.models.dictionary.Model;
import org.archivemanager.models.dictionary.ModelRelation;
import org.archivemanager.services.dictionary.XmlDictionaryImporter;
import org.archivemanager.util.FileUtility;
import org.springframework.beans.factory.annotation.Autowired;

import com.fasterxml.jackson.databind.ObjectMapper;


//@Component
public class DataDictionaryImportComponent {
	private final static Logger log = Logger.getLogger(DataDictionaryImportComponent.class.getName());
	@Autowired private ObjectMapper mapper;
	@Autowired private PropertyConfiguration propertyService;
	
	private List<DataDictionary> dictionaries = new ArrayList<DataDictionary>();
	
	
	@PostConstruct
	public void initialize() {			
		try	{
			List<String> dictionaries = new ArrayList<String>();//propertyService.getDictionaries();
			List<Model> systemModels = processImports(dictionaries);
			System.out.println("system data dictionary loaded, "+systemModels.size()+" models loaded...");					
		} catch(Exception e) {
			e.printStackTrace();
		} 
	}
	
	protected DataDictionary processImport(String importFile) throws Exception {
		XmlDictionaryImporter importer = new XmlDictionaryImporter();
		InputStream stream = null;		
		if(importFile.startsWith("http://")) {
			URL url = new URL(importFile);
	        stream = url.openConnection().getInputStream();			
		} else {
			String homeDir = propertyService.getHomeDirectory() != null ? propertyService.getHomeDirectory() : "";
			if(homeDir.length() > 0 && !homeDir.endsWith("/")) homeDir = homeDir + "/";
			File file = new File(homeDir + importFile);		
			if(file.exists()) 
				stream = new FileInputStream(file); 
			else {
				try {
					URL url = Thread.currentThread().getContextClassLoader().getResource(importFile);
					stream = url.openStream();
				} catch(Exception e) {
					log.log(Level.SEVERE, "error opening importFile:"+importFile, e);
				}
			}
		}
		importer.process(stream);
		return importer.getDictionary();
	}
	public List<Model> processImports(List<String> imports) {
		List<Model> models = new ArrayList<Model>();		
		long dictionaryId = 1;
		try {
			for(String importFile : imports) {					
				DataDictionary dictionary = processImport(importFile);
				dictionary.setId(dictionaryId);
				dictionaries.add(dictionary);
				models.addAll(dictionary.getModels());
				dictionaryId++;
				save(dictionary);
			}
			long modelId = 1;
			for(Model model : models) {
				model.setId(modelId);
				modelId++;
				for(Model m : models) {
					for(ModelRelation relation : m.getSourceRelations()) {
						if(relation.getEndName().equals(model.getQName()))
							model.getTargetRelations().add(relation);
					}
					if(m.getParentName() != null && m.getParentName().toString().equals(model.getQName().toString())) {
						m.setParent(model);
						model.getChildren().add(m);
					}				
				}
			}
		} catch(Exception e) {
			log.log(Level.SEVERE, "", e);
		}
		return models;
	}
	public void save(DataDictionary dictionary) {
		String homeDir = propertyService.getHomeDirectory() != null ? propertyService.getHomeDirectory()+"/resources/dictionaries" : "";
		if(homeDir.length() > 0 && !homeDir.endsWith("/")) homeDir = homeDir + "/";
		File file = new File(homeDir + dictionary.getQName()+".json");
		try {
			FileUtility.writeToFile(mapper.writeValueAsString(dictionary), file);
		} catch(Exception e) {
			log.log(Level.SEVERE, "", e);
		}
	}

}
