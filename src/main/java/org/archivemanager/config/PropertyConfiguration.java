package org.archivemanager.config;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;


@ConfigurationProperties("am")
public class PropertyConfiguration {	
	
	@Value("${am.logo}")
	private String logo;
	public String getLogo() {
		return logo;
	}
	
	@Value("${am.home.directory}")
	private String homeDirectory;
	public String getHomeDirectory() {
		return homeDirectory;
	}
	
	@Value("#{'${am.public.urls}'.split(',')}")
	private List<String> publicUrls;
	public List<String> getPublicUrls() {
		return publicUrls;
	}
	
	@Value("${am.datastore.username}")
	private String datastoreUsername;
	public String getDatastoreUsername() {
		return datastoreUsername;
	}
	
	@Value("${am.datastore.host}")
	private String datastoreHost;
	public String getDatastoreHost() {
		return datastoreHost;
	}
	
	@Value("${am.datastore.password}")
	private String datastorePassword;
	public String getDatastorePassword() {
		return datastorePassword;
	}
	
	@Value("${am.search.host}")
	private String searchHost;
	public String getSearchHost() {
		return searchHost;
	}
	
	@Value("${am.search.port}")
	private int searchPort;
	public int getSearchPort() {
		return searchPort;
	}
	
	@Value("${am.node.cleanup}")
	private String nodeCleanup;
	public List<String> getNodeCleanup() {
		if(nodeCleanup != null) return Arrays.asList(nodeCleanup.split(","));
		else return Collections.emptyList();
	}
	
	@Value("${am.relationship.cleanup}")
	private String relationshipCleanup;
	public List<String> getRelationshipCleanup() {
		if(relationshipCleanup != null) return Arrays.asList(relationshipCleanup.split(","));
		else return Collections.emptyList();
	}
	
	@Value("${am.entity.indexing.batch.size}")
	private int indexingBatchSize;
	public int getEntityIndexingBatchSize() {
		return indexingBatchSize;
	}
	
	@Value("${am.scheduling.threads}")
	private int schedulingThreads;
	public int getSchedulingThreads() {
		return schedulingThreads;
	}
	
	@Value("${ldap.urls}")
	private String ldapUrls;	 
	public String getLdapUrls() {
		return ldapUrls;
	}

	@Value("${ldap.base.dn}")
	private String ldapBaseDn;	 
	public String getLdapBaseDn() {
		return ldapBaseDn;
	}
	
	@Value("${ldap.username}")
	private String ldapSecurityPrincipal;
	public String getLdapSecurityPrincipal() {
		return ldapSecurityPrincipal;
	}
	
	@Value("${ldap.password}")
	private String ldapPrincipalPassword;
	public String getLdapPrincipalPassword() {
		return ldapPrincipalPassword;
	}
	
	@Value("${ldap.user.dn.pattern}")
	private String ldapUserDnPattern;
	public String getLdapUserDnPattern() {
		return ldapUserDnPattern;
	}
	
	@Value("${ldap.enabled}")
	private String ldapEnabled;
	public String getLdapEnabled() {
		return ldapEnabled;
	}
	
	@Value("${token.expiration}")
	private int tokenExpiration;
	public int getTokenExpiration() {
		return tokenExpiration;
	}
	
	@Value("${token.issuer}")
	private String tokenIssuer;
	public String getTokenIssuer() {
		return tokenIssuer;
	}
	
	@Value("${token.secret.key}")
	private String tokenSecretKey;
	public String getTokenSecretKey() {
		return tokenSecretKey;
	}
	
	@Value("${token.clock.skew}")
	private int tokenClockSkew;
	public int getTokenClockSkew() {
		return tokenClockSkew;
	}
	
	@Value("${kibana.url}")
	private String kibanaUrl;
	public String getKibanaUrl() {
		return kibanaUrl;
	}
	
	@Value("${neo4j.url}")
	private String neo4jUrl;
	public String getNeo4jUrl() {
		return neo4jUrl;
	}
}
