package org.archivemanager.services.search.indexing;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.archivemanager.models.ClassificationModel;
import org.archivemanager.models.RepositoryModel;
import org.archivemanager.models.SystemModel;
import org.archivemanager.models.system.QName;
import org.archivemanager.services.content.ContentModel;
import org.archivemanager.services.dictionary.DataDictionaryService;
import org.archivemanager.services.entity.Association;
import org.archivemanager.services.entity.Entity;
import org.archivemanager.services.entity.EntityIndexer;
import org.archivemanager.services.entity.EntityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


@Component
public class RepositoryEntityIndexer implements EntityIndexer {
	@Autowired private DataDictionaryService dictionaryService;
	@Autowired private EntityService entityService;
	
	public RepositoryEntityIndexer(DataDictionaryService dictionaryService, EntityService entityService) {
		this.dictionaryService = dictionaryService;
		this.entityService = entityService;
	}
	
	@Override
	public void index(Entity entity, IndexEntity data) {	
		if(entity.getQName().equals(RepositoryModel.COLLECTION))
			data.getFields().add(new IndexField("sort_", 4L));
		else if(entity.getQName().equals(ClassificationModel.PERSON) || entity.getQName().equals(ClassificationModel.CORPORATION) || entity.getQName().equals(ClassificationModel.FAMILY))
			data.getFields().add(new IndexField("sort_", 3L));
		else if(entity.getQName().equals(ClassificationModel.SUBJECT))
			data.getFields().add(new IndexField("sort_", 2L));
		else
			data.getFields().add(new IndexField("sort_", 0L));
		
		if(dictionaryService.isA(entity.getQName(), RepositoryModel.ITEM)) {
			Association parent_assoc = entity.getTargetAssociation(RepositoryModel.CATEGORIES);
			if(parent_assoc == null) parent_assoc = entity.getTargetAssociation(RepositoryModel.ITEMS);
			if(parent_assoc != null && parent_assoc.getSource() != null) {
				try {
					Entity sourceEntity = entityService.getEntity(parent_assoc.getSource());
					if(sourceEntity != null) {
						data.getFields().add(new IndexField("parent_id", sourceEntity.getId()));
						data.getFields().add(new IndexField("parent_qname", sourceEntity.getQName().toString()));
						List<Entity> path = getPath(sourceEntity);
						Long[] path_ids = new Long[path.size()];
						StringBuffer path_labels = new StringBuffer();
						for(int i=0; i < path.size(); i++) {
							path_ids[i] = path.get(i).getId();
							path_labels.append(path.get(i).getName()+"__");
							if(i==0) data.getFields().add(new IndexField(RepositoryModel.COLLECTION_ID.toString(), path_ids[0]));
						}
						data.getFields().add(new IndexField(SystemModel.PATH.toString(), path_ids));
						data.getFields().add(new IndexField(SystemModel.PATH_LABELS.toString(), path_labels.toString()));
					}
				} catch(Exception e) {
					e.printStackTrace();
				}
			}
			List<Association> subject_assocs = entity.getSourceAssociations(ClassificationModel.SUBJECTS);
			if(subject_assocs != null && subject_assocs.size() > 0) {
				try {
					Long[] subj_ids = new Long[subject_assocs.size()];
					StringBuffer subj_labels = new StringBuffer();
					for(int i=0; i < subject_assocs.size(); i++) {
						Association subject_assoc = subject_assocs.get(i);
						Entity targetEntity = entityService.getEntity(subject_assoc.getTarget());
						subj_ids[i] = targetEntity.getId();
						subj_labels.append(targetEntity.getName()+"__");
					}
					data.getFields().add(new IndexField(ClassificationModel.SUBJECTS.toString(), subj_ids));
					data.getFields().add(new IndexField(ClassificationModel.SUBJECTS_LABELS.toString(), subj_labels.toString()));
					
				} catch(Exception e) {
					e.printStackTrace();
				}
			}
			List<Association> people_assocs = entity.getSourceAssociations(ClassificationModel.PEOPLE);
			if(people_assocs != null && people_assocs.size() > 0) {
				try {
					Long[] people_ids = new Long[people_assocs.size()];
					StringBuffer people_labels = new StringBuffer();
					for(int i=0; i < people_assocs.size(); i++) {
						Association people_assoc = people_assocs.get(i);
						Entity targetEntity = entityService.getEntity(people_assoc.getTarget());
						people_ids[i] = targetEntity.getId();
						people_labels.append(targetEntity.getName()+"__");
					}
					data.getFields().add(new IndexField(ClassificationModel.PEOPLE.toString(), people_ids));
					data.getFields().add(new IndexField(ClassificationModel.PEOPLE_LABELS.toString(), people_labels.toString()));
					
				} catch(Exception e) {
					e.printStackTrace();
				}
			}
			List<Association> corp_assocs = entity.getSourceAssociations(ClassificationModel.CORPORATIONS);
			if(corp_assocs != null && corp_assocs.size() > 0) {
				try {
					Long[] corp_ids = new Long[corp_assocs.size()];
					StringBuffer corp_labels = new StringBuffer();
					for(int i=0; i < corp_assocs.size(); i++) {
						Association corp_assoc = corp_assocs.get(i);
						Entity targetEntity = entityService.getEntity(corp_assoc.getTarget());
						corp_ids[i] = targetEntity.getId();
						corp_labels.append(targetEntity.getName()+"__");
					}
					data.getFields().add(new IndexField(ClassificationModel.CORPORATIONS.toString(), corp_ids));
					data.getFields().add(new IndexField(ClassificationModel.CORPORATIONS_LABELS.toString(), corp_labels.toString()));
					
				} catch(Exception e) {
					e.printStackTrace();
				}
			}
			List<Association> link_assocs = entity.getSourceAssociations(ContentModel.WEB_LINKS);
			if(link_assocs != null && link_assocs.size() > 0) {
				try {
					Long[] link_ids = new Long[link_assocs.size()];
					StringBuffer link_labels = new StringBuffer();
					for(int i=0; i < link_assocs.size(); i++) {
						Association link_assoc = link_assocs.get(i);
						Entity targetEntity = entityService.getEntity(link_assoc.getTarget());
						link_ids[i] = targetEntity.getId();
						link_labels.append(targetEntity.getPropertyValueString(SystemModel.URL)+"__");
					}
					data.getFields().add(new IndexField(ContentModel.WEB_LINKS.toString(), link_ids));
					data.getFields().add(new IndexField(ContentModel.WEB_LINKS_LABELS.toString(), link_labels.toString()));
					
				} catch(Exception e) {
					e.printStackTrace();
				}
			}
		}
		
		for(Association assoc : entity.getSourceAssociations()) {			
			try {
				Entity node = entityService.getEntity(assoc.getTarget());
				if(node != null) {
					QName nodeQname = node.getQName();
					if(nodeQname.equals(SystemModel.NOTE)) {
						String type = node.hasProperty(new QName(SystemModel.NOTE_TYPE.toString())) ? node.getPropertyValueString(SystemModel.NOTE_TYPE) : null;
						if(type != null) {
							if(type.equals("General note") || type.equals("Abstract") || 
									type.equals("General Physical Description note") || 
									type.equals("Table of Contents")) {
								String property = node.hasProperty(new QName(SystemModel.NOTE_CONTENT.toString())) ? node.getPropertyValueString(SystemModel.NOTE_CONTENT) : null;
								if(property != null) {
									String content = property.toString();
									if(content != null && content.length() > 0) {
										data.getFields().add(new IndexField(SystemModel.NOTE.toString(), content));
										//data.appendFreeText(content);
									} 
								}
							}
						}
					}
				}
			} catch(Exception e) {
				data.getMessages().add("problem loading source entity id:"+assoc.getTarget()+" uid:"+assoc.getTargetUid());
				e.printStackTrace();
			}
		}
	}
	@Override
	public void index(Association association, IndexAssociation data) {
		
	}
	protected List<Entity> getPath(Entity entity) {
		List<Entity> path = new ArrayList<Entity>();
		path.add(entity);
		Association association = entity.getTargetAssociation(RepositoryModel.CATEGORIES,RepositoryModel.ITEMS);
		if(association != null) {
			Entity node = entityService.getEntity(association.getSource());
			while(node != null) {
				path.add(node);
				association = node.getTargetAssociation(RepositoryModel.CATEGORIES,RepositoryModel.ITEMS);
				if(association == null) break;
				node = entityService.getEntity(association.getSource());
			}
		}
		Collections.reverse(path);
		return path;
	}
}
