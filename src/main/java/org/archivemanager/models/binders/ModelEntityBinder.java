package org.archivemanager.models.binders;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.archivemanager.models.dictionary.ModelField;
import org.archivemanager.models.dictionary.ModelRelation;
import org.archivemanager.models.system.QName;
import org.archivemanager.services.dictionary.DataDictionaryService;
import org.archivemanager.services.entity.Association;
import org.archivemanager.services.entity.Entity;
import org.archivemanager.services.entity.EntityService;
import org.archivemanager.services.entity.Property;
import org.archivemanager.web.model.AssociationRecord;
import org.archivemanager.web.model.EntityRecord;
import org.archivemanager.web.model.PropertyRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class ModelEntityBinder extends EntityBinderSupport {
	@Autowired private DataDictionaryService dictionaryService;
	@Autowired private EntityService entityService;
	@Autowired private BCryptPasswordEncoder passwordEncoder;
	
	
	public Entity getEntity(EntityRecord record) {
		Entity entity = record.getId() != 0 ? entityService.getEntity(record.getId()) : new Entity(record.getId(), record.getQName());
		entity.setQName(record.getQName());
		List<ModelField> fields = dictionaryService.getModelFields(entity.getQName());
		for(ModelField field : fields) {
			PropertyRecord property = getProperty(record, field);
			if(property != null) entity.addProperty(property.getQName(), property.getValue());
		}
		/*
		List<ModelRelation> relations = dictionaryService.getModelRelations(entity.getQName(), true);
		for(ModelRelation relation : relations) {
			if(!relation.isHidden()) {
				if(relation.getMapping() != null) {
					ModelRelation mappedRelation = dictionaryService.getModelRelation(entity.getQName(), relation.getMapping());					
					List<AssociationRecord> associations = getAssociations(record, mappedRelation.getQName());
					if(associations != null) {
						for(AssociationRecord r : associations) {
							Association a = new Association(r.getId(), r.getQName(), r.getSource(), r.getTarget());
							entity.getSourceAssociations().add(a);						
						}
					}
				} else {
					List<AssociationRecord> associations = getAssociations(record, relation.getQName());
					if(associations != null) {
						for(AssociationRecord r : associations) {
							if(r.getQName() == null || r.getSource() == null || r.getTarget() == null) continue;
							Association assoc = entityService.getAssociation(r.getQName(), r.getSource(), r.getTarget());
							if(assoc == null) {								
								Association a = new Association(r.getId(), r.getQName(), r.getSource(), r.getTarget());
								entity.getSourceAssociations().add(a);	
							}
						}
					}
				}				
			}
		}
		*/		
		return entity;
	}
	public Association getAssociation(AssociationRecord record) {
		Association association = record.getId() != 0 ? entityService.getAssociation(record.getId()) : new Association(record.getId(), record.getQName());
		association.setSource(record.getSource());
		association.setTarget(record.getTarget());
		Entity sourceEntity = entityService.getEntity(record.getSource());
		List<ModelField> fields = dictionaryService.getModelFields(sourceEntity.getQName(), record.getQName());
		for(ModelField field : fields) {
			PropertyRecord property = getProperty(record, field.getQName());
			if(property != null) association.addProperty(property.getQName(), property.getValue());
		}		
		return association;
	}
	public EntityRecord getEntityRecord(Long id, Entity entity) {
		long update = entity.getModified() == 0 ? entity.getCreated() : entity.getModified();
		String modified = update > 0 ? DateFormat.getInstance().format(new Date(update)) : "";
		EntityRecord record = new EntityRecord(id, entity.getQName(), modified);
		List<ModelField> fields = dictionaryService.getModelFields(entity.getQName()).stream().filter(f->!f.getFormat().equals("password")).collect(Collectors.toList());
		for(ModelField field : fields) {
			Property property = entity.getProperty(field.getQName());
			if(property == null) record.getProperties().add(new PropertyRecord(field.getQName(), "", field.getLabel(), null));
			else record.getProperties().add(new PropertyRecord(property.getQName(), "", field.getLabel(), property.getValue()));
		}
		List<ModelRelation> relations = dictionaryService.getModelRelations(entity.getQName()).stream().filter(r->!r.isHidden()).collect(Collectors.toList());
		for(ModelRelation relation : relations) {
			if(!relation.isHidden()) {
				record.getAssociations().put(relation.getQName(),  new ArrayList<AssociationRecord>());
				if(relation.getMapping() != null) {
					ModelRelation mappedRelation = dictionaryService.getModelRelation(entity.getQName(), relation.getMapping());					
					List<Association> associations = entity.getSourceAssociations(mappedRelation.getQName(),relation.getQName());
					for(Association a : associations) {
						try {
							Entity targetEntity = entityService.getEntity(a.getTarget());
							if(targetEntity.getQName().toString().equals(relation.getEndName().toString())) {
								AssociationRecord r = getAssociationRecord(a);
								r.getProperties().addAll(mappedRelation.getFields().stream().map(p-> new PropertyRecord(p.getQName(), "", p.getLabel(), p.getValue())).collect(Collectors.toList()));
								List<AssociationRecord> records = record.getAssociations().get(relation.getQName());				
								records.add(r);
							}
						} catch(Exception e) {
							e.printStackTrace();
						}
					}
				} else {
					record.getAssociations().put(relation.getQName(), new ArrayList<AssociationRecord>());
					List<Association> associations = entity.getSourceAssociations(relation.getQName());
					for(Association a : associations) {
						AssociationRecord r = getAssociationRecord(a);
						List<AssociationRecord> records = record.getAssociations().get(relation.getQName());				
						records.add(r);
					}
				}				
			}
		}
		return record;
	}
    public AssociationRecord getAssociationRecord(Association association) {
    	AssociationRecord record = new AssociationRecord(association.getId(), association.getQName(), association.getSource(), association.getTarget());    	
    	Entity sourceEntity = entityService.getEntity(record.getSource());
    	Entity targetEntity = entityService.getEntity(record.getTarget());		
    	record.setName(targetEntity.getName());
    	record.setTargetName(targetEntity.getQName());    	
    	ModelRelation relation = dictionaryService.getModelRelation(sourceEntity.getQName(), association.getQName());
    	record.setView(relation.getView());    	
    	for(ModelField field : relation.getFields().stream().filter(f-> !f.isHidden()).collect(Collectors.toList())) {
			Property property = association.getProperty(field.getQName());
			if(property == null) record.getProperties().add(new PropertyRecord(field.getQName(), "", field.getLabel(), null));
			else record.getProperties().add(new PropertyRecord(property.getQName(), "", field.getLabel(), property.getValue()));
		}			
    	List<ModelField> targetFields = dictionaryService.getModelFields(targetEntity.getQName());
    	for(ModelField field : targetFields.stream().filter(f-> !f.isHidden()).collect(Collectors.toList())) {
    		Object prop = targetEntity.getPropertyValue(field.getQName());
    		if(prop != null) record.getAttributes().add(new PropertyRecord(field.getQName(), "", field.getLabel(), prop));
    	}
    	return record;
    }
	protected PropertyRecord getProperty(EntityRecord record, ModelField field) {
    	if(field.getQName() != null && field.getQName().getNamespace() != null && field.getQName().getLocalName() != null) {
    		for(PropertyRecord prop : record.getProperties()) {
    			if(prop.getQName() != null && prop.getQName().getNamespace() != null && prop.getQName().getLocalName() != null &&
    					prop.getQName().getNamespace().equals(field.getQName().getNamespace()) && prop.getQName().getLocalName().equals(field.getQName().getLocalName())) {
    				if(field.getFormat() != null && field.getFormat().equals(ModelField.FORMAT_PASSWORD))
    					prop.setValue(passwordEncoder.encode((String)prop.getValue()));
    				return prop;
    			}
    		}
    	}
    	return null;
    }
	protected PropertyRecord getProperty(AssociationRecord record, QName qname) {
    	if(qname != null && qname.getNamespace() != null && qname.getLocalName() != null) {
    		for(PropertyRecord prop : record.getProperties()) {
    			if(prop.getQName() != null && prop.getQName().getNamespace() != null && prop.getQName().getLocalName() != null &&
    					prop.getQName().getNamespace().equals(qname.getNamespace()) && prop.getQName().getLocalName().equals(qname.getLocalName())) 
    				return prop;
    		}
    	}
    	return null;
    }
	protected AssociationRecord getAssociation(EntityRecord record, QName qname, Long source, Long target) {
    	if(qname != null && qname.getNamespace() != null && qname.getLocalName() != null) {
    		List<AssociationRecord> records = record.getAssociations().get(qname);
    		if(records != null) {
	    		for(AssociationRecord r : records) {
	    			if(r.getSource().equals(source) && r.getTarget().equals(target))
	    				return r;
	    		}
    		}
    	}
    	return null;
    }
    public List<AssociationRecord> getAssociations(EntityRecord record, QName qname) {
    	return record.getAssociations().get(qname);
    }
}
