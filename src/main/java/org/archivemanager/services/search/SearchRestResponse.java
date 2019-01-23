package org.archivemanager.services.search;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


public class SearchRestResponse<T> implements Serializable {
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
	private List<SearchAttribute> attributes = new ArrayList<SearchAttribute>();
	private List<SearchNode> breadcrumb = new ArrayList<SearchNode>();
	
	public SearchRestResponse() {}
	public SearchRestResponse(String operation, String message) {
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
	public void setTotal(int total) {
		this.total = total;
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
	public void setRows(List<T> rows) {
		this.rows = rows;
	}
	public void addRow(T rows) {
		this.rows.add(rows);
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
	public List<SearchAttribute> getAttributes() {
		return attributes;
	}
	public void setAttributes(List<SearchAttribute> attributes) {
		this.attributes = attributes;
	}
	public List<SearchNode> getBreadcrumb() {
		return breadcrumb;
	}
	public void setBreadcrumb(List<SearchNode> breadcrumb) {
		this.breadcrumb = breadcrumb;
	}
	
}