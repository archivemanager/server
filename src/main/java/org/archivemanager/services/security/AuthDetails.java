package org.archivemanager.services.security;

import java.io.Serializable;


public class AuthDetails implements Serializable {
	private static final long serialVersionUID = -5119677972070986397L;
	private String id;
	private long issued_at;
	private String instance_url;
	private String signature;
	private String access_token;
	private String error;
	private String error_description;
	private int errorCount;
	
	public AuthDetails() {
		
	}
	
	public String getError() {
		return error;
	}
	public void setError(String error) {
		this.error = error;
	}
	public String getError_description() {
		return error_description;
	}
	public void setError_description(String error_description) {
		this.error_description = error_description;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public long getIssued_at() {
		return issued_at;
	}
	public void setIssued_at(long issued_at) {
		this.issued_at = issued_at;
	}
	public String getInstance_url() {
		return instance_url;
	}
	public void setInstance_url(String instance_url) {
		this.instance_url = instance_url;
	}
	public String getSignature() {
		return signature;
	}
	public void setSignature(String signature) {
		this.signature = signature;
	}
	public String getAccess_token() {
		return access_token;
	}
	public void setAccess_token(String access_token) {
		this.access_token = access_token;
	}
	public int getErrorCount() {
		return errorCount;
	}
	public void setErrorCount(int errorCount) {
		this.errorCount = errorCount;
	}
	public boolean isLoggedIn() {
		//return !Strings.isNullOrEmpty(access_token) && Strings.isNullOrEmpty(error);
		return access_token != null && access_token.length() > 0 && (error == null || error.length() == 0);
	}

	@Override
	public String toString() {
		return "AuthDetails [id=" + id + ", issued_at=" + issued_at
				+ ", instance_url=" + instance_url + ", signature=" + signature
				+ ", access_token=" + access_token + ", error=" + error
				+ ", error_description=" + error_description + "]";
	}
	
}
