package org.archivemanager.services.data.listener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.archivemanager.config.PropertyConfiguration;
import org.archivemanager.models.ClassificationModel;
import org.archivemanager.models.RepositoryModel;
import org.archivemanager.models.SystemModel;
import org.archivemanager.models.dictionary.ModelField;
import org.archivemanager.models.system.QName;
import org.archivemanager.services.dictionary.DataDictionaryService;
import org.archivemanager.services.entity.Association;
import org.archivemanager.services.entity.BasePersistenceListener;
import org.archivemanager.services.entity.Entity;
import org.archivemanager.services.entity.Property;
import org.archivemanager.services.entity.neo4j.RemoteNeo4jService;
import org.archivemanager.services.search.indexing.IndexingService;
import org.neo4j.driver.v1.types.Node;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


@Component
public class DataIntegrityPersistenceListener extends BasePersistenceListener {
	private final static Logger log = Logger.getLogger(DataIntegrityPersistenceListener.class.getName());
	@Autowired private DataDictionaryService dictionaryService;
	@Autowired private RemoteNeo4jService neo4jService;
	@Autowired private PropertyConfiguration properties;
	@Autowired private IndexingService indexingService;
	
	public void onSelect(Entity entity) {
		
	}
	
	public void onIndex(Entity entity) {
		boolean dirty = clean(entity);
		if(dirty) {
			neo4jService.save(entity.getId(), toMap(entity));
			log.info("data integrity entity update id:"+entity.getId()+", qname:"+entity.getQName()+", name:"+entity.getName());
		}
	}
			
	protected boolean clean(Entity entity) {
		boolean dirty = false;
		try {
			if(dictionaryService.isA(entity.getQName(), RepositoryModel.ITEM)) {
				if(entity.getName() == null || entity.getName().equals("")) {
					Property dateExpression = entity.getProperty(RepositoryModel.DATE_EXPRESSION);
					if(dateExpression != null) {
						entity.addProperty(SystemModel.NAME, dateExpression.getValue());
					}
				}
			}
			List<Property> deletes = new ArrayList<Property>();
			for(Property property : entity.getProperties()) {
				Object value = property.getValue();
				if(value == null)	{
					continue;										
				}
				if(value instanceof String) {
					String val = ((String)value).trim();
					if(val.equals("null") || val.equals("<p>&nbsp;</p>"))	{
						property.setValue(null);
						dirty = true;
						continue;
					}
					if(val.startsWith("<p>") && val.endsWith("</p>")) {
						val = val.substring(3, val.length()-4);
						property.setValue(val);
						dirty = true;
					}
					if(val.contains("<br>")) {
						val = val.replace("<br>", "");
						property.setValue(val);
						dirty = true;
					}
				}
				if(property.getQName().equals(ClassificationModel.NAMED_ENTITY) || property.getQName().equals(ClassificationModel.PERSON) || property.getQName().equals(ClassificationModel.CORPORATION)) {
					if(property.getQName().toString().equals("openapps_org_repository_1_0_function")) {
						moveProperty(entity, "openapps_org_repository_1_0_function", "openapps_org_classification_1_0_function");
						dirty = true;
					}
				}				
				if(property.getQName().equals(RepositoryModel.CATEGORY)) {
					if(property.getQName().toString().equals("openapps_org_repository_1_0_url")) {
						deletes.add(property);
					}
				}				
				if(dictionaryService.isA(entity.getQName(), RepositoryModel.ITEM)) {
					if(property.getQName().toString().equals("openapps_org_repository_1_0_level")) {
						deletes.add(property);
					}					
				}
				if(property.getQName().equals(ClassificationModel.PERSON) || property.getQName().equals(ClassificationModel.CORPORATION)) {
					if(property.getQName().toString().equals("openapps_org_classification_1_0_search_values")) {
						deletes.add(property);
					}
				}				
				if(property.getQName().toString().equals("openapps_org_repository_1_0_description")) {
					moveProperty(entity, "openapps_org_repository_1_0_description", "openapps_org_system_1_0_description");
					dirty = true;
				}
				if(property.getQName().toString().equals("openapps_org_content_1_0_title")) {
					moveProperty(entity, "openapps_org_content_1_0_title", "openapps_org_system_1_0_name");
					dirty = true;
				}					
				if(property.getQName().toString().equals("openapps_org_system_1_0_title")) {
					moveProperty(entity, "openapps_org_system_1_0_title", "openapps_org_system_1_0_name");
					dirty = true;
				}
				if(property.getQName().toString().equals("name")) {
					moveProperty(entity, "name", SystemModel.NAME.toString());
					dirty = true;
				}				
				if(property.getQName().toString().equals("openapps_org_classification_1_0_url")) {
					moveProperty(entity, "openapps_org_classification_1_0_url", "openapps_org_system_1_0_url");
					dirty = true;
				}
				if(property.getQName().toString().equals("openapps_org_repository_1_0_isPublic")) {
					moveProperty(entity, "openapps_org_repository_1_0_isPublic", "openapps_org_repository_1_0_public");
					dirty = true;
				}
				if(property.getQName().toString().equals("openapps_org_repository_1_0_extent_units")) {
					moveProperty(entity, "openapps_org_repository_1_0_extent_units", "openapps_org_repository_1_0_extent_type");
					dirty = true;
				}
				if(property.getQName().toString().equals("openapps_org_content_1_0_name") || 
						property.getQName().toString().equals("openapps_org_system_1_0_title")) {
					moveProperty(entity, property.getQName().toString(), "openapps_org_system_1_0_name");
					dirty = true;
				}			
			}
			if(deletes.size() > 0) {
				dirty = true;
				for(Property property : deletes) {
					entity.removeProperty(property.getQName());
				}
			}
			
			for(Association association : entity.getAssociations()) {
				if(properties.getRelationshipCleanup().contains(association.getQName().toString())) {
					neo4jService.removeRelation(association.getId());
					indexingService.removeIndexAssociation(association.getId());
					log.log(Level.INFO,"cleaned up relation:"+association.getId()+", qname="+association.getQName());
				} else {
					boolean save = false;
					if(association.getQName().equals(ClassificationModel.NAMED_ENTITIES)) {
						Node source = neo4jService.getNode(association.getSource());
						Node  target = neo4jService.getNode(association.getTarget());
						if(source != null && target != null) {
							QName sourceName = new QName(source.get("qname").asString());
							QName targetName = new QName(target.get("qname").asString());
							association.setSourceName(sourceName);
							association.setTargetName(targetName);						
							if(targetName.equals(ClassificationModel.CORPORATION) && !association.getQName().equals(ClassificationModel.CORPORATIONS)) {
								association.setQname(ClassificationModel.CORPORATIONS);
								save = true;
							} else if(targetName.equals(ClassificationModel.PERSON) && !association.getQName().equals(ClassificationModel.PEOPLE)) {
								association.setQname(ClassificationModel.PEOPLE);
								save = true;
							} else if(targetName.equals(ClassificationModel.FAMILY) && !association.getQName().equals(ClassificationModel.FAMILIES)) {
								association.setQname(ClassificationModel.FAMILIES);	
								save = true;
							}						
						} else {
							log.info("invalid association id="+association.getId()+", source="+association.getSource()+", target="+association.getTarget());
						}
					}
					if(save) {
						neo4jService.removeRelation(association.getId());
						indexingService.removeIndexAssociation(association.getId());
						neo4jService.addRelation(association.getQName(), association.getSource(), association.getTarget());
						log.info("association move complete : id="+association.getId()+", source="+ClassificationModel.NAMED_ENTITIES+", target="+association.getQName());
					}
				}
			}
		} catch(Exception e) {
			log.log(Level.SEVERE, "", e);
		}
		return dirty;
	}
	
