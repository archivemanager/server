package org.archivemanager.services.search.indexing;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import static java.lang.Math.toIntExact;

import org.archivemanager.models.SystemModel;
import org.archivemanager.models.dictionary.ModelField;
import org.archivemanager.models.system.QName;
import org.archivemanager.services.dictionary.DataDictionaryService;
import org.archivemanager.services.entity.Association;
import org.archivemanager.services.entity.Entity;
import org.archivemanager.services.entity.EntityIndexer;
import org.archivemanager.services.entity.EntityService;
import org.archivemanager.services.entity.InvalidEntityException;
import org.archivemanager.services.entity.Property;
import org.archivemanager.services.search.parsing.date.DateParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DefaultEntityIndexer implements EntityIndexer {
	@Autowired private DataDictionaryService dictionaryService;
	@Autowired private EntityService entityService;
	
	private SimpleDateFormat dateFormat = new SimpleDateFormat("d MMMM, yyyy");
	
	
	public DefaultEntityIndexer(DataDictionaryService dictionaryService, EntityService entityService) {
		this.dictionaryService = dictionaryService;
		this.entityService = entityService;
	}
	
	public void index(Entity entity, IndexEntity data) {
		StringBuffer freeText = new StringBuffer();
		if(entity != null) {
			try {
				String id = String.valueOf(entity.getId());
				if(id != null) {			
					//Model model = dictionaryService.getModel(entity.getQName());
					
					//data.getFields().add(new IndexField(ModelField.TYPE_SMALLTEXT, "uuid", entity.getUid(), false));				
					Property name = entity.getProperty(SystemModel.NAME.toString());
					if(name != null) {
						data.getFields().add(new IndexField(SystemModel.NAME.toString(), name.getValue()));
						appendFreeText(freeText, (String)name.getValue());
					} else if(entity.getName() != null) {
						data.getFields().add(new IndexField(SystemModel.NAME.toString(), entity.getName()));
						appendFreeText(freeText, entity.getName());
					}
					try {
						List<QName> qnames = dictionaryService.getQNames(entity.getQName());
						if(qnames.size() > 1) {
							String[] array = new String[qnames.size()];
							for(int i=0; i < qnames.size(); i++) {
			        			array[i] = qnames.get(i).toString();
			        		}
							data.getFields().add(new IndexField("qname", array));
						} else data.getFields().add(new IndexField("qname", qnames.get(0).toString()));
						
					} catch(Exception e) {
						throw new InvalidEntityException("", e);
					}				
					if(entity.getCreated() > 0) data.getFields().add(new IndexField("created", entity.getCreated()));
					if(entity.getModified() > 0) data.getFields().add(new IndexField("modified", entity.getModified()));
					data.getFields().add(new IndexField("deleted", entity.isDeleted()));
					if(entity.getUser() > 0) data.getFields().add(new IndexField("user", entity.getUser()));
									
					List<ModelField> modelFields = dictionaryService.getModelFields(entity.getQName());
					for(ModelField field : modelFields) {
						Property property = entity.getProperty(field.getQName());
						if(property != null && property.getValue() != null && !property.getValue().equals("")) {
							if(field.getType().equals(ModelField.TYPE_BOOLEAN)) {
								Boolean val = property.getValue() instanceof Boolean ? (Boolean)property.getValue() : Boolean.valueOf((String)property.getValue());
								data.getFields().add(new IndexField(field.getQName().toString(), val));
							} else if(field.getType().equals(ModelField.TYPE_DATE)) {
								String textVal = (String)property.getValue();
								data.getFields().add(new IndexField(field.getQName().toString(), textVal));
								Date date_value = parseTextDate(textVal, data);
								if(date_value != null) {
									long epoch = date_value.getTime();
									data.getFields().add(new IndexField(field.getQName().toString()+"_", epoch));
								} else {
									data.getFields().add(new IndexField(field.getQName().toString()+"_", 0L));
								}
							} else if(field.getType().equals(ModelField.TYPE_DOUBLE)) {
								Double val = property.getValue() instanceof Double ? (Double)property.getValue() : Double.valueOf((String)property.getValue());
								data.getFields().add(new IndexField(field.getQName().toString(), val));
							} else if(field.getType().equals(ModelField.TYPE_INTEGER)) {
								Integer val = 0;
								if(property.getValue() instanceof Integer) val = (Integer)property.getValue();
								if(property.getValue() instanceof Long) val = toIntExact((Long)property.getValue());
								if(property.getValue() instanceof String) {
									String v = (String)property.getValue();
									if(v.length() > 0) val = Integer.valueOf((String)property.getValue());
								}
								data.getFields().add(new IndexField(field.getQName().toString(), val));
							} else if(field.getType().equals(ModelField.TYPE_LONG)) {
								Long val = property.getValue() instanceof Long ? (Long)property.getValue() : Long.valueOf((String)property.getValue());
								data.getFields().add(new IndexField(field.getQName().toString(), val));
							} else if(field.getType().equals(ModelField.TYPE_LONGTEXT) || 
									field.getType().equals(ModelField.TYPE_MEDIUMTEXT) ||
									field.getType().equals(ModelField.TYPE_SMALLTEXT)) {
								String textVal = (String)property.getValue();
								data.getFields().add(new IndexField(property.getQName().toString(), textVal));
								if(field.isSearchable()) appendFreeText(freeText, textVal);
							}
						}
					}	
					
					List<Long> associations = new ArrayList<Long>();
					for(Association assoc : entity.getSourceAssociations()) {
						if(associations.contains(assoc.getTarget())) {
							//log.info("duplicate source association for id:"+assoc.getTarget()+" qname:"+assoc.getQName().toString());
						} else {
							//log.info("added source association for id:"+assoc.getTarget()+" qname:"+assoc.getQName().toString());
							Entity targetEntity = entityService.getEntity(assoc.getTarget());
							if(targetEntity != null && targetEntity.getName() != null)
								data.getFacets().add(new IndexFacet(assoc.getQName(), targetEntity.getName(), assoc.getTarget()));
							associations.add(assoc.getTarget());
						}
					}
					Long[] source_ids = associations.toArray(new Long[associations.size()]);
					data.getFields().add(new IndexField(SystemModel.SOURCE_ASSOC.getLocalName(), source_ids));
					associations.clear();
					for(Association assoc : entity.getTargetAssociations()) {
						if(associations.contains(assoc.getSource())) {
							//log.info("duplicate target association for id:"+assoc.getSource()+" qname:"+assoc.getQName().toString());
						} else {
							//log.info("added target association for id:"+assoc.getSource()+" qname:"+assoc.getQName().toString());
							Entity sourceEntity = entityService.getEntity(assoc.getSource());
							if(sourceEntity != null && sourceEntity.getName() != null)
								data.getFacets().add(new IndexFacet(assoc.getQName(), sourceEntity.getName(), assoc.getSource()));
							associations.add(assoc.getSource());
						}
					}
					Long[] target_ids = associations.toArray(new Long[associations.size()]);
					data.getFields().add(new IndexField(SystemModel.TARGET_ASSOC.getLocalName(), target_ids));
					/** Access Control List **/
					List<Association> permissionAssocs = entity.getSourceAssociations(SystemModel.PERMISSIONS);
					if(permissionAssocs != null && permissionAssocs.size() > 0) {
						for(Association assoc : permissionAssocs) {
							Entity targetEntity = entityService.getEntity(assoc.getTarget());
							String nodeid = targetEntity.getPropertyValueString(SystemModel.PERMISSION_TARGET);
							if(nodeid != null && !nodeid.equals("null")) 
								data.getFields().add(new IndexField("acl", Long.valueOf(nodeid)));
							else data.getMessages().add("no permission target available on association");
						}
						
					} else data.getFields().add(new IndexField("acl", 0));
				}
			} catch(Exception e) {
				e.printStackTrace();
			}
		}
	}
	@Override
	public void index(Association association, IndexAssociation data) {
		//SearchResult target = searchService.getNode(association.getTarget());
		//Entity source = entityService.getEntity(association.getSource());
		data.setId(association.getId());
		data.setQName(association.getQName());
		data.getFields().add(new IndexField(SystemModel.SOURCE.toString(), association.getSource()));
		data.getFields().add(new IndexField(SystemModel.TARGET.toString(), association.getTarget()));
		if(association.getSourceName() != null) data.getFields().add(new IndexField(SystemModel.SOURCE_NAME.toString(), association.getSourceName().toString()));
		if(association.getTargetName() != null) data.getFields().add(new IndexField(SystemModel.TARGET_NAME.toString(), association.getTargetName().toString()));
		for(Property property : association.getProperties()) {
			if(property.getValue() != null && !property.getValue().equals(""))
				data.getFields().add(new IndexField(property.getQName().toString(), property.getValue()));
		}
		/*
		if(target != null) {
			for(Property property : target.getProperties()) {
				if(property.getValue() != null && !property.getValue().equals(""))
					data.getFields().add(new IndexField("target_"+property.getQName().toString(), property.getValue()));
			}
		}
		if(source != null) {
			for(Property property : source.getProperties()) {
				if(property.getValue() != null && !property.getValue().equals(""))
					data.getFields().add(new IndexField("source_"+property.getQName().toString(), property.getValue()));
			}
		}
		*/
	}
	
	private DateParser parser = new DateParser();
	protected Date parseTextDate(String in, IndexEntity data) {
		String dateStr = cleanDate(in);
		Date date = parser.parse(dateStr);
		if(date == null && !in.toLowerCase().equals("undated")) 
			data.getMessages().add("problem parsing "+in);
		else
			data.getMessages().add(in+" parsed to "+dateFormat.format(date)+" and epoch "+date.getTime());
		return date;
	}
	protected String cleanDate(String in) {
		in = in.replace("circa ", "");
		in = in.replace("after ", "");
		in = in.replace("received ", "");
		in = in.replace("postmarked ", "");
		in = in.replace(".", "");
		in = in.replace("[", "");
		in = in.replace("]", "");
		if(in.contains(",") && (in.contains("/") || in.contains("-"))) {
			String[] dates = in.split(",");
			if(dates.length > 0) return dates[0].trim();
		}
		return in;
	}	
	protected void appendFreeText(StringBuffer freeText, String text) {
		if(text == null) return;
		String cleanText = text.replace(",", "").replace("\"", "").replace(";", "").replace(".", "").replace(":", "");
		String[] parts = cleanText.split(" ");
		for(String part : parts) {
			if(freeText.indexOf(part) == -1) 
				freeText.append(part.toLowerCase()+" ");
		}
	}

	public DataDictionaryService getDictionaryService() {
		return dictionaryService;
	}
	public EntityService getEntityService() {
		return entityService;
	}
	
}
