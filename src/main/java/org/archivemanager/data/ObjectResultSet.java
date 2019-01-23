package org.archivemanager.data;
import java.util.ArrayList;
import java.util.List;


public class ObjectResultSet<T> {
	private int resultSize;
	private int startRow;
	private int endRow;
	private List<T> results = new ArrayList<T>();

	
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
	public List<T> getResults() {
		return results;
	}
	public void setResults(List<T> results) {
		this.results = results;
	}
	public Object getResult(int index) {
		return results.get(index);
	}
	
}
