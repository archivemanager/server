package org.archivemanager.models.binders;

import org.archivemanager.models.DictionaryModel;
import org.archivemanager.models.SystemModel;
import org.archivemanager.models.dictionary.DataDictionary;
import org.archivemanager.models.dictionary.Model;
import org.archivemanager.models.dictionary.ModelField;
import org.archivemanager.models.dictionary.ModelFieldValue;
import org.archivemanager.models.dictionary.ModelObject;
import org.archivemanager.models.dictionary.ModelRelation;
import org.archivemanager.models.system.QName;
import org.archivemanager.services.entity.Association;
import org.archivemanager.services.entity.Entity;
import org.archivemanager.services.entity.EntityService;
import org.archivemanager.web.model.EntityRecord;
import org.archivemanager.web.model.PropertyRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


@Component
public class DataDictionaryModelEntityBinder extends ModelEntityBinder {
	@Autowired EntityService entityService;
	
	
	public ModelObject getModelObject(Entity entity) {
		ModelObject object = null;
		if(entity.getQName().equals(DictionaryModel.DICTIONARY)) {
			DataDictionary obj = getDataDictionary(entity);
			object = obj;
		} else if(entity.getQName().equals(DictionaryModel.MODEL)) {
			Model obj = getModel(entity);
			obj.setId(entity.getId());
			object = obj;
		} else if(entity.getQName().equals(DictionaryModel.MODEL_FIELD)) {
			ModelField obj = getModelField(entity);
			obj.setId(entity.getId());
			object = obj;
		} else if(entity.getQName().equals(DictionaryModel.MODEL_RELATION)) {
			ModelRelation obj = getModelRelation(entity);
			obj.setId(entity.getId());
			object = obj;
		} else if(entity.getQName().equals(DictionaryModel.MODEL_FIELD_VALUE)) {
			ModelFieldValue obj = getModelFieldValue(entity);
			obj.setId(entity.getId());
			object = obj;
		}
		return object;
	}
	public DataDictionary getDataDictionary(Entity entity) {
		DataDictionary dictionary = new DataDictionary(entity.getId(),entity.getQName(), entity.getName(), entity.getPropertyValueString(SystemModel.DESCRIPTION));
		dictionary.setShared(entity.getPropertyValueBoolean(DictionaryModel.SHARED));
		dictionary.setInheritance(entity.getPropertyValueString(DictionaryModel.INHERITANCE));
		for(Association modelAssoc : entity.getSourceAssociations(DictionaryModel.MODELS)) {
			Entity fieldEntity = entityService.getEntity(modelAssoc.getTarget());
			dictionary.getModels().add(getModel(fieldEntity));
		}
		return dictionary;
	}
	public Model getModel(Entity entity) {
		Model model = new Model(entity.getId(),entity.getQName(), entity.getPropertyValueString(SystemModel.DESCRIPTION));
		model.setAuditable(entity.getPropertyValueBoolean(DictionaryModel.AUDITABLE));
		model.setFreetext(entity.getPropertyValueBoolean(DictionaryModel.FREETEXT));
		model.setHeader(entity.getPropertyValueString(DictionaryModel.HEADER));
		model.setLabel(entity.getPropertyValueString(DictionaryModel.LABEL));
		//model.setParentName(parentName);
		for(Association fieldAssoc : entity.getSourceAssociations(DictionaryModel.MODEL_FIELDS)) {
			Entity fieldEntity = entityService.getEntity(fieldAssoc.getTarget());
			model.getFields().add(getModelField(fieldEntity));
		}
		for(Association relationAssoc : entity.getSourceAssociations(DictionaryModel.MODEL_RELATIONS)) {
			Entity relationEntity = entityService.getEntity(relationAssoc.getTarget());
			model.getRelations().add(getModelRelation(relationEntity));
		}
		return model;
	}
	public ModelField getModelField(Entity entity) {
		ModelField field = new ModelField(entity.getId(),entity.getQName(), entity.getName(), entity.getPropertyValueString(SystemModel.DESCRIPTION));
		field.setDefaultValue(entity.getPropertyValueString(DictionaryModel.DEFAULTVALUE));
		field.setFormat(entity.getPropertyValueString(DictionaryModel.FORMAT));
		field.setHidden(entity.getPropertyValueBoolean(DictionaryModel.HIDDEN));
		field.setIndex(entity.getPropertyValueInteger(DictionaryModel.INDEX));
		field.setLabel(entity.getPropertyValueString(DictionaryModel.LABEL));
		field.setMandatory(entity.getPropertyValueBoolean(DictionaryModel.MANDATORY));
		field.setMaxSize(entity.getPropertyValueInteger(DictionaryModel.MAXSIZE));
		field.setMinSize(entity.getPropertyValueInteger(DictionaryModel.MINSIZE));
		field.setMaxValue(entity.getPropertyValueInteger(DictionaryModel.MAXVALUE));
		field.setMinValue(entity.getPropertyValueInteger(DictionaryModel.MINVALUE));
		field.setOrder(entity.getPropertyValueInteger(DictionaryModel.ORDER));
		field.setSearchable(entity.getPropertyValueBoolean(DictionaryModel.SEARCHABLE));
		field.setSort(entity.getPropertyValueInteger(DictionaryModel.SORT));
		field.setSortable(entity.getPropertyValueBoolean(DictionaryModel.SORTABLE));
		return field;
	}
	public ModelRelation getModelRelation(Entity entity) {
		ModelRelation relation = new ModelRelation(entity.getId(),entity.getQName());
		relation.setStartName(new QName(entity.getPropertyValueString(SystemModel.SOURCE)));
		relation.setEndName(new QName(entity.getPropertyValueString(SystemModel.TARGET)));
		for(Association fieldAssoc : entity.getSourceAssociations(DictionaryModel.MODEL_FIELDS)) {
			Entity fieldEntity = entityService.getEntity(fieldAssoc.getTarget());
			relation.getFields().add(getModelField(fieldEntity));
		}
		return relation;
	}
	public ModelFieldValue getModelFieldValue(Entity entity) {
		ModelFieldValue value = new ModelFieldValue(entity.getId(), entity.getName(), entity.getPropertyValueString(SystemModel.DESCRIPTION));
		value.setCategory(entity.getPropertyValueString(DictionaryModel.CATEGORY));
		value.setLabel(entity.getPropertyValueString(DictionaryModel.LABEL));
		value.setValue(entity.getPropertyValueString(DictionaryModel.VALUE));
		return value;
	}
	