	protected void moveProperty(Entity entity, String source, String target) {
		QName sourceName = new QName(source);
		Object sourceValue = entity.getPropertyValue(sourceName);
		if(sourceValue != null) {
			entity.removeProperty(sourceName);
			entity.addProperty(new QName(target), sourceValue);
			log.info("property move complete : id="+entity.getId()+", source="+source+", target="+target);
		} else log.info("property move failed, source value missing : id="+entity.getId()+", source="+source+", target="+target);
	}
	protected Map<String,Object> toMap(Entity entity) {
		if(entity.getUid() == null || entity.getUid().length() == 0) entity.setUid(java.util.UUID.randomUUID().toString());
		Map<String,Object> properties = new HashMap<String,Object>();
		properties.put("uid", entity.getUid());
		properties.put("accessed", entity.getAccessed());
		properties.put("created", entity.getCreated());
		properties.put("creator", entity.getCreator());
		properties.put("modified", entity.getModified());
		properties.put("modifier", entity.getModifier());
		properties.put("xid", entity.getXid());
		properties.put("user", entity.getUser());
		properties.put("deleted", entity.isDeleted());
		properties.put("qname", entity.getQName().toString());		
		try {
			List<ModelField> fields = dictionaryService.getModelFields(entity.getQName());
			for(Property property : entity.getProperties()) {
				ModelField field = null;
				for(ModelField f : fields) {
					if(f.getQName().equals(property.getQName())) {
						field = f;
						break;
					}
				}
				if(field != null) {
					if(property.getValue() != null && !property.getValue().equals("")) {
						if(property.getValue() instanceof String) {
							properties.put(property.getQName().toString(), clean((String)property.getValue()));
						} else {
							properties.put(property.getQName().toString(), property.getValue());
						}
					}
				} 
			}
		} catch(Exception e) {
			log.log(Level.SEVERE, "", e);
		}
		return properties;
	}
	protected String clean(String in) {
		if(in.startsWith("<p>") && in.endsWith("</p>")) {
			in = in.substring(3, in.length()-4);
		}
		if(in.equals("&nbsp;") || in.equals("null")) return "";
		return in;
	}
}
