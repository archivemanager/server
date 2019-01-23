package org.archivemanager.models.binders;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.archivemanager.models.ClassificationModel;
import org.archivemanager.models.RepositoryModel;
import org.archivemanager.models.SystemModel;
import org.archivemanager.models.repository.Audio;
import org.archivemanager.models.repository.Category;
import org.archivemanager.models.repository.Collection;
import org.archivemanager.models.repository.DigitalObject;
import org.archivemanager.models.repository.Entry;
import org.archivemanager.models.repository.Item;
import org.archivemanager.models.repository.NamedEntity;
import org.archivemanager.models.repository.Note;
import org.archivemanager.models.repository.PathNode;
import org.archivemanager.models.repository.Result;
import org.archivemanager.models.repository.Subject;
import org.archivemanager.models.repository.Video;
import org.archivemanager.models.repository.WebLink;
import org.archivemanager.models.system.Permission;
import org.archivemanager.services.content.ContentModel;
import org.archivemanager.services.entity.Association;
import org.archivemanager.services.entity.Entity;
import org.archivemanager.services.entity.EntityService;
import org.archivemanager.util.HTMLUtility;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


@Component
public class RepositoryModelEntityBinder extends ModelEntityBinder {
	@Autowired private EntityService entityService;
	
	
	
	
	public Result getResult(Entity entity, boolean sources) {
		Result result = null;
		if(entity.getQName().equals(RepositoryModel.COLLECTION))
			result = getCollection(entity, sources);
		else if(entity.getQName().equals(ClassificationModel.ENTRY))
			result = getEntry(0L, entity);
		else if(entity.getQName().equals(ClassificationModel.PERSON) || entity.getQName().equals(ClassificationModel.CORPORATION))
			result = getNamedEntity(0L, entity);
		else if(entity.getQName().equals(ClassificationModel.SUBJECT))
			result = getSubject(0L, entity);
		else if(entity.getQName().equals(RepositoryModel.CATEGORY))
			result = getCategory(0L, entity);		
		else 
			result = getItem(0L, entity);
		if(result != null) {
			result.setUser(entity.getUser());			
		}
		return result;
	}
	
