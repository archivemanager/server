package org.archivemanager.data;

import java.util.ArrayList;
import java.util.List;
import java.io.Serializable;

public class RestResponse<T> implements Serializable {
	private static final long serialVersionUID = 2715290179971751910L;
	private int status;
	private int startRow;
	private int endRow;
	private int total;
	private long time;
	private String query;
	private String explanation;
	private String operation = "";
	private List<String> messages = new ArrayList<String>();
	private List<String> errors = new ArrayList<String>();
	private List<T> rows = new ArrayList<T>();

	public RestResponse() {}
	public RestResponse(String operation, String message) {
		this.operation = operation;
		if(operation.equals("error")) errors.add(message);
		else if(operation.equals("message")) messages.add(message);
	}

	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	public int getStartRow() {
		return startRow;
	}
	public void setStartRow(int startRow) {
		this.startRow = startRow;
	}
	public int getEndRow() {
		return endRow;
	}
	public void setEndRow(int endRow) {
		this.endRow = endRow;
	}
	public int getTotal() {
		return total;
	}
	public void setTotal(int totalRows) {
		this.total = totalRows;
	}
	public String getOperation() {
		return operation;
	}
	public void setOperation(String operation) {
		this.operation = operation;
	}
	public List<String> getMessages() {
		return messages;
	}
	public void setMessages(List<String> messages) {
		this.messages = messages;
	}
	public void addMessage(String message) {
		messages.add(message);
	}
	public List<String> getErrors() {
		return errors;
	}
	public void setErrors(List<String> errors) {
		this.errors = errors;
	}
	public void addError(String error) {
		errors.add(error);
	}	
	public List<T> getRows() {
		return rows;
	}
	public void setRows(List<T> data) {
		this.rows = data;
	}
	public void addRow(T data) {
		this.rows.add(data);
	}
	public String getQuery() {
		return query;
	}
	public void setQuery(String query) {
		this.query = query;
	}
	public String getExplanation() {
		return explanation;
	}
	public void setExplanation(String explanation) {
		this.explanation = explanation;
	}
	public long getTime() {
		return time;
	}
	public void setTime(long time) {
		this.time = time;
	}
}
