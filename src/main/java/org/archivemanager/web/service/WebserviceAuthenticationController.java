package org.archivemanager.web.service;

import org.archivemanager.models.SystemModel;
import org.archivemanager.models.binders.SystemModelEntityBinder;
import org.archivemanager.models.system.User;
import org.archivemanager.services.entity.Entity;
import org.archivemanager.services.entity.EntityService;
import org.archivemanager.services.security.TokenService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.google.common.collect.ImmutableMap;

@RestController
@RequestMapping("/service/auth")
public class WebserviceAuthenticationController {
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	@Autowired private EntityService entityService;
	@Autowired private TokenService tokenService;
	@Autowired private SystemModelEntityBinder binder;
	@Autowired private BCryptPasswordEncoder passwordEncoder;
	
	
	@RequestMapping("/login")
	public String login(@RequestParam("email") final String email, @RequestParam("password") final String password) {
		Entity entity = entityService.getEntity(SystemModel.USER, SystemModel.EMAIL, email);
	    if(entity != null) {
		   	User user = binder.getUser(entity);
		   	if(user != null) {
		    	if(!user.isActive()) {
		        	logger.info("message", "there was an authentication problem, account disabled.");
		        } else if(passwordEncoder.matches(password, user.getPassword())) {
		        	logger.info("Succesful authentication!"); 
		        	String token = tokenService.expiring(ImmutableMap.of("email", email));
		        	return token;
		        }		 
        	}  	
	    }
	    throw new RuntimeException("invalid login and/or password");
	}
	@GetMapping("/logout")
	public boolean logout(@RequestParam("email") String email) {
		//authentication.logout(user);
	    return true;
	}
	/*
	@GetMapping("/refresh")
	public String refresh(HttpServletRequest request) {
		String authToken = request.getHeader(tokenHeader);
        final String token = authToken.substring(7);
        String username = jwtTokenUtil.getUsernameFromToken(token);
        JwtUser user = (JwtUser) userDetailsService.loadUserByUsername(username);

        if (jwtTokenUtil.canTokenBeRefreshed(token, user.getLastPasswordResetDate())) {
            String refreshedToken = jwtTokenUtil.refreshToken(token);
            return ResponseEntity.ok(new JwtAuthenticationResponse(refreshedToken));
        } else {
            return ResponseEntity.badRequest().body(null);
        }
	}
	*/
}