	public Collection getCollection(Entity entity, boolean sources) {
		long id = entity.getId();
		String title = decode(entity.getName());
		String description = entity.getPropertyValueString(RepositoryModel.DESCRIPTION);
		String contentType = entity.getQName().getLocalName();
		if(description == null) description = "";
		Collection collection = new Collection(id, 0L, title, description, contentType);
		
		if(entity.hasProperty(RepositoryModel.URL) && entity.getPropertyValueString(RepositoryModel.URL).length() > 0) 
			collection.setUrl(entity.getPropertyValueString(RepositoryModel.URL));
		if(entity.hasProperty(RepositoryModel.BIOGRAPHICAL_NOTE)) collection.setBioNote(entity.getPropertyValueString(RepositoryModel.BIOGRAPHICAL_NOTE));
		if(entity.hasProperty(RepositoryModel.SCOPE_NOTE)) collection.setScopeNote(entity.getPropertyValueString(RepositoryModel.SCOPE_NOTE));
				
		if(entity.hasProperty(RepositoryModel.DATE_EXPRESSION)) {
			collection.setDateExpression(entity.getPropertyValueString(RepositoryModel.DATE_EXPRESSION));
			if(collection.getName() == null) collection.setName(collection.getDateExpression());			
		}
		if(entity.hasProperty(RepositoryModel.BEGIN_DATE)) collection.setBegin(entity.getPropertyValueString(RepositoryModel.BEGIN_DATE));
		if(entity.hasProperty(RepositoryModel.END_DATE)) collection.setEnd(entity.getPropertyValueString(RepositoryModel.END_DATE));		
		if(entity.hasProperty(RepositoryModel.CONTAINER) ) collection.setContainer(entity.getPropertyValueString(RepositoryModel.CONTAINER));		
		if(entity.hasProperty(RepositoryModel.COMMENT) ) collection.setComment(entity.getPropertyValueString(RepositoryModel.COMMENT));
		if(entity.hasProperty(RepositoryModel.CODE) ) collection.setCode(entity.getPropertyValueString(RepositoryModel.CODE));
		if(entity.hasProperty(RepositoryModel.IDENTIFIER) ) collection.setIdentifier(entity.getPropertyValueString(RepositoryModel.IDENTIFIER));
		if(entity.hasProperty(RepositoryModel.ACCESSION_DATE) ) collection.setAccessionDate(entity.getPropertyValueString(RepositoryModel.ACCESSION_DATE));
		if(entity.hasProperty(RepositoryModel.BULK_BEGIN) ) collection.setBulkBegin(entity.getPropertyValueString(RepositoryModel.BULK_BEGIN));
		if(entity.hasProperty(RepositoryModel.BULK_END) ) collection.setBulkEnd(entity.getPropertyValueString(RepositoryModel.BULK_END));
		if(entity.hasProperty(RepositoryModel.LANGUAGE) ) collection.setLanguage(entity.getPropertyValueString(RepositoryModel.LANGUAGE));
		if(entity.hasProperty(RepositoryModel.EXTENT_UNITS) ) collection.setExtentUnits(entity.getPropertyValueString(RepositoryModel.EXTENT_UNITS));
		if(entity.hasProperty(RepositoryModel.EXTENT_NUMBER) ) collection.setExtentNumber(entity.getPropertyValueString(RepositoryModel.EXTENT_NUMBER));
		if(entity.hasProperty(RepositoryModel.RESTRICTIONS) ) collection.setRestrictions(entity.getPropertyValueBoolean(RepositoryModel.RESTRICTIONS));
		if(entity.hasProperty(RepositoryModel.INTERNAL) ) collection.setInternal(entity.getPropertyValueBoolean(RepositoryModel.INTERNAL));
		if(entity.hasProperty(RepositoryModel.IS_PUBLIC) ) collection.setPublic(entity.getPropertyValueBoolean(RepositoryModel.IS_PUBLIC));
		if(entity.hasProperty(RepositoryModel.SEARCHABLE) ) collection.setSearchable(entity.getPropertyValueBoolean(RepositoryModel.SEARCHABLE));
		
		if(sources && entity.getSourceAssociations().size() > 0) {
			for(int i=0; i < entity.getSourceAssociations().size(); i++) {
				Association association = entity.getSourceAssociations().get(i);
				Entity targetEntity = entityService.getEntity(association.getTarget());
				try {
					if(targetEntity != null) {	
						if(association.getQName().getLocalName().equals("categories")) {
							Category category = getCategory(association.getId(), targetEntity);
							collection.getCategories().add(category);
						} else if(association.getQName().getLocalName().equals("notes")) {					
							if(targetEntity.hasProperty(SystemModel.NOTE_TYPE)) {
								String type = targetEntity.getPropertyValueString(SystemModel.NOTE_TYPE);
								String content = targetEntity.getPropertyValueString(SystemModel.NOTE_CONTENT);
								collection.getNotes().add(new Note(targetEntity.getId(), association.getId(), type, decode(content)));
								if(type.equals("Abstract")) collection.setAbstractNote(decode(content));
							}					
						} else if(association.getQName().getLocalName().equals("named_entities")) {
							String localName = targetEntity.getQName().getLocalName();
							NamedEntity name = getNamedEntity(association.getId(), targetEntity);
							if(localName.equals("person")) collection.getPeople().add(name);
							if(localName.equals("corporation")) collection.getCorporations().add(name);
						} else if(association.getQName().getLocalName().equals("subjects")) {
							Subject subject = getSubject(association.getId(), targetEntity);
							collection.getSubjects().add(subject);
						} else if(association.getQName().getLocalName().equals("files")) {
							DigitalObject file = getDigitalObject(association.getId(), targetEntity);
							collection.getDigitalObjects().add(file);
						} else if(association.getQName().getLocalName().equals("web_links")) {
							WebLink file = getWebLink(association.getId(), targetEntity);
							collection.getWeblinks().add(file);
						} else if(association.getQName().getLocalName().equals("permissions")) {
							Permission permission = getPermission(association.getId(), targetEntity);
							collection.getPermissions().add(permission);
						}
					}
				} catch(Exception e) {
					e.printStackTrace();
				}
			}
		}

		Collections.sort(collection.getPeople(), sorter);
		Collections.sort(collection.getCorporations(), sorter);
		Collections.sort(collection.getSubjects(), sorter);
		return collection;
	}	
	public Permission getPermission(long assoc, Entity entity) {
		Permission permission = new Permission();
		permission.setId(entity.getId());
		permission.setLevel(Integer.valueOf(entity.getPropertyValueString(SystemModel.PERMISSION_LEVEL)));
		permission.setName(entity.getName());
		permission.setSource(Long.valueOf(entity.getPropertyValueString(SystemModel.PERMISSION_SOURCE)));
		permission.setTarget(Long.valueOf(entity.getPropertyValueString(SystemModel.PERMISSION_TARGET)));
		return permission;
	}	
	public Category getCategory(long assoc, Entity entity) {
		Category category = new Category(entity.getId(), assoc, entity.getName());
		category.setDescription(entity.getPropertyValueString(RepositoryModel.DESCRIPTION));
		category.setContentType("category");
		if(entity.getSourceAssociations().size() > 0) {
			for(int i=0; i < entity.getSourceAssociations().size(); i++) {
				Association association = entity.getSourceAssociations().get(i);
				Entity targetEntity = entityService.getEntity(association.getTarget());
				try {
					if(association.getQName().getLocalName().equals("items")) {
						Item item = getItem(assoc, targetEntity);
						category.getItems().add(item);
					} else if(association.getQName().getLocalName().equals("categories")) {
						Category childCategory = getCategory(assoc, targetEntity);
						category.getCategories().add(childCategory);
					}					
				} catch(Exception e) {
					e.printStackTrace();
				}
				
			}
		}
		
		return category;
	}
	
