package org.archivemanager.services.search.aggregation;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.archivemanager.services.search.SearchAttribute;
import org.archivemanager.services.search.SearchAttributeValue;
import org.archivemanager.services.search.SearchResult;
import org.archivemanager.services.search.SearchService;
import org.elasticsearch.search.aggregations.AggregationBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.aggregations.bucket.terms.Terms.Bucket;

public class SearchAggregation {
	protected SearchService searchService;
	protected String label;
	protected String sort = "count";
	protected int startRow = 0;
	protected int endRow = 0;
	protected String name;
	protected int size;	
	protected long minDocCount = 5;		
	
	protected List<SearchAttributeValue> cache = new ArrayList<SearchAttributeValue>();
	
	
	public SearchAggregation(String name, int size, SearchService searchService) {
		this.name = name;
		this.size = size;
		this.searchService = searchService;
	}
	
	public SearchAttribute getSearchAttribute(String query, String sort, int startRow, int endRow) {
		SearchAttribute attribute = new SearchAttribute(label);
		List<SearchAttributeValue> localCache = query != null && query.length() > 0 ? 
				cache.stream().filter(v->v.getName().toLowerCase().startsWith(query.toLowerCase())).collect(Collectors.toList()) : 
					cache;
		if(sort.equals("alpha")) {
	    	Collections.sort(localCache, new Comparator<SearchAttributeValue>() {
				@Override
				public int compare(SearchAttributeValue o1, SearchAttributeValue o2) {
					return o1.getName().compareTo(o2.getName());
				}	    		
			});
	    } else {
	    	Collections.sort(localCache, new Comparator<SearchAttributeValue>() {
				@Override
				public int compare(SearchAttributeValue o1, SearchAttributeValue o2) {
					if(o1.getCount() > o2.getCount()) return -1;
					if(o1.getCount() < o2.getCount()) return 1;
					return 0;
				}	    		
			});
	    }
	    if(endRow == 0) endRow = size;
	    int resultCount = 0;
	    for(SearchAttributeValue value : localCache) {
	    	if(resultCount >= startRow && (endRow == 0 || resultCount < endRow)) {
	        	attribute.getValues().add(value);		            
        	}
	    	resultCount++;
	    }
	    return attribute;
	}
	public AggregationBuilder getAggregationBuilder() {
		TermsAggregationBuilder aggregation = AggregationBuilders.terms(getName()).field(getName());
		aggregation.size(50000);
		aggregation.minDocCount(getMinDocCount());
		return aggregation;
	}
	public void setSearchResponse(org.elasticsearch.action.search.SearchResponse searchResponse) {
		Terms aggregation = searchResponse.getAggregations().get(name);
		for(Bucket bucket : aggregation.getBuckets()) {
		   	if(bucket.getDocCount() != size) {			    		
		   		long entityId = Long.valueOf(bucket.getKeyAsString());
				SearchResult entity = searchService.search("nodes", entityId);
				if(entity != null) {
					SearchAttributeValue value = new SearchAttributeValue(entity.getName());
					String targetName = entity.getName();
					if(targetName != null) {
						value.setCount((int)bucket.getDocCount());
						value.setName(targetName);
						value.setQuery(name+":"+entity.getId());
						cache.add(value);						
					}
				}					
		   	}
		}	    
	}
	public int getTotal() {
		return cache.size();
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}	
	public int getSize() {
		return size;
	}
	public void setSize(int size) {
		this.size = size;
	}
	
	public long getMinDocCount() {
		return minDocCount;
	}
	public void setMinDocCount(long minDocCount) {
		this.minDocCount = minDocCount;
	}	
}
