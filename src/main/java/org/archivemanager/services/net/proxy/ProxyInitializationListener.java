package org.archivemanager.services.net.proxy;

public interface ProxyInitializationListener {

	void initializationCompleted();
	void initializationProgress(String message, int percent);
	
}
