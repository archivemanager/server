package org.archivemanager.web.portal;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;


@Controller
@RequestMapping("/portal")
public class PortalController {

	
	@RequestMapping(value = "/reserve", method = RequestMethod.GET)
	public String getRepository(final Model model, HttpServletRequest request, HttpServletResponse resp) {
		
		return "/portal/reserve";
	}
}
