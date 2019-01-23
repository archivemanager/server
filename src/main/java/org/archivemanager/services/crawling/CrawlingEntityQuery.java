package org.archivemanager.services.crawling;


public class CrawlingEntityQuery {

	
	public CrawlingEntityQuery(String domain, Crawler crawler, String queryString, int page) {
		/*
		setEntityQnames(new QName[] {CrawlingModel.DOCUMENT,RealEstateModel.PROPERTY});
		setType(EntityQuery.TYPE_LUCENE_TEXT);
		setDefaultOperator("OR");
		getClauses().add(new Clause(Clause.OPERATOR_AND, new Property(CrawlingModel.LOADED, "true")));
		addSort(new Sort(Sort.LONG, CrawlingModel.TIMESTAMP.toString(), true));
		if(page > 0) {
			setStartRow((10 * page) - 10);
			setEndRow(10 * page); 
		}
		if(queryString != null) {
			if(queryString.equals("deleted documents")) {
				setDeleted(true);
			} else {
				getFields().add(CrawlingModel.CONTENT.toString());
				setQueryString(queryString);
			}
		}
		if(crawler != null) {
			getProperties().add(new Property(CrawlingModel.CRAWLERS, String.valueOf(crawler.getId())));
		} else if(domain != null) {
			getProperties().add(new Property(CrawlingModel.DOMAIN, domain));	
		}
		*/
	}
}
