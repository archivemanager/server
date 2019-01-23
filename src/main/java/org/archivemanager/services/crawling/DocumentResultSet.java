package org.archivemanager.services.crawling;
import java.util.ArrayList;
import java.util.List;


public class DocumentResultSet {
	private String query;
	private int resultSize;
	private int startRow;
	private int endRow;
	private List<Document> results = new ArrayList<Document>();

	
	public DocumentResultSet() {}
	public DocumentResultSet(String query, int resultSize, int startRow, int endRow) {
		this.query = query;
		this.resultSize = resultSize;
		this.startRow = startRow;
		this.endRow = endRow;
	}
	
	public String getQuery() {
		return query;
	}
	public void setQuery(String query) {
		this.query = query;
	}
	public int getResultSize() {
		return resultSize;
	}
	public void setResultSize(int resultSize) {
		this.resultSize = resultSize;
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
	public List<Document> getResults() {
		return results;
	}
	public void setResults(List<Document> results) {
		this.results = results;
	}
	
}
