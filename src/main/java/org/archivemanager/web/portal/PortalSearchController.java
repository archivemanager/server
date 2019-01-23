package org.archivemanager.web.portal;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.archivemanager.data.Sort;
import org.archivemanager.models.ClassificationModel;
import org.archivemanager.models.RepositoryModel;
import org.archivemanager.models.SearchModel;
import org.archivemanager.models.SystemModel;
import org.archivemanager.models.binders.RepositoryModelEntityBinder;
import org.archivemanager.models.binders.RepositoryModelSearchBinder;
import org.archivemanager.models.dictionary.ModelField;
import org.archivemanager.models.repository.Attribute;
import org.archivemanager.models.repository.AttributeValue;
import org.archivemanager.models.repository.Breadcrumb;
import org.archivemanager.models.repository.Category;
import org.archivemanager.models.repository.Collection;
import org.archivemanager.models.repository.Item;
import org.archivemanager.models.repository.Paging;
import org.archivemanager.models.repository.Result;
import org.archivemanager.models.repository.ResultSet;
import org.archivemanager.models.repository.Subject;
import org.archivemanager.models.system.QName;
import org.archivemanager.models.system.User;
import org.archivemanager.services.entity.Association;
import org.archivemanager.services.entity.Entity;
import org.archivemanager.services.entity.EntityResultSet;
import org.archivemanager.services.entity.EntityService;
import org.archivemanager.services.entity.EntitySorter;
import org.archivemanager.services.search.Clause;
import org.archivemanager.services.search.Parameter;
import org.archivemanager.services.search.SearchAttribute;
import org.archivemanager.services.search.SearchAttributeValue;
import org.archivemanager.services.search.SearchNode;
import org.archivemanager.services.search.SearchRequest;
import org.archivemanager.services.search.SearchResponse;
import org.archivemanager.services.search.SearchResult;
import org.archivemanager.services.search.SearchService;
import org.archivemanager.services.search.aggregation.SearchAggregation;
import org.archivemanager.services.security.SecurityService;
import org.archivemanager.util.DateUtility;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fasterxml.jackson.databind.ObjectMapper;