	public Entry getEntry(long assoc, Entity entity) {
		long id = entity.getId();
		String title = entity.getName();
		String description = entity.getPropertyValueString(RepositoryModel.DESCRIPTION);
		String contentType = cleanContentType(entity.getQName().getLocalName());
		Entry entry = new Entry(id, assoc, title, description, contentType);
		
		if(entity.hasProperty(ClassificationModel.COLLECTION_NAME)) 
			entry.setCollection(entity.getPropertyValueString(ClassificationModel.COLLECTION_NAME));
		if(entity.hasProperty(ClassificationModel.ITEMS)) {
			String itemDescription = (String)entity.getProperty(ClassificationModel.ITEMS).getValue();
			itemDescription = itemDescription.replace("\n", "<br>");
			entry.setDescription(itemDescription);
		}
		
		if(entity.getTargetAssociations().size() > 0) {
			for(int i=0; i < entity.getTargetAssociations().size(); i++) {
				Association association = entity.getTargetAssociations().get(i);
				if(association.getQName().getLocalName().equals("entries")) {
					Entity sourceEntity = entityService.getEntity(association.getSource());
					try {
						entry.setNamedEntity(String.valueOf(association.getSource()));
						if(sourceEntity != null) {											
							entry.setName(sourceEntity.getName());
						}
					} catch(Exception e) {
						e.printStackTrace();
					}
				}
			}
		}
		return entry;
	}
	
