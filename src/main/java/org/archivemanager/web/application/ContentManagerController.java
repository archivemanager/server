package org.archivemanager.web.application;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.archivemanager.data.IDName;
import org.archivemanager.models.SystemModel;
import org.archivemanager.models.system.QName;
import org.archivemanager.models.system.User;
import org.archivemanager.services.content.ContentModel;
import org.archivemanager.services.dictionary.DataDictionaryService;
import org.archivemanager.services.search.SearchRequest;
import org.archivemanager.services.search.SearchResponse;
import org.archivemanager.services.search.SearchRestResponse;
import org.archivemanager.services.search.SearchResult;
import org.archivemanager.services.search.SearchService;
import org.archivemanager.services.security.SecurityService;
import org.archivemanager.web.utility.WebUtility;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;


@Controller
@RequestMapping("/app/content")
public class ContentManagerController extends ApplicationController {
	@Autowired private SecurityService securityService;
	@Autowired private DataDictionaryService dictionaryService;
	@Autowired private SearchService searchService;
	
	//private EntitySorter entitySort = new EntitySorter(new Sort(ModelField.TYPE_SMALLTEXT, SystemModel.NAME.toString(), false));
	//private AssociationSorter assocSort = new AssociationSorter(new Sort(ModelField.TYPE_SMALLTEXT, SystemModel.NAME.toString(), false));
	
	
	@RequestMapping(value = "", method = RequestMethod.GET)
	public String getClassification(final Model model, HttpServletRequest request, HttpServletResponse resp) {
		User user = securityService.getCurrentUser(WebUtility.getHttpRequest(request));
		if(user != null) {
			model.addAttribute("openapps_user", user);
			model.addAttribute("roles", getRolesString(user.getRoles()));					
		}
		return "content";
	}
	
	/*** Services ***/	
	@ResponseBody
	@RequestMapping(value="/search.json")
	public SearchRestResponse<IDName> search(@RequestParam(required=false) String query, @RequestParam(required=false) QName qname, 
			@RequestParam(required=false, defaultValue="0") int page, @RequestParam(required=false, defaultValue="20") int rows,
			HttpServletRequest request, HttpServletResponse response) throws Exception {
		SearchRestResponse<IDName> data = new SearchRestResponse<IDName>();
		if(qname == null) qname = ContentModel.DIGITAL_CONTENT;
		SearchRequest entityQuery = new SearchRequest(qname);
		if(query != null) {
			if(qname.equals(ContentModel.WEB_LINK)) entityQuery.addParameter(SystemModel.URL.toString(), query);
		}
		entityQuery.setStartRow((page * rows)-rows);
		entityQuery.setEndRow(page * rows);
		SearchResponse results = searchService.search(entityQuery);		
		if(results != null) {
			data.setStartRow(results.getStartRow());
			if(results.getResultSize() >= results.getEndRow()) data.setEndRow(results.getEndRow());
			else data.setEndRow(results.getResultSize());
			data.setTotal(results.getResultSize());
			for(SearchResult entity : results.getResults()) {
				org.archivemanager.models.dictionary.Model model = dictionaryService.getModel(entity.getQName());
				if(model.getHeader() != null) {
					String value = (String)entity.getData().get(model.getHeader());
					data.addRow(new IDName(String.valueOf(entity.getId()), value, entity.getQName().toString()));
				} else {
					data.addRow(new IDName(String.valueOf(entity.getId()), entity.getName(), entity.getQName().toString()));
				}
			}
			data.setAttributes(results.getAttributes());
			data.setBreadcrumb(results.getBreadcrumb());
		}		
		return data;
	}
}
