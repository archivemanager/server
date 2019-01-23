package org.archivemanager.models;

import org.archivemanager.models.system.QName;


public class SearchModel {
	public static final String OPENSEARCH_SYSTEM_NAMESPACE = "openapps_org_search_1_0";
	
	public static final QName DICTIONARY = new QName(OPENSEARCH_SYSTEM_NAMESPACE, "dictionary");
	public static final QName DEFINITION = new QName(OPENSEARCH_SYSTEM_NAMESPACE, "definition");
	public static final QName ATTRIBUTE = new QName(OPENSEARCH_SYSTEM_NAMESPACE, "attribute");
	
	public static final QName DEFINITIONS = new QName(OPENSEARCH_SYSTEM_NAMESPACE, "definitions");
	public static final QName ATTRIBUTES = new QName(OPENSEARCH_SYSTEM_NAMESPACE, "attributes");
	public static final QName ATTRIBUTE_VALUES = new QName(OPENSEARCH_SYSTEM_NAMESPACE, "attribute_values");
	public static final QName DICTIONARIES = new QName(OPENSEARCH_SYSTEM_NAMESPACE, "dictionaries");
	public static final QName SEARCHER = new QName(OPENSEARCH_SYSTEM_NAMESPACE, "searcher");
	
	public static final QName QUERY = new QName(OPENSEARCH_SYSTEM_NAMESPACE, "query");
	public static final QName TYPE = new QName(OPENSEARCH_SYSTEM_NAMESPACE, "type");
	public static final QName NAME = new QName(OPENSEARCH_SYSTEM_NAMESPACE, "name");
	public static final QName VALUE = new QName(OPENSEARCH_SYSTEM_NAMESPACE, "value");
	
	public static final QName SEARCH_TERMS = new QName(OPENSEARCH_SYSTEM_NAMESPACE, "search_terms");
	public static final QName SEARCH_TERM = new QName(OPENSEARCH_SYSTEM_NAMESPACE, "search_term");
	
	public static final QName EVENT = new QName(OPENSEARCH_SYSTEM_NAMESPACE, "event");
	public static final QName QUERY_EVENT = new QName(OPENSEARCH_SYSTEM_NAMESPACE, "query_event");
	public static final QName VIEW_EVENT = new QName(OPENSEARCH_SYSTEM_NAMESPACE, "view_event");
	
	public static final QName EXPLANATION = new QName(OPENSEARCH_SYSTEM_NAMESPACE, "explanation");
	
}
