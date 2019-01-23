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
import org.archivemanager.services.search.SearchRequest;
import org.archivemanager.services.search.SearchResponse;
import org.archivemanager.services.search.SearchResult;
import org.archivemanager.services.search.SearchService;
import org.archivemanager.util.HTMLUtility;
import org.springframework.stereotype.Component;


@Component
public class RepositoryModelSearchBinder extends EntityBinderSupport {
	private SearchService searchService;
	
	
	public RepositoryModelSearchBinder(SearchService searchService) {
		this.searchService = searchService;
	}
	
	public Result getResult(SearchResult entity) {
		Result result = null;
		if(entity.getQName().equals(RepositoryModel.COLLECTION))
			result = getCollection(entity);
		else if(entity.getQName().equals(ClassificationModel.ENTRY))
			result = getEntry(entity);
		else if(entity.getQName().equals(ClassificationModel.PERSON) || entity.getQName().equals(ClassificationModel.CORPORATION))
			result = getNamedEntity(entity);
		else if(entity.getQName().equals(ClassificationModel.SUBJECT))
			result = getSubject(entity);
		else if(entity.getQName().equals(RepositoryModel.CATEGORY))
			result = getCategory(entity);
		else 
			result = getItem(entity);
		if(result != null) {
			result.setUser(entity.getUser());			
		}
		return result;
	}
	
