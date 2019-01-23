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
import org.archivemanager.util.FileUtility;


public class Document extends Entity {
	private static final long serialVersionUID = -572852029548894303L;
	private String date;
	private String timezone;
	private Long crawlerId;
	private byte[] imageData;
	private byte[] documentData;
	
			
	public Document() {
		setQName(CrawlingModel.DOCUMENT);
	}
	public Document(Entity entity) {
		setId(entity.getId());
		addProperty(SystemModel.NAME, entity.getName());
		setProperties(entity.getProperties());
		setSourceAssociations(entity.getSourceAssociations());
		setTargetAssociations(entity.getTargetAssociations());
	}
	public Document(String url) {
		setQName(CrawlingModel.DOCUMENT);
		setUrl(url);
		setLoaded(true);
		setViewed(false);
	}
	public Document(JsonObject object) {
		setQName(CrawlingModel.DOCUMENT);
		if(object.containsKey("id")) setId(object.getJsonNumber("id").longValue());
		if(object.containsKey("uid")) setUid(object.getString("uid"));
		if(object.containsKey("name")) addProperty(SystemModel.NAME, object.getString("name"));
		if(object.containsKey("summary")) setSummary(object.getString("summary"));
		if(object.containsKey("url")) setUrl(object.getString("url"));
		
		if(object.containsKey("status")) setStatus(object.getInt("status"));
		if(object.containsKey("image_url")) setImageUrl(object.getString("image_url"));
		if(object.containsKey("timestamp")) setTimestamp(object.getJsonNumber("timestamp").longValue());
		if(object.containsKey("content_type")) setContentType(object.getString("content_type"));
		if(object.containsKey("loaded")) setLoaded(object.getBoolean("loaded"));
		if(object.containsKey("viewed")) setViewed(object.getBoolean("viewed"));
		if(object.containsKey("content")) setContent(object.getString("content"));
		/*
		if(object.containsKey("video")) setIsVideo(object.getBoolean("video"));
		if(object.containsKey("author")) setSummary(object.getString("author"));
		if(object.containsKey("price")) setSummary(object.getString("price"));
		if(object.containsKey("size")) setSummary(object.getString("size"));
		if(object.containsKey("journal")) setSummary(object.getString("journal"));
		if(object.containsKey("assignee")) setSummary(object.getString("assignee"));
		if(object.containsKey("inventor")) setSummary(object.getString("inventor"));
		
		if(object.containsKey("mls_id")) setSummary(object.getString("mls_id"));
		if(object.containsKey("")) setSummary(object.getString("list_price"));
		if(object.containsKey("")) setSummary(object.getString("lot_size"));
		if(object.containsKey("")) setSummary(object.getString("home_size"));
		*/
	}
	public Document(String protocol, String domain, String path, String name) {
		setQName(CrawlingModel.DOCUMENT);
		setProtocol(protocol);
		setDomain(domain);
		if(path != null) setPath(path);
		addProperty(SystemModel.NAME, name);
		setLoaded(true);
		setViewed(false);
	}
	public Document(String url, String name, String summary) {
		setQName(CrawlingModel.DOCUMENT);
		setUrl(url);
		addProperty(SystemModel.NAME, name);
		setSummary(summary);
		setLoaded(true);
		setViewed(false);
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
			if(url.getPath() != null && url.getPath().length() > 0) {
				if(FileUtility.isFile(url.getPath())) {
					int index = url.getPath().lastIndexOf("/");
					String fileName = url.getPath().substring(index+1, url.getPath().length());
					String path = url.getPath().substring(0, index);
					setFile(fileName);
					setPath(path);
				} else setPath(url.getPath());
			}
			if(url.getQuery() != null) {
				setQuery(url.getQuery());
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	public String getUrl() {
		String url = getProtocol() + "://" + getDomain();
		if(getPath() != null && getPath().length() > 0) url = url + getPath();
		if(getFile() != null && getFile().length() > 0) url = url + "/" + getFile();
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
	public String getFile() {
		return getPropertyValueString(SystemModel.FILE);
	}
	public void setFile(String file) {
		try {
			addProperty(SystemModel.FILE, file);
		} catch(Exception e) {
			System.out.println("InvalidPropertyException "+SystemModel.FILE+" -> "+file);
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
	public String getImageUrl() {
		return getPropertyValueString(CrawlingModel.IMAGE_URL);
	}
	public void setImageUrl(String url) {
		try {
			addProperty(CrawlingModel.IMAGE_URL, url);
		} catch(Exception e) {
			System.out.println("InvalidPropertyException "+CrawlingModel.IMAGE_URL+" -> "+url);
		}
	}
	public long getImage() {
		String image = getPropertyValueString(CrawlingModel.IMAGE);
		if(image != null && image.length() > 0) return Long.valueOf(image);
		return 0;
	}
	public void setImage(long id) {
		try {
			addProperty(CrawlingModel.IMAGE, id);
		} catch(Exception e) {
			System.out.println("InvalidPropertyException "+CrawlingModel.IMAGE+" -> "+id);
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
	public String getDisplayDate() {
		return getPropertyValueString(CrawlingModel.DISPLAY_DATE);
	}
	public void setDisplayDate(String date) {
		try {
			addProperty(CrawlingModel.DISPLAY_DATE, date);
		} catch(Exception e) {
			System.out.println("InvalidPropertyException "+CrawlingModel.DISPLAY_DATE+" -> "+date);
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
	public String getSummary() {
		String summary = getPropertyValueString(CrawlingModel.SUMMARY);
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
	public String getJournal() {
		return getPropertyValueString(CrawlingModel.JOURNAL);
	}
	public void setJournal(String journal) {
		try {
			addProperty(CrawlingModel.JOURNAL, journal);
		} catch(Exception e) {
			System.out.println("InvalidPropertyException "+CrawlingModel.JOURNAL+" -> "+journal);
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
	public boolean getViewed() {
		String viewed = getPropertyValueString(CrawlingModel.VIEWED);
		if(viewed != null && viewed.length() > 0 && viewed.equals("true")) return true;
		return false;
	}
	public void setViewed(boolean viewed) {
		try {
			addProperty(CrawlingModel.VIEWED, viewed);
		} catch(Exception e) {
			System.out.println("InvalidPropertyException "+CrawlingModel.VIEWED+" -> "+viewed);
		}
	}
	public boolean getFlagged() {
		String viewed = getPropertyValueString(CrawlingModel.FLAGGED);
		if(viewed != null && viewed.length() > 0 && viewed.equals("true")) return true;
		return false;
	}
	public void setFlagged(boolean flagged) {
		try {
			addProperty(CrawlingModel.FLAGGED, flagged);
		} catch(Exception e) {
			System.out.println("InvalidPropertyException "+CrawlingModel.FLAGGED+" -> "+flagged);
		}
	}
	public int getStatus() {
		String status = getPropertyValueString(CrawlingModel.STATUS);
		if(status != null && status.length() > 0) return Integer.valueOf(status);
		return Crawler.STATUS_PAUSED;
	}
	public void setStatus(int status) {
		try {
			addProperty(CrawlingModel.STATUS, status);
		} catch(Exception e) {
			System.out.println("InvalidPropertyException "+CrawlingModel.STATUS+" -> "+status);
		}
	}
	
	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
	}
	public String getTimezone() {
		return timezone;
	}
	public void setTimezone(String timezone) {
		this.timezone = timezone;
	}
	public byte[] getImageData() {
		return imageData;
	}
	public void setImageData(byte[] imageData) {
		this.imageData = imageData;
	}
	public byte[] getDocumentData() {
		return documentData;
	}
	public void setDocumentData(byte[] documentData) {
		this.documentData = documentData;
	}
	public Long getCrawlerId() {
		return crawlerId;
	}
	public void setCrawlerId(Long crawlerId) {
		this.crawlerId = crawlerId;
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
		builder.add("id", getId());
		if(getUid() != null) builder.add("uid", getUid());
		if(getName() != null) builder.add("name", getName());
		if(getSummary() != null) builder.add("summary", getSummary());
		builder.add("timestamp", getTimestamp());
		builder.add("status", getStatus());
		builder.add("url", getUrl());
		if(crawlerId != null) builder.add("crawler", getCrawlerId());
		if(getImageUrl() != null) builder.add("image_url", getImageUrl());
		if(getContent() != null) builder.add("content", getContent());
		//if(object.containsKey("content_type")) setContentType(object.getString("content_type"));
		//if(object.containsKey("loaded")) setLoaded(object.getBoolean("loaded"));
		//if(object.containsKey("viewed")) setViewed(object.getBoolean("viewed"));
		return builder.build();
	}
}
