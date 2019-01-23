package org.archivemanager.web.application;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.archivemanager.data.IDName;
import org.archivemanager.services.dictionary.DataDictionaryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;


@Controller
@RequestMapping("/app/reporting")
public class ReportingController {
	@Autowired private DataDictionaryService dictionaryService;
		
	
	@RequestMapping(value = "", method = RequestMethod.GET)
	public String getRepository(final Model model, HttpServletRequest request, HttpServletResponse resp) {
		List<IDName> data = new ArrayList<IDName>();
		List<org.archivemanager.models.dictionary.Model> models = dictionaryService.getModels();
		for(org.archivemanager.models.dictionary.Model m : models){
			if(m.isSearchable()) {
				IDName value = new IDName(m.getQName().toString(), m.getLabel());
				data.add(value);
			}
		}
		model.addAttribute("models", data.stream().sorted((o1, o2)->o1.getName().compareTo(o2.getName())).collect(Collectors.toList()));
		return "reporting";
	}
	
	
	
 }
