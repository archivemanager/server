package org.archivemanager.services.crawling;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.archivemanager.data.Sort;
import org.archivemanager.models.CrawlingModel;
import org.archivemanager.models.SystemModel;
import org.archivemanager.models.dictionary.ModelField;
import org.archivemanager.models.system.QName;
import org.archivemanager.services.cache.CacheService;
import org.archivemanager.services.content.ContentService;
import org.archivemanager.services.entity.Association;
import org.archivemanager.services.entity.Entity;
import org.archivemanager.services.entity.EntityService;
import org.archivemanager.services.entity.InvalidEntityException;
import org.archivemanager.services.entity.Property;
import org.archivemanager.services.net.http.HttpComponent;
import org.archivemanager.services.net.http.HttpResponse;
import org.archivemanager.services.net.http.URLConnectionComponent;
import org.archivemanager.services.net.proxy.ProxyService;
import org.archivemanager.services.scheduling.SchedulingService;
import org.archivemanager.services.search.Clause;
import org.archivemanager.services.search.Parameter;
import org.archivemanager.services.search.SearchRequest;
import org.archivemanager.services.search.SearchResponse;
import org.archivemanager.services.search.SearchResult;
import org.archivemanager.services.search.SearchService;
import org.joda.time.DateTime;
import org.springframework.stereotype.Service;

@Service
@SuppressWarnings("unchecked")
public class StandardCrawlingService implements CrawlingService {
	private HttpComponent http = new URLConnectionComponent();
	private EntityService entityService;
	private SchedulingService schedulingService;
	private CacheService cacheService;
	private ContentService contentService;	
	private ProxyService proxyService;
	private SearchService searchService;
	
	//private Proxy PROXY = new HttpProxy(Proxy.Type.SOCKS, new InetSocketAddress("127.0.0.1", 9150));
			
	private List<String> blacklist = new ArrayList<String>();
	private Map<String, CrawlingEngine> engines = new HashMap<String, CrawlingEngine>();
	private List<CrawlProcessor> processors = new ArrayList<CrawlProcessor>();
		
	private long crawlingDelay = 15;
	
	public StandardCrawlingService(){}
		
	public void initialize() {
		jcifs.Config.registerSmbURLHandler();
		for(CrawlProcessor processor : processors) {
			processor.setContentService(contentService);
			processor.setCrawlingService(this);
			processor.initialize();
		}
		engines.put("smb", new JCIFSCrawlingEngine(entityService, this));
	}
	
	public byte[] load(String url, boolean shouldProxy) throws CrawlingException {
		HashMap<String,String> headers = new HashMap<String,String>();
		headers.put("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/42.0.2311.152 Safari/537.36");
		try {
			//HttpResponse response = shouldProxy ? http.get(url, PROXY, headers) : http.get(url, headers);
			HttpResponse response = http.get(url, headers, null);
			return response.getContentBytes();			
		} catch(Exception e) {
			e.printStackTrace();
		}
		return new byte[0];
	}
	public Crawler getDefinition(long id) throws Exception {
		Entity entity = entityService.getEntity(id);
		Crawler definition = new Crawler(entity);
		return definition;
	}
	
	
	public Seed getSeed(String urlStr) {
		Seed seed = (Seed)cacheService.get("seedCache", urlStr);
		if(seed != null) return seed;
		SearchRequest query = new SearchRequest(CrawlingModel.SEED);
		query.setOperator("AND");
		try {
			URL url = new URL(null, urlStr, new jcifs.smb.Handler());
			String pathStr = url.getPath() != null && url.getPath().length() > 0 ? url.getPath() : null;
			String queryStr = url.getQuery() != null && url.getQuery().length() > 0 ? url.getQuery() : null;
			Clause clause = new Clause();
			clause.setOperator(Clause.OPERATOR_AND);
			clause.addProperty(new Property(new QName("openapps_org_system_1_0", "protocol.keyword"), url.getProtocol()));
			clause.addProperty(new Property(new QName("openapps_org_system_1_0", "domain.keyword"), url.getHost()));
			if(pathStr != null)
				clause.addProperty(new Property(new QName("openapps_org_system_1_0", "path"), "\""+pathStr+"\""));
			if(queryStr != null)
				clause.addProperty(new Property(new QName("openapps_org_system_1_0", "query.keyword"), queryStr));
			query.getClauses().add(clause);
		} catch(Exception e) {
			e.printStackTrace();
		}		
		SearchResponse entities = searchService.search(query);
		//System.out.println(query.getNativeQuery().toString());
		if(entities.getResults().size() > 0) {			
			for(SearchResult entity : entities.getResults()) {
				try {
					seed = new Seed(entityService.getEntity(Long.valueOf(entity.getId())));
					if(seed.getUrl().equals(urlStr)) {
						cacheService.put("seedCache", urlStr, seed);
						return seed;
					}
				} catch(Exception e) {
					e.printStackTrace();
				}
			}
		}
		return null;
	}
	
