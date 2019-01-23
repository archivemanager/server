package org.archivemanager.services.net.http;

public class HttpProxy {
	private String host;
	private int port;
	private String type;
	
	
	public HttpProxy(String host, int port, String type) {
		this.host = host;
		this.port = port;
		this.type = type;
	}
	
	public String getHost() {
		return host;
	}
	public void setHost(String host) {
		this.host = host;
	}
	public int getPort() {
		return port;
	}
	public void setPort(int port) {
		this.port = port;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	
}
