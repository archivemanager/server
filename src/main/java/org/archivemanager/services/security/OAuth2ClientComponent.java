package org.archivemanager.services.security;
import java.net.URLEncoder;
import java.util.Map;

import org.archivemanager.services.net.http.HttpComponent;
import org.archivemanager.services.net.http.HttpResponse;
import org.archivemanager.services.net.http.URLConnectionComponent;
import org.springframework.beans.factory.annotation.Autowired;

import com.fasterxml.jackson.databind.ObjectMapper;


//@Component
public class OAuth2ClientComponent {
	private HttpComponent http = new URLConnectionComponent();
	private String url;
	private String grantType;
	private String clientId;
	private String clientSecret;
	private String username;
	private String password;
	@Autowired private ObjectMapper mapper;
	
	
	@SuppressWarnings("rawtypes")
	public AuthDetails getAuthDetails() {
		AuthDetails details = new AuthDetails();
		final StringBuilder result = new StringBuilder();
		addParameter(result, "grant_type", grantType);
		addParameter(result, "client_id", clientId);
		addParameter(result, "client_secret", clientSecret);
		addParameter(result, "username", username);
		addParameter(result, "password", password);
		try {
			HttpResponse response = http.post(url, result.toString().getBytes(), null, null);
			Map map = mapper.readValue(response.getContent(), Map.class);
			details.setAccess_token((String)map.get("access_token"));
		} catch(Exception e) {
			e.printStackTrace();
		}
		return details;
	}
	protected void addParameter(StringBuilder result, String name, String value) {
		try {
			final String encodedName = URLEncoder.encode(name, "UTF-8");
	        final String encodedValue = value != null ? URLEncoder.encode(value, "UTF-8") : "";
	        if(result.length() > 0)
	            result.append("&");
	        result.append(encodedName);
	        result.append("=");
	        result.append(encodedValue);
		} catch(Exception e) {
			System.out.println("error encoding "+name+"/"+value);
		}
	}
	
	public void setUrl(String url) {
		this.url = url;
	}
	public void setGrantType(String grantType) {
		this.grantType = grantType;
	}
	public void setClientId(String clientId) {
		this.clientId = clientId;
	}
	public void setClientSecret(String clientSecret) {
		this.clientSecret = clientSecret;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	
}
