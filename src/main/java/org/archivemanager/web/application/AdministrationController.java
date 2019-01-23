package org.archivemanager.web.application;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.archivemanager.config.PropertyConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;


@Controller
@RequestMapping("/administration")
public class AdministrationController {
	@Autowired private PropertyConfiguration properties;
	
	
	@RequestMapping(value = "", method = RequestMethod.GET)
	public String getRepository(final Model model, HttpServletRequest request, HttpServletResponse resp) {
		model.addAttribute("kibanaUrl", properties.getKibanaUrl());
		model.addAttribute("neo4jUrl", properties.getNeo4jUrl());
		return "administration";
	}
}