@Controller
@RequestMapping("/portal/search")
public class PortalSearchController {
	//private final static Logger log = Logger.getLogger(PortalController.class.getName());
	@Autowired private EntityService entityService;
	@Autowired private SearchService searchService;
	@Autowired private SecurityService securityService;
	@Autowired private ObjectMapper objMapper;
	@Autowired private RepositoryModelSearchBinder binder;
	@Autowired private RepositoryModelEntityBinder entityBinder;
	
	
	@ResponseBody
	@RequestMapping(value="/results")
	public ResultSet searchResults(HttpServletRequest request, HttpServletResponse response) throws IOException {		
		String view = getParameterString(request, "view", "default");		
		String code = getParameterString(request, "code", "");		
		String contentType = getParameterString(request, "contentType", "false");
		String dateExpression = getParameterString(request, "dateExpression", "false");
		String displayCollection = getParameterString(request, "collection", "false");
		String displayDescription = getParameterString(request, "description", "false");
		String displaySummary = getParameterString(request, "summary", "false");
		String displayLanguage = getParameterString(request, "language", "false");
		String sort = getParameterString(request, "sort", "openapps_org_system_1_0_name");
		String direction = getParameterString(request, "direction", "asc");
		String size = getParameterString(request, "size", "10");
		String detailPage = getParameterString(request, "detailPage", "detail");
		int maxFieldSize = getParameterInt(request, "maxFieldSize", 100);
		Boolean displayAllResults = getParameterBool(request, "all-results", true);
		
		String query = getParameterString(request, "query", "");
		String page = getParameterString(request, "page", "1");
		boolean sources = getParameterBool(request, "sources", false);
		boolean targets = getParameterBool(request, "targets", false);
		String id = getParameterString(request, "id", null);
		
		//request.setAttribute("baseUrl", baseUrl);
		//request.setAttribute("mediaUrl", mediaUrl);
		request.setAttribute("query", query);
		request.setAttribute("contentType", contentType);
		request.setAttribute("dateExpression", dateExpression);
		request.setAttribute("collection", displayCollection);
		request.setAttribute("description", displayDescription);
		request.setAttribute("summary", displaySummary);
		request.setAttribute("language", displayLanguage);
		request.setAttribute("detailPage", detailPage);
		request.setAttribute("maxFieldSize", maxFieldSize+"px");
		request.setAttribute("sort", sort);
		request.setAttribute("direction", direction);
		request.setAttribute("size", size);
		
		ResultSet results = null;
		if(view != null) {
			if(view.equals("gallery1")) {			
				if(query == null || query.length() == 0) query = "all results";
				results = searchResultSet(request, "item", code, id, query, Integer.valueOf(page), null, direction, 100, targets, sources);
			} else if(view.equals("gallery2")) {			
				request.setAttribute("query", query);
				if(query == null || query.length() == 0) query = "all results";
				try {
					List<Collection> collections = collectionNav(request);
					request.setAttribute("collections", collections);			
					results = searchResultSet(request, "item", code, id, query, Integer.valueOf(page), null, direction, 100, targets, sources);
				} catch(Exception e) {
					e.printStackTrace();
				}		
			} else if(view.equals("videos")) {			
				if(query == null || query.length() == 0) query = "all results";
				results = searchResultSet(request, "item", code, id, query, Integer.valueOf(page), new String[]{"date_expression_"}, direction, 9, targets, true);
			
			} else if(view.equals("collections")) {
				if(query == null || query.length() == 0) query = "all results";
				if(query.length() == 1) query = query+"*";
				results = searchResultSet(request, "collection", code, id, query.toLowerCase(), Integer.valueOf(page), new String[]{sort}, direction, Integer.valueOf(size), targets, sources);	
				
			} else if(view.equals("archive_results")) {
				results = searchResultSet(request, "archive", null, id, query, Integer.valueOf(page), new String[]{"sort_","relevance","openapps_org_system_1_0_name"}, direction, Integer.valueOf(size), targets, sources);
			
			} else if(view.equals("notable_figures_entries")) {
				if(query == null || query.length() == 0) query = "all results";
				if(query.length() == 1) query = query+"*";
				results = searchResultSet(request, "entry", null, id, query.toLowerCase(), Integer.valueOf(page), new String[]{"openapps_org_system_1_0_name"}, direction, Integer.valueOf(size), true, sources);	
			} else if(view.equals("series")) {
				try {
					List<Collection> collections = collections(request);
					request.setAttribute("collections", collections);
				} catch(Exception e) {
					e.printStackTrace();
				}
			} else if(view.equals("subject_guides")) {
				try {
					SearchRequest subjectQuery = new SearchRequest(ClassificationModel.SUBJECT);
					if(id == null || id.equals("0")) {
						subjectQuery.getParameters().add(new Parameter(ClassificationModel.SOURCE.toString(), "Subject Guide"));
						SearchResponse resultSet = searchService.search(subjectQuery);
						results = getResultSet(resultSet);
					} else {
						results = subject_guides(request, id);
						SearchRequest req = new SearchRequest();
						req.setNodeId(Long.valueOf(id));
						SearchResponse res = searchService.search(req);
						results.setParent(binder.getSubject(res.getResult(0)));
					}
				} catch(Exception e) {
					e.printStackTrace();
				} 
			} else if(view.equals("digital_reserve")) {				
				results = searchResultSet(request, "reserve", null, id, query, Integer.valueOf(page), new String[]{"openapps_org_system_1_0_name"}, direction, 9, targets, true);
			} else {
				if(!code.equals("id") && id != null && !id.equals("0") && !query.contains("source_assoc:"+id)) {
					query += " source_assoc:"+id;
				}
				if(id != null && id.length() > 0) {				
					request.setAttribute("baseUrl", "?id="+id);
				} else {
					request.setAttribute("baseUrl", "");
				}				
				if(!query.equals("") || displayAllResults) {
					results = searchResultSet(request, "item", code, id, query.trim(), Integer.valueOf(page), new String[]{sort}, direction, Integer.valueOf(size), targets, sources);
				}
			}
		} else {
			view = "results";		
		}
		return results;
	}
	@ResponseBody
	@RequestMapping(value="/item")
	public ResultSet resultItem(@RequestParam Long id, HttpServletRequest request, HttpServletResponse response) throws IOException {
		String displayName = getParameterString(request, "displayName", null);
		String displayDescription = getParameterString(request, "displayDescription", "false");
		String displaySummary = getParameterString(request, "displaySummary", "false");
		String displayDateExpression = getParameterString(request, "displayDateExpression", "false");
		String displayContainer = getParameterString(request, "displayContainer", "false");
		String displayCollectionName = getParameterString(request, "displayCollectionName", "false");
		String displayContentType = getParameterString(request, "displayContentType", "false");
		String displayLanguage = getParameterString(request, "displayLanguage", "false");
		String missingImage = getParameterString(request, "missingImage", null);
		if(missingImage != null && missingImage.length() == 0) missingImage = null;
		
		request.setAttribute("missingImage", missingImage);
		request.setAttribute("displayName", displayName);
		request.setAttribute("displayDescription", displayDescription);
		request.setAttribute("displaySummary", displaySummary);
		request.setAttribute("displayDateExpression", displayDateExpression);
		request.setAttribute("displayContainer", displayContainer);
		request.setAttribute("displayCollectionName", displayCollectionName);
		request.setAttribute("displayContentType", displayContentType);
		request.setAttribute("displayLanguage", displayLanguage);
		
		ResultSet results = new ResultSet();
		Result result = null;
		if(id != null) {
			SearchRequest req = new SearchRequest();
			req.setNodeId(Long.valueOf(id));
			SearchResponse res = searchService.search(req);
			SearchResult entity = res.getResult(0);			
			String localName = entity.getQName().getLocalName();
			if(localName.equals("subject")) {
				result = binder.getSubject(entity);
			} else if(localName.equals("person") || localName.equals("corporation")) {
				result = binder.getNamedEntity(entity);
			} else if(localName.equals("collection")) {
				Entity e = entityService.getEntity(entity.getId());
				result = entityBinder.getCollection(e, true);
			} else if(localName.equals("video")) {
				result = binder.getVideo(entity);
			} else if(localName.equals("audio")) {
				result = binder.getAudio(entity);
			} else {
				result = binder.getItem(entity);
			}
			results.getResults().add(result);
		}
		
		/*** Logging ***/
		String ipAddress = request.getHeader("X-FORWARDED-FOR");  
	    if(ipAddress == null) {  
	         ipAddress = request.getRemoteAddr();  
	    }
	    //if(ipAddress != null && !ipAddress.equals("127.0.0.1")) {
		    Entity event = new Entity(SearchModel.VIEW_EVENT);
		    try {
		    	event.addProperty(SystemModel.TIMESTAMP, DateUtility.getCurrentDate());
		    	event.addProperty(SystemModel.IPADDRESS, ipAddress);
		    	event.addProperty(SystemModel.TARGET, Long.valueOf(id));
		    	//event.addProperty(SystemModel.URL, results.getResultCount());
		    	//event.addProperty(SearchModel.QUERY, searchRequest.getQuery());
		    	event.addProperty(SystemModel.VALUE, result.getName());
		    	entityService.updateEntity(event);
		    } catch(Exception e) {
		    	e.printStackTrace();
		    }
	    //}
		return results;
	}
			
