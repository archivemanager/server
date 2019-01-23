package org.archivemanager.web.application;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;

@Controller
public class ExceptionController implements ErrorController {
	private final ErrorAttributes errorAttributes;
	
	
	@Autowired
	public ExceptionController(ErrorAttributes errorAttributes) {
	    this.errorAttributes = errorAttributes;
	}

	@RequestMapping(value = "/error")
    public String error(final Model model, HttpServletRequest request) {
		Map<String, Object> map = getErrorAttributes(request,getTraceParameter(request));
		
	    model.addAttribute("error", map.get("error"));
	    model.addAttribute("exception", map.get("exception"));
		return "error";
    }

    @Override
    public String getErrorPath() {
        return "";
    }
    private boolean getTraceParameter(HttpServletRequest request) {
        String parameter = request.getParameter("trace");
        if (parameter == null) {
            return false;
        }
        return !"false".equals(parameter.toLowerCase());
    }
    private Map<String, Object> getErrorAttributes(HttpServletRequest aRequest, boolean includeStackTrace) {
        WebRequest requestAttributes = new ServletWebRequest(aRequest);
        return errorAttributes.getErrorAttributes(requestAttributes, includeStackTrace);
    }
}
