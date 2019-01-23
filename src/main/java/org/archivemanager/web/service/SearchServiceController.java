package org.archivemanager.web.service;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.archivemanager.data.RestResponse;
import org.archivemanager.data.Sort;
import org.archivemanager.models.ClassificationModel;
import org.archivemanager.models.RepositoryModel;
import org.archivemanager.models.SystemModel;
import org.archivemanager.models.dictionary.ModelField;
import org.archivemanager.models.system.QName;
import org.archivemanager.models.system.User;
import org.archivemanager.services.entity.Entity;
import org.archivemanager.services.search.FilterRule;
import org.archivemanager.services.search.SearchAttribute;
import org.archivemanager.services.search.SearchAttributeValue;
import org.archivemanager.services.search.SearchRequest;
import org.archivemanager.services.search.SearchResponse;
import org.archivemanager.services.search.SearchRestResponse;
import org.archivemanager.services.search.SearchResult;
import org.archivemanager.services.search.aggregation.ContentTypeAggregation;
import org.archivemanager.services.search.aggregation.SearchAggregation;
import org.archivemanager.services.security.GuestUser;
import org.archivemanager.util.NumberUtility;
import org.archivemanager.util.StringFormatUtility;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/service/search")
public class SearchServiceController extends WebserviceSupport {
	private final static Logger log = Logger.getLogger(SearchServiceController.class.getName());
	
