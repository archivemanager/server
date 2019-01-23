package org.archivemanager.services.crawling;


public interface Crawlable {

	Long getId();
	void setUrl(String url);
	String getUrl();
	String getProtocol();
	String getDomain();
	String getPath();
	String getQuery();
	String getName();
	
}
