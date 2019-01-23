package org.archivemanager.services.content;

import java.util.List;

public interface EntityContentService extends ContentService {
	boolean hasContent(String url);
	byte[] readContent(String url);
	long writeContent(String url, byte[] content);
	long writeContent(byte[] request, byte[] response);
	long processImage(String icon, int targetHeight, int targetWidth, boolean shouldProxy);
	
	void removeContent(long id);	
	Content getContent(long id);
	Content getContent(String url);
		
	List<Content> getContentByDocument(long id);
	void associateDocumentToContent(long documentId, long contentId);
	
}
