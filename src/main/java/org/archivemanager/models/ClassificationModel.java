package org.archivemanager.models;

import org.archivemanager.models.system.QName;



public class ClassificationModel {
	public static final String OPENAPPS_CLASSIFICATION_NAMESPACE = "openapps_org_classification_1_0";
	
	public static final QName NAMED_ENTITIES = new QName(OPENAPPS_CLASSIFICATION_NAMESPACE, "named_entities");
	public static final QName NAMED_ENTITY = new QName(OPENAPPS_CLASSIFICATION_NAMESPACE, "named_entity");
	public static final QName PERSON = new QName(OPENAPPS_CLASSIFICATION_NAMESPACE, "person");
	public static final QName PEOPLE = new QName(OPENAPPS_CLASSIFICATION_NAMESPACE, "people");
	public static final QName PEOPLE_LABELS = new QName(OPENAPPS_CLASSIFICATION_NAMESPACE, "people_labels");
	
	public static final QName CORPORATION = new QName(OPENAPPS_CLASSIFICATION_NAMESPACE, "corporation");
	public static final QName CORPORATIONS_LABELS = new QName(OPENAPPS_CLASSIFICATION_NAMESPACE, "corporations_labels");
	public static final QName CORPORATIONS = new QName(OPENAPPS_CLASSIFICATION_NAMESPACE, "corporations");
	public static final QName FAMILY = new QName(OPENAPPS_CLASSIFICATION_NAMESPACE, "family");
	public static final QName FAMILIES = new QName(OPENAPPS_CLASSIFICATION_NAMESPACE, "families");
	public static final QName ACADEMIC_COURSE = new QName(OPENAPPS_CLASSIFICATION_NAMESPACE, "academic_course");
	
	public static final QName DATES = new QName(OPENAPPS_CLASSIFICATION_NAMESPACE, "dates");
	public static final QName SOURCE = new QName(OPENAPPS_CLASSIFICATION_NAMESPACE, "source");
	public static final QName TYPE = new QName(OPENAPPS_CLASSIFICATION_NAMESPACE, "type");
	public static final QName RULE = new QName(OPENAPPS_CLASSIFICATION_NAMESPACE, "rule");
	public static final QName NOTE = new QName(OPENAPPS_CLASSIFICATION_NAMESPACE, "note");
	public static final QName NOTE_TYPE = new QName(OPENAPPS_CLASSIFICATION_NAMESPACE, "note_type");
	public static final QName FUNCTION = new QName(OPENAPPS_CLASSIFICATION_NAMESPACE, "function");
	
	public static final QName USERS = new QName(OPENAPPS_CLASSIFICATION_NAMESPACE, "users");
	public static final QName SUBJECTS = new QName(OPENAPPS_CLASSIFICATION_NAMESPACE, "subjects");
	public static final QName SUBJECTS_LABELS = new QName(OPENAPPS_CLASSIFICATION_NAMESPACE, "subjects_labels");
	public static final QName SUBJECT = new QName(OPENAPPS_CLASSIFICATION_NAMESPACE, "subject");	
	
	public static final QName NAMED_ENTITY_SEARCH_VALUES = new QName(OPENAPPS_CLASSIFICATION_NAMESPACE, "search_values");
	
	public static final QName ENTRY = new QName(OPENAPPS_CLASSIFICATION_NAMESPACE, "entry");
	public static final QName DATE = new QName(OPENAPPS_CLASSIFICATION_NAMESPACE, "date");
	public static final QName ENTRIES = new QName(OPENAPPS_CLASSIFICATION_NAMESPACE, "entries");
	public static final QName ITEMS= new QName(OPENAPPS_CLASSIFICATION_NAMESPACE, "items");
	public static final QName COLLECTION_NAME = new QName(OPENAPPS_CLASSIFICATION_NAMESPACE, "collection_name");
	public static final QName LOCATION = new QName(OPENAPPS_CLASSIFICATION_NAMESPACE, "location");
	
}
