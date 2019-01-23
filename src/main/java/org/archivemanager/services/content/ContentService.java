package org.archivemanager.services.content;


public interface ContentService {
	
	byte[] load(String url, boolean shouldProxy);
	byte[] read(long id);
	void write(long id, byte[] content);	
	void remove(long id);
	
	String getDomain(String url);
	String getUrl(String in);
	
}
