package org.archivemanager.web.application;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.archivemanager.data.IDName;
import org.archivemanager.models.ClassificationModel;
import org.archivemanager.models.dictionary.ModelField;
import org.archivemanager.models.dictionary.ModelForm;
import org.archivemanager.models.dictionary.ModelRelation;
import org.archivemanager.models.system.QName;
import org.archivemanager.models.system.User;
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
@RequestMapping("/app/classification")
public class ClassificationManagerController extends ApplicationController {
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
			
			List<ModelForm> forms = new ArrayList<ModelForm>();
			forms.add(getModelForm(ClassificationModel.SUBJECT));
			forms.add(getModelForm(ClassificationModel.PERSON));
			forms.add(getModelForm(ClassificationModel.CORPORATION));			
			model.addAttribute("forms", forms);
		}
		return "classification";
	}
	
	protected ModelForm getModelForm(QName qname) {
		List<ModelField> fields = dictionaryService.getModelFields(qname);
		List<ModelRelation> relations = dictionaryService.getModelRelations(qname);
		ModelForm form = new ModelForm(qname);
		form.setRelations(relations);
		form.setLongtextFields(fields.stream().filter(field -> field.getType().equals("longtext")).collect(Collectors.toList()));
		form.setMediumtextFields(fields.stream().filter(field -> !field.isHidden() && field.getType().equals("mediumtext")).collect(Collectors.toList()));
		form.setSmalltextFields(fields.stream().filter(field -> !field.isHidden() && field.getType().equals("smalltext") && field.getValues().isEmpty()).collect(Collectors.toList()));
		form.setMultivalueFields(fields.stream().filter(field -> !field.isHidden() && field.getType().equals("smalltext") && !field.getValues().isEmpty()).collect(Collectors.toList()));
		form.setIntegerFields(fields.stream().filter(field -> !field.isHidden() && field.getType().equals("integer")).collect(Collectors.toList()));
		form.setBooleanFields(fields.stream().filter(field -> !field.isHidden() && field.getType().equals("boolean")).collect(Collectors.toList()));			
		return form;
	}
	
	/*** Services ***/	
	@ResponseBody
	@RequestMapping(value="/search.json")
	public SearchRestResponse<IDName> search(@RequestParam(required=false) String query, @RequestParam(required=false) QName qname, 
			@RequestParam(required=false, defaultValue="0") int page, @RequestParam(required=false, defaultValue="20") int rows,
			HttpServletRequest request, HttpServletResponse response) throws Exception {
		SearchRestResponse<IDName> data = new SearchRestResponse<IDName>();
		if(qname == null) qname = ClassificationModel.CORPORATION;
		SearchRequest entityQuery = new SearchRequest(qname, query);
		entityQuery.setStartRow((page * rows)-rows);
		entityQuery.setEndRow(page * rows);
		//entityQuery.addSort(new Sort(Sort.STRING, SystemModel.NAME.toString()+".keyword", "asc"));
		SearchResponse results = searchService.search(entityQuery);		
		if(results != null) {
			data.setStartRow(results.getStartRow());
			if(results.getResultSize() >= results.getEndRow()) data.setEndRow(results.getEndRow());
			else data.setEndRow(results.getResultSize());
			data.setTotal(results.getResultSize());
			for(SearchResult entity : results.getResults()) {
				data.addRow(new IDName(String.valueOf(entity.getId()), entity.getName(), entity.getQName().toString()));
			}
			data.setAttributes(results.getAttributes());
			data.setBreadcrumb(results.getBreadcrumb());
		}		
		return data;
	}
}
