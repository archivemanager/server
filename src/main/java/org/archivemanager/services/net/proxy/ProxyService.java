package org.archivemanager.services.net.proxy;

import java.io.IOException;
import java.util.Map;

public interface ProxyService {

	void start();
	void stop();
	byte[] get(String url, Map<String,String> headers) throws IOException;
	
}
