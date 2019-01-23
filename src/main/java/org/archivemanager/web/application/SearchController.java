package org.archivemanager.web.application;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.archivemanager.data.RestResponse;
import org.archivemanager.data.Sort;
import org.archivemanager.models.SearchModel;
import org.archivemanager.models.SystemModel;
import org.archivemanager.models.binders.RepositoryModelSearchBinder;
import org.archivemanager.models.repository.Result;
import org.archivemanager.services.search.SearchRequest;
import org.archivemanager.services.search.SearchResponse;
import org.archivemanager.services.search.SearchResult;
import org.archivemanager.services.search.SearchService;
import org.archivemanager.util.DateUtility;
import org.archivemanager.web.model.SearchEventRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;


@Controller
@RequestMapping("/app/search")
public class SearchController {
	@Autowired private SearchService searchService;
	@Autowired private RepositoryModelSearchBinder binder;
	
	
	@RequestMapping(value = "", method = RequestMethod.GET)
	public String getApplication(final Model model, HttpServletRequest request, HttpServletResponse resp) {		
		return "search/search";
	}	
	
	@RequestMapping(value = "/embedded", method = RequestMethod.GET)
	public String getEmbedded(final Model model, HttpServletRequest request, HttpServletResponse resp) {		
		return "search/include/results";
	}
	
	@RequestMapping(value = "/logs", method = RequestMethod.GET)
	public String getLogs(final Model model, HttpServletRequest request, HttpServletResponse resp) {		
		return "search/logs";
	}
	
	@ResponseBody
	@RequestMapping(value="/detail.json")
	public Result detail(@RequestParam long id) throws Exception {
		SearchResult result = searchService.search("nodes", id);
		Result entity = binder.getResult(result);
		return entity;
	}
	
	/** Services **/
	@ResponseBody
	@RequestMapping(value="/logs/searches.json")
	public RestResponse<SearchEventRecord> getSearches(@RequestParam(required=false) String query, @RequestParam(required=false, defaultValue="1") int page, 
			@RequestParam(required=false, defaultValue="20") int rows) throws Exception {
		RestResponse<SearchEventRecord> data = new RestResponse<SearchEventRecord>();		
		int start = (page*rows) -  rows;
		int end = page*rows;
		data.setStartRow(start);
		data.setEndRow(end);
		SearchRequest request = new SearchRequest(SearchModel.QUERY_EVENT);
		request.setStartRow(start);
		request.setEndRow(end);
		request.addSort(new Sort("long", "openapps_org_system_1_0_timestamp", "desc"));
		SearchResponse response = searchService.search(request);
		for(SearchResult result : response.getResults()) {
			SearchEventRecord event = new SearchEventRecord(result);
			data.addRow(event);
		}
		data.setTotal(response.getResultSize());
		return data;
	}
	@ResponseBody
	@RequestMapping(value = "/logs/search_summary.json", method = RequestMethod.GET)
	public List<SearchEventRecord> getSearchSummary(@RequestParam(required=false) String date) throws Exception {
		Map<String, Integer> data = new HashMap<String, Integer>();
		List<SearchEventRecord> records = new ArrayList<SearchEventRecord>();
		
		SearchRequest request = new SearchRequest(SearchModel.QUERY_EVENT);
		request.setStartRow(0);
		request.setEndRow(1000);
		request.addSort(new Sort("long", "openapps_org_system_1_0_timestamp", "asc"));
		SearchResponse response = searchService.search(request);
		for(SearchResult result : response.getResults()) {
			Long timestamp = (Long)result.getData().get(SystemModel.TIMESTAMP.toString());
			String label = DateUtility.formatDate((Long)timestamp, "d MMM");
			Integer count = data.get(label);
			if(count == null) {
				count = new Integer(0);
			}
			data.put(label, count+1);
		}
		for(String timestamp : data.keySet()) {
			records.add(new SearchEventRecord(timestamp, data.get(timestamp), "", "", ""));
		}		
		return records.stream().sorted(new Comparator<SearchEventRecord>() {
			@Override
			public int compare(SearchEventRecord o1, SearchEventRecord o2) {
				return o1.getTimestamp().compareTo(o2.getTimestamp());
			}			
		}).collect(Collectors.toList());
	}
	@ResponseBody
	@RequestMapping(value="/logs/views.json")
	public RestResponse<SearchEventRecord> getViews(@RequestParam(required=false) String query, @RequestParam(required=false, defaultValue="1") int page, 
			@RequestParam(required=false, defaultValue="20") int size) throws Exception {
		RestResponse<SearchEventRecord> data = new RestResponse<SearchEventRecord>();		
		int start = (page*size) -  size;
		int end = page*size;
		data.setStartRow(start);
		data.setEndRow(end);
		SearchRequest request = new SearchRequest(SearchModel.VIEW_EVENT);
		request.setStartRow(start);
		request.setEndRow(end);
		request.addSort(new Sort("long", "openapps_org_system_1_0_timestamp", "asc"));
		SearchResponse response = searchService.search(request);
		for(SearchResult result : response.getResults()) {
			SearchEventRecord event = new SearchEventRecord(result);
			data.addRow(event);
		}
		data.setTotal(response.getResultSize());
		return data;
	}
	@ResponseBody
	@RequestMapping(value = "/logs/view_summary.json", method = RequestMethod.GET)
	public List<SearchEventRecord> getViewSummary(@RequestParam(required=false) String date) throws Exception {
		Map<String, Integer> data = new HashMap<String, Integer>();
		List<SearchEventRecord> records = new ArrayList<SearchEventRecord>();
		
		SearchRequest request = new SearchRequest(SearchModel.VIEW_EVENT);
		request.setStartRow(0);
		request.setEndRow(1000);
		request.addSort(new Sort("long", "openapps_org_system_1_0_timestamp", "asc"));
		SearchResponse response = searchService.search(request);
		for(SearchResult result : response.getResults()) {
			Long timestamp = (Long)result.getData().get(SystemModel.TIMESTAMP.toString());
			String label = DateUtility.formatDate((Long)timestamp, "d MMM");
			Integer count = data.get(label);
			if(count == null) {
				count = new Integer(0);
			}
			data.put(label, count+1);
		}
		for(String timestamp : data.keySet()) {
			records.add(new SearchEventRecord(timestamp, data.get(timestamp), "", "", ""));
		}		
		return records.stream().sorted(new Comparator<SearchEventRecord>() {
			@Override
			public int compare(SearchEventRecord o1, SearchEventRecord o2) {
				return o1.getTimestamp().compareTo(o2.getTimestamp());
			}			
		}).collect(Collectors.toList());
	}
}
