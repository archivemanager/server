package org.archivemanager.web.service;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Logger;

import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObjectBuilder;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.archivemanager.config.PropertyConfiguration;
import org.archivemanager.data.RestResponse;
import org.archivemanager.models.SystemModel;
import org.archivemanager.models.dictionary.ModelField;
import org.archivemanager.models.dictionary.ModelRelation;
import org.archivemanager.models.system.QName;
import org.archivemanager.services.entity.Association;
import org.archivemanager.services.entity.Entity;
import org.archivemanager.services.entity.InvalidEntityException;
import org.archivemanager.services.entity.Property;
import org.archivemanager.services.entity.ValidationResult;
import org.archivemanager.services.search.SearchRequest;
import org.archivemanager.services.search.SearchResponse;
import org.archivemanager.services.search.SearchResult;
import org.archivemanager.services.search.indexing.EntityIndexingJob;
import org.archivemanager.util.NumberUtility;
import org.jfree.util.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;


@Controller
@RequestMapping("/service/entity")
public class EntityServiceController extends WebserviceSupport {
  private final static Logger log = Logger.getLogger(EntityServiceController.class.getName());
  @Autowired private PropertyConfiguration properties;
  
  private Map<Long, List<Long>> cache = new HashMap<Long, List<Long>>();
  public static final QName OPENAPPS_ENTITIES = new QName(SystemModel.OPENAPPS_SYSTEM_NAMESPACE,
      "entities");

  @ResponseBody
  @RequestMapping(value="/association/search.json")
  public RestResponse<Object> searchAssociations(@RequestParam Long id, 
		  @RequestParam(required=false, defaultValue="0") int page, @RequestParam(required=false, defaultValue="20") int rows,
		  HttpServletRequest request, HttpServletResponse response) throws Exception {
	  RestResponse<Object> data = new RestResponse<Object>();
	  List<Association> results = getEntityService().getTargetAssociations(id);
	  if(results != null) {
		  data.setStartRow(page*rows);
		  data.setEndRow((page*rows)+rows);
		  if(results.size() < data.getTotal())  data.setEndRow(results.size());
		  data.setTotal(results.size());
		  int resultCount = 0;
		  for(Association result : results) {
			  if(resultCount >= data.getStartRow() && (data.getEndRow() == 0 || resultCount < data.getEndRow())) {
				  data.getRows().add(result);
			  }
			  resultCount++;
		  }
	  }		
	  return data;
  }
  @ResponseBody
  @RequestMapping(value = "/named_entities/get.json", method = RequestMethod.GET)
  public RestResponse<Object> fetchNamedEntities(HttpServletRequest request,
      HttpServletResponse response,
      @RequestParam String qname) throws Exception {

    QName qName = QName.createQualifiedName(qname);
    prepareResponse(response);
    RestResponse<Object> data = new RestResponse<Object>();
    
    SearchRequest query  = new SearchRequest(qName);
    SearchResponse results = getSearchService().search(query);
    
    JsonArrayBuilder builder = Json.createArrayBuilder();
    for (SearchResult c : results.getResults()) {
      JsonObjectBuilder object = Json.createObjectBuilder();
      Entity entity = getEntityService().getEntity(Long.valueOf(c.getId()));
      String name = (String)entity.getProperty(SystemModel.NAME).getValue();
      builder.add(object.add("name", name)).build();
    }
    return data;
  }
  
  @ResponseBody
  @RequestMapping(value = "/get.json", method = RequestMethod.GET)
  public RestResponse<Object> fetchEntity(HttpServletRequest request, HttpServletResponse response,
      @RequestParam(required = false) long id) throws Exception {
    String sourcesStr = request.getParameter("sources");
    String targetsStr = request.getParameter("targets");
    String view = request.getParameter("view");
    
    boolean sources = (sourcesStr != null && sourcesStr.equals("true")) ? true : false;
    boolean targets = (targetsStr != null && targetsStr.equals("true")) ? true : false;

    prepareResponse(response);

    return getEntity(id, view, sources, targets);
  }