	protected ResultSet searchResultSet(HttpServletRequest request, String type, String code, String id, String query, int page, String[] sorts, String direction, int size, boolean targets, boolean sources) throws IOException {
		User user = null;
		UsernamePasswordAuthenticationToken token = (UsernamePasswordAuthenticationToken)request.getUserPrincipal();
		if(token != null) {
	 		org.springframework.security.core.userdetails.User userDetails = (org.springframework.security.core.userdetails.User)token.getPrincipal();
			user = securityService.getUserByUsername(userDetails.getUsername());
		}
		String parms = getParameterString(request, "parms", null);
		
		SearchRequest searchRequest = null;
		ResultSet results = new ResultSet();
		int end = size * page;
		int start = end - size;
		String paramterString = "";		
		if(sorts == null) sorts = new String[] {"openapps_org_system_1_0_name"};
		
		if(type != null && type.equals("archive")) {
			searchRequest = getSearchRequest(query, start, end, sorts, direction, false, ClassificationModel.CORPORATION,ClassificationModel.PERSON,RepositoryModel.COLLECTION);
		} else if(type != null && type.equals("collection")) {
			searchRequest = getSearchRequest(RepositoryModel.COLLECTION, query.toUpperCase(), start, end, sorts, direction, false);
			searchRequest.setFields(new String[]{"openapps_org_system_1_0_name.keyword"});
			searchRequest.addParameter(RepositoryModel.CODE.toString(), code);
		} else if(type != null && type.equals("subjects")) {
			searchRequest = getSearchRequest(query, start, end, sorts, direction, false, ClassificationModel.SUBJECT);
		} else if(type != null && type.equals("reserve")) {
			String email = getParameterString(request, "email", "");
			Entity u = entityService.getEntity(SystemModel.USER, SystemModel.EMAIL, email);
			searchRequest = getSearchRequest(RepositoryModel.ITEM, query, start, end, sorts, direction, false);
			List<Association> courses = u.getAssociations(ClassificationModel.USERS);				
			for(Association association : courses) {
				if(association.getQName().equals(ClassificationModel.USERS))
					searchRequest.addParameter("source_assoc", association.getSource());
			}
		} else {
			 if(type != null && type.equals("entry")) {
				searchRequest = getSearchRequest(ClassificationModel.ENTRY, query, start, end, sorts, direction, false);
			} else {
				searchRequest = getSearchRequest(RepositoryModel.ITEM, query, start, end, sorts, direction, false);
			}
			if(code != null && code.length() > 0) {
				if(code.equals("id")) {
					searchRequest.addParameter("openapps_org_system_1_0_path", id);
				} else {
					SearchRequest collectionQuery = new SearchRequest(RepositoryModel.COLLECTION);
					collectionQuery.getParameters().add(new Parameter(RepositoryModel.CODE.toString(), code));
					SearchResponse collectionResults = searchService.search(collectionQuery);
					if(collectionResults.getResults().size() == 1) {
						searchRequest.addParameter("openapps_org_system_1_0_path", String.valueOf(collectionResults.getResults().get(0).getId()));
					} else if(collectionResults.getResults().size() > 1) {
						Clause clause = new Clause();
						clause.setOperator(Clause.OPERATOR_OR);
						for(SearchResult collection : collectionResults.getResults()) {
							clause.addParameter(new Parameter("openapps_org_system_1_0_path", String.valueOf(collection.getId())));
							//searchRequest.setCollection(collection.getId());
						}
						searchRequest.addClause(clause);
					}
				}
			}		
		}
		if(user != null && !user.getName().equals("administrator"))
			searchRequest.setUser(user);
		
		
		if(parms != null && parms.length() > 0) {
			String[] parmVals = parms.split(",");
			for(String p : parmVals) {
				if(request.getParameter(p) != null && request.getParameter(p).length() > 0) {
					paramterString += "&" + p + "=" + URLEncoder.encode(request.getParameter(p), "UTF-8");
					searchRequest.getRequestParameters().put(p, new String[]{request.getParameter(p)});
				}
			}
		}
		
		searchRequest.getAggregations().add(new SearchAggregation(SystemModel.QNAMES.toString(), 20, searchService));
		searchRequest.getAggregations().add(new SearchAggregation(RepositoryModel.COLLECTIONS.toString(), 20, searchService));	
		searchRequest.getAggregations().add(new SearchAggregation(ClassificationModel.CORPORATIONS.toString(), 20, searchService));
		searchRequest.getAggregations().add(new SearchAggregation(ClassificationModel.PEOPLE.toString(), 20, searchService));
		searchRequest.getAggregations().add(new SearchAggregation(ClassificationModel.SUBJECTS.toString(), 20, searchService));
		
		SearchResponse searchResponse = searchService.search(searchRequest);
		if(searchResponse != null) {			
			results.setQuery(query.trim());
			try {
				int maxFieldSize = getParameterInt(request, "maxFieldSize", 0);
				results.setStart(searchResponse.getStartRow());
				results.setEnd(searchResponse.getEndRow());
				results.setTime(searchResponse.getTime());
				results.setResultCount(searchResponse.getResultSize());
				results.setQuery(query);
				results.setPageSize(size);				
				for(SearchResult entity :searchResponse.getResults()) {
					Result result = null;
					String localName = entity.getQName().getLocalName();
					if(localName.equals("entry")) {
						result = binder.getEntry(entity);
					} else if(localName.equals("collection")) {
						result = binder.getCollection(entity);
					} else if(localName.equals("video")) {
						result = binder.getVideo(entity);						
					} else {
						result = binder.getItem(entity);									
					}
					if(result != null) {
						if(result.getDescription() != null && maxFieldSize > 0) {
							if(result.getDescription().length() > 250) {
								result.setDescription(result.getDescription().substring(0, 250)+"...");
							}
						}
						if(result instanceof Item && maxFieldSize > 0) {
							Item item = (Item)result;
							if(item.getSummary() != null) {
								if(item.getSummary().length() > maxFieldSize) {
									item.setSummary(item.getSummary().substring(0, maxFieldSize)+"...");
								}
							}
						}
						results.getResults().add(result);
					}
				}
				if(!searchResponse.getAttributes().isEmpty()) {
					for(SearchAttribute att : searchResponse.getAttributes()) {
						Attribute attribute = new Attribute(att.getName());
						for(SearchAttributeValue valueNode : att.getValues()) {
							String name = valueNode.getName();
							String pageQuery = valueNode.getQuery().replace("//",  "/");
							if(results.getPageSize() != 10) pageQuery += "&size="+results.getPageSize();
							if(results.getSort() != null) pageQuery += "&sort="+results.getSort();
							AttributeValue value = new AttributeValue(name, pageQuery, String.valueOf(valueNode.getCount()));
							attribute.getValues().add(value);
						}
						attribute.setCount(String.valueOf(attribute.getValues().size()));
						if(attribute.getName().equals("Collections") && attribute.getValues().size() <= 1) attribute.setDisplay(false);
						else if(attribute.getValues().size() == 0) attribute.setDisplay(false);
						else attribute.setDisplay(true);
						results.getAttributes().add(attribute);
					}
				}
				if(searchResponse.getBreadcrumb() != null && !searchResponse.getBreadcrumb().isEmpty()) {
					for(SearchNode crumbNode : searchResponse.getBreadcrumb()) {
						String pageQuery = crumbNode.getQuery() != null ? crumbNode.getQuery().replace("//",  "/").trim() : "";
						if(crumbNode.getLabel() != null) {
							Breadcrumb crumb = new Breadcrumb(crumbNode.getLabel().trim(), pageQuery);
							results.getBreadcrumbs().add(crumb);
							results.setQuery(crumb.getQuery());
							results.setLabel(crumb.getName());
						}
					}
				}
				doPaging(results, query, page, paramterString);
			} catch(Exception e) {
				e.printStackTrace();
			}
			
			/*** Logging ***/
			String ipAddress = request.getHeader("X-FORWARDED-FOR");  
		    if(ipAddress == null) {  
		         ipAddress = request.getRemoteAddr();  
		    }
		    //if(ipAddress != null && !ipAddress.equals("127.0.0.1")) {
			    Entity event = new Entity(SearchModel.EVENT);
			    try {
			    	event.addProperty(SystemModel.TYPE, "search");
			    	event.addProperty(SystemModel.TIMESTAMP, DateUtility.getCurrentDate());
			    	event.addProperty(SystemModel.IPADDRESS, ipAddress);
			    	event.addProperty(SystemModel.COUNT, results.getResultCount());
			    	event.addProperty(SearchModel.QUERY, searchRequest.getQuery());
			    	event.addProperty(SystemModel.VALUE, objMapper.writeValueAsString(searchRequest));
			    	entityService.updateEntity(event);
			    } catch(Exception e) {
			    	e.printStackTrace();
			    }
		    //}
		}
		return results;
	}
	
