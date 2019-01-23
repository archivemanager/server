package org.archivemanager.services.crawling;

import java.io.StringWriter;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.archivemanager.models.SystemModel;
import org.archivemanager.models.dictionary.ModelField;
import org.archivemanager.models.dictionary.ModelFieldValue;
import org.archivemanager.services.content.ContentService;


public class ConfiguredCrawlProcessor implements CrawlProcessor {
	private static final long serialVersionUID = -7437565537497174879L;
	private CrawlingService crawlingService;
	private ContentService contentService;
	private String type;
	private String url;
	private String icon;
	private String category;
	private String title;
	private String description;
	private String imageSelector;
	private int frequency;
	private boolean anonymous = true;
	private boolean featured;
	private boolean domain = true;
	private boolean cached = true;
	private int hops;
	private int maxResults;
	private String configurationId;
	private String fetch = FETCH_STANDARD;
	private byte[] iconData;
	
	protected List<String> blacklist = new ArrayList<String>();
	protected List<String> whitelist = new ArrayList<String>();
	protected List<ElementSelector> selectors = new ArrayList<ElementSelector>();
	private List<ModelFieldValue> subsites = new ArrayList<ModelFieldValue>();
	private List<ModelField> fields = new ArrayList<ModelField>();
	private List<ElementDisplay> displays = new ArrayList<ElementDisplay>();
	
	
	@Override
	public void initialize() {
		for(ModelField field : fields) {
			if(field.getName().equals("count")) {
				getResultCountModel(field);					
			} 
		}
		if(subsites.size() > 0) {
			try {
				URL u = new URL(url);
				List<Crawler> crawlers = getCrawlingService().getCrawlersByDomain(u.getHost());
				fields.add(getSiteModel(crawlers, "Sub-Site"));
			} catch(Exception e) {e.printStackTrace();}
		}
	}

	@Override
	public boolean canExtract(Seed seed) {
		return false;
	}
	@Override
	public Crawler getCrawler(Map<String, String> properties) {
		Crawler crawler = new Crawler();
		crawler.setUrl(getUrl());
		crawler.addProperty(SystemModel.NAME, getTitle());
		crawler.setStatus(Crawler.STATUS_ACTIVE);
		crawler.setFrequency(getFrequency());
		crawler.setAnonymous(isAnonymous());		
		crawler.setConfigurationId(getConfigurationId());				
		crawler.setCached(isCached());
		String count = (properties.get("count") != null && properties.get("count").length() > 0) ? properties.get("count") : "10";
		crawler.setMaxResults(Integer.valueOf(count));		
		for(String key : properties.keySet()) {
			String value = properties.get(key);
			if(key.equals("url")) crawler.setUrl(value);
			else if(key.equals("url_name")) crawler.addProperty(SystemModel.NAME, getTitle() + " - " +value);
			else {
				if(key.equals("query")) crawler.addProperty(SystemModel.NAME, crawler.getName()+" - "+value);
				String q = value.replace(" ", "+");
				String newUrl = crawler.getUrl().replace("["+key+"]", q);
				crawler.setUrl(newUrl);
			}
		}
		if(getSubsites().size() > 0) {
			for(ModelFieldValue subsite : getSubsites()) {
				if(subsite.getValue().equals(crawler.getUrl())) {
					if(subsite.getCategory() != null) 
						crawler.setCategory(subsite.getCategory());
					else
						crawler.setCategory(getCategory());
				}
			}
		} else {
			crawler.setCategory(getCategory());
		}
		return crawler;
	}
	@Override
	public Seed start(Seed seed) throws CrawlingException {
		return null;
	}
	@Override
	public List<Seed> seed(Seed seed) throws CrawlingException {
		return null;
	}
	@Override
	public Document extract(Crawler crawler, Seed seed) throws CrawlingException {
		return null;
	}

	@Override
	public String process(String html) throws CrawlingException {
		return null;
	}	
	
