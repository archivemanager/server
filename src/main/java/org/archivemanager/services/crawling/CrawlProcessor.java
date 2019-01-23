package org.archivemanager.services.crawling;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

import org.archivemanager.models.dictionary.ModelField;
import org.archivemanager.services.content.ContentService;


public interface CrawlProcessor extends Serializable {
	public static final String FETCH_STANDARD = "standard";
	public static final String FETCH_AUTOMATED = "automated";
		
	void initialize();
	
	String getType();
	String getUrl();
	String getName();
	String getDescription();
	String getCategory();
	String getIcon();
	String getConfigurationId();
	String getFetch();
	int getMaxResults();
	boolean isFeatured();
	boolean isAnonymous();
	List<ModelField> getFields();
	
	boolean canExtract(Seed seed);
	
	Crawler getCrawler(Map<String,String> properties);
	
	Seed start(Seed seed) throws CrawlingException;
	List<Seed> seed(Seed seed) throws CrawlingException;	
	Document extract(Crawler crawler, Seed seed) throws CrawlingException;
	String process(String html) throws CrawlingException;
	
	void setContentService(ContentService contentService);
	void setCrawlingService(CrawlingService crawlingService);
	
	public byte[] getIconData();
	public void setIconData(byte[] iconData);
}
