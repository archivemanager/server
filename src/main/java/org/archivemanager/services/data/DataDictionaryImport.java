package org.archivemanager.services.data;

import javax.annotation.PostConstruct;

import org.archivemanager.models.DictionaryModel;
import org.archivemanager.models.dictionary.DataDictionary;
import org.archivemanager.models.dictionary.Model;
import org.archivemanager.models.dictionary.ModelField;
import org.archivemanager.models.dictionary.ModelRelation;
import org.archivemanager.services.dictionary.DataDictionaryService;
import org.archivemanager.services.entity.Entity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

//@Component
public class DataDictionaryImport {
	@Autowired private DataDictionaryService dictionaryService;
	
	
	@PostConstruct
	public void run() {
		for(DataDictionary dictionary : dictionaryService.getDictionaries()) {
			Entity dictionaryEntity = new Entity(DictionaryModel.DICTIONARY);
			for(Model model : dictionary.getModels()) {
				
				for(ModelField field : model.getFields()) {
					
				}
				for(ModelRelation relation : model.getSourceRelations()) {
					
				}
			}			
		}
	}
}