	public ModelField getSiteModel(List<Crawler> crawlers, String label) {
		ModelField field = new ModelField(ModelField.TYPE_MEDIUMTEXT, label, SystemModel.URL);
		field.setMaxSize(125);
		List<String> urls = new ArrayList<String>();		
		if(crawlers != null) {
			for(Crawler crawler : crawlers) {
				urls.add(crawler.getUrl());
			}
		}
		for(ModelFieldValue value : subsites) {
			if(!urls.contains(value.getValue()))
				field.getValues().add(value);
		}		
	    return field;
	}
	
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getIcon() {
		return icon;
	}
	public void setIcon(String icon) {
		this.icon = icon;
	}
	public String getCategory() {
		return category;
	}
	public void setCategory(String category) {
		this.category = category;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public int getFrequency() {
		return frequency;
	}
	public void setFrequency(int frequency) {
		this.frequency = frequency;
	}
	public boolean isAnonymous() {
		return anonymous;
	}
	public void setAnonymous(boolean anonymous) {
		this.anonymous = anonymous;
	}
	public boolean isFeatured() {
		return featured;
	}
	public void setFeatured(boolean featured) {
		this.featured = featured;
	}
	public boolean isDomain() {
		return domain;
	}
	public void setDomain(boolean domain) {
		this.domain = domain;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public int getHops() {
		return hops;
	}
	public void setHops(int hops) {
		this.hops = hops;
	}	
	public List<ModelFieldValue> getSubsites() {
		return subsites;
	}
	public void setSubsites(List<ModelFieldValue> subsites) {
		this.subsites = subsites;
	}
	public List<ModelField> getFields() {
		if(subsites.size() > 0) {
			for(ModelField field : fields) {
				if(field.getLabel().equals("Sub-Site")) {
					try {
						URL u = new URL(url);
						List<Crawler> crawlers = getCrawlingService().getCrawlersByDomain(u.getHost());
						ModelField newField = (getSiteModel(crawlers, "Sub-Site"));
						field.setValues(newField.getValues());
					} catch(Exception e) {e.printStackTrace();}
				}
			}
		}
		return fields;
	}
	public void setFields(List<ModelField> fields) {
		this.fields = fields;
	}
	public List<ElementSelector> getSelectors() {
		return selectors;
	}
	public void setSelectors(List<ElementSelector> selectors) {
		this.selectors = selectors;
	}
	public List<ElementDisplay> getDisplays() {
		return displays;
	}
	public void setDisplays(List<ElementDisplay> displays) {
		this.displays = displays;
	}
	public List<String> getBlacklist() {
		return blacklist;
	}
	public void setBlacklist(List<String> blacklist) {
		this.blacklist = blacklist;
	}
	public List<String> getWhitelist() {
		return whitelist;
	}
	public void setWhitelist(List<String> whitelist) {
		this.whitelist = whitelist;
	}
	public String getImageSelector() {
		return imageSelector;
	}
	public void setImageSelector(String imageSelector) {
		this.imageSelector = imageSelector;
	}
	@Override
	public String getName() {
		return title;
	}	
	public CrawlingService getCrawlingService() {
		return crawlingService;
	}
	public void setCrawlingService(CrawlingService crawlingService) {
		this.crawlingService = crawlingService;
	}
	public ContentService getContentService() {
		return contentService;
	}
	public void setContentService(ContentService contentService) {
		this.contentService = contentService;
	}
	@Override
	public String getConfigurationId() {
		return configurationId;
	}
	public void setConfigurationId(String configId) {
		this.configurationId = configId;
	}
	@Override
	public int getMaxResults() {
		return maxResults;
	}
	public void setMaxResults(int results) {
		this.maxResults = results;
	}
	@Override
	public String getFetch() {
		return fetch;
	}
	public void setFetch(String fetch) {
		this.fetch = fetch;
	}
	public boolean isCached() {
		return cached;
	}
	public void setCached(boolean cached) {
		this.cached = cached;
	}

	public String toXml() {
		StringWriter writer = new StringWriter();
		String anonymous = isAnonymous() ? "true" : "false";
		String domain = isDomain() ? "true" : "false";
		writer.write("<agent type=\""+getType()+"\" anonymous=\""+anonymous+"\" domain=\""+domain+"\" results=\""+getMaxResults()+"\" frequency=\""+getFrequency()+"\" category=\""+getCategory()+"\">");
		if(getTitle() != null && getTitle().length() > 0)
			writer.write("<title>"+getTitle()+"</title>");
		if(getUrl() != null && getUrl().length() > 0)
			writer.write("<url>"+getUrl()+"</url>");
		for(String entry : whitelist) {
			writer.write("<whitelist>"+entry+"</whitelist>");
		}
		for(String entry : blacklist) {
			writer.write("<blacklist>"+entry+"</blacklist>");
		}
		for(ElementSelector selector : getSelectors()) {			
			writer.write("<selector type=\""+selector.getType()+"\" value=\""+selector.getValue()+"\"");
			if(selector.getFormat() != null)
				writer.write(" format=\""+selector.getFormat()+"\"");
			if(selector.getAttribute() != null)
				writer.write(" attribute=\""+selector.getAttribute()+"\"");
			writer.write(">");
			for(ElementFunction function : selector.getFunctions()) {
				if(function.getValue() != null) writer.write("<function type=\""+function.getType()+"\" value=\""+function.getValue()+"\">");
				else writer.write("<function type=\""+function.getType()+"\">");
				for(ElementValue value : function.getValues()) {
					writer.write("<value type=\""+value.getType()+"\" name=\""+value.getName()+"\" value=\""+value.getValue()+"\" />");
				}
				writer.write("</function>");
			}
			writer.write("</selector>");
		}
		writer.write("</agent>");
		return writer.toString();
	}
	
	protected String substring(ElementFunction function, String value) {
		if(function.getValues().size() == 1) {
			ElementValue v = function.getValues().get(0);
			if(v.getType().equals("integer")) {
				int length = Integer.valueOf(v.getValue());
				if(value.length() >= length) {
					if(v.getName().equals("end")) value = value.substring(0, length);
					else value.substring(length, value.length());
				}
			} else {
				int index = value.indexOf(v.getValue());
				if(index > -1) {
					if(v.getName().equals("end")) value = value.substring(0, index);
					else value = value.substring(index + v.getValue().length(), value.length());
				}
			}
		} else if(function.getValues().size() == 2) {
			ElementValue v1 = function.getValues().get(0);
			ElementValue v2 = function.getValues().get(1);
			int index1 = 0;
			int index2 = 0;
			if(v1.getType().equals("integer")) {
				if(v1.getName().equals("end")) index2 = Integer.valueOf(v1.getValue());
				else index1 = Integer.valueOf(v1.getValue());
			} else {
				if(v1.getName().equals("end")) {
					if(index1 > -1) index2 = value.indexOf(v1.getValue(), index1);
					else index2 = value.indexOf(v1.getValue());
				} else {
					index1 = value.indexOf(v1.getValue());
					if(index1 > -1)  index1 = index1 + v1.getValue().length();
				}
			}
			if(v2.getType().equals("integer")) {
				if(v2.getName().equals("end")) index2 = Integer.valueOf(v2.getValue());
				else index1 = Integer.valueOf(v1.getValue());
			} else {
				if(v2.getName().equals("end")) {
					if(index1 > -1) index2 = value.indexOf(v2.getValue(), index1);
					else index2 = value.indexOf(v2.getValue());
				} else {
					index1 = value.indexOf(v2.getValue() + v2.getValue().length());
					if(index1 > -1)  index1 = index1 + v2.getValue().length();
				}
			}
			if(index1 > -1 && index2 > -1) 
				value = value.substring(index1, index2);								
		}
		return value;
	}
	protected void getResultCountModel(ModelField field) {
		field.setMaxSize(80);
		field.getValues().add(new ModelFieldValue("10","10",""));
		field.getValues().add(new ModelFieldValue("20","20",""));
		field.getValues().add(new ModelFieldValue("30","30",""));
		field.getValues().add(new ModelFieldValue("40","40",""));
		field.getValues().add(new ModelFieldValue("50","50",""));
		field.getValues().add(new ModelFieldValue("60","60",""));
		field.getValues().add(new ModelFieldValue("70","70",""));
		field.getValues().add(new ModelFieldValue("80","80",""));
		field.getValues().add(new ModelFieldValue("90","90",""));
		field.getValues().add(new ModelFieldValue("100","100",""));
		field.getValues().add(new ModelFieldValue("110","110",""));
		field.getValues().add(new ModelFieldValue("120","120",""));
		field.getValues().add(new ModelFieldValue("130","130",""));
		field.getValues().add(new ModelFieldValue("140","140",""));
		field.getValues().add(new ModelFieldValue("150","150",""));
		field.getValues().add(new ModelFieldValue("200","200",""));
		field.getValues().add(new ModelFieldValue("250","250",""));
		field.getValues().add(new ModelFieldValue("300","300",""));
		field.getValues().add(new ModelFieldValue("400","400",""));
		field.getValues().add(new ModelFieldValue("500","500",""));
	}
	/*
	protected int getModelFieldType(String in) {
		if(in != null) {
			if(in.equals("text")) return ModelField.TYPE_MEDIUMTEXT;
			if(in.equals("integer")) return ModelField.TYPE_INTEGER;
			if(in.equals("short_text")) return ModelField.TYPE_SHORTTEXT;
		}
		return ModelField.TYPE_NULL;
	}
	*/
	public byte[] getIconData() {
		return iconData;
	}
	public void setIconData(byte[] iconData) {
		this.iconData = iconData;
	}
}