	protected void doPaging(ResultSet results, String query, int currentPage, String parameters) {
		int pageCount = 0;		
		if(results.getResultCount() > results.getPageSize()) {
			double ratio = (double)results.getResultCount() / results.getPageSize();
			pageCount = (int)(Math.ceil(ratio));
		}
		results.setPageCount(pageCount);
		results.setPage(currentPage);
		
		if(query != null && !query.equals("all results")) {
			results.setQuery("query="+query);
		} else {
			results.setQuery("query="+parameters);
		}
		
		if(results.getPageSize() != 10) {
			for(Breadcrumb page : results.getBreadcrumbs()) {
				page.setQuery(page.getQuery() + "&size="+results.getPageSize());
			}
		}
		if(results.getSort() != null) {
			for(Breadcrumb page : results.getBreadcrumbs()) {
				page.setQuery(page.getQuery() + "&sort="+results.getSort());
			}
		}	
		
		int startPage = currentPage < 5 ? 1 : currentPage - 4;
		int endPage = (pageCount >= 10) ? 10 : pageCount;
		/*
		if(currentPage == 1) {
			String pageQuery = results.getQuery()+"&page=1&size="+results.getPageSize();
			if(results.getSort() != null) pageQuery += "&sort="+results.getSort();
			results.getPaging().add(new Paging("1", pageQuery));
		}
		*/
		if(results.getPage() > 1 && pageCount > 1) {
			Paging paging = new Paging("Previous", results.getQuery(), (currentPage - 1), results.getPageSize());
			if(results.getSort() != null) paging.setSort(results.getSort());
			results.getPaging().add(paging);
		}
		for(int i = startPage; i <= endPage; i++) {
			Paging paging = new Paging(String.valueOf(i), results.getQuery(), i, results.getPageSize());
			if(results.getSort() != null) paging.setSort(results.getSort());
			results.getPaging().add(paging);
		}
		if(pageCount > currentPage) {
			Paging paging = new Paging("Next", results.getQuery(), (currentPage + 1), results.getPageSize());
			if(results.getSort() != null) paging.setSort(results.getSort());
			results.getPaging().add(paging);
		}
	}
	protected List<Collection> collectionNav(HttpServletRequest request) throws Exception {
		String code = getParameterString(request, "code", "");
		getParameterString(request, "code", "");
		List<Collection> collections = new ArrayList<Collection>();
		
		EntitySorter entitySorter = new EntitySorter(new Sort(ModelField.TYPE_SMALLTEXT, SystemModel.NAME.toString(), "desc"));
		SearchRequest collectionQuery = new SearchRequest(RepositoryModel.COLLECTION);
		collectionQuery.getParameters().add(new Parameter(RepositoryModel.CODE.toString(), code));
		SearchResponse collectionResults = searchService.search(collectionQuery);
		if(collectionResults != null && collectionResults.getResultSize() > 0) {
			for(SearchResult collectionEntity : collectionResults.getResults()) {
				Collection collection = binder.getCollection(collectionEntity);
				collection.setDescription("");
				List<Association> seriesAssociations = entityService.getEntity(collectionEntity.getId()).getSourceAssociations(RepositoryModel.CATEGORIES);
				List<Entity> series = new ArrayList<Entity>();
				for(Association seriesAssoc : seriesAssociations) {
					Entity target = entityService.getEntity(seriesAssoc.getTarget());
					series.add(target);
				}
				Collections.sort(series, entitySorter);
				for(Entity seriesEntity : series) {
					Category category = new Category();
					category.setId(seriesEntity.getId());
					category.setName(seriesEntity.getName());
					collection.getCategories().add(category);
					/*
					List<Association> subseriesAssociations = seriesEntity.getSourceAssociations(RepositoryModel.CATEGORIES);
					for(Association subseriesAssoc : subseriesAssociations) {
						Entity target2 = entityService.getEntity(subseriesAssoc.getTarget());
						Category series2 = new Category();
						series2.setId(String.valueOf(target2.getId()));
						series2.setTitle(target2.getName());
						series.getCategories().add(series2);
					}
					*/
				}
				collections.add(collection);
			}			
		}
		return collections;
	}
	protected ResultSet subject_guides(HttpServletRequest request, String id) throws Exception {
		SearchRequest searchRequest = getSearchRequest("source_assoc:"+id, 0, 1000, new String[]{"openapps_org_system_1_0_name"}, "desc", false, RepositoryModel.ITEM,RepositoryModel.COLLECTION);
		SearchResponse searchResponse = searchService.search(searchRequest);
		ResultSet results = new ResultSet();
		results.setStart(searchResponse.getStartRow());
		results.setEnd(searchResponse.getEndRow());
		if(searchResponse != null) {			
			for(SearchResult searchResult :searchResponse.getResults()) {
				String localName = searchResult.getQName().getLocalName();
				if(localName.equals("collection")) {
					Entity entity = entityService.getEntity(searchResult.getId());
					Collection collection = entityBinder.getCollection(entity, true);
					List<Subject> subjectGuides = new ArrayList<Subject>();
					List<Subject> subjects = collection.getSubjects();
					for(Subject subject : subjects) {
						if(subject.getSource() != null && subject.getSource().equals("Subject Guide"))
							subjectGuides.add(subject);
					}
					collection.setSubjects(subjectGuides);
					results.getResults().add(collection);
				} else {
					Item item = binder.getItem(searchResult);
					results.getResults().add(item);
				}
			}
		}
		//log.info(searchResponse.getResultSize()+" results for subject guide query parsed to "+searchResponse..getQueryParse()+" ----> "+searchResponse.getQueryExplanation());
		return results;
	}
	