	public Item getItem(long assoc, Entity entity) {
		long id = entity.getId();
		String title = entity.getName();
		String description = entity.getPropertyValueString(RepositoryModel.DESCRIPTION);
		String contentType = entity.getQName().getLocalName();
		Item item = new Item(id, assoc, title, description, contentType);
		if(entity.hasProperty(RepositoryModel.LANGUAGE)) item.setLanguage(getLanguageLabel(entity.getPropertyValueString(RepositoryModel.LANGUAGE)));
		else item.setLanguage("English");
		if(entity.hasProperty(RepositoryModel.CONTAINER)) item.setContainer(entity.getPropertyValueString(RepositoryModel.CONTAINER));
		if(entity.hasProperty(RepositoryModel.SUMMARY) && entity.getPropertyValueString(RepositoryModel.SUMMARY).length() > 0) item.setSummary(entity.getPropertyValueString(RepositoryModel.SUMMARY));
		if(entity.hasProperty(RepositoryModel.DATE_EXPRESSION)) {
			if(item.getName() == null) item.setName(entity.getPropertyValueString(RepositoryModel.DATE_EXPRESSION));
			item.setDateExpression(entity.getPropertyValueString(RepositoryModel.DATE_EXPRESSION));
		}

		Map<String,Object> entityPropMap = new HashMap<>();
		entity.getPropertyMap().forEach((key, object) -> entityPropMap.put(key,object));
		item.setData(entityPropMap);

		List<Entity> path = getComponentPath(entity);
		if(path.size() > 0) {
			for(int i=0; i < path.size(); i++) {
				Entity node = path.get(i);
				PathNode pathNode = new PathNode(String.valueOf(node.getId()), node.getName());
				item.getPath().add(pathNode);
				if(node.getQName().equals(RepositoryModel.COLLECTION)) {
					item.setCollectionId(String.valueOf(node.getId()));
					item.setCollectionName(node.getName());
					item.setCollectionUrl(node.getPropertyValueString(RepositoryModel.URL));
				}
			}
		}
		if(entity.getSourceAssociations().size() > 0) {
			for(int i=0; i < entity.getSourceAssociations().size(); i++) {
				Association association = entity.getSourceAssociations().get(i);
				Entity targetEntity = entityService.getEntity(association.getTarget());
				try {
					if(targetEntity != null) {	
						if(association.getQName().getLocalName().equals("notes")) {					
							if(targetEntity.hasProperty(SystemModel.NOTE_TYPE)) {
							String type = targetEntity.getPropertyValueString(SystemModel.NOTE_TYPE);
							String content = targetEntity.getPropertyValueString(SystemModel.NOTE_CONTENT);
							if(type.equals("General note") || type.equals("General Physical Description note") ||
								type.equals("Table of Contents")) {
								item.getNotes().add(new Note(targetEntity.getId(), association.getId(), type, content));
							}
							if(type.equals("Abstract")) item.setAbstractNote(decode(content));
							if(type.equals("Language of Materials note")) {
								item.setNativeContent(content);
							}
						}					
						} else if(association.getQName().getLocalName().equals("named_entities")) {
							String localName = targetEntity.getQName().getLocalName();
							NamedEntity name = getNamedEntity(association.getId(), targetEntity);
							if(localName.equals("person")) item.getPeople().add(name);
							if(localName.equals("corporation")) item.getCorporations().add(name);
						} else if(association.getQName().getLocalName().equals("subjects")) {
							Subject subject = getSubject(association.getId(), targetEntity);
							item.getSubjects().add(subject);
						} else if(association.getQName().getLocalName().equals("files")) {
							DigitalObject file = getDigitalObject(association.getId(), targetEntity);
							item.getDigitalObjects().add(file);
						} else if(association.getQName().getLocalName().equals("web_links")) {
							WebLink file = getWebLink(association.getId(), targetEntity);
							item.getWeblinks().add(file);
						}
					}
				} catch(Exception e) {
					e.printStackTrace();
				}
			}
		}
		Collections.sort(item.getPeople(), sorter);
		Collections.sort(item.getCorporations(), sorter);
		Collections.sort(item.getSubjects(), sorter);
		return item;
	}
	
