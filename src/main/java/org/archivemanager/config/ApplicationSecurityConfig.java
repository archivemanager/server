package org.archivemanager.config;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.archivemanager.services.security.CustomAuthenticationProvider;
import org.archivemanager.services.security.NoRedirectStrategy;
import org.archivemanager.services.security.TokenAuthenticationFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;


@Configuration
@Order(1)
public class ApplicationSecurityConfig extends WebSecurityConfigurerAdapter {
	@Autowired private UserDetailsService userDetailsService;
	@Autowired private PropertyConfiguration properties;
	@Autowired private CustomAuthenticationProvider authProvider;
	@Autowired private BCryptPasswordEncoder encoder;
			  
		  
    @Override
    protected void configure(HttpSecurity http) throws Exception {
    	List<String> publicUrls = new ArrayList<String>();
    	List<String> propertyUrls = properties.getPublicUrls();
    	publicUrls.add("/css/**");
    	publicUrls.add("/js/**");
    	publicUrls.add("/images/**");
    	publicUrls.add("/403");
    	publicUrls.add("/portal/**");
    	publicUrls.add("/service/auth/**");
    	publicUrls.add("/service/search/**");
    	for(String u : propertyUrls) {
    		if(u.length() > 0)
    			publicUrls.add(u);
    	}
    	String[] urls = publicUrls.toArray(new String[publicUrls.size()]);
        http
        	.cors().and()
            .authorizeRequests()
	            .antMatchers(urls).permitAll()
	        	.antMatchers("/**").authenticated()
	            .anyRequest().permitAll()
	            .and()
            .formLogin()
                .loginPage("/login")
                .usernameParameter("email")
    			.passwordParameter("password")
                .defaultSuccessUrl("/")
                .permitAll()
                .and()
            .logout()
                .permitAll()
                .and()
            .csrf()
            	.disable()
            .headers()
				.frameOptions().sameOrigin()
				.httpStrictTransportSecurity().disable();;
    }
    
    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList("http://localhost:3333"));
        configuration.setAllowedMethods(Arrays.asList("GET","POST"));
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
    @Autowired
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.authenticationProvider(authProvider);
        /*
        .ldapAuthentication().contextSource()
			.url(properties.getLdapUrls() + properties.getLdapBaseDn())
	        .managerDn(properties.getLdapSecurityPrincipal())
	        .managerPassword(properties.getLdapPrincipalPassword())
	        .and()
	        .userDnPatterns(properties.getLdapUserDnPattern());
	        */
    }
    @Bean
    TokenAuthenticationFilter restAuthenticationFilter() throws Exception {
      final TokenAuthenticationFilter filter = new TokenAuthenticationFilter(new AntPathRequestMatcher("/service/**"));
      filter.setAuthenticationManager(authenticationManager());
      filter.setAuthenticationSuccessHandler(successHandler());
      return filter;
    }
    @Bean
    SimpleUrlAuthenticationSuccessHandler successHandler() {
      final SimpleUrlAuthenticationSuccessHandler successHandler = new SimpleUrlAuthenticationSuccessHandler();
      successHandler.setRedirectStrategy(new NoRedirectStrategy());
      return successHandler;
    }
    @Bean
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public FilterRegistrationBean disableAutoRegistration(final TokenAuthenticationFilter filter) {
      final FilterRegistrationBean registration = new FilterRegistrationBean(filter);
      registration.setEnabled(false);
      return registration;
    }
    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService).passwordEncoder(encoder);
    }
}