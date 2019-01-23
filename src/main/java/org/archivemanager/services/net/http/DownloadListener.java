package org.archivemanager.services.net.http;

public interface DownloadListener {
	
	void update(int current, int total);
	
}