	protected ResultSet getResultSet(SearchResponse searchResponse) {
		ResultSet results = new ResultSet();
		results.setStart(searchResponse.getStartRow());
		results.setEnd(searchResponse.getEndRow());
		results.setTime(searchResponse.getTime());
		results.setResultCount(searchResponse.getResultSize());
		for(SearchResult entity :searchResponse.getResults()) {
			Result result = null;
			String localName = entity.getQName().getLocalName();
			if(localName.equals("entry")) {
				result = binder.getEntry(entity);
			} else if(localName.equals("collection")) {
				result = binder.getCollection(entity);
			} else if(localName.equals("video")) {
				result = binder.getVideo(entity);						
			} else if(localName.equals("subject")) {
				result = binder.getSubject(entity);
			} else {
				result = binder.getItem(entity);									
			}
			if(result != null) {
				results.getResults().add(result);
			}
		}
		return results;
	}
	protected ResultSet getResultSet(EntityResultSet resultset) {
		ResultSet results = new ResultSet();
		results.setResultCount(resultset.getSize());
		for(Entity entity : resultset.getData()) {
			Result result = null;
			String localName = entity.getQName().getLocalName();
			if(localName.equals("entry")) {
				result = entityBinder.getEntry(0, entity);
			} else if(localName.equals("collection")) {
				result = entityBinder.getCollection(entity, false);
			} else if(localName.equals("video")) {
				result = entityBinder.getVideo(entity);						
			} else if(localName.equals("subject")) {
				result = entityBinder.getSubject(0, entity);
			} else {
				result = entityBinder.getItem(0, entity);									
			}
			if(result != null) {
				results.getResults().add(result);
			}
		}
		return results;
	}
	protected SearchRequest getSearchRequest(String query, int startRow, int endRow, String[] sorts, String direction, boolean attributes,QName... qnames) {
		SearchRequest sQuery = new SearchRequest(query, qnames);
		if(attributes) {
			sQuery.getAggregations().add(new SearchAggregation(SystemModel.QNAMES.toString(), 20, searchService));
			sQuery.getAggregations().add(new SearchAggregation(RepositoryModel.COLLECTIONS.toString(), 20, searchService));	
			sQuery.getAggregations().add(new SearchAggregation(ClassificationModel.CORPORATIONS.toString(), 20, searchService));
			sQuery.getAggregations().add(new SearchAggregation(ClassificationModel.PEOPLE.toString(), 20, searchService));
			sQuery.getAggregations().add(new SearchAggregation(ClassificationModel.SUBJECTS.toString(), 20, searchService));
		}
		sQuery.setStartRow(startRow);
		sQuery.setEndRow(endRow);
		if(sorts != null) {
			for(String sort : sorts) {
				Sort lSort = null;
				String[] s = sort.split(",");
				if(s.length == 2) {
					if(s[0].endsWith("_")) lSort = new Sort(ModelField.TYPE_LONG, sort, direction);
					else lSort = new Sort(ModelField.TYPE_SMALLTEXT, s[0], direction);						
				} else if(s.length == 1) {
					lSort = new Sort(ModelField.TYPE_SMALLTEXT, sort, direction);
				}
				sQuery.addSort(lSort);
			}
		}				
		return sQuery;
	}
	protected SearchRequest getSearchRequest(QName qname, String query, int startRow, int endRow, String[] sorts, String direction, boolean attributes) {
		SearchRequest sQuery = new SearchRequest(qname, query);
		if(attributes) {
			sQuery.getAggregations().add(new SearchAggregation(SystemModel.QNAMES.toString(), 20, searchService));
			sQuery.getAggregations().add(new SearchAggregation(RepositoryModel.COLLECTIONS.toString(), 20, searchService));	
			sQuery.getAggregations().add(new SearchAggregation(ClassificationModel.CORPORATIONS.toString(), 20, searchService));
			sQuery.getAggregations().add(new SearchAggregation(ClassificationModel.PEOPLE.toString(), 20, searchService));
			sQuery.getAggregations().add(new SearchAggregation(ClassificationModel.SUBJECTS.toString(), 20, searchService));
		}
		sQuery.setStartRow(startRow);
		sQuery.setEndRow(endRow);
		if(sorts != null) {
			for(String sort : sorts) {
				Sort lSort = null;
				String[] sortStrings = sort.split(",");
				for(String sortStr : sortStrings) {
					String[] s = sortStr.split(" ");
					if(s.length == 2) {
						if(s[0].endsWith("_")) lSort = new Sort(ModelField.TYPE_LONG, s[0], direction);
						else lSort = new Sort(ModelField.TYPE_SMALLTEXT, s[0], direction);						
					} else if(s.length == 1) {
						if(s[0].endsWith("_")) lSort = new Sort(ModelField.TYPE_LONG, s[0], direction);
						else lSort = new Sort(ModelField.TYPE_SMALLTEXT, s[0], direction);
					}
					sQuery.addSort(lSort);
				}
			}
		}				
		return sQuery;
	}
	protected List<Collection> collections(HttpServletRequest request) throws Exception {
		String code = getParameterString(request, "code", "");
		List<Collection> collections = new ArrayList<Collection>();
		EntitySorter entitySorter = new EntitySorter(new Sort(ModelField.TYPE_SMALLTEXT, SystemModel.NAME.toString(), "desc"));
		SearchRequest collectionQuery = new SearchRequest(RepositoryModel.COLLECTION);
		collectionQuery.getParameters().add(new Parameter(RepositoryModel.CODE.toString(), code));
		SearchResponse collectionResults = searchService.search(collectionQuery);
		if(collectionResults != null && collectionResults.getResultSize() > 0) {
			for(SearchResult collectionEntity : collectionResults.getResults()) {
				Collection collection = binder.getCollection(collectionEntity);
				collection.setDescription("");
				List<Association> seriesAssociations = entityService.getEntity(collectionEntity.getId()).getSourceAssociations(RepositoryModel.CATEGORIES);
				List<Entity> series = new ArrayList<Entity>();
				for(Association seriesAssoc : seriesAssociations) {
					Entity target = entityService.getEntity(seriesAssoc.getTarget());
					series.add(target);
				}
				Collections.sort(series, entitySorter);
				for(Entity seriesEntity : series) {
					Category category = new Category();
					category.setId(seriesEntity.getId());
					category.setName(seriesEntity.getName());
					collection.getCategories().add(category);
				}
				collections.add(collection);
			}			
		}
		return collections;
	}
	protected String getParameterString(HttpServletRequest request, String name, String defaultValue) {
		String value = request.getParameter(name);
		if(value != null && value.length() > 0) return value;
		else return defaultValue;
	}
	protected int getParameterInt(HttpServletRequest request, String name, int defaultValue) {
		String value = request.getParameter(name);
		if(value != null && value.length() > 0) return Integer.parseInt(value);
		else return defaultValue;
	}
	protected boolean getParameterBool(HttpServletRequest request, String name, boolean defaultValue) {
		String value = request.getParameter(name);
		if(value != null && value.length() > 0) return Boolean.valueOf(value);
		else return defaultValue;
	}
}
