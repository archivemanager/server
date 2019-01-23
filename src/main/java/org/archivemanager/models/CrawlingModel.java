package org.archivemanager.models;

import org.archivemanager.models.system.QName;


public class CrawlingModel {
	public static final String CRAWLING_MODEL_1_0_URI = "openapps_org_crawling_1_0";
	
	public static final QName TYPE = new QName(CRAWLING_MODEL_1_0_URI, "type");
	public static final QName URL = new QName(CRAWLING_MODEL_1_0_URI, "url");
	public static final QName CRAWLER = new QName(CRAWLING_MODEL_1_0_URI, "crawler");
	public static final QName CRAWLERS = new QName(CRAWLING_MODEL_1_0_URI, "crawlers");
	public static final QName SEED = new QName(CRAWLING_MODEL_1_0_URI, "seed");
	public static final QName PAGE = new QName(CRAWLING_MODEL_1_0_URI, "page");
	public static final QName SEEDS = new QName(CRAWLING_MODEL_1_0_URI, "seeds");
	public static final QName PROCESSOR = new QName(CRAWLING_MODEL_1_0_URI, "processor");
	public static final QName PROCESSORS = new QName(CRAWLING_MODEL_1_0_URI, "processors");
	public static final QName SEEDED = new QName(CRAWLING_MODEL_1_0_URI, "seeded");
	public static final QName EXTRACTED = new QName(CRAWLING_MODEL_1_0_URI, "extracted");
	public static final QName CONFIGURATION = new QName(CRAWLING_MODEL_1_0_URI, "configuration");
	public static final QName CONFIGURATION_ID = new QName(CRAWLING_MODEL_1_0_URI, "configuration_id");
	
	public static final QName CATEGORY = new QName(CRAWLING_MODEL_1_0_URI, "category");
	public static final QName CATEGORIES = new QName(CRAWLING_MODEL_1_0_URI, "categories");
	
	public static final QName STATUS = new QName(CRAWLING_MODEL_1_0_URI, "status");
	public static final QName LAST_CRAWL = new QName(CRAWLING_MODEL_1_0_URI, "last_crawl");
	public static final QName NEXT_CRAWL = new QName(CRAWLING_MODEL_1_0_URI, "next_crawl");
	public static final QName FREQUENCY = new QName(CRAWLING_MODEL_1_0_URI, "frequency");
	public static final QName TIME = new QName(CRAWLING_MODEL_1_0_URI, "time");
	
	public static final QName FILTER_TERMS = new QName(CRAWLING_MODEL_1_0_URI, "filter_terms");
	public static final QName SEARCH_TERMS = new QName(CRAWLING_MODEL_1_0_URI, "search_terms");
	public static final QName DAY_OF_WEEK = new QName(CRAWLING_MODEL_1_0_URI, "day_of_week");
	public static final QName TIME_OF_DAY_1 = new QName(CRAWLING_MODEL_1_0_URI, "time_of_day_1");
	public static final QName TIME_OF_DAY_2 = new QName(CRAWLING_MODEL_1_0_URI, "time_of_day_2");
	public static final QName MAX_RESULTS = new QName(CRAWLING_MODEL_1_0_URI, "max_results");
	public static final QName HOPS = new QName(CRAWLING_MODEL_1_0_URI, "hops");
	public static final QName SAME_DOMAIN = new QName(CRAWLING_MODEL_1_0_URI, "same_domain");
	
	public static final QName MESSAGE = new QName(CRAWLING_MODEL_1_0_URI, "message");
	public static final QName ERROR_COUNT = new QName(CRAWLING_MODEL_1_0_URI, "error_count");
	public static final QName LOADED = new QName(CRAWLING_MODEL_1_0_URI, "loaded");
	public static final QName CACHED = new QName(CRAWLING_MODEL_1_0_URI, "cached");
	public static final QName VIEWED = new QName(CRAWLING_MODEL_1_0_URI, "viewed");
	public static final QName FLAGGED = new QName(CRAWLING_MODEL_1_0_URI, "flagged");
	public static final QName NUM_TRIES = new QName(CRAWLING_MODEL_1_0_URI, "num_tries");
	public static final QName ANONYMOUS = new QName(CRAWLING_MODEL_1_0_URI, "anonymous");
	
