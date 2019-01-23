package org.archivemanager.services.dictionary;

public class DataDictionaryException extends Exception {
	private static final long serialVersionUID = -8761640056971903307L;

	
	public DataDictionaryException(String msg) {
		super(msg);
	}
	public DataDictionaryException(String msg, Exception e) {
		super(msg, e);
	}
}
