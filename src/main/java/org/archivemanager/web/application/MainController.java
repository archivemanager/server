package org.archivemanager.web.application;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.archivemanager.config.PropertyConfiguration;
import org.archivemanager.models.ClassificationModel;
import org.archivemanager.models.RepositoryModel;
import org.archivemanager.models.system.User;
import org.archivemanager.services.search.SearchRequest;
import org.archivemanager.services.search.SearchService;
import org.archivemanager.services.search.aggregation.SearchAggregation;
import org.archivemanager.services.security.SecurityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ValidationUtils;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;


@Controller
@RequestMapping("/")
public class MainController {
	@Autowired private PropertyConfiguration properties;
	@Autowired private SecurityService securityService;
	@Autowired private SearchService searchService;
	
	
	@RequestMapping(value = "", method = RequestMethod.GET)
	public String getHome(Model model, HttpServletRequest req, HttpServletResponse resp) {
		SearchRequest request = new SearchRequest(RepositoryModel.ITEM);
		request.getAggregations().add(new SearchAggregation(RepositoryModel.COLLECTION_ID.toString(), 20, searchService));	
		request.getAggregations().add(new SearchAggregation(ClassificationModel.CORPORATIONS.toString(), 20, searchService));
		request.getAggregations().add(new SearchAggregation(ClassificationModel.PEOPLE.toString(), 20, searchService));
		request.getAggregations().add(new SearchAggregation(ClassificationModel.SUBJECTS.toString(), 20, searchService));
		request.setStartRow(0);
		request.setEndRow(1);
		searchService.search(request);		
		req.getSession().setAttribute("search-request", request);
		return "home";
	}	
	@RequestMapping(value = "/splash", method = RequestMethod.GET)
    public String splash(Model model, String error, String logout) {
        return "splash";
    }
	
	@RequestMapping(value = "/login", method = RequestMethod.GET)
    public String login(Model model, String error, String logout) {
		model.addAttribute("logo", properties.getLogo());
        if (error != null)
            model.addAttribute("error", "Your username and password is invalid.");
        if (logout != null)
            model.addAttribute("message", "You have been logged out successfully.");
        return "login";
    }	
	@RequestMapping(value = "/registration", method = RequestMethod.GET)
    public String registration(Model model) {
        model.addAttribute("userForm", new User());

        return "registration";
    }

    @RequestMapping(value = "/registration", method = RequestMethod.POST)
    public String registration(@ModelAttribute("userForm") User user, BindingResult bindingResult, Model model) {
    	ValidationUtils.rejectIfEmptyOrWhitespace(bindingResult, "username", "NotEmpty");
        if (user.getName().length() < 6 || user.getName().length() > 32) {
        	bindingResult.rejectValue("username", "Size.userForm.username");
        }
        if(securityService.getUserByUsername(user.getName()) != null) {
        	bindingResult.rejectValue("username", "Duplicate.userForm.username");
        }
        ValidationUtils.rejectIfEmptyOrWhitespace(bindingResult, "password", "NotEmpty");
        if (user.getPassword().length() < 8 || user.getPassword().length() > 32) {
        	bindingResult.rejectValue("password", "Size.userForm.password");
        }
        if (bindingResult.hasErrors()) {
            return "registration";
        }

        securityService.addUser(user);

        //securityService.autologin(user.getUsername(), user.getPassword());

        return "redirect:/";
    }    
}
