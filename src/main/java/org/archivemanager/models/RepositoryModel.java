package org.archivemanager.models;

import org.archivemanager.models.system.QName;



public class RepositoryModel {

	public static final String NAMESPACE_ARCHIVE = "openapps_org_repository_1_0";
	
	public static final QName REPOSITORY= new QName(NAMESPACE_ARCHIVE, "repository");
	public static final QName REPOSITORY_OBJECT = new QName(NAMESPACE_ARCHIVE, "repository_object");
	public static final QName TYPE= new QName(NAMESPACE_ARCHIVE, "type");
	
	public static final QName COLLECTION = new QName(NAMESPACE_ARCHIVE, "collection");
	public static final QName COLLECTION_ID = new QName(NAMESPACE_ARCHIVE, "collection_id");
	public static final QName COLLECTION_URL = new QName(NAMESPACE_ARCHIVE, "collection_url");
	public static final QName COLLECTION_NAME = new QName(NAMESPACE_ARCHIVE, "collection_name");
	public static final QName COLLECTION_CODE = new QName(NAMESPACE_ARCHIVE, "collection_code");
	public static final QName COLLECTIONS = new QName(NAMESPACE_ARCHIVE, "collections");
	public static final QName IS_PUBLIC = new QName(NAMESPACE_ARCHIVE, "isPublic");
	public static final QName INTERNAL = new QName(NAMESPACE_ARCHIVE, "internal");
	public static final QName SEARCHABLE = new QName(NAMESPACE_ARCHIVE, "searchable");
	public static final QName SIZE = new QName(NAMESPACE_ARCHIVE, "size");
	public static final QName RESTRICTIONS = new QName(NAMESPACE_ARCHIVE, "restrictions");
	public static final QName COMMENT= new QName(NAMESPACE_ARCHIVE, "comment");
	public static final QName IDENTIFIER= new QName(NAMESPACE_ARCHIVE, "identifier");
	public static final QName BULK_BEGIN= new QName(NAMESPACE_ARCHIVE, "bulk_begin");
	public static final QName BULK_END= new QName(NAMESPACE_ARCHIVE, "bulk_end");
	public static final QName EXTENT_UNITS= new QName(NAMESPACE_ARCHIVE, "extent_units");
	
	public static final QName CATEGORIES= new QName(NAMESPACE_ARCHIVE, "categories");
	public static final QName CATEGORY= new QName(NAMESPACE_ARCHIVE, "category");
	public static final QName CATEGORY_LEVEL = new QName(NAMESPACE_ARCHIVE, "level");
	
	public static final QName ITEM= new QName(NAMESPACE_ARCHIVE, "item");
	public static final QName ITEMS= new QName(NAMESPACE_ARCHIVE, "items");
	public static final QName VIDEO= new QName(NAMESPACE_ARCHIVE, "video");
	
	public static final QName DATE_EXPRESSION = new QName(NAMESPACE_ARCHIVE, "date_expression");
	public static final QName CONTAINER = new QName(NAMESPACE_ARCHIVE, "container");
	public static final QName CONTAINERS = new QName(NAMESPACE_ARCHIVE, "containers");
	
	public static final QName ACCESSIONS= new QName(NAMESPACE_ARCHIVE, "accessions");
	public static final QName ACCESSION= new QName(NAMESPACE_ARCHIVE, "accession");
	public static final QName ACCESSION_NUMBER= new QName(NAMESPACE_ARCHIVE, "identifier");
	public static final QName DATE = new QName(NAMESPACE_ARCHIVE, "date");
	public static final QName ACCESSION_COST = new QName(NAMESPACE_ARCHIVE, "cost");
	public static final QName ACCESSION_GENERAL_NOTE = new QName(NAMESPACE_ARCHIVE, "general_note");
	
	public static final QName LOCATIONS= new QName(NAMESPACE_ARCHIVE, "locations");
	public static final QName LOCATION= new QName(NAMESPACE_ARCHIVE, "location");
	public static final QName BUILDING= new QName(NAMESPACE_ARCHIVE, "building");
	public static final QName FLOOR= new QName(NAMESPACE_ARCHIVE, "floor");
	public static final QName AISLE= new QName(NAMESPACE_ARCHIVE, "aisle");
	public static final QName BAY= new QName(NAMESPACE_ARCHIVE, "bay");
	public static final QName CODE= new QName(NAMESPACE_ARCHIVE, "code");
	
	public static final QName EXTENT= new QName(NAMESPACE_ARCHIVE, "extent");
	public static final QName EXTENTS= new QName(NAMESPACE_ARCHIVE, "extents");
	public static final QName EXTENT_VALUE = new QName(NAMESPACE_ARCHIVE, "value");
	public static final QName EXTENT_TYPE = new QName(NAMESPACE_ARCHIVE, "type");
	public static final QName EXTENT_NUMBER = new QName(NAMESPACE_ARCHIVE, "extent_number");
	public static final QName PAGEBOX_QUANTITY = new QName(NAMESPACE_ARCHIVE, "pagebox_quantity");
	public static final QName CONTENT_TYPE = new QName(NAMESPACE_ARCHIVE, "content_type");
	
	public static final QName BIOGRAPHICAL_NOTE = new QName(NAMESPACE_ARCHIVE, "bio_note");
	public static final QName SCOPE_NOTE = new QName(NAMESPACE_ARCHIVE, "scope_note");
	public static final QName ABSTRACT_NOTE = new QName(NAMESPACE_ARCHIVE, "abstract_note");
	public static final QName BEGIN_DATE= new QName(NAMESPACE_ARCHIVE, "begin");
	public static final QName END_DATE= new QName(NAMESPACE_ARCHIVE, "end");
	
	public static final QName COLLECTION_IDENTIFIER= new QName(NAMESPACE_ARCHIVE, "identifier");
	public static final QName ACCESSION_DATE= new QName(NAMESPACE_ARCHIVE, "accession_date");
	public static final QName DESCRIPTION= new QName(NAMESPACE_ARCHIVE, "description");
	public static final QName SUMMARY= new QName(NAMESPACE_ARCHIVE, "summary");
	
	public static final QName URL= new QName(NAMESPACE_ARCHIVE, "url");
	public static final QName FUNCTION = new QName(NAMESPACE_ARCHIVE, "function");
	
	public static final QName LANGUAGE = new QName(NAMESPACE_ARCHIVE, "language");
	public static final QName ORDER = new QName(NAMESPACE_ARCHIVE, "order");
	
	public static final QName GENRE = new QName(NAMESPACE_ARCHIVE, "genre");
	public static final QName FORM = new QName(NAMESPACE_ARCHIVE, "form");
	public static final QName MEDIUM = new QName(NAMESPACE_ARCHIVE, "medium");
}
