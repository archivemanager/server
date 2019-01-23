package org.archivemanager.web.service;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.archivemanager.data.IDName;
import org.archivemanager.data.RestResponse;
import org.archivemanager.data.Sort;
import org.archivemanager.exception.InvalidQualifiedNameException;
import org.archivemanager.models.ClassificationModel;
import org.archivemanager.models.RepositoryModel;
import org.archivemanager.models.SystemModel;
import org.archivemanager.models.binders.RepositoryModelEntityBinder;
import org.archivemanager.models.dictionary.Model;
import org.archivemanager.models.dictionary.ModelField;
import org.archivemanager.models.dictionary.ModelFieldValue;
import org.archivemanager.models.dictionary.ModelRelation;
import org.archivemanager.models.repository.Result;
import org.archivemanager.models.system.QName;
import org.archivemanager.services.entity.Association;
import org.archivemanager.services.entity.Entity;
import org.archivemanager.services.entity.ImportProcessor;
import org.archivemanager.services.entity.InvalidAssociationException;
import org.archivemanager.services.entity.InvalidEntityException;
import org.archivemanager.services.entity.InvalidPropertyException;
import org.archivemanager.services.entity.ModelValidationException;
import org.archivemanager.services.entity.Property;
import org.archivemanager.services.search.SearchRequest;
import org.archivemanager.services.search.SearchResponse;
import org.archivemanager.services.search.SearchRestResponse;
import org.archivemanager.services.search.SearchResult;
import org.archivemanager.web.model.AssociationRecord;
import org.archivemanager.web.model.EntityRecord;
import org.archivemanager.web.model.PropertyRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;


