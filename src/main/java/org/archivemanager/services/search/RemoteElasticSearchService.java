package org.archivemanager.services.search;
import static org.elasticsearch.index.query.QueryBuilders.boolQuery;
import static org.elasticsearch.index.query.QueryBuilders.termQuery;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.archivemanager.data.Sort;
import org.archivemanager.models.SearchModel;
import org.archivemanager.models.SystemModel;
import org.archivemanager.models.system.QName;
import org.archivemanager.services.dictionary.DataDictionaryService;
import org.archivemanager.services.search.aggregation.SearchAggregation;
import org.archivemanager.services.search.parsing.QueryParser;
import org.archivemanager.util.NumberUtility;
import org.elasticsearch.action.search.ClearScrollRequest;
import org.elasticsearch.action.search.SearchScrollRequest;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.aggregations.AggregationBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.FieldSortBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class RemoteElasticSearchService implements SearchService {
	private static final long serialVersionUID = 586282630873987158L;
	private final static Logger log = Logger.getLogger(RemoteElasticSearchService.class.getName());	
	@Autowired private DataDictionaryService dictionaryService;
	@Autowired private QueryParser searchParser;
	@Autowired private RestHighLevelClient client;
	@Autowired private List<SearchPlugin> plugins = new ArrayList<SearchPlugin>();
					
	public static final int MAX_RESULTS = 999999;
	
	
	@Override
	public int count(SearchRequest query) {
		String index = isEvent(query.getQnames()) ? "events" : "nodes";
		if(query.getRelationships().size() > 0) index = "associations";		
		org.elasticsearch.action.search.SearchRequest searchRequest = new org.elasticsearch.action.search.SearchRequest(index);		
		query = searchParser.parse(query);
		QueryBuilder queryBuilder = (QueryBuilder)query.getNativeQuery();
		SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();		
		searchSourceBuilder.size(0);
		searchSourceBuilder.query(queryBuilder);			
		searchRequest.source(searchSourceBuilder);
		try {			
			org.elasticsearch.action.search.SearchResponse searchResponse = client.search(searchRequest);
			return (int)searchResponse.getHits().totalHits;
		} catch(Exception e) {
			log.log(Level.SEVERE, "error searching ", e);
		}
		return 0;
	}
	@Override
	public SearchResponse search(SearchRequest query) {
		SearchResponse response = new SearchResponse();
		if(query.getNodeId() != null) {
			SearchResult result = search(query.getIndex(), query.getNodeId());
			response.getResults().add(result);
			return response;
		}			
		//long startTime = System.currentTimeMillis();
		int start = query.getStartRow();
		int end = query.getEndRow();
		int size = query.getEndRow() - query.getStartRow();
		
		if(query.getIndex() == null) {
			String index = isEvent(query.getQnames()) ? "events" : "nodes";
			if(query.getRelationships().size() > 0) index = "associations";
			query.setIndex(index);
		}
		
		org.elasticsearch.action.search.SearchRequest searchRequest = new org.elasticsearch.action.search.SearchRequest(query.getIndex());
		searchRequest.scroll(TimeValue.timeValueSeconds(1000));
		SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();		
		if(size > 0) searchSourceBuilder.size(size);
		//searchSourceBuilder.from(start);
		
		if(query.getSorts() != null && query.getSorts().size() > 0) {
	    	for(int i=0; i < query.getSorts().size(); i++) {
				Sort s = query.getSorts().get(i);
				SortOrder order = (s.getDirection() == null || s.getDirection().equals("asc")) ? SortOrder.ASC : SortOrder.DESC;
				if(s.getType().equals("long"))
					searchSourceBuilder.sort(new FieldSortBuilder(s.getField()).order(order).unmappedType("long").missing("_last"));
				else if(s.getType().equals("integer")) 
					searchSourceBuilder.sort(new FieldSortBuilder(s.getField()+".keyword").order(order).unmappedType("integer").missing("_last"));
				else 
					searchSourceBuilder.sort(new FieldSortBuilder(s.getField()+".keyword").order(order).unmappedType("text").missing("_last"));
	    	}
		} else if(query.getIndex().equals("nodes")){
			SortOrder order = SortOrder.ASC;
			searchSourceBuilder.sort(new FieldSortBuilder(SystemModel.NAME.toString()+".keyword").order(order).unmappedType("text").missing("_last"));
		}
		
		if(query.getAggregations().size() > 0) {	
			for(SearchAggregation aggregation : query.getAggregations()) {
				AggregationBuilder qnameAggregation = aggregation.getAggregationBuilder();
				searchSourceBuilder.aggregation(qnameAggregation);
			}
		}		
		try {
			query = searchParser.parse(query);
			QueryBuilder queryBuilder = (QueryBuilder)query.getNativeQuery();
			searchSourceBuilder.query(queryBuilder);			
			searchRequest.source(searchSourceBuilder);
						
			org.elasticsearch.action.search.SearchResponse searchResponse = client.search(searchRequest);
			response.setSearchId(searchResponse.getScrollId());
			response.setParse(query.getParse());
			response.setExplanation(queryBuilder.toString().replace("\n", "").replace("  ", ""));
			
			boolean moreResultsExist = true;
		    int resultCount = 0;
		    while(moreResultsExist) {
		        String scrollId = searchResponse.getScrollId();
		        for (SearchHit hit : searchResponse.getHits()) {
		        	if(resultCount >= start && (end == 0 || resultCount < end)) {
			        	String id = hit.getId();			        	
						if(id != null) {	
							SearchResult result = getSearchResult(query.getIndex(), id, hit.getSourceAsMap());
							if(result != null) 
								response.getResults().add(result);
							else 
								log.log(Level.SEVERE, "no search result returned for id:"+id);
						} else 
							log.log(Level.SEVERE, "no entity returned for id:"+id);			            
		        	}
		        	resultCount++;
		        }
		        if(resultCount >= searchResponse.getHits().getTotalHits() || (end > 0 && resultCount >= end)) {
		            moreResultsExist = false;
		            ClearScrollRequest request = new ClearScrollRequest();
		            request.addScrollId(scrollId);
		            client.clearScroll(request);
		            
		            response.setResultSize((int)searchResponse.getHits().getTotalHits());
					response.setStartRow(start);
					response.setEndRow(end);
					for(SearchPlugin plugin : plugins) {
						plugin.response(query, response);
					}					
		            break;
		        }
		        SearchScrollRequest scrollRequest = new SearchScrollRequest(scrollId);
		        scrollRequest.scroll(TimeValue.timeValueSeconds(1000));
		        searchResponse = client.searchScroll(scrollRequest);
		    }
		    if(query.getAggregations().size() > 0 && searchResponse.getAggregations() != null) {
		    	for(SearchAggregation aggregation : query.getAggregations()) {
		    		aggregation.setSearchResponse(searchResponse);
		    		SearchAttribute attribute = aggregation.getSearchAttribute(null, "numeric", 0, 20);
		    		response.getAttributes().add(attribute);
		    	}			   		
		    }
		    log.info(response.getResultSize()+" results for "+response.getExplanation());
		} catch(Exception e) {
			log.log(Level.SEVERE, "error searching ", e);
		}		
		return response;
	}
	
	
	public List<SearchResult> searchAssociations(long id) {
		List<SearchResult>  results = new ArrayList<SearchResult>();
		try {
			org.elasticsearch.action.search.SearchRequest searchRequest = new org.elasticsearch.action.search.SearchRequest("associations");
			searchRequest.scroll(TimeValue.timeValueSeconds(1000));
			BoolQueryBuilder query = boolQuery();
			query.should(termQuery("source", id));
			query.should(termQuery("target", id));
			SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder(); 
			searchSourceBuilder.query(query); 
			searchRequest.source(searchSourceBuilder);
			org.elasticsearch.action.search.SearchResponse searchResponse = client.search(searchRequest);
			for(SearchHit hit : searchResponse.getHits()) {
				SearchResult result = new SearchResult(Long.valueOf(hit.getId()));				
				result.setSource(NumberUtility.getLong(hit.getSourceAsMap().get("source")));
				result.setTarget(NumberUtility.getLong(hit.getSourceAsMap().get("target")));				
				for(String key : hit.getSourceAsMap().keySet()) {
					Object obj = hit.getSourceAsMap().get(key);
					if(obj != null && !obj.equals("null")) {
						if(key.equals("qname")) result.setQName(new QName((String)obj));
						else if(key.equals("source"))result.setSource(NumberUtility.getLong(obj));
						else if(key.equals("target")) result.setTarget(NumberUtility.getLong(obj));
						else result.getData().put(key, hit.getSourceAsMap().get(key));
					}
				}
			}			
		} catch(Exception e) {
			log.log(Level.SEVERE, "", e);
		}
		return results;
	}
	public List<SearchResult> searchAssociations(long id, QName... qnames) {
		List<SearchResult>  results = new ArrayList<SearchResult>();
		try {
			org.elasticsearch.action.search.SearchRequest searchRequest = new org.elasticsearch.action.search.SearchRequest("associations");
			searchRequest.scroll(TimeValue.timeValueSeconds(1000));
			BoolQueryBuilder query = boolQuery();
			query.must(termQuery("source", id));
			//query.should(termQuery("target", id));
			if(qnames.length == 1) {
				query.must(termQuery("qname", qnames[0].toString()));
			} else {
				BoolQueryBuilder qnameQuery = boolQuery();
				for(QName q : qnames) {
					qnameQuery.should(termQuery("qname", q.toString()));
				}
				query.must(qnameQuery);
			}
			SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder(); 
			searchSourceBuilder.query(query); 
			searchRequest.source(searchSourceBuilder);
			org.elasticsearch.action.search.SearchResponse searchResponse = client.search(searchRequest);
			for(SearchHit hit : searchResponse.getHits()) {
				SearchResult result = new SearchResult(Long.valueOf(hit.getId()));
				result.setQName(new QName((String)hit.getSourceAsMap().get("qname")));
				result.setSource(NumberUtility.getLong(hit.getSourceAsMap().get("source")));
				result.setTarget(NumberUtility.getLong(hit.getSourceAsMap().get("target")));
				result.setData(hit.getSourceAsMap());				
				results.add(result);
			}			
		} catch(Exception e) {
			log.log(Level.SEVERE, "", e);
		}
		return results;
	}
	public SearchResponse searchAssociations(SearchRequest request) {
		SearchResponse response = new SearchResponse();
		try {
			int start = request.getStartRow();
			int end = request.getEndRow();
			org.elasticsearch.action.search.SearchRequest searchRequest = new org.elasticsearch.action.search.SearchRequest("associations");
			searchRequest.scroll(TimeValue.timeValueSeconds(1000));
			BoolQueryBuilder query = boolQuery();
			query.must(termQuery("targetId", request.getId()));
			//query.should(termQuery("target", id));
			if(request.getQnames().size() == 1) {
				query.must(termQuery("qname", request.getQnames().get(0).toString()));
			} else {
				BoolQueryBuilder qnameQuery = boolQuery();
				for(QName q : request.getQnames()) {
					qnameQuery.should(termQuery("qname", q.toString()));
				}
				query.must(qnameQuery);
			}
			SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder(); 
			searchSourceBuilder.query(query); 
			searchRequest.source(searchSourceBuilder);
			org.elasticsearch.action.search.SearchResponse searchResponse = client.search(searchRequest);
			
			boolean moreResultsExist = true;
		    int resultCount = 0;
		    while(moreResultsExist) {
		        String scrollId = searchResponse.getScrollId();
		        for (SearchHit hit : searchResponse.getHits()) {
		        	if(resultCount >= start && (end == 0 || resultCount < end)) {
			        	String id = hit.getId();			        	
						if(id != null) {	
							SearchResult result = new SearchResult(Long.valueOf(hit.getId()));
							result.setQName(new QName((String)hit.getSourceAsMap().get("qname")));
							result.setSource(NumberUtility.getLong(hit.getSourceAsMap().get("sourceId")));
							result.setTarget(NumberUtility.getLong(hit.getSourceAsMap().get("targetId")));
							result.setData(hit.getSourceAsMap());				
							response.getResults().add(result);
						} else log.log(Level.SEVERE, "no entity returned for id:"+id);			            
		        	}
		        	resultCount++;
		        }
		        if(resultCount >= searchResponse.getHits().getTotalHits() || (end > 0 && resultCount >= end)) {
		            moreResultsExist = false;
		            ClearScrollRequest scrollRequest = new ClearScrollRequest();
		            scrollRequest.addScrollId(scrollId);
		            client.clearScroll(scrollRequest);
		            
		            response.setResultSize((int)searchResponse.getHits().getTotalHits());
					response.setStartRow(start);
					response.setEndRow(end);
		            break;
		        }
		        SearchScrollRequest scrollRequest = new SearchScrollRequest(scrollId);
		        scrollRequest.scroll(TimeValue.timeValueSeconds(1000));
		        searchResponse = client.searchScroll(scrollRequest);
		    }			
		} catch(Exception e) {
			log.log(Level.SEVERE, "", e);
		}		
		return response;
	}
		
	public SearchResult search(String index, long id) {
		SearchResult result = null;
		try {
			org.elasticsearch.action.search.SearchRequest searchRequest = new org.elasticsearch.action.search.SearchRequest(index);
			SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
			searchSourceBuilder.query(QueryBuilders.idsQuery().addIds(String.valueOf(id)));	
			searchRequest.source(searchSourceBuilder);
			
			org.elasticsearch.action.search.SearchResponse searchResponse = client.search(searchRequest);
			for (SearchHit hit : searchResponse.getHits()) {
	        	result = getSearchResult(index, hit.getId(), hit.getSourceAsMap());
	        	break;
	        }
		} catch(Exception e) {
			log.log(Level.SEVERE, "", e);
		}
		return result;
	}
	@SuppressWarnings("unchecked")
	protected SearchResult getSearchResult(String index, String id, Map<String, Object> data) {
		SearchResult result = NumberUtility.isLong(id) ? new SearchResult(Long.valueOf(id)) : new SearchResult();
		for(String key : data.keySet()) {
			Object obj = data.get(key);
			if(obj != null && !obj.equals("null")) {
				if(key.equals("qname")) {
					Object val = data.get(key);
					if(val instanceof String) 
						result.setQName(new QName((String)val));
					else if(val instanceof List) {
						List<String> list = (List<String>)val;
						if(list.size() > 0)
							result.setQName(new QName((String)list.get(0)));
					}					
				} else if(key.equals(SystemModel.NAME.toString())) {
					result.setName((String)data.get(key));
					result.getData().put(key, data.get(key));
				} else if(key.equals(SystemModel.TARGET.toString())) {
					result.setTarget(Long.valueOf((Integer)data.get(key)));
				} else if(key.equals(SystemModel.SOURCE.toString())) {
					result.setSource(Long.valueOf((Integer)data.get(key)));
				} else {
					result.getData().put(key, data.get(key));
				}
			}
		}
		return result;
	}
			
	public List<SearchPlugin> getPlugins() {
		return plugins;
	}
	public void setPlugins(List<SearchPlugin> plugins) {
		this.plugins = plugins;
	}
		
	protected boolean isEvent(List<QName> qnames) {
		for(QName qname : qnames) {
			if(dictionaryService.isA(qname, SearchModel.EVENT))
				return true;
		}
		return false;
	}
		
}