package org.archivemanager.services.crawling;

import java.util.List;

import org.archivemanager.data.Sort;
import org.archivemanager.models.system.QName;
import org.archivemanager.services.entity.InvalidEntityException;
import org.archivemanager.services.search.SearchResponse;


public interface CrawlingService {

	Seed getSeed(String url);
	Document getDocument(String url);
	Document getDocument(long id) throws Exception;
	Crawler getCrawler(long id);
	Crawler getCrawler(String url);
	Crawler getPrimaryCrawler(Seed seed);
	void copyCrawlers(Seed seed1, Seed seed2);
	//boolean isSecure(Crawler crawler);
	
	Search saveSearch(Long crawler, String query);
	
	//void addDocumentToCrawler(Crawler crawler, Document document);
	void addSeedToCrawler(Crawler crawler, Seed seed);
	//void removeSeedFromCrawler(Crawler crawler, Seed seed);
	
	SearchResponse getSeeds(long crawler, int status);
	List<Seed> getRunningSeeds(long crawler);
	List<Crawler> getCrawlersByCategory(String category);
	List<Crawler> getCrawlersByDomain(String domain);
	List<Crawler> getCrawlers(Seed seed);
	DocumentResultSet getDocuments(Crawler crawler, String queryString, int page, int maxResults, Sort... sorts);
	List<Document> getNewDocuments(long crawler);
	List<Document> getUnloadedDocuments();
	List<Search> getSearches(long crawler);
			
	Crawler saveCrawler(Crawler crawler);
	Seed saveSeed(Seed seed);
	Document saveDocument(Document document);
	List<String> getDomains();
	
	long getSeedDelay(String frequency);
	//long calculateNextCrawl(Crawler crawler);
	List<String> getBlacklist();
	
	CrawlProcessor getCrawlProcessor(Crawler crawler);
	List<CrawlProcessor> getProcessorsByCategory(String category, boolean filtered);
	void setCrawlProcessors(List<CrawlProcessor> processors);
	
	CrawlingEngine getEngine(String protocol);
	
	//byte[] load(String url, boolean shouldProxy) throws CrawlingException;
	//int count(EntityQuery query);
	void remove(QName qname, long id) throws InvalidEntityException;
	
	CrawlingUser getUser();
	void saveUser(CrawlingUser user);
}
