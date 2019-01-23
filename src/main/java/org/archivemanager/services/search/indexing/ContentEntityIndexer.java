package org.archivemanager.services.search.indexing;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.archivemanager.models.RepositoryModel;
import org.archivemanager.models.SystemModel;
import org.archivemanager.services.content.ContentModel;
import org.archivemanager.services.dictionary.DataDictionaryService;
import org.archivemanager.services.entity.Association;
import org.archivemanager.services.entity.Entity;
import org.archivemanager.services.entity.EntityIndexer;
import org.archivemanager.services.entity.EntityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


@Component
public class ContentEntityIndexer implements EntityIndexer{
	@Autowired private DataDictionaryService dictionaryService;
	@Autowired private EntityService entityService;
	
	
	@Override
	public void index(Entity entity, IndexEntity data) {
		if(dictionaryService.isA(entity.getQName(), ContentModel.WEB_LINK)) {
			List<Entity> path = getPath(entity);
			Long[] path_ids = new Long[path.size()];
			StringBuffer path_labels = new StringBuffer();
			for(int i=0; i < path.size(); i++) {
				path_ids[i] = path.get(i).getId();
				path_labels.append(path.get(i).getName()+"__");
			}
			data.getFields().add(new IndexField(SystemModel.PATH.toString(), path_ids));
			data.getFields().add(new IndexField(SystemModel.PATH_LABELS.toString(), path_labels.toString()));
			
			if(path.size() > 0) {
				Entity tail = path.get(path.size()-1);
				data.getFields().add(new IndexField(SystemModel.PARENT_TYPE.toString(), tail.getQName().toString()));
			}
		}
	}

	@Override
	public void index(Association association, IndexAssociation data) {
		
		
	}

	protected List<Entity> getPath(Entity entity) {
		List<Entity> path = new ArrayList<Entity>();
		Association association = entity.getTargetAssociation(ContentModel.WEB_LINKS,RepositoryModel.CATEGORIES,RepositoryModel.ITEMS);
		if(association != null) {
			Entity node = entityService.getEntity(association.getSource());
			while(node != null) {
				path.add(node);
				association = node.getTargetAssociation(ContentModel.WEB_LINKS,RepositoryModel.CATEGORIES,RepositoryModel.ITEMS);
				if(association == null) break;
				node = entityService.getEntity(association.getSource());
			}
		}
		Collections.reverse(path);
		return path;
	}
}
