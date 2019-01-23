package org.archivemanager.services.net.proxy;

public interface ProxyStatusListener {
	
	public static final int STATUS_ERR = -1;
	public static final int STATUS_CON = 1;
	
	void status(int status);
	
}