	public EntityRecord getEntityRecord(ModelObject entity) {
		EntityRecord record = new EntityRecord(entity.getId(), entity.getQName(), "");
		record.getProperties().add(new PropertyRecord(SystemModel.QNAME, "mediumtext", "QName", entity.getQName()));
		record.getProperties().add(new PropertyRecord(SystemModel.LABEL, "mediumtext", "Label", entity.getLabel()));
		record.getProperties().add(new PropertyRecord(SystemModel.DESCRIPTION, "mediumtext", "Description", entity.getDescription()));
		if(entity instanceof DataDictionary) {
			DataDictionary dictionary = (DataDictionary)entity;			
			record.getProperties().add(new PropertyRecord(DictionaryModel.INHERITANCE, "mediumtext", "Inheritance", dictionary.getInheritance()));
			record.getProperties().add(new PropertyRecord(DictionaryModel.SHARED, "boolean", "Shared", dictionary.isShared()));
		} else if(entity instanceof Model) {
			Model model = (Model)entity;
			if(model.getParent() != null) record.getProperties().add(new PropertyRecord(DictionaryModel.PARENT, "mediumtext", "Parent", model.getParent().getQName()));	
			record.getProperties().add(new PropertyRecord(DictionaryModel.LABEL, "mediumtext", "Label", model.getLabel()));			
			record.getProperties().add(new PropertyRecord(DictionaryModel.HEADER, "mediumtext", "Header", model.getHeader()));
			record.getProperties().add(new PropertyRecord(DictionaryModel.STORED, "boolean", "Stored", model.isStored()));
			record.getProperties().add(new PropertyRecord(DictionaryModel.SEARCHABLE, "boolean", "Searchable", model.isSearchable()));
			record.getProperties().add(new PropertyRecord(DictionaryModel.FREETEXT, "boolean", "FreeText", model.isFreetext()));
			record.getProperties().add(new PropertyRecord(DictionaryModel.AUDITABLE, "boolean", "Auditable", model.isAuditable()));
		} else if(entity instanceof ModelField) {
			ModelField field = (ModelField)entity;
			record.getProperties().add(new PropertyRecord(DictionaryModel.LABEL, "mediumtext", "Label", field.getLabel()));
			record.getProperties().add(new PropertyRecord(DictionaryModel.VALUE, "mediumtext", "Value", field.getValue()));
			record.getProperties().add(new PropertyRecord(DictionaryModel.TYPE, "smalltext", "Type", field.getType()));
			record.getProperties().add(new PropertyRecord(DictionaryModel.FORMAT, "mediumtext", "Format", field.getFormat()));
			record.getProperties().add(new PropertyRecord(DictionaryModel.INDEX, "integer", "Index", field.getIndex()));
			record.getProperties().add(new PropertyRecord(DictionaryModel.MANDATORY, "boolean", "Mandatory", field.isMandatory()));
			record.getProperties().add(new PropertyRecord(DictionaryModel.UNIQUE, "boolean", "Unique", field.isUnique()));
			record.getProperties().add(new PropertyRecord(DictionaryModel.HIDDEN, "boolean", "Hidden", field.isHidden()));
			record.getProperties().add(new PropertyRecord(DictionaryModel.SORTABLE, "boolean", "Sortable", field.isSortable()));
			record.getProperties().add(new PropertyRecord(DictionaryModel.SEARCHABLE, "boolean", "Searchable", field.isSearchable()));
			record.getProperties().add(new PropertyRecord(DictionaryModel.MINVALUE, "integer", "Minimum Value", field.getMinValue()));
			record.getProperties().add(new PropertyRecord(DictionaryModel.MAXVALUE, "integer", "Maximum Value", field.getMaxValue()));
			record.getProperties().add(new PropertyRecord(DictionaryModel.MINSIZE, "integer", "Minimum Size", field.getMinSize()));
			record.getProperties().add(new PropertyRecord(DictionaryModel.MAXSIZE, "integer", "Maximum Size", field.getMaxSize()));
			record.getProperties().add(new PropertyRecord(DictionaryModel.ORDER, "integer", "Order", field.getOrder()));
			record.getProperties().add(new PropertyRecord(DictionaryModel.SORT, "integer", "Sort", field.getSort()));
			record.getProperties().add(new PropertyRecord(DictionaryModel.DEFAULTVALUE, "mediumtext", "Default Value", field.getDefaultValue()));
		} else if(entity instanceof ModelRelation) {
			ModelRelation relation = (ModelRelation)entity;
			record.getProperties().add(new PropertyRecord(SystemModel.TARGET, "mediumtext", "Target QName",relation.getEndName()));
			record.getProperties().add(new PropertyRecord(DictionaryModel.LABEL, "mediumtext", "Label", relation.getLabel()));
			record.getProperties().add(new PropertyRecord(DictionaryModel.VIEW, "mediumtext", "View", relation.getView()));
			record.getProperties().add(new PropertyRecord(DictionaryModel.MANY, "boolean", "Many", relation.isMany()));
			record.getProperties().add(new PropertyRecord(DictionaryModel.CASCADE, "boolean", "Cascade", relation.isCascade()));
			record.getProperties().add(new PropertyRecord(DictionaryModel.HIDDEN, "boolean", "Hidden", relation.isHidden()));
			
		}		
		return record;
	}
}