@Controller
@RequestMapping("/service/repository")
public class RepositoryServiceController extends WebserviceSupport {
	@Autowired private RepositoryModelEntityBinder entityBinder;
		
	
	@ResponseBody
	@RequestMapping(value="/entity/fetch.json", method = RequestMethod.GET)
	public EntityRecord fetchEntity(@RequestParam("id") Long id, HttpServletRequest req, HttpServletResponse res) throws Exception {		
		Entity result = getEntityService().getEntity(id);
		EntityRecord record = entityBinder.getEntityRecord(result.getId(), result);		
		return record;
	}
	@ResponseBody
	@RequestMapping(value="/entity/search.json")
	public SearchRestResponse<Map<String,Object>> search(@RequestParam(required=false) String query, @RequestParam(required=false) QName qname, 
			@RequestParam(required=false, defaultValue="0") int page, @RequestParam(required=false, defaultValue="20") int rows,
			HttpServletRequest request, HttpServletResponse response) throws Exception {
		SearchRestResponse<Map<String,Object>> data = new SearchRestResponse<Map<String,Object>>();
		if(qname == null) qname = RepositoryModel.COLLECTION;
		SearchRequest entityQuery = new SearchRequest(qname, query);
		entityQuery.setStartRow((page * rows)-rows);
		entityQuery.setEndRow(page * rows);
		String sort = request.getParameter("sort");
		if(sort != null) {
			String[] sorts = sort.split("_");
			Sort sortValue = sorts[1].equals("asc") ? new Sort(Sort.STRING, sorts[0], "asc") : new Sort(Sort.STRING, sorts[0], "desc");
			entityQuery.addSort(sortValue);
		}
		SearchResponse results = getSearchService().search(entityQuery);
		if(results != null) {
			data.setStartRow(results.getStartRow());
			if(results.getResultSize() >= results.getEndRow()) data.setEndRow(results.getEndRow());
			else data.setEndRow(results.getResultSize());
			data.setTotal(results.getResultSize());
			for(SearchResult result : results.getResults()) {
				data.getRows().add(result.toMap());
			}
			data.setAttributes(results.getAttributes());
			data.setBreadcrumb(results.getBreadcrumb());
		}		
		return data;
	}	
	@ResponseBody
	@RequestMapping(value = "/entity/update.json", method = RequestMethod.POST)
	public EntityRecord updateEntity(@RequestBody EntityRecord record, HttpServletRequest request, HttpServletResponse response) throws Exception {
		prepareResponse(response);
	    EntityRecord data = null;
	    try { 
	    	Entity entity = entityBinder.getEntity(record);
	    	getEntityService().updateEntity(entity);
	    	if(record.getSource() > 0) {
	    		Entity source = getEntityService().getEntity(record.getSource());
	    		if(source != null) {
	    			List<ModelRelation> relations = getDictionaryService().getModelRelations(source.getQName());
	    			for(ModelRelation relation : relations) {
	    				if(getDictionaryService().isA(record.getQName(), relation.getEndName())) {
	    					boolean added = false;
	    					for(Association assoc : source.getSourceAssociations()) {
	    						if(assoc.getTarget().equals(entity.getId()))
	    							added = true;
	    					}
	    					if(!added) {
		    					Association association = new Association(relation.getQName(), source.getId(), entity.getId());
	 	    					getEntityService().updateAssociation(association);
	    					}
	    				}
	    			}
	    		}
	    	}
	    	data = entityBinder.getEntityRecord(entity.getId(), entity);
	    } catch (Exception e) {
	      e.printStackTrace();
	    }
	    return data;
	}
	@ResponseBody
	@RequestMapping(value = "/entity/associated/update.json", method = RequestMethod.POST)
	public AssociationRecord updateAssociationEntity(@RequestBody EntityRecord record, HttpServletRequest request, HttpServletResponse response) throws Exception {
		prepareResponse(response);
		AssociationRecord data = null;
	    try { 
	    	Entity entity = entityBinder.getEntity(record);
	    	getEntityService().updateEntity(entity);
	    	if(record.getSource() > 0) {
	    		Entity source = getEntityService().getEntity(record.getSource());
	    		if(source != null) {
	    			List<ModelRelation> relations = getDictionaryService().getModelRelations(source.getQName());
	    			for(ModelRelation relation : relations) {
	    				if(getDictionaryService().isA(record.getQName(), relation.getEndName())) {
	    					boolean added = false;
	    					for(Association assoc : source.getSourceAssociations()) {
	    						if(assoc.getTarget().equals(entity.getId()))
	    							added = true;
	    					}
	    					if(!added) {
		    					Association association = new Association(relation.getQName(), source, entity);
	 	    					getEntityService().updateAssociation(association);
	 	    					data = entityBinder.getAssociationRecord(association);
	    					}
	    				}
	    			}
	    		}
	    	}
	    	
	    } catch (Exception e) {
	      e.printStackTrace();
	    }
	    return data;
	}
	@ResponseBody
	@RequestMapping(value = "/association/update.json", method = RequestMethod.POST)
	public EntityRecord updateAssociation(@RequestBody AssociationRecord record, HttpServletRequest request, HttpServletResponse response) throws Exception {
		prepareResponse(response);
		EntityRecord data = null;
		try {
			Association association = entityBinder.getAssociation(record);
			getEntityService().updateAssociation(association);	
			Entity sourceEntity = getEntityService().getEntity(association.getSource());
			data = entityBinder.getEntityRecord(sourceEntity.getId(), sourceEntity);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return data;
	}
	@ResponseBody
	@RequestMapping(value = "/association/add.json", method = RequestMethod.POST)
	public AssociationRecord addAssociation(@RequestBody AssociationRecord record, HttpServletResponse response) throws Exception {
		prepareResponse(response);
		AssociationRecord data = null;
		try {
			Entity sourceEntity = getEntityService().getEntity(record.getSource());
			List<Association> associations = sourceEntity.getSourceAssociations();
			for(Association association : associations) {
				if(association.getSource().equals(record.getSource()) && association.getTarget().equals(record.getTarget()))
					throw new Exception("Duplicate association source:"+record.getSource()+", target="+record.getTarget());
			}
			Entity targetEntity = getEntityService().getEntity(record.getTarget());
			List<ModelRelation> relations = getDictionaryService().getModelRelations(sourceEntity.getQName());
			for(ModelRelation relation : relations) {
				if(relation.getEndName().toString().equals(targetEntity.getQName().toString())) {
					Association association = new Association(relation.getQName(), record.getSource(), record.getTarget());
					association.setSourceName(relation.getStartName());
					association.setTargetName(relation.getEndName());
					for(PropertyRecord r : record.getProperties()) {
						association.addProperty(r.getQName(), r.getValue());
					}
					getEntityService().updateAssociation(association);
					data = entityBinder.getAssociationRecord(association);
					data.setView(relation.getView());
					data.setName(targetEntity.getName());
				}
			}			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return data;
	}		
	@ResponseBody
	@RequestMapping(value = "/collection/repository/switch.json", method = RequestMethod.POST)
	public AssociationRecord switchRepository(@RequestParam("id") Long id, @RequestParam("source") Long source, HttpServletResponse response) throws Exception {
		prepareResponse(response);
		AssociationRecord data = null;
		try {
			Entity entity = getEntityService().getEntity(id);
			Association association = entity.getTargetAssociation(RepositoryModel.COLLECTIONS);
			if(association != null) {
				association.setSource(source);
				getEntityService().updateAssociation(association);	
				data = entityBinder.getAssociationRecord(association);				
			}			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return data;
	}
	@ResponseBody
	@RequestMapping(value = "/entity/category/switch.json", method = RequestMethod.POST)
	public AssociationRecord switchAssociation(@RequestParam("id") Long id, @RequestParam("source") Long source, HttpServletResponse response) throws Exception {
		prepareResponse(response);
		AssociationRecord data = null;
		try {
			Entity entity = getEntityService().getEntity(id);
			Association association = entity.getTargetAssociation(RepositoryModel.ITEMS);
			if(association != null) {
				association.setSource(source);
				getEntityService().updateAssociation(association);	
				data = entityBinder.getAssociationRecord(association);				
			}			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return data;
	}
	@ResponseBody
	@RequestMapping(value = "/entity/remove.json", method = RequestMethod.POST)
	public RestResponse<Object> removeEntity(HttpServletRequest request, HttpServletResponse response,
	      @RequestParam("id") Long id) throws Exception {
		prepareResponse(response);
	    RestResponse<Object> data = new RestResponse<Object>();
	    try {
	      Entity entity = getEntityService().getEntity(id);
	      String name = (String)entity.getProperty(SystemModel.NAME).getValue();
	      getEntityService().removeEntity(id);
	      Map<String, Object> record = new HashMap<String, Object>();
	      record.put("id", entity.getId());
	      record.put("uid", entity.getUid());
	      record.put("name", name);
	      data.addRow(record);
	      data.setStatus(0);
	    } catch (Exception e) {
	      e.printStackTrace();
	      data.setStatus(-1);
	      data.addMessage(e.getMessage());
	    }
	    return data;
	}
	@ResponseBody
	@RequestMapping(value="/entity/value/fetch.json", method = RequestMethod.GET)
	public Result fetchEntityValue(@RequestParam("id") Long id, HttpServletRequest req, HttpServletResponse res) throws Exception {
		Entity entity = getEntityService().getEntity(id);
		Result result = entityBinder.getResult(entity, true);
		return result;
	}
	
	@ResponseBody
	@RequestMapping(value="/item/qnames.json", method = RequestMethod.GET)
	public List<IDName> getItemQnames(HttpServletRequest request, HttpServletResponse res) throws Exception {
		List<IDName> results = new ArrayList<IDName>();
		List<Model> models = getDictionaryService().getChildModels(RepositoryModel.ITEM);
		for(Model model : models){
			results.add(new IDName(model.getQName().toString(), model.getLabel()));
		}
		return results;
	}
	
	@ResponseBody
	@RequestMapping(value="/entity/note_types.json", method = RequestMethod.GET)
	public List<Map<String,Object>> getNoteTypes(HttpServletRequest request, HttpServletResponse res) throws Exception {
		List<Map<String,Object>> results = new ArrayList<Map<String,Object>>();
		Model model = getDictionaryService().getModel(SystemModel.NOTE);
		ModelField typeField = model.getField(SystemModel.NOTE_TYPE);
		for (ModelFieldValue value : typeField.getValues()){
			Map<String,Object> map = new HashMap<String,Object>();
			map.put("label",value.getLabel());
			map.put("value", value.getValue());
			results.add(map);
		}
		return results;
	}
	
	@ResponseBody
	@RequestMapping(value="/entity/content_type.json", method = RequestMethod.GET)
	public List<Map<String,Object>> getContentTypes(HttpServletRequest request, HttpServletResponse res) throws Exception {
		List<Map<String,Object>> results = new ArrayList<Map<String,Object>>();
		List<Model> models = getDictionaryService().getChildModels(RepositoryModel.ITEM);
		for (Model value : models){
			Map<String,Object> map = new HashMap<String,Object>();
			map.put("label",value.getName());
			map.put("value", value.getQName().toString());
			results.add(map);
		}
		return results;
	}

	@ResponseBody
	@RequestMapping(value="/entity/languages.json", method = RequestMethod.GET)
	public List<Map<String,Object>> getEnumLanguage (HttpServletRequest request, HttpServletResponse res) throws Exception {
		List<Map<String,Object>> results = new ArrayList<Map<String,Object>>();
		Model model = getDictionaryService().getModel(RepositoryModel.COLLECTION);
		ModelField typeField = model.getField(RepositoryModel.LANGUAGE);
		for (ModelFieldValue value : typeField.getValues()){
			Map<String,Object> map = new HashMap<String,Object>();
			map.put("label",value.getLabel());
			map.put("value", value.getValue());
			results.add(map);
		}
		return results;
	}

	@ResponseBody
	@RequestMapping(value="/entity/classification-rule.json", method = RequestMethod.GET)
	public List<Map<String,Object>> getEnumClassificationRule(HttpServletRequest request, HttpServletResponse res) throws Exception {
		List<Map<String,Object>> results = new ArrayList<Map<String,Object>>();
		Model model = getDictionaryService().getModel(ClassificationModel.NAMED_ENTITY);
		ModelField typeField = model.getField(ClassificationModel.RULE);
		for (ModelFieldValue value : typeField.getValues()){
			Map<String,Object> map = new HashMap<String,Object>();
			map.put("label",value.getLabel());
			map.put("value", value.getValue());
			results.add(map);
		}
		return results;
	}
	
	@ResponseBody
	@RequestMapping(value="/entity/classification-function.json", method = RequestMethod.GET)
	public List<Map<String,Object>> getEnumClassificationFunction(HttpServletRequest request, HttpServletResponse res) throws Exception {
		List<Map<String,Object>> results = new ArrayList<Map<String,Object>>();
		Model model = getDictionaryService().getModel(ClassificationModel.NAMED_ENTITY);
		ModelField typeField = model.getField(ClassificationModel.FUNCTION);
		for (ModelFieldValue value : typeField.getValues()){
			Map<String,Object> map = new HashMap<String,Object>();
			map.put("label",value.getLabel());
			map.put("value", value.getValue());
			results.add(map);
		}
		return results;
	}
	
	@ResponseBody
	@RequestMapping(value="/entity/classification-source.json", method = RequestMethod.GET)
	public List<Map<String,Object>> getEnumClassificationSource(HttpServletRequest request, HttpServletResponse res) throws Exception {
		List<Map<String,Object>> results = new ArrayList<Map<String,Object>>();
		Model model = getDictionaryService().getModel(SystemModel.NOTE);
		ModelField typeField = model.getField(SystemModel.NOTE_TYPE);
		for (ModelFieldValue value : typeField.getValues()){
			Map<String,Object> map = new HashMap<String,Object>();
			map.put("label",value.getLabel());
			map.put("value", value.getValue());
			results.add(map);
		}
		return results;
	}
	
	@ResponseBody
	@RequestMapping(value="/entity/subject-sources.json", method = RequestMethod.GET)
	public List<Map<String,Object>> getSubjectSources(HttpServletRequest request, HttpServletResponse res) throws Exception {
		List<Map<String,Object>> results = new ArrayList<Map<String,Object>>();
		Model model = getDictionaryService().getModel(ClassificationModel.SUBJECT);
		ModelField typeField = model.getField(ClassificationModel.SOURCE);
		for (ModelFieldValue value : typeField.getValues()){
			Map<String,Object> map = new HashMap<String,Object>();
			map.put("label",value.getLabel());
			map.put("value", value.getValue());
			results.add(map);
		}
		return results;
	}
	
	@ResponseBody
	@RequestMapping(value="/entity/subject-types.json", method = RequestMethod.GET)
	public List<Map<String,Object>> getSubjectTypes(HttpServletRequest request, HttpServletResponse res) throws Exception {
		List<Map<String,Object>> results = new ArrayList<Map<String,Object>>();
		Model model = getDictionaryService().getModel(ClassificationModel.SUBJECT);
		ModelField typeField = model.getField(ClassificationModel.TYPE);
		for (ModelFieldValue value : typeField.getValues()){
			Map<String,Object> map = new HashMap<String,Object>();
			map.put("label",value.getLabel());
			map.put("value", value.getValue());
			results.add(map);
		}
		return results;
	}
	
	@ResponseBody
	@RequestMapping(value="/entity/item-genres.json", method = RequestMethod.GET)
	public List<Map<String,Object>> getItemGenres(@RequestParam("qname") QName qname, HttpServletRequest request, HttpServletResponse res) throws Exception {
		List<Map<String,Object>> results = new ArrayList<Map<String,Object>>();
		Model model = getDictionaryService().getModel(qname);
		ModelField typeField = model.getField(RepositoryModel.GENRE);
		if(typeField != null) {
			for (ModelFieldValue value : typeField.getValues()){
				Map<String,Object> map = new HashMap<String,Object>();
				map.put("label",value.getLabel());
				map.put("value", value.getValue());
				results.add(map);
			}
		}
		return results;
	}
	
	@ResponseBody
	@RequestMapping(value="/entity/item-forms.json", method = RequestMethod.GET)
	public List<Map<String,Object>> getItemForms(@RequestParam("qname") QName qname, HttpServletRequest request, HttpServletResponse res) throws Exception {
		List<Map<String,Object>> results = new ArrayList<Map<String,Object>>();
		Model model = getDictionaryService().getModel(qname);
		ModelField typeField = model.getField(RepositoryModel.FORM);
		if(typeField != null) {
			for (ModelFieldValue value : typeField.getValues()){
				Map<String,Object> map = new HashMap<String,Object>();
				map.put("label",value.getLabel());
				map.put("value", value.getValue());
				results.add(map);
			}
		}
		return results;
	}
	
	@ResponseBody
	@RequestMapping(value="/entity/item-mediums.json", method = RequestMethod.GET)
	public List<Map<String,Object>> getItemMediums(@RequestParam("qname") QName qname, HttpServletRequest request, HttpServletResponse res) throws Exception {
		List<Map<String,Object>> results = new ArrayList<Map<String,Object>>();
		Model model = getDictionaryService().getModel(qname);
		ModelField typeField = model.getField(RepositoryModel.MEDIUM);
		if(typeField != null) {
			for (ModelFieldValue value : typeField.getValues()){
				Map<String,Object> map = new HashMap<String,Object>();
				map.put("label",value.getLabel());
				map.put("value", value.getValue());
				results.add(map);
			}
		}
		return results;
	}
	
	protected void printNodeTaxonomy(List<Object> list, String parent, Entity node, ImportProcessor parser) throws InvalidEntityException {
		Map<String,Object> entityMap = new HashMap<String,Object>();
		entityMap.put("id", node.getUid());
		entityMap.put("name", node.getName());
		entityMap.put("parent", parent);
		for(Property property : node.getProperties()) {
			entityMap.put(property.getQName().getLocalName(), property.getValue());
		}
		if(node.getChildren().size() > 0) {
			entityMap.put("isFolder", true);
		} else {
			entityMap.put("isFolder", false);
		}
		list.add(entityMap);
		for(Association assoc : node.getChildren()) {
			Entity child = parser.getEntityById(assoc.getTargetUid());
			printNodeTaxonomy(list, node.getUid(), child, parser);
		}
	}
	protected void cascadeQName(Long id, QName association, QName qname) throws InvalidAssociationException, InvalidEntityException, InvalidPropertyException, ModelValidationException, InvalidQualifiedNameException {
		Entity entity = getEntityService().getEntity(id);
		for(Association assoc : entity.getSourceAssociations(association)) {
			Entity targetEntity = getEntityService().getEntity(assoc.getTarget());
			if(association.equals(RepositoryModel.CATEGORIES)) {
				assoc.setQname(RepositoryModel.ITEMS);
				getEntityService().updateAssociation(assoc);
				targetEntity.addProperty(RepositoryModel.CATEGORY_LEVEL, "item");
			}
			targetEntity.setQName(qname);
			getEntityService().updateEntity(targetEntity);
			cascadeQName(targetEntity.getId(), association, qname);
		}
	}
	protected Map<String,Object> getNodeData(String id, String parent, String name, String qname, String localName) {
		Map<String,Object> nodeData = new HashMap<String,Object>();
		nodeData.put("id", id);
		nodeData.put("parent", parent);
		nodeData.put("name", name);
		nodeData.put("qname", qname);
		nodeData.put("localName", localName);
		return nodeData;
	}
	protected List<Entity> getPath(Entity entity) throws Exception {
		List<Entity> entities = new ArrayList<Entity>();
		Association parent_assoc = entity.getTargetAssociation(RepositoryModel.CATEGORIES);
		if(parent_assoc == null) parent_assoc = entity.getTargetAssociation(RepositoryModel.ITEMS);
		if(parent_assoc != null && parent_assoc.getSource() != null) {
			Entity parent = getEntityService().getEntity(parent_assoc.getSource());
			while(parent != null) {
				entities.add(parent);
				parent_assoc = parent.getTargetAssociation(RepositoryModel.CATEGORIES);
				if(parent_assoc == null) parent_assoc = parent.getTargetAssociation(RepositoryModel.ITEMS);
				if(parent_assoc != null && parent_assoc.getSource() != null) {
					parent = getEntityService().getEntity(parent_assoc.getSource());
				} else parent = null;
			}
		}
		Collections.reverse(entities);
		return entities;
	}
	protected String getPath(List<Entity> entities) {
		StringWriter out = new StringWriter();
		for(int i=1; i < entities.size(); i++) {
			Entity entity = entities.get(i);
			out.write("/" + entity.getName());
		}
		return out.toString();
	}	
	protected void appendRepository(HttpServletRequest request, Entity entity) throws Exception {
		String repository = request.getParameter("repository");
		if(repository != null && repository.length() > 0 && !repository.equals("null")) {
			List<Association> assocs = entity.getAssociations(RepositoryModel.COLLECTIONS);
			Entity target = getEntityService().getEntity(Long.valueOf(repository));
			if(assocs.size() == 0) {
				Association a = new Association(RepositoryModel.COLLECTIONS, target,  entity);
				entity.getTargetAssociations().add(a);
			} else {
				Association a = assocs.get(0);
				//a.setSourceEntity(target);
				a.setSource(target.getId());
				//a.setTargetEntity(entity);
				a.setTarget(entity.getId());
				try {
					getEntityService().updateAssociation(a);
				} catch(Exception e) {
					e.printStackTrace();
				}
			}
		}
	}
}