package org.archivemanager.services.search.indexing;

import org.archivemanager.models.ClassificationModel;
import org.archivemanager.services.dictionary.DataDictionaryService;
import org.archivemanager.services.entity.Entity;
import org.archivemanager.services.entity.EntityService;


public class NotableFigureEntryIndexer extends DefaultEntityIndexer {

	
	public NotableFigureEntryIndexer(DataDictionaryService dictionaryService, EntityService entityService) {
		super(dictionaryService, entityService);
	}
	@Override
	public void index(Entity entity, IndexEntity data) {
		super.index(entity, data);
		
		if(entity.hasProperty(ClassificationModel.COLLECTION_NAME)) {
			data.getFields().add(new IndexField("collection_name_e", entity.getPropertyValue(ClassificationModel.COLLECTION_NAME)));
		}
	}
}
