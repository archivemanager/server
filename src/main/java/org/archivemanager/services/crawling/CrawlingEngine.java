package org.archivemanager.services.crawling;

import java.util.List;


public interface CrawlingEngine {

	void setCrawlingDelay(long crawlingDelay);
	void setStatus(int status);

	CrawlingJob crawl(Crawler crawler);
	byte[] load(Crawler crawler, Document document);
	List<CrawlingJob> getRunningJobs();
	
}
