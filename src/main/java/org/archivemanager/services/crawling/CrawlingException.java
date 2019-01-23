package org.archivemanager.services.crawling;

public class CrawlingException extends Exception {
	private static final long serialVersionUID = -6102012018775372018L;

	
	public CrawlingException(final String message) {
		super(message);
	}
	
	public CrawlingException(final String message, final Throwable e) {
		super(message, e);
	}
}
