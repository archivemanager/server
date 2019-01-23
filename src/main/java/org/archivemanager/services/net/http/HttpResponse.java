package org.archivemanager.services.net.http;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HttpResponse {
	private String method;
	private String url;
	private String host;
	private String path;
	private String referer;
	private byte[] contentBytes;
	private String content;
	private int statusCode;
	private String message;
	private String contentType;
	private String encoding;
	private Map<String, List<String>> headers = new HashMap<String, List<String>>();
	
	public HttpResponse(){}
	public HttpResponse(String data) {
		content = data;
		/*
		ByteArrayInputStream is = new ByteArrayInputStream(data); 
		BufferedReader bfReader = new BufferedReader(new InputStreamReader(is)); 
		String temp = null;
		ByteArray bodyArray = new ByteArray();
		try {
			int lineCount = 0;
			while((temp = bfReader.readLine()) != null) {
				if(lineCount == 0) {	
				}else if(temp.startsWith("Connection:")) {
				} else if(temp.startsWith("Server:")) {
				} else if(temp.startsWith("Via:")) {
				} else if(temp.startsWith("Age:")) {					
				} else if(temp.startsWith("Vary:")) {					
				} else if(temp.startsWith("Cache-Control:")) {				
				} else if(temp.startsWith("Content-Type:")) {
					//text/html; charset=utf-8
					String value = temp.substring(13).trim();
					encoding = value.substring(0, value.indexOf(";")+1);
					contentType = value.substring(value.indexOf("charset="));
				} else if(temp.startsWith("Content-Length:")) {		
				} else if(temp.startsWith("Expires:")) {
				} else if(temp.startsWith("Last-Modified:")) {
				} else if(temp.startsWith("P3P:")) {
				} else if(temp.startsWith("Date:")) {
				} else if(temp.startsWith("X-UA-Compatible:")){
				} else if(temp.startsWith("IE=Edgex-Instance-Name:")){
				} else if(temp.startsWith("Httpd-Identifier:")){
				} else if(temp.startsWith("Transfer-Encoding:")){
				} else if(temp.startsWith("Pragma:")){
				} else if(temp.startsWith("X-Yahoo-Request-Id:")){
				} else if(temp.startsWith("X-Frame-Options:")){
				} else if(temp.startsWith("X-Server-Time-FetchImage:")){
				} else if(temp.startsWith("Set-Cookie:")){
				} else if(temp.length() == 0) {
					
				} else if(temp.length() > 0) {
					bodyArray.append(temp.getBytes());
				}
				lineCount++;
			}
			content = bodyArray.getArray();
			bodyArray.close();
		} catch(Exception e) {
			e.printStackTrace();
		}
		*/
	}

	public String getMethod() {
		return method;
	}
	public void setMethod(String method) {
		this.method = method;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
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
	public byte[] getContentBytes() {
		return contentBytes;
	}
	public void setContentBytes(byte[] contentBytes) {
		this.contentBytes = contentBytes;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
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
	public String getContentType() {
		return contentType;
	}
	public void setContentType(String contentType) {
		this.contentType = contentType;
	}
	public String getEncoding() {
		return encoding;
	}
	public void setEncoding(String encoding) {
		this.encoding = encoding;
	}
	public Map<String, List<String>> getHeaders() {
		return headers;
	}
	public void setHeaders(Map<String, List<String>> headers) {
		this.headers = headers;
	}
	
}