  @ResponseBody
  @RequestMapping(value = "/get.json/{id}", method = RequestMethod.GET)
  public RestResponse<Object> getEntity(HttpServletRequest request, HttpServletResponse response,
      @PathVariable("id") Long id) throws Exception {
    String sourcesStr = request.getParameter("sources");
    String targetsStr = request.getParameter("targets");
    String view = request.getParameter("view");

    boolean sources = (sourcesStr != null && sourcesStr.equals("true")) ? true : false;
    boolean targets = (targetsStr != null && targetsStr.equals("true")) ? true : false;

    prepareResponse(response);

    return getEntity(id, view, sources, targets);
  }

  @ResponseBody
  @RequestMapping(value = "/add.json", method = RequestMethod.POST)
  public RestResponse<Object> addEntity(HttpServletRequest request, HttpServletResponse response,
      @RequestParam("qname") String entityQname) throws Exception {
	  QName qname = QName.createQualifiedName(entityQname);
	  prepareResponse(response);
	  RestResponse<Object> data = new RestResponse<Object>();
	  try {
	      Entity entity = getEntity(request, qname);
	      //entity.setUser(user.getId());
	      //String fmt = request.getParameter("format");      
	      ValidationResult validationResult = validate(entity);
	      if(validationResult.isValid()) {
	    	  getEntityService().updateEntity(entity);
	          data.addRow(entity);
	          data.setStatus(0);
	      }
	  } catch (Exception e) {
	      e.printStackTrace();
	      data.setStatus(-1);
	      data.addMessage(e.getMessage());
	  }
	  return data;
  }
  /*
  @ResponseBody
  @RequestMapping(value = "/addEntity.json", method = RequestMethod.POST)
  public RestResponse<Object> addEntityRestRequest(@RequestBody RestRequest<Entity> restRequest,
      HttpServletRequest request, HttpServletResponse response) throws Exception {
    prepareResponse(response);
    RestResponse<Object> data = new RestResponse<Object>();
    try {
      Entity entity = restRequest.getData().get(0);
      //entity.setUser(user.getId());
      getEntityService().addEntity(entity);
      if (restRequest.getFormat() != null && restRequest.getFormat().equals("tree")) {
        FormatInstructions instructions = new FormatInstructions(true);
        instructions.setFormat(FormatInstructions.FORMAT_JSON);
        data.addData(getEntityService().export(instructions, entity));
      } else {
        FormatInstructions instructions = new FormatInstructions(false, true, false);
        instructions.setFormat(FormatInstructions.FORMAT_JSON);
        data.addData(getEntityService().export(instructions, entity));
      }
      data.setStatus(0);
    } catch (Exception e) {
      e.printStackTrace();
      data.setStatus(-1);
      data.addMessage(e.getMessage());
    }
    return data;
  }
  */
  @ResponseBody
  @RequestMapping(value = "/remove.json", method = RequestMethod.POST)
  public RestResponse<Object> removeEntity(HttpServletRequest request, HttpServletResponse response,
      @RequestParam("id") Long id) throws Exception {
    prepareResponse(response);
    RestResponse<Object> data = new RestResponse<Object>();
    Map<String, Object> record = new HashMap<String, Object>();
    try {
      Entity entity = getEntityService().getEntity(id);
      if(entity != null) {
    	  record.put("id", entity.getId());
    	  record.put("uid", entity.getUid());
	      String name = entity.hasProperty(SystemModel.NAME) ? (String)entity.getProperty(SystemModel.NAME).getValue() : "";
	      record.put("name", name);
	      getEntityService().removeEntity(id);	      
      } else {
    	  getEntityService().index(id);
      }
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
  @RequestMapping(value = "/index.json", method = RequestMethod.GET)
  public RestResponse<Map<String, Object>> indexQName(HttpServletRequest request, HttpServletResponse response,
      @RequestParam("qname") String qname) throws Exception {
    QName q = QName.createQualifiedName(qname);
    prepareResponse(response);
    RestResponse<Map<String, Object>> data = new RestResponse<Map<String, Object>>();
    
    org.archivemanager.models.dictionary.Model model = getDictionaryService().getModel(q);
    if(model.isSearchable()) {
		int count = getEntityService().count(model.getQName());
		double ratio = (double)count / properties.getEntityIndexingBatchSize();
		int pages =	(int)(Math.ceil(ratio));
			
		for(int i=0; i < pages; i++) {
			int end = (i*properties.getEntityIndexingBatchSize()) + properties.getEntityIndexingBatchSize();
			EntityIndexingJob job = new EntityIndexingJob(getEntityService(), model.getQName(), i, properties.getEntityIndexingBatchSize());
			getSchedulingService().run(job);
			
			Map<String, Object> statusData = new HashMap<String, Object>();
		    String uid = job.getUid();
		    String message = job.getLastMessage();
		    statusData.put("uid", uid);
		    statusData.put("lastMessage", message);
		    statusData.put("isRunning", !job.isComplete());
		    data.addRow(statusData);
		      
			Log.info("search index added "+end+" of "+count+" "+model.getQName().getLocalName()+"s");
		}
		Log.info(count+" "+model.getQName().getLocalName()+"s indexed successfully");		
	}    
    return data;
  }

  @ResponseBody
  @RequestMapping(value = "/get/children.json", method = RequestMethod.GET)
  public RestResponse<Object> getEntities(HttpServletRequest request, HttpServletResponse response,
      @RequestParam("id") Long id) throws Exception {
    //String printTargets = request.getParameter("targets");
    //String printSources = request.getParameter("sources");
     prepareResponse(response);
    RestResponse<Object> data = new RestResponse<Object>();
    try {
      Entity entity = getEntityService().getEntity(id);
      List<Association> assocs = entity.getSourceAssociations();

      for (Association assoc : assocs) {
        Entity target = getEntityService().getEntity(assoc.getTarget());
        data.addRow(target);
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
    return data;
  }

  @ResponseBody
  @RequestMapping(value = "/association/add.json", method = RequestMethod.POST)
  public RestResponse<Object> associationAdd(HttpServletRequest request,
      HttpServletResponse response,
      @RequestParam("assoc_qname") String assoc_qname,
      @RequestParam(required = false) String entity_qname,
      @RequestParam("source") String source,
      @RequestParam(required = false) String target,
      @RequestParam(required = false) boolean targets) throws Exception {
	    String id = request.getParameter("id");
	    QName assocQname = QName.createQualifiedName(assoc_qname);
	    QName entityQname = QName.createQualifiedName(entity_qname);
	    prepareResponse(response);
	    RestResponse<Object> data = new RestResponse<Object>();
	    try {
	    	if(target == null) {
	    		Entity entity = getEntity(request, entityQname);
	    		if(entity.getId() == null || entity.getId() == 0) {
	    			if(NumberUtility.isLong(source)) {
	    				getEntityService().updateEntity(entity);
	    				Association assoc = new Association(assocQname, Long.valueOf(source), entity.getId());
	    				getEntityService().updateAssociation(assoc);
	    				data.addRow(entity);
	    			}
	    		}
	    	} else {
	    		Association assoc = getAssociation(request, id, source, target, assocQname);
	    		getEntityService().updateAssociation(assoc);
	    		Entity sourceEntity = getEntityService().getEntity(Long.valueOf(source));
	    		Entity targetEntity = getEntityService().getEntity(Long.valueOf(target));
	    		if (targets) {
	    			data.addRow(targetEntity);
	    		} else {
	    			data.addRow(sourceEntity);
	    		}
	    	}
	    	data.setStatus(0);
	    	data.addMessage("Association created successfully");
	    } catch (Exception e) {
	      e.printStackTrace();
	    }
	    return data;
  }

  @ResponseBody
  @RequestMapping(value = "/association/addall.json", method = RequestMethod.POST)
  public RestResponse<Object> associationAddAll(HttpServletRequest request,
      HttpServletResponse response,
      @RequestParam("assoc_qname") String assoc_qname,
      @RequestParam("source") String source,
      @RequestParam("targetIds") String targetIds,
      @RequestParam(value = "target_qname", required = false) String target_qname,
      @RequestParam(required = false) boolean targets) throws Exception {

    QName assocQname = QName.createQualifiedName(assoc_qname);
    prepareResponse(response);
    RestResponse<Object> data = new RestResponse<Object>();
    try {
      String[] targetForAssociation = targetIds.split("~~");
      Entity entity = getEntityService().getEntity(Long.valueOf(source));
      Entity targetEntity;
      List<Association> assocs = entity.getAssociations(assocQname);

      for (int i = 0; i < targetForAssociation.length; i++) {
        List<String> targetsMeta = Arrays.asList(targetForAssociation[i].split(":"));
        String id = targetsMeta.get(0);
        String name = targetsMeta.get(1);

        boolean match = false;
        if (id.equals("New")) {
          QName targetQname = QName.createQualifiedName(target_qname);
          targetEntity = getEntity(name, targetQname);
          getEntityService().updateEntity(targetEntity);
        } else {
          targetEntity = getEntityService().getEntity(Long.valueOf(id));
          for (Association assoc : assocs) {
            if (assoc.getTarget().equals(targetEntity.getId())) {
              match = true;
              break;
            }
          }
        }

        if (!match) {
          Association assoc = getAssociation(request, null, source,
              Long.toString(targetEntity.getId()),
              assocQname);
          Entity sourceEntity = getEntityService().getEntity(Long.valueOf(source));
          assoc.setSource(sourceEntity.getId());
          assoc.setTarget(targetEntity.getId());
          getEntityService().updateAssociation(assoc);

          if (targets) {
            data.addRow(targetEntity);
          } else {
            data.addRow(sourceEntity);
          }
        }
      }
      data.setStatus(200);
      return data;

    } catch (Exception e) {
      e.printStackTrace();
      data.setStatus(500);
      data.setMessages(Arrays.asList(e.getMessage()));
    }
    return data;
  }

  @ResponseBody
  @RequestMapping(value = "/association/switch.json", method = RequestMethod.POST)
  public RestResponse<Object> switchAssociation(HttpServletRequest request,
      HttpServletResponse response,
      @RequestParam("id") Long id, @RequestParam("source") Long sourceEntityId,
      @RequestParam("target") Long targetEntityId) throws Exception {
    RestResponse<Object> data = new RestResponse<Object>();
    try {
      Association assoc = getEntityService().getAssociation(id);
      Entity target = getEntityService().getEntity(targetEntityId);
      Entity source = getEntityService().getEntity(sourceEntityId);
      if (assoc != null && source != null && target != null) {
        if (assoc.getSource() == source.getId()) {
          assoc.setTarget(target.getId());
        } else {
          assoc.setSource(source.getId());
        }
        getEntityService().updateAssociation(assoc);
      }
      data.addRow(source);
      return data;
    } catch (Exception e) {
      e.printStackTrace();
    }
    return data;
  }

  @ResponseBody
  @RequestMapping(value = "/association/remove.json", method = RequestMethod.POST)
  public RestResponse<Object> removeAssociation(HttpServletRequest request,
      HttpServletResponse response, @RequestParam("id") Long id,
      @RequestParam(required = false) String ticket) throws Exception {
    RestResponse<Object> data = new RestResponse<Object>();
    try {
      getEntityService().removeAssociation(id);
      Map<String, Object> record = new HashMap<String, Object>();
      record.put("id", id);
      data.addRow(record);
    } catch (Exception e) {
      e.printStackTrace();
    }
    return data;
  }

  @ResponseBody
  @RequestMapping(value = "/association/remove/target.json", method = RequestMethod.POST)
  public RestResponse<Object> removeAssociationTarget(HttpServletRequest request,
      HttpServletResponse response, @RequestParam("id") Long id,
      @RequestParam("target_id") Long target) throws Exception {
    RestResponse<Object> data = new RestResponse<Object>();
    try {
      Entity entity = getEntityService().getEntity(id);
      getEntityService().removeEntity(id);

      Map<String, Object> record = new HashMap<String, Object>();
      record.put("id", entity.getId());
      record.put("uid", entity.getUid());
      data.addRow(record);

    } catch (Exception e) {
      e.printStackTrace();
    }
    return data;
  }

  @ResponseBody
  @RequestMapping(value = "/node/browse.json", method = RequestMethod.GET)
  public RestResponse<Object> fetchJson(HttpServletRequest request, HttpServletResponse response)
      throws Exception {
    String parent = request.getParameter("parent");
    String startStr = request.getParameter("_startRow");
    String endStr = request.getParameter("_endRow");
    String mode = request.getParameter("mode");

    int start = Integer.valueOf(startStr);
    int end = Integer.valueOf(endStr);

    return browse(Long.valueOf(parent), mode, start, end);
  }

  @ResponseBody
  @RequestMapping(value = "/node/get.json", method = RequestMethod.GET)
  public RestResponse<Object> getEntityJSON(HttpServletRequest request,
      HttpServletResponse response, @RequestParam("id") Long id) throws Exception {
    return getNode(id);
  }

  @ResponseBody
  @RequestMapping(value = "/node/remove.json", method = RequestMethod.POST)
  public RestResponse<Object> removeNode(HttpServletRequest request, HttpServletResponse response,
      @RequestParam("id") Long id) throws Exception {
    return removeNode(id);
  }
  
  public Entity getEntity(HttpServletRequest request, QName entityQname) throws InvalidEntityException {
	  Entity entity = null;
	  String id = request.getParameter("id");
	  if (id != null && id.length() > 0) {
	      entity = getEntityService().getEntity(Long.valueOf(id));
	  } else {
	      entity = new Entity(entityQname);
	      entity.setCreated(System.currentTimeMillis());
	      entity.setModified(System.currentTimeMillis());
	      String uid = request.getParameter("uid");
	      if (uid != null && uid.length() > 0) {
	        entity.setUid(uid);
	      } else {
	        entity.setUid(UUID.randomUUID().toString());
	      }
	  }
	  if(entity != null) {
		  try {    	  
	          List<ModelField> fields = getDictionaryService().getModelFields(entity.getQName());
		        for (ModelField field : fields) {
		          try {
		            String value = field.getQName().getLocalName().equals("function") ?
		                request.getParameter("_" + field.getQName().getLocalName()) :
		                request.getParameter(field.getQName().getLocalName());
		            if(value != null && value.length() > 0 && !value.equals("null")) {
		              if (field.getType() == ModelField.TYPE_DATE) {
		                entity.addProperty(ModelField.TYPE_DATE, field.getQName(), value);
		              } else if (field.getType() == ModelField.TYPE_INTEGER) {
		                int val = Integer.valueOf(value);
		                entity.addProperty(ModelField.TYPE_INTEGER, field.getQName(), val);
		              } else if (field.getType() == ModelField.TYPE_LONG) {
		                long val = Long.valueOf(value);
		                entity.addProperty(ModelField.TYPE_LONG, field.getQName(), val);
		              } else if (field.getType() == ModelField.TYPE_BOOLEAN) {
		                boolean val = Boolean.valueOf(value);
		                entity.addProperty(ModelField.TYPE_BOOLEAN, field.getQName(), val);
		              } else {
		                entity.addProperty(field.getQName(), clean(value));
		              }
		            }
		          } catch (Exception e) {
		            e.toString();
		          }
		        }
		        for (ModelRelation relation : getDictionaryService().getModelRelations(entity.getQName())) {
		          String values = request.getParameter(relation.getQName().getLocalName());
		          if (values != null) {
		            String[] ids = values.replaceFirst("\\[", "").replaceAll("\\]", "").split(",");
		            List<Association> assocs = entity.getAssociations(relation.getQName());
		            for (String target_id : ids) {
		              if (target_id.length() > 0) {
		                boolean match = false;
		                for (Association assoc : assocs) {
		                  if (assoc.getTarget().equals(Long.valueOf(target_id))) {
		                    match = true;
		                  }
		                }
		                if (!match) {
		                  Entity target = getEntityService().getEntity(Long.valueOf(target_id));
		                  Association a = new Association(relation.getQName(), entity, target);
		                  entity.getSourceAssociations().add(a);
		                }
		              }
		            }
		          }
		        }
		      } catch (Exception e) {
		        throw new InvalidEntityException(
		            "entity model not found for qname:" + entityQname.toString(), e);
		      }
	    	}
	    return entity;
	  }
  protected void response(RestResponse<Object> data, int status, String message) {
    data.setStatus(status);
    if (status == -1) {
      data.addError(message);
      log.info(message);
    } else if (message != null && message.length() > 0) {
      data.addMessage(message);
      log.info(message);
    }
  }

  protected RestResponse<Object> getEntity(long id, String view, boolean sources,
      boolean targets) {
    RestResponse<Object> data = new RestResponse<Object>();
    try {
      Entity entity =getEntityService().getEntity(id);
      if (entity != null) {
       data.addRow(entity);
      }
      data.setStatus(0);
    } catch (Exception e) {
      e.printStackTrace();
      data.setStatus(-1);
      data.addMessage(e.getMessage());
    }
    return data;
  }

  protected RestResponse<Object> browse(Long parent, String mode, int start, int end) {
    RestResponse<Object> data = new RestResponse<Object>();
    try {
      List<Long> nodes = null;
      if (parent == null || parent.equals("null")) {
        nodes = cache.get(0L);
        if (nodes == null) {
          nodes = new ArrayList<Long>(0);//getEntityService().getAllNodes();
          cache.put(0L, nodes);
        }
      } else {
        Entity parentEntity = getEntityService().getEntity(parent);
        if (mode != null && mode.equals("id")) {
          nodes = new ArrayList<Long>();
          if (parentEntity != null) {
            nodes.add(parentEntity.getId());
          }
        } else if (mode != null && mode.equals("name")) {
          //getEntityService().
        } else {
          if (parentEntity != null) {
            nodes = cache.get(parentEntity.getId());
            if (nodes == null) {
              nodes = new ArrayList<Long>();
              for (Association assoc : parentEntity.getSourceAssociations()) {
                nodes.add(assoc.getTarget());
              }
              cache.put(parentEntity.getId(), nodes);
            }
          }
        }
      }
      if (nodes != null && nodes.size() > 0) {
        data.setStartRow(start);
        if (nodes.size() >= end) {
          data.setEndRow(end);
        } else {
          data.setEndRow(nodes.size() - 1);
        }
        data.setTotal(nodes.size());
        int index = 0;
        for (Long node : nodes) {
          if (index >= start && index < end) {
            data.getRows().add(getNodeData(node, false, false));
          }
          index++;
          if (index == end) {
            break;
          }
        }
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
    return data;
  }

  protected RestResponse<Object> getNode(long id) {
    RestResponse<Object> data = new RestResponse<Object>();
    try {
      data.getRows().add(getNodeData(id, true, true));
    } catch (Exception e) {
      e.printStackTrace();
    }
    return data;
  }

  protected RestResponse<Object> removeNode(long id) {
    RestResponse<Object> data = new RestResponse<Object>();
    try {
      getEntityService().removeEntity(id);
      Map<String, Object> nodeData = new HashMap<String, Object>();
      nodeData.put("id", id);
      data.getRows().add(nodeData);
    } catch (Exception e) {
      e.printStackTrace();
    }
    return data;
  }

  protected Map<String, Object> getNodeData(Long node, boolean sources, boolean targets) {
    Map<String, Object> nodeData = new HashMap<String, Object>();
    try {
      Entity entity = getEntityService().getEntity(node);
      String qname = entity.getQName().toString();
      nodeData.put("id", String.valueOf(node));
      if (qname.equals("{openapps.org_system_1.0}entities")) {
        nodeData.put("name", "Entities");
      } else if (qname.equals("{openapps.org_system_1.0}models")) {
        nodeData.put("name", "Models");
      }
      if (entity.hasProperty(SystemModel.NAME)) {
        nodeData.put("name", entity.getProperty(SystemModel.NAME));
      } else if (entity.hasProperty(SystemModel.DESCRIPTION)) {
        nodeData.put("name", entity.getProperty(SystemModel.DESCRIPTION));
      } else {
        nodeData.put("name", entity.getQName().toString());
      }
      if (entity.getSourceAssociations().size() > 0) {
        nodeData.put("children", "true");
      }
      List<Property> nodeProperties = entity.getProperties();
      for (Property property : nodeProperties) {
        nodeData.put(property.getQName().toString(), String.valueOf(property.getValue()));
      }
      if (sources) {
        List<Map<String, Object>> sourceData = new ArrayList<Map<String, Object>>();
        for (Association relationship : entity.getSourceAssociations()) {
          Map<String, Object> source = new HashMap<String, Object>();
          source.put("id", relationship.getId());
          source.put("start", relationship.getSource());
          source.put("end", relationship.getTarget());
          source.put("type", relationship.getQName());
          List<Property> relationshipProperties = relationship.getProperties();
          for (Property property : relationshipProperties) {
            source.put(property.getQName().toString(), String.valueOf(property.getValue()));
          }
          sourceData.add(source);
        }
        nodeData.put("outgoing", sourceData);
      }
      if (targets) {
        List<Map<String, Object>> sourceData = new ArrayList<Map<String, Object>>();
        for (Association relationship : entity.getTargetAssociations()) {
          Map<String, Object> source = new HashMap<String, Object>();
          source.put("id", relationship.getId());
          source.put("start", relationship.getSource());
          source.put("end", relationship.getTarget());
          source.put("type", relationship.getQName());
          List<Property> relationshipProperties = relationship.getProperties();
          for (Property property : relationshipProperties) {
            source.put(property.getQName().toString(), String.valueOf(property.getValue()));
          }
          sourceData.add(source);
        }
        nodeData.put("incoming", sourceData);
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
    return nodeData;
  }
  /*
  protected void printNode(StringWriter buff, Long node) throws NodeException {
		Map<String,Object> nodeProperties = getEntityService().getNodeProperties(node);
		for(String propertyName : nodeProperties.keySet()) {
			buff.append("<"+propertyName+">"+nodeProperties.get(propertyName)+"</"+propertyName+">");
		}
	}
	protected Long getEntityNode(QName qname) {
		try {
			Relationship modelsRelation = getEntityService().getSingleRelationship(0, new QName(OPENAPPS_ENTITIES.toString()), Direction.OUTGOING);
			if(modelsRelation != null) {
				for(Relationship relation : getEntityService().getRelationships(modelsRelation.getEndNode(), Direction.OUTGOING)) {
					String relQname = getEntityService().hasNodeProperty(relation.getEndNode(), "qname") ? (String)getEntityService().getNodeProperty(relation.getEndNode(), "qname") : "";
					if(relQname.equals(qname.toString()))
						return relation.getEndNode();
				}
			}
		} catch(Exception e) {
			//getLoggingService().error(e);
		}
		return null;
	}
	*/
}
