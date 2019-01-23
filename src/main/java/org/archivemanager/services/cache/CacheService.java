package org.archivemanager.services.cache;

import java.io.Serializable;
import java.util.List;


public interface CacheService extends Serializable {

	void put(String cache, String id, Object object);
	List<String> getKeys(String cacheName);
	Object get(String cache, String id);
	void remove(String cache, String id);
	boolean has(String cache, String id);
	
}
