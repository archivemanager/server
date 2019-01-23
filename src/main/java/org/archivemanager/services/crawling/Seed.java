package org.archivemanager.services.crawling;
import java.io.StringWriter;
import java.net.URL;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.JsonWriter;

import org.archivemanager.models.CrawlingModel;
import org.archivemanager.models.SystemModel;
import org.archivemanager.services.entity.Entity;


public class Seed extends Entity {
	private static final long serialVersionUID = -6268988968223535266L;
	private byte[] content;
	
	public static final int STATUS_BLACKLISTED = 5;
	public static final int STATUS_EXTRACTED = 4;
	public static final int STATUS_LOADED = 3;
	public static final int STATUS_HIT = 2;	
	public static final int STATUS_RUNNING = 1;
	public static final int STATUS_PAUSED = 0;
	public static final int STATUS_ERROR = -1;
	
	
	public Seed() {
		setQName(CrawlingModel.SEED);
	}
	public Seed(Entity entity) {
		setEntity(entity);
	}
	public Seed(String url, String name) {
		setQName(CrawlingModel.DOCUMENT);
		setUrl(url);
		addProperty(SystemModel.NAME, name);
		setTimestamp(System.currentTimeMillis());
	}
	public Seed(JsonObject object) {
		setQName(CrawlingModel.SEED);
		if(object.containsKey("id")) setId(object.getJsonNumber("id").longValue());
		if(object.containsKey("uid")) setUid(object.getString("uid"));
		if(object.containsKey("url")) setUrl(object.getString("url"));
		if(object.containsKey("status")) setStatus(object.getInt("status"));
	}
	
	public void setEntity(Entity entity) {
		setId(entity.getId());
		addProperty(SystemModel.NAME, entity.getName());
		setProperties(entity.getProperties());
		setSourceAssociations(entity.getSourceAssociations());
		setTargetAssociations(entity.getTargetAssociations());
	}
	
	//Transient
	public byte[] getContent() {
		return content;
	}
	public void setContent(byte[] content) {
		this.content = content;
	}
	