	public Document getDocument(String urlStr) {
		Document document = (Document)cacheService.get("documentCache", urlStr);
		if(document != null) return document;
		SearchRequest query = new SearchRequest(CrawlingModel.DOCUMENT);
		query.setOperator("AND");
		try {
			URL url = new URL(null, urlStr, new jcifs.smb.Handler());
			int index = url.getPath().lastIndexOf("/");
			String fileStr = url.getPath().substring(index+1, url.getPath().length());
			String pathStr = url.getPath().substring(0, index);			
			String queryStr = url.getQuery() != null && url.getQuery().length() > 0 ? url.getQuery() : null;
			
			Clause clause = new Clause();
			clause.setOperator(Clause.OPERATOR_AND);
			clause.addProperty(new Property(new QName("openapps_org_system_1_0", "protocol"), url.getProtocol()));
			clause.addProperty(new Property(new QName("openapps_org_system_1_0", "domain"), url.getHost()));			
			
			if(pathStr != null)
				clause.addProperty(new Property(new QName("openapps_org_system_1_0", "path"), "\""+pathStr+"\""));
			if(fileStr != null)
				clause.addProperty(new Property(new QName("openapps_org_system_1_0", "file"), "\""+fileStr+"\""));
			if(queryStr != null && !queryStr.contains(" "))
				clause.addProperty(new Property(new QName("openapps_org_system_1_0", "query"), queryStr));	
			query.getClauses().add(clause);
			
		} catch(Exception e) {
			e.printStackTrace();
		}
		SearchResponse entities = searchService.search(query);
		//System.out.println(query.getNativeQuery().toString());
		if(entities.getResults().size() > 0) {
			document = new Document();
			for(SearchResult entity : entities.getResults()) {
				try {
					document.setEntity(entityService.getEntity(Long.valueOf(entity.getId())));
					String documentUrl = document.getUrl();
					if(documentUrl.equals(urlStr)) {
						cacheService.put("documentCache", urlStr, document);
						return document;
					}
				} catch(Exception e) {
					e.printStackTrace();
				}
			}
		}
		return document;
	}
	public Document getDocument(long id) throws Exception {
		Entity entity = entityService.getEntity(id);
		return new Document(entity);
	}
	public Crawler getCrawler(long id) {
		try {
			Entity entity = entityService.getEntity(id);
			Crawler crawler = new Crawler(entity);
			//setNewDocuments(crawler);
			return crawler;
		} catch(Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	public Crawler getCrawler(String urlStr) {
		SearchRequest query = new SearchRequest(CrawlingModel.CRAWLER);
		query.setOperator("AND");
		try {
			URL url = new URL(null, urlStr, new jcifs.smb.Handler());
			String pathStr = url.getPath() != null && url.getPath().length() > 0 ? url.getPath() : null;
			String queryStr = url.getQuery() != null && url.getQuery().length() > 0 ? url.getQuery() : null;
			Clause clause = new Clause();
			clause.setOperator(Clause.OPERATOR_AND);
			clause.addProperty(new Property(new QName("openapps_org_system_1_0", "protocol_e"), url.getProtocol()));
			clause.addProperty(new Property(new QName("openapps_org_system_1_0", "domain_e"), url.getHost()));
			if(pathStr != null)
				clause.addProperty(new Property(new QName("openapps_org_system_1_0", "path_e"), pathStr));
			if(queryStr != null)
				clause.addProperty(new Property(new QName("openapps_org_system_1_0", "query_e"), queryStr));
			query.getClauses().add(clause);
		} catch(Exception e) {
			e.printStackTrace();
		}
		SearchResponse entities = searchService.search(query);
		if(entities.getResults().size() > 0) {			
			for(SearchResult entity : entities.getResults()) {
				try {
					Crawler crawler = new Crawler(entityService.getEntity(Long.valueOf(entity.getId())));
					if(crawler.getUrl().equals(urlStr)) {
						//setNewDocuments(crawler);
						return crawler;
					}
				} catch(Exception e) {
					e.printStackTrace();
				}
			}
		}
		return null;
	}
	public boolean isSecure(Crawler crawler) {
		if(crawler.getUrl().contains("trulia.com") ||
				crawler.getUrl().contains("foxnews.com"))
			return false;
		return true;
	}
	public Crawler getPrimaryCrawler(Seed seed) {
		List<Association> associations = seed.getAssociations(CrawlingModel.SEEDS);
		Crawler crawler = null;
		for(Association association : associations) {
			Crawler c = getCrawler(association.getSource());
			if(crawler == null || c.getFrequency() > crawler.getFrequency())
				crawler = c;
		}
		return crawler;
	}
	public void copyCrawlers(Seed seed1, Seed seed2) {
		List<Association> associations = seed1.getAssociations(CrawlingModel.SEEDS);
		for(Association association : associations) {
			try {
				Entity source = entityService.getEntity(association.getSource());
				Association assoc = new Association(CrawlingModel.SEEDS, source, seed2);
				entityService.updateAssociation(assoc);
			} catch(Exception e) {
				e.printStackTrace();
			}
		}
	}
	public void addSeedToCrawler(Crawler crawler, Seed seed) {
		boolean add = true;
		List<Association> associations = crawler.getAssociations(CrawlingModel.SEEDS);
		for(Association association : associations) {
			if(association.getTarget() == seed.getId()) add = false;
		}
		if(add) {
			Association assoc = new Association(CrawlingModel.SEEDS, crawler, seed);
			try {
				entityService.updateAssociation(assoc);
			} catch(Exception e) {
				e.printStackTrace();
			}
		}
	}
	public void removeSeedFromCrawler(Crawler crawler, Seed seed) {
		List<Association> associations = crawler.getAssociations(CrawlingModel.SEEDS);
		for(Association association : associations) {
			if(association.getTarget() == seed.getId()) {
				try {
					entityService.removeAssociation(association.getId());
				} catch(Exception e) {
					e.printStackTrace();
				}				
			}
		}
	}
	public void addSearchToCrawler(Crawler crawler, Search search) {
		boolean add = true;
		List<Association> associations = crawler.getAssociations(CrawlingModel.SEARCHES);
		if(search.getId() == null || search.getId() == 0) {
			try {
				entityService.updateEntity(search);
				Association assoc = new Association(CrawlingModel.SEARCHES, crawler.getId(), search.getId());
				entityService.updateAssociation(assoc);
			} catch(Exception e) {
				e.printStackTrace();
			}
		} else {
			for(Association association : associations) {
				if(association.getTarget() == search.getId()) add = false;
			}
			if(add) {
				Association assoc = new Association(CrawlingModel.SEARCHES, crawler, search);
				try {
					entityService.updateAssociation(assoc);
				} catch(Exception e) {
					e.printStackTrace();
				}
			}
		}
	}
	public void removeSearchFromCrawler(Crawler crawler, Search search) {
		List<Association> associations = crawler.getAssociations(CrawlingModel.SEARCHES);
		for(Association association : associations) {
			if(association.getTarget() == search.getId()) {
				try {
					entityService.removeAssociation(association.getId());
				} catch(Exception e) {
					e.printStackTrace();
				}
			}
		}
	}
	public void addDocumentToSeed(Seed seed, Document document) {
		boolean add = true;
		List<Association> associations = seed.getAssociations(CrawlingModel.DOCUMENTS);
		for(Association association : associations) {
			if(association.getTarget() == document.getId()) add = false;
		}
		if(add) {
			Association assoc = new Association(CrawlingModel.DOCUMENTS, seed, document);
			try {
				entityService.updateAssociation(assoc);
			} catch(Exception e) {
				e.printStackTrace();
			}
		}
	}
	public void removeDocumentFromSeed(Seed seed, Document document) {
		List<Association> associations = seed.getAssociations(CrawlingModel.DOCUMENTS);
		for(Association association : associations) {
			if(association.getTarget() == document.getId()) {
				try {
					entityService.removeAssociation(association.getId());
				} catch(Exception e) {
					e.printStackTrace();
				}
			}
		}
	}
	public SearchResponse getSeeds(long crawler, int status) {
		SearchRequest query2 = new SearchRequest(CrawlingModel.SEED);
		query2.getParameters().add(new Parameter(CrawlingModel.CRAWLERS.toString(), String.valueOf(crawler)));
		if(status != 0) query2.getParameters().add(new Parameter(CrawlingModel.STATUS.toString(), String.valueOf(status)));
		query2.setEndRow(10);
		SearchResponse entities = searchService.search(query2);
		return entities;
	}
	@SuppressWarnings("rawtypes")
	public List getRunningSeeds(long crawler) {
		List<Seed> seeds = new ArrayList<Seed>();
		SearchRequest query2 = new SearchRequest(CrawlingModel.SEED);
		//query2.getProperties().add(new Property(CrawlingModel.CRAWLER, String.valueOf(crawler)));
		Clause statusClause = new Clause(Clause.OPERATOR_OR, new Property(CrawlingModel.STATUS, Seed.STATUS_HIT), new Property(CrawlingModel.STATUS, Seed.STATUS_RUNNING));
		query2.getClauses().add(statusClause);
		SearchResponse entities = searchService.search(query2);
		for(SearchResult entity : entities.getResults()) {
			try {
				Seed seed = new Seed(entityService.getEntity(Long.valueOf(entity.getId())));
				List<Crawler> crawlers = getCrawlers(seed);
				for(Crawler c : crawlers) {
					if(c.getId().equals(crawler))
						seeds.add(seed);
				}
			} catch(Exception e) {
				e.printStackTrace();
			}
		}
		return seeds;
	}
	public Crawler[] getCrawlers(String domain) {
		List<Crawler> crawlers = new ArrayList<Crawler>();
		SearchRequest query = new SearchRequest(CrawlingModel.CRAWLER);
		query.setDeleted(false);
		if(domain != null && domain.length() > 0)
			query.getParameters().add(new Parameter(SystemModel.DOMAIN.toString(), domain));
		SearchResponse entities = searchService.search(query);
		for(SearchResult entity : entities.getResults()) {
			try {
				Crawler crawler = new Crawler(entityService.getEntity(Long.valueOf(entity.getId())));
				//setNewDocuments(crawler);
				crawlers.add(crawler);
			} catch(Exception e) {
				e.printStackTrace();
			}
		}
		return crawlers.toArray(new Crawler[crawlers.size()]);
	}
	public List<Crawler> getCrawlers(Seed seed) {
		List<Crawler> crawlers = new ArrayList<Crawler>();
		List<Association> associations = seed.getAssociations(CrawlingModel.SEEDS);
		for(Association association : associations) {
			try {
				Entity entity = entityService.getEntity(association.getSource());
				Crawler crawler = new Crawler(entity);
				//setNewDocuments(crawler);
				crawlers.add(crawler);
			} catch(Exception e) {
				e.printStackTrace();
			}
		}
		return crawlers;
	}
	public DocumentResultSet getDocuments(Crawler crawler, String queryString, int start, int end, Sort... sorts) {
		SearchRequest query = new SearchRequest(CrawlingModel.DOCUMENT);
		query.setOperator("OR");
		query.setDeleted(false);
		query.getClauses().add(new Clause(Clause.OPERATOR_AND, new Property(CrawlingModel.LOADED, "true")));
		if(sorts == null) query.addSort(new Sort(ModelField.TYPE_LONG, CrawlingModel.TIMESTAMP.toString(), "desc"));
		else {
			query.addSort(sorts[0]);
		}
		query.setStartRow(start);
		query.setEndRow(end); 
		/*
		if(queryString != null) {
			if(queryString.equals("deleted documents")) {
				query.setDeleted(true);
			} else {
				query.getFields().add(CrawlingModel.CONTENT.toString());
				query.setQueryString(queryString);
			}
		}
		*/
		if(crawler != null) {
			query.getParameters().add(new Parameter(CrawlingModel.CRAWLERS.toString(), String.valueOf(crawler.getId())));
		} 
		SearchResponse entities = searchService.search(query);
		DocumentResultSet documents = new DocumentResultSet(queryString, entities.getResultSize(), entities.getStartRow(), entities.getEndRow());
		
		for(SearchResult documentEntity : entities.getResults()) {			
			try {
				Document document = (Document)entityService.getEntity(Long.valueOf(documentEntity.getId()));
				documents.getResults().add(document);
			} catch(Exception e) {
				e.printStackTrace();
			}
		}
		System.out.println("entity search: " +entities.getResultSize()+" results parsed to " + query.getNativeQuery());
		return documents;
	}
	public List<Document> getNewDocuments(long crawler) {
		List<Document> documents = new ArrayList<Document>();
		SearchRequest query = new SearchRequest(CrawlingModel.DOCUMENT);
		query.setOperator("AND");
		query.addSort(new Sort(ModelField.TYPE_LONG, CrawlingModel.TIMESTAMP.toString(), "desc"));
				
		query.getParameters().add(new Parameter(CrawlingModel.CRAWLERS.toString(), String.valueOf(crawler)));
		query.getParameters().add(new Parameter(CrawlingModel.VIEWED.toString(), "false"));
		
		SearchResponse entities = searchService.search(query);
		//log.info("entity search: " +entities.getResultSize()+" results parsed to " + query.getNativeQuery());
		for(SearchResult entity : entities.getResults()) {
			try {
				documents.add(new Document(entityService.getEntity(Long.valueOf(entity.getId()))));
			} catch(Exception e) {
				e.printStackTrace();
			}
		}
		return documents;
	}
	public void markAllDocumentsViewed(Crawler crawler) {
		SearchRequest entityQuery = new SearchRequest(CrawlingModel.DOCUMENT);
    	entityQuery.setOperator("AND");				
    	entityQuery.getParameters().add(new Parameter(CrawlingModel.CRAWLERS.toString(), String.valueOf(crawler)));
    	entityQuery.getParameters().add(new Parameter(CrawlingModel.VIEWED.toString(), "false"));
		int newDocs = searchService.count(entityQuery); 
		DocumentResultSet documents = getDocuments(crawler, null, 1, newDocs);
    	for(Entity entity : documents.getResults()) {
    		Document document = new Document(entity);
    		document.setViewed(true);
    		try {
    			getEntityService().updateEntity(document);
    		} catch(Exception e) {
    			System.out.println("error setting document to 'viewed'");
    		}
    	}
    }
	public List<Document> getUnloadedDocuments() {
		List<Document> documents = new ArrayList<Document>();
		SearchRequest query = new SearchRequest(CrawlingModel.DOCUMENT);
		query.getParameters().add(new Parameter(CrawlingModel.LOADED.toString(), "false"));
		
		SearchResponse entities = searchService.search(query);
		for(SearchResult entity : entities.getResults()) {
			try {
				documents.add(new Document(entityService.getEntity(Long.valueOf(entity.getId()))));
			} catch(Exception e) {
				e.printStackTrace();
			}
		}
		return documents;
	}
	public List<Search> getSearches(long crawler) {
		List<Search> searches = new ArrayList<Search>();
		try {
		Entity crawlerEntity = entityService.getEntity(crawler);
			List<Association> assocs = crawlerEntity.getAssociations(CrawlingModel.SEARCHES);
			for(Association assoc : assocs) {
				Entity entity = entityService.getEntity(assoc.getTarget());
				searches.add(new Search(entity));
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
		return searches;
	}
	public Crawler saveCrawler(Crawler crawler) {		
		try {
			if(crawler.getId() == null || crawler.getId() == 0) {
				String url = crawler.getUrl();
				int index = 0;				
				while(index > -1) {
					int start = crawler.getUrl().indexOf("[", index);
					if(start > -1) {
						int end = crawler.getUrl().indexOf("]", start);
						if(end > -1) {
							String key = crawler.getUrl().substring(start+1, end);
							String value = crawler.getPropertyValueString(QName.createQualifiedName(CrawlingModel.CRAWLING_MODEL_1_0_URI, key));
							if(value != null) {
								url = url.replace("["+key+"]", value);
							}
						}
					}
					if(start > -1) index = start + 1;
					else index = -1;
				}
				crawler.setUrl(url);
				entityService.updateEntity(crawler);				
			} else {
				entityService.updateEntity(crawler);
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
		return crawler;
	}
	public Seed saveSeed(Seed seed) {
		try {
			if(seed.getId() == null || seed.getId() == 0) {
				entityService.updateEntity(seed);
			} else {
				entityService.updateEntity(seed);			
			}
			cacheService.put("seedCache", seed.getUrl(), seed);
		} catch(Exception e) {
			e.printStackTrace();
		}
		return seed;
	}
	public Document saveDocument(Document document) {
		try {
			entityService.updateEntity(document);
		} catch(Exception e) {
			e.printStackTrace();
		}
		return document;
	}
	public void saveDocument(Seed seed, Document document) {
		try {
			if(document.getId() == null || document.getId() == 0) {
				entityService.updateEntity(document);
				addDocumentToSeed(seed, document);
				seed.setStatus(Seed.STATUS_LOADED);
				entityService.updateEntity(seed);
			} else {
				entityService.updateEntity(document);
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	public List<String> getDomains() {
		List<String> list = new ArrayList<String>();
		SearchRequest query = new SearchRequest(CrawlingModel.CRAWLER);
		SearchResponse result = searchService.search(query);
		for(SearchResult crawler : result.getResults()) {
			try {
				String domain = entityService.getEntity(Long.valueOf(crawler.getId())).getPropertyValueString(SystemModel.DOMAIN);
				if(domain != null) {
					if(!list.contains(domain))
						list.add(domain);
				}
			} catch(Exception e) {
				e.printStackTrace();
			}
		}
		Collections.sort(list);
		return list;
	}
	public long getSeedDelay(String frequency) {
		if(frequency.equals("demand")) return 0;
		if(frequency.equals("daily")) return 1000 * 60 * 60 * 24;
		if(frequency.equals("12hours")) return 1000 * 60 * 60 * 12;
		if(frequency.equals("4hours")) return 1000 * 60 * 60 * 4;
		if(frequency.equals("hourly")) return 1000 * 60 * 60;
		if(frequency.equals("30minutes")) return 1000 * 60 * 30;
		if(frequency.equals("15minutes")) return 1000 * 60 * 15;
		if(frequency.equals("10minutes")) return 1000 * 60 * 10;
		if(frequency.equals("5minutes")) return 1000 * 60 * 5;
		if(frequency.equals("1minute")) return 1000 * 60 * 1;
		if(frequency.equals("30seconds")) return 1000 * 30;
		return 0;
	}
	public long calculateNextCrawl(Crawler crawler) {
		DateTime current = new DateTime();
		DateTime timeOfDay1 = (crawler.getTimeOfDay1() != null && crawler.getTimeOfDay1().length() == 6) ? getDateTime(crawler.getTimeOfDay1()) : current.plusDays(1);		
		long nextCrawl = System.currentTimeMillis();
		
		if(crawler.getFrequency() == Crawler.FREQ_MONTHLY) {
			nextCrawl = current.plusMonths(1).getMillis();
		} else if(crawler.getFrequency() == Crawler.FREQ_WEEKLY) {
			nextCrawl = current.plusWeeks(1).getMillis();
		} else if(crawler.getFrequency() == Crawler.FREQ_DAILY) {			
			if(timeOfDay1.isBeforeNow()) timeOfDay1.plusDays(1);
			nextCrawl = timeOfDay1.getMillis();
		} else if(crawler.getFrequency() == Crawler.FREQ_2DAILY) {
			DateTime timeOfDay2 = (crawler.getTimeOfDay2() != null && crawler.getTimeOfDay2().length() == 6) ? getDateTime(crawler.getTimeOfDay2()) : current;
			if(timeOfDay1.isBeforeNow() && timeOfDay2.isAfterNow()) nextCrawl = timeOfDay2.getMillis();
			else if(timeOfDay2.isBeforeNow() && timeOfDay1.isAfterNow()) nextCrawl = timeOfDay1.getMillis();
			else nextCrawl = timeOfDay1.plusDays(1).getMillis();
		} else if(crawler.getFrequency() == Crawler.FREQ_8HOUR) {
			nextCrawl = current.plusHours(8).getMillis();
		} else if(crawler.getFrequency() == Crawler.FREQ_4HOUR) {
			nextCrawl = current.plusHours(4).getMillis();
		} else if(crawler.getFrequency() == Crawler.FREQ_2HOUR) {
			nextCrawl = current.plusHours(2).getMillis();
		} else if(crawler.getFrequency() == Crawler.FREQ_HOUR) {
			nextCrawl = current.plusHours(1).getMillis();
		} else {
			nextCrawl = current.plusDays(1).getMillis();
		}
		return nextCrawl;
	}
	
	@Override
	public void remove(QName qname, long id) throws InvalidEntityException {
		getEntityService().removeEntity(id);
	}
	/*
	protected void setNewDocuments(Crawler crawler) {
		EntityQuery query2 = new EntityQuery(new QName[]{CrawlingModel.DOCUMENT,RealEstateModel.PROPERTY});
		query2.setType(EntityQuery.TYPE_LUCENE_TEXT);
		query2.setDefaultOperator("AND");
		query2.setSort(new Sort(ModelField.TYPE_LONG, CrawlingModel.TIMESTAMP.toString(), true));				
		query2.getProperties().add(new Property(CrawlingModel.CRAWLERS, String.valueOf(crawler.getId())));
		query2.getProperties().add(new Property(CrawlingModel.VIEWED, "false"));		
		int newDocs = getEntityService().count(query2);		
		crawler.setNewDocuments(newDocs);
	}
	*/
	protected DateTime getDateTime(String timeOfDay) {
		DateTime current = new DateTime();
		int hour = Integer.valueOf(timeOfDay.substring(0, 2));
		int minute = (timeOfDay != null && timeOfDay.length() == 6) ? Integer.valueOf(timeOfDay.substring(2, 4)) : current.getMinuteOfHour();
		String ampm = (timeOfDay != null && timeOfDay.length() == 6) ? timeOfDay.substring(4, 6) : "AM";
		if(ampm.equals("PM")) hour = hour + 12;
		DateTime fireTime = new DateTime(current.getYear(), current.getMonthOfYear(), current.getDayOfMonth(), Integer.valueOf(hour), Integer.valueOf(minute), 0, 0);
		return fireTime;
	}
	public CrawlingEngine getEngine(String protocol) {
		return engines.get(protocol);
	}
	public Map<String, CrawlingEngine> getEngines() {
		return engines;
	}
	public void setEngines(Map<String, CrawlingEngine> engines) {
		this.engines = engines;
	}
	public List<String> getBlacklist() {
		return blacklist;
	}
	public void setBlacklist(List<String> blacklist) {
		this.blacklist = blacklist;
	}
	public List<CrawlProcessor> getProcessors() {
		return processors;
	}
	public void setProcessors(List<CrawlProcessor> processors) {
		this.processors = processors;
	}
	public EntityService getEntityService() {
		return entityService;
	}
	public void setEntityService(EntityService entityService) {
		this.entityService = entityService;
	}	
	public SchedulingService getSchedulingService() {
		return schedulingService;
	}
	public void setSchedulingService(SchedulingService schedulingService) {
		this.schedulingService = schedulingService;
	}
	public long getCrawlingDelay() {
		return crawlingDelay;
	}
	public void setCrawlingDelay(long crawlingDelay) {
		this.crawlingDelay = crawlingDelay;
	}
	public CacheService getCacheService() {
		return cacheService;
	}
	public void setCacheService(CacheService cacheService) {
		this.cacheService = cacheService;
	}
	public ContentService getContentService() {
		return contentService;
	}
	public void setContentService(ContentService contentService) {
		this.contentService = contentService;
	}		
	public ProxyService getProxyService() {
		return proxyService;
	}
	public void setProxyService(ProxyService proxyService) {
		this.proxyService = proxyService;
	}
	public void setSearchService(SearchService searchService) {
		this.searchService = searchService;
	}
	@Override
	public CrawlProcessor getCrawlProcessor(Crawler arg0) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public CrawlingUser getUser() {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public void saveUser(CrawlingUser arg0) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public List<Crawler> getCrawlersByCategory(String arg0) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public List<Crawler> getCrawlersByDomain(String arg0) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public List<CrawlProcessor> getProcessorsByCategory(String arg0, boolean arg1) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public Search saveSearch(Long arg0, String arg1) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public void setCrawlProcessors(List<CrawlProcessor> arg0) {
		// TODO Auto-generated method stub
		
	}

}
