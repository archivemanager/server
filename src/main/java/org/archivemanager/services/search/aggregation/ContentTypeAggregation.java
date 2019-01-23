package org.archivemanager.services.search.aggregation;

import java.util.ArrayList;
import java.util.List;

import org.archivemanager.models.RepositoryModel;
import org.archivemanager.models.SystemModel;
import org.archivemanager.models.system.QName;
import org.archivemanager.services.search.SearchAttributeValue;
import org.archivemanager.services.search.SearchService;
import org.archivemanager.util.StringFormatUtility;
import org.elasticsearch.search.aggregations.AggregationBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.aggregations.bucket.terms.Terms.Bucket;

public class ContentTypeAggregation extends SearchAggregation {
	private List<QName> blacklist = new ArrayList<QName>();
	
	public ContentTypeAggregation(String name, int size, SearchService searchService) {
		super(name, size, searchService);
		blacklist.add(RepositoryModel.ITEM);
		blacklist.add(RepositoryModel.REPOSITORY_OBJECT);
		blacklist.add(SystemModel.ENTITY);
	}

	public AggregationBuilder getAggregationBuilder() {
		TermsAggregationBuilder aggregation = AggregationBuilders.terms("qname").field("qname");
		aggregation.size(50000);
		aggregation.minDocCount(1);
		return aggregation;
	}
	public void setSearchResponse(org.elasticsearch.action.search.SearchResponse searchResponse) {
		Terms aggregation = searchResponse.getAggregations().get("qname");
		for(Bucket bucket : aggregation.getBuckets()) {
		   	if(bucket.getDocCount() != size) {			    		
		   		String itemQname = bucket.getKeyAsString();
		   		QName q = new QName(itemQname);
		   		if(!blacklist.contains(q)) {
			   		String name = StringFormatUtility.toTitleCase(q.getLocalName().replace("_", " "));
					SearchAttributeValue value = new SearchAttributeValue(name);
					value.setCount((int)bucket.getDocCount());
					value.setQuery("qname:"+itemQname);
					cache.add(value);
		   		}
		   	}
		}	    
	}
}
