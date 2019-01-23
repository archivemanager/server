package org.archivemanager.models;

import org.archivemanager.models.system.QName;

public class DictionaryModel {
	public static final String OPENAPPS_SYSTEM_NAMESPACE = "openapps_org_dictionary_1_0";
	
	public static final QName DICTIONARIES = new QName(OPENAPPS_SYSTEM_NAMESPACE, "dictionaries");
	public static final QName DICTIONARY = new QName(OPENAPPS_SYSTEM_NAMESPACE, "dictionary");
	public static final QName CATEGORY = new QName(OPENAPPS_SYSTEM_NAMESPACE, "category");
	public static final QName MODELS = new QName(OPENAPPS_SYSTEM_NAMESPACE, "models");
	public static final QName MODEL = new QName(OPENAPPS_SYSTEM_NAMESPACE, "model");
	public static final QName MODEL_FIELDS = new QName(OPENAPPS_SYSTEM_NAMESPACE, "fields");
	public static final QName MODEL_FIELD = new QName(OPENAPPS_SYSTEM_NAMESPACE, "field");
	public static final QName MODEL_RELATIONS = new QName(OPENAPPS_SYSTEM_NAMESPACE, "relations");
	public static final QName MODEL_RELATION = new QName(OPENAPPS_SYSTEM_NAMESPACE, "relation");
	public static final QName MODEL_FIELD_VALUES = new QName(OPENAPPS_SYSTEM_NAMESPACE, "values");
	public static final QName MODEL_FIELD_VALUE = new QName(OPENAPPS_SYSTEM_NAMESPACE, "value");
	public static final QName MODEL_ASPECTS = new QName(OPENAPPS_SYSTEM_NAMESPACE, "aspects");
	public static final QName MODEL_ASPECT = new QName(OPENAPPS_SYSTEM_NAMESPACE, "aspect");
	public static final QName PARENT = new QName(OPENAPPS_SYSTEM_NAMESPACE, "parent");
	
	public static final QName QUALIFIED_NAME = new QName(OPENAPPS_SYSTEM_NAMESPACE, "qualified_name");
	public static final QName INHERITANCE = new QName(OPENAPPS_SYSTEM_NAMESPACE, "inheritance");
	public static final QName SHARED = new QName(OPENAPPS_SYSTEM_NAMESPACE, "shared");
	public static final QName LABEL = new QName(OPENAPPS_SYSTEM_NAMESPACE, "label");
	public static final QName HEADER = new QName(OPENAPPS_SYSTEM_NAMESPACE, "header");
	public static final QName STORED = new QName(OPENAPPS_SYSTEM_NAMESPACE, "stored");
	public static final QName SEARCHABLE = new QName(OPENAPPS_SYSTEM_NAMESPACE, "searchable");
	public static final QName FREETEXT = new QName(OPENAPPS_SYSTEM_NAMESPACE, "freetext");
	public static final QName AUDITABLE = new QName(OPENAPPS_SYSTEM_NAMESPACE, "auditable");
	
	public static final QName VALUE = new QName(OPENAPPS_SYSTEM_NAMESPACE, "value");
	public static final QName TYPE = new QName(OPENAPPS_SYSTEM_NAMESPACE, "type");
	public static final QName FORMAT = new QName(OPENAPPS_SYSTEM_NAMESPACE, "format");
	public static final QName INDEX = new QName(OPENAPPS_SYSTEM_NAMESPACE, "index");
	public static final QName MANDATORY = new QName(OPENAPPS_SYSTEM_NAMESPACE, "manadatory");
	public static final QName UNIQUE = new QName(OPENAPPS_SYSTEM_NAMESPACE, "unique");
	public static final QName HIDDEN = new QName(OPENAPPS_SYSTEM_NAMESPACE, "hidden");
	public static final QName SORTABLE = new QName(OPENAPPS_SYSTEM_NAMESPACE, "sortable");
	public static final QName MINVALUE = new QName(OPENAPPS_SYSTEM_NAMESPACE, "minvalue");
	public static final QName MAXVALUE = new QName(OPENAPPS_SYSTEM_NAMESPACE, "maxvalue");
	public static final QName MINSIZE = new QName(OPENAPPS_SYSTEM_NAMESPACE, "minsize");
	public static final QName MAXSIZE = new QName(OPENAPPS_SYSTEM_NAMESPACE, "maxsize");
	public static final QName ORDER = new QName(OPENAPPS_SYSTEM_NAMESPACE, "order");
	public static final QName SORT = new QName(OPENAPPS_SYSTEM_NAMESPACE, "sort");
	public static final QName DEFAULTVALUE = new QName(OPENAPPS_SYSTEM_NAMESPACE, "defaultvalue");
	
	public static final QName VIEW = new QName(OPENAPPS_SYSTEM_NAMESPACE, "view");
	public static final QName MANY = new QName(OPENAPPS_SYSTEM_NAMESPACE, "many");
	public static final QName CASCADE = new QName(OPENAPPS_SYSTEM_NAMESPACE, "cascade");
}
