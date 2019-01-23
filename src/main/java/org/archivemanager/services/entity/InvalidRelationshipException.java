package org.archivemanager.services.entity;

public class InvalidRelationshipException extends Exception {
	private static final long serialVersionUID = -1263396981484183200L;
	
	
	public InvalidRelationshipException(String msg) {
		super(msg);
	}
	public InvalidRelationshipException(String msg, Exception e) {
		super(msg, e);
	}
}
