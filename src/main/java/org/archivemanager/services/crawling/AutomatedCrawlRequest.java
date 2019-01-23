package org.archivemanager.services.crawling;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

public class AutomatedCrawlRequest {
	private String url;
	private int pageCount;
	private String locationChangedScript;
	private List<byte[]> pages = new ArrayList<byte[]>();
	private CountDownLatch latch = new CountDownLatch(1);
	
	public AutomatedCrawlRequest() {}
	public AutomatedCrawlRequest(String url, String locationChangedScript, int pageCount) {
		this.url = url;
		this.locationChangedScript = locationChangedScript;
		this.pageCount = pageCount;
	}
	
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getLocationChangedScript() {
		return locationChangedScript;
	}
	public void setLocationChangedScript(String locationChangedScript) {
		this.locationChangedScript = locationChangedScript;
	}
	public int getPageCount() {
		return pageCount;
	}
	public void setPageCount(int pageCount) {
		this.pageCount = pageCount;
	}
	public List<byte[]> getPages() {
		return pages;
	}
	public void setPages(List<byte[]> pages) {
		this.pages = pages;
	}
	public CountDownLatch getLatch() {
		return latch;
	}
	
}
