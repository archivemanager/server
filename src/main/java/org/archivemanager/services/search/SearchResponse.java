package org.archivemanager.services.search;
import java.util.ArrayList;
import java.util.List;

import org.archivemanager.services.search.SearchNode;
import org.archivemanager.services.search.Token;


public class SearchResponse {
	private String searchId;
	private int resultSize;
	private int startRow;
	private int endRow;		
	private float topScore;
	private String parse;
	private String explanation;
	private long time;
	private List<SearchResult> results = new ArrayList<SearchResult>();
	private List<SearchAttribute> attributes = new ArrayList<SearchAttribute>();
	private List<SearchNode> breadcrumb;
	private List<Token> tokens;
	
	public SearchAttribute getAttribute(String name) {
		for(SearchAttribute attribute : attributes) {
			if(attribute.getName().equals(name))
				return attribute;
		}
		return null;
	}
	
	public String getSearchId() {
		return searchId;
	}
	public void setSearchId(String searchId) {
		this.searchId = searchId;
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
	public long getTime() {
		return time;
	}
	public void setTime(long time) {
		this.time = time;
	}
	public String getParse() {
		return parse;
	}
	public void setParse(String parse) {
		this.parse = parse;
	}
	public String getExplanation() {
		return explanation;
	}
	public void setExplanation(String explanation) {
		this.explanation = explanation;
	}
	public SearchResult getResult(int index) {
		return results.get(index);
	}
	public List<SearchResult> getResults() {
		return results;
	}
	public void setResults(List<SearchResult> results) {
		this.results = results;
	}
	public List<SearchAttribute> getAttributes() {
		return attributes;
	}
	public void setAttributes(List<SearchAttribute> attributes) {
		this.attributes = attributes;
	}
	public SearchAttribute findAttribute(String name) {
		for(int i=0; i < attributes.size(); i++) {
			if(attributes.get(i).getName().equals(name)) return attributes.get(i);
		}
		SearchAttribute att = new SearchAttribute(name);
		attributes.add(att);
		return att;
	}
	public List<SearchNode> getBreadcrumb() {
		return breadcrumb;
	}
	public void setBreadcrumb(List<SearchNode> breadcrumb) {
		this.breadcrumb = breadcrumb;
	}
	public List<Token> getTokens() {
		return tokens;
	}
	public void setTokens(List<Token> tokens) {
		this.tokens = tokens;
	}
	public float getTopScore() {
		return topScore;
	}
	public void setTopScore(float topScore) {
		this.topScore = topScore;
	}
	
}
