package org.archivemanager.web.service;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.archivemanager.config.PropertyConfiguration;
import org.archivemanager.data.GridDataItem;
import org.archivemanager.data.TreeNode;
import org.archivemanager.models.SystemModel;
import org.archivemanager.models.dictionary.Model;
import org.archivemanager.models.dictionary.ModelField;
import org.archivemanager.models.dictionary.ModelRelation;
import org.archivemanager.models.system.QName;
import org.archivemanager.models.system.User;
import org.archivemanager.services.cache.CacheService;
import org.archivemanager.services.content.DigitalObjectService;
import org.archivemanager.services.crawling.CrawlingService;
import org.archivemanager.services.dictionary.DataDictionaryService;
import org.archivemanager.services.entity.Association;
import org.archivemanager.services.entity.Entity;
import org.archivemanager.services.entity.EntityService;
import org.archivemanager.services.entity.InvalidAssociationException;
import org.archivemanager.services.entity.InvalidEntityException;
import org.archivemanager.services.entity.ModelValidationException;
import org.archivemanager.services.entity.Property;
import org.archivemanager.services.entity.ValidationField;
import org.archivemanager.services.entity.ValidationResult;
import org.archivemanager.services.scheduling.SchedulingService;
import org.archivemanager.services.search.SearchRequest;
import org.archivemanager.services.search.SearchResponse;
import org.archivemanager.services.search.SearchResult;
import org.archivemanager.services.search.SearchService;
import org.archivemanager.services.search.indexing.IndexingService;
import org.archivemanager.services.security.SecurityService;
import org.archivemanager.util.NumberUtility;
import org.springframework.beans.factory.annotation.Autowired;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;


public abstract class WebserviceSupport {

  private final static Logger log = Logger.getLogger(WebserviceSupport.class.getName());
  private ObjectMapper objectMapper;
  @Autowired
  private EntityService entityService;
  @Autowired
  private SearchService searchService;
  @Autowired
  private IndexingService indexingService;
  @Autowired
  private DataDictionaryService dictionaryService;
  @Autowired
  private CacheService cacheService;
  @Autowired
  private SchedulingService schedulingService;
  @Autowired
  private SecurityService securityService;
  @Autowired
  private PropertyConfiguration propertyService;
  @Autowired
  private DigitalObjectService digitalObjectService;
  @Autowired
  private CrawlingService crawlingService;

  protected void getUser(User user) throws Exception {
    Long nodeId = (Long) cacheService.get("openapps.security.users", String.valueOf(user.getXid()));
    if (nodeId == null) {
      SearchRequest query = new SearchRequest(SystemModel.USER);
      query.addParameter("xid", String.valueOf(user.getXid()));
      SearchResponse userEntities = searchService.search(query);
      Entity userEntity = null;
      if (userEntities != null && userEntities.getResults().size() > 0) {
        if (userEntities.getResults().size() > 1) {
          for (SearchResult u : userEntities.getResults()) {
            if (userEntity == null) {
              userEntity = getEntityService().getEntity(u.getId());
            } else {
              entityService.removeEntity(Long.valueOf(u.getId()));
            }
          }
        } else {
          userEntity = getEntityService().getEntity(userEntities.getResults().get(0).getId());
        }
        nodeId = userEntity.getId();
        cacheService
            .put("openapps.security.users", String.valueOf(user.getXid()), userEntity.getId());
      } else {
        userEntity = new Entity(SystemModel.USER);
        if (userEntity != null) {
          userEntity.setXid(user.getXid());
          userEntity.addProperty(SystemModel.NAME, user.getName());
          userEntity.addProperty(SystemModel.FIRSTNAME, user.getFirstName());
          userEntity.addProperty(SystemModel.LASTNAME, user.getLastName());
          userEntity.addProperty(SystemModel.EMAIL, user.getEmail());
          entityService.updateEntity(userEntity);
          nodeId = userEntity.getId();
          cacheService
              .put("openapps.security.users", String.valueOf(user.getXid()), userEntity.getId());
        }
      }
    }
    user.setId(nodeId);
  }
  
