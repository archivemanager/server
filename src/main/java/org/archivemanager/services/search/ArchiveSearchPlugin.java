package org.archivemanager.services.search;

import org.archivemanager.data.Sort;
import org.archivemanager.models.ClassificationModel;
import org.archivemanager.models.RepositoryModel;
import org.archivemanager.models.dictionary.ModelField;
import org.archivemanager.models.system.QName;

public class ArchiveSearchPlugin implements SearchPlugin {
	
	private QName[] archiveQNames = new QName[]{RepositoryModel.COLLECTION,ClassificationModel.SUBJECT,ClassificationModel.PERSON,ClassificationModel.CORPORATION};
	
	@Override
	public void initialize() {}

	@Override
	public void request(SearchRequest request) {
		if(request.getContext() != null && request.getContext().equals("archive")) {
			request.setQNames(archiveQNames);
			request.addSort(new Sort(ModelField.TYPE_SMALLTEXT, "score", "desc"));
		}
	}

	@Override
	public void response(SearchRequest request, SearchResponse response) {
		
		
	}

}
