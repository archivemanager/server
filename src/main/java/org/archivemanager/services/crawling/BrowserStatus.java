package org.archivemanager.services.crawling;

public class BrowserStatus {
	private int status;
	private String url;
	private boolean isLoading;
	private boolean canGoBack;
	private boolean canGoForward;
	
	public static final int LOADING = 114;
	public static final int LOADED = 115;
	public static final int RELOAD = 116;
	public static final int BACKWARD = 117;
	public static final int FORWARD = 118;
	
	
	public BrowserStatus() {}
	public BrowserStatus(int status, String url, boolean isLoading, boolean canGoBack, boolean canGoForward) {
		this.status = status;
		this.url = url;
		this.isLoading = isLoading;
		this.canGoBack = canGoBack;
		this.canGoForward = canGoForward;
	}
	
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public boolean isLoading() {
		return isLoading;
	}
	public void setLoading(boolean isLoading) {
		this.isLoading = isLoading;
	}
	public boolean isCanGoBack() {
		return canGoBack;
	}
	public void setCanGoBack(boolean canGoBack) {
		this.canGoBack = canGoBack;
	}
	public boolean isCanGoForward() {
		return canGoForward;
	}
	public void setCanGoForward(boolean canGoForward) {
		this.canGoForward = canGoForward;
	}	
	
}
