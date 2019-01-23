package org.archivemanager.services.crawling;

import java.util.ArrayList;
import java.util.List;

import org.archivemanager.services.entity.Entity;


public class MultiPageSeed extends Seed {
	private static final long serialVersionUID = -3983642691169086698L;
	private int pageCount;
	private boolean automated;
	private String locationChangedScript;
	private List<String> urls = new ArrayList<String>();
	private List<byte[]> pages = new ArrayList<byte[]>();
	
	
	public MultiPageSeed() {
		super();
	}
	public MultiPageSeed(Entity entity) {
		super(entity);
	}
	public MultiPageSeed(String url, String name) {
		super(url, name);
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
	public boolean isAutomated() {
		return automated;
	}
	public void setAutomated(boolean automated) {
		this.automated = automated;
	}
	public List<String> getUrls() {
		return urls;
	}
	public void setUrls(List<String> urls) {
		this.urls = urls;
	}
	public List<byte[]> getPages() {
		return pages;
	}
	public void setPages(List<byte[]> pages) {
		this.pages = pages;
	}
	
}
