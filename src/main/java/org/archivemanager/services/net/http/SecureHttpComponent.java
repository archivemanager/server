package org.archivemanager.services.net.http;

import java.io.IOException;
import java.util.Map;

public interface SecureHttpComponent {

	HttpResponse get(String urlStr, Map<String,String> headers) throws IOException;
	
	HttpResponse post(String urlStr, byte[] data, Map<String,String> headers) throws IOException;
	
	void setUsername(String username);
	
	void setPassword(String password);
	
}
