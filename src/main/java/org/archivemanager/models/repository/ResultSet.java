package org.archivemanager.models.repository;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class ResultSet implements Serializable {
	private static final long serialVersionUID = 7475159832192849105L;
	private String baseUrl;
	private String query;
	private String label;
	private String sort;
	private int page;
	private int pageSize = 10;
	private int pageCount;
	private int start;
	private int end;
	private int resultCount;
	private long time;
	private Result parent;
	private List<Breadcrumb> breadcrumbs = new ArrayList<Breadcrumb>();
	private List<Result> results = new ArrayList<Result>();
	private List<Attribute> attributes = new ArrayList<Attribute>();
	private List<Paging> paging = new ArrayList<Paging>();
	
	public ResultSet() {}
	
	
	public String getBaseUrl() {
		return baseUrl;
	}
	public void setBaseUrl(String baseUrl) {
		this.baseUrl = baseUrl;
	}
	public String getQuery() {
		return query;
	}
	public void setQuery(String query) {
		this.query = query;
	}
	public String getLabel() {
		return label;
	}
	public void setLabel(String label) {
		this.label = label;
	}
	public String getSort() {
		return sort;
	}
	public void setSort(String sort) {
		this.sort = sort;
	}
	public int getPage() {
		return page;
	}
	public void setPage(int page) {
		this.page = page;
	}
	public int getPageSize() {
		return pageSize;
	}
	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}
	public int getPageCount() {
		return pageCount;
	}
	public void setPageCount(int pageCount) {
		this.pageCount = pageCount;
	}
	public int getStart() {
		return start;
	}
	public void setStart(int start) {
		this.start = start;
	}
	public int getEnd() {
		return end;
	}
	public void setEnd(int end) {
		this.end = end;
	}
	public int getResultCount() {
		return resultCount;
	}
	public long getTime() {
		return time;
	}
	public void setTime(long time) {
		this.time = time;
	}
	public void setResultCount(int resultCount) {
		this.resultCount = resultCount;
	}	
	public Result getParent() {
		return parent;
	}
	public void setParent(Result parent) {
		this.parent = parent;
	}
	@JsonIgnore
	public Breadcrumb getLastBreadcrumb() {
		if(breadcrumbs != null && breadcrumbs.size() > 0) {
			return breadcrumbs.get(breadcrumbs.size() - 1);
		}
		return null;
	}
	public List<Breadcrumb> getBreadcrumbs() {
		return breadcrumbs;
	}
	public void setBreadcrumbs(List<Breadcrumb> breadcrumbs) {
		this.breadcrumbs = breadcrumbs;
	}
	public List<Result> getResults() {
		return results;
	}
	public void setResults(List<Result> results) {
		this.results = results;
	}
	public List<Attribute> getAttributes() {
		return attributes;
	}
	public void setAttributes(List<Attribute> attributes) {
		this.attributes = attributes;
	}
	public List<Paging> getPaging() {
		return paging;
	}
	public void setPaging(List<Paging> paging) {
		this.paging = paging;
	}
	
}
