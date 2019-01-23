package org.archivemanager.web.application;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping("/documentation")
public class DocumentationController {

	
	@RequestMapping(value = "", method = RequestMethod.GET)
	public String getHome(HttpServletRequest request, HttpServletResponse resp) {
		return "documentation";
	}
	@RequestMapping(value = "/javadoc", method = RequestMethod.GET)
	public String getJavadoc(HttpServletRequest request, HttpServletResponse resp) {
		return "documentation/javadoc";
	}
	@RequestMapping(value = "/admin-applications", method = RequestMethod.GET)
	public String getAdminApplications(HttpServletRequest request, HttpServletResponse resp) {
		return "documentation/admin-applications";
	}
	@RequestMapping(value = "/development", method = RequestMethod.GET)
	public String getDevelopment(HttpServletRequest request, HttpServletResponse resp) {
		return "documentation/development";
	}
	
}
