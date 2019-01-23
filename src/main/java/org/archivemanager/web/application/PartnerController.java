package org.archivemanager.web.application;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/partner")
public class PartnerController {

	
	@RequestMapping(value="/kibana")
	public String displaySearch(HttpServletRequest request, HttpServletResponse response) throws IOException {
		
		return "partner/kibana";
	}
}