  public Entity getEntity(String name, QName entityQname) throws InvalidEntityException {
    Entity entity = new Entity(entityQname);
    entity.setCreated(System.currentTimeMillis());
    entity.setModified(System.currentTimeMillis());
    entity.setUid(UUID.randomUUID().toString());
    if (entity != null) {
      try {
        if (name != null) {
          entity.addProperty(SystemModel.NAME, name);
        }
        List<ModelField> fields = dictionaryService.getModelFields(entity.getQName());
        for (ModelField field : fields) {
          try {
            if (field.getType() == ModelField.TYPE_DATE) {
              entity.addProperty(ModelField.TYPE_DATE, field.getQName(), null);
            } else if (field.getType() == ModelField.TYPE_INTEGER) {
              entity.addProperty(ModelField.TYPE_INTEGER, field.getQName(), 0);
            } else if (field.getType() == ModelField.TYPE_LONG) {
              entity.addProperty(ModelField.TYPE_LONG, field.getQName(), 0);
            } else if (field.getType() == ModelField.TYPE_BOOLEAN) {
              entity.addProperty(ModelField.TYPE_BOOLEAN, field.getQName(), false);
            } else {
              entity.addProperty(field.getQName(), null);
            }

          } catch (Exception e) {
            e.toString();
          }
        }
      } catch (Exception e) {
        throw new InvalidEntityException(
            "entity model not found for qname:" + entityQname.toString(), e);
      }
    }
    return entity;
  }

  public Association getAssociation(HttpServletRequest request, String id, String source,
      String target, QName associationQname) throws InvalidAssociationException {
    Association entity = null;
//		String id = request.getParameter("id");
//		String source = request.getParameter("source");
//		String target = request.getParameter("target");
    if (id != null && id.length() > 0) {
      entity = entityService.getAssociation(Long.valueOf(id));
    } else {
      if (source == null) {
        throw new InvalidAssociationException(
            "association source is null for qname:" + associationQname.toString());
      }
      if (target == null) {
        throw new InvalidAssociationException(
            "association target is null for qname:" + associationQname.toString());
      }
      try {
        entity = new Association(associationQname);
        if (NumberUtility.isLong(source)) {
          entity.setSource(Long.valueOf(source));
        } else {
          entity.setSourceUid(source);
        }
        if (NumberUtility.isLong(target)) {
          entity.setTarget(Long.valueOf(target));
        } else {
          entity.setTargetUid(target);
        }
      } catch (Exception e) {
        throw new InvalidAssociationException(
            "invalid entities source:" + source + " target:" + target, e);
      }
    }
    if (entity == null) {
      throw new InvalidAssociationException(
          "association cannot be found for qname:" + associationQname.toString());
    }
    try {
      Entity sourceEntity = entityService.getEntity(Long.valueOf(source));
      ModelRelation model = dictionaryService.getModelRelation(sourceEntity.getQName(), associationQname);
      if(model == null) model = dictionaryService.getTargetModelRelation(sourceEntity.getQName(), associationQname);
      if(model == null) {
        throw new InvalidAssociationException(
            "entity relation not found for qname:" + associationQname.toString());
      }
      for (ModelField field : model.getFields()) {
        String value = request.getParameter(field.getQName().toString());
        if (value == null) {
          value = field.getQName().getLocalName().equals("function") ?
              request.getParameter("_" + field.getQName().getLocalName()) :
              request.getParameter(field.getQName().getLocalName());
        }
        if (value != null && !value.equals("null")) {
          /*
					if(field.getFormat() == ModelField.FORMAT_PASSWORD) {
						SimpleHash simpleHash = new SimpleHash(hash, value);
						value = simpleHash.toHex();
					}
					*/
          try {
            entity.addProperty(field.getQName(), value);
          } catch (Exception e) {
            e.toString();
          }
        } else {
          if (entity.hasProperty(field.getQName())) {
            entity.removeProperty(field.getQName());
          }
        }
      }
    } catch (Exception e) {
      throw new InvalidAssociationException("", e);
    }
    return entity;
  }

