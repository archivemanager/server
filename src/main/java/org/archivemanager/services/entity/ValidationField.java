package org.archivemanager.services.entity;

public class ValidationField {
	private String field;
	private int exception;
		
	public static final Integer REQUIRED_VALUE_MISSING = 1;
	public static final Integer MINIMUM_VALUE_RESTRICTION = 2;
	public static final Integer MAXIMUM_VALUE_RESTRICTION = 3;
	public static final Integer WRONG_DATATYPE_RESTRICTION = 4;
		
	public ValidationField() {}
	public ValidationField(String field, int exception) {
		this.field = field;
		this.exception = exception;
	}
	
	public String getField() {
		return field;
	}
	public void setField(String field) {
		this.field = field;
	}
	public int getException() {
		return exception;
	}
	public void setException(int exception) {
		this.exception = exception;
	}
		
}
