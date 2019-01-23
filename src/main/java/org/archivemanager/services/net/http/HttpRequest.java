package org.archivemanager.services.net.http;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import org.archivemanager.data.io.ByteArray;



public class HttpRequest {
	private String method;
	private String host;
	private String path;
	private String url;
	private String referer;
	private byte[] content;
	private String encoding;
	private String contentType;
	private int statusCode;
	private String message;
	private Object nativeRequest;
	private Map<String,String> parameters = new HashMap<String,String>();
	private Map<String,Object> headers = new HashMap<String,Object>();
	
	public HttpRequest(){}
	public HttpRequest(byte[] data) {
		ByteArrayInputStream is = new ByteArrayInputStream(data); 
		BufferedReader bfReader = new BufferedReader(new InputStreamReader(is)); 
		
		String temp = null;
		ByteArray bodyArray = new ByteArray();
		try {
			while((temp = bfReader.readLine()) != null){ 
				if(temp.startsWith("GET")) {
					method = "GET";
					path = temp.substring(4, temp.length() - 9);
				} else if(temp.startsWith("POST")) {
					method = "POST";
					path = temp.substring(5, temp.length() - 9);
				} else if(temp.startsWith("Host:")) {
					host = temp.substring(5).trim();
					url = path.startsWith(host) ? path : host+path; 
				} else if(temp.startsWith("Referer:")) {
					referer = temp.substring(9);
				} else if(temp.startsWith("Accept-Language:")) {					
				} else if(temp.startsWith("Accept-Encoding:")) {					
				} else if(temp.startsWith("Accept-Charset:")) {				
				} else if(temp.startsWith("User-Agent:")) {			
				} else if(temp.startsWith("Cache-Control:")) {		
				} else if(temp.startsWith("Cache-Control:")) {
				} else if(temp.startsWith("Pragma:")) {
				} else if(temp.startsWith("Accept:")) {
				} else if(temp.startsWith("Connection:")) {
				} else if(temp.startsWith("Coookie:")) {
				} else if(temp.startsWith("X-Requested-With:")) {
				} else if(temp.length() > 0) {
					bodyArray.append(temp.getBytes());
				}				
			}
			content = bodyArray.getArray();
			bodyArray.close();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	public String getMethod() {
		return method;
	}
	public void setMethod(String method) {
		this.method = method;
	}
	public String getPath() {
		return path;
	}
	public void setPath(String path) {
		this.path = path;
	}
	public String getHost() {
		return host;
	}
	public void setHost(String host) {
		this.host = host;
	}
	public String getReferer() {
		return referer;
	}
	public void setReferer(String referer) {
		this.referer = referer;
	}
	public byte[] getContent() {
		return content;
	}
	public void setContent(byte[] content) {
		this.content = content;
	}
	public int getStatusCode() {
		return statusCode;
	}
	public void setStatusCode(int statusCode) {
		this.statusCode = statusCode;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public Object getNativeRequest() {
		return nativeRequest;
	}
	public void setNativeRequest(Object nativeRequest) {
		this.nativeRequest = nativeRequest;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getEncoding() {
		return encoding;
	}
	public void setEncoding(String encoding) {
		this.encoding = encoding;
	}
	public String getContentType() {
		return contentType;
	}
	public void setContentType(String contentType) {
		this.contentType = contentType;
	}
	public Map<String, String> getParameters() {
		return parameters;
	}
	public void setParameters(Map<String, String> parameters) {
		this.parameters = parameters;
	}	
	public String getParameter(String key) {
		return parameters.get(key);
	}
	public Map<String, Object> getHeaders() {
		return headers;
	}
	public void setHeaders(Map<String, Object> headers) {
		this.headers = headers;
	}
	
}