  public ValidationResult validate(Entity entity) {
    ValidationResult result = new ValidationResult();
    try {
      Model model = dictionaryService.getModel(entity.getQName());
      if (model == null) {
        result.setException(ValidationResult.MODEL_MISSING);
      } else {
        for (ModelField field : model.getFields()) {
          if (field.getQName() != null && field.getQName().getNamespace() != null
              && field.getQName().getLocalName() != null) {
            Property property = entity
                .getProperty(field.getQName().getNamespace(), field.getQName().getLocalName());
            if (property == null) {
              property = entity.getProperty("", field.getQName().getLocalName());
            }
            if (property != null) {
              if (property.getQName().getNamespace() == null) {
                property.getQName().setNamespace(field.getQName().getNamespace());
              }
              if (property.toString().length() < field.getMinSize()) {
                result.addValidationField(new ValidationField(field.getQName().getLocalName(),
                    ValidationField.MINIMUM_VALUE_RESTRICTION));
              } else if (field.getMaxSize() > 0 && property.toString().length() > field
                  .getMaxSize()) {
                result.addValidationField(new ValidationField(field.getQName().getLocalName(),
                    ValidationField.MAXIMUM_VALUE_RESTRICTION));
              }
            } else if (field.isMandatory()) {
              result.addValidationField(new ValidationField(field.getQName().getLocalName(),
                  ValidationField.REQUIRED_VALUE_MISSING));
            }
          }
        }
      }
    } catch (Exception e) {
      result.setValid(false);
      result.setMessage(e.getLocalizedMessage());
      log.log(Level.SEVERE, result.toString(), e);
    }
    return result;
  }

  public ValidationResult validate(Association association) throws ModelValidationException {
    ValidationResult result = new ValidationResult(true);
    if (association.getSource() == 0) {
      result.setValid(false);
      result.addValidationField(new ValidationField("source", ValidationResult.MISSING_VALUE));
    }
    if (association.getTarget() == 0) {
      result.setValid(false);
      result.addValidationField(new ValidationField("target", ValidationResult.MISSING_VALUE));
    }
    if (result.isValid()) {
      try {
    	  Entity sourceEntity = getEntityService().getEntity(association.getSource());
    	  Entity targetEntity = getEntityService().getEntity(association.getTarget());
    	  Model model = dictionaryService.getModel(sourceEntity.getQName());
        
        QName endQname = targetEntity.getQName();
        if (model != null) {
          if (association.getQName() == null) {
            for (ModelRelation rel : model.getRelations()) {
              if (rel.getEndName().equals(endQname)) {
                association.setQname(rel.getQName());
                result.setValid(true);
                return result;
              }
            }
          } else {
            Model m = model;
            while (m != null) {
              if (m.containsRelation(association.getQName())) {
                result.setValid(true);
                return result;
              }
              m = dictionaryService.getModel(m.getParentName());
            }
          }
          result.setValid(false);
          result.setMessage("Association of type " + association.getQName() + " not found.");
        }
      } catch (Exception e) {
        throw new ModelValidationException(result);
      }
    }
    return result;
  }

  protected TreeNode getTreeNode(String id, String uid, String parent, String type,
      String namespace, String title, Boolean isFolder) {
    TreeNode node = new TreeNode();
    node.setId(uid);
    node.setUid(uid);
    node.setParent(parent);
    node.setType(type);
    node.setNamespace(namespace);
    node.setTitle(title);
    node.setIsFolder(isFolder.toString());
    if (type != null) {
      node.setIcon("/theme/images/tree_icons/" + type + ".png");
    }
    return node;
  }

  public ObjectMapper getObjectMapper() {
    if (objectMapper == null) {
      objectMapper = new ObjectMapper()
          .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
          .disable(SerializationFeature.FAIL_ON_EMPTY_BEANS)
          .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
          .setSerializationInclusion(JsonInclude.Include.NON_EMPTY)
          .registerModule(new Jdk8Module())
          .registerModule(new JavaTimeModule());
    }
    return objectMapper;
  }

  public EntityService getEntityService() {
    return entityService;
  }

  public SearchService getSearchService() {
    return searchService;
  }
  
  public IndexingService getIndexingService() {
	return indexingService;
  }

public DataDictionaryService getDictionaryService() {
    return dictionaryService;
  }

  public CacheService getCacheService() {
    return cacheService;
  }

  public SchedulingService getSchedulingService() {
    return schedulingService;
  }

  public SecurityService getSecurityService() {
    return securityService;
  }

  public PropertyConfiguration getPropertyService() {
    return propertyService;
  }

  public DigitalObjectService getDigitalObjectService() {
    return digitalObjectService;
  }

  public CrawlingService getCrawlingService() {
    return crawlingService;
  }

