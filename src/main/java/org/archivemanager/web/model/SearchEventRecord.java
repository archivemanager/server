package org.archivemanager.web.model;

import org.archivemanager.models.SearchModel;
import org.archivemanager.models.SystemModel;
import org.archivemanager.services.search.SearchResult;
import org.archivemanager.util.DateUtility;

public class SearchEventRecord {
	private String timestamp;
	private int count;
	private String ipaddress;
	private String query;
	private String explanation;
	
	
	public SearchEventRecord() {}
	public SearchEventRecord(String timestamp, int count, String ipaddress, String query, String explanation) {
		this.timestamp = timestamp;
		this.count = count;
		this.ipaddress = ipaddress;
		this.query = query;
		this.explanation = explanation;
	}
	public SearchEventRecord(SearchResult result) {
		Object timestamp = result.get(SystemModel.TIMESTAMP.toString());
		if(timestamp != null) this.timestamp = DateUtility.formatDate((Long)timestamp);
		Object query = result.get(SearchModel.QUERY.toString());
		if(query != null) this.query = (String)query;
		Object explanation = result.get(SearchModel.EXPLANATION.toString());
		if(explanation != null) this.explanation = (String)explanation;
		Object ipaddress = result.get(SystemModel.IPADDRESS.toString());
		if(ipaddress != null) this.ipaddress = (String)ipaddress;
		Object count = result.get(SystemModel.COUNT.toString());
		if(count != null) this.count = (Integer)count;
	}
		
	public String getTimestamp() {
		return timestamp;
	}
	public void setTimestamp(String timestamp) {
		this.timestamp = timestamp;
	}
	public int getCount() {
		return count;
	}
	public void setCount(int count) {
		this.count = count;
	}
	public String getIpaddress() {
		return ipaddress;
	}
	public void setIpaddress(String ipaddress) {
		this.ipaddress = ipaddress;
	}
	public String getQuery() {
		return query;
	}
	public void setQuery(String query) {
		this.query = query;
	}
	public String getExplanation() {
		return explanation;
	}
	public void setExplanation(String explanation) {
		this.explanation = explanation;
	}
	
	
}
