package org.archivemanager.services.entity;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

public class ValidationResult {
	private boolean valid = true;
	private int exception;
	private List<ValidationField> results = new ArrayList<ValidationField>();
	private String message;
	
	public static final Integer MODEL_MISSING = 1;
	public static final Integer MISSING_VALUE = 2;
	
	public ValidationResult() {}
	public ValidationResult(boolean valid) {
		this.valid = valid;
	}
	public boolean isValid() {
		return valid;
	}
	public void setValid(boolean valid) {
		this.valid = valid;
	}
	public int getException() {
		return exception;
	}
	public void setException(int exception) {
		this.exception = exception;
		this.valid = false;
	}
	public List<ValidationField> getResults() {
		return results;
	}
	public void setResults(List<ValidationField> results) {
		this.results = results;
	}
	public void addValidationField(ValidationField field) {
		this.valid = false;
		results.add(field);
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	@Override
	public String toString() {
		StringWriter buff = new StringWriter();
		buff.append("Validation Result : "+message+" Exception : "+exception);
		return buff.toString();
	}
}