  protected Object clean(Object in) {
	  if(in != null && in instanceof String) {
		  String val = (String)in;
		  if(val.startsWith("<p>") && val.endsWith("</p>"))
			  val = val.substring(3, val.length()-4);
		  if(in.equals("&nbsp;") || in.equals("null")) return "";
		  return val;
	  }
	  return in;
  }
  protected void prepareResponse(HttpServletResponse response) {
    response.setHeader("Pragma", "no-cache");
    response.setHeader("Cache-Control", "no-cache");
    response.setDateHeader("Expires", 0);
  }

  protected List<Object> getEntityData(List<Entity> data) {
    List<Object> entities = new ArrayList<Object>();
    for (Entity entity : data) {
      GridDataItem item = new GridDataItem(entity.getUid());
      int columnCount = entity.getProperties().size();
      for (int i = 0; i < entity.getProperties().size(); i++) {
        if (i == 0) {
          item.setValue0(entity.getProperties().get(i).getValue());
        } else if (i == 1) {
          item.setValue1(entity.getProperties().get(i).getValue());
        } else if (i == 2) {
          item.setValue2(entity.getProperties().get(i).getValue());
        } else if (i == 3) {
          item.setValue3(entity.getProperties().get(i).getValue());
        } else if (i == 4) {
          item.setValue4(entity.getProperties().get(i).getValue());
        } else if (i == 5) {
          item.setValue5(entity.getProperties().get(i).getValue());
        } else if (i == 6) {
          item.setValue6(entity.getProperties().get(i).getValue());
        } else if (i == 7) {
          item.setValue7(entity.getProperties().get(i).getValue());
        } else if (i == 8) {
          item.setValue8(entity.getProperties().get(i).getValue());
        } else if (i == 9) {
          item.setValue9(entity.getProperties().get(i).getValue());
        } else if (i == 10) {
          item.setValue10(entity.getProperties().get(i).getValue());
        }
      }
      for (int i = 0; i < entity.getSourceAssociations().size(); i++) {
        Association assoc = entity.getSourceAssociations().get(i);
        try {
          Entity target = getEntityService().getEntity(assoc.getTarget());
          String targetName = target.getPropertyValueString(SystemModel.NAME);
          if (columnCount + i == 0) {
            item.setValue0(targetName);
          } else if (columnCount + i == 1) {
            item.setValue1(targetName);
          } else if (columnCount + i == 2) {
            item.setValue2(targetName);
          } else if (columnCount + i == 3) {
            item.setValue3(targetName);
          } else if (columnCount + i == 4) {
            item.setValue4(targetName);
          } else if (columnCount + i == 5) {
            item.setValue5(targetName);
          } else if (columnCount + i == 6) {
            item.setValue6(targetName);
          } else if (columnCount + i == 7) {
            item.setValue7(targetName);
          } else if (columnCount + i == 8) {
            item.setValue8(targetName);
          } else if (columnCount + i == 9) {
            item.setValue9(targetName);
          } else if (columnCount + i == 10) {
            item.setValue10(targetName);
          }
        } catch (Exception e) {
          e.printStackTrace();
        }
      }
      for (int i = 0; i < entity.getTargetAssociations().size(); i++) {
        Association assoc = entity.getTargetAssociations().get(i);
        try {
          Entity target = getEntityService().getEntity(assoc.getSource());
          String targetName = target.getPropertyValueString(SystemModel.NAME);
          if (columnCount + i == 0) {
            item.setValue0(targetName);
          } else if (columnCount + i == 1) {
            item.setValue1(targetName);
          } else if (columnCount + i == 2) {
            item.setValue2(targetName);
          } else if (columnCount + i == 3) {
            item.setValue3(targetName);
          } else if (columnCount + i == 4) {
            item.setValue4(targetName);
          } else if (columnCount + i == 5) {
            item.setValue5(targetName);
          } else if (columnCount + i == 6) {
            item.setValue6(targetName);
          } else if (columnCount + i == 7) {
            item.setValue7(targetName);
          } else if (columnCount + i == 8) {
            item.setValue8(targetName);
          } else if (columnCount + i == 9) {
            item.setValue9(targetName);
          } else if (columnCount + i == 10) {
            item.setValue10(targetName);
          }
        } catch (Exception e) {
          e.printStackTrace();
        }
      }
      entities.add(item);
    }
    return entities;
  }
}
