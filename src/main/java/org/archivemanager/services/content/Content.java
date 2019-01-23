package org.archivemanager.services.content;

import java.net.URL;

import org.archivemanager.models.SystemModel;
import org.archivemanager.services.entity.Entity;


public class Content extends Entity {
	private static final long serialVersionUID = -2041111526151270482L;
	private byte[] content;
	
	public Content() {
		setQName(ContentModel.CONTENT);
	}
	public Content(Entity entity) {
		setProperties(entity.getProperties());
		//setSourceAssociations(entity.getSourceAssociations());
		//setTargetAssociations(entity.getTargetAssociations());
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
	protected void setProtocol(String protocol) {
		try {
			addProperty(SystemModel.PROTOCOL, protocol);
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	public String getDomain() {
		return getPropertyValueString(SystemModel.DOMAIN);
	}
	protected void setDomain(String domain) {
		//if(domain != null) domain = domain.replace("google2.", "").replace("www.", "");
		try {
			addProperty(SystemModel.DOMAIN, domain);
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	public String getPath() {
		return getPropertyValueString(SystemModel.PATH);
	}
	protected void setPath(String path) {
		try {
			addProperty(SystemModel.PATH, path);
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	public String getQuery() {
		return getPropertyValueString(SystemModel.QUERY);
	}
	protected void setQuery(String query) {
		try {
			addProperty(SystemModel.QUERY, query);
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	public byte[] getContent() {
		return content;
	}
	public void setContent(byte[] content) {
		this.content = content;
	}
	public String getContentType() {
		return getPropertyValueString(ContentModel.CONTENT_TYPE);
	}
	public void setContentType(String message) {
		try {
			addProperty(ContentModel.CONTENT_TYPE, message);
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	public long getTimestamp() {
		String timestamp = getPropertyValueString(ContentModel.TIMESTAMP);
		if(timestamp != null && timestamp.length() > 0) return Long.valueOf(timestamp);
		return 0;
	}
	public void setTimestamp(long timestamp) {
		try {
			addProperty(ContentModel.TIMESTAMP, timestamp);
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
}