	public Collection getCollection(SearchResult entity) {
		long id = entity.getId();
		String title = decode(entity.getName());
		String description = entity.getString(RepositoryModel.DESCRIPTION);
		String contentType = entity.getQName().getLocalName();
		if(description == null) description = "";
		Collection collection = new Collection(id, 0L, title, description, contentType);
		
		if(entity.hasProperty(RepositoryModel.URL) && entity.getString(RepositoryModel.URL).length() > 0) 
			collection.setUrl(entity.getString(RepositoryModel.URL));
		if(entity.hasProperty(RepositoryModel.BIOGRAPHICAL_NOTE)) collection.setBioNote(entity.getString(RepositoryModel.BIOGRAPHICAL_NOTE));
		if(entity.hasProperty(RepositoryModel.SCOPE_NOTE)) collection.setScopeNote(entity.getString(RepositoryModel.SCOPE_NOTE));
				
		if(entity.hasProperty(RepositoryModel.DATE_EXPRESSION)) {
			collection.setDateExpression(entity.getString(RepositoryModel.DATE_EXPRESSION));
			if(collection.getName() == null) collection.setName(collection.getDateExpression());			
		}
		if(entity.hasProperty(RepositoryModel.BEGIN_DATE)) collection.setBegin(entity.getString(RepositoryModel.BEGIN_DATE));
		if(entity.hasProperty(RepositoryModel.END_DATE)) collection.setEnd(entity.getString(RepositoryModel.END_DATE));		
		if(entity.hasProperty(RepositoryModel.CONTAINER) ) collection.setContainer(entity.getString(RepositoryModel.CONTAINER));		
		if(entity.hasProperty(RepositoryModel.COMMENT) ) collection.setComment(entity.getString(RepositoryModel.COMMENT));
		if(entity.hasProperty(RepositoryModel.CODE) ) collection.setCode(entity.getString(RepositoryModel.CODE));
		if(entity.hasProperty(RepositoryModel.IDENTIFIER) ) collection.setIdentifier(entity.getString(RepositoryModel.IDENTIFIER));
		if(entity.hasProperty(RepositoryModel.ACCESSION_DATE) ) collection.setAccessionDate(entity.getString(RepositoryModel.ACCESSION_DATE));
		if(entity.hasProperty(RepositoryModel.BULK_BEGIN) ) collection.setBulkBegin(entity.getString(RepositoryModel.BULK_BEGIN));
		if(entity.hasProperty(RepositoryModel.BULK_END) ) collection.setBulkEnd(entity.getString(RepositoryModel.BULK_END));
		if(entity.hasProperty(RepositoryModel.LANGUAGE) ) collection.setLanguage(entity.getString(RepositoryModel.LANGUAGE));
		if(entity.hasProperty(RepositoryModel.EXTENT_UNITS) ) collection.setExtentUnits(entity.getString(RepositoryModel.EXTENT_UNITS));
		if(entity.hasProperty(RepositoryModel.EXTENT_NUMBER) ) collection.setExtentNumber(entity.getString(RepositoryModel.EXTENT_NUMBER));
		if(entity.hasProperty(RepositoryModel.RESTRICTIONS) ) collection.setRestrictions(entity.getBoolean(RepositoryModel.RESTRICTIONS));
		if(entity.hasProperty(RepositoryModel.INTERNAL) ) collection.setInternal(entity.getBoolean(RepositoryModel.INTERNAL));
		if(entity.hasProperty(RepositoryModel.IS_PUBLIC) ) collection.setPublic(entity.getBoolean(RepositoryModel.IS_PUBLIC));
		
		SearchRequest request = new SearchRequest();
		request.setIndex("associations");
		request.addParameter(SystemModel.SOURCE, entity.getId());
		SearchResponse response = searchService.search(request);
		List<SearchResult> associations = response.getResults();
		if(associations.size() > 0) {
			for(int i=0; i < associations.size(); i++) {
				SearchResult association = associations.get(i);
				try {
					if(association != null) {	
						if(association.getQName().getLocalName().equals("categories")) {
							Category category = getCategory(association);
							collection.getCategories().add(category);
						} else if(association.getQName().getLocalName().equals("notes")) {					
							if(association.hasProperty(SystemModel.NOTE_TYPE)) {
								String type = (String)association.get(SystemModel.NOTE_TYPE.toString());
								String content = (String)association.get(SystemModel.NOTE_CONTENT.toString());
								collection.getNotes().add(new Note(association.getId(), association.getId(), type, decode(content)));
								if(type.equals("Abstract")) collection.setAbstractNote(decode(content));
							}					
						} else if(association.getQName().getLocalName().equals("named_entities")) {
							String localName = association.getQName().getLocalName();
							NamedEntity name = getNamedEntity(association);
							if(localName.equals("person")) collection.getPeople().add(name);
							if(localName.equals("corporation")) collection.getCorporations().add(name);
						} else if(association.getQName().getLocalName().equals("subjects")) {
							Subject subject = getSubject(association);
							collection.getSubjects().add(subject);
						} else if(association.getQName().getLocalName().equals("files")) {
							DigitalObject file = getDigitalObject(association);
							collection.getDigitalObjects().add(file);
						} else if(association.getQName().getLocalName().equals("web_links")) {
							WebLink file = getWebLink(association);
							collection.getWeblinks().add(file);
						} else if(association.getQName().getLocalName().equals("permissions")) {
							Permission permission = getPermission(association);
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
	public NamedEntity getNamedEntity(SearchResult node) {
		SearchResult entity = searchService.search("nodes", node.getTarget());
		String title = HTMLUtility.removeTags(entity.getName());
		String description = entity.getString(RepositoryModel.DESCRIPTION);
		String contentType = cleanContentType(entity.getQName().getLocalName());
		NamedEntity name = new NamedEntity(entity.getId(), 0L, title, description, contentType);
		
		if(entity.hasProperty(RepositoryModel.FUNCTION)) name.setFunction(entity.getString(RepositoryModel.FUNCTION));
		if(entity.hasProperty(ClassificationModel.RULE)) name.setRole(entity.getString(ClassificationModel.RULE));
		if(entity.hasProperty(ClassificationModel.NOTE)) name.setNote(entity.getString(ClassificationModel.NOTE));
		if(entity.hasProperty(ClassificationModel.DATES)) name.setDates(entity.getString(ClassificationModel.DATES));
		if(entity.hasProperty(ClassificationModel.SOURCE)) name.setSource(entity.getString(ClassificationModel.SOURCE));
		
		SearchRequest request = new SearchRequest();
		request.setIndex("associations");
		request.addParameter(SystemModel.SOURCE, entity.getId());
		SearchResponse response = searchService.search(request);
		List<SearchResult> associations = response.getResults();
		if(associations.size() > 0) {
			for(int i=0; i < associations.size(); i++) {				
				SearchResult association = associations.get(i);
				if(entity.getId().equals(association.getSource())) {
					if(association.getQName().getLocalName().equals("entries")) {
						try {
							Entry entry = getEntry(association);
							name.getEntries().add(entry);
						} catch(Exception e) {
							e.printStackTrace();
						}
					}
				}
			}
			for(int i=0; i < associations.size(); i++) {
				SearchResult association = associations.get(i);
				if(entity.getId().equals(association.getTarget()) &&association.getQName().getLocalName().equals("named_entities")) {
					SearchRequest entityRequest = new SearchRequest();
					entityRequest.setNodeId(entity.getId());
					SearchResponse entityResponse = searchService.search(entityRequest);
					SearchResult sourceEntity = entityResponse.getResult(0);
					try {
						if(sourceEntity.getQName().equals(RepositoryModel.COLLECTION)) {
							Collection collection = getCollection(sourceEntity);
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
	
	public Audio getAudio(SearchResult entity) {
		long id = entity.getId();
		String title = entity.getName();
		String description = entity.getString(RepositoryModel.DESCRIPTION);
		String contentType = cleanContentType(entity.getQName().getLocalName());
		Audio audio = new Audio(id, 0L, title, description, contentType);
		if(entity.hasProperty(RepositoryModel.DATE_EXPRESSION)) {
			audio.setDateExpression(entity.getString(RepositoryModel.DATE_EXPRESSION));
		}
		SearchRequest request = new SearchRequest();
		request.setIndex("associations");
		request.addParameter(SystemModel.SOURCE, entity.getId());
		SearchResponse response = searchService.search(request);
		List<SearchResult> associations = response.getResults();
		if(associations.size() > 0) {
			for(int i=0; i < associations.size(); i++) {
				SearchResult association = associations.get(i);
				try {
					if(association != null) {
						if(association.getQName().getLocalName().equals("web_links")) {
							WebLink file = getWebLink(association);
							audio.getWeblinks().add(file);
						} else if(association.getQName().getLocalName().equals("named_entities")) {
							SearchRequest entityRequest = new SearchRequest();
							entityRequest.setNodeId(association.getTarget());
							SearchResponse entityResponse = searchService.search(entityRequest);
							SearchResult targetResult = entityResponse.getResult(0);
							String localName = targetResult.getQName().getLocalName();
							NamedEntity name = getNamedEntity(association);
							if(localName.equals("person")) audio.getPeople().add(name);
							if(localName.equals("corporation")) audio.getCorporations().add(name);
						} else if(association.getQName().getLocalName().equals("subjects")) {
							Subject subject = getSubject(association);
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
	public DigitalObject getDigitalObject(SearchResult entity) {
		DigitalObject file = new DigitalObject();
		file.setId(String.valueOf(entity.getId()));
		//file.setUuid(entity.getUid());
		file.setTitle(entity.getName());						
		if(entity.hasProperty(ContentModel.TYPE)) file.setType(entity.getString(ContentModel.TYPE));
		//if(source.has("group")) file.setGroup(source.getString("group"));
		//if(source.has("order")) file.setOrder(source.getInt("order"));
		return file;
	}
	public Permission getPermission(SearchResult entity) {
		Permission permission = new Permission();
		permission.setId(entity.getId());
		permission.setLevel(Integer.valueOf(entity.getString(SystemModel.PERMISSION_LEVEL)));
		permission.setName(entity.getName());
		permission.setSource(Long.valueOf(entity.getString(SystemModel.PERMISSION_SOURCE)));
		permission.setTarget(Long.valueOf(entity.getString(SystemModel.PERMISSION_TARGET)));
		return permission;
	}
	public Category getCategory(SearchResult entity) {
		Category category = new Category(entity.getId(), 0L, entity.getName());
		category.setDescription(entity.getString(RepositoryModel.DESCRIPTION));
		category.setContentType("category");
		
		SearchRequest request = new SearchRequest();
		request.setIndex("associations");
		request.addParameter(SystemModel.SOURCE, entity.getId());
		SearchResponse response = searchService.search(request);
		List<SearchResult> associations = response.getResults();
		if(associations.size() > 0) {
			for(int i=0; i < associations.size(); i++) {
				SearchResult association = associations.get(i);
				if(association.getSource().equals(entity.getId())) {
					try {
						if(association.getQName().getLocalName().equals("items")) {
							Item item = getItem(association);
							category.getItems().add(item);
						} else if(association.getQName().getLocalName().equals("categories")) {
							Category childCategory = getCategory(association);
							category.getCategories().add(childCategory);
						}					
					} catch(Exception e) {
						e.printStackTrace();
					}
				}
			}
		}
		
		return category;
	}
	public Entry getEntry(SearchResult entity) {
		long id = entity.getId();
		String title = entity.getName();
		String description = entity.getString(RepositoryModel.DESCRIPTION);
		String contentType = cleanContentType(entity.getQName().getLocalName());
		Entry entry = new Entry(id, 0L, title, description, contentType);
		
		if(entity.hasProperty(ClassificationModel.COLLECTION_NAME)) 
			entry.setCollection(entity.getString(ClassificationModel.COLLECTION_NAME));
		if(entity.hasProperty(ClassificationModel.ITEMS)) {
			String itemDescription = (String)entity.getString(ClassificationModel.ITEMS);
			itemDescription = itemDescription.replace("\n", "<br>");
			entry.setDescription(itemDescription);
		}		
		SearchRequest request = new SearchRequest();
		request.setIndex("associations");
		request.addParameter(SystemModel.SOURCE, entity.getId());
		SearchResponse response = searchService.search(request);
		if(response.getResults().size() > 0) {
			for(int i=0; i < response.getResults().size(); i++) {
				SearchResult association = response.getResults().get(i);
				if(association.getTarget().equals(entity.getId())) {
					if(association.getQName().getLocalName().equals("entries")) {
						SearchRequest entityRequest = new SearchRequest();
						request.setIndex("associations");
						request.addParameter(SystemModel.SOURCE, entity.getId());
						SearchResponse entityResponse = searchService.search(entityRequest);
						try {
							entry.setNamedEntity(String.valueOf(association.getSource()));
							if(entityResponse.getResults().size() > 0) {											
								entry.setName(entityResponse.getResults().get(0).getName());
							}
						} catch(Exception e) {
							e.printStackTrace();
						}
					}
				}
			}
		}
		return entry;
	}
	public Item getItem(SearchResult entity) {
		long id = entity.getId();
		String title = entity.getName();
		String description = entity.getString(RepositoryModel.DESCRIPTION);
		String contentType = entity.getQName().getLocalName();
		Item item = new Item(id, 0L, title, description, contentType);
		if(entity.hasProperty(RepositoryModel.LANGUAGE)) item.setLanguage(getLanguageLabel(entity.getString(RepositoryModel.LANGUAGE)));
		else item.setLanguage("English");
		if(entity.hasProperty(RepositoryModel.CONTAINER)) item.setContainer(entity.getString(RepositoryModel.CONTAINER));
		if(entity.hasProperty(RepositoryModel.SUMMARY) && entity.getString(RepositoryModel.SUMMARY).length() > 0) item.setSummary(entity.getString(RepositoryModel.SUMMARY));
		if(entity.hasProperty(RepositoryModel.DATE_EXPRESSION)) {
			if(item.getName() == null) item.setName(entity.getString(RepositoryModel.DATE_EXPRESSION));
			item.setDateExpression(entity.getString(RepositoryModel.DATE_EXPRESSION));
		}

		Map<String,Object> entityPropMap = new HashMap<>();
		entity.getData().forEach((key, object) -> entityPropMap.put(key,object));
		item.setData(entityPropMap);

		List<SearchResult> path = getComponentPath(entity);
		if(path.size() > 0) {
			for(int i=0; i < path.size(); i++) {
				SearchResult node = path.get(i);
				PathNode pathNode = new PathNode(String.valueOf(node.getId()), node.getName());
				item.getPath().add(pathNode);
				if(node.getQName().equals(RepositoryModel.COLLECTION)) {
					item.setCollectionId(String.valueOf(node.getId()));
					item.setCollectionName(node.getName());
					item.setCollectionUrl(node.getString(RepositoryModel.URL));
				}
			}
		}
		
		SearchRequest request = new SearchRequest();
		request.setIndex("associations");
		request.addParameter(SystemModel.SOURCE, entity.getId());
		SearchResponse response = searchService.search(request);
		if(response.getResults().size() > 0) {
			for(int i=0; i < response.getResults().size(); i++) {
				SearchResult association = response.getResults().get(i);
				try {
					if(association != null && association.getQName() != null) {	
						if(association.getQName().getLocalName().equals("notes")) {					
							if(association.hasProperty(SystemModel.NOTE_TYPE)) {
							String type = association.getPropertyValueString(SystemModel.NOTE_TYPE);
							String content = association.getPropertyValueString(SystemModel.NOTE_CONTENT);
							if(type.equals("General note") || type.equals("General Physical Description note") ||
								type.equals("Table of Contents")) {
								item.getNotes().add(new Note(association.getId(), association.getId(), type, content));
							}
							if(type.equals("Abstract")) item.setAbstractNote(decode(content));
							if(type.equals("Language of Materials note")) {
								item.setNativeContent(content);
							}
						}					
						} else if(association.getQName().getLocalName().equals("named_entities")) {
							String localName = association.getQName().getLocalName();
							NamedEntity name = getNamedEntity(association);
							if(localName.equals("person")) item.getPeople().add(name);
							if(localName.equals("corporation")) item.getCorporations().add(name);
						} else if(association.getQName().getLocalName().equals("people")) {
							NamedEntity person = getNamedEntity(association);
							item.getPeople().add(person);
						} else if(association.getQName().getLocalName().equals("corporations")) {
							NamedEntity corporation = getNamedEntity(association);
							item.getCorporations().add(corporation);
						} else if(association.getQName().getLocalName().equals("subjects")) {
							Subject subject = getSubject(association);
							item.getSubjects().add(subject);
						} else if(association.getQName().getLocalName().equals("files")) {
							DigitalObject file = getDigitalObject(association);
							item.getDigitalObjects().add(file);
						} else if(association.getQName().getLocalName().equals("web_links")) {
							WebLink file = getWebLink(association);
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
	public Video getVideo(SearchResult entity) {
		long id = entity.getId();
		String title = entity.getName();
		String description = entity.getString(RepositoryModel.DESCRIPTION);
		String contentType = cleanContentType(entity.getQName().getLocalName());
		Video video = new Video(id, 0L, title, description, contentType);
		if(entity.hasProperty(RepositoryModel.DATE_EXPRESSION)) {
			video.setDateExpression(entity.getString(RepositoryModel.DATE_EXPRESSION));
		}
		
		SearchRequest request = new SearchRequest();
		request.setIndex("associations");
		request.addParameter(SystemModel.SOURCE, entity.getId());
		SearchResponse response = searchService.search(request);
		if(response.getResults().size() > 0) {
			for(int i=0; i < response.getResults().size(); i++) {
				SearchResult association = response.getResults().get(i);
				try {
					if(association != null) {
						if(association.getQName().getLocalName().equals("web_links")) {
							WebLink file = getWebLink(association);
							video.getWeblinks().add(file);
						} else if(association.getQName().getLocalName().equals("named_entities")) {
							String localName = association.getQName().getLocalName();
							NamedEntity name = getNamedEntity(association);
							if(localName.equals("person")) video.getPeople().add(name);
							if(localName.equals("corporation")) video.getCorporations().add(name);
						} else if(association.getQName().getLocalName().equals("subjects")) {
							Subject subject = getSubject(association);
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
	public WebLink getWebLink(SearchResult node) {
		SearchResult entity = searchService.search("nodes", node.getTarget());
		WebLink file = new WebLink();
		file.setId(String.valueOf(entity.getId()));
		if(entity.hasProperty(SystemModel.URL)) {
			file.setUrl(entity.getString(SystemModel.URL));
			int endIdx = file.getUrl().indexOf("/", 7);
			if(endIdx > -1) file.setDomain(file.getUrl().substring(0, endIdx));
		}
		file.setTitle(entity.getName());
		if(entity.hasProperty(ContentModel.TYPE)) file.setType(entity.getString(ContentModel.TYPE));
		return file;
	}
	public Subject getSubject(SearchResult node) {
		SearchResult entity = searchService.search("nodes", node.getTarget());
		String title = entity.getName();
		String description = entity.getString(SystemModel.DESCRIPTION);
		String contentType = cleanContentType(entity.getQName().getLocalName());
		Subject subject = new Subject(entity.getId(), 0L, title, description, contentType);
		if(entity.hasProperty(ClassificationModel.TYPE)) subject.setType(entity.getString(ClassificationModel.TYPE));
		if(entity.hasProperty(ClassificationModel.SOURCE)) subject.setSource(entity.getString(ClassificationModel.SOURCE));
		
		SearchRequest request = new SearchRequest();
		request.setIndex("associations");
		request.addParameter(SystemModel.SOURCE, entity.getId());
		SearchResponse response = searchService.search(request);
		for(SearchResult associationResult : response.getResults()) {
			if(associationResult.getTarget().equals(entity.getId())) {
				try {
					Collection collection = getCollection(associationResult);
					subject.getCollections().add(collection);					
				} catch(Exception e) {
					e.printStackTrace();
				}
			}
		}
		return subject;
	}
	
	protected List<SearchResult> getComponentPath(SearchResult comp) {
		List<SearchResult> path = new ArrayList<SearchResult>();
		if(comp != null) {
			SearchResult parent = getParent(comp.getId());
			while(parent != null && parent.getSource() != null) {
				try {
					SearchRequest request = new SearchRequest();
					request.setNodeId(parent.getSource());
					SearchResponse response = searchService.search(request);
					path.add(response.getResults().get(0));
					parent = getParent(comp.getId());
				} catch(Exception e) {
					e.printStackTrace();
				}
			}
		}
		Collections.reverse(path);
		return path;
	}
	protected SearchResult getParent(long id) {
		SearchRequest request = new SearchRequest();
		request.setIndex("associations");
		request.addParameter(SystemModel.TARGET, id);
		SearchResponse response = searchService.search(request);
		for(SearchResult association : response.getResults()) {
			if(association.getQName() != null) {
				if(association.getQName().equals(RepositoryModel.CATEGORIES) || association.getQName().equals(RepositoryModel.ITEMS))
					return association;
			}
		}
		return null;
	}
}
