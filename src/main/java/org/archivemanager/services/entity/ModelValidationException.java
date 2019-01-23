package org.archivemanager.services.entity;




public class ModelValidationException extends Exception {
	private static final long serialVersionUID = -495213738511904461L;
	private ValidationResult result;
	
	public ModelValidationException(ValidationResult result) {
		this.result = result;
	}

	public ValidationResult getResult() {
		return result;
	}
	public void setResult(ValidationResult result) {
		this.result = result;
	}
	
}
