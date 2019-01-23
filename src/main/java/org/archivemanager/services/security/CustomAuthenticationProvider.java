package org.archivemanager.services.security;
import java.util.HashSet;
import java.util.Set;

import org.archivemanager.models.SystemModel;
import org.archivemanager.models.binders.SystemModelEntityBinder;
import org.archivemanager.models.system.Role;
import org.archivemanager.models.system.User;
import org.archivemanager.services.entity.Entity;
import org.archivemanager.services.entity.EntityService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class CustomAuthenticationProvider implements AuthenticationProvider{ 
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	@Autowired private EntityService entityService;
	@Autowired private BCryptPasswordEncoder passwordEncoder;
	@Autowired private SystemModelEntityBinder binder;
	
	
	@Override
	public Authentication authenticate(Authentication authentication) throws AuthenticationException {
		String email = authentication.getName();
        String password = authentication.getCredentials().toString();        
        logger.info("Name = " + email + " ,Password = " + password);        
        User user = null;
        Set<GrantedAuthority> grantedAuthorities = new HashSet<>();
        Entity entity = entityService.getEntity(SystemModel.USER, SystemModel.EMAIL, email);
        if(entity != null) {
	       	user = binder.getUser(entity);
	        for(Role role : user.getRoles()){
	        	if(role.getName() != null) grantedAuthorities.add(new SimpleGrantedAuthority(role.getName()));
	       	}
		    if(user != null) {
		    	if(!user.isActive()) {
		        	logger.info("message", "there was an authentication problem, account disabled.");
		        } else if(passwordEncoder.matches(password, user.getPassword())) {
		        	logger.info("Succesful authentication!");        		
		        	return new UsernamePasswordAuthenticationToken(email, password, grantedAuthorities);
		        }		 
        	}
        }
        logger.info("Login fail!");        
        return null;
	}
 
	@Override
	public boolean supports(Class<?> authentication) {
		return authentication.equals(UsernamePasswordAuthenticationToken.class);
	}
	
}