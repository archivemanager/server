package org.archivemanager.services.cache;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;


public class TimedCache<K,V> {
	private long maxAge;
	private Map<K,Item<V>> store;

	
	public TimedCache(int minutes) {
		this.maxAge = 1000 * 60 * minutes;
		this.store = new WeakHashMap<K,Item<V>>();
	}
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void put(K key ,V value) {
		store.put(key, new Item(value));
	}
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public V get(K key) {
		Item item = getItem(key);
		return item == null ? null : (V)item.payload;
	}
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public synchronized V get(K key, Callback block) {
		Item item = getItem(key);
		if(item == null) {
			Object value = block.execute();
			item = new Item(value);
			store.put(key, item);
		}
		return (V)item.payload;
	}
	public Set<K> keySet() {
		return store.keySet();
	}
	@SuppressWarnings({ "unchecked"})
	public Set<V> entrySet() {
		Set<V> set = new HashSet<V>();
		for(Item<V> item : store.values()) {
			set.add((V)item.payload);
		}
		return set;
	}
	public void remove(K key) {
		store.remove(key);
	}
	@SuppressWarnings({ "rawtypes" })
	private Item getItem(K key) {
		Item item = (Item) store.get(key);
		if (item == null) {
			return null;
		}
		if(System.currentTimeMillis() - item.birth > maxAge) {
			store.remove(key);
			return null;
		}
		return item;		
	}
	private static class Item<V> {
		long birth;
		Object payload;
		Item(V payload) {
			this.birth = System.currentTimeMillis();
			this.payload = payload;
		}
	}
	public static interface Callback {
		Object execute();
	}
}