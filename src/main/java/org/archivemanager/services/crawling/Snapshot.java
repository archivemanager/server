package org.archivemanager.services.crawling;

import java.net.URL;

import org.archivemanager.models.CrawlingModel;
import org.archivemanager.models.SystemModel;
import org.archivemanager.services.entity.Entity;


public class Snapshot extends Entity implements Crawlable {
	private static final long serialVersionUID = -6268988968223535266L;	
	
	public Snapshot() {
		setQName(CrawlingModel.SNAPSHOT);
	}
	public Snapshot(Entity entity) {
		setId(entity.getId());
		addProperty(SystemModel.NAME, entity.getName());
		setProperties(entity.getProperties());
		setSourceAssociations(entity.getSourceAssociations());
		setTargetAssociations(entity.getTargetAssociations());
	}
	public Snapshot(String url, String name, String summary) {
		setQName(CrawlingModel.SNAPSHOT);
		setUrl(url);
		addProperty(SystemModel.NAME, name);
		setSummary(summary);
		setTimestamp(System.currentTimeMillis());
	}
	
	public void setEntity(Entity entity) {
		setId(entity.getId());
		addProperty(SystemModel.NAME, entity.getName());
		setProperties(entity.getProperties());
		setSourceAssociations(entity.getSourceAssociations());
		setTargetAssociations(entity.getTargetAssociations());
	}
	
	public void setUrl(String urlStr) {
		try {
			URL url = new URL(urlStr);
			setProtocol(url.getProtocol());
			setDomain(url.getHost());
			if(url.getPath() != null && url.getPath().length() > 0) setPath(url.getPath());
			else setPath("");
			if(url.getQuery() != null) setQuery(url.getQuery());
			else setQuery("");
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
	public void setProtocol(String protocol) {
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
		//domain = domain.replace("google2.", "").replace("www.", "");
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
	public long getVersion() {
		String version = getPropertyValueString(CrawlingModel.VERSION);
		if(version != null && version.length() > 0) return Long.valueOf(version);
		return 0;
	}
	public void setVersion(long version) {
		try {
			addProperty(CrawlingModel.VERSION, version);
		} catch(Exception e) {
			System.out.println("InvalidPropertyException "+CrawlingModel.VERSION+" -> "+version);
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
		String summary = getPropertyValueString(CrawlingModel.SUMMARY) != null ? getPropertyValueString(CrawlingModel.SUMMARY) : "";
		return summary;
	}
	public void setSummary(String summary) {
		try {
			addProperty(CrawlingModel.SUMMARY, summary);
		} catch(Exception e) {
			System.out.println("InvalidPropertyException "+CrawlingModel.SUMMARY+" -> "+summary);
		}
	}
	public String getContent() {
		return getPropertyValueString(CrawlingModel.CONTENT);
	}
	public void setContent(String message) {
		try {
			addProperty(CrawlingModel.CONTENT, message);
		} catch(Exception e) {
			System.out.println("InvalidPropertyException "+CrawlingModel.CONTENT+" -> "+message);
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
	
}
