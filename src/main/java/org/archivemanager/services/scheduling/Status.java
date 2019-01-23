package org.archivemanager.services.scheduling;

import java.io.Serializable;


public class Status implements Serializable {
	private static final long serialVersionUID = -4797470361403491914L;
	private long id;
	private String uid;
	private int state;
	private int completion;
	 
	private String message;
	
	
	public Status() {
		
	}
	
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public String getUid() {
		return uid;
	}
	public void setUid(String uid) {
		this.uid = uid;
	}
	public int getState() {
		return state;
	}
	public void setState(int state) {
		this.state = state;
	}
	public int getCompletion() {
		return completion;
	}
	public void setCompletion(int completion) {
		this.completion = completion;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
			
}
