package org.archivemanager.services.cache;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;


@Service
public class ArchiveManagerCacheService implements CacheService {
	private static final long serialVersionUID = 5970747979434528920L;
	private Map<String, InMemoryCache<String, Object>> caches = new HashMap<String, InMemoryCache<String, Object>>();
	
	
	@Override
	public void put(String cacheName, String id, Object entity) {
		InMemoryCache<String, Object> cache = caches.get(cacheName);
		if(cache == null) {
			//expires after 1 day, cleans up every hour, and holds max 10,000 items
			cache = new InMemoryCache<String, Object>(60 *60 * 24, 60 *60 * 24, 10000);
			caches.put(cacheName, cache);
		}
		cache.put(id, entity);
	}
	@Override
	public boolean has(String cacheName, String id) {
		InMemoryCache<String, Object> cache = caches.get(cacheName);
		if(cache != null) {
			return cache.containsKey(id);
		} 
		return false;
	}
	@Override
	public Object get(String cacheName, String id) {
		InMemoryCache<String, Object> cache = caches.get(cacheName);
		if(cache != null) {
			Object element = cache.get(id);
			return element;
		} 
		return null;
	}
	@Override
	public List<String> getKeys(String cacheName) {
		InMemoryCache<String, Object> cache = caches.get(cacheName);
		if(cache != null) return cache.getKeys();
		return new ArrayList<String>(0);
	}
	@Override
	public void remove(String cacheName, String id) {
		InMemoryCache<String, Object> cache = caches.get(cacheName);
		if(cache != null) cache.remove(id);
	}
}