	public NamedEntity getNamedEntity(long assoc, Entity entity) {
		long id = entity.getId();
		String title = HTMLUtility.removeTags(entity.getName());
		String description = entity.getPropertyValueString(RepositoryModel.DESCRIPTION);
		String contentType = cleanContentType(entity.getQName().getLocalName());
		NamedEntity name = new NamedEntity(id, assoc, title, description, contentType);
		
		if(entity.hasProperty(RepositoryModel.FUNCTION)) name.setFunction(entity.getPropertyValueString(RepositoryModel.FUNCTION));
		if(entity.hasProperty(ClassificationModel.RULE)) name.setRole(entity.getPropertyValueString(ClassificationModel.RULE));
		if(entity.hasProperty(ClassificationModel.NOTE)) name.setNote(entity.getPropertyValueString(ClassificationModel.NOTE));
		if(entity.hasProperty(ClassificationModel.DATES)) name.setDates(entity.getPropertyValueString(ClassificationModel.DATES));
		if(entity.hasProperty(ClassificationModel.SOURCE)) name.setSource(entity.getPropertyValueString(ClassificationModel.SOURCE));
				
		if(entity.getSourceAssociations().size() > 0) {
			for(int i=0; i < entity.getSourceAssociations().size(); i++) {
				Association association = entity.getSourceAssociations().get(i);
				if(association.getQName().getLocalName().equals("entries")) {
					Entity targetEntity = entityService.getEntity(association.getTarget());
					try {
						Entry entry = getEntry(association.getId(), targetEntity);
						name.getEntries().add(entry);
					} catch(Exception e) {
						e.printStackTrace();
					}
				}
			}
		}
		if(entity.getTargetAssociations().size() > 0) {
			for(int i=0; i < entity.getTargetAssociations().size(); i++) {
				Association association = entity.getTargetAssociations().get(i);
				if(association.getQName().getLocalName().equals("named_entities")) {
					Entity sourceEntity = entityService.getEntity(association.getSource());
					try {
						if(sourceEntity.getQName().equals(RepositoryModel.COLLECTION)) {
							Collection collection = getCollection(sourceEntity, false);
							name.getCollections().add(collection);
						}
					} catch(Exception e) {
						e.printStackTrace();
					}
				}
			}
		}
		return name;
	}	
	public Audio getAudio(long assoc, Entity entity) {
		long id = entity.getId();
		String title = entity.getName();
		String description = entity.getPropertyValueString(RepositoryModel.DESCRIPTION);
		String contentType = cleanContentType(entity.getQName().getLocalName());
		Audio audio = new Audio(id, assoc, title, description, contentType);
		if(entity.hasProperty(RepositoryModel.DATE_EXPRESSION)) {
			audio.setDateExpression(entity.getPropertyValueString(RepositoryModel.DATE_EXPRESSION));
		}
		if(entity.getSourceAssociations().size() > 0) {
			for(int i=0; i < entity.getSourceAssociations().size(); i++) {
				Association association = entity.getSourceAssociations().get(i);
				Entity targetEntity = entityService.getEntity(association.getTarget());
				try {
					if(targetEntity != null) {
						if(association.getQName().getLocalName().equals("web_links")) {
							WebLink file = getWebLink(association.getId(), targetEntity);
							audio.getWeblinks().add(file);
						} else if(association.getQName().getLocalName().equals("named_entities")) {
							String localName = targetEntity.getQName().getLocalName();
							NamedEntity name = getNamedEntity(association.getId(), targetEntity);
							if(localName.equals("person")) audio.getPeople().add(name);
							if(localName.equals("corporation")) audio.getCorporations().add(name);
						} else if(association.getQName().getLocalName().equals("subjects")) {
							Subject subject = getSubject(association.getId(), targetEntity);
							audio.getSubjects().add(subject);
						}
					}
				} catch(Exception e) {
					e.printStackTrace();
				}
			}
		}
		for(WebLink link : audio.getWeblinks()) {
			if(link.getType().equals("avatar"))
				audio.setAvatar(link.getUrl());
			else if(link.getType().equals("rendition"))
				audio.setRendition(link.getUrl());
		}
		return audio;
	}
	
	public Subject getSubject(long assoc, Entity entity) {
		String title = entity.getName();
		String description = entity.getPropertyValueString(SystemModel.DESCRIPTION);
		String contentType = cleanContentType(entity.getQName().getLocalName());
		Subject subject = new Subject(entity.getId(), assoc, title, description, contentType);
		if(entity.hasProperty(ClassificationModel.TYPE)) subject.setType(entity.getPropertyValueString(ClassificationModel.TYPE));
		if(entity.hasProperty(ClassificationModel.SOURCE)) subject.setSource(entity.getPropertyValueString(ClassificationModel.SOURCE));
		List<Association> collectionAssociations = entity.getTargetAssociations(RepositoryModel.COLLECTION);
		for(Association collectionAssociation : collectionAssociations) {
			Entity sourceEntity = entityService.getEntity(collectionAssociation.getSource());
			try {
				if(sourceEntity != null) {
					Collection collection = getCollection(sourceEntity, false);
					subject.getCollections().add(collection);
				}
			} catch(Exception e) {
				e.printStackTrace();
			}
		}
		return subject;
	}
	public Video getVideo(Entity entity) {
		return getVideo(0L, entity);
	}
	
