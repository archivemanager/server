package org.archivemanager.web.utility;

import java.util.Enumeration;

import javax.servlet.http.HttpServletRequest;

import org.archivemanager.services.net.http.HttpRequest;

public class WebUtility {

	
	public static HttpRequest getHttpRequest(HttpServletRequest request) {
		HttpRequest req = new HttpRequest();
		req.setNativeRequest(request);
		req.setUrl(request.getRequestURI());
		req.setHost(request.getRemoteHost());
		req.setPath(request.getRequestURI());
		for(Object key : request.getParameterMap().keySet()) {
			Object v = request.getParameterMap().get(key);
			if(v instanceof String[]) {
				String value = "";
				for(String s : (String[])v) {
					value += s+",";
				}
				if(value.endsWith(",")) value = value.substring(0, value.length()-1);
				req.getParameters().put((String)key, value);
			} else req.getParameters().put((String)key, (String)v);
		}
		Enumeration<String> en = request.getHeaderNames();
		while(en.hasMoreElements()) {
			String key = en.nextElement();
			Object v = request.getHeader(key);
			req.getHeaders().put(key, v);			
		}
		return req;
	}
}