	public void setUrl(URL url) {
		try {
			setProtocol(url.getProtocol());
			setDomain(url.getHost());
			if(url.getPath() != null && url.getPath().length() > 0) setPath(url.getPath());
			if(url.getQuery() != null) setQuery(url.getQuery());
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	public void setUrl(String urlStr) {
		try {
			URL url = new URL(urlStr);
			setProtocol(url.getProtocol());
			setDomain(url.getHost());
			if(url.getPath() != null && url.getPath().length() > 0) setPath(url.getPath());
			if(url.getQuery() != null) setQuery(url.getQuery());
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	public String getUrl() {
		String url = getProtocol() + "://" + getDomain();
		if(getPath() != null && getPath().length() > 0) url = url + getPath();
		if(getQuery() != null && getQuery().length() > 0) url = url + "?" + getQuery();
		return url;
	}
	public String getProtocol() {
		return getPropertyValueString(SystemModel.PROTOCOL);
	}
	protected void setProtocol(String protocol) {
		try {
			addProperty(SystemModel.PROTOCOL, protocol);
		} catch(Exception e) {
			System.out.println("InvalidPropertyException "+SystemModel.PROTOCOL+" -> "+protocol);
		}
	}
	public String getDomain() {
		return getPropertyValueString(SystemModel.DOMAIN);
	}
	public void setDomain(String domain) {
		//if(domain != null) domain = domain.replace("google2.", "").replace("www.", "");
		try {
			addProperty(SystemModel.DOMAIN, domain);
		} catch(Exception e) {
			System.out.println("InvalidPropertyException "+SystemModel.DOMAIN+" -> "+domain);
		}
	}
	public String getPath() {
		return getPropertyValueString(SystemModel.PATH);
	}
	public void setPath(String path) {
		try {
			addProperty(SystemModel.PATH, path);
		} catch(Exception e) {
			System.out.println("InvalidPropertyException "+SystemModel.PATH+" -> "+path);
		}
	}
	public String getQuery() {
		return getPropertyValueString(SystemModel.QUERY);
	}
	public void setQuery(String query) {
		try {
			addProperty(SystemModel.QUERY, query);
		} catch(Exception e) {
			System.out.println("InvalidPropertyException "+SystemModel.QUERY+" -> "+query);
		}
	}
	public String getIcon() {
		return getPropertyValueString(CrawlingModel.ICON);
	}
	public void setIcon(String icon) {
		try {
			addProperty(CrawlingModel.ICON, icon);
		} catch(Exception e) {
			System.out.println("InvalidPropertyException "+CrawlingModel.ICON+" -> "+icon);
		}
	}
	public long getTimestamp() {
		String timestamp = getPropertyValueString(CrawlingModel.TIMESTAMP);
		if(timestamp != null && timestamp.length() > 0) return Long.valueOf(timestamp);
		return 0;
	}
	public void setTimestamp(long timestamp) {
		try {
			addProperty(CrawlingModel.TIMESTAMP, timestamp);
		} catch(Exception e) {
			System.out.println("InvalidPropertyException "+CrawlingModel.TIMESTAMP+" -> "+timestamp);
		}
	}
	public String getSummary() {
		return getPropertyValueString(CrawlingModel.SUMMARY);
	}
	public void setSummary(String summary) {
		try {
			addProperty(CrawlingModel.SUMMARY, summary);
		} catch(Exception e) {
			System.out.println("InvalidPropertyException "+CrawlingModel.SUMMARY+" -> "+summary);
		}
	}
	public int getHops() {
		String hops = getPropertyValueString(CrawlingModel.HOPS);
		if(hops != null && hops.length() > 0) return Integer.valueOf(hops);
		return 1;
	}
	public void setHops(int hops) {
		try {
			addProperty(CrawlingModel.HOPS, hops);
		} catch(Exception e) {
			System.out.println("InvalidPropertyException "+CrawlingModel.HOPS+" -> "+hops);
		}
	}
	public int getStatus() {
		String status = getPropertyValueString(CrawlingModel.STATUS);
		if(status != null && status.length() > 0) return Integer.valueOf(status);
		return STATUS_PAUSED;
	}
	public void setStatus(int status) {
		try {
			addProperty(CrawlingModel.STATUS, status);
		} catch(Exception e) {
			System.out.println("InvalidPropertyException "+CrawlingModel.STATUS+" -> "+status);
		}
	}
	public String getMessage() {
		return getPropertyValueString(CrawlingModel.MESSAGE);
	}
	public void setMessage(String message) {
		try {
			addProperty(CrawlingModel.MESSAGE, message);
		} catch(Exception e) {
			System.out.println("InvalidPropertyException "+CrawlingModel.MESSAGE+" -> "+message);
		}
	}	
	public String getSeeded() {
		return getPropertyValueString(CrawlingModel.SEEDED);
	}
	public void setSeeded(String seeded) {
		try {
			addProperty(CrawlingModel.SEEDED, seeded);
		} catch(Exception e) {
			System.out.println("InvalidPropertyException "+CrawlingModel.SEEDED+" -> "+seeded);
		}
	}
	public String getExtracted() {
		return getPropertyValueString(CrawlingModel.EXTRACTED);
	}
	public void setExtracted(String extracted) {
		try {
			addProperty(CrawlingModel.EXTRACTED, extracted);
		} catch(Exception e) {
			System.out.println("InvalidPropertyException "+CrawlingModel.EXTRACTED+" -> "+extracted);
		}
	}
	public String getContentType() {
		return getPropertyValueString(CrawlingModel.CONTENT_TYPE);
	}
	public void setContentType(String message) {
		try {
			addProperty(CrawlingModel.CONTENT_TYPE, message);
		} catch(Exception e) {
			System.out.println("InvalidPropertyException "+CrawlingModel.CONTENT_TYPE+" -> "+message);
		}
	}
	
	public int getErrorCount() {
		String hops = getPropertyValueString(CrawlingModel.HOPS);
		if(hops != null && hops.length() > 0) return Integer.valueOf(hops);
		return 0;
	}
	public void setErrorCount(int count) {
		try {
			addProperty(CrawlingModel.ERROR_COUNT, count);
		} catch(Exception e) {
			System.out.println("InvalidPropertyException "+CrawlingModel.HOPS+" -> "+count);
		}
	}
	public boolean getLoaded() {
		String loaded = getPropertyValueString(CrawlingModel.LOADED);
		if(loaded != null && loaded.length() > 0 && loaded.equals("true")) return true;
		return false;
	}
	public void setLoaded(boolean loaded) {
		try {
			addProperty(CrawlingModel.LOADED, loaded);
		} catch(Exception e) {
			System.out.println("InvalidPropertyException "+CrawlingModel.LOADED+" -> "+loaded);
		}
	}
	public boolean getAnonymous() {
		String domain = getPropertyValueString(CrawlingModel.ANONYMOUS);
		if(domain != null && domain.length() > 0 && domain.equals("true")) return true;
		return false;
	}
	public void setAnonymous(boolean anonymous) {
		try {
			addProperty(CrawlingModel.ANONYMOUS, anonymous);
		} catch(Exception e) {
			System.out.println("InvalidPropertyException "+CrawlingModel.ANONYMOUS+" -> "+anonymous);
		}
	}
	public long getLastCrawl() {
		String lastCrawl = getPropertyValueString(CrawlingModel.LAST_CRAWL);
		if(lastCrawl != null && lastCrawl.length() > 0) return Long.valueOf(lastCrawl);
		return 0;
	}
	public void setLastCrawl(long lastCrawl) {
		try {
			addProperty(CrawlingModel.LAST_CRAWL, lastCrawl);
		} catch(Exception e) {
			System.out.println("InvalidPropertyException "+CrawlingModel.LAST_CRAWL+" -> "+lastCrawl);
		}
	}
	public int getMaxResults() {
		String count = getPropertyValueString(CrawlingModel.MAX_RESULTS);
		if(count != null && count.length() > 0) return Integer.valueOf(count);
		return 0;
	}
	public void setMaxResults(int count) {
		try {
			addProperty(CrawlingModel.MAX_RESULTS, count);
		} catch(Exception e) {
			System.out.println("InvalidPropertyException "+CrawlingModel.MAX_RESULTS+" -> "+count);
		}
	}
	public int getNumTries() {
		String count = getPropertyValueString(CrawlingModel.NUM_TRIES);
		if(count != null && count.length() > 0) return Integer.valueOf(count);
		return 0;
	}
	public void setNumTries(int count) {
		try {
			addProperty(CrawlingModel.NUM_TRIES, count);
		} catch(Exception e) {
			System.out.println("InvalidPropertyException "+CrawlingModel.NUM_TRIES+" -> "+count);
		}
	}
	@Override
	public boolean equals(Object obj) {
		if(obj instanceof Seed) {
			Seed seed = (Seed)obj;
			return getUrl().equals(seed.getUrl());
		}
		return false;
	}
	
	public String toJson() {
		StringWriter writer = new StringWriter();
		JsonWriter jsonWriter = Json.createWriter(writer);				
		JsonObject obj = toJsonObject();		
		jsonWriter.write(obj);
		return writer.toString();
	}
	public JsonObject toJsonObject() {
		JsonObjectBuilder builder = Json.createObjectBuilder();		
		if(getId() != null) builder.add("id", getId());
		if(getUid() != null) builder.add("uid", getUid());
		if(getName() != null) builder.add("name", getName());
		if(getUrl() != null) builder.add("url", getUrl());
		if(getContent() != null && getContent().length > 0) builder.add("content", new String(getContent()));
		builder.add("status", getStatus());
		return builder.build();
	}
}