	public Video getVideo(long assoc, Entity entity) {
		long id = entity.getId();
		String title = entity.getName();
		String description = entity.getPropertyValueString(RepositoryModel.DESCRIPTION);
		String contentType = cleanContentType(entity.getQName().getLocalName());
		Video video = new Video(id, assoc, title, description, contentType);
		if(entity.hasProperty(RepositoryModel.DATE_EXPRESSION)) {
			video.setDateExpression(entity.getPropertyValueString(RepositoryModel.DATE_EXPRESSION));
		}
		if(entity.getSourceAssociations().size() > 0) {
			for(int i=0; i < entity.getSourceAssociations().size(); i++) {
				Association association = entity.getSourceAssociations().get(i);
				Entity targetEntity = entityService.getEntity(association.getSource());
				try {
					if(targetEntity != null) {
						if(association.getQName().getLocalName().equals("web_links")) {
							WebLink file = getWebLink(association.getId(), targetEntity);
							video.getWeblinks().add(file);
						} else if(association.getQName().getLocalName().equals("named_entities")) {
							String localName = targetEntity.getQName().getLocalName();
							NamedEntity name = getNamedEntity(association.getId(), targetEntity);
							if(localName.equals("person")) video.getPeople().add(name);
							if(localName.equals("corporation")) video.getCorporations().add(name);
						} else if(association.getQName().getLocalName().equals("subjects")) {
							Subject subject = getSubject(association.getId(), targetEntity);
							video.getSubjects().add(subject);
						}
					}
				} catch(Exception e) {
					e.printStackTrace();
				}
			}
		}
		for(WebLink link : video.getWeblinks()) {
			if(link.getType().equals("avatar"))
				video.setAvatar(link.getUrl());
			else if(link.getType().equals("rendition"))
				video.setRendition(link.getUrl());
		}
		return video;
	}	
	public DigitalObject getDigitalObject(long assoc, Entity entity) {
		DigitalObject file = new DigitalObject();
		file.setId(String.valueOf(entity.getId()));
		file.setUuid(entity.getUid());
		file.setTitle(entity.getName());						
		if(entity.hasProperty(ContentModel.TYPE)) file.setType(entity.getPropertyValueString(ContentModel.TYPE));
		//if(source.has("group")) file.setGroup(source.getString("group"));
		//if(source.has("order")) file.setOrder(source.getInt("order"));
		return file;
	}
	
	public WebLink getWebLink(long assoc, Entity entity) {
		WebLink file = new WebLink();
		file.setId(String.valueOf(entity.getId()));
		if(entity.hasProperty(SystemModel.URL)) {
			file.setUrl(entity.getPropertyValueString(SystemModel.URL));
			int endIdx = file.getUrl().indexOf("/", 7);
			if(endIdx > -1) file.setDomain(file.getUrl().substring(0, endIdx));
		}
		file.setTitle(entity.getName());
		if(entity.hasProperty(ContentModel.TYPE)) file.setType(entity.getPropertyValueString(ContentModel.TYPE));
		return file;
	}
			
	protected List<Entity> getComponentPath(Entity comp) {
		List<Entity> path = new ArrayList<Entity>();
		if(comp != null) {
			Association parent = comp.getTargetAssociation(RepositoryModel.CATEGORIES, RepositoryModel.ITEMS);
			while(parent != null && parent.getSource() != null) {
				try {
					Entity p = entityService.getEntity(parent.getSource());
					path.add(p);
					parent = p.getTargetAssociation(RepositoryModel.CATEGORIES, RepositoryModel.ITEMS);
				} catch(Exception e) {
					e.printStackTrace();
				}
			}
		}
		Collections.reverse(path);
		return path;
	}	
}