	public static final QName DOCUMENT = new QName(CRAWLING_MODEL_1_0_URI, "document");
	public static final QName DOCUMENTS = new QName(CRAWLING_MODEL_1_0_URI, "documents");
	public static final QName SUMMARY = new QName(CRAWLING_MODEL_1_0_URI, "summary");
	public static final QName DOMAIN = new QName(CRAWLING_MODEL_1_0_URI, "domain");
	public static final QName JOURNAL = new QName(CRAWLING_MODEL_1_0_URI, "journal");
	public static final QName TIMESTAMP = new QName(CRAWLING_MODEL_1_0_URI, "timestamp");
	public static final QName DISPLAY_DATE = new QName(CRAWLING_MODEL_1_0_URI, "display_date");
	public static final QName CONTENT_TYPE = new QName(CRAWLING_MODEL_1_0_URI, "content_type");
	public static final QName ORIGIN = new QName(CRAWLING_MODEL_1_0_URI, "origin");
	public static final QName ICON = new QName(CRAWLING_MODEL_1_0_URI, "icon");
	public static final QName CONTENT = new QName(CRAWLING_MODEL_1_0_URI, "content");
	public static final QName IMAGE = new QName(CRAWLING_MODEL_1_0_URI, "image");
	public static final QName IMAGE_URL = new QName(CRAWLING_MODEL_1_0_URI, "image_url");
	public static final QName ATTRIBUTE = new QName(CRAWLING_MODEL_1_0_URI, "attribute");
	public static final QName AUTHOR = new QName(CRAWLING_MODEL_1_0_URI, "author");
	public static final QName VIDEO = new QName(CRAWLING_MODEL_1_0_URI, "video");
	
	public static final QName SEARCH = new QName(CRAWLING_MODEL_1_0_URI, "search");
	public static final QName SEARCHES = new QName(CRAWLING_MODEL_1_0_URI, "searches");
	public static final QName ACTIVE = new QName(CRAWLING_MODEL_1_0_URI, "active");
	
	public static final QName SNAPSHOT = new QName(CRAWLING_MODEL_1_0_URI, "snapshot");
	public static final QName VERSION = new QName(CRAWLING_MODEL_1_0_URI, "version");
	
	public static final QName SEED_SELECTOR = new QName(CRAWLING_MODEL_1_0_URI, "seed_selector");
	public static final QName URL_SELECTOR = new QName(CRAWLING_MODEL_1_0_URI, "url_selector");
	public static final QName NAME_SELECTOR = new QName(CRAWLING_MODEL_1_0_URI, "name_selector");
	public static final QName SUMMARY_SELECTOR = new QName(CRAWLING_MODEL_1_0_URI, "summary_selector");
	public static final QName TIMESTAMP_SELECTOR = new QName(CRAWLING_MODEL_1_0_URI, "timestamp_selector");
	public static final QName TIMESTAMP_FORMAT = new QName(CRAWLING_MODEL_1_0_URI, "timestamp_format");
	public static final QName TIMESTAMP_ZONE = new QName(CRAWLING_MODEL_1_0_URI, "timestamp_zone");
	public static final QName AUTHOR_SELECTOR = new QName(CRAWLING_MODEL_1_0_URI, "author_selector");
	public static final QName IMAGE_SELECTOR = new QName(CRAWLING_MODEL_1_0_URI, "image_selector");
	
	public static final QName ENTERTAINMENT = new QName(CRAWLING_MODEL_1_0_URI, "entertainment");
	public static final QName BUSINESS = new QName(CRAWLING_MODEL_1_0_URI, "business");
	public static final QName LIFESTYLE = new QName(CRAWLING_MODEL_1_0_URI, "lifestyle");
	public static final QName POLITICS = new QName(CRAWLING_MODEL_1_0_URI, "politics");
	public static final QName NEWS = new QName(CRAWLING_MODEL_1_0_URI, "news");
	public static final QName RESEARCH = new QName(CRAWLING_MODEL_1_0_URI, "research");
	public static final QName SPORTS = new QName(CRAWLING_MODEL_1_0_URI, "sports");
	public static final QName TECHNOLOGY = new QName(CRAWLING_MODEL_1_0_URI, "technology");
	public static final QName WORLD = new QName(CRAWLING_MODEL_1_0_URI, "world");
	
	public static final QName MANIFEST_ENTRY = new QName(CRAWLING_MODEL_1_0_URI, "manifest_entry");
}