	@CrossOrigin
	@ResponseBody
	@RequestMapping(value="/results.json")
	public SearchRestResponse<Map<String,Object>> search(
			@RequestParam(required=false) String query, 
			@RequestParam(required=false) QName qname, 
			@RequestParam(required=false, defaultValue="0") int page, 
			@RequestParam(required=false, defaultValue="20") int rows,
			@RequestParam(required=false, defaultValue="0") int start, 
			@RequestParam(required=false, defaultValue="0") int end,
			HttpServletRequest request, HttpServletResponse response) throws Exception {
		SearchRestResponse<Map<String,Object>> data = new SearchRestResponse<Map<String,Object>>();
		if(qname == null) qname = RepositoryModel.ITEM;
		SearchRequest entityQuery = new SearchRequest(qname, query);
		if(end > 0) {
			entityQuery.setStartRow(start);
			entityQuery.setEndRow(end);
		} else {
			entityQuery.setStartRow((page * rows)-rows);
			entityQuery.setEndRow(page * rows);
		}
		String sort = request.getParameter("sort");
		String direction = request.getParameter("direction");
		if(sort != null) {
			Sort sortValue = new Sort(Sort.STRING, sort, direction);
			entityQuery.addSort(sortValue);
		}
		if(qname.equals(RepositoryModel.ITEM)) {
			entityQuery.getAggregations().add(new ContentTypeAggregation(SystemModel.QNAME.toString(), 20, getSearchService()));
			entityQuery.getAggregations().add(new SearchAggregation(RepositoryModel.COLLECTION_ID.toString(), 20, getSearchService()));	
		}
		entityQuery.getAggregations().add(new SearchAggregation(ClassificationModel.CORPORATIONS.toString(), 20, getSearchService()));
		entityQuery.getAggregations().add(new SearchAggregation(ClassificationModel.PEOPLE.toString(), 20, getSearchService()));
		entityQuery.getAggregations().add(new SearchAggregation(ClassificationModel.SUBJECTS.toString(), 20, getSearchService()));
		
		SearchResponse results = getSearchService().search(entityQuery);
		if(results != null) {
			data.setStartRow(results.getStartRow());
			if(results.getResultSize() >= results.getEndRow()) data.setEndRow(results.getEndRow());
			else data.setEndRow(results.getResultSize());
			data.setTotal(results.getResultSize());
			for(SearchResult result : results.getResults()) {
				data.getRows().add(result.toMap());
			}
			data.setAttributes(results.getAttributes());
			data.setBreadcrumb(results.getBreadcrumb());
		}
		request.getSession().setAttribute("search-request", entityQuery);
		return data;
	}
	@CrossOrigin
	@ResponseBody
	@RequestMapping(value="/result.json")
	public Map<String,Object> detail(@RequestParam long id) throws Exception {
		SearchRequest request = new SearchRequest();
		request.setNodeId(id);
		SearchResponse response = getSearchService().search(request);
		Map<String,Object> map = response.getResult(0).toMap();
		String q = (String)map.get("qname");
		map.put(SystemModel.CONTENT_TYPE.toString(), StringFormatUtility.toTitleCase(new QName(q).getLocalName().replace("_", " ")));
		return map;
	}
	@CrossOrigin
	@ResponseBody
	@RequestMapping(value="/attribute/values.json")
	public SearchRestResponse<SearchAttributeValue> searchAttribute(
			@RequestParam final QName qname,
			@RequestParam(required=false) String query,
			@RequestParam(required=false, defaultValue="numeric") String sort,
			@RequestParam(required=false, defaultValue="0") int page, 
			@RequestParam(required=false, defaultValue="20") int rows,
			HttpServletRequest req, HttpServletResponse resp) throws Exception {
		SearchRestResponse<SearchAttributeValue> data = new SearchRestResponse<SearchAttributeValue>();
		int start = (page * rows)-rows;
		int end = page * rows;
		
		SearchRequest request = (SearchRequest)req.getSession().getAttribute("search-request");
		if(request != null) {
			Optional<SearchAggregation> aggregation = request.getAggregations().stream().filter(a->a.getName().equals(qname.toString())).findFirst();
			if(aggregation.isPresent()) {
				SearchAttribute attribute = aggregation.get().getSearchAttribute(query, sort, start, end);
				data.setRows(attribute.getValues());
				data.setStartRow(start);
				data.setEndRow(end);
				data.setTotal(aggregation.get().getTotal());
			}
		} else {
			
		}
		return data;
	}
	@CrossOrigin
	@ResponseBody
	@RequestMapping(value="/structured.json")
	public SearchRestResponse<Map<String,Object>> structuredSearch(@RequestParam QName qname, @RequestParam(required=false) String query, 
			@RequestParam(required=false) FilterRule[] filters, @RequestParam(required=false, defaultValue="1") int page, 
			@RequestParam(required=false, defaultValue="0") int rows, HttpServletRequest req) throws Exception {
		SearchRestResponse<Map<String,Object>> data = new SearchRestResponse<Map<String,Object>>();		
		int start = (page*rows) -  rows;
		int end = page*rows;
		data.setStartRow(start);
		data.setEndRow(end);
		SearchRequest request = new SearchRequest(qname, query);
		
		List<ModelField> fields = getDictionaryService().getModelFields(qname);
		for(ModelField field : fields) {
			if(field.isSearchable()) {
				String parm = req.getParameter(field.getQName().toString());
				if(parm != null)
					request.addParameter(field.getQName().toString(), parm);
			}
		}
		
		if(filters != null) {
			request.getFilters().addAll(Arrays.asList(filters));
		}
		request.setStartRow(start);
		request.setEndRow(end);
		SearchResponse response = getSearchService().search(request);
		for(SearchResult result : response.getResults()) {
			Map<String,Object> map = new HashMap<String,Object>();
			map.put("id", String.valueOf(result.getId()));
			for(String key : result.getData().keySet()) {
				if(!key.equals("acl") && !key.equals("source_assoc") && !key.equals("target_assoc") && 
						!key.equals("sort_") && !key.equals("created") && !key.equals("deleted") && 
						!key.equals("user") && !key.equals("modified")) {
					Object value = result.getData().get(key);
					if(value != null) map.put(key, value);
					else map.put(key, "");
				}
			}
			data.getRows().add(map);
		}
		data.setTotal(response.getResultSize());
		return data;
	}
	@CrossOrigin
	@ResponseBody
	@RequestMapping(value="/entities.json", method = RequestMethod.POST)
	public RestResponse<Entity> search(@RequestBody SearchRequest query, HttpServletRequest request, HttpServletResponse response) throws Exception {
		request.setCharacterEncoding("UTF-8");
		response.setCharacterEncoding("UTF-8");
		//EntityQuery query = new EntityQuery();
		//query.fromJson(requestData);
		
		if(query.getUser() != null) {
			User user = getSecurityService().getUserByUsername(query.getUser().getName());
			if(user == null) {
				user = getSecurityService().addUser(query.getUser());
			}
			query.setUser(user);
		} else query.setUser(new GuestUser());
		
		prepareResponse(response);
		
		RestResponse<Entity> data = new RestResponse<Entity>();
		long startTime = System.currentTimeMillis();
		
		SearchResponse results = null;
		List<Entity> entities = new ArrayList<Entity>();
		if(query != null && !query.getQuery().equals("null")) {
			if(NumberUtility.isLong(query.getQuery())) {
				try {
					Entity entity = getEntityService().getEntity(Long.valueOf(query.getQuery()));
					if(entity != null) {
						entities.add(entity);
						data.setStartRow(0);
						data.setEndRow(1);
						data.setTotal(1);
					}
				} catch(Exception e) {
					e.printStackTrace();
				}
			} else if(query.getQuery().equals("orphans")) {
				log.info("received orphans query...");
				List<Entity> list = new ArrayList<Entity>();
				//EntityQuery eQuery = new EntityQuery(query.getEntityQnames(), null, sort, false);
				query.setStartRow(0);
				query.setEndRow(10000);
				results = getSearchService().search(query);
				for(SearchResult entity : results.getResults()) {
					if(getEntityService().getEntity(entity.getId()).getTargetAssociations().size() == 0)
						list.add(getEntityService().getEntity(entity.getId()));
				}
				for(int i=query.getStartRow(); i < query.getEndRow(); i++) {
					if(list.size() > i) entities.add(list.get(i));
				}
				data.setStartRow(query.getStartRow());
				if(list.size() >= results.getEndRow()) data.setEndRow(results.getEndRow());
				else data.setEndRow(list.size());
				data.setTotal(list.size());
			} else {
				results = getSearchService().search(query);
				for(SearchResult entity : results.getResults()) {
					entities.add(getEntityService().getEntity(entity.getId()));
				}
			}
		} else {
			results = getSearchService().search(query);
			for(SearchResult entity : results.getResults()) {
				entities.add(getEntityService().getEntity(entity.getId()));
			}
		}		
		try {
			for(Entity entity : entities) {
				//data.getResponse().addData(getEntityService().export(instr, entity));
				data.addRow(entity);
			}
		} catch(Exception e) {
			e.printStackTrace();
		}		
		data.setTime(System.currentTimeMillis() - startTime);
		if(results != null) {
			data.setStartRow(results.getStartRow());
			if(results.getResultSize() >= results.getEndRow()) data.setEndRow(results.getEndRow());
			else data.setEndRow(results.getResultSize());
			data.setTotal(results.getResultSize());
		}		
		if(query.getQnames().size() > 1)	
			data.addMessage((entities.size()+" "+query.getQnames().get(0).getLocalName()+" fetched"));
		else {
			String msg = "";
			for(QName q : query.getQnames())
				msg += q.toString()+" ";
			data.addMessage((entities.size()+" "+msg.trim()+" fetched"));
		}
		return data;
	}
	@CrossOrigin
	@ResponseBody
	@RequestMapping(value="/associations.json")
	public SearchRestResponse<Map<String,Object>> search(@RequestParam Long id, 
			@RequestParam(required=false, defaultValue="0") int page, @RequestParam(required=false, defaultValue="20") int rows,
			HttpServletRequest request, HttpServletResponse response) throws Exception {
		SearchRestResponse<Map<String,Object>> data = new SearchRestResponse<Map<String,Object>>();
		SearchRequest searchRequest = new SearchRequest();
		searchRequest.setId(id);
		SearchResponse results = getSearchService().search(searchRequest);
		if(results != null) {
			data.setStartRow(results.getStartRow());
			if(results.getResultSize() >= results.getEndRow()) data.setEndRow(results.getEndRow());
			else data.setEndRow(results.getResultSize());
			data.setTotal(results.getResultSize());
			for(SearchResult result : results.getResults()) {
				Map<String,Object> map = new HashMap<String,Object>();
				map.put("id", result.getId());
				map.put("qname", result.getQName().toString());
				map.put("source", result.getSource());
				map.put("target", result.getTarget());
				for(String key : result.getData().keySet()) {
					map.put(key, result.getData().get(key));
				}
				data.getRows().add(map);
			}
		}		
		return data;
	}
}
