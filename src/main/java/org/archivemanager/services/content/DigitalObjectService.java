package org.archivemanager.services.content;

public interface DigitalObjectService {

	FileNode addDigitalObject(long userId, String name, String description, String group, int order, byte[] payload);
	FileNode getDigitalObject(long nodeId);
	void removeDigitalObject(long nodeId);
	
}